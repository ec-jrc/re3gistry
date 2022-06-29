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
<%@page import="eu.europa.ec.re3gistry2.migration.utility.ItemclassStatistics"%>
<%@page import="eu.europa.ec.re3gistry2.migration.migrationmodel.Registry"%>
<%@page import="eu.europa.ec.re3gistry2.migration.migrationmodel.Register"%>
<%@page import="eu.europa.ec.re3gistry2.migration.migrationmodel.Localization"%>
<%@page import="eu.europa.ec.re3gistry2.migration.utility.RegisterStatistics"%>
<%@page import="eu.europa.ec.re3gistry2.migration.utility.RegistryStatistics"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    String dbAddress = (String) request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS);
    String dbPort = (String) request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPORT);
    String dbName = (String) request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBNAME);
    String dbUsername = (String) request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME);
    String dbPassword = (String) request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD);

    List<RegistryStatistics> registryList = (List<RegistryStatistics>) request.getAttribute(BaseConstants.KEY_REQUEST_REGISTRY_STATISTICS);
    List<RegisterStatistics> registerList = (List<RegisterStatistics>) request.getAttribute(BaseConstants.KEY_REQUEST_REGISTER_LIST_STATISTICS);

%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="../includes/head.inc.jsp" %>
    <body>
        <%@include file="../includes/header.inc.jsp"%>

        <div class="container registry mt-5">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("installation.main.title")}</h1>
                </div>
            </div>


            <div class="row mt-5">

                <div class="col-sm-3">
                    <div id="nav-tabs-wrapper" class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
                        <a class="nav-link disabled" href="#welcome" data-toggle="tab"><i class="fas fa-home"></i> ${localization.getString("installation.steps.welcome")}</a>
                        <a class="nav-link disabled" href="#admin" data-toggle="tab"><i class="fas fa-user-cog"></i> ${localization.getString("installation.steps.adminuser")}</a>
                        <a class="nav-link disabled" href="#profile" data-toggle="tab"><i class="fas fa-check"></i></i> ${localization.getString("installation.steps.profile")}</a>
                        <a class="nav-link disabled" href="#setup" data-toggle="tab"><i class="fas fa-cogs"></i> ${localization.getString("installation.steps.setup")}</a>
                        <a class="nav-link disabled" href="#summary" data-toggle="tab"><i class="fas fa-list"></i> ${localization.getString("installation.steps.summary")}</a>
                        <a class="nav-link active" href="#install" data-toggle="tab"><i class="fas fa-spinner"></i> ${localization.getString("installation.steps.install")}</a>
                    </div>
                </div>


                <div class="col-sm-9">
                    <div class="tab-content">
                        
                         <div role="tabpanel" class="tab-pane fade show active" id="welcome">
                            <h3 class="text-primary">${localization.getString("installation.finalstep.title")}</h3>

                            <div class="row mt-3">

                                <div class="col-sm-12">

                                    <c:set var="user" scope="request" value="<%= (RegUser) session.getAttribute(constants.KEY_SESSION_USER)%>"/> 

                                    <p class="mt-3">${localization.getString("installation.finalstep.description.1")}</p>
                                    <p class="mt-3">${localization.getString("installation.finalstep.description.2")}</p>                                    
                                    <p class="mt-3">${localization.getString("installation.finalstep.description.3")} <span class="text-primary">${user.getEmail()}</span> ${localization.getString("installation.finalstep.description.4")}</p>                                    
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


        </div>



        <%@include file="../includes/footer.inc.jsp" %>
        <%@include file="../includes/pageend.inc.jsp" %>

        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.3/css/bootstrap-select.min.css">

        <!-- Latest compiled and minified JavaScript -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.3/js/bootstrap-select.min.js"></script>

        <script>
            // Starting the migration and redirecting to the waiting page
            $('#start-procedure').on('click', function (e) {
                e.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: './<%=WebConstants.PAGE_URINAME_INSTALL%>',
                    data: {
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS%>': '<%=dbAddress%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBPORT%>': '<%=dbPort%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBNAME%>': '<%=dbName%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME%>': '<%=dbUsername%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD%>': '<%=dbPassword%>',
                        '<%=BaseConstants.KEY_REQUEST_STEP%>': '<%=BaseConstants.KEY_REQUEST_MIGRATION_PROCESS%>'
                    },
                    success: function (res) {
                    }
                });
                // Redirect to the waiting page
                location.href = '';
            });
        </script>

    </body>
</html>