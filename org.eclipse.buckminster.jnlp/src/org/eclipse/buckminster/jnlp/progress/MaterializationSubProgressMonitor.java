/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.jnlp.progress;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Karel Brezina
 * 
 */
public class MaterializationSubProgressMonitor implements IProgressMonitor
{
	private static final String PROGRESS_STOP = "progress_stop.gif";

	private static final int MAX_LABEL_LENGTH = 90;

	private static final long DONE_SLEEP = 2000;
	
	private static final String AVAILABLE = "available";

	private Composite m_parentComposite;

	private Composite m_composite;
	
	private String m_jobName;

	private Label m_subTaskLabel;

	private ProgressBar m_progressBar;

	private ToolBar m_cancelBar;

	private ToolItem m_cancelButton;

	private boolean m_canceled = false;

	public MaterializationSubProgressMonitor(Composite parent, Job job)
	{
		m_parentComposite = parent;
		m_jobName = job == null ? "operation" : job.getName();
		//System.out.println("Create");
	}

	private Control createControl(Composite parent)
	{
		// Try to reuse Composite to avoid flickering
		m_composite = null;
		for(Control progressComposite : parent.getChildren())
		{
			if(AVAILABLE.equals(progressComposite.getData()))
			{
				m_composite = (Composite)progressComposite;
				m_composite.setData(null);
				
				for(Control control : m_composite.getChildren())
				{
					control.dispose();
				}
				break;
			}
		}
		
		if(m_composite == null)
		{
			m_composite = new Composite(parent, SWT.NONE);
		}
		m_composite.setLayout(new GridLayout(2, false));
		m_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		m_subTaskLabel = new Label(m_composite, SWT.NONE);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		m_subTaskLabel.setLayoutData(layoutData);

		m_progressBar = new ProgressBar(m_composite, SWT.NONE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		m_progressBar.setLayoutData(layoutData);

		m_cancelBar = new ToolBar(m_composite, SWT.FLAT);
		m_cancelButton = new ToolItem(m_cancelBar, SWT.PUSH);
		// cancelToolBar.pack ();

		// m_cancelButton = new Button(m_composite, SWT.TOGGLE);
		m_cancelButton.setImage(getImage(PROGRESS_STOP));
		m_cancelButton.setEnabled(!isCanceled());
		
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setCanceled(true);
			}
		});

		m_composite.setVisible(true);

		m_parentComposite.layout(new Control[] { m_composite });

		return m_composite;
	}

	private Image getImage(String imageName)
	{
		Class<?> myClass = this.getClass();
		String imageResource = "/icons/" + imageName;
		URL imageUrl = myClass.getResource(imageResource);
		return ImageDescriptor.createFromURL(imageUrl).createImage();
	}

	public void beginTask(final String name, final int totalWork)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				createControl(m_parentComposite);
				setTaskName(name);
				m_progressBar.setMinimum(0);
				m_progressBar.setMaximum(totalWork);
			}
		});
		//System.out.println("Begin");
	}

	public void done()
	{
		if(m_cancelButton != null)
		{
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					m_cancelButton.setEnabled(false);
				}
			});
		}

		subTask(m_jobName + " completed");
		
		try
		{
			Thread.sleep(DONE_SLEEP);
		}
		catch(InterruptedException e)
		{
			// nothing
		}

		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				m_composite.setData(AVAILABLE);
				m_composite.setVisible(false);
				m_composite.update();
			}
		});
		//System.out.println("Done");
	}

	public void internalWorked(final double work)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				m_progressBar.setSelection(m_progressBar.getSelection() + Double.valueOf(work).intValue());
				// System.out.println("JOB: " + m_subTaskLabel.getText() + " " + m_progressBar.getSelection() + "/" +
				// m_progressBar.getMaximum() + " - " + work);
			}
		});
	}

	public boolean isCanceled()
	{
		return m_canceled;
	}

	public void setCanceled(boolean value)
	{
		m_canceled = value;

		if(m_cancelButton != null)
		{
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					m_cancelButton.setEnabled(false);
				}
			});
		}
		//System.out.println("Cancel");
	}

	public void setTaskName(String name)
	{
		// nothing
	}

	public void subTask(final String name)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				m_subTaskLabel.setText(name == null
						? ""
						: (name.length() > MAX_LABEL_LENGTH
								? name.substring(0, MAX_LABEL_LENGTH - 3) + "..."
								: name));
				m_composite.layout(new Control[] { m_subTaskLabel });
			}
		});
	}

	public void worked(final int work)
	{
		internalWorked(work);
	}
}
