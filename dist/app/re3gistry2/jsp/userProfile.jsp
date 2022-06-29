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
<%@page import="eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping"%>
<%@page import="java.util.Set"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalizationhistory"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemhistory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationhistoryManager"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalizationproposed"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemproposed"%>
<%@page import="javax.persistence.NoResultException"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegStatuslocalization"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegAction"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>

<%    // Instantiating managers
    // Getting system localization
    ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

    // Init the date formatter
    SimpleDateFormat dt = new SimpleDateFormat(systemLocalization.getString("property.dateformat"));

    // Getting the current user from session
    RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);

%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("profile.label.title")}</h1>
                </div>
            </div>

            <%                // Getting feedback messages
                String errorMessage = (String) request.getAttribute(BaseConstants.KEY_REQUEST_ERROR_MESSAGE);
                String successMessage = (String) request.getAttribute(BaseConstants.KEY_REQUEST_USER_SUCCESS_MESSAGES);
                if (errorMessage != null && errorMessage.length() > 0) {
            %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert"><%=errorMessage%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <%
            } else if (successMessage != null && successMessage.length() > 0) {
            %>
            <div class="alert alert-success alert-dismissible fade show" role="alert"><%=successMessage%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <%
                }
            %>

            <form id="editing-form" class="needs-validation mt-3" method="post"  accept-charset="UTF-8" data-toggle="validator" role="form">                
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                
                <input type="hidden" name="<%=constants.KEY_FORM_FIELD_NAME_USERUUID%>" value="<%=currentUser.getUuid()%>" />
                <input type="hidden" name="<%=constants.KEY_FORM_FIELD_NAME_SUBMITACTION%>" value="true" />

                <div class="form-group row editing-labels">
                    <label class="col-sm-4">${localization.getString('label.name')}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="<%=currentUser.getName()%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_USER_NAME%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>                            
                        </div>
                    </div>
                </div>

                <%
                    // The SSO Reference is displayed only in case of Login type ECAS
                    if (currentUser.getSsoreference() != null && configuration.getProperties().getProperty("application.login.type").equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS)) {
                %>
                <div class="form-group row editing-labels">
                    <div class="col-sm-4"><strong>${localization.getString('label.ssoref')}</strong></div>
                    <div class="col-sm-8"><%=(currentUser.getSsoreference() != null) ? currentUser.getSsoreference() : ""%></div>
                </div>
                <%
                    }
                %>
                <div class="form-group row">
                    <div class="col-sm-4"><strong>${localization.getString('label.email')}</strong></div>
                    <div class="col-sm-8"><%= (currentUser.getEmail() != null) ? currentUser.getEmail() : ""%></div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-4"><strong>${localization.getString('label.insertdate')}</strong></div>
                    <div class="col-sm-8"><%= (currentUser.getInsertdate() != null) ? dt.format(currentUser.getInsertdate()) : ""%></div>
                </div>
                <%
                    if (currentUser.getEditdate() != null) {
                %>
                <div class="form-group row">
                    <div class="col-sm-4"><strong>${localization.getString('label.editdate')}</strong></div>
                    <div class="col-sm-8"><%= (currentUser.getEditdate() != null) ? dt.format(currentUser.getEditdate()) : ""%></div>
                </div>            
                <%
                    }
                %>
                <div class="form-group row">
                    <div class="col-sm-4"><strong>${localization.getString('label.status')}</strong></div>
                    <div class="col-sm-8">
                        <%if (currentUser.getEnabled() != null && currentUser.getEnabled()) {%>
                        ${localization.getString("label.enabled")}                    
                        <%} else {%>
                        ${localization.getString("label.disabled")}
                        <%}%>
                    </div>
                </div>

                <div class="form-group row mt-3">
                    <div class="col-sm-6"></div>
                    <div class="col-sm-3">
                        <%
                            // if Login type SHIRO show the button change password
                            if (configuration.getProperties().getProperty("application.login.type").equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO)) {
                        %>
                        <button type="button" class="btn btn-primary btn-submit-action width100" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=currentUser.getUuid()%>"><i class="fas fa-key"></i> ${localization.getString("label.changepassword")}</button><br/>
                        <%
                            }
                        %>
                    </div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.savechanges")}</button>
                    </div>
                </div>
                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

            </form>

            <div class="modal" tabindex="-1" role="dialog" id="submit_action">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">

                        <div class="modal-header">
                            <h5 class="modal-title"><i class="fas fa-key"></i> ${localization.getString('changepassword.title')}</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                        </div>

                        <form method="post">
                            
                            <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                            
                            <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ACTIONTYPE%>" value="<%=constants.KEY_ACTION_TYPE_CHANGEPASSWORD%>" />
                            <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>" value="true" />

                            <div class="modal-body">
                                <div class="row form-group editing-labels">
                                    <label class="col-sm-4 col-form-label" for="${constants.KEY_REQUEST_USER_OLD_PASSWORD}">
                                        ${localization.getString("changepassword.old.password")}
                                    </label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="password" class="form-control" required title="${localization.getString("changepassword.old.password")}" name="${constants.KEY_REQUEST_USER_OLD_PASSWORD}" id="${constants.KEY_REQUEST_USER_OLD_PASSWORD}" value=""/>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                    <span id="confirmCurrentMessage" class="confirmMessage"></span>

                                </div>

                                <div class="row form-group editing-labels">
                                    <label class="col-sm-4 col-form-label" for="${constants.KEY_REQUEST_USER_NEW_PASSWORD}">
                                        ${localization.getString("changepassword.new.password")}
                                    </label>

                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="password" class="form-control" required title="${localization.getString("changepassword.new.password")}" name="${constants.KEY_REQUEST_USER_NEW_PASSWORD}" id="${constants.KEY_REQUEST_USER_NEW_PASSWORD}" value=""/>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row form-group editing-labels">
                                    <label class="col-sm-4 col-form-label" for="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}">
                                        ${localization.getString("changepassword.confirm.new.password")}
                                    </label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="password" class="form-control" required title="${localization.getString("changepassword.confirm.new.password")}" name="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}" id="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}" value=""  onkeyup="checkPass(); return false;"/>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                                            </div>
                                        </div>
                                        <span id="confirmMessage" class="confirmMessage"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="modal-footer">         
                                <div class="col-sm-6">
                                    <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> ${localization.getString('label.cancel')}</button>
                                </div>
                                <div class="col-sm-6">
                                    <button type="submit" disabled="disabled" id="buttonPasswordSave" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString('label.submit')}</button>
                                </div>
                            </div>
                        </form>  
                    </div>
                </div>
            </div>

            <%
                /* Group detail list */
                List<RegUserRegGroupMapping> regUserRegGroupMapping = (List<RegUserRegGroupMapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS);

            %>
            <h3>${localization.getString("label.associatedgroups")}</h3>
            <table id="groups-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.group")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%                        for (RegUserRegGroupMapping tmp : regUserRegGroupMapping) {
                    %>
                    <tr>
                        <td><%=tmp.getRegGroup().getName()%></td>                    
                    </tr> 
                    <%
                        }
                    %>  
                </tbody>
            </table>
            <br/> 
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            $(function () {
                $('[data-toggle="tooltip"]').tooltip();

                $('[data-toggle=confirmation]').confirmation({
                    rootSelector: '[data-toggle=confirmation]'
                });

                $('#groups-table').DataTable({
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "order": [[0, "desc"]]
                });
            });

            $('.btn-submit-action').on('click', function () {
                $('#submit_action').modal();
            });

            function checkPass()
            {
                let pass1 = $('#<%=BaseConstants.KEY_REQUEST_USER_NEW_PASSWORD%>');
                let pass2 = $('#<%=BaseConstants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD%>');
                var message = $('#confirmMessage');

                var goodColor = "#5cb85c";
                var badColor = "#d9534f";

                if (pass1.val() === pass2.val()) {
                    pass2.css('background-color', goodColor);
                    message.css('color', goodColor);
                    message.html("Passwords match!");
                    $('#buttonPasswordSave').prop('disabled', false);
                } else {
                    pass2.css('background-color', badColor);
                    message.css('color', badColor);
                    message.html("Passwords do not match!");
                    $('#buttonPasswordSave').prop('disabled', true);
                }
            }
        </script>
    </body>
</html>