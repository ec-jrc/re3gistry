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
 * through Action 2016.10: European Location Interoperability Solutions
 */
package eu.europa.ec.re3gistry2.migration.manager;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.model.utility.UuidHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldtypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationHandler;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Customattribute;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Item;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclass;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclasscustomattribute;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemcollection;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemparent;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemsuccessor;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegFieldtype;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldmappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemhistoryUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateItems {

    private EntityManager entityManagerRe3gistry2;
    private EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;
    private final MigrationManager migrationManager;

    public MigrateItems(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
    }

    public void startMigrationItems() throws Exception {
        ArrayList<RegItem> itemsToIndexSOLR = new ArrayList<>();
        /**
         * commit is true because we want to commit everything related to item
         * class creation
         */

        boolean commit = false;
        int procedureorder = migrationManager.getProcedureorder();

        /**
         * itemMapHistory contains the uuid of old item from old DB and u uuid
         * of new regItemHistory
         */
        HashMap<String, String> correspondentItemToRegItemHistoryMap = new HashMap<>();

        /**
         * migrate item - item class
         */
        try {
            /**
             * get all the item class for the item
             */
            Query queryItemclass = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_BY_DATAPROCEDUREORDER_ASC);
            List<Itemclass> itemclassList;
            try {
                itemclassList = queryItemclass.getResultList();
            } catch (Exception ex) {
                logger.error("Error in  getting the result list for " + queryItemclass + " " + ex.getMessage());
                throw new Exception("Error in  getting the localization for " + queryItemclass + " " + ex.getMessage());
            }

            MigrateItemLatestVersion migrateItemLatestVersion = new MigrateItemLatestVersion(migrationManager);
            MigrateItemHistory migrateItemHistory = new MigrateItemHistory(migrationManager);

            for (Itemclass itemclass : itemclassList) {

                    Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER = 0;

                    /**
                     * if the reg item class exist than use than one, otherwise
                     * create it
                     */
                    RegItemclass regItemclass;
                    try {
                        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
                        regItemclass = regItemclassManager.getByLocalid(itemclass.getUriname());
                    } catch (Exception exe) {
                        try {
                            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManagerRe3gistry2);
                            RegItemclasstype regItemclasstypeItem = regItemclasstypeManager.getByLocalid("item");
                            regItemclass = addRegItemClassByItemclass(itemclass, procedureorder, regItemclasstypeItem, commit);
                        } catch (Exception ex) {
                            logger.error(ex.getMessage());
                            throw new Exception(ex.getMessage());
                        }
                    }
                    procedureorder++;

                    RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
                    HashMap<String, RegField> regFieldsMap = new HashMap<>();
                    regFieldsMap.put("label", regInstallationHandler.createDefaultField(regItemclass, "label", Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_STRING_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("definition", regInstallationHandler.createDefaultField(regItemclass, "definition", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("description", regInstallationHandler.createDefaultField(regItemclass, "description", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));

                    regFieldsMap.put("collection", regInstallationHandler.createDefaultField(regItemclass, "collection", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_COLLECTION_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("parent", regInstallationHandler.createDefaultField(regItemclass, "parent", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_PARENT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("successor", regInstallationHandler.createDefaultField(regItemclass, "successor", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("predecessor", regInstallationHandler.createDefaultField(regItemclass, "predecessor", Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("registry", regInstallationHandler.createDefaultField(regItemclass, "registry", Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_REGISTRY_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));
                    regFieldsMap.put("register", regInstallationHandler.createDefaultField(regItemclass, "register", Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_REGISTER_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));

                    AUTO_INCREMENT_REG_FIELD_LIST_ORDER = migrateCustomattributesByItemClass(itemclass, regItemclass, regFieldsMap, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                    regFieldsMap.put("status", regInstallationHandler.createDefaultField(regItemclass, "status", Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_STATUS_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit));

                    /**
                     * now you can commit all the item classes
                     */
                    try {
                        if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                            entityManagerRe3gistry2.getTransaction().begin();
                        }
                        entityManagerRe3gistry2.getTransaction().commit();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                        throw new Exception(ex.getMessage());
                    }
                    /**
                     * commit is false because we don't want to commit anything
                     * before the end of the item creation so, once the item and
                     * all its related relations have been created, than we
                     * commit the entire item and relations
                     */
                    commit = false;
                    try {
                        Query queryItemByItemClass = entityManagerRe3gistry2Migration.createNativeQuery(ConstantsMigration.ITEMS_LATEST_VERSION_LIST_BY_ITEMCLASS.replace(":" + ConstantsMigration.KEY_PARAMETER_ITEMCLASS, "'" + itemclass.getUuid() + "'"), ConstantsMigration.KEY_PARAMETER_ITEMRESULT);
                        queryItemByItemClass.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, itemclass);

                        List<Item> itemsList;
                        try {
                            itemsList = queryItemByItemClass.getResultList();
                        } catch (Exception ex) {
                            logger.error("Error in  getting the result list for " + queryItemByItemClass + " " + ex.getMessage());
                            throw new Exception("Error in  getting the localization for " + queryItemByItemClass + " " + ex.getMessage());
                        }

                        for (Item item : itemsList) {

                            Item collection = getCollectionFromItem(item);

                            RegItem regItem = migrateItemLatestVersion.migrateItemLatestVersion(item, collection, regItemclass, regFieldsMap, item.getItemclass(), commit);
                            itemsToIndexSOLR.add(regItem);
                            /**
                             * check if item has other versions*
                             */
                            try {
                                List<Item> previousVersions = getAllItemVersionOrderDescByVersionnumber(item);
                                if (previousVersions != null) {
                                    for (Item previousVersionItem : previousVersions) {
                                        if (previousVersionItem.getVersionnumber() != 0) {

                                            Item collectionHistory = getCollectionFromItem(previousVersionItem);
                                            RegItemhistory regItemHistory = migrateItemHistory.migrateItemHistory(previousVersionItem, collectionHistory, regItemclass, regFieldsMap, regItem, commit);
                                            correspondentItemToRegItemHistoryMap.put(previousVersionItem.getUuid() + previousVersionItem.getVersionnumber(), regItemHistory.getUuid());
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage());
                                throw new Exception(ex.getMessage());
                            }

                            try {
                                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                                    entityManagerRe3gistry2.getTransaction().begin();
                                }
                                entityManagerRe3gistry2.getTransaction().commit();
                            } catch (Exception ex) {
                                logger.error(ex.getMessage());
                                throw new Exception(ex.getMessage());
                            }

                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                    }
            }

            /**
             * index SOLR
             */
            itemsToIndexSOLR.forEach((regItem) -> {
                try {
                    SolrHandler.indexSingleItem(regItem);
                } catch (Exception e) {
                    logger.error("@ MigrateItems.startMigrationItems: Solr indexing error.", e);
                }
            });
            /**
             * migrate parent the commit is coming back to true because we want
             * to commit everything once is created
             */
            commit = true;

            /**
             * migrate collection
             */
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManagerRe3gistry2);
            try {
                List<Itemcollection> itemListCollection = null;
                try {
                    Query queryCollection = entityManagerRe3gistry2Migration.createNamedQuery("Itemcollection.findAll", Itemcollection.class);
                    itemListCollection = queryCollection.getResultList();

                } catch (Exception ex) {

                    try {
                        if (entityManagerRe3gistry2Migration.isOpen()) {
                            entityManagerRe3gistry2Migration.close();
                        }
                        migrationManager.getEntityManagerFactoryRe3gistry2Migration();
                        entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();

                        if (!entityManagerRe3gistry2Migration.getTransaction().isActive()) {
                            entityManagerRe3gistry2Migration.getTransaction().begin();
                        }
                        Query queryCollection = entityManagerRe3gistry2Migration.createNamedQuery("Itemcollection.findAll", Itemcollection.class);
                        itemListCollection = queryCollection.getResultList();
                    } catch (Exception exx) {
                        logger.error(exx.getMessage());
                        throw new Exception(exx.getMessage());
                    }
                }

                String key = BaseConstants.KEY_PREDICATE_COLLECTION;
                for (Itemcollection itemcollection : itemListCollection) {
                    Item item = itemcollection.getItem();
                    Item collection = itemcollection.getCollection();

                    String itemUuid = item.getUuid();
                    int itemVersion = item.getVersionnumber();
                    int collectionVersion = collection.getVersionnumber();
                    String collectionUuid = collection.getUuid();

                    try {
                        if (itemVersion == 0) {
                            RegItem regItem = getRegItemFromItem(item);
                            if (collectionVersion == 0) {
                                RegItem regItemCollection = getRegItemFromItem(collection);
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemCollection, commit, key);
                            } else {
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(collectionUuid + collectionVersion)), commit, key);
                            }
                        } else if (collectionVersion == 0) {
                            RegItem regItemCollection = getRegItemFromItem(collection);
                            migrateItemHistory.migrateRegRelation(regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(itemUuid + itemVersion)), regItemCollection, commit, key);
                        } else {
                            migrateItemHistory.migrateRegRelation(regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(itemUuid + itemVersion)), regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(collectionUuid + collectionVersion)), commit, key);
                        }
                    } catch (Exception exe) {
                        logger.error(exe.getMessage() + " - item: " + itemUuid + " - collection: " + collection.getUuid());
                        throw new Exception(exe.getMessage() + " - item: " + itemUuid + " - collection: " + collection.getUuid());
                    }
                }

            } catch (Exception exe) {
                System.out.println(exe.getMessage());
                logger.error(exe.getMessage());
                throw new Exception(exe.getMessage());
            }
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.getTransaction().commit();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }

            /**
             * migrate parent
             */
            try {
                Query queryParent = entityManagerRe3gistry2Migration.createNamedQuery("Itemparent.findAll", Itemparent.class);
                List<Itemparent> itemListParent;
                try {
                    itemListParent = queryParent.getResultList();
                } catch (Exception ex) {
                    logger.error("Error in  getting the result list for " + queryParent + " " + ex.getMessage());
                    throw new Exception("Error in  getting the localization for " + queryParent + " " + ex.getMessage());
                }

                String key = BaseConstants.KEY_PREDICATE_PARENT;
                for (Itemparent itemparent : itemListParent) {
                    Item item = itemparent.getItem();
                    Item parent = itemparent.getParent();

                    String itemUuid = item.getUuid();
                    String parentUuid = parent.getUuid();

                    int itemVersion = item.getVersionnumber();
                    int parentVersion = parent.getVersionnumber();

                    try {
                        if (itemVersion == 0) {
                            RegItem regItem = getRegItemFromItem(item);
                            if (parentVersion == 0) {
                                RegItem regItemPrent = getRegItemFromItem(parent);
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemPrent, commit, key);
                            } else {
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(parentUuid + parentVersion)), commit, key);
                            }
                        } else if (parentVersion == 0) {
                            RegItem regItemPrent = getRegItemFromItem(parent);
                            migrateItemHistory.migrateRegRelation(regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(itemUuid + itemVersion)), regItemPrent, commit, key);
                        } else {
                            migrateItemHistory.migrateRegRelation(regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(itemUuid + itemVersion)), regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(parentUuid + parentVersion)), commit, key);
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage() + " - item: " + itemUuid + " - parent: " + parentUuid);
                        throw new Exception(ex.getMessage() + " - item: " + itemUuid + " - parent: " + parentUuid);
                    }

                    try {
                        if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                            entityManagerRe3gistry2.getTransaction().begin();
                        }
                        entityManagerRe3gistry2.getTransaction().commit();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                        throw new Exception(ex.getMessage());
                    }
                }
            } catch (Exception exe) {
                logger.error(exe.getMessage());
                throw new Exception(exe.getMessage());
            }

            /**
             * migrate successor/predecessor
             */
            try {
                Query querySuccessor = entityManagerRe3gistry2Migration.createNamedQuery("Itemsuccessor.findAll", Itemsuccessor.class);
                List<Itemsuccessor> itemListSuccessor;
                try {
                    itemListSuccessor = querySuccessor.getResultList();
                } catch (Exception ex) {
                    logger.error("Error in  getting the result list for " + querySuccessor + " " + ex.getMessage());
                    throw new Exception("Error in  getting the localization for " + querySuccessor + " " + ex.getMessage());
                }

                String keySuccessor = BaseConstants.KEY_PREDICATE_SUCCESSOR;
                String keyPredecessor = BaseConstants.KEY_PREDICATE_PREDECESSOR;
                for (Itemsuccessor itemsuccessor : itemListSuccessor) {
                    Item item = itemsuccessor.getItem();
                    Item successor = itemsuccessor.getSuccessor();

                    String itemUuid = item.getUuid();
                    String successorUuid = successor.getUuid();

                    int itemVersion = item.getVersionnumber();
                    int successorVersion = successor.getVersionnumber();

                    try {
                        if (itemVersion == 0) {
                            RegItem regItem = getRegItemFromItem(item);

                            if (successorVersion == 0) {
                                RegItem regItemSuccessor = getRegItemFromItem(successor);
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemSuccessor, commit, keySuccessor);
                                migrateItemLatestVersion.migrateRegRelation(regItemSuccessor, regItem, commit, keyPredecessor);
                            } else {
                                final RegItemhistory regItemhistorySuccessor = regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(successorUuid + successorVersion));
                                migrateItemLatestVersion.migrateRegRelation(regItem, regItemhistorySuccessor, commit, keySuccessor);
                                migrateItemHistory.migrateRegRelation(regItemhistorySuccessor, regItem, commit, keyPredecessor);
                            }

                        } else {
                            final RegItemhistory regItemHistory = regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(itemUuid + itemVersion));

                            if (successorVersion == 0) {
//                                    final RegItem regItemSuccessor = regItemManager.get(correspondentItemToRegItemMap.get(successorUuid));
                                final RegItem regItemSuccessor = getRegItemFromItem(successor);
                                migrateItemHistory.migrateRegRelation(regItemHistory, regItemSuccessor, commit, keySuccessor);
                                migrateItemLatestVersion.migrateRegRelation(regItemSuccessor, regItemHistory, commit, keyPredecessor);
                            } else {
                                final RegItemhistory regItemHistorySuccessor = regItemhistoryManager.get(correspondentItemToRegItemHistoryMap.get(successorUuid + successorVersion));
                                migrateItemHistory.migrateRegRelation(regItemHistory, regItemHistorySuccessor, commit, keySuccessor);
                                migrateItemHistory.migrateRegRelation(regItemHistorySuccessor, regItemHistory, commit, keyPredecessor);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage() + " - item: " + itemUuid + " - successor: " + successorUuid);
                        throw new Exception(ex.getMessage() + " - item: " + itemUuid + " - successor: " + successorUuid);
                    }

                    try {
                        if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                            entityManagerRe3gistry2.getTransaction().begin();
                        }
                        entityManagerRe3gistry2.getTransaction().commit();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                        throw new Exception(ex.getMessage());
                    }
                }
            } catch (Exception exe) {
                logger.error(exe.getMessage());
                throw new Exception(exe.getMessage());
            }
        } catch (Exception exe) {
            logger.error(exe.getMessage());
            throw new Exception(exe.getMessage());
        }

        migrationManager.setProcedureorder(procedureorder);
    }

    private RegItem getRegItemFromItem(Item item) throws Exception {
        RegItem regItem = null;
        try {
            Item collection = getCollectionFromItem(item);

            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

            RegItemclass regItemclass = regItemclassManager.getByLocalid(item.getItemclass().getUriname());

            String uuid = null;
            String localid = item.getUriname();
            try {
                if (collection == null) {
                    uuid = RegItemUuidHelper.getUuid(localid, null, regItemclass);
                } else {
                    uuid = RegItemUuidHelper.getUuid(localid, regItemManager.getByLocalidAndRegItemClass(collection.getUriname(), regItemclassManager.getByLocalid(collection.getItemclass().getUriname())), regItemclass);
                }
            } catch (Exception ex) {
                uuid = RegItemUuidHelper.getUuid(localid, null, regItemclass);
            }

            regItem = regItemManager.get(uuid);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return regItem;
    }

    private RegItemhistory getRegItemHistoryFromItem(Item item) throws Exception {
        int itemVersion = item.getVersionnumber();
        RegItemhistory regItemhistory = null;
        try {
            Item collection = getCollectionFromItem(item);
            RegItem regCollection = getRegItemFromItem(collection);

            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManagerRe3gistry2);
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

            RegItemclass regItemclass = regItemclassManager.getByLocalid(item.getItemclass().getUriname());

            String uuid = null;
            String localid = item.getUriname();
            try {
                if (regCollection == null) {
                    String relatedItemUuid = RegItemUuidHelper.getUuid(localid, null, regItemclass);
                    uuid = RegItemhistoryUuidHelper.getUuid(localid, null, regItemclass, regItemManager.get(relatedItemUuid), itemVersion);
                } else {
                    String relatedItemUuid = RegItemUuidHelper.getUuid(localid, regCollection, regItemclass);
                    uuid = RegItemhistoryUuidHelper.getUuid(localid, regCollection, regItemclass, regItemManager.get(relatedItemUuid), itemVersion);
                }
            } catch (Exception ex) {
                uuid = RegItemUuidHelper.getUuid(localid, null, regItemclass);
            }

            regItemhistory = regItemhistoryManager.get(uuid);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return regItemhistory;
    }

    private Item getCollectionFromItem(Item item) {
        Item collection = null;
        try {
            Query itemQuery = entityManagerRe3gistry2Migration.createNamedQuery("Itemcollection.findByItem", Itemcollection.class);
            itemQuery.setParameter("item", item);

            List<Itemcollection> collectionList;
            try {
                collectionList = itemQuery.getResultList();
            } catch (Exception ex) {
                logger.error("Error in  getting the result list for " + itemQuery + " " + ex.getMessage());
                throw new Exception("Error in  getting the localization for " + itemQuery + " " + ex.getMessage());
            }

            for (Itemcollection itemcollection : collectionList) {
                if (itemcollection.getCollection().getVersionnumber() == 0) {
                    collection = itemcollection.getCollection();
                    break;
                }
            }
        } catch (Exception ex) {
            collection = null;
        }
        return collection;
    }

    private RegItemclass addRegItemClassByItemclass(Itemclass itemclass, int procedureorder, RegItemclasstype regItemclasstypeItem, boolean commit) throws Exception {
        RegItemclass regItemclass = new RegItemclass();

        regItemclass.setUuid(UuidHelper.createUuid(itemclass.getUriname(), RegItemclass.class));
        regItemclass.setLocalid(itemclass.getUriname());

        if (itemclass.getParent() != null) {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass regItemclassParent = regItemclassManager.getByLocalid(itemclass.getParent().getUriname());
            regItemclass.setRegItemclassParent(regItemclassParent);
        } else {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass parent = regItemclassManager.getByLocalid(itemclass.getRegister().getUriname());
            regItemclass.setRegItemclassParent(parent);
        }

        regItemclass.setRegItemclasstype(regItemclasstypeItem);

        RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
        regItemclass.setRegStatus(regStatusManager.get("1"));

        regItemclass.setSystemitem(Boolean.FALSE);
        regItemclass.setActive(Boolean.TRUE);

        regItemclass.setDataprocedureorder(procedureorder);
        regItemclass.setInsertdate(new Date());
        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regItemclass);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return regItemclass;
    }

    private int migrateCustomattributesByItemClass(Itemclass itemclass, RegItemclass regItemclass, HashMap<String, RegField> regFieldsMap, Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER, boolean commit) throws Exception {
        Query queryItemclasscustomattribute = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS);
        queryItemclasscustomattribute.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, itemclass);

        try {
            List<Itemclasscustomattribute> itemclasscustomattributeList;
            try {
                itemclasscustomattributeList = queryItemclasscustomattribute.getResultList();
            } catch (Exception ex) {
                logger.error("Error in  getting the result list for " + queryItemclasscustomattribute + " " + ex.getMessage());
                throw new Exception("Error in  getting the localization for " + queryItemclasscustomattribute + " " + ex.getMessage());
            }

            for (Itemclasscustomattribute itemclasscustomattribute : itemclasscustomattributeList) {
                Query queryCustomattribute = entityManagerRe3gistry2Migration.createNamedQuery("Customattribute.findByUuid", Customattribute.class);
                queryCustomattribute.setParameter("uuid", itemclasscustomattribute.getCustomattribute().getUuid());

                List<Customattribute> customattributeList;
                try {
                    customattributeList = queryCustomattribute.getResultList();
                } catch (Exception ex) {
                    logger.error("Error in  getting the result list for " + queryCustomattribute + " " + ex.getMessage());
                    throw new Exception("Error in  getting the localization for " + queryCustomattribute + " " + ex.getMessage());
                }

                for (Customattribute customattribute : customattributeList) {

                    RegField regField;
                    if (customattribute.getName().equals(BaseConstants.KEY_FIELD_EXTENSIBILITY) || customattribute.getName().equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {
                        regField = addRegFieldByCustomAttributeReferenceToAnotherRegister(itemclass, customattribute, commit);
                    } else {
                        regField = addRegFieldByCustomAttribute(itemclass, customattribute, commit);
                    }

                    regFieldsMap.put(regField.getLocalid(), regField);
                    addRegFieldMappingByCustomAttribute(regField, regItemclass, itemclasscustomattribute, customattribute, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                }
            }
        } catch (Exception exx) {
            logger.error(exx.getMessage());
            throw new Exception(exx.getMessage());
        }
        return AUTO_INCREMENT_REG_FIELD_LIST_ORDER;
    }

    private RegField addRegFieldByCustomAttribute(Itemclass itemclass, Customattribute customattribute, boolean commit) throws Exception {
        RegFieldManager regFieldManager = new RegFieldManager(entityManagerRe3gistry2);

        RegField regField = new RegField();
        try {
            regField = regFieldManager.getByLocalid(customattribute.getName());
        } catch (Exception exx) {
            regField.setUuid(UuidHelper.createUuid(customattribute.getName(), RegField.class));
            regField.setLocalid(customattribute.getName());
            regField.setIstitle(Boolean.FALSE);

            /**
             * set status, but maybe will be deleted
             */
            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regField.setRegStatus(regStatusManager.get("1"));

            RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManagerRe3gistry2);

            /**
             * set reg field type
             */
            try {

                Query itemclasscustomattributeForeygnKeyQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE);
                itemclasscustomattributeForeygnKeyQuery.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, itemclass);
                itemclasscustomattributeForeygnKeyQuery.setParameter("customattribute", customattribute);

                Itemclasscustomattribute itemclasscustomattributByItemclassAndCustomAttribute = null;
                try {
                    itemclasscustomattributByItemclassAndCustomAttribute = (Itemclasscustomattribute) itemclasscustomattributeForeygnKeyQuery.getSingleResult();
                } catch (Exception ex) {
                    logger.error("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for customattribute" + customattribute.getUuid() + ex.getMessage());
                    throw new Exception("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for customattribute" + customattribute.getUuid() + ex.getMessage());
                }

                if (itemclasscustomattributByItemclassAndCustomAttribute != null) {
                    RegFieldtype regFieldtype;
                    if (itemclasscustomattributByItemclassAndCustomAttribute.getIsforeignkey()) {
                        regFieldtype = regFieldtypeManager.get(BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID);

                        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
                        try {
                            regField.setRegItemclassReference(regItemclassManager.getByLocalid(customattribute.getName()));
                        } catch (Exception ex) {
                            logger.error("Error in getting the foreign key item class with the name" + customattribute.getName() + customattribute.getUuid() + ex.getMessage());
                            throw new Exception("Error in getting foreign key the item class with the name" + customattribute.getName() + customattribute.getUuid() + ex.getMessage());
                        }
                    } else {
                        regFieldtype = regFieldtypeManager.get(BaseConstants.KEY_FIELDTYPE_STRING_UUID);
                    }
                    regField.setRegFieldtype(regFieldtype);
                }

                /**
                 * difficult to understand how to put the hidden
                 */
                regField.setInsertdate(new Date());
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regField);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }

                RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
                RegLocalization regLocalizationField = new RegLocalization();
                regLocalizationField.setUuid(RegLocalizationUuidHelper.getUuid(0, regLanguagecodeManager.getMasterLanguage(), null, regField));
                regLocalizationField.setRegItem(null);
                regLocalizationField.setRegField(regField);
                regLocalizationField.setFieldValueIndex(0);
                regLocalizationField.setValue(customattribute.getName());
                regLocalizationField.setHref(null);
                regLocalizationField.setRegAction(null);
                regLocalizationField.setRegLanguagecode(regLanguagecodeManager.getMasterLanguage());
                regLocalizationField.setInsertdate(new Date());
                regLocalizationField.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationField);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, "", regField.getUuid(), regLanguagecodeManager.getMasterLanguage()) + " " + ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        }

        return regField;
    }

    private RegField addRegFieldByCustomAttributeReferenceToAnotherRegister(Itemclass itemclass, Customattribute customattribute, boolean commit) throws Exception {
        RegFieldManager regFieldManager = new RegFieldManager(entityManagerRe3gistry2);

        String customattributeName = customattribute.getName();
        if (customattributeName.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY)) {
            customattributeName = BaseConstants.KEY_ITEMCLASS_EXTENSIBILITY_ITEM;
        } else if (customattributeName.equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {
            customattributeName = BaseConstants.KEY_ITEMCLASS_GOVERNANCELEVEL_ITEM;
        }

        RegField regField = new RegField();
        try {
            regField = regFieldManager.getByLocalid(customattributeName);
        } catch (Exception exx) {
            regField.setUuid(UuidHelper.createUuid(customattributeName, RegField.class));
            regField.setLocalid(customattributeName);
            regField.setIstitle(Boolean.FALSE);

            /**
             * set status, but maybe will be deleted
             */
            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regField.setRegStatus(regStatusManager.get("1"));

            RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManagerRe3gistry2);

            /**
             * set reg field type
             */
            try {

                Query itemclasscustomattributeForeygnKeyQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE);
                itemclasscustomattributeForeygnKeyQuery.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, itemclass);
                itemclasscustomattributeForeygnKeyQuery.setParameter("customattribute", customattribute);

                Itemclasscustomattribute itemclasscustomattributByItemclassAndCustomAttribute = null;
                try {
                    itemclasscustomattributByItemclassAndCustomAttribute = (Itemclasscustomattribute) itemclasscustomattributeForeygnKeyQuery.getSingleResult();
                } catch (Exception ex) {
                    logger.error("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for customattribute" + customattribute.getUuid() + ex.getMessage());
                    throw new Exception("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for customattribute" + customattribute.getUuid() + ex.getMessage());
                }

                if (itemclasscustomattributByItemclassAndCustomAttribute != null) {
                    RegFieldtype regFieldtype = regFieldtypeManager.get(BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID);

                    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
                    regField.setRegItemclassReference(regItemclassManager.getByLocalid(customattributeName));

                    regField.setRegFieldtype(regFieldtype);
                }

                regField.setInsertdate(new Date());
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regField);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }

                RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
                RegLocalization regLocalizationField = new RegLocalization();
                regLocalizationField.setUuid(RegLocalizationUuidHelper.getUuid(0, regLanguagecodeManager.getMasterLanguage(), null, regField));
                regLocalizationField.setRegItem(null);
                regLocalizationField.setRegField(regField);
                regLocalizationField.setFieldValueIndex(0);
                regLocalizationField.setValue(customattributeName);
                regLocalizationField.setHref(null);
                regLocalizationField.setRegAction(null);
                regLocalizationField.setRegLanguagecode(regLanguagecodeManager.getMasterLanguage());
                regLocalizationField.setInsertdate(new Date());
                regLocalizationField.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationField);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, "", regField.getUuid(), regLanguagecodeManager.getMasterLanguage()) + " " + ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }

        return regField;
    }

    private void addRegFieldMappingByCustomAttribute(RegField regField, RegItemclass regItemclass, Itemclasscustomattribute itemclasscustomattribute, Customattribute customattribute, Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER, boolean commit) throws Exception {
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManagerRe3gistry2);

        try {
            regFieldmappingManager.getByFieldAndItemClass(regField, regItemclass);
        } catch (Exception e) {
            RegFieldmapping regFieldmapping = new RegFieldmapping();
            regFieldmapping.setUuid(RegFieldmappingUuidHelper.getUuid(regField, regItemclass));
            regFieldmapping.setRegField(regField);
            regFieldmapping.setRegItemclass(regItemclass);
            regFieldmapping.setHidden(Boolean.FALSE);
            regFieldmapping.setHashref(Boolean.FALSE);
            regFieldmapping.setRequired(itemclasscustomattribute.getRequired());
            regFieldmapping.setMultivalue(customattribute.getMultivalue());
            /**
             * difficult to understand how to put the list order
             */
            regFieldmapping.setListorder(AUTO_INCREMENT_REG_FIELD_LIST_ORDER);
            /**
             * difficult to understand how to put the table visibility
             */
            regFieldmapping.setTablevisible(Boolean.TRUE);
            regFieldmapping.setInsertdate(new Date());
            /**
             * set status, but maybe will be deleted
             */
            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regFieldmapping.setRegStatus(regStatusManager.get("1"));
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regFieldmapping);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        }
    }

    public List<Item> getAllItemVersionOrderDescByVersionnumber(Item item) throws Exception {
        if (item == null) {
            throw new Exception("The item passed by parameter is null.");
        }

        Query q = null;
        try {
            Query collectionQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCOLLECTION_BY_ITEM);
            collectionQuery.setParameter("item", item);

            /**
             * has collection?
             */
            Itemcollection itemcollection = null;
            try {
                itemcollection = (Itemcollection) collectionQuery.getSingleResult();
            } catch (Exception ex) {
                q = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.GET_ITEM_BY_ITEMACLASS_URINAME_DESC);
                q.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, item.getItemclass());
                q.setParameter("uriname", item.getUriname());
            }

            if (itemcollection != null) {
                String collectionUriname = itemcollection.getCollection().getUriname();
                q = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.GET_ITEM_ALL_VERSION_COLLECTION);
                q.setParameter("itemuriname", item.getUriname());
                q.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, item.getItemclass());
                q.setParameter("collectionuriname", collectionUriname);
            }
        } catch (Exception e) {
            q = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.GET_ITEM_BY_ITEMACLASS_URINAME_DESC);
            q.setParameter(ConstantsMigration.KEY_PARAMETER_ITEMCLASS, item.getItemclass());
            q.setParameter("uriname", item.getUriname());

        }

        List<Item> items = null;
        try {
            if (q != null) {
                items = (List<Item>) q.getResultList();
            }
        } catch (Exception e) {
            return items;
        }

        return items;
    }

}
