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
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="eu.europa.ec.re3gistry2.javaapi.cache.CacheHelper"%>
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
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegLocalizationproposedManager regLocalizationProposedManager = new RegLocalizationproposedManager(entityManager);
    RegLocalizationhistoryManager regLocalizationhistoryManager = new RegLocalizationhistoryManager(entityManager);
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);

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

            <div class="mb-3">
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="nav-item"><a class="nav-link active" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER%>" role="tab">${localization.getString("label.actions")}</a></li>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS%>" role="tab">${localization.getString("label.users")}</a></li>
                    <% if (!Configuration.checkWorkflowSimplified()) {
                        %>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS%>" role="tab">${localization.getString("label.groups")}</a></li>
                    <% }
                    %>
                    <li role="presentation" class="nav-item"><a class="nav-link" href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT%>" role="tab">${localization.getString("label.dataexport")}</a></li>
                </ul>
            </div>    

            <%
                String operationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE);
                if (operationSuccess != null) {
            %>
            <div class="alert alert-success alert-dismissible" role="alert">
                <%=operationSuccess%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <%
                }
            %>

            <%
                // Getting the list of action
                RegAction regAction = (RegAction) request.getAttribute(BaseConstants.KEY_REQUEST_ACTION);
                Set<RegAction> regActions = (Set<RegAction>) request.getAttribute(BaseConstants.KEY_REQUEST_ACTION_LIST);

                if (regAction != null) {
            %>
           
                <% if (CacheHelper.checkCacheCompleteRunning()) {
                %>
                <div class="alert alert-warning alert-dismissible" role="alert">
                    ${localization.getString("label.cacheactionrunning")}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
                <%}%>
            
            <div class="row">
                <div class="col-sm-9">
                    <h3>${localization.getString("label.actiondetails")}</h3>
                </div>
                <div class="col-sm-3">
                    <a href=".<%=WebConstants.PAGE_URINAME_REGISTRYMANAGER%>" class="btn btn-light float-right width100" role="button" aria-pressed="true">
                        <i class="fas fa-long-arrow-alt-left"></i> ${localization.getString("label.backto")} ${localization.getString("label.actionslist")}
                    </a>
                </div>
            </div>
            <%
            } else {
            %><h3 style="display: inline-block">${localization.getString("label.actionslist")}</h3><%
                }

                if (regActions.size() > 0) {
                
                    for (RegAction pubAction : regActions) {

                        // Checking the status of the action
                                boolean showActionPublish = false;

                                if (pubAction.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED)
                                        || (pubAction.getRegItemRegistry() != null && pubAction.getRegItemRegister() == null && !pubAction.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_PUBLISHED))) {
                                    showActionPublish = true;
                                }

                        if(CacheHelper.checkCacheCompleteRunning() && showActionPublish){
                    %>
                    
                    <p style="display: inline-block; margin-left: 35rem; font-style: italic;"><svg style="display: inline-block;" width="24" height="24" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12.032 17.025c-.774 0-1.407-.674-1.407-1.499V11.03c0-.825.633-1.5 1.407-1.5s1.406.675 1.406 1.5v4.496c0 .825-.632 1.5-1.406 1.5zm0 4.497c-.774 0-1.407-.674-1.407-1.499 0-.825.633-1.499 1.407-1.499s1.406.674 1.406 1.499c0 .825-.632 1.499-1.406 1.499zm0-21.517L0 24h23.995L12.032.005z"/></svg> The publication will remain disabled until the caching process is completed.</p>
                    
                    <%
                    }
}

            %>
            <table id="list-table" class="table table-striped table-bordered mt-3" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.label")}</th>
                        <th>${localization.getString("label.startdate")}</th>
                        <th>${localization.getString("label.register")} / ${localization.getString("label.registry")}</th>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.userowner")}</th>
                        <th>${localization.getString("label.actions")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%     
                       for (RegAction tmp : regActions) { 
                    %>
                    <tr>
                        <td>
                            <%if (regAction != null) {%>
                            <%=tmp.getLabel()%>
                            <%} else {%>
                            <a href="?<%=BaseConstants.KEY_REQUEST_ACTION_UUID%>=<%=tmp.getUuid()%>"><%=tmp.getLabel()%></a>
                            <%}%>
                        </td>
                        <td><%=dt.format(tmp.getInsertdate())%></td>
                        <td><%
                            String tdText = "";
                            List<RegLocalization> tmpLocalization;
                            if (tmp.getRegItemRegister() != null) {
                                tmpLocalization = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), tmp.getRegItemRegister(), currentLanguage);
                                tdText = tmpLocalization.get(0).getValue();
                            }
                            %><%=tdText%>
                        </td>
                        <td><%
                            RegStatuslocalizationManager regStatusLocalizationManager = new RegStatuslocalizationManager(entityManager);
                            RegStatuslocalization regStatuslocalization = null;
                            try {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), currentLanguage);
                            } catch (NoResultException e) {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), masterLanguage);
                            }
                            %>
                            <%=regStatuslocalization.getLabel()%>
                            <%=(tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT) && tmp.getChangeRequest() != null && tmp.getChangeRequest().length() > 0) ? " (" + systemLocalization.getString("label.changeneeded") + ")" : ""%>
                            <%=(tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUBMITTED)) ? " (" + systemLocalization.getString("label.by") + " " + tmp.getSubmittedBy().getName() + ")" : ""%>
                            <%=(tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED)) ? " (" + systemLocalization.getString("label.by") + " " + tmp.getRejectedBy().getName() + ")" : ""%>
                            <%=(tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED)) ? " (" + systemLocalization.getString("label.by") + " " + tmp.getApprovedBy().getName() + ")" : ""%>
                        </td>
                        <td><%=tmp.getRegUser().getName() + " (" + tmp.getRegUser().getSsoreference() + ")"%></td>
                        <td>
                         <%
                               
                            %>
                            
                                <%
                                    // Checking the status of the action
                                    boolean showSubmit = false;
                                    boolean showChangesRequest = false;

                                    // Check if the user is the owner of the action
                                    if (Configuration.checkWorkflowSimplified()== false) {
                                    
                                    }
                                    if (tmp.getRegUser().getSsoreference().equals(regUser.getSsoreference())) {
                                        if (tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT)) {
                                            showSubmit = true;
                                        }
                                        if (tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT) && (tmp.getChangeRequest() != null && tmp.getChangeRequest().length() > 0)) {
                                            showChangesRequest = true;
                                        }
                                    }
                                    
                                // Checking the status of the action
                                boolean showActionPublish = false;

                                if (tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED)
                                        || (tmp.getRegItemRegistry() != null && tmp.getRegItemRegister() == null && !tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_PUBLISHED))) {
                                    showActionPublish = true;
                                }
                                    
                                %>
                                <%if (showActionPublish==false) {%>
                                    <%if (showSubmit && !(regAction == null && showChangesRequest)) {%>
                                    <a href="#" class="btn btn-sm btn-success btn-submit-action btn-reg-action" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>"><i class="far fa-paper-plane"></i> ${localization.getString("label.submit")}</a><br/>
                                    <a data-toggle="confirmation" data-title="${localization.getString("discard.action.confirm")}" data-placement="left" data-singleton="true" class="btn btn-sm btn-danger btn-approve-action  btn-reg-action mt-1" href=".<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL%>?<%=BaseConstants.KEY_REQUEST_ACTION_UUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_DIRECTPUBLISH%>=true"><i class="fas fa-trash"></i> ${localization.getString("label.discardaction")}</a>
                                    <%}%>

                                    <%if (showChangesRequest && regAction == null) {%>
                                    <a class="btn btn-sm btn-warning btn-submit-action btn-reg-action" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>" href="?<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>=<%=tmp.getUuid()%>"><i class="fab fa-readme"></i> ${localization.getString("submittingorganisations.label.readchangerequest")}</a><br/>
                                    <%}%>
                                <%}%>
                                
                            <%-- Action buttons --%>
                            <%if (showActionPublish) {%>
                            <% if (!CacheHelper.checkCacheCompleteRunning()) {%>
                            <a href="?<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>=true" data-toggle="confirmation" data-title="${localization.getString("registrymanager.label.publish.confirm")}" data-placement="left" data-singleton="true" class="btn btn-sm btn-success btn-approve-action btn-reg-action" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>"><i class="fas fa-upload"></i> ${localization.getString("registrymanager.label.publish")}</a><br/>
                            <%} else {%>
                            <a href="?<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>=true" title=" Publishing is currently unavailable, wait for the current caching process to end." data-toggle="confirmation" data-title="${localization.getString("registrymanager.label.publish.confirm")}" data-placement="left" data-singleton="true" class="btn btn-sm btn-success btn-approve-action btn-reg-action disabled" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>"><i class="fas fa-upload"></i> ${localization.getString("registrymanager.label.publish")}</a><br/>                            
                            <%}%>
                            <%}%>
                            <script>
                                setTimeout(function () {
                                    location = ''
                                }, 60 * 1000)
                            </script>
                        </td>
                    </tr> 
                    <%
                        }
                    %>    
                </tbody>
            </table>

            <%
            } else {
            %><p>${localization.getString("label.noaction")}</p><%
                }
            %>

            <%
                if (regAction != null) {

                    /* -- Submit details -- */
                    if (regAction.getSubmittedBy() != null) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('label.submittedby')}</strong></div>
                <div class="col-sm-9"><%=regAction.getSubmittedBy().getName()%></div>
            </div>
            <%
                }
                if (regAction.getIssueTrackerLink() != null && regAction.getIssueTrackerLink().length() > 0) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('submittingorganisations.label.issuetracker')}</strong></div>
                <div class="col-sm-9"><a href="<%=regAction.getIssueTrackerLink()%>" target="_blank"><%=regAction.getIssueTrackerLink()%></a></div>
            </div>
            <%
                }
                if (regAction.getChangelog() != null && regAction.getChangelog().length() > 0) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('submittingorganisations.label.changelog')}</strong></div>
                <div class="col-sm-9"><%=regAction.getChangelog()%></div>
            </div>
            <%
                }

                if (regAction.getSubmittedBy() != null) {
            %><hr/><%
                }
                /* -- -- */

 /* -- Reject details -- */
                if (regAction.getRejectedBy() != null) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('label.rejectedby')}</strong></div>
                <div class="col-sm-9"><%=regAction.getRejectedBy().getName()%></div>
            </div>
            <%
                }
                if (regAction.getRejectMessage() != null && regAction.getRejectMessage().length() > 0) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('label.rejectmessage')}</strong></div>
                <div class="col-sm-9">
                    <div class="alert alert-danger">
                        <%=regAction.getRejectMessage()%>
                    </div>
                </div>
            </div>
            <%
                }
                if (regAction.getRejectedBy() != null) {
            %><hr/><%
                }
                /* -- -- */

 /* -- Approve details -- */
                if (regAction.getApprovedBy() != null) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('label.approvedby')}</strong></div>
                <div class="col-sm-9"><%=regAction.getApprovedBy().getName()%></div>
            </div>
            <%
                }
                if (regAction.getApprovedBy() != null) {
            %><hr/><%
                }
                /* -- -- */

 /* -- Change request  -- */
                if (regAction.getChangeRequest() != null && regAction.getChangeRequest().length() > 0) {
            %>
            <div class="row">
                <div class="col-sm-3"><strong>${localization.getString('label.requestedchange')}</strong></div>
                <div class="col-sm-9">
                    <div class="alert alert-warning">
                        <%=regAction.getChangeRequest()%>
                    </div>
                </div>
            </div>
            <hr/>
            <%
                    }
                    /* -- -- */

                }

                List<RegItemproposed> regItemproposeds = (List<RegItemproposed>) request.getAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSEDS);
                List<RegItemhistory> regItemhistorys = (List<RegItemhistory>) request.getAttribute(BaseConstants.KEY_REQUEST_ITEM_HISTORYS);
                List<RegItem> regItems = (List<RegItem>) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMS);

                if (regItemproposeds != null && regItemproposeds.size() > 0) {

            %>

            <h3>${localization.getString("label.listofchangesinactionproposed")}</h3>
            <table id="item-list" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.label")}</th>
                        <th>${localization.getString("label.language")}</th>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.userowner")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%                    for (RegItemproposed tmp : regItemproposeds) {
                            //Gettign the label reg field
                            RegField regFieldTitle = regFieldManager.getTitleRegField();

                            //Getting the localiztion
                            List<RegLocalizationproposed> regLocalizationproposeds = null;
                            try {
                                regLocalizationproposeds = regLocalizationProposedManager.getAll(regFieldTitle, tmp);
                            } catch (NoResultException e) {
                                regLocalizationproposeds = regLocalizationProposedManager.getAll(regFieldTitle, tmp);
                            }
                            for (RegLocalizationproposed tmpLocalizationproposed : regLocalizationproposeds) {

                                //The title reg field should be single
                                //RegLocalizationproposed tmpLocalizationproposed = regLocalizationproposeds.get(0);
                                String relatedItemUuid = null;
                                boolean newItem = false;
                                if (tmp.getRegItemReference() != null) {
                                    relatedItemUuid = tmp.getRegItemReference().getUuid();
                                } else {
                                    relatedItemUuid = tmp.getUuid();
                                    newItem = true;
                                }
                    %>
                    <tr>
                        <td><a href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=relatedItemUuid%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%= tmpLocalizationproposed.getRegLanguagecode().getIso6391code()%>"><%=tmpLocalizationproposed.getValue()%></a><%= (newItem) ? " (" + systemLocalization.getString("label.new") + ")" : ""%></td>
                        <td><%=tmpLocalizationproposed.getRegLanguagecode().getIso6391code()%></td>
                        <td><%
                            RegStatuslocalizationManager regStatusLocalizationManager = new RegStatuslocalizationManager(entityManager);
                            RegStatuslocalization regStatuslocalization = null;
                            try {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), currentLanguage);
                            } catch (NoResultException e) {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), masterLanguage);
                            }
                            %>
                            <%=regStatuslocalization.getLabel()%>
                        </td>
                        <td><%=tmp.getRegUser().getName()%></td>
                    </tr><%
                            }
                        }
                    %>
                </tbody>
            </table>
            <%
            } else if (regItemhistorys != null && regItemhistorys.size() > 0) {
            %>

            <h3>${localization.getString("label.listofchangesrejected")}</h3>
            <table id="item-list" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.label")}</th>
                        <th>${localization.getString("label.language")}</th>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.userowner")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (RegItemhistory tmp : regItemhistorys) {
                            //Gettign the label reg field
                            RegField regFieldTitle = regFieldManager.getTitleRegField();

                            //Getting the localiztion
                            List<RegLocalizationhistory> regLocalizationhistorys = null;
                            try {
                                regLocalizationhistorys = regLocalizationhistoryManager.getAll(regFieldTitle, tmp);
                            } catch (NoResultException e) {
                                regLocalizationhistorys = regLocalizationhistoryManager.getAll(regFieldTitle, tmp);
                            }
                            for (RegLocalizationhistory tmpLocalizationhistory : regLocalizationhistorys) {

                                //The title reg field should be single
                                //RegLocalizationhistory tmpLocalizationhistory = regLocalizationhistorys.get(0);
                                boolean newItem = false;
                    %>
                    <tr>
                        <td><%--<a href=".<%=WebConstants.PAGE_URINAME_BROWSE_HISTORY%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=tmpLocalizationhistory.getRegLanguagecode().getIso6391code() %>">--%><%=tmpLocalizationhistory.getValue()%><%--</a>--%><%= (newItem) ? " (" + systemLocalization.getString("label.new") + ")" : ""%></td>
                        <td><%=tmpLocalizationhistory.getRegLanguagecode().getIso6391code()%></td>
                        <td><%
                            RegStatuslocalizationManager regStatusLocalizationManager = new RegStatuslocalizationManager(entityManager);
                            RegStatuslocalization regStatuslocalization = null;
                            try {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), currentLanguage);
                            } catch (NoResultException e) {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), masterLanguage);
                            }
                            %>
                            <%=regStatuslocalization.getLabel()%>
                        </td>
                        <td><%=tmp.getRegUser().getName()%></td>
                    </tr><%
                            }
                        }
                    %>
                </tbody>
            </table>
            <%  } else if (regItems != null && regItems.size() > 0) {
            %>

            <h3>${localization.getString("label.listofchangespublished")}</h3>
            <table id="item-list" class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.label")}</th>
                        <th>${localization.getString("label.language")}</th>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.userowner")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (RegItem tmp : regItems) {
                            //Gettign the label reg field
                            RegField regFieldTitle = regFieldManager.getTitleRegField();

                            //Getting the localiztion
                            List<RegLocalization> regLocalizations = null;
                            try {
                                regLocalizations = regLocalizationManager.getAll(regFieldTitle, tmp, regAction);
                                if (regLocalizations.isEmpty()) {
                                    regLocalizations = regLocalizationManager.getAll(regFieldTitle, tmp, masterLanguage);
                                }
                            } catch (Exception e) {
                            }
                            for (RegLocalization tmpLocalization : regLocalizations) {

                                //The title reg field should be single
                                //RegLocalization tmpLocalization = regLocalizations.get(0);
                                boolean newItem = false;
                                if ((tmpLocalization.getRegLanguagecode().getMasterlanguage() && (tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                                        || tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                                        || tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)))
                                        || (!tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                                        && !tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                                        && !tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED))) {%>
                    <tr>
                        <td><a href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?<%=BaseConstants.KEY_REQUEST_ITEMUUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>=<%=tmpLocalization.getRegLanguagecode().getIso6391code()%>"><%=tmpLocalization.getValue()%></a><%= (newItem) ? " (" + systemLocalization.getString("label.new") + ")" : ""%></td>
                        <td><%=tmpLocalization.getRegLanguagecode().getIso6391code()%></td>
                        <td><%
                            RegStatuslocalizationManager regStatusLocalizationManager = new RegStatuslocalizationManager(entityManager);
                            RegStatuslocalization regStatuslocalization = null;
                            try {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), currentLanguage);
                            } catch (NoResultException e) {
                                regStatuslocalization = regStatusLocalizationManager.get(tmp.getRegStatus(), masterLanguage);
                            }
                            %>
                            <%=regStatuslocalization.getLabel()%>
                        </td>
                        <td><%=tmp.getRegUser().getName()%></td>
                    </tr><%
                                }
                            }
                        }
                    %>
                </tbody>
            </table>
            <%  }
            %>

        </div>
          <div class="modal fade" tabindex="-1" role="dialog" id="submit_action">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form method="post">

                        <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>"/>
                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>" value="true" />
                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_DIRECTPUBLISH%>" value="true" />
                        <div class="modal-header">
                            <h5 class="modal-title">${localization.getString('submittingorganisations.label.modal.submitaction')} <%--(ID: <span id="actionIdDisplay"></span>)--%></h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <p>${localization.getString('submittingorganisations.label.modal.submitdesc')}</p>

                            <div class="form-group">
                                <label>Action title</label>
                                <input required class="form-control" disabled="" type="text" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REACTIONLABEL%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_REACTIONLABEL%>" <%=(regAction != null && regAction.getLabel() != null && regAction.getLabel().length() > 0) ? "value=\"" + regAction.getLabel() + "\"" : ""%> maxlength="<%= configuration.getProperties().getProperty("application.input.maxlength")%>" />
                            </div>

                            <div class="form-group">
                                <label><i class="fas fa-star text-danger"></i> ${localization.getString('submittingorganisations.label.modal.submitdescnotice')}</label>
                                <textarea class="form-control border-danger" rows="5" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_CHANGELOG%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_CHANGELOG%>">
                                    <%=(regAction != null && regAction.getChangelog() != null && regAction.getChangelog().length() > 0) ? regAction.getChangelog() : ""%>
                                </textarea>
                                <label class="text-sm text-danger">${localization.getString('submittingorganisations.label.modal.field.required')}</label>
                            </div>

                            <div class="form-group">
                                <label>${localization.getString('submittingorganisations.label.modal.issue')}</label>
                                <input class="form-control" type="text" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>" <%=(regAction != null && regAction.getIssueTrackerLink() != null && regAction.getIssueTrackerLink().length() > 0) ? "value=\"" + regAction.getIssueTrackerLink() + "\"" : ""%> maxlength="<%= configuration.getProperties().getProperty("application.input.maxlength")%>"  />
                            </div>

<!--                            <div class="col-sm-12 text-sm">
                                <i class="fas fa-star text-danger"></i> = Required fields
                            </div>-->
                        </div>
                        <div class="modal-footer">
                            <div class="col-sm-6">
                                <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> ${localization.getString('label.cancel')}</button>
                            </div>
                            <div class="col-sm-6">
                                <button type="submit" class="btn btn-success width100 d-none" id="submitButton"><i class="far fa-paper-plane"></i> ${localization.getString('label.submit')}</button>
                            </div>
                        </div>
                    </form>  
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
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
            <% if (regAction == null) { %>
                                    $('#list-table').DataTable({
                                        "dom": '<"top">rt<"bottom"lip><"clear">',
                                        "order": [[5, "desc"], [1, "desc"]]
                                    });
            <% }%>
                                    $('#item-list').DataTable({
                                        "dom": '<"top">rt<"bottom"lip><"clear">',
                                        "order": [[0, "desc"]]
                                    });
                                });
                                

        
        
            <%-- Init the confirm message --%>
            $('[data-toggle=confirmation]').confirmation({
                rootSelector: '[data-toggle=confirmation]'
            });

            $('.btn-submit-action').on('click', function () {
                var actionUuid = $(this).data('<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>');
                $.get(".<%=WebConstants.PAGE_URINAME_ACTIONDETAIL%>?<%=BaseConstants.KEY_REQUEST_ACTION_UUID%>=" + actionUuid, function (data) {
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_CHANGELOG%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_CHANGELOG%>);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_ISSUETRACKERLINK%>);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_REACTIONLABEL%>').val(data.<%=BaseConstants.KEY_JSON_FIELDS_LABEL%>);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_DIRECTPUBLISH%>').val(true);
                    $('#actionIdDisplay').text(actionUuid);
                    $('#submit_action #<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>').val(actionUuid);
                    if (data.<%=BaseConstants.KEY_JSON_FIELDS_CHANGEREQUEST%> != '' && data.<%=BaseConstants.KEY_JSON_FIELDS_CHANGEREQUEST%> != 'undefined' && data.<%=BaseConstants.KEY_JSON_FIELDS_CHANGEREQUEST%> != null) {
                        $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_REACTIONLABEL%>').attr('disabled', 'disabled');
                    }
                    $('#submit_action').modal();
                });
            });

            $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_CHANGELOG%>').bind('input propertychange', function () {

                if (this.value.length) {
                    $('#submitButton').removeClass("d-none");
                } else {
                    $('#submitButton').addClass("d-none");
                }
            });

        
        </script>

    </body>
</html>
