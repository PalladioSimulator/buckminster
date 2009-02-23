package org.eclipse.buckminster.pde.cspecgen.feature;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.buckminster.ant.actor.AntActor;
import org.eclipse.buckminster.core.cspec.builder.ActionBuilder;
import org.eclipse.buckminster.core.cspec.builder.ArtifactBuilder;
import org.eclipse.buckminster.core.cspec.builder.CSpecBuilder;
import org.eclipse.buckminster.core.cspec.builder.ComponentRequestBuilder;
import org.eclipse.buckminster.core.cspec.builder.GroupBuilder;
import org.eclipse.buckminster.core.cspec.builder.PrerequisiteBuilder;
import org.eclipse.buckminster.core.cspec.model.UpToDatePolicy;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.helpers.FilterUtils;
import org.eclipse.buckminster.core.helpers.TextUtils;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.reader.ICatalogReader;
import org.eclipse.buckminster.core.version.IVersionType;
import org.eclipse.buckminster.jarprocessor.JarProcessorActor;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.osgi.filter.FilterFactory;
import org.eclipse.buckminster.pde.Messages;
import org.eclipse.buckminster.pde.cspecgen.CSpecGenerator;
import org.eclipse.buckminster.pde.tasks.P2SiteGenerator;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.plugin.IMatchRules;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PluginModelManager;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.osgi.framework.InvalidSyntaxException;

@SuppressWarnings("restriction")
public class CSpecFromSource extends CSpecGenerator
{
	private static final String ACTION_COPY_FEATURES = "copy.features"; //$NON-NLS-1$

	private static final String ACTION_COPY_SITE_FEATURES = "copy.subfeatures"; //$NON-NLS-1$

	private static final String ATTRIBUTE_FEATURE_REFS = "feature.references"; //$NON-NLS-1$

	private static final String ATTRIBUTE_SOURCE_FEATURE_REFS = "source.feature.references"; //$NON-NLS-1$

	private static final String ATTRIBUTE_INTERNAL_PRODUCT_ROOT = "internal.product.root"; //$NON-NLS-1$

	private static final Filter SIGNING_ENABLED;

	private static final Filter SIGNING_DISABLED;

	private static final Filter PACK_ENABLED;

	private static final Filter PACK_DISABLED;

	private static final Filter SIGNING_AND_PACK_DISABLED;

	private static final Filter SIGNING_ENABLED_AND_PACK_DISABLED;

	static
	{
		try
		{
			SIGNING_ENABLED = FilterFactory.newInstance("(site.signing=true)");
			SIGNING_DISABLED = FilterFactory.newInstance("(!(site.signing=true))");
			PACK_ENABLED = FilterFactory.newInstance("(site.pack200=true)");
			PACK_DISABLED = FilterFactory.newInstance("(!(site.pack200=true))");
			SIGNING_AND_PACK_DISABLED = FilterFactory.newInstance("(&(!(site.pack200=true))(!(site.signing=true)))");
			SIGNING_ENABLED_AND_PACK_DISABLED = FilterFactory
					.newInstance("(&(!(site.pack200=true))(site.signing=true))");
		}
		catch(InvalidSyntaxException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}

	private static boolean isListOK(String list, Object item)
	{
		if(list == null || list.length() == 0)
			return true;
		StringTokenizer tokens = new StringTokenizer(list, ","); //$NON-NLS-1$
		while(tokens.hasMoreTokens())
			if(item.equals("*") || item.equals(tokens.nextElement())) //$NON-NLS-1$
				return true;
		return false;
	}

	private final Map<String, String> m_buildProperties;

	private final IFeature m_feature;

	protected CSpecFromSource(CSpecBuilder cspecBuilder, ICatalogReader reader, IFeature feature,
			Map<String, String> buildProperties)
	{
		super(cspecBuilder, reader);
		m_feature = feature;
		m_buildProperties = buildProperties;
	}

	@Override
	public void generate(IProgressMonitor monitor) throws CoreException
	{
		CSpecBuilder cspec = getCSpec();
		cspec.setName(m_feature.getId());
		cspec.setVersion(m_feature.getVersion(), IVersionType.OSGI);
		cspec.setComponentTypeID(IComponentType.ECLIPSE_FEATURE);
		cspec.setFilter(FilterUtils.createFilter(m_feature.getOS(), m_feature.getWS(), m_feature.getArch(), m_feature
				.getNL()));

		// This feature and all included features. Does not imply copying since
		// the group will reference the features where they are found.
		//
		GroupBuilder featureRefs = cspec.addGroup(ATTRIBUTE_FEATURE_REFS, true); // without self
		cspec.addGroup(ATTRIBUTE_SOURCE_FEATURE_REFS, true).setFilter(SOURCE_FILTER);

		// All bundles imported by this feature and all included features. Does
		// not imply copying since the group will reference the bundles where they
		// are found.
		//
		cspec.addGroup(ATTRIBUTE_BUNDLE_JARS, true);

		// Source of all bundles imported by this feature and all included features.
		//
		cspec.addGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS, true).setFilter(SOURCE_FILTER);

		// This feature, its imported bundles, all included features, and their
		// imported bundles copied into a site structure (with a plugins and a
		// features folder).
		//
		cspec.addGroup(ATTRIBUTE_FEATURE_EXPORTS, true);
		cspec.addGroup(ATTRIBUTE_SITE_FEATURE_EXPORTS, true);

		cspec.addGroup(ATTRIBUTE_PRODUCT_ROOT_FILES, true);
		generateRemoveDirAction("build", OUTPUT_DIR, true, ATTRIBUTE_FULL_CLEAN); //$NON-NLS-1$

		addFeatures();
		addPlugins();

		MonitorUtils.begin(monitor, 100);
		if(m_buildProperties == null)
		{
			ArtifactBuilder binIncludes = null;
			for(String path : getReader().list(MonitorUtils.subMonitor(monitor, 20)))
			{
				if(FEATURE_FILE.equals(path))
					//
					// Handled separately
					//
					continue;

				if(binIncludes == null)
					binIncludes = getCSpec().addArtifact(ATTRIBUTE_JAR_CONTENTS, false, null, null);
				binIncludes.addPath(new Path(path));
			}
		}
		else
		{
			cspec.addArtifact(ATTRIBUTE_BUILD_PROPERTIES, false, null, null).addPath(new Path(BUILD_PROPERTIES_FILE));
			for(Map.Entry<String, String> entry : m_buildProperties.entrySet())
			{
				String key = entry.getKey();
				if(IBuildEntry.BIN_INCLUDES.equals(key))
				{
					createBinIncludesArtifact(entry.getValue());
					continue;
				}

				if(!key.startsWith(ROOT))
					continue;

				Filter filter = null;
				String[] permSpec = null;
				String permStr = null;
				if(key.length() > ROOT.length())
				{
					if(key.charAt(ROOT.length()) != '.')
						continue;

					String[] s = TextUtils.split(key.substring(ROOT.length() + 1), "."); //$NON-NLS-1$
					switch(s.length)
					{
					case 2: // permissions.digits
						permStr = s[1];
						break;
					case 3: // os.ws.arch
						filter = FilterUtils.createFilter(s[0], s[1], s[2], null);
						break;
					case 5: // os.ws.arch.permissions.digits
						permSpec = s;
						permStr = s[4];
						break;
					}
				}

				if(permStr != null)
					FeatureBuilder.addRootsPermissions(featureRefs.getInstallerHintsForAdd(), permStr,
							entry.getValue(), permSpec);
				else
					createRootsArtifact(entry.getValue(), filter);
			}

			GroupBuilder productRoots = cspec.getGroup(ATTRIBUTE_INTERNAL_PRODUCT_ROOT);
			if(productRoots != null)
			{
				List<PrerequisiteBuilder> preqs = productRoots.getPrerequisites();
				if(preqs.size() == 1)
				{
					// Replace the internal product root group with it's one and only
					// prerequisite.
					//
					String prName = productRoots.getName();
					ArtifactBuilder artifact = cspec.getArtifactBuilder(preqs.iterator().next().getName());
					cspec.removeAttribute(prName);

					cspec.removeAttribute(artifact.getName());
					artifact.setName(prName);
					cspec.addAttribute(artifact);
				}
			}
			MonitorUtils.worked(monitor, 20);
		}

		createFeatureManifestAction();
		createFeatureJarAction();
		createFeatureSourceManifestAction();
		createFeatureSourceJarAction();
		createFeatureExportsAction();
		createSiteFeatureExportsAction();
		createSiteRepackAction();
		createSiteSignAction();
		createSitePackAction();
		createSiteAction();
		addProducts(MonitorUtils.subMonitor(monitor, 80));
		MonitorUtils.done(monitor);
	}

	@Override
	protected String getProductOutputFolder(String productId)
	{
		return m_buildProperties == null
				? null
				: m_buildProperties.get(productId + TOP_FOLDER_SUFFIX);
	}

	@Override
	protected String getPropertyFileName()
	{
		return FEATURE_PROPERTIES_FILE;
	}

	void addFeatures() throws CoreException
	{
		IFeatureChild[] features = m_feature.getIncludedFeatures();
		if(features == null || features.length == 0)
			return;

		ComponentQuery query = getReader().getNodeQuery().getComponentQuery();
		CSpecBuilder cspec = getCSpec();
		ActionBuilder fullClean = cspec.getRequiredAction(ATTRIBUTE_FULL_CLEAN);
		GroupBuilder featureRefs = cspec.getRequiredGroup(ATTRIBUTE_FEATURE_REFS);
		GroupBuilder featureSourceRefs = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_FEATURE_REFS);
		GroupBuilder bundleJars = cspec.getRequiredGroup(ATTRIBUTE_BUNDLE_JARS);
		GroupBuilder sourceBundleJars = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS);
		GroupBuilder productRootFiles = cspec.getRequiredGroup(ATTRIBUTE_PRODUCT_ROOT_FILES);
		for(IFeatureChild feature : features)
		{
			ComponentRequestBuilder dep = createDependency(feature);
			if(skipComponent(query, dep))
				continue;

			cspec.addDependency(dep);
			featureRefs.addExternalPrerequisite(dep.getName(), ATTRIBUTE_FEATURE_JARS);
			featureSourceRefs.addExternalPrerequisite(dep.getName(), ATTRIBUTE_SOURCE_FEATURE_JARS);
			bundleJars.addExternalPrerequisite(dep.getName(), ATTRIBUTE_BUNDLE_JARS);
			sourceBundleJars.addExternalPrerequisite(dep.getName(), ATTRIBUTE_SOURCE_BUNDLE_JARS);
			fullClean.addExternalPrerequisite(dep.getName(), ATTRIBUTE_FULL_CLEAN);
			productRootFiles.addExternalPrerequisite(dep.getName(), ATTRIBUTE_PRODUCT_ROOT_FILES);
		}
	}

	void addPlugins() throws CoreException
	{
		IFeaturePlugin[] plugins = m_feature.getPlugins();
		if(plugins == null || plugins.length == 0)
			return;

		Map<String, ? extends Object> props = getReader().getNodeQuery().getProperties();
		Object os = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_OS);
		Object ws = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_WS);
		Object arch = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_ARCH);

		ComponentQuery query = getReader().getNodeQuery().getComponentQuery();
		CSpecBuilder cspec = getCSpec();
		ActionBuilder fullClean = cspec.getRequiredAction(ATTRIBUTE_FULL_CLEAN);
		GroupBuilder bundleJars = cspec.getRequiredGroup(ATTRIBUTE_BUNDLE_JARS);
		GroupBuilder sourceBundleJars = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS);
		PluginModelManager manager = PDECore.getDefault().getModelManager();
		for(IFeaturePlugin plugin : plugins)
		{
			if(!(isListOK(plugin.getOS(), os) && isListOK(plugin.getWS(), ws) && isListOK(plugin.getArch(), arch)))
				if(manager.findEntry(plugin.getId()) == null)
					continue;

			ComponentRequestBuilder dep = createDependency(plugin);
			if(skipComponent(query, dep))
				continue;

			if(!addDependency(dep))
				continue;

			bundleJars.addExternalPrerequisite(dep.getName(), ATTRIBUTE_BUNDLE_AND_FRAGMENTS);
			sourceBundleJars.addExternalPrerequisite(dep.getName(), ATTRIBUTE_BUNDLE_AND_FRAGMENTS_SOURCE);
			fullClean.addExternalPrerequisite(dep.getName(), ATTRIBUTE_FULL_CLEAN);
		}
	}

	ComponentRequestBuilder createDependency(IFeatureChild feature) throws CoreException
	{
		Filter filter = FilterUtils.createFilter(feature.getOS(), feature.getWS(), feature.getArch(), feature.getNL());
		return createDependency(feature.getId(), IComponentType.ECLIPSE_FEATURE, feature.getVersion(), feature
				.getMatch(), filter);
	}

	ComponentRequestBuilder createDependency(IFeaturePlugin plugin) throws CoreException
	{
		Filter filter = FilterUtils.createFilter(plugin.getOS(), plugin.getWS(), plugin.getArch(), plugin.getNL());
		return createDependency(plugin.getId(), IComponentType.OSGI_BUNDLE, plugin.getVersion(), IMatchRules.PERFECT,
				filter);
	}

	private void createBinIncludesArtifact(String binIncludesStr) throws CoreException
	{
		ArtifactBuilder binIncludes = null;
		StringTokenizer tokens = new StringTokenizer(binIncludesStr, ","); //$NON-NLS-1$
		while(tokens.hasMoreTokens())
		{
			String path = tokens.nextToken().trim();
			if(FEATURE_FILE.equals(path) || BUILD_PROPERTIES_FILE.equals(path))
				//
				// Handled separately
				//
				continue;

			if(binIncludes == null)
				binIncludes = getCSpec().addArtifact(ATTRIBUTE_JAR_CONTENTS, false, null, null);
			binIncludes.addPath(new Path(path));
		}
	}

	private ActionBuilder createCopyFeaturesAction() throws CoreException
	{
		// Copy all features (including this one) to the features directory.
		//
		ActionBuilder copyFeatures = addAntAction(ACTION_COPY_FEATURES, TASK_COPY_GROUP, false);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_FEATURE_JARS);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_JARS);
		copyFeatures.setPrerequisitesAlias(ALIAS_REQUIREMENTS);
		copyFeatures.setProductAlias(ALIAS_OUTPUT);
		copyFeatures.setProductBase(OUTPUT_DIR_SITE.append(FEATURES_FOLDER));
		copyFeatures.setUpToDatePolicy(UpToDatePolicy.MAPPER);
		return copyFeatures;
	}

	private ActionBuilder createCopySiteFeaturesAction() throws CoreException
	{
		// Copy all features (excluding this one) to the features directory.
		//
		ActionBuilder copyFeatures = addAntAction(ACTION_COPY_SITE_FEATURES, TASK_COPY_GROUP, false);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_FEATURE_REFS);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_REFS);
		copyFeatures.setPrerequisitesAlias(ALIAS_REQUIREMENTS);
		copyFeatures.setProductAlias(ALIAS_OUTPUT);
		copyFeatures.setProductBase(OUTPUT_DIR_SITE.append(FEATURES_FOLDER));
		copyFeatures.setUpToDatePolicy(UpToDatePolicy.MAPPER);
		return copyFeatures;
	}

	private void createFeatureExportsAction() throws CoreException
	{
		GroupBuilder featureExports = getCSpec().getRequiredGroup(ATTRIBUTE_FEATURE_EXPORTS);
		featureExports.addLocalPrerequisite(createCopyFeaturesAction());
		featureExports.addLocalPrerequisite(createCopyPluginsAction());
		featureExports.setPrerequisiteRebase(OUTPUT_DIR_SITE);
	}

	private void createFeatureJarAction() throws CoreException
	{
		CSpecBuilder cspec = getCSpec();

		// Create the action that builds the final jar file for the feature
		//
		ActionBuilder featureJarBuilder = addAntAction(ATTRIBUTE_FEATURE_JAR, TASK_CREATE_FEATURE_JAR, false);
		featureJarBuilder.addLocalPrerequisite(ATTRIBUTE_MANIFEST, ALIAS_MANIFEST);

		if(cspec.getArtifactBuilder(ATTRIBUTE_JAR_CONTENTS) != null)
			featureJarBuilder.addLocalPrerequisite(ATTRIBUTE_JAR_CONTENTS);
		featureJarBuilder.setPrerequisitesAlias(ALIAS_REQUIREMENTS);

		featureJarBuilder.setProductAlias(ALIAS_OUTPUT);
		featureJarBuilder.setProductBase(OUTPUT_DIR_JAR);
		featureJarBuilder.setUpToDatePolicy(UpToDatePolicy.COUNT);
		featureJarBuilder.setProductFileCount(1);

		GroupBuilder featureJars = cspec.addGroup(ATTRIBUTE_FEATURE_JARS, true); // including self
		featureJars.addLocalPrerequisite(featureJarBuilder);
		featureJars.addLocalPrerequisite(ATTRIBUTE_FEATURE_REFS);
	}

	private void createFeatureManifestAction() throws CoreException
	{
		// Create the artifact that represents the original feature.xml file
		//
		IPath featureFile = new Path(FEATURE_FILE);
		ArtifactBuilder rawManifest = getCSpec().addArtifact(ATTRIBUTE_RAW_MANIFEST, false, null, null);
		rawManifest.addPath(featureFile);

		// Create the action that creates the version expanded feature.xml
		//
		ActionBuilder manifest = addAntAction(ATTRIBUTE_MANIFEST, TASK_EXPAND_FEATURE_VERSION, true);
		manifest.addLocalPrerequisite(ATTRIBUTE_RAW_MANIFEST, ALIAS_MANIFEST);
		manifest.addLocalPrerequisite(ATTRIBUTE_BUNDLE_JARS, ALIAS_BUNDLES);
		manifest.addLocalPrerequisite(ATTRIBUTE_FEATURE_REFS, ALIAS_FEATURES);
		if(getCSpec().getAttribute(ATTRIBUTE_BUILD_PROPERTIES) != null)
			manifest.addLocalPrerequisite(ATTRIBUTE_BUILD_PROPERTIES, ALIAS_PROPERTIES);

		manifest.setProductAlias(ALIAS_OUTPUT);
		manifest.setProductBase(OUTPUT_DIR_TEMP);
		manifest.addProductPath(featureFile);
	}

	private void createFeatureSourceJarAction() throws CoreException
	{
		CSpecBuilder cspec = getCSpec();

		// Create the action that builds the jar file with all source bundles for the feature
		//
		ActionBuilder featureJarBuilder = addAntAction(ATTRIBUTE_SOURCE_FEATURE_JAR, TASK_CREATE_FEATURE_JAR, false);
		featureJarBuilder.addLocalPrerequisite(ATTRIBUTE_SOURCE_MANIFEST, ALIAS_MANIFEST);

		// We use the same content as the original feature (i.e. license, etc.).
		//
		if(cspec.getArtifactBuilder(ATTRIBUTE_JAR_CONTENTS) != null)
			featureJarBuilder.addLocalPrerequisite(ATTRIBUTE_JAR_CONTENTS);
		featureJarBuilder.setPrerequisitesAlias(ALIAS_REQUIREMENTS);

		featureJarBuilder.setProductAlias(ALIAS_OUTPUT);
		featureJarBuilder.setProductBase(OUTPUT_DIR_SOURCE_JAR);
		featureJarBuilder.setUpToDatePolicy(UpToDatePolicy.COUNT);
		featureJarBuilder.setProductFileCount(1);

		GroupBuilder featureJars = cspec.addGroup(ATTRIBUTE_SOURCE_FEATURE_JARS, true); // including self
		featureJars.addLocalPrerequisite(featureJarBuilder);
		featureJars.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_REFS);
	}

	private void createFeatureSourceManifestAction() throws CoreException
	{
		// Create the action that creates the version expanded feature.xml for features
		// and bundles that contains source code.
		//
		ActionBuilder manifest = addAntAction(ATTRIBUTE_SOURCE_MANIFEST, TASK_CREATE_SOURCE_FEATURE, true);
		manifest.addLocalPrerequisite(ATTRIBUTE_MANIFEST, ALIAS_MANIFEST);
		manifest.addLocalPrerequisite(ATTRIBUTE_SOURCE_BUNDLE_JARS, ALIAS_BUNDLES);
		manifest.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_REFS, ALIAS_FEATURES);
		manifest.setProductAlias(ALIAS_OUTPUT);
		manifest.setProductBase(OUTPUT_DIR_TEMP);
		manifest.addProductPath(new Path("source." + FEATURE_FILE)); //$NON-NLS-1$
	}

	private void createRootsArtifact(String filesAndFolders, Filter filter) throws CoreException
	{
		CSpecBuilder cspec = getCSpec();
		StringTokenizer tokenizer = new StringTokenizer(filesAndFolders, ","); //$NON-NLS-1$
		GroupBuilder productRoots = null;
		while(tokenizer.hasMoreTokens())
		{
			// Here PDE decided that files needed a prefix whereas folders did not and that
			// absolute paths needed another prefix whereas relative paths did not. I'm curious;
			// why not say that entries ending with '/' are folders and use standard notation
			// for absolute paths?
			//
			String token = tokenizer.nextToken().trim();
			if(token.startsWith("absolute:")) //$NON-NLS-1$
				//
				// Why whould a feature copy anything using absolute paths? If it did, it would
				// make it hopelessly dependent on install location. Not good. We don't permit
				// it here.
				//
				throw BuckminsterException.fromMessage(NLS.bind(
						Messages.component_0_contains_absolute_path_in_buildproperties, getCSpec().getName()));

			IPath path;
			boolean isFile = token.startsWith("file:"); //$NON-NLS-1$
			if(isFile)
			{
				if("file:bin/win32/win32/x86/eclipse.exe".equals(token)) //$NON-NLS-1$
					token = "file:bin/win32/win32/x86/launcher.exe"; //$NON-NLS-1$
				else if("file:bin/wpf/win32/x86/eclipse.exe".equals(token)) //$NON-NLS-1$
					token = "file:bin/wpf/win32/x86/launcher.exe"; //$NON-NLS-1$

				path = new Path(token.substring(5));
			}
			else
				path = new Path(token);

			// Make sure it really is relative
			//
			IPath leaf = null;
			path = path.makeRelative().setDevice(null);
			if(isFile)
			{
				leaf = new Path(path.lastSegment());
				path = path.removeLastSegments(1);
			}
			path = path.addTrailingSeparator();

			if(productRoots == null)
				productRoots = getInternalProductRoot();

			ArtifactBuilder productRoot = null;
			List<PrerequisiteBuilder> preqs = productRoots.getPrerequisites();
			for(PrerequisiteBuilder preq : preqs)
			{
				ArtifactBuilder ag = cspec.getRequiredArtifact(preq.getName());
				if(ag.getBase().equals(path))
				{
					productRoot = ag;
					break;
				}
			}

			if(productRoot == null)
			{
				int n = preqs.size();
				productRoot = cspec.addArtifact(ATTRIBUTE_INTERNAL_PRODUCT_ROOT + '.' + n, false, null, path);
				productRoot.setFilter(filter);
				productRoots.addLocalPrerequisite(productRoot);
			}

			if(isFile)
				productRoot.addPath(leaf);
		}
	}

	private void createSiteAction() throws CoreException
	{
		ActionBuilder siteBuilder = getCSpec().addAction(ATTRIBUTE_SITE_P2, true, ACTOR_P2_SITE_GENERATOR, false);

		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_FEATURE_EXPORTS, P2SiteGenerator.ALIAS_SITE,
				SIGNING_AND_PACK_DISABLED);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_PACKED, P2SiteGenerator.ALIAS_SITE, PACK_ENABLED);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_SIGNED, P2SiteGenerator.ALIAS_SITE,
				SIGNING_ENABLED_AND_PACK_DISABLED);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_FEATURE_JAR, P2SiteGenerator.ALIAS_SITE_FEATURE);
		siteBuilder.setProductBase(OUTPUT_DIR_SITE_P2);
	}

	private void createSiteFeatureExportsAction() throws CoreException
	{
		GroupBuilder featureExports = getCSpec().getRequiredGroup(ATTRIBUTE_SITE_FEATURE_EXPORTS);
		featureExports.addLocalPrerequisite(createCopySiteFeaturesAction());
		featureExports.addLocalPrerequisite(ACTION_COPY_PLUGINS);
		featureExports.setPrerequisiteRebase(OUTPUT_DIR_SITE);
	}

	private void createSitePackAction() throws CoreException
	{
		ActionBuilder siteBuilder = getCSpec().addAction(ATTRIBUTE_SITE_PACKED, true, JarProcessorActor.ACTOR_ID, true);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_FEATURE_EXPORTS, JarProcessorActor.ALIAS_JAR_FOLDER,
				SIGNING_DISABLED);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_SIGNED, JarProcessorActor.ALIAS_JAR_FOLDER, SIGNING_ENABLED);
		siteBuilder.getProperties().put(JarProcessorActor.PROP_COMMAND, JarProcessorActor.COMMAND_PACK);
		siteBuilder.setProductBase(OUTPUT_DIR_SITE_PACKED);
	}

	private void createSiteRepackAction() throws CoreException
	{
		ActionBuilder siteBuilder = getCSpec().addAction(ATTRIBUTE_SITE_REPACKED, false, JarProcessorActor.ACTOR_ID,
				true);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_FEATURE_EXPORTS, JarProcessorActor.ALIAS_JAR_FOLDER);
		siteBuilder.getProperties().put(JarProcessorActor.PROP_COMMAND, JarProcessorActor.COMMAND_REPACK);
		siteBuilder.setProductBase(OUTPUT_DIR_SITE_REPACKED);
	}

	private void createSiteSignAction() throws CoreException
	{
		ActionBuilder siteBuilder = getCSpec().addAction(ATTRIBUTE_SITE_SIGNED, true, AntActor.ACTOR_ID, true);
		Map<String, String> actorProps = siteBuilder.getActorProperties();
		actorProps.put(AntActor.PROP_BUILD_FILE_ID, "buckminster.signing"); //$NON-NLS-1$
		actorProps.put(AntActor.PROP_TARGETS, "sign.jars"); //$NON-NLS-1$

		siteBuilder.setPrerequisitesAlias(ALIAS_REQUIREMENTS);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_REPACKED, null, PACK_ENABLED);
		siteBuilder.addLocalPrerequisite(ATTRIBUTE_SITE_FEATURE_EXPORTS, null, PACK_DISABLED);
		siteBuilder.setProductBase(OUTPUT_DIR_SITE_SIGNED);
		siteBuilder.setProductAlias(ALIAS_OUTPUT);
	}

	private GroupBuilder getInternalProductRoot() throws CoreException
	{
		CSpecBuilder cspec = getCSpec();
		GroupBuilder productRoot = cspec.getGroup(ATTRIBUTE_INTERNAL_PRODUCT_ROOT);
		if(productRoot == null)
		{
			productRoot = cspec.addGroup(ATTRIBUTE_INTERNAL_PRODUCT_ROOT, false);
			cspec.getRequiredGroup(ATTRIBUTE_PRODUCT_ROOT_FILES).addLocalPrerequisite(productRoot);
		}
		return productRoot;
	}
}
