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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
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
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationproposedUuidHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

// ### Jsp Helper ### 
// This class is providing methods that retrieves objects and format it in 
// the right HTML for the frontend jsps 
public class JspHelper {

    private JspHelper() {
    }

    /**
     * This method returns a String with the formatted HTML of the Registry
     * object ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
     * @param regItemproposed
     * @param regRelationpredicateRegistry
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @return String representing the HTML for the front-end with the Registry
     * relation
     */
    public static String jspRegistryFieldHandler(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicateRegistry, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String formattedObjectText = "";

        try {
            // Get the "registry" RegRelation
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
            // There is only one registry per each register/item (getting index 0)
            RegRelation regRelationTmp = tmpRegRelations.get(0);

            // Getting the label for the registry in the RegRelation.
            List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), currentLanguage);
            if (tmpRegLocalizations.isEmpty()) {
                // Getting the master language in case the curren language is not available
                tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), masterLanguage);
            }

            // There is only one localization for the registry (geting index 0)
            formattedObjectText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

        } catch (Exception e) {
            logger.error("@ JspHelper.jspRegistryFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Registry
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, regLocalizationproposeds, regItem, formattedObjectText, formattedObjectText);
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
     * @param regItemproposed
     * @param regRelationpredicateRegister
     * @param regLocalizationManager
     * @param titleRegField
     * @param currentLanguage
     * @param masterLanguage
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspRegisterFieldHandler(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicateRegister, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String formattedObjectText = "";

        try {
            // Get the "register" RegRelation
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicateRegister);
            // There is only one register per each RegItem (getting index 0)
            RegRelation regRelationTmp = tmpRegRelations.get(0);

            // Getting the label for the register in the RegRelation.
            List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), currentLanguage);
            if (tmpRegLocalizations.isEmpty()) {
                // Getting the master language in case the curren language is not available
                tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), masterLanguage);
            }

            // There is only one localization for the registry (geting index 0)
            formattedObjectText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

        } catch (Exception e) {
            logger.error("@ JspHelper.jspRegisterFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, regLocalizationproposeds, regItem, formattedObjectText, formattedObjectText);
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItem
     * @param regItemproposed
     * @param currentLanguage
     * @param masterLanguage
     * @param regStatusManager
     * @param regStatuslocalizationManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspStatusFieldHandler(RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Showing 2 columns if the proposal is available
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
            }

            // Creating the main div for the field
            tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : regItem.getUuid()) + "\" >";

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

            String regStatusLocalid = (regItemproposed != null) ? regItemproposed.getRegStatus().getLocalid() : regItem.getRegStatus().getLocalid();
            // Setting the formatted object text            
            tmp += getRegStatusHTML(regStatusLocalid, regStatusManager, regStatuslocalizationManager, currentLanguage, masterLanguage);

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            // In case of available proposal, showing the column of the current value
            // In the case of the registry it is always the same because it cannot be changed
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

                // Setting the formatted object text
                tmp += getRegStatusHTML(regItem.getRegStatus().getLocalid(), regStatusManager, regStatuslocalizationManager, currentLanguage, masterLanguage);

                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspRegisterFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItem
     * @param regItemproposed
     * @param regLocalizationproposeds
     * @param currentLanguage
     * @param masterLanguage
     * @param regStatusManager
     * @param regStatuslocalizationManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspDateCreationHandler(RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String dateFormat = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String tmp = "";

        try {
            // Showing 2 columns if the proposal is available
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
            }

            // Creating the main div for the field
            tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : regItem.getUuid()) + "\" >";

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

            // Setting the formatted object text            
            String dataStr = sdf.format(regItem.getInsertdate());
            tmp += dataStr;

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            // In case of available proposal, showing the column of the current value
            // In the case of the registry it is always the same because it cannot be changed
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

                // Setting the formatted object text
                String itemProposedDataStr = sdf.format(regItemproposed.getInsertdate());
                tmp += itemProposedDataStr;

                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspRegisterFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Register
     * object ready for the front-end JSP
     *
     * @param regItem
     * @param regItemproposed
     * @param regLocalizationproposeds
     * @param currentLanguage
     * @param masterLanguage
     * @param regStatusManager
     * @param regStatuslocalizationManager
     * @return String representing the HTML for the front-end with the Register
     * relation
     */
    public static String jspDateEditHandler(RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegStatusManager regStatusManager, RegStatuslocalizationManager regStatuslocalizationManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String dateFormat = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String tmp = "";

        try {
            // Showing 2 columns if the proposal is available
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
            }

            // Creating the main div for the field
            tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : regItem.getUuid()) + "\" >";

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

            // Setting the formatted object text            
            if (regItem.getEditdate() != null) {
                String dataStr = sdf.format(regItem.getEditdate());
                tmp += dataStr;
            } else {
                tmp += "-";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            // In case of available proposal, showing the column of the current value
            // In the case of the registry it is always the same because it cannot be changed
            if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

                // Setting the formatted object text
                if (regItemproposed.getEditdate() != null) {
                    String itemProposedDataStr = sdf.format(regItemproposed.getEditdate());
                    tmp += itemProposedDataStr;
                } else {
                    tmp += "-";
                }

                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspRegisterFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Register
        // reference of the  RegItem
        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Registry
     * object ready for the front-end JSP
     *
     * @param regFieldmapping
     * @param regItem
     * @param regItemproposed
     * @param regItemRegGroupRegRoleMappingManager
     * @param regItemproposedRegGroupRegRoleMappingManager
     * @param systemLocalization
     * @return String representing the HTML for the front-end with the Registry
     * relation
     */
    public static String jspGroupFieldHandler(RegFieldmapping regFieldmapping, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager, RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager, ResourceBundle systemLocalization) {

        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Getting the list of groups for that Item and that field (the group role is associated to the field)
            List<RegItemRegGroupRegRoleMapping> tmpRegItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regItem, regFieldmapping.getRegField().getRegRoleReference());
            List<RegItemproposedRegGroupRegRoleMapping> tmpRegItemproposedRegGroupRegRoleMappings = new ArrayList<>();

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Process the RegItemdRegGroupRegRoleMapping available in the system -- */
            for (RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping : tmpRegItemRegGroupRegRoleMappings) {
                RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = null;
                try {
                    // Getting the eventual RegItemproposedRegGroupRegRoleMapping                 
                    regItemproposedRegGroupRegRoleMapping = regItemproposedRegGroupRegRoleMappingManager.getByReference(regItemRegGroupRegRoleMapping);
                    tmpRegItemproposedRegGroupRegRoleMappings.add(regItemproposedRegGroupRegRoleMapping);

                } catch (NoResultException e) {
                }

                // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                tmp += handleRegGroupOutput(regItemproposed, regLocalizationproposeds, regItemRegGroupRegRoleMapping, regItemproposedRegGroupRegRoleMapping, regFieldmapping);
            }

            /* -- Processing any eventual new proposed value -- */
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
            if (tmpRegItemRegGroupRegRoleMappings.isEmpty() && regItemproposed == null) {
                tmp += systemLocalization.getString("label.nogroup");
            } else if (tmpRegItemRegGroupRegRoleMappings.isEmpty() && regItemproposed != null && tmpRegItemproposedRegGroupRegRoleMappings.isEmpty()) {
                tmp += "<div class=\"row\"><div class=\"col-sm-6\">" + systemLocalization.getString("label.nogroup") + "</div><div class=\"col-sm-6\">" + systemLocalization.getString("label.nogroup") + "</div></div>";
            }

            // Handling the add button (if the user has the rights)
            /*if (canAdd) {
                if (!tmpRegItemproposedRegGroupRegRoleMappings.isEmpty()) {
                    if ((regFieldmapping.getMultivalue() || (tmpRegItemRegGroupRegRoleMappings.isEmpty() && tmpRegItemproposedRegGroupRegRoleMappings.isEmpty())) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a class=\"btn btn-xs btn-success pull-right btn-group-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + systemLocalization.getString("label.add") + "</a>";
                    }
                } else {
                    if ((regFieldmapping.getMultivalue() || tmpRegItemRegGroupRegRoleMappings.isEmpty()) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a class=\"btn btn-xs btn-success pull-right btn-group-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + systemLocalization.getString("label.add") + "</a>";
                    }
                }
            }*/
        } catch (Exception e) {
            logger.error("@ JspHelper.jspGroupFieldHandler: generic error.", e);
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Collection
     * object ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
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
    public static String jspCollectionFieldHandler(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicateCollection, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String outText = "";

        try {
            // Get the "collection" reg relation (only one collection per each item)
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicateCollection);
            // Some RegItem may not have a collection
            if (!tmpRegRelations.isEmpty()) {
                RegRelation regRelationTmp = tmpRegRelations.get(0);

                // Getting the label for the collection in the reg relation.
                List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), currentLanguage);
                if (tmpRegLocalizations.isEmpty()) {
                    tmpRegLocalizations = regLocalizationManager.getAll(titleRegField, regRelationTmp.getRegItemObject(), masterLanguage);
                }
                // There is only one localization for the collectrion
                outText = "<a href=\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + tmpRegLocalizations.get(0).getValue() + "</a>";

            } else {
                outText = localization.getString("label.nocollection");
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspCollectionFieldHandler: generic error.", e);
        }

        // Returning the HTML string for the Collection
        // reference of the  RegItem
        return getRelationObjectsHTML(regItemproposed, regLocalizationproposeds, regItem, outText, outText);
    }

    /**
     * This method returns a String with the formatted HTML of the Predecessor
     * object(s) ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
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
    public static String jspPredecessorFieldHandler(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicatePredecessor, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegRelationproposedManager regRelationproposedManager, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Get the "parent" RegRelation of the current item
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicatePredecessor);

            List<RegRelationproposed> tmpRegRelationproposeds = new ArrayList<>();

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Process the RegRelation available in the system: the information for each localization -- */
            for (RegRelation regRelation : tmpRegRelations) {
                RegRelationproposed regRelationproposed = null;
                try {
                    // Getting the eventual RegRelationProposed by RegRelation reference 
                    regRelationproposed = regRelationproposedManager.getByRegRelationReference(regRelation);
                    tmpRegRelationproposeds.add(regRelationproposed);
                } catch (NoResultException e) {
                }

                // Handling the the RegRelation parent
                tmp += handleRegLocalizationRelationGeneric(regItemproposed, regLocalizationproposeds, regRelation, regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, false, false);
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

                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, regLocalizationproposeds, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, localization.getString("label.noparent"), false, false);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no predecessor available (both in current and proposed)
            if (tmpRegRelations.isEmpty() && regItemproposed == null) {
                tmp += localization.getString("label.nopredecessor");
            } else if (tmpRegRelations.isEmpty() && regItemproposed != null && tmpRegRelationproposeds.isEmpty()) {
                tmp += "<div class=\"row\"><div class=\"col-sm-6\">" + localization.getString("label.nopredecessor") + "</div><div class=\"col-sm-6\">" + localization.getString("label.nopredecessor") + "</div></div>";
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspPredecessorFieldHandler: generic error.", e);
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Successor
     * object(s) ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
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
    public static String jspSuccessorFieldHelper(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicateSuccessor, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegRelationproposedManager regRelationproposedManager, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        String tmp = "";

        try {
            // Get the "parent" RegRelation of the current item
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicateSuccessor);

            List<RegRelationproposed> tmpRegRelationproposeds = new ArrayList<>();

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Process the RegRelation available in the system: the information for each localization -- */
            for (RegRelation regRelation : tmpRegRelations) {
                RegRelationproposed regRelationproposed = null;
                try {
                    // Getting the eventual RegRelationProposed by RegRelation reference 
                    regRelationproposed = regRelationproposedManager.getByRegRelationReference(regRelation);
                    tmpRegRelationproposeds.add(regRelationproposed);
                } catch (NoResultException e) {
                }

                // Handling the the RegRelation parent
                tmp += handleRegLocalizationRelationGeneric(regItemproposed, regLocalizationproposeds, regRelation, regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, false, false);
            }

            /* -- Processing any eventual new proposed value -- */
            List<RegRelationproposed> regRelationproposeds;
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because the relations are stored
                // only in the MasterLanguage
                regRelationproposeds = regRelationproposedManager.getAllNew(regItemproposed, regRelationpredicateSuccessor);
                for (RegRelationproposed regRelationproposed : regRelationproposeds) {

                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, regLocalizationproposeds, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, localization.getString("label.noparent"), false, false);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no predecessor available (both in current and proposed)
            if (tmpRegRelations.isEmpty() && (regItemproposed == null || (regItemproposed != null && (regLocalizationproposeds == null || regLocalizationproposeds.isEmpty())))) {
                tmp += localization.getString("label.nosuccessor");
            } else if (tmpRegRelations.isEmpty() && regItemproposed != null && tmpRegRelationproposeds.isEmpty()) {
                tmp += "<div class=\"row\"><div class=\"col-sm-6\">" + localization.getString("label.nosuccessor") + "</div><div class=\"col-sm-6\">" + localization.getString("label.nosuccessor") + "</div></div>";
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspSuccessorFieldHelper: generic error.", e);
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the Parent
     * object(s) ready for the front-end JSP
     *
     * @param regRelationManager
     * @param regItem
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
    public static String jspParentFieldHandler(RegRelationManager regRelationManager, RegItem regItem, RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelationpredicate regRelationpredicateParent, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, RegFieldmapping regFieldmapping, RegRelationproposedManager regRelationproposedManager, RegLocalizationproposedManager regLocalizationproposedManager, RegUser currentUser, Map<String, RegGroup> currentUserGroupsMap, List<RegItemRegGroupRegRoleMapping> itemMappings) {
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
            List<RegRelation> tmpRegRelations = regRelationManager.getAll(regItem, regRelationpredicateParent);

            List<RegRelationproposed> tmpRegRelationproposeds = new ArrayList<>();

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list data-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + "\">";
            }

            /* -- Process the RegRelation available in the system: the information for each localization -- */
            for (RegRelation regRelation : tmpRegRelations) {
                RegRelationproposed regRelationproposed = null;
                try {
                    // Getting the eventual RegRelationProposed by RegRelation reference 
                    regRelationproposed = regRelationproposedManager.getByRegRelationReference(regRelation);
                    tmpRegRelationproposeds.add(regRelationproposed);
                } catch (NoResultException e) {
                }

                // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                tmp += handleRegLocalizationRelationGeneric(regItemproposed, regLocalizationproposeds, regRelation, regRelationproposed, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, true, canAdd);
            }

            /* -- Processing any eventual new proposed value -- */
            List<RegRelationproposed> regRelationproposeds;
            try {
                // Getting the eventual RegLocalizationproposed without reference to 
                // the RegLocalization: it is a new proposed value(s)
                // The master language is used because the relations are stored
                // only in the MasterLanguage
                regRelationproposeds = regRelationproposedManager.getAllNew(regItemproposed, regRelationpredicateParent);
                for (RegRelationproposed regRelationproposed : regRelationproposeds) {

                    // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                    tmp += handleRegLocalizationproposedRelationGeneric(regRelationproposed, regLocalizationproposeds, localization, currentLanguage, masterLanguage, regLocalizationManager, titleRegField, regFieldmapping, regLocalizationproposedManager, localization.getString("label.noparent"), true, canAdd);
                    tmpRegRelationproposeds.add(regRelationproposed);
                }
            } catch (NoResultException e) {
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            // Handling the case of no parent available (both in current and proposed)
            if (tmpRegRelations.isEmpty() && (regItemproposed == null || (regItemproposed != null && (regLocalizationproposeds == null || regLocalizationproposeds.isEmpty())))) {
                tmp += localization.getString("label.noparent");
                tmp += "<hr/>";
            } else if (tmpRegRelations.isEmpty() && regItemproposed != null && tmpRegRelationproposeds.isEmpty()) {
                tmp += "<div class=\"row\"><div class=\"col-sm-6\">" + localization.getString("label.noparent") + "</div><div class=\"col-sm-6\">" + localization.getString("label.noparent") + "</div></div>";
                tmp += "<hr/>";
            }

            // Handling the add button (if the user has the rights)
            if (canAdd) {
                if (!tmpRegRelationproposeds.isEmpty()) {
                    if ((regFieldmapping.getMultivalue() || (tmpRegRelations.isEmpty() && tmpRegRelationproposeds.isEmpty())) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-parent-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + localization.getString("label.add") + "</a>";
                    }
                } else {
                    if ((regFieldmapping.getMultivalue() || tmpRegRelations.isEmpty()) && currentLanguage.getMasterlanguage()) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-parent-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + localization.getString("label.add") + "</a>";
                    }
                }
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspParentFieldHandler: generic error.", e);
        }

        return tmp;
    }

    /**
     * This method returns a String with the formatted HTML of the simple value
     * field or objectRelation
     *
     * @param regItem
     * @param regItemManager
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
     * @return String representing the HTML for the simple value field or the
     * Object relation(s)
     */
    public static String jspNormalValueHandler(RegLocalizationManager regLocalizationManager, RegItemManager regItemManager, RegFieldmapping regFieldmapping, RegItem regItem, RegItemproposed regItemproposed, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle systemLocalization, RegField titleRegField, RegLocalizationproposedManager regLocalizationproposedManager, ResourceBundle localization, RegUser currentUser, Map<String, RegGroup> currentUserGroupsMap, List<RegItemRegGroupRegRoleMapping> itemMappings) {
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
        String inputRights = ((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite) ? "" : " disabled=\"disabled\"";
        boolean canAdd = ((permissionItemRegisterRegistry || permissionRegisterRegistry) && canWrite);

        try {
            // Initializing the reg Localization in the Master language. If the current
            // language is different, it will be loaded in the specific field method (handleRegLocalizationSimpleValues)
            List<RegLocalization> regLocalizations = regLocalizationManager.getAll(regFieldmapping.getRegField(), regItem, masterLanguage);

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "<ul class=\"field-list" + ((regFieldmapping.getRequired()) ? " field-list-required" : "") + ((!regLocalizations.isEmpty() && regLocalizations.get(0).getValue() == null) ? " data-list" : "") + "\">";
            }

            // Process the information for each localization
            for (RegLocalization regLocalization : regLocalizations) {

                // Handling the the simple value(s) of the RegLocalization
                if (regLocalization.getValue() != null) {

                    // Handling the the RegField (get the HTML string with translated labels of the fields)
                    tmp += handleRegLocalizationSimpleValues(regLocalization, regItemproposed, regLocalizationManager, systemLocalization, regFieldmapping, currentLanguage, masterLanguage, regLocalizationproposedManager, inputRights, canAdd);

                } // Managing the relation to another item
                else if (regLocalization.getRegRelationReference() != null) {

                    // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                    tmp += handleRegLocalizationRelationReferences(regLocalization, regItemproposed, systemLocalization, currentLanguage, masterLanguage, regLocalizationManager, regItemManager, titleRegField, regFieldmapping, regLocalizationproposedManager, canAdd);
                } // The relation reference is stored in the localization as master language.
                // In case the language of the current localization is not master language and the
                // localization  has no simple value, the followig check will get the RegRelation regerence            
                else {

                    // Getting the RegItem localization for the current field
                    List<RegLocalization> checkRegLocalizations = regLocalizationManager.getAll(regFieldmapping.getRegField(), regItem, masterLanguage);

                    for (RegLocalization regLocalizationCheck : checkRegLocalizations) {

                        // Checking if this is a relation reference
                        if (regLocalizationCheck.getRegRelationReference() != null) {

                            // Handling the the RegRelation parent (get the HTML string with translated labels of the items)
                            tmp += handleRegLocalizationRelationReferences(regLocalizationCheck, regItemproposed, systemLocalization, currentLanguage, masterLanguage, regLocalizationManager, regItemManager, titleRegField, regFieldmapping, regLocalizationproposedManager, canAdd);
                        }
                    }
                }
                // Index of each field (needed for multiple values fields)
            }

            /* -- Get any eventual new proposed value -- */
            List<RegLocalizationproposed> regLocalizationproposeds = null;
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
                        List<RegLocalization> checkRegLocalizations = regLocalizationManager.getAll(regFieldmapping.getRegField(), regItem, masterLanguage);

                        for (RegLocalization regLocalizationCheck : checkRegLocalizations) {

                            // Checking if this is a relation reference
                            if (regLocalizationCheck.getRegRelationReference() != null) {

                                tmp += handleRegLocalizationRelationReferences(regLocalizationCheck, regItemproposed, systemLocalization, currentLanguage, masterLanguage, regLocalizationManager, regItemManager, titleRegField, regFieldmapping, regLocalizationproposedManager, canAdd);
                            }
                        }
                    }
                    // Index of each field (needed for multiple values fields)
                }

            } catch (NoResultException e) {
            }

            // Handling the multivalued fields
            if (regFieldmapping.getMultivalue()) {
                tmp += "</ul>";
            }

            /* -- Managing the "no value" cases -- */
            // Case with values but no proposed value
            if ((regLocalizations != null && !regLocalizations.isEmpty()) || (regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty())) {
                String addType = ((regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)) ? "btn-relation-add" : "btn-value-add");

                // Checking if the add button has to be visible
                if (regFieldmapping.getMultivalue() && currentLanguage.getMasterlanguage() && canAdd) {
                    tmp += "<a href=\"\" class=\"btn btn-xs btn-success pull-right " + addType + "\" data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\" data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + systemLocalization.getString("label.add") + "</a>";
                }
            } else // Cases with no values
            {
                // Relation field case
                if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION) && currentLanguage.getMasterlanguage()) {
                    if (regItemproposed != null) {
                        tmp += "<div class=\"row noref\">";
                        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
                    }

                    // Creating the main div for the field
                    tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : "") + "\" >";

                    tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                    tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

                    // There is only one localization for the registry
                    tmp += systemLocalization.getString("label.referencenoavailable");

                    tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                    tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                    tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

                    // In case of proposal available, showing the column of the current value
                    if (regItemproposed != null) {
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

                        // There is only one localization for the registry
                        tmp += systemLocalization.getString("label.referencenoavailable");

                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                    }

                    tmp += "<hr/>";

                    // Showing the add button in case of adding rights
                    if (canAdd) {
                        tmp += "<a href=\"#\" class=\"btn btn-xs btn-success pull-right btn-relation-add\"" + ((regFieldmapping.getMultivalue()) ? " data-" + WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD + "=\"true\"" : "") + " data-" + WebConstants.DATA_PARAMETER_LANGUAGECONEUUID + "=\"" + currentLanguage.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_FIELDMAPPINGUUID + "=\"" + regFieldmapping.getUuid() + "\" data-" + WebConstants.DATA_PARAMETER_ITEMUUID + "=\"" + regItem.getUuid() + "\">" + systemLocalization.getString("label.add") + "</a>";
                    }
                } else {
                    // Value field case

                    if (regItemproposed != null) {
                        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
                    }

                    if (regFieldmapping.getRequired() && !regFieldmapping.getHashref()) {
                        tmp += "<div class=\"input-group\">";
                    }

                    String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + "0" + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY;
                    String inputNameHref = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + 0 + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;

                    // Showing the empty input
                    if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                        tmp += "<textarea " + inputRights + "class=\"form-control\" " + inputRights + "rows=\"4\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + "0" + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + "></textarea>";
                    } else {

                        if (!regFieldmapping.getHashref()) {
                            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" " + inputRights + "class=\"form-control\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + "0" + "\" value=\"\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";
                        } else {
                            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

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

                            // -- Showing the value --            
                            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" value=\"\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"0\" name=\"" + inputName + "\" " + ((regFieldmapping.getRequired()) ? "required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

                            // Required notice (if needed)
                            if (regFieldmapping.getRequired()) {
                                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                                // Div for form validation errors
                                tmp += "<div class=\"help-block with-errors\"></div>";
                                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                            } else if (regFieldmapping.getMultivalue()) {
                                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                                // Div for form validation errors
                                tmp += "<div class=\"help-block with-errors\"></div>";
                                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                            } else {
                                // Div for form validation errors
                                tmp += "<div class=\"help-block with-errors\"></div>";
                                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                            }

                            // In case the language is not the master language, showing the
                            // master language translation
                            if (!currentLanguage.getMasterlanguage()) {
                                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>:</p>";
                            }

                            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

                            // ----
                            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

                            // Showing the link field href label
                            tmp += "<div class=\"col-sm-3\">" + localization.getString("label.href") + ":</div>";
                            tmp += "<div class=\"col-sm-9\">";

                            // -- Showing the href -- 
                            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control href-control\" value=\"\" name=\"" + inputNameHref + "\" maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

                            // In case the language is not the master language, showing the
                            // master language translation (href could point to different languages)
                            if (!currentLanguage.getMasterlanguage()) {
                                tmp += "<p class=\"small\"><strong >" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>:</p>";
                            }

                            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                        }
                    }

                    if (regFieldmapping.getRequired() && !regFieldmapping.getHashref()) {
                        tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + systemLocalization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                    }

                    if (regItemproposed != null) {
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

                        // There is only one localization for the registry
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                    }

                }
            }
        } catch (Exception e) {
            logger.error("@ JspHelper.jspNormalValueHandler: generic error.", e);
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * simple value field (RegLocalization)
     */
    private static String handleRegLocalizationSimpleValues(RegLocalization regLocalization, RegItemproposed regItemproposed, RegLocalizationManager regLocalizationManager, ResourceBundle localization, RegFieldmapping regFieldmapping, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationproposedManager regLocalizationproposedManager, String inputRights, boolean canAdd) throws Exception {
        String tmp = "";
        Configuration configuration = Configuration.getInstance();

        // The regLocalization passed by paramete is always in the master language
        // Getting regLocalization in the eventual other language
        // If the regField is of type date or number, it is stored only in the master language
        RegLocalization currentLanguageLocalization = null;
        if (!currentLanguage.getMasterlanguage()) {
            if (!regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE) && !regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER)) {
                try {
                    currentLanguageLocalization = getRegLocalizationInCurrentLanguage(regLocalization, regLocalizationManager, currentLanguage);
                } catch (NoResultException e) {
                }
            } else {
                currentLanguageLocalization = regLocalization;
            }
        } else {
            currentLanguageLocalization = regLocalization;
        }

        // Getting the RegLocalizationproposed if available
        RegLocalizationproposed regLocalizationproposed = null;
        try {
            // If the currentLanguageLocalization is null, check if there is a
            // new localization proposed for the current value/field and language
            if (currentLanguageLocalization == null) {
                regLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalization, regItemproposed, regLocalizationproposedManager, currentLanguage);
            } else {
                regLocalizationproposed = regLocalizationproposedManager.getByRegLocalizationReferenceAndLanguage(currentLanguageLocalization, currentLanguage);
            }
        } catch (NoResultException e) {
        }

        /* -- Calculating field values and names -- */
        // Calculating if it is a new or updated content
        String currentValue = (currentLanguageLocalization != null && currentLanguageLocalization.getValue() != null) ? currentLanguageLocalization.getValue() : "";
        String currentValueCleaned = Jsoup.parse(currentValue).text();

        String proposedValue = (regLocalizationproposed != null && regLocalizationproposed.getValue() != null) ? regLocalizationproposed.getValue() : "";
        String proposedValueCleaned = Jsoup.parse(proposedValue).text();

        String newContentClass = ((((regLocalizationproposed != null && currentLanguageLocalization != null)
                && (((!proposedValueCleaned.equals(currentValueCleaned))
                || (regLocalizationproposed.getHref() != null && !regLocalizationproposed.getHref().equals(currentLanguageLocalization.getHref())))
                || (currentLanguageLocalization.getValue() == null || currentLanguageLocalization.getValue().length() < 1)))
                || (regLocalizationproposed != null && regLocalizationproposed.getValue() != null && regLocalizationproposed.getValue().length() < 1 && currentLanguageLocalization != null)
                || (regLocalizationproposed != null && regLocalizationproposed.getValue() == null && currentLanguageLocalization != null) || (regLocalizationproposed != null && regLocalizationproposed.getHref() == null && currentLanguageLocalization != null && currentLanguageLocalization.getHref() != null))
                || (currentLanguageLocalization == null && regLocalizationproposed != null)
                        ? " class=\"pe-icon new-content\"" : "");

        // Getting value
        String inputValue = ((regLocalizationproposed != null)
                ? ((regLocalizationproposed.getValue() != null && regLocalizationproposed.getValue().length() > 0) ? regLocalizationproposed.getValue() : "")
                : ((currentLanguageLocalization != null && currentLanguageLocalization.getValue() != null && currentLanguageLocalization.getValue().length() > 0)
                ? currentLanguageLocalization.getValue() : ""));
        if (inputValue.contains("\"")) {
                inputValue = inputValue.replace("\"", "&quot;");
                }
        
        // Getting input name
        String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalization != null) ? currentLanguageLocalization.getFieldValueIndex() : regLocalization.getFieldValueIndex()) + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY + ((currentLanguageLocalization != null) ? currentLanguageLocalization.getUuid() : ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : ""));

        // Getting the href
        String inputHref = ((regLocalizationproposed != null)
                ? ((regLocalizationproposed.getHref() != null && regLocalizationproposed.getHref().length() > 0) ? regLocalizationproposed.getHref() : "")
                : ((currentLanguageLocalization != null && currentLanguageLocalization.getHref() != null && currentLanguageLocalization.getHref().length() > 0)
                ? currentLanguageLocalization.getHref() : ""));

        //Getting the href input name
        String inputNameHref = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalization != null) ? currentLanguageLocalization.getFieldValueIndex() : regLocalization.getFieldValueIndex()) + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;

        // Getting fieldValueIndex
        int fieldValueIndex = ((regLocalizationproposed != null)
                ? ((regLocalizationproposed.getFieldValueIndex() > -1)
                ? regLocalizationproposed.getFieldValueIndex() : 0)
                : ((currentLanguageLocalization != null && currentLanguageLocalization.getFieldValueIndex() > -1)
                ? currentLanguageLocalization.getFieldValueIndex() : 0));

        /* -- */
 /* -- Generating the page -- */
        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        // Showing 2 columns if the proposal is available
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        // Checking if the simple value has also a link
        if (regFieldmapping.getHashref()) {

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

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
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : ((currentLanguageLocalization != null) ? currentLanguageLocalization.getUuid() : "")) + "\" \"><i class=\"fas fa-trash-alt\"></i></a>";
                tmp += "</span>";
            }

            // -- Showing the value --            
            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" value=\"" + inputValue + "\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" name=\"" + inputName + "\" " + ((regFieldmapping.getRequired()) ? "required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else if (regFieldmapping.getMultivalue()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else {
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>:" + regLocalization.getValue() + "</p>";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            // ----
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

            // Showing the link field href label
            tmp += "<div class=\"col-sm-3\">" + localization.getString("label.href") + ":</div>";
            tmp += "<div class=\"col-sm-9\">";

            // -- Showing the href -- 
            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control href-control\" value=\"" + inputHref + "\" name=\"" + inputNameHref + "\" maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

            // In case the language is not the master language, showing the
            // master language translation (href could point to different languages)
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong >" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>: " + regLocalization.getHref() + "</p>";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        } // If the localization has no href, showing just the value
        else {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

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
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : ((currentLanguageLocalization != null) ? currentLanguageLocalization.getUuid() : "")) + "\" \"><i class=\"fas fa-trash-alt\"></i></a>";
                tmp += "</span>";
            }

            // -- Showing the value -- 
            if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                tmp += "<textarea " + inputRights + "class=\"form-control\" rows=\"4\" " + inputRights + "data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + ">" + inputValue + "</textarea>";
            } else {
                tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" value=\"" + inputValue + "\" name=\"" + inputName + "\"" + ((regFieldmapping.getRequired()) ? " required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";
            }

            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else if (regFieldmapping.getMultivalue()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else {
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>: " + regLocalization.getValue() + "</p>";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        }
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            // Case with href field
            if (regFieldmapping.getHashref()) {

                tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                tmp += "<div class=\"col-sm-12 cur-href\">";
                tmp += (currentLanguageLocalization != null) ? currentLanguageLocalization.getValue() : "";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // ----            
                tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
                tmp += "<div class=\"col-sm-12 cur-href\">";
                tmp += (currentLanguageLocalization != null && currentLanguageLocalization.getHref() != null) ? currentLanguageLocalization.getHref() : "";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            } //Case with the simple value 
            else {
                tmp += (currentLanguageLocalization != null) ? currentLanguageLocalization.getValue() : "";
            }
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
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

        // Calculating if it is a new or updated content
        String newContentClass = ((!inputValue.equals("")) ? " class=\"pe-icon new-content\"" : "");

        // Getting input name
        String inputName = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getFieldValueIndex() : (regLocalizationproposed != null) ? regLocalizationproposed.getFieldValueIndex() : 0) + BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : "");

        // Getting the href
        String inputHref = ((currentLanguageLocalizationproposed != null)
                ? ((currentLanguageLocalizationproposed.getHref() != null && currentLanguageLocalizationproposed.getHref().length() > 0)
                ? currentLanguageLocalizationproposed.getHref() : "")
                : "");

        //Getting the href input name
        String inputNameHref = regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getFieldValueIndex() : ((regLocalizationproposed != null) ? regLocalizationproposed.getFieldValueIndex() : 0)) + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;

        // Getting fieldValueIndex
        int fieldValueIndex = ((currentLanguageLocalizationproposed != null)
                ? ((currentLanguageLocalizationproposed.getFieldValueIndex() > -1)
                ? currentLanguageLocalizationproposed.getFieldValueIndex() : 0)
                : ((regLocalizationproposed != null) ? regLocalizationproposed.getFieldValueIndex() : 0));

        /* -- */
 /* -- Generating the page -- */
        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        // Checking if the simple value has also a link
        if (regFieldmapping.getHashref()) {

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

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
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "")) + "\"><i class=\"fas fa-trash-alt\"></i></a>";
                tmp += "</span>";
            }

            // -- Showing the value --            
            tmp += "<input " + inputRights + "type=\"" + handleFieldType(regFieldmapping, localization) + "\" class=\"form-control\" value=\"" + inputValue + "\" data-" + WebConstants.DATA_PARAMETER_FIELDVALUEINDEX + "=\"" + fieldValueIndex + "\" name=\"" + inputName + "\" " + ((regFieldmapping.getRequired()) ? "required" : "") + " maxlength=\"" + configuration.getProperties().getProperty("application.input.maxlength") + "\" />";

            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                tmp += "<div class=\"input-group-append\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + localization.getString("label.fieldrequired") + "\"><div class=\"input-group-text\"><i class=\"fa fa-asterisk text-danger\"></i > </div></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else if (regFieldmapping.getMultivalue()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else {
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>:" + ((regLocalizationproposed != null) ? regLocalizationproposed.getValue() : "") + "</p>";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

            // ----
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;

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

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        } // If the localization has no href, showing just the value
        else {

            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

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
                tmp += "<a href=\"#\" class=\"btn btn-danger btn-value-remove\" title=\"" + localization.getString("label.remove") + "\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((currentLanguageLocalizationproposed != null) ? currentLanguageLocalizationproposed.getUuid() : ((regLocalizationproposed != null) ? regLocalizationproposed.getUuid() : "")) + "\"><i class=\"fas fa-trash-alt\"></i></a>";
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
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else if (regFieldmapping.getMultivalue()) {
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
                // Div for form validation errors
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            } else {
                tmp += "<div class=\"help-block with-errors\"></div>";
                tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            }

            // In case the language is not the master language, showing the
            // master language translation
            if (!currentLanguage.getMasterlanguage()) {
                tmp += "<p class=\"small\"><strong>" + localization.getString("label.masterlanguage") + "(" + masterLanguage.getIso6391code() + ")</strong>: " + ((regLocalizationproposed != null) ? regLocalizationproposed.getValue() : "") + "</p>";
            }

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        }

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation reference field (RegLocalization)
     */
    private static String handleRegLocalizationRelationReferences(RegLocalization regLocalization, RegItemproposed regItemproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegItemManager regItemMamnager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation reference and the related item object
        RegRelation regRelation = regLocalization.getRegRelationReference();
        RegItem regItemObject = regRelation.getRegItemObject();

        // Getting the RegLocalizationproposed from the reference to the current
        // RegLocalization if available and the related RegItemObject
        RegRelationproposed regRelationproposed = null;
        RegLocalizationproposed regLocalizationproposed = null;
        RegItem regItemObjectFromProposedRelation = null;
        RegItemproposed regItemproposedObjectFromProposedRelation = null;
        try {
            regLocalizationproposed = regLocalizationproposedManager.getByRegLocalizationReferenceAndLanguage(regLocalization, masterLanguage);
            regRelationproposed = regLocalizationproposed.getRegRelationproposedReference();
            if (regRelationproposed != null && regRelationproposed.getRegItemObject() != null) {
                regItemObjectFromProposedRelation = regRelationproposed.getRegItemObject();
            } else if (regRelationproposed != null && regRelationproposed.getRegItemproposedObject() != null) {
                regItemproposedObjectFromProposedRelation = regRelationproposed.getRegItemproposedObject();
            }
        } catch (NoResultException e) {
        }

        String newContentClass = ((regLocalizationproposed != null && regLocalizationproposed.getRegRelationproposedReference() == null) ? " class=\"pe-icon new-content\"" : "");

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue" + ((regItemproposed == null) || (regItemproposed != null && regRelationproposed != null) ? " cnt" : "") + "\">";
        }

        // Showing 2 columns if the proposal is available
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regRelationproposed != null) ? regRelationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        if (regLocalizationproposed != null) {
            if (regLocalizationproposed.getRegRelationproposedReference() != null) {
                tmp += getRelationproposedHtml(regLocalizationproposed, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, true, canAdd);
            } else {
                // Showing the restore relation button (if it is in the master language the user has the rights)
                if (currentLanguage.getMasterlanguage() && canAdd) {
                    /*test*/
                    if (!regFieldmapping.getMultivalue() && regLocalizationproposed.getRegRelationproposedReference() == null) {
                        Date tmpDate = new Date();
                        String timestamp = Long.toString(tmpDate.getTime());
                        tmp += "<select data-live-search=\"true\" class=\"selectpicker form-control\" name=\"" + regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + timestamp + "\">";
                        tmp += "<option selected=\"selected\"></option>";
                        List<RegItem> regItems = regItemMamnager.getAll(regItemObject.getRegItemclass());
                        regItems.remove(regItemObject);

                        for (RegItem regItem : regItems) {
                            List<RegLocalization> tmpLocalizations = regLocalizationManager.getAll(titleRegField, regItem, masterLanguage);
                            // Getting just the first localization (it's the RegItem's label)
                            regLocalization = tmpLocalizations.get(0);
                            tmp += "<option value=\"" + regItem.getUuid() + "\">" + regLocalization.getValue() + "</option>";
                        }
                        tmp += "</select>";
                    }
                    /*test*/
                    tmp += "<a class=\"pull-right btn btn-xs btn-restore-relation\" data-toggle=\"tooltip\" data-placement=\"top\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONUUID + "=\"" + regLocalizationproposed.getRegLocalizationReference().getUuid() + "\" title=\"" + localization.getString("label.restoreoriginalrelation") + "\"><i class=\"fa fa-undo\" aria-hidden=\"true\"></i></a>";
                }
            }
        } else {
            tmp += getRelationHtml(regLocalization, regRelation, regItemObject, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, (regRelationproposed != null && (regLocalizationproposed != null && regLocalizationproposed.getRegRelationproposedReference() != null)), true, canAdd);
        }

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            tmp += getRelationHtml(regLocalization, regRelation, regItemObject, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, (regRelationproposed != null || regLocalizationproposed.getRegRelationproposedReference() == null), true, canAdd);

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

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
        }

        String newContentClass = " class=\"pe-icon new-content\"";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue cnt\">";
        }

        // Showing 2 columns if the proposal is available
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regRelationproposed != null) ? regRelationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        tmp += getRelationproposedHtml(regLocalizationproposed, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, true, canAdd);

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regLocalizationproposed != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation generic field (RegLocalization)
     */
    private static String handleRegLocalizationRelationGeneric(RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegRelation regRelation, RegRelationproposed regRelationproposed, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, boolean canEdit, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation item object
        RegItem regItemObject = regRelation.getRegItemObject();

        String newContentClass = (((regRelationproposed != null && regRelationproposed.getRegRelationReference() != null && regRelation != null && !regRelationproposed.getRegRelationReference().getUuid().equals(regRelation.getUuid()))
                || (regItemproposed != null && regRelationproposed == null && regRelation != null)) ? " class=\"pe-icon new-content\"" : "");

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue" + ((regItemproposed == null) || (regItemproposed != null && regRelationproposed != null) ? " cnt" : "") + "\">";
        }

        // Showing 2 columns if the proposal is available
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regRelationproposed != null) ? regRelationproposed.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        // Setting the right output handler
        if (regItemproposed != null && regRelationproposed != null) {
            tmp += getRelationproposedHtml(null, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, canEdit, canAdd);
        } else if (regItemproposed == null && regRelationproposed == null) {
            tmp += getRelationHtml(null, regRelation, regItemObject, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, (regRelationproposed != null), canEdit, canAdd);
        } else if (regItemproposed != null && regRelationproposed == null && currentLanguage.getMasterlanguage() && canEdit) {
            tmp += "<a class=\"pull-right btn btn-xs btn-restore-relation\" data-toggle=\"tooltip\" data-placement=\"top\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONUUID + "=\"" + regRelation.getUuid() + "\" title=\"" + localization.getString("label.restoreoriginalrelation") + "\"><i class=\"fa fa-undo\" aria-hidden=\"true\"></i></a>";
        }

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            tmp += getRelationHtml(null, regRelation, regItemObject, regLocalizationManager, titleRegField, currentLanguage, masterLanguage, localization, true, canEdit, canAdd);

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the
     * Relation generic field (RegLocalizationproposed)
     */
    private static String handleRegLocalizationproposedRelationGeneric(RegRelationproposed regRelationproposed, List<RegLocalizationproposed> regLocalizationproposeds, ResourceBundle localization, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegFieldmapping regFieldmapping, RegLocalizationproposedManager regLocalizationproposedManager, String noValueString, boolean canEdit, boolean canAdd) throws Exception {
        String tmp = "";

        // Getting the relation item object
        RegItem regItemObject = regRelationproposed.getRegItemObject();

        String newContentClass = " class=\"pe-icon new-content\"";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue cnt\">";
        }

        // Showing 2 columns if the proposal is available
        if (regRelationproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + regRelationproposed.getUuid() + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        tmp += getRelationproposedHtml(null, regRelationproposed, regItemObject, regLocalizationManager, regLocalizationproposedManager, titleRegField, currentLanguage, masterLanguage, localization, canEdit, canAdd);

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regRelationproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
            tmp += noValueString;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

        if (regFieldmapping.getMultivalue()) {
            tmp += "</li>";
        }

        return tmp;
    }

    /**
     * This private method returns a String with the formatted HTML of the group
     */
    private static String handleRegGroupOutput(RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping, RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping, RegFieldmapping regFieldmapping) {
        String tmp = "";

        String newContentClass = "";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        // Showing 2 columns if the proposal is available
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposedRegGroupRegRoleMapping != null) ? regItemproposedRegGroupRegRoleMapping.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        // Setting the right output handler
        if (regItemproposed != null && regItemproposedRegGroupRegRoleMapping != null) {
            tmp += getGroupproposedHtml(regItemproposedRegGroupRegRoleMapping);
        } else if (regItemproposed == null && regItemproposedRegGroupRegRoleMapping == null) {
            tmp += getGroupHtml(regItemRegGroupRegRoleMapping);
        }

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            tmp += getGroupHtml(regItemRegGroupRegRoleMapping);

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

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

        String newContentClass = " class=\"pe-icon new-content\"";

        // Handling the multivalued fields
        if (regFieldmapping.getMultivalue()) {
            tmp += "<li class=\"multivalue\">";
        }

        // Showing 2 columns if the proposal is available
        if (regItemproposedRegGroupRegRoleMapping != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposedRegGroupRegRoleMapping != null) ? regItemproposedRegGroupRegRoleMapping.getUuid() : "") + "\"" + newContentClass + " >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        if (regItemproposedRegGroupRegRoleMapping != null) {
            RegGroup tmpGroupproposed = regItemproposedRegGroupRegRoleMapping.getRegGroup();
            tmp += "<a title=\"" + tmpGroupproposed.getEmail() + " - " + tmpGroupproposed.getWebsite() + "\">" + tmpGroupproposed.getName() + "</a>";
        }

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of proposal available, showing the column of the current value
        if (regItemproposedRegGroupRegRoleMapping != null) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }

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
     * This private method retrieves the RegLocalization object passed by
     * parameter in the current language
     */
    private static RegLocalization getRegLocalizationInCurrentLanguage(RegLocalization regLocalization, RegLocalizationManager regLocalizationManager, RegLanguagecode currentLanguagecode) throws Exception {

        String tmpUuid = RegLocalizationUuidHelper.getUuid(
                regLocalization.getFieldValueIndex(),
                currentLanguagecode,
                regLocalization.getRegItem(),
                regLocalization.getRegField()
        );

        RegLocalization out = null;
        try {
            out = regLocalizationManager.get(tmpUuid);
        } catch (NoResultException e) {
        }

        return out;
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
        }

        return out;
    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static RegLocalizationproposed getRegLocalizationproposedInCurrentLanguage(RegLocalization regLocalization, RegItemproposed regItemproposed, RegLocalizationproposedManager regLocalizationproposedManager, RegLanguagecode currentLanguagecode) throws Exception {

        String tmpUuid = RegLocalizationproposedUuidHelper.getUuid(regLocalization.getFieldValueIndex(),
                currentLanguagecode,
                regItemproposed,
                regLocalization.getRegField()
        );

        RegLocalizationproposed out = null;
        try {
            out = regLocalizationproposedManager.get(tmpUuid);
        } catch (NoResultException e) {
        }

        return out;
    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static String getRelationHtml(RegLocalization regLocalizationOriginal, RegRelation regRelation, RegItem regItemObject, RegLocalizationManager regLocalizationManager, RegField titleRegField, RegLanguagecode currentLanguage, RegLanguagecode masterLanguage, ResourceBundle localization, boolean regRelationProposedavailable, boolean canEdit, boolean canAdd) throws Exception {
        // Getting the localization for the relation object
        List<RegLocalization> regLocalizationTmps;
        try {
            // Checking the localizations for the current relation in the current language
            regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelation.getRegItemObject(), currentLanguage);
            if (regLocalizationTmps.isEmpty()) {
                // If not available in the current language, checking the localizations for the current relation in the master language
                regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelation.getRegItemObject(), masterLanguage);
            }
        } catch (NoResultException e) {
            // If not available in the current language, checking the localizations for the current relation in the master language
            regLocalizationTmps = regLocalizationManager.getAll(titleRegField, regRelation.getRegItemObject(), masterLanguage);
        }

        // Showing the related object label and link                   
        // The uuid passed to the delete button is the one of the original RegLocalization
        // (from the initial item and not from the related item (regLocalizationTmps refers
        // to the related item)
        return ((currentLanguage.getMasterlanguage() && !regRelationProposedavailable && canEdit && canAdd) ? "<a href=\"#\" title=\""
                + localization.getString("label.remove") + "\" class=\"btn btn-xs btn-danger btn-relation-remove\" data-"
                + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((regLocalizationOriginal != null) ? regLocalizationOriginal.getUuid() : regRelation.getUuid())
                + "\"><i class=\"far fa-trash-alt\"></i></a>&nbsp;" : "") + "<a href=\""
                + WebConstants.PAGE_PATH_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemObject.getUuid() + "&"
                + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + regLocalizationTmps.get(0).getValue() + "</a>";

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
        return ((currentLanguage.getMasterlanguage() && canEdit && canAdd) ? "<a  href=\"#\" title=\"" + localization.getString("label.remove") + "\" class=\"btn btn-xs btn-danger btn-relation-remove\" data-" + WebConstants.DATA_PARAMETER_REGLOCALIZATIONPROPOSEDUUID + "=\"" + ((regLocalizationproposedOriginal != null) ? regLocalizationproposedOriginal.getUuid() : ((regRelationproposed != null) ? regRelationproposed.getUuid() : "")) + "\"><i class=\"far fa-trash-alt\"></i></a>&nbsp;" : "") + "<a href=\"" + WebConstants.PAGE_PATH_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemObject.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + currentLanguage.getUuid() + "\">" + ((regLocalizationproposedTmps != null && !regLocalizationproposedTmps.isEmpty()) ? regLocalizationproposedTmps.get(0).getValue() : ((regLocalizationTmps != null && !regLocalizationTmps.isEmpty()) ? regLocalizationTmps.get(0).getValue() : "")) + "</a>";

    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static String getGroupHtml(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping) {
        /* The reference to the reg group is always mandatory */

        // Showing the currend Group and any eventual group from the same role.
        // The reference to a group is always mandatory and it may be multivalued
        String tmp = "";
        if (regItemRegGroupRegRoleMapping.getRegGroup().getWebsite() != null && regItemRegGroupRegRoleMapping.getRegGroup().getEmail() != null) {
            tmp += "<span data-toggle=\"tooltip\" title=\"Web: " + regItemRegGroupRegRoleMapping.getRegGroup().getWebsite() + " - e-mail: " + regItemRegGroupRegRoleMapping.getRegGroup().getEmail() + "\">" + regItemRegGroupRegRoleMapping.getRegGroup().getName() + "</span>";
        } else {
            tmp += "<span data-toggle=\"tooltip\">" + regItemRegGroupRegRoleMapping.getRegGroup().getName() + "</span>";
        }
        return tmp;
    }

    /**
     * This private method retrieves the RegLocalizationproposed object passed
     * by parameter in the current language
     */
    private static String getGroupproposedHtml(RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping) {

        String tmp = "";
        if (regItemproposedRegGroupRegRoleMapping.getRegGroup().getWebsite() != null && regItemproposedRegGroupRegRoleMapping.getRegGroup().getEmail() != null) {
            tmp += "<span data-toggle=\"tooltip\" title=\"Web: " + regItemproposedRegGroupRegRoleMapping.getRegGroup().getWebsite() + " - e-mail: " + regItemproposedRegGroupRegRoleMapping.getRegGroup().getEmail() + "\">" + regItemproposedRegGroupRegRoleMapping.getRegGroup().getName() + "</span>";
        } else {
            tmp += "<span data-toggle=\"tooltip\">" + regItemproposedRegGroupRegRoleMapping.getRegGroup().getName() + "</span>";
        }
        return tmp;
    }

    /**
     * This private method the HTML string representing the RegRelation
     */
    private static String getRelationObjectsHTML(RegItemproposed regItemproposed, List<RegLocalizationproposed> regLocalizationproposeds, RegItem regItem, String formattedObjectTextCurrent, String formattedObjectTextProposed) {
        // Preparing the output string
        String tmp = "";

        // Showing 2 columns if the proposal is available
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;
        }

        // Creating the main div for the field
        tmp += "<div id=\"" + WebConstants.DATA_PARAMETER_VLLISTIDPREFIX + "" + ((regItemproposed != null) ? regItemproposed.getUuid() : regItem.getUuid()) + "\" >";

        tmp += WebConstants.HTML_CONSTANT_DIV_ROW_OPENING;
        tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_12_OPENING;

        // Setting the formatted object text
        tmp += formattedObjectTextCurrent;

        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;

        // In case of available proposal, showing the column of the current value
        // In the case of the registry it is always the same because it cannot be changed
        if (regItemproposed != null && regLocalizationproposeds != null && !regLocalizationproposeds.isEmpty()) {
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_COL_SM_6_OPENING;

            // Setting the formatted object text
            tmp += formattedObjectTextProposed;

            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
            tmp += WebConstants.HTML_CONSTANT_DIV_CLOSING;
        }
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
}
