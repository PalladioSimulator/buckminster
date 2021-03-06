/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.buckminster.ant.tasks.SignatureCleanerTask;
import org.eclipse.buckminster.ant.types.FileSetGroup;

/**
 * @author Thomas Hallgren
 */
public class SignatureCleaner extends Task {
	private final ArrayList<FileSet> fileSets = new ArrayList<FileSet>();

	private ArrayList<FileSetGroup> fileSetGroups;

	/**
	 * Adds a nested <code>&lt;filesetgroup&gt;</code> element.
	 */
	public void add(FileSetGroup fsGroup) throws BuildException {
		if (fileSetGroups == null)
			fileSetGroups = new ArrayList<FileSetGroup>();
		fileSetGroups.add(fsGroup);
	}

	public void addFileset(FileSet fileSet) {
		fileSets.add(fileSet);
	}

	@Override
	public void execute() throws BuildException {
		if (fileSetGroups != null) {
			for (FileSetGroup fsg : fileSetGroups)
				for (FileSet fs : fsg.getFileSets())
					addFileset(fs);
			fileSetGroups = null;
		}

		Project proj = getProject();
		ArrayList<File> jarFiles = new ArrayList<File>();
		for (FileSet fs : fileSets) {
			DirectoryScanner ds = fs.getDirectoryScanner(proj);
			File dir = fs.getDir(proj);
			for (String file : ds.getIncludedFiles())
				jarFiles.add(new File(dir, file));
		}
		SignatureCleanerTask cleaner = new SignatureCleanerTask(jarFiles);
		try {
			cleaner.clean();
		} catch (Exception e) {
			throw new BuildException(e.toString(), e, this.getLocation());
		}
	}
}
