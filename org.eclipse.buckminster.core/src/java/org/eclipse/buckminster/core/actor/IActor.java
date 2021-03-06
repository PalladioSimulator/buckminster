/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.actor;

import org.eclipse.buckminster.core.cspec.model.Action;
import org.eclipse.buckminster.core.metadata.model.IModelCache;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author kolwing
 * 
 */
public interface IActor {
	String getId();

	String getName();

	void init(Action action) throws CoreException;

	boolean isUpToDate(Action action, IModelCache ctx, long prerequisiteAge, long oldestInTarget) throws CoreException;

	IStatus perform(IActionContext ctx, IProgressMonitor monitor) throws CoreException;
}
