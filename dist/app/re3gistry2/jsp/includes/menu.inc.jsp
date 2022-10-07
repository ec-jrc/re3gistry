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
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRole"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRoleManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="wconstants" class="eu.europa.ec.re3gistry2.base.utility.WebConstants" scope="session"/>

<%
    // Getting the configuration
    boolean checInstallation = configuration.checkInstallation();
    boolean checWorkflowSimplified = configuration.checkWorkflowSimplified();

    // Instantiating managers
    RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(entityManager);
    RegRoleManager regRoleManager = new RegRoleManager(entityManager);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionSubmitProposal = {BaseConstants.KEY_USER_ACTION_SUBMITPROPOSAL};
    String[] actionApproveProposal = {BaseConstants.KEY_USER_ACTION_APPROVEPROPOSAL};
    String[] actionPublishProposal = {BaseConstants.KEY_USER_ACTION_PUBLISHPROPOSAL};
    boolean permissionSubmitProposal = UserHelper.checkGenericAction(actionSubmitProposal, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
    boolean permissionApproveProposal = UserHelper.checkGenericAction(actionApproveProposal, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
    boolean permissionPublishProposal = UserHelper.checkGenericAction(actionPublishProposal, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

    RegRole regRoleSubmittingOrganization = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION);
    RegRole regRoleControlBody = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_CONTROLBODY);
    RegRole regRoleRegisterManager = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_REGISTERMANAGER);
    RegRole regRoleRegistryManager = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_REGISTRYMANAGER);

    boolean partOfSubmittingOrganizations = UserHelper.checkUserRole(regUser, regRoleSubmittingOrganization, regItemRegGroupRegRoleMappingManager, regUserRegGroupMappingManager);
    boolean partOfControlBody = UserHelper.checkUserRole(regUser, regRoleControlBody, regItemRegGroupRegRoleMappingManager, regUserRegGroupMappingManager);
    boolean partOfRegisterManager = UserHelper.checkUserRole(regUser, regRoleRegisterManager, regItemRegGroupRegRoleMappingManager, regUserRegGroupMappingManager);
    boolean partOfRegistryManager = UserHelper.checkUserRole(regUser, regRoleRegistryManager, regItemRegGroupRegRoleMappingManager, regUserRegGroupMappingManager);

%>
<nav class="navbar navbar-expand-lg navbar-light sticky-top">
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".openmenu" aria-controls="openmenu" aria-expanded="false" aria-label="${localization.getString("label.togglenavi")}">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse openmenu">

        <ul class="navbar-nav small">   
            <%--- Standard menu ---%>
            <%--- Login and User menu ---%>
            ${checInstallation}

            <%        if (checInstallation) {
                        
            %>
            <c:choose>
                <c:when test="${not empty regUser}">
                    <li class="nav-item bg-hover-gray<c:if test="${(currentPageName == wconstants.PAGE_URINAME_INDEX) || (currentPageName == '')}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_INDEX}"><i class="fas fa-home"></i> ${localization.getString('menu.home.label')}</a>
                    </li>
                    <li class="nav-item bg-hover-gray<c:if test="${(currentPageName == wconstants.PAGE_URINAME_BROWSE) || (currentPageName == wconstants.PAGE_URINAME_ADDITEM)}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_BROWSE}"><i class="fas fa-list"></i> ${localization.getString('menu.browse.label')}</a>
                    </li>
                    <% if (partOfRegistryManager) { %>
                    <li class="nav-item bg-hover-gray<c:if test="${(currentPageName == wconstants.PAGE_URINAME_ITEMCLASS) || (currentPageName == wconstants.PAGE_URINAME_ADDITEMCLASS) || (currentPageName == wconstants.PAGE_URINAME_FIELD)}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_ITEMCLASS}"><i class="fas fa-sitemap"></i> ${localization.getString('menu.itemclass.label')}</a>
                    </li>
                    <% } %>

                    <% if ((partOfSubmittingOrganizations || partOfRegistryManager) && (checWorkflowSimplified==false)) { %>
                    <li class="nav-item bg-hover-gray<c:if test="${currentPageName == wconstants.PAGE_URINAME_SUBMITTINGORGANISATIONS}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_SUBMITTINGORGANISATIONS}"><i class="fas fa-user-plus"></i> ${localization.getString('menu.submittingorganisations.label')}</a>
                    </li>
                    <% } %>
                    <% if (partOfControlBody && (checWorkflowSimplified==false)) { %>
                    <li class="nav-item bg-hover-gray<c:if test="${currentPageName == wconstants.PAGE_URINAME_CONTROLBODY}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_CONTROLBODY}"><i class="fas fa-user-check"></i> ${localization.getString('menu.controlbody.label')}</a>
                    </li>
                    <% } %>
                    <% if (partOfRegisterManager && (checWorkflowSimplified==false)) { %>
                    <li class="nav-item bg-hover-gray<c:if test="${currentPageName == wconstants.PAGE_URINAME_REGISTERMANAGER}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_REGISTERMANAGER}"><i class="fas fa-user-cog"></i> ${localization.getString('menu.registermanager.label')}</a>
                    </li>
                    <% } %>
                    <% if (partOfRegistryManager) { %>
                    <li class="nav-item bg-hover-gray<c:if test="${currentPageName == wconstants.PAGE_URINAME_REGISTRYMANAGER}"> active</c:if>">
                        <a class="nav-link" href=".${wconstants.PAGE_URINAME_REGISTRYMANAGER }"><i class="fas fa-user-graduate"></i> ${localization.getString('menu.registrymanager.label')}</a>
                    </li>
                    <% }%>

                </c:when>
            </c:choose>
            <%
                }
            %>
        </ul>

        <ul class="navbar-nav ml-auto small">
            <c:choose>
                <c:when test="${not empty regUser}">
                    <li class="nav-item bg-hover-gray">
                        <a class="nav-link" title="View user details" href=".<%=wconstants.PAGE_URINAME_USERPROFILE%>"><%=regUser.getName()%>&nbsp;<i class="fas fa-user"></i></a>
                    </li>
                    <li class="nav-item bg-hover-gray">
                        <a class="nav-link" href="logout">${localization.getString('login.label.logout')}&nbsp;<i class="fas fa-sign-out-alt"></i></a>
                    </li>
                </c:when>
                <c:otherwise>
                    <%--<li class="nav-item bg-hover-gray">
                        <a class="nav-link" href="login">${localization.getString('login.label.signin')}&nbsp;<i class="fas fa-sign-in-alt"></i></a>
                    </li>--%>
                </c:otherwise>
            </c:choose>
        </ul>

        <div class="d-md-none">						
            <hr/>
            <select id="cngl" data-live-search="true" class="selectpicker">
                <c:forEach items="${availableLanguages}" var="lng">
                    <option value="${lng.getLocaleCode()}"<c:if test="${lng.getLocaleCode() == localization.getString('property.localeid')}"> selected="selected"</c:if>>
                        ${lng.getLocaleLabel()}
                    </option>
                </c:forEach>
            </select>
            <hr/>
        </div>
    </div>
</nav>