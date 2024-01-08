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
<head prefix="${properties.getProperty("web.head_prefix")}">
    <meta charset="${properties.get("web.meta_charset")}">
    <meta http-equiv="X-UA-Compatible" content="${properties.get("web.meta_xuacompatible")}">
    <meta name="viewport" content="${properties.get("web.meta_viewport")}">	
    <meta name="description" content="${properties.get("web.default_description")}">
    <meta name="keyword" content="${properties.get("web.default_title")}">
    <meta name="author" content="${properties.get("web.meta_author")}">

    <meta property="og:title" content="${properties.get("web.default_title")}"/>
    <meta property="og:description" content="${properties.get("web.default_description")}"/>
    <meta property="og:url" content="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
    <meta property="og:image" content="${properties.get("web.cdn_url")}img/ec.ico/favicon-194x194.png"/>
    <meta property="og:type" content="website"/>

    <title>${properties.get("web.default_title")}</title>

    <link rel="apple-touch-icon" sizes="57x57" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="${properties.get("web.cdn_url")}img/ec.ico/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="${properties.get("web.cdn_url")}img/ec.ico/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="${properties.get("web.cdn_url")}img/ec.ico/favicon-194x194.png" sizes="194x194">
    <link rel="icon" type="image/png" href="${properties.get("web.cdn_url")}img/ec.ico/favicon-96x96.png" sizes="96x96">
    <link rel="icon" type="image/png" href="${properties.get("web.cdn_url")}img/ec.ico/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="${properties.get("web.cdn_url")}img/ec.ico/favicon-16x16.png" sizes="16x16">
    <link rel="shortcut icon" href="${properties.get("web.cdn_url")}img/ec.ico/favicon.ico">
    <meta name="msapplication-TileImage" content="${properties.get("web.cdn_url")}img/ec.ico/mstile-144x144.png">
    <meta name="msapplication-TileColor" content="${properties.get("web.meta_msthemecolor")}">
    <meta name="theme-color" content="${properties.get("web.meta_themecolor")}">

    <%-- CSS from the INSPIRE CDN ---%>
    <link rel="stylesheet" href="${properties.get("web.cdn_url")}css/ec.eu.css">

    <%-- CSS specific to the project --%>
    <link rel="stylesheet" href="./res/css/re3gistry2.css">
    <link rel="stylesheet" href="./res/lib/DataTables/DataTables-1.10.18/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="./res/css/bootstrap-toggle.min.css">
    <link rel="stylesheet" href="./res/css/bootstrap-datepicker.min.css"/>

    <%-- JS from INSPIRE CDN --%>
    <script src="${properties.get("web.cdn_url")}js/jquery.min.js"></script>
    <script src="${properties.get("web.cdn_url")}js/popper.min.js"></script>
    <script src="${properties.get("web.cdn_url")}js/bootstrap.min.js"></script>
    <script src="${properties.get("web.cdn_url")}js/bootstrap-select.min.js" defer></script>
    <!--<script src="${properties.get("web.cdn_url")}js/jquery.cookieNotice.js" defer></script>-->
    <!-- consent.js is no longer available at this URL: --> 
    <!-- script src="//ec.europa.eu/wel/cookie-consent/consent.js" type="text/javascript"></script -->
    <script data-search-pseudo-elements src="${properties.get("web.cdn_url")}js/fontawesome.all.min.js"></script>

    <%-- JS specific to the project --%>
<c:choose>
    <c:when test="${not empty regUser}">
        <script src="./res/js/common.js" defer></script>
    </c:when>
</c:choose>
<script src="./res/js/bootstrap-confirmation.min.js" defer></script>
<script src="./res/lib/DataTables/DataTables-1.10.18/js/jquery.dataTables.min.js" defer></script>
<script src="./res/lib/DataTables/DataTables-1.10.18/js/dataTables.bootstrap4.min.js" defer></script>
<script src="./res/lib/ckeditor/ckeditor.js"></script>

<script src="./res/js/validator.min.js"></script>
<script src="./res/js/bootstrap-toggle.min.js" defer></script>
<script src="./res/js/bootstrap-datepicker.min.js" defer></script>

<link
    rel="stylesheet"
    href="./res/js-ecl-v2/package/dist/styles/ecl-ec-preset-website.css"
    crossorigin="anonymous"
    media="screen"
    />
<script
    src="./res/js-ecl-v2/package/dist/scripts/ecl-ec-preset-website.js"
    crossorigin="anonymous"
></script>

</head>
