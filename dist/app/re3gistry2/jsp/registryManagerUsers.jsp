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

            <div class="mt-3">
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER%>" role="tab">${localization.getString("label.actions")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link active" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>" role="tab">${localization.getString("label.users")}</a></li>
                    <% if (!Configuration.checkWorkflowSimplified()) {
                        %>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>" role="tab">${localization.getString("label.groups")}</a></li>
                        <% }
                    %>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT%>" role="tab">${localization.getString("label.dataexport")}</a></li>
                </ul>
            </div>

            <%
                // Getting feedback messages
                Object requestResult = request.getAttribute(BaseConstants.KEY_REQUEST_RESULT);
                if (requestResult != null) {
                    if ((Boolean) requestResult) {
            %>
            <div class="alert alert-success alert-dismissible mt-2" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                ${localization.getString("user.label.updatesuccess")}
            </div>
            <%
            } else {
            %>
            <div class="alert alert-danger alert-dismissible mt2" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                ${localization.getString("error.generic")}
            </div>
            <%
                    }
                }

                // Getting the list of users or the user detail
                RegUser regUserDetail = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSERDETAIL);
                List<RegUser> regUsersDetail = (List<RegUser>) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSERSDETAIL);

                if (regUserDetail != null) {
            %>
            <div class="row">
                <div class="col-sm-9">
                    <h3 class="mt-2">${localization.getString("label.userdetails")}</h3>
                </div>
                <div class="col-sm-3">
                    <a href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>" class="btn btn-light float-right mt-2 width100" role="button" aria-pressed="true">
                        <i class="fas fa-long-arrow-alt-left"></i> ${localization.getString("label.backto")} ${localization.getString("label.userslist")}
                    </a>                    
                </div>
            </div>
            <%
            } else {
            %>
            <div class="row mt-3">
                <div class="col-sm-9">
                    <h3 class="mt-2">${localization.getString("label.userslist")}</h3>
                </div>
                <div class="col-sm-3">
                    <a class="btn btn-default btn-success width100 mt-2" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS_ADD%>"><i class="fas fa-user-plus"></i> ${localization.getString("label.adduser")}</a>
                </div>
            </div>
            <%
                }


                /* User detail list */
                if (regUsersDetail != null && regUsersDetail.size() > 0) {
            %>
            <table id="list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.name")}</th>
                            <%
                                final Properties properties = Configuration.getInstance().getProperties();
                                String privacy = properties.getProperty(BaseConstants.KEY_PRIVACY_APPLICATION);
                                if (privacy == null || (privacy != null && !privacy.equals("ON"))) {
                            %>
                        <th>${localization.getString("label.email")}</th>
                            <%}%>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.insertdate")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (RegUser tmp : regUsersDetail) {
                    %>
                    <tr>
                        <td><a href="?<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=<%=tmp.getUuid()%>"><%=tmp.getName()%></a></td>                    
                            <%
                               if (privacy == null || (privacy != null && !privacy.equals("ON"))) {
                            %>
                        <td><%=tmp.getEmail()%></td>
                        <%}%>
                        <td><%=(tmp.getEnabled()) ? systemLocalization.getString("label.enabled") : systemLocalization.getString("label.disabled")%></td>
                        <td><%=dt.format(tmp.getInsertdate())%></td>
                    </tr> 
                    <%
                        }
                    %>    
                </tbody>
            </table>

            <%
                }

                /* User detail */
                if (regUserDetail != null) {
            %>
            <form id="editing-form" method="post"  accept-charset="UTF-8" data-toggle="validator" role="form">

                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_USERUUID%>" value="<%=regUserDetail.getUuid()%>" />
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>" value="true" />

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString('label.name')}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input  maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="<%= (regUserDetail.getName() != null) ? regUserDetail.getName() : ""%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_USER_NAME%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>              
                        </div>
                    </div>
                </div>                      

                <%
                    final Properties properties = Configuration.getInstance().getProperties();
                    String privacy = properties.getProperty(BaseConstants.KEY_PRIVACY_APPLICATION);
                    if (privacy == null || (privacy != null && !privacy.equals("ON"))) {
                %>

                <%
                    // The SSO Reference is displayed only in case of Login type ECAS
                    if (regUserDetail.getSsoreference() != null && configuration.getProperties().getProperty("application.login.type").equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS)) {
                %>
                <div class="row editing-labels">
                    <div class="col-sm-4"><strong>${localization.getString('label.ssoref')}</strong></div>
                    <div class="col-sm-8"><%=(regUserDetail.getSsoreference() != null) ? regUserDetail.getSsoreference() : ""%></div>
                </div>
                <%
                    }
                %>

                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.email')}</label>
                    <div class="col-sm-8"><%= (regUserDetail.getEmail() != null) ? regUserDetail.getEmail() : ""%></div>
                </div>
                <%
                    }
                %>
                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.insertdate')}</label>
                    <div class="col-sm-8"><%= (regUserDetail.getInsertdate() != null) ? dt.format(regUserDetail.getInsertdate()) : ""%></div>
                </div>
                <%
                    if (regUserDetail.getEditdate() != null) {
                %>
                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.editdate')}</label>
                    <div class="col-sm-8"><%= (regUserDetail.getEditdate() != null) ? dt.format(regUserDetail.getEditdate()) : ""%></div>
                </div>            
                <%
                    }
                %>
                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.status')}</label>
                    <div class="col-sm-8">
                        <%if (regUserDetail.getEnabled() != null && regUserDetail.getEnabled()) {%>
                        ${localization.getString("label.enabled")}                    
                        <%} else {%>
                        ${localization.getString("label.disabled")}
                        <%}%>
                    </div>
                </div>


                <div class="row mt-3 mb-3">

                    <div class="col-sm-6"></div>


                    <%
                        if (!regUserDetail.getUuid().equals(currentUser.getUuid())) {
                            if (regUserDetail.getEnabled() != null && regUserDetail.getEnabled()) {%>           
                    <div class="col-sm-3">                            
                        <a href="?<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=<%=regUserDetail.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTIONTYPE%>=<%=BaseConstants.KEY_ACTION_TYPE_DISABLEUSER%>" class="btn btn-danger width100"><i class="fas fa-ban"></i> ${localization.getString("label.disableuser")}</a>
                    </div>
                    <%} else {%>
                    <div class="col-sm-3">
                        <a href="?<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=<%=regUserDetail.getUuid()%>&<%=BaseConstants.KEY_REQUEST_ACTIONTYPE%>=<%=BaseConstants.KEY_ACTION_TYPE_ENABLEUSER%>" class="btn btn-primary width100"><i class="fas fa-user"></i> ${localization.getString("label.enableuser")}</a>
                    </div>
                    <%}
                        }
                    %>

                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.savechanges")}</button>
                    </div>

                </div>

                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

            </form>
            <%
                }
            %>



            <%
                /* Group detail list */
                List<RegUserRegGroupMapping> regUserRegGroupMapping = (List<RegUserRegGroupMapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS);
                if (regUserDetail != null) {
            %>
            <h3>${localization.getString("label.associatedgroups")}</h3>
            <table id="groups-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.group")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<RegGroup> regGroups = (List<RegGroup>) request.getAttribute(BaseConstants.KEY_REQUEST_REGGROUPS);
                    %>
                    <%
                        for (RegGroup tmp : regGroups) {
                            boolean found = false;
                            String foundUuid = null;
                            // Check if the user is in the current group
                            for (RegUserRegGroupMapping tmp1 : regUserRegGroupMapping) {
                                if (tmp1.getRegGroup().getUuid().equals(tmp.getUuid())) {
                                    found = true;
                                    foundUuid = tmp1.getUuid();
                                    break;
                                }
                            }
                    %>
                    <tr>
                        <td><input class="cbUpdate" data-<%=BaseConstants.KEY_REQUEST_USERGROUPMAPPING_UUID%>="<%=foundUuid%>" data-<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=<%=regUserDetail.getUuid()%> data-<%=BaseConstants.KEY_REQUEST_GROUP_UUID%>="<%=tmp.getUuid()%>" type="checkbox" <%=(found) ? "checked=\"checked\"" : ""%> /></td>
                        <td><a href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>?<%=BaseConstants.KEY_REQUEST_GROUP_UUID%>=<%=tmp.getUuid()%>"><%=tmp.getName()%></a></td>                    
                    </tr> 
                    <%
                        }
                    %>  
                </tbody>
            </table>
                <div class="col-sm-3">

                        <button class="btn btn-success width100" id="mySaveButton"><i class="far fa-save"></i> ${localization.getString("label.savechanges")}</button>
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

            <%-- Init the data tables --%>
            $(function () {
            <% if (regUserDetail == null) { %>
                $('#list-table').DataTable({
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "order": [[0, "asc"]]
                });
            <% }%>
                $('#groups-table').DataTable({
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "order": [[0, "desc"]]
                });
            });            
            $("#mySaveButton").on('click', function () {
                var collection = document.getElementsByClassName("cbUpdate");                
                var regUserDetailUuid = $(collection[0]).data("<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>");                
                var regUserRegGroupMappingUuidList = new Array (collection.length);
                var regGroupUUIDList = new Array(collection.length);
                var checkedList = new Array(collection.length);
                
                for(var i=0; i<collection.length; i++){        
                        regUserRegGroupMappingUuidList[i] = $(collection[i]).data("<%=BaseConstants.KEY_REQUEST_USERGROUPMAPPING_UUID%>");
                        regGroupUUIDList[i] = $(collection[i]).data("<%=BaseConstants.KEY_REQUEST_GROUP_UUID%>");
                        checkedList[i] = $(collection[i]).is(":checked");   
                }
                          
                $.get(".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>?<%=BaseConstants.KEY_REQUEST_GROUP_UUID%>=" + regGroupUUIDList + "&<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=" + regUserDetailUuid + "&<%=BaseConstants.KEY_REQUEST_ACTIONTYPE%>=<%=BaseConstants.KEY_ACTION_TYPE_REMOVEUSERGROUP%>&<%=BaseConstants.KEY_REQUEST_USERGROUPMAPPING_UUID%>=" + regUserRegGroupMappingUuidList + "&<%=BaseConstants.KEY_REQUEST_CHECKED%>=" + checkedList, function (data) {});
            });

        </script>

    </body>
</html>