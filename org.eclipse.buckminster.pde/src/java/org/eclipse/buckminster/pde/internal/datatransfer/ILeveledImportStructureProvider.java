/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.buckminster.pde.internal.datatransfer;

/**
 * Interface which can provide structure and content information for an element
 * (for example, a file system element). Used by the import wizards to abstract
 * the commonalities between importing from the file system and importing from
 * an archive.
 * 
 * @since 3.1
 */
interface ILeveledImportStructureProvider extends IImportStructureProvider {
	/**
	 * Returns the entry that this importer uses as the root sentinel.
	 * 
	 * @return TarEntry entry
	 */
	public abstract Object getRoot();

	/**
	 * Tells the provider to strip N number of directories from the path of any
	 * path or file name returned by the IImportStructureProvider (Default=0).
	 * 
	 * @param level
	 *            The number of directories to strip
	 */
	public abstract void setStrip(int level);

	/**
	 * Returns the number of directories that this IImportStructureProvider is
	 * stripping from the file name
	 * 
	 * @return int Number of entries
	 */
	public abstract int getStrip();
}
