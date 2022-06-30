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

            <form class="form-group mt-4" id="target" onsubmit="redirectInstallation()" method="POST">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="${constants.KEY_REQUEST_CLEAN_INSTALLATION_PROFILE}"/>

                <c:set var="installationError" scope="request" value="<%= (String) request.getAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR)%>"/> 
                <c:set var="migrationError" scope="request" value="<%= (String) request.getAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_MIGRATION_ERROR)%>"/> 

                <div class="row mt-5">

                    <div class="col-3">
                        <div id="nav-tabs-wrapper" class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
                            <a class="nav-link disabled" href="#welcome" data-toggle="tab"><i class="fas fa-home"></i> ${localization.getString("installation.steps.welcome")}</a>
                            <a class="nav-link disabled" href="#admin" data-toggle="tab"><i class="fas fa-user-cog"></i> ${localization.getString("installation.steps.adminuser")}</a>
                            <a class="nav-link active" href="#profile" data-toggle="tab"><i class="fas fa-check"></i></i> ${localization.getString("installation.steps.profile")}</a>
                            <a class="nav-link disabled" href="#setup" data-toggle="tab"><i class="fas fa-cogs"></i> ${localization.getString("installation.steps.setup")}</a>
                            <a class="nav-link disabled" href="#summary" data-toggle="tab"><i class="fas fa-list"></i> ${localization.getString("installation.steps.summary")}</a>
                        </div>
                    </div>

                    <div class="col-sm-9">
                        <div class="tab-content">

                            <c:if test = "${not empty installationError}">
                                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                                <div class="alert alert-danger text-center alert-dismissible">
                                    <h3 class="text-danger"><i class="fas fa-exclamation-triangle"></i> ${localization.getString("installation.error")}</h3>
                                    <hr/>
                                    <p>
                                        <button class="btn btn-danger" type="button" data-toggle="collapse" data-target="#collapseInstallation" aria-expanded="false" aria-controls="collapseExample">
                                            <i class="fab fa-readme"></i> ${localization.getString("read.more")}
                                        </button>
                                    </p>
                                    <div class="collapse mt-4" id="collapseInstallation">
                                        <div class="card card-body">
                                            <p>${installationError}</p>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test = "${not empty migrationError}">
                                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                                <div class="alert alert-danger text-center alert-dismissible">
                                    <h3 class="text-danger"><i class="fas fa-exclamation-triangle"></i> ${localization.getString("migration.error")}</h3>
                                    <hr/>

                                    <p>
                                        <button class="btn btn-danger" type="button" data-toggle="collapse" data-target="#collapseMigration" aria-expanded="false" aria-controls="collapseExample">
                                            <i class="fab fa-readme"></i> ${localization.getString("read.more")}
                                        </button>
                                    </p>
                                    <div class="collapse mt-4" id="collapseMigration">
                                        <div class="card card-body">
                                            <p>${migrationError}</p>
                                        </div>
                                    </div>
                                </div>
                            </c:if>


                            <div role="tabpanel" class="tab-pane fade show active" id="profile">
                                <h3 class="text-primary menu-title">${localization.getString("installation.profile.choose.title")}</h3>
                                <p class="mt-3">${localization.getString("installation.profile.choose.description")}</p>

                                <div class="my-4 mt-5 mb-5">
                                    <div class="form-check">
                                        <label class="text-primary">
                                            <input type="radio" name="${constants.KEY_REQUEST_PROFILE}" checked id="${constants.KEY_REQUEST_CLEAN_INSTALLATION_PROFILE}" value="${constants.KEY_REQUEST_CLEAN_INSTALLATION_PROFILE}"> 
                                            <span class="label-text">${localization.getString("installation.installation.title")}</span>
                                        </label>
                                        <p>${localization.getString("installation.profile.cleaninstallation.description")}</p>
                                    </div>
                                    <div class="form-check">
                                        <label class="text-success">
                                            <input type="radio" name="${constants.KEY_REQUEST_PROFILE}" id="${constants.KEY_REQUEST_MIGRATION_PROFILE}" value="${constants.KEY_REQUEST_MIGRATION_PROFILE}">
                                            <span class="label-text">${localization.getString("installation.migration.title")}</span>
                                        </label>
                                        <p>${localization.getString("installation.profile.migration.description")}</p>
                                        <p>${localization.getString("installation.profile.migration.description.note")}</p>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-sm-6 col-md-8">
                                    </div>
                                    <div class="col-sm-6 col-md-4">
                                        <button class="btn btn-primary btn-md width100" id="save" type="submit"><i class="far fa-save"></i> ${localization.getString("installation.button.saveandcontinue")}</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </form>

        </div>

        <%@include file="../includes/footer.inc.jsp" %>
        <%@include file="../includes/pageend.inc.jsp" %>

        <script>
            function redirectInstallation() {
                var radioValue = $("input[name='profile']:checked").val();
                document.getElementById('step').value = radioValue;
            }
        </script>

    </body>
</html>