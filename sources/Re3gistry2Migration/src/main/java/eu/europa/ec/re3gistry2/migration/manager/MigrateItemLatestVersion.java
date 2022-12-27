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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Customattributevalue;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Item;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclass;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclasscustomattribute;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationhistoryUuidHelper;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateItemLatestVersion {

    private final Logger logger;
    public static final AtomicInteger AUTO_INCREMENT_REG_FIELD_LIST_ORDER = new AtomicInteger(1);

    private final RegUser migrationUser;
    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final MigrationManager migrationManager;
    private final RegLanguagecodeManager regLanguagecodeManager;

    public MigrateItemLatestVersion(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.migrationUser = migrationManager.getMigrationUser();
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
        this.regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
    }

    protected RegItem migrateItemLatestVersion(Item item, Item collection, RegItemclass regItemclass, HashMap<String, RegField> regFieldsMap, Itemclass itemclass, boolean commit) throws Exception {
        String uuid;
        /**try {
            if (collection == null) {
                uuid = RegItemUuidHelper.getUuid(item.getUriname(), null, regItemclass);
            } else {
                String localid = item.getUriname();
                RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

                uuid = RegItemUuidHelper.getUuid(localid, regItemManager.getByLocalidAndRegItemClass(collection.getUriname(), regItemclassManager.getByLocalid(collection.getItemclass().getUriname())), regItemclass);
            }
        } catch (Exception ex) {
            uuid = RegItemUuidHelper.getUuid(item.getUriname(), null, regItemclass);
        }**/
        uuid = item.getUuid();

        RegItem regItem = null;
        try {
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            regItem = regItemManager.get(uuid);
        } catch (Exception excep) {

            try {
                if (collection == null) {
                    regItem = createRegItemWithoutCollection(item, regItemclass, commit);
                } else {
                    regItem = createRegItemWithCollection(item, regItemclass, collection, commit);
                }
            } catch (Exception exce) {
                regItem = createRegItemWithoutCollection(item, regItemclass, commit);
                logger.info("Pruebas catch latestVersion",exce);
            }

            if (regItem != null) {

                try {
                    addLocalization(item, regItem, regFieldsMap, commit);
                    addCustomAttribute(item, itemclass, regItem, regFieldsMap, commit);
                    addRegistryRelation(item, regItem, commit);
                    addRegisterRelation(item, regItem, commit);

                } catch (Exception exx) {
                    logger.error(exx.getMessage());
                    logger.info("Pruebas catch latestVersionAddlocal",exx);
                    throw new Exception(exx.getMessage());
                }
            }
        }
        if(regItem == null){
            throw new Exception("regItem NULL latestVersion");
        }
        return regItem;
    }

    private void addLocalization(Item item, RegItem regItem, HashMap<String, RegField> regFieldsMap, boolean commit) throws Exception {
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

                RegLocalization regLocalizationLabel = new RegLocalization();
                regLocalizationLabel.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItem, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL)));
                regLocalizationLabel.setRegItem(regItem);
                regLocalizationLabel.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL));
                regLocalizationLabel.setFieldValueIndex(fieldIndex);
                regLocalizationLabel.setValue(localization.getLabel());
                regLocalizationLabel.setHref(null);
                regLocalizationLabel.setRegAction(null);
                regLocalizationLabel.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationLabel.setInsertdate(new Date());
                regLocalizationLabel.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationLabel);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, regItem.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_LABEL).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                }
            }

            if (localization.getDefinition() != null && !localization.getDefinition().isEmpty()) {
                RegLocalization regLocalizationDefinition = new RegLocalization();
                regLocalizationDefinition.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItem, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION)));
                regLocalizationDefinition.setRegItem(regItem);
                regLocalizationDefinition.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION));
                regLocalizationDefinition.setFieldValueIndex(fieldIndex);
                regLocalizationDefinition.setValue(localization.getDefinition());
                regLocalizationDefinition.setHref(null);
                regLocalizationDefinition.setRegAction(null);
                regLocalizationDefinition.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationDefinition.setInsertdate(new Date());
                regLocalizationDefinition.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationDefinition);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, regItem.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DEFINITION).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                }
            }

            if (localization.getDescription() != null && !localization.getDescription().isEmpty()) {
                RegLocalization regLocalizationDescription = new RegLocalization();
                regLocalizationDescription.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItem, regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION)));
                regLocalizationDescription.setRegItem(regItem);
                regLocalizationDescription.setRegField(regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION));
                regLocalizationDescription.setFieldValueIndex(fieldIndex);
                regLocalizationDescription.setValue(localization.getDescription());
                regLocalizationDescription.setHref(null);
                regLocalizationDescription.setRegAction(null);
                regLocalizationDescription.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationDescription.setInsertdate(new Date());
                regLocalizationDescription.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationDescription);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, regItem.getUuid(), regFieldsMap.get(ConstantsMigration.KEY_PARAMETER_DESCRIPTION).getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
                }
            }
        }
    }

    private void addCustomAttribute(Item item, Itemclass itemclass, RegItem regItem, HashMap<String, RegField> regFieldsMap, boolean commit) throws Exception {
        /**
         * get custom attribute value by item
         */
        Query customattributevalueQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.CUSTOMATTRIBUTEVALUE_BY_ITEM);
        customattributevalueQuery.setParameter("item", item);
        List<Customattributevalue> customattributesValueList;
        logger.error("pruebas addCustomAttribute item = " + item.getUuid());
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
            logger.error("Error in Languagecode.findByMasterlanguage " + ex.getMessage());
        }
        for (Customattributevalue customattributevalue : customattributesValueList) {
            int fieldIndex = 0;

            /**
             * get itemclasscustomattribute from customattribute and item class
             */
            Query itemclasscustomattributeForeygnKeyQuery = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE);
            itemclasscustomattributeForeygnKeyQuery.setParameter("itemclass", itemclass);
            itemclasscustomattributeForeygnKeyQuery.setParameter("customattribute", customattributevalue.getCustomattribute());
            Itemclasscustomattribute itemclasscustomattributByItemclassAndCustomAttribute = null;
            try {
                itemclasscustomattributByItemclassAndCustomAttribute = (Itemclasscustomattribute) itemclasscustomattributeForeygnKeyQuery.getSingleResult();
            } catch (Exception ex) {
                logger.error("Error in  " + ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE + " for " + itemclass.getUriname() + " and " + customattributevalue.getCustomattribute().getUuid() + ex.getMessage());
                throw new Exception("Error in  " + ConstantsMigration.ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE + " for " + itemclass.getUriname() + " and " + customattributevalue.getCustomattribute().getUuid() + ex.getMessage());
            }

            String customAttributeItemClassUriname = customattributevalue.getCustomattribute().getName();
            if ((itemclasscustomattributByItemclassAndCustomAttribute != null && itemclasscustomattributByItemclassAndCustomAttribute.getIsforeignkey())
                    || customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_EXTENSIBILITY)
                    || customAttributeItemClassUriname.equals(BaseConstants.KEY_FIELD_GOVERNANCELEVEL)) {
                /**
                 * get localization of custom attribute value with
                 * masterlanguage
                 */
                try {
                    Query queryLocalizationByCustomattributevalue = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE);
                    queryLocalizationByCustomattributevalue.setParameter("customattributevalue", customattributevalue);
                    queryLocalizationByCustomattributevalue.setParameter("language", masterLanguagecode);
                    Localization customattributevalueLocalization = null;
                    try {
                        customattributevalueLocalization = (Localization) queryLocalizationByCustomattributevalue.getSingleResult();
                        logger.error("pruebas addCustomAttribute localization  = " + customattributevalueLocalization.getUuid());
                    } catch (Exception ex) {
                        logger.error("Error in  " + ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE + " for customattributevalue" + customattributevalue.getUuid() + " and language" + masterLanguagecode.getUuid() + ex.getMessage());
                        throw new Exception("Error in  " + ConstantsMigration.LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE + " for customattributevalue" + customattributevalue.getUuid() + " and language" + masterLanguagecode.getUuid() + ex.getMessage());
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
                            logger.error("pruebas addCustomAttribute preForeygnKeyRegItem");
                            RegItem foreygnKeyRegItem = regItemManager.getByLocalidAndRegItemClass(customattributevalueLocalization.getLabel(), regItemclassManager.getByLocalid(customattribute));
                            logger.error("pruebas addCustomAttribute foreygnKeyRegItem  = " + foreygnKeyRegItem.getUuid());
                            createRegrelationFromRegItemAndRegfieldAndKoreygnKeyRegItem(regItem, regFieldsMap, foreygnKeyRegItem, customattributevalue, fieldIndex, commit);

                        } else if (customattributevalue.getCustomattribute().getMultivalue()) {
                            String[] customattributeList = customattributevalueLocalization.getLabel().split(",");
                            for (String customattribute : customattributeList) {
                                createRegrelationFromRegItemAndRegfield(customattribute, customAttributeItemClassUriname, regItem, regFieldsMap, customattributevalue, fieldIndex, commit);
                                fieldIndex++;
                            }
                        } else {
                            createRegrelationFromRegItemAndRegfield(customattributevalueLocalization.getLabel(), customAttributeItemClassUriname, regItem, regFieldsMap, customattributevalue, fieldIndex, commit);
                        }

                    }

                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
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
                            createRegLocalizationForRegItemAndRegFieldFromCustomattribute(regFieldsMap, customattributevalueLocalization, regItem, reglanguagecodeByLanguage, customattribute, fieldIndex, commit);
                            fieldIndex++;
                        }
                    } else {
                        createRegLocalizationForRegItemAndRegFieldFromCustomattribute(regFieldsMap, customattributevalueLocalization, regItem, reglanguagecodeByLanguage, customattributevalueLocalization.getLabel(), fieldIndex, commit);
                    }

                }
            }
        }
    }

    private void createRegLocalizationForRegItemAndRegFieldFromCustomattribute(HashMap<String, RegField> regFieldsMap, Localization customattributevalueLocalization, RegItem regItem, RegLanguagecode reglanguagecodeByLanguage, String customattribute, int fieldIndex, boolean commit) throws Exception {
        RegField regFieldUsedForLocalization = regFieldsMap.get(customattributevalueLocalization.getCustomattributevalue().getCustomattribute().getName());

        RegLocalization regLocalizationCustomattribute = new RegLocalization();
        regLocalizationCustomattribute.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, regItem, regFieldUsedForLocalization));
        regLocalizationCustomattribute.setRegLanguagecode(reglanguagecodeByLanguage);
        regLocalizationCustomattribute.setRegItem(regItem);
        regLocalizationCustomattribute.setRegField(regFieldUsedForLocalization);
        regLocalizationCustomattribute.setFieldValueIndex(fieldIndex);
        regLocalizationCustomattribute.setValue(customattribute);
        if ((customattribute.contains(BaseConstants.KEY_PARAMETER_HTTP) || customattribute.contains(BaseConstants.KEY_PARAMETER_HTTPS)) && !customattribute.contains(" ")) {
            regLocalizationCustomattribute.setHref(customattribute);
        } else {
            regLocalizationCustomattribute.setHref(null);
        }
        regLocalizationCustomattribute.setRegAction(null);
        regLocalizationCustomattribute.setRegRelationReference(null);
        regLocalizationCustomattribute.setInsertdate(new Date());

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regLocalizationCustomattribute);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }

            if (regLocalizationCustomattribute.getHref() != null) {
                /**
                 * update the field mapping get the mapping for
                 * regFieldUsedForLocalization and
                 */
                RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManagerRe3gistry2);
                RegFieldmapping mapping = regFieldmappingManager.getByFieldAndItemClass(regFieldUsedForLocalization, regItem.getRegItemclass());

                mapping.setHashref(Boolean.TRUE);

                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.merge(mapping);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            }

        } catch (Exception ex) {
            logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, regItem.getUuid(), regFieldUsedForLocalization.getUuid(), reglanguagecodeByLanguage.getIso6391code()) + " " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private void createRegrelationFromRegItemAndRegfield(String customattribute, String customAttributeItemClassUriname, RegItem regItem, HashMap<String, RegField> regFieldsMap, Customattributevalue customattributevalue, int fieldIndex, boolean commit) throws Exception {
        RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

        RegItem foreygnKeyRegItem = regItemManager.getByLocalidAndRegItemClass(customattribute, regItemclassManager.getByLocalid(customAttributeItemClassUriname));

        createRegrelationFromRegItemAndRegfieldAndKoreygnKeyRegItem(regItem, regFieldsMap, foreygnKeyRegItem, customattributevalue, fieldIndex, commit);
    }

    private void createRegrelationFromRegItemAndRegfieldAndKoreygnKeyRegItem(RegItem regItem, HashMap<String, RegField> regFieldsMap, RegItem foreygnKeyRegItem, Customattributevalue customattributevalue, int fieldIndex, boolean commit) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);
        /**
         * create reg relation between reg item and reg item of the foreign key
         */
        RegRelation regRelationForeignkey = new RegRelation();
        regRelationForeignkey.setUuid(RegRelationUuidHelper.getUuid(regItem, predicate, foreygnKeyRegItem));
//        regRelationForeignkey.setUuid(regItem.getUuid());
        regRelationForeignkey.setRegItemSubject(regItem);
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

        RegLocalization regLocalizationCustomAttributeForeignKey = new RegLocalization();
        regLocalizationCustomAttributeForeignKey.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, migrationManager.getRegmasterLanguage(), regItem, regFieldForForeignKey));
        regLocalizationCustomAttributeForeignKey.setRegItem(regItem);
        regLocalizationCustomAttributeForeignKey.setRegField(regFieldForForeignKey);
        regLocalizationCustomAttributeForeignKey.setFieldValueIndex(fieldIndex);
        regLocalizationCustomAttributeForeignKey.setRegLanguagecode(migrationManager.getRegmasterLanguage());
        regLocalizationCustomAttributeForeignKey.setValue(null);
        regLocalizationCustomAttributeForeignKey.setHref(null);
        regLocalizationCustomAttributeForeignKey.setRegAction(null);
        regLocalizationCustomAttributeForeignKey.setRegRelationReference(regRelationForeignkey);
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
            logger.error(MessageFormat.format(ConstantsMigration.ERROR_CANTCREATEREGLOALIZATIONFORREGITEM, regItem.getUuid(), regFieldForForeignKey.getUuid(), migrationManager.getRegmasterLanguage()) + " " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    protected void migrateRegRelation(RegItem regItem, RegItem regitemRelation, boolean commit, String key) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(key);
//        String uuid = RegRelationUuidHelper.getUuid(regItem, predicate, regitemRelation);
        String uuid = regItem.getUuid();
        try {
            RegRelationManager regRelationManager = new RegRelationManager(entityManagerRe3gistry2);
            regRelationManager.get(uuid);
        } catch (Exception ex) {
            try {
                RegRelation regRelation = new RegRelation();
                regRelation.setUuid(uuid);
                regRelation.setRegItemSubject(regItem);
                regRelation.setRegRelationpredicate(predicate);
                regRelation.setRegItemObject(regitemRelation);
                regRelation.setInsertdate(new Date());
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regRelation);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
            }
        }
    }

    protected void migrateRegRelation(RegItem regItem, RegItemhistory regitemRelationHistory, boolean commit, String key) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
        final RegRelationpredicate predicate = regRelationpredicateManager.get(key);
//        String uuid = RegRelationhistoryUuidHelper.getUuid(null, regItem, predicate, regitemRelationHistory, null);
        String uuid = regItem.getUuid();
        try {
            RegRelationhistoryManager regRelationhistoryManager = new RegRelationhistoryManager(entityManagerRe3gistry2);
            regRelationhistoryManager.get(uuid);
        } catch (Exception ex) {
            try {
                RegRelationhistory regRelationHistory = new RegRelationhistory();
                regRelationHistory.setUuid(uuid);
                regRelationHistory.setRegItemSubject(regItem);
                regRelationHistory.setRegRelationpredicate(predicate);
                regRelationHistory.setRegItemhistoryObject(regitemRelationHistory);
                regRelationHistory.setInsertdate(new Date());
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regRelationHistory);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
            }
        }
    }

    private void addRegistryRelation(Item item, RegItem regItem, boolean commit) throws Exception {
        try {
            /**
             * add reg relation to registry
             */
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass registryRegItemclass = regItemclassManager.getByLocalid(item.getItemclass().getRegister().getRegistry().getUriname());
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItem registryItem = regItemManager.getByLocalidAndRegItemClass(item.getItemclass().getRegister().getRegistry().getUriname(), registryRegItemclass);

            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
            final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
            RegRelation regRelationRegisterRegistry = new RegRelation();

            regRelationRegisterRegistry.setUuid(RegRelationUuidHelper.getUuid(regItem, predicate, registryItem)); 
            regRelationRegisterRegistry.setRegItemSubject(regItem);
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
                logger.error(ex.getMessage(), ex);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
    }

    private void addRegisterRelation(Item item, RegItem regItem, boolean commit) throws Exception {
        try {
            /**
             * add reg relation to register
             */
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass registerRegItemclass = regItemclassManager.getByLocalid(item.getItemclass().getRegister().getUriname());
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItem registerItem = regItemManager.getByLocalidAndRegItemClass(item.getItemclass().getRegister().getUriname(), registerRegItemclass);

            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
            final RegRelationpredicate predicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
            RegRelation regRelationRegisterRegistry = new RegRelation();

            regRelationRegisterRegistry.setUuid(RegRelationUuidHelper.getUuid(regItem, predicate, registerItem));
            regRelationRegisterRegistry.setRegItemSubject(regItem);
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
                logger.error(ex.getMessage(), ex);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
    }

    public RegItem createRegItemWithoutCollection(Item item, RegItemclass regItemclass, boolean commit) throws Exception {
        RegItem regItem = new RegItem();
        String localid = item.getUriname();
        //final String uuid = RegItemUuidHelper.getUuid(localid, null, regItemclass);
        String uuid = item.getUuid();

        try {
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            regItem = regItemManager.get(uuid);
        } catch (Exception ex) {

            int version = item.getVersionnumber();
            String status = item.getStatus().getUriname();
            /**
             * create RegItem for the register
             */
            regItem.setUuid(uuid);
            regItem.setLocalid(localid);
            regItem.setRegItemclass(regItemclass);

            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            /**
             * change
             */
            RegStatus regStatus = regStatusManager.findByLocalid(status);
            regItem.setRegStatus(regStatus);

            if (migrationUser == null) {
                RegUserManager regUserManager = new RegUserManager(entityManagerRe3gistry2);
                regItem.setRegUser(regUserManager.findByEmail(BaseConstants.MIGRATION_USER_NAME));//change it
            } else {
                regItem.setRegUser(migrationUser);
            }
            regItem.setRorExport(Boolean.FALSE);

            if (item.getDatecreation() != null) {
                regItem.setInsertdate(item.getDatecreation());
            } else {
                regItem.setInsertdate(new Date());
            }
            if (item.getDatelastupdate() != null) {
                regItem.setEditdate(item.getDatelastupdate());
            }
            regItem.setCurrentversion(version);

            if (localid.startsWith(BaseConstants.KEY_PARAMETER_HTTP) || localid.startsWith(BaseConstants.KEY_PARAMETER_HTTPS)) {
                regItem.setExternal(Boolean.TRUE);
            } else {
                regItem.setExternal(Boolean.FALSE);
            }

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regItem);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error("Could not create uuid for item localid: " + localid + " and item class: " + regItemclass.getLocalid() + exx.getMessage());
                throw new Exception(exx.getMessage());
            }
        }

        return regItem;
    }

    RegItem createRegItemWithCollection(Item item, RegItemclass regItemclass, Item itemcollection, boolean commit) throws Exception {
        RegItem regItem = new RegItem();
        String localid = item.getUriname();
        /**
         * create RegItem for the register
         */
        RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

        //final String uuid = RegItemUuidHelper.getUuid(localid, regItemManager.getByLocalidAndRegItemClass(itemcollection.getUriname(), regItemclassManager.getByLocalid(itemcollection.getItemclass().getUriname())), regItemclass);
        String uuid = item.getUuid();

        try {
            regItem = regItemManager.get(uuid);
        } catch (Exception exx) {

            int version = item.getVersionnumber();
            String status = item.getStatus().getUriname();

            regItem.setUuid(uuid);
            regItem.setLocalid(localid);
            regItem.setRegItemclass(regItemclass);

            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            RegStatus regStatus = regStatusManager.findByLocalid(status);
            regItem.setRegStatus(regStatus);

            if (migrationUser == null) {
                RegUserManager regUserManager = new RegUserManager(entityManagerRe3gistry2);
                regItem.setRegUser(regUserManager.findByEmail(BaseConstants.MIGRATION_USER_NAME));
            } else {
                regItem.setRegUser(migrationUser);
            }
            regItem.setRorExport(Boolean.FALSE);

            if (item.getDatecreation() != null) {
                regItem.setInsertdate(item.getDatecreation());
            } else {
                regItem.setInsertdate(new Date());
            }
            if (item.getDatelastupdate() != null) {
                regItem.setEditdate(item.getDatelastupdate());
            }
            regItem.setCurrentversion(version);

            if (localid.startsWith(BaseConstants.KEY_PARAMETER_HTTP) || localid.startsWith(BaseConstants.KEY_PARAMETER_HTTPS)) {
                regItem.setExternal(Boolean.TRUE);
            } else {
                regItem.setExternal(Boolean.FALSE);
            }

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regItem);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                logger.error("Could not create uuid for item localid: " + localid + " and collection localid: " + itemcollection.getUriname() + " and item class: " + regItemclass.getLocalid() + ex.getMessage());
                throw new Exception(ex.getMessage());
            }
        }

        return regItem;
    }
}
