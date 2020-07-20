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
package eu.europa.ec.re3gistry2.javaapi.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class RegInstallationStepCleanInstallationSummaryHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManagerRe3gistry2;

    // System localization
    ResourceBundle systemLocalization;
    private final HttpServletRequest request;

    /**
     * This method initializes the class
     *
     * @param request
     * @throws Exception
     */
    public RegInstallationStepCleanInstallationSummaryHandler(HttpServletRequest request) throws Exception {
        this.request = request;
        entityManagerRe3gistry2 = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();

        saveSession();
    }

    private void saveSession() throws Exception {
        HttpSession session = request.getSession();

        String[] activeLanguageCodes = request.getParameterValues(BaseConstants.KEY_REQUEST_ACTIVE_LANGUAGECODES);
        String masterLanguageCode = request.getParameter(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
        
        session.setAttribute(BaseConstants.KEY_REQUEST_ACTIVE_LANGUAGECODES, activeLanguageCodes);
        session.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguageCode);

        String registryLocalID = request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_LOCALID);
        String registryBaseURI = request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_BASEURI);
        String registryLabel = request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_LABEL);
        String registryContentSummary = request.getParameter(BaseConstants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY);
        
        session.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_LOCALID, registryLocalID);
        session.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_BASEURI, registryBaseURI);
        session.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_LABEL, registryLabel);
        session.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY, registryContentSummary);
    }

}
