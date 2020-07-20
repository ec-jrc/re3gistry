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
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<footer>
    <div class="ft1">
        <div class="container">			
            <div class="row">
                <div class="col">${localization.getString("label.title")} - ${localization.getString("label.subtitle")}</div>
                <div class="col"><span class="float-right">${properties.getProperty("application.version")}</span></div>
            </div>
        </div>
    </div>
    <div class="ft2">
        <div class="container">
            <div class="row">
                <div class="col-sm d-none d-md-block">
                    <img class="ec-inspire-logo" src="${properties.get("web.cdn_url")}img/ec.inspire.logo/logo_en.png"  alt="${localization.getString("label.footerlogo")}">
                </div>
                <div class="col-sm">
                    <h4>${localization.getString("footer.c1.title")}<a data-toggle="collapse" href="#c1" aria-expanded="false" aria-controls="c1" class="navbar-toggle d-sm-none">&nbsp;</a></h4>
                    <ol id="c1" class="collapse multi-collapse show">
                        <li><a href="${localization.getString("footer.c1.l1")}">${localization.getString("footer.c1.i1")}</a></li>
			<li><a href="${localization.getString("footer.c1.l2")}">${localization.getString("footer.c1.i2")}</a></li>
                        <li><a href="${localization.getString("footer.c1.l3")}">${localization.getString("footer.c1.i3")}</a></li>
                        <li><a href="${localization.getString("footer.c1.l4")}">${localization.getString("footer.c1.i4")}</a></li>
                        <li><a href="${localization.getString("footer.c1.l5")}">${localization.getString("footer.c1.i5")}</a></li>
                    </ol>
                </div>
                <div class="col-sm">
                    <h4>${localization.getString("footer.c2.title")}<a data-toggle="collapse" href="#c2" aria-expanded="false" aria-controls="c2" class="navbar-toggle d-sm-none">&nbsp;</a></h4>
                    <ol id="c2" class="collapse multi-collapse show">
                        <li><a href="${localization.getString("footer.c2.l1")}">${localization.getString("footer.c2.i1")}</a></li>
			<li><a href="${localization.getString("footer.c2.l2")}">${localization.getString("footer.c2.i2")}</a></li>
                        <li><a href="${localization.getString("footer.c2.l3")}">${localization.getString("footer.c2.i3")}</a></li>
                        <li><a href="${localization.getString("footer.c2.l4")}">${localization.getString("footer.c2.i4")}</a></li>
                    </ol>
                </div>
                <div class="col-sm">
                    <h4>${localization.getString("footer.c3.title")}
                        <a data-toggle="collapse" href="#c3" aria-expanded="false" aria-controls="c3" class="navbar-toggle d-sm-none">&nbsp;</a>
                    </h4>
                    <ol id="c3" class="collapse multi-collapse show">
                        <li><a href="${localization.getString("footer.c3.l1")}">${localization.getString("footer.c3.i1")}</a></li>
			<li><a href="${localization.getString("footer.c3.l2")}">${localization.getString("footer.c3.i2")}</a></li>
                        <li><a href="${localization.getString("footer.c3.l3")}">${localization.getString("footer.c3.i3")}</a></li>
                        <li><a href="${localization.getString("footer.c3.l4")}">${localization.getString("footer.c3.i4")}</a></li>
                        <li><a href="${localization.getString("footer.c3.l5")}">${localization.getString("footer.c3.i5")}</a></li>
                    </ol>
                </div>
                <div class="col-sm">
                    <h4>${localization.getString("footer.c4.title")}
                        <a data-toggle="collapse" href="#c4" aria-expanded="false" aria-controls="c4" class="navbar-toggle d-sm-none">&nbsp;</a>
                    </h4>
                    <ol id="c4" class="collapse multi-collapse show">
                        <li><a href=".<%=WebConstants.PAGE_URINAME_INDEX %>">${localization.getString("footer.c4.i1")}</a></li>
			<li><a href=".<%=WebConstants.PAGE_URINAME_BROWSE %>">${localization.getString("footer.c4.i2")}</a></li>
                        <li><a href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS %>">${localization.getString("footer.c4.i3")}</a></li>
                        <li><a href=".<%=WebConstants.PAGE_URINAME_HELP %>">${localization.getString("footer.c4.i4")}</a></li>
                        <li><a href=".<%=WebConstants.PAGE_URINAME_ABOUT %>">${localization.getString("footer.c4.i5")}</a></li>
                    </ol>
                </div>
            </div>
        </div>
    </div>
    <div class="ft3">
        <div class="container">
            <div class="float-right">
                <ol>
                    <li><a href="${properties.get("web.link_about").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.about")}</a></li><!--
                    --><li><a href="${properties.get("web.link_contact").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.contact")}</a></li><!--
                    --><li><a href="${properties.get("web.link_termsofuse").toString().replace("__lang__", localization.getString("property.localeid"))}">${localization.getString("label.termsofuse")}</a></li><!--
                    --><li><a href="${properties.get("web.link_privacy").toString()}">${localization.getString("label.privacy")}</a></li><!--
                    --><li><a href="${properties.get("web.link_legal").toString().replace("__lng__",localization.getString("property.localeid"))}">${localization.getString("label.legal")}</a></li><!--
                    --><li><a href="${properties.get("web.link_cookies").toString().replace("__lang__", localization.getString("property.localeid"))}">${localization.getString("label.cookies")}</a></li>
                </ol>
                <div class="social">
                    <div>
                        <a href="${properties.get("web.link_social_tw")}"><i class="fab fa-twitter-square"></i></a>
                        <a href="${properties.get("web.link_social_li")}"><i class="fab fa-linkedin"></i></a>
                        <a href="${properties.get("web.link_social_fb")}"><i class="fab fa-facebook-square"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>