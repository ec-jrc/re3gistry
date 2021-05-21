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
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegUser"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Checking if the current user has the rights to perfor mactions on RegItemclasses
    String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
    boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row mb-3">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("itemclass.label.title")}</h1>
                </div>
            </div>

            <div class="alert alert-success alert-dismissible d-none alert-wrong-order" role="alert">
                ${localization.getString("label.successreordering")}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>

            <%                String operationError = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);
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

            <!--<h2 class="mt-3">${localization.getString("label.registrycontentclasslist")}</h2>-->
            <table id="itemclass-list-table" class="table table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.procedureorder")} *</th>
                        <th>${localization.getString("label.localid")}</th>
                        <th>${localization.getString("label.type")}</th>
                        <th>${localization.getString("label.parent")}</th>
                        <th>${localization.getString("label.baseuri")}</th>
                        <th>${localization.getString("label.creationdate")}</th>
                        <th>${localization.getString("label.actions")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<RegItemclass> regItemclasses = (List) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES);
                        for (RegItemclass tmpRegItemclass : regItemclasses) {
                    %>
                    <tr data-<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>="<%=tmpRegItemclass.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_PARENTITEMCLASSPROCEDUREORDER%>="<%=(tmpRegItemclass.getRegItemclassParent() != null) ? tmpRegItemclass.getRegItemclassParent().getDataprocedureorder() : ""%>" class="table-color-<%=tmpRegItemclass.getRegItemclasstype().getLocalid()%>">
                        <td><%=tmpRegItemclass.getDataprocedureorder()%></td>
                        <td><a href=".<%=WebConstants.PAGE_URINAME_FIELD%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=<%=tmpRegItemclass.getUuid()%>&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}"><%=tmpRegItemclass.getLocalid()%></a></td>
                        <td><%=tmpRegItemclass.getRegItemclasstype().getLocalid()%></td>
                        <td><%=(tmpRegItemclass.getRegItemclassParent() != null) ? tmpRegItemclass.getRegItemclassParent().getLocalid() : ""%></td>
                        <td><%=(tmpRegItemclass.getBaseuri() != null) ? tmpRegItemclass.getBaseuri() : ""%></td>
                        <td><%=tmpRegItemclass.getInsertdate()%></td>
                        <td>
                            <ul>
                                <li>
                                    <a class="text-primary" href=".<%=WebConstants.PAGE_URINAME_FIELD%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=<%=tmpRegItemclass.getUuid()%>&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}"><i class="far fa-eye" title="${localization.getString("label.listfields")}"></i></a>
                                        <% if (permissionRegisterRegistry) { %>
                                        <% if (tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {%>
                                        <%--<li><a href=".<%=WebConstants.PAGE_URINAME_ADDITEMCLASS%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=<%=tmpRegItemclass.getUuid() %>&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}">${localization.getString("label.addregisteritemclass")}</a></li>--%>
                                        <%}%>
                                        <% if (tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {%>
                                        <%--<li><a href=".<%=WebConstants.PAGE_URINAME_ADDITEMCLASS%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=<%=tmpRegItemclass.getUuid() %>&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}">${localization.getString("label.additemitemclass")}</a></li>--%>
                                        <%}%>
                                        <% if (tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM) && regItemclassManager.getChildItemclass(tmpRegItemclass).size() == 0) {%>
                                    <a class="text-success" href=".<%=WebConstants.PAGE_URINAME_ADDITEMCLASS%>?${constants.KEY_REQUEST_ITEMCLASSUUID}=<%=tmpRegItemclass.getUuid()%>&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}"><i class="fas fa-plus" title="${localization.getString("label.addchilditemitemclass")}"></i></a>
                                        <% }%>
                                    <a class="edit-itemclass text-primary" data-<%=BaseConstants.KEY_FORM_FIELD_NAME_DATA_ITEMCLASSUUID%>=<%=tmpRegItemclass.getUuid()%> href="#"><i class="far fa-edit" title="${localization.getString("label.edit")}"></i></a>
                                    <a class="text-danger btn-approve-action btn-reg-action" data-toggle="confirmation" data-title="${localization.getString("discard.contentclass.confirm")}" data-placement="left" data-singleton="true" href=".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=tmpRegItemclass.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTION%>=<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE%>"><i class="far fa-trash-alt" title="${localization.getString("label.remove")}"></i></a>
                                </li>
                                <% } %>
                            </ul>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>

            <p>&nbsp;</p> 
            <h5>${localization.getString("label.note")}</h5>    
            <p>${localization.getString("label.procedureorder.notice")}</p> 
            <p>&nbsp;</p>
            <h5>${localization.getString("label.contenttype.legend")}</h5>
            <p>${localization.getString("label.contenttype.legend.registry")}</p>
            <p>${localization.getString("label.contenttype.legend.register")}</p>
            <p>${localization.getString("label.contenttype.legend.item")}</p>

            <%-- if (permissionRegisterRegistry) { %>
            <a class="btn btn-success" href=".<%=WebConstants.PAGE_URINAME_ADDITEMCLASS%>?${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}">${localization.getString("label.additemclass")}</a>
            <% } --%>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            var itemClassTable;
            $(function () {
            <%-- Init the DataTable for the item class list --%>
            dtOprions = {
            dom: '<"top">rt<"bottom"lip><"clear">',
                    order: [[0, "asc"]],
                    pageLength: 10,
            <%-- If the user has the rights to update the procedure order 
            of the RegItemclass, activating the function --%>
            <% if (permissionRegisterRegistry) { %>
            rowReorder: true,
                    columnDefs: [{orderable: true, className: 'reorder', targets: 0, type: 'num-fmt'
                    }
                    ],
            <% } %>
            columns: [{width: "7%"}, {width: "15%"}, {width: "7%"}, {width: "15%"}, {width: "20%"}, null, {width: "10%"}],
            }

            itemClassTable = $('#itemclass-list-table').DataTable(dtOprions);
            <%-- If the user has the rights, activating the update of the 
            procedure order on drag & drop --%>
            <% if (permissionRegisterRegistry) {%>
            itemClassTable.on('row-reordered', function (e, diff, edit) {
            if ($(diff[0].node).data('<%=WebConstants.DATA_PARAMETER_PARENTITEMCLASSPROCEDUREORDER%>') < diff[0].newPosition) {
            //Updating the dragged item
            $.ajax({
            url: ".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=" + $(diff[0].node).data('<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>') + "&<%=BaseConstants.KEY_REQUEST_NEWPOSITION%>=" + diff[0].newPosition
            }).done(function () {
            });
                    //Updating the substituted item
                    $.ajax({
                    url: ".<%=WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=" + $(diff[1].node).data('<%=WebConstants.DATA_PARAMETER_ITEMCLASSUUID%>') + "&<%=BaseConstants.KEY_REQUEST_NEWPOSITION%>=" + diff[1].newPosition
                    }).done(function () {
            $(".alert-wrong-order").removeClass("hidden");
            });
            } else {
            $('#errorModal').modal('toggle');
                    $('#errorModal').on('hidden.bs.modal', function (e) {
            location.reload();
            });
            }
            });
            <% }%>

            <%-- Init the confirm message --%>
            $('[data-toggle=confirmation]').confirmation({
            rootSelector: '[data-toggle=confirmation]'
            });
            });
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

        <!-- Modal Error -->
        <div class="modal fade" id="errorModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="myModalLabel">${localization.getString("label.error")}</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>                        
                    </div>
                    <div class="modal-body">
                        ${localization.getString("error.wrongorder")}
                    </div>
                    <div class="modal-footer">
                        <div class="col-sm-6"></div>
                        <div class="col-sm-6">
                            <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> ${localization.getString("label.close")}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Edit -->
        <div class="modal fade" id="editModal" tabindex="-1" role="dialog">
            <form id="edit-form" action=".<%=WebConstants.PAGE_URINAME_EDITITEMCLASS%>">

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

        <%-- Custom libraries for this page --%>
        <%-- If the user has the rights to update the procedure order of the 
        RegItemclass, loading the rowReorder plugin for DataTables --%>
        <% if (permissionRegisterRegistry) { %>
        <link rel="stylesheet" href="res/lib/DataTables/RowReorder-1.2.4/css/rowReorder.bootstrap4.min.css">
        <script src="res/lib/DataTables/RowReorder-1.2.4/js/dataTables.rowReorder.min.js" defer></script>
        <% }%>

        <script src="./res/js/bootstrap-confirmation.min.js"></script>
    </body>
</html>