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
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationhistoryManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalizationhistory"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemhistory"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
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
                    <h1 class="page-heading">${localization.getString("controlbody.label.title")}</h1>
                </div>
            </div>

            <%
                // Getting the list of action
                RegAction regAction = (RegAction) request.getAttribute(BaseConstants.KEY_REQUEST_ACTION);
                List<RegAction> regActions = (List<RegAction>) request.getAttribute(BaseConstants.KEY_REQUEST_ACTION_LIST);

                if (regAction != null) {
            %>
            <div class="row">
                <div class="col-sm-9">
                    <h3>${localization.getString("label.actiondetails")}</h3>
                </div>
                <div class="col-sm-3">
                    <a href=".<%=WebConstants.PAGE_URINAME_CONTROLBODY%>" class="btn btn-light float-right width100" role="button" aria-pressed="true">
                        <i class="fas fa-long-arrow-alt-left"></i> ${localization.getString("label.backto")} ${localization.getString("label.actionslist")}
                    </a>
                </div>
            </div>
            <%
            } else {
            %><h3>${localization.getString("label.actionslist")}</h3><%
                }

                if (regActions.size() > 0) {

            %>
            <table id="list-table" class="table table-striped table-bordered  mt-3" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${localization.getString("label.label")}</th>
                        <th>${localization.getString("label.startdate")}</th>
                        <th>${localization.getString("label.register")}</th>
                        <th>${localization.getString("label.status")}</th>
                        <th>${localization.getString("label.userowner")}</th>
                        <th>${localization.getString("label.actions")}</th>
                    </tr>
                </thead>
                <tbody>
                    <%                        for (RegAction tmp : regActions) {
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
                            } else {
                                tmpLocalization = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), tmp.getRegItemRegistry(), currentLanguage);
                                // In case of title field there is only one element
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
                        <td><%=tmp.getRegUser().getName()%></td>
                        <td>
                            <%
                                // Checking the status of the action
                                boolean showActions = false;

                                if (tmp.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUBMITTED)) {
                                    showActions = true;
                                }
                            %>
                            <%-- Action buttons --%>
                            <%if (showActions) {%>
                            <a href="?<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>=<%=tmp.getUuid()%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_APPROVE_TYPE%>=<%=BaseConstants.KEY_ACTION_TYPE_APPROVE%>&<%=BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION%>=true" data-toggle="confirmation" data-title="${localization.getString("controlbody.label.approve.confirm")}" data-placement="left" data-singleton="true" class="btn btn-sm btn-success btn-approve-action btn-reg-action" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>"><i class="fas fa-check"></i> ${localization.getString("controlbody.label.approve")}</a><br/>
                            <button class="btn btn-sm btn-warning btn-action btn-reg-action mt-1" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_ACTIONTYPE%>="<%=BaseConstants.KEY_ACTION_TYPE_APPROVEWITHCHANGES%>"><i class="fas fa-file-signature"></i> ${localization.getString("controlbody.label.approvewithchanges")}</button><br/>
                            <button class="btn btn-sm btn-danger btn-action btn-reg-action mt-1" data-<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>="<%=tmp.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_ACTIONTYPE%>="<%=BaseConstants.KEY_ACTION_TYPE_REJECT%>"><i class="far fa-trash-alt"></i> ${localization.getString("controlbody.label.reject")}</button><br/>
                            <%}%>

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

                if (regAction != null) {

            %><hr/><%                    /* -- Submit details -- */
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
                        <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_APPROVE_TYPE%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_APPROVE_TYPE%>" />
                        <div class="modal-header">
                            <h5 class="modal-title"><span id="actionTitleDisplay"></span> <%--(ID: <span id="actionIdDisplay"></span>)--%></h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label id="actionDescriptionDisplay"></label>
                                <textarea required class="form-control" rows="5" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_COMMENTS%>"></textarea>
                            </div>
                            <div class="form-group">
                                <label>${localization.getString('submittingorganisations.label.modal.issue')}</label>
                                <input class="form-control" type="text" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>" id="<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>" <%=(regAction != null && regAction.getIssueTrackerLink() != null && regAction.getIssueTrackerLink().length() > 0) ? "value=\"" + regAction.getIssueTrackerLink() + "\"" : ""%> maxlength="<%= configuration.getProperties().getProperty("application.input.maxlength")%>" />
                            </div>
                        </div>
                        <div class="modal-footer">
                            <div class="col-sm-6">
                                <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> ${localization.getString('label.cancel')}</button>
                            </div>
                            <div class="col-sm-6">
                                <button type="submit" class="btn btn-primary width100"><i class="far fa-paper-plane"></i> ${localization.getString('label.submit')}</button>
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

            var actionApproveWithChanges_Title = "${localization.getString('controlbody.label.approvewithchanges')}";
            var actionApproveWithChanges_Description = "${localization.getString('controlbody.label.modal.approvewithchanges.description')}";

            var actionReject_Title = "${localization.getString('controlbody.label.reject')}";
            var actionReject_Description = "${localization.getString('controlbody.label.modal.reject.description')}";

            $('.btn-action').on('click', function () {
                var actionUuid = $(this).data('<%=WebConstants.DATA_PARAMETER_ACTIONUUID%>');
                var actionType = $(this).data('<%=WebConstants.DATA_PARAMETER_ACTIONTYPE%>');

                $.get(".<%=WebConstants.PAGE_URINAME_ACTIONDETAIL%>?<%=BaseConstants.KEY_REQUEST_ACTION_UUID%>=" + actionUuid, function (data) {
                    //$('#actionIdDisplay').text(actionUuid);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID%>').val(actionUuid);
                    $('#<%=BaseConstants.KEY_FORM_FIELD_NAME_APPROVE_TYPE%>').val(actionType);

                    if (actionType === "<%=BaseConstants.KEY_ACTION_TYPE_APPROVEWITHCHANGES%>") {
                        $('#btn-action').text(actionApproveWithChanges_Title);
                        $('#actionTitleDisplay').text(actionApproveWithChanges_Title);
                        $('#actionDescriptionDisplay').text(actionApproveWithChanges_Description);
                    } else if (actionType === "<%=BaseConstants.KEY_ACTION_TYPE_REJECT%>") {
                        $('#btn-action').text(actionReject_Title);
                        $('#actionTitleDisplay').text(actionReject_Title);
                        $('#actionDescriptionDisplay').text(actionReject_Description);
                    }
                    $("#<%=BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE%>").val(data.<%=BaseConstants.KEY_JSON_FIELDS_ISSUETRACKERLINK%>);

                    $('#submit_action').modal();
                });
            });

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
        </script>

    </body>
</html>