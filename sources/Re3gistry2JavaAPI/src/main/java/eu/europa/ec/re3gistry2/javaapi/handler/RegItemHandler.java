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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationUuidHelper;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import java.util.ArrayList;

public class RegItemHandler {

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
    public RegItemHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    /**
     * This method move an RegItemproposed to an RegItem
     *
     * @param regItemProposed
     * @return
     * @throws java.lang.Exception
     */
    public String regItemProposedToRegItem(RegItemproposed regItemProposed) throws Exception {

        // Instantiating managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemhistoryRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);

        String operationSuccess = null;
        RegItem regItem = null;
        RegItem newRegItem = null;

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

                // Getting the curent latest version of thr RegItem if available
                if (regItemProposed.getRegItemReference() != null) {
                    regItem = regItemProposed.getRegItemReference();
                }

                // If the regItem is not there, creating it
                if (regItem == null) {

                    newRegItem = new RegItem();

                    // Getting eventual collection of the RegItemproposed
                    String newRegItemUuid = RegItemUuidHelper.getUuid(regItemProposed.getLocalid(), regItemCollection, regItemProposed.getRegItemclass());

                    newRegItem.setUuid(newRegItemUuid);
                    newRegItem.setLocalid(regItemProposed.getLocalid());
                    newRegItem.setRegItemclass(regItemProposed.getRegItemclass());
                    newRegItem.setInsertdate(new Date());
                    newRegItem.setEditdate(regItemProposed.getEditdate());
                    newRegItem.setRegAction(regItemProposed.getRegAction());
                    newRegItem.setRegUser(regItemProposed.getRegUser());
                    newRegItem.setRorExport(regItemProposed.getRorExport());
                    newRegItem.setRegStatus(regItemProposed.getRegStatus());
                    newRegItem.setExternal((regItemProposed.getExternal() != null) ? regItemProposed.getExternal() : Boolean.FALSE);
                    newRegItem.setCurrentversion(BaseConstants.KEY_LATEST_VERSION);

                    regItemManager.add(newRegItem);

                    regItem = newRegItem;

                } else {
                    // update the existing RegItem
                    regItem.setLocalid(regItemProposed.getLocalid());
                    regItem.setRegItemclass(regItemProposed.getRegItemclass());
                    regItem.setInsertdate(new Date());
                    regItem.setEditdate(regItemProposed.getEditdate());
                    regItem.setRegAction(regItemProposed.getRegAction());
                    regItem.setRegUser(regItemProposed.getRegUser());
                    regItem.setRorExport(regItemProposed.getRorExport());
                    regItem.setRegStatus(regItemProposed.getRegStatus());
                    regItem.setExternal((regItemProposed.getExternal() != null) ? regItemProposed.getExternal() : Boolean.FALSE);

                    regItemManager.update(regItem);
                }

            List <RegRelation> relationsDlt = new ArrayList();
                //  Copying the RegRelationproposed to RegRelation (if needed)
                List<RegRelationproposed> regRelationnproposeds = regRelationproposedManager.getAllBySubject(regItemProposed);
            for (RegRelationproposed tmpRegRelationproposed : regRelationnproposeds) {

                    boolean relationNotFound = false;

                    // If this is not a new RegItem, updating the reg relation
                    if (newRegItem == null) {
                        try {
                            if (tmpRegRelationproposed.getRegRelationReference() != null && !tmpRegRelationproposed.getRegItemObject().equals(tmpRegRelationproposed.getRegRelationReference().getRegItemObject())) {
                                RegRelation regRelationTmp = regRelationManager.get(tmpRegRelationproposed.getRegRelationReference().getUuid());
                                regRelationTmp.setEditdate(tmpRegRelationproposed.getEditdate());
                                regRelationTmp.setRegItemObject(tmpRegRelationproposed.getRegItemObject());
                                if(tmpRegRelationproposed.getRegRelationpredicate().getUuid().equals("7")){
                                    relationsDlt.add(tmpRegRelationproposed.getRegRelationReference());
                                    relationNotFound = true;
                                }
                            } 
                            else if(tmpRegRelationproposed.getRegRelationReference() == null){
                                relationNotFound = true;
                            }
                        } catch (NoResultException e) {
                            relationNotFound = true;
                        }
                    }

                    // If this is a new RegItem or the relation was not found,
                    // creating the relation
                    if (newRegItem != null || relationNotFound) {
                        
                            RegRelation newRegRelation = new RegRelation();
                            String newUuid = RegRelationUuidHelper.getUuid(regItem, tmpRegRelationproposed.getRegRelationpredicate(), tmpRegRelationproposed.getRegItemObject());

                            newRegRelation.setUuid(newUuid);
                            newRegRelation.setInsertdate(new Date());
                            newRegRelation.setEditdate(tmpRegRelationproposed.getEditdate());
                            newRegRelation.setRegItemObject(tmpRegRelationproposed.getRegItemObject());
                            newRegRelation.setRegItemSubject(regItem);
                            newRegRelation.setRegRelationpredicate(tmpRegRelationproposed.getRegRelationpredicate());

                            regRelationManager.add(newRegRelation);
                            
                        
                    }
                }

                //Copying the RegRelationproposed by object to RegRelation (if needed)
                List<RegRelationproposed> regRelationnproposedsByObject = regRelationproposedManager.getAllByObject(regItemProposed);
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposedsByObject) {

                    boolean relationNotFound = false;

                    // If this is not a new RegItem, updating the reg relation
                    if (newRegItem == null) {
                        try {
                            if (tmpRegRelationproposed.getRegRelationReference() != null) {
                                RegRelation regRelationTmp = regRelationManager.get(tmpRegRelationproposed.getRegRelationReference().getUuid());
                                regRelationTmp.setEditdate(tmpRegRelationproposed.getEditdate());
                                regRelationTmp.setRegItemObject(tmpRegRelationproposed.getRegItemSubject());
                            } else {
                                relationNotFound = true;
                            }
                        } catch (NoResultException e) {
                            relationNotFound = true;
                        }
                    }

                    // If this is a new RegItem or the relation was not found,
                    // creating the relation
                    if (newRegItem != null || relationNotFound) {

                        RegRelation newRegRelation = new RegRelation();
                        String newUuid = RegRelationUuidHelper.getUuid(regItem, tmpRegRelationproposed.getRegRelationpredicate(), tmpRegRelationproposed.getRegItemSubject());

                        newRegRelation.setUuid(newUuid);
                        newRegRelation.setInsertdate(new Date());
                        newRegRelation.setEditdate(tmpRegRelationproposed.getEditdate());
                        newRegRelation.setRegItemObject(regItem);
                        newRegRelation.setRegItemSubject(tmpRegRelationproposed.getRegItemSubject());
                        newRegRelation.setRegRelationpredicate(tmpRegRelationproposed.getRegRelationpredicate());

                        regRelationManager.add(newRegRelation);
                    }
                }

                // Copying the RegItemproposedLocalizations to RegItemLocalization and removing them
                List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemProposed);
                
                for (RegLocalizationproposed tmpRegLocalizationproposed : regLocalizationproposeds) {
                    RegLocalization tmpRegLocalization = tmpRegLocalizationproposed.getRegLocalizationReference();
                    
                    // Removingthe relation if it is a remove relation request
                    if (tmpRegLocalizationproposed.getValue() == null && tmpRegLocalizationproposed.getRegRelationproposedReference() == null) {
                        tmpRegLocalization = tmpRegLocalizationproposed.getRegLocalizationReference();
                        RegRelation tmpRegRelation = tmpRegLocalization.getRegRelationReference();

                        // Removing the localization referencing to the RegRealtion
                        regLocalizationManager.delete(tmpRegLocalization);

                        // Removing the related RegRelation
                        if (tmpRegRelation != null) {
                            regRelationManager.delete(tmpRegRelation);
                        }

                    } else {
                        String newUuid = RegLocalizationUuidHelper.getUuid(tmpRegLocalizationproposed.getFieldValueIndex(), tmpRegLocalizationproposed.getRegLanguagecode(), regItem, tmpRegLocalizationproposed.getRegField());
                        if(tmpRegLocalizationproposed.getValue() == null && tmpRegLocalizationproposed.getRegLocalizationReference()!=null){
                            newUuid = tmpRegLocalizationproposed.getRegLocalizationReference().getUuid();
                    }

                        // Check if the RegLocalization is available
                        RegLocalization regLocalization = null;
                        try {
                            regLocalization = regLocalizationManager.get(newUuid);
                        } catch (NoResultException e) {
                        }

                        if (regLocalization == null) {

                            RegLocalization newRegLocalization = new RegLocalization();
                            newRegLocalization.setUuid(newUuid);
                            newRegLocalization.setInsertdate(new Date());
                            newRegLocalization.setValue(tmpRegLocalizationproposed.getValue());
                            newRegLocalization.setRegLanguagecode(tmpRegLocalizationproposed.getRegLanguagecode());
                            newRegLocalization.setRegField(tmpRegLocalizationproposed.getRegField());
                            newRegLocalization.setHref(tmpRegLocalizationproposed.getHref());
                            newRegLocalization.setEditdate(tmpRegLocalizationproposed.getEditdate());
                            newRegLocalization.setFieldValueIndex(tmpRegLocalizationproposed.getFieldValueIndex());
                            newRegLocalization.setRegItem(regItem);
                            newRegLocalization.setRegAction(tmpRegLocalizationproposed.getRegAction());

                            RegRelationproposed tmpRegRelationProposed = tmpRegLocalizationproposed.getRegRelationproposedReference();
                            if (tmpRegRelationProposed != null) {
                                String newUuidRelation = RegRelationUuidHelper.getUuid(regItem, tmpRegRelationProposed.getRegRelationpredicate(), tmpRegRelationProposed.getRegItemObject());

                                // Getting the related RegRelation
                                try {
                                    RegRelation tmpRegRelation = regRelationManager.get(newUuidRelation);
                                    newRegLocalization.setRegRelationReference(tmpRegRelation);
                                } catch (NoResultException e) {
                                    newRegLocalization.setRegRelationReference(null);
                                }
                            } else {
                                newRegLocalization.setRegRelationReference(null);
                            }

                            regLocalizationManager.add(newRegLocalization);
                        } else {
                            // Updating the existing RegLocalization
                            regLocalization.setUuid(newUuid);
                            regLocalization.setInsertdate(new Date());
                            regLocalization.setValue(tmpRegLocalizationproposed.getValue());
                            regLocalization.setRegLanguagecode(tmpRegLocalizationproposed.getRegLanguagecode());
                            regLocalization.setRegField(tmpRegLocalizationproposed.getRegField());
                            regLocalization.setHref(tmpRegLocalizationproposed.getHref());
                            regLocalization.setEditdate(tmpRegLocalizationproposed.getEditdate());
                            regLocalization.setFieldValueIndex(tmpRegLocalizationproposed.getFieldValueIndex());
                            regLocalization.setRegItem(regItem);
                            regLocalization.setRegAction(tmpRegLocalizationproposed.getRegAction());

                            RegRelationproposed tmpRegRelationProposed = tmpRegLocalizationproposed.getRegRelationproposedReference();
                            if (tmpRegRelationProposed != null) {
                                String newUuidRelation = RegRelationUuidHelper.getUuid(regItem, tmpRegRelationProposed.getRegRelationpredicate(), tmpRegRelationProposed.getRegItemObject());

                                // Getting the related RegRelation
                                try {
                                    RegRelation tmpRegRelation = regRelationManager.get(newUuidRelation);
                                    regLocalization.setRegRelationReference(tmpRegRelation);
                                } catch (NoResultException e) {
                                    regLocalization.setRegRelationReference(null);
                                }
                            } else {
                                regLocalization.setRegRelationReference(null);
                            }

                            regLocalizationManager.update(regLocalization);
                        }
                    }

                    regLocalizationproposedManager.delete(tmpRegLocalizationproposed);
                    
                    
                }
                for(RegRelation relationDelete : relationsDlt){
                        regRelationManager.delete(relationDelete);
                    }

                // The removal of the regRelationproposed needs to be done after
                // the removal of the RegLocalizationproposed because of foreign
                // key constraints
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposeds) {
                    regRelationproposedManager.delete(tmpRegRelationproposed);
                }
                for (RegRelationproposed tmpRegRelationproposed : regRelationnproposedsByObject) {
                    regRelationproposedManager.delete(tmpRegRelationproposed);
                }

                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Copy RegItemproposedRegGroupRegRoleMapping to RegItemRegGroupRegRoleMapping and removing them
                List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemProposed);
                for (RegItemproposedRegGroupRegRoleMapping tmpRegItemproposedRegGroupRegRoleMapping : regItemproposedRegGroupRegRoleMappings) {

                    RegItemRegGroupRegRoleMapping newRegItemRegGroupRegRoleMapping = new RegItemRegGroupRegRoleMapping();
                    String newUuid = RegItemRegGroupRegRoleMappingUuidHelper.getUuid(regItem.getUuid(), tmpRegItemproposedRegGroupRegRoleMapping.getRegGroup().getUuid(), tmpRegItemproposedRegGroupRegRoleMapping.getRegRole().getUuid());

                    // Check if the mapping is already there, nothing to do
                    try {

                        regItemhistoryRegGroupRegRoleMappingManager.get(newUuid);

                    } catch (NoResultException e) {
                        //If it is not there, adding it

                        newRegItemRegGroupRegRoleMapping.setUuid(newUuid);
                        newRegItemRegGroupRegRoleMapping.setInsertdate(new Date());
                        newRegItemRegGroupRegRoleMapping.setEditdate(tmpRegItemproposedRegGroupRegRoleMapping.getEditdate());
                        newRegItemRegGroupRegRoleMapping.setRegGroup(tmpRegItemproposedRegGroupRegRoleMapping.getRegGroup());
                        newRegItemRegGroupRegRoleMapping.setRegItem(regItem);
                        newRegItemRegGroupRegRoleMapping.setRegRole(tmpRegItemproposedRegGroupRegRoleMapping.getRegRole());

                        regItemhistoryRegGroupRegRoleMappingManager.add(newRegItemRegGroupRegRoleMapping);

                    }

                    // Removing the RegItemproposedRegGroupRegRoleMapping
                    regItemproposedRegGroupRegRoleMappingManager.delete(tmpRegItemproposedRegGroupRegRoleMapping);
                }

                entityManager.getTransaction().commit();

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Removing the RegItemProposed
                regItemproposedManager.delete(regItemProposed);

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

            // Adding the RegItem to Solr
            try {
                SolrHandler.indexSingleItem(regItem);
            } catch (Exception e) {
                logger.error("@ RegItemHelper.regItemProposedToRegItem: Solr indexing error.", e);
            }

        } catch (NoResultException e) {
            logger.error("@ RegItemHelper.regItemProposedToRegItem: unable to perform the requests.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error("@ RegItemHelper.regItemProposedToRegItem: generic error.", e);
            operationSuccess = systemLocalization.getString(BaseConstants.KEY_ERROR_GENERIC);
        } finally {

        }

        return operationSuccess;
    }
           
        public void removeUnusedRelations(RegItemproposed regItemproposed){
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            
            try{
                synchronized (sync) {

                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                RegItem regItem = regItemproposed.getRegItemReference();
                List<RegRelation> itemRelations = new ArrayList();
                itemRelations = regRelationManager.getAllBySubject(regItem);
                RegLocalization tmpLocalization;
                List<RegRelation> relationsDlt = new ArrayList();
                //The method getAllByRelation can only find one localization. One relation cant be on 2 localizations
                for(RegRelation relation : itemRelations){
                    tmpLocalization = null;
                    if(relation.getRegRelationpredicate().getUuid().equals("7")){
                       if(regLocalizationManager.getAllByRelation(relation).isEmpty()){
                            relationsDlt.add(relation);
                        }
                    }
                    
                }
                for(RegRelation relationDlt : relationsDlt){
                    regRelationManager.delete(relationDlt);
                }
                entityManager.getTransaction().commit();
            }
            } catch(Exception e){
                e.printStackTrace();
            }
            
               
        }
}
