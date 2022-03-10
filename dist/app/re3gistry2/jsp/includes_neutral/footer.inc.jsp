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

<footer class="ecl-footer-core"  style="background-color: #343a40; margin-top: 15px">
    <div class="ecl-container ecl-footer-standardised__container">
        <section class="ecl-footer-core__section ecl-footer-core__section1">
            <a href="https://github.com/ec-jrc/re3gistry/releases"
               class="ecl-footer-core__title ecl-link ecl-link--standalone">Re3gistry version 2.3.0</a>
            <div class="ecl-footer-core__description"><a data-i18n-link="l-ec-website" class="ecl-link ecl-link--standalone" 
                                                         href="https://inspire-sandbox.jrc.ec.europa.eu/registry"
                                                         aria-label="European Commission">
                    <img class="mt-2" src="./res/img/logo-2.png" alt="" width="30%" height="50%">
                </a></div>
            <div class="ecl-footer-core__description">Reference codes management tool</div>
        </section>
        <div class="ecl-footer-standardised__section2">
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">About</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item"><a href="https://joinup.ec.europa.eu/collection/are3na/solution/re3gistry/about" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Re3gistry features</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/ec-jrc/re3gistry/releases" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Get the code</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://joinup.ec.europa.eu/collection/are3na/solution/re3gistry/best-practices-registers-and-registries" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Best practices</a></li>
                </ul>
            </section>
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">
                    Re3gistry API
                </div>
                <ul class="ecl-footer-standardised__list ecl-footer-standardised__list--inline">
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire-sandbox.jrc.ec.europa.eu/registry/api.html" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Access API</a></li>
                </ul>
            </section>
        </div>
        <div class="ecl-footer-standardised__section3">
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">Documentation</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/ec-jrc/re3gistry/blob/master/documentation/user-manual.md" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">User manual</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/ec-jrc/re3gistry/blob/master/documentation/administrator-manual.md" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Administrator manual</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/ec-jrc/re3gistry/blob/master/documentation/developer-manual.md" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Developer manual</a></li>
                </ul>
            </section>
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">Stay tuned</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item">
                        <a href="/registry/release-note.xml" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before">
                            <svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#branded--rss"></use>
                            </svg>&nbsp;<span class="ecl-link__label">Re3gistry sandbox RSS feed</span>
                        </a>
                    </li>   
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/ec-jrc/re3gistry" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Join the Re3gistry community</a></li>
                </ul>
            </section>
        </div>
    </div>
</footer>

<div class="overlay-loader">
    <div class="load-feedback">
        <svg focusable="false" aria-hidden="true" class="ecl-icon ecl-icon--m ecl-icon--inverted custom-spinner">
        <use xlink:href="./res/ecl-v2/static/media/icons.svg#general--spinner"></use>
        </svg> <span class="ecl-u-type-m ecl-u-type-color-white">Loading ...</span>
    </div>
</div>
