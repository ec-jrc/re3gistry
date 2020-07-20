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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldmappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemclassUuidHelper;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class RegItemclassHandler {

    // Init logger
    Logger logger;

    // System localization
    ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();

    /**
     * This method initializes the class
     *
     * @throws Exception
     */
    public RegItemclassHandler() throws Exception {
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    /**
     * This method save a new RegItemclass in the database
     *
     * @param itemclassLocalId
     * @param itemclassTypeUuid
     * @param parentItemclassUuid
     * @param itemclassBaseuri
     * @return
     * @throws Exception
     */
    public String saveNewRegItemclass(String itemclassLocalId, String itemclassTypeUuid, String parentItemclassUuid, String itemclassBaseuri) throws Exception {

        // Creating the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Instantiating managers
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManager);

        String operationSuccess = null;

        RegItemclasstype regItemclassType;
        try {
            // Getting the RegItemclassType
            regItemclassType = regItemclasstypeManager.get(itemclassTypeUuid);

            // Getting the eventual RegItemclass parent
            RegItemclass regItemclassParent = null;
            if (parentItemclassUuid != null && parentItemclassUuid.length() > 0) {
                try {
                    regItemclassParent = regItemclassManager.get(parentItemclassUuid);
                } catch (NoResultException e) {
                }
            }

            // Getting the RegStatus valid
            RegStatusManager regStatusManager = new RegStatusManager(entityManager);
            RegStatus regStatusValid;
            try {
                regStatusValid = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_VALID);

                // Creating the new object
                RegItemclass regItemclass = new RegItemclass();
                String newUuid = RegItemclassUuidHelper.getUuid(itemclassLocalId, ((regItemclassParent != null) ? regItemclassParent.getLocalid() : null), regItemclassType);

                // Check if the regItemclass is already available
                try {
                    regItemclassManager.get(newUuid);
                    // It it is available, sending an error
                    operationSuccess = systemLocalization.getString("error.itemclass.localid.available");

                } catch (NoResultException e) {

                    // Check if the localid has already been used
                    try {
                        regItemclassManager.getByLocalid(itemclassLocalId);
                        operationSuccess = systemLocalization.getString("error.itemclass.localid.available");

                    } catch (NoResultException ee) {

                        // If it is new, filling the object
                        regItemclass.setUuid(newUuid);
                        regItemclass.setLocalid(itemclassLocalId);
                        regItemclass.setBaseuri(itemclassBaseuri);
                        regItemclass.setInsertdate(new Date());
                        regItemclass.setRegItemclassParent(regItemclassParent);
                        regItemclass.setRegStatus(regStatusValid);
                        regItemclass.setRegItemclasstype(regItemclassType);
                        regItemclass.setSystemitem(Boolean.FALSE);
                        regItemclass.setActive(Boolean.TRUE);

                        // Get max procedureorder integer
                        int maxDataprocedureorder = regItemclassManager.getRegItemclassMaxDataprocedureorder();
                        regItemclass.setDataprocedureorder(++maxDataprocedureorder);

                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Store the RegItemclass
                            regItemclassManager.add(regItemclass);
                            entityManager.getTransaction().commit();

                            // Getting the regField object
                            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
                            RegField regFieldDefinition = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_DEFINITION_LOCALID);
                            RegField regFieldContentsummary = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID);
                            RegField regFieldStatus = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_STATUS_LOCALID);
                            RegField regFieldRegistryManager = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTRYMANAGER);
                            RegField regFieldRegisterManager = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER);
                            RegField regFieldRegisterOwner = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER);
                            RegField regFieldControlBody = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY);
                            RegField regFieldSubmittingorganizations = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS);

                            // Mapping the required fields (Label, Status)
                            RegFieldmapping labelRegFieldMapping = createRequiredRegFieldmapping(regFieldLabel, regItemclass, regStatusValid, 0);
                            RegFieldmapping definitionRegFieldMapping = createRequiredRegFieldmapping(regFieldDefinition, regItemclass, regStatusValid, 1);
                            RegFieldmapping contentsummaryRegFieldMapping = createRequiredRegFieldmapping(regFieldContentsummary, regItemclass, regStatusValid, 1);
                            RegFieldmapping statusRegFieldMapping = createRequiredRegFieldmapping(regFieldStatus, regItemclass, regStatusValid, 2);
                            RegFieldmapping registryManagerRegFieldMapping = createRequiredRegFieldmapping(regFieldRegistryManager, regItemclass, regStatusValid, 3);
                            RegFieldmapping registerManagerRegFieldMapping = createRequiredRegFieldmapping(regFieldRegisterManager, regItemclass, regStatusValid, 3);
                            RegFieldmapping registerOwnerRegFieldMapping = createRequiredRegFieldmapping(regFieldRegisterOwner, regItemclass, regStatusValid, 4);
                            RegFieldmapping controlBodyRegFieldMapping = createRequiredRegFieldmapping(regFieldControlBody, regItemclass, regStatusValid, 5);
                            RegFieldmapping submittingOrganizationRegFieldMapping = createRequiredRegFieldmapping(regFieldSubmittingorganizations, regItemclass, regStatusValid, 6);

                            registryManagerRegFieldMapping.setTablevisible(false);
                            registerManagerRegFieldMapping.setTablevisible(false);
                            registerOwnerRegFieldMapping.setTablevisible(false);
                            controlBodyRegFieldMapping.setTablevisible(false);
                            submittingOrganizationRegFieldMapping.setTablevisible(false);
                            submittingOrganizationRegFieldMapping.setMultivalue(true);

                            // Storing the RegFieldmappings 
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }

                            // Setting the mandatory fields for all itemclass type
                            regFieldmappingManager.add(labelRegFieldMapping);
                            regFieldmappingManager.add(statusRegFieldMapping);

                            if (regItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                                // Setting the mandatory field forn registers
                                regFieldmappingManager.add(registryManagerRegFieldMapping);
                                regFieldmappingManager.add(contentsummaryRegFieldMapping);
                            } else if (regItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                                // Setting the mandatory field forn registers
                                regFieldmappingManager.add(registerManagerRegFieldMapping);
                                regFieldmappingManager.add(registerOwnerRegFieldMapping);
                                regFieldmappingManager.add(controlBodyRegFieldMapping);
                                regFieldmappingManager.add(submittingOrganizationRegFieldMapping);
                                regFieldmappingManager.add(contentsummaryRegFieldMapping);
                            } else if (regItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                                regFieldmappingManager.add(definitionRegFieldMapping);
                            }

                            entityManager.getTransaction().commit();

                        }
                        /* ## End Synchronized ## */
                    }
                }
            } catch (NoResultException e) {
                logger.error("@ RegItemclassHandler.saveNewRegItemclass: unable to get the RegStatus valid.", e);
                operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
            }
        } catch (NoResultException e) {
            logger.error("@ RegItemclassHandler.saveNewRegItemclasss: unable to get the RegItemclasstype.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error("@ RegItemclassHandler.saveNewRegItemclasss: generic error.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return operationSuccess;
    }

    /**
     * This method change the procedure order field in the RegItemclass
     *
     * @param itemclassUuid
     * @param newPosition
     * @return
     * @throws java.lang.Exception
     */
    public String reorderRegItemclass(String itemclassUuid, int newPosition) throws Exception {

        // Creating the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Instantiating managers
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);

        String operationSuccess = null;
        try {
            // Getting the RegItemclass
            RegItemclass temp = regItemclassManager.get(itemclassUuid);

            if (temp != null) {

                // The writing operation on the Database are synchronized
                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    // Reordering the RegItemclass

                    temp.setDataprocedureorder(newPosition);

                    regItemclassManager.update(temp);

                    entityManager.getTransaction().commit();
                }
                /* ## End Synchronized ## */

            }
        } catch (NoResultException e) {
            logger.error("@ RegItemclassHandler.reorderRegItemclass: unable to get the RegItemclass.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error("@ RegItemclassHandler.reorderRegItemclass: generic error.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationSuccess;
    }

    public boolean removeRegItemclass(RegItemclass regItemclass, HttpServletRequest request) throws Exception {
        String operation = null;
        boolean success = false;

        // Creating the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        try {

            // Instantiating managers
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);

            // Check if the itemclass has child
            try {
                List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItemclass);
                if (!childItemclasses.isEmpty()) {
                    operation = systemLocalization.getString(BaseConstants.KEY_ERROR_NOTDELETABLE_CONTENTCLASSASSOCIATED);
                }
            } catch (NoResultException e) {
            }

            // Check if there are item associated
            try {
                List<RegItem> childItems = regItemManager.getAll(regItemclass);
                if (!childItems.isEmpty()) {
                    operation = systemLocalization.getString(BaseConstants.KEY_ERROR_NOTDELETABLE_ITEMSASSOCIATED);
                }
            } catch (NoResultException e) {
            }

            if (operation != null && !operation.isEmpty()) {
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operation);
            }

            // If there are no element associated to the itemclass, deleting it
            if (operation == null) {

                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }

                    // removing all associated fields
                    RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
                    List<RegFieldmapping> regFieldmappings = regFieldmappingManager.getAll(regItemclass);
                    for (RegFieldmapping tmp : regFieldmappings) {
                        regFieldmappingManager.delete(tmp);
                    }

                    regItemclassManager.delete(regItemclass);

                    entityManager.getTransaction().commit();
                }
                operation = systemLocalization.getString(BaseConstants.KEY_ITEMCLASS_SUCCESS_DELETABLE);
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE, operation);
                success = true;
                /* ## End Synchronized ## */
            }

        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return success;
    }

    public String editRegitemclass(RegItemclass regItemclass, String localId, String baseUri, HttpServletRequest request) throws Exception {
        String operation = null;

        // Creating the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        try {

            // Instantiating managers
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);

            // Check if the itemclass has child
            try {
                List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItemclass);
                if (!childItemclasses.isEmpty()) {
                    operation = systemLocalization.getString(BaseConstants.KEY_ERROR_NOTDELETABLE_CONTENTCLASSASSOCIATED);
                }
            } catch (NoResultException e) {
            }

            // Check if there are item associated
            try {
                List<RegItem> childItems = regItemManager.getAll(regItemclass);
                if (!childItems.isEmpty()) {
                    operation = systemLocalization.getString(BaseConstants.KEY_ERROR_NOTDELETABLE_ITEMSASSOCIATED);
                }
            } catch (NoResultException e) {
            }

            if (operation != null && !operation.isEmpty()) {
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operation);
            }

            // If there are no element associated to the itemclass, updating it
            if (operation == null) {

                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }

                    regItemclass.setLocalid(localId);
                    regItemclass.setBaseuri(baseUri);

                    regItemclassManager.update(regItemclass);

                    entityManager.getTransaction().commit();
                }
                operation = systemLocalization.getString(BaseConstants.KEY_ITEMCLASS_SUCCESS_EDITABLE);
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE, operation);
                /* ## End Synchronized ## */
            }
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return operation;
    }

    private static RegFieldmapping createRequiredRegFieldmapping(RegField regFieldLabel, RegItemclass regItemclass, RegStatus regStatusValid, int position) throws Exception {
        RegFieldmapping tmpRegFieldMapping = new RegFieldmapping();

        String labelFieldMappingUuid = RegFieldmappingUuidHelper.getUuid(regFieldLabel, regItemclass);
        tmpRegFieldMapping.setUuid(labelFieldMappingUuid);
        tmpRegFieldMapping.setInsertdate(new Date());
        tmpRegFieldMapping.setListorder(position);
        tmpRegFieldMapping.setRegField(regFieldLabel);
        tmpRegFieldMapping.setRegItemclass(regItemclass);
        tmpRegFieldMapping.setRegStatus(regStatusValid);
        tmpRegFieldMapping.setRequired(true);
        tmpRegFieldMapping.setTablevisible(true);
        tmpRegFieldMapping.setMultivalue(false);
        tmpRegFieldMapping.setHashref(false);
        tmpRegFieldMapping.setHidden(false);

        return tmpRegFieldMapping;
    }
}
