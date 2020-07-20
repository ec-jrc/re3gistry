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
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class RegInstallationStepCleanInstallationProfileHandler {

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
    public RegInstallationStepCleanInstallationProfileHandler(HttpServletRequest request) throws Exception {
        this.request = request;
        entityManagerRe3gistry2 = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();

        getAllLanguages();
    }

    private void getAllLanguages() throws Exception {
        // Getting all the languages
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
        List<RegLanguagecode> regLanguagecodes = regLanguagecodeManager.getAllActive();
        request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES, regLanguagecodes);
    }

}
