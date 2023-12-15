/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.utility;

public class Constants {

    /* ###################################### */
 /* ###### Configurations constants ###### */
    /**
     * The name of this module
     */
    public static final String MODULE_NAME = "RoR";
    /**
     * MD5 message digest key
     */
    public static final String MD5_MESSAGE_DIGEST_KEY = "MD5";
    /**
     * InputStream default CHARSET
     */
    public static final String INPUT_STREAM_DEFAULT_CHARSET = "UTF-8";
    /**
     * Persistence unit name property key
     */
    public static final String KEY_PERSISTENCE_UNIT_NAME = "application.persistenceunitname";
    /**
     * Configuration folder name
     */
    public static final String CONFIGURATIONS_FOLDER_NAME = "configurations";
    /**
     * Configuration folder name
     */
    public static final String CONFIGURATIONS_FILE_NAME = "configuration.properties";
    /**
     * Web inf path name
     */
    public static final String WEBINF_FOLDER_NAME = "WEB-INF";
    /**
     * Classes path name
     */
    public static final String CLASSES_FOLDER_NAME = "classes";
    /**
     * Log configuration file name
     */
    public static final String LOGCONFIG_FILE_NAME = "logcfg.xml";
    /**
     * Java temp dir
     */
    public static final String JAVA_TMP_DIR = "java.io.tmpdir";

    /**
     * Language session key
     */
    public static final String LANGUAGE_SESSION_KEY = "lang";
    /**
     * Language request key
     */
    public static final String LANGUAGE_REQUEST_KEY = "lang";
    /**
     * Available Language key
     */
    public static final String AVAILABLE_LANGUAGE_PROPERTY_KEY = "application.language.available";
    /**
     * Available Language separator key
     */
    public static final String AVAILABLE_LANGUAGE_SEPARATOR = "-";
    /**
     * Available Language separator key
     */
    public static final String AVAILABLE_LANGUAGE_LABEL_PROPERTY_KEY = "application.language.label.";
    /**
     * Localization prefix
     */
    public static final String LOCALIZATION_PREFIX = "localizations";
    /**
     * Localization suffix
     */
    public static final String LOCALIZATION_SUFFIX = "LocalizationBundle";

    /**
     * Request referer key
     */
    public static final String KEY_REQUEST_REFERER = "referer";
    /**
     * Locked Request referer key
     */
    public static final String CNG_LANGUARE_LOCKED_REFERRER = "webgate.ec.europa.eu";

    /**
     * Private page name
     */
    public static final String PRIVATE_PAGE_NAME = "private.jsp";

    /**
     * Parameters key
     */
    public static final String PARAMETER_REGISTRY_ACTION_TYPE = "att";
    /**
     * Parameters key
     */
    public static final String PARAMETER_REGISTRY_ACTION_STARTER_TYPE = "atst";
    /**
     * Parameters key
     */
    public static final String PARAMETER_REGISTRY_DESCRIPTORURL = "desurl";
    /**
     * Parameters key
     */
    public static final String PARAMETER_REGISTRY_ID = "id";
    /**
     * Parameters key
     */
    public static final String PARAMETER_PROCEDURE_ID = "id";

    /**
     * Property key
     */
    public static final String KEY_THREAD_MAX_NUMBER = "thread.max.number";
   
    /**
     * Property key
     */
    public static final String KEY_XSL_VALIDATOR_PATH_REGISTRY = "xsl.validator.registry.path";
    /**
     * Property key
     */
    public static final String KEY_XSL_VALIDATOR_PATH_REGISTER = "xsl.validator.register.path";

    /**
     * Action type add key
     */
    public static final String PARAMETER_REGISTRY_ACTION_TYPE_ADD = "a";
    /**
     * Action type del key
     */
    public static final String PARAMETER_REGISTRY_ACTION_TYPE_DELETE = "d";

    /**
     * Action starter type manual key
     */
    public static final String PARAMETER_REGISTRY_ACTION_STARTER_TYPE_MANUAL = "m";

    /**
     * Action starter type automatic key
     */
    public static final String PARAMETER_REGISTRY_ACTION_STARTER_TYPE_AUTOMATIC = "a";

    /**
     * Descriptor type Registry
     */
    public static final String DESCRIPTOR_TYPE_REGISTRY = "registry";
    /**
     * Descriptor type Register
     */
    public static final String DESCRIPTOR_TYPE_REGISTER = "register";
    /**
     * Descriptor type Item
     */
    public static final String TYPE_ITEM = "item";

    /* ### Procedure statuses ### */
    /**
     * Procedure status waiting
     */
    public static final String PROCEDURE_STATUS_FIRSTINSERT = "firstinsert";
    /**
     * Procedure status waiting
     */
    public static final String PROCEDURE_STATUS_WAITING = "waiting";
    /**
     * Procedure status running
     */
    public static final String PROCEDURE_STATUS_RUNNING = "running";
    /**
     * Procedure status error
     */
    public static final String PROCEDURE_STATUS_ERROR = "error";
    /**
     * Procedure status Success
     */
    public static final String PROCEDURE_STATUS_SUCCESS = "success";

    /* ### JSON action statuses ###*/
    /**
     * JSON action status ok
     */
    public static final String JSON_ACTION_STATUS_OK = "ok";
    /**
     * JSON action status error
     */
    public static final String JSON_ACTION_STATUS_ERROR = "error";

    /**
     * Relation predicate key
     */
    public static final String RELATION_PREDICATE_KEY = "relies on";
    /**
     * Relation status active
     */
    public static final String RELATION_STATUS_ACTIVE = "http://inspire.ec.europa.eu/registry/status/valid";

    /* ########################################### */
 /* ################## Mail ################### */
    /**
     * Key properties mail smtp host
     */
    public static final String KEY_EXPORT_MAIL_HOST = "mail.smtp.host";

    /**
     * Key properties mail smtp port
     */
    public static final String KEY_EXPORT_MAIL_PORT = "mail.smtp.port";

    /**
     * Key properties mail smtp auth
     */
    public static final String KEY_EXPORT_MAIL_AUTH = "mail.smtp.auth";

    /**
     * Key properties mail smtp starttls
     */
    public static final String KEY_EXPORT_MAIL_STARTTLS = "mail.smtp.starttls.enable";

    /**
     * Key properties mail user
     */
    public static final String KEY_EXPORT_MAIL_USER = "mail.user";

    /**
     * Key properties mail password
     */
    public static final String KEY_EXPORT_MAIL_PASSWORD = "mail.password";

    /**
     * Key properties mail sender
     */
    public static final String KEY_EXPORT_MAIL_SENDER = "mail.sender";

    /**
     * Frequencies key (from
     * http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html)
     */
    public static final String FREQUENCIES_ANNUAL = "ANNUAL";
    public static final String FREQUENCIES_ANNUAL_2 = "ANNUAL_2";
    public static final String FREQUENCIES_ANNUAL_3 = "ANNUAL_3";
    public static final String FREQUENCIES_BIENNIAL = "BIENNIAL";
    public static final String FREQUENCIES_BIMONTHLY = "BIMONTHLY";
    public static final String FREQUENCIES_BIWEEKLY = "BIWEEKLY";
    public static final String FREQUENCIES_CONT = "CONT";
    public static final String FREQUENCIES_DAILY = "DAILY";
    public static final String FREQUENCIES_DAILY_2 = "DAILY_2";
    public static final String FREQUENCIES_IRREG = "IRREG";
    public static final String FREQUENCIES_MONTHLY = "MONTHLY";
    public static final String FREQUENCIES_MONTHLY_2 = "MONTHLY_2";
    public static final String FREQUENCIES_MONTHLY_3 = "MONTHLY_3";
    public static final String FREQUENCIES_NEVER = "NEVER";
    public static final String FREQUENCIES_OTHER = "OTHER";
    public static final String FREQUENCIES_QUARTERLY = "QUARTERLY";
    public static final String FREQUENCIES_TRIENNIAL = "TRIENNIAL";
    public static final String FREQUENCIES_UNKNOWN = "UNKNOWN";
    public static final String FREQUENCIES_UPDATE_CONT = "UPDATE_CONT";
    public static final String FREQUENCIES_WEEKLY = "WEEKLY";
    public static final String FREQUENCIES_WEEKLY_2 = "WEEKLY_2";
    public static final String FREQUENCIES_WEEKLY_3 = "WEEKLY_3";
    
    /**
     * Frequencies key and int values (from
     * http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html)
     */
    public static final Double FREQUENCIES_TIME_ANNUAL = 365.0;
    public static final Double FREQUENCIES_TIME_ANNUAL_2 = 182.0;
    public static final Double FREQUENCIES_TIME_ANNUAL_3 = 121.0;
    public static final Double FREQUENCIES_TIME_BIENNIAL = 730.0;
    public static final Double FREQUENCIES_TIME_BIMONTHLY = 60.0;
    public static final Double FREQUENCIES_TIME_BIWEEKLY = 14.0;
    public static final Double FREQUENCIES_TIME_CONT = 0.0;
    public static final Double FREQUENCIES_TIME_DAILY = 1.0;
    public static final Double FREQUENCIES_TIME_DAILY_2 = 0.5;
    public static final Double FREQUENCIES_TIME_IRREG = 0.0;
    public static final Double FREQUENCIES_TIME_MONTHLY = 30.0;
    public static final Double FREQUENCIES_TIME_MONTHLY_2 = 15.0;
    public static final Double FREQUENCIES_TIME_MONTHLY_3 = 10.0;
    public static final Double FREQUENCIES_TIME_NEVER = 0.0;
    public static final Double FREQUENCIES_TIME_OTHER = 0.0;
    public static final Double FREQUENCIES_TIME_QUARTERLY = 90.0;
    public static final Double FREQUENCIES_TIME_TRIENNIAL = 1095.0;
    public static final Double FREQUENCIES_TIME_UNKNOWN = 0.0;
    public static final Double FREQUENCIES_TIME_UPDATE_CONT = 0.0;
    public static final Double FREQUENCIES_TIME_WEEKLY = 7.0;
    public static final Double FREQUENCIES_TIME_WEEKLY_2 = 3.0;
    public static final Double FREQUENCIES_TIME_WEEKLY_3 = 2.0;
    
    
    //User levels
    public static final int USER_LEVEL_ADMIN = 0;
    public static final int USER_LEVEL_STANDARD = 1;
}
