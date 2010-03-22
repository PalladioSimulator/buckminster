/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.pde.test;

import java.net.URL;
import java.util.Map;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.common.model.Format;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.ctype.IResolutionBuilder;
import org.eclipse.buckminster.core.helpers.BMProperties;
import org.eclipse.buckminster.core.metadata.model.BOMNode;
import org.eclipse.buckminster.core.metadata.model.Resolution;
import org.eclipse.buckminster.core.reader.IComponentReader;
import org.eclipse.buckminster.core.reader.IReaderType;
import org.eclipse.buckminster.core.resolver.IResolver;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.pde.PDEPlugin;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class PDEBuilderTest extends PDETestCase
{
	public void testPluginCompiler() throws Exception
	{
		PDEPlugin pdePlugin = PDEPlugin.getDefault();
		if(pdePlugin == null)
			throw new Exception("This test must be run as a \"JUnit Plug-in Test\""); //$NON-NLS-1$

		Map<String, String> props = BMProperties.getSystemProperties();
		IProgressMonitor nulMon = new NullProgressMonitor();
		String[] componentTypes = new String[] { IComponentType.OSGI_BUNDLE, IComponentType.ECLIPSE_FEATURE };
		IResolver resolver = this.createResolver(pdePlugin.toString(), IComponentType.OSGI_BUNDLE);
		CorePlugin corePlugin = CorePlugin.getDefault();
		URL location = FileLocator.toFileURL(pdePlugin.getBundle().getEntry("/")); //$NON-NLS-1$
		Provider provider = new Provider(null, IReaderType.URL_CATALOG, componentTypes, null, new Format(
				location.toString()), null, null, null, props, null, null);

		IComponentType bundleType = CorePlugin.getDefault().getComponentType(IComponentType.OSGI_BUNDLE);
		IReaderType readerType = provider.getReaderType();
		IComponentReader[] reader = new IComponentReader[] { readerType.getReader(provider, bundleType,
				resolver.getContext().getRootNodeQuery(), null, nulMon) };

		IResolutionBuilder builder = corePlugin.getResolutionBuilder("plugin2cspec"); //$NON-NLS-1$
		BOMNode node = builder.build(reader, false, nulMon);
		IOUtils.close(reader[0]);

		Resolution resolution = node.getResolution();
		assertNotNull("Resolution is null", resolution);
		Utils.serialize(resolution.getCSpec(), System.out);

		location = FileLocator.toFileURL(Platform.getBundle("org.junit").getEntry("/")); //$NON-NLS-1$ //$NON-NLS-2$
		resolver = this.createResolver("org.junit", null); //$NON-NLS-1$
		provider = new Provider(null, IReaderType.URL_CATALOG, componentTypes, null, new Format(location.toString()),
				null, null, null, props, null, null);

		reader[0] = readerType.getReader(provider, bundleType, resolver.getContext().getRootNodeQuery(), null, nulMon);
		node = builder.build(reader, false, nulMon);
		IOUtils.close(reader[0]);

		resolution = node.getResolution();
		assertNotNull("Resolution is null", resolution);
		Utils.serialize(resolution.getCSpec(), System.out);
		if(reader[0] != null)
			reader[0].close();
	}
}
