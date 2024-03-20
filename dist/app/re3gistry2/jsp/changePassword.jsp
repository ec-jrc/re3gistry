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
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.text.MessageFormat"%>
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

<%    // Checking if the current user has the rights to add a new itemclass
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);
    String codeQueryParam = request.getQueryString();

    String codeValue = null;
    if (codeQueryParam != null && codeQueryParam.startsWith("code=")) {
        codeValue = codeQueryParam.substring(5);
    }


%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp" %>
        <div class="container mt-5">
            <c:set var="userErrorMessages" scope="request" value="${requestScope.userErrorMessages}"/>
            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("changePassword.label.title")}</h1>
                </div>
            </div>
            <c:choose>
                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION}">
                    <div class=" alert alert-danger text-center">
                        <h1 class="text-danger text-center">${localization.getString("changePassword.text.error")}</h1>
                        <hr/>
                        ${localization.getString("changePassword.text.error.body")}
                    </div>
                </c:when>
            </c:choose>
            <form action="activate" class="form-group mt-4" method="POST">
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                <input type="hidden" required name="${constants.KEY_FIELD_TYPE_NUMBER}" id="${constants.KEY_FIELD_TYPE_NUMBER}" value="<%= codeValue%>"/>
                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="4"/>

                <p class="mt-3">${localization.getString("changePassword.body")}</p>

                <div class="my-4 mt-5 mb-5">
                    <div class="row form-group editing-labels">
                        <div class="col-sm-5">
                            <label class="text-primary"  for="${constants.KEY_REQUEST_USER_OLD_PASSWORD}">
                                ${localization.getString("changePassword.label.currentPassword")}
                            </label>
                            <p class="gray-text">${localization.getString("changePassword.label.message")}</p>
                        </div>
                        <div class="col-sm-7">
                            <div class="input-group">
                                <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-lock"></i></div></div>
                                <input type="password" class="form-control" required title="${localization.getString("changePassword.label.currentPassword")}" name="${constants.KEY_REQUEST_USER_OLD_PASSWORD}" id="${constants.KEY_REQUEST_USER_OLD_PASSWORD}" value="" placeholder="${localization.getString("changePassword.label.currentPassword")}"/>
                                <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text">
                                            <i class="fas fa-asterisk text-danger" aria-hidden="true"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <span id="wrongPassword" class="wrongPassword"></span>
                        </div>
                    </div>
                    <div class="row form-group editing-labels">
                        <div class="col-sm-5">
                            <label class="text-primary" for="${constants.KEY_REQUEST_USER_NEW_PASSWORD}">
                                ${localization.getString("changePassword.label.newPassword")}
                            </label>
                            <p class="gray-text">${localization.getString("changePassword.label.passwordText")}</p>
                        </div>
                        <div class="col-sm-7">
                            <div class="input-group">
                                <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-lock"></i></div></div>
                                <input type="password" class="form-control" required title="${localization.getString("changePassword.label.newPassword")}" name="${constants.KEY_REQUEST_USER_NEW_PASSWORD}" id="${constants.KEY_REQUEST_USER_NEW_PASSWORD}" value="" placeholder="${localization.getString("changePassword.label.newPassword")}"/>
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
                            <label class="text-primary" for="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}">
                                ${localization.getString("changePassword.label.confirmNewPassword")}
                            </label>
                        </div>
                        <div class="col-sm-7">
                            <div class="input-group">
                                <div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-lock"></i></div></div>
                                <input type="password" class="form-control" required title="${localization.getString("changePassword.label.confirmNewPassword")}" name="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}" id="${constants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD}" value=""  placeholder="${localization.getString("changePassword.label.confirmNewPassword")}" onkeyup="checkPass(); return false;"/>
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

                <div class="row">
                    <div class="col-sm-6 col-md-8">
                    </div>
                    <div class="col-sm-6 col-md-4">
                        <button class="btn btn-primary btn-md width100" type="submit" id="buttonSave" style="display: none;">
                            <i class="far fa-save"></i> ${localization.getString("changePassword.label.title")}
                        </button>
                    </div>
                </div>
            </form>
        </div>

        <script>
            function checkPass() {
                // JavaScript function to check password match and strength
                var pass1 = document.getElementById('<%=BaseConstants.KEY_REQUEST_USER_NEW_PASSWORD%>');
                var pass2 = document.getElementById('<%=BaseConstants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD%>');
                var pass3 = document.getElementById('<%=BaseConstants.KEY_REQUEST_USER_OLD_PASSWORD%>');
                var buttonSave = document.getElementById('buttonSave');
                var message = document.getElementById('confirmMessage');
                var wrongPassword = document.getElementById('wrongPassword');
                var goodColor = "#5cb85c";
                var badColor = "#d9534f";
                var minLength = 8;
                var regex = /^(?=.*[A-Za-z])(?=.*\d).+$/;
                if (pass1.value === pass2.value) {
                    if (pass1.value.length >= minLength && regex.test(pass1.value)) {
                        pass2.style.backgroundColor = goodColor;
                        message.style.color = goodColor;
                        message.innerHTML = "Passwords match and meet the strength criteria!";
                        buttonSave.style.display = 'block';
                    } else {
                        pass2.style.backgroundColor = badColor;
                        message.style.color = badColor;
                        message.innerHTML = "Passwords match, but the password should have a minimum of 8 characters with at least one letter and one number.";
                        buttonSave.style.display = 'none';
                    }
                } else {
                    pass2.style.backgroundColor = badColor;
                    message.style.color = badColor;
                    message.innerHTML = "Passwords do not match!";
                    buttonSave.style.display = 'none';
                }

                


            }
        </script>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
    </body>
</html>
