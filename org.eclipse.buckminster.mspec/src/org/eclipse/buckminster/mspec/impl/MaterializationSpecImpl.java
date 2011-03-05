/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.buckminster.mspec.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.buckminster.model.common.ComponentName;
import org.eclipse.buckminster.model.common.ConflictResolution;
import org.eclipse.buckminster.model.common.util.ExpandingProperties;
import org.eclipse.buckminster.mspec.MaterializationNode;
import org.eclipse.buckminster.mspec.MaterializationSpec;
import org.eclipse.buckminster.mspec.MspecPackage;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.runtime.URLUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Materialization Spec</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.buckminster.mspec.impl.MaterializationSpecImpl#getMspecNodes
 * <em>Mspec Nodes</em>}</li>
 * <li>
 * {@link org.eclipse.buckminster.mspec.impl.MaterializationSpecImpl#getName
 * <em>Name</em>}</li>
 * <li>
 * {@link org.eclipse.buckminster.mspec.impl.MaterializationSpecImpl#getShortDesc
 * <em>Short Desc</em>}</li>
 * <li>{@link org.eclipse.buckminster.mspec.impl.MaterializationSpecImpl#getUrl
 * <em>Url</em>}</li>
 * <li>
 * {@link org.eclipse.buckminster.mspec.impl.MaterializationSpecImpl#getMaxParallelJobs
 * <em>Max Parallel Jobs</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MaterializationSpecImpl extends MaterializationDirectiveImpl implements MaterializationSpec {
	/**
	 * The cached value of the '{@link #getMspecNodes() <em>Mspec Nodes</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMspecNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<MaterializationNode> mspecNodes;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getShortDesc() <em>Short Desc</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getShortDesc()
	 * @generated
	 * @ordered
	 */
	protected static final String SHORT_DESC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShortDesc() <em>Short Desc</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getShortDesc()
	 * @generated
	 * @ordered
	 */
	protected String shortDesc = SHORT_DESC_EDEFAULT;

	/**
	 * The default value of the '{@link #getUrl() <em>Url</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected String url = URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxParallelJobs()
	 * <em>Max Parallel Jobs</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getMaxParallelJobs()
	 * @generated
	 * @ordered
	 */
	protected static final int MAX_PARALLEL_JOBS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getMaxParallelJobs()
	 * <em>Max Parallel Jobs</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getMaxParallelJobs()
	 * @generated
	 * @ordered
	 */
	protected int maxParallelJobs = MAX_PARALLEL_JOBS_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected MaterializationSpecImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES:
				return getMspecNodes();
			case MspecPackage.MATERIALIZATION_SPEC__NAME:
				return getName();
			case MspecPackage.MATERIALIZATION_SPEC__SHORT_DESC:
				return getShortDesc();
			case MspecPackage.MATERIALIZATION_SPEC__URL:
				return getUrl();
			case MspecPackage.MATERIALIZATION_SPEC__MAX_PARALLEL_JOBS:
				return getMaxParallelJobs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES:
				return ((InternalEList<?>) getMspecNodes()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES:
				return mspecNodes != null && !mspecNodes.isEmpty();
			case MspecPackage.MATERIALIZATION_SPEC__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MspecPackage.MATERIALIZATION_SPEC__SHORT_DESC:
				return SHORT_DESC_EDEFAULT == null ? shortDesc != null : !SHORT_DESC_EDEFAULT.equals(shortDesc);
			case MspecPackage.MATERIALIZATION_SPEC__URL:
				return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
			case MspecPackage.MATERIALIZATION_SPEC__MAX_PARALLEL_JOBS:
				return maxParallelJobs != MAX_PARALLEL_JOBS_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES:
				getMspecNodes().clear();
				getMspecNodes().addAll((Collection<? extends MaterializationNode>) newValue);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__NAME:
				setName((String) newValue);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__SHORT_DESC:
				setShortDesc((String) newValue);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__URL:
				setUrl((String) newValue);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__MAX_PARALLEL_JOBS:
				setMaxParallelJobs((Integer) newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES:
				getMspecNodes().clear();
				return;
			case MspecPackage.MATERIALIZATION_SPEC__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__SHORT_DESC:
				setShortDesc(SHORT_DESC_EDEFAULT);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__URL:
				setUrl(URL_EDEFAULT);
				return;
			case MspecPackage.MATERIALIZATION_SPEC__MAX_PARALLEL_JOBS:
				setMaxParallelJobs(MAX_PARALLEL_JOBS_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public ConflictResolution getConflictResolution(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		ConflictResolution cr = null;
		if (node != null)
			cr = node.getConflictResolution();

		if (cr == null) {
			cr = getConflictResolution();
			if (cr == null)
				cr = CONFLICT_RESOLUTION_EDEFAULT;
		}
		return cr;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public URL getContextURL() {
		Resource resource = eResource();
		if (resource == null)
			return null;

		try {
			return new URL(resource.getURI().toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public IPath getLeafArtifact(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		if (node == null)
			return null;
		String la = node.getLeafArtifact();
		return la == null ? null : Path.fromPortableString(la);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public MaterializationNode getMatchingNode(ComponentName component, Map<String, String> properties) {
		for (MaterializationNode aNode : getMspecNodes()) {
			Pattern pattern = aNode.getNamePattern();
			if (!(pattern == null || pattern.matcher(component.getId()).find()))
				continue;

			String matchingCType = aNode.getComponentType();
			if (!(matchingCType == null || matchingCType.equals(component.getType())))
				continue;

			Filter filter = aNode.getFilter();
			if (!(filter == null || filter.match(properties)))
				continue;
			return aNode;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String getMaterializer(ComponentName component, Map<String, String> properties) {
		String mat = null;
		MaterializationNode node = getMatchingNode(component, properties);
		if (node != null)
			mat = node.getMaterializer();
		if (mat == null)
			mat = getMaterializer();
		return mat;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public int getMaxParallelJobs() {
		return maxParallelJobs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public EList<MaterializationNode> getMspecNodes() {
		if (mspecNodes == null) {
			mspecNodes = new EObjectContainmentEList<MaterializationNode>(MaterializationNode.class, this,
					MspecPackage.MATERIALIZATION_SPEC__MSPEC_NODES);
		}
		return mspecNodes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String getProjectName(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		if (node == null)
			return null;

		Pattern bindingNamePattern = node.getBindingNamePattern();
		String bindingNameReplacement = node.getBindingNameReplacement();
		if (bindingNamePattern == null || bindingNameReplacement == null)
			return null;

		Matcher matcher = bindingNamePattern.matcher(component.getId());
		if (matcher.matches()) {
			String repl = matcher.replaceAll(bindingNameReplacement).trim();
			if (repl.length() > 0)
				return repl;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public URL getResolvedURL(Map<String, String> properties) {
		if (getContextURL() == null || getUrl() == null)
			return null;
		return URLUtils.resolveURL(getContextURL(), ExpandingProperties.expand(properties, getUrl(), 0));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public IPath getResourcePath(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		if (node == null)
			return null;
		String path = node.getResourcePath();
		return path == null ? null : Path.fromPortableString(path);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public String getShortDesc() {
		return shortDesc;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String getSuffix(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		return node == null ? null : node.getSuffix();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public String getUrl() {
		return url;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean isExcluded(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		return node != null && node.isExclude();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean isExpand(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		return node != null && (node.isUnpack() && node.isExpand());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean isUnpack(ComponentName component, Map<String, String> properties) {
		MaterializationNode node = getMatchingNode(component, properties);
		return node != null && node.isUnpack();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void setMaxParallelJobs(int newMaxParallelJobs) {
		int oldMaxParallelJobs = maxParallelJobs;
		maxParallelJobs = newMaxParallelJobs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MspecPackage.MATERIALIZATION_SPEC__MAX_PARALLEL_JOBS, oldMaxParallelJobs,
					maxParallelJobs));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MspecPackage.MATERIALIZATION_SPEC__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void setShortDesc(String newShortDesc) {
		String oldShortDesc = shortDesc;
		shortDesc = newShortDesc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MspecPackage.MATERIALIZATION_SPEC__SHORT_DESC, oldShortDesc, shortDesc));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void setUrl(String newUrl) {
		String oldUrl = url;
		url = newUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MspecPackage.MATERIALIZATION_SPEC__URL, oldUrl, url));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", shortDesc: ");
		result.append(shortDesc);
		result.append(", url: ");
		result.append(url);
		result.append(", maxParallelJobs: ");
		result.append(maxParallelJobs);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	protected EClass eStaticClass() {
		return MspecPackage.Literals.MATERIALIZATION_SPEC;
	}

} // MaterializationSpecImpl
