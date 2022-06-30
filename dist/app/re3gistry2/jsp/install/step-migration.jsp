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

                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="${constants.KEY_REQUEST_MIGRATION_SUMMARY}"/>

                <div class="row mt-5">

                    <div class="col-3">
                        <div id="nav-tabs-wrapper" class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
                            <a class="nav-link disabled" href="#welcome" data-toggle="tab"><i class="fas fa-home"></i> ${localization.getString("installation.steps.welcome")}</a>
                            <a class="nav-link disabled" href="#admin" data-toggle="tab"><i class="fas fa-user-cog"></i> ${localization.getString("installation.steps.adminuser")}</a>
                            <a class="nav-link disabled" href="#profile" data-toggle="tab"><i class="fas fa-check"></i></i> ${localization.getString("installation.steps.profile")}</a>
                            <a class="nav-link active" href="#setup" data-toggle="tab"><i class="fas fa-cogs"></i> ${localization.getString("installation.steps.setup")}</a>
                            <a class="nav-link disabled" href="#summary" data-toggle="tab"><i class="fas fa-list"></i> ${localization.getString("installation.steps.summary")}</a>
                        </div>
                    </div>


                    <div class="col-sm-9">
                        <div class="tab-content">
                            <div role="tabpanel" class="tab-pane fade show active" id="stup">
                                <h3 class="text-primary menu-title">${localization.getString("installation.migration.title")}</h3>
                                <p class="mt-3">${localization.getString("installation.migration.description")}</p>
                                <br/>


                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}">${localization.getString("index.label.migration.dbaddress")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="form-control" required title="${localization.getString("index.label.migration.dbaddress")}" name="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}" id="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBPORT}">${localization.getString("index.label.migration.dbport")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="form-control" required title="${localization.getString("index.label.migration.dbport")}" name="${constants.KEY_REQUEST_MIGRATION_DBPORT}" id="${constants.KEY_REQUEST_MIGRATION_DBPORT}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBNAME}">${localization.getString("index.label.migration.dbname")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group">

                                            <input type="text" class="form-control" required title="${localization.getString("index.label.migration.dbname")}" name="${constants.KEY_REQUEST_MIGRATION_DBNAME}" id="${constants.KEY_REQUEST_MIGRATION_DBNAME}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}">${localization.getString("index.label.migration.dbusername")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="form-control" required title="${localization.getString("index.label.migration.dbusername")}" name="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}" id="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}">${localization.getString("index.label.migration.dbpassword")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="password" class="form-control" required title="${localization.getString("index.label.migration.dbpassword")}" name="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}" id="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt-4">
                                    <div class="col-sm-6 col-md-8">
                                    </div>
                                    <div class="col-sm-6 col-md-4">
                                        <button class="btn btn-primary btn-md width100" disabled="" type="submit"><i class="far fa-save"></i> ${localization.getString("installation.button.saveandcontinue")}</button>
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
            function doCheck() {
                var allFilled = true;
                $('.form-fields:not(#subscription-input)').each(function () {
                    if ($(this).val() === '') {
                        allFilled = false;
                    }
                });

                $('button[type=submit]').prop('disabled', !allFilled);
                if (allFilled) {
                    $('button[type=submit]').removeAttr('disabled');
                }
            }

            $(document).ready(function () {
                doCheck();
                $('.form-fields').keyup(doCheck);
            });
        </script>

    </body>
</html>