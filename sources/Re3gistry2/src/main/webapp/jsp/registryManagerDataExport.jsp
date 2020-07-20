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
<%@page import="eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler"%>
<%@page import="java.util.Properties"%>
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

<%    // Getting languages
    RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);

    // Instantiating managers
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
                    <h1 class="page-heading">${localization.getString("registrymanager.label.title")}</h1>
                </div>
            </div>

            <div class="mt-3 mb-4">
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER%>" role="tab">${localization.getString("label.actions")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>" role="tab">${localization.getString("label.users")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>" role="tab">${localization.getString("label.groups")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link active" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT%>" role="tab">${localization.getString("label.dataexport")}</a></li>
                </ul>
            </div>

            <%
                // Getting the configuration
                Properties properties = Configuration.getInstance().getProperties();
                String isSolrActive = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_ACTIVE);

                String buttonDisabled = (SolrHandler.checkSolrCompleteIndexinglRunning()) ? " disabled" : "";

                if (isSolrActive.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            %>

            <div class="card">
                <div class="card-header">
                    <i class="fab fa-searchengin"></i> ${localization.getString("label.solr")}
                </div>
                <div class="card-body">
                    <h5 class="card-title"></h5>
                    <p class="card-text">${localization.getString("label.solrdescription")}</p>
                    <% if (SolrHandler.checkSolrCompleteIndexinglRunning()) { %>
                    <p class="card-text mt-3 alert alert-warning">${localization.getString("label.solrrunning")}</p>
                    <% } else {%>
                    <a id="startSolrIndexing" class="btn btn-primary btn-md<%=buttonDisabled%>" href="?<%=BaseConstants.KEY_REQUEST_STARTINDEX%>=<%=BaseConstants.KEY_BOOLEAN_STRING_TRUE%>" role="button">${localization.getString("label.solrstartindexing")}</a>
                    <script>
                        $('#startSolrIndexing').on('click',function(){
                            $(this).addClass('disabled');
                            setTimeout(function(){ 
                                location.reload();
                            }, 3000);                            
                        });
                    </script>
                    <% } %>
                </div>
            </div>
            <%
                }
            %>


        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
        <script src="./res/js/bootstrap-confirmation.min.js"></script>

        <script>
            <%-- Init the confirm message --%>
            $('[data-toggle=confirmation]').confirmation({
                rootSelector: '[data-toggle=confirmation]'
            });
        </script>

    </body>
</html>