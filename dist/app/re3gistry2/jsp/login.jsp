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
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<jsp:useBean id="webconstants" class="eu.europa.ec.re3gistry2.base.utility.WebConstants" scope="session"/>

<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container mt-5">

            <%-- Depending on the login type, there are different cases for the UI --%>
            <c:set var="userErrorMessages" scope="request" value="${requestScope.userErrorMessages}"/>

            <c:choose>

                <%-- ECAS --%>
                <c:when test="${properties['application.login.type'] == 'ECAS'}">
                    <div class="col-2 mt-4"><a></a></div>
                    <div class="col-8 card card-login-ecas mx-auto mt-4">
                        <fieldset class="align-items-center">

                            <h1 class="mt-5 mb-5 text-center">${localization.getString("login.ecas.label.title")}</h1>

                            <%-- ECAS login error messages --%>
                            <c:choose>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_NOT_AVAILABLE}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                        <hr/>
                                        ${localization.getString("login.text.error.usernotavailable")}
                                    </div>
                                </c:when>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_NOT_ENABLED}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                        <hr/>
                                        ${localization.getString("login.text.error.usernotenabled")}
                                    </div>
                                </c:when>
                            </c:choose>

                            <%-- Installation success message --%>
                            <c:set var="installationSuccess" scope="request" value="${sessionScope.installationSuccess}"/>
                            <c:if test="${installationSuccess == constants.KEY_REQUEST_INSTALLATION_SUCCESS}">
                                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                                <div class="alert alert-success text-center  alert-dismissible">
                                    <h1 class="text-success text-center">${localization.getString("installation.success.login.congratulations")}</h1>
                                    <hr/>
                                    <p>${localization.getString("installation.success.login")}</p>
                                </div>
                            </c:if>

                            <p>${localization.getString('login.text.loginmessage')}</p>

                            <div class="row mt-5 mb-5">
                                <div class="col-sm-12">
                                    <a class="btn btn-primary width100" href="${webconstants.PAGE_URINAME_INDEX.replace("/", "")}">${localization.getString('login.label.ecaslogin')}</a>
                                </div>
                            </div>

                        </fieldset>
                    </div>
                    <div class="col-2 mt-4"><a></a></div>
                        </c:when>

                <%-- SHIRO --%>
                <c:when test="${properties['application.login.type'] == 'SHIRO'}">                    
                    <div class="col-2 mt-4"><a></a></div>
                    <div class="col-8 card mx-auto mt-4">
                        <form name="loginform" method="post" accept-charset="UTF-8" data-toggle="validator">
                            <fieldset class="align-items-center">
                                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                                <h1 class="mt-5 mb-5 text-center">${localization.getString("login.label.title")}</h1>

                                <%--SHIRO messages--%>
                                <c:choose>
                                    <c:when test="${simpleShiroApplicationLoginFailure == 'org.apache.shiro.authc.IncorrectCredentialsException'}">
                                        <div class=" alert alert-danger">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.credentialerror")}
                                        </div>
                                    </c:when>
                                    <c:when test="${simpleShiroApplicationLoginFailure == 'org.apache.shiro.authc.UnknownAccountException'}">
                                        <div class=" alert alert-danger">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.unknownaccount")}
                                        </div>
                                    </c:when>
                                    <c:when test="${simpleShiroApplicationLoginFailure == 'org.apache.shiro.authc.LockedAccountException'}">
                                        <div class=" alert alert-danger">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.lockedAccountException")}
                                        </div>
                                    </c:when>
                                </c:choose>                     

                                <%--System messages--%>
                                <c:choose>
                                    <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_UNKNOWN_ACCOUNT_EXCEPTION}">
                                        <div class=" alert alert-danger text-center">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.unknownaccount")}
                                        </div>
                                    </c:when>
                                    <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION}">
                                        <div class=" alert alert-danger text-center">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.credentialerror")}
                                        </div>
                                    </c:when>
                                    <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_LOCKED_ACCOUNT_EXCEPTION}">
                                        <div class=" alert alert-danger text-center">
                                            <h1 class="text-danger text-center">${localization.getString("login.text.error.signin.faild")}</h1>
                                            <hr/>
                                            ${localization.getString("login.text.error.lockedAccountException")}
                                        </div>
                                    </c:when>
                                </c:choose>

                                <%-- Reset password success message --%>
                                <c:set var="resetPasswordSuccess" scope="request" value="${sessionScope.resetPasswordSuccess}"/>
                                <c:if test="${resetPasswordSuccess == constants.KEY_REQUEST_USER_RESETPASSWORD_SUCCESS}">
                                    <div class=" alert alert-success text-center">
                                        <h1 class="text-success text-center">${localization.getString("login.reset.password.title")}</h1>
                                        <hr/>
                                        ${localization.getString("success.resetpassword")}
                                    </div>
                                </c:if>

                                <%-- Installation success message --%>
                                <c:set var="installationSuccess" scope="request" value="${sessionScope.installationSuccess}"/>
                                <c:if test="${installationSuccess == constants.KEY_REQUEST_INSTALLATION_SUCCESS}">
                                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                                    <div class="alert alert-success text-center  alert-dismissible">
                                        <h1 class="text-success text-center">${localization.getString("installation.success.login.congratulations")}</h1>
                                        <hr/>
                                        <p>${localization.getString("installation.success.login")}</p>
                                    </div>
                                </c:if>

                                <div class="form-group">
                                    <label class="sr-only" for="${constants.KEY_FORM_FIELD_NAME_USERNAME}">${localization.getString("login.label.username")}</label>
                                    <input required type="email" class="form-control" id="${constants.KEY_FORM_FIELD_NAME_USERNAME}" name="${constants.KEY_FORM_FIELD_NAME_USERNAME}" placeholder="${localization.getString("installation.admin.email.example")}">                            
                                </div>

                                <div class="form-group mt-3">
                                    <label class="sr-only" for="${constants.KEY_FORM_FIELD_NAME_USERNAME}">${localization.getString("login.label.password")}</label>
                                    <input required type="password" class="form-control" id="${constants.KEY_FORM_FIELD_NAME_PASSWORD}" name="${constants.KEY_FORM_FIELD_NAME_PASSWORD}" placeholder="${localization.getString("login.text.placeholder.password")}">
                                </div>

                                <div class="row mt-5 mb-5">
                                    <div class="col-sm-7">
                                        <a href=".<%=WebConstants.PAGE_URINAME_RESETPASSWORD%>" class="btn btn-link" role="button" aria-pressed="true">
                                            ${localization.getString('login.forgot.password')}
                                        </a>
                                    </div>
                                    <div class="col-sm-5">
                                        <button type="submit" class="btn btn-primary width100">${localization.getString('login.label.signin')}</button>
                                    </div>
                                </div>

                                <%--<p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>--%>

                            </fieldset>
                        </form>
                    </div>
                    <div class="col-2 mt-4"><a></a></div>
                        </c:when>
                    </c:choose>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

    </body>
</html>