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
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>

<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <c:choose>
                <%-- Showing an item --%>
                <c:when test="${empty registryList and (not empty item or (empty item and not empty regItemproposed))}">

                    <%-- Page title --%>                      
                    <div class="row">
                        <div class="col">
                            <h1 class="page-heading">${localization.getString("browse.label.title")}</h1>
                        </div>
                    </div>

                    <%-- Script to handle tab data loading on page load --%>
                    <script>
                        <c:if test="${not empty item}">
                        var itemUuid = "${item.uuid}";
                        var isRegItem = true;
                        </c:if>
                        <c:if test="${empty item and not empty regItemproposed}">
                        var itemUuid = "${regItemproposed.uuid}";
                        var isRegItem = false;
                        </c:if>

                        $(document).ready(function () {
                            loadAndRender(itemUuid, "${currentLanguage.uuid}", null, isRegItem);
                            return false;
                        });
                    </script>

                    <%                        boolean errorGeneric = false;
                        String operationSuccess = null;
                        if (request.getSession().getAttribute(BaseConstants.KEY_ERROR_GENERIC) != null) {
                            errorGeneric = (Boolean) request.getSession().getAttribute(BaseConstants.KEY_ERROR_GENERIC);
                        }
                        if (request.getSession().getAttribute(BaseConstants.KEY_OPERATION_SUCCESS) != null || request.getAttribute(BaseConstants.KEY_OPERATION_SUCCESS) != null) {
                            try {
                                operationSuccess = (String) request.getSession().getAttribute(BaseConstants.KEY_OPERATION_SUCCESS);
                            } catch (Exception e) {
                                operationSuccess = null;
                            }
                        }
                        if (errorGeneric) {
                    %>
                    <div class="alert alert-danger alert-dismissible mt-3" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        ${localization.getString("error.generic")}
                    </div>
                    <%
                        }
                        if (operationSuccess != null) {
                    %>
                    <div class="alert alert-success alert-dismissible mt-3"  role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        ${localization.getString("label.operationsuccess")}
                    </div>
                    <%
                        }
                        request.getSession().setAttribute(BaseConstants.KEY_ERROR_GENERIC, null);
                        request.getSession().setAttribute(BaseConstants.KEY_OPERATION_SUCCESS, null);
                    %>

                    <%
                        String bulkOperationError = (String) request.getAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR);
                        String bulkOperationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_BULK_SUCCESS);
                        String operationResult = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);

                        if (bulkOperationError != null) {%>
                    <div class="alert alert-danger alert-dismissible mt-3" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <%=bulkOperationError%>
                    </div>
                    <% }

        if (bulkOperationSuccess != null) {%>
                    <div class="alert alert-success text-center alert-dismissible">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h1 class="text-success text-center">${localization.getString("installation.success.login.congratulations")}</h1>
                        <hr/>
                        <%=bulkOperationSuccess%>
                    </div>
                    <% }

        if (operationResult != null) {%>
                    <div class="alert alert-danger alert-dismissible mt-3" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <%=operationResult%>
                    </div>
                    <% } %>

                    <%-- Language tabs --%>
                    <div class="browse-contents">
                        <ul class="nav nav-tabs mb-3" role="tablist">
                            <%
                                List<RegLanguagecode> regLanguagecodes = (List<RegLanguagecode>) request.getAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES);
                                RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
                                List<String> changedLanguages = (List<String>) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATIONPROPOSED_CHANGEDLANGUAGES);
                                for (RegLanguagecode temp : regLanguagecodes) {
                                    if (temp.getUuid() != null && temp.getUuid().equals(currentLanguage.getUuid())) {
                            %><li role="presentation" class="nav-item"><a class="nav-link active" href="#<%=temp.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="<%=temp.getUuid()%>" role="tab" data-target="#AJtab" data-toggle="tabajax"><%=(changedLanguages != null && changedLanguages.contains(temp.getUuid())) ? "<strong>" : ""%><%=temp.getIso6391code()%><%=(changedLanguages != null && changedLanguages.contains(temp.getUuid())) ? "</strong>" : ""%></a></li><%
                            } else {
                                %><li role="presentation" class="nav-item"><a class="nav-link" href="#<%=temp.getUuid()%>" data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="<%=temp.getUuid()%>" role="tab" data-target="#AJtab" data-toggle="tabajax"><%=(changedLanguages != null && changedLanguages.contains(temp.getUuid())) ? "<strong>" : ""%><%=temp.getIso6391code()%><%=(changedLanguages != null && changedLanguages.contains(temp.getUuid())) ? "</strong>" : ""%></a></li><%
                                        }
                                    };

                                %>
                        </ul>
                        <div class="tab-content">                        
                            <div class="loader_c"><div class="loader"></div></div>
                            <div role="tabpanel" class="tab-pane active" id="AJtab"></div>                            
                        </div>
                    </div>
                </c:when>

                <%-- Showing the list of registry --%>
                <%-- TODO: complete the multiregistry functionalities --%>
                <c:when test="${not empty registryList}">

                    <%-- Page title --%>
                    <div class="row bdot">
                        <div class="col-sm-9">
                            <h1>${localization.getString("browse.label.title")}</h1>
                        </div>
                        <div class="col-sm-3"></div>
                        <div class="hrl"></div>
                    </div>

                    <table id="registry-list-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                        <thead>
                            <tr><th>${localization.getString("label.localid")}</th></tr>                        
                        <thead>
                        <tbody>
                            <c:forEach var="registry" items="${registryList}" varStatus="loop">
                                <tr><td><a href=".<%=WebConstants.PAGE_URINAME_BROWSE%>?${constants.KEY_REQUEST_ITEMUUID}=${registry.uuid}&${constants.KEY_REQUEST_LANGUAGEUUID}=${currentLanguage.uuid}">${registry.localid}</a></td></tr>
                                    </c:forEach>
                        </tbody>
                    </table>                
                </c:when>

                <%-- No contents available --%>
                <c:otherwise>
                    <p>${label.registry}</p>
                </c:otherwise>
            </c:choose>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
        <script src="./res/js/bootstrap-confirmation.min.js"></script>
        <script>
            <%-- Script to manage the data load in the tabs and the item list table --%>
                        $('[data-toggle="tabajax"]').click(function (e) {
            <%-- Check if the content has been edited --%>
                            var check = unloadCheck();
                            var cnfr = true;
                            if (check) {
                                $(this).blur();
                                cnfr = confirm(unloadCheck());
                            }
                            if (cnfr) {
                                unsaved = false;
                                var targetURL = updateQueryStringParameter(location.href, '${constants.KEY_REQUEST_LANGUAGEUUID}', $(this).data('<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>') /*attr('data-languageUuid')*/);
                                history.pushState({lang: $(this).data('<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>') /*attr('data-languageUuid')*/}, null, targetURL);
                                loadAndRender(itemUuid, $(this).data('<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>') /*attr('data-languageUuid')*/, $(this), isRegItem);
                                $('#editing-form').validator('update');
                            }
                            return false;
                        });

            <%-- Script to manage the nav history for ajax actions --%>
                        window.addEventListener("popstate", function (e) {
                            var lang;
                            if (e.state !== null && e.state.lang !== null) {
                                lang = e.state.lang;
                            } else {
                                var url = new URL(document.location.href);
                                var lang = url.searchParams.get("${constants.KEY_REQUEST_LANGUAGEUUID}");
                                if (!lang) {
                                    lang = '${masterLanguage.uuid}';
                                }
                            }
                            loadAndRender(itemUuid, lang, $('[data-<%=WebConstants.DATA_PARAMETER_LANGUAGECONEUUID%>="' + lang + '"]'), isRegItem);
                        });

            <%-- Add value button handler --%>
                        $(document).on("click", ".btn-value-add", function (e) {
                            e.preventDefault();
                            var fieldMappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
                            getAddValueString(fieldMappingUuid, $(this));
                        });

            <%-- Remove value button handler --%>
                        $(document).on("click", ".btn-value-remove, .btn-relation-remove", function (e) {
                            e.preventDefault();
                            var regLocalizationUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID%>");
                            removeValue($(this), regLocalizationUuid, isRegItem);
                        });

            <%-- Restore original relation button handler --%>
                        $(document).on("click", ".btn-restore-relation", function (e) {
                            e.preventDefault();
                            var regLocalizationUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_REGLOCALIZATIONUUID%>");
                            restoreOriginalRegRelationValue(regLocalizationUuid, true);
                        });

            <%-- Add relation button handler --%>
                        $(document).on("click", ".btn-relation-add", function (e) {
                            e.preventDefault();
                            var regFieldmappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
                            var regItemUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_ITEMUUID%>");
                            getAddRelationString(regFieldmappingUuid, regItemUuid, $(this));
                        });

            <%--  Add parent button handler --%>
                        $(document).on("click", ".btn-parent-add", function (e) {
                            e.preventDefault();
                            var regFieldmappingUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID%>");
                            var regItemUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_ITEMUUID%>");
                            getAddParentString(regFieldmappingUuid, regItemUuid, $(this));
                        });

            <%-- Discard changes button handler --%>
                        $(document).on("click", ".btn-discard-changes", function (e) {
                            e.preventDefault();
                            var regItemUuid = $(this).data("<%=WebConstants.DATA_PARAMETER_ITEMUUID%>");
                            discardChanges(regItemUuid, $(this));
                        });

        </script>
    </body>
</html>