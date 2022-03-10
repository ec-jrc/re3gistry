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
<c:if test="${(not empty properties.getProperty('application.headingmessage.class')) && (not empty properties.getProperty('application.headingmessage.text'))}">
<div class="ecl-message ecl-message--info" style="padding-bottom: 0.1em; padding-top: 0.5em" data-ecl-message="" role="alert" data-ecl-auto-init="Message">
        <div class="ecl-message__content" style="text-align: center">
            <div class="ecl-message__title" style="max-width: 100%">
                ${properties.getProperty('application.headingmessage.text')}
            </div>
        </div>
    </div>
</c:if>
    