/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.materializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.metadata.model.Resolution;
import org.eclipse.buckminster.core.mspec.model.MaterializationSpec;
import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.BuckminsterPreferences;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

/**
 * A job that will materialize according to specifications.
 * 
 * @author Thomas Hallgren
 */
public class MaterializationJob extends Job {
	public static final String MAX_PARALLEL_JOBS = "maxParallelMaterializationJobs"; //$NON-NLS-1$

	public static final int MAX_PARALLEL_JOBS_DEFAULT = 4;

	public static int getMaxParallelJobs() {
		return BuckminsterPreferences.getNode().getInt(MAX_PARALLEL_JOBS, MAX_PARALLEL_JOBS_DEFAULT);
	}

	public static void run(MaterializationContext context) throws CoreException {
		try {
			MaterializationJob mbJob = new MaterializationJob(context);
			mbJob.schedule();
			mbJob.join(); // long running
			IStatus status = mbJob.getResult();

			if (status.getSeverity() == IStatus.CANCEL)
				throw new OperationCanceledException();

			// We wait to give the event manager a chance to deliver all
			// events while the JobBlocker still active. This gives us
			// a chance to add dynamic dependencies to projects
			//
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Throwable t) {
			throw BuckminsterException.wrap(t);
		}
	}

	/**
	 * Runs this job immediately without scheduling. This method is intended to
	 * be called when the materialization is done from the GUI and uses a
	 * <code>IRunnableWithProgress</code>
	 * 
	 * @param context
	 * @param monitor
	 * @throws CoreException
	 */
	public static void runDelegated(MaterializationContext context, IProgressMonitor monitor) throws CoreException {
		MaterializationJob mbJob = new MaterializationJob(context);
		mbJob.internalRun(monitor, false);
	}

	public static void setMaxParallelJobs(int maxJobs) {
		if (maxJobs > 0 && maxJobs <= 20)
			BuckminsterPreferences.getNode().putInt(MAX_PARALLEL_JOBS, maxJobs);
	}

	public static void setUp() {
		IEclipsePreferences defaultNode = BuckminsterPreferences.getDefaultNode();
		defaultNode.putInt(MAX_PARALLEL_JOBS, MAX_PARALLEL_JOBS_DEFAULT);
		try {
			defaultNode.flush();
		} catch (BackingStoreException e) {
			Buckminster.getLogger().error(e, e.toString());
		}
	}

	private final MaterializationContext context;

	public MaterializationJob(MaterializationContext ctx) {
		super(Messages.Materializing);
		context = ctx;

		// Report using the standard job reporter.
		//
		this.setSystem(false);
		this.setUser(false);
		this.setPriority(LONG);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			internalRun(monitor, true);
		} catch (CoreException e) {
			CorePlugin.getLogger().error(e, e.getMessage());
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		}
		context.emitWarningAndErrorTags();
		return Status.OK_STATUS;
	}

	protected MaterializationContext getMaterializationContext() {
		return context;
	}

	protected void internalRun(final IProgressMonitor monitor, boolean waitForCompletion) throws CoreException {
		BillOfMaterials bom = context.getBillOfMaterials();

		Queue<MaterializerJob> allJobs = prepareJobs(monitor, bom);

		if (allJobs != null) {
			triggerJobs(monitor, allJobs);
			waitForJobs(monitor, allJobs, bom);
		}
		if (context.getStatus().getSeverity() == IStatus.ERROR)
			throw BuckminsterException.wrap(context.getStatus());

		InstallerJob installerJob = new InstallerJob(context, !waitForCompletion);
		installerJob.schedule();
		if (waitForCompletion) {
			try {
				installerJob.join();
			} catch (InterruptedException e) {
				throw new OperationCanceledException();
			}
		}
	}

	protected Queue<MaterializerJob> prepareJobs(IProgressMonitor monitor, BillOfMaterials bom) throws CoreException {
		CorePlugin corePlugin = CorePlugin.getDefault();
		Map<String, List<Resolution>> resPerMat = new LinkedHashMap<String, List<Resolution>>();
		MaterializationSpec mspec = context.getMaterializationSpec();
		for (Resolution cr : bom.findMaterializationCandidates(context, mspec)) {
			String materializer = mspec.getMaterializerID(cr);
			List<Resolution> crs = resPerMat.get(materializer);
			if (crs == null) {
				crs = new ArrayList<Resolution>();
				resPerMat.put(materializer, crs);
			}
			crs.add(cr);
		}

		if (resPerMat.size() == 0)
			return null;

		final Queue<MaterializerJob> allJobs = new ConcurrentLinkedQueue<MaterializerJob>();
		for (Map.Entry<String, List<Resolution>> entry : resPerMat.entrySet()) {
			IMaterializer materializer = corePlugin.getMaterializer(entry.getKey());
			List<Resolution> resolutions = entry.getValue();
			if (materializer.canWorkInParallel()) {
				// Start one job for each resolution
				//
				for (Resolution res : resolutions)
					allJobs.offer(new MaterializerJob(entry.getKey(), materializer, Collections.singletonList(res), context));
			} else
				allJobs.offer(new MaterializerJob(entry.getKey(), materializer, resolutions, context));
		}

		return allJobs;
	}

	protected void triggerJobs(final IProgressMonitor monitor, final Queue<MaterializerJob> allJobs) {
		// -- Schedule at most maxParallelJobs. After that, let the
		// termination of
		// a job schedule a new one until the queue is empty.
		//
		// This is the listener that schedule a new job on termination of
		// another
		//
		IJobChangeListener listener = new JobChangeAdapter() {
			@Override
			public void aboutToRun(IJobChangeEvent event) {
				if (monitor.isCanceled() || (!context.isContinueOnError() && context.getStatus().getSeverity() == IStatus.ERROR))
					cancel();
			}

			@Override
			public void done(IJobChangeEvent event) {
				if (!monitor.isCanceled()) {
					MaterializerJob mjob = allJobs.poll();
					if (mjob != null) {
						mjob.addJobChangeListener(this);
						mjob.schedule();
					}
				}
			}
		};

		int maxJobs = context.getMaxParallelJobs();
		for (int idx = 0; idx < maxJobs; ++idx) {
			MaterializerJob job = allJobs.poll();
			if (job == null)
				break;

			job.addJobChangeListener(listener);
			job.schedule();
		}
	}

	protected void waitForJobs(IProgressMonitor monitor, Queue<MaterializerJob> allJobs, BillOfMaterials bom) throws CoreException {
		// Wait until all jobs have completed
		//
		IJobManager jobManager = Job.getJobManager();
		try {
			jobManager.join(context, monitor);
		} catch (OperationCanceledException e) {
			jobManager.cancel(context);
			allJobs.clear();
			throw e;
		} catch (InterruptedException e) {
			jobManager.cancel(context);
			allJobs.clear();
			throw new OperationCanceledException();
		}
	}
}
