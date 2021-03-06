/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.core.mspec.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.XMLConstants;
import org.eclipse.buckminster.core.common.model.ExpandingProperties;
import org.eclipse.buckminster.core.cspec.IComponentIdentifier;
import org.eclipse.buckminster.core.cspec.IComponentName;
import org.eclipse.buckminster.core.cspec.model.ComponentName;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.materializer.IMaterializer;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.metadata.model.Resolution;
import org.eclipse.buckminster.core.mspec.ConflictResolution;
import org.eclipse.buckminster.core.mspec.IMaterializationNode;
import org.eclipse.buckminster.core.mspec.IMaterializationSpec;
import org.eclipse.buckminster.core.mspec.builder.MaterializationNodeBuilder;
import org.eclipse.buckminster.core.mspec.builder.MaterializationSpecBuilder;
import org.eclipse.buckminster.core.parser.IParser;
import org.eclipse.buckminster.core.parser.IParserFactory;
import org.eclipse.buckminster.download.DownloadManager;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.runtime.URLUtils;
import org.eclipse.buckminster.sax.ISaxable;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Thomas Hallgren
 */
public class MaterializationSpec extends MaterializationDirective implements ISaxable, IMaterializationSpec {
	public static final String TAG = "mspec"; //$NON-NLS-1$

	public static final String ATTR_NAME = "name"; //$NON-NLS-1$

	public static final String ATTR_SHORT_DESC = "shortDesc"; //$NON-NLS-1$

	public static final String ATTR_URL = "url"; //$NON-NLS-1$

	public static MaterializationSpec fromStream(String systemId, InputStream stream) throws CoreException {
		try {
			IParserFactory pf = CorePlugin.getDefault().getParserFactory();
			IParser<MaterializationSpec> parser = pf.getMaterializationSpecParser(false);
			return parser.parse(systemId, stream);
		} catch (Exception e) {
			throw BuckminsterException.wrap(e);
		}
	}

	public static MaterializationSpec fromURL(URL url) throws CoreException {
		InputStream stream = null;
		try {
			stream = DownloadManager.read(url, null);
			return fromStream(url.toString(), stream);
		} catch (IOException e) {
			throw BuckminsterException.wrap(e);
		} finally {
			IOUtils.close(stream);
		}
	}

	private final String name;

	private final String shortDesc;

	private final String url;

	private final List<MaterializationNode> nodes;

	private final URL contextURL;

	private BillOfMaterials currentBom;

	public MaterializationSpec(MaterializationSpecBuilder builder) {
		super(builder);
		this.name = builder.getName();
		this.shortDesc = builder.getShortDesc();
		this.url = builder.getURL();
		this.contextURL = builder.getContextURL();
		ArrayList<MaterializationNode> nodeList = new ArrayList<MaterializationNode>();
		for (MaterializationNodeBuilder nodeBuilder : builder.getNodeBuilders())
			nodeList.add(nodeBuilder.createMaterializationNode());
		this.nodes = Utils.createUnmodifiableList(nodeList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(MaterializationSpecBuilder.class)) {
			MaterializationSpecBuilder bld = new MaterializationSpecBuilder();
			bld.initFrom(this);
			return bld;
		}
		return super.getAdapter(adapter);
	}

	public ConflictResolution getConflictResolution(Resolution res) {
		return getConflictResolution(getMatchingNode(res));
	}

	@Override
	public URL getContextURL() {
		return contextURL;
	}

	@Override
	public String getDefaultTag() {
		return TAG;
	}

	public IPath getLeafArtifact(Resolution resolution) {
		IMaterializationNode node = getMatchingNode(resolution);
		return node == null ? null : node.getLeafArtifact();
	}

	@Override
	public IMaterializationNode getMatchingNode(IComponentName cName) {
		if (currentBom != null && cName instanceof IComponentIdentifier) {
			try {
				return getMatchingNode(currentBom.getResolvedNode(((IComponentIdentifier) cName)).getResolution());
			} catch (CoreException e) {
			}
		}
		return getMatchingNode(cName, ((ComponentName) cName).getProperties());
	}

	@Override
	public IMaterializationNode getMatchingNode(Resolution res) {
		return getMatchingNode(res.getComponentIdentifier(), res.getProperties());
	}

	public IMaterializer getMaterializer(Resolution resolution) throws CoreException {
		return CorePlugin.getDefault().getMaterializer(getMaterializerID(resolution));
	}

	public String getMaterializerID(Resolution resolution) throws CoreException {
		IMaterializationNode node = getMatchingNode(resolution);
		String materializer = (node == null) ? null : node.getMaterializerID();
		if (materializer == null) {
			materializer = getMaterializerID();
			if (materializer == null) {
				if ((IComponentType.OSGI_BUNDLE.equals(resolution.getComponentTypeId()) || IComponentType.ECLIPSE_FEATURE.equals(resolution
						.getComponentTypeId())) && !resolution.getProvider().hasSource()) {
					materializer = IMaterializer.P2;
				} else {
					materializer = resolution.getProvider().getReaderType().getRecommendedMaterializer();
				}
			}
		}
		return materializer;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<? extends IMaterializationNode> getNodes() {
		return nodes;
	}

	public String getProjectName(Resolution res) throws CoreException {
		return getProjectName(res.getComponentIdentifier(), getMatchingNode(res));
	}

	@Override
	public URL getResolvedURL() {
		return URLUtils.resolveURL(contextURL, ExpandingProperties.expand(RMContext.getGlobalPropertyAdditions(), url, 0));
	}

	public IPath getResourcePath(Resolution res) {
		IMaterializationNode node = getMatchingNode(res);
		return node == null ? null : node.getResourcePath();
	}

	@Override
	public String getShortDesc() {
		return shortDesc;
	}

	public String getSuffix(Resolution res) {
		IMaterializationNode node = getMatchingNode(res);
		return node == null ? null : node.getSuffix();
	}

	@Override
	public String getURL() {
		return url;
	}

	public boolean isExcluded(IComponentName cname) {
		IMaterializationNode node = getMatchingNode(cname);
		return node != null && node.isExclude();
	}

	public boolean isExcluded(Resolution res) {
		IMaterializationNode node = getMatchingNode(res);
		return node != null && node.isExclude();
	}

	public boolean isExpand(Resolution res) {
		IMaterializationNode node = getMatchingNode(res);
		return node != null && (node.isUnpack() && node.isExpand());
	}

	public boolean isUnpack(Resolution res) {
		IMaterializationNode node = getMatchingNode(res);
		return node != null && node.isUnpack();
	}

	@Override
	public void toSax(ContentHandler handler) throws SAXException {
		handler.startDocument();
		toSax(handler, XMLConstants.BM_METADATA_NS, XMLConstants.BM_METADATA_PREFIX, getDefaultTag());
		handler.endDocument();
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) throws SAXException {
		super.addAttributes(attrs);
		Utils.addAttribute(attrs, ATTR_NAME, name);
		Utils.addAttribute(attrs, ATTR_URL, url);
		if (shortDesc != null)
			Utils.addAttribute(attrs, ATTR_SHORT_DESC, shortDesc);
	}

	@Override
	protected void emitElements(ContentHandler receiver, String namespace, String prefix) throws SAXException {
		super.emitElements(receiver, namespace, prefix);
		for (MaterializationNode node : nodes)
			node.toSax(receiver, namespace, prefix, node.getDefaultTag());
	}

	private ConflictResolution getConflictResolution(IMaterializationNode node) {
		ConflictResolution cr = null;
		if (node != null)
			cr = node.getConflictResolution();

		if (cr == null) {
			cr = getConflictResolution();
			if (cr == null)
				cr = ConflictResolution.getDefault();
		}
		return cr;
	}

	private IMaterializationNode getMatchingNode(IComponentName cName, Map<String, ? extends Object> props) {
		for (MaterializationNode aNode : nodes) {
			Pattern pattern = aNode.getNamePattern();
			if (!(pattern == null || pattern.matcher(cName.getName()).find()))
				continue;

			String matchingCType = aNode.getComponentTypeID();
			if (!(matchingCType == null || matchingCType.equals(cName.getComponentTypeID())))
				continue;

			Filter filter = aNode.getFilter();
			if (!(filter == null || filter.matches(props)))
				continue;
			return aNode;
		}
		return null;
	}

	private String getProjectName(ComponentName cName, IMaterializationNode node) throws CoreException {
		if (node == null)
			return cName.getProjectName();

		Pattern bindingNamePattern = node.getBindingNamePattern();
		String bindingNameReplacement = node.getBindingNameReplacement();
		if (bindingNamePattern == null || bindingNameReplacement == null)
			return cName.getProjectName();

		Matcher matcher = bindingNamePattern.matcher(cName.getName());
		if (matcher.matches()) {
			String repl = matcher.replaceAll(bindingNameReplacement).trim();
			if (repl.length() > 0)
				return repl;
		}
		return cName.getProjectName();
	}
}
