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
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldtypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRoleManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegFieldtype;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRole;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegFieldmappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemclassUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRoleUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserRegGroupMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserUuidHelper;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

public class RegInstallationHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManagerRe3gistry2;

    // System localization
    ResourceBundle systemLocalization;

    /**
     * This method initializes the class
     *
     * @param entityManagerRe3gistry2
     * @throws Exception
     */
    public RegInstallationHandler(EntityManager entityManagerRe3gistry2) throws Exception {
        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    public void saveActiveLanguages(String[] activeLanguageCodes, boolean commit) throws Exception {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);

        try {
            List<RegLanguagecode> allLanguages = regLanguagecodeManager.getAll();
            for (RegLanguagecode relanguage : allLanguages) {

                boolean exist = false;
                for (String activeLanguage : activeLanguageCodes) {
                    if (relanguage.getIso6391code().equals(activeLanguage)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    relanguage.setActive(Boolean.FALSE);
                    try {
                        if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                            entityManagerRe3gistry2.getTransaction().begin();
                        }
                        entityManagerRe3gistry2.merge(relanguage);
                        if (commit) {
                            entityManagerRe3gistry2.getTransaction().commit();
                        }
                    } catch (Exception ex) {
                        throw new Exception(ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public RegLanguagecode saveMasterLanguage(String masterLanguageCodes, boolean commit) throws Exception {
        try {
            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);

            List<RegLanguagecode> allLanguages = regLanguagecodeManager.getAll();
            allLanguages.forEach((language) -> {
                language.setMasterlanguage(Boolean.FALSE);

                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.merge(language);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            });

            RegLanguagecode master = regLanguagecodeManager.getByIso6391code(masterLanguageCodes);
            master.setActive(Boolean.TRUE);
            master.setMasterlanguage(Boolean.TRUE);
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.merge(master);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }

            return master;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

    }

    public RegItem createRegistry(String registryLocalID, String registryBaseURI, String registryLabel, String registryContentSummary, RegUser adminUser, boolean commit) throws Exception {
        RegItem registryItem = null;
        try {
            RegItemclass regItemclass = createRegItemClassForRegistry(registryLocalID, registryBaseURI, commit);
            if (regItemclass != null) {
                /**
                 * move methods from migration manager in a common place
                 */

                registryItem = createRegItemWithoutCollection(registryLocalID, regItemclass, 0, adminUser, commit);

                int fieldIndex = 0;
                int AUTO_INCREMENT_REG_FIELD_LIST_ORDER = 0;
                RegField regFieldLabel = createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID, Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_STRING_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                RegField regFieldContentSummary = createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID, Boolean.FALSE, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);
                RegField regFieldRegistryManager = createDefaultField(regItemclass, BaseConstants.KEY_FIELD_MANDATORY_REGISTRYMANAGER, Boolean.TRUE, BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID, BaseConstants.REGISTRY_MANAGER_ROLE_UUID, AUTO_INCREMENT_REG_FIELD_LIST_ORDER++, commit);

                RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
                RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

                /**
                 * create localization for label
                 */
                if (registryLabel != null && !registryLabel.isEmpty()) {
                    RegLocalization regLocalizationLabel = new RegLocalization();
                    regLocalizationLabel.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, masterLanguage, registryItem, regFieldLabel));
                    regLocalizationLabel.setRegItem(registryItem);
                    regLocalizationLabel.setRegField(regFieldLabel);
                    regLocalizationLabel.setFieldValueIndex(fieldIndex);
                    regLocalizationLabel.setRegLanguagecode(masterLanguage);
                    regLocalizationLabel.setValue(registryLabel);
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
                        throw new Exception(ex.getMessage());
                    }
                }

                /**
                 * create localization for content summary
                 */
                if (registryContentSummary != null && !registryContentSummary.isEmpty()) {
                    RegLocalization regLocalizationContentSummary = new RegLocalization();
                    regLocalizationContentSummary.setUuid(RegLocalizationUuidHelper.getUuid(fieldIndex, masterLanguage, registryItem, regFieldContentSummary));
                    regLocalizationContentSummary.setRegItem(registryItem);
                    regLocalizationContentSummary.setRegField(regFieldContentSummary);
                    regLocalizationContentSummary.setFieldValueIndex(fieldIndex);
                    regLocalizationContentSummary.setValue(registryContentSummary);
                    regLocalizationContentSummary.setHref(null);
                    regLocalizationContentSummary.setRegAction(null);
                    regLocalizationContentSummary.setRegLanguagecode(masterLanguage);
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
                        throw new Exception(ex.getMessage());
                    }
                }

                /**
                 * create RegItemRegGroupRegRoleMapping
                 */
                RegGroupManager regGroupManager = new RegGroupManager(entityManagerRe3gistry2);
                RegGroup regGroupRegistryManager = regGroupManager.getByLocalid(BaseConstants.KEY_ROLE_REGISTRYMANAGER);
                RegRoleManager regRoleManager = new RegRoleManager(entityManagerRe3gistry2);
                RegRole regRoleRegistryManager = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_REGISTRYMANAGER);

                RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping = new RegItemRegGroupRegRoleMapping();
                regItemRegGroupRegRoleMapping.setUuid(RegItemRegGroupRegRoleMappingUuidHelper.getUuid(registryItem.getUuid(), regGroupRegistryManager.getUuid(), regRoleRegistryManager.getUuid()));
                regItemRegGroupRegRoleMapping.setRegItem(registryItem);
                regItemRegGroupRegRoleMapping.setRegGroup(regGroupRegistryManager);
                regItemRegGroupRegRoleMapping.setRegRole(regRoleRegistryManager);
                regItemRegGroupRegRoleMapping.setInsertdate(new Date());
                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regItemRegGroupRegRoleMapping);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception ex) {
                    throw new Exception(ex.getMessage());
                }

            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        return registryItem;
    }

    public RegField addFieldAndLocalizationDefaultByName(String fieldLocalID, String fieldTypeUuid, String roleID, boolean commit) throws Exception {
        RegRole regRole;
        RegRoleManager regRoleManager = new RegRoleManager(entityManagerRe3gistry2);
        if (roleID != null) {
            regRole = regRoleManager.get(roleID);
        } else {
            regRole = null;
        }
        RegField regField = createRegFielAndFieldLocalizationdByName(fieldLocalID, fieldTypeUuid, regRole, commit);
        return regField;
    }

    private RegItemclass createRegItemClassForRegistry(String registryLocalID, String registryBaseURI, boolean commit) throws Exception {
        RegItemclass regItemclass = new RegItemclass();

        try {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            regItemclass = regItemclassManager.getByLocalid(registryLocalID);

        } catch (Exception ex) {
            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManagerRe3gistry2);
            RegItemclasstype regItemclasstypeRegistry;
            try {
                regItemclasstypeRegistry = regItemclasstypeManager.getByLocalid("registry");
                regItemclass.setUuid(RegItemclassUuidHelper.getUuid(registryLocalID, null, regItemclasstypeRegistry));
                regItemclass.setLocalid(registryLocalID);
                regItemclass.setRegItemclasstype(regItemclasstypeRegistry);

                /**
                 * set status, but maybe will be deleted
                 */
                RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
                regItemclass.setRegStatus(regStatusManager.get("1"));

                int procedureorder = 0;
                regItemclass.setDataprocedureorder(procedureorder);
                regItemclass.setInsertdate(new Date());
                regItemclass.setBaseuri(registryBaseURI);
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
                    return regItemclass;
                } catch (Exception exe) {
                    logger.error(exe.getMessage());
                    throw new Exception(ex.getMessage());
                }
            } catch (Exception ex1) {
                logger.error(ex1.getMessage());
                throw new Exception(ex.getMessage());
            }
        }
        return regItemclass;
    }

    public RegItem createRegItemWithoutCollection(String uriname, RegItemclass regItemclass, int version, RegUser reguser, boolean commit) throws Exception {
        /**
         * create RegItem for the register
         */
        RegItem regItem = new RegItem();
        regItem.setUuid(RegItemUuidHelper.getUuid(uriname, null, regItemclass));
        regItem.setLocalid(uriname);
        regItem.setRegItemclass(regItemclass);

        RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
        /**
         * the register and registry have valid status
         */
        RegStatus regStatus = regStatusManager.get("1");
        regItem.setRegStatus(regStatus);

        regItem.setRegUser(reguser);
        regItem.setRorExport(Boolean.FALSE);

        if (uriname.startsWith("http://") || uriname.startsWith("https://")) {
            regItem.setExternal(Boolean.TRUE);
        } else {
            regItem.setExternal(Boolean.FALSE);
        }

        regItem.setCurrentversion(version);
        regItem.setInsertdate(new Date());

        try {
            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.persist(regItem);
            if (commit) {
                entityManagerRe3gistry2.getTransaction().commit();
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        return regItem;
    }

    public RegField createDefaultField(RegItemclass regItemclass, String name, Boolean required, String regFieldTypeUuid, String roleID, Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER, boolean commit) throws Exception {
        try {
            /**
             * check if the field is already created
             */
            RegRole regRole;
            if (roleID != null) {
                RegRoleManager regRoleManager = new RegRoleManager(entityManagerRe3gistry2);
                regRole = regRoleManager.get(roleID);
            } else {
                regRole = null;
            }

            RegFieldManager regFieldManager = new RegFieldManager(entityManagerRe3gistry2);
            RegField regField;
            try {
                regField = regFieldManager.getByLocalid(name);
            } catch (Exception ex) {
                regField = createRegFielAndFieldLocalizationdByName(name, regFieldTypeUuid, regRole, commit);
            }

            /**
             * RegFieldmapping label
             */
            String uuid = RegFieldmappingUuidHelper.getUuid(regField, regItemclass);
            try {
                RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManagerRe3gistry2);
                regFieldmappingManager.get(uuid);
            } catch (Exception exx) {

                RegFieldmapping regFieldmapping = new RegFieldmapping();

                regFieldmapping.setUuid(uuid);
                regFieldmapping.setRegField(regField);
                regFieldmapping.setRegItemclass(regItemclass);
                regFieldmapping.setListorder(AUTO_INCREMENT_REG_FIELD_LIST_ORDER);
                regFieldmapping.setTablevisible(Boolean.TRUE);

                RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
                regFieldmapping.setRegStatus(regStatusManager.get("1"));

                regFieldmapping.setRequired(required);
                regFieldmapping.setHidden(Boolean.FALSE);
                regFieldmapping.setHashref(Boolean.FALSE);
                regFieldmapping.setMultivalue(Boolean.FALSE);
                regFieldmapping.setInsertdate(new Date());
                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regFieldmapping);
                    if (commit) {
                        entityManagerRe3gistry2.getTransaction().commit();
                    }
                } catch (Exception exception) {
                    throw new Exception(exception.getMessage());
                }
            }

            return regField;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public RegField createRegFielAndFieldLocalizationdByName(String localid, String regFieldTypeUuid, RegRole regRole, boolean commit) throws Exception {
        RegFieldManager regFieldManager = new RegFieldManager(entityManagerRe3gistry2);

        RegField regField = new RegField();
        RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManagerRe3gistry2);
        try {
            regField = regFieldManager.getByLocalid(localid);
        } catch (Exception ex) {
            RegFieldtype regFieldtype = regFieldtypeManager.get(regFieldTypeUuid);
            regField.setUuid(RegFieldUuidHelper.getUuid(localid, regFieldtype));
            regField.setLocalid(localid);
            if (regRole != null) {
                regField.setRegRoleReference(regRole);
            }
            regField.setInsertdate(new Date());

            if (localid.equals("label")) {
                regField.setIstitle(Boolean.TRUE);
            } else {
                regField.setIstitle(Boolean.FALSE);
            }

            /**
             * set status, but maybe will be deleted
             */
            RegStatusManager regStatusManager = new RegStatusManager(entityManagerRe3gistry2);
            regField.setRegStatus(regStatusManager.get("1"));

            try {
                regField.setRegFieldtype(regFieldtype);
                regField.setInsertdate(new Date());
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regField);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
                throw new Exception(ex.getMessage());
            }
            /**
             * create reg_field localization for reg master language
             */
            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
            RegLanguagecode regmasterLanguage = regLanguagecodeManager.getMasterLanguage();
            RegLocalization regLocalizationField = new RegLocalization();
            regLocalizationField.setUuid(RegLocalizationUuidHelper.getUuid(0, regmasterLanguage, null, regField));
            regLocalizationField.setRegField(regField);
            regLocalizationField.setFieldValueIndex(0);
            regLocalizationField.setRegLanguagecode(regmasterLanguage);
            regLocalizationField.setValue(localid);
            regLocalizationField.setHref(null);
            regLocalizationField.setRegAction(null);
            regLocalizationField.setRegRelationReference(null);
            regLocalizationField.setInsertdate(new Date());

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(regLocalizationField);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
                throw new Exception(ex.getMessage());
            }
        }

        return regField;
    }

    public void setAllGroupsAndAllRightsByUser(RegUser user, boolean commit) throws Exception {
        /**
         * give all the groups to the migration user
         */
        RegGroupManager regGroupManager = new RegGroupManager(entityManagerRe3gistry2);
        RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(entityManagerRe3gistry2);
        List<RegGroup> regGroups = regGroupManager.getAll();
        for (RegGroup regGroup : regGroups) {
            RegUserRegGroupMapping regUserRegGroupMapping = new RegUserRegGroupMapping();
            final String uuid = RegUserRegGroupMappingUuidHelper.getUuid(user, regGroup);

            try {
                regUserRegGroupMapping = regUserRegGroupMappingManager.get(uuid);
            } catch (Exception e) {
                regUserRegGroupMapping.setUuid(uuid);
                regUserRegGroupMapping.setRegGroup(regGroup);
                regUserRegGroupMapping.setRegUser(user);
                regUserRegGroupMapping.setInsertdate(new Date());
                regUserRegGroupMapping.setIsGroupadmin(Boolean.TRUE);

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.merge(regUserRegGroupMapping);
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

    public void createAllGroups(boolean commit) throws Exception {
        createGroupByName(BaseConstants.KEY_ROLE_REGISTRYMANAGER, "Registry Manager", commit);
        createGroupByName(BaseConstants.KEY_ROLE_REGISTERMANAGER, "Register Manager", commit);
        createGroupByName(BaseConstants.KEY_ROLE_REGISTEROWNER, "Register Owner", commit);
        createGroupByName(BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION, "Register Submitter", commit);
        createGroupByName(BaseConstants.KEY_ROLE_CONTROLBODY, "Control Body", commit);
    }

    public RegGroup createGroupByName(String groupLocalID, String groupName, boolean commit) throws Exception {
        RegGroup regGroup = new RegGroup();

        try {
            RegGroupManager regGroupManager = new RegGroupManager(entityManagerRe3gistry2);
            regGroup = regGroupManager.getByLocalid(groupLocalID);
        } catch (Exception ex) {
            regGroup.setUuid(RegRoleUuidHelper.getUuid(groupLocalID));
            regGroup.setLocalid(groupLocalID);
            regGroup.setName(groupName);
            regGroup.setInsertdate(new Date());
            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.merge(regGroup);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception exx) {
                logger.error(exx.getMessage());
                throw new Exception(ex.getMessage());
            }
        }

        return regGroup;
    }

    public RegUser createRegUser(String email, String key, boolean commit, boolean enableUser) throws Exception {
        RegUser adminRegUser;
        try {
            RegUserManager regUserManager = new RegUserManager(entityManagerRe3gistry2);
            adminRegUser = regUserManager.findByEmail(email);
        } catch (Exception exx) {
            adminRegUser = new RegUser();

            adminRegUser.setUuid(RegUserUuidHelper.getUuid(email));
            adminRegUser.setSsoreference(email);
            adminRegUser.setName(email);
            adminRegUser.setEmail(email);
            if (enableUser) {
                adminRegUser.setEnabled(Boolean.TRUE);
            } else {
                adminRegUser.setEnabled(Boolean.FALSE);
            }
            adminRegUser.setInsertdate(new Date());

            // Getting properties
            Properties properties = Configuration.getInstance().getProperties();
            String loginType = properties.getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE, BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO);
            if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO) && key != null) {
                UserHelper.generatePassword(adminRegUser, key);
            }

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.persist(adminRegUser);
                if (commit) {
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                adminRegUser = null;
                throw new Exception(ex.getMessage());
            }
        }

        return adminRegUser;
    }

    public void createRegFieldMapping(RegField regField, RegItemclass regItemclass, Integer AUTO_INCREMENT_REG_FIELD_LIST_ORDER, boolean commit) throws Exception {
        try {
            /**
             * regFieldmappingRegisterOwner
             */
            RegFieldmapping regFieldmapping = new RegFieldmapping();

            regFieldmapping.setUuid(RegFieldmappingUuidHelper.getUuid(regField, regItemclass));
            regFieldmapping.setRegField(regField);
            regFieldmapping.setRegItemclass(regItemclass);

            regFieldmapping.setListorder(AUTO_INCREMENT_REG_FIELD_LIST_ORDER);
            regFieldmapping.setTablevisible(Boolean.FALSE);
            regFieldmapping.setRequired(Boolean.TRUE);
            regFieldmapping.setHidden(Boolean.FALSE);
            regFieldmapping.setHashref(Boolean.FALSE);
            regFieldmapping.setMultivalue(Boolean.FALSE);
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
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    public String getRoleIDByReferenceRefType(String referenceRefType) {
        String roleID = null;
        if (referenceRefType.toLowerCase().contains("registrymanager")) {
            roleID = BaseConstants.REGISTRY_MANAGER_ROLE_UUID;
        } else if (referenceRefType.toLowerCase().contains("registermanager")) {
            roleID = BaseConstants.REGISTER_MANAGER_ROLE_UUID;
        } else if (referenceRefType.toLowerCase().contains("registerowner")) {
            roleID = BaseConstants.REGISTER_OWNER_ROLE_UUID;
        } else if (referenceRefType.toLowerCase().contains("registersubmitter")) {
            roleID = BaseConstants.SUBMITTING_ORGANIZATION_ROLE_UUID;
        } else if (referenceRefType.toLowerCase().contains("controlbody")) {
            roleID = BaseConstants.CONTROL_BODY_ROLE_UUID;
        }
        return roleID;
    }
}
