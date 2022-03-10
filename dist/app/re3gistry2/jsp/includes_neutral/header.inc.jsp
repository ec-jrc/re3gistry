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


<header class="ecl-site-header" data-ecl-site-header="true" data-ecl-auto-init="SiteHeader">
    <div class="ecl-site-header__container ecl-container">
        <div class="ecl-site-header__banner">
            <a data-i18n-link="l-ec-website" class="ecl-link ecl-link--standalone" 
               href="https://inspire-sandbox.jrc.ec.europa.eu/registry"
               aria-label="European Commission">
                <img src="./res/img/logo-1.png" alt="" width="50%" height="50%">
            </a>
            <div class="ecl-site-header__selector"><a class="ecl-link ecl-link--standalone ecl-site-header__selector-link"
                                                      href="" data-ecl-language-selector="true">English<span class="ecl-site-header__language-icon"><svg
                            focusable="false" aria-hidden="true" class="ecl-icon ecl-icon--m">
                        <use xlink:href="./res/ecl-v2/static/media/icons.svg#general--language"></use>
                        </svg><span class="ecl-site-header__language-code">en</span></span></a>
                <div hidden="" class="ecl-language-list ecl-language-list--overlay" aria-labelledby="ecl-language-list__title"
                     role="dialog" data-ecl-language-list-overlay="true">
                    <div class="ecl-language-list__container ecl-container">
                        <div class="ecl-row">
                            <div class="ecl-language-list__close ecl-col-12 ecl-col-lg-8 ecl-offset-lg-2"><button
                                    data-ecl-language-list-close="true" type="submit"
                                    class="ecl-language-list__close-button ecl-button ecl-button--ghost"><span
                                        class="ecl-button__container"><span class="ecl-button__label" data-ecl-label="true" data-i18n="c-close">Close</span><svg
                                            focusable="false" aria-hidden="true" data-ecl-icon="true"
                                            class="ecl-button__icon ecl-button__icon--after ecl-icon ecl-icon--s">
                                        <use xlink:href="./res/ecl-v2/static/media/icons.svg#ui--close"></use>
                                        </svg></span></button></div>
                            <div class="ecl-language-list__title ecl-col-12 ecl-col-lg-8 ecl-offset-lg-2"
                                 id="ecl-language-list__title"><svg focusable="false" aria-hidden="true"
                                                               class="ecl-language-list__title-icon ecl-icon ecl-icon--m">
                                <use xlink:href="./res/ecl-v2/static/media/icons.svg#general--generic-lang"></use>
                                </svg>Select your language</div>
                        </div>
                        <div class="ecl-row">
                            <div class="ecl-language-list__column ecl-col-12 ecl-col-lg-4 ecl-offset-lg-2">
                                <ul class="ecl-language-list__list">
                                    <li class="ecl-language-list__item "><a lang="bg" hrefLang="bg" rel="alternate"
                                                                            href="#language_bg"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">български</a></li>
                                    <li class="ecl-language-list__item "><a lang="es" hrefLang="es" rel="alternate"
                                                                            href="#language_es"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">español</a></li>
                                    <li class="ecl-language-list__item "><a lang="cs" hrefLang="cs" rel="alternate"
                                                                            href="#language_cs"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">čeština</a></li>
                                    <li class="ecl-language-list__item "><a lang="da" hrefLang="da" rel="alternate"
                                                                            href="#language_da"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">dansk</a></li>
                                    <li class="ecl-language-list__item "><a lang="de" hrefLang="de" rel="alternate"
                                                                            href="#language_de"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">Deutsch</a></li>
                                    <li class="ecl-language-list__item "><a lang="et" hrefLang="et" rel="alternate"
                                                                            href="#language_et"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">eesti</a></li>
                                    <li class="ecl-language-list__item "><a lang="el" hrefLang="el" rel="alternate"
                                                                            href="#language_el"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">ελληνικά</a></li>
                                    <li class="ecl-language-list__item ecl-language-list__item--is-active"><a lang="en" hrefLang="en"
                                                                                                              rel="alternate" href="#language_en"
                                                                                                              class="ecl-language-list__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-after"><span
                                                class="ecl-link__label">English</span> <svg focusable="false" aria-hidden="true"
                                                class="ecl-link__icon ecl-icon ecl-icon--xs">
                                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#ui--check"></use>
                                            </svg></a></li>
                                    <li class="ecl-language-list__item "><a lang="fr" hrefLang="fr" rel="alternate"
                                                                            href="#language_fr"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">français</a></li>
                                    <li class="ecl-language-list__item "><a lang="ga" hrefLang="ga" rel="alternate"
                                                                            href="#language_ga"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">Gaeilge</a></li>
                                    <li class="ecl-language-list__item "><a lang="hr" hrefLang="hr" rel="alternate"
                                                                            href="#language_hr"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">hrvatski</a></li>
                                    <li class="ecl-language-list__item "><a lang="it" hrefLang="it" rel="alternate"
                                                                            href="#language_it"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">italiano</a></li>
                                </ul>
                            </div>
                            <div class="ecl-language-list__column ecl-col-12 ecl-col-lg-4">
                                <ul class="ecl-language-list__list">
                                    <li class="ecl-language-list__item "><a lang="lv" hrefLang="lv" rel="alternate"
                                                                            href="#language_lv"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">latviešu</a></li>
                                    <li class="ecl-language-list__item "><a lang="lt" hrefLang="lt" rel="alternate"
                                                                            href="#language_lt"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">lietuvių</a></li>
                                    <li class="ecl-language-list__item "><a lang="hu" hrefLang="hu" rel="alternate"
                                                                            href="#language_hu"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">magyar</a></li>
                                    <li class="ecl-language-list__item "><a lang="mt" hrefLang="mt" rel="alternate"
                                                                            href="#language_mt"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">Malti</a></li>
                                    <li class="ecl-language-list__item "><a lang="nl" hrefLang="nl" rel="alternate"
                                                                            href="#language_nl"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">Nederlands</a></li>
                                    <li class="ecl-language-list__item "><a lang="pl" hrefLang="pl" rel="alternate"
                                                                            href="#language_pl"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">polski</a></li>
                                    <li class="ecl-language-list__item "><a lang="pt" hrefLang="pt" rel="alternate"
                                                                            href="#language_pt"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">português</a></li>
                                    <li class="ecl-language-list__item "><a lang="ro" hrefLang="ro" rel="alternate"
                                                                            href="#language_ro"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">română</a></li>
                                    <li class="ecl-language-list__item "><a lang="sk" hrefLang="sk" rel="alternate"
                                                                            href="#language_sk"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">slovenčina</a></li>
                                    <li class="ecl-language-list__item "><a lang="sl" hrefLang="sl" rel="alternate"
                                                                            href="#language_sl"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">slovenščina</a></li>
                                    <li class="ecl-language-list__item "><a lang="fi" hrefLang="fi" rel="alternate"
                                                                            href="#language_fi"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">suomi</a></li>
                                    <li class="ecl-language-list__item "><a lang="sv" hrefLang="sv" rel="alternate"
                                                                            href="#language_sv"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone">svenska</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</header>

<div class="ecl-page-header" style="background-color: #343a40">
    <div class="ecl-container">
        <nav class="ecl-page-header__breadcrumb ecl-breadcrumb" aria-label="You are here:" data-ecl-breadcrumb="true">
            <ol class="ecl-breadcrumb__container"  style="background-color: #343a40; border-bottom: 0px solid #fff;">
            </ol>
        </nav>
        <h1 class="ecl-page-header__title ecl-u-type-color-white" data-i18n="s-site-title">Re3gistry sandbox - admin interface</h1>
    </div>
</div>


<div class="hb4">
    <div class="container">
        <%@include file="menu.inc.jsp" %>        
    </div>
</div>
