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
<div class="hb3 hidden-xs">
    <div class="container">
        <ol class="breadcrumb" vocab="http://schema.org/" typeof="BreadcrumbList">
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="<%=p.get("web.link_eu")%>/index_<%=p.get("web.default_locale")%>.htm"><span property="name">European Commission</span></a></li>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="<%=p.get("web.link_inspire")%>"><span property="name">INSPIRE</span></a></li>
            
            <%
            if(type.equals("registries")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp?type=registries"><span property="name">Registries</span></a></li>
            <%
            }else if(type.equals("registers")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp?type=registers"><span property="name">Registers</span></a></li>
            <%
            }else if(type.equals("relations")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp?type=relations"><span property="name">Relations</span></a></li>
            <%
            }else if(type.equals("search")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="search.jsp"><span property="name">Federated search</span></a></li>
            <%
            }else if(type.equals("help")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="help.jsp"><span property="name">Help</span></a></li>
            <%
            }else if(type.equals("private")){
            %>
            <li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="private.jsp"><span property="name">Private area</span></a></li>
            <%
            }else{
            %>
            <li class="active" property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="index.jsp"><span property="name">Register Federation</span></a></li>
            <%
            }
            %>
        </ol>
    </div>
</div> 