/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Cloudsmith Inc.
 *******************************************************************************/
package org.eclipse.buckminster.jnlp.p2;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * The activator class controls the plug-in life cycle
 */
public class JNLPPlugin implements BundleActivator {
	// The plug-in ID
	public static final String JNLP_P2 = "org.eclipse.buckminster.jnlp.p2"; //$NON-NLS-1$
	// The shared instance
	private static JNLPPlugin plugin;

	private BundleContext context;

	private PackageAdmin packageAdmin = null;

	private ServiceReference packageAdminRef = null;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JNLPPlugin getDefault() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public JNLPPlugin() {
		//nothing to do
	}

	public Bundle getBundle(String symbolicName) {
		if (packageAdmin == null)
			return null;
		Bundle[] bundles = packageAdmin.getBundles(symbolicName, null);
		if (bundles == null)
			return null;
		//Return the first bundle that is not installed or uninstalled
		for (int i = 0; i < bundles.length; i++) {
			if ((bundles[i].getState() & (Bundle.INSTALLED | Bundle.UNINSTALLED)) == 0) {
				return bundles[i];
			}
		}
		return null;
	}

	/**
	 * Returns the bundle context for this bundle.
	 * @return the bundle context
	 */
	public BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext aContext) throws Exception {
		this.context = aContext;
		plugin = this;
		packageAdminRef = context.getServiceReference(PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) context.getService(packageAdminRef);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext aContext) throws Exception {
		context.ungetService(packageAdminRef);
		packageAdmin = null;
		packageAdminRef = null;
		plugin = null;
	}
}
