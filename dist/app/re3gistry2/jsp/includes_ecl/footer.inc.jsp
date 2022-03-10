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
<footer class="ecl-footer-core mt-5">
    <div class="ecl-container ecl-footer-standardised__container">
        <section class="ecl-footer-core__section ecl-footer-core__section1">
            <a href="https://github.com/ec-jrc/re3gistry/releases"
               class="ecl-footer-core__title ecl-link ecl-link--standalone">Re3gistry version 2.3.0</a>
            <div class="ecl-footer-core__description">The INSPIRE Registry is managed by the European Commission Joint Research Centre B6 Unit</div>
        </section>
        <div class="ecl-footer-standardised__section2">
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">Contact us</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item"><a href="mailto:jrc-inspire-support@ec.europa.eu?subject=%5BRegistry%20Helpdesk%5D" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Contact information</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://github.com/INSPIRE-MIF/helpdesk-registry/" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Helpdesk</a></li>
                </ul>
            </section>
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">Follow us on</div>
                <ul class="ecl-footer-standardised__list ecl-footer-standardised__list--inline">
                    <li class="ecl-footer-standardised__list-item"><a href="https://www.facebook.com/groups/inspiredirective/" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#branded--facebook"></use>
                            </svg>&nbsp;<span class="ecl-link__label">Facebook</span></a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://twitter.com/INSPIRE_EU" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#branded--twitter"></use>
                            </svg>&nbsp;<span class="ecl-link__label">Twitter</span></a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://www.linkedin.com/groups/1066897" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#branded--linkedin"></use>
                            </svg>&nbsp;<span class="ecl-link__label">Linkedin</span></a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="./res/release-note.xml" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                            <use xlink:href="./res/ecl-v2/static/media/icons.svg#branded--rss"></use>
                            </svg>&nbsp;<span class="ecl-link__label">RSS feed</span></a></li>        
                </ul>
            </section>
        </div>
        <div class="ecl-footer-standardised__section3">
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">About us</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item"><a href="./res/about.html" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Information about the INSPIRE Registry</a></li>
                </ul>
            </section>
            <section class="ecl-footer-standardised__section">
                <div class="ecl-footer-standardised__title ecl-footer-standardised__title--separator">Related sites</div>
                <ul class="ecl-footer-standardised__list">
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire.ec.europa.eu/" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">INSPIRE Knowledge Base</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire-geoportal.ec.europa.eu/" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">INSPIRE Geoportal</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire.ec.europa.eu/forum" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">INSPIRE Community Forum</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire.ec.europa.eu/registry" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">INSPIRE Registry</a></li>
                    <li class="ecl-footer-standardised__list-item"><a href="https://inspire.ec.europa.eu/register-federation" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">INSPIRE Register Federation</a></li>
                </ul>
            </section>
        </div>
        <section class="ecl-footer-standardised__section ecl-footer-standardised__section7 ">
            <a href="https://ec.europa.eu/info/index_en" class="ecl-footer-standardised__title ecl-link ecl-link--standalone">
                European Commission
            </a>
        </section>
        <section class="ecl-footer-standardised__section ecl-footer-standardised__section8">
            <ul class="ecl-footer-standardised__list">
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/about-european-commission/contact_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Contact the European Commission</a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://europa.eu/european-union/contact/social-networks_en#n:+i:4+e:1+t:+s" class="ecl-footer-standardised__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-after"><span class="ecl-link__label">Follow the European Commission on social media</span>&nbsp;<svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs">
                        <use xlink:href="./res/ecl-v2/static/media/icons.svg#ui--external"></use>
                        </svg></a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/resources-partners_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Resources for partners</a></li>
            </ul>
        </section>
        <section class="ecl-footer-standardised__section ecl-footer-standardised__section9">
            <ul class="ecl-footer-standardised__list">
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/language-policy_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Language policy</a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/cookies_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Cookies</a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/privacy-policy_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Privacy policy</a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/legal-notice_en" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Legal notice</a></li>
                <li class="ecl-footer-standardised__list-item"><a href="https://ec.europa.eu/info/brexit-content-disclaimer" class="ecl-footer-standardised__link ecl-link ecl-link--standalone">Brexit content disclaimer</a></li>
            </ul>
        </section>
    </div>
</footer>