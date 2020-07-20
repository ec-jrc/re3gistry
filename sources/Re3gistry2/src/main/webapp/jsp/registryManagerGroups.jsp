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
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>" role="tab">${localization.getString("label.users")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link active" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>" role="tab">${localization.getString("label.groups")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT%>" role="tab">${localization.getString("label.dataexport")}</a></li>
                </ul>
            </div>
            <%
                // Getting feedback messages
                Object requestResult = request.getAttribute(BaseConstants.KEY_REQUEST_RESULT);
                if (requestResult != null) {
                    if ((Boolean) requestResult) {
            %>
            <div class="alert alert-success alert-dismissible mt-3" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                ${localization.getString("group.label.updatesuccess")}
            </div>
            <%
            } else {
            %>
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                ${localization.getString("error.generic")}
            </div>
            <%
                    }
                }

                // Getting the list of users or the user detail
                RegGroup regGroup = (RegGroup) request.getAttribute(BaseConstants.KEY_REQUEST_REGGROUP);
                List<RegGroup> regGroups = (List<RegGroup>) request.getAttribute(BaseConstants.KEY_REQUEST_REGGROUPS);

                if (regGroup != null) {
            %>
            <div class="row mt-3 mb-2">
                <div class="col-sm-9">
                    <h3 class="mt-2">${localization.getString("label.group")}</h3>
                </div>
                <div class="col-sm-3">
                    <a href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>" class="btn btn-light float-right mt-2 width100" role="button" aria-pressed="true">
                        <i class="fas fa-long-arrow-alt-left"></i> ${localization.getString("label.backto")} ${localization.getString("label.groups")}
                    </a>                    
                </div>
            </div>

            <%
            } else {
            %>
            <div class="row">
                <div class="col-sm-9">
                    <h3 class="mt-2">${localization.getString("label.groups")}</h3>
                </div>
                <div class="col-sm-3">
                    <a class="btn btn-success width100 mt-2" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS_ADD%>"><i class="fas fa-plus"></i> ${localization.getString("label.addgroup")}</a>
                </div>
            </div>
            <%
                }

                /* User detail list */
                if (regGroups != null && regGroups.size() > 0) {
            %>
            <table id="list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.name")}</th>
                        <th>${localization.getString("label.insertdate")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (RegGroup tmp : regGroups) {
                    %>
                    <tr>
                        <td><a href="?<%=BaseConstants.KEY_REQUEST_GROUP_UUID%>=<%=tmp.getUuid()%>"><%=tmp.getName()%></a></td>                    
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
                if (regGroup != null) {
            %>
            <form id="editing-form" method="post"  accept-charset="UTF-8" data-toggle="validator" role="form">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_GROUPUUID%>" value="<%=regGroup.getUuid()%>" />
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>" value="true" />

                <div class="row form-group editing-labels">

                    <label class="col-sm-4">${localization.getString('label.name')}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input  maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="<%= (regGroup.getName() != null) ? regGroup.getName() : ""%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_GROUP_NAME%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>

                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString('label.email')}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input  maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="<%= (regGroup.getEmail() != null) ? regGroup.getEmail() : ""%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_EMAIL%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <span class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString('label.website')}</label>
                    <div class="col-sm-8">                        
                        <input  maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" type="text" class="form-control" value="<%= (regGroup.getWebsite() != null) ? regGroup.getWebsite() : ""%>" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_WEBSITE%>" />                            
                    </div>
                </div>

                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.insertdate')}</label>
                    <div class="col-sm-8"><%= (regGroup.getInsertdate() != null) ? dt.format(regGroup.getInsertdate()) : ""%></div>
                </div>
                <%
                    if (regGroup.getEditdate() != null) {
                %>
                <div class="row">
                    <label class="col-sm-4">${localization.getString('label.editdate')}</label>
                    <div class="col-sm-8"><%= (regGroup.getEditdate() != null) ? dt.format(regGroup.getEditdate()) : ""%></div>
                </div>            
                <%
                    }
                %>

                <div class="row mt-2">
                    <div class="col-sm-9"></div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.savechanges")}</button>
                    </div>
                </div>

                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

            </form>
            <%
                }
            %>

            <hr/>            

            <%
                /* Group detail list */
                List<RegUserRegGroupMapping> regUserRegGroupMapping = (List<RegUserRegGroupMapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS);
                if (regUserRegGroupMapping != null && regUserRegGroupMapping.size() > 0) {
            %>
            <h3>${localization.getString("label.associatedusers")}</h3>
            <table id="groups-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.name")}</th>
                        <th>${localization.getString("label.email")}</th>
                        <th>${localization.getString("label.status")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (RegUserRegGroupMapping tmp : regUserRegGroupMapping) {
                            RegUser regUserDetail = tmp.getRegUser();
                    %>
                    <tr>
                        <td><a href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>?<%=BaseConstants.KEY_REQUEST_USERDETAIL_UUID%>=<%=regUserDetail.getUuid()%>"><%=regUserDetail.getName()%></a></td>
                        <td><%=regUserDetail.getEmail()%></td>
                        <td><%=(regUserDetail.getEnabled()) ? systemLocalization.getString("label.enabled") : systemLocalization.getString("label.disabled")%></td>
                    </tr> 
                    <%
                        }
                    %>    
                </tbody>
            </table>
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

            $(function () {
            <% if (regGroup == null) { %>
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
        </script>

    </body>
</html>