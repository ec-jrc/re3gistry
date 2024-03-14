<%-- 
/*
 * Copyright 2010,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-info@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--- @include file="/includes/topalert.inc.jsp" ---%> 
<header>	
    <div class="hb1">
        <div class="container relative">			
            <a class="ec-logo" href="#"><img src="<%=p.get("web.cdn_url")%>img/ec.logo/logo_<%=localization.getString("common.localeid")%>.gif" alt="<%=localization.getString("common.ec.logo")%>"/></a>
            <span class="mt"><%=localization.getString("common.inspire")%></span>			

            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".openmenu" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only"><%=localization.getString("common.togglenavi")%></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

            <div id="topm" class="topm navbar-collapse collapse openmenu">						
                <ul class="lnk">
                    <li><a href="<%=p.get("web.link_about").toString().replace("__lng__", localization.getString("common.localeid"))%>"><%=localization.getString("common.about")%></a></li><li><a href="<%=p.get("web.link_contact").toString().replace("__lng__", localization.getString("common.localeid"))%>"><%=localization.getString("common.contact")%></a></li><li><a href="<%=p.get("web.link_privacy").toString()%>"><%=localization.getString("common.privacy")%></a></li><li><a href="<%=p.get("web.link_legal").toString().replace("__lng__", localization.getString("common.localeid"))%>"><%=localization.getString("common.legal")%></a></li><li><a href="<%=p.get("web.link_cookies").toString()%>"><%=localization.getString("common.cookies")%></a></li>
                </ul><!--		
                --><select id="cngl" data-live-search="true" class="selectpicker">
                    <%
                        Iterator iterator = LocalizationMgr.getAvailableLanguages().iterator();
                        while (iterator.hasNext()) {
                            Localization tmp = (Localization) iterator.next();
                    %>
                    <option<%=(tmp.getLocaleCode().equals(localization.getString("common.localeid"))) ? " selected=\"selected\"" : ""%> value="<%=tmp.getLocaleCode()%>"><%=p.get("application.language.label." + tmp.getLocaleCode())%></option>
                    <%
                        }
                    %>
                </select>
            </div>
        </div>
    </div>
    <div class="hb2">
        <div class="container">
            <span><%=localization.getString("common.title")%></span>
        </div>
    </div>

    <div id="affx">
        <%@include file="/includes/breadcrumbs.inc.jsp" %>

        <div class="hb4">
            <div class="container">
                <%@include file="/includes/menu.inc.jsp" %>        
            </div>
        </div>
    </div>    
</header> 
<!-- Analytics with  EU CCK v2 -->
<script type="application/json">
    {"utility" : "cck"}
</script>
<script type="application/json">
    {
    "utility" : "analytics",
    "siteID" : "84c65ccc-9997-4cfa-91f3-ea0ef9d059b1",
    "instance" : ["inspire.ec.europa.eu"],
    "instance":"ec",
    "mode":"auto"
    }
</script>