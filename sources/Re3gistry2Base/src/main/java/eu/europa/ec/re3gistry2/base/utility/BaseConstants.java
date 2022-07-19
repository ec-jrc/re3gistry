/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.base.utility;

public class BaseConstants {

    /* - Generic keys - */
    // Appname
    public static final String KEY_APP_NAME = "Re3gistry2";

    // Locked Request referer key
    public static final String KEY_CHANGE_LANGUAGE_LOCKED_REFERRER = "webgate.ec.europa.eu";
    // Boolean string values
    public static final String KEY_BOOLEAN_STRING_TRUE = "true";
    public static final String KEY_BOOLEAN_STRING_FALSE = "false";
    // Latest version for new items
    public static final int KEY_LATEST_VERSION = 0;

    public static final String KEY_STANDARD_DATEFORMAT = "yyyy-MM-dd";

    /* - Files and folders - */
    // Web inf path name
    public static final String KEY_FOLDER_NAME_WEBINF = "WEB-INF";
    // Classes path name
    public static final String KEY_FOLDER_NAME_CLASSES = "classes";
    // Configuration folder name
    public static final String KEY_FOLDER_NAME_CONFIGURATIONS = "configurations_files";
    // Configuration file name
    public static final String KEY_FILE_NAME_CONFIGURATIONS = "configuration.properties";
    // ECAS properties file
    public static final String KEY_FILE_ECAS_PROPERTIES = "ecas-config.properties";
    // Install folder name
//    public static final String KEY_FOLDER_NAME_INSTALL = "install";
//    public static final String KEY_FILE_NAME_INSTALL = "create-and-init.sql";
    public static final String KEY_FILE_NAME_INSTALL = "drop.sql";
    // Installation file flag
    public static final String KEY_FILE_NAME_SYSTEMINSTALLED = "system.installed";
    public static final String KEY_FILE_NAME_SYSTEMINSTALLING = "system.installing";

    public static final String KEY_FILE_NAME_SOLR_COMPLETE_INDEXING_RUNNING = "solr_complete_indexing.running";
    public static final String KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING = "cacheall_complete.running";

    /* - Properties - */
    // Persistence unit name
    public static final String KEY_PROPERTY_PERSISTENCE_UNIT_NAME = "application.persistenceunitname";
    // Available Language key
    public static final String KEY_PROPERTY_AVAILABLE_LANGUAGE = "application.language.available";
    // Available Language separator key
    public static final String KEY_PROPERTY_SEPARATOR_AVAILABLE_LANGUAGE = "-";
    // Available Language separator key
    public static final String KEY_PROPERTY_AVAILABLE_LANGUAGE_LABEL = "application.language.label.";
    // Localization prefix
    public static final String KEY_PROPERTY_LOCALIZATION_PREFIX = "localizations";
    // Localization suffix
    public static final String KEY_PROPERTY_LOCALIZATION_SUFFIX = "LocalizationBundle";
    // Default locale
    public static final String KEY_PROPERTY_DEFAULT_LOCALE = "application.language.defaultLocale";
    // Suffix for roles permissions
    public static final String KEY_PROPERTY_ROLEPERMISSION_SUFFIX = "role.permissions.";
    // Login type keys
    public static final String KEY_PROPERTY_LOGIN_TYPE = "application.login.type";
    public static final String KEY_PROPERTY_LOGIN_TYPE_SHIRO = "SHIRO";
    public static final String KEY_PROPERTY_LOGIN_TYPE_ECAS = "ECAS";
    
    public static final String KEY_PROPERTY_INTERFACE_TYPE = "application.selected.interface";
    public static final String KEY_PROPERTY_INTERFACE_NEUTRAL_TYPE = "neutral";
    public static final String KEY_PROPERTY_INTERFACE_ECL_TYPE = "ecl";

    public static final String KEY_PROPERTY_BASESTATUSGROUP = "application.status.maingroup";
    public static final String KEY_PROPERTY_DATEFORMAT = "application.dateformat";

    public static final String ATTRIBUTE_CACHE_KEY = "re3gistry-rest-api-cache";
    public static final String ATTRIBUTE_CACHE_QUEUE_KEY = "re3gistry-rest-api-cache-queue";

    /**
     * Mail keys
     */
    public static final String KEY_MAIL_HOST = "mail.smtp.host";
    public static final String KEY_MAIL_PORT = "mail.smtp.port";
    public static final String KEY_MAIL_AUTH = "mail.smtp.auth";
    public static final String KEY_MAIL_STARTTLS = "mail.smtp.starttls.enable";
    public static final String KEY_MAIL_TEMPLATE = "mail.template";
    public static final String KEY_MAIL_USER = "mail.user";
    public static final String KEY_MAIL_PASSWORD = "mail.password";
    public static final String KEY_MAIL_SENDER = "mail.sender";
    public static final String KEY_MAIL_APPLICATION_ROOTURL = "application.rooturl";
    public static final String KEY_RELEASENOTE_RSS_APPLICATION = "application.releasenote.rss.path";
    public static final String KEY_TEXT_BANNER_APPLICATION = "application.banner";
    public static final String KEY_PRIVACY_APPLICATION = "application.privacy";

    public static final String KEY_SELECTED_INTERFACE = "application.selected.interface";
    public static final String KEY_SELECTED_INTERFACE_ECL = "ecl";
    public static final String KEY_SELECTED_INTERFACE_NEUTRAL = "neutral";
    
    /* Mail Subjects  and body */
    public static final String KEY_EMAIL_SUBJECT_SUCCESS = "mail.text.subject.success";
    public static final String KEY_EMAIL_BODY_SUCCESS = "mail.text.body.success";
    public static final String KEY_EMAIL_SUBJECT_ERROR = "mail.text.subject.error";
    public static final String KEY_EMAIL_BODY_ERROR = "mail.text.body.error";
    public static final String KEY_EMAIL_SUBJECT_NEW_REGISTRATION = "mail.text.subject.newregistration";
    public static final String KEY_EMAIL_BODY_NEW_REGISTRATION = "mail.text.error.newregistration";
    public static final String KEY_EMAIL_BODY_ECAS_NEW_REGISTRATION = "mail.text.error.ecas.newregistration";
    public static final String KEY_EMAIL_SUBJECT_RESETPASSWORD = "mail.text.subject.resetpassword.registration";
    public static final String KEY_EMAIL_BODY_RESETPASSWORD = "mail.text.error.resetpassword.registration";

    public static final String KEY_EMAIL_SUBJECT_SOLR_SUCCESS = "mail.text.subject.solr.success";
    public static final String KEY_EMAIL_BODY_SOLR_SUCCESS = "mail.text.body.solr.success";
    public static final String KEY_EMAIL_SUBJECT_SOLR_ERROR = "mail.text.subject.solr.error";
    public static final String KEY_EMAIL_BODY_SOLR_ERROR = "mail.text.body.solr.error";
    public static final String KEY_EMAIL_SUBJECT_CACHE_SUCCESS = "mail.text.subject.cache.success";
    public static final String KEY_EMAIL_BODY_CACHE_SUCCESS = "mail.text.body.cache.success";
    public static final String KEY_EMAIL_SUBJECT_CACHE_ERROR = "mail.text.subject.cache.error";
    public static final String KEY_EMAIL_BODY_CACHE_ERROR = "mail.text.body.cache.error";
    public static final String KEY_EMAIL_SUBJECT_ITEM_PUBLISHED = "mail.text.subject.item.publised";
    public static final String KEY_EMAIL_BODY_ITEM_PUBLISHED = "mail.text.body.item.publised";

    /**
     * mail subject and body bulk import
     */
    public static final String KEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS = "mail.text.subject.bulkimport.success";
    public static final String KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR = "mail.text.subject.bulkimport.error";
    public static final String KEY_EMAIL_BODY_BULKIMPORT_SUCCESS = "mail.text.body.bulkimport.success";
    public static final String KEY_EMAIL_BODY_BULKIMPORT_ERROR = "mail.text.body.bulkimport.error";

    // Input sanitizer keys
    public static final String KEY_PROPERTY_INPUT_SANITIZER_LEVEL = "application.input.sanitizer.level";
    public static final String KEY_PROPERTY_INPUT_SANITIZER_LEVEL_SIMPLETEXT = "simpleText";
    public static final String KEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASIC = "basic";
    public static final String KEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASICWITHIMAGES = "basicWithImages";
    public static final String KEY_PROPERTY_INPUT_SANITIZER_LEVEL_RELAXED = "relaxed";

    // Solr configurations
    public static final String KEY_PROPERTY_SOLR_ACTIVE = "application.solr.isactive";
    public static final String KEY_PROPERTY_SOLR_URL = "application.solr.url";
    public static final String KEY_PROPERTY_SOLR_CORE = "application.solr.core";
    public static final String KEY_PROPERTY_SOLR_CONNECTION_TIMEOUT = "application.solr.connectiontimeout";
    public static final String KEY_PROPERTY_SOLR_SOCKET_TIMEOUT = "application.solr.sockettimeout";
    public static final String KEY_PROPERTY_SOLR_QUERY_FQ_PARENT_ITEMCLASS_LOCALID = "parent_itemclass_localid";
    public static final String KEY_PROPERTY_SOLR_QUERY_FQ_ITEMCLASS_LOCALID = "itemclass_localid";
    public static final String KEY_PROPERTY_SOLR_QUERY_FL = "id";
    public static final String KEY_PROPERTY_SOLR_DOCUMENT_ID = "id";
    public static final String KEY_PROPERTY_SOLR_QUERY_FIELD_LABEL_PREFIX = "fl_label_";
    public static final String KEY_PROPERTY_SOLR_FILEDTOINDEX_LOCALID = "application.solr.fieldtoindex.localid";

    // Field name separator
    public static final String KEY_SOLR_FIELD_NAME_SEPARATOR = "_";

    // Solr fields name
    public static final String KEY_SOLR_FIELD_NAME_ID = "id";
    public static final String KEY_SOLR_FIELD_NAME_LOCALID = "localid";
    public static final String KEY_SOLR_FIELD_NAME_MODE = "mode";
    public static final String KEY_SOLR_FIELD_NAME_MAPPED_ITEMCLASS = "mapped_itemclass";
    public static final String KEY_SOLR_FIELD_NAME_SORTINGVALUE = "sorting_value";
    public static final String KEY_SOLR_FIELD_NAME_TABLEVISIBLE = "table_visible";
    public static final String KEY_SOLR_FIELD_NAME_FIELD_PREFIX = "fl_";
    public static final String KEY_SOLR_FIELD_NAME_REGISTER_LABEL = "register_label";
    public static final String KEY_SOLR_FIELD_NAME_REGISTRY_LABEL = "registry_label";
    public static final String KEY_SOLR_FIELD_NAME_SORTING = "field_sorting";

    // Solr item field name, prefixes, suffixes
    public static final String KEY_SOLR_ITEM_ID = "id";
    public static final String KEY_SOLR_ITEM_LOCALID = "localid";
    public static final String KEY_SOLR_ITEM_FIELD_PREFIX = "fl_";
    public static final String KEY_SOLR_ITEM_RELATION_PREFIX = "rel_";
    public static final String KEY_SOLR_ITEM_MASTER_LANGUAGE = "master_language";
    public static final String KEY_SOLR_ITEM_EXTERNAL = "external";
    public static final String KEY_SOLR_ITEM_CURRENTVERSION = "currentversion";
    public static final String KEY_SOLR_ITEM_VERSIONNUMBER = "versionnumber";
    public static final String KEY_SOLR_ITEM_INSERTDATE = "insertdate";
    public static final String KEY_SOLR_ITEM_EDITDATE = "editdate";
    public static final String KEY_SOLR_ITEM_ITEMCLASSLOCALID = "itemclass_localid";
    public static final String KEY_SOLR_ITEM_ITEMCLASSTYPE = "itemclass_type";
    public static final String KEY_SOLR_ITEM_PARENTITEMCLASSLOCALID = "parent_itemclass_localid";
    public static final String KEY_SOLR_ITEM_PARENTITEMCLASSBASEURI = "parent_itemclass_baseuri";
    public static final String KEY_SOLR_ITEM_PARENTITEMCLASSTYPE = "parent_itemclass_type";
    public static final String KEY_SOLR_ITEM_REGISTRYITEMCLASSBASEURI = "registry_itemclass_baseuri";
    public static final String KEY_SOLR_ITEM_REGISTRYITEMCLASSLOCALID = "registry_itemclass_localid";
    public static final String KEY_SOLR_ITEM_REGISTRYLOCALID = "registry_localid";
    public static final String KEY_SOLR_ITEM_REGISTERITEMCLASSBASEURI = "register_itemclass_baseuri";
    public static final String KEY_SOLR_ITEM_REGISTERITEMCLASSLOCALID = "register_itemclass_localid";
    public static final String KEY_SOLR_ITEM_REGISTERLOCALID = "register_localid";
    public static final String KEY_SOLR_ITEM_COLLECTIONLOCALID = "collection_localid";
    public static final String KEY_SOLR_ITEM_COLLECTIONITEMCLASSLOCALID = "collection_itemclass_localid";
    public static final String KEY_SOLR_ITEM_CONTAINEDITEMCLASSLOCALID = "contained_itemclass_localid";
    public static final String KEY_SOLR_ITEM_URI = "uri";
    public static final String KEY_SOLR_ITEM_HREF_PREFIX = "value_href_";
    public static final String KEY_SOLR_ITEM_STATUS = "status_uri";

    /* - Session keys - */
    // Language session key
    public static final String KEY_SESSION_LANGUAGE = "lang";
    // Session user key
    public static final String KEY_SESSION_USER = "user";
    public static final String MIGRATION_USER_NAME = "user@migration.com";
    public static final String MIGRATION_USER_PASSWORD = "migration";

    public static final String KEY_FIELD_EXTENSIBILITY = "Extensibility";
    public static final String KEY_ITEMCLASS_EXTENSIBILITY = "extensibility";
    public static final String KEY_ITEMCLASS_EXTENSIBILITY_ITEM = "extensibility-item";
    public static final String KEY_FIELD_GOVERNANCELEVEL = "GovernanceLevel";
    public static final String KEY_ITEMCLASS_GOVERNANCELEVEL = "governance-level";
    public static final String KEY_ITEMCLASS_GOVERNANCELEVEL_ITEM = "governance-level-item";

    // Session user group
    public static final String KEY_SESSION_USERPERGROUPSMAP = "userGroupsMap";
    // Available languages
    public static final String KEY_SESSION_AVAILABLELANGUAGES = "availableLanguages";
    public static final String KEY_SESSION_RESET_PASSWORD_SUCCESS = "resetPasswordSuccess";

    /* - Keys user actions - */
    public static final String KEY_USER_ACTION_MANAGEITEMPROPOSAL = "ManageItemProposal";
    public static final String KEY_USER_ACTION_MANAGEREGISTERREGISTRY = "ManageRegisterRegistry";
    public static final String KEY_USER_ACTION_MANAGEFIELDMAPPING = "ManageFieldMapping";
    public static final String KEY_USER_ACTION_MANAGEFIELD = "ManageField";
    public static final String KEY_USER_ACTION_MANAGEUSER = "ManageUser";
    public static final String KEY_USER_ACTION_MANAGEGROUP = "ManageGroup";
    public static final String KEY_USER_ACTION_MAPGROUPTOITEM = "MapGroupToItem";
    public static final String KEY_USER_ACTION_MAPUSERTOGROUP = "MapUserToGroup";
    public static final String KEY_USER_ACTION_SUBMITPROPOSAL = "SubmitProposal";
    public static final String KEY_USER_ACTION_APPROVEPROPOSAL = "ApproveProposal";
    public static final String KEY_USER_ACTION_PUBLISHPROPOSAL = "PublishProposal";
    public static final String KEY_USER_ACTION_MANAGESYSTEM = "ManageSystem";

    /* - Request keys - */
    // Request parameters keys
    public static final String KEY_REQUEST_ITEMUUID = "itemUuid";
    public static final String KEY_REQUEST_ITEMCLASSUUID = "itemclassUuid";
    public static final String KEY_REQUEST_LANGUAGEUUID = "languageUuid";
    public static final String KEY_REQUEST_FIELDUUID = "fieldUuid";
    public static final String KEY_REQUEST_FIELDMAPPINGUUID = "fieldmappingUuid";
    public static final String KEY_REQUEST_NEWPOSITION = "newPosition";
    public static final String KEY_REQUEST_CHECKBOXTYPE = "checkboxType";
    public static final String KEY_REQUEST_CHECKED = "checked";
    public static final String KEY_REQUEST_USER_ERROR_MESSAGES = "userErrorMessages";
    public static final String KEY_REQUEST_USER_SUCCESS_MESSAGES = "userSuccessMessages";
    public static final String KEY_REQUEST_USER_NOT_AVAILABLE = "userNotAvailable";
    public static final String KEY_REQUEST_USER_NOT_LOGGEDIN = "userNotLoggedin";
    public static final String KEY_REQUEST_USER_NOT_ENABLED = "userNotEnabled";

    public static final String KEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR = "installationError";
    public static final String KEY_REQUEST_USER_CREATION_STARTED = "userCreationStarted";
    public static final String KEY_REQUEST_USER_CREATION_ERROR = "userCreationError";

    public static final String KEY_REQUEST_USER_RESETPASSWORD_SUCCESS = "userResetpasswordError";
    public static final String KEY_REQUEST_USER_RESETPASSWORD_ERROR = "userResetpasswordError";
    public static final String KEY_REQUEST_USER_RESETPASSWORD_USERNOTAVAILABLE_ERROR = "usernotavailableResetpasswordError";

    public static final String KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR = "installationCleanDBError";
    public static final String KEY_REQUEST_INSTALLATION_SUCCESS = "installationSuccess";
    public static final String KEY_REQUEST_INSTALLATION_MIGRATION_ERROR = "installationMigrationError";

    public static final String KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION = "incorrectCredentialsException";
    public static final String KEY_REQUEST_USER_UNKNOWN_ACCOUNT_EXCEPTION = "UnknownAccountException";
    public static final String KEY_REQUEST_USER_LOCKED_ACCOUNT_EXCEPTION = "LockedAccountException";

    public static final String KEY_REQUEST_LOGIN_CONFIGURATION_ERRORR = "loginConfigurationError";
    public static final String KEY_REQUEST_LANGUAGE = "lang";
    public static final String KEY_REQUEST_REFERER = "referer";
    public static final String KEY_REQUEST_NEWITEMINSERT = "newiteminsert";
    public static final String KEY_REQUEST_NEWSTATUSLOCALID = "newStatusLocalid";
    public static final String KEY_REQUEST_NEWREGSTATUS = "newRegStatus";
    public static final String KEY_REQUEST_NOTEDITABLE = "notEditable";
    public static final String KEY_REQUEST_STEP = "step";
    public static final String KEY_REQUEST_GROUP_UUID = "groupuuid";
    public static final String KEY_REQUEST_ACTIONTYPE = "actionType";
    public static final String KEY_REQUEST_USERDETAIL_UUID = "userdetailuuid";
    public static final String KEY_REQUEST_USERGROUPMAPPING_UUID = "usergroupmappinguuid";
    public static final String KEY_REQUEST_MASTERLANGUAGE = "masterLanguage";
    public static final String KEY_REQUEST_CURRENTLANGUAGE = "currentLanguage";
    public static final String KEY_REQUEST_LANGUAGECODES = "languageCodes";
    public static final String KEY_REQUEST_ACTIVE_LANGUAGECODES = "activeLanguageCodes";
    public static final String KEY_REQUEST_OPERATIONRESULT = "operationresult";
    public static final String KEY_REQUEST_REGITEMCLASSES = "regItemclasses";
    public static final String KEY_REQUEST_REGFIELDMAPPINGS = "regFieldmappings";
    public static final String KEY_REQUEST_PARENTREGITEMCLASS = "parentRegItemclass";
    public static final String KEY_REQUEST_REGISTRYITEM = "registryRegItem";
    public static final String KEY_REQUEST_REGISTRYITEMCLASS = "registryRegItemclass";
    public static final String KEY_REQUEST_REGFIELD = "regField";
    public static final String KEY_REQUEST_REGITEMCLASS = "regItemclass";
    public static final String KEY_REQUEST_REGITEMCLASS_NEWITEM = "regItemclassNewItem";
    public static final String KEY_REQUEST_REGFIELDS = "regFields";
    public static final String KEY_REQUEST_REGFIELDMAPPINGSITEMCLASS = "regFieldmappingsItemclass";
    public static final String KEY_REQUEST_REGFIELDTYPES = "regFieldtypes";
    public static final String KEY_REQUEST_LOCALIZATION = "localization";
    public static final String KEY_REQUEST_REGISTRYLIST = "registryList";
    public static final String KEY_REQUEST_ITEM = "item";
    public static final String KEY_REQUEST_SELECT_LIST_ITEMS = "selectListItems";
    public static final String KEY_REQUEST_REGITEM_REGGROUP_REG_ROLE_MAPPING_ROLES = "regItemRegGroupRegRoleMappings";
    public static final String KEY_REQUEST_ITEMS_ALREADY_IN_RELATION = "alreadyInRelationListItems";
    public static final String KEY_REQUEST_ITEM_PROPOSED = "regItemproposed";
    public static final String KEY_REQUEST_ITEM_PROPOSEDS = "regItemproposeds";
    public static final String KEY_REQUEST_ITEM_HISTORYS = "regItemhistorys";
    public static final String KEY_REQUEST_FIELDMAPPINGS = "fieldMappings";
    public static final String KEY_REQUEST_DT_START = "start";
    public static final String KEY_REQUEST_DT_LENGTH = "length";
    public static final String KEY_REQUEST_DT_DRAW = "draw";
    public static final String KEY_REQUEST_DT_SEARCHVALUE = "search[value]";
    public static final String KEY_REQUEST_REGLOCALIZATION = "regLocalization";
    public static final String KEY_REQUEST_LOCALIZATIONMASTERLANGUAGE = "regLocalizationMasterLanguage";
    public static final String KEY_REQUEST_REGITEM = "regItem";
    public static final String KEY_REQUEST_REGITEMS = "regItems";
    public static final String KEY_REQUEST_REGUSERSDETAIL = "regUsersDetail";
    public static final String KEY_REQUEST_REGGROUPS = "regGroups";
    public static final String KEY_REQUEST_CHILDITEMCLASS = "childItemclass";
    public static final String KEY_REQUEST_REGITEMCLASSTYPES = "regItemclasstypes";
    public static final String KEY_REQUEST_REGUSER = "regUser";
    public static final String KEY_REQUEST_REGUSERDETAIL = "regUserDetail";
    public static final String KEY_REQUEST_REGGROUP = "regGroup";
    public static final String KEY_REQUEST_SHOW_CHANGES = "showchanges";
    public static final String KEY_REQUEST_SHOW_CHANGESBAR = "showchangesbar";
    public static final String KEY_REQUEST_LOCALIZATIONPROPOSED_CHANGEDLANGUAGES = "changedLanguagesd";
    public static final String KEY_REQUEST_LOCALIZATIONPROPOSEDUUID = "localizationProposedUuid";
    public static final String KEY_REQUEST_REGLOCALIZATIONUUID = "localizationUuid";
    public static final String KEY_REQUEST_REMOVE_VALUE_TYPE = "removeValueType";
    public static final String KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE = "remove";
    public static final String KEY_REQUEST_REMOVE_VALUE_TYPE_UPDATE = "update";
    public static final String KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD = "reload";
    public static final String KEY_REQUEST_TYPE_EDIT = "edit";
    public static final String KEY_REQUEST_AVAILABLELANGUAGES = "availableLanguages";
    public static final String KEY_REQUEST_CURRENTPAGENAME = "currentPageName";
    public static final String KEY_REQUEST_PROPERTIES = "properties";
    public static final String KEY_REQUEST_REGSTATUSLOCALIZATIONS = "regStatuslocalizations";
    public static final String KEY_REQUEST_REGSTATUSGROUP = "regStatusgroup";

    public static final String KEY_REQUEST_DOWNLOAD_BULK_TEMPLATE = "downloadBulkImportTemplate";
    public static final String KEY_REQUEST_LOAD_BULK_TEMPLATE = "loadBulkImport";
    public static final String KEY_REQUEST_LOAD_FILE_BULK = "fileUpload.csv";
    public static final String KEY_REQUEST_BULK_ERROR = "bulkError";
    public static final String KEY_REQUEST_BULK_SUCCESS = "bulkSuccess";

    public static final String KEY_REQUEST_CSRF_PREVENTIONSALTCACHE = "csrfPreventionSaltCache";
    public static final String KEY_REQUEST_CSRF_PREVENTIONSALT = "csrfPreventionSalt";
    public static final String KEY_REQUEST_CSRF_PREVENTION_ERROR = "csrfPreventionError";

    /**
     * request migration DB
     */
    public static final String KEY_REQUEST_REGISTRY_STATISTICS = "registryStatistics";
    public static final String KEY_REQUEST_REGISTER_LIST_STATISTICS = "registerListStatistics";

    public static final String KEY_REQUEST_MIGRATION_DBADDRESS = "dbAddress";
    public static final String KEY_REQUEST_MIGRATION_DBPORT = "dbPort";
    public static final String KEY_REQUEST_MIGRATION_DBNAME = "dbName";
    public static final String KEY_REQUEST_MIGRATION_DBUSERNAME = "dbUsername";
    public static final String KEY_REQUEST_MIGRATION_DBPASSWORD = "dbPassword";
    public static final String KEY_REQUEST_ADMIN_USERNAME = "adminUsername";
    public static final String KEY_REQUEST_ADMIN_PASSWORD = "adminPassword";
    public static final String KEY_REQUEST_ADMIN_SSOREFERENCE = "adminSsoreference";
    public static final String KEY_REQUEST_ADMIN_CONFIRM_PASSWORD = "adminConfirmPassword";
    public static final String KEY_REQUEST_CLEAN_INSTALLATION_PROFILE = "clean-installation";
    public static final String KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY = "clean-installation-summary";
    public static final String KEY_REQUEST_CLEAN_INSTALLATION_PROCESS = "clean-installation-process";
    public static final String KEY_REQUEST_MIGRATION_PROFILE = "migration";
    public static final String KEY_REQUEST_MIGRATION_PROCESS = "migration-process";
    public static final String KEY_REQUEST_MIGRATION_SUMMARY = "migration-summary";
    public static final String KEY_REQUEST_PROFILE = "profile";

    public static final String KEY_REQUEST_USER_OLD_PASSWORD = "oldPassword";
    public static final String KEY_REQUEST_USER_NEW_PASSWORD = "newPassword";
    public static final String KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD = "confirmNewPassword";

    public static final String KEY_REQUEST_MIGRATION_ERROR = "migrationError";
    public static final String KEY_REQUEST_MIGRATION_COMPLETED = "migrationCompleted";
    public static final String KEY_REQUEST_FORM_ITEMUUID = "form_item-uuid";
    public static final String KEY_REQUEST_FORM_ITEMUUID_NEW = "new";
    public static final String KEY_REQUEST_ACTION_LIST = "actionlist";
    public static final String KEY_REQUEST_ACTION = "action";
    public static final String KEY_REQUEST_ACTION_UUID = "actionuuid";
    public static final String KEY_REQUEST_SUCCESSORS = "successors";
    public static final String KEY_REQUEST_REGISTRY_REGITEMCLASSES = "registryRegItemclasses";
    public static final String KEY_REQUEST_REGISTER_REGITEMCLASSES = "registerRegItemclasses";
    public static final String KEY_REQUEST_ITEM_REGITEMCLASSES = "itemRegItemclasses";
    public static final String KEY_REQUEST_REGISTRY_LOCALID = "registryLocalID";
    public static final String KEY_REQUEST_REGISTRY_ID = "ID";
    public static final String KEY_REQUEST_REGISTRY_BASEURI = "registryBaseURI";
    public static final String KEY_REQUEST_REGISTRY_LABEL = "registryLabel";
    public static final String KEY_REQUEST_REGUSERREGGROUPMAPPINGS = "regUserRegGroupMappings";
    public static final String KEY_REQUEST_RESULT_MESSAGE = "resultMessage";
    public static final String KEY_REQUEST_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_REQUEST_RESULT = "requestResult";
    public static final String KEY_REQUEST_REGISTRY_CONTENT_SUMMARY = "registryContentSummary";
    public static final String KEY_REQUEST_STARTINDEX = "startIndex";
    public static final String KEY_REQUEST_STARTCACHING = "startCaching";
    public static final String KEY_REQUEST_STARTCACHING_MASTERLANGUAGE = "startCachingMasterLanguage";
    public static final String KEY_REQUEST_REMOVECACHING = "removeCaching";

    /* - Keys for naming the form fields - */
    public static final String KEY_FORM_FIELD_NAME_HREF_SUFFIX = "__href";
    public static final String KEY_FORM_FIELD_NAME_REFERENCEKEY = "__locref__";
    public static final String KEY_FORM_FIELD_NAME_INDEXKEY = "__n";
    public static final String KEY_FORM_FIELD_NAME_USERNAME = "username";
    public static final String KEY_FORM_FIELD_NAME_PASSWORD = "password";
    public static final String KEY_FORM_FIELD_NAME_REMEMBERME = "rememberMe";
    public static final String KEY_FORM_FIELD_NAME_LOCALID = "localid";
    public static final String KEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID = "itemclasstypeUuid";
    public static final String KEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID = "parentitemclassUuid";
    public static final String KEY_FORM_FIELD_NAME_FIELDUUID = "fieldUuid";
    public static final String KEY_FORM_FIELD_NAME_ACTIONUUID = "actionUuid";
    public static final String KEY_FORM_FIELD_NAME_USERUUID = "userUuid";
    public static final String KEY_FORM_FIELD_NAME_GROUPUUID = "groupUuid";
    public static final String KEY_FORM_FIELD_NAME_SUBMITACTION = "submitAction";
    public static final String KEY_FORM_FIELD_NAME_UPDATELABELACTION = "updateLabelAction";
    public static final String KEY_FORM_FIELD_NAME_APPROVE_TYPE = "approveType";
    public static final String KEY_FORM_FIELD_NAME_ITEMCLASSUUID = "itemclassUuid";
    public static final String KEY_FORM_FIELD_NAME_LOCALIZATIONUUID = "localizationuuid";
    public static final String KEY_FORM_FIELD_NAME_BASEURI = "baseuri";
    public static final String KEY_FORM_FIELD_NAME_DATAPROCEDUREORDER = "dataprocedureorder";
    public static final String KEY_FORM_FIELD_NAME_LABEL = "label";
    public static final String KEY_FORM_FIELD_NAME_FIELDTYPEUUID = "fieldtypeUuid";
    public static final String KEY_FORM_FIELD_NAME_ITEMCLASSREFERENCEUUID = "itemclassreferenceUuid";
    public static final String KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID = "languagecodeUuid";
    public static final String KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD = "editField";
    public static final String KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD = "removeField";
    public static final String KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD_FROM_CONTENTCLASS = "editFieldFromContentType";
    public static final String KEY_FORM_FIELD_NAME_VALUE = "value";
    public static final String KEY_FORM_FIELD_NAME_LANGUAGEUUID = "form_languageUuid";
    public static final String KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID = "regItemContainerUuid";
    public static final String KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID = "newItem_RegItemclassUuid";
    public static final String KEY_FORM_FIELD_NAME_CHANGELOG = "changeLog";
    public static final String KEY_FORM_FIELD_NAME_ISSUEREFERENCE = "issueReference";
    public static final String KEY_FORM_FIELD_NAME_COMMENTS = "comments";
    public static final String KEY_FORM_FIELD_NAME_REACTIONLABEL = "regActionLabel";
    public static final String KEY_FORM_FIELD_NAME_REGISTRY_UUID = "registryUuid";
    public static final String KEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID = "registryItemclassUuid";
    public static final String KEY_FORM_FIELD_NAME_KEEPPATH = "keeppath";
    public static final String KEY_FORM_FIELD_NAME_REGISTRY_BASEURI = "registrybaseuri";
    public static final String KEY_FORM_FIELD_NAME_REGISTRY_LOCALID = "registrylocalid";
    public static final String KEY_FORM_FIELD_NAME_REGISTER_BASEURI = "registerbaseuri";
    public static final String KEY_FORM_FIELD_NAME_DEFINITION = "definition";
    public static final String KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID = "itemclassuuid";
    public static final String KEY_FORM_FIELD_NAME_USER_NAME = "name";
    public static final String KEY_FORM_FIELD_NAME_GROUP_NAME = "name";
    public static final String KEY_FORM_FIELD_NAME_EMAIL = "email";
    public static final String KEY_FORM_FIELD_NAME_WEBSITE = "website";
    public static final String KEY_FORM_FIELD_NAME_SSO_REFERENCE = "ssoreference";
    public static final String KEY_FORM_FIELD_NAME_SELECTED_GROUPS_NEW_USER = "selectedgroups";
    public static final String KEY_FORM_FIELD_NAME_EXTERNAL_ITEM = "externalItem";
    public static final String KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT = "registerFederationExport";

    /* - Action types - */
 /* Item actions */
    public static final String KEY_ACTION_TYPE_APPROVE = "approve";
    public static final String KEY_ACTION_TYPE_APPROVEWITHCHANGES = "approvewithchanges";
    public static final String KEY_ACTION_TYPE_REJECT = "reject";
    public static final String KEY_ACTION_TYPE_DISCARD_ACTION = "discardAction";
    /* User/group actions */
    public static final String KEY_ACTION_TYPE_ENABLEUSER = "enableuser";
    public static final String KEY_ACTION_TYPE_DISABLEUSER = "disableuser";
    public static final String KEY_ACTION_TYPE_REMOVEUSERGROUP = "removeusergroupr";
    public static final String KEY_ACTION_TYPE_CHANGEPASSWORD = "changePassord";

    /* - Itemclass type keys - */
    public static final String KEY_ITEMCLASS_TYPE_ITEM = "item";
    public static final String KEY_ITEMCLASS_TYPE_REGISTER = "register";
    public static final String KEY_ITEMCLASS_TYPE_REGISTRY = "registry";

    /* - Relation predicate keys - */
    public static final String KEY_PREDICATE_REGISTRY = "1";
    public static final String KEY_PREDICATE_REGISTER = "2";
    public static final String KEY_PREDICATE_COLLECTION = "3";
    public static final String KEY_PREDICATE_PARENT = "4";
    public static final String KEY_PREDICATE_SUCCESSOR = "5";
    public static final String KEY_PREDICATE_PREDECESSOR = "6";
    public static final String KEY_PREDICATE_REFERENCE = "7";

    /**
     * Field type ID
     */
    public static final String KEY_FIELDTYPE_STRING_UUID = "1";
    public static final String KEY_FIELDTYPE_DATE_UUID = "2";
    public static final String KEY_FIELDTYPE_NUMBER_UUID = "3";
    public static final String KEY_FIELDTYPE_REGISTRY_UUID = "4";
    public static final String KEY_FIELDTYPE_REGISTER_UUID = "5";
    public static final String KEY_FIELDTYPE_COLLECTION_UUID = "6";
    public static final String KEY_FIELDTYPE_PARENT_UUID = "7";
    public static final String KEY_FIELDTYPE_SUCCESSOR_UUID = "8";
    public static final String KEY_FIELDTYPE_PREDECESSOR_UUID = "9";
    public static final String KEY_FIELDTYPE_RELATIONREFERENCE_UUID = "10";
    public static final String KEY_FIELDTYPE_LONGTEXT_UUID = "11";
    public static final String KEY_FIELDTYPE_GROUPREFERENCE_UUID = "12";
    public static final String KEY_FIELDTYPE_STATUS_UUID = "13";
    public static final String KEY_FIELDTYPE_DATECREATION_UUID = "14";
    public static final String KEY_FIELDTYPE_DATEEDIT_UUID = "15";

    public static final String REGISTRY_MANAGER_ROLE_UUID = "1";
    public static final String REGISTER_MANAGER_ROLE_UUID = "2";
    public static final String REGISTER_OWNER_ROLE_UUID = "3";
    public static final String CONTROL_BODY_ROLE_UUID = "4";
    public static final String SUBMITTING_ORGANIZATION_ROLE_UUID = "5";

    /**
     * -Bulk import keys-
     */
    public static final String KEY_BULK_LOCALID = "LocalID";
    public static final String KEY_BULK_LANGUAGE = "Language";
    public static final String KEY_BULK_COLLECTION = "Collection";


    /* - Statuses keys - */
    public static final String KEY_STATUS_LOCALID_VALID = "valid";
    public static final String KEY_STATUS_LOCALID_INVALID = "invalid";
    public static final String KEY_STATUS_LOCALID_SUPERSEDED = "superseded";
    public static final String KEY_STATUS_LOCALID_RETIRED = "retired";
    public static final String KEY_STATUS_LOCALID_DRAFT = "draft";
    public static final String KEY_STATUS_LOCALID_SUBMITTED = "submitted";
    public static final String KEY_STATUS_LOCALID_NEEDREVIEW = "needreview";
    public static final String KEY_STATUS_LOCALID_PROPOSED = "proposed";
    public static final String KEY_STATUS_LOCALID_NOTACCEPTED = "notAccepted";
    public static final String KEY_STATUS_LOCALID_ACCEPTEDWITHCHANGES = "acceptedWithChanges";
    public static final String KEY_STATUS_LOCALID_CHANGESCOMPLETED = "changesCompleted";
    public static final String KEY_STATUS_LOCALID_ACCEPTED = "accepted";
    public static final String KEY_STATUS_LOCALID_WITHDRAWN = "withdrawn";
    public static final String KEY_STATUS_LOCALID_PUBLISHED = "published";

    /* - Field type keys - */
    public static final String KEY_FIELD_TYPE_REGISTRY = "registry";
    public static final String KEY_FIELD_TYPE_REGISTER = "register";
    public static final String KEY_FIELD_TYPE_COLLECTION = "collection";
    public static final String KEY_FIELD_TYPE_PARENT = "parent";
    public static final String KEY_FIELD_TYPE_GROUP = "groupReference";
    public static final String KEY_FIELD_TYPE_SUCCESSOR = "successor";
    public static final String KEY_FIELD_TYPE_PREDECESSOR = "predecessor";
    public static final String KEY_FIELD_TYPE_RELATION = "relationReference";
    public static final String KEY_FIELD_TYPE_STRING = "text";
    public static final String KEY_FIELD_TYPE_LONGTEXT = "longtext";
    public static final String KEY_FIELD_TYPE_NUMBER = "number";
    public static final String KEY_FIELD_TYPE_DATE = "date";
    public static final String KEY_FIELD_TYPE_STATUS = "status";
    public static final String KEY_FIELD_TYPE_DATECREATION = "dateCreation";
    public static final String KEY_FIELD_TYPE_DATEEDIT = "dateEdit";

    /* - Mandatory field localid - */
    public static final String KEY_FIELD_MANDATORY_LABEL_LOCALID = "label";
    public static final String KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID = "contentsummary";
    public static final String KEY_FIELD_MANDATORY_STATUS_LOCALID = "status";
    public static final String KEY_FIELD_MANDATORY_REGISTRYMANAGER = "registryManager";
    public static final String KEY_FIELD_MANDATORY_REGISTERMANAGER = "registerManager";
    public static final String KEY_FIELD_MANDATORY_REGISTEROWNER = "registerOwner";
    public static final String KEY_FIELD_MANDATORY_CONTROLBODY = "controlBody";
    public static final String KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS = "submittingOrganization";

    /* - Fields localid - */
    public static final String KEY_FIELD_DEFINITION_LOCALID = "definition";

    /* - Checkbox type type keys - */
    public static final String KEY_FIELD_CHECKBOX_TYPE_HREF = "href";
    public static final String KEY_FIELD_CHECKBOX_TYPE_HIDDEN = "hiddenf";
    public static final String KEY_FIELD_CHECKBOX_TYPE_MULTIVALUED = "multivalued";
    public static final String KEY_FIELD_CHECKBOX_TYPE_TABLEVISIBLE = "tablevisible";
    public static final String KEY_FIELD_CHECKBOX_TYPE_REQUIRED = "required";

    public static final String KEY_ROLE_SUBMITTINGORGANIZATION = "submittingOrganization";
    public static final String KEY_ROLE_CONTROLBODY = "controlBody";
    public static final String KEY_ROLE_REGISTRYMANAGER = "registryManager";
    public static final String KEY_ROLE_REGISTERMANAGER = "registerManager";
    public static final String KEY_ROLE_REGISTEROWNER = "registerOwner";
    public static final String KEY_ROLE_SUBMITTER = "submitter";
    public static final String KEY_ROLE_CONTACT_POINT = "contactPoint";
    public static final String KEY_ROLE_LICENSE = "license";

    /* - JSON field keys - */
    public static final String KEY_JSON_FIELDS_UUID = "uuid";
    public static final String KEY_JSON_FIELDS_CHANGELOG = "changelog";
    public static final String KEY_JSON_FIELDS_CHANGEREQUEST = "changerequest";
    public static final String KEY_JSON_FIELDS_ISSUETRACKERLINK = "issuetrackerlink";
    public static final String KEY_JSON_FIELDS_LABEL = "label";
    public static final String KEY_JSON_FIELDS_REJECTMESSAGE = "rejectmessage";
    public static final String KEY_JSON_FIELDS_LOCALID = "localid";
    public static final String KEY_JSON_FIELDS_BASEURI = "baseuri";
    public static final String KEY_JSON_FIELDS_ITEMCLASSTYPE = "itemclasstype";
    public static final String KEY_JSON_FIELDS_NOTEDITABLE = "noteditable";

    /* - Error keys - */
    public static final String KEY_ERROR_GENERIC = "error.generic";
    public static final String KEY_ERROR_WRONGORDER = "error.wrongorder";
    public static final String KEY_ERROR_NOTDELETABLE_CONTENTCLASSASSOCIATED = "error.itemclass.notdeletable.childcontentclassassociated";
    public static final String KEY_ERROR_NOTDELETABLE_ITEMSASSOCIATED = "error.itemclass.notdeletable.itemsassociated";
    public static final String KEY_ITEMCLASS_SUCCESS_DELETABLE = "success.itemclass.deletable";
    public static final String KEY_ITEMCLASS_SUCCESS_EDITABLE = "success.itemclass.editable";
    public static final String KEY_ERROR_FIELD_EXIST_LOCALID = "error.field.exit.localid";
    public static final String KEY_ERROR_FIELD_EXIST_LABEL = "error.field.exit.label";

    public static final String KEY_PARAMETER_HTTP = "http://";
    public static final String KEY_PARAMETER_HTTPS = "https://";

    /* - Operation success - */
    public static final String KEY_OPERATION_SUCCESS = "operation.success";
    public static final String KEY_OPERATION_CACHE_ISRUNNING = "operation.cacheinprogress";
    public static final String KEY_OPERATION_REMOVE_CACHE_SUCCESS = "operation.remove.cache.success";

    /* Re3gistry2 rest API keys*/
    public static final String KEY_ALLOW_NULL_FIELDS = "application.returnnullfields";
    public static final String KEY_DISK_CACHE_PATH = "application.cache.disk.path";
    public static final String KEY_DISK_CACHE_MAXIMUM_SPACE = "application.cache.disk.maximum.space";
    public static final String KEY_CACHE_NAME_UUID = "items-by-uuid";
    public static final String KEY_CACHE_NAME_URL = "items-by-url";


    public String getKEY_APP_NAME() {
        return KEY_APP_NAME;
    }

    public String getKEY_CHANGE_LANGUAGE_LOCKED_REFERRER() {
        return KEY_CHANGE_LANGUAGE_LOCKED_REFERRER;
    }

    public String getKEY_BOOLEAN_STRING_TRUE() {
        return KEY_BOOLEAN_STRING_TRUE;
    }

    public String getKEY_BOOLEAN_STRING_FALSE() {
        return KEY_BOOLEAN_STRING_FALSE;
    }

    public int getKEY_LATEST_VERSION() {
        return KEY_LATEST_VERSION;
    }

    public String getKEY_FOLDER_NAME_WEBINF() {
        return KEY_FOLDER_NAME_WEBINF;
    }

    public String getKEY_FOLDER_NAME_CLASSES() {
        return KEY_FOLDER_NAME_CLASSES;
    }

    public String getKEY_FOLDER_NAME_CONFIGURATIONS() {
        return KEY_FOLDER_NAME_CONFIGURATIONS;
    }

    public String getKEY_FILE_NAME_CONFIGURATIONS() {
        return KEY_FILE_NAME_CONFIGURATIONS;
    }

    public String getKEY_FILE_ECAS_PROPERTIES() {
        return KEY_FILE_ECAS_PROPERTIES;
    }

    public String getKEY_FILE_NAME_INSTALL() {
        return KEY_FILE_NAME_INSTALL;
    }

    public String getKEY_FILE_NAME_SYSTEMINSTALLED() {
        return KEY_FILE_NAME_SYSTEMINSTALLED;
    }

    public String getKEY_FILE_NAME_SYSTEMINSTALLING() {
        return KEY_FILE_NAME_SYSTEMINSTALLING;
    }

    public String getKEY_PROPERTY_PERSISTENCE_UNIT_NAME() {
        return KEY_PROPERTY_PERSISTENCE_UNIT_NAME;
    }

    public String getKEY_PROPERTY_AVAILABLE_LANGUAGE() {
        return KEY_PROPERTY_AVAILABLE_LANGUAGE;
    }

    public String getKEY_PROPERTY_SEPARATOR_AVAILABLE_LANGUAGE() {
        return KEY_PROPERTY_SEPARATOR_AVAILABLE_LANGUAGE;
    }

    public String getKEY_PROPERTY_AVAILABLE_LANGUAGE_LABEL() {
        return KEY_PROPERTY_AVAILABLE_LANGUAGE_LABEL;
    }

    public String getKEY_PROPERTY_LOCALIZATION_PREFIX() {
        return KEY_PROPERTY_LOCALIZATION_PREFIX;
    }

    public String getKEY_PROPERTY_LOCALIZATION_SUFFIX() {
        return KEY_PROPERTY_LOCALIZATION_SUFFIX;
    }

    public String getKEY_PROPERTY_DEFAULT_LOCALE() {
        return KEY_PROPERTY_DEFAULT_LOCALE;
    }

    public String getKEY_PROPERTY_ROLEPERMISSION_SUFFIX() {
        return KEY_PROPERTY_ROLEPERMISSION_SUFFIX;
    }

    public String getKEY_PROPERTY_LOGIN_TYPE() {
        return KEY_PROPERTY_LOGIN_TYPE;
    }

    public String getKEY_PROPERTY_LOGIN_TYPE_SHIRO() {
        return KEY_PROPERTY_LOGIN_TYPE_SHIRO;
    }

    public String getKEY_PROPERTY_LOGIN_TYPE_ECAS() {
        return KEY_PROPERTY_LOGIN_TYPE_ECAS;
    }

    public String getKEY_MAIL_HOST() {
        return KEY_MAIL_HOST;
    }

    public String getKEY_MAIL_PORT() {
        return KEY_MAIL_PORT;
    }

    public String getKEY_MAIL_AUTH() {
        return KEY_MAIL_AUTH;
    }

    public String getKEY_MAIL_STARTTLS() {
        return KEY_MAIL_STARTTLS;
    }

    public String getKEY_MAIL_TEMPLATE() {
        return KEY_MAIL_TEMPLATE;
    }

    public String getKEY_MAIL_USER() {
        return KEY_MAIL_USER;
    }

    public String getKEY_MAIL_PASSWORD() {
        return KEY_MAIL_PASSWORD;
    }

    public String getKEY_MAIL_SENDER() {
        return KEY_MAIL_SENDER;
    }

    public String getKEY_EMAIL_SUBJECT_SUCCESS() {
        return KEY_EMAIL_SUBJECT_SUCCESS;
    }

    public String getKEY_EMAIL_BODY_SUCCESS() {
        return KEY_EMAIL_BODY_SUCCESS;
    }

    public String getKEY_EMAIL_SUBJECT_ERROR() {
        return KEY_EMAIL_SUBJECT_ERROR;
    }

    public String getKEY_EMAIL_BODY_ERROR() {
        return KEY_EMAIL_BODY_ERROR;
    }

    public String getKEY_EMAIL_SUBJECT_NEW_REGISTRATION() {
        return KEY_EMAIL_SUBJECT_NEW_REGISTRATION;
    }

    public String getKEY_EMAIL_BODY_NEW_REGISTRATION() {
        return KEY_EMAIL_BODY_NEW_REGISTRATION;
    }

    public String getKEY_PROPERTY_INPUT_SANITIZER_LEVEL() {
        return KEY_PROPERTY_INPUT_SANITIZER_LEVEL;
    }

    public String getKEY_PROPERTY_INPUT_SANITIZER_LEVEL_SIMPLETEXT() {
        return KEY_PROPERTY_INPUT_SANITIZER_LEVEL_SIMPLETEXT;
    }

    public String getKEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASIC() {
        return KEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASIC;
    }

    public String getKEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASICWITHIMAGES() {
        return KEY_PROPERTY_INPUT_SANITIZER_LEVEL_BASICWITHIMAGES;
    }

    public String getKEY_PROPERTY_INPUT_SANITIZER_LEVEL_RELAXED() {
        return KEY_PROPERTY_INPUT_SANITIZER_LEVEL_RELAXED;
    }

    public String getKEY_SESSION_LANGUAGE() {
        return KEY_SESSION_LANGUAGE;
    }

    public String getKEY_SESSION_USER() {
        return KEY_SESSION_USER;
    }

    public String getMIGRATION_USER_NAME() {
        return MIGRATION_USER_NAME;
    }

    public String getMIGRATION_USER_PASSWORD() {
        return MIGRATION_USER_PASSWORD;
    }

    public String getKEY_SESSION_USERPERGROUPSMAP() {
        return KEY_SESSION_USERPERGROUPSMAP;
    }

    public String getKEY_SESSION_AVAILABLELANGUAGES() {
        return KEY_SESSION_AVAILABLELANGUAGES;
    }

    public String getKEY_USER_ACTION_MANAGEITEMPROPOSAL() {
        return KEY_USER_ACTION_MANAGEITEMPROPOSAL;
    }

    public String getKEY_USER_ACTION_MANAGEREGISTERREGISTRY() {
        return KEY_USER_ACTION_MANAGEREGISTERREGISTRY;
    }

    public String getKEY_USER_ACTION_MANAGEFIELDMAPPING() {
        return KEY_USER_ACTION_MANAGEFIELDMAPPING;
    }

    public String getKEY_USER_ACTION_MANAGEFIELD() {
        return KEY_USER_ACTION_MANAGEFIELD;
    }

    public String getKEY_USER_ACTION_MANAGEUSER() {
        return KEY_USER_ACTION_MANAGEUSER;
    }

    public String getKEY_USER_ACTION_MANAGEGROUP() {
        return KEY_USER_ACTION_MANAGEGROUP;
    }

    public String getKEY_USER_ACTION_MAPGROUPTOITEM() {
        return KEY_USER_ACTION_MAPGROUPTOITEM;
    }

    public String getKEY_USER_ACTION_MAPUSERTOGROUP() {
        return KEY_USER_ACTION_MAPUSERTOGROUP;
    }

    public String getKEY_USER_ACTION_SUBMITPROPOSAL() {
        return KEY_USER_ACTION_SUBMITPROPOSAL;
    }

    public String getKEY_USER_ACTION_APPROVEPROPOSAL() {
        return KEY_USER_ACTION_APPROVEPROPOSAL;
    }

    public String getKEY_USER_ACTION_PUBLISHPROPOSAL() {
        return KEY_USER_ACTION_PUBLISHPROPOSAL;
    }

    public String getKEY_USER_ACTION_MANAGESYSTEM() {
        return KEY_USER_ACTION_MANAGESYSTEM;
    }

    public String getKEY_REQUEST_ITEMUUID() {
        return KEY_REQUEST_ITEMUUID;
    }

    public String getKEY_REQUEST_ITEMCLASSUUID() {
        return KEY_REQUEST_ITEMCLASSUUID;
    }

    public String getKEY_REQUEST_LANGUAGEUUID() {
        return KEY_REQUEST_LANGUAGEUUID;
    }

    public String getKEY_REQUEST_FIELDUUID() {
        return KEY_REQUEST_FIELDUUID;
    }

    public String getKEY_REQUEST_FIELDMAPPINGUUID() {
        return KEY_REQUEST_FIELDMAPPINGUUID;
    }

    public String getKEY_REQUEST_NEWPOSITION() {
        return KEY_REQUEST_NEWPOSITION;
    }

    public String getKEY_REQUEST_CHECKBOXTYPE() {
        return KEY_REQUEST_CHECKBOXTYPE;
    }

    public String getKEY_REQUEST_CHECKED() {
        return KEY_REQUEST_CHECKED;
    }

    public String getKEY_REQUEST_USER_ERROR_MESSAGES() {
        return KEY_REQUEST_USER_ERROR_MESSAGES;
    }

    public String getKEY_REQUEST_USER_SUCCESS_MESSAGES() {
        return KEY_REQUEST_USER_SUCCESS_MESSAGES;
    }

    public String getKEY_REQUEST_USER_NOT_AVAILABLE() {
        return KEY_REQUEST_USER_NOT_AVAILABLE;
    }

    public String getKEY_REQUEST_USER_NOT_LOGGEDIN() {
        return KEY_REQUEST_USER_NOT_LOGGEDIN;
    }

    public String getKEY_REQUEST_USER_NOT_ENABLED() {
        return KEY_REQUEST_USER_NOT_ENABLED;
    }

    public String getKEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR() {
        return KEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR;
    }

    public String getKEY_REQUEST_USER_CREATION_STARTED() {
        return KEY_REQUEST_USER_CREATION_STARTED;
    }

    public String getKEY_REQUEST_USER_CREATION_ERROR() {
        return KEY_REQUEST_USER_CREATION_ERROR;
    }

    public String getKEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR() {
        return KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR;
    }

    public String getKEY_REQUEST_INSTALLATION_SUCCESS() {
        return KEY_REQUEST_INSTALLATION_SUCCESS;
    }

    public String getKEY_REQUEST_INSTALLATION_MIGRATION_ERROR() {
        return KEY_REQUEST_INSTALLATION_MIGRATION_ERROR;
    }

    public String getKEY_REQUEST_USER_RESETPASSWORD_SUCCESS() {
        return KEY_REQUEST_USER_RESETPASSWORD_SUCCESS;
    }

    public String getKEY_REQUEST_USER_RESETPASSWORD_ERROR() {
        return KEY_REQUEST_USER_RESETPASSWORD_ERROR;
    }

    public String getKEY_REQUEST_USER_RESETPASSWORD_USERNOTAVAILABLE_ERROR() {
        return KEY_REQUEST_USER_RESETPASSWORD_USERNOTAVAILABLE_ERROR;
    }

    public String getKEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION() {
        return KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION;
    }

    public String getKEY_REQUEST_USER_UNKNOWN_ACCOUNT_EXCEPTION() {
        return KEY_REQUEST_USER_UNKNOWN_ACCOUNT_EXCEPTION;
    }

    public String getKEY_REQUEST_USER_LOCKED_ACCOUNT_EXCEPTION() {
        return KEY_REQUEST_USER_LOCKED_ACCOUNT_EXCEPTION;
    }

    public String getKEY_REQUEST_LOGIN_CONFIGURATION_ERRORR() {
        return KEY_REQUEST_LOGIN_CONFIGURATION_ERRORR;
    }

    public String getKEY_REQUEST_LANGUAGE() {
        return KEY_REQUEST_LANGUAGE;
    }

    public String getKEY_REQUEST_REFERER() {
        return KEY_REQUEST_REFERER;
    }

    public String getKEY_REQUEST_NEWITEMINSERT() {
        return KEY_REQUEST_NEWITEMINSERT;
    }

    public String getKEY_REQUEST_NEWSTATUSLOCALID() {
        return KEY_REQUEST_NEWSTATUSLOCALID;
    }

    public String getKEY_REQUEST_NEWREGSTATUS() {
        return KEY_REQUEST_NEWREGSTATUS;
    }

    public String getKEY_REQUEST_NOTEDITABLE() {
        return KEY_REQUEST_NOTEDITABLE;
    }

    public String getKEY_REQUEST_STEP() {
        return KEY_REQUEST_STEP;
    }

    public String getKEY_REQUEST_GROUP_UUID() {
        return KEY_REQUEST_GROUP_UUID;
    }

    public String getKEY_REQUEST_ACTIONTYPE() {
        return KEY_REQUEST_ACTIONTYPE;
    }

    public String getKEY_REQUEST_USERDETAIL_UUID() {
        return KEY_REQUEST_USERDETAIL_UUID;
    }

    public String getKEY_REQUEST_USERGROUPMAPPING_UUID() {
        return KEY_REQUEST_USERGROUPMAPPING_UUID;
    }

    public String getKEY_REQUEST_MASTERLANGUAGE() {
        return KEY_REQUEST_MASTERLANGUAGE;
    }

    public String getKEY_REQUEST_CURRENTLANGUAGE() {
        return KEY_REQUEST_CURRENTLANGUAGE;
    }

    public String getKEY_REQUEST_LANGUAGECODES() {
        return KEY_REQUEST_LANGUAGECODES;
    }

    public String getKEY_REQUEST_ACTIVE_LANGUAGECODES() {
        return KEY_REQUEST_ACTIVE_LANGUAGECODES;
    }

    public String getKEY_REQUEST_OPERATIONRESULT() {
        return KEY_REQUEST_OPERATIONRESULT;
    }

    public String getKEY_REQUEST_REGITEMCLASSES() {
        return KEY_REQUEST_REGITEMCLASSES;
    }

    public String getKEY_REQUEST_REGFIELDMAPPINGS() {
        return KEY_REQUEST_REGFIELDMAPPINGS;
    }

    public String getKEY_REQUEST_PARENTREGITEMCLASS() {
        return KEY_REQUEST_PARENTREGITEMCLASS;
    }

    public String getKEY_REQUEST_REGISTRYITEM() {
        return KEY_REQUEST_REGISTRYITEM;
    }

    public String getKEY_REQUEST_REGISTRYITEMCLASS() {
        return KEY_REQUEST_REGISTRYITEMCLASS;
    }

    public String getKEY_REQUEST_REGFIELD() {
        return KEY_REQUEST_REGFIELD;
    }

    public String getKEY_REQUEST_REGITEMCLASS() {
        return KEY_REQUEST_REGITEMCLASS;
    }

    public String getKEY_REQUEST_REGITEMCLASS_NEWITEM() {
        return KEY_REQUEST_REGITEMCLASS_NEWITEM;
    }

    public String getKEY_REQUEST_REGFIELDS() {
        return KEY_REQUEST_REGFIELDS;
    }

    public String getKEY_REQUEST_REGFIELDMAPPINGSITEMCLASS() {
        return KEY_REQUEST_REGFIELDMAPPINGSITEMCLASS;
    }

    public String getKEY_REQUEST_REGFIELDTYPES() {
        return KEY_REQUEST_REGFIELDTYPES;
    }

    public String getKEY_REQUEST_LOCALIZATION() {
        return KEY_REQUEST_LOCALIZATION;
    }

    public String getKEY_REQUEST_REGISTRYLIST() {
        return KEY_REQUEST_REGISTRYLIST;
    }

    public String getKEY_REQUEST_ITEM() {
        return KEY_REQUEST_ITEM;
    }

    public String getKEY_REQUEST_SELECT_LIST_ITEMS() {
        return KEY_REQUEST_SELECT_LIST_ITEMS;
    }

    public String getKEY_REQUEST_REGITEM_REGGROUP_REG_ROLE_MAPPING_ROLES() {
        return KEY_REQUEST_REGITEM_REGGROUP_REG_ROLE_MAPPING_ROLES;
    }

    public String getKEY_REQUEST_ITEMS_ALREADY_IN_RELATION() {
        return KEY_REQUEST_ITEMS_ALREADY_IN_RELATION;
    }

    public String getKEY_REQUEST_ITEM_PROPOSED() {
        return KEY_REQUEST_ITEM_PROPOSED;
    }

    public String getKEY_REQUEST_ITEM_PROPOSEDS() {
        return KEY_REQUEST_ITEM_PROPOSEDS;
    }

    public String getKEY_REQUEST_ITEM_HISTORYS() {
        return KEY_REQUEST_ITEM_HISTORYS;
    }

    public String getKEY_REQUEST_FIELDMAPPINGS() {
        return KEY_REQUEST_FIELDMAPPINGS;
    }

    public String getKEY_REQUEST_DT_START() {
        return KEY_REQUEST_DT_START;
    }

    public String getKEY_REQUEST_DT_LENGTH() {
        return KEY_REQUEST_DT_LENGTH;
    }

    public String getKEY_REQUEST_DT_DRAW() {
        return KEY_REQUEST_DT_DRAW;
    }

    public String getKEY_REQUEST_REGLOCALIZATION() {
        return KEY_REQUEST_REGLOCALIZATION;
    }

    public String getKEY_REQUEST_LOCALIZATIONMASTERLANGUAGE() {
        return KEY_REQUEST_LOCALIZATIONMASTERLANGUAGE;
    }

    public String getKEY_REQUEST_REGITEM() {
        return KEY_REQUEST_REGITEM;
    }

    public String getKEY_REQUEST_REGITEMS() {
        return KEY_REQUEST_REGITEMS;
    }

    public String getKEY_REQUEST_REGUSERSDETAIL() {
        return KEY_REQUEST_REGUSERSDETAIL;
    }

    public String getKEY_REQUEST_REGGROUPS() {
        return KEY_REQUEST_REGGROUPS;
    }

    public String getKEY_REQUEST_CHILDITEMCLASS() {
        return KEY_REQUEST_CHILDITEMCLASS;
    }

    public String getKEY_REQUEST_REGITEMCLASSTYPES() {
        return KEY_REQUEST_REGITEMCLASSTYPES;
    }

    public String getKEY_REQUEST_REGUSER() {
        return KEY_REQUEST_REGUSER;
    }

    public String getKEY_REQUEST_REGUSERDETAIL() {
        return KEY_REQUEST_REGUSERDETAIL;
    }

    public String getKEY_REQUEST_REGGROUP() {
        return KEY_REQUEST_REGGROUP;
    }

    public String getKEY_REQUEST_SHOW_CHANGES() {
        return KEY_REQUEST_SHOW_CHANGES;
    }

    public String getKEY_REQUEST_SHOW_CHANGESBAR() {
        return KEY_REQUEST_SHOW_CHANGESBAR;
    }

    public String getKEY_REQUEST_LOCALIZATIONPROPOSED_CHANGEDLANGUAGES() {
        return KEY_REQUEST_LOCALIZATIONPROPOSED_CHANGEDLANGUAGES;
    }

    public String getKEY_REQUEST_LOCALIZATIONPROPOSEDUUID() {
        return KEY_REQUEST_LOCALIZATIONPROPOSEDUUID;
    }

    public String getKEY_REQUEST_REGLOCALIZATIONUUID() {
        return KEY_REQUEST_REGLOCALIZATIONUUID;
    }

    public String getKEY_REQUEST_REMOVE_VALUE_TYPE() {
        return KEY_REQUEST_REMOVE_VALUE_TYPE;
    }

    public String getKEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE() {
        return KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE;
    }

    public String getKEY_REQUEST_REMOVE_VALUE_TYPE_UPDATE() {
        return KEY_REQUEST_REMOVE_VALUE_TYPE_UPDATE;
    }

    public String getKEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD() {
        return KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;
    }

    public String getKEY_REQUEST_TYPE_EDIT() {
        return KEY_REQUEST_TYPE_EDIT;
    }

    public String getKEY_REQUEST_AVAILABLELANGUAGES() {
        return KEY_REQUEST_AVAILABLELANGUAGES;
    }

    public String getKEY_REQUEST_CURRENTPAGENAME() {
        return KEY_REQUEST_CURRENTPAGENAME;
    }

    public String getKEY_REQUEST_PROPERTIES() {
        return KEY_REQUEST_PROPERTIES;
    }

    public String getKEY_REQUEST_REGISTRY_STATISTICS() {
        return KEY_REQUEST_REGISTRY_STATISTICS;
    }

    public String getKEY_REQUEST_REGISTER_LIST_STATISTICS() {
        return KEY_REQUEST_REGISTER_LIST_STATISTICS;
    }

    public String getKEY_REQUEST_MIGRATION_DBADDRESS() {
        return KEY_REQUEST_MIGRATION_DBADDRESS;
    }

    public String getKEY_REQUEST_MIGRATION_DBPORT() {
        return KEY_REQUEST_MIGRATION_DBPORT;
    }

    public String getKEY_REQUEST_MIGRATION_DBNAME() {
        return KEY_REQUEST_MIGRATION_DBNAME;
    }

    public String getKEY_REQUEST_MIGRATION_DBUSERNAME() {
        return KEY_REQUEST_MIGRATION_DBUSERNAME;
    }

    public String getKEY_REQUEST_MIGRATION_DBPASSWORD() {
        return KEY_REQUEST_MIGRATION_DBPASSWORD;
    }

    public String getKEY_REQUEST_ADMIN_USERNAME() {
        return KEY_REQUEST_ADMIN_USERNAME;
    }

    public String getKEY_REQUEST_ADMIN_PASSWORD() {
        return KEY_REQUEST_ADMIN_PASSWORD;
    }

    public String getKEY_REQUEST_ADMIN_SSOREFERENCE() {
        return KEY_REQUEST_ADMIN_SSOREFERENCE;
    }

    public String getKEY_REQUEST_ADMIN_CONFIRM_PASSWORD() {
        return KEY_REQUEST_ADMIN_CONFIRM_PASSWORD;
    }

    public String getKEY_REQUEST_CLEAN_INSTALLATION_PROFILE() {
        return KEY_REQUEST_CLEAN_INSTALLATION_PROFILE;
    }

    public String getKEY_REQUEST_CLEAN_INSTALLATION_SUMMARY() {
        return KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY;
    }

    public String getKEY_REQUEST_CLEAN_INSTALLATION_PROCESS() {
        return KEY_REQUEST_CLEAN_INSTALLATION_PROCESS;
    }

    public String getKEY_REQUEST_MIGRATION_PROFILE() {
        return KEY_REQUEST_MIGRATION_PROFILE;
    }

    public String getKEY_REQUEST_MIGRATION_PROCESS() {
        return KEY_REQUEST_MIGRATION_PROCESS;
    }

    public String getKEY_REQUEST_MIGRATION_SUMMARY() {
        return KEY_REQUEST_MIGRATION_SUMMARY;
    }

    public String getKEY_REQUEST_PROFILE() {
        return KEY_REQUEST_PROFILE;
    }

    public String getKEY_REQUEST_USER_OLD_PASSWORD() {
        return KEY_REQUEST_USER_OLD_PASSWORD;
    }

    public String getKEY_REQUEST_USER_NEW_PASSWORD() {
        return KEY_REQUEST_USER_NEW_PASSWORD;
    }

    public String getKEY_REQUEST_USER_CONFIRM_NEW_PASSWORD() {
        return KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD;
    }

    public String getKEY_REQUEST_MIGRATION_ERROR() {
        return KEY_REQUEST_MIGRATION_ERROR;
    }

    public String getKEY_REQUEST_MIGRATION_COMPLETED() {
        return KEY_REQUEST_MIGRATION_COMPLETED;
    }

    public String getKEY_REQUEST_FORM_ITEMUUID() {
        return KEY_REQUEST_FORM_ITEMUUID;
    }

    public String getKEY_REQUEST_FORM_ITEMUUID_NEW() {
        return KEY_REQUEST_FORM_ITEMUUID_NEW;
    }

    public String getKEY_REQUEST_ACTION_LIST() {
        return KEY_REQUEST_ACTION_LIST;
    }

    public String getKEY_REQUEST_ACTION() {
        return KEY_REQUEST_ACTION;
    }

    public String getKEY_REQUEST_ACTION_UUID() {
        return KEY_REQUEST_ACTION_UUID;
    }

    public String getKEY_REQUEST_SUCCESSORS() {
        return KEY_REQUEST_SUCCESSORS;
    }

    public String getKEY_REQUEST_REGISTRY_REGITEMCLASSES() {
        return KEY_REQUEST_REGISTRY_REGITEMCLASSES;
    }

    public String getKEY_REQUEST_REGISTER_REGITEMCLASSES() {
        return KEY_REQUEST_REGISTER_REGITEMCLASSES;
    }

    public String getKEY_REQUEST_ITEM_REGITEMCLASSES() {
        return KEY_REQUEST_ITEM_REGITEMCLASSES;
    }

    public String getKEY_REQUEST_REGISTRY_LOCALID() {
        return KEY_REQUEST_REGISTRY_LOCALID;
    }

    public String getKEY_REQUEST_REGISTRY_ID() {
        return KEY_REQUEST_REGISTRY_ID;
    }

    public String getKEY_REQUEST_REGISTRY_BASEURI() {
        return KEY_REQUEST_REGISTRY_BASEURI;
    }

    public String getKEY_REQUEST_REGISTRY_LABEL() {
        return KEY_REQUEST_REGISTRY_LABEL;
    }

    public String getKEY_REQUEST_REGUSERREGGROUPMAPPINGS() {
        return KEY_REQUEST_REGUSERREGGROUPMAPPINGS;
    }

    public String getKEY_REQUEST_RESULT_MESSAGE() {
        return KEY_REQUEST_RESULT_MESSAGE;
    }

    public String getKEY_REQUEST_ERROR_MESSAGE() {
        return KEY_REQUEST_ERROR_MESSAGE;
    }

    public String getKEY_REQUEST_RESULT() {
        return KEY_REQUEST_RESULT;
    }

    public String getKEY_REQUEST_REGISTRY_CONTENT_SUMMARY() {
        return KEY_REQUEST_REGISTRY_CONTENT_SUMMARY;
    }

    public String getKEY_FORM_FIELD_NAME_HREF_SUFFIX() {
        return KEY_FORM_FIELD_NAME_HREF_SUFFIX;
    }

    public String getKEY_FORM_FIELD_NAME_REFERENCEKEY() {
        return KEY_FORM_FIELD_NAME_REFERENCEKEY;
    }

    public String getKEY_FORM_FIELD_NAME_INDEXKEY() {
        return KEY_FORM_FIELD_NAME_INDEXKEY;
    }

    public String getKEY_FORM_FIELD_NAME_USERNAME() {
        return KEY_FORM_FIELD_NAME_USERNAME;
    }

    public String getKEY_FORM_FIELD_NAME_PASSWORD() {
        return KEY_FORM_FIELD_NAME_PASSWORD;
    }

    public String getKEY_FORM_FIELD_NAME_REMEMBERME() {
        return KEY_FORM_FIELD_NAME_REMEMBERME;
    }

    public String getKEY_FORM_FIELD_NAME_LOCALID() {
        return KEY_FORM_FIELD_NAME_LOCALID;
    }

    public String getKEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID() {
        return KEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID;
    }

    public String getKEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID() {
        return KEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID;
    }

    public String getKEY_FORM_FIELD_NAME_FIELDUUID() {
        return KEY_FORM_FIELD_NAME_FIELDUUID;
    }

    public String getKEY_FORM_FIELD_NAME_ACTIONUUID() {
        return KEY_FORM_FIELD_NAME_ACTIONUUID;
    }

    public String getKEY_FORM_FIELD_NAME_USERUUID() {
        return KEY_FORM_FIELD_NAME_USERUUID;
    }

    public String getKEY_FORM_FIELD_NAME_GROUPUUID() {
        return KEY_FORM_FIELD_NAME_GROUPUUID;
    }

    public String getKEY_FORM_FIELD_NAME_SUBMITACTION() {
        return KEY_FORM_FIELD_NAME_SUBMITACTION;
    }

    public String getKEY_FORM_FIELD_NAME_UPDATELABELACTION() {
        return KEY_FORM_FIELD_NAME_UPDATELABELACTION;
    }

    public String getKEY_FORM_FIELD_NAME_APPROVE_TYPE() {
        return KEY_FORM_FIELD_NAME_APPROVE_TYPE;
    }

    public String getKEY_FORM_FIELD_NAME_ITEMCLASSUUID() {
        return KEY_FORM_FIELD_NAME_ITEMCLASSUUID;
    }

    public String getKEY_FORM_FIELD_NAME_LOCALIZATIONUUID() {
        return KEY_FORM_FIELD_NAME_LOCALIZATIONUUID;
    }

    public String getKEY_FORM_FIELD_NAME_BASEURI() {
        return KEY_FORM_FIELD_NAME_BASEURI;
    }

    public String getKEY_FORM_FIELD_NAME_DATAPROCEDUREORDER() {
        return KEY_FORM_FIELD_NAME_DATAPROCEDUREORDER;
    }

    public String getKEY_FORM_FIELD_NAME_LABEL() {
        return KEY_FORM_FIELD_NAME_LABEL;
    }

    public String getKEY_FORM_FIELD_NAME_FIELDTYPEUUID() {
        return KEY_FORM_FIELD_NAME_FIELDTYPEUUID;
    }

    public String getKEY_FORM_FIELD_NAME_ITEMCLASSREFERENCEUUID() {
        return KEY_FORM_FIELD_NAME_ITEMCLASSREFERENCEUUID;
    }

    public String getKEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID() {
        return KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID;
    }

    public String getKEY_FORM_FIELD_NAME_REQUEST_EDITFIELD() {
        return KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD;
    }

    public String getKEY_FORM_FIELD_NAME_VALUE() {
        return KEY_FORM_FIELD_NAME_VALUE;
    }

    public String getKEY_FORM_FIELD_NAME_LANGUAGEUUID() {
        return KEY_FORM_FIELD_NAME_LANGUAGEUUID;
    }

    public String getKEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID() {
        return KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID;
    }

    public String getKEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID() {
        return KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID;
    }

    public String getKEY_FORM_FIELD_NAME_CHANGELOG() {
        return KEY_FORM_FIELD_NAME_CHANGELOG;
    }

    public String getKEY_FORM_FIELD_NAME_ISSUEREFERENCE() {
        return KEY_FORM_FIELD_NAME_ISSUEREFERENCE;
    }

    public String getKEY_FORM_FIELD_NAME_COMMENTS() {
        return KEY_FORM_FIELD_NAME_COMMENTS;
    }

    public String getKEY_FORM_FIELD_NAME_REACTIONLABEL() {
        return KEY_FORM_FIELD_NAME_REACTIONLABEL;
    }

    public String getKEY_FORM_FIELD_NAME_REGISTRY_UUID() {
        return KEY_FORM_FIELD_NAME_REGISTRY_UUID;
    }

    public String getKEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID() {
        return KEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID;
    }

    public String getKEY_FORM_FIELD_NAME_KEEPPATH() {
        return KEY_FORM_FIELD_NAME_KEEPPATH;
    }

    public String getKEY_FORM_FIELD_NAME_REGISTRY_BASEURI() {
        return KEY_FORM_FIELD_NAME_REGISTRY_BASEURI;
    }

    public String getKEY_FORM_FIELD_NAME_REGISTRY_LOCALID() {
        return KEY_FORM_FIELD_NAME_REGISTRY_LOCALID;
    }

    public String getKEY_FORM_FIELD_NAME_REGISTER_BASEURI() {
        return KEY_FORM_FIELD_NAME_REGISTER_BASEURI;
    }

    public String getKEY_FORM_FIELD_NAME_DEFINITION() {
        return KEY_FORM_FIELD_NAME_DEFINITION;
    }

    public String getKEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID() {
        return KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID;
    }

    public String getKEY_FORM_FIELD_NAME_USER_NAME() {
        return KEY_FORM_FIELD_NAME_USER_NAME;
    }

    public String getKEY_FORM_FIELD_NAME_GROUP_NAME() {
        return KEY_FORM_FIELD_NAME_GROUP_NAME;
    }

    public String getKEY_FORM_FIELD_NAME_EMAIL() {
        return KEY_FORM_FIELD_NAME_EMAIL;
    }

    public String getKEY_FORM_FIELD_NAME_WEBSITE() {
        return KEY_FORM_FIELD_NAME_WEBSITE;
    }

    public String getKEY_FORM_FIELD_NAME_SSO_REFERENCE() {
        return KEY_FORM_FIELD_NAME_SSO_REFERENCE;
    }

    public String getKEY_FORM_FIELD_NAME_SELECTED_GROUPS_NEW_USER() {
        return KEY_FORM_FIELD_NAME_SELECTED_GROUPS_NEW_USER;
    }

    public String getKEY_FORM_FIELD_NAME_EXTERNAL_ITEM() {
        return KEY_FORM_FIELD_NAME_EXTERNAL_ITEM;
    }

    public String getKEY_ACTION_TYPE_APPROVE() {
        return KEY_ACTION_TYPE_APPROVE;
    }

    public String getKEY_ACTION_TYPE_APPROVEWITHCHANGES() {
        return KEY_ACTION_TYPE_APPROVEWITHCHANGES;
    }

    public static String getKEY_ACTION_TYPE_DISCARD_ACTION() {
        return KEY_ACTION_TYPE_DISCARD_ACTION;
    }

    public String getKEY_ACTION_TYPE_REJECT() {
        return KEY_ACTION_TYPE_REJECT;
    }

    public String getKEY_ACTION_TYPE_ENABLEUSER() {
        return KEY_ACTION_TYPE_ENABLEUSER;
    }

    public String getKEY_ACTION_TYPE_DISABLEUSER() {
        return KEY_ACTION_TYPE_DISABLEUSER;
    }

    public String getKEY_ACTION_TYPE_REMOVEUSERGROUP() {
        return KEY_ACTION_TYPE_REMOVEUSERGROUP;
    }

    public String getKEY_ACTION_TYPE_CHANGEPASSWORD() {
        return KEY_ACTION_TYPE_CHANGEPASSWORD;
    }

    public String getKEY_ITEMCLASS_TYPE_ITEM() {
        return KEY_ITEMCLASS_TYPE_ITEM;
    }

    public String getKEY_ITEMCLASS_TYPE_REGISTER() {
        return KEY_ITEMCLASS_TYPE_REGISTER;
    }

    public String getKEY_ITEMCLASS_TYPE_REGISTRY() {
        return KEY_ITEMCLASS_TYPE_REGISTRY;
    }

    public String getKEY_PREDICATE_REGISTRY() {
        return KEY_PREDICATE_REGISTRY;
    }

    public String getKEY_PREDICATE_REGISTER() {
        return KEY_PREDICATE_REGISTER;
    }

    public String getKEY_PREDICATE_COLLECTION() {
        return KEY_PREDICATE_COLLECTION;
    }

    public String getKEY_PREDICATE_PARENT() {
        return KEY_PREDICATE_PARENT;
    }

    public String getKEY_PREDICATE_SUCCESSOR() {
        return KEY_PREDICATE_SUCCESSOR;
    }

    public String getKEY_PREDICATE_PREDECESSOR() {
        return KEY_PREDICATE_PREDECESSOR;
    }

    public String getKEY_PREDICATE_REFERENCE() {
        return KEY_PREDICATE_REFERENCE;
    }

    public String getKEY_FIELDTYPE_STRING_UUID() {
        return KEY_FIELDTYPE_STRING_UUID;
    }

    public String getKEY_FIELDTYPE_DATE_UUID() {
        return KEY_FIELDTYPE_DATE_UUID;
    }

    public String getKEY_FIELDTYPE_NUMBER_UUID() {
        return KEY_FIELDTYPE_NUMBER_UUID;
    }

    public String getKEY_FIELDTYPE_REGISTRY_UUID() {
        return KEY_FIELDTYPE_REGISTRY_UUID;
    }

    public String getKEY_FIELDTYPE_REGISTER_UUID() {
        return KEY_FIELDTYPE_REGISTER_UUID;
    }

    public String getKEY_FIELDTYPE_COLLECTION_UUID() {
        return KEY_FIELDTYPE_COLLECTION_UUID;
    }

    public String getKEY_FIELDTYPE_PARENT_UUID() {
        return KEY_FIELDTYPE_PARENT_UUID;
    }

    public String getKEY_FIELDTYPE_SUCCESSOR_UUID() {
        return KEY_FIELDTYPE_SUCCESSOR_UUID;
    }

    public String getKEY_FIELDTYPE_PREDECESSOR_UUID() {
        return KEY_FIELDTYPE_PREDECESSOR_UUID;
    }

    public String getKEY_FIELDTYPE_RELATIONREFERENCE_UUID() {
        return KEY_FIELDTYPE_RELATIONREFERENCE_UUID;
    }

    public String getKEY_FIELDTYPE_LONGTEXT_UUID() {
        return KEY_FIELDTYPE_LONGTEXT_UUID;
    }

    public String getKEY_FIELDTYPE_GROUPREFERENCE_UUID() {
        return KEY_FIELDTYPE_GROUPREFERENCE_UUID;
    }

    public String getKEY_FIELDTYPE_STATUS_UUID() {
        return KEY_FIELDTYPE_STATUS_UUID;
    }

    public String getREGISTRY_MANAGER_ROLE_UUID() {
        return REGISTRY_MANAGER_ROLE_UUID;
    }

    public String getREGISTER_MANAGER_ROLE_UUID() {
        return REGISTER_MANAGER_ROLE_UUID;
    }

    public String getREGISTER_OWNER_ROLE_UUID() {
        return REGISTER_OWNER_ROLE_UUID;
    }

    public String getCONTROL_BODY_ROLE_UUID() {
        return CONTROL_BODY_ROLE_UUID;
    }

    public String getSUBMITTING_ORGANIZATION_ROLE_UUID() {
        return SUBMITTING_ORGANIZATION_ROLE_UUID;
    }

    public String getKEY_STATUS_LOCALID_VALID() {
        return KEY_STATUS_LOCALID_VALID;
    }

    public String getKEY_STATUS_LOCALID_INVALID() {
        return KEY_STATUS_LOCALID_INVALID;
    }

    public String getKEY_STATUS_LOCALID_SUPERSEDED() {
        return KEY_STATUS_LOCALID_SUPERSEDED;
    }

    public String getKEY_STATUS_LOCALID_RETIRED() {
        return KEY_STATUS_LOCALID_RETIRED;
    }

    public String getKEY_STATUS_LOCALID_DRAFT() {
        return KEY_STATUS_LOCALID_DRAFT;
    }

    public String getKEY_STATUS_LOCALID_SUBMITTED() {
        return KEY_STATUS_LOCALID_SUBMITTED;
    }

    public String getKEY_STATUS_LOCALID_NEEDREVIEW() {
        return KEY_STATUS_LOCALID_NEEDREVIEW;
    }

    public String getKEY_STATUS_LOCALID_PROPOSED() {
        return KEY_STATUS_LOCALID_PROPOSED;
    }

    public String getKEY_STATUS_LOCALID_NOTACCEPTED() {
        return KEY_STATUS_LOCALID_NOTACCEPTED;
    }

    public String getKEY_STATUS_LOCALID_ACCEPTEDWITHCHANGES() {
        return KEY_STATUS_LOCALID_ACCEPTEDWITHCHANGES;
    }

    public String getKEY_STATUS_LOCALID_CHANGESCOMPLETED() {
        return KEY_STATUS_LOCALID_CHANGESCOMPLETED;
    }

    public String getKEY_STATUS_LOCALID_ACCEPTED() {
        return KEY_STATUS_LOCALID_ACCEPTED;
    }

    public String getKEY_STATUS_LOCALID_WITHDRAWN() {
        return KEY_STATUS_LOCALID_WITHDRAWN;
    }

    public String getKEY_STATUS_LOCALID_PUBLISHED() {
        return KEY_STATUS_LOCALID_PUBLISHED;
    }

    public String getKEY_FIELD_TYPE_REGISTRY() {
        return KEY_FIELD_TYPE_REGISTRY;
    }

    public String getKEY_FIELD_TYPE_REGISTER() {
        return KEY_FIELD_TYPE_REGISTER;
    }

    public String getKEY_FIELD_TYPE_COLLECTION() {
        return KEY_FIELD_TYPE_COLLECTION;
    }

    public String getKEY_FIELD_TYPE_PARENT() {
        return KEY_FIELD_TYPE_PARENT;
    }

    public String getKEY_FIELD_TYPE_GROUP() {
        return KEY_FIELD_TYPE_GROUP;
    }

    public String getKEY_FIELD_TYPE_SUCCESSOR() {
        return KEY_FIELD_TYPE_SUCCESSOR;
    }

    public String getKEY_FIELD_TYPE_PREDECESSOR() {
        return KEY_FIELD_TYPE_PREDECESSOR;
    }

    public String getKEY_FIELD_TYPE_RELATION() {
        return KEY_FIELD_TYPE_RELATION;
    }

    public String getKEY_FIELD_TYPE_STRING() {
        return KEY_FIELD_TYPE_STRING;
    }

    public String getKEY_FIELD_TYPE_LONGTEXT() {
        return KEY_FIELD_TYPE_LONGTEXT;
    }

    public String getKEY_FIELD_TYPE_NUMBER() {
        return KEY_FIELD_TYPE_NUMBER;
    }

    public String getKEY_FIELD_TYPE_DATE() {
        return KEY_FIELD_TYPE_DATE;
    }

    public String getKEY_FIELD_TYPE_STATUS() {
        return KEY_FIELD_TYPE_STATUS;
    }

    public String getKEY_FIELD_MANDATORY_LABEL_LOCALID() {
        return KEY_FIELD_MANDATORY_LABEL_LOCALID;
    }

    public String getKEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID() {
        return KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID;
    }

    public String getKEY_FIELD_MANDATORY_STATUS_LOCALID() {
        return KEY_FIELD_MANDATORY_STATUS_LOCALID;
    }

    public String getKEY_FIELD_MANDATORY_REGISTRYMANAGER() {
        return KEY_FIELD_MANDATORY_REGISTRYMANAGER;
    }

    public String getKEY_FIELD_MANDATORY_REGISTERMANAGER() {
        return KEY_FIELD_MANDATORY_REGISTERMANAGER;
    }

    public String getKEY_FIELD_MANDATORY_REGISTEROWNER() {
        return KEY_FIELD_MANDATORY_REGISTEROWNER;
    }

    public String getKEY_FIELD_MANDATORY_CONTROLBODY() {
        return KEY_FIELD_MANDATORY_CONTROLBODY;
    }

    public String getKEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS() {
        return KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS;
    }

    public String getKEY_FIELD_DEFINITION_LOCALID() {
        return KEY_FIELD_DEFINITION_LOCALID;
    }

    public String getKEY_FIELD_CHECKBOX_TYPE_HREF() {
        return KEY_FIELD_CHECKBOX_TYPE_HREF;
    }

    public String getKEY_FIELD_CHECKBOX_TYPE_HIDDEN() {
        return KEY_FIELD_CHECKBOX_TYPE_HIDDEN;
    }

    public String getKEY_FIELD_CHECKBOX_TYPE_MULTIVALUED() {
        return KEY_FIELD_CHECKBOX_TYPE_MULTIVALUED;
    }

    public String getKEY_FIELD_CHECKBOX_TYPE_TABLEVISIBLE() {
        return KEY_FIELD_CHECKBOX_TYPE_TABLEVISIBLE;
    }

    public String getKEY_FIELD_CHECKBOX_TYPE_REQUIRED() {
        return KEY_FIELD_CHECKBOX_TYPE_REQUIRED;
    }

    public String getKEY_ROLE_SUBMITTINGORGANIZATION() {
        return KEY_ROLE_SUBMITTINGORGANIZATION;
    }

    public String getKEY_ROLE_CONTROLBODY() {
        return KEY_ROLE_CONTROLBODY;
    }

    public String getKEY_ROLE_REGISTRYMANAGER() {
        return KEY_ROLE_REGISTRYMANAGER;
    }

    public String getKEY_ROLE_REGISTERMANAGER() {
        return KEY_ROLE_REGISTERMANAGER;
    }

    public String getKEY_ROLE_REGISTEROWNER() {
        return KEY_ROLE_REGISTEROWNER;
    }

    public String getKEY_ROLE_CONTACT_POINT() {
        return KEY_ROLE_CONTACT_POINT;
    }

    public String getKEY_ROLE_LICENSE() {
        return KEY_ROLE_LICENSE;
    }

    public String getKEY_JSON_FIELDS_UUID() {
        return KEY_JSON_FIELDS_UUID;
    }

    public String getKEY_JSON_FIELDS_CHANGELOG() {
        return KEY_JSON_FIELDS_CHANGELOG;
    }

    public String getKEY_JSON_FIELDS_CHANGEREQUEST() {
        return KEY_JSON_FIELDS_CHANGEREQUEST;
    }

    public String getKEY_JSON_FIELDS_ISSUETRACKERLINK() {
        return KEY_JSON_FIELDS_ISSUETRACKERLINK;
    }

    public String getKEY_JSON_FIELDS_LABEL() {
        return KEY_JSON_FIELDS_LABEL;
    }

    public String getKEY_JSON_FIELDS_REJECTMESSAGE() {
        return KEY_JSON_FIELDS_REJECTMESSAGE;
    }

    public String getKEY_JSON_FIELDS_LOCALID() {
        return KEY_JSON_FIELDS_LOCALID;
    }

    public String getKEY_JSON_FIELDS_BASEURI() {
        return KEY_JSON_FIELDS_BASEURI;
    }

    public String getKEY_JSON_FIELDS_ITEMCLASSTYPE() {
        return KEY_JSON_FIELDS_ITEMCLASSTYPE;
    }

    public String getKEY_JSON_FIELDS_NOTEDITABLE() {
        return KEY_JSON_FIELDS_NOTEDITABLE;
    }

    public String getKEY_ERROR_GENERIC() {
        return KEY_ERROR_GENERIC;
    }

    public String getKEY_ERROR_WRONGORDER() {
        return KEY_ERROR_WRONGORDER;
    }

    public static String getKEY_ERROR_NOTDELETABLE_CONTENTCLASSASSOCIATED() {
        return KEY_ERROR_NOTDELETABLE_CONTENTCLASSASSOCIATED;
    }

    public static String getKEY_ERROR_NOTDELETABLE_ITEMSASSOCIATED() {
        return KEY_ERROR_NOTDELETABLE_ITEMSASSOCIATED;
    }

    public static String getKEY_MAIL_APPLICATION_ROOTURL() {
        return KEY_MAIL_APPLICATION_ROOTURL;
    }

    public static String getKEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS() {
        return KEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS;
    }

    public static String getKEY_EMAIL_SUBJECT_BULKIMPORT_ERROR() {
        return KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR;
    }

    public static String getKEY_EMAIL_BODY_BULKIMPORT_SUCCESS() {
        return KEY_EMAIL_BODY_BULKIMPORT_SUCCESS;
    }

    public static String getKEY_EMAIL_BODY_BULKIMPORT_ERROR() {
        return KEY_EMAIL_BODY_BULKIMPORT_ERROR;
    }

    public static String getKEY_REQUEST_DOWNLOAD_BULK_TEMPLATE() {
        return KEY_REQUEST_DOWNLOAD_BULK_TEMPLATE;
    }

    public static String getKEY_REQUEST_LOAD_BULK_TEMPLATE() {
        return KEY_REQUEST_LOAD_BULK_TEMPLATE;
    }

    public static String getKEY_REQUEST_LOAD_FILE_BULK() {
        return KEY_REQUEST_LOAD_FILE_BULK;
    }

    public static String getKEY_REQUEST_BULK_ERROR() {
        return KEY_REQUEST_BULK_ERROR;
    }

    public static String getKEY_REQUEST_BULK_SUCCESS() {
        return KEY_REQUEST_BULK_SUCCESS;
    }

    public static String getKEY_FORM_FIELD_NAME_REQUEST_EDITFIELD_FROM_CONTENTCLASS() {
        return KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD_FROM_CONTENTCLASS;
    }

    public static String getKEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT() {
        return KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT;
    }

    public static String getKEY_BULK_LOCALID() {
        return KEY_BULK_LOCALID;
    }

    public static String getKEY_BULK_LANGUAGE() {
        return KEY_BULK_LANGUAGE;
    }

    public static String getKEY_BULK_COLLECTION() {
        return KEY_BULK_COLLECTION;
    }

    public static String getKEY_ITEMCLASS_SUCCESS_DELETABLE() {
        return KEY_ITEMCLASS_SUCCESS_DELETABLE;
    }

    public static String getKEY_ITEMCLASS_SUCCESS_EDITABLE() {
        return KEY_ITEMCLASS_SUCCESS_EDITABLE;
    }

    public static String getKEY_PARAMETER_HTTP() {
        return KEY_PARAMETER_HTTP;
    }

    public static String getKEY_PARAMETER_HTTPS() {
        return KEY_PARAMETER_HTTPS;
    }

    public static String getKEY_SUCCESS_DELETABLE() {
        return KEY_ITEMCLASS_SUCCESS_DELETABLE;
    }

    public String getKEY_OPERATION_SUCCESS() {
        return KEY_OPERATION_SUCCESS;
    }

}
