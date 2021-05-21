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
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldtype"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldmapping"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Initializing managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

    // Getting objects from the request
    RegLanguagecode regLanguagecode = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    RegField regField = (RegField) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELD);
    RegItemclass regItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS);

    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
    int childItemClassSize = regItemclassManager.getChildItemclass(regItemclass).size();

    // Checking if the current user has the rights to add a new itemclass
    String[] actionManageFieldMapping = {BaseConstants.KEY_USER_ACTION_MANAGEFIELDMAPPING};
    boolean permissionManageFieldMapping = UserHelper.checkGenericAction(actionManageFieldMapping, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("contentclass.label.title")}</h1>
                </div>
            </div>

            <ol class="breadcrumb breadcrumb-in-page mb-3" vocab="http://schema.org/" typeof="BreadcrumbList">
                <li property="itemListElement" typeof="ListItem">
                    <a property="item" typeof="WebPage" href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS%>"><span property="name">${localization.getString('menu.itemclass.label')}</span></a>
                </li>
                <li property="itemListElement" typeof="ListItem">
                    <span property="item" typeof="WebPage"><span property="name">${regItemclass.localid}</span></span>
                </li>
            </ol>

            <%
                String operationError = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);
                if (operationError != null) {
            %>
            <div class="alert alert-danger alert-dismissible" role="alert">
                <%=operationError%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <%
                }
            %>

            <%
                String operationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE);
                if (operationSuccess != null) {
            %>
            <div class="alert alert-success alert-dismissible" role="alert">
                <%=operationSuccess%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <%
                }
            %>

            <div class="row">
                <div class="col-sm-9">
                    <h1>${regItemclass.localid}</h1>   
                </div>
                <div class="col-sm-3">
                    <a href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS%>" class="btn btn-light float-right width100" role="button" aria-pressed="true">
                        <i class="fas fa-long-arrow-alt-left"></i> ${localization.getString("label.backto")} ${localization.getString('menu.itemclass.label')}
                    </a>
                </div>
            </div>

            <div class="row mt-3">

                <c:choose>
                    <c:when test="${regItemclass.regItemclasstype.localid eq BaseConstants.KEY_ITEMCLASS_TYPE_ITEM}">

                        <%if (childItemClassSize != 0) {%>
                        <div class="col-sm-6">
                            <a class="btn btn-primary edit-itemclass width100" data-<%=BaseConstants.KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID%>=${regItemclass.uuid} href="#"><i class="far fa-edit" title="${localization.getString("label.edit")}"></i> ${localization.getString("label.edit")}</a>
                        </div>
                        <div class="col-sm-6">
                            <a class="btn btn-danger btn-approve-action btn-reg-action width100" data-toggle="confirmation" data-title="${localization.getString("discard.contentclass.confirm")}" data-placement="left" data-singleton="true" href=".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=${regItemclass.uuid}&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD%>=<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD%>"><i class="far fa-trash-alt" title="${localization.getString("label.remove")}"></i> ${localization.getString("label.remove")}</a>
                        </div>
                        <%} else {%>
                        <div class="col-sm-4">
                            <a class="btn btn-success width100" href=".<%=WebConstants.PAGE_URINAME_ADDITEMCLASS%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=${regItemclass.uuid}&${constants.KEY_REQUEST_LANGUAGEUUID}=${regLanguagecode.uuid}"><i class="fas fa-plus" title="${localization.getString("label.addchilditemitemclass")}"></i> ${localization.getString("label.addchilditemitemclass")}</a>
                        </div>
                        <div class="col-sm-4">
                            <a class="btn btn-primary edit-itemclass width100" data-<%=BaseConstants.KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID%>=${regItemclass.uuid} href="#"><i class="far fa-edit" title="${localization.getString("label.edit")}"></i> ${localization.getString("label.edit")}</a>
                        </div>
                        <div class="col-sm-4">
                            <a class="btn btn-danger btn-approve-action btn-reg-action width100" data-toggle="confirmation" data-title="${localization.getString("discard.contentclass.confirm")}" data-placement="left" data-singleton="true" href=".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=${regItemclass.uuid}&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD%>=<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD%>"><i class="far fa-trash-alt" title="${localization.getString("label.remove")}"></i> ${localization.getString("label.remove")}</a>
                        </div>
                        <%}%>


                    </c:when>
                    <c:otherwise>
                        <div class="col-sm-6">
                            <a class="btn btn-primary edit-itemclass width100" data-<%=BaseConstants.KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID%>=${regItemclass.uuid} href="#"><i class="far fa-edit" title="${localization.getString("label.edit")}"></i> ${localization.getString("label.edit")}</a>
                        </div>
                        <div class="col-sm-6">
                            <a class="btn btn-danger btn-approve-action btn-reg-action width100" data-toggle="confirmation" data-title="${localization.getString("discard.contentclass.confirm")}" data-placement="left" data-singleton="true" href=".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=${regItemclass.uuid}&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE%>"><i class="far fa-trash-alt" title="${localization.getString("label.remove")}"></i> ${localization.getString("label.remove")}</a>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

            <hr/>

            <%-- if this is a request for a Field detail, showing the details --%>
            <%if (regField != null) {%>
            <!-- Language tab -->
            <div class="mb-5">
                <!-- Nav tabs -->
                <ul class="nav nav-tabs mb-3" role="tablist">
                    <c:forEach var="languageCode" items="${languageCodes}" varStatus="loop">
                        <c:choose>
                            <c:when test="${(not empty currentLanguage) && (currentLanguage.iso6391code == languageCode.iso6391code) }">
                                <li role="presentation" class="nav-item"><a class="nav-link active" href="#" data-<%=WebConstants.DATA_PARAMETER_LANGUAGEUUID%>="${languageCode.iso6391code}" role="tab" data-target="#AJtab" data-toggle="tabajax">${languageCode.iso6391code}</a></li>
                                </c:when>
                                <c:otherwise>
                                <li role="presentation" class="nav-item"><a class="nav-link" href="#" data-<%=WebConstants.DATA_PARAMETER_LANGUAGEUUID%>="${languageCode.iso6391code}" role="tab" data-target="#AJtab" data-toggle="tabajax">${languageCode.iso6391code}</a></li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                </ul>
                <!-- Tab panes -->
                <div class="tab-content" style="position:relative">
                    <div class="loader_c"><div class="loader"></div></div>
                    <div role="tabpanel" class="tab-pane active" id="AJtab"></div>
                </div>
                <hr/>
            </div>
            <%}%>

            <div class="row">
                <div class="col-sm-9">
                    <h2>${localization.getString("label.showfieldforitemclass")} ${regItemclass.regItemclasstype.localid} ${regItemclass.localid}</h2>
                </div>
                <div class="col-sm-3">
                    <%-- If the user has the rights to map RegFields, showing
      the map button --%>
                    <% if (permissionManageFieldMapping) {%>
                    <a href=".<%=WebConstants.PAGE_URINAME_MAPFIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>" class="btn btn-success width100"><i class="fas fa-plus"></i> ${localization.getString("label.field.map")}</a>
                    <% } %>
                </div>
            </div>
            <div class="alert alert-warning mt-3" role="alert">
                ${localization.getString("label.changesautosaved")}
            </div>

            <c:if test="${not empty regFieldmappings}">

                <table id="field-list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>${localization.getString("label.listorder")}</th>
                            <th>${localization.getString("label.label")}</th>
                            <th>${localization.getString("label.type")}</th>
                            <th>${localization.getString("label.ishyperlinked")}</th>
                            <th>${localization.getString("label.hidden")}</th>
                            <th>${localization.getString("label.multivalued")}</th>
                            <th>${localization.getString("label.required")}</th>
                            <th>${localization.getString("label.tablevisible")}</th>
                            <th>${localization.getString("label.actions")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<RegFieldmapping> regFieldMapping = (List<RegFieldmapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGS);
                            for (RegFieldmapping tmpRegFieldmpping : regFieldMapping) {
                        %>
                        <tr data-<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>="<%=tmpRegFieldmpping.getRegItemclass().getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_FIELDUUID%>="<%=tmpRegFieldmpping.getRegField().getUuid()%>" >
                            <%
                                RegLocalization regLocalization = regLocalizationManager.get(tmpRegFieldmpping.getRegField(), masterLanguage);
                                String currentRegFieldtypeLocalId = tmpRegFieldmpping.getRegField().getRegFieldtype().getLocalid();
                                // Disable the checkbox for the fields that does not need that specific property
                                boolean hideChecks = false;
                                if (currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_GROUP)
                                        || currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)
                                        || currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)
                                        || currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)
                                        || currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_PREDECESSOR)
                                        || currentRegFieldtypeLocalId.equals(BaseConstants.KEY_FIELD_TYPE_SUCCESSOR)) {
                                    hideChecks = true;
                                }
                                //Disabling the checkbox if the user has not the rights to change it
                                boolean hideChecksPermission = false;
                                if (!permissionManageFieldMapping) {
                                    hideChecksPermission = true;
                                }

                                // Hide Table visible for the register (the registry has more Itemclasses contained).
                                // In this case the register are listed in the teble just with the label.
                                boolean hideTableVisible = false;
                                if (regItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                                    hideTableVisible = true;
                                }

                                boolean hideUnMap = false;
                                // Avoid the edit and unmap if the field is a label or status
                                if (tmpRegFieldmpping.getRegField().getLocalid().equals(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID) || tmpRegFieldmpping.getRegField().getLocalid().equals(BaseConstants.KEY_FIELD_MANDATORY_STATUS_LOCALID)) {
                                    hideChecksPermission = true;
                                    hideUnMap = true;
                                }
                            %>
                            <td><%=tmpRegFieldmpping.getListorder()%></td>
                            <td><a href=".<%=WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>&<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=<%=tmpRegFieldmpping.getRegField().getUuid()%>"><%=regLocalization.getValue()%></a></td>
                            <td><%=tmpRegFieldmpping.getRegField().getRegFieldtype().getLocalid()%></td>
                            <td><input class="cbUpdate" data-type="<%=BaseConstants.KEY_FIELD_CHECKBOX_TYPE_HREF%>" <%=(hideChecks || hideChecksPermission) ? "disabled " : ""%>type="checkbox"<%if (tmpRegFieldmpping.getHashref()) {%> checked<%}%>></td>
                            <td><input class="cbUpdate" data-type="<%=BaseConstants.KEY_FIELD_CHECKBOX_TYPE_HIDDEN%>" <%--<%=(hideChecksPermission) ? "disabled " : ""%>--%>type="checkbox"<%if (tmpRegFieldmpping.getHidden()) {%> checked<%}%>></td>
                            <td><input class="cbUpdate" data-type="<%=BaseConstants.KEY_FIELD_CHECKBOX_TYPE_MULTIVALUED%>" <%=(hideChecks || hideChecksPermission) ? "disabled " : ""%>type="checkbox"<%if (tmpRegFieldmpping.getMultivalue()) {%> checked<%}%>></td>
                            <td><input class="cbUpdate" data-type="<%=BaseConstants.KEY_FIELD_CHECKBOX_TYPE_REQUIRED%>" <%=(hideChecks || hideChecksPermission) ? "disabled " : ""%>type="checkbox"<%if (tmpRegFieldmpping.getRequired()) {%> checked<%}%>></td>
                            <td><input class="cbUpdate" data-type="<%=BaseConstants.KEY_FIELD_CHECKBOX_TYPE_TABLEVISIBLE%>" <%=(hideTableVisible) ? "disabled " : ""%>type="checkbox"<%if ((tmpRegFieldmpping.getTablevisible() && !hideTableVisible) || (tmpRegFieldmpping.getTablevisible() && hideTableVisible && tmpRegFieldmpping.getRegField().getIstitle())) {%> checked<%}%>></td>
                            <td>
                                <% if (permissionManageFieldMapping && !hideUnMap) {%>
                                <a class="text-danger btn-approve-action btn-reg-action" data-toggle="confirmation" data-title="${localization.getString("discard.field.confirm")}" data-placement="left" data-singleton="true" href=".<%=WebConstants.PAGE_URINAME_MAPFIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>&<%=BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID%>=<%=tmpRegFieldmpping.getUuid()%>"><i class="far fa-trash-alt"  title="${localization.getString("label.field.unmap")}"></i></a>
                                    <% }%>
                                <a class="text-success" href=".<%=WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>&<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=<%=tmpRegFieldmpping.getRegField().getUuid()%>"><i class="far fa-edit"  title="${localization.getString("label.edit")}"></i></a>
                            </td>
                        </tr>
                        <%}%>
                    </tbody>
                </table>

                <!-- Modal Edit -->
                <div class="modal fade" id="editModal" tabindex="-1" role="dialog">
                    <form id="edit-form" method="post" action=".<%=WebConstants.PAGE_URINAME_EDITITEMCLASS%>">

                        <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD_FROM_CONTENTCLASS%>" value="true" />
                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LANGUAGEUUID%>" value="<%=regLanguagecode.getUuid()%>" />

                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title"><i class="far fa-edit"></i> ${localization.getString("label.edit")}</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>                            
                                </div>
                                <div class="modal-body">
                                    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>" id="<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>" />
                                    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ACTION%>" id="<%=BaseConstants.KEY_REQUEST_ACTION%>" value="<%=BaseConstants.KEY_REQUEST_TYPE_EDIT%>" />
                                    <div class="form-group">
                                        <label for="recipient-name" class="control-label">${localization.getString("label.localid")}</label>
                                        <input type="text" class="form-control" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>">
                                    </div>
                                    <div class="form-group">
                                        <label for="message-text" class="control-label">${localization.getString("label.baseuri")}</label>
                                        <input type="text" class="form-control" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>">
                                    </div>
                                    <div class="alert alert-danger alert-dismissible alert-itemclass-not-editable hidden" role="alert">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                        ${localization.getString("error.itemclass.noteditable")}
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class="col-sm-6">
                                        <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> ${localization.getString("label.close")}</button>
                                    </div>
                                    <div class="col-sm-6">
                                        <button type="submit" class="btn btn-success btn-save width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>


                <script>
                    var fieldsTable;
                    $(function(){
                    <%-- Init the DataTable for the field list --%>
                    fieldsTable = $('#field-list-table').DataTable({
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                            order: [[0, "asc"]],
                            pageLength: 10,
                    <%-- If the user has the rights to update the list order
                    of the RegField, activating the function --%>
                    <% if (permissionManageFieldMapping) { %>
                    rowReorder: true,
                            columnDefs: [{orderable: true, className: 'reorder', targets: 0, type: 'num-fmt' }],
                    <% } %>
                    "columns": [null, { "width": "16%" }, null, null, null, null, null, null, null],
                    'drawCallback': function () {
                    <%-- Init the update on checkbox (if the user has the right) --%>
                    <% if (permissionManageFieldMapping) {%>
                    $(".cbUpdate").on('change', function(){
                    var itemclassUuid = $(this).parent().parent().data("<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>");
                    var fieldUuid = $(this).parent().parent().data("<%=WebConstants.DATA_PARAMETER_FIELDUUID%>");
                    var checkboxType = $(this).data("type");
                    var checked = $(this).is(":checked");
                    $.get(".<%=WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=" + fieldUuid + "&<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=" + itemclassUuid + "&<%=BaseConstants.KEY_REQUEST_CHECKBOXTYPE%>=" + checkboxType + "&<%=BaseConstants.KEY_REQUEST_CHECKED%>=" + checked, function(data) {});
                    });
                    <% } %>
                    }
                    });
                    <%-- If the user has the rights, activating the update of the
                    list order on drag & drop --%>
                    <% if (permissionManageFieldMapping) {%>
                    fieldsTable.on('row-reordered', function (e, diff, edit) {
                    for (var i = 0, ien = diff.length; i < ien; i++) {
                    $.ajax({
                    url: ".<%=WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=" + $(diff[i].node).data('<%=WebConstants.DATA_PARAMETER_FIELDUUID%>') + "&<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=" + $(diff[i].node).data('<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>') + "&<%=BaseConstants.KEY_REQUEST_NEWPOSITION%>=" + diff[i].newPosition
                    }).done(function() {});
                    }
                    });
                    <% } %>

                    <%-- Loading the tab in case of request for RegField detail --%>
                    <% if (regField != null) {%>
                    $.get(".<%=WebConstants.PAGE_URINAME_FIELDLOADER%>?<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=" + fieldUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>", function (data) {
                    $("#AJtab").html(data);
                    $('#editing-form').validator('update');
                    $(".loader_c").hide();
                    });
                    <% } %>

                    <%-- Init the confirm message --%>
                    $('[data-toggle=confirmation]').confirmation({
                    rootSelector: '[data-toggle=confirmation]'
                    });
                    <%-- Init the update on checkbox (if the user has the right) --%>
                    <% if (permissionManageFieldMapping) {%>
                    $(".cbUpdate").on('change', function(){
                    var itemclassUuid = $(this).parent().parent().data("<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>");
                    var fieldUuid = $(this).parent().parent().data("<%=WebConstants.DATA_PARAMETER_FIELDUUID%>");
                    var checkboxType = $(this).data("type");
                    var checked = $(this).is(":checked");
                    $.get(".<%=WebConstants.PAGE_URINAME_FIELD%>?<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=" + fieldUuid + "&<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=" + itemclassUuid + "&<%=BaseConstants.KEY_REQUEST_CHECKBOXTYPE%>=" + checkboxType + "&<%=BaseConstants.KEY_REQUEST_CHECKED%>=" + checked, function(data) {});
                    });
                    <% } %>
                    });
                    <%-- Handling the click on a tab in case of request for RegField detail --%>
                    <% if (regField != null) {%>
                    var fieldUuid = "<%=regField.getUuid()%>";
                    // Ajax Tabs
                    $('[data-toggle="tabajax"]').click(function(e) {
                    <%-- Check if the content has been edited --%>
                    var check = unloadCheck();
                    var cnfr = true;
                    if (check){
                    $(this).blur();
                    cnfr = confirm(unloadCheck());
                    }
                    if (cnfr){
                    unsaved = false;
                    $(".loader_c").height($("#AJtab").height());
                    $(".loader_c").show();
                    var $this = $(this),
                            loadurl = ".<%=WebConstants.PAGE_URINAME_FIELDLOADER%>?<%=BaseConstants.KEY_REQUEST_FIELDUUID%>=" + fieldUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=" + $this.attr('data-languageUuid') + "&<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>",
                            targ = $this.attr('data-target');
                    $.get(loadurl, function(data) {
                    $(targ).html(data);
                    $('#editing-form').validator('update');
                    $(".loader_c").hide();
                    });
                    $this.tab('show');
                    }
                    return false;
                    });
                    <% } %>
                </script>
            </c:if>
            <c:if test="${empty regFieldmappings}">
                <% if (permissionManageFieldMapping) {%>
                <p>This Content class has no fields mapped. You can start mapping fields using the buffon below.</p>
                <a href=".<%=WebConstants.PAGE_URINAME_MAPFIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=regLanguagecode.getUuid()%>" class="btn btn-success"><i class="fas fa-plus"></i> ${localization.getString("label.field.map")}</a>
                <% } %>
            </c:if>
        </div>
        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <%-- Custom libraries for this page --%>
        <script src="res/js/bootstrap-confirmation.min.js"></script>
        <%-- If the user has the rights to update the list order of the
        RegField, loading the rowReorder plugin for DataTables --%>
        <% if (permissionManageFieldMapping) { %>
        <link rel="stylesheet" href="res/lib/DataTables/RowReorder-1.2.4/css/rowReorder.bootstrap4.min.css">
        <script src="res/lib/DataTables/RowReorder-1.2.4/js/dataTables.rowReorder.min.js" defer></script>
        <% }%>

        <script>
                    $(".edit-itemclass").on('click', function (e) {
                    e.preventDefault();
                    itemclassUuid = $(this).data('<%=BaseConstants.KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID%>');
                    $.ajax({
                    url: '.<%=WebConstants.PAGE_URINAME_EDITITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=' + itemclassUuid
                    }).done(function (data) {
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>').removeAttr("disabled");
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>').removeAttr("disabled");
                    $('.btn-save').removeAttr("disabled");
                    $('.alert-itemclass-not-editable').addClass('hidden');
                    $('#<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_UUID%>);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_LOCALID%>);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_BASEURI%>);
                    console.log(data.<%=BaseConstants.KEY_JSON_FIELDS_NOTEDITABLE%> === true);
                    if (data.<%=BaseConstants.KEY_JSON_FIELDS_NOTEDITABLE%> === true) {
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>').attr("disabled", "disabled");
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>').attr("disabled", "disabled");
                    $('.btn-save').attr("disabled", "disabled");
                    $('.alert-itemclass-not-editable').removeClass('hidden');
                    } else if (data.<%=BaseConstants.KEY_JSON_FIELDS_ITEMCLASSTYPE%> === '<%=BaseConstants.KEY_ITEMCLASS_TYPE_ITEM%>') {
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>').attr("disabled", "disabled");
                    }
                    $('#editModal').modal('toggle');
                    });
                    });
        </script>

    </body>
</html>