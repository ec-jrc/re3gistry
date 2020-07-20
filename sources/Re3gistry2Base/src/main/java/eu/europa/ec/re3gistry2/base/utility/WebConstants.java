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

public class WebConstants {

    // Jsp folder
    public static final String PAGE_JSP_FOLDER = "/jsp";

    // Jsp file extension
    public static final String PAGE_JSP_EXTENSION = ".jsp";
    // Js file extension
    public static final String PAGE_JS_EXTENSION = ".js";

    // Pages urinames
    public static final String PAGE_URINAME_INSTALL = "/install";
    public static final String PAGE_PATH_INSTALL = "/install";
    public static final String PAGE_URINAME_STEP = "/step-";

    public static final String PAGE_URINAME_INSTALL_RUNNING = "/installRunning";
    public static final String PAGE_PATH_INSTALL_RUNNING = "/install";

    public static final String PAGE_REQUESTPATH_COMMONJS = "/res/js";
    public static final String PAGE_URINAME_COMMONJS = "/common";
    public static final String PAGE_PATH_COMMONJS = "/dynamicJs";

    public static final String PAGE_URINAME_INDEX = "/index";
    public static final String PAGE_PATH_INDEX = "";

    public static final String PAGE_URINAME_LOGIN = "/login";
    public static final String PAGE_PATH_LOGIN = "";

    public static final String PAGE_URINAME_RESETPASSWORD = "/resetPassword";
    public static final String PAGE_PATH_RESETPASSWORD = "";

    public static final String PAGE_URINAME_LOGOUT = "/logout";
    public static final String PAGE_PATH_LOGOUT = "";

    public static final String PAGE_URINAME_BROWSE = "/browse";
    public static final String PAGE_PATH_BROWSE = "";

    public static final String PAGE_URINAME_BROWSE_NEWITEMPROPOSED = "/browseNew";
    public static final String PAGE_PATH_BROWSE_NEWITEMPROPOSED = "";

    public static final String PAGE_URINAME_BROWSE_HISTORY = "/browseHistory";
    public static final String PAGE_PATH_BROWSE_HISTORY = "";

    public static final String PAGE_URINAME_BROWSELOADER = "/browseLoader";
    public static final String PAGE_PATH_BROWSELOADER = "/ajaxServices";
    
    public static final String PAGE_URINAME_BULK_TEMPLATE = "/bulkImport";
    public static final String PAGE_PATH_BULK_TEMPLATE = "";

    public static final String PAGE_URINAME_BROWSELOADERNEWPROPOSAL = "/browseLoaderNewProposal";
    public static final String PAGE_PATH_BROWSELOADERNEWPROPOSAL = "/ajaxServices";

    public static final String PAGE_URINAME_ADDFIELDRELATION = "/addFieldRelation";
    public static final String PAGE_PATH_ADDFIELDRELATION = "/ajaxServices";

    public static final String PAGE_URINAME_ADDFIELDPARENT = "/addFieldParent";
    public static final String PAGE_PATH_ADDFIELDPARENT = "/ajaxServices";

    public static final String PAGE_URINAME_ADDGROUPMAPPING = "/addFieldGroupMapping";
    public static final String PAGE_PATH_ADDGROUPMAPPING = "/ajaxServices";

    public static final String PAGE_URINAME_ADDFIELDVALUE = "/addFieldValue";
    public static final String PAGE_PATH_ADDFIELDVALUE = "/ajaxServices";

    public static final String PAGE_URINAME_ITEMCLASS = "/itemclass";
    public static final String PAGE_PATH_ITEMCLASS = "";

    public static final String PAGE_URINAME_ADDITEM = "/addItem";
    public static final String PAGE_PATH_ADDITEM = "";

    public static final String PAGE_URINAME_ADDITEMCLASS = "/addItemclass";
    public static final String PAGE_PATH_ADDITEMCLASS = "";

    public static final String PAGE_URINAME_FIELD = "/field";
    public static final String PAGE_PATH_FIELD = "";

    public static final String PAGE_URINAME_MAPFIELD = "/mapField";
    public static final String PAGE_PATH_MAPFIELD = "";

    public static final String PAGE_URINAME_FIELDLOADER = "/fieldLoader";
    public static final String PAGE_PATH_FIELDLOADER = "/ajaxServices";

    public static final String PAGE_URINAME_ACTIONDETAIL = "/actionDetail";
    public static final String PAGE_PATH_ACTIONDETAIL = "/ajaxServices";

    public static final String PAGE_URINAME_ITEMLISTLOADER = "/itemListLoader";
    public static final String PAGE_URINAME_ITEMPROPOSEDLISTLOADER = "/itemProposedListLoader";
    public static final String PAGE_URINAME_ITEMCHILDRENLISTLOADER = "/itemChildrenListLoader";
    public static final String PAGE_URINAME_USERACTIVITY = "/itemListUserActivityr";

    public static final String PAGE_URINAME_REMOVEFIELDVALUE = "/removeFieldValue";
    public static final String PAGE_PATH_REMOVEFIELDVALUE = "/ajaxServices";

    public static final String PAGE_URINAME_DISCARDPROPOSAL = "/discardProposal";
    public static final String PAGE_PATH_DISCARDPROPOSAL = "/ajaxServices";

    public static final String PAGE_URINAME_RESTOREORIGINALRELATION = "/restoreOriginalRelation";
    public static final String PAGE_PATH_RESTOREORIGINALRELATION = "/ajaxServices";

    public static final String PAGE_URINAME_ADDFIELD = "/addField";
    public static final String PAGE_PATH_ADDFIELD = "";

    /**
     * installation steps
     */
    public static final String PAGE_URINAME_STEP_1 = "/step-1";
    public static final String PAGE_URINAME_STEP_2 = "/step-2";
    public static final String PAGE_URINAME_STEP_3 = "/step-3";
    public static final String PAGE_URINAME_STEP_4 = "/step-4";
    public static final String PAGE_URINAME_STEP_MIGRATION = "/step-migration";
    public static final String PAGE_URINAME_STEP_INSTALLATION = "/step-installation";

    public static final String PAGE_URINAME_SUBMITTINGORGANISATIONS = "/submittingOrganisations";
    public static final String PAGE_PATH_SUBMITTINGORGANISATIONS = "";

    public static final String PAGE_URINAME_CONTROLBODY = "/controlBody";
    public static final String PAGE_PATH_CONTROLBODY = "";

    public static final String PAGE_URINAME_REGISTERMANAGER = "/registerManager";
    public static final String PAGE_PATH_REGISTERMANAGER = "";

    public static final String PAGE_URINAME_REGISTRYMANAGER = "/registryManager";
    public static final String PAGE_PATH_REGISTRYMANAGER = "";

    public static final String PAGE_URINAME_REGISTRYMANAGER_USERS = "/registryManagerUsers";
    public static final String PAGE_PATH_REGISTRYMANAGER_USERS = "";

    public static final String PAGE_URINAME_REGISTRYMANAGER_USERS_ADD = "/registryManagerUsersAdd";
    public static final String PAGE_PATH_REGISTRYMANAGER_USERS_ADD = "";

    public static final String PAGE_URINAME_REGISTRYMANAGER_GROUPS = "/registryManagerGroups";
    public static final String PAGE_PATH_REGISTRYMANAGER_GROUPS = "";
    
    public static final String  PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT = "/registryManagerDataExport";
    public static final String PAGE_PATH_REGISTRYMANAGER_DATAEXPORT = "";
            
    public static final String PAGE_URINAME_REGISTRYMANAGER_GROUPS_ADD = "/registryManagerGroupsAdd";
    public static final String PAGE_PATH_REGISTRYMANAGER_GROUPS_ADD = "";

    public static final String PAGE_URINAME_STATUSCHANGE = "/statusChange";
    public static final String PAGE_PATH_STATUSCHANGE = "";

    public static final String PAGE_URINAME_ADDSUCCESSOR = "/addSuccessor";
    public static final String PAGE_PATH_ADDSUCCESSOR = "/ajaxServices";

    public static final String PAGE_URINAME_ADDREGISTER = "/addRegister";
    public static final String PAGE_PATH_ADDREGISTER = "";

    public static final String PAGE_URINAME_EDITITEMCLASS = "/editItemclass";
    public static final String PAGE_PATH_EDITITEMCLASS = "/ajaxServices";

    public static final String PAGE_URINAME_USERPROFILE = "/userProfile";
    public static final String PAGE_PATH_USERPROFILE = "";

    public static final String PAGE_URINAME_CHANGELOCALE = "/changeLocale";
    public static final String PAGE_PATH_CHANGELOCALE = "";

    public static final String PAGE_URINAME_ABOUT = "/about";
    public static final String PAGE_PATH_ABOUT = "";

    public static final String PAGE_URINAME_HELP = "/help";
    public static final String PAGE_PATH_HELP = "";
    
    public static final String PAGE_URINAME_STATUS = "/status";
    public static final String PAGE_PATH_STATUS = "";

    // Data parameters
    public static final String DATA_PARAMETER_ACTIONUUID = "actionuuid";
    public static final String DATA_PARAMETER_ACTIONTYPE = "actiontype";
    public static final String DATA_PARAMETER_ITEMCLASSUUID = "itemclassuuid";
    public static final String DATA_PARAMETER_PARENTITEMCLASSPROCEDUREORDER = "parentitemclassprocedureorder";
    public static final String DATA_PARAMETER_FIELDMAPPINGUUID = "fieldmappinguuid";
    public static final String DATA_PARAMETER_ITEMUUID = "itemuuid";
    public static final String DATA_PARAMETER_LANGUAGECONEUUID = "languagecodeuuid";
    public static final String DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID = "reglocalizationproposeduuid";
    public static final String DATA_PARAMETER_REGITEMREGGROUPREGROLEMAPPINGUUID = "regitemreggroupregrolemappinguuid";
    public static final String DATA_PARAMETER_TEMPID = "tempid";
    public static final String DATA_PARAMETER_VLLISTIDPREFIX = "vl-cont-";
    public static final String DATA_PARAMETER_FIELDVALUEINDEX = "fieldvalueindex";
    public static final String DATA_PARAMETER_RELATIONUUID = "relation-uuid";
    public static final String DATA_PARAMETER_REGLOCALIZATIONUUID = "reglocalizationuuid";
    public static final String DATA_PARAMETER_MULTIVALUEDFIELD = "multivaluedfield";
    public static final String DATA_PARAMETER_FIELDUUID = "fielduuid";
    public static final String DATA_PARAMETER_LANGUAGEUUID = "languageuuid";
    public static final String DATA_PARAMETER_ITEMCLASSLOCALID = "itemclasslocalid";

    // HTML constants
    public static final String HTML_CONSTANT_DIV_ROW_OPENING = "<div class=\"row\">";
    public static final String HTML_CONSTANT_DIV_COL_SM_6_OPENING = "<div class=\"col-sm-6\">";
    public static final String HTML_CONSTANT_DIV_COL_SM_12_OPENING = "<div class=\"col-sm-12\">";
    public static final String HTML_CONSTANT_DIV_CLOSING = "</div>";

    public String getPAGE_JSP_FOLDER() {
        return PAGE_JSP_FOLDER;
    }

    public String getPAGE_JSP_EXTENSION() {
        return PAGE_JSP_EXTENSION;
    }

    public String getPAGE_JS_EXTENSION() {
        return PAGE_JS_EXTENSION;
    }

    public String getPAGE_REQUESTPATH_COMMONJS() {
        return PAGE_REQUESTPATH_COMMONJS;
    }

    public String getPAGE_URINAME_COMMONJS() {
        return PAGE_URINAME_COMMONJS;
    }

    public String getPAGE_PATH_COMMONJS() {
        return PAGE_PATH_COMMONJS;
    }

    public String getPAGE_URINAME_INDEX() {
        return PAGE_URINAME_INDEX;
    }

    public String getPAGE_PATH_INDEX() {
        return PAGE_PATH_INDEX;
    }

    public String getPAGE_URINAME_LOGIN() {
        return PAGE_URINAME_LOGIN;
    }

    public String getPAGE_PATH_LOGIN() {
        return PAGE_PATH_LOGIN;
    }

    public String getPAGE_URINAME_RESETPASSWORD() {
        return PAGE_URINAME_RESETPASSWORD;
    }

    public String getPAGE_PATH_RESETPASSWORD() {
        return PAGE_PATH_RESETPASSWORD;
    }

    public String getPAGE_URINAME_LOGOUT() {
        return PAGE_URINAME_LOGOUT;
    }

    public String getPAGE_PATH_LOGOUT() {
        return PAGE_PATH_LOGOUT;
    }

    public String getPAGE_URINAME_BROWSE() {
        return PAGE_URINAME_BROWSE;
    }

    public String getPAGE_PATH_BROWSE() {
        return PAGE_PATH_BROWSE;
    }

    public String getPAGE_URINAME_BROWSE_NEWITEMPROPOSED() {
        return PAGE_URINAME_BROWSE_NEWITEMPROPOSED;
    }

    public String getPAGE_PATH_BROWSE_NEWITEMPROPOSED() {
        return PAGE_PATH_BROWSE_NEWITEMPROPOSED;
    }

    public String getPAGE_URINAME_BROWSELOADER() {
        return PAGE_URINAME_BROWSELOADER;
    }

    public String getPAGE_PATH_BROWSELOADER() {
        return PAGE_PATH_BROWSELOADER;
    }

    public String getPAGE_URINAME_BROWSELOADERNEWPROPOSAL() {
        return PAGE_URINAME_BROWSELOADERNEWPROPOSAL;
    }

    public String getPAGE_PATH_BROWSELOADERNEWPROPOSAL() {
        return PAGE_PATH_BROWSELOADERNEWPROPOSAL;
    }

    public String getPAGE_URINAME_ADDFIELDRELATION() {
        return PAGE_URINAME_ADDFIELDRELATION;
    }

    public String getPAGE_PATH_ADDFIELDRELATION() {
        return PAGE_PATH_ADDFIELDRELATION;
    }

    public String getPAGE_URINAME_ADDFIELDPARENT() {
        return PAGE_URINAME_ADDFIELDPARENT;
    }

    public String getPAGE_PATH_ADDFIELDPARENT() {
        return PAGE_PATH_ADDFIELDPARENT;
    }

    public String getPAGE_URINAME_ADDGROUPMAPPING() {
        return PAGE_URINAME_ADDGROUPMAPPING;
    }

    public String getPAGE_PATH_ADDGROUPMAPPING() {
        return PAGE_PATH_ADDGROUPMAPPING;
    }

    public String getPAGE_URINAME_ADDFIELDVALUE() {
        return PAGE_URINAME_ADDFIELDVALUE;
    }

    public String getPAGE_PATH_ADDFIELDVALUE() {
        return PAGE_PATH_ADDFIELDVALUE;
    }

    public String getPAGE_URINAME_ITEMCLASS() {
        return PAGE_URINAME_ITEMCLASS;
    }

    public String getPAGE_PATH_ITEMCLASS() {
        return PAGE_PATH_ITEMCLASS;
    }

    public String getPAGE_URINAME_ADDITEM() {
        return PAGE_URINAME_ADDITEM;
    }

    public String getPAGE_PATH_ADDITEM() {
        return PAGE_PATH_ADDITEM;
    }

    public String getPAGE_URINAME_ADDITEMCLASS() {
        return PAGE_URINAME_ADDITEMCLASS;
    }

    public String getPAGE_PATH_ADDITEMCLASS() {
        return PAGE_PATH_ADDITEMCLASS;
    }

    public String getPAGE_URINAME_FIELD() {
        return PAGE_URINAME_FIELD;
    }

    public String getPAGE_PATH_FIELD() {
        return PAGE_PATH_FIELD;
    }

    public String getPAGE_URINAME_MAPFIELD() {
        return PAGE_URINAME_MAPFIELD;
    }

    public String getPAGE_PATH_MAPFIELD() {
        return PAGE_PATH_MAPFIELD;
    }

    public String getPAGE_URINAME_FIELDLOADER() {
        return PAGE_URINAME_FIELDLOADER;
    }

    public String getPAGE_PATH_FIELDLOADER() {
        return PAGE_PATH_FIELDLOADER;
    }

    public String getPAGE_URINAME_ACTIONDETAIL() {
        return PAGE_URINAME_ACTIONDETAIL;
    }

    public String getPAGE_PATH_ACTIONDETAIL() {
        return PAGE_PATH_ACTIONDETAIL;
    }

    public String getPAGE_URINAME_ITEMLISTLOADER() {
        return PAGE_URINAME_ITEMLISTLOADER;
    }

    public String getPAGE_URINAME_ITEMPROPOSEDLISTLOADER() {
        return PAGE_URINAME_ITEMPROPOSEDLISTLOADER;
    }

    public String getPAGE_URINAME_REMOVEFIELDVALUE() {
        return PAGE_URINAME_REMOVEFIELDVALUE;
    }

    public String getPAGE_PATH_REMOVEFIELDVALUE() {
        return PAGE_PATH_REMOVEFIELDVALUE;
    }

    public String getPAGE_URINAME_DISCARDPROPOSAL() {
        return PAGE_URINAME_DISCARDPROPOSAL;
    }

    public String getPAGE_PATH_DISCARDPROPOSAL() {
        return PAGE_PATH_DISCARDPROPOSAL;
    }

    public String getPAGE_URINAME_RESTOREORIGINALRELATION() {
        return PAGE_URINAME_RESTOREORIGINALRELATION;
    }

    public String getPAGE_PATH_RESTOREORIGINALRELATION() {
        return PAGE_PATH_RESTOREORIGINALRELATION;
    }

    public String getPAGE_URINAME_ADDFIELD() {
        return PAGE_URINAME_ADDFIELD;
    }

    public String getPAGE_PATH_ADDFIELD() {
        return PAGE_PATH_ADDFIELD;
    }

    public String getPAGE_URINAME_SUBMITTINGORGANISATIONS() {
        return PAGE_URINAME_SUBMITTINGORGANISATIONS;
    }

    public String getPAGE_PATH_SUBMITTINGORGANISATIONS() {
        return PAGE_PATH_SUBMITTINGORGANISATIONS;
    }

    public String getPAGE_URINAME_CONTROLBODY() {
        return PAGE_URINAME_CONTROLBODY;
    }

    public String getPAGE_PATH_CONTROLBODY() {
        return PAGE_PATH_CONTROLBODY;
    }

    public String getPAGE_URINAME_REGISTERMANAGER() {
        return PAGE_URINAME_REGISTERMANAGER;
    }

    public String getPAGE_PATH_REGISTERMANAGER() {
        return PAGE_PATH_REGISTERMANAGER;
    }

    public String getPAGE_URINAME_REGISTRYMANAGER() {
        return PAGE_URINAME_REGISTRYMANAGER;
    }

    public String getPAGE_PATH_REGISTRYMANAGER() {
        return PAGE_PATH_REGISTRYMANAGER;
    }

    public String getDATA_PARAMETER_ACTIONUUID() {
        return DATA_PARAMETER_ACTIONUUID;
    }

    public String getDATA_PARAMETER_ACTIONTYPE() {
        return DATA_PARAMETER_ACTIONTYPE;
    }

    public String getDATA_PARAMETER_ITEMCLASSUUID() {
        return DATA_PARAMETER_ITEMCLASSUUID;
    }

    public String getDATA_PARAMETER_FIELDMAPPINGUUID() {
        return DATA_PARAMETER_FIELDMAPPINGUUID;
    }

    public String getDATA_PARAMETER_ITEMUUID() {
        return DATA_PARAMETER_ITEMUUID;
    }

    public String getDATA_PARAMETER_LANGUAGECONEUUID() {
        return DATA_PARAMETER_LANGUAGECONEUUID;
    }

    public String getDATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID() {
        return DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID;
    }

    public String getDATA_PARAMETER_REGITEMREGGROUPREGROLEMAPPINGUUID() {
        return DATA_PARAMETER_REGITEMREGGROUPREGROLEMAPPINGUUID;
    }

    public String getDATA_PARAMETER_TEMPID() {
        return DATA_PARAMETER_TEMPID;
    }

    public String getDATA_PARAMETER_VLLISTIDPREFIX() {
        return DATA_PARAMETER_VLLISTIDPREFIX;
    }

    public String getDATA_PARAMETER_FIELDVALUEINDEX() {
        return DATA_PARAMETER_FIELDVALUEINDEX;
    }

    public String getDATA_PARAMETER_RELATIONUUID() {
        return DATA_PARAMETER_RELATIONUUID;
    }

    public String getDATA_PARAMETER_REGLOCALIZATIONUUID() {
        return DATA_PARAMETER_REGLOCALIZATIONUUID;
    }

    public String getDATA_PARAMETER_MULTIVALUEDFIELD() {
        return DATA_PARAMETER_MULTIVALUEDFIELD;
    }

    public String getDATA_PARAMETER_FIELDUUID() {
        return DATA_PARAMETER_FIELDUUID;
    }

    public String getDATA_PARAMETER_LANGUAGEUUID() {
        return DATA_PARAMETER_LANGUAGEUUID;
    }

    public String getDATA_PARAMETER_ITEMCLASSLOCALID() {
        return DATA_PARAMETER_ITEMCLASSLOCALID;
    }

}
