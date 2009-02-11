/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.cspec.builder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.buckminster.core.TargetPlatform;
import org.eclipse.buckminster.core.common.model.Documentation;
import org.eclipse.buckminster.core.cspec.IAttribute;
import org.eclipse.buckminster.core.cspec.ICSpecData;
import org.eclipse.buckminster.core.cspec.IComponentRequest;
import org.eclipse.buckminster.core.cspec.IGenerator;
import org.eclipse.buckminster.core.cspec.model.AttributeAlreadyDefinedException;
import org.eclipse.buckminster.core.cspec.model.CSpec;
import org.eclipse.buckminster.core.cspec.model.ComponentIdentifier;
import org.eclipse.buckminster.core.cspec.model.DependencyAlreadyDefinedException;
import org.eclipse.buckminster.core.cspec.model.GeneratorAlreadyDefinedException;
import org.eclipse.buckminster.core.cspec.model.MissingAttributeException;
import org.eclipse.buckminster.core.cspec.model.MissingDependencyException;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.helpers.FilterUtils;
import org.eclipse.buckminster.core.version.IVersion;
import org.eclipse.buckminster.core.version.IVersionDesignator;
import org.eclipse.buckminster.core.version.IVersionType;
import org.eclipse.buckminster.core.version.VersionFactory;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.osgi.filter.FilterFactory;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.core.VersionRange;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.IRequiredCapability;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @author Thomas Hallgren
 */
@SuppressWarnings("restriction")
public class CSpecBuilder implements ICSpecData
{
	private static final String FEATURE_GROUP = ".feature.group"; //$NON-NLS-1$

	private HashMap<String, AttributeBuilder> m_attributes;

	private String m_componentType;

	private HashMap<String, ComponentRequestBuilder> m_dependencies;

	private Documentation m_documentation;

	private HashMap<String, GeneratorBuilder> m_generators;

	private String m_name;

	private URL m_projectInfo;

	private String m_shortDesc;

	private IVersion m_version;

	private Filter m_filter;

	public CSpecBuilder()
	{
	}

	public CSpecBuilder(IInstallableUnit iu) throws CoreException
	{
		String name = iu.getId();
		boolean isFeature = name.endsWith(FEATURE_GROUP);
		if(isFeature)
		{
			name = name.substring(0, name.length() - FEATURE_GROUP.length());
			setComponentTypeID(IComponentType.ECLIPSE_FEATURE);
		}
		else
			setComponentTypeID(IComponentType.OSGI_BUNDLE);

		setName(name);

		Version v = iu.getVersion();
		if(v != null)
			setVersion(v.toString(), IVersionType.OSGI);

		String filterStr = iu.getFilter();
		if(filterStr != null)
			try
			{
				Filter filter = FilterFactory.newInstance(filterStr);
				filter = FilterUtils.replaceAttributeNames(filter, "osgi", TargetPlatform.TARGET_PREFIX); //$NON-NLS-1$
				setFilter(filter);
			}
			catch(InvalidSyntaxException e)
			{
				throw BuckminsterException.wrap(e);
			}

		for(IRequiredCapability cap : iu.getRequiredCapabilities())
		{
			// We only bother with direct dependencies to other IU's here
			// since package imports etc. are not yet supported
			//
			String namespace = cap.getNamespace();
			name = cap.getName();
			String ctype;
			if(IInstallableUnit.NAMESPACE_IU_ID.equals(namespace))
			{
				if(name.endsWith(FEATURE_GROUP))
				{
					name = name.substring(0, name.length() - FEATURE_GROUP.length());
					ctype = IComponentType.ECLIPSE_FEATURE;
				}
				else if(isFeature)
					ctype = IComponentType.OSGI_BUNDLE;
				else
					continue;
			}
			else if(IComponentType.OSGI_BUNDLE.equals(namespace))
				ctype = namespace;
			else
				// Package or something else that we don't care about here
				continue;

			ComponentRequestBuilder crb = new ComponentRequestBuilder();
			crb.setName(name);
			crb.setComponentTypeID(ctype);

			VersionRange vr = cap.getRange();
			if(vr != null)
				crb.setVersionDesignator(vr.toString(), IVersionType.OSGI);

			filterStr = cap.getFilter();
			if(filterStr != null)
				try
				{
					Filter filter = FilterFactory.newInstance(filterStr);
					filter = FilterUtils.replaceAttributeNames(filter, "osgi", TargetPlatform.TARGET_PREFIX); //$NON-NLS-1$
					crb.setFilter(filter);
				}
				catch(InvalidSyntaxException e)
				{
					throw BuckminsterException.wrap(e);
				}
			addDependency(crb);
		}
	}

	public ActionBuilder addAction(String actionName, boolean publ, String actorName, boolean always)
			throws AttributeAlreadyDefinedException
	{
		ActionBuilder bld = createActionBuilder();
		bld.setName(actionName);
		bld.setPublic(publ);
		bld.setActorName(actorName);
		bld.setAlways(always);
		addAttribute(bld);
		return bld;
	}

	public ArtifactBuilder addArtifact(String name, boolean publ, String type, IPath base)
			throws AttributeAlreadyDefinedException
	{
		ArtifactBuilder bld = createArtifactBuilder();
		bld.setName(name);
		bld.setPublic(publ);
		bld.setType(type);
		bld.setBase(base);
		addAttribute(bld);
		return bld;
	}

	public void addAttribute(IAttribute attribute) throws AttributeAlreadyDefinedException
	{
		String name = attribute.getName();
		if(m_attributes == null)
			m_attributes = new HashMap<String, AttributeBuilder>();
		else if(m_attributes.containsKey(name))
			throw new AttributeAlreadyDefinedException(m_name, name);
		m_attributes.put(name, attribute.getAttributeBuilder(this));
	}

	public boolean addDependency(IComponentRequest dependency) throws CoreException
	{
		String name = dependency.getName();
		ComponentRequestBuilder old = getDependency(name);
		if(old == null)
		{
			if(m_dependencies == null)
				m_dependencies = new HashMap<String, ComponentRequestBuilder>();

			ComponentRequestBuilder bld;
			if(dependency instanceof ComponentRequestBuilder)
				bld = (ComponentRequestBuilder)dependency;
			else
			{
				bld = createDependencyBuilder();
				bld.initFrom(dependency);
			}
			m_dependencies.put(name, bld);
			return true;
		}

		IVersionDesignator vd = old.getVersionDesignator();
		IVersionDesignator nvd = dependency.getVersionDesignator();
		if(vd == null)
			vd = nvd;
		else
		{
			if(nvd != null)
			{
				vd = vd.merge(nvd);
				if(vd == null)
					//
					// Version ranges were not possible to merge, i.e. no intersection
					//
					throw new DependencyAlreadyDefinedException(getName(), old.getName());
			}
		}

		Filter fl = old.getFilter();
		Filter nfl = dependency.getFilter();
		if(fl == null || nfl == null)
			fl = null;
		else
		{
			if(!fl.equals(nfl))
			{
				try
				{
					fl = FilterFactory.newInstance("(|" + fl + nfl + ')'); //$NON-NLS-1$
				}
				catch(InvalidSyntaxException e)
				{
					throw BuckminsterException.wrap(e);
				}
			}
		}

		if(vd == old.getVersionDesignator() && fl == old.getFilter())
			return false;

		old.setVersionDesignator(vd);
		old.setFilter(fl);
		return false;
	}

	public void addGenerator(IGenerator generator) throws GeneratorAlreadyDefinedException
	{
		String name = generator.getGenerates();
		if(m_generators == null)
			m_generators = new HashMap<String, GeneratorBuilder>();
		else if(m_generators.containsKey(name))
			throw new GeneratorAlreadyDefinedException(m_name, name);

		GeneratorBuilder bld = createGeneratorBuilder();
		bld.initFrom(generator);
		m_generators.put(name, bld);
	}

	public GroupBuilder addGroup(String name, boolean publ) throws AttributeAlreadyDefinedException
	{
		GroupBuilder bld = createGroupBuilder();
		bld.setName(name);
		bld.setPublic(publ);
		addAttribute(bld);
		return bld;
	}

	public ActionBuilder addInternalAction(String actionName, boolean publ) throws AttributeAlreadyDefinedException
	{
		return addAction(actionName, publ, null, true);
	}

	public void clear()
	{
		m_name = null;
		m_componentType = null;
		m_version = null;
		m_filter = null;
		m_projectInfo = null;
		m_documentation = null;
		m_shortDesc = null;
		m_dependencies = null;
		m_attributes = null;
		m_generators = null;
	}

	public ActionArtifactBuilder createActionArtifactBuilder()
	{
		return new ActionArtifactBuilder(this);
	}

	public ActionBuilder createActionBuilder()
	{
		return new ActionBuilder(this);
	}

	public ArtifactBuilder createArtifactBuilder()
	{
		return new ArtifactBuilder(this);
	}

	public AttributeBuilder createAttributeBuilder()
	{
		return new AttributeBuilder(this);
	}

	public CSpec createCSpec()
	{
		return new CSpec(this);
	}

	public ComponentRequestBuilder createDependencyBuilder()
	{
		return new ComponentRequestBuilder();
	}

	public GeneratorBuilder createGeneratorBuilder()
	{
		return new GeneratorBuilder(this);
	}

	public GroupBuilder createGroupBuilder()
	{
		return new GroupBuilder(this);
	}

	public ActionBuilder getActionBuilder(String name)
	{
		AttributeBuilder attr = m_attributes.get(name);
		return attr instanceof ActionBuilder
				? (ActionBuilder)attr
				: null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapterType)
	{
		if(CSpecBuilder.class.isAssignableFrom(adapterType))
			return this;

		if(CSpec.class.isAssignableFrom(adapterType))
			return createCSpec();

		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

	public ArtifactBuilder getArtifactBuilder(String name)
	{
		AttributeBuilder attr = m_attributes.get(name);
		return attr instanceof ArtifactBuilder
				? (ArtifactBuilder)attr
				: null;
	}

	public AttributeBuilder getAttribute(String name)
	{
		return m_attributes == null
				? null
				: m_attributes.get(name);
	}

	public Map<String, AttributeBuilder> getAttributes()
	{
		return m_attributes;
	}

	public ComponentIdentifier getComponentIdentifier()
	{
		return new ComponentIdentifier(m_name, m_componentType, m_version);
	}

	public String getComponentTypeID()
	{
		return m_componentType;
	}

	public Map<String, ComponentRequestBuilder> getDependencies()
	{
		return m_dependencies;
	}

	public ComponentRequestBuilder getDependency(String dependencyName)
	{
		return m_dependencies == null
				? null
				: m_dependencies.get(dependencyName);
	}

	public Documentation getDocumentation()
	{
		return m_documentation;
	}

	public Filter getFilter()
	{
		return m_filter;
	}

	public GeneratorBuilder getGenerator(String generatorName)
	{
		return m_generators == null
				? null
				: m_generators.get(generatorName);
	}

	public Map<String, GeneratorBuilder> getGenerators()
	{
		return m_generators;
	}

	public GroupBuilder getGroup(String name)
	{
		AttributeBuilder attr = m_attributes.get(name);
		return attr instanceof GroupBuilder
				? (GroupBuilder)attr
				: null;
	}

	public String getName()
	{
		return m_name;
	}

	public URL getProjectInfo()
	{
		return m_projectInfo;
	}

	public ActionBuilder getRequiredAction(String name) throws MissingAttributeException
	{
		AttributeBuilder attr = m_attributes.get(name);
		if(attr instanceof ActionBuilder)
			return (ActionBuilder)attr;
		throw new MissingAttributeException(m_name, name);
	}

	public ArtifactBuilder getRequiredArtifact(String name) throws MissingAttributeException
	{
		AttributeBuilder attr = m_attributes.get(name);
		if(attr instanceof ArtifactBuilder)
			return (ArtifactBuilder)attr;
		throw new MissingAttributeException(m_name, name);
	}

	public AttributeBuilder getRequiredAttribute(String name) throws MissingAttributeException
	{
		AttributeBuilder attr = m_attributes.get(name);
		if(attr == null)
			throw new MissingAttributeException(m_name, name);
		return attr;
	}

	public ComponentRequestBuilder getRequiredDependency(String name) throws MissingDependencyException
	{
		ComponentRequestBuilder dep = getDependency(name);
		if(dep == null)
			throw new MissingDependencyException(m_name, name);
		return dep;
	}

	public GroupBuilder getRequiredGroup(String name) throws MissingAttributeException
	{
		AttributeBuilder attr = m_attributes.get(name);
		if(attr instanceof GroupBuilder)
			return (GroupBuilder)attr;
		throw new MissingAttributeException(m_name, name);
	}

	public String getShortDesc()
	{
		return m_shortDesc;
	}

	public String getTagInfo(String parentInfo)
	{
		return CSpec.getTagInfo(getComponentIdentifier(), m_projectInfo, parentInfo);
	}

	public IVersion getVersion()
	{
		return m_version;
	}

	public void initFrom(ICSpecData cspec)
	{
		m_name = cspec.getName();
		m_componentType = cspec.getComponentTypeID();
		m_version = cspec.getVersion();
		m_filter = cspec.getFilter();
		m_projectInfo = cspec.getProjectInfo();
		m_documentation = cspec.getDocumentation();
		m_shortDesc = cspec.getShortDesc();

		Map<String, ? extends IAttribute> attrs = cspec.getAttributes();
		if(attrs.size() > 0)
		{
			m_attributes = new HashMap<String, AttributeBuilder>(attrs.size());
			for(IAttribute attr : attrs.values())
				m_attributes.put(attr.getName(), attr.getAttributeBuilder(this));
		}
		else
			m_attributes = null;

		Map<String, ? extends IComponentRequest> deps = cspec.getDependencies();
		if(deps.size() > 0)
		{
			m_dependencies = new HashMap<String, ComponentRequestBuilder>(deps.size());
			for(IComponentRequest dep : deps.values())
			{
				ComponentRequestBuilder db = createDependencyBuilder();
				db.initFrom(dep);
				m_dependencies.put(dep.getName(), db);
			}
		}
		else
			m_dependencies = null;

		Map<String, ? extends IGenerator> gens = cspec.getGenerators();
		if(gens.size() > 0)
		{
			m_generators = new HashMap<String, GeneratorBuilder>(gens.size());
			for(IGenerator gen : gens.values())
			{
				GeneratorBuilder gb = createGeneratorBuilder();
				gb.initFrom(gen);
				m_generators.put(gen.getGenerates(), gb);
			}
		}
		else
			m_generators = null;
	}

	public void removeAttribute(String name)
	{
		if(m_attributes != null)
			m_attributes.remove(name);
	}

	public void removeDependency(String name)
	{
		if(m_dependencies != null)
			m_dependencies.remove(name);
	}

	public void removeGenerator(String name)
	{
		if(m_generators != null)
			m_generators.remove(name);
	}

	public void setComponentTypeID(String componentType)
	{
		m_componentType = componentType;
	}

	public void setDocumentation(Documentation documentation)
	{
		m_documentation = documentation;
	}

	public void setFilter(Filter filter)
	{
		m_filter = filter;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public void setProjectInfo(URL projectInfo)
	{
		m_projectInfo = projectInfo;
	}

	public void setShortDesc(String shortDesc)
	{
		m_shortDesc = shortDesc;
	}

	public void setVersion(IVersion version)
	{
		m_version = version;
	}

	public void setVersion(String versionString, String versionTypeId) throws CoreException
	{
		if(versionString == null)
		{
			m_version = null;
			return;
		}

		versionString = versionString.trim();
		if(versionString.length() == 0)
		{
			m_version = null;
			return;
		}

		if(versionTypeId != null)
		{
			versionTypeId = versionTypeId.trim();
			if(versionTypeId.length() == 0)
				versionTypeId = null;
		}
		m_version = VersionFactory.createVersion(versionTypeId, versionString);
	}

}
