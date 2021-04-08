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
package eu.europa.ec.re3gistry2.migration.manager;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Customattributevalue;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Item;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclasscustomattribute;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.migration.utility.StatusConvert;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalizationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemhistoryUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationhistoryUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationhistoryUuidHelper;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateItemHistory {

    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;
    private final MigrationManager migrationManager;
    public static final AtomicInteger AUTO_INCREMENT_REG_FIELD_LIST_ORDER = new AtomicInteger(1);
    private final RegLanguagecodeManager regLanguagecodeManager;
    private final RegUser migrationUser;

    public MigrateItemHistory(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.migrationUser = migrationManager.getMigrationUser();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
        this.regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
    }

    protected RegItemhistory migrateItemHistory(Item item, Item collectionHistory, RegItemclass regItemclass, HashMap<String, RegField> regFieldsMap, RegItem regItemLatestVersion, boolean commit) throws Exception {
        String uuid;
        try {
            if (collectionHistory == null) {
                uuid = RegItemhistoryUuidHelper.getUuid(item.getUriname(), null, regItemclass, regItemLatestVersion, item.getVersionnumber());
            } else {
                String localid = item.getUriname();
                int version = item.getVersionnumber();

                RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

                uuid = RegItemhistoryUuidHelper.getUuid(localid, regItemManager.getByLocalidAndRegItemClass(collectionHistory.getUriname(), regItemclassManager.getByLocalid(collectionHistory.getItemclass().getUriname())), regItemclass, regItemLatestVersion, version);
            }

        } catch (Exception ex) {
            uuid = RegItemhistoryUuidHelper.getUuid(item.getUriname(), null, regItemclass, regItemLatestVersion, item.getVersionnumber());
        }

        RegItemhistory regItemhistory = null;

        try {
            RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManagerRe3gistry2);
            regItemhistory = regItemhistoryManager.get(uuid);
        } catch (Exception excep) {

            try {
                if (collectionHistory == null) {
                    regItemhistory = createRegItemHistoryWithoutCollection(item, regItemclass, regItemLatestVersion, commit);
                } else {
                    regItemhistory = createRegItemHistoryWithCollection(item, regItemclass, collectionHistory, regItemLatestVersion, commit);
                }

            } catch (Exception ex) {
                regItemhistory = createRegItemHistoryWithoutCollection(item, regItemclass, regItemLatestVersion, commit);
            }

            if (regItemhistory != null) {
                addLocalization(item, regItemhistory, regFieldsMap, commit);
                addCustomAttribute(item, regItemhistory, regFieldsMap, commit);
                addRegistryRelationHistory(item, regItemhistory, commit);
                addRegisterRelationHistory(item, regItemhistory, commit);

            }
        }
        return regItemhistory;
    }

    private void addCustomAttribute(Item item, RegItemhistory regItemhistory, HashMap<String, RegField> regFieldsMap, boolean commit) throws Exception {
        /**
         * get custom attribute value by item
         */
        Query customattributevalueQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.CUSTOMATTRIBUTEVALUE_BY_ITEM);
        customattributevalueQuery.setParameter("item", item);
        List<Customattributevalue> customattributesValueList;
        try {
            customattributesValueList = customattributevalueQuery.getResultList();
        } catch (Exception ex) {
            logger.error("Error in  getting the result list for " + customattributevalueQuery + " " + ex.getMessage());
            throw new Exception("Error in  getting the localization for " + customattributevalueQuery + " " + ex.getMessage());
        }

        Query queryMasterLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findByMasterlanguage", Languagecode.class);
        queryMasterLanguagecode.setParameter("masterlanguage", Boolean.TRUE);
        Languagecode masterLanguagecode = null;
        try {
            masterLanguagecode = (Languagecode) queryMasterLanguagecode.getSingleResult();
        } catch (Exception ex) {
            logger.error("Error in  Languagecode.findByMasterlanguage" + ex.getMessage());
            throw new Exception("Error in  getting the localization for " + queryMasterLanguagecode + " " + ex.getMessage());
        }

        for (Customattributevalue customattributevalue : customattributesValueList) {
            int fieldIndex = 0;

            /**
             * get itemclasscustomattribute from customattribute and item class
             */
            Query itemclasscustomattributeForeygnKeyQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE);
            itemclasscustomattributeForeygnKeyQuery.setParameter("itemclass", item.getItemclass());
            itemclasscustomattributeForeygnKeyQuery.setParameter("customattribute", customattributevalue.getCustomattribute());
            Itemclasscustomattribute itemclasscustomattributByItemclassAndCustomAttribute = null;
            try {
                itemclasscustomattributByItemclassAndCustomAttribute = (Itemclasscustomattribute) itemclasscustomattributeForeygnKeyQuery.getSingleResult();
            } catch (Exception ex) {
                logger.error("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for itemclass" + item.getItemclass().getUuid() + " and customattribute " + customattributevalue.getCustomattribute().getName() + ex.getMessage());
                throw new Exception("Error in  " + ConstantsMigration.KEY_PARAMETER_ITEMCLASS + " for itemclass" + item.getItemclass().getUuid() + " and customattribute " + customattributevalue.getCustomattribute().getName() + ex.getMessage());
            }

            String customAttributeItemClassUriname = customattributevalue.getCustomattribute().getName();
            if (itemclasscustomattributByItemclassAndCustomAttribute != null && itemclasscustomattributByItemclassAndCustomAttribute.getIsforeignkey()
                    || customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY)
                    || customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {
                /**
                 * get localization of custom attribute value with
                 * masterlanguage
                 */
                Query queryLocalizationByCustomattributevalue = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE);
                queryLocalizationByCustomattributevalue.setParameter("customattributevalue", customattributevalue);
                queryLocalizationByCustomattributevalue.setParameter("language", masterLanguagecode);

                try {
                    Localization customattributevalueLocalization = null;
                    try {
                        customattributevalueLocalization = (Localization) queryLocalizationByCustomattributevalue.getSingleResult();
                    } catch (Exception ex) {
                        logger.error("Error in  " + ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE + " for customattributevalue" + customattributevalue.getUuid() + " and master language " + masterLanguagecode + ex.getMessage());
                        throw new Exception("Error in  " + ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE + " for customattributevalue" + customattributevalue.getUuid() + " and master language " + masterLanguagecode + ex.getMessage());
                    }

                    if (customattributevalueLocalization != null) {

                        if (customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY) || customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {

                            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

                            String customattribute = "";
                            if (customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY)) {
                                customattribute = BaseConstants.KEY_ITEMCLASS_EXTENSIBILITY_ITEM;
                            } else {
                                customattribute = BaseConstants.KEY_ITEMCLASS_GOVERNANCELEVEL_ITEM;
                            }
                            RegItem foreygnKeyRegItem = regItemManager.getByLocalidAndRegItemClass(customattributevalueLocalization.getLabel(), regItemclassManager.getByLocalid(customattribute));

                            createRegRelationFromRegItemAndRegFieldAndForeygnRegItem(regItemhistory, foreygnKeyRegItem, commit, regFieldsMap, customattributevalue, fieldIndex);

                        } else {
                            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

                            if (customattributevalue.getCustomattribute().getMultivalue()) {
                                if (customattributevalueLocalization != null) {

                                    String[] customattributeList = customattributevalueLocalization.getLabel().split(",");
                                    for (String customattribute : customattributeList) {
                                        createRegrelationFromRegItemHistoryAndRegfieldHistory(customattribute, regItemclassManager, customAttributeItemClassUriname, regItemhistory, regFieldsMap, customattributevalue, fieldIndex, commit);
                                        fieldIndex++;
                                    }

                                }
                            } else {
                                createRegrelationFromRegItemHistoryAndRegfieldHistory(customattributevalueLocalization.getLabel(), regItemclassManager, customAttributeItemClassUriname, regItemhistory, regFieldsMap, customattributevalue, fieldIndex, commit);
                            }
                        }
                    }

                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());

                }
            } else {
                /**
                 * get localization by custom attribute value
                 */
                Query queryLocalizationByCustomattributevalue = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE);
                queryLocalizationByCustomattributevalue.setParameter("customattributevalue", customattributevalue);
                List<Localization> customattributevalueLocalizationList;
                try {
                    customattributevalueLocalizationList = queryLocalizationByCustomattributevalue.getResultList();
                } catch (Exception ex) {
                    logger.error("Error in  getting the result list for " + queryLocalizationByCustomattributevalue + " " + ex.getMessage());
                    throw new Exception("Error in  getting the localization for " + queryLocalizationByCustomattributevalue + " " + ex.getMessage());
                }

                for (Localization customattributevalueLocalization : customattributevalueLocalizationList) {

                    RegLanguagecode reglanguagecodeByLanguage = regLanguagecodeManager.getByIso6391code(customattributevalueLocalization.getLanguage().getIsocode());

                    if (customattributevalue.getCustomattribute().getMultivalue()) {
                        String[] customattributeList = customattributevalueLocalization.getLabel().split(",");
                        for (String customattribute : customattributeList) {
                            createReglocalizationHistoryForRegitemHistoryAndRegfieldFromCustomattribute(regFieldsMap, customattributevalueLocalization, regItemhistory, reglanguagecodeByLanguage, customattribute, fieldIndex, commit);
                            fieldIndex++;
                        }
                    } else {
                        createReglocalizationHistoryForRegitemHistoryAndRegfieldFromCustomattribute(regFieldsMap, customattributevalueLocalization, regItemhistory, reglanguagecodeByLanguage, customattributevalueLocalization.getLabel(), fieldIndex, commit);
                    }

                }
            }
        }
    }

    private void addLocalization(Item item, RegItemhistory regItemhistory, HashMap<String, RegField> regFieldsMap, boolean commit) throws Exception {
        /**
         * get localization by item
         */
        Query queryLocalization = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_ITEM);
        queryLocalization.setParameter("item", item);

        List<Localization> itemListLocalization;
        try {
            itemListLocalization = queryLocalization.getResultList();
        } catch (Exception ex) {
            logger.error("Error in  getting the result list for " + queryLocalization + " " + ex.getMessage());
            throw new Exception("Error in  getting the localization for " + queryLocalization + " " + ex.getMessage());
        }
        for (Localization localization : itemListLocalization) {
            int fieldIndex = 0;

            RegLanguagecode reglanguagecodeByLanguage = regLanguagecodeManager.getByIso6391code(localization.getLanguage().getIsocode());

            if (localization.getLabel() != null && !localization.getLabel().isEmpty()) {

                RegLocalizationhistory regLocalizationLabel = new RegLocalizationhistory();
                regLocalizationLabel.setUuid(RegLocalizationhistoryUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItemhistory, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL)));
                regLocalizationLabel.setRegItemhistory(regItemhistory);
                regLocalizationLabel.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL));
                regLocalizationLabel.setFieldValueIndex(fieldIndex);
                regLocalizationLabel.setValue(localization.getLabel());
                regLocalizationLabel.setHref(null);
                regLocalizationLabel.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationLabel.setInsertdate(new Date());
                regLocalizationLabel.setRegRelationhistoryReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationLabel);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEMHISTORY, regItemhistory.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            }

            if (localization.getDefinition() != null && !localization.getDefinition().isEmpty()) {
                RegLocalizationhistory regLocalizationDefinition = new RegLocalizationhistory();
                regLocalizationDefinition.setUuid(RegLocalizationhistoryUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItemhistory, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION)));
                regLocalizationDefinition.setRegItemhistory(regItemhistory);
                regLocalizationDefinition.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION));
                regLocalizationDefinition.setFieldValueIndex(fieldIndex);
                regLocalizationDefinition.setValue(localization.getDefinition());
                regLocalizationDefinition.setHref(null);
                regLocalizationDefinition.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationDefinition.setInsertdate(new Date());
                regLocalizationDefinition.setRegRelationhistoryReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationDefinition);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEMHISTORY, regItemhistory.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            }

            if (localization.getDescription() != null && !localization.getDescription().isEmpty()) {
                RegLocalizationhistory regLocalizationDescription = new RegLocalizationhistory();
                regLocalizationDescription.setUuid(RegLocalizationhistoryUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItemhistory, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION)));
                regLocalizationDescription.setRegItemhistory(regItemhistory);
                regLocalizationDescription.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION));
                regLocalizationDescription.setFieldValueIndex(fieldIndex);
                regLocalizationDescription.setValue(localization.getDescription());
                regLocalizationDescription.setHref(null);
                regLocalizationDescription.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationDescription.setInsertdate(new Date());
                regLocalizationDescription.setRegRelationhistoryReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationDescription);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEMHISTORY, regItemhistory.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            }
        }
    }

    private void createReglocalizationHistoryForRegitemHistoryAndRegfieldFromCustomattribute(HashMap<String, RegField> regFieldsMap, Localization customattributevalueLocalization, RegItemhistory regItemHistory, RegLanguagecode reglanguagecodeByLanguage, String customattribute, int fieldIndex, boolean commit) throws Exception {
        RegLocalizationhistory regLocalizationCustomattribute = new RegLocalizationhistory();
        RegField regFieldUsedForLocalization = regFieldsMap.get(customattributevalueLocalization.getCustomattributevalue().getCustomattribute().getName());

        regLocalizationCustomattribute.setUuid(RegLocalizationhistoryUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItemHistory, regFieldUsedForLocalization));
        regLocalizationCustomattribute.setRegItemhistory(regItemHistory);
        regLocalizationCustomattribute.setRegField(regFieldUsedForLocalization);
        regLocalizationCustomattribute.setFieldValueIndex(fieldIndex);
        regLocalizationCustomattribute.setValue(customattribute);
        regLocalizationCustomattribute.setHref(null);
        regLocalizationCustomattribute.setRegLanguagecode(reglanguagecodeByLanguage);
        regLocalizationCustomattribute.setInsertdate(new Date());
        regLocalizationCustomattribute.setRegRelationhistoryReference(null);

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regLocalizationCustomattribute);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEMHISTORY, regItemHistory.getUuid(), regFieldUsedForLocalization.getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private void createRegrelationFromRegItemHistoryAndRegfieldHistory(String customattribute, RegItemclassManager regItemclassManager, String customAttributeItemClassUriname, RegItemhistory regItemhistory, HashMap<String, RegField> regFieldsMap, Customattributevalue customattributevalue, int fieldIndex, boolean commit) throws Exception {
        RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
        RegItem foreygnKeyRegItem = regItemManager.getByLocalidAndRegItemClass(customattribute, regItemclassManager.getByLocalid(customAttributeItemClassUriname));

        createRegRelationFromRegItemAndRegFieldAndForeygnRegItem(regItemhistory, foreygnKeyRegItem, commit, regFieldsMap, customattributevalue, fieldIndex);
    }

    private void createRegRelationFromRegItemAndRegFieldAndForeygnRegItem(RegItemhistory regItemhistory, RegItem foreygnKeyRegItem, boolean commit, HashMap<String, RegField> regFieldsMap, Customattributevalue customattributevalue, int fieldIndex) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);
        /**
         * create reg relation between reg item and reg item of the foreign key
         */
        RegRelationhistory regRelationForeignkey = new RegRelationhistory();
        regRelationForeignkey.setUuid(RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, null, foreygnKeyRegItem));
        regRelationForeignkey.setRegItemhistorySubject(regItemhistory);
        regRelationForeignkey.setRegRelationpredicate(predicate);
        regRelationForeignkey.setRegItemObject(foreygnKeyRegItem);
        regRelationForeignkey.setInsertdate(new Date());

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regRelationForeignkey);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }

        /**
         * create RegLocalization for master language
         */
        String customattribute = customattributevalue.getCustomattribute().getName();
        if (customattribute.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY)) {
            customattribute = BaseConstants.KEY_ITEMCLASS_EXTENSIBILITY_ITEM;
        } else if (customattribute.equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {
            customattribute = BaseConstants.KEY_ITEMCLASS_GOVERNANCELEVEL_ITEM;
        }
        RegField regFieldForForeignKey = regFieldsMap.get(customattribute);

        RegLocalizationhistory regLocalizationCustomAttributeForeignKey = new RegLocalizationhistory();
        regLocalizationCustomAttributeForeignKey.setUuid(RegLocalizationhistoryUuidHelper.getUuid(fieldIndex, migrationManager.getRegmasterLanguage(), regItemhistory, regFieldForForeignKey));
        regLocalizationCustomAttributeForeignKey.setRegItemhistory(regItemhistory);
        regLocalizationCustomAttributeForeignKey.setRegField(regFieldForForeignKey);
        regLocalizationCustomAttributeForeignKey.setFieldValueIndex(fieldIndex);
        regLocalizationCustomAttributeForeignKey.setRegLanguagecode(migrationManager.getRegmasterLanguage());
        regLocalizationCustomAttributeForeignKey.setValue(null);
        regLocalizationCustomAttributeForeignKey.setHref(null);
        regLocalizationCustomAttributeForeignKey.setRegRelationhistoryReference(regRelationForeignkey);
        regLocalizationCustomAttributeForeignKey.setInsertdate(new Date());
        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regLocalizationCustomAttributeForeignKey);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

//    protected void migrateParentHistory(Item item, Item parent, boolean commit, HashMap<Item, Item> mapCollection) throws Exception {
//        RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManagerRe3gistry2);
//        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
//        RegItemhistory regItemhistory = regItemhistoryManager.getByLocalidVersionnumberAndRegItemClass(item.getUriname(), item.getVersionnumber(), regItemclassManager.getByLocalid(item.getItemclass().getUriname()));
//
//        try {
//            Query parentCollectionQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCOLLECTION_BY_ITEM);
//            parentCollectionQuery.setParameter("item", parent);
//
//            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
//            RegItem regitemParent = regItemManager.getByLocalidAndRegItemClass(parent.getUriname(), regItemclassManager.getByLocalid(parent.getItemclass().getUriname()));
//
//            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
//            final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);
//
//            RegRelationhistory regRelationParent = new RegRelationhistory();
//            regRelationParent.setUuid(RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, null, regitemParent));
//            regRelationParent.setRegItemhistorySubject(regItemhistory);
//            regRelationParent.setRegRelationpredicate(predicate);
//            regRelationParent.setRegItemObject(regitemParent);
//            regRelationParent.setInsertdate(new Date());
//
//            try {
//                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
//                    entityManagerRe3gistry2.getTransaction().begin();
//                }
//                entityManagerRe3gistry2.persist(regRelationParent);
//                if (commit) {
//                    entityManagerRe3gistry2.getTransaction().commit();
//                }
//            } catch (Exception ex) {
//                logger.error(ex.getMessage());
//                throw new Exception(ex.getMessage());
//            }
//        } catch (Exception ex) {
////            logger.error(ex.getMessage());
////            throw new Exception(ex.getMessage());
//        }
//    }
    protected void migrateRegRelation(RegItemhistory regItemhistory, RegItem regitemCollection, boolean commit, String key) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(key);
        String uuid = RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, null, regitemCollection);
        try {
            RegRelationhistoryManager regRelationhistoryManager = new RegRelationhistoryManager(entityManagerRe3gistry2);
            regRelationhistoryManager.get(uuid);
        } catch (Exception exx) {
            try {

                RegRelationhistory regRelationCollection = new RegRelationhistory();
                regRelationCollection.setUuid(uuid);
                regRelationCollection.setRegItemhistorySubject(regItemhistory);
                regRelationCollection.setRegRelationpredicate(predicate);
                regRelationCollection.setRegItemObject(regitemCollection);
                regRelationCollection.setInsertdate(new Date());

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regRelationCollection);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        }
    }

    protected void migrateRegRelation(RegItemhistory regItemhistory, RegItemhistory regitemRelationHistory, boolean commit, String key) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(key);
        String uuid = RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, regitemRelationHistory, null);
        try {
            RegRelationhistoryManager regRelationhistoryManager = new RegRelationhistoryManager(entityManagerRe3gistry2);
            regRelationhistoryManager.get(uuid);
        } catch (Exception exx) {
            try {
                RegRelationhistory regRelationHistory = new RegRelationhistory();
                regRelationHistory.setUuid(uuid);
                regRelationHistory.setRegItemhistorySubject(regItemhistory);
                regRelationHistory.setRegRelationpredicate(predicate);
                regRelationHistory.setRegItemhistoryObject(regitemRelationHistory);
                regRelationHistory.setInsertdate(new Date());
                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regRelationHistory);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        }
    }

    protected void migrateSuccessorAndPredecessorHistory(Item item, Item successor, boolean commit, HashMap<Item, Item> mapCollection) throws Exception {
        RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManagerRe3gistry2);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
        RegItemhistory regItemhistory = regItemhistoryManager.getByLocalidVersionnumberAndRegItemClass(item.getUriname(), item.getVersionnumber(), regItemclassManager.getByLocalid(item.getItemclass().getUriname()));

        try {
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);

            String createSuccessorUuid = migrateSuccessorHistory(regItemManager, successor, regItemclassManager, regRelationpredicateManager, regItemhistory, commit);
            migratePredecessorHistory(regItemManager, createSuccessorUuid, regRelationpredicateManager, regItemhistory, commit);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
//            throw new Exception(ex.getMessage());
        }
    }

    private void migratePredecessorHistory(RegItemManager regItemManager, String createSuccessorUuid, RegRelationpredicateManager regRelationpredicateManager, RegItemhistory regItemhistory, boolean commit) throws Exception {
        RegItem regitemPredessor = regItemManager.get(createSuccessorUuid);
        final RegRelationpredicate predicatePredecessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR);

        RegRelationhistory regRelationPredessor = new RegRelationhistory();
        regRelationPredessor.setUuid(RegRelationhistoryUuidHelper.getUuid(null, regitemPredessor, predicatePredecessor, regItemhistory, null));
        regRelationPredessor.setRegItemSubject(regitemPredessor);
        regRelationPredessor.setRegRelationpredicate(predicatePredecessor);
        regRelationPredessor.setRegItemhistoryObject(regItemhistory);
        regRelationPredessor.setInsertdate(new Date());

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regRelationPredessor);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private String migrateSuccessorHistory(RegItemManager regItemManager, Item successor, RegItemclassManager regItemclassManager, RegRelationpredicateManager regRelationpredicateManager, RegItemhistory regItemhistory, boolean commit) throws Exception {
        RegItem regitemSuccessor = regItemManager.getByLocalidAndRegItemClass(successor.getUriname(), regItemclassManager.getByLocalid(successor.getItemclass().getUriname()));
        String createSuccessorUuid = regitemSuccessor.getUuid();
        final RegRelationpredicate predicateSuccessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR);
        RegRelationhistory regRelationSuccessor = new RegRelationhistory();
        regRelationSuccessor.setUuid(RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicateSuccessor, null, regitemSuccessor));
        regRelationSuccessor.setRegItemhistorySubject(regItemhistory);
        regRelationSuccessor.setRegRelationpredicate(predicateSuccessor);
        regRelationSuccessor.setRegItemObject(regitemSuccessor);
        regRelationSuccessor.setInsertdate(new Date());
        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regRelationSuccessor);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return createSuccessorUuid;
    }

    private void addRegistryRelationHistory(Item item, RegItemhistory regItemhistory, boolean commit) throws Exception {
        try {
            /**
             * add reg relation to registry
             */
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass registryRegItemclass = regItemclassManager.getByLocalid(item.getItemclass().getRegister().getRegistry().getUriname());
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItem registryItem = regItemManager.getByLocalidAndRegItemClass(item.getItemclass().getRegister().getRegistry().getUriname(), registryRegItemclass);

            RegRelationhistory regRelationRegisterRegistry = new RegRelationhistory();
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
            final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);

            regRelationRegisterRegistry.setUuid(RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, null, registryItem));
            regRelationRegisterRegistry.setRegItemhistorySubject(regItemhistory);
            regRelationRegisterRegistry.setRegRelationpredicate(predicate);
            regRelationRegisterRegistry.setRegItemObject(registryItem);
            regRelationRegisterRegistry.setInsertdate(new Date());

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regRelationRegisterRegistry);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private void addRegisterRelationHistory(Item item, RegItemhistory regItemhistory, boolean commit) throws Exception {
        try {
            /**
             * add reg relation to register
             */
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass registerRegItemclass = regItemclassManager.getByLocalid(item.getItemclass().getRegister().getUriname());
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItem registerItem = regItemManager.getByLocalidAndRegItemClass(item.getItemclass().getRegister().getUriname(), registerRegItemclass);

            RegRelationhistory regRelationRegisterRegistry = new RegRelationhistory();
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
            final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

            regRelationRegisterRegistry.setUuid(RegRelationhistoryUuidHelper.getUuid(regItemhistory, null, predicate, null, registerItem));
            regRelationRegisterRegistry.setRegItemhistorySubject(regItemhistory);
            regRelationRegisterRegistry.setRegRelationpredicate(predicate);
            regRelationRegisterRegistry.setRegItemObject(registerItem);
            regRelationRegisterRegistry.setInsertdate(new Date());

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regRelationRegisterRegistry);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    public RegItemhistory createRegItemHistoryWithoutCollection(Item item, RegItemclass regItemclass, RegItem regItemLatestVersion, boolean commit) throws Exception {
        String localid = item.getUriname();
        int version = item.getVersionnumber();

        /**
         * create RegItem for the register
         */
        RegItemhistory regItemhistory = new RegItemhistory();
        regItemhistory.setUuid(RegItemhistoryUuidHelper.getUuid(localid, null, regItemclass, regItemLatestVersion, version));
        regItemhistory.setLocalid(localid);
        regItemhistory.setRegItemclass(regItemclass);
        regItemhistory.setRegItemReference(regItemLatestVersion);

        regItemhistory.setRegUser(migrationUser);

        regItemhistory.setInsertdate(new Date());
        regItemhistory.setVersionnumber(version);

        if (localid.startsWith("http://") || localid.startsWith("https://")) {
            regItemhistory.setExternal(Boolean.TRUE);
        } else {
            regItemhistory.setExternal(Boolean.FALSE);
        }

        /**
         * set reg_status taken from the old db to the new db
         */
        StatusConvert statusConvert = new StatusConvert();
        RegStatus historyStatus = statusConvert.convertFromOldDBToNewDB(item.getStatus().getUriname(), entityManagerRe3gistry2);
        regItemhistory.setRegStatus(historyStatus);

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regItemhistory);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return regItemhistory;
    }

    public RegItemhistory createRegItemHistoryWithCollection(Item item, RegItemclass regItemclass, Item itemcollection, RegItem regItemLatestVersion, boolean commit) throws Exception {
        String localid = item.getUriname();
        int version = item.getVersionnumber();

        /**
         * create RegItem for the register
         */
        RegItemhistory regItemhistory = new RegItemhistory();
        RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

        regItemhistory.setUuid(RegItemhistoryUuidHelper.getUuid(localid, regItemManager.getByLocalidAndRegItemClass(itemcollection.getUriname(), regItemclassManager.getByLocalid(itemcollection.getItemclass().getUriname())), regItemclass, regItemLatestVersion, version));
        regItemhistory.setLocalid(localid);
        regItemhistory.setRegItemclass(regItemclass);
        regItemhistory.setRegItemReference(regItemLatestVersion);

        regItemhistory.setRegUser(migrationUser);

        regItemhistory.setInsertdate(new Date());
        regItemhistory.setVersionnumber(version);

        if (localid.startsWith("http://") || localid.startsWith("https://")) {
            regItemhistory.setExternal(Boolean.TRUE);
        } else {
            regItemhistory.setExternal(Boolean.FALSE);
        }

        /**
         * set reg_status taken from the old db to the new db
         */
        StatusConvert statusConvert = new StatusConvert();
        RegStatus historyStatus = statusConvert.convertFromOldDBToNewDB(item.getStatus().getUriname(), entityManagerRe3gistry2);
        regItemhistory.setRegStatus(historyStatus);

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regItemhistory);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        return regItemhistory;
    }
}
