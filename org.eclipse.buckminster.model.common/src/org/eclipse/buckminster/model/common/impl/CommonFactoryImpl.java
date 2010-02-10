/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.buckminster.model.common.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.buckminster.model.common.CommonFactory;
import org.eclipse.buckminster.model.common.CommonPackage;
import org.eclipse.buckminster.model.common.ComponentIdentifier;
import org.eclipse.buckminster.model.common.ComponentRequest;
import org.eclipse.buckminster.model.common.Constant;
import org.eclipse.buckminster.model.common.Documentation;
import org.eclipse.buckminster.model.common.Format;
import org.eclipse.buckminster.model.common.Match;
import org.eclipse.buckminster.model.common.PropertyConstant;
import org.eclipse.buckminster.model.common.PropertyElement;
import org.eclipse.buckminster.model.common.PropertyRef;
import org.eclipse.buckminster.model.common.Replace;
import org.eclipse.buckminster.model.common.RxGroup;
import org.eclipse.buckminster.model.common.RxPattern;
import org.eclipse.buckminster.model.common.Split;
import org.eclipse.buckminster.model.common.SplitType;
import org.eclipse.buckminster.model.common.ToLower;
import org.eclipse.buckminster.model.common.ToUpper;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.osgi.filter.FilterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.osgi.framework.InvalidSyntaxException;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class CommonFactoryImpl extends EFactoryImpl implements CommonFactory {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static CommonPackage getPackage() {
		return CommonPackage.eINSTANCE;
	}

	/**
	 * Creates the default factory implementation. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public static CommonFactory init() {
		try {
			CommonFactory theCommonFactory = (CommonFactory) EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/buckminster/Common-1.0");
			if (theCommonFactory != null) {
				return theCommonFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new CommonFactoryImpl();
	}

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public CommonFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertFilterToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertPatternToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertPropertyKeyToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertSplitTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case CommonPackage.SPLIT_TYPE:
				return convertSplitTypeToString(eDataType, instanceValue);
			case CommonPackage.PROPERTY_KEY:
				return convertPropertyKeyToString(eDataType, instanceValue);
			case CommonPackage.UUID:
				return convertUuidToString(eDataType, instanceValue);
			case CommonPackage.PATTERN:
				return convertPatternToString(eDataType, instanceValue);
			case CommonPackage.FILTER:
				return convertFilterToString(eDataType, instanceValue);
			case CommonPackage.VERSION:
				return convertVersionToString(eDataType, instanceValue);
			case CommonPackage.VERSION_RANGE:
				return convertVersionRangeToString(eDataType, instanceValue);
			case CommonPackage.URL:
				return convertURLToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertURLToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertUuidToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertVersionRangeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String convertVersionToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case CommonPackage.CONSTANT:
				return createConstant();
			case CommonPackage.DOCUMENTATION:
				return createDocumentation();
			case CommonPackage.FORMAT:
				return createFormat();
			case CommonPackage.MATCH:
				return createMatch();
			case CommonPackage.PROPERTY_CONSTANT:
				return createPropertyConstant();
			case CommonPackage.PROPERTY_ELEMENT:
				return createPropertyElement();
			case CommonPackage.PROPERTY_REF:
				return createPropertyRef();
			case CommonPackage.REPLACE:
				return createReplace();
			case CommonPackage.RX_GROUP:
				return createRxGroup();
			case CommonPackage.RX_PATTERN:
				return createRxPattern();
			case CommonPackage.SPLIT:
				return createSplit();
			case CommonPackage.TO_LOWER:
				return createToLower();
			case CommonPackage.TO_UPPER:
				return createToUpper();
			case CommonPackage.DOCUMENT_ROOT:
				return createDocumentRoot();
			case CommonPackage.COMPONENT_REQUEST:
				return createComponentRequest();
			case CommonPackage.COMPONENT_IDENTIFIER:
				return createComponentIdentifier();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ComponentIdentifier createComponentIdentifier() {
		ComponentIdentifierImpl componentIdentifier = new ComponentIdentifierImpl();
		return componentIdentifier;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ComponentRequest createComponentRequest() {
		ComponentRequestImpl componentRequest = new ComponentRequestImpl();
		return componentRequest;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Constant createConstant() {
		ConstantImpl constant = new ConstantImpl();
		return constant;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Documentation createDocumentation() {
		DocumentationImpl documentation = new DocumentationImpl();
		return documentation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject createDocumentRoot() {
		EObject documentRoot = super.create(CommonPackage.Literals.DOCUMENT_ROOT);
		return documentRoot;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Filter createFilterFromString(EDataType eDataType, String initialValue) {
		try {
			return initialValue == null ? null : FilterFactory.newInstance(initialValue);
		} catch (InvalidSyntaxException e) {
			return null;
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Format createFormat() {
		FormatImpl format = new FormatImpl();
		return format;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case CommonPackage.SPLIT_TYPE:
				return createSplitTypeFromString(eDataType, initialValue);
			case CommonPackage.PROPERTY_KEY:
				return createPropertyKeyFromString(eDataType, initialValue);
			case CommonPackage.UUID:
				return createUuidFromString(eDataType, initialValue);
			case CommonPackage.PATTERN:
				return createPatternFromString(eDataType, initialValue);
			case CommonPackage.FILTER:
				return createFilterFromString(eDataType, initialValue);
			case CommonPackage.VERSION:
				return createVersionFromString(eDataType, initialValue);
			case CommonPackage.VERSION_RANGE:
				return createVersionRangeFromString(eDataType, initialValue);
			case CommonPackage.URL:
				return createURLFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Match createMatch() {
		MatchImpl match = new MatchImpl();
		return match;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Pattern createPatternFromString(EDataType eDataType, String initialValue) {
		return Pattern.compile(initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public PropertyConstant createPropertyConstant() {
		PropertyConstantImpl propertyConstant = new PropertyConstantImpl();
		return propertyConstant;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public PropertyElement createPropertyElement() {
		PropertyElementImpl propertyElement = new PropertyElementImpl();
		return propertyElement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String createPropertyKeyFromString(EDataType eDataType, String initialValue) {
		return (String) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public PropertyRef createPropertyRef() {
		PropertyRefImpl propertyRef = new PropertyRefImpl();
		return propertyRef;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Replace createReplace() {
		ReplaceImpl replace = new ReplaceImpl();
		return replace;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public RxGroup createRxGroup() {
		RxGroupImpl rxGroup = new RxGroupImpl();
		return rxGroup;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public RxPattern createRxPattern() {
		RxPatternImpl rxPattern = new RxPatternImpl();
		return rxPattern;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Split createSplit() {
		SplitImpl split = new SplitImpl();
		return split;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public SplitType createSplitTypeFromString(EDataType eDataType, String initialValue) {
		SplitType result = SplitType.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ToLower createToLower() {
		ToLowerImpl toLower = new ToLowerImpl();
		return toLower;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ToUpper createToUpper() {
		ToUpperImpl toUpper = new ToUpperImpl();
		return toUpper;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public URL createURLFromString(EDataType eDataType, String initialValue) {
		try {
			return initialValue == null ? null : new URL(initialValue);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public UUID createUuidFromString(EDataType eDataType, String initialValue) {
		return initialValue == null ? null : UUID.fromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Version createVersionFromString(EDataType eDataType, String initialValue) {
		return Version.create(initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public VersionRange createVersionRangeFromString(EDataType eDataType, String initialValue) {
		return initialValue == null ? null : new VersionRange(initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public CommonPackage getCommonPackage() {
		return (CommonPackage) getEPackage();
	}

} // CommonFactoryImpl
