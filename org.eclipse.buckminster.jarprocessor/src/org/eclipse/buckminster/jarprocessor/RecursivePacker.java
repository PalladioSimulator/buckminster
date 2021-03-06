package org.eclipse.buckminster.jarprocessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Pack200.Packer;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.buckminster.core.helpers.FileUtils;
import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.core.runtime.CoreException;

public class RecursivePacker extends RecursivePack200 {
	private final boolean useRedunantGZipping;

	public RecursivePacker(File tempDir, List<String> defaultArgs, boolean useRedundantGZipping) {
		super(tempDir, defaultArgs);
		this.useRedunantGZipping = useRedundantGZipping;
	}

	public boolean pack(File jarFile, File destFolder, boolean retainUnpacked) throws CoreException {
		Logger log = Buckminster.getLogger();
		String fileName = jarFile.getAbsolutePath();
		InputStream input = null;
		OutputStream output = null;

		boolean sharedFolder;
		if (destFolder == null) {
			sharedFolder = true;
			destFolder = jarFile.getParentFile();
		} else
			sharedFolder = destFolder.equals(jarFile.getParentFile());

		try {
			input = new ZipInputStream(new FileInputStream(jarFile));
			JarInfo jarInfo = JarInfo.getJarInfo(null, fileName, (ZipInputStream) input);
			IOUtils.close(input);
			input = null;

			if (!(jarInfo.hasClasses() || (jarInfo.isNested() && !jarInfo.isExcludeChildrenPack()))) {
				log.debug("Packer: Skipping %s since it contains no classes and no nested jars to pack", fileName); //$NON-NLS-1$
				if (!sharedFolder)
					FileUtils.copyFile(jarFile, destFolder, jarFile.getName(), null);
				return false;
			}
			if (jarInfo.isExcludePack()) {
				log.debug("Packer: Skipping %s since is excluded", fileName); //$NON-NLS-1$
				if (!sharedFolder)
					FileUtils.copyFile(jarFile, destFolder, jarFile.getName(), null);
				return false;
			}
			if (jarInfo.isSigned() && !jarInfo.isConditioned()) {
				log.debug("Packer: Skipping %s since is signed but not conditioned", fileName); //$NON-NLS-1$
				if (!sharedFolder)
					FileUtils.copyFile(jarFile, destFolder, jarFile.getName(), null);
				return false;
			}

			File packedFile = new File(destFolder, jarFile.getName() + PACK_GZ_SUFFIX);
			input = new FileInputStream(jarFile);
			output = new BufferedOutputStream(new FileOutputStream(packedFile));
			output = new BufferedOutputStream(new GZIPOutputStream(output));

			// We must refrain from nested packing if
			// a) Nested packing is excluded using excludeChildrenPack
			// b) This jar has classes and is signed. That's because if we
			// change the nested file, the
			// CP tables used when normalizing will change, and thus, the packed
			// classes will change.
			//
			if (jarInfo.isNested())
				nestedPack(input, jarInfo, output);
			else
				pack(jarInfo, input, output);
			IOUtils.close(input);
			input = null;
			if (sharedFolder) {
				if (!retainUnpacked)
					jarFile.delete();
			} else {
				if (retainUnpacked)
					FileUtils.copyFile(jarFile, destFolder, jarFile.getName(), null);
			}
			return true;
		} catch (IOException e) {
			throw BuckminsterException.fromMessage(e, "Unable to pack %s", fileName); //$NON-NLS-1$
		} finally {
			IOUtils.close(input);
			IOUtils.close(output);
		}
	}

	protected void nestedPack(final InputStream input, final JarInfo jarInfo, OutputStream packedOut) throws IOException, CoreException {
		File packInputFile = null;
		try {
			tempDir.mkdirs();
			packInputFile = File.createTempFile("conditioned_", ".jar", tempDir); //$NON-NLS-1$//$NON-NLS-2$
			OutputStream tempOut = null;
			try {
				tempOut = new BufferedOutputStream(new FileOutputStream(packInputFile));
				processNestedJars(new ZipInputStream(input), jarInfo, tempOut);
			} finally {
				IOUtils.close(tempOut);
			}

			Packer packer = getPacker(jarInfo);
			packer.pack(new JarFile(packInputFile), packedOut);
		} finally {
			if (packInputFile != null)
				packInputFile.delete();
		}
	}

	private void processNestedJars(ZipInputStream jarIn, JarInfo jarInfo, OutputStream output) throws IOException, CoreException {
		Logger log = Buckminster.getLogger();
		ZipOutputStream jarOut = new ZipOutputStream(output);
		ZipEntry entry;
		boolean packChildren = !jarInfo.isExcludeChildrenPack();
		while ((entry = jarIn.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.endsWith(JAR_SUFFIX)) {
				JarInfo nested = jarInfo.getNestedInfo(name);
				if (nested != null) {
					if (!packChildren)
						log.debug("Packer: Skipping recursive pack of %s since parent has children pack disabled", name); //$NON-NLS-1$
					else if (nested.isSigned() && !nested.isConditioned())
						log.debug("Packer: Skipping recursive pack of %s since it is signed but not conditioned", name); //$NON-NLS-1$
					else if (jarInfo.hasClasses() && jarInfo.isSigned()) {
						log.debug("Packer: Skipping recursive pack of %s since parent is signed and has classes", name); //$NON-NLS-1$
					} else {
						if (useRedunantGZipping) {
							jarOut.putNextEntry(createEntry(entry, name + PACK_GZ_SUFFIX));
							GZIPOutputStream gzipOut = new GZIPOutputStream(jarOut);
							log.debug("Packer: Recursive gzipped pack of %s", name); //$NON-NLS-1$
							nestedPack(jarIn, nested, gzipOut);
							gzipOut.finish();
						} else {
							log.debug("Packer: Recursive pack of %s", name); //$NON-NLS-1$
							jarOut.putNextEntry(createEntry(entry, name + PACK_SUFFIX));
							nestedPack(jarIn, nested, jarOut);
						}
						continue;
					}
				}
			}
			jarOut.putNextEntry(createEntry(entry));
			if (!entry.isDirectory())
				IOUtils.copy(jarIn, jarOut, null);
		}
		jarOut.finish();
	}
}
