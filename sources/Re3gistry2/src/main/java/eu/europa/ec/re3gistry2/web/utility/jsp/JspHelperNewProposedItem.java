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
package eu.europa.ec.re3gistry2.web.utility.jsp;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationproposedUuidHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.web.controller.RegisterManager;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

// ### Jsp Helper ### 
// This class is providing methods that retrieves objects and format it in 
// the right HTML for the frontend jsps 
public class JspHelperNewProposedItem {

    private JspHelperNewProposedItem() {
    }

    /**
     * This method returns a String with the formatted HTML of the Registry
     * object ready for the front-end JSP
     *
     * @param regItemproposed
     * @param regRelationpredicateRegistry
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param regRelationproposedManager
     * @return String representing the HTML for the front-end with the Registry
     * relation
     */
    public static String jspRegistryFieldHandler(RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicateRegistry, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegRelationproposedManager regRelationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String formattedObjectText = "";

        try {
            // Get the "registry" RegRelation
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegistry);
            // There is only one registry per each register/item (getting index 0)
            RegRelationproposed regRelationproposedTmp = tmpRegRelationproposeds.get(0);

            // Getting the label for the registry in the RegRelation.
            List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), currentLanguage);
            if (tmpRegLocalizations.isEmpty()) {
                // Getting the master language in case the curren language is not available
                tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), masterLanguage);
            }

            // There is only one localization for the registry (geting index 0)
            formattedObjectText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationproposedTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        // Returning the HTML string for the Registry
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, formattedObjectText);
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItemproposed
     * @param regRelationpredicateRegister
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param regRelationproposedManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspRegisterFieldHandler(RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicateRegister, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegRelationproposedManager regRelationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String formattedObjectText = "";

        try {
            // Get the "register" RegRelation
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
            // There is only one register per each RegItem (getting index 0)
            RegRelationproposed regRelationproposedTmp = tmpRegRelationproposeds.get(0);

            // Getting the label for the register in the RegRelation.
            List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), currentLanguage);
            if (tmpRegLocalizations.isEmpty()) {
                // Getting the master language in case the curren language is not available
                tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), masterLanguage);
            }

            // There is only one localization for the registry (geting index 0)
            formattedObjectText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationproposedTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

        } catch (Exception e) {
           java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, formattedObjectText);
    }

    /**
     * This method returns a String with the formatted HTML of the Registry
     * object ready for the front-end JSP
     *
     * @param regFieldmapping
     * @param regItemproposed
     * @param regItemproposedRegGroupRegRoleMappingManager
     * @param systemLocalization
     * @return String representing the HTML for the front-end with the Registry
     * relation
     */
    public static String jspGroupFieldHandler(RegFieldmapping regFieldmapping, RegItemproposed regItemproposed, ResourceBundle systemLocalization, RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager) {

        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Getting the list of groups for that Item and that field (the group role is associated to the field)
            List<RegItemproposedRegGroupRegRoleMapping> tmpRegItemproposedRegGroupRegRoleMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemproposed, regFieldmapping.getRegField().getRegRoleReference());

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Processing new proposed value -- */
            List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappings = regItemproposedRegGroupRegRoleMappingManager.getAllNew(regItemproposed, regFieldmapping.getRegField().getRegRoleReference());
            for (RegItemproposedRegGroupRegRoleMapping tmpRegIteproposedRegGroupRegRoleMapping : regItemproposedRegGroupRegRoleMappings) {
                // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                tmp += handleRegGroupproposedOutput(tmpRegIteproposedRegGroupRegRoleMapping, regFieldmapping);
                tmpRegItemproposedRegGroupRegRoleMappings.add(tmpRegIteproposedRegGroupRegRoleMapping);
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no group available (both in current and proposed)
            if (tmpRegItemproposedRegGroupRegRoleMappings.isEmpty()) {
                tmp += systemLocalization.getString("label.nogroup");
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Collection
     * object ready for the front-end JSP
     *
     * @param regRelationproposedManager
     * @param regItemproposed
     * @param regRelationpredicateCollection
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param localization
     * @return String representing the HTML for the front-end with the
     * Collection relation
     */
    public static String jspCollectionFieldHandler(RegRelationproposedManager regRelationproposedManager, RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicateCollection, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String outText = "";

        try {
            // Get the "collection" reg relation (only one collection per each item)
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateCollection);
            // Some RegItem may not have a collection
            if (!tmpRegRelationproposeds.isEmpty()) {
                RegRelationproposed regRelationproposedTmp = tmpRegRelationproposeds.get(0);

                // Getting the label for the collection in the reg relation.
                List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), currentLanguage);
                if (tmpRegLocalizations.isEmpty()) {
                    tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationproposedTmp.getRegItemObject(), masterLanguage);
                }
                // There is only one localization for the collectrion
                outText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationproposedTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

            } else {
                outText = localization.getString("label.nocollection");
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        // Returning the HTML string for the Collection
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, outText);
    }

    /**
     * This method returns a String with the formatted HTML of the Predecessor
     * object(s) ready for the front-end JSP
     *
     * @param regItemproposed
     * @param regRelationpredicatePredecessor
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param localization
     * @param regRelationproposedManager
     * @param regFieldmapping
     * @param regLocalizationproposedManager
     * @return String representing the HTML for the front-end with the
     * Predecessor relation(s)
     */
    public static String jspPredecessorFieldHandler(RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicatePredecessor, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegRelationproposedManager regRelationproposedManager, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Get the "parent" RegRelation of the current item
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicatePredecessor);

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Process the RegRelationproposed available in the system: the information for each localization -- */
            for (RegRelationproposed regRelationproposed : tmpRegRelationproposeds) {
                // Handling the the RegRelation parent
                tmp += handleRegLocalizationRelationGeneric(regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, false, false);
            }

            /* -- Processing any eventual new proposed value -- */
            List<RegRelationproposed> regRelationproposeds;
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because the relations are stored
                // only in the MasterLanguage
                regRelationproposeds = regRelationproposedManager.getAllNew(regItemproposed, regRelationpredicatePredecessor);
                for (RegRelationproposed regRelationproposed : regRelationproposeds) {

                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, false, false);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no predecessor available (both in current and proposed)
            if (tmpRegRelationproposeds.isEmpty()) {
                tmp += localization.getString("label.nopredecessor");
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Successor
     * object(s) ready for the front-end JSP
     *
     * @param regItemproposed
     * @param regRelationpredicateSuccessor
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param localization
     * @param regRelationproposedManager
     * @param regFieldmapping
     * @param regLocalizationproposedManager
     * @return String representing the HTML for the front-end with the Successor
     * relation(s)
     */
    public static String jspSuccessorFieldHelper(RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicateSuccessor, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegRelationproposedManager regRelationproposedManager, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Get the "parent" RegRelation of the current item
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateSuccessor);

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Processing new proposed value -- */
            List<RegRelationproposed> regRelationproposeds;
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because the relations are stored
                // only in the MasterLanguage
                regRelationproposeds = regRelationproposedManager.getAllNew(regItemproposed, regRelationpredicateSuccessor);
                for (RegRelationproposed regRelationproposed : regRelationproposeds) {

                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, false, false);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no predecessor available (both in current and proposed)
            if (tmpRegRelationproposeds.isEmpty()) {
                tmp += localization.getString("label.nosuccessor");
            }
        } catch (Exception e) {
           java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Parent
     * object(s) ready for the front-end JSP
     *
     * @param regItemproposed
     * @param regRelationpredicateParent
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param localization
     * @param regRelationproposedManager
     * @param regFieldmapping
     * @param regLocalizationproposedManager
     * @param currentUser
     * @param currentUserGroupsMap
     * @param itemMappings
     * @return String representing the HTML for the front-end with the Parent
     * relation(s)
     */
    public static String jspParentFieldHandler(RegItemproposed regItemproposed, RegRelationpredicate regRelationpredicateParent, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegFieldmapping regFieldmapping, RegRelationproposedManager regRelationproposedManager, RegLocalizationproposedManager regLocalizationproposedManager, RegUser currentUser, Map<String, RegGroup> currentUserGroupsMap, List<RegItemRegGroupRegRoleMapping> itemMappings) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
        boolean permissionRegisterRegistry = UserHelper.checkRegItemAction(actionRegisterRegistry, currentUserGroupsMap, itemMappings);

        boolean canWrite = JspCommon.canWrite(regItemproposed, currentUser);

        // Disabling the input if the current user has no rights on the RegItem/RegItemproposed
        boolean canAdd = ((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite);

        try {
            // Get the "parent" RegRelation of the current item
            List<RegRelationproposed> tmpRegRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateParent);

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list data-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Processing new proposed value -- */
            List<RegRelationproposed> regRelationproposeds;
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because the relations are stored
                // only in the MasterLanguage
                regRelationproposeds = regRelationproposedManager.getAllNew(regItemproposed, regRelationpredicateParent);
                for (RegRelationproposed regRelationproposed : regRelationproposeds) {

                    // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, true, canAdd);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no parent available (both in current and proposed)
            if (tmpRegRelationproposeds.isEmpty()) {
                tmp += localization.getString("label.noparent");
            }

            // Handling the add button (if the user has the rights)
            if (canAdd) {
                if (!tmpRegRelationproposeds.isEmpty()) {
                    if ((regFieldmapping.getMultivalue() || (tmpRegRelationproposeds.isEmpty())) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-parent-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItemproposed.getUuid() + "\">" + localization.getString("label.add") + "</a>";
                    }
                } else {
                    if ((regFieldmapping.getMultivalue() || tmpRegRelationproposeds.isEmpty()) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-parent-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItemproposed.getUuid() + "\">" + localization.getString("label.add") + "</a>";
                    }
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the simple value
     * field or objectRelation
     *
     * @param regItemproposed
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @param localization
     * @param regFieldmapping
     * @param regLocalizationproposedManager
     * @param systemLocalization
     * @param currentUser
     * @param currentUserGroupsMap
     * @param itemMappings
     * @param itemProposedMappings
     * @return String representing the HTML for the simple value field or the
     * Object relation(s)
     */
    public static String jspNormalValueHandler(RegLocalizationManager regLocalizationManager, RegFieldmapping regFieldmapping, RegItemproposed regItemproposed, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle systemLocalization, RegField titleRegField, RegLocalizationproposedManager regLocalizationproposedManager, ResourceBundle localization, RegUser currentUser, Map<String, RegGroup> currentUserGroupsMap, List<RegItemRegGroupRegRoleMapping> itemMappings, List<RegItemproposedRegGroupRegRoleMapping> itemProposedMappings) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};

        boolean permissionItemRegisterRegistry = false;
        boolean permissionRegisterRegistry = false;
        if (itemMappings != null) {
            permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
            permissionRegisterRegistry = UserHelper.checkRegItemAction(actionRegisterRegistry, currentUserGroupsMap, itemMappings);
        } else {
            permissionItemRegisterRegistry = UserHelper.checkRegItemproposedAction(actionItemRegisterRegistry, currentUserGroupsMap, itemProposedMappings);
            permissionRegisterRegistry = UserHelper.checkRegItemproposedAction(actionRegisterRegistry, currentUserGroupsMap, itemProposedMappings);
        }

        boolean canWrite = JspCommon.canWrite(regItemproposed, currentUser);

        // Disabling the input if the current user has no rights on the RegItem/RegItemproposed
        String inputRights = ((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite) ? "" : " disabled=\"disabled\"";
        boolean canAdd = ((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite);

        try {
            // Initializing the reg Localization in the Master language. If the current
            // language is different, it will be loaded in the specific field method (handleRegLocalizationSimpleValues)
            List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regFieldmapping.getRegField(), regItemproposed, masterLanguage);

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + ((!regLocalizationproposeds.isEmpty() && regLocalizationproposeds.get(0).getValue() == null) ? " data-list" : "") + "\">";
            }

            /* -- Get any new proposed value -- */
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because if the current language is 
                // different, it will be loaded in the specific field method (handleRegLocalizationproposedSimpleValues)
                regLocalizationproposeds = regLocalizationproposedManager.getAllNew(regFieldmapping.getRegField(), regItemproposed, masterLanguage);
                for (RegLocalizationproposed regLocalizationproposed : regLocalizationproposeds) {
                    // Handling the the simple value(s) of the RegLocalization
                    if (regLocalizationproposed.getValue() != null) {

                        tmp += handleRegLocalizationproposedSimpleValues(regLocalizationproposed, regLocalizationproposedManager, systemLocalization, regFieldmapping, currentLanguage, masterLanguage, inputRights, canAdd);

                    } // Managing the relation to another item
                    else if (regLocalizationproposed.getRegRelationproposedReference() != null) {

                        tmp += handleRegLocalizationproposedRelationReferences(regLocalizationproposed, systemLocalization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, canAdd);
                    } // The relation reference is stored in the localization as EN language.
                    // In case the language of the current localization is not EN and the
                    // localization  has no simple value, the followig check will get the RegRelation regerence            
                    else {

                        // Getting the RegItem localization for the current field
                        List<RegLocalizationproposed> checkRegLocalizationproposeds = regLocalizationproposedManager.getAll(regFieldmapping.getRegField(), regItemproposed, masterLanguage);

                        for (RegLocalizationproposed regLocalizationproposedCheck : checkRegLocalizationproposeds) {

                            // Checking if this is a relation reference
                            if (regLocalizationproposedCheck.getRegRelationproposedReference() != null) {

                                tmp += handleRegLocalizationRelationReferences(regLocalizationproposedCheck, regItemproposed, systemLocalization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, canAdd);
                            }
                        }
                    }
                    // Index of each field (needed for multiple values fields)
                }
            } catch (NoResultException e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            /* -- Managing the "no value" cases -- */
            // Case with values but no proposed value
            if ((regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty())) {
                String addType = ((regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)) ? "btn-relation-add" : "btn-value-add");
                // Checking if the add button have to be visible
                if (regFieldmapping.getMultivalue() && currentLanguage.getMasterlanguage() && canAdd) {
                    tmp += "<a href=\"\" class=\"btn btn-xs btn-success pull-right " + addType + "\" data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\" data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItemproposed.getUuid() + "\">" + systemLocalization.getString("label.add") + "</a>";
                }
            } else // Cases with no values
            {
                // Relation field case
                if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION) && currentLanguage.getMasterlanguage()) {

                    // Creating the main div for the field
                    tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : "") + "\" >";

                    tmp += "<div class=\"row\">";
                    tmp += "<div class=\"col-sm-12\">";

                    // There is only one localization for the registry
                    tmp += systemLocalization.getString("label.referencenoavailable");

                    tmp += "</div>";
                    tmp += "</div>";
                    tmp += "</div>";

                    tmp += "<hr/>";

                    // Showing the add button in case of adding rights
                    if (canAdd) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-relation-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + ((regItemproposed != null) ? regItemproposed.getUuid() : "") + "\">" + systemLocalization.getString("label.add") + "</a>";
                    }
                } else {
                    // Value field case

                    if (regFieldmapping.getRequired()) {
                        tmp += "<div class=\"input-group\">";
                    }

                    String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + "0" + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY;

                    // Showing the empty input
                    if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                        tmp += "<textarea " + inputRights + "class=\"form-control\" " + inputRights + "rows=\"4\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + "0" + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + "></textarea>";
                    } else {
                        tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" " + inputRights + "class=\"form-control\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + "0" + "\" value=\"\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";
                    }

                    if (regFieldmapping.getRequired()) {
                        tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + systemLocalization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                        tmp += "</div>";
                    }
                }
            }
        } catch (Exception e) {
           java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * simple value field (RegLocalizationproposed)
     */
    private static String handleRegLocalizationproposedSimpleValues(RegLocalizationproposed regLocalizationproposed, RegLocalizationproposedManager regLocalizationproposedManager, ResourceBundle localization, RegFieldmapping regFieldmapping, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, String inputRights, boolean canAdd) throws Exception {
        String tmp = "";
        Configuration configuration = Configuration.getInstance();

        // The regLocalizationproposed passed by paramete is always in the master language
        // Getting regLocalizationproposed in the eventual other language
        // If the regField is of type date or number, it is stored only in the master language
        RegLocalizationproposed currentLanguageLocalizationproposed = null;
        if (!currentLanguage.getMasterlanguage()) {
            if (!regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE) && !regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER)) {
                try {
                    currentLanguageLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalizationproposed, regLocalizationproposedManager, currentLanguage);
                } catch (NoResultException e) {
                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
                }
            } else {
                currentLanguageLocalizationproposed = regLocalizationproposed;
            }
        } else {
            currentLanguageLocalizationproposed = regLocalizationproposed;
        }

        /* -- Calculating field values and names -- */
        // Getting value
        String inputValue = ((currentLanguageLocalizationproposed != null)
                ? ((currentLanguageLocalizationproposed.getValue() != null && currentLanguageLocalizationproposed.getValue().length() > 0)
                ? currentLanguageLocalizationproposed.getValue() : "")
                : "");
        
        if (inputValue.contains("\"")) {
                inputValue = inputValue.replace("\"", "&quot;");
                }

        // Calculating if it is a new or updated content
        String newContentClass = "";

        // Getting input name
        String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getFieldValueIndex() : regLocalizationproposed.getFieldValueIndex()) + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : "");

        // Getting the href
        String inputHref = ((currentLanguageLocalizationproposed != null)
                ? ((currentLanguageLocalizationproposed.getHref() != null && currentLanguageLocalizationproposed.getHref().length() > 0)
                ? currentLanguageLocalizationproposed.getHref() : "")
                : "");

        //Getting the href input name
        String inputNameHref = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getFieldValueIndex() : regLocalizationproposed.getFieldValueIndex()) + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;

        // Getting fieldValueIndex
        int fieldValueIndex = ((currentLanguageLocalizationproposed != null)
                ? ((currentLanguageLocalizationproposed.getFieldValueIndex() > -1)
                ? currentLanguageLocalizationproposed.getFieldValueIndex() : 0)
                : regLocalizationproposed.getFieldValueIndex());

        /* -- */
 /* -- Generating the page -- */
        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        // Checking if the simple value has also a link
        if (regFieldmapping.getHashref()) {

            tmp += "<div class=\"row\">";

            // Showing the lable text
            tmp += "<div class=\"col-sm-3\">" + localization.getString("label.value") + ":</div>";
            tmp += "<div class=\"col-sm-9\">";

            // Handling the required notice (asterisc icon)
            if (regFieldmapping.getRequired() || regFieldmapping.getMultivalue()) {
                tmp += "<div class=\"form-group\">";
                tmp += "<div class=\"input-group\">";
            } else {
                tmp += "<div class=\"input-cnt\">";
            }

            // Displaying the delete button in case of multivalued field
            // To delete a value the language shall be the master language and the user shall have the rights
            if (regFieldmapping.getMultivalue() && currentLanguage.getMasterlanguage() && canAdd) {
                tmp += "<span class=\"input-group-btn\">";
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "")) + "\"><i class=\"far fa-trash-alt\"></i></a>";
                tmp += "</span>";
            }

            // -- Showing the value --            
            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" value=\"" + inputValue + "\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" name=\"" + inputName + "\" " + ((regFieldmapping.getRequired()) ? "required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                tmp += "</div>";
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            } else if (regFieldmapping.getMultivalue()) {
                tmp += "</div>";
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            } else {
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>:" + ((regLocalizationproposed != null) ? regLocalizationproposed.getValue() : "") + "</p>";
            }

            tmp += "</div>";
            tmp += "</div>";

            // ----
            tmp += "<div class=\"row\">";

            // Showing the link field href label
            tmp += "<div class=\"col-sm-3\">" + localization.getString("label.href") + ":</div>";
            tmp += "<div class=\"col-sm-9\">";

            // -- Showing the href -- 
            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control href-control\" value=\"" + inputHref + "\" name=\"" + inputNameHref + "\" maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

            // In case the language is not the master language, showing the
            // master language translation (href could point to different languages)
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong >" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>: " + ((regLocalizationproposed != null) ? regLocalizationproposed.getHref() : "") + "</p>";
            }

            tmp += "</div>";
            tmp += "</div>";

        } // If the localization has no href, showing just the value
        else {

            tmp += "<div class=\"row\">";
            tmp += "<div class=\"col-sm-12\">";

            // Handling the required notice (asterisc icon)
            if (regFieldmapping.getRequired() || regFieldmapping.getMultivalue()) {
                tmp += "<div class=\"form-group\">";
                tmp += "<div class=\"input-group\">";
            } else {
                tmp += "<div class=\"input-cnt\">";
            }

            // Displaying the delete button in case of multivalued field
            // To delete a value the language shall be the master language and the user shall have the rights
            if (regFieldmapping.getMultivalue() && currentLanguage.getMasterlanguage() && canAdd) {
                tmp += "<span class=\"input-group-btn\">";
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "")) + "\"><i class=\"far fa-trash-alt\"></i></a>";
                tmp += "</span>";
            }

            // -- Showing the value -- 
            if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                tmp += "<textarea " + inputRights + "class=\"form-control\" rows=\"4\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + ">" + inputValue + "</textarea>";
            } else {
                tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" value=\"" + inputValue + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";
            }

            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                tmp += "</div>";
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            } else if (regFieldmapping.getMultivalue()) {
                tmp += "</div>";
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            } else {
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += "</div>";
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>: " + ((regLocalizationproposed != null) ? regLocalizationproposed.getValue() : "") + "</p>";
            }

            tmp += "</div>";
            tmp += "</div>";

        }

        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation reference field (RegLocalization)
     */
    private static String handleRegLocalizationRelationReferences(RegLocalizationproposed regLocalizationproposed, RegItemproposed regItemproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation reference and the related item object
        RegRelationproposed regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();
        RegItem regItemObject = regRelationproposed.getRegItemObject();

        // Getting the RegLocalizationproposed from the reference to the current
        // RegLocalization if available and the related RegItemObject
        RegItem regItemObjectFromProposedRelation = null;
        RegItemproposed regItemproposedObjectFromProposedRelation = null;
        try {
            regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();
            if (regRelationproposed != null && regRelationproposed.getRegItemObject() != null) {
                regItemObjectFromProposedRelation = regRelationproposed.getRegItemObject();
            } else if (regRelationproposed != null && regRelationproposed.getRegItemproposedObject() != null) {
                regItemproposedObjectFromProposedRelation = regRelationproposed.getRegItemproposedObject();
            }
        } catch (NoResultException e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue" + ((regItemproposed == null) || (regRelationproposed != null) ? " cnt" : "") + "\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regRelationproposed != null) ? regRelationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        if (regLocalizationproposed != null) {
            if (regLocalizationproposed.getRegRelationproposedReference() != null) {
                tmp += getRelationproposedHtml(regLocalizationproposed, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, true, canAdd);
            } else {
                // Showing the restore relation button (if it is in the master language the user has the rights)
                if (currentLanguage.getMasterlanguage() && canAdd) {
                    tmp += "<a class=\"pull-right btn btn-xs btn-restore-relation\" data-toggle=\"tooltip\" data-placement=\"top\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONUUID + "=\"" + regLocalizationproposed.getRegLocalizationReference().getUuid() + "\" title=\"" + localization.getString("label.restoreoriginalrelation") + "\"><i class=\"fa fa-undo\" aria-hidden=\"true\"></i></a>";
                }
            }
        } else {
            tmp += getRelationHtml(regRelationproposed, regItemObject, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, (regRelationproposed != null && (regLocalizationproposed != null && regLocalizationproposed.getRegRelationproposedReference() != null)), true, canAdd);
        }

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation reference field (RegLocalization)
     */
    private static String handleRegLocalizationproposedRelationReferences(RegLocalizationproposed regLocalizationproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation reference and the related item object
        RegRelationproposed regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();
        RegItem regItemObject = regRelationproposed.getRegItemObject();

        RegItem regItemObjectFromProposedRelation = null;
        RegItemproposed regItemproposedObjectFromProposedRelation = null;
        try {
            regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();
            if (regRelationproposed != null && regRelationproposed.getRegItemObject() != null) {
                regItemObjectFromProposedRelation = regRelationproposed.getRegItemObject();
            } else if (regRelationproposed != null && regRelationproposed.getRegItemproposedObject() != null) {
                regItemproposedObjectFromProposedRelation = regRelationproposed.getRegItemproposedObject();
            }
        } catch (NoResultException e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue cnt\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regRelationproposed != null) ? regRelationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        tmp += getRelationproposedHtml(regLocalizationproposed, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, true, canAdd);

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation generic field (RegLocalization)
     */
    private static String handleRegLocalizationRelationGeneric(RegRelationproposed regRelationproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canEdit, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation item object
        RegItem regItemObject = regRelationproposed.getRegItemObject();

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue cnt\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + regRelationproposed.getUuid() + "\"" + newContentClass + " >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        // Setting the right output handler
        tmp += getRelationproposedHtml(null, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, canEdit, canAdd);

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation generic field (RegLocalizationproposed)
     */
    private static String handleRegLocalizationproposedRelationGeneric(RegRelationproposed regRelationproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canEdit, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation item object
        RegItem regItemObject = regRelationproposed.getRegItemObject();

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue cnt\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + regRelationproposed.getUuid() + "\"" + newContentClass + " >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        tmp += getRelationproposedHtml(null, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, canEdit, canAdd);

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the Group
     * (proposed)
     */
    private static String handleRegGroupproposedOutput(RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping, RegFieldmapping regFieldmapping) {

        String tmp = "";

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposedRegGroupRegRoleMapping != null) ? regItemproposedRegGroupRegRoleMapping.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        if (regItemproposedRegGroupRegRoleMapping != null) {
            RegGroup tmpGroupproposed = regItemproposedRegGroupRegRoleMapping.getRegGroup();
            tmp += "<a data-toggle=\"tooltip\" title=\"" + tmpGroupproposed.getEmail() + " - " + tmpGroupproposed.getWebsite() + "\">" + tmpGroupproposed.getName() + "</a>";
        }

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This method will return the type of the field
     *
     * @param regFieldMapping
     * @param localization
     * @return
     */
    public static String handleFieldType(RegFieldmapping regFieldMapping, ResourceBundle localization) {

        switch (regFieldMapping.getRegField().getRegFieldtype().getLocalid()) {
            case BaseConstants.KEY_FIELD_TYPE_STRING:
                return BaseConstants.KEY_FIELD_TYPE_STRING;
            case BaseConstants.KEY_FIELD_TYPE_NUMBER:
                return "text\" pattern=\"\\d+\" data-validate=\"true\" data-error=\"" + localization.getString("label.patternerror.number");
            case BaseConstants.KEY_FIELD_TYPE_DATE:
                return BaseConstants.KEY_FIELD_TYPE_STRING + "\" data-date-format=\"dd-mm-yyyy\" class=\"form-control date\" data-error=\"" + localization.getString("label.patternerror.date");
            default:
                break;
        }

        return null;
    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static RegLocalizationproposed getRegLocalizationproposedInCurrentLanguage(RegLocalizationproposed regLocalizationproposed, RegLocalizationproposedManager regLocalizationproposedManager, RegLanguagecode currentLanguagecode) throws Exception {

        String tmpUuid = RegLocalizationproposedUuidHelper.getUuid(regLocalizationproposed.getFieldValueIndex(),
                currentLanguagecode,
                regLocalizationproposed.getRegItemproposed(),
                regLocalizationproposed.getRegField()
        );

        RegLocalizationproposed out = null;
        try {
            out = regLocalizationproposedManager.get(tmpUuid);
        } catch (NoResultException e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        return out;
    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static String getRelationHtml(RegRelationproposed regRelationproposed, RegItem regItemObject, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, boolean regRelationProposedavailable, boolean canEdit, boolean canAdd) throws Exception {
        // Getting the localization for the relation object
        List<RegLocalization> regLocalizationTmps;
        try {
            // Checking the localizations for the current relation in the current language
            regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), currentLanguage);
            if (regLocalizationTmps.isEmpty()) {
                // If not available in the current language, checking the localizations for the current relation in the master language
                regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), masterLanguage);
            }
        } catch (NoResultException e) {
            // If not available in the current language, checking the localizations for the current relation in the master language
            regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), masterLanguage);
        }

        // Showing the related object label and link                   
        // The uuid passed to the delete button is the one of the original RegLocalization
        // (from the initial item and not from the related item (regLocalizationTmps refers
        // to the related item)
        return ((currentLanguage.getMasterlanguage() && !regRelationProposedavailable && canEdit && canAdd) ? "<a href=\"#\" title=\"" + localization.getString("label.remove") + "\" class=\"btn btn-xs btn-danger btn-relation-remove\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + (regRelationproposed.getUuid()) + "\"><i class=\"far fa-trash-alt\"></i></a>&nbsp;" : "") + "<a href=\"" + WebConstants.PAGE_PATH_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemObject.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + regLocalizationTmps.get(0).getValue() + "</a>";

    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static String getRelationproposedHtml(RegLocalizationproposed regLocalizationproposedOriginal, RegRelationproposed regRelationproposed, RegItem regItemObject, RegLocalizationManager regLocalizationManager, RegLocalizationproposedManager regLocalizationproposedManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, boolean canEdit, boolean canAdd) throws Exception {

        List<RegLocalizationproposed> regLocalizationproposedTmps = null;
        List<RegLocalization> regLocalizationTmps = null;

        // Check if the relation is pointing to a RegItem or to a RegItemproposed
        if (regRelationproposed != null && regRelationproposed.getRegItemproposedObject() != null) {
            // Handling the case of RegRelationproposed
            try {
                // Checking the localizations for the current RegRelationproposed in the current language
                regLocalizationproposedTmps = regLocalizationproposedManager.getAll(titleRegField, regRelationproposed.getRegItemproposedObject(), currentLanguage);
                if (regLocalizationproposedTmps.isEmpty()) {
                    // If not available in the current language, checking the localizations for the current RegRelationproposed in the master language
                    regLocalizationproposedTmps = regLocalizationproposedManager.getAll(titleRegField, regRelationproposed.getRegItemproposedObject(), masterLanguage);
                }
            } catch (NoResultException e) {
                // If not available in the current language, checking the localizations for the current RegRelationproposed in the master language
                regLocalizationproposedTmps = regLocalizationproposedManager.getAll(titleRegField, regRelationproposed.getRegItemproposedObject(), masterLanguage);
            }
        } else {
            // Handling the case of RegRelation
            if (regRelationproposed != null) {
                try {
                    // Checking the localizations for the current RegRrelation in the current language
                    regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), currentLanguage);
                    if (regLocalizationTmps.isEmpty()) {
                        // If not available in the current language, checking the localizations for the current RegRrelation in the master language
                        regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), masterLanguage);
                    }
                } catch (NoResultException e) {
                    // If not available in the current language, checking the localizations for the current RegRelation in the master language
                    regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelationproposed.getRegItemObject(), masterLanguage);
                }
            }
        }

        // Showing the related object label and link                   
        // The uuid passed to the delete button is the one of the original RegLocalization
        // (from the initial item and not from the related item (regLocalizationTmps refers
        // to the related item)                   
        return ((currentLanguage.getMasterlanguage() && canEdit && canAdd) ? "<a href=\"#\" title=\"" + localization.getString("label.remove") + "\" class=\"btn btn-xs btn-danger btn-relation-remove\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((regLocalizationproposedOriginal != null) ? regLocalizationproposedOriginal.getUuid() : ((regRelationproposed != null) ? regRelationproposed.getUuid() : "")) + "\"><i class=\"far fa-trash-alt\"></i></a>&nbsp;" : "") + "<a href=\"" + WebConstants.PAGE_PATH_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemObject.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + ((regLocalizationproposedTmps != null && !regLocalizationproposedTmps.isEmpty()) ? regLocalizationproposedTmps.get(0).getValue() : ((regLocalizationTmps != null && !regLocalizationTmps.isEmpty()) ? regLocalizationTmps.get(0).getValue() : "")) + "</a>";

    }

    /**
     * This private method the HTML string representing the RegRelation
     */
    private static String getRelationObjectsHTML(RegItemproposed regItemproposed, String formattedObjectTextProposed) {
        // Preparing the output string
        String tmp = "";

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : ((regItemproposed != null) ? regItemproposed.getUuid() : "")) + "\" >";

        tmp += "<div class=\"row\">";
        tmp += "<div class=\"col-sm-12\">";

        // Setting the formatted object text
        tmp += formattedObjectTextProposed;

        tmp += "</div>";
        tmp += "</div>";
        tmp += "</div>";

        return tmp;
    }

    public static String getRegStatusHTML(String regStatusLocalId, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage) throws Exception {

        String tmp = "";

        // Getting the requested RegStatus
        RegStatus regStatus = regStatusManager.findByLocalid(regStatusLocalId);
        RegStatusgroup regStatusgroup = regStatus.getRegStatusgroup();

        //Getting the RegStatus localization
        RegStatuslocalization regStatusLocalization;
        try {
            regStatusLocalization = regStatuslocalizationManager.get(regStatus, currentLanguage);
        } catch (NoResultException e) {
            regStatusLocalization = regStatuslocalizationManager.get(regStatus, masterLanguage);
        }

        String statusURI = regStatusgroup.getBaseuri()+ "/" + regStatusgroup.getLocalid() + "/" + regStatus.getLocalid();
        tmp += "<a data-uri=\"/" + statusURI + "\" href=\"" + statusURI + "\">" + regStatusLocalization.getLabel() + "</a>";

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItemproposed
     * @param currentLanguage
     * @param masterLanguage
     * @param regStatusManager
     * @param regStatuslocalizationManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspDateCreationHandler(RegItemproposed regItemproposed, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String dateFormat = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String tmp = "";

        try {

            // Creating the main div for the field
            tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : null) + "\" >";

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

            // Setting the formatted object text            
            String dataStr = "";
            if (regItemproposed != null) {
                dataStr = sdf.format(regItemproposed.getInsertdate());
            }
            tmp += dataStr;

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItemproposed
     * @param currentLanguage
     * @param masterLanguage
     * @param regStatusManager
     * @param regStatuslocalizationManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspDateEditHandler(RegItemproposed regItemproposed, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String dateFormat = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String tmp = "";

        try {

            // Creating the main div for the field
            tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : null) + "\" >";

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

            // Setting the formatted object text            
            if (regItemproposed != null && regItemproposed.getEditdate() != null) {
                String dataStr = sdf.format(regItemproposed.getEditdate());
                tmp += dataStr;
            } else {
                tmp += "-";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within jspHelperNewProposedItem class. Please check the details: " + e.getMessage(), e.getMessage());
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return tmp;
    }
}
