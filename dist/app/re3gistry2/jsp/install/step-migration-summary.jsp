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

        <div class="container mt-5">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("installation.main.title")}</h1>
                </div>
            </div>

            <form id="migrate" class="form-group mt-4" method="POST">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                
                <input type="hidden" required name="${constants.KEY_REQUEST_STEP}" id="${constants.KEY_REQUEST_STEP}" value="${constants.KEY_REQUEST_MIGRATION_PROCESS}"/>

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
                        <div class="tab-content">
                            <div role="tabpanel" class="tab-pane fade show active" id="summary">
                                <h1 class="text-primary menu-title">${localization.getString("installation.migration.summary.title")}</h1>
                                <h3 class="mt-4">${localization.getString("installation.migration.summary.database")}</h3>
                                <br/>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}">${localization.getString("index.label.migration.dbaddress")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("index.label.migration.dbaddress")}" name="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}" id="${constants.KEY_REQUEST_MIGRATION_DBADDRESS}" value="<%=dbAddress%>" />
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBPORT}">${localization.getString("index.label.migration.dbport")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group  width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("index.label.migration.dbport")}" name="${constants.KEY_REQUEST_MIGRATION_DBPORT}" id="${constants.KEY_REQUEST_MIGRATION_DBPORT}" value="<%=dbPort%>" />
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBNAME}">${localization.getString("index.label.migration.dbname")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group  width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("index.label.migration.dbname")}" name="${constants.KEY_REQUEST_MIGRATION_DBNAME}" id="${constants.KEY_REQUEST_MIGRATION_DBNAME}" value="<%=dbName%>" />
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}">${localization.getString("index.label.migration.dbusername")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required title="${localization.getString("index.label.migration.dbusername")}" name="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}" id="${constants.KEY_REQUEST_MIGRATION_DBUSERNAME}" value="<%=dbUsername%>" />
                                        </div>
                                    </div>
                                </div>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label for="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}">${localization.getString("index.label.migration.dbpassword")}</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <input type="password" class="form-control borderradius5" disabled="" required title="${localization.getString("index.label.migration.dbpassword")}" name="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}" id="${constants.KEY_REQUEST_MIGRATION_DBPASSWORD}" value="<%=dbPassword%>" />
                                        </div>
                                    </div>
                                </div>

                                <h3 class="mt-4">${localization.getString("installation.migration.summary.content")}: <%=dbName%></h3>

                                <%
                                    for (RegistryStatistics tmpRegItemclass : registryList) {
                                %>

                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label>Registry name</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required value="<%=((Localization) tmpRegItemclass.getRegistryLocalization()).getLabel()%>" />
                                        </div>
                                    </div>
                                </div>
                                <div class="row form-group editing-labels">
                                    <div class="col-sm-4">
                                        <label>Registry URI</label>
                                    </div>
                                    <div class="col-sm-8">
                                        <div class="input-group width100">
                                            <input type="text" class="form-control borderradius5" disabled="" required value="<%=((Registry) tmpRegItemclass.getRegistry()).getBaseuri()%>/<%=((Registry) tmpRegItemclass.getRegistry()).getUriname()%>" />
                                        </div>
                                    </div>
                                </div>

                                <%
                                    }
                                %>

                                <h3 class="mt-3">${localization.getString("installation.migration.summary.register.belonging")}</h3>

                                <div class="row">
                                    <%
                                        for (RegisterStatistics tmpRegItemclass : registerList) {
                                    %>
                                    <div class="col-md-6 col-sm-6 pl-2 pr-2">
                                        <div class="plan color-1 columnstatistics">

                                            <div class="plan-statistics">
                                                <h3 class="dont-break-out"><%=((Localization) tmpRegItemclass.getRegisterLocalization()).getLabel()%></h3>
                                            </div>
                                            <div class="plan-features">
                                                <p class="dont-break-out"><a href="<%=((Register) tmpRegItemclass.getRegister()).getBaseuri()%>/<%=((Register) tmpRegItemclass.getRegister()).getUriname()%>"><%=((Register) tmpRegItemclass.getRegister()).getBaseuri()%>/<%=((Register) tmpRegItemclass.getRegister()).getUriname()%></a></p>

                                                <%List<ItemclassStatistics> itemclassStatisticsList = tmpRegItemclass.getItemclassStatistics();
                                                    for (ItemclassStatistics itemclassStatistics : itemclassStatisticsList) {
                                                        String uriname = itemclassStatistics.getItemclass().getUriname();
                                                        int statistics = itemclassStatistics.getStatistics();
                                                %>
                                                <p><span><%=uriname%></span>: <b><%=statistics%></b></p>
                                                <%
                                                    }
                                                %>

                                            </div>

                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                </div>



                            </div>
                            <div class="row">
                                <div class="col-sm-6 col-md-8">
                                </div>
                                <div class="col-sm-6 col-md-4">
                                    <button class="btn btn-primary btn-md pull-right width100" type="submit"><i class="far fa-play-circle"></i> ${localization.getString("installation.button.saveandstart.system")}</button>
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
            // Starting the migration and redirecting to the waiting page
            $('#migrate').on('submit', function (e) {
                e.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '.<%=WebConstants.PAGE_URINAME_INSTALL%>',
                    data: {
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS%>': '<%=dbAddress%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBPORT%>': '<%=dbPort%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBNAME%>': '<%=dbName%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME%>': '<%=dbUsername%>',
                        '<%=BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD%>': '<%=dbPassword%>',
                        'csrfPreventionSalt': '${csrfPreventionSalt}',
                        '<%=BaseConstants.KEY_REQUEST_STEP%>': '<%=BaseConstants.KEY_REQUEST_MIGRATION_PROCESS%>'
                    }/*,
                     success: function (res) {
                     location.href='.<%=WebConstants.PAGE_URINAME_LOGIN%>';
                     }*/
                });

                // Redirect to the waiting page
                setTimeout(function () {
                    location.href = '.<%=WebConstants.PAGE_URINAME_INSTALL_RUNNING%>';
                }, 5000);
            });
        </script>

    </body>
</html>