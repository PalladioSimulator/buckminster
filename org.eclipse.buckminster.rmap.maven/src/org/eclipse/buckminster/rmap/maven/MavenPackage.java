/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.buckminster.rmap.maven;

import org.eclipse.buckminster.rmap.RmapPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.buckminster.rmap.maven.MavenFactory
 * @model kind="package"
 * @generated
 */
public interface MavenPackage extends EPackage {
	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.buckminster.rmap.maven.impl.GroupAndArtifactImpl
		 * <em>Group And Artifact</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.eclipse.buckminster.rmap.maven.impl.GroupAndArtifactImpl
		 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getGroupAndArtifact()
		 * @generated
		 */
		EClass GROUP_AND_ARTIFACT = eINSTANCE.getGroupAndArtifact();

		/**
		 * The meta object literal for the '<em><b>Artifact Id</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute GROUP_AND_ARTIFACT__ARTIFACT_ID = eINSTANCE.getGroupAndArtifact_ArtifactId();

		/**
		 * The meta object literal for the '<em><b>Group Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute GROUP_AND_ARTIFACT__GROUP_ID = eINSTANCE.getGroupAndArtifact_GroupId();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.buckminster.rmap.maven.impl.MapEntryImpl
		 * <em>Map Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.eclipse.buckminster.rmap.maven.impl.MapEntryImpl
		 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMapEntry()
		 * @generated
		 */
		EClass MAP_ENTRY = eINSTANCE.getMapEntry();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MAP_ENTRY__NAME = eINSTANCE.getMapEntry_Name();

		/**
		 * The meta object literal for the '<em><b>Aliases</b></em>' reference
		 * list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference MAP_ENTRY__ALIASES = eINSTANCE.getMapEntry_Aliases();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.buckminster.rmap.maven.impl.MappingsImpl
		 * <em>Mappings</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.eclipse.buckminster.rmap.maven.impl.MappingsImpl
		 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMappings()
		 * @generated
		 */
		EClass MAPPINGS = eINSTANCE.getMappings();

		/**
		 * The meta object literal for the '<em><b>Entries</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference MAPPINGS__ENTRIES = eINSTANCE.getMappings_Entries();

		/**
		 * The meta object literal for the '<em><b>Rules</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference MAPPINGS__RULES = eINSTANCE.getMappings_Rules();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.buckminster.rmap.maven.impl.MavenProviderImpl
		 * <em>Provider</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.eclipse.buckminster.rmap.maven.impl.MavenProviderImpl
		 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMavenProvider()
		 * @generated
		 */
		EClass MAVEN_PROVIDER = eINSTANCE.getMavenProvider();

		/**
		 * The meta object literal for the '<em><b>Mappings</b></em>'
		 * containment reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EReference MAVEN_PROVIDER__MAPPINGS = eINSTANCE.getMavenProvider_Mappings();

	}

	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "maven";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/buckminster/MavenProvider-1.0";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "maven";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	MavenPackage eINSTANCE = org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl.init();

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.buckminster.rmap.maven.impl.GroupAndArtifactImpl
	 * <em>Group And Artifact</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see org.eclipse.buckminster.rmap.maven.impl.GroupAndArtifactImpl
	 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getGroupAndArtifact()
	 * @generated
	 */
	int GROUP_AND_ARTIFACT = 0;

	/**
	 * The feature id for the '<em><b>Artifact Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int GROUP_AND_ARTIFACT__ARTIFACT_ID = 0;

	/**
	 * The feature id for the '<em><b>Group Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int GROUP_AND_ARTIFACT__GROUP_ID = 1;

	/**
	 * The number of structural features of the '<em>Group And Artifact</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int GROUP_AND_ARTIFACT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.buckminster.rmap.maven.impl.MapEntryImpl
	 * <em>Map Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.buckminster.rmap.maven.impl.MapEntryImpl
	 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMapEntry()
	 * @generated
	 */
	int MAP_ENTRY = 1;

	/**
	 * The feature id for the '<em><b>Artifact Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAP_ENTRY__ARTIFACT_ID = GROUP_AND_ARTIFACT__ARTIFACT_ID;

	/**
	 * The feature id for the '<em><b>Group Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAP_ENTRY__GROUP_ID = GROUP_AND_ARTIFACT__GROUP_ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAP_ENTRY__NAME = GROUP_AND_ARTIFACT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Aliases</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAP_ENTRY__ALIASES = GROUP_AND_ARTIFACT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Map Entry</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAP_ENTRY_FEATURE_COUNT = GROUP_AND_ARTIFACT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.buckminster.rmap.maven.impl.MappingsImpl
	 * <em>Mappings</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.buckminster.rmap.maven.impl.MappingsImpl
	 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMappings()
	 * @generated
	 */
	int MAPPINGS = 2;

	/**
	 * The feature id for the '<em><b>Entries</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAPPINGS__ENTRIES = 0;

	/**
	 * The feature id for the '<em><b>Rules</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAPPINGS__RULES = 1;

	/**
	 * The number of structural features of the '<em>Mappings</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAPPINGS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.buckminster.rmap.maven.impl.MavenProviderImpl
	 * <em>Provider</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.buckminster.rmap.maven.impl.MavenProviderImpl
	 * @see org.eclipse.buckminster.rmap.maven.impl.MavenPackageImpl#getMavenProvider()
	 * @generated
	 */
	int MAVEN_PROVIDER = 3;

	/**
	 * The feature id for the '<em><b>Component Types</b></em>' attribute list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__COMPONENT_TYPES = RmapPackage.PROVIDER__COMPONENT_TYPES;

	/**
	 * The feature id for the '<em><b>Component Types Attr</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__COMPONENT_TYPES_ATTR = RmapPackage.PROVIDER__COMPONENT_TYPES_ATTR;

	/**
	 * The feature id for the '<em><b>Reader Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__READER_TYPE = RmapPackage.PROVIDER__READER_TYPE;

	/**
	 * The feature id for the '<em><b>Source</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__SOURCE = RmapPackage.PROVIDER__SOURCE;

	/**
	 * The feature id for the '<em><b>Mutable</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__MUTABLE = RmapPackage.PROVIDER__MUTABLE;

	/**
	 * The feature id for the '<em><b>Resolution Filter</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__RESOLUTION_FILTER = RmapPackage.PROVIDER__RESOLUTION_FILTER;

	/**
	 * The feature id for the '<em><b>Version Converter</b></em>' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__VERSION_CONVERTER = RmapPackage.PROVIDER__VERSION_CONVERTER;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__URI = RmapPackage.PROVIDER__URI;

	/**
	 * The feature id for the '<em><b>Matchers</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__MATCHERS = RmapPackage.PROVIDER__MATCHERS;

	/**
	 * The feature id for the '<em><b>Documentation</b></em>' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__DOCUMENTATION = RmapPackage.PROVIDER__DOCUMENTATION;

	/**
	 * The feature id for the '<em><b>Mappings</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER__MAPPINGS = RmapPackage.PROVIDER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Provider</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MAVEN_PROVIDER_FEATURE_COUNT = RmapPackage.PROVIDER_FEATURE_COUNT + 1;

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.buckminster.rmap.maven.GroupAndArtifact
	 * <em>Group And Artifact</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Group And Artifact</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.GroupAndArtifact
	 * @generated
	 */
	EClass getGroupAndArtifact();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.buckminster.rmap.maven.GroupAndArtifact#getArtifactId
	 * <em>Artifact Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Artifact Id</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.GroupAndArtifact#getArtifactId()
	 * @see #getGroupAndArtifact()
	 * @generated
	 */
	EAttribute getGroupAndArtifact_ArtifactId();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.buckminster.rmap.maven.GroupAndArtifact#getGroupId
	 * <em>Group Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Group Id</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.GroupAndArtifact#getGroupId()
	 * @see #getGroupAndArtifact()
	 * @generated
	 */
	EAttribute getGroupAndArtifact_GroupId();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.buckminster.rmap.maven.MapEntry <em>Map Entry</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Map Entry</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.MapEntry
	 * @generated
	 */
	EClass getMapEntry();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.buckminster.rmap.maven.MapEntry#getAliases
	 * <em>Aliases</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Aliases</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.MapEntry#getAliases()
	 * @see #getMapEntry()
	 * @generated
	 */
	EReference getMapEntry_Aliases();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.buckminster.rmap.maven.MapEntry#getName <em>Name</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.MapEntry#getName()
	 * @see #getMapEntry()
	 * @generated
	 */
	EAttribute getMapEntry_Name();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.buckminster.rmap.maven.Mappings <em>Mappings</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Mappings</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.Mappings
	 * @generated
	 */
	EClass getMappings();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.buckminster.rmap.maven.Mappings#getEntries
	 * <em>Entries</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Entries</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.Mappings#getEntries()
	 * @see #getMappings()
	 * @generated
	 */
	EReference getMappings_Entries();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.buckminster.rmap.maven.Mappings#getRules
	 * <em>Rules</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Rules</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.Mappings#getRules()
	 * @see #getMappings()
	 * @generated
	 */
	EReference getMappings_Rules();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MavenFactory getMavenFactory();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.buckminster.rmap.maven.MavenProvider
	 * <em>Provider</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Provider</em>'.
	 * @see org.eclipse.buckminster.rmap.maven.MavenProvider
	 * @generated
	 */
	EClass getMavenProvider();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.buckminster.rmap.maven.MavenProvider#getMappings
	 * <em>Mappings</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Mappings</em>
	 *         '.
	 * @see org.eclipse.buckminster.rmap.maven.MavenProvider#getMappings()
	 * @see #getMavenProvider()
	 * @generated
	 */
	EReference getMavenProvider_Mappings();

} // MavenPackage
