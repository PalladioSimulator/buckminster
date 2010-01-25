/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.reader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.helpers.FileHandle;
import org.eclipse.buckminster.core.helpers.FileUtils;
import org.eclipse.buckminster.core.materializer.MaterializationContext;
import org.eclipse.buckminster.core.metadata.model.Resolution;
import org.eclipse.buckminster.core.mspec.ConflictResolution;
import org.eclipse.buckminster.core.version.ProviderMatch;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.buckminster.runtime.URLUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Thomas Hallgren
 */
public abstract class AbstractCatalogReader extends AbstractReader implements ICatalogReader
{
	private boolean m_prefStateKnown = false;

	private IEclipsePreferences m_preferences;

	protected AbstractCatalogReader(IReaderType readerType, ProviderMatch providerMatch)
	{
		super(readerType, providerMatch);
	}

	public final boolean exists(String fileName, IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask(null, 100);
		try
		{
			File addOnFolder = getOverlayFolder(MonitorUtils.subMonitor(monitor, 10));
			if(addOnFolder != null && new File(addOnFolder, fileName).exists())
			{
				MonitorUtils.worked(monitor, 90);
				return true;
			}
			return innerExists(fileName, MonitorUtils.subMonitor(monitor, 90));
		}
		finally
		{
			monitor.done();
		}
	}

	public final FileHandle getContents(String fileName, IProgressMonitor monitor) throws CoreException, IOException
	{
		ProviderMatch ri = getProviderMatch();
		Logger logger = CorePlugin.getLogger();

		monitor.beginTask(null, 100);
		try
		{
			File addOnFolder = getOverlayFolder(MonitorUtils.subMonitor(monitor, 10));
			if(addOnFolder != null)
			{
				File addOnFile = new File(addOnFolder, fileName);
				if(addOnFile.exists())
				{
					logger.debug(
							"Provider %s(%s): getContents will use overlay %s for file = %s", getReaderType().getId(), ri.getRepositoryURI(), addOnFile, fileName); //$NON-NLS-1$
					MonitorUtils.worked(monitor, 90);
					return new FileHandle(fileName, addOnFile, false);
				}
			}
			return innerGetContents(fileName, MonitorUtils.subMonitor(monitor, 90));
		}
		finally
		{
			monitor.done();
		}
	}

	public final List<FileHandle> getRootFiles(Pattern matchPattern, IProgressMonitor monitor) throws CoreException,
			IOException
	{
		ArrayList<FileHandle> files = new ArrayList<FileHandle>();
		monitor.beginTask(null, 100);
		File addOnFolder = getOverlayFolder(MonitorUtils.subMonitor(monitor, 10));
		if(addOnFolder != null)
		{
			String[] names = addOnFolder.list();
			if(names != null)
			{
				for(String name : names)
				{
					if(matchPattern.matcher(name).matches())
						files.add(new FileHandle(name, new File(addOnFolder, name), false));
				}
			}
		}
		innerGetMatchingRootFiles(matchPattern, files, MonitorUtils.subMonitor(monitor, 90));
		return files;
	}

	public final List<String> list(IProgressMonitor monitor) throws CoreException
	{
		ArrayList<String> files = new ArrayList<String>();
		innerList(files, monitor);
		return files;
	}

	public final void materialize(IPath location, Resolution resolution, MaterializationContext ctx,
			IProgressMonitor monitor) throws CoreException
	{
		ProviderMatch pm = this.getProviderMatch();
		CorePlugin.getLogger().debug(
				"Provider %s(%s): materializing to %s", getReaderType().getId(), pm.getRepositoryURI(), location); //$NON-NLS-1$

		monitor.beginTask(null, 100);
		try
		{
			innerMaterialize(location, MonitorUtils.subMonitor(monitor, 80));
			copyOverlay(location, MonitorUtils.subMonitor(monitor, 10));
		}
		finally
		{
			monitor.done();
		}
	}

	public synchronized IEclipsePreferences readBuckminsterPreferences(IProgressMonitor monitor) throws CoreException
	{
		if(m_prefStateKnown)
		{
			MonitorUtils.complete(monitor);
			return m_preferences;
		}

		try
		{
			m_preferences = readFile(EclipsePreferencesReader.BUCKMINSTER_PROJECT_PREFS_PATH,
					EclipsePreferencesReader.INSTANCE, monitor);
			return m_preferences;
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
		catch(IOException e)
		{
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			m_prefStateKnown = true;
		}
	}

	public final <T> T readFile(String fileName, IStreamConsumer<T> consumer, IProgressMonitor monitor)
			throws CoreException, IOException
	{
		MonitorUtils.begin(monitor, 100);
		try
		{
			File addOnFolder = getOverlayFolder(MonitorUtils.subMonitor(monitor, 10));
			if(addOnFolder == null)
				return innerReadFile(fileName, consumer, MonitorUtils.subMonitor(monitor, 90));

			InputStream tmp = null;
			IProgressMonitor overlayReadMon = MonitorUtils.subMonitor(monitor, 10);
			try
			{
				File file = new File(addOnFolder, fileName);
				tmp = new BufferedInputStream(new FileInputStream(file));
				return consumer.consumeStream(this, file.getAbsolutePath(), tmp, overlayReadMon);
			}
			catch(FileNotFoundException e)
			{
				// This is OK, just continue with the innerReadFile
				MonitorUtils.complete(overlayReadMon);
			}
			finally
			{
				IOUtils.close(tmp);
			}
			return innerReadFile(fileName, consumer, MonitorUtils.subMonitor(monitor, 80));
		}
		finally
		{
			MonitorUtils.done(monitor);
		}
	}

	@Override
	protected void copyOverlay(IPath destination, IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask(null, 100);
		try
		{
			File addOnFolder = getOverlayFolder(MonitorUtils.subMonitor(monitor, 50));
			if(addOnFolder != null)
			{
				// Copy the addOnFolder. Overwrite is always OK for addOnFolders
				//
				File destDir = destination.toFile();
				destDir.mkdirs();
				FileUtils.deepCopyUnchecked(addOnFolder, destDir, MonitorUtils.subMonitor(monitor, 50));
			}
			else
				MonitorUtils.worked(monitor, 50);
		}
		finally
		{
			monitor.done();
		}
	}

	protected File getOverlayFolder(IProgressMonitor monitor) throws CoreException
	{
		URL overlay = getNodeQuery().getOverlayFolder();
		if(overlay == null)
		{
			MonitorUtils.complete(monitor);
			return null;
		}

		File fileOverlay;
		try
		{
			fileOverlay = FileUtils.getFile(FileLocator.toFileURL(overlay));
			if(fileOverlay == null)
				return obtainRemoteOverlayFolder(overlay, monitor);

			if(!fileOverlay.isAbsolute())
			{
				// Relative overlays are relative to the workspace root so that they
				// can reside in other projects residing in the workspace from which
				// prototyping takes place.
				//
				IPath wsRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation();
				fileOverlay = wsRoot.append(new Path(fileOverlay.toString())).toFile();
			}

			String fos = fileOverlay.toString();
			if(fos.endsWith(".zip") || fos.endsWith(".jar")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				File dest = FileUtils.createTempFolder("bmovl", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
				FileUtils.unzip(URLUtils.normalizeToURL(fos), getConnectContext(), null, dest,
						ConflictResolution.REPLACE, monitor);
				return dest;
			}

			if(!fileOverlay.isDirectory())
				throw new IllegalOverlayException(Messages.Only_folders_zip_and_jar_archives_allowed);

			// Monitor was not used for anything so make it complete
			//
			MonitorUtils.complete(monitor);
			return fileOverlay;
		}
		catch(IOException e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	protected abstract boolean innerExists(String fileName, IProgressMonitor monitor) throws CoreException;

	protected FileHandle innerGetContents(String fileName, IProgressMonitor monitor) throws CoreException, IOException
	{
		OutputStream tmp = null;
		File tempFile = null;
		try
		{
			tempFile = createTempFile();
			tmp = new FileOutputStream(tempFile);
			final OutputStream out = tmp;
			readFile(fileName, new IStreamConsumer<Object>()
			{
				public Object consumeStream(IComponentReader reader, String streamName, InputStream stream,
						IProgressMonitor mon) throws IOException
				{
					FileUtils.copyFile(stream, out, mon);
					return null;
				}
			}, monitor);
			FileHandle fh = new FileHandle(fileName, tempFile, true);
			tempFile = null;
			return fh;
		}
		catch(FileNotFoundException e)
		{
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			IOUtils.close(tmp);
			if(tempFile != null)
				tempFile.delete();
		}
	}

	protected void innerGetMatchingRootFiles(Pattern pattern, List<FileHandle> files, IProgressMonitor monitor)
			throws CoreException, IOException
	{
	}

	protected void innerList(List<String> files, IProgressMonitor monitor) throws CoreException
	{
	}

	protected abstract <T> T innerReadFile(String fileName, IStreamConsumer<T> consumer, IProgressMonitor monitor)
			throws CoreException, IOException;

	private File obtainRemoteOverlayFolder(URL url, IProgressMonitor monitor) throws CoreException
	{
		String path = url.getPath();
		if(!(path.endsWith(".zip") || path.endsWith(".jar"))) //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalOverlayException(Messages.Only_zip_and_jar_archives_allowed_for_remote_overlays);

		File dest = FileUtils.createTempFolder("bmovl", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
		FileUtils.unzip(url, getConnectContext(), null, dest, ConflictResolution.REPLACE, monitor);
		return dest;
	}
}
