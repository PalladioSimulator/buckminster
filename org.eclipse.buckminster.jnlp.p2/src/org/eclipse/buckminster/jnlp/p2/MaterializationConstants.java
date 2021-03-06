/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.jnlp.p2;

/**
 * @author Karel Brezina
 * 
 */
public interface MaterializationConstants
{
	public static final String STEP_START = "StartStep";

	public static final String STEP_LOGIN = "LoginStep";

	public static final String STEP_RESTRICTION = "FolderRestrictionStep";

	public static final String STEP_SELECT_DISTRO = "SelectDistroStep";

	public static final String STEP_DOWNLOAD_LOCATION = "SimpleDownloadLocationStep";

	public static final String STEP_ADVANCED_SETTINGS = "SimpleAdvancedSettingsStep";

	public static final String STEP_OPERATION = "OperationStep";

	public static final String STEP_DONE = "DoneStep";

	public static final String STEP_FEEDS = "InfoStep";

	public static final String STEP_PUBLISH_LOGIN = "LoginStep";

	public static final String STEP_PUBLISH = "PublishStep";

	public static final String STEP_TP_INTRO = "TPIntroStep";

	public static final String STEP_TP_NEW_OR_CURRENT = "TPNewOrCurrent";

	public static final String STEP_TP_NEW_RECOMMENDED = "TPNewRecommended";

	public static final String STEP_TP_NEW_LOCATION = "TPNewLocation";

	public static final String STEP_TP_BACKUP_FOLDER = "TPNewLocation";

	public static final String STEP_TP_UPTODATE = "TPUptodate";

	public static final String STEP_TP_TOOL_SELECTION = "TPToolSelection";

	public static final String STEP_TP_OPERATION = "TPOperation";

	public static final String STEP_TP_DONE = "TPDone";

	public static final String PROP_PROPVERSION = "propVersion";

	public static final String PROP_POPUP_DELAY = "popupDelay";

	public static final String PROP_PROFILE_IMAGE_URL = "profileImageURL";

	public static final String PROP_PROFILE_TEXT = "profileText";

	public static final String PROP_ARTIFACT_URL = "artifactURL";

	public static final String PROP_ARTIFACT_NAME = "artifactName";

	public static final String PROP_ARTIFACT_VERSION = "artifactVersion";

	public static final String PROP_ARTIFACT_DESCRIPTION = "artifactDescription";

	public static final String PROP_ARTIFACT_DOCUMENTATION = "artifactDocumentation";

	public static final String PROP_BASE_PATH_URL = "basePathURL";

	public static final String PROP_FOLDER_PATH = "folderPath";

	public static final String PROP_WINDOW_TITLE = "windowTitle";

	public static final String PROP_WINDOW_ICON = "windowIcon";

	public static final String PROP_WIZARD_ICON = "wizardIcon";

	public static final String PROP_MATERIALIZATION_IMAGE = "splashImage";

	public static final String PROP_HELP_URL = "helpURL";

	public static final String PROP_MORE_INFO_URL = "moreInfoURL";

	public static final String PROP_ERROR_URL = "errorURL";

	public static final String PROP_LOGIN_REQUIRED = "loginRequired";

	public static final String PROP_LEARN_MORE_URL = "learnMoreURL";

	public static final String PROP_LEARN_MORE_CLOUDFEEDS_URL = "learnMoreCloudfeedsURL";

	public static final String PROP_LEARN_MORE_CLOUDREADER_URL = "learnMoreCloudreaderURL";

	public static final String PROP_HOME_PAGE_URL = "homePageURL";

	public static final String PROP_SERVICE_PROVIDER = "serviceProvider";

	public static final String PROP_LOGIN_KEY = "loginKey";

	public static final String PROP_DRAFT = "draft";

	public static final String PROP_DISTRO_ID = "distroId";

	public static final String PROP_CSPEC_ID = "cspecId";

	public static final String PROP_CSPEC_NAME = "cspecName";

	public static final String PROP_CSPEC_TYPE = "cspecType";

	public static final String PROP_CSPEC_VERSION_STRING = "cspecVersionString";

	public static final String PROP_ECLIPSE_DISTRO_TOOLS_34_UPDATE_SITE_URL = "eclipseDistroTools3.4UpdateSiteURL";

	public static final String PROP_ECLIPSE_DISTRO_TOOLS_33_UPDATE_SITE_URL = "eclipseDistroTools3.3UpdateSiteURL";

	public static final String PROP_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$

	public static final String PROP_SUPPORT_EMAIL = "supportEmail"; //$NON-NLS-1$

	public static final String PROPERTY_DISTRO_NAME = "distro.name";

	public static final String[] MATERIALIZERS = { "filesystem", "workspace" };

	public static final String ARTIFACT_TYPE_MSPEC = "mspec";

	public static final String ARTIFACT_TYPE_UNKNOWN = "unknown";

	public static final String ARTIFACT_UNKNOWN_TEXT = "error reading info";

	public static final String WINDOW_TITLE_UNKNOWN = "Materialization Wizard";

	public static final String READER_TYPE_CSSITE = "cssite";

	public static final String ERROR_WINDOW_TITLE = "Materialization Error";

	public static final String ERROR_HELP_TITLE = "troubleshooting your materialization";

	public static final String ERROR_HELP_URL = "http://www.eclipse.org/buckminster/materialization/troubleshooting.php";

	public static final String ERROR_CODE_MISSING_ARGUMENT_EXCEPTION = "MissingArgumentException";

	public static final String ERROR_CODE_REMOTE_IO_EXCEPTION = "RemoteIOException";

	public static final String ERROR_CODE_MISSING_PROPERTY_EXCEPTION = "MissingPropertyException";

	public static final String ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION = "MalformedPropertyException";

	public static final String ERROR_CODE_ARTIFACT_EXCEPTION = "ArtifactSAXException";

	public static final String ERROR_CODE_MATERIALIZATION_EXCEPTION = "MaterializationException";

	public static final String ERROR_CODE_RUNTIME_EXCEPTION = "RuntimeException";

	public static final String ERROR_CODE_BOM_IO_EXCEPTION = "BOMIOException";

	public static final String ERROR_CODE_FILE_IO_EXCEPTION = "FileIOException";

	public static final String ERROR_CODE_NO_AUTHENTICATOR_EXCEPTION = "NoAuthenticatorException";

	public static final String ERROR_CODE_AUTHENTICATOR_EXCEPTION = "AuthenticatorException";

	public static final String ERROR_CODE_NO_PUBLISHER_EXCEPTION = "NoPublisherException";

	public static final String ERROR_CODE_PUBLISHER_EXCEPTION = "PublisherException";

	public static final String ERROR_CODE_PUBLISHING_EXCEPTION = "PublishingException";

	public static final String ERROR_CODE_ALREADY_RUNNING_EXCEPTION = "AlreadyRunningException";

	public static final String ERROR_CODE_403_EXCEPTION = "403Exception";

	public static final String ERROR_CODE_404_EXCEPTION = "404Exception";

	public static final String ERROR_CODE_500_EXCEPTION = "500Exception";

	public static final String DEFAULT_MATERIALIZATION_FOLDER = "materializations";

	public static final String META_AREA = ".metadata";

	public static final String MATERIALIZATOR_PROPERTIES = "materializator.properties";

	public static final String LOCALPROP_ENABLE_TP_WIZARD = "enable.tp.wizard";

	public static final String VALUE_TRUE = "true";

	public static final String VALUE_FALSE = "false";
}
