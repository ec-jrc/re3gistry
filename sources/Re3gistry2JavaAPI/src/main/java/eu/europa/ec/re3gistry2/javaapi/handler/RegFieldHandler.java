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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldtypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldtype;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import java.util.Date;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

public class RegFieldHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManager;

    // System localization
    ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();

    /**
     * This method initializes the class
     *
     * @throws Exception
     */
    public RegFieldHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    public String editRegField(String formFieldUuid, String formLocalizationUuid, String formRegLanguagecodeUuid, String value) throws Exception {

        // initializing managers
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        String operationResult = null;

        try {
            // Getting the RegLocalization object
            try {
                RegLocalization regLocalization = regLocalizationManager.get(formLocalizationUuid);
                regLocalization.setValue(value);

                // The writing operation on the Database are synchronized
                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    // Store the RegItemclass
                    regLocalizationManager.update(regLocalization);
                    entityManager.getTransaction().commit();
                }
                /* ## End Synchronized ## */

            } catch (NoResultException e) {
                // If the RegLocalization is not available, this is the addition
                // of a new tralslation (new RegLocalization)

                try {
                    //Getting the regField to create the new RegLocalization
                    RegField regField = regFieldManager.get(formFieldUuid);

                    try {
                        // Getting the RegLanguagecode
                        RegLanguagecode regLanguagecode = regLanguagecodeManager.get(formRegLanguagecodeUuid);

                        String newLocalizationUuid = RegLocalizationUuidHelper.getUuid(0, regLanguagecode, null, regField);

                        RegLocalization newLocalization = new RegLocalization();

                        newLocalization.setUuid(newLocalizationUuid);
                        newLocalization.setInsertdate(new Date());
                        newLocalization.setFieldValueIndex(0);
                        newLocalization.setHref(null);
                        newLocalization.setRegField(regField);
                        newLocalization.setRegItem(null);
                        newLocalization.setRegLanguagecode(regLanguagecode);
                        newLocalization.setRegRelationReference(null);
                        newLocalization.setValue(value);

                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Store the RegItemclass
                            regLocalizationManager.add(newLocalization);
                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */
                    } catch (NoResultException e1) {
                        logger.error("@ RegFieldHandler.editRegField: unable to retrieve the RegLanguagecode.", e);
                        operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                    }
                } catch (NoResultException e1) {
                    logger.error("@ RegFieldHandler.editRegField: unable to retrieve the RegFields.", e);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                }
            }
        } catch (NoResultException e) {
            logger.error("@ RegFieldHandler.editRegField: unable to retrieve the RegItemclass.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegFieldHandler.editRegField: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }

    /**
     *
     * @param formLocalId
     * @param formLabel
     * @param formFieldtypeUuid
     * @param formItemclassreferenceUuid
     * @return String containing eventual error messages (null if no errors)
     */
    public String newRegField(String formLocalId, String formLabel, String formFieldtypeUuid, String formItemclassreferenceUuid) {

        // Init managers
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        String operationResult = null;

        try {
            // Getting the RegLanguagecode object (master language)
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

            try {
                // Getting the RegStatus object
                RegStatus regStatusValid = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_VALID);

                try {
                    // Getting the RegFieldtype object
                    RegFieldtype regFieldtype = regFieldtypeManager.get(formFieldtypeUuid);

                    // New field uuid
                    String fieldUuid = RegFieldUuidHelper.getUuid(formLocalId, regFieldtype);

//                    check if the field exist already
                    try {
                        regFieldManager.get(fieldUuid);
                        logger.error("@ RegFieldHandler.newRegField: The filed with the localID:" + fieldUuid + " exist already.", "");
                        operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_FIELD_EXIST_LOCALID);
                    } catch (Exception ex) {
                        
                        RegField newField = new RegField();
                        newField.setUuid(fieldUuid);
                        newField.setInsertdate(new Date());
                        newField.setIstitle(false);
                        newField.setLocalid(formLocalId);
                        newField.setRegFieldtype(regFieldtype);
                        newField.setRegRoleReference(null);
                        newField.setRegStatus(regStatusValid);

                        try {
                            if (formItemclassreferenceUuid != null && formItemclassreferenceUuid.length() > 0) {
                                // Getting the RegItemclass object
                                RegItemclass regItemclassReference = regItemclassManager.get(formItemclassreferenceUuid);
                                newField.setRegItemclassReference(regItemclassReference);
                            }
                        } catch (NoResultException e) {
                        }

                        // Creating the localization
                        String localizationUuid = RegLocalizationUuidHelper.getUuid(0, masterLanguage, null, newField);
                        RegLocalization newLocalization = new RegLocalization();
                        newLocalization.setUuid(localizationUuid);
                        newLocalization.setFieldValueIndex(0);
                        newLocalization.setInsertdate(new Date());
                        newLocalization.setHref(null);
                        newLocalization.setRegField(newField);
                        newLocalization.setRegItem(null);
                        newLocalization.setRegLanguagecode(masterLanguage);
                        newLocalization.setValue(formLabel);

                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Store the RegField
                            regFieldManager.update(newField);

                            //Store the regLocalization
                            regLocalizationManager.add(newLocalization);

                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */
                    }
                } catch (NoResultException e) {
                    logger.error("@ RegFieldHandler.newRegField: unable to retrieve the RegFieldtype.", e);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                }
            } catch (NoResultException e) {
                logger.error("@ RegFieldHandler.newRegField: unable to retrieve the RegStatus.", e);
                operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
            }
        } catch (NoResultException e) {
            logger.error("@ RegFieldHandler.newRegField: unable to retrieve the RegLanguagecode.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegFieldHandler.newRegField: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }
}
