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
<%@page import="eu.europa.ec.re3gistry2.base.utility.ItemproposedHelper"%>
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspCommon"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelationproposed"%>
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
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspHelperNewProposedItem"%>
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

    // Getting the configuration
    Configuration configuration = Configuration.getInstance();

    // Initializing managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
    RegRelationManager regRelationManager = new RegRelationManager(entityManager);
    RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
    RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);
    RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
    RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
    RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
    RegStatusManager regStatusManager = new RegStatusManager(entityManager);
    RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManager);

    // Getting objects from the request
    List<RegFieldmapping> regFieldmappings = (List<RegFieldmapping>) request.getAttribute(BaseConstants.KEY_REQUEST_FIELDMAPPINGS);
    //RegItem regItem = (RegItem) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEM);
    RegItemproposed regItemproposed = (RegItemproposed) request.getAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED);
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
    if (!regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
        List<RegRelationproposed> regRelationproposedRegistries = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegistry);
        try {
            regItemRegistry = regRelationproposedRegistries.get(0).getRegItemObject();
        } catch (Exception e) {
            regItemRegistry = null;
        }
    }

    // Setting the register
    RegItem regItemRegister = null;
    if (!regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
        List<RegRelationproposed> regRelationproposedRegisters = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
        try {
            regItemRegister = regRelationproposedRegisters.get(0).getRegItemObject();
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
    RegItemproposed regItemproposedCheck = null;
    if (regItemRegister == null && regItemRegistry != null) {
        regItemproposedCheck = regItemproposed;
    } else if (regItemRegister != null) {
        regItemCheck = regItemRegister;
    } else if (regItemRegister == null && regItemRegistry == null) {
        regItemproposedCheck = regItemproposed;
    }

    // Getting the item, group, role mapping for this item
    List<RegItemRegGroupRegRoleMapping> itemMappings = null;
    List<RegItemproposedRegGroupRegRoleMapping> itemProposedMappings = null;
    if (regItemproposedCheck == null) {
        itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);
    } else {
        itemProposedMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemproposedCheck);
    }

    // Creating the URI of the current RegItem
    String itemURI = ItemproposedHelper.getProposedURI(regItemproposed, regItemRegister, regRelationpredicateCollection, regRelationproposedManager, regRelationManager);

    HashMap<String, String> registryHash = ItemproposedHelper.getRegistryLocalizationByRegItemproposed(regItemproposed, entityManager, currentLanguage);
    String registryLabel = "";
    String registryURI = "";
    for (HashMap.Entry<String, String> en : registryHash.entrySet()) {
        registryLabel = en.getKey();
        registryURI = en.getValue();
    }

    HashMap<String, String> registerHash = ItemproposedHelper.getRegisterLocalizationByRegItemproposed(regItemproposed, entityManager, currentLanguage);
    String registerLabel = "";
    String registerURI = "";
    for (HashMap.Entry<String, String> en : registerHash.entrySet()) {
        registerLabel = en.getKey();
        registerURI = en.getValue();
    }

    String collecitonHTML = ItemproposedHelper.getBreadcrumbCollectionHTMLForRegItemproposed("", regItemproposed, entityManager, currentLanguage);
%>


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
<form id="editing-form" method="post" accept-charset="UTF-8" role="form">
    
    <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
    
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_FORM_ITEMUUID%>" value="<%=regItemproposed.getUuid()%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID%>" value="<%=currentLanguage.getUuid()%>" /> 

    <%-- The URI field is always available and non editable --%>
    <div class="row form-group editing-labels">
        <label for="URI" class="col-sm-4">${localization.getString("label.uri")}</label>

        <div class="col-sm-8">
            <div class="input-group">    
                <input required type="text" class="form-control" required disabled="disabled" value="<%=itemURI%>" name="URI" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>"/>
                <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldnoeditableuri")}"><div class="input-group-text"><i class="fa fa-ban text-danger" aria-hidden="true"></i></div></div>
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
        // Setting the variable to check the availability of content translations
        boolean fieldTranslationUnavailable = false;

        // Processing the regItem's fields
        for (RegFieldmapping regFieldmapping : regFieldmappings) {

            if ((regItemproposed.getExternal() && regFieldmapping.getRegField().getIstitle()) || !regItemproposed.getExternal()) {

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

    %>


    <div class="row form-group editing-labels">

        <%-- To mark the field label translation unavailable in the current language
        use: <%=(fieldTranslationUnavailable) ? "*" : ""%> inside the label tag --%>

        <%-- Display the label of the current field --%>
        <label for="<%=regFieldmapping.getUuid()%>" class="col-sm-4 control-label"><%=regFieldLocalization.getValue()%><% if (regFieldmapping.getHidden()) {%>  <i title="<%=localization.getString("label.hiddenpublicinterface")%>" class="notice fas fa-eye-slash"></i><%}%></label>

        <div class="col-sm-8">            
            <%
                /* ---- Field handler by type ---- */
 /* Each field type handler is kept separated because each of the field
                can have different behaviour */

                // Checking if the field is a reference to the Registry
                if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)) {%>                
            <%-- Get the "registry" reg relation (only one registry per each item) --%>
            <%= JspHelperNewProposedItem.jspRegistryFieldHandler(regItemproposed, regRelationpredicateRegistry, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, regRelationproposedManager)%>
            <%} // Checking if the field is a reference to the Register
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)) {%>                
            <%-- Get the "register" reg relation (only one register per each item) --%>
            <%= JspHelperNewProposedItem.jspRegisterFieldHandler(regItemproposed, regRelationpredicateRegister, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, regRelationproposedManager)%>
            <%} // Checking if the field is a reference to the collection        
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)) {%>                
            <%-- Get the "collection" reg relation (only one collection per each item) --%>                
            <%=JspHelperNewProposedItem.jspCollectionFieldHandler(regRelationproposedManager, regItemproposed, regRelationpredicateCollection, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization)%>
            <%} // Checking if the field is a reference to the predecessor
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PREDECESSOR)) {%>                
            <%-- Get the "predecessor" reg relation of the current item --%>
            <%=JspHelperNewProposedItem.jspPredecessorFieldHandler(regItemproposed, regRelationpredicatePredecessor, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regRelationproposedManager, regFieldmapping, regLocalizationproposedManager)%>
            <%} // Checking if the field is a reference to the successor
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_SUCCESSOR)) {%>                
            <%-- Get the "successor" reg relation of the current item --%>
            <%=JspHelperNewProposedItem.jspSuccessorFieldHelper(regItemproposed, regRelationpredicateSuccessor, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regRelationproposedManager, regFieldmapping, regLocalizationproposedManager)%>
            <%} // Checking if the field is a reference to the parent        
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {%>                
            <%-- Get the "parent" reg relation of the current item --%>
            <%=JspHelperNewProposedItem.jspParentFieldHandler(regItemproposed, regRelationpredicateParent, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, regFieldmapping, regRelationproposedManager, regLocalizationproposedManager, currentUser, currentUserGroupsMap, itemMappings)%>       
            <%} // Checking if the field is a reference to a RegGroup
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)) {%>                
            <%-- Process the "status" reference of the current item --%>
            <%=JspHelperNewProposedItem.getRegStatusHTML(regItemproposed.getRegStatus().getLocalid(), regStatusManager, regStatuslocalizationManager, currentLanguage, masterLanguage)%>
            <%} else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {%>                
            <%-- Process the "group" reference of the current item --%>
            <%=JspHelperNewProposedItem.jspGroupFieldHandler(regFieldmapping, regItemproposed, localization, regItemproposedRegGroupRegRoleMappingManager)%>
            <%} // If the field is not one of the predefined reference, process it as a standard field or relation reference            
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATECREATION)) {%>                
            <%-- Process the "date creation" of the current item --%>
            <%=JspHelperNewProposedItem.jspDateCreationHandler(regItemproposed, currentLanguage, masterLanguage, regStatusManager, regStatuslocalizationManager)%>
            <%} // Date Edit
            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATEEDIT)) {%>                
            <%-- Process the "date edit" of the current item --%>
            <%=JspHelperNewProposedItem.jspDateEditHandler(regItemproposed, currentLanguage, masterLanguage, regStatusManager, regStatuslocalizationManager)%>
            <%} else {%>
            <%-- Process the "normal value" --%>
            <%=JspHelperNewProposedItem.jspNormalValueHandler(regLocalizationManager, regFieldmapping, regItemproposed, currentLanguage, masterLanguage, localization, titleRegField, regLocalizationproposedManager, localization, currentUser, currentUserGroupsMap, itemMappings, itemProposedMappings)%>
            <%}%>        
        </div>
    </div>
    <%
            }
        }
    %>

    <%-- Save proposal # Action type: AddEditItemProposal, AddEditAll --%>
    <%
        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};

        boolean permissionItemRegisterRegistry = false;
        boolean permissionRegisterRegistry = false;
        if (itemMappings != null) {
            permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
            permissionRegisterRegistry = UserHelper.checkRegItemAction(actionRegisterRegistry, currentUserGroupsMap, itemMappings);
        } else {
            permissionItemRegisterRegistry = UserHelper.checkRegItemproposedAction(actionItemRegisterRegistry, currentUserGroupsMap, itemProposedMappings);
            permissionRegisterRegistry = UserHelper.checkRegItemproposedAction(actionRegisterRegistry, currentUserGroupsMap, itemProposedMappings);
        }
        // Check if there are no proposals or if the user is the current owner of the proposal
        boolean currentUserOwnerOfProposal = (regItemproposed == null || (regItemproposed.getRegUser().getUuid().equals(currentUser.getUuid())));

        // Check if there are no proposals or if the proposed item is in draft status
        boolean proposalDraft = (regItemproposed == null || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT));

        // Check if this regItem can be updated
        boolean canWrite = JspCommon.canWrite(regItemproposed, currentUser);
    %>

    <%-- Manage RoR export flag in case of Register --%>
    <% if (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {%>
    <div class="row form-group editing-labels">
        <%if (regItemproposed != null) {%>

        <%}%>
        <label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" class="col-sm-4">${localization.getString("label.registerfederationexport")}</label>

        <div class="col-sm-8">
            <div class="input-group">    
                <input <% if (!((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite)) { %>disabled="disabled"<% }%> name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" type="checkbox"<%=(regItemproposed.getRorExport()) ? " checked" : ""%> data-toggle="toggle" data-offstyle="secondary" data-size="mini" value=""/>
            </div>
        </div>
        <script>
            $('[data-toggle="toggle"]').bootstrapToggle();
            $(function () {
                $('[data-toggle="toggle"]').change(function () {
                    $(this).val($(this).prop("checked"));
                });
            });
        </script>        
        <%if (regItemproposed != null) {%>

        <%}%>
    </div>    
    <% } %>

    <%
        if (permissionItemRegisterRegistry && canWrite) {
    %>
    <div class="row">
        <div class="col-sm-6"></div>
        <div class="col-sm-3">
            <a class="btn btn-danger btn-approve-action width100" data-toggle="confirmation" data-title="${localization.getString("discard.proposal.confirm")}" data-placement="top" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemproposed.getUuid()%>"><i class="far fa-trash-alt"></i> ${localization.getString("label.discardcurrentchanges")}</a>
        </div>
        <div class="col-sm-3">
            <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
        </div>
    </div>
    <% } %>

    <%if (!currentUserOwnerOfProposal) {%>
    <div class="alert alert-warning mt-3" role="alert"><%=localization.getString("label.proposalnotowner")%></div>
    <%}%>

    <%if (!proposalDraft) {%>
    <div class="alert alert-warning" role="alert"><%=localization.getString("label.proposalnotdraft")%></div>
    <%}%>

    <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

</form>


<%
// Showing the message in case of translation unavailable for the fileds' label
    if (fieldTranslationUnavailable && (permissionItemRegisterRegistry || permissionRegisterRegistry)) {%>
<p class="small mt-3"><%=MessageFormat.format(localization.getString("label.translationnotavailable"), WebConstants.PAGE_URINAME_FIELD, BaseConstants.KEY_REQUEST_ITEMCLASSUUID, regItemproposed.getRegItemclass().getUuid(), BaseConstants.KEY_REQUEST_LANGUAGEUUID, currentLanguage.getUuid())%></p>
<% } %>

<script>
    <%-- activating the validator only if the current user can edit --%>
    <% if (permissionItemRegisterRegistry || permissionRegisterRegistry) { %>
    $('#editing-form').validator();
    <% }%>
    $('[data-toggle="tooltip"]').tooltip();

    <%-- Init the confirm message --%>
    $('[data-toggle=confirmation]').confirmation({
        rootSelector: '[data-toggle=confirmation]'
    });

    $('.date').datepicker({
        calendarWeeks: true,
        todayHighlight: true,
        autoclose: true
    });
</script>
<script src="./res/js/bootstrap-confirmation.min.js"></script>