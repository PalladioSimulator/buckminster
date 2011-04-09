/*******************************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.pde.test;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.buckminster.pde.mapfile.MapFile;
import org.eclipse.buckminster.pde.mapfile.MapFileEntry;
import org.eclipse.buckminster.runtime.BuckminsterPreferences;
import org.eclipse.buckminster.runtime.Logger;

/**
 * @author Thomas Hallgren
 * 
 */
public class MapFileTest extends TestCase {
	@Override
	public void setUp() throws Exception {
		BuckminsterPreferences.setLogLevelConsole(Logger.DEBUG);
		BuckminsterPreferences.setLogLevelEclipseLogger(Logger.SILENT);
	}

	public void testMap() throws Exception {
		ArrayList<MapFileEntry> bld = new ArrayList<MapFileEntry>();
		URL url = new URL("http://download.eclipse.org/tools/orbit/downloads/drops/R20100114021427/directory.txt"); //$NON-NLS-1$
		InputStream input = url.openStream();
		MapFile.parse(input, url.toString(), null, null, bld);
		input.close();

		url = new URL("http://download.eclipse.org/tools/orbit/downloads/drops/R20100114021427/orbitBundles-R20100114021427.map"); //$NON-NLS-1$
		input = url.openStream();
		MapFile.parse(input, url.toString(), null, null, bld);
		input.close();

		for (MapFileEntry entry : bld)
			System.out.println(entry);
	}
}
