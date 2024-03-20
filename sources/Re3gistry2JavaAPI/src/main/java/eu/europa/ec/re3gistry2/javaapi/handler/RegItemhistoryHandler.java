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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItemhistoryRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationhistory;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemhistoryRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemhistoryUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationhistoryUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationhistoryUuidHelper;
import java.util.ArrayList;
import static java.util.Collections.max;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

public class RegItemhistoryHandler {

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
    public RegItemhistoryHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    /**
     * This method copies RegItem to an RegItemhistory
     *
     * @param regItem
     * @return
     * @throws java.lang.Exception
     */
    public String copyRegItemToRegItemhistory(RegItem regItem) throws Exception {

        // Instantiating managers
        RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLocalizationhistoryManager regLocalizationhistoryManager = new RegLocalizationhistoryManager(entityManager);
        RegRelationhistoryManager regRelationhistoryManager = new RegRelationhistoryManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegItemhistoryRegGroupRegRoleMappingManager regItemhistoryRegGroupRegRoleMappingManager = new RegItemhistoryRegGroupRegRoleMappingManager(entityManager);

        String operationSuccess = null;
        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Getting the regRelationpredicate collection
                RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

                List<RegRelation> regItemCollections = regRelationManager.getAll(regItem, regRelationpredicateCollection);

                RegItem regItemCollection = null;
                if (regItemCollections != null && !regItemCollections.isEmpty()) {
                    // Each RegItemproposed has just one collection
                    regItemCollection = regItemCollections.get(0).getRegItemObject();
                }

                // Getting the current latest version of thr RegItemhistory if available
                int versionNumber = 0;
                try {
                    RegItemhistory maxVersionRegItemhistory = regItemhistoryManager.getMaxVersionByLocalidAndRegItemClass(regItem.getLocalid(), regItem.getRegItemclass());
                    int maxVersion = maxVersionRegItemhistory.getVersionnumber();

                    if (maxVersion > 0) {

                        int minVersion = regItemhistoryManager.getMinVersionByLocalidAndRegItemClass(maxVersionRegItemhistory.getLocalid(), maxVersionRegItemhistory.getRegItemclass()).getVersionnumber();
                        if (minVersion == 0) {
                            List<RegItemhistory> regItemHistoryList = regItemhistoryManager.getByLocalidAndRegItemClass(maxVersionRegItemhistory.getLocalid(), maxVersionRegItemhistory.getRegItemclass());
                            ArrayList<Integer> versions = new ArrayList<>();
                            for (RegItemhistory regItemhistoryItem : regItemHistoryList) {
                                versions.add(regItemhistoryItem.getVersionnumber());
                            }   
                                maxVersionRegItemhistory.setVersionnumber(max(versions)+1);
                                regItemhistoryManager.update(maxVersionRegItemhistory);
                        }

                        versionNumber = maxVersion + 1;
                    } else {
                        //history starts with 1
                        //it was 0 before the release 2.3.2:  versionNumber = 0;
                        versionNumber = 1;
                    }
                } catch (NoResultException e) {
                }

                //Creating the new RegItemhistory
                RegItemhistory newRegItemhistory = new RegItemhistory();

                // Getting eventual collection of the RegItemproposed
                String newRegItemhistoryUuid = RegItemhistoryUuidHelper.getUuid(regItem.getLocalid(), regItemCollection, regItem.getRegItemclass(), regItem, versionNumber);

                newRegItemhistory.setUuid(newRegItemhistoryUuid);
                if(versionNumber==0){
                    newRegItemhistory.setVersionnumber(versionNumber+1);
                }else{
                    newRegItemhistory.setVersionnumber(versionNumber);
                }
                
                newRegItemhistory.setLocalid(regItem.getLocalid());
                newRegItemhistory.setRegItemclass(regItem.getRegItemclass());
                newRegItemhistory.setInsertdate(new Date());
                newRegItemhistory.setEditdate(regItem.getEditdate());
                newRegItemhistory.setRegAction(regItem.getRegAction());
                newRegItemhistory.setRegItemReference(regItem);
                newRegItemhistory.setRegUser(regItem.getRegUser());
                newRegItemhistory.setRorExport(regItem.getRorExport());
                newRegItemhistory.setRegStatus(regItem.getRegStatus());
                newRegItemhistory.setExternal((regItem.getExternal() != null) ? regItem.getExternal() : Boolean.FALSE);

                regItemhistoryManager.add(newRegItemhistory);

                //  Copying the RegRelation to history
                List<RegRelation> regRelations = regRelationManager.getAllBySubject(regItem);
                for (RegRelation tmpRegRelation : regRelations) {

                    RegRelationhistory newRegRelationhistory = new RegRelationhistory();
                    String newUuid = RegRelationhistoryUuidHelper.getUuid(newRegItemhistory, null, tmpRegRelation.getRegRelationpredicate(), null, tmpRegRelation.getRegItemObject());

                    newRegRelationhistory.setUuid(newUuid);
                    newRegRelationhistory.setInsertdate(new Date());
                    newRegRelationhistory.setEditdate(tmpRegRelation.getEditdate());
                    newRegRelationhistory.setRegItemObject(tmpRegRelation.getRegItemObject());
                    newRegRelationhistory.setRegItemSubject(null);
                    newRegRelationhistory.setRegItemhistoryObject(null);
                    newRegRelationhistory.setRegItemhistorySubject(newRegItemhistory);
                    newRegRelationhistory.setRegRelationpredicate(tmpRegRelation.getRegRelationpredicate());

                    regRelationhistoryManager.add(newRegRelationhistory);
                }

                List<RegRelation> regRelationsObject = regRelationManager.getAllByObject(regItem);
                for (RegRelation tmpRegRelation : regRelationsObject) {

                    RegRelationhistory newRegRelationhistory = new RegRelationhistory();
                    String newUuid = RegRelationhistoryUuidHelper.getUuid(null, tmpRegRelation.getRegItemSubject(), tmpRegRelation.getRegRelationpredicate(), newRegItemhistory, null);

                    newRegRelationhistory.setUuid(newUuid);
                    newRegRelationhistory.setInsertdate(new Date());
                    newRegRelationhistory.setEditdate(tmpRegRelation.getEditdate());
                    newRegRelationhistory.setRegItemObject(null);
                    newRegRelationhistory.setRegItemSubject(tmpRegRelation.getRegItemSubject());
                    newRegRelationhistory.setRegItemhistoryObject(newRegItemhistory);
                    newRegRelationhistory.setRegItemhistorySubject(null);
                    newRegRelationhistory.setRegRelationpredicate(tmpRegRelation.getRegRelationpredicate());

                    regRelationhistoryManager.add(newRegRelationhistory);
                }

                // Copying the RegItemLocalizations to history and removing them
                List<RegLocalization> regLocalizations = regLocalizationManager.getAll(regItem);
                for (RegLocalization tmpRegLocalization : regLocalizations) {

                    RegLocalizationhistory newRegLocalizationhistory = new RegLocalizationhistory();
                    String newUuid = RegLocalizationhistoryUuidHelper.getUuid(tmpRegLocalization.getFieldValueIndex(), tmpRegLocalization.getRegLanguagecode(), newRegItemhistory, tmpRegLocalization.getRegField());

                    newRegLocalizationhistory.setUuid(newUuid);
                    newRegLocalizationhistory.setInsertdate(new Date());
                    newRegLocalizationhistory.setValue(tmpRegLocalization.getValue());
                    newRegLocalizationhistory.setRegLanguagecode(tmpRegLocalization.getRegLanguagecode());
                    newRegLocalizationhistory.setRegField(tmpRegLocalization.getRegField());
                    newRegLocalizationhistory.setHref(tmpRegLocalization.getHref());
                    newRegLocalizationhistory.setEditdate(tmpRegLocalization.getEditdate());
                    newRegLocalizationhistory.setFieldValueIndex(tmpRegLocalization.getFieldValueIndex());
                    newRegLocalizationhistory.setRegItemhistory(newRegItemhistory);
                    newRegLocalizationhistory.setRegAction(regItem.getRegAction());

                    RegRelation tmpRegRelation = tmpRegLocalization.getRegRelationReference();
                    if (tmpRegRelation != null) {
                        String newUuidRelationHistory = RegRelationhistoryUuidHelper.getUuid(newRegItemhistory, null, tmpRegRelation.getRegRelationpredicate(), null, tmpRegRelation.getRegItemObject());

                        // Getting the related RegRelationhistory
                        try {
                            RegRelationhistory tmpRegRelationhistory = regRelationhistoryManager.get(newUuidRelationHistory);
                            newRegLocalizationhistory.setRegRelationhistoryReference(tmpRegRelationhistory);
                        } catch (NoResultException e) {
                            newRegLocalizationhistory.setRegRelationhistoryReference(null);
                        }
                    } else {
                        newRegLocalizationhistory.setRegRelationhistoryReference(null);
                    }

                    regLocalizationhistoryManager.add(newRegLocalizationhistory);

                    // Delete regLocalization
                    // Done after the removal of the proposed items int the RegActionHandler
                }

                // The removal of the regRelation needs to be done after
                // the removal of the RegLocalization because of foreign
                // key constraints
                // Done after the removal of the proposed items int the RegActionHandler
                // Copy RegItemRegGroupRegRoleMapping to history and removing them
                List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regItem);
                for (RegItemRegGroupRegRoleMapping tmpRegItemRegGroupRegRoleMapping : regItemRegGroupRegRoleMappings) {

                    RegItemhistoryRegGroupRegRoleMapping newRegItemhistoryRegGroupRegRoleMapping = new RegItemhistoryRegGroupRegRoleMapping();
                    String newUuid = RegItemhistoryRegGroupRegRoleMappingUuidHelper.getUuid(newRegItemhistory.getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegGroup().getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegRole().getUuid());

                    newRegItemhistoryRegGroupRegRoleMapping.setUuid(newUuid);
                    newRegItemhistoryRegGroupRegRoleMapping.setInsertdate(new Date());
                    newRegItemhistoryRegGroupRegRoleMapping.setEditdate(tmpRegItemRegGroupRegRoleMapping.getEditdate());
                    newRegItemhistoryRegGroupRegRoleMapping.setRegGroup(tmpRegItemRegGroupRegRoleMapping.getRegGroup());
                    newRegItemhistoryRegGroupRegRoleMapping.setRegItemhistory(newRegItemhistory);
                    newRegItemhistoryRegGroupRegRoleMapping.setRegRole(tmpRegItemRegGroupRegRoleMapping.getRegRole());

                    regItemhistoryRegGroupRegRoleMappingManager.add(newRegItemhistoryRegGroupRegRoleMapping);

                    // Delete RegItemRegGroupRegRoleMapping
                    // Done after the removal of the proposed items int the RegActionHandler
                }

                // Removing the RegItem
                // Done after the removal of the proposed items int the RegActionHandler
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
            logger.error("@ RegItemclassHandler.reorderRegItemclass: unable to get the RegItemclass.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegItemclassHandler.reorderRegItemclass: generic error.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        }

        return operationSuccess;
    }

    /**
     * This method move an RegItemproposed to an RegItemhistory
     *
     * @param regItemProposed
     * @return
     */
    public String regItemProposedToRegItemhistory(RegItemproposed regItemProposed) throws Exception {

        // Instantiating managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLocalizationhistoryManager regLocalizationhistoryManager = new RegLocalizationhistoryManager(entityManager);
        RegRelationhistoryManager regRelationhistoryManager = new RegRelationhistoryManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
        RegItemhistoryRegGroupRegRoleMappingManager regItemhistoryRegGroupRegRoleMappingManager = new RegItemhistoryRegGroupRegRoleMappingManager(entityManager);

        String operationSuccess = null;
        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Getting the regRelationpredicate collection
                RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

                List<RegRelationproposed> regItemproposedCollections = regRelationproposedManager.getAll(regItemProposed, regRelationpredicateCollection);

                RegItem regItemCollection = null;
                if (regItemproposedCollections != null && !regItemproposedCollections.isEmpty()) {
                    // Each RegItemproposed has just one collection
                    regItemCollection = regItemproposedCollections.get(0).getRegItemObject();
                }

                // Getting the curent latest version of thr RegItemhistory if available
                int versionNumber = 0;
                try {
                    RegItemhistory regItemHistory = regItemhistoryManager.getMinVersionByLocalidAndRegItemClass(regItemProposed.getLocalid(), regItemProposed.getRegItemclass());
                    versionNumber = regItemHistory.getVersionnumber() - 1;
                } catch (NoResultException e) {
                }

                //Creating the new RegItemhistory
                RegItemhistory newRegItemhistory = new RegItemhistory();

                // Getting eventual collection of the RegItemproposed
                String newRegItemhistoryUuid = RegItemhistoryUuidHelper.getUuid(regItemProposed.getLocalid(), regItemCollection, regItemProposed.getRegItemclass(), regItemProposed.getRegItemReference(), versionNumber);

                newRegItemhistory.setUuid(newRegItemhistoryUuid);
                newRegItemhistory.setVersionnumber(versionNumber);
                newRegItemhistory.setLocalid(regItemProposed.getLocalid());
                newRegItemhistory.setRegItemclass(regItemProposed.getRegItemclass());
                newRegItemhistory.setInsertdate(new Date());
                newRegItemhistory.setEditdate(regItemProposed.getEditdate());
                newRegItemhistory.setRegAction(regItemProposed.getRegAction());
                newRegItemhistory.setRegItemReference(regItemProposed.getRegItemReference());
                newRegItemhistory.setRegUser(regItemProposed.getRegUser());
                newRegItemhistory.setRorExport(regItemProposed.getRorExport());
                newRegItemhistory.setRegStatus(regItemProposed.getRegStatus());
                newRegItemhistory.setExternal((regItemProposed.getExternal() != null) ? regItemProposed.getExternal() : Boolean.FALSE);

                regItemhistoryManager.add(newRegItemhistory);

                //  Copying the RegRelationproposed to history
                List<RegRelationproposed> regRelationnproposeds = regRelationproposedManager.getAllBySubject(regItemProposed);
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposeds) {

                    RegRelationhistory newRegRelationhistory = new RegRelationhistory();
                    String newUuid = RegRelationhistoryUuidHelper.getUuid(newRegItemhistory, null, tmpRegRelationproposed.getRegRelationpredicate(), null, tmpRegRelationproposed.getRegItemObject());

                    newRegRelationhistory.setUuid(newUuid);
                    newRegRelationhistory.setInsertdate(new Date());
                    newRegRelationhistory.setEditdate(tmpRegRelationproposed.getEditdate());
                    newRegRelationhistory.setRegItemObject(tmpRegRelationproposed.getRegItemObject());
                    newRegRelationhistory.setRegItemSubject(null);
                    newRegRelationhistory.setRegItemhistoryObject(null);
                    newRegRelationhistory.setRegItemhistorySubject(newRegItemhistory);
                    newRegRelationhistory.setRegRelationpredicate(tmpRegRelationproposed.getRegRelationpredicate());

                    regRelationhistoryManager.add(newRegRelationhistory);
                }

                List<RegRelationproposed> regRelationnproposedsObject = regRelationproposedManager.getAllByObject(regItemProposed);
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposedsObject) {

                    RegRelationhistory newRegRelationhistory = new RegRelationhistory();
                    String newUuid = RegRelationhistoryUuidHelper.getUuid(null, tmpRegRelationproposed.getRegItemSubject(), tmpRegRelationproposed.getRegRelationpredicate(), newRegItemhistory, null);

                    newRegRelationhistory.setUuid(newUuid);
                    newRegRelationhistory.setInsertdate(new Date());
                    newRegRelationhistory.setEditdate(tmpRegRelationproposed.getEditdate());
                    newRegRelationhistory.setRegItemObject(null);
                    newRegRelationhistory.setRegItemSubject(tmpRegRelationproposed.getRegItemSubject());
                    newRegRelationhistory.setRegItemhistoryObject(newRegItemhistory);
                    newRegRelationhistory.setRegItemhistorySubject(null);
                    newRegRelationhistory.setRegRelationpredicate(tmpRegRelationproposed.getRegRelationpredicate());

                    regRelationhistoryManager.add(newRegRelationhistory);
                }

                // Copying the RegItemproposedLocalizations to history and removing them
                List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemProposed);
                for (RegLocalizationproposed tmpRegLocalizationproposed : regLocalizationproposeds) {

                    RegLocalizationhistory newRegLocalizationhistory = new RegLocalizationhistory();
                    String newUuid = RegLocalizationhistoryUuidHelper.getUuid(tmpRegLocalizationproposed.getFieldValueIndex(), tmpRegLocalizationproposed.getRegLanguagecode(), newRegItemhistory, tmpRegLocalizationproposed.getRegField());

                    newRegLocalizationhistory.setUuid(newUuid);
                    newRegLocalizationhistory.setInsertdate(new Date());
                    newRegLocalizationhistory.setValue(tmpRegLocalizationproposed.getValue());
                    newRegLocalizationhistory.setRegLanguagecode(tmpRegLocalizationproposed.getRegLanguagecode());
                    newRegLocalizationhistory.setRegField(tmpRegLocalizationproposed.getRegField());
                    newRegLocalizationhistory.setHref(tmpRegLocalizationproposed.getHref());
                    newRegLocalizationhistory.setEditdate(tmpRegLocalizationproposed.getEditdate());
                    newRegLocalizationhistory.setFieldValueIndex(tmpRegLocalizationproposed.getFieldValueIndex());
                    newRegLocalizationhistory.setRegItemhistory(newRegItemhistory);
                    newRegLocalizationhistory.setRegAction(regItemProposed.getRegAction());

                    RegRelationproposed tmpRegRelationProposed = tmpRegLocalizationproposed.getRegRelationproposedReference();
                    if (tmpRegRelationProposed != null) {
                        String newUuidRelationHistory = RegRelationhistoryUuidHelper.getUuid(newRegItemhistory, null, tmpRegRelationProposed.getRegRelationpredicate(), null, tmpRegRelationProposed.getRegItemObject());

                        // Getting the related RegRelationhistory
                        try {
                            RegRelationhistory tmpRegRelationhistory = regRelationhistoryManager.get(newUuidRelationHistory);
                            newRegLocalizationhistory.setRegRelationhistoryReference(tmpRegRelationhistory);
                        } catch (NoResultException e) {
                            newRegLocalizationhistory.setRegRelationhistoryReference(null);
                        }
                    } else {
                        newRegLocalizationhistory.setRegRelationhistoryReference(null);
                    }

                    regLocalizationhistoryManager.add(newRegLocalizationhistory);

                    regLocalizationproposedManager.delete(tmpRegLocalizationproposed);
                }

                // The removal of the regRelationproposed needs to be done after
                // the removal of the RegLocalizationproposed because of foreign
                // key constraints
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposeds) {
                    regRelationproposedManager.delete(tmpRegRelationproposed);
                }
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposedsObject) {
                    regRelationproposedManager.delete(tmpRegRelationproposed);
                }

                // Copy RegItemproposedRegGroupRegRoleMapping to history and removing them
                List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemProposed);
                for (RegItemproposedRegGroupRegRoleMapping tmpRegItemproposedRegGroupRegRoleMapping : regItemproposedRegGroupRegRoleMappings) {

                    RegItemhistoryRegGroupRegRoleMapping newRegItemhistoryRegGroupRegRoleMapping = new RegItemhistoryRegGroupRegRoleMapping();
                    String newUuid = RegItemhistoryRegGroupRegRoleMappingUuidHelper.getUuid(newRegItemhistory.getUuid(), tmpRegItemproposedRegGroupRegRoleMapping.getRegGroup().getUuid(), tmpRegItemproposedRegGroupRegRoleMapping.getRegRole().getUuid());

                    newRegItemhistoryRegGroupRegRoleMapping.setUuid(newUuid);
                    newRegItemhistoryRegGroupRegRoleMapping.setInsertdate(new Date());
                    newRegItemhistoryRegGroupRegRoleMapping.setEditdate(tmpRegItemproposedRegGroupRegRoleMapping.getEditdate());
                    newRegItemhistoryRegGroupRegRoleMapping.setRegGroup(tmpRegItemproposedRegGroupRegRoleMapping.getRegGroup());
                    newRegItemhistoryRegGroupRegRoleMapping.setRegItemhistory(newRegItemhistory);
                    newRegItemhistoryRegGroupRegRoleMapping.setRegRole(tmpRegItemproposedRegGroupRegRoleMapping.getRegRole());

                    regItemhistoryRegGroupRegRoleMappingManager.add(newRegItemhistoryRegGroupRegRoleMapping);

                    regItemproposedRegGroupRegRoleMappingManager.delete(tmpRegItemproposedRegGroupRegRoleMapping);
                }

                // Removing the RegItemProposed
                regItemproposedManager.delete(regItemProposed);

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
            logger.error("@ RegItemclassHandler.reorderRegItemclass: unable to get the RegItemclass.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            logger.error("@ RegItemclassHandler.reorderRegItemclass: generic error.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        }

        return operationSuccess;
    }
}