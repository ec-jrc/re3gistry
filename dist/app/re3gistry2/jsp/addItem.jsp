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
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegStatus"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemproposed"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRole"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspHelper"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelation"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelationpredicate"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.ItemHelper"%>
<%@page import="javax.persistence.NoResultException"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldmapping"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Getting the system localization
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Initializing managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegRelationManager regRelationManager = new RegRelationManager(entityManager);
    RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);
    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
    RegGroupManager regGroupManager = new RegGroupManager(entityManager);
    RegStatusManager regStatusManager = new RegStatusManager(entityManager);
    RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManager);

    // Getting objects from the request
    RegLanguagecode regLanguagecode = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    RegItem regItem = (RegItem) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEM);

    List<RegFieldmapping> regFieldmappings = (List<RegFieldmapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGS);

    String operationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);

    // Getting request parameter
    String newItem_ItemclassUuid = (String) request.getAttribute(BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID);

    // Getting the new RegItemclass (in case of new register)
    RegItemclass newRegItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS_NEWITEM);

    // Checking if the current user has the rights to add a new item
    String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
    String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
    boolean permissionItemRegisterRegistry = UserHelper.checkGenericAction(actionItemRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
    boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

    // External item flag
    boolean externalItem = (request.getAttribute(BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM) != null) ? (Boolean) request.getAttribute(BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM) : false;
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading"><%if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) { %>${localization.getString("addRegister.label.title")}<%} else {%>${localization.getString("addItem.label.title")}<%}%></h1>
                </div>
            </div>

            <% if (operationSuccess != null) {%>
            <div class="alert alert-danger alert-dismissible mt-3" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <%=operationSuccess%>
            </div>
            <% }%>

            <%-- Handling the addition of registers --%>
            <% if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && (newItem_ItemclassUuid == null || (newItem_ItemclassUuid != null && newItem_ItemclassUuid.length() < 1))) {%>
            <p>${localization.getString("addItem.text.informative")}</p>
            <p><%=MessageFormat.format(localization.getString("addItem.text.textlink"), WebConstants.PAGE_URINAME_ADDITEMCLASS)%></p>

            <p>&nbsp;</p>

            <form id="editing-form" class="new-insert" method="post" accept-charset="UTF-8" role="form" data-toggle="validator">

                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID%>" value="<%=regItem.getUuid()%>" />

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">
                        ${localization.getString("label.itemclass")}
                    </label>
                    <div class="col-sm-8">
                        <div class="input-group">                                        
                            <select class="selectpicker form-control" required name="<%=BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID%>">
                                <option value=""></option>
                                <%
                                    List<RegItemclass> regItemclasses = regItemclassManager.getAll();

                                    //Listing the itemclasses to be chosen
                                    for (RegItemclass tmpRegItemclass : regItemclasses) {
                                        // When adding registers, all the itemclass can be chosen except the registry
                                        if (tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) || tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                                %><option disabled="disabled" value="<%=tmpRegItemclass.getUuid()%>"><%=tmpRegItemclass.getLocalid()%></option><%
                                } else {
                                    RegItemManager regItemManager = new RegItemManager(entityManager);
                                    RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                                    List<RegItem> regItems = regItemManager.getAll(tmpRegItemclass);
                                    List<RegItemproposed> regItemproposeds = regItemproposedManager.getAll(tmpRegItemclass);
                                    if (regItems.isEmpty() && regItemproposeds.isEmpty()) {
                                %><option  value="<%=tmpRegItemclass.getUuid()%>"><%=tmpRegItemclass.getLocalid()%></option><%
                                } else {
                                    %><option disabled="disabled" value="<%=tmpRegItemclass.getUuid()%>"><%=tmpRegItemclass.getLocalid()%></option><%
                                                }
                                            }
                                        }
                                    %>                
                            </select>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <hr/>
                <% if (permissionRegisterRegistry) { %>
                <button type="submit" class="btn btn-success">${localization.getString("label.continue")}</button>
                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>
                <%}%>
            </form>

            <%} else {%>
            <p><%=MessageFormat.format(localization.getString("addItem.text.insertinmaserlanguage"), masterLanguage.getIso6391code())%></p>
            <p>&nbsp;</p>
            <%
                // Loading the needed RegRelation predicates (to chreate the URI)
                RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
                RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
                RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

                // Setting the registry
                RegItem regItemRegistry = null;
                List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
                try {
                    regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
                } catch (Exception e) {
                    regItemRegistry = regItem;
                }

                // Setting the register
                RegItem regItemRegister = null;
                List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
                try {
                    regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                } catch (Exception e) {
                    regItemRegister = regItem;
                }

                // Creating the uri for the containing item
                String itemURI;
                if (newRegItemclass != null && (newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY))) {
                    itemURI = newRegItemclass.getBaseuri() + "/" + newRegItemclass.getLocalid();
                    // getting the new Item RegItemclass
                    newItem_ItemclassUuid = newRegItemclass.getUuid();
                } else {
                    itemURI = ItemHelper.getURI(regItem, regItemRegistry, regItemRegister, regRelationpredicateCollection, regRelationManager);

                }
            %>  

            <form id="editing-form" class="new-insert" method="post" accept-charset="UTF-8" role="form">

                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_NEWITEMINSERT%>" value="<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>" />
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID%>" value="<%=regLanguagecode.getUuid()%>" />
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID%>" value="<%=regItem.getUuid()%>" />
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID%>" value="<%=(newItem_ItemclassUuid != null) ? newItem_ItemclassUuid : ""%>" />

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("label.externalvalue")}</label>
                    <div class="col-sm-8">
                        <input type="checkbox" class="nochange" <%=(externalItem) ? "checked=\"checked\"" : ""%> id="<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>" />
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("label.uri")}</label>
                    <div class="col-sm-8">
                        <%
                            if (newRegItemclass != null && (newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY))) {
                        %><input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" value="<%=newRegItemclass.getRegItemclasstype().getLocalid()%>" /><%
                            out.print(itemURI);
                        } else {
                        %>
                        <%
                            if (!externalItem) {
                        %>
                        <div class="row"> 
                            <div class="col-sm-6">
                                <%=itemURI%>/ 
                            </div>

                            <div class="col-sm-6">
                                <%}%>
                                <div class="input-group">
                                    <input maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" data-toggle="tooltip" data-placement="top" title="<%=localization.getString("label.fielduriinfo")%>" type="text" class="form-control<%=(!externalItem) ? " input-uri" : ""%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" value="" required/>
                                    <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                        <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                                    </div>
                                </div>
                                <%
                                    if (!externalItem) {
                                %>
                            </div>                         
                        </div>
                        <% } %> 
                        <% } %>                           
                    </div>
                </div>

                <%
                    // Processing the regItem's fields
                    for (RegFieldmapping regFieldmapping : regFieldmappings) {

                        if ((externalItem && regFieldmapping.getRegField().getIstitle()) || !externalItem) {

                            // Getting the fields localization
                            RegLocalization regFieldLocalization = null;
                            try {
                                regFieldLocalization = regLocalizationManager.get(regFieldmapping.getRegField(), regLanguagecode);
                            } catch (NoResultException e) {
                                regFieldLocalization = regLocalizationManager.get(regFieldmapping.getRegField(), masterLanguage);
                            }
                %>
                <div class="row form-group editing-labels">
                    <label class="col-sm-4"><%=regFieldLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <%
                            if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {
                                if (regFieldmapping.getMultivalue()) {
                        %><ul class="field-list data-list<%=(regFieldmapping.getRequired()) ? " field-list-required" : ""%>"></ul><%
                            }
                        %><a href=\"#\" data-requiredadd="requiredadd" class="btn btn-xs btn-success pull-right btn-parent-add<%=(regFieldmapping.getRequired()) ? " add-required" : ""%>"<%=((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "")%> data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="<%=regLanguagecode.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>="<%=regFieldmapping.getUuid()%>" data-<%= WebConstants.DATA_PARAMETER_ITEMUUID%>="<%= regItem.getUuid()%>" data-<%=BaseConstants.KEY_REQUEST_NEWITEMINSERT%>="<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>"><%=localization.getString("label.add")%></a><%
                        } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)) {
                            List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegistry, regLanguagecode);
                            if (tmpRegLocalizations.isEmpty()) {
                                tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegistry, masterLanguage);
                            }
                            %><a target="_blank" href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemRegistry.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>"><%=tmpRegLocalizations.get(0).getValue()%></a><%
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)) {
                                List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegister, regLanguagecode);
                                if (tmpRegLocalizations.isEmpty()) {
                                    tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegister, masterLanguage);
                                }
                            %><a target="_blank" href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItemRegister.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>"><%=tmpRegLocalizations.get(0).getValue()%></a><%
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_SUCCESSOR) || regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PREDECESSOR)) {
                            %>${localization.getString("label.nosuccessorpredecessor")}<%
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)) {
                                if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                            %>${localization.getString("label.collectionnotapplicable")}<%
                            } else {
                                List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItem, regLanguagecode);
                                if (tmpRegLocalizations.isEmpty()) {
                                    tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItem, masterLanguage);
                                }
                            %><a target="_blank" href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=regItem.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>"><%=tmpRegLocalizations.get(0).getValue()%></a><%
                                }
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)) {
                                if (regFieldmapping.getMultivalue()) {
                            %><ul class="field-list data-list<%=(regFieldmapping.getRequired()) ? " field-list-required" : ""%>"></ul><%
                                }
                            %><a href=\"#\" data-requiredadd="requiredadd" class="btn btn-xs btn-success pull-right btn-relation-add<%=(regFieldmapping.getRequired()) ? " add-required" : ""%>"<%=((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "")%> data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="<%=regLanguagecode.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>="<%=regFieldmapping.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_ITEMUUID%>="<%=regItem.getUuid()%>"><%=localization.getString("label.add")%></a><%
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)) {
                                // Getting the draft status (new proposed items will have the draft status)
                                %><%=JspHelper.getRegStatusHTML(BaseConstants.KEY_STATUS_LOCALID_DRAFT, regStatusManager, regStatuslocalizationManager, regLanguagecode, masterLanguage)%><%
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {
                                String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + "0" + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY;

                                if (regFieldmapping.getRequired()) {
                            %><div class="input-group"><%
                                }
                            %>
                            <select data-live-search="true" class="selectpicker form-control" name="<%= inputName%>"<%=((regFieldmapping.getRequired()) ? " required" : "")%>>
                                <option selected="selected"></option>
                                <%
                                    RegRole regRoleReference = regFieldmapping.getRegField().getRegRoleReference();
                                    List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regRoleReference);
                                    HashMap<String, String> checkDuplicates = new HashMap();

                                    List<RegGroup> regGroups = regGroupManager.getAll();
                                    for (RegGroup tmp : regGroups) {
                                %><option value="<%=tmp.getUuid()%>"><%=tmp.getName()%></option><%
                                    }
                                %>
                            </select>
                            <%
                                if (regFieldmapping.getRequired()) {
                            %>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                            <%
                            %></div><%                                }
                            } else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATECREATION)) {%>                
                            <%-- Do nothing: the date will be automatically created later --%>
                            <%} // Date Edit
                            else if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATEEDIT)) {%>                
                            <%-- Do nothing: the date will be automatically created later --%>

                        <% } else {
                              if (regFieldmapping.getMultivalue()) {
			      %><ul class="field-list data-list<%=(regFieldmapping.getRequired()) ? " field-list-required" : ""%>"></ul>
			      <a href=\"#\" data-requiredadd="requiredadd" class="btn btn-xs btn-success pull-right btn-value-add<%=(regFieldmapping.getRequired()) ? " add-required" : ""%>"<%=((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "")%> data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="<%=regLanguagecode.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>="<%=regFieldmapping.getUuid()%>" data-<%= WebConstants.DATA_PARAMETER_ITEMUUID%>="<%= regItem.getUuid()%>" data-<%=BaseConstants.KEY_REQUEST_NEWITEMINSERT%>="<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>"><%=localization.getString("label.add")%></a><%
                              } else {
                            if (regFieldmapping.getRequired()) {
                        %><div class="input-group"><%
                            }

                            String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + "0" + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY;
                            if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                            %><textarea class="form-control" rows=4" data-<%=WebConstants.DATA_PARAMETER_FIELDVALUEINDEX%>="0" name="<%=inputName%>"<%=((regFieldmapping.getRequired()) ? " required" : "")%>></textarea><%
                            } else {
                            %><input type="<%=JspHelper.handleFieldType(regFieldmapping, localization)%>" class="form-control" data-<%=WebConstants.DATA_PARAMETER_FIELDVALUEINDEX%>="0" value="" name="<%=inputName%>"<%=((regFieldmapping.getRequired()) ? " required" : "")%> maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" /><%
                                }

                                if (regFieldmapping.getRequired()) {
                            %>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div><%
                                }
                            }
                            }
                        %>
                    </div>
                </div>
                <%}
                    }%>

                <%-- Manage RoR export flag in case of Register --%>
                <div class="row form-group editing-labels">
                    <label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" class="col-sm-4">${localization.getString("label.registerfederationexport")}</label>

                    <div class="col-sm-8"> 
                        <input name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT%>" type="checkbox" checked data-toggle="toggle" data-offstyle="secondary" data-size="mini" value=""/>
                    </div>
                    <script>
                        $(function () {
                            $('[data-toggle="toggle"]').bootstrapToggle();
                            $('[data-toggle="toggle"]').change(function () {
                                $(this).val($(this).prop("checked"));
                            });
                        });
                    </script>        
                </div>

                <%if (regFieldmappings.isEmpty()) {
                %><hr/><p><%= MessageFormat.format(localization.getString("label.nofieldsmapped"), "." + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + newRegItemclass.getUuid())%></p><%
                } else {
                %>                
                <hr/>
                <% if (permissionItemRegisterRegistry || permissionRegisterRegistry) {%>
                <div class="row">
                    <div class="col-sm-6"></div>
                    <div class="col-sm-3">
                        <a href=".<%=WebConstants.PAGE_URINAME_BROWSE%>" class="btn btn-secondary width100"><i class="fas fa-ban"></i> ${localization.getString("label.cancel")}</a>
                    </div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
                    </div>
                </div>
                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i>${localization.getString("label.completerequiredfields")}</p>
                    <% }%>
                    <%}%>
                    <%}%>
            </form>

        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            //  Add value button handler
            $(document).on("click", ".btn-value-add", function (e) {
              e.preventDefault();
              var fieldMappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
              getAddValueString(fieldMappingUuid, $(this));
            }); 
            // Add relation button handler
            $(document).on("click", ".btn-relation-add", function (e) {
                e.preventDefault();
                var regFieldmappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
                var regItemUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_ITEMUUID%>");
                getAddRelationString(regFieldmappingUuid, regItemUuid, $(this));
            });
            // Add parent button handler
            $(document).on("click", ".btn-parent-add", function (e) {
                e.preventDefault();
                var regFieldmappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
                var regItemUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_ITEMUUID%>");
                getAddParentString(regFieldmappingUuid, regItemUuid, $(this));
            });

            // Remove value button handler
            $(document).on("click", ".btn-value-remove, .btn-relation-remove", function (e) {
                e.preventDefault();
                var regLocalizationUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID%>");
                removeValue($(this), regLocalizationUuid, false);
            });

            //Hangle the external item checkbox
            $(document).on("change", "#<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>", function (e) {
                e.preventDefault();
                var url = window.location.href;
                if ($(this).prop('checked', true) && url.indexOf('<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>=<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>') < 0) {
                            url += '&<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>=<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>';
                        } else if ($(this).prop('checked', false)) {
                            url = url.replace('&<%=BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM%>=<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>', '');
                        }
                        window.location.href = url;
                    });

            <%-- activating the validator only if the current user can edit --%>
            <% if (permissionItemRegisterRegistry || permissionRegisterRegistry) { %>
                    $('#editing-form').validator();

                    $('form').on('submit', function (e) {
                        if (e.isDefaultPrevented()) {
                            submitted = false;
                        } else {
                            var err = 0;
                            $(".add-required").each(function () {
                                var lis = $(this).parent().find("select");
                                if (lis.length < 1 || lis.val() === undefined || lis.val() === "") {
                                    $(this).parent().find("ul").addClass("has-error");
                                    err++;
                                } else {
                                    $(this).parent().find("ul").removeClass("has-error");
                                }
                            });
                            if (err > 0) {
                                submitted = false;
                                e.preventDefault();
                                $(".form-validation-messages").show();
                                console.log("ccc");
                            } else {
                                submitted = true;
                                $(".form-validation-messages").hide();
                            }
                        }
                    });

            <% }%>
                    $('[data-toggle="tooltip"]').tooltip();
                    CKEDITOR.replaceAll();


                    $(function () {
                        $('.date').datepicker({
                            calendarWeeks: true,
                            todayHighlight: true,
                            autoclose: true
                        });
                    });
        </script>
    </body>
</html>