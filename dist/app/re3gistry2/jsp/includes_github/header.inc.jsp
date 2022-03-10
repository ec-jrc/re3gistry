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
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="topalert.inc.jsp" %>

<header>	
    <div class="hb1">
        <div class="container relative">			
            <!--<a class="ec-logo" href="#"><img src="${properties.get("web.cdn_url")}img/ec.logo/logo_${localization.getString("property.localeid")}.gif" alt="${localization.getString("label.eclogo")}"/></a>-->
            <a data-i18n-link="l-ec-website" class="ecl-link ecl-link--standalone" 
               href="/registry">
                <img src="./res/img/logo-1.png" alt="" width="30%" height="30%">
            </a>
            <!--<span class="mt">${localization.getString("label.title")}</span>-->			

            <div class="topm d-none d-md-block">						
                <ul class="lnk small">
                    <li><a href=".${properties.get("web.link_about").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.about")}</a></li><!--
                    --><li><a href="${properties.get("web.link_contact").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.contact")}</a></li><!--
                    --><li><a href="${properties.get("web.link_privacy").toString()}">${localization.getString("label.privacy")}</a></li><!--
                    --><li><a href="${properties.get("web.link_legal").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.legal")}</a></li>
                </ul>
                <select id="cngl" data-live-search="true" class="selectpicker">
                    <c:forEach items="${availableLanguages}" var="lng">
                        <option value="${lng.getLocaleCode()}"<c:if test="${lng.getLocaleCode() == localization.getString('property.localeid')}"> selected="selected"</c:if>>${lng.getLocaleLabel()}</option>
                    </c:forEach>
                </select>
                <div class="userbox small">
                    <%--<c:choose>
                        <c:when test="${not empty regUser}">
                            ${localization.getString("label.user")}: <a title="View user details" href=".<%=WebConstants .PAGE_URINAME_USERPROFILE %>"><%=regUser.getName()%></a>
                            <br/><a href="logout">${localization.getString('login.label.logout')} <i class="fas fa-sign-out-alt"></i></a>
                        </c:when>
                        <c:otherwise>
                            <a href="login"><i class="fas fa-sign-in-alt"></i> ${localization.getString('login.label.signin')}</a>
                        </c:otherwise>
                    </c:choose>--%>
                </div>
            </div>
        </div>
    </div>
    <div class="hb2">
        <div class="container">
            <span>${localization.getString("label.subtitle")}</span>
        </div>
    </div>
    <div class="hb3 d-none d-sm-block">
        <%@include file="breadcrumbs.inc.jsp" %> 
    </div>
    <div class="hb4">
        <div class="container">
            <%@include file="menu.inc.jsp" %>        
        </div>
    </div>

</header>