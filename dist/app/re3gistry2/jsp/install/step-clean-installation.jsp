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
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Getting objects from the request
    List<RegLanguagecode> regLanguagecodeList = (List<RegLanguagecode>) request.getAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES);

%>
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

                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="${constants.KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY}"/>

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
                                <h1 class="text-primary menu-title">${localization.getString("installation.cleaninstallation.title")}</h1>
                                <p class="mt-4">${localization.getString("installation.cleaninstallation.configuration.description")}</p>
                                <h3>${localization.getString("installation.cleaninstallation.languages.description")}</h3>
                                <br/>

                                <div class="row form-group editing-labels" id="systemlanguagediv">
                                    <div class="col-sm-6">
                                        <label>${localization.getString("installation.choose.system.language")}</label>
                                        <p class="gray-text">${localization.getString("installation.choose.system.language.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <select id="systemlanguage" required class="selectpicker form-control" multiple name="<%=BaseConstants.KEY_REQUEST_ACTIVE_LANGUAGECODES%>" data-live-search="true" title="${localization.getString("installation.choose.system.languages")}" data-actions-box="true" multiple>

                                                <%
                                                    for (RegLanguagecode tmpRegItemclass : regLanguagecodeList) {
                                                %>
                                                <option value="<%=tmpRegItemclass.getUuid()%>" data-reference="<%=tmpRegItemclass.getLabel()%>"><%=tmpRegItemclass.getLabel()%> </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels" id="masterlanguagediv">
                                    <div class="col-sm-6">
                                        <label>${localization.getString("installation.master.language")}</label>
                                        <p class="gray-text">${localization.getString("installation.choose.master.language.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <select id="masterlanguagediv" required class="selectpicker form-control" name="<%=BaseConstants.KEY_REQUEST_MASTERLANGUAGE%>" data-live-search="true" title="${localization.getString("installation.choose.master.language")}" data-actions-box="true">

                                                <%
                                                    for (RegLanguagecode tmpRegItemclass : regLanguagecodeList) {
                                                %>
                                                <option value="<%=tmpRegItemclass.getUuid()%>" data-reference="<%=tmpRegItemclass.getLabel()%>"><%=tmpRegItemclass.getLabel()%> </option>

                                                <%
                                                    }
                                                %>
                                            </select>
                                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <br/>
                                <h3>${localization.getString("installation.cleaninstallation.fileds.description")}</h3>
                                <br/>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-6">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_BASEURI}">${localization.getString("installation.clean.registry.baseuri")}</label>
                                        <p class="gray-text">${localization.getString("installation.clean.registry.baseuri.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <input type="url" class="form-control" required title="${localization.getString("installation.clean.registry.baseuri")}" name="${constants.KEY_REQUEST_REGISTRY_BASEURI}" id="${constants.KEY_REQUEST_REGISTRY_LOCALID}" value=""  placeholder="http://example.com"/>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-6">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_LOCALID}">${localization.getString("installation.clean.registry.localid")}</label>
                                        <p class="gray-text">${localization.getString("installation.clean.registry.localid.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <input type="text" class="form-control" required title="${localization.getString("installation.clean.registry.localid")}" name="${constants.KEY_REQUEST_REGISTRY_LOCALID}" id="${constants.KEY_REQUEST_REGISTRY_LOCALID}" value="" placeholder="Add a string without white spaces"/>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-6">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_LABEL}">${localization.getString("installation.clean.registry.label")}</label>
                                        <p class="gray-text">${localization.getString("installation.clean.registry.label.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <input type="text" class="form-control" required title="${localization.getString("installation.clean.registry.label")}" name="${constants.KEY_REQUEST_REGISTRY_LABEL}" id="${constants.KEY_REQUEST_REGISTRY_LABEL}" value="" />
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-6">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}">${localization.getString("installation.clean.registry.content.summary")}</label>
                                        <p class="gray-text">${localization.getString("installation.clean.registry.content.summary.description")}</p>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="input-group">
                                            <textarea class="form-control" required title="${localization.getString("installation.clean.registry.content.summary")}" name="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}" id="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}" rows="5"></textarea>
                                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-sm-6 col-md-8">
                                    </div>
                                    <div class="col-sm-6 col-md-4">
                                        <button class="btn btn-primary btn-md width100" type="submit"><i class="far fa-save"></i> ${localization.getString("installation.button.saveandcontinue")}</button>
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
            $("input#registryLocalID").on({
                keydown: function (e) {
                    if (e.which === 32)
                        return false;
                },
                change: function () {
                    this.value = this.value.replace(/\s/g, "");
                }
            });

        </script>

    </body>
</html>