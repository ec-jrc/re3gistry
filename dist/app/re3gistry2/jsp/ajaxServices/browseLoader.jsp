<%-- 
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
--%>
<%@page import="java.util.Properties"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemManager"%>
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspCommon"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRole"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegUser"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspHelper"%>
<%@page import="eu.europa.ec.re3gistry2.web.controller.BrowseLoader"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalizationproposed"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemproposed"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager"%>
<%@page import="javax.persistence.NoResultException"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.ItemHelper"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelationpredicate"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelation"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldmapping"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Setup the entity manager
    EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

    // Loading the system localization (note: it is different from the content localization)
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Getting configuration
    Configuration configuration = Configuration.getInstance();

    // Getting the configuration
    Properties properties = Configuration.getInstance().getProperties();

    // Initializing managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
    RegRelationManager regRelationManager = new RegRelationManager(entityManager);
    RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
    RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);
    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
    RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
    RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
    RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
    RegStatusManager regStatusManager = new RegStatusManager(entityManager);
    RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManager);
    RegItemManager regItemManager = new RegItemManager(entityManager);

    // Getting objects from the request
    List<RegFieldmapping> regFieldmappings = (List<RegFieldmapping>) request.getAttribute(BaseConstants.KEY_REQUEST_FIELDMAPPINGS);
    RegItem regItem = (RegItem) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEM);
    RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    RegField titleRegField = regFieldManager.getTitleRegField();

    // Loading the needed RegRelation predicates (to chreate the URI)
    RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
    RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
    RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
    RegRelationpredicate regRelationpredicatePredecessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR);
    RegRelationpredicate regRelationpredicateSuccessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR);
    RegRelationpredicate regRelationpredicateParent = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);

    // Other initializations
    boolean showChangesBar = (request.getAttribute(BaseConstants.KEY_REQUEST_SHOW_CHANGESBAR) != null) ? (Boolean) request.getAttribute(BaseConstants.KEY_REQUEST_SHOW_CHANGESBAR) : false;

    // Setting the registry
    RegItem regItemRegistry = null;
    if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
        List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
        try {
            regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
        } catch (Exception e) {
            regItemRegistry = null;
        }
    }

    // Setting the register
    RegItem regItemRegister = null;
    if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
        List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
        try {
            regItemRegister = regRelationRegisters.get(0).getRegItemObject();
        } catch (Exception e) {
            regItemRegister = null;
        }
    }

    // Getting the current user from session
    RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);

    // Getting the user permission mapping from the session
    HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

    // Getting the uuid of the register to check the group. If the Register is 
    // empty, using the registry
    RegItem regItemCheck = null;
    if (regItemRegister == null && regItemRegistry != null) {
        regItemCheck = regItem;
    } else if (regItemRegister != null) {
        regItemCheck = regItemRegister;
    } else if (regItemRegister == null && regItemRegistry == null) {
        regItemCheck = regItem;
    }

    // Getting the item, group, role mapping for this item
    List<RegItemRegGroupRegRoleMapping> itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);

    // If the item is not a registry, adding the registry to the itemMappings in
    // order to allow the registry managers to manage the contained registers and items
    if (regItem != null && (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM) || regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER))) {
        List<RegItemRegGroupRegRoleMapping> itemMappingsRegistry = regItemRegGroupRegRoleMappingManager.getAll(regItemRegistry);
        itemMappings.addAll(itemMappingsRegistry);
    }

    // Creating the URI of the current RegItem
    String itemURI = ItemHelper.getURI(regItem, regItemRegistry, regItemRegister, regRelationpredicateCollection, regRelationManager);

    HashMap<String, String> registryHash = ItemHelper.getRegistryLocalizationByRegItem(regItem, entityManager, currentLanguage);
    String registryLabel = "";
    String registryURI = "";
    for (HashMap.Entry<String, String> en : registryHash.entrySet()) {
        registryLabel = en.getKey();
        registryURI = en.getValue();
    }

    HashMap<String, String> registerHash = ItemHelper.getRegisterLocalizationByRegItem(regItem, entityManager, currentLanguage);
    String registerLabel = "";
    String registerURI = "";
    for (HashMap.Entry<String, String> en : registerHash.entrySet()) {
        registerLabel = en.getKey();
        registerURI = en.getValue();
    }

    boolean statusDisallowed = regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED);

    String collecitonHTML = ItemHelper.getBreadcrumbCollectionHTMLForRegitem("", regItem, entityManager, currentLanguage);
    request.setCharacterEncoding("UTF-8");
%>

<link rel="stylesheet" href="res/css/css-loader.css">
<ol class="breadcrumb m-0 p-0" vocab="http://schema.org/" typeof="BreadcrumbList" style="font-size: 1.0em; font-weight: 700">

    <%        if (!registryURI.isEmpty() && !registryLabel.isEmpty()) {
    %>
    <li property="itemListElement" typeof="ListItem">
        <a property="item" typeof="WebPage" href="<%=registryURI%>" class="text-primary">
            <span property="name"><%=registryLabel%></span>
        </a>
    </li>
    <%
        }%>

    <%if (!registerURI.isEmpty() && !registerLabel.isEmpty()) {
    %>
    <li property="itemListElement" typeof="ListItem">
        <a property="item" typeof="WebPage" href="<%=registerURI%>"  class="text-primary">
            <span property="name"><%=registerLabel%></span>
        </a>
    </li>
    <%
        }%>

    <%if (!collecitonHTML.isEmpty()) {
    %>
    <%=collecitonHTML%>
    <%
        }%>
    <!-- To be completed -->

</ol>
<%
    if ((!registryURI.isEmpty() && !registryLabel.isEmpty()) || (!registerURI.isEmpty() && !registerLabel.isEmpty()) || (!registryURI.isEmpty() && !registryLabel.isEmpty())) {
%>
<hr/>
<%}
%>

<%-- /---- Editing form start ----/ --%>
<form id="editing-form" method="post" accept-charset="utf-8" role="form">

    <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_FORM_ITEMUUID%>" value="<%=regItem.getUuid()%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID%>" value="<%=currentLanguage.getUuid()%>" /> 


    <%if (statusDisallowed) { %>
    <div class="row"> 
        <div class="col-12">
        <div class="alert alert-warning" role="alert">
            <%=MessageFormat.format(localization.getString("warning.item.cannot.beedited"), regItem.getRegStatus().getLocalid())%>
        </div>
        </div>
    </div>
    <% }%>

    <%-- The URI field is always available and non editable --%>
    <div class="row form-group editing-labels">
        <label for="URI" class="col-sm-4">${localization.getString("label.uri")}</label>
        <div class="col-sm-8">
            <div class="input-group">    
                <input required type="text" class="form-control" required disabled="disabled" value="<%=itemURI%>" name="URI" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>"/>
                <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldnoeditableuri")}">
                    <div class="input-group-text"><i class="fa fa-ban text-danger" aria-hidden="true"></i></div>
                </div>
            </div>
        </div>
    </div>

    <%-- Show the changes header (if needed) --%>    
    <% if (showChangesBar) { %>
    <div class="row">
        <div class="col-sm-4"></div>
        <div class="col-sm-4"><div class="box-arrow-down alert-info">Proposed</div></div>
        <div class="col-sm-4"><div class="box-arrow-down alert-success">Current</div></div>
    </div>
    <% } %>

    <%
        RegItemproposed regItemproposed = null;
        // Setting the variable to check the availability of content translations
        boolean fieldTranslationUnavailable = false;

        // Getting the eventual regItemproposed
        try {
            regItemproposed = regItemproposedManager.getByRegItemReference(regItem);
        } catch (NoResultException e) {
        }

        List<RegLocalizationproposed> regLocalizationproposeds = null;
        if (regItemproposed != null) {
            regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemproposed, currentLanguage);
        }

        // Processing the regItem's fields
        for (RegFieldmapping regFieldmapping : regFieldmappings) {

            if ((regItem.getExternal() && regFieldmapping.getRegField().getIstitle()) || !regItem.getExternal()) {

                // Getting the fields localization (the translation of the label of each field)
                RegLocalization regFieldLocalization = null;
                try {
                    // Trying to get the field label translation int the current language
                    regFieldLocalization = regLocalizationManager.get(regFieldmapping.getRegField(), currentLanguage);
                    fieldTranslationUnavailable = false;
                } catch (NoResultException e) {
                    // If the current language is not available, the field label translation
                    // are loaded in the master language
                    regFieldLocalization = regLocalizationManager.get(regFieldmapping.getRegField(), masterLanguage);
                    fieldTranslationUnavailable = true;
                }

//                // Getting the eventual regItemproposed
//                try {
//                    regItemproposed = regItemproposedManager.getByRegItemReference(regItem);
//                } catch (NoResultException e) {
//                }
//                
//                List<RegLocalizationproposed> regLocalizationproposeds = null;
//                if(regItemproposed!=null){
//                    regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemproposed, currentLanguage);
//                }
%>


    <div class="row form-group editing-labels">

        <%-- To mark the field label translation unavailable in the current language
        use: <%=(fieldTranslationUnavailable) ? "*" : ""%> inside the label tag --%>

        <%-- Display the label of the current field --%>
        <label for="<%=regFieldmapping.getUuid()%>" class="col-sm-4 control-label"><%=regFieldLocalization.getValue()%><% if (regFieldmapping.getHidden()) {%>  <i title="<%=localization.getString("label.hiddenpublicinterface")%>" class="far fa-eye-slash"></i><%}%></label>

        <div class="col-sm-8">            
            <%
                /* ---- Field handler by type ---- */
 /* Each field type handler is kept separated because each of the field
            can have different behaviour */

                // Checking if the field is a reference to the Registry
                if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)) {%>                
            <%-- Get the "registry" reg relation (only one registry per each item) --%>
            <%= JspHelper.jspRegistryFieldHandler(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicateRegistry, regLocalizationManager, titleRegField, currentLanguage, masterLanguage)%>
            <%} // Checking if the field is a reference to the Register
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)) {%>                
            <%-- Get the "register" reg relation (only one register per each item) --%>
            <%= JspHelper.jspRegisterFieldHandler(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicateRegister, regLocalizationManager, titleRegField, currentLanguage, masterLanguage)%>
            <%} // Checking if the field is a reference to the collection        
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)) {%>                
            <%-- Get the "collection" reg relation (only one collection per each item) --%>                
            <%=JspHelper.jspCollectionFieldHandler(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicateCollection, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization)%>
            <%} // Checking if the field is a reference to the predecessor
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PREDECESSOR)) {%>                
            <%-- Get the "predecessor" reg relation of the current item --%>
            <%=JspHelper.jspPredecessorFieldHandler(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicatePredecessor, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regRelationproposedManager, regFieldmapping, regLocalizationproposedManager)%>
            <%} // Checking if the field is a reference to the successor
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_SUCCESSOR)) {%>                
            <%-- Get the "successor" reg relation of the current item --%>
            <%=JspHelper.jspSuccessorFieldHelper(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicateSuccessor, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regRelationproposedManager, regFieldmapping, regLocalizationproposedManager)%>
            <%} // Checking if the field is a reference to the parent        
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {%>                
            <%-- Get the "parent" reg relation of the current item --%>
            <%=JspHelper.jspParentFieldHandler(regRelationManager, regItem, regItemproposed, regLocalizationproposeds, regRelationpredicateParent, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regFieldmapping, regRelationproposedManager, regLocalizationproposedManager, currentUser, currentUserGroupsMap, itemMappings)%>       
            <%} // Checking if the field is a reference to a RegGroup        
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {%>           
            <%-- Process the "group" reference of the current item --%>
            <%=JspHelper.jspGroupFieldHandler(regFieldmapping, regItem, regItemproposed, regLocalizationproposeds, regItemRegGroupRegRoleMappingManager, regItemproposedRegGroupRegRoleMappingManager, localization)%>
            <%} // Status field
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)) {%>                
            <%-- Process the "status" reference of the current item --%>
            <%=JspHelper.jspStatusFieldHandler(regItem, regItemproposed, regLocalizationproposeds, currentLanguage, masterLanguage, regStatusManager, regStatuslocalizationManager)%>
            <%} // Date Creation
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATECREATION)) {%>                
            <%-- Process the "date creation" of the current item --%>
            <%=JspHelper.jspDateCreationHandler(regItem, regItemproposed, regLocalizationproposeds, currentLanguage, masterLanguage, regStatusManager, regStatuslocalizationManager)%>
            <%} // Date Edit
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATEEDIT)) {%>                
            <%-- Process the "date edit" of the current item --%>
            <%=JspHelper.jspDateEditHandler(regItem, regItemproposed, regLocalizationproposeds, currentLanguage, masterLanguage, regStatusManager, regStatuslocalizationManager)%>
            <%} // If the field is not one of the predefined reference, process it as a standard field or relation reference
            else {%>
            <%-- Process the "normal value" --%>
            <%=JspHelper.jspNormalValueHandler(regLocalizationManager, regItemManager, regFieldmapping, regItem, regItemproposed, currentLanguage, masterLanguage, localization, titleRegField, regLocalizationproposedManager, localization, currentUser, currentUserGroupsMap, itemMappings)%>
            <%}%>        
        </div>

    </div>
    <%
            }
        }
    %>

    <%-- Save proposal # Action type: AddEditItemProposal, AddEditAll --%>
    <%
        String[] actionItemProposal = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL};
        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionItemProposal = UserHelper.checkRegItemAction(actionItemProposal, currentUserGroupsMap, itemMappings);
        boolean permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
        boolean permissionRegisterRegistry = UserHelper.checkRegItemAction(actionRegisterRegistry, currentUserGroupsMap, itemMappings);

        // Check if there are no proposals or if the user is the current owner of the proposal
        boolean currentUserOwnerOfProposal = (regItemproposed == null || (regItemproposed.getRegUser().getUuid().equals(currentUser.getUuid())));

        // Check if there are no proposals or if the proposed item is in draft status
        boolean proposalDraft = (regItemproposed == null || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT));

        // Check if this regItem can be updated
        boolean canWrite = JspCommon.canWrite(regItemproposed, currentUser);
    %>

    <%-- Manage RoR export flag in case of Register --%>
    <% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {%>
    <div class="row form-group editing-labels">
        <%if (regItemproposed != null) {%>
        <%}%>
        <label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" class="col-sm-4">${localization.getString("label.registerfederationexport")}</label>

        <div class="col-sm-8">
            <%if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {%>
            <div class="row">
                <div class="col-sm-6">
                    <div id="vl-cont" class="<%=(regItem.getRorExport() != regItemproposed.getRorExport()) ? "pe-icon new-content" : ""%>">
                        <div class="row">
                            <div class="col-sm-12">
                                <div class="input-cnt">
                                    <input <% if (!((permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite)) { %>disabled="disabled"<% }%>  name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" type="checkbox"<%=(regItemproposed.getRorExport()) ? " checked" : ""%> data-toggle="toggle" data-offstyle="secondary" data-size="mini" value=""/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6"><%=(regItem.getRorExport()) ? localization.getString("label.on") : localization.getString("label.off")%></div>
            </div>
            <%} else {%>
            <div class="input-group">    
                <input <% if (!((permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite)) { %>disabled="disabled"<% }%> name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" type="checkbox"<%=(regItem.getRorExport()) ? " checked" : ""%> data-toggle="toggle" data-offstyle="secondary" data-size="mini" value="<%=regItem.getRorExport()%>"/>
            </div>
            <%}%>
        </div>
        <%if (regItemproposed != null) {%>

        <%}%>
    </div>    
    <% } %>

    <div class="row mt-3">

        <%
            if ((permissionItemProposal || permissionItemRegisterRegistry) && canWrite) {
        %>
        <% if (regItemproposed != null) {%>
        <div class="col-sm-3"></div>
        <div class="col-sm-3">
            <a class="btn btn-danger width100" data-toggle="confirmation" data-title="${localization.getString("discard.proposal.confirm")}" data-placement="top" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemproposed.getUuid()%>"><i class="far fa-trash-alt"></i> ${localization.getString("label.discardcurrentchanges")}</a>
        </div>
        <% } else { %>
        <div class="col-sm-6"></div>
        <%}%>


        <%
            boolean separatorNeeded = false;
            if ((permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite && !statusDisallowed) { %>
        <div class="col-sm-3">
            <div class="dropdown d-inline">
                <button type="button" class="btn btn-secondary dropdown-toggle width100" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${localization.getString("label.moreactions")}</button>
                <ul class="dropdown-menu">
                    <%-- If the current RegItem is not a Registry or a Register --%>
                    <% if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) { %>

                    <%-- Propose a supersession # Action type: ManageItemProposal, ManageRegisterRegistry --%>
                    <% if ((permissionItemProposal || permissionItemRegisterRegistry) && canWrite && !(regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED))) {
                            separatorNeeded = true;
                    %>
                    <a class="dropdown-item btn-modal-successor" class="btn-modal-successor" data-toggle="modal" data-keyboard="true" href=".<%=WebConstants.PAGE_URINAME_ADDSUCCESSOR%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=currentLanguage.getUuid()%>&<%=BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID%>=<%=BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED%>">${localization.getString("label.supersede")}</a>
                    <% } %>

                    <%-- Propose an invalidation # Action type: ManageItemProposal, ManageRegisterRegistry --%>
                    <% if ((permissionItemProposal || permissionItemRegisterRegistry) && canWrite && !(regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED))) {
                            separatorNeeded = true;
                    %>
                    <a class="dropdown-item btn-modal-successor" class="btn-modal-successor" data-toggle="modal" data-keyboard="true" href=".<%=WebConstants.PAGE_URINAME_ADDSUCCESSOR%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=currentLanguage.getUuid()%>&<%=BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID%>=<%=BaseConstants.KEY_STATUS_LOCALID_INVALID%>">${localization.getString("label.invalidate")}</a>
                    <% } %>

                    <%-- Propose a retirement # Action type: ManageItemProposal, ManageRegisterRegistry --%>
                    <% if ((permissionItemProposal || permissionItemRegisterRegistry) && canWrite && !(regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED) || regItem.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED))) {
                            separatorNeeded = true;
                    %>
                    <a class="dropdown-item" href=".<%=WebConstants.PAGE_URINAME_STATUSCHANGE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=currentLanguage.getUuid()%>&<%=BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID%>=<%=BaseConstants.KEY_STATUS_LOCALID_RETIRED%>">${localization.getString("label.retire")}</a>
                    <% } %>
                    <%if (separatorNeeded) {%>
                    <div role="separator" class="dropdown-divider"></div>
                    <%
                            }
                        }
                    %>

                    <%-- Manage itemclass # Action type: ManageRegisterRegistry --%>
                    <% if (permissionRegisterRegistry && canWrite) {%>                
                    <a class="dropdown-item" href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItem.getRegItemclass().getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=currentLanguage.getUuid()%>">${localization.getString("label.manageitemclass")}</a>
                    <a class="dropdown-item" href=".<%=WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItem.getRegItemclass().getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=currentLanguage.getUuid()%>">${localization.getString("label.managefields")}</a>
                    <% } %>

                </ul>
            </div> 
        </div> 
        <script>
            $(document).on('click', '.btn-modal-successor', function (e) {
                var href = $(this).attr("href");
                $('#modalSuccessor .modal-content').html('<div class="modal-header">The list of items is being loaded.</div><div class="modal-body"><img src="./res/img/ajax-loader.gif" /></div><div class="modal-footer"><button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fas fa-ban"></i> Close</button></div>');
                $('#modalSuccessor').modal();
                $("#modalSuccessor .modal-content").load(href, function () {
                    $(".selectpicker").selectpicker();
                });
                return false;
            })
        </script>
        <% } %>

        <% if (!statusDisallowed) { %>        
        <div class="col-sm-3">
            <button id="save-clarification" disabled="disabled" type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.saveclarification")}</button>
        </div>
        <% } %>
        <% } %>


    </div>


    <%-- Explain why the proposal cannot be edited --%>

    <%if (!currentUserOwnerOfProposal) {%>
    <div class="alert alert-warning mt-3" role="alert"><%=localization.getString("label.proposalnotowner")%></div>
    <%}%>

    <%if (!proposalDraft
                && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)) {%>
    <div class="alert alert-warning" role="alert"><%=localization.getString("label.proposalnotdraft")%></div>
    <%}%>

    <%if (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)) {%>
    <div class="alert alert-warning mt-3" role="alert"><%=localization.getString("label.proposalInvalidation")%></div>
    <a class="btn btn-danger" data-toggle="confirmation" data-title="${localization.getString("discard.proposal.confirm")}" data-placement="top" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemproposed.getUuid()%>"><i class="far fa-trash-alt"></i> ${localization.getString("label.discardcurrentchanges")}</a>
    <%}%>

    <%if (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)) {%>
    <div class="alert alert-warning mt-3" role="alert"><%=localization.getString("label.proposalRetirement")%></div>
    <a class="btn btn-danger" data-toggle="confirmation" data-title="${localization.getString("discard.proposal.confirm")}" data-placement="top" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemproposed.getUuid()%>"><i class="far fa-trash-alt"></i> ${localization.getString("label.discardcurrentchanges")}</a>
    <%}%>

    <%if (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)) {%>
    <div class="alert alert-warning mt-3" role="alert"><%=localization.getString("label.proposalSuperseded")%></div>
    <a class="btn btn-danger" data-toggle="confirmation" data-title="${localization.getString("discard.proposal.confirm")}" data-placement="top" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemproposed.getUuid()%>"><i class="far fa-trash-alt"></i> ${localization.getString("label.discardcurrentchanges")}</a>
    <%}%>


    <%-- Init bootstrap toggle--%>
    <script>

        <%
            if ((!currentUserOwnerOfProposal)
                    || (!proposalDraft
                    && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                    && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                    && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED))
                    || (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID))
                    || (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED))
                    || (!proposalDraft && regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED))) {
        %>
        $('[data-toggle="toggle"]').prop('disabled', 'disabled');
        <%
            }
        %>

        $('[data-toggle="toggle"]').bootstrapToggle();
        $(function () {
            $('[data-toggle="toggle"]').change(function () {
                $(this).val($(this).prop("checked"));
            });
        });
    </script>

    <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

</form>

<div class="modal fade" tabindex="-1" role="dialog" id="modalSuccessor">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
        </div>
    </div>
</div><!-- /.modal -->

<hr/>

<%-- ## Creating the Available items list (if available) ## --%>
<%
// Checking if the item can have Available items (if the itemclass has child)
    List<RegItemclass> regItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
    if (regItemclasses.size() > 0) {

// Creating the table with the list of fields (note: the table contents are loaded via ajax)
%>
<div class="row mt-3">
    <div class="col-sm-6">
        <h2>${localization.getString("label.containeditems")}</h2>
    </div>
    <div class="col-sm-6">
        <%--Control button to add item/register to the list--%>

        <div class="row">

            <%--Differentiating the permission if it is a registry or a simple item--%>
            <% if ((regItemRegistry != null && regItem.getUuid().equals(regItemRegistry.getUuid()))) { %>        
            <% if ((permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite) {%>

            <div class="col-sm-6">
                <a href=".<%=(regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) ? WebConstants.PAGE_URINAME_ADDREGISTER : WebConstants.PAGE_URINAME_ADDITEM%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>" class="btn btn-success float-right width100"><% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {%><i class="fas fa-plus"></i> ${localization.getString("label.addregister")}<%} else {%><i class="fas fa-plus"></i> ${localization.getString("label.additem")}<%}%></a>
            </div>
            <% } %>        
            <% } else { %>
            <% if (permissionItemProposal || permissionItemRegisterRegistry) {%>

            <% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {%>

            <%-- Bulk import commands --%>
            <%
                if (!statusDisallowed) { %> 
                
               <div class="col-sm-6">
            <div class="dropdown d-inline"> 
                    <button type="button" class="btn btn-primary float-right width100" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Start bulk process</button>
                <ul class="dropdown-menu" x-placement="bottom-start" style="position: absolute; will-change: transform; top: 0px; left: 0px; transform: translate3d(0px, 32px, 0px);">              
                    <a class="dropdown-item" data-target="#exampleModalLong" data-toggle="modal"> ${localization.getString("label.bulkimport")}</a>
                    <a class="dropdown-item" data-target="#exampleModalLongEdit" data-toggle="modal"> ${localization.getString("label.bulkedit")}</a>
                </ul>
            </div> 
        </div>
                    

            <% }%>  


            <!-- Modal -->
            <div class="modal fade" id="exampleModalLong" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <form method="POST" action=".<%=WebConstants.PAGE_URINAME_BULK_TEMPLATE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_LOAD_BULK_TEMPLATE%>&<%=BaseConstants.KEY_REQUEST_ISBULKEDIT%>=<%="false"%>&csrfPreventionSalt=${csrfPreventionSalt}" enctype="multipart/form-data">

                        <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalLongTitle">${localization.getString("label.bulkimport")}</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <h5>${localization.getString("download.template.bulkimport")}</h5>
                                <a href=".<%=WebConstants.PAGE_URINAME_BULK_TEMPLATE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_DOWNLOAD_BULK_TEMPLATE%>" class="btn btn-success" title="${localization.getString("template.bulkimport.title")}">
                                    <i class="fas fa-download"></i> ${localization.getString("template")}
                                </a>
                                <hr/>
                                <h5 for="templateFileUpload">${localization.getString("choose.csv.file")}</h5>
                                <div class="form-group">
                                    <div class="input-group" name="divFile">
                                        <input type="file" class="form-control-file" id="templateFileUpload" placeholder='${localization.getString("choose.file")}...' accept=".csv" name="<%=BaseConstants.KEY_REQUEST_LOAD_FILE_BULK%>" id="<%=BaseConstants.KEY_REQUEST_LOAD_FILE_BULK%>"/>
                                    </div>
                                </div>
                            </div>
                            <div id="browseLoaderModalFooter" class="modal-footer">
                                <div class="col-sm-6">
                                    <button type="reset" class="btn btn-danger width100"><i class="fas fa-sync-alt"></i> ${localization.getString("reset")}</button>                                    
                                </div>
                                <div class="col-sm-6">
                                    <button id="btnStartBulkImport" type="submit" class="btn btn-primary width100">
                                        <i class="fas fa-upload"></i> ${localization.getString("label.start.bulkimport")}
                                    </button>
                                    <script>
            $(document).on('click', '#btnStartBulkImport', function (e) {
                $('#bufferLoader').html('<div class="lds-ellipsis"><div></div><div></div><div></div><div></div></div>');
            });
        </script>
                                    
                                </div> 

                            </div>
                                                                        <div id="bufferLoader"></div>
                        </div>
                    </form>
                </div>
            </div>
            <!-- Same structure but for bulk edit instead of bulk import -->                        
            <div class="modal fade" id="exampleModalLongEdit" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongEditTitle" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <form method="POST" action=".<%=WebConstants.PAGE_URINAME_BULK_TEMPLATE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_LOAD_BULK_TEMPLATE%>&<%=BaseConstants.KEY_REQUEST_ISBULKEDIT%>=<%="true"%>&csrfPreventionSalt=${csrfPreventionSalt}" enctype="multipart/form-data">

                        <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalLongEditTitle">${localization.getString("label.bulkedit")}</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <h5>${localization.getString("download.template.bulkedit")}</h5>
                                <a href=".<%=WebConstants.PAGE_URINAME_BULK_TEMPLATE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_DOWNLOAD_BULK_TEMPLATE%>" class="btn btn-success" title="${localization.getString("template.bulkedit.title")}">
                                    <i class="fas fa-download"></i> ${localization.getString("template")}
                                </a>
                                <hr/>
                                <h5 for="templateFileUpload">${localization.getString("choose.csv.file")}</h5>
                                <div class="form-group">
                                    <div class="input-group" name="divFile">
                                        <input type="file" class="form-control-file" id="templateFileUpload" placeholder='${localization.getString("choose.file")}...' accept=".csv" name="<%=BaseConstants.KEY_REQUEST_LOAD_FILE_BULK%>" id="<%=BaseConstants.KEY_REQUEST_LOAD_FILE_BULK%>"/>
                                    </div>
                                </div>
                            </div>
                            <div id="browseLoaderModalFooter" class="modal-footer">
                                <div class="col-sm-6">
                                    <button type="reset" class="btn btn-danger width100"><i class="fas fa-sync-alt"></i> ${localization.getString("reset")}</button>                                    
                                </div>
                                <div class="col-sm-6">
                                    <button id="btnStartBulkImport" type="submit" class="btn btn-primary width100">
                                        <i class="fas fa-upload"></i> ${localization.getString("label.start.bulkedit")}
                                    </button>
                                    <script>
            $(document).on('click', '#btnStartBulkImport', function (e) {
                $('#bufferLoader').html('<div class="lds-ellipsis"><div></div><div></div><div></div><div></div></div>');
            });
        </script>
                                    
                                </div> 

                            </div>
                                                                        <div id="bufferLoader"></div>
                        </div>
                    </form>
                </div>
            </div>                         
                                    
                                    
            <% } else { %>
            <div class="col-sm-6"></div>
            
            <%}%>

            <%
                if (!statusDisallowed) {%> 
            <div class="col-sm-6">
                <a href=".<%=(regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) ? WebConstants.PAGE_URINAME_ADDREGISTER : WebConstants.PAGE_URINAME_ADDITEM%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>" class="btn btn-success float-right width100"><% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {%><i class="fas fa-plus"></i> ${localization.getString("label.addregister")}<%} else {%><i class="fas fa-plus"></i> ${localization.getString("label.additem")}<%}%></a>
            </div>  
            <% } %> 
            <% } %> 
            <% } %> 
        </div>

    </div>
</div>
<div id="langMessage">${localization.getString("label.notranslation")}</div>

<%
    String isSolrActive = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_ACTIVE);
    if (isSolrActive.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
%>

<script>
    $('#list-table-search').on('keyup', function () {
        let txt = this.value;
        $('#list-table').DataTable().search(txt).draw();
    });
</script>
<% } %>

<table id="list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
    <thead>
        <tr>
            <%  // If there are more than one "contained item class", load just the label of the Available items (registers)    
                if (regItemclasses.size() > 1) {
                    RegLocalization fieldLocalization;
                    try {
                        fieldLocalization = regLocalizationManager.get(titleRegField, currentLanguage);
                    } catch (NoResultException e) {
                        // if not available in the current language, get it in the master language
                        fieldLocalization = regLocalizationManager.get(titleRegField, masterLanguage);
                    }
                    if (isSolrActive.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            %> <th><div class="d-flex"><div class="mt-2"><%=fieldLocalization.getValue()%></div> <div class="ml-auto"><input class="form-control form-control-sm w-auto" id="list-table-search" type="text" placeholder="<%= localization.getString("label.filterlabel")%>" /></div></div></th><%
            } else {
                        %><th><div class="d-flex"><div class="mt-2"><%=fieldLocalization.getValue()%></div></div><%
                            }
                        } else {
                            // Getting the field list for the Available items
                            RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
                            regFieldmappings = regFieldmappingManager.getAll(regItemclasses.get(0));

                            for (RegFieldmapping tmpRegFieldmapping : regFieldmappings) {
                                // Showing the table header for each reg field (if it is visible in the table)
                                if (tmpRegFieldmapping.getTablevisible()) {
                                    RegLocalization fieldLocalization;
                                    try {
                                        fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), currentLanguage);
                                    } catch (NoResultException e) {
                                        fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), masterLanguage);
                                    }

                                    if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)) {
                    %> <th><%=fieldLocalization.getValue()%> <i class="fas fa-info-circle text-primary" title="${localization.getString("label.registrystatus")}"></i></th><%
                    } else {
                        if (isSolrActive.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                %> <th><div class="d-flex"><div class="mt-2"><%=fieldLocalization.getValue()%></div> <%=(tmpRegFieldmapping.getRegField().getIstitle()) ? " <div class=\"ml-auto\"><input class=\"form-control form-control-sm w-auto\" id=\"list-table-search\" type=\"text\" placeholder=\"" + localization.getString("label.filterlabel") + "\" /></div>" : ""%></div></th><%
                } else {
                %> <th><div class="d-flex"><div class="mt-2"><%=fieldLocalization.getValue()%></div></div><%
                                    }
                                }
                            }
                        }
                    }
                    %>
        </tr>
    </thead>
</table>



<div id="proposedShow" class="mt-4">
    <h2>${localization.getString("label.proposeditems")}</h2>
    <%-- Showing any eventual RegItemProposed --%>
    <table id="proposed-list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
        <thead>
            <tr>
                <%  // If there are more than one "contained item class", load just the label of the Available items (registers)    
                    if (regItemclasses.size() > 1) {
                        RegLocalization fieldLocalization;
                        try {
                            fieldLocalization = regLocalizationManager.get(titleRegField, currentLanguage);
                        } catch (NoResultException e) {
                            // if not available in the current language, get it in the master language
                            fieldLocalization = regLocalizationManager.get(titleRegField, masterLanguage);
                        }
                %><th><%=fieldLocalization.getValue()%></th><%
                } else {
                    // Getting the field list for the Available items
                    RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
                    regFieldmappings = regFieldmappingManager.getAll(regItemclasses.get(0));

                    for (RegFieldmapping tmpRegFieldmapping : regFieldmappings) {
                        // Showing the table header for each reg field (if it is visible in the table)
                        if (tmpRegFieldmapping.getTablevisible()) {
                            RegLocalization fieldLocalization;
                            try {
                                fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), currentLanguage);
                            } catch (NoResultException e) {
                                fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), masterLanguage);
                            }
                    %>
                <th><%=fieldLocalization.getValue()%></th>
                    <%
                                }
                            }
                        }
                    %>
            </tr>
        </thead>
    </table>

    <hr/>
</div>

<%
    // If the item cannot have Available items (the itemclass has no child), a message is shown
} else {
    if (permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) {
        if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
%><p><%=localization.getString("label.noregisters")%></p>
<%--Control button to add item/register to the list--%>

<div class="row mt-3">
    <div class="col-md-3 offset-md-9">
        <%--Differentiating the permission if it is a registry or a simple item--%>
        <% if ((regItemRegistry != null && regItem.getUuid().equals(regItemRegistry.getUuid()))) { %>        
        <% if (permissionRegisterRegistry) {%>
        <a href=".<%=(regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) ? WebConstants.PAGE_URINAME_ADDREGISTER : WebConstants.PAGE_URINAME_ADDITEM%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>" class="btn btn-success width100"><% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {%><i class="fas fa-plus"></i> ${localization.getString("label.addregister")}<%} else {%><i class="fas fa-plus"></i> ${localization.getString("label.additem")}<%}%></a>
        <% } %>        
        <% } else { %>
        <% if (permissionItemProposal || permissionItemRegisterRegistry) {%>
        <a href=".<%=(regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) ? WebConstants.PAGE_URINAME_ADDREGISTER : WebConstants.PAGE_URINAME_ADDITEM%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>" class="btn btn-success width100"><% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {%><i class="fas fa-plus"></i> ${localization.getString("label.addregister")}<%} else {%><i class="fas fa-plus"></i> ${localization.getString("label.additem")}<%}%></a>
        <% } %> 
        <% } %> 
    </div>
</div>
<%
} else {
    if (regItem.getExternal()) {
%><p><%=localization.getString("label.externalitemsnocontained")%></p><%
} else {
    if (permissionItemRegisterRegistry) {
%><p><%=MessageFormat.format(localization.getString("label.itemclassmanagemessage"), WebConstants.PAGE_URINAME_ITEMCLASS, BaseConstants.KEY_REQUEST_ITEMCLASSUUID, regItem.getRegItemclass().getUuid(), BaseConstants.KEY_REQUEST_LANGUAGEUUID, currentLanguage.getUuid())%></p><%
} else {
%><p><%=localization.getString("label.itemclassmanagemessagenomanage")%></p><%
            }
        }
    }
} else {
    if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
%><p><%=localization.getString("label.noregisters")%></p><%
} else {
%><p><%=localization.getString("label.itemclassmanagemessagenomanage")%></p><%
            }
        }
    }
%>

<%
    /* Showing eventual children items */
%>
<div id="childrenShow" class="mt-3">
    <h2>${localization.getString("label.childrenitems")}</h2>
    <table id="children-list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
        <thead>
            <tr>
                <%                // Getting the field list for the Available items
                    RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
                    regFieldmappings = regFieldmappingManager.getAll(regItem.getRegItemclass());
                    int fieldToShow = 0;

                    for (RegFieldmapping tmpRegFieldmapping : regFieldmappings) {
                        // Showing the table header for each reg field (if it is visible in the table)
                        if (tmpRegFieldmapping.getTablevisible()) {
                            RegLocalization fieldLocalization;
                            try {
                                fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), currentLanguage);
                            } catch (NoResultException e) {
                                fieldLocalization = regLocalizationManager.get(tmpRegFieldmapping.getRegField(), masterLanguage);
                            }
                            fieldToShow++;
                %>
                <th><%=fieldLocalization.getValue()%></th>
                    <%
                            }
                        }
                        if (fieldToShow < 1) {
                    %>
                <th>${localization.getString("label.nofieldstoshow")}</th>
                    <%
                        }
                    %>
            </tr>
        </thead>
    </table>
</div>
<%
    // Showing the message in case of translation unavailable for the fileds' label
    if (fieldTranslationUnavailable && (permissionItemRegisterRegistry || permissionRegisterRegistry)) {%>
<p class="small mt-3"><%=MessageFormat.format(localization.getString("label.translationnotavailable"), WebConstants.PAGE_URINAME_FIELD, BaseConstants.KEY_REQUEST_ITEMCLASSUUID, regItem.getRegItemclass().getUuid(), BaseConstants.KEY_REQUEST_LANGUAGEUUID, currentLanguage.getUuid())%></p>
<% } %>

<script>
    <%-- activating the validator only if the current user can edit --%>
    <% if (permissionItemProposal || permissionItemRegisterRegistry || permissionRegisterRegistry) { %>
    $('#editing-form').validator();
    <%-- Init the confirm message 
    $('[data-toggle=confirmation]').confirmation({
        rootSelector: '[data-toggle=confirmation]'
    });--%>
    <% }%>
    $('[data-toggle="tooltip"]').tooltip();

    <%-- Init the confirm message --%>
    $('[data-toggle=confirmation]').confirmation({
        rootSelector: '[data-toggle=confirmation]'
    });
</script>

<script src="./res/js/bootstrap-confirmation.min.js"></script>

<script>
    function bs_input_file() {
        $(".input-file").before(
                function () {
                    if (!$(this).prev().hasClass('input-ghost')) {
                        var element = $("<input type='file' class='input-ghost' style='visibility:hidden; height:0'>");
                        element.attr("name", $(this).attr("name"));
                        element.change(function () {
                            element.next(element).find('input').val((element.val()).split('\\').pop());
                        });
                        $(this).find("button.btn-choose").click(function () {
                            element.click();
                        });
                        $(this).find("button.btn-reset").click(function () {
                            element.val(null);
                            $(this).parents(".input-file").find('input').val('');
                        });
                        $(this).find('input').css("cursor", "pointer");
                        $(this).find('input').mousedown(function () {
                            $(this).parents('.input-file').prev().click();
                            return false;
                        });
                        return element;
                    }
                }
        );
    }
    $(function () {
        bs_input_file();
    });
    $('#myModal').on('shown.bs.modal', function () {
        $('#myInput').trigger('focus')
    })

    $('.date').datepicker({
        calendarWeeks: true,
        todayHighlight: true,
        autoclose: true
    });
</script>
