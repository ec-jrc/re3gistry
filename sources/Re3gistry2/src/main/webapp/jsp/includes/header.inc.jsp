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
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="java.util.List"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager"%>
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
<!--            <div >
                <h3><a href="https://github.com/ec-jrc/re3gistry" class="mt-5 mb-5 text-center">Frontend panel</a></h3>
            </div>-->
            
            <div class="ecl-site-header__selector"><a class="ecl-link ecl-link--standalone ecl-site-header__selector-link"
                                                      href="" data-ecl-language-selector="true">${localization.getString("label.language.current")}<span class="ecl-site-header__language-icon"><svg
                            focusable="false" aria-hidden="true" class="ecl-icon ecl-icon--m">
                        <use xlink:href="./res/ecl-v2/static/media/icons.svg#general--language"></use>
                        </svg><span class="ecl-site-header__language-code">${localization.getString("label.language.current.code")}</span></span></a>
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
                                    
                                    <%
                                        EntityManager localEntityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
                                        RegLanguagecodeManager manager = new RegLanguagecodeManager(localEntityManager);
                                        List <RegLanguagecode> activeLanguages = manager.getAllActive();
                                        
                                        int listSize = activeLanguages.size();                                                     
                                        List<RegLanguagecode> firstColumn = new ArrayList<RegLanguagecode>(activeLanguages.subList(0, (listSize) / 2));
                                        List<RegLanguagecode> secondColumn = new ArrayList<RegLanguagecode>(activeLanguages.subList((listSize) / 2, listSize));
                                                                                                   
                                        for(int i=0; i<firstColumn.size(); i++){
                                        %>
                                        <li class="ecl-language-list__item "><a lang='<%=firstColumn.get(i).getIso6391code()%>' hrefLang='<%=firstColumn.get(i).getIso6391code()%>' rel="alternate"
                                                                                href=".<%=WebConstants.PAGE_URINAME_CHANGELOCALE%>?<%=BaseConstants.KEY_REQUEST_LANGUAGE%>=<%=firstColumn.get(i).getIso6391code()%>"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone"> <%= firstColumn.get(i).getLabel() %></a></li>
                                        <%
                                        }

                                        %>                                
                                    </ul>
                            </div>
                            <div class="ecl-language-list__column ecl-col-12 ecl-col-lg-4">
                                <ul class="ecl-language-list__list">
                                    <% 
                                    
                                        for(int y=0; y<secondColumn.size(); y++){
                                        %>
                                        <li class="ecl-language-list__item "><a lang='<%=secondColumn.get(y).getIso6391code()%>' hrefLang='<%=secondColumn.get(y).getIso6391code()%>' rel="alternate"
                                                                            href=".<%=WebConstants.PAGE_URINAME_CHANGELOCALE%>?<%=BaseConstants.KEY_REQUEST_LANGUAGE%>=<%=secondColumn.get(y).getIso6391code()%>"
                                                                            class="ecl-language-list__link ecl-link ecl-link--standalone"> <%= secondColumn.get(y).getLabel() %></a></li>
                                        <%
                                        }
                                    
                                    
                                    %>
                                    
                                    
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
