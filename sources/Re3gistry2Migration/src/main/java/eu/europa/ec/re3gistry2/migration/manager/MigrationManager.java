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
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRoleManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationHandler;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Reference;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRole;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRoleUuidHelper;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import static org.eclipse.persistence.config.EntityManagerProperties.*;

public class MigrationManager {

    private static EntityManager entityManagerRe3gistry2Migration;
    private final EntityManager entityManagerRe3gistry2;

    private RegLanguagecode regmasterLanguage;
    private final Logger logger;
    private int procedureorder = 0;

    private RegUser migrationUser;
    private EntityManagerFactory entityManagerFactoryRe3gistry2Migration;
    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    public MigrationManager(String dbAddress, String dbPort, String dbName, String dbUsername, String dbPassword, EntityManager entityManagerRe3gistry2) {
        logger = Configuration.getInstance().getLogger();

        this.dbAddress = dbAddress;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;

//        if (entityManagerRe3gistry2Migration == null) {
            Map emProperties = new HashMap();
            emProperties.put(JDBC_DRIVER, "org.postgresql.Driver");
            emProperties.put(JDBC_URL, "jdbc:postgresql://" + dbAddress + ":" + dbPort + "/" + dbName);
            emProperties.put(JDBC_USER, dbUsername);
            emProperties.put(JDBC_PASSWORD, dbPassword);
            this.entityManagerFactoryRe3gistry2Migration = Persistence.createEntityManagerFactory("Re3gistry2Migration", emProperties);
            entityManagerRe3gistry2Migration = entityManagerFactoryRe3gistry2Migration.createEntityManager();
//        }

        if (!entityManagerRe3gistry2Migration.getTransaction().isActive()) {
            entityManagerRe3gistry2Migration.getTransaction().begin();
        }
    }

    public void startMigration() throws Exception {
        try {

            /**
             * create the migration user to be used for the reg items and reg
             * items history
             */
            RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
            migrationUser = regInstallationHandler.createRegUser(BaseConstants.MIGRATION_USER_NAME, BaseConstants.MIGRATION_USER_PASSWORD, true, false);
            regInstallationHandler.setAllGroupsAndAllRightsByUser(migrationUser, true);
            /**
             * migrate languages, and change the existing one from the inserting
             * script
             */
            MigrateLanguage migrateLanguage = new MigrateLanguage(this);
            migrateLanguage.startMigrationLanguageCode();

            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
            regmasterLanguage = regLanguagecodeManager.getMasterLanguage();

            MigrateRegistry migrateRegistry = new MigrateRegistry(this);
            migrateRegistry.startMigrationRegistry();

            MigrateRegisters migrateRegisters = new MigrateRegisters(this);
            migrateRegisters.startMigrationRegister();

            MigrateExtensiblityAndGovernanceLevel migrateExtensiblity = new MigrateExtensiblityAndGovernanceLevel(this);
            migrateExtensiblity.startMigrationExtensiblityAndGovernanceLevel();

            MigrateItems migrateItems = new MigrateItems(this);
            migrateItems.startMigrationItems();
        } catch (Exception e) {
            try {
                if (entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().rollback();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
            throw new Exception(e.getMessage());
        } finally {
            try {
                entityManagerRe3gistry2.close();
                entityManagerRe3gistry2Migration.close();
            } catch (Exception e) {
                /**
                 * ignored
                 */
            }
        }
    }

    public RegRole createRoleByName(String groupLocalID, String groupName, boolean commit) throws Exception {
        RegRole regRole = new RegRole();
        String uuid = RegRoleUuidHelper.getUuid(groupLocalID);
        try {
            RegRoleManager regRoleManager = new RegRoleManager(entityManagerRe3gistry2);
            regRole = regRoleManager.get(uuid);
        } catch (Exception ex) {
            regRole.setUuid(uuid);
            regRole.setLocalid(groupLocalID);
            regRole.setName(groupName);
            regRole.setInsertdate(new Date());
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.merge(regRole);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
            }
        }

        return regRole;
    }

    public void createRegisterFieldGroupAndLocalization(RegItemclass regItemclass, RegItem registerItem, String groupLocalID, String groupName, Reference reference, String regFieldTypeUuid, Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER, int fieldIndex, boolean commit) throws Exception {
        RegRoleManager regRoleManager = new RegRoleManager(entityManagerRe3gistry2);

        RegRole regRole;

        RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
        String roleID = regInstallationHandler.getRoleIDByReferenceRefType(reference.getReftype());
        if (roleID != null) {
            regRole = regRoleManager.get(roleID);
        } else {
            regRole = null;
        }
        RegField regField = regInstallationHandler.createRegFielAndFieldLocalizationdByName(groupLocalID, regFieldTypeUuid, regRole, commit);
        regInstallationHandler.createRegFieldMapping(regField, regItemclass, AUTO_INCREMENT_REG_FIELD_LIST_ORDER, commit);
        /**
         * create register manager group
         */

        if (!groupName.isEmpty()) {
            RegGroup regGroup = regInstallationHandler.createGroupByName(groupLocalID, groupName, commit);
            createRegGroupMapping(registerItem, regGroup, regRole, commit);
        }

        createRegGroupLocalization(reference, fieldIndex, registerItem, regField, commit);
    }

    private void createRegGroupLocalization(Reference reference, int fieldIndex, RegItem registerItem, RegField regField, boolean commit) {
        Query queryLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findAll", Languagecode.class);
        List<Languagecode> languagecodeList = queryLanguagecode.getResultList();

        Query queryReference = entityManagerRe3gistry2Migration.createNamedQuery("Reference.findByUuid", Reference.class);
        String referenceUuid = reference.getUuid();
        queryReference.setParameter("uuid", referenceUuid);
        Reference registryManagerReference = (Reference) queryReference.getSingleResult();

        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
        languagecodeList.forEach((languagecode) -> {
            Query queryLocalizationReference = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REFERENCE_AND_LANGUAGE);
            queryLocalizationReference.setParameter("reference", registryManagerReference);
            Localization referenceLocalization;
            try {
                queryLocalizationReference.setParameter("language", languagecode);
                referenceLocalization = (Localization) queryLocalizationReference.getSingleResult();

                RegLanguagecode reglanguagecodeReferenceList = regLanguagecodeManager.getByIso6391code(referenceLocalization.getLanguage().getIsocode());
                RegLocalization regLocalization = new RegLocalization();

                regLocalization.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, reglanguagecodeReferenceList, registerItem, regField));
                regLocalization.setRegItem(registerItem);
                regLocalization.setRegField(regField);
                regLocalization.setFieldValueIndex(fieldIndex);
                regLocalization.setRegLanguagecode(reglanguagecodeReferenceList);
                regLocalization.setValue(referenceLocalization.getLabel());

                regLocalization.setRegRelationReference(null);
                if (referenceLocalization.getUri() != null) {
                    regLocalization.setHref(referenceLocalization.getUri());
                } else if (registryManagerReference.getEmail() != null) {
                    regLocalization.setHref("mailto:" + registryManagerReference.getEmail());
                } else {
                    regLocalization.setHref(null);
                }
                regLocalization.setRegAction(null);
                regLocalization.setInsertdate(new Date());

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.merge(regLocalization);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }

                    if (regLocalization.getHref() != null) {
                        /**
                         * update the field mapping
                         */
                        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManagerRe3gistry2);
                        RegFieldmapping mapping = regFieldmappingManager.getByFieldAndItemClass(regField, registerItem.getRegItemclass());

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
                    logger.error(ex.getMessage());
                    throw new Exception(ex.getMessage());
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        });
    }

    private void createRegGroupMapping(RegItem registerItem, RegGroup regGroup, RegRole regRole, boolean commit) throws Exception {
        RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping = new RegItemRegGroupRegRoleMapping();
        regItemRegGroupRegRoleMapping.setUuid(RegItemRegGroupRegRoleMappingUuidHelper.getUuid(registerItem.getUuid(), regGroup.getUuid(), regRole.getUuid()));
        regItemRegGroupRegRoleMapping.setRegItem(registerItem);
        regItemRegGroupRegRoleMapping.setRegGroup(regGroup);
        regItemRegGroupRegRoleMapping.setRegRole(regRole);
        regItemRegGroupRegRoleMapping.setInsertdate(new Date());
        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.merge(regItemRegGroupRegRoleMapping);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    public EntityManager getEntityManagerRe3gistry2() {
        return entityManagerRe3gistry2;
    }

    public EntityManager getEntityManagerRe3gistry2Migration() {
        return entityManagerRe3gistry2Migration;
    }

    public Logger getLogger() {
        return logger;
    }

    public int getProcedureorder() {
        return procedureorder;
    }

    public void setProcedureorder(int procedureorder) {
        this.procedureorder = procedureorder;
    }

    public RegUser getMigrationUser() {
        if (migrationUser == null) {
            logger.error("USER is null");
        }
        return migrationUser;
    }

    public void setMigrationUser(RegUser migrationUser) {
        this.migrationUser = migrationUser;
    }

    public RegLanguagecode getRegmasterLanguage() {
        if (regmasterLanguage == null) {
            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
            try {
                regmasterLanguage = regLanguagecodeManager.getMasterLanguage();
            } catch (Exception ex) {
                regmasterLanguage = null;
            }
        }
        return regmasterLanguage;
    }

    public void setRegmasterLanguage(RegLanguagecode regmasterLanguage) {
        this.regmasterLanguage = regmasterLanguage;
    }

    public EntityManagerFactory getEntityManagerFactoryRe3gistry2Migration() {
        if (entityManagerFactoryRe3gistry2Migration == null) {
            Map emProperties = new HashMap();
            emProperties.put(JDBC_DRIVER, "org.postgresql.Driver");
            emProperties.put(JDBC_URL, "jdbc:postgresql://" + dbAddress + ":" + dbPort + "/" + dbName);
            emProperties.put(JDBC_USER, dbUsername);
            emProperties.put(JDBC_PASSWORD, dbPassword);
            this.entityManagerFactoryRe3gistry2Migration = Persistence.createEntityManagerFactory("Re3gistry2Migration", emProperties);
            entityManagerRe3gistry2Migration = entityManagerFactoryRe3gistry2Migration.createEntityManager();
        }
        return entityManagerFactoryRe3gistry2Migration;
    }

}
