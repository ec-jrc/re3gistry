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
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.ResourceBundle"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Getting the system localization
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Initializing managers
    RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

    String[] activeLanguageCodes = (String[]) request.getParameterValues(BaseConstants.KEY_REQUEST_ACTIVE_LANGUAGECODES);
    String masterLanguageCode = (String) request.getParameter(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);

    String registryLocalID = (String) request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_LOCALID);
    String registryBaseURI = (String) request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_BASEURI);
    String registryLabel = (String) request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_LABEL);
    String registryContentSummary = (String) request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY);

//    RegUser regUser1 = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);
    
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

                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="${constants.KEY_REQUEST_CLEAN_INSTALLATION_PROCESS}"/>

                <div class="row mt-5">

                    <div class="col-3">
                        <div id="nav-tabs-wrapper" class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
                            <a class="nav-link disabled" href="#welcome" data-toggle="tab"><i class="fas fa-home"></i> ${localization.getString("installation.steps.welcome")}</a>
                            <a class="nav-link disabled" href="#admin" data-toggle="tab"><i class="fas fa-user-cog"></i> ${localization.getString("installation.steps.adminuser")}</a>
                            <a class="nav-link disabled" href="#profile" data-toggle="tab"><i class="fas fa-check"></i></i> ${localization.getString("installation.steps.profile")}</a>
                            <a class="nav-link disabled" href="#setup" data-toggle="tab"><i class="fas fa-cogs"></i> ${localization.getString("installation.steps.setup")}</a>
                            <a class="nav-link active" href="#summary" data-toggle="tab"><i class="fas fa-list"></i> ${localization.getString("installation.steps.summary")}</a>
                        </div>
                    </div>

                    <div class="col-sm-9">
                        <div class="tab-content columnbottom">
                            <div role="tabpanel" class="tab-pane fade show active" id="summary">
                                <h1 class="text-primary menu-title">${localization.getString("installation.cleaninstallation.title.summary")}</h1>
                                <h3>${localization.getString("installation.cleaninstallation.languages.description")}</h3>
                                <br/>

                                <div class="row form-group editing-labels" id="systemlanguagediv">
                                    <div class="col-sm-4"><label>${localization.getString("installation.choose.system.language")}</label></div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <%--<%=regLanguagecodeManager.getByIso6391code(masterLanguageCode).getLabel()%>--%> 
                                            <%for (String iso6391code : activeLanguageCodes) {
                                            %>

                                            <%=regLanguagecodeManager.getByIso6391code(iso6391code).getLabel()%> | 
                                            <%
                                                }
                                            %>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels" id="masterlanguagediv">
                                    <div class="col-sm-4"><label>${localization.getString("installation.master.language")}</label></div>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <p><%=regLanguagecodeManager.getByIso6391code(masterLanguageCode).getLabel()%></p>
                                        </div>
                                    </div>
                                </div>

                                <br/>
                                <h3>${localization.getString("installation.cleaninstallation.fileds.description")}</h3>
                                <p>${localization.getString("installation.cleaninstallation.configuration.new.registry.description")}</p>
                                <br/>

                                <h1><%=registryLabel%></h1>
                                <hr/>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_ID}">${localization.getString("installation.clean.registry.id")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group  width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("installation.clean.registry.id")}" name="${constants.KEY_REQUEST_REGISTRY_ID}" id="${constants.KEY_REQUEST_REGISTRY_ID}" value="<%=registryBaseURI%>/<%=registryLocalID%>" placeholder=""/>
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_LABEL}">${localization.getString("installation.clean.registry.label")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group  width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("installation.clean.registry.label")}" name="${constants.KEY_REQUEST_REGISTRY_LABEL}" id="${constants.KEY_REQUEST_REGISTRY_LABEL}" value="<%=registryLabel%>" />
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}">${localization.getString("installation.clean.registry.content.summary")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <textarea class="form-control borderradius5" disabled=""required title="${localization.getString("installation.clean.registry.content.summary")}" name="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}" id="${constants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY}" rows="5"><%=registryContentSummary%></textarea>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-sm-6 col-md-8">
                                    </div>
                                    <div class="col-sm-6 col-md-4">
                                        <button class="btn btn-primary btn-md width100" type="submit"><i class="far fa-play-circle"></i> ${localization.getString("installation.button.saveandstart.system")}</button>
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