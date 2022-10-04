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
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class RegInstallationStepCleanInstallationProcessHandler {

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
     * @param entityManagerRe3gistry2
     * @throws Exception
     */
    public RegInstallationStepCleanInstallationProcessHandler(HttpServletRequest request, EntityManager entityManagerRe3gistry2) throws Exception {
        this.request = request;
        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();

        createRegistryFromSession();
    }

    private void createRegistryFromSession() throws Exception {
        if (!entityManagerRe3gistry2.getTransaction().isActive()) {
            entityManagerRe3gistry2.getTransaction().begin();
        }

        boolean commit = false;

        HttpSession session = request.getSession();
        RegUser currentRegUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);
        if (currentRegUser == null) {
            throw new Exception("There is no user");
        }

        RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);

        String[] activeLanguageCodes = (String[]) session.getAttribute(BaseConstants.KEY_REQUEST_ACTIVE_LANGUAGECODES);
        String masterLanguageCode = (String) session.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
        
        regInstallationHandler.saveActiveLanguages(activeLanguageCodes, commit);
        RegLanguagecode masterLanguage = regInstallationHandler.saveMasterLanguage(masterLanguageCode, commit);

        if (!masterLanguage.getIso6391code().equals("en")) {
            RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManagerRe3gistry2);
            List<RegStatuslocalization> regStatuslocalizations = regStatuslocalizationManager.getAll();
            for (RegStatuslocalization regStatuslocalization : regStatuslocalizations) {
                regStatuslocalization.setRegLanguagecode(masterLanguage);

                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.merge(regStatuslocalization);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            }
        }

        String registryLocalID = (String) session.getAttribute(BaseConstants.KEY_REQUEST_REGISTRY_LOCALID);
        String registryBaseURI = (String) session.getAttribute(BaseConstants.KEY_REQUEST_REGISTRY_BASEURI);
        String registryLabel = (String) session.getAttribute(BaseConstants.KEY_REQUEST_REGISTRY_LABEL);
        String registryContentSummary = (String) session.getAttribute(BaseConstants.KEY_REQUEST_REGISTRY_CONTENT_SUMMARY);

        RegItem registryItem = regInstallationHandler.createRegistry(registryLocalID, registryBaseURI, registryLabel, registryContentSummary, currentRegUser, commit);

        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_DEFINITION_LOCALID, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, commit);
        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_MANDATORY_STATUS_LOCALID, BaseConstants.KEY_FIELDTYPE_STATUS_UUID, null, commit);

        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER, BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, BaseConstants.REGISTER_MANAGER_ROLE_UUID, commit);
        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER, BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, BaseConstants.REGISTER_OWNER_ROLE_UUID, commit);
        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY, BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, BaseConstants.CONTROL_BODY_ROLE_UUID, commit);
        regInstallationHandler.addFieldAndLocalizationDefaultByName(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS, BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, BaseConstants.SUBMITTING_ORGANIZATION_ROLE_UUID, commit);

        changeMasterLanguageForStatusLocalization(masterLanguage);
        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.getTransaction().commit();
        } catch (Exception e) {
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.getTransaction().setRollbackOnly();
                throw new Exception(e.getMessage());
            } catch (Exception ex) {
                throw new Exception(e.getMessage());
            }
        } finally {
            try {
                entityManagerRe3gistry2.close();
            } catch (Exception e) {
                // Never mind this
            }
        }

        session.setAttribute(BaseConstants.KEY_SESSION_USER, null);

        session.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
    }

    private void changeMasterLanguageForStatusLocalization(RegLanguagecode masterLanguage) throws Exception {
        RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManagerRe3gistry2);
        try {
            for (RegStatuslocalization regStatuslocalization : regStatuslocalizationManager.getAll()) {
                regStatuslocalization.setRegLanguagecode(masterLanguage);
                entityManagerRe3gistry2.merge(regStatuslocalization);
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
