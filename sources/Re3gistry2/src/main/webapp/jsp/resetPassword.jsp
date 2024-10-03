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

<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container mt-5">

            <div class="card card-login mx-auto mt-4">
                <div class="card-body">
                    <form name="resetform" method="post" accept-charset="UTF-8" data-toggle="validator">
                        <fieldset class="align-items-center">
                            
                            <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                            <h1 class="mt-5 mb-5 text-center">${localization.getString("login.reset.password.title")}</h1>
                            <h4 class="mt-5 mb-5 text-center">${localization.getString("login.reset.password.description")}</h4>
                            <hr class="mb-4"/>

                            <%--System messages--%>
                            <c:choose>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_RESETPASSWORD_ERROR}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.reset.password.title")}</h1>
                                        <hr/>
                                        ${localization.getString("error.resetpassword")}
                                    </div>
                                </c:when>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_RESETPASSWORD_USERNOTAVAILABLE_ERROR}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.reset.password.title")}</h1>
                                        <hr/>
                                        ${localization.getString("error.user.notavailable")}
                                    </div>
                                </c:when>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_RESETPASSWORD_NO_CONNECTION_ERROR}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.reset.password.title")}</h1>
                                        <hr/>
                                        ${localization.getString("error.user.noconnection")}
                                    </div>
                                </c:when>
                                <c:when test="${userErrorMessages == constants.KEY_REQUEST_USER_RESETPASSWORD_GENERIC_ERROR}">
                                    <div class=" alert alert-danger text-center">
                                        <h1 class="text-danger text-center">${localization.getString("login.reset.password.title")}</h1>
                                        <hr/>
                                        ${localization.getString("error.user.generic")}
                                    </div>
                                </c:when>
                            </c:choose>
                            

                            <div class="form-group">
                                <label class="text-primary" for="${constants.KEY_FORM_FIELD_NAME_USERNAME}">${localization.getString("login.label.email")}</label>
                                <input required type="email" class="form-control" id="${constants.KEY_FORM_FIELD_NAME_USERNAME}" name="${constants.KEY_FORM_FIELD_NAME_USERNAME}" placeholder="${localization.getString("installation.admin.email.example")}">                            
                            </div>

                            <div class="row mt-5 mb-5">
                                <div class="col-sm-12">
                                    <button type="submit" class="btn btn-primary width100">${localization.getString('login.reset.password.title')}</button>
                                </div>
                            </div>

                        </fieldset>
                    </form>
                </div>
            </div>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

    </body>
</html>