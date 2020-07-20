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
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclasstype"%>
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
<%    // Setting system localization
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Initializing managers
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegGroupManager regGroupManager = new RegGroupManager(entityManager);

    // Getting objects from the request
    RegItem registryRegItem = (RegItem) request.getAttribute(BaseConstants.KEY_REQUEST_REGISTRYITEM);
    RegItemclass registryRegItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_REGISTRYITEMCLASS);

    String operationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);

    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
    boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("addRegister.label.title")}</h1>
                </div>
            </div>

            <% if (operationSuccess != null) {%>
            <div class="alert alert-danger alert-dismissible mt-3" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <%=operationSuccess%>
            </div>
            <% }%>

            <%-- Editing form --%>
            <form id="editing-form" method="post" accept-charset="utf-8" role="form" class="mt-3">

                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <%-- Showing the registry in which you are inserting a register --%>    
                <div class="row form-group editing-labels" title="${localization.getString("registry.tooltip")}">
                    <label class="col-sm-4">${localization.getString("label.registry")}</label>
                    <div class="col-sm-8">
                        <input type="hidden" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_UUID%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_UUID%>" value="<%=registryRegItem.getUuid()%>" />
                        <input type="hidden" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID%>" value="<%=registryRegItemclass.getUuid()%>" />
                        <%= registryRegItemclass.getBaseuri() + '/' + registryRegItemclass.getLocalid()%>
                    </div>
                </div>

                <%-- Showing the list of itemclass --%>         
                <div class="row form-group editing-labels" title="${localization.getString("registry.path.tooltip")}">
                    <label class="col-sm-4" for="<%=BaseConstants.KEY_FORM_FIELD_NAME_KEEPPATH%>">${localization.getString("label.keeppath")}</label>
                    <div class="col-sm-8">
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_KEEPPATH%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_KEEPPATH%>" />
                            </label>
                        </div>

                        <input type="hidden" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_BASEURI%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_BASEURI%>" value="<%=registryRegItemclass.getBaseuri()%>" />
                        <input type="hidden" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_LOCALID%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_LOCALID%>" value="<%=registryRegItemclass.getLocalid()%>" />

                    </div>
                </div>

                <div class="row form-group editing-labels" title="${localization.getString("registry.localid.tooltip")}">
                    <label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" class="col-sm-4">${localization.getString("label.localid")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input id="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" data-toggle="tooltip" data-placement="top" title="<%=localization.getString("label.fielduriinfo")%>" type="text" class="form-control input-uri" value="" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <hr/>

                <div class="row form-group editing-labels alert alert-warning" role="alert">
                    <label class="col-sm-4 mt-2" for="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>">${localization.getString("label.uripreview")}</label>
                    <div class="col-sm-8 mt-2">
                        <span id="uripreview"></span>
                        <input type="hidden" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTER_BASEURI%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTER_BASEURI%>" value="" />
                    </div>
                </div>

                <hr/>

                <p><%=MessageFormat.format(localization.getString("addItem.text.insertinmaserlanguage"), masterLanguage.getIso6391code())%></p>

                <p>&nbsp;</p>

                <%-- Showing the label field --%>
                <div class="row form-group editing-labels" title="${localization.getString("registry.label.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField labelRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
                        RegLocalization labelLocalization = regLocalizationManager.get(labelRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID%>" class="col-sm-4"><%= labelLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input id="<%=BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID%>" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="" name="<%=BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Showing the contentsummary field --%>
                <div class="row form-group editing-labels" title="${localization.getString("registry.contentsummary.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField contentSummaryRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID);
                        RegLocalization contentSummaryLocalization = regLocalizationManager.get(contentSummaryRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID%>" class="col-sm-4"><%=contentSummaryLocalization.getValue()%></label>
                    <div class="col-sm-8">                            
                        <textarea id="<%=BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID%>" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="" name="<%=BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID%>"></textarea>
                    </div>
                </div>

                <%-- Showing the register ownerr field and selector --%>                            
                <div class="row form-group editing-labels" title="${localization.getString("registry.registerowner.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField registerOwnerRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER);
                        RegLocalization registerOwnerLocalization = regLocalizationManager.get(registerOwnerRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER%>" class="col-sm-4"><%= registerOwnerLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select data-live-search="true" class="selectpicker form-control" name="<%=BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER%>" required>
                                <option selected="selected"></option>
                                <%
                                    List<RegGroup> regGroups = regGroupManager.getAll();
                                    for (RegGroup tmp : regGroups) {
                                %><option value="<%=tmp.getUuid()%>"><%=tmp.getName()%></option><%
                                    }
                                %>
                            </select>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>


                <%-- Showing the register manager field and selector --%>                            
                <div class="row form-group editing-labels" title="${localization.getString("registry.registermanager.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField registerManagerRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER);
                        RegLocalization registerManagerLocalization = regLocalizationManager.get(registerManagerRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER%>" class="col-sm-4"><%= registerManagerLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select data-live-search="true" class="selectpicker form-control" name="<%=BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER%>" required>
                                <option selected="selected"></option>
                                <%
                                    for (RegGroup tmp : regGroups) {
                                %><option value="<%=tmp.getUuid()%>"><%=tmp.getName()%></option><%
                                    }
                                %>
                            </select>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Showing the control body field and selector --%>                            
                <div class="row form-group editing-labels"  title="${localization.getString("registry.controlbody.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField controlBodyRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY);
                        RegLocalization controlBodyLocalization = regLocalizationManager.get(controlBodyRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY%>" class="col-sm-4"><%= controlBodyLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select data-live-search="true" class="selectpicker form-control" name="<%=BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY%>" required>
                                <option selected="selected"></option>
                                <%
                                    for (RegGroup tmp : regGroups) {
                                %><option value="<%=tmp.getUuid()%>"><%=tmp.getName()%></option><%
                                    }
                                %>
                            </select>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Showing the control body field and selector --%>                            
                <div class="row form-group editing-labels"  title="${localization.getString("registry.submittingorganization.tooltip")}">
                    <%
                        //Getting the label RegField and its localization
                        RegField submittingOrganizationRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS);
                        RegLocalization submittingOrganizationLocalization = regLocalizationManager.get(submittingOrganizationRegField, currentLanguage);
                    %>
                    <label for="<%=BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS%>" class="col-sm-4"><%= submittingOrganizationLocalization.getValue()%></label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select multiple data-live-search="true" class="selectpicker form-control" name="<%=BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS%>" required>
                                <%
                                    for (RegGroup tmp : regGroups) {
                                %><option value="<%=tmp.getUuid()%>"><%=tmp.getName()%></option><%
                                    }
                                %>
                            </select>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>


                <%-- If the user has the rights to insert a new RegItemclass, showing 
                the save button --%>
                <% if (permissionRegisterRegistry) {%>
                <div class="row mt-3">
                    <div class="col-sm-6"></div>
                    <div class="col-sm-3">
                        <a href=".<%=WebConstants.PAGE_URINAME_BROWSE%>" class="btn btn-secondary width100"><i class="fas fa-ban"></i> ${localization.getString("label.cancel")}</a>                        
                    </div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
                    </div>
                </div>
                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>
                <% }%>

                <script>
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>, #<%=BaseConstants.KEY_FORM_FIELD_NAME_KEEPPATH%>').on('keyup change paste', function (e) {
                        var registrybaseuri = $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_BASEURI%>');
                        var registrylocalid = $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTRY_LOCALID%>');
                        var registerbaseuri = $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_REGISTER_BASEURI%>');
                        var registerlocalid = $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>');
                        var uripreview = $('#uripreview');
                        var outregisterbaseuri = '';
                        if ($('#<%=BaseConstants.KEY_FORM_FIELD_NAME_KEEPPATH%>').prop('checked')) {
                            outregisterbaseuri = registrybaseuri.val() + '/' + registrylocalid.val();
                        } else {
                            outregisterbaseuri = registrybaseuri.val();
                        }
                        registerbaseuri.val(outregisterbaseuri);
                        uripreview.html(outregisterbaseuri + '/' + registerlocalid.val());
                    });
                </script>
            </form>     

            <%-- Handling the various itemclasstypes/icemclass relation and constraints --%> 
            <script>
                $('#editing-form').on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        submitted = false;
                    } else {
                        itemclass_t = $('#itemclass_type');
                        p_itemclass = $('#parent_itemclass');
                        itemclass_t.prop('disabled', false);
                        p_itemclass.prop('disabled', false);
                        submittted = true;
                    }
                });

                $(function () {
                    $('#editing-form').validator();
                    CKEDITOR.replaceAll();
                    $('[data-toggle="tooltip"]').tooltip();

                    $('.date').datepicker({
                        calendarWeeks: true,
                        todayHighlight: true,
                        autoclose: true
                    });
                });
            </script>            
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
    </body>
</html>