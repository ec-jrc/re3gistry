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

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.InputSanitizerHelper;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.exceptions.ExceptionConstants;
import eu.europa.ec.re3gistry2.base.utility.exceptions.UnauthorizedUserException;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegActionUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemproposedRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationproposedUuidHelper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

public class RegItemproposedHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManager;

    // Synchronization object
    private static final Object sync = new Object();

    /**
     * This method initializes the class
     *
     * @throws Exception
     */
    public RegItemproposedHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
    }

    /**
     * This method handles the save of a new RegItemproposed (proposal of a new
     * item)
     *
     * @param requestParameters
     * @param regUser
     * @param localization
     * @throws Exception
     */
    public void handleRegItemproposedAddition(Map requestParameters, RegUser regUser, ResourceBundle localization) throws Exception {
        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

        // Init relation predicate
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

        // Getting the ID of the RegItem that is going to contain this element
        String[] regItemcontainetUuidTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID);
        // !!! Sanitizing input
        String regItemContainerUuid = InputSanitizerHelper.sanitizeInput(regItemcontainetUuidTmp[0]);

        // Getting the ID of the RegItemclass of the new item
        String[] regItemclassUuidTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID);
        // !!! Sanitizing input
        String regItemclassUuid = InputSanitizerHelper.sanitizeInput(regItemclassUuidTmp[0]);

        // Getting the new localID of the RegItem
        String[] regItemLocalIdTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        // !!! Sanitizing input
        String regItemLocalId = InputSanitizerHelper.sanitizeInput(regItemLocalIdTmp[0]);

        // External Value flag
        String[] externalValueTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM);
        // !!! Sanitizing input
        String externalValueStr = (externalValueTmp != null) ? InputSanitizerHelper.sanitizeInput(externalValueTmp[0]) : "";
        boolean externalValue = (externalValueStr != null && externalValueStr.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));

        // Register Federation export
        String[] registerFederationExportTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT);
        // !!! Sanitizing input
        String registerFederationExport;
        boolean registerFederationExportBol;

        if (registerFederationExportTmp != null) {
            registerFederationExport = InputSanitizerHelper.sanitizeInput(registerFederationExportTmp[0]);
            registerFederationExportBol = (registerFederationExport != null && registerFederationExport.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));
        } else {
            registerFederationExportBol = false;
        }

        // Getting the RegItem that will contain the new proposed item
        RegItem regItemContainer = null;
        try {
            regItemContainer = regItemManager.get(regItemContainerUuid);
        } catch (NoResultException e) {
        }

        // Getting the RegItemclass of the new item
        RegItemclass regItemclass = null;
        try {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            regItemclass = regItemclassManager.get(regItemclassUuid);
        } catch (NoResultException e) {
        }

        RegItem collection = null;
        if (regItemContainer != null && !regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && !regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
            collection = regItemContainer;
        }

        // Check if the URI is already available in the RegItem or RegProposeditem
        try {
            String tmpUuid = RegItemUuidHelper.getUuid(regItemLocalId, collection, regItemclass);

            regItemManager.get(tmpUuid);
            // An item with the same LocalId is already available
            throw new Exception(localization.getString("error.item.localid.available"));
        } catch (NoResultException e) {
            try {
                String tmpUuid = RegItemproposedUuidHelper.getUuid(regItemLocalId, collection, regItemclass, null);

                regItemproposedManager.get(tmpUuid);
                // An item with the same LocalId is already available
                throw new Exception(localization.getString("error.item.localid.available"));
            } catch (NoResultException e1) {

                // If the Item is not available, creating it
                //Getting the status draft
                RegStatus regStatusDraft = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

                //Creating the RegItemproposed Uuid
                String newRegItemproposedUuid = RegItemproposedUuidHelper.getUuid(regItemLocalId, collection, regItemclass, null);

                //Creating the RegItemproposed
                RegItemproposed regItemproposed = new RegItemproposed();

                regItemproposed.setUuid(newRegItemproposedUuid);
                regItemproposed.setLocalid(regItemLocalId);
                regItemproposed.setInsertdate(new Date());
                regItemproposed.setRegItemReference(null);
                regItemproposed.setRegItemclass(regItemclass);
                regItemproposed.setRegUser(regUser);
                regItemproposed.setRegStatus(regStatusDraft);
                regItemproposed.setRorExport(registerFederationExportBol);
                regItemproposed.setExternal(externalValue);

                // Setting the register
                RegItem regItemRegister = null;
                if (regItemContainer != null && !regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                    List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItemContainer, regRelationpredicateRegister);
                    try {
                        regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                    } catch (Exception e2) {
                        regItemRegister = null;
                    }
                } else {
                    regItemRegister = regItemContainer;
                }

                // Setting the registry
                RegItem regItemRegistry = null;
                if (regItemContainer != null && !regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                    List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItemContainer, regRelationpredicateRegistry);
                    try {
                        regItemRegistry = regRelationRegistries.get(0).getRegItemObject();

                    } catch (Exception e2) {
                        regItemRegistry = null;
                    }
                } else {
                    regItemRegistry = regItemContainer;
                }

                // The writing operation on the Database are synchronized
                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }

                    // Check if the related RegAction is already available, otherwise
                    // create the RegAction.
                    RegAction regAction = regActionCheck(regUser, regItemRegister, regItemRegistry);

                    // Committing the eventual creation of the RegAction
                    entityManager.getTransaction().commit();

                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }

                    regItemproposed.setRegAction(regAction);

                    // Adding the RegItemproposed to the DB
                    regItemproposedManager.add(regItemproposed);

                    // Update the RegFields
                    updateFields(regItemproposed, requestParameters);

                    //Create Base relations
                    if (regItemContainer != null && regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                        // Adding the Collection relation
                        RegRelationproposed regRelationproposedCollection = new RegRelationproposed();
                        String newRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateCollection, null, regItemContainer);
                        regRelationproposedCollection.setUuid(newRelationproposedUuid);
                        regRelationproposedCollection.setInsertdate(new Date());
                        regRelationproposedCollection.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedCollection.setRegItemSubject(null);
                        regRelationproposedCollection.setRegItemObject(regItemContainer);
                        regRelationproposedCollection.setRegItemproposedObject(null);
                        regRelationproposedCollection.setRegRelationReference(null);
                        regRelationproposedCollection.setRegRelationpredicate(regRelationpredicateCollection);
                        regRelationproposedManager.add(regRelationproposedCollection);

                        // Adding the Register relation
                        RegRelationproposed regRelationproposedRegister = new RegRelationproposed();
                        String newRelationproposedRegisterUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegister, null, regItemRegister);
                        regRelationproposedRegister.setUuid(newRelationproposedRegisterUuid);
                        regRelationproposedRegister.setInsertdate(new Date());
                        regRelationproposedRegister.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedRegister.setRegItemSubject(null);
                        regRelationproposedRegister.setRegItemObject(regItemRegister);
                        regRelationproposedRegister.setRegItemproposedObject(null);
                        regRelationproposedRegister.setRegRelationReference(null);
                        regRelationproposedRegister.setRegRelationpredicate(regRelationpredicateRegister);
                        regRelationproposedManager.add(regRelationproposedRegister);

                        // Adding the Registry relation
                        RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
                        String newRelationproposedRegistryUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegistry, null, regItemRegistry);
                        regRelationproposedRegistry.setUuid(newRelationproposedRegistryUuid);
                        regRelationproposedRegistry.setInsertdate(new Date());
                        regRelationproposedRegistry.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedRegistry.setRegItemSubject(null);
                        regRelationproposedRegistry.setRegItemObject(regItemRegistry);
                        regRelationproposedRegistry.setRegItemproposedObject(null);
                        regRelationproposedRegistry.setRegRelationReference(null);
                        regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateRegistry);
                        regRelationproposedManager.add(regRelationproposedRegistry);

                    } else if (regItemContainer != null && regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                        // Adding the Register relation
                        RegRelationproposed regRelationproposedRegister = new RegRelationproposed();
                        String newRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegister, null, regItemContainer);
                        regRelationproposedRegister.setUuid(newRelationproposedUuid);
                        regRelationproposedRegister.setInsertdate(new Date());
                        regRelationproposedRegister.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedRegister.setRegItemSubject(null);
                        regRelationproposedRegister.setRegItemObject(regItemContainer);
                        regRelationproposedRegister.setRegItemproposedObject(null);
                        regRelationproposedRegister.setRegRelationReference(null);
                        regRelationproposedRegister.setRegRelationpredicate(regRelationpredicateRegister);
                        regRelationproposedManager.add(regRelationproposedRegister);

                        // Adding the Registry relation
                        RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
                        String newRelationproposedRegistryUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegistry, null, regItemRegistry);
                        regRelationproposedRegistry.setUuid(newRelationproposedRegistryUuid);
                        regRelationproposedRegistry.setInsertdate(new Date());
                        regRelationproposedRegistry.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedRegistry.setRegItemSubject(null);
                        regRelationproposedRegistry.setRegItemObject(regItemRegistry);
                        regRelationproposedRegistry.setRegItemproposedObject(null);
                        regRelationproposedRegistry.setRegRelationReference(null);
                        regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateRegistry);
                        regRelationproposedManager.add(regRelationproposedRegistry);

                    } else if (regItemContainer != null && regItemContainer.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                        // Adding the Registry relation
                        RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
                        String newRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegistry, null, regItemContainer);
                        regRelationproposedRegistry.setUuid(newRelationproposedUuid);
                        regRelationproposedRegistry.setInsertdate(new Date());
                        regRelationproposedRegistry.setRegItemproposedSubject(regItemproposed);
                        regRelationproposedRegistry.setRegItemSubject(null);
                        regRelationproposedRegistry.setRegItemObject(regItemContainer);
                        regRelationproposedRegistry.setRegItemproposedObject(null);
                        regRelationproposedRegistry.setRegRelationReference(null);
                        regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateRegistry);
                        regRelationproposedManager.add(regRelationproposedRegistry);
                    }

                    // Setting the RegGroup
                    entityManager.getTransaction().commit();
                }
                /* ## End Synchronized ## */
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * This method handles the save of a proposal
     *
     * @param requestParameters
     * @param regUser
     * @throws Exception
     */
    public void handleRegItemproposedSave(Map requestParameters, RegUser regUser) throws Exception {

        // Getting the ID of the RegItem (original item)
        String[] regItemUuidTmp = (String[]) requestParameters.get(BaseConstants.KEY_REQUEST_FORM_ITEMUUID);
        // !!! Sanitizing input
        String regItemUuid = InputSanitizerHelper.sanitizeInput(regItemUuidTmp[0]);

        // Getting the RegItem on which the proposal is done
        RegItem regItem = null;
        try {
            RegItemManager regItemManager = new RegItemManager(entityManager);
            regItem = regItemManager.get(regItemUuid);
        } catch (NoResultException e) {
        }

        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        // Check if the RegItemproposed is already available
        try {
            RegItemproposed regItemproposed = regItemproposedManager.getByRegItemReference(regItem);

            // Checking the ownership
            if (!regItemproposed.getRegUser().getUuid().equals(regUser.getUuid())) {
                // If the user is not the owner of the current RegItemproposed, deny the update
                throw new UnauthorizedUserException(ExceptionConstants.KEY_EXCEPTION_UNAUTHORIZED_USER_OWNER);
            }

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                // Update the RegItemproposed
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                updateRegItemproposed(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
            // If the RegItemproposed is not available, creating it

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Copy the RegItem to the RegItemproposed
                RegItemproposed regItemproposed = copyRegItemToRegItemproposed(regItem, regUser);

                // Copy all the regRelations
                copyRegRelationsToRegRelationproposeds(regItem, regItemproposed);

                // Copy groups relations
                copyRegItemRegGroupRegRoleMappingToRegItemproposedRegGroupRegRoleMapping(regItem, regItemproposed);

                // Register Federation export
                String[] registerFederationExportTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT);
                // !!! Sanitizing input
                String registerFederationExport;
                boolean registerFederationExportBol;

                if (registerFederationExportTmp != null) {
                    registerFederationExport = InputSanitizerHelper.sanitizeInput(registerFederationExportTmp[0]);
                    registerFederationExportBol = (registerFederationExport != null && registerFederationExport.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));
                } else {
                    registerFederationExportBol = false;
                }

                if (regItemproposed != null) {
                    regItemproposed.setRorExport(registerFederationExportBol);
                }

                // Update the RegFields
                updateFields(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * This method handles the save of a proposal for a new proposed item
     *
     * @param requestParameters
     * @param regUser
     * @throws Exception
     */
    public void handleRegItemproposedNewSave(Map requestParameters, RegUser regUser) throws Exception {

        // Getting the ID of the RegItem (original item)
        String[] regItemUuidTmp = (String[]) requestParameters.get(BaseConstants.KEY_REQUEST_FORM_ITEMUUID);
        // !!! Sanitizing input
        String regItemUuid = InputSanitizerHelper.sanitizeInput(regItemUuidTmp[0]);

        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        // Getting the RegItem on which the proposal is done
        RegItemproposed regItemproposed = null;
        try {
            regItemproposed = regItemproposedManager.get(regItemUuid);
        } catch (NoResultException e) {
        }

        // Check if the RegItemproposed is already available
        try {

            // Checking the ownership
            if (regItemproposed != null && !regItemproposed.getRegUser().getUuid().equals(regUser.getUuid())) {
                // If the user is not the owner of the current RegItemproposed, deny the update
                throw new UnauthorizedUserException(ExceptionConstants.KEY_EXCEPTION_UNAUTHORIZED_USER_OWNER);
            }

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                // Update the RegItemproposed
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                updateRegItemproposed(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * This method handles the deletion of a value or relation
     *
     * @param regLocalizationproposedUUID
     * @param regUser
     * @return the string to handle the right behaviour in the frontend UI
     */
    public String handleDeleteFieldContent(String regLocalizationproposedUUID, RegUser regUser) {

        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        String returnString = "";

        try {
            // Getting the regLocalizationproposed to remove
            RegLocalizationproposed regLocalizationproposed = regLocalizationproposedManager.get(regLocalizationproposedUUID);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                // Init transaction
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // If there is a reference to a RegLocalization, set the
                // RegLocalizationproposed to blank (propose the removal of the term)
                if (regLocalizationproposed.getRegLocalizationReference() != null) {

                    // If there is a reference to a RegRelationproposed, deleting also the RegRelationproposed
                    RegRelationproposed regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();

                    regLocalizationproposed.setValue(null);
                    regLocalizationproposed.setHref(null);
                    regLocalizationproposed.setRegRelationproposedReference(null);

                    // Updating the RegLocalizationproposed
                    regLocalizationproposedManager.update(regLocalizationproposed);

                    // Deleting the related RegRelation
                    if (regRelationproposed != null) {
                        regRelationproposedManager.delete(regRelationproposed);
                    }

                    // This return string will tell the frontend UI the right 
                    // behaviour after the delete action
                    returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;

                } else {
                    // If the RegLocalizationproposed has no RegLocalization 
                    // reference, it is a proposal for a new relation.                    
                    // Removing the value passed by parameter
                    regLocalizationproposedManager.delete(regLocalizationproposed);

                    // Deleting the related RegRelationproposed (if available)
                    if (regLocalizationproposed.getRegRelationproposedReference() != null) {
                        regRelationproposedManager.delete(regLocalizationproposed.getRegRelationproposedReference());

                        // This return string will tell the frontend UI the right 
                        // behaviour after the delete action
                        returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;
                    } else {
                        // This return string will tell the frontend UI the right 
                        // behaviour after the delete action
                        returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE;
                    }
                }

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e1) {

            // If the RegLocalizationproposed is not available, creating it (in
            // this case the reference passed is the RegLocalization and not the 
            // RegLocalizationproposed)
            try {

                RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

                // Getting the related RegLocalization
                RegLocalization tmpLocalization = regLocalizationManager.get(regLocalizationproposedUUID);
                // Getting the related RegItem
                RegItem regItem = tmpLocalization.getRegItem();

                // Check if the RegItemproposed is already available
                try {
                    RegItemproposed regItemproposed = regItemproposedManager.getByRegItemReference(regItem);

                    // The writing operation on the Database are synchronized
                    /* ## Start Synchronized ## */
                    synchronized (sync) {

                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }

                        // If the RegItemproposed is available, creating just the 
                        // RegLocalizationproposed (deleting the values)                   
                        RegLocalizationproposed newRegLocalizationproposed = new RegLocalizationproposed();
                        String newRegLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(tmpLocalization.getFieldValueIndex(), tmpLocalization.getRegLanguagecode(), regItemproposed, tmpLocalization.getRegField());

                        newRegLocalizationproposed.setUuid(newRegLocalizationproposedUuid);
                        newRegLocalizationproposed.setFieldValueIndex(tmpLocalization.getFieldValueIndex());
                        newRegLocalizationproposed.setHref(null);
                        newRegLocalizationproposed.setInsertdate(new Date());
                        newRegLocalizationproposed.setRegField(tmpLocalization.getRegField());
                        newRegLocalizationproposed.setRegLanguagecode(tmpLocalization.getRegLanguagecode());
                        newRegLocalizationproposed.setRegLocalizationReference(tmpLocalization);
                        newRegLocalizationproposed.setValue(null);
                        newRegLocalizationproposed.setRegRelationproposedReference(null);
                        newRegLocalizationproposed.setRegItemproposed(regItemproposed);
                        //newRegLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                        regLocalizationproposedManager.add(newRegLocalizationproposed);

                        // This return string will tell the frontend UI the right 
                        // behaviour after the delete action
                        returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_UPDATE;

                        entityManager.getTransaction().commit();
                    }
                    /* ## End Synchronized ## */

                } catch (NoResultException e2) {
                    // If the RegItemproposed is not available, creating it and
                    // creating the related proposed items

                    // The writing operation on the Database are synchronized
                    /* ## Start Synchronized ## */
                    synchronized (sync) {
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }

                        // Copying te RegItem to a RegItemporposed
                        RegItemproposed regItemproposed = copyRegItemToRegItemproposed(regItem, regUser);

                        // Copy all the regRelations
                        copyRegRelationsToRegRelationproposeds(regItem, regItemproposed);

                        // Creating the RegLocalizationproposed copying them from
                        // the RegLocalizations (only in the master language)
                        copyRegLocalizationsToRegLocalizationproposed(regItem, regItemproposed, regLocalizationproposedUUID);

                        returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;

                        entityManager.getTransaction().commit();
                    }
                    /* ## End Synchronized ## */
                }

            } catch (NoResultException e3) {
                // In this case the reference passed is the Uuid of the RegRelationproposed
                // to be deleted. 

                //Getting the RegRelationproposed object to be deleted
                try {
                    RegRelationproposed regRelationproposed = regRelationproposedManager.get(regLocalizationproposedUUID);

                    // The writing operation on the Database are synchronized
                    /* ## Start Synchronized ## */
                    synchronized (sync) {
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }

                        //Removing the RegRelationproposed
                        regRelationproposedManager.delete(regRelationproposed);

                        returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;

                        entityManager.getTransaction().commit();
                    }
                    /* ## End Synchronized ## */

                } catch (NoResultException e4) {

                    // In this case the user is trying to delete a standard RegRelation reference (e.g.)
                    // a parent relationship but the RegItemproposed is not yet there
                    // Creating it and duplicating the other elements
                    try {
                        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
                        RegRelation tmpRegRelation = regRelationManager.get(regLocalizationproposedUUID);
                        RegItem regItemSubject = tmpRegRelation.getRegItemSubject();

                        // The writing operation on the Database are synchronized
                        /* ## Start Synchronized ## */
                        synchronized (sync) {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }

                            // Copying te RegItem to a RegItemporposed
                            RegItemproposed regItemproposed = copyRegItemToRegItemproposed(regItemSubject, regUser);

                            // Copy all the regRelations
                            copyRegRelationsToRegRelationproposeds(regItemSubject, regItemproposed);

                            // Creating the RegLocalizationproposed copying them from
                            // the RegLocalizations (only in the master language)
                            copyRegLocalizationsToRegLocalizationproposed(regItemSubject, regItemproposed, regLocalizationproposedUUID);

                            returnString = BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD;

                            entityManager.getTransaction().commit();

                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            // Removing the RegRelationProposed (after the clone)
                            RegRelationproposed tmpRegrelationproposed = regRelationproposedManager.getByRegRelationReference(tmpRegRelation);
                            regRelationproposedManager.delete(tmpRegrelationproposed);

                            entityManager.getTransaction().commit();
                        }
                        /* ## End Synchronized ## */

                    } catch (NoResultException e5) {
                        logger.error("@ RegItemproposedHandler.handleDeleteFieldContent: problem during the delete operation of a field value", e5);
                    } catch (Exception e6) {
                        logger.error("@ RegItemproposedHandler.handleDeleteFieldContent: problem during the delete operation of a field value", e6);
                    }
                } catch (Exception e7) {
                    logger.error("@ RegItemproposedHandler.handleDeleteFieldContent: problem during the delete operation of a field value", e7);
                }
            } catch (Exception e8) {
                logger.error("@ RegItemproposedHandler.handleDeleteFieldContent: problem during the delete operation of a field value", e8);
            }
        } catch (Exception e9) {
            logger.error("@ RegItemproposedHandler.handleDeleteFieldContent: problem during the delete operation of a field value", e9);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return returnString;
    }

    /**
     * Restore the original reference to a RegRelarionproposed if it has been
     * cancelled
     *
     * @param regLocalizationUuid
     * @throws Exception
     */
    public void restoreOriginalRelation(String regLocalizationUuid) throws Exception {

        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        try {
            // Getting the master language
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

            RegLocalization regLocalization = null;
            RegRelation regRelation = null;

            //Getting the RegLocalization
            try {
                regLocalization = regLocalizationManager.get(regLocalizationUuid);
            } catch (NoResultException e) {
                // If the regLocalization is not available, checking the RegRelation.
                // In cace of generical relation (like parent relation) the RegRelation 
                // uuid is passed rather than the RegLocalization uuid
                regRelation = regRelationManager.get(regLocalizationUuid);
            }

            if (regLocalization != null) {
                //Getting the related RegLocalizationproposed to restore the RegRelationProposed
                RegLocalizationproposed regLocalizationProposed = regLocalizationproposedManager.getByRegLocalizationReferenceAndLanguage(regLocalization, masterLanguage);

                //Copying the RegRelation to RegRelationproposed
                if (regLocalization.getRegRelationReference() != null) {

                    String newRegRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regLocalizationProposed.getRegItemproposed(), null, regLocalization.getRegRelationReference().getRegRelationpredicate(), null, regLocalization.getRegRelationReference().getRegItemObject());

                    RegRelationproposed regRelationproposed = null;

                    // The writing operation on the Database are synchronized
                    /* ## Start Synchronized ## */
                    synchronized (sync) {
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }

                        try {
                            // Check if the RegRelationproposed is already there
                            regRelationproposed = regRelationproposedManager.get(newRegRelationproposedUuid);

                        } catch (NoResultException e) {

                            // If it is not there, it creates it
                            regRelationproposed = new RegRelationproposed();

                            regRelationproposed.setUuid(newRegRelationproposedUuid);
                            regRelationproposed.setRegItemSubject(null);
                            regRelationproposed.setRegItemproposedSubject(regLocalizationProposed.getRegItemproposed());
                            regRelationproposed.setRegItemObject(regLocalization.getRegRelationReference().getRegItemObject());
                            regRelationproposed.setRegItemproposedObject(null);
                            regRelationproposed.setRegRelationReference(regLocalization.getRegRelationReference());
                            regRelationproposed.setRegRelationpredicate(regLocalization.getRegRelationReference().getRegRelationpredicate());
                            regRelationproposed.setInsertdate(new Date());

                            regRelationproposedManager.add(regRelationproposed);
                        }

                        //Update the RegLocalizationproposed
                        regLocalizationProposed.setRegRelationproposedReference(regRelationproposed);

                        regLocalizationproposedManager.update(regLocalizationProposed);

                        entityManager.getTransaction().commit();
                    }
                    /* ## End Synchronized ## */
                }
            } else if (regRelation != null) {
                //Managing the case of a generic reg relation like parent, successor, ...)               

                RegRelationproposed regRelationProposed = new RegRelationproposed();

                //Getting the RegItemproposed (subject of the relation)
                RegItemproposed tmpRegItemProposedSubject = regItemproposedManager.getByRegItemReference(regRelation.getRegItemSubject());

                String tmpUuid = RegRelationproposedUuidHelper.getUuid(tmpRegItemProposedSubject, null, regRelation.getRegRelationpredicate(), null, regRelation.getRegItemObject());

                regRelationProposed.setUuid(tmpUuid);
                regRelationProposed.setInsertdate(new Date());
                regRelationProposed.setRegItemObject(regRelation.getRegItemObject());
                regRelationProposed.setRegItemSubject(null);
                regRelationProposed.setRegItemproposedObject(null);
                regRelationProposed.setRegItemproposedSubject(tmpRegItemProposedSubject);
                regRelationProposed.setRegRelationReference(regRelation);
                regRelationProposed.setRegRelationpredicate(regRelation.getRegRelationpredicate());

                // The writing operation on the Database are synchronized
                /* ## Start Synchronized ## */
                synchronized (sync) {

                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }

                    regRelationproposedManager.add(regRelationProposed);

                    entityManager.getTransaction().commit();
                }
                /* ## End Synchronized ## */
            }

        } catch (NoResultException e) {
        } catch (Exception e) {
            logger.error("@ RegItemproposedHandler.restoreOriginalRelation: problem during the delete operation of a field value", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void handleDiscardProposal(RegItemproposed regItemProposed) throws Exception {
        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);

        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Getting all the associated RegLocalizationproposed
                List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemProposed);
                // remove all the RegLocalizationproposed
                for (RegLocalizationproposed tmp : regLocalizationproposeds) {
                    regLocalizationproposedManager.delete(tmp);
                }
                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Getting all the associated RegRelationproposed by subject
                List<RegRelationproposed> regRelationproposeds = regRelationproposedManager.getAllBySubject(regItemProposed);
                // remove all the RegLocalizationproposed
                for (RegRelationproposed tmp : regRelationproposeds) {
                    regRelationproposedManager.delete(tmp);
                }
                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Getting all the associated RegRelationproposed by object
                List<RegRelationproposed> regRelationproposedsObject = regRelationproposedManager.getAllByObject(regItemProposed);
                // remove all the RegLocalizationproposed
                for (RegRelationproposed tmp : regRelationproposedsObject) {
                    regRelationproposedManager.delete(tmp);
                }
                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Getting all the associated RegRelationproposed
                List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemProposed);
                // remove all the RegLocalizationproposed
                for (RegItemproposedRegGroupRegRoleMapping tmp : regItemproposedRegGroupRegRoleMappings) {
                    regItemproposedRegGroupRegRoleMappingManager.delete(tmp);
                }
                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                //Removing the RegItemproposed
                regItemproposedManager.delete(regItemProposed);

                entityManager.getTransaction().commit();

                // Check, if this was the only item inthe RegAction, removing it
                RegAction regAction = regItemProposed.getRegAction();
                List<RegItemproposed> regItemproposeds = regItemproposedManager.getAll(regAction);
                if (regItemproposeds.size() < 1) {
                    RegActionManager regActionManager = new RegActionManager(entityManager);
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    regActionManager.delete(regAction);
                    entityManager.getTransaction().commit();
                }
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }

    /* -- Supporting methods -- */
    // Create the new RegItemproposed
    private RegItemproposed copyRegItemToRegItemproposed(RegItem regItem, RegUser regUser) throws Exception {
        if (regItem != null) {
            // Init managers
            RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
            RegStatusManager regStatusManager = new RegStatusManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

            // Getting the eventual collection relation
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
            RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
            RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
            List<RegRelation> regRelationCollections = regRelationManager.getAll(regItem, regRelationpredicateCollection);
            RegRelation regRelationCollection = null;
            if (!regRelationCollections.isEmpty()) {
                // Every items can have just one relation of type collection
                regRelationCollection = regRelationCollections.get(0);
            }

            // Setting the register
            RegItem regItemRegister;
            if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
                try {
                    regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                } catch (Exception e2) {
                    regItemRegister = null;
                }
            } else {
                regItemRegister = regItem;
            }

            // Setting the registry
            RegItem regItemRegistry;
            if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
                try {
                    regItemRegistry = regRelationRegistries.get(0).getRegItemObject();

                } catch (Exception e2) {
                    regItemRegistry = null;
                }
            } else {
                regItemRegistry = regItem;
            }

            // Getting the reg status draft for the proposed item
            RegStatus regStatusDraft = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

            //Copying it to the RegItemproposed
            RegItemproposed regItemproposed = new RegItemproposed();

            String itemProposedUuid = RegItemproposedUuidHelper.getUuid(regItem.getLocalid(), ((regRelationCollection != null) ? regRelationCollection.getRegItemObject() : null), regItem.getRegItemclass(), regItem);
            regItemproposed.setUuid(itemProposedUuid);
            regItemproposed.setLocalid(regItem.getLocalid());
            regItemproposed.setRegStatus(regStatusDraft);
            regItemproposed.setRegItemclass(regItem.getRegItemclass());
            regItemproposed.setInsertdate(regItem.getInsertdate());
            regItemproposed.setRegUser(regUser);
            regItemproposed.setRorExport(regItem.getRorExport());
            regItemproposed.setRegItemReference(regItem);

            // Check if the related RegAction is already available, otherwise
            // create the RegAction.
            RegAction regAction = regActionCheck(regUser, regItemRegister, regItemRegistry);

            regItemproposed.setRegAction(regAction);

            // Saving the RegItemproposed
            regItemproposedManager.add(regItemproposed);

            return regItemproposed;
        } else {
            return null;
        }
    }

    // Update an existing RegItemproposed
    private void updateRegItemproposed(RegItemproposed regItemproposed, Map requestParameters) throws Exception {

        //Updating the regItemproposed edit date
        regItemproposed.setEditdate(new Date());

        // Register Federation export
        String[] registerFederationExportTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT);
        // !!! Sanitizing input
        String registerFederationExport;
        boolean registerFederationExportBol;

        if (registerFederationExportTmp != null) {
            registerFederationExport = InputSanitizerHelper.sanitizeInput(registerFederationExportTmp[0]);
            registerFederationExportBol = (registerFederationExport != null && registerFederationExport.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));
        } else {
            registerFederationExportBol = false;
        }

        regItemproposed.setRorExport(registerFederationExportBol);

        // Updating fields
        updateFields(regItemproposed, requestParameters);

    }

    // Copy all the RegRelation related to the item passed by parameter and all the
    // RegLocalization to RegRelationproposed and RegLocalizationproposed
    private void copyRegRelationsToRegRelationproposeds(RegItem regItem, RegItemproposed regItemproposed) throws Exception {

        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);

        // Getting all the relations related to that object
        List<RegRelation> regRelations = regRelationManager.getAllBySubject(regItem);

        // Copying RegRelations to RegRelationProposeds
        HashMap<String, RegRelationproposed> tempHashmap = new HashMap();

        for (RegRelation regRelation : regRelations) {

            RegRelationproposed regRelationproposed = new RegRelationproposed();
            String regRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelation.getRegRelationpredicate(), null, regRelation.getRegItemObject());

            regRelationproposed.setUuid(regRelationproposedUuid);
            regRelationproposed.setRegItemSubject(null);
            regRelationproposed.setRegItemproposedSubject(regItemproposed);
            regRelationproposed.setRegItemObject(regRelation.getRegItemObject());
            regRelationproposed.setRegItemproposedObject(null);
            regRelationproposed.setRegRelationReference(regRelation);
            regRelationproposed.setRegRelationpredicate(regRelation.getRegRelationpredicate());
            regRelationproposed.setInsertdate(new Date());

            regRelationproposedManager.add(regRelationproposed);

            tempHashmap.put(regRelation.getUuid(), regRelationproposed);
        }

        // Replicating the relevant Reglocalizationproposeds pointing to the 
        //  RegRelationproposed copyied above
        // Getting all the localization with a reference to a reg relation 
        // related to the current RegItem
        List<RegLocalization> regLocalizations = regLocalizationManager.getAllWithRelationReference(regItem);
        for (RegLocalization regLocalization : regLocalizations) {
            // Creating the regLocalizationproposed for the regRelationreference
            RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
            String newUuid = RegLocalizationproposedUuidHelper.getUuid(regLocalization.getFieldValueIndex(), regLocalization.getRegLanguagecode(), regItemproposed, regLocalization.getRegField());
            regLocalizationproposed.setUuid(newUuid);
            regLocalizationproposed.setFieldValueIndex(regLocalization.getFieldValueIndex());
            regLocalizationproposed.setHref(regLocalization.getHref());
            regLocalizationproposed.setRegField(regLocalization.getRegField());
            regLocalizationproposed.setRegItemproposed(regItemproposed);
            regLocalizationproposed.setRegLanguagecode(regLocalization.getRegLanguagecode());
            regLocalizationproposed.setRegLocalizationReference(regLocalization);
            regLocalizationproposed.setRegRelationproposedReference(tempHashmap.get(regLocalization.getRegRelationReference().getUuid()));
            regLocalizationproposed.setValue(regLocalization.getValue());
            regLocalizationproposed.setInsertdate(new Date());
            regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

            regLocalizationproposedManager.add(regLocalizationproposed);
        }
    }

    // Copy all the RegItemRegGroupRegRoleMapping related to the item passed by parameter
    // to RegItemproposedRegGroupRegRoleMapping
    private void copyRegItemRegGroupRegRoleMappingToRegItemproposedRegGroupRegRoleMapping(RegItem regItem, RegItemproposed regItemproposed) throws Exception {

        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);

        // Getting all the RegItemRegGroupRegRoleMapping related to that RegItem
        List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regItem);

        for (RegItemRegGroupRegRoleMapping tmpRegItemRegGroupRegRoleMapping : regItemRegGroupRegRoleMappings) {

            RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = new RegItemproposedRegGroupRegRoleMapping();
            String newUuid = RegItemproposedRegGroupRegRoleMappingUuidHelper.getUuid(regItemproposed.getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegGroup().getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegRole().getUuid());

            regItemproposedRegGroupRegRoleMapping.setUuid(newUuid);
            regItemproposedRegGroupRegRoleMapping.setRegItemRegGroupRegRoleMappingReference(tmpRegItemRegGroupRegRoleMapping);
            regItemproposedRegGroupRegRoleMapping.setInsertdate(new Date());
            regItemproposedRegGroupRegRoleMapping.setRegGroup(tmpRegItemRegGroupRegRoleMapping.getRegGroup());
            regItemproposedRegGroupRegRoleMapping.setRegItemproposed(regItemproposed);
            regItemproposedRegGroupRegRoleMapping.setRegRole(tmpRegItemRegGroupRegRoleMapping.getRegRole());

            regItemproposedRegGroupRegRoleMappingManager.add(regItemproposedRegGroupRegRoleMapping);
        }
    }

    // Update fields
    private String updateFields(RegItemproposed regItemproposed, Map requestParameters) throws Exception {

        // Init variables        
        Set s = requestParameters.entrySet();
        Iterator it = s.iterator();
        String[] regLanguagecodeUuidTemp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGLANGUAGECODEUUID);
        // !!! Sanitizing input
        String regLanguagecodeUuid = InputSanitizerHelper.sanitizeInput(regLanguagecodeUuidTemp[0]);

        // Init managers
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        // Getting the reg languagecode of the current edited field
        RegLanguagecode regLanguagecode = regLanguagecodeManager.get(regLanguagecodeUuid);
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        // Errors string
        String errors = "";

        // Iterate on all the form fields
        while (it.hasNext()) {

            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();

            // ** Each field is univocally numbered: in this way there are no field 
            // in the request with more than one value **
            // This is better to keep the consistency with right value since with
            // fields with the same name and multi values different browsers may
            // have different implementation on the handling of the index/order of the value
            String key = entry.getKey();
            String[] tmp = processFieldName(key);
            String fieldUuid = tmp[0];

            String referenceRegLocalizationUuid = tmp[2];

            // Getting the value
            String[] values = entry.getValue();
            String value = values[0];

            // Handling charset            
            byte[] bytes = value.getBytes(Charset.defaultCharset());
            value = new String(bytes, StandardCharsets.UTF_8);

            // !!! Sanitizing form input
            value = (value.length() == 0) ? "" : InputSanitizerHelper.sanitizeInput(value);

            // Getting the eventual href
            String hrefFieldName = processFieldHrefName(key);
            String[] paramHrefs = (String[]) requestParameters.get(hrefFieldName);
            String href = "";
            if (paramHrefs != null) {
                //For each value there is just one link
                href = paramHrefs[0];
                // !!! Sanitizing form input
                href = InputSanitizerHelper.sanitizeInput(href);
            }

            try {
                // Getting the current regFieldMapping
                RegFieldmapping regFieldmapping = regFieldmappingManager.get(fieldUuid);

                // Getting the RegField
                RegField regField = regFieldmapping.getRegField();

                // Handling the RelationReference case
                if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)) {
                    if (value.trim().length() > 0) {
                        RegItemManager regItemManager = new RegItemManager(entityManager);
                        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
                        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

                        // Getting the right RegRelationPredicate
                        RegRelationpredicate regRelationPredicateRealtion = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);

                        // Getting the RegItem pointed by the relation
                        RegItem regItemRelation = regItemManager.get(value);

                        // Creating the new RegRelationproposed
                        String newRegRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationPredicateRealtion, null, regItemRelation);
                        RegRelationproposed newRegRelationproposed = new RegRelationproposed();
                        newRegRelationproposed.setInsertdate(new Date());
                        newRegRelationproposed.setRegItemproposedSubject(regItemproposed);
                        newRegRelationproposed.setRegRelationpredicate(regRelationPredicateRealtion);
                        newRegRelationproposed.setRegItemObject(regItemRelation);
                        newRegRelationproposed.setUuid(newRegRelationproposedUuid);

                        // Adding the newly created RegRelationproposed
                        regRelationproposedManager.add(newRegRelationproposed);

                        // Creating the new RegLocalizationproposed
                        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();

                        // Getting the new fieldValueIndex (max of the field index
                        // of the localizations for the current field)
                        List<RegLocalizationproposed> tmpLocalizations = regLocalizationproposedManager.getAll(regField, regItemproposed, regLanguagecode);
                        int fieldValueIndexNew = -1;
                        for (RegLocalizationproposed tmpRegLocalization : tmpLocalizations) {
                            if (tmpRegLocalization.getFieldValueIndex() > fieldValueIndexNew) {
                                fieldValueIndexNew = tmpRegLocalization.getFieldValueIndex();
                            }
                        }
                        fieldValueIndexNew++;

                        // Creating the new uuid
                        String regLocalizationproposedUuid = "";
                        regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, masterLanguage, regItemproposed, regField);

                        // Setting fields
                        regLocalizationproposed.setUuid(regLocalizationproposedUuid);
                        regLocalizationproposed.setRegItemproposed(regItemproposed);
                        regLocalizationproposed.setRegField(regField);
                        regLocalizationproposed.setInsertdate(new Date());
                        regLocalizationproposed.setFieldValueIndex(fieldValueIndexNew);

                        regLocalizationproposed.setRegLanguagecode(masterLanguage);

                        regLocalizationproposed.setValue(null);
                        regLocalizationproposed.setHref(null);

                        regLocalizationproposed.setRegRelationproposedReference(newRegRelationproposed);

                        regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                        RegLocalization tmpRegLocalizationReference = null;
                        if (!regFieldmapping.getMultivalue()) {
                            // Check if there is a null value to remove for this regField
                            List<RegLocalizationproposed> nullpRegLocalizationproposeds = regLocalizationproposedManager.getAllNull(regField, regItemproposed);
                            for (RegLocalizationproposed tmpRegLocalizationProposed : nullpRegLocalizationproposeds) {
                                tmpRegLocalizationReference = tmpRegLocalizationProposed.getRegLocalizationReference();
                                regLocalizationproposedManager.delete(tmpRegLocalizationProposed);
                            }
                        }

                        if (tmpRegLocalizationReference != null) {
                            regLocalizationproposed.setRegLocalizationReference(tmpRegLocalizationReference);
                        }

                        // Saving the RegLocalizationproposed
                        regLocalizationproposedManager.add(regLocalizationproposed);
                    }

                } else if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {
                    // Handling the Parent case
                    if (value.trim().length() > 0) {
                        RegItemManager regItemManager = new RegItemManager(entityManager);
                        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
                        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

                        // Getting the right RegRelationPredicate
                        RegRelationpredicate regRelationPredicateRealtion = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);

                        // Getting the RegItem pointed by the relation
                        RegItem regItemRelation = regItemManager.get(value);

                        // Creating the new RegRelationproposed
                        String newRegRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationPredicateRealtion, null, regItemRelation);
                        RegRelationproposed newRegRelationproposed = new RegRelationproposed();
                        newRegRelationproposed.setInsertdate(new Date());
                        newRegRelationproposed.setRegItemproposedSubject(regItemproposed);
                        newRegRelationproposed.setRegRelationpredicate(regRelationPredicateRealtion);
                        newRegRelationproposed.setRegItemObject(regItemRelation);
                        newRegRelationproposed.setUuid(newRegRelationproposedUuid);

                        // Adding the newly created RegRelationproposed
                        regRelationproposedManager.add(newRegRelationproposed);

                    }
                } else if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {
                    // Handling the group case
                    if (value.trim().length() > 0) {
                        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
                        String newUuid = RegItemproposedRegGroupRegRoleMappingUuidHelper.getUuid(regItemproposed.getUuid(), value, regFieldmapping.getRegField().getRegRoleReference().getUuid());

                        try {
                            // Check if the mapping is already available
                            regItemproposedRegGroupRegRoleMappingManager.get(newUuid);

                        } catch (NoResultException e) {

                            //Getting the reg group passed from parameter
                            RegGroupManager regGroupManager = new RegGroupManager(entityManager);
                            RegGroup regGroup = null;
                            try {
                                regGroup = regGroupManager.get(value);

                                RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = new RegItemproposedRegGroupRegRoleMapping();
                                regItemproposedRegGroupRegRoleMapping.setUuid(newUuid);
                                regItemproposedRegGroupRegRoleMapping.setInsertdate(new Date());
                                regItemproposedRegGroupRegRoleMapping.setRegGroup(regGroup);
                                regItemproposedRegGroupRegRoleMapping.setRegItemproposed(regItemproposed);
                                regItemproposedRegGroupRegRoleMapping.setRegRole(regFieldmapping.getRegField().getRegRoleReference());

                                regItemproposedRegGroupRegRoleMappingManager.add(regItemproposedRegGroupRegRoleMapping);

                            } catch (NoResultException e1) {
                            }
                        }
                    }
                } else {

                    // Check if the RegLocalizationproposed for the specific language is already there
                    RegLocalization regLocalizationReference = null;
                    try {
                        RegLocalizationproposed regLocalizationproposed;

                        // If the reference to the RegLocalizationReference is available,
                        // checking if the related regLocalizationproposed is available
                        try {
                            if (referenceRegLocalizationUuid == null) {
                                throw new NoResultException();
                            }

                            regLocalizationReference = regLocalizationManager.get(referenceRegLocalizationUuid);
                            regLocalizationproposed = regLocalizationproposedManager.getByRegLocalizationReferenceAndLanguage(regLocalizationReference, regLanguagecode);

                            // In case the current language is not the master language,
                            // search for eventual RegLocalization proposed in that language
                            if (!regLanguagecode.getMasterlanguage()) {
                                regLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalizationproposed, regLocalizationproposedManager, regLanguagecode);
                            }

                        } catch (NoResultException ex) {
                            if (referenceRegLocalizationUuid == null) {
                                throw new NoResultException();
                            }
                            // Otherwise, the reference to the RegLocalizationproposed
                            // is stored in the same field name fragment: checking if a
                            /// new RegLocalizationproposed is already in the db
                            regLocalizationproposed = regLocalizationproposedManager.get(referenceRegLocalizationUuid);

                            // In case the current language is not the master language,
                            // search for eventual RegLocalization proposed in that language
                            if (!regLanguagecode.getMasterlanguage()) {
                                regLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalizationproposed, regLocalizationproposedManager, regLanguagecode);
                            }

                            // If a new Localizationproposed is in the DB, removing the eventual
                            // reference to the RegLocalization and setting it to null because in this
                            // case it is not representing the related RegLocalization
                            regLocalizationReference = null;
                        }

                        // Checking if the field has been changed and needs to be updated
                        if ((regLocalizationproposed.getValue() == null && value != null && value.length() != 0) || (regLocalizationproposed.getValue() != null && !regLocalizationproposed.getValue().equals(value)) || (regFieldmapping.getHashref() && regLocalizationproposed.getHref() != null && !regLocalizationproposed.getHref().equals(href)) || (regFieldmapping.getHashref() && regLocalizationproposed.getHref() == null && href != null && href.length() > 0)) {

                            // Setting the values in the new RegLocalizationproposed
                            regLocalizationproposed.setValue(value);

                            // Updating the href if the RegFieldmapping has also the href
                            if (regFieldmapping.getHashref()) {
                                regLocalizationproposed.setHref((href != null && href.length() > 0) ? href : null);
                            }

                            // Setting the RegLocalization reference (if available)
                            if (regLocalizationReference != null) {
                                regLocalizationproposed.setRegLocalizationReference(regLocalizationReference);
                            }

                            regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                            regLocalizationproposedManager.add(regLocalizationproposed);
                        }

                    } // If the RegLocalizationproposed is not there copy and create it
                    // in the RegLocalizationproposed
                    catch (NoResultException e) {

                        // Getting the eventual regLocalization reference
                        if (referenceRegLocalizationUuid != null) {
                            try {
                                regLocalizationReference = regLocalizationManager.get(referenceRegLocalizationUuid);
                            } catch (NoResultException nr) {
                                regLocalizationReference = null;
                            }
                        } else {
                            regLocalizationReference = null;
                        }

                        if (value == null || (value != null && value.length() < 0)) {
                            value = "";
                        }

                        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();

                        // Getting the new fieldValueIndex (max of the field index
                        // of the localizations for the current field)
                        List<RegLocalizationproposed> tmpLocalizations = regLocalizationproposedManager.getAll(regField, regItemproposed, regLanguagecode);
                        int fieldValueIndexNew = -1;
                        for (RegLocalizationproposed tmpRegLocalization : tmpLocalizations) {
                            if (tmpRegLocalization.getFieldValueIndex() > fieldValueIndexNew) {
                                fieldValueIndexNew = tmpRegLocalization.getFieldValueIndex();
                            }
                        }
                        fieldValueIndexNew++;

                        // Creating the new uuid
                        String regLocalizationproposedUuid = "";
                        // If the regFieldType of the current field is number or date,
                        // the element is stored only in the master language
                        if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER) || regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE)) {
                            regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, masterLanguage, regItemproposed, regField);
                        } else {
                            regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, regLanguagecode, regItemproposed, regField);
                        }

                        // Setting fields
                        regLocalizationproposed.setUuid(regLocalizationproposedUuid);
                        regLocalizationproposed.setRegItemproposed(regItemproposed);
                        regLocalizationproposed.setRegField(regField);
                        regLocalizationproposed.setInsertdate(new Date());
                        regLocalizationproposed.setFieldValueIndex(fieldValueIndexNew);
                        regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                        // If the regFieldType of the current field is number or date,
                        // the element is stored only in the master language 
                        if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER) || regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE)) {
                            regLocalizationproposed.setRegLanguagecode(masterLanguage);
                        } else {
                            regLocalizationproposed.setRegLanguagecode(regLanguagecode);
                        }

                        // Setting the RegLocalization refderence (if available)
                        if (regLocalizationReference != null) {
                            regLocalizationproposed.setRegLocalizationReference(regLocalizationReference);
                        }

                        //Setting the value
                        regLocalizationproposed.setValue(value);

                        // Setting the href if the RegFieldmapping has also the href
                        if (regFieldmapping.getHashref()) {
                            regLocalizationproposed.setHref((href != null && href.length() > 0) ? href : null);
                        }

                        // Saving the RegLocalizationproposed
                        regLocalizationproposedManager.add(regLocalizationproposed);

                    }
                }

            } catch (NoResultException e) {
                // The form field is not a RegField: no action needed
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return errors;

    }

    // This method handles the fields name (composed by multiple parts)
    private String[] processFieldName(String key) {
        String fieldUuid;
        String fieldValueIndex = null;
        String referenceRegLocalizationUuid = null;

        // Checking if there is a reference to a RegLocalization
        String[] tmp;
        if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY)) {
            tmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY);

            // Getting the field uuid and field value index
            String[] fieldTmp;
            String tmpUuidAndFieldValueIndex = tmp[0];
            fieldTmp = tmpUuidAndFieldValueIndex.split(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY);
            fieldUuid = fieldTmp[0];
            fieldValueIndex = fieldTmp[1];

            // Getting the referenc e localization
            referenceRegLocalizationUuid = (tmp.length > 1) ? tmp[1] : null;
        } else if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY) && !key.contains(BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX)) {
            String[] fieldTmp;
            fieldTmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY);
            fieldUuid = fieldTmp[0];
            fieldValueIndex = fieldTmp[1];
        } else {
            fieldUuid = key;
        }

        // Removing the field name index
        String[] outs = new String[3];
        outs[0] = fieldUuid;
        outs[1] = fieldValueIndex;
        outs[2] = referenceRegLocalizationUuid;

        return outs;
    }

    // This method handles the href fields name (composed by multiple parts)
    private String processFieldHrefName(String key) {
        String fieldHrefName;

        // Checking if there is a reference to a RegLocalization
        String[] tmp;
        if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY)) {
            tmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY);
            fieldHrefName = tmp[0] + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;
        } else {
            fieldHrefName = key + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;
        }

        return fieldHrefName;
    }

    // This method retrieve the RegLocalizationproposed in the current language
    private RegLocalizationproposed getRegLocalizationproposedInCurrentLanguage(RegLocalizationproposed regLocalizationproposed, RegLocalizationproposedManager regLocalizationproposedManager, RegLanguagecode regLanguagecode) throws Exception {

        String tmpUuid = RegLocalizationproposedUuidHelper.getUuid(
                regLocalizationproposed.getFieldValueIndex(),
                regLanguagecode,
                regLocalizationproposed.getRegItemproposed(),
                regLocalizationproposed.getRegField()
        );
        regLocalizationproposed = regLocalizationproposedManager.get(tmpUuid);

        return regLocalizationproposed;
    }

    // This method copies the RegLocalization to RegLocalizationproposed
    private void copyRegLocalizationsToRegLocalizationproposed(RegItem regItem, RegItemproposed regItemproposed, String checkDeleteUuid) throws Exception {

        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegLanguagecodeManager regLanguacecodeManager = new RegLanguagecodeManager(entityManager);
        RegLanguagecode masterLanguage = regLanguacecodeManager.getMasterLanguage();

        // Getting all the regLocalizations realted to the RegItem (in the master language)
        List<RegLocalization> regLocalizations = regLocalizationManager.getAll(regItem, masterLanguage);

        for (RegLocalization regLocalization : regLocalizations) {

            String newRegLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(regLocalization.getFieldValueIndex(), regLocalization.getRegLanguagecode(), regItemproposed, regLocalization.getRegField());

            // Checking if the regLocalizationproposed is already available ( it
            // may have been copyied by the reg relation copyier)
            try {
                // If the RegLocalizationproposed is already there, update it accordingly
                RegLocalizationproposed regLocalizationproposed = regLocalizationproposedManager.get(newRegLocalizationproposedUuid);

                // If the localization is the one passed by the delete action, deleting the values
                if (checkDeleteUuid != null && regLocalization.getUuid().equals(checkDeleteUuid)) {
                    // If there is a reference to a RegRelationproposed, deleting also the RegRelationproposed
                    RegRelationproposed regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();

                    // Removing the values and the reference to the RegRelationproposed
                    regLocalizationproposed.setValue(null);
                    regLocalizationproposed.setHref(null);
                    regLocalizationproposed.setRegRelationproposedReference(null);

                    regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                    regLocalizationproposedManager.update(regLocalizationproposed);

                    if (regRelationproposed != null) {
                        regRelationproposedManager.delete(regRelationproposed);
                    }
                }

            } catch (NoResultException e) {
                // Else, creating the new RegLocalizationproposed
                RegLocalizationproposed newRegLocalizationproposed = new RegLocalizationproposed();
                newRegLocalizationproposed.setUuid(newRegLocalizationproposedUuid);
                newRegLocalizationproposed.setFieldValueIndex(regLocalization.getFieldValueIndex());
                newRegLocalizationproposed.setInsertdate(new Date());
                newRegLocalizationproposed.setRegField(regLocalization.getRegField());
                newRegLocalizationproposed.setRegItemproposed(regItemproposed);
                newRegLocalizationproposed.setRegLanguagecode(regLocalization.getRegLanguagecode());
                newRegLocalizationproposed.setRegLocalizationReference(regLocalization);
                newRegLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                // If the localization is the one passed by the delete action, deleting the values
                if (checkDeleteUuid != null && regLocalization.getUuid().equals(checkDeleteUuid)) {
                    newRegLocalizationproposed.setValue(null);
                    newRegLocalizationproposed.setHref(null);
                    newRegLocalizationproposed.setRegRelationproposedReference(null);
                } else {
                    newRegLocalizationproposed.setValue(regLocalization.getValue());
                    newRegLocalizationproposed.setHref(regLocalization.getHref());

                    // Getting the related RegRelationproposed
                    try {
                        RegRelationproposed regRelationproposed = regRelationproposedManager.getByRegRelationReference(regLocalization.getRegRelationReference());
                        newRegLocalizationproposed.setRegRelationproposedReference(regRelationproposed);
                    } catch (NoResultException ex) {
                        newRegLocalizationproposed.setRegRelationproposedReference(null);
                    }
                }

                regLocalizationproposedManager.add(newRegLocalizationproposed);
            }
        }
    }

    private RegAction regActionCheck(RegUser regUser, RegItem regItemRegister, RegItem regItemRegistry) throws Exception {
        RegAction regAction = null;

        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        List<RegAction> regActions;
        RegStatus regStatus;

        // Getting the RegStatus draft, to check that there are not other RegAction
        // dratf from the same user
        regStatus = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

        boolean notFound = false;

        // checking if the RegAction is available
        try {
            regActions = regActionManager.getAllWithNoComments(regUser, regItemRegister, regItemRegistry, regStatus);
            if (regActions != null && regActions.isEmpty()) {
                notFound = true;
            } else if (regActions != null) {
                // There should be only one regAction in status draft with 
                // no comments per each user. 
                regAction = regActions.get(0);
            }
        } catch (NoResultException e) {
            notFound = true;
        }

        // Creating the RegAction if not found
        if (notFound) {

            regAction = new RegAction();
            Date currentDate = new Date();
            String newUuid = RegActionUuidHelper.getUuid(regUser, currentDate);

            // Creating the default label
            RegItem regItemcheck = (regItemRegister != null) ? regItemRegister : regItemRegistry;
            List<RegLocalization> regLocalization = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemcheck, masterLanguage);
            String defaultLabel = systemLocalization.getString("label.actionon") + " " + regLocalization.get(0).getValue();

            regAction.setUuid(newUuid);
            regAction.setLabel(defaultLabel);
            regAction.setInsertdate(currentDate);
            regAction.setRegUser(regUser);
            regAction.setChangeRequest(null);
            regAction.setRejectMessage(null);
            regAction.setIssueTrackerLink(null);
            regAction.setRegStatus(regStatus);
            regAction.setChangesImplemented(false);
            regAction.setRegItemRegister(regItemRegister);
            regAction.setRegItemRegistry(regItemRegistry);

            regActionManager.add(regAction);

        }

        return regAction;
    }

    public RegItemproposed completeCopyRegItemToRegItemporposed(RegItem regItem, RegUser regUser) throws Exception {
        RegItemproposed regItemproposed = null;
        synchronized (sync) {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            regItemproposed = copyRegItemToRegItemproposed(regItem, regUser);
            entityManager.getTransaction().commit();

            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            if (regItem != null && regItemproposed != null) {
                copyRegItemRegGroupRegRoleMappingToRegItemproposedRegGroupRegRoleMapping(regItem, regItemproposed);
            }
            entityManager.getTransaction().commit();

            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            if (regItem != null && regItemproposed != null) {
                copyRegRelationsToRegRelationproposeds(regItem, regItemproposed);
            }
            entityManager.getTransaction().commit();

            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            copyRegLocalizationsToRegLocalizationproposed(regItem, regItemproposed, null);
            entityManager.getTransaction().commit();
        }
        return regItemproposed;
    }

    public String newRegisterLite(String registryItemUuid, String registerLocalId, RegItemclass registerItemclass, RegUser regUser, String labelValue, String contentSummaryValue, String registerOwnerGroupUuid, String registerManagerGroupUuid, String controlBodyGroupUuid, String[] submittingOrganizationGroupUuids) throws Exception {

        // Instantiating managers
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);

        synchronized (sync) {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }

            //Getting the Registry
            RegItem registryRegItem = regItemManager.get(registryItemUuid);

            // Adding the register item
            RegItemproposed registerRegItemproposed = new RegItemproposed();
            // Creating the regItem UUID
            String uuid = RegItemproposedUuidHelper.getUuid(registerLocalId, null, registerItemclass, null);

            // Getting the draft status
            RegStatus regStatusDraft = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

            registerRegItemproposed.setUuid(uuid);
            registerRegItemproposed.setLocalid(registerLocalId);
            registerRegItemproposed.setEditdate(null);
            registerRegItemproposed.setExternal(false);
            registerRegItemproposed.setInsertdate(new Date());
            registerRegItemproposed.setRegItemclass(registerItemclass);
            registerRegItemproposed.setRegStatus(regStatusDraft);
            registerRegItemproposed.setRegUser(regUser);
            registerRegItemproposed.setRorExport(true);

            // Check if the related RegAction is already available, otherwise
            // create the RegAction.
            RegAction regAction = regActionCheck(regUser, null, registryRegItem);
            registerRegItemproposed.setRegAction(regAction);

            regItemproposedManager.add(registerRegItemproposed);

            entityManager.getTransaction().commit();

            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }

            // Adding the registry relation
            RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);

            // Adding the Registry relation
            RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
            String newRelationproposedRegistryUuidLabel = RegRelationproposedUuidHelper.getUuid(registerRegItemproposed, null, regRelationpredicateRegistry, null, registryRegItem);
            regRelationproposedRegistry.setUuid(newRelationproposedRegistryUuidLabel);
            regRelationproposedRegistry.setInsertdate(new Date());
            regRelationproposedRegistry.setRegItemproposedSubject(registerRegItemproposed);
            regRelationproposedRegistry.setRegItemSubject(null);
            regRelationproposedRegistry.setRegItemObject(registryRegItem);
            regRelationproposedRegistry.setRegItemproposedObject(null);
            regRelationproposedRegistry.setRegRelationReference(null);
            regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateRegistry);
            regRelationproposedManager.add(regRelationproposedRegistry);

            // Adding the related localization (label, content summaryn)
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

            // Setting the Label
            RegField regFieldLabel = regFieldManager.getTitleRegField();
            String newRegLocalizationproposedUuidLabel = RegLocalizationproposedUuidHelper.getUuid(0, masterLanguage, registerRegItemproposed, regFieldLabel);
            RegLocalizationproposed newRegLocalizationproposedLabel = new RegLocalizationproposed();
            newRegLocalizationproposedLabel.setUuid(newRegLocalizationproposedUuidLabel);
            newRegLocalizationproposedLabel.setFieldValueIndex(0);
            newRegLocalizationproposedLabel.setInsertdate(new Date());
            newRegLocalizationproposedLabel.setRegField(regFieldLabel);
            newRegLocalizationproposedLabel.setRegItemproposed(registerRegItemproposed);
            newRegLocalizationproposedLabel.setRegLanguagecode(masterLanguage);
            newRegLocalizationproposedLabel.setRegLocalizationReference(null);
            newRegLocalizationproposedLabel.setValue(labelValue);
            newRegLocalizationproposedLabel.setHref(null);
            newRegLocalizationproposedLabel.setRegAction(regAction);
            regLocalizationproposedManager.add(newRegLocalizationproposedLabel);

            //Getting the contentsummary field
            if (contentSummaryValue != null && !contentSummaryValue.isEmpty()) {
                RegField regFieldContentSummary = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID);
                String newRegLocalizationproposedUuidContentSummary = RegLocalizationproposedUuidHelper.getUuid(0, masterLanguage, registerRegItemproposed, regFieldContentSummary);
                RegLocalizationproposed newRegLocalizationproposedContentSummary = new RegLocalizationproposed();
                newRegLocalizationproposedContentSummary.setUuid(newRegLocalizationproposedUuidContentSummary);
                newRegLocalizationproposedContentSummary.setFieldValueIndex(0);
                newRegLocalizationproposedContentSummary.setInsertdate(new Date());
                newRegLocalizationproposedContentSummary.setRegField(regFieldContentSummary);
                newRegLocalizationproposedContentSummary.setRegItemproposed(registerRegItemproposed);
                newRegLocalizationproposedContentSummary.setRegLanguagecode(masterLanguage);
                newRegLocalizationproposedContentSummary.setRegLocalizationReference(null);
                newRegLocalizationproposedContentSummary.setValue(contentSummaryValue);
                newRegLocalizationproposedContentSummary.setHref(null);
                newRegLocalizationproposedContentSummary.setRegAction(regAction);
                regLocalizationproposedManager.add(newRegLocalizationproposedContentSummary);
            }
            entityManager.getTransaction().commit();

            // -- Add the group relations -- //
            // Getting the registrOwner field
            RegField registerOwnerRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER);
            mapGroup(registerRegItemproposed, registerOwnerGroupUuid, registerOwnerRegField);

            RegField registerManagerRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER);
            mapGroup(registerRegItemproposed, registerManagerGroupUuid, registerManagerRegField);

            RegField controlBodyRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY);
            mapGroup(registerRegItemproposed, controlBodyGroupUuid, controlBodyRegField);

            RegField submittingOrganizationRegField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS);
            for (String submittingOrganizationGroupUuid : submittingOrganizationGroupUuids) {
                mapGroup(registerRegItemproposed, submittingOrganizationGroupUuid, submittingOrganizationRegField);
            }

            if (entityManager != null) {
                entityManager.close();
            }
        }

        return null;
    }

    private void mapGroup(RegItemproposed registerRegItemproposed, String groupUuid, RegField groupRegField) throws Exception {
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
        String registerOwnerNewUuid = RegItemproposedRegGroupRegRoleMappingUuidHelper.getUuid(registerRegItemproposed.getUuid(), groupUuid, groupRegField.getRegRoleReference().getUuid());
        try {
            // Check if the mapping is already available
            regItemproposedRegGroupRegRoleMappingManager.get(registerOwnerNewUuid);
        } catch (NoResultException e) {

            //Getting the reg group passed from parameter
            RegGroupManager regGroupManager = new RegGroupManager(entityManager);
            RegGroup regGroup;
            try {
//                if (groupUuid == null) {
//                    regGroup = regGroupManager.getByLocalid("registryManager");
//                } else {
                    regGroup = regGroupManager.get(groupUuid);
//                }

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = new RegItemproposedRegGroupRegRoleMapping();
                regItemproposedRegGroupRegRoleMapping.setUuid(registerOwnerNewUuid);
                regItemproposedRegGroupRegRoleMapping.setInsertdate(new Date());
                regItemproposedRegGroupRegRoleMapping.setRegGroup(regGroup);
                regItemproposedRegGroupRegRoleMapping.setRegItemproposed(registerRegItemproposed);
                regItemproposedRegGroupRegRoleMapping.setRegRole(groupRegField.getRegRoleReference());

                regItemproposedRegGroupRegRoleMappingManager.add(regItemproposedRegGroupRegRoleMapping);

                entityManager.getTransaction().commit();
            } catch (NoResultException e1) {
            }
        }
    }
}
