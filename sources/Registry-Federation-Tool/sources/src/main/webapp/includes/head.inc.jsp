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
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<head prefix="<%=p.get("web.head_prefix")%>">
    <meta charset="<%=p.get("web.meta_charset")%>">
    <meta http-equiv="X-UA-Compatible" content="<%=p.get("web.meta_xuacompatible")%>">
    <meta name="viewport" content="<%=p.get("web.meta_viewport")%>">	
    <meta name="description" content="<%=p.get("web.default_description")%>">
    <meta name="keyword" content="<%=p.get("web.default_title")%>">
    <meta name="author" content="<%=p.get("web.meta_author")%>">

    <meta property="og:title" content="<%=p.get("web.default_title")%>"/>
    <meta property="og:description" content="<%=p.get("web.default_description")%>"/>
    <%
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {            
            queryString = StringEscapeUtils.escapeHtml4(queryString);
            queryString = StringEscapeUtils.escapeEcmaScript(queryString);
            queryString = StringEscapeUtils.escapeXml11(queryString);
            requestURL += "?" + queryString;
        }
    %>    
    <meta property="og:url" content="<%=requestURL%>"/>
    <meta property="og:image" content="<%=p.get("web.cdn_url")%>img/ec.ico/favicon-194x194.png"/>
    <meta property="og:type" content="website"/>

    <title><%=p.get("web.default_title")%></title>

    <link rel="stylesheet" href="<%=p.get("web.cdn_url")%>css/ec.eu.css">
    <link rel="stylesheet" href="<%=p.get("web.cdn_url")%>css/dataTables.bootstrap.css">
    <link rel="stylesheet" href="<%=p.get("web.application_root_url")%>res/css/ror.css">
    <link rel="stylesheet" href="<%=p.get("web.application_root_url")%>res/css/magnific-popup.css">
    <link rel="stylesheet" href="<%=p.get("web.application_root_url")%>res/css/jquery-ui.min.css">

    <link rel="apple-touch-icon" sizes="57x57" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="<%=p.get("web.cdn_url")%>img/ec.ico/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="<%=p.get("web.cdn_url")%>img/ec.ico/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="<%=p.get("web.cdn_url")%>img/ec.ico/favicon-194x194.png" sizes="194x194">
    <link rel="icon" type="image/png" href="<%=p.get("web.cdn_url")%>img/ec.ico/favicon-96x96.png" sizes="96x96">
    <link rel="icon" type="image/png" href="<%=p.get("web.cdn_url")%>img/ec.ico/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="<%=p.get("web.cdn_url")%>img/ec.ico/favicon-16x16.png" sizes="16x16">
    <link rel="shortcut icon" href="<%=p.get("web.cdn_url")%>img/ec.ico/favicon.ico">
    <meta name="msapplication-TileImage" content="<%=p.get("web.cdn_url")%>img/ec.ico/mstile-144x144.png">
    <meta name="msapplication-TileColor" content="<%=p.get("web.meta_msthemecolor")%>">
    <meta name="theme-color" content="<%=p.get("web.meta_themecolor")%>">

    <script src="<%=p.get("web.cdn_url")%>js/jquery.min.js"></script>
    
    <!--<script src="//ec.europa.eu/wel/cookie-consent/consent.js" type="text/javascript"></script>-->
    <script defer src="https://europa.eu/webtools/load.js" type="text/javascript"></script>

</head>