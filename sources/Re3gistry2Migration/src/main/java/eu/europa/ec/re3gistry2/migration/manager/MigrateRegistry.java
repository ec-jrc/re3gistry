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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationHandler;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Registry;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemclassUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateRegistry {

    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;
    private final MigrationManager migrationManager;
    private final RegLanguagecodeManager regLanguagecodeManager;

    public MigrateRegistry(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
        this.regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
    }

    public void startMigrationRegistry() throws Exception {
        ArrayList<RegItem> itemsToIndexSOLR = new ArrayList<>();

        int procedureorder = migrationManager.getProcedureorder();
        boolean commit = false;

        /**
         * migrate Registry item class and localization
         */
        try {
            Query queryRegistry = entityManagerRe3gistry2Migration.createNamedQuery("Registry.findAll", Registry.class);
            List<Registry> registryList;
            try {
                registryList = queryRegistry.getResultList();
            } catch (Exception ex) {
                logger.error("Error in  getting the result list for " + queryRegistry + " " + ex.getMessage());
                throw new Exception("Error in  getting the localization for " + queryRegistry + " " + ex.getMessage());
            }

            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManagerRe3gistry2);
            RegItemclasstype regItemclasstypeRegistry = regItemclasstypeManager.getByLocalid("registry");

            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            for (Registry registry : registryList) {
                Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER = 0;
                /**
                 * create new RegItemclass for each registry
                 */
                RegItemclass regItemclass = new RegItemclass();
                try {
                    regItemclass = regItemclassManager.getByLocalid(registry.getUriname());
                } catch (Exception ex) {
                    regItemclass = migrateRegistryItemclassByItemClass(regItemclass, registry, regItemclasstypeRegistry, procedureorder, commit);
                    procedureorder++;
                }

                RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
                /**
                 * create registry regItem
                 */

                try {
                    String uuid = RegItemUuidHelper.getUuid(registry.getUriname(), null, regItemclass);
                    RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                    regItemManager.get(uuid);
                } catch (Exception ex) {

                    RegItem registryItem = regInstallationHandler.createRegItemWithoutCollection(registry.getUriname(), regItemclass, 0, migrationManager.getMigrationUser(), commit, registry.getDatecreation(), registry.getDatelastupdate());
                    itemsToIndexSOLR.add(registryItem);
                    int fieldIndex = 0;

                    /**
                     * create fields
                     */
                    RegField regFieldLabel = regInstallationHandler.createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID, Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_STRING_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                    RegField regFieldContentSummary = regInstallationHandler.createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID, Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);

                    /**
                     * migrate registry localization
                     */
                    migrateRegistryLocalization(registry, fieldIndex, registryItem, regFieldLabel, regFieldContentSummary, commit);

                    Query queryMasterLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findByMasterlanguage", Languagecode.class);
                    queryMasterLanguagecode.setParameter("masterlanguage", Boolean.TRUE);
                    Languagecode masterLanguagecode = (Languagecode) queryMasterLanguagecode.getSingleResult();

                    Query queryLocalizationReference = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REFERENCE_AND_LANGUAGE);
                    queryLocalizationReference.setParameter("reference", registry.getRegistrymanager());
                    queryLocalizationReference.setParameter("language", masterLanguagecode);

                    try {
                        Localization referenceLocalization = (Localization) queryLocalizationReference.getSingleResult();
                        /**
                         * create groups with localization
                         */
                        migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registryItem, "registryManager", referenceLocalization.getLabel(), registry.getRegistrymanager(), BaseConstants.KEY_FIELDTYPE_STRING_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);
                    } catch (Exception referenceLocalizationException) {
                        logger.error("Query " + queryLocalizationReference + " didn't return any result. " + referenceLocalizationException.getMessage());
                        throw new Exception("Query " + queryLocalizationReference + " didn't return any result. " + referenceLocalizationException.getMessage());
                    }

                }
            }
            migrationManager.setProcedureorder(procedureorder);

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.getTransaction().commit();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }

            itemsToIndexSOLR.forEach((regItem) -> {
                try {
                    SolrHandler.indexSingleItem(regItem);
                } catch (Exception e) {
                    logger.error("@ MigrateRegistry.regItemProposedToRegItem: Solr indexing error.", e);
                }
            });

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private void migrateRegistryLocalization(Registry registry, int fieldIndex, RegItem registryItem, RegField regFieldLabel, RegField regFieldContentSummary, boolean commit) throws Exception {
        /**
         * create registry localization
         */
        Query queryLocalizationRegistry = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REGISTRY);
        queryLocalizationRegistry.setParameter("registry", registry);

        List<Localization> localizationRegistryList;
        try {
            localizationRegistryList = queryLocalizationRegistry.getResultList();
        } catch (Exception ex) {
            logger.error("Error in  getting the result list for " + queryLocalizationRegistry + " " + ex.getMessage());
            throw new Exception("Error in  getting the localization for " + queryLocalizationRegistry + " " + ex.getMessage());
        }

        for (Localization registryLocalization : localizationRegistryList) {

            RegLanguagecode reglanguagecodeByLanguage = regLanguagecodeManager.getByIso6391code(registryLocalization.getLanguage().getIsocode());

            if (registryLocalization.getLabel() != null && !registryLocalization.getLabel().isEmpty()) {
                RegLocalization regLocalizationLabel = new RegLocalization();
                regLocalizationLabel.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, registryItem, regFieldLabel));
                regLocalizationLabel.setRegItem(registryItem);
                regLocalizationLabel.setRegField(regFieldLabel);
                regLocalizationLabel.setFieldValueIndex(fieldIndex);
                regLocalizationLabel.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationLabel.setValue(registryLocalization.getLabel());
                regLocalizationLabel.setHref(null);
                regLocalizationLabel.setRegAction(null);
                regLocalizationLabel.setRegRelationReference(null);
                regLocalizationLabel.setInsertdate(new Date());

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationLabel);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            }

            if (registryLocalization.getDefinition() != null && !registryLocalization.getDefinition().isEmpty()) {
                RegLocalization regLocalizationContentSummary = new RegLocalization();
                regLocalizationContentSummary.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, registryItem, regFieldContentSummary));
                regLocalizationContentSummary.setRegItem(registryItem);
                regLocalizationContentSummary.setRegField(regFieldContentSummary);
                regLocalizationContentSummary.setFieldValueIndex(fieldIndex);
                regLocalizationContentSummary.setValue(registryLocalization.getDefinition());
                regLocalizationContentSummary.setHref(null);
                regLocalizationContentSummary.setRegAction(null);
                regLocalizationContentSummary.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationContentSummary.setInsertdate(new Date());
                regLocalizationContentSummary.setRegRelationReference(null);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLocalizationContentSummary);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());
                }
            }

        }
    }

    private RegItemclass migrateRegistryItemclassByItemClass(RegItemclass regItemclass, Registry registry, RegItemclasstype regItemclasstypeRegistry, int procedureorder, boolean commit) throws Exception {
        try {
            regItemclass.setUuid(RegItemclassUuidHelper.getUuid(registry.getUriname(), null, regItemclasstypeRegistry));
            regItemclass.setLocalid(registry.getUriname());
            regItemclass.setRegItemclasstype(regItemclasstypeRegistry);

            /**
             * set status, but maybe will be deleted
             */
            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regItemclass.setRegStatus(regStatusManager.get("1"));

            regItemclass.setDataprocedureorder(procedureorder);
            regItemclass.setInsertdate(new Date());
            regItemclass.setBaseuri(registry.getBaseuri());
            regItemclass.setActive(Boolean.TRUE);
            regItemclass.setSystemitem(Boolean.FALSE);
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regItemclass);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exe) {
                logger.error(exe.getMessage());
                throw new Exception(exe.getMessage());
            }
            return regItemclass;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

}
