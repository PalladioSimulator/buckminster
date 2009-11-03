/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.core.query.model;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.buckminster.core.common.model.Documentation;
import org.eclipse.buckminster.core.common.model.ExpandingProperties;
import org.eclipse.buckminster.core.common.model.SAXEmitter;
import org.eclipse.buckminster.core.helpers.DateAndTimeUtils;
import org.eclipse.buckminster.core.helpers.TextUtils;
import org.eclipse.buckminster.core.query.IAdvisorNode;
import org.eclipse.buckminster.core.query.builder.AdvisorNodeBuilder;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.sax.AbstractSaxableElement;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.equinox.internal.provisional.p2.metadata.VersionRange;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("restriction")
public class AdvisorNode extends AbstractSaxableElement implements Cloneable, IAdvisorNode
{
	public static final String ATTR_ATTRIBUTES = "attributes"; //$NON-NLS-1$

	public static final String ATTR_COMPONENT_TYPE = "componentType"; //$NON-NLS-1$

	public static final String ATTR_FILTER = "filter"; //$NON-NLS-1$

	public static final String ATTR_MUTABLE_LEVEL = "mutableLevel"; //$NON-NLS-1$

	public static final String ATTR_NAME_PATTERN = "namePattern"; //$NON-NLS-1$

	public static final String ATTR_OVERLAY_FOLDER = "overlayFolder"; //$NON-NLS-1$

	public static final String ATTR_PRUNE = "prune"; //$NON-NLS-1$

	public static final String ATTR_SKIP_COMPONENT = "skipComponent"; //$NON-NLS-1$

	public static final String ATTR_SOURCE_LEVEL = "sourceLevel"; //$NON-NLS-1$

	public static final String ATTR_USE_TARGET_PLATFORM = "useTargetPlatform"; //$NON-NLS-1$

	public static final String ATTR_USE_MATERIALIZATION = "useMaterialization"; //$NON-NLS-1$

	public static final String ATTR_USE_WORKSPACE = "useWorkspace"; //$NON-NLS-1$

	public static final String ATTR_VERSION_OVERRIDE = "versionOverride"; //$NON-NLS-1$

	public static final String ATTR_VERSION_OVERRIDE_TYPE = "versionOverrideType"; //$NON-NLS-1$

	public static final String ATTR_ALLOW_CIRCULAR_DEPENDENCY = "allowCircularDependency"; //$NON-NLS-1$

	public static final String ATTR_WHEN_NOT_EMPTY = "whenNotEmpty"; //$NON-NLS-1$

	public static final String ATTR_USE_REMOTE_RESOLUTION = "useRemoteResolution"; //$NON-NLS-1$

	public static final String ATTR_SYSTEM_DISCOVERY = "systemDiscovery"; //$NON-NLS-1$

	public static final String ATTR_BRANCH_TAG_PATH = "branchTagPath"; //$NON-NLS-1$

	public static final String ATTR_REVISION = "revision"; //$NON-NLS-1$

	public static final String ATTR_TIMESTAMP = "timestamp"; //$NON-NLS-1$

	public static final String ATTR_RESOLUTION_PRIO = "resolutionPrio"; //$NON-NLS-1$

	public static final String TAG = "advisorNode"; //$NON-NLS-1$

	private final boolean m_allowCircularDependency;

	private final Documentation m_documentation;

	private final List<String> m_attributes;

	private final String m_componentTypeID;

	private final Filter m_filter;

	private final MutableLevel m_mutableLevel;

	private final Pattern m_namePattern;

	private final URL m_overlayFolder;

	private final Map<String, String> m_properties;

	private final boolean m_prune;

	private final boolean m_skipComponent;

	private final SourceLevel m_sourceLevel;

	private final boolean m_useTargetPlatform;

	private final boolean m_useMaterialization;

	private final boolean m_useWorkspace;

	private final VersionRange m_versionOverride;

	private final boolean m_useRemoteResolution;

	private final boolean m_systemDiscovery;

	private final VersionSelector[] m_branchTagPath;

	private final long m_revision;

	private final Date m_timestamp;

	private final int[] m_resolutionPrio;

	public AdvisorNode(AdvisorNodeBuilder bld)
	{
		m_documentation = bld.getDocumentation();
		m_allowCircularDependency = bld.allowCircularDependency();
		m_namePattern = bld.getNamePattern();
		m_filter = bld.getFilter();
		m_componentTypeID = bld.getComponentTypeID();
		m_overlayFolder = bld.getOverlayFolder();
		m_prune = bld.isPrune();
		m_mutableLevel = bld.getMutableLevel();
		m_sourceLevel = bld.getSourceLevel();
		m_skipComponent = bld.skipComponent();
		m_useMaterialization = bld.isUseMaterialization();
		m_useTargetPlatform = bld.isUseTargetPlatform();
		m_useWorkspace = bld.isUseWorkspace();
		m_versionOverride = bld.getVersionOverride();
		m_useRemoteResolution = bld.isUseRemoteResolution();
		m_systemDiscovery = bld.isSystemDiscovery();
		m_branchTagPath = bld.getBranchTagPath();
		m_revision = bld.getRevision();
		m_timestamp = bld.getTimestamp();
		m_resolutionPrio = bld.getResolutionPrio();
		m_attributes = Utils.createUnmodifiableList(bld.getAttributes());
		m_properties = ExpandingProperties.createUnmodifiableProperties(bld.getProperties());
	}

	public boolean allowCircularDependency()
	{
		return m_allowCircularDependency;
	}

	public final List<String> getAttributes()
	{
		return m_attributes;
	}

	public final VersionSelector[] getBranchTagPath()
	{
		return m_branchTagPath;
	}

	public final String getComponentTypeID()
	{
		return m_componentTypeID;
	}

	public String getDefaultTag()
	{
		return TAG;
	}

	public Documentation getDocumentation()
	{
		return m_documentation;
	}

	public Filter getFilter()
	{
		return m_filter;
	}

	public final MutableLevel getMutableLevel()
	{
		return m_mutableLevel;
	}

	public final Pattern getNamePattern()
	{
		return m_namePattern;
	}

	public URL getOverlayFolder()
	{
		return m_overlayFolder;
	}

	public Map<String, String> getProperties()
	{
		return m_properties;
	}

	public int[] getResolutionPrio()
	{
		return m_resolutionPrio;
	}

	public long getRevision()
	{
		return m_revision;
	}

	public final SourceLevel getSourceLevel()
	{
		return m_sourceLevel;
	}

	public Date getTimestamp()
	{
		return m_timestamp;
	}

	public final VersionRange getVersionOverride()
	{
		return m_versionOverride;
	}

	public final boolean isPrune()
	{
		return m_prune;
	}

	public final boolean isSystemDiscovery()
	{
		return m_systemDiscovery;
	}

	public final boolean isUseMaterialization()
	{
		return m_useMaterialization;
	}

	public final boolean isUseRemoteResolution()
	{
		return m_useRemoteResolution;
	}

	public final boolean isUseTargetPlatform()
	{
		return m_useTargetPlatform;
	}

	public final boolean isUseWorkspace()
	{
		return m_useWorkspace;
	}

	public final boolean skipComponent()
	{
		return m_skipComponent;
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) throws SAXException
	{
		if(m_namePattern != null)
			Utils.addAttribute(attrs, ATTR_NAME_PATTERN, m_namePattern.toString());
		if(m_filter != null)
			Utils.addAttribute(attrs, ATTR_FILTER, m_filter.toString());
		if(m_overlayFolder != null)
			Utils.addAttribute(attrs, ATTR_OVERLAY_FOLDER, m_overlayFolder.toString());
		if(m_componentTypeID != null)
			Utils.addAttribute(attrs, ATTR_COMPONENT_TYPE, m_componentTypeID);
		if(m_mutableLevel != MutableLevel.INDIFFERENT)
			Utils.addAttribute(attrs, ATTR_MUTABLE_LEVEL, m_mutableLevel.name());
		if(m_sourceLevel != SourceLevel.INDIFFERENT)
			Utils.addAttribute(attrs, ATTR_SOURCE_LEVEL, m_sourceLevel.name());
		if(m_skipComponent)
			Utils.addAttribute(attrs, ATTR_SKIP_COMPONENT, "true"); //$NON-NLS-1$
		if(m_allowCircularDependency)
			Utils.addAttribute(attrs, ATTR_ALLOW_CIRCULAR_DEPENDENCY, "true"); //$NON-NLS-1$
		if(!m_systemDiscovery)
			Utils.addAttribute(attrs, ATTR_SYSTEM_DISCOVERY, "false"); //$NON-NLS-1$
		if(!m_useMaterialization)
			Utils.addAttribute(attrs, ATTR_USE_MATERIALIZATION, "false"); //$NON-NLS-1$
		if(!m_useRemoteResolution)
			Utils.addAttribute(attrs, ATTR_USE_REMOTE_RESOLUTION, "false"); //$NON-NLS-1$
		if(!m_useTargetPlatform)
			Utils.addAttribute(attrs, ATTR_USE_TARGET_PLATFORM, "false"); //$NON-NLS-1$
		if(!m_useWorkspace)
			Utils.addAttribute(attrs, ATTR_USE_WORKSPACE, "false"); //$NON-NLS-1$

		if(m_versionOverride != null)
			Utils.addAttribute(attrs, ATTR_VERSION_OVERRIDE, m_versionOverride.toString());
		String tmp = TextUtils.concat(m_attributes, ","); //$NON-NLS-1$
		if(tmp != null)
			Utils.addAttribute(attrs, ATTR_ATTRIBUTES, tmp);
		if(m_prune)
			Utils.addAttribute(attrs, ATTR_PRUNE, "true"); //$NON-NLS-1$

		tmp = VersionSelector.toString(m_branchTagPath);
		if(tmp != null)
			Utils.addAttribute(attrs, ATTR_BRANCH_TAG_PATH, tmp);

		if(!Arrays.equals(m_resolutionPrio, DEFAULT_RESOLUTION_PRIO))
		{
			StringBuilder bld = new StringBuilder();
			bld.append(m_resolutionPrio[0]);
			for(int idx = 1; idx < m_resolutionPrio.length; ++idx)
			{
				bld.append(',');
				bld.append(m_resolutionPrio[idx]);
			}
			Utils.addAttribute(attrs, ATTR_RESOLUTION_PRIO, bld.toString());
		}

		if(m_revision != -1)
			Utils.addAttribute(attrs, ATTR_REVISION, Long.toString(m_revision));

		if(m_timestamp != null)
			Utils.addAttribute(attrs, ATTR_TIMESTAMP, DateAndTimeUtils.toISOFormat(m_timestamp));
	}

	@Override
	protected void emitElements(ContentHandler handler, String namespace, String prefix) throws SAXException
	{
		if(m_documentation != null)
			m_documentation.toSax(handler, namespace, prefix, m_documentation.getDefaultTag());
		SAXEmitter.emitProperties(handler, m_properties, namespace, prefix, true, false);
	}
}
