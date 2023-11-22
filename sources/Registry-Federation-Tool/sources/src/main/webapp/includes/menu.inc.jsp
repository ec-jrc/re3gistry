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
<%@page import="eu.europa.ec.ror.model.User"%>
<%@page import="eu.europa.ec.ror.managers.UserMgr"%>
<%@page import="eu.cec.digit.ecas.client.jaas.DetailedUser"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<ul id="mnm" class="nav-menu nav-menu-pills navbar-collapse collapse openmenu">
    <li<%if (type.equals("registries")) {%> class="active"<%}%>>
        <a href="<%=p.get("web.application_root_url")%>index.jsp?type=registries" title="<%=localization.getString("registries.title")%>"><span class="fa fa-database"></span> <span class="hidden-xs"><%=localization.getString("registries.title")%></span></a>
    </li>
    <li<%if (type.equals("registers")) {%> class="active"<%}%>>
        <a href="<%=p.get("web.application_root_url")%>index.jsp?type=registers" title="<%=localization.getString("registers.title")%>"><span class="fa fa-list"></span> <span class="hidden-xs"><%=localization.getString("registers.title")%></span></a>
    </li>
    <li<%if (type.equals("relations")) {%> class="active"<%}%>>
        <a href="<%=p.get("web.application_root_url")%>index.jsp?type=relations" title="<%=localization.getString("relations.title")%>"><span class="glyphicon glyphicon-link"></span> <span class="hidden-xs"><%=localization.getString("relations.title")%></span></a>
    </li>
    <li <%if (request.getRequestURI().contains("search")) {%>class="active"<%}%>>
        <a href="<%=p.get("web.application_root_url")%>search.jsp" title="<%=localization.getString("search.title")%>"><span class="glyphicon glyphicon-search"></span> <span class="hidden-xs"><%=localization.getString("search.title")%></span></a>
    </li>
    <li class="<%if (request.getRequestURI().contains("help")) {%> active<%}%>">
        <a href="<%=p.get("web.application_root_url")%>help.jsp" title="<%=localization.getString("common.label.help.short")%>"><span class="glyphicon glyphicon-question-sign"></span> <span class="hidden-xs"><%=localization.getString("common.label.help.short")%></span></a>
    </li>
    
    <%
    DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();
    User u = null;
    if (detailedUser != null) {
        String uid = detailedUser.getUid();
        u = UserMgr.getUserByEcasId(uid);
        
        //If ECAS uid is not available, check the ECAS email
        if(u==null){
            uid = detailedUser.getEmail();
            u = UserMgr.getUserByEcasId(uid);
        }
        
        if(u!=null && u.getActive()){
    %>
    <li class="pull-right">
        <a href="<%=p.get("web.application_root_url")%>private.jsp?ac=logout" style="margin-right:0"><span class="glyphicon glyphicon-log-out"></span> <span class="hidden-xs"><%=localization.getString("common.label.logout")%></span></a>
    </li>
    <li class="pull-right<%if (request.getRequestURI().contains("private")) {%> active<%}%>">
        <a href="<%=p.get("web.application_root_url")%>private.jsp"><span class="glyphicon glyphicon-lock"></span> <span class="hidden-xs"><%=localization.getString("common.label.private")%></span></a>
    </li>
    <%
        }else{
        %>
        <li class="pull-right">
            <a href="<%=p.get("web.application_root_url")%>private.jsp"><span class="glyphicon glyphicon-log-in"></span> <span class="hidden-xs"><%=localization.getString("common.label.login")%></span></a>
        </li>
        <%
        }
    }else{
    %>
    <li class="pull-right">
        <a href="<%=p.get("web.application_root_url")%>private.jsp"><span class="glyphicon glyphicon-log-in"></span> <span class="hidden-xs"><%=localization.getString("common.label.login")%></span></a>
    </li>
    <%
    }
    %>
</ul>
<!--<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#mnm" aria-expanded="false" aria-controls="navbar">
    <span class="sr-only"><%=localization.getString("common.togglenavi")%></span>
    <span class="icon-bar ib1"></span>
    <span class="icon-bar ib1"></span>
    <span class="icon-bar ib1"></span>
</button>-->