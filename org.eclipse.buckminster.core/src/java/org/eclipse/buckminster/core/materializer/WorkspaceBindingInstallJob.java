/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.materializer;

import java.util.Arrays;

import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.metadata.StorageManager;
import org.eclipse.buckminster.core.metadata.model.WorkspaceBinding;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Thomas Hallgren
 */
public class WorkspaceBindingInstallJob extends WorkspaceJob
{
	private static class Repeater extends JobChangeAdapter
	{
		@Override
		public void done(IJobChangeEvent event)
		{
			Job active = s_active;
			if(active != null)
				active.schedule(REPEAT_DELAY);
		}
	}

	private static WorkspaceBindingInstallJob s_active = null;

	public static final int REPEAT_DELAY = 60000;

	public static void start()
	{
		if(s_active != null)
			return;

		s_active = new WorkspaceBindingInstallJob();
		s_active.addJobChangeListener(new Repeater());
		s_active.schedule(5000);
	}

	public static void stop()
	{
		Job active = s_active;
		s_active = null;
		if(active != null)
			active.cancel();
	}

	private WorkspaceBindingInstallJob()
	{
		super(Messages.WorkspaceBindingInstallJob_workspace_binding_installer);
		setPriority(Job.BUILD);
		setSystem(true);
		setUser(false);
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
	{
		StorageManager sm = StorageManager.getDefault();
		WorkspaceBinding[] wsBindings = sm.getWorkspaceBindings().getElements();
		if(wsBindings.length == 0)
		{
			MonitorUtils.complete(monitor);
			return Status.OK_STATUS;
		}

		monitor.beginTask(null, wsBindings.length * 100);
		WorkspaceMaterializer wsMaterializer = new WorkspaceMaterializer();

		// Sort by timestamp
		//
		Arrays.sort(wsBindings);
		for(WorkspaceBinding wsBinding : wsBindings)
		{
			wsMaterializer.installLocal(wsBinding, new RMContext(wsBinding.getProperties()), MonitorUtils.subMonitor(
					monitor, 100));
			wsBinding.remove(sm);
		}
		monitor.done();
		return Status.OK_STATUS;
	}
}
