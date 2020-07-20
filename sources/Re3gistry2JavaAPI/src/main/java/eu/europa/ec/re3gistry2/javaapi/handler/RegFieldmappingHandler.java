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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldmappingUuidHelper;
import java.util.Date;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

public class RegFieldmappingHandler {

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
    public RegFieldmappingHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    /**
     * This method add a new RegFieldMapping
     * @param formItemclassUuid
     * @param formFieldUuid
     * @return String containing eventual error messages (null if no errors)
     */
    public String addNewRegFieldMapping(String formItemclassUuid, String formFieldUuid) {

        // Initializing managers
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        String operationResult = null;

        try {
            // Getting the regItemclass object
            RegItemclass regItemclass = regItemclassManager.get(formItemclassUuid);

            // Getting the RegField object
            try {
                RegField regField = regFieldManager.get(formFieldUuid);

                // Getting the valid status
                try {
                    RegStatus regStatusValid = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_VALID);

                    // Creating the new RegFieldmapping uuid
                    String newUuid = RegFieldmappingUuidHelper.getUuid(regField, regItemclass);

                    // Check if the regFieldmapping is already available
                    try {
                        regFieldmappingManager.get(newUuid);
                    } catch (NoResultException e) {
                        // If it is not available, inserting it
                        RegFieldmapping regFieldmapping = new RegFieldmapping();
                        regFieldmapping.setUuid(newUuid);
                        regFieldmapping.setHashref(false);
                        regFieldmapping.setHidden(false);
                        regFieldmapping.setMultivalue(false);
                        regFieldmapping.setRequired(false);
                        regFieldmapping.setTablevisible(false);
                        regFieldmapping.setRegField(regField);
                        regFieldmapping.setRegItemclass(regItemclass);
                        regFieldmapping.setInsertdate(new Date());
                        regFieldmapping.setRegStatus(regStatusValid);
                        
                        /// Handling special cases in witch some mapping has some predefined valued
                        if(regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP) && regField.getRegRoleReference().getLocalid().equals(BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION)){
                            regFieldmapping.setMultivalue(true);
                        }

                        // Getting the max listorder
                        int maxListorder = regFieldmappingManager.getRegFieldmappingMaxListorder(regItemclass);
                        regFieldmapping.setListorder(++maxListorder);

                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Store the RegItemclass
                            regFieldmappingManager.add(regFieldmapping);
                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */
                    }
                } catch (NoResultException e) {
                    logger.error("@ RegFieldmappingHandler.addNewRegFieldMapping: unable to retrieve the RegStatus valid.", e);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                }
            } catch (NoResultException e) {
                logger.error("@ RegFieldmappingHandler.addNewRegFieldMapping: unable to retrieve the RegField.", e);
                operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
            }
        } catch (NoResultException e) {
            logger.error("@ RegFieldmappingHandler.addNewRegFieldMapping: unable to retrieve the RegItemclass.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegFieldmappingHandler.addNewRegFieldMapping: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }

    /**
     * This method remove the RegFieldmapping
     *
     * @param formFieldmappingUuid
     * @return String containing eventual error messages (null if no errors)
     */
    public String removeRegFieldMapping(String formFieldmappingUuid) {

        // Instantiating managers
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);

        String operationResult = null;

        try {
            // Getting the RegFieldMapping
            RegFieldmapping temp = regFieldmappingManager.get(formFieldmappingUuid);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Removing the RegFieldMapping
                regFieldmappingManager.delete(temp);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */
        } catch (Exception e) {
            logger.error("@ RegFieldmappingHandler.removeRegFieldMapping: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }

    /**
     * This method is used to change the list order of the RegFieldmapping
     *
     * @param itemclassUuid
     * @param fieldUuid
     * @param newPosition
     * @return String containing eventual error messages (null if no errors)
     */
    public String reorderRegFieldmapping(String itemclassUuid, String fieldUuid, int newPosition) {

        // Initializing managers
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);

        String operationResult = null;
        try {
            // Getting the RegItemclass passed by parameter
            RegItemclass regItemclass = regItemclassManager.get(itemclassUuid);

            try {
                // Getting the RegField passed by parameter
                RegField regField = regFieldManager.get(fieldUuid);

                try {
                    // Getting the RegItemclass
                    RegFieldmapping temp = regFieldmappingManager.getByFieldAndItemClass(regField, regItemclass);
                    if (temp != null) {
                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Reordering the RegItemclass

                            temp.setListorder(newPosition);

                            regFieldmappingManager.update(temp);

                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */
                    }
                } catch (NoResultException e) {
                    logger.error("@ RegFieldmappingHandler.reorderRegFieldmapping: unable to get the RegFieldmapping.", e);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                }
            } catch (NoResultException e) {
                logger.error("@ RegFieldmappingHandler.reorderRegFieldmapping: unable to get the RegField.", e);
                operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
            }
        } catch (NoResultException e) {
            logger.error("@ RegFieldmappingHandler.reorderRegFieldmapping: unable to get the RegItemclass.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegFieldmappingHandler.reorderRegFieldmapping: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }

    /**
     * This method is updating the properties of the RegFieldmapping
     *
     * @param formFieldUuid
     * @param formItemclassUuid
     * @param formCheckboxType
     * @param checked
     * @return String containing eventual error messages (null if no errors)
     */
    public String updateCheckbox(String formFieldUuid, String formItemclassUuid, String formCheckboxType, boolean checked) {

        // Initializing managers
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);

        String operationResult = null;
        try {
            // Getting the RegItemclass passed by parameter
            RegItemclass regItemclass = regItemclassManager.get(formItemclassUuid);

            try {
                // Getting the RegField passed by parameter
                RegField regField = regFieldManager.get(formFieldUuid);

                try {
                    // Getting the RegFieldmapping
                    RegFieldmapping temp = regFieldmappingManager.getByFieldAndItemClass(regField, regItemclass);
                    if (temp != null) {
                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Reordering the RegItemclass

                            switch (formCheckboxType) {
                                case BaseConstants.KEY_FIELD_CHECKBOX_TYPE_HREF:
                                    temp.setHashref(checked);
                                    break;
                                case BaseConstants.KEY_FIELD_CHECKBOX_TYPE_HIDDEN:
                                    temp.setHidden(checked);
                                    break;
                                case BaseConstants.KEY_FIELD_CHECKBOX_TYPE_REQUIRED:
                                    temp.setRequired(checked);
                                    break;
                                case BaseConstants.KEY_FIELD_CHECKBOX_TYPE_MULTIVALUED:
                                    temp.setMultivalue(checked);
                                    break;
                                case BaseConstants.KEY_FIELD_CHECKBOX_TYPE_TABLEVISIBLE:
                                    temp.setTablevisible(checked);
                                    break;
                                default:
                                    break;
                            }
                            
                            //Check the special cases
                            if(regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP) && regField.getRegRoleReference().getLocalid().equals(BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION)){
                                temp.setMultivalue(true);
                            }

                            regFieldmappingManager.update(temp);

                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */
                    }
                } catch (NoResultException e) {
                    logger.error("@ RegFieldmappingHandler.updateCheckbox: unable to get the RegFieldmapping.", e);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
                }
            } catch (NoResultException e) {
                logger.error("@ RegFieldmappingHandler.updateCheckbox: unable to get the RegField.", e);
                operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
            }
        } catch (NoResultException e) {
            logger.error("@ RegFieldmappingHandler.updateCheckbox: unable to get the RegItemclass.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegFieldmappingHandler.updateCheckbox: generic error.", e);
            operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }
}