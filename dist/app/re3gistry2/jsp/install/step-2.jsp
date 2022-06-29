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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="../includes/head.inc.jsp" %>
    <body>
        <%@include file="../includes/header.inc.jsp"%>

        <div class="container mt-5">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("installation.main.title")}</h1>
                </div>
            </div>

            <form action="install" class="form-group mt-4" method="POST">

                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="3"/>

                <div class="row mt-5">

                    <div class="col-3">
                        <div id="nav-tabs-wrapper" class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
                            <a class="nav-link disabled" href="#welcome" data-toggle="tab"><i class="fas fa-home"></i> ${localization.getString("installation.steps.welcome")}</a>
                            <a class="nav-link active" href="#admin" data-toggle="tab"><i class="fas fa-user-cog"></i> ${localization.getString("installation.steps.adminuser")}</a>
                            <a class="nav-link disabled" href="#profile" data-toggle="tab"><i class="fas fa-check"></i></i> ${localization.getString("installation.steps.profile")}</a>
                            <a class="nav-link disabled" href="#setup" data-toggle="tab"><i class="fas fa-cogs"></i> ${localization.getString("installation.steps.setup")}</a>
                            <a class="nav-link disabled" href="#summary" data-toggle="tab"><i class="fas fa-list"></i> ${localization.getString("installation.steps.summary")}</a>
                        </div>
                    </div>

                    <div class="col-9">
                        <div class="tab-content">                           

                            <c:set var="installationError" value="<%= (String) request.getAttribute(constants.KEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR)%>"/>

                            <c:if test="${installationError == constants.KEY_REQUEST_USER_CREATION_ERROR}">
                                <div class=" alert alert-danger"><i class="fas fa-user-times"></i> ${localization.getString("installation.error.creation.user")}</div>
                            </c:if>

                            <div role="tabpanel" class="tab-pane fade show active" id="admin">

                                <h3 class="text-primary">${localization.getString("installation.admin.choose.title")}</h3>

                                <c:choose>
                                    <%-- ECAS --%>
                                    <c:when test="${properties['application.login.type'] == 'ECAS'}">
                                        <p class="mt-3">${localization.getString("installation.admin.ecas.choose.description")}</p>

                                        <div class="my-4 mt-5 mb-5">
                                            <div class="row form-group editing-labels">
                                                <div class="col-sm-5">
                                                    <label class="text-primary" for="${constants.KEY_REQUEST_ADMIN_USERNAME}">
                                                        ${localization.getString("installation.admin.username")}
                                                    </label>
                                                    <p class="gray-text">${localization.getString("installation.admin.ecas.email.description")}</p>
                                                </div>
                                                <div class="col-sm-7">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-at"></i></div></div>
                                                        <input type="email" class="form-control" required title="${localization.getString("installation.admin.username")}" name="${constants.KEY_REQUEST_ADMIN_USERNAME}" id="${constants.KEY_REQUEST_ADMIN_USERNAME}" value="" placeholder="${localization.getString("installation.admin.email.example")}"/>
                                                        <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                            <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="my-4 mt-5 mb-5">
                                            <div class="row form-group editing-labels">
                                                <div class="col-sm-5">
                                                    <label class="text-primary" for="${constants.KEY_REQUEST_ADMIN_SSOREFERENCE}">
                                                        ${localization.getString("label.ssoref")}
                                                    </label>
                                                    <p class="gray-text">${localization.getString("installation.admin.ecas.sso.description")}</p>
                                                </div>
                                                <div class="col-sm-7">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-at"></i></div></div>
                                                        <input type="text" class="form-control" required title="${localization.getString("label.ssoref")}" name="${constants.KEY_REQUEST_ADMIN_SSOREFERENCE}" id="${constants.KEY_REQUEST_ADMIN_SSOREFERENCE}" value="" placeholder="${localization.getString("installation.admin.sso.example")}"/>
                                                        <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                            <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-sm-6 col-md-8">
                                            </div>
                                            <div class="col-sm-6 col-md-4">
                                                <button class="btn btn-primary btn-md width100" type="submit" id="buttonSave"><i class="far fa-save"></i> ${localization.getString("installation.button.saveandcontinue")}</button>
                                            </div>
                                        </div>
                                    </c:when>

                                    <%-- SHIRO --%>
                                    <c:when test="${properties['application.login.type'] == 'SHIRO'}">

                                        <p class="mt-3">${localization.getString("installation.admin.choose.description")}</p>

                                        <div class="my-4 mt-5 mb-5">
                                            <div class="row form-group editing-labels">
                                                <div class="col-sm-5">
                                                    <label class="text-primary" for="${constants.KEY_REQUEST_ADMIN_USERNAME}">
                                                        ${localization.getString("installation.admin.username")}
                                                    </label>
                                                    <p class="gray-text">${localization.getString("installation.admin.email.description")}</p>
                                                </div>
                                                <div class="col-sm-7">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-at"></i></div></div>
                                                        <input type="email" class="form-control" required title="${localization.getString("installation.admin.username")}" name="${constants.KEY_REQUEST_ADMIN_USERNAME}" id="${constants.KEY_REQUEST_ADMIN_USERNAME}" value="" placeholder="${localization.getString("installation.admin.email.example")}"/>
                                                        <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                            <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="row form-group editing-labels">

                                                <div class="col-sm-5">
                                                    <label class="text-primary" for="${constants.KEY_REQUEST_ADMIN_PASSWORD}">
                                                        ${localization.getString("installation.admin.password")}
                                                    </label>
                                                    <p class="gray-text">${localization.getString("installation.admin.password.confirmation.description")}</p>
                                                </div>
                                                <div class="col-sm-7">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-lock"></i></div></div>
                                                        <input type="password" class="form-control" required title="${localization.getString("installation.admin.password")}" name="${constants.KEY_REQUEST_ADMIN_PASSWORD}" id="${constants.KEY_REQUEST_ADMIN_PASSWORD}" value=""  placeholder="${localization.getString("installation.admin.password")}"/>
                                                        <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                            <div class="input-group-prepend">
                                                                <div class="input-group-text">
                                                                    <i class="fas fa-asterisk text-danger" aria-hidden="true"></i>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="row form-group editing-labels">
                                                <div class="col-sm-5">
                                                    <label class="text-primary" for="${constants.KEY_REQUEST_ADMIN_CONFIRM_PASSWORD}">
                                                        ${localization.getString("installation.admin.confirmation.password")}
                                                    </label>
                                                </div>
                                                <div class="col-sm-7">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-lock"></i></div></div>
                                                        <input type="password" class="form-control" required title="${localization.getString("installation.admin.confirmation.password")}" name="${constants.KEY_REQUEST_ADMIN_CONFIRM_PASSWORD}" id="${constants.KEY_REQUEST_ADMIN_CONFIRM_PASSWORD}" value=""  placeholder="${localization.getString("installation.admin.confirmation.password")}" onkeyup="checkPass(); return false;"/>
                                                        <div class="input-group-prepend">
                                                            <div class="input-group-text">
                                                                <i class="fas fa-asterisk text-danger" aria-hidden="true"></i>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <span id="confirmMessage" class="confirmMessage"></span>
                                                </div>
                                            </div>

                                        </div>

                                        <c:if test="${installationError==constants.KEY_REQUEST_USER_CREATION_STARTED}">
                                            <div class="row">
                                                <div class="col-sm-6 col-md-8">
                                                </div>
                                                <div class="col-sm-6 col-md-4">
                                                    <button class="btn btn-primary btn-md width100" type="submit" id="buttonSave" style="display: none;"><i class="far fa-save"></i> ${localization.getString("installation.button.saveandcontinue")}</button>
                                                </div>
                                            </div>
                                        </c:if>

                                    </c:when>        
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

            </form>
        </div>

        <%@include file="../includes/footer.inc.jsp" %>
        <%@include file="../includes/pageend.inc.jsp" %>

        <script>
            function checkPass()
            {
                //Store the password field objects into variables ...
                var pass1 = document.getElementById('<%=BaseConstants.KEY_REQUEST_ADMIN_PASSWORD%>');
                var pass2 = document.getElementById('<%=BaseConstants.KEY_REQUEST_ADMIN_CONFIRM_PASSWORD%>');
                var buttonSave = document.getElementById('buttonSave');
                //Store the Confimation Message Object ...
                var message = document.getElementById('confirmMessage');
                //Set the colors we will be using ...
                var goodColor = "#5cb85c";
                var badColor = "#d9534f";
                //Compare the values in the password field 
                //and the confirmation field
                if (pass1.value === pass2.value) {
                    //The passwords match. 
                    //Set the color to the good color and inform
                    //the user that they have entered the correct password 
                    pass2.style.backgroundColor = goodColor;
                    message.style.color = goodColor;
                    message.innerHTML = "Passwords match!"
                    buttonSave.style.display = 'block';
                } else {
                    //The passwords do not match.
                    //Set the color to the bad color and
                    //notify the user.
                    pass2.style.backgroundColor = badColor;
                    message.style.color = badColor;
                    message.innerHTML = "Passwords do not match!"
                    buttonSave.style.display = 'none';
                }
            }

        </script>

    </body>
</html>