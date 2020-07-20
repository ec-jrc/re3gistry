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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationHandler;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Register;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemclassUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationUuidHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateRegisters {

    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;
    private final MigrationManager migrationManager;
    private final RegLanguagecodeManager regLanguagecodeManager;

    public MigrateRegisters(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
        this.regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
    }

    public void startMigrationRegister() throws Exception {
        ArrayList<RegItem> itemsToIndexSOLR = new ArrayList<>();
        int procedureorder = migrationManager.getProcedureorder();
        boolean commit = false;
        /**
         * migrate Register item class
         */
        try {
            Query queryRegister = entityManagerRe3gistry2Migration.createNamedQuery("Register.findAll", Register.class);
            List<Register> registerList = queryRegister.getResultList();

            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManagerRe3gistry2);
            RegItemclasstype regItemclasstypeRegister = regItemclasstypeManager.getByLocalid("register");

            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);

            for (Register register : registerList) {
                Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER = 0;

                /**
                 * create item class if does not exist
                 */
                RegItemclass regItemclass = new RegItemclass();
                try {
                    regItemclass = regItemclassManager.getByLocalid(register.getUriname());
                } catch (Exception ex) {
                    regItemclass = migrateRegisterItemclassByItemClass(regItemclass, register, regItemclasstypeRegister, procedureorder, commit);
                    procedureorder++;
                }

                try {
                    String uuid = RegItemUuidHelper.getUuid(register.getUriname(), null, regItemclass);
                    RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                    regItemManager.get(uuid);
                } catch (Exception excep) {

                    RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
                    /**
                     * create register reg item
                     */
                    RegItem registerItem = regInstallationHandler.createRegItemWithoutCollection(register.getUriname(), regItemclass, 0, migrationManager.getMigrationUser(), commit);

                    itemsToIndexSOLR.add(registerItem);
                    /**
                     * create fields
                     */
                    RegField regFieldLabel = regInstallationHandler.createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID, Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_STRING_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                    RegField regFieldContentSummary = regInstallationHandler.createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID, Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);

                    int fieldIndex = migrateRegisterLocalization(register, registerItem, regFieldLabel, regFieldContentSummary, commit);

                    Query queryMasterLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findByMasterlanguage", Languagecode.class);
                    queryMasterLanguagecode.setParameter("masterlanguage", Boolean.TRUE);
                    Languagecode masterLanguagecode = (Languagecode) queryMasterLanguagecode.getSingleResult();

                    Query queryLocalizationReference = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REFERENCE_AND_LANGUAGE);
                    queryLocalizationReference.setParameter("language", masterLanguagecode);

                    queryLocalizationReference.setParameter(ConstantsMigration.KEY_PARAMETER_REFERENCE, register.getRegistermanager());
                    Localization referenceRegisterManagerLocalization = (Localization) queryLocalizationReference.getSingleResult();
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_REGISTERMANAGER, referenceRegisterManagerLocalization.getLabel(), register.getRegistermanager(), BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);

                    queryLocalizationReference.setParameter(ConstantsMigration.KEY_PARAMETER_REFERENCE, register.getRegisterowner());
                    Localization referenceRegisterOwnerLocalization = (Localization) queryLocalizationReference.getSingleResult();
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_REGISTEROWNER, referenceRegisterOwnerLocalization.getLabel(), register.getRegisterowner(), BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);

                    queryLocalizationReference.setParameter(ConstantsMigration.KEY_PARAMETER_REFERENCE, register.getRegistercontrolbody());
                    Localization referenceControlBodyLocalization = (Localization) queryLocalizationReference.getSingleResult();
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_CONTROLBODY, referenceControlBodyLocalization.getLabel(), register.getRegistercontrolbody(), BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);

                    queryLocalizationReference.setParameter(ConstantsMigration.KEY_PARAMETER_REFERENCE, register.getSubmitter());
                    Localization referenceSubmitterLocalization = (Localization) queryLocalizationReference.getSingleResult();
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION, referenceSubmitterLocalization.getLabel(), register.getSubmitter(), BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);

                    /**
                     * no role
                     */
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_CONTACT_POINT, "", register.getContactpoint(), BaseConstants.KEY_FIELDTYPE_STRING_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);
                    migrationManager.createRegisterFieldGroupAndLocalization(regItemclass, registerItem, BaseConstants.KEY_ROLE_LICENSE, "", register.getLicense(), BaseConstants.KEY_FIELDTYPE_STRING_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, fieldIndex, commit);

                    /**
                     * add reg relation to registry
                     */
                    RegItemclass registryRegItemclass = regItemclassManager.getByLocalid(register.getRegistry().getUriname());
                    RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
                    RegItem registryItem = regItemManager.getByLocalidAndRegItemClass(register.getRegistry().getUriname(), registryRegItemclass);

                    RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManagerRe3gistry2);
                    RegRelationpredicate regRelationPredicate = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);

                    RegRelation regRelationRegisterRegistry = new RegRelation();
                    regRelationRegisterRegistry.setUuid(RegRelationUuidHelper.getUuid(registerItem, regRelationPredicate, registryItem));
                    regRelationRegisterRegistry.setRegItemSubject(registerItem);
                    regRelationRegisterRegistry.setRegRelationpredicate(regRelationPredicate);
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

                    try {
                        entityManagerRe3gistry2.getTransaction().commit();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                        throw new Exception(ex.getMessage());
                    }

                    itemsToIndexSOLR.forEach((regItem) -> {
                        try {
                            SolrHandler.indexSingleItem(regItem);
                        } catch (Exception e) {
                            logger.error("@ MigrateRegister.startMigrationRegister: Solr indexing error.", e);
                        }
                    });
                }
            }

        } catch (Exception exe) {
            logger.error(exe.getMessage());
            throw new Exception(exe.getMessage());
        }

        migrationManager.setProcedureorder(procedureorder);
    }

    private int migrateRegisterLocalization(Register register, RegItem registerItem, RegField regFieldLabel, RegField regFieldContentSummary, boolean commit) throws Exception {
        /**
         * create register localization
         */
        int fieldIndex = 0;
        Query queryLocalizationRegistry = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REGISTER);
        queryLocalizationRegistry.setParameter("register", register);
        List<Localization> localizationRegisterList = queryLocalizationRegistry.getResultList();
        for (Localization localization : localizationRegisterList) {

            RegLanguagecode reglanguagecodeByLanguage = regLanguagecodeManager.getByIso6391code(localization.getLanguage().getIsocode());

            if (localization.getLabel() != null && !localization.getLabel().isEmpty()) {
                RegLocalization regLocalizationLabel = new RegLocalization();
                regLocalizationLabel.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, registerItem, regFieldLabel));
                regLocalizationLabel.setRegLanguagecode(reglanguagecodeByLanguage);
                regLocalizationLabel.setRegItem(registerItem);
                regLocalizationLabel.setRegField(regFieldLabel);
                regLocalizationLabel.setFieldValueIndex(fieldIndex);
                regLocalizationLabel.setRegRelationReference(null);
                regLocalizationLabel.setValue(localization.getLabel());
                regLocalizationLabel.setHref(null);
                regLocalizationLabel.setRegAction(null);
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

            if (localization.getDefinition() != null && !localization.getDefinition().isEmpty()) {
                RegLocalization regLocalizationContentSummary = new RegLocalization();
                regLocalizationContentSummary.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeByLanguage, registerItem, regFieldContentSummary));
                regLocalizationContentSummary.setRegItem(registerItem);
                regLocalizationContentSummary.setRegField(regFieldContentSummary);
                regLocalizationContentSummary.setFieldValueIndex(fieldIndex);
                regLocalizationContentSummary.setValue(localization.getDefinition());
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
        return fieldIndex;
    }

    private RegItemclass migrateRegisterItemclassByItemClass(RegItemclass regItemclass, Register register, RegItemclasstype regItemclasstypeRegister, int procedureorder, boolean commit) throws Exception {
        try {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass parent = regItemclassManager.getByLocalid(register.getRegistry().getUriname());

            regItemclass.setUuid(RegItemclassUuidHelper.getUuid(register.getUriname(), parent.getLocalid(), regItemclasstypeRegister));
            regItemclass.setLocalid(register.getUriname());

            regItemclass.setRegItemclassParent(parent);

            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regItemclass.setRegStatus(regStatusManager.get("1"));

            regItemclass.setRegItemclasstype(regItemclasstypeRegister);
            regItemclass.setDataprocedureorder(procedureorder);

            regItemclass.setInsertdate(new Date());
            regItemclass.setBaseuri(register.getBaseuri());

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
