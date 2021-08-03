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
package eu.europa.ec.re3gistry2.web.controller;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.InputSanitizerHelper;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ITEMPROPOSEDLISTLOADER)
public class ItemProposedListLoaderServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String itemUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String sStart = request.getParameter(BaseConstants.KEY_REQUEST_DT_START);
        String sLength = request.getParameter(BaseConstants.KEY_REQUEST_DT_LENGTH);
        String sDraw = request.getParameter(BaseConstants.KEY_REQUEST_DT_DRAW);

        itemUUID = (itemUUID != null) ? InputSanitizerHelper.sanitizeInput(itemUUID) : null;
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;
        sStart = (sStart != null) ? InputSanitizerHelper.sanitizeInput(sStart) : null;
        sLength = (sLength != null) ? InputSanitizerHelper.sanitizeInput(sLength) : null;
        sDraw = (sDraw != null) ? InputSanitizerHelper.sanitizeInput(sDraw) : null;

        int start;
        int length;
        int draw;

        try {
            draw = Integer.parseInt(sDraw);
            start = Integer.parseInt(sStart);
            length = Integer.parseInt(sLength);
        } catch (Exception e) {
            draw = 0;
            start = 0;
            length = 10;
        }

        //Preparing the out string
        String outs = "{\"draw\":" + draw + ",";
        outs += "\"data\":[";

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Initializing managers
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegStatuslocalizationManager regStatuslocalizationManager = new RegStatuslocalizationManager(entityManager);

        //Loading the needed RegRelation predicates (to chreate the URI)
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
        RegRelationpredicate regRelationpredicatePredecessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR);
        RegRelationpredicate regRelationpredicateSuccessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR);
        RegRelationpredicate regRelationpredicateParent = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode regLanguagecode;
        if (languageUUID != null && languageUUID.length() == 2) {
            try {
                regLanguagecode = regLanguagecodeManager.get(languageUUID);
            } catch (Exception e) {
                regLanguagecode = masterLanguage;
            }
        } else {
            regLanguagecode = masterLanguage;
        }

        // Getting the item (container)
        RegItem regItem = regItemManager.get(itemUUID);

        // Listing the RegFields for the current RegItem
        List<RegFieldmapping> regFieldmappings;

        // #--- Creating the list of contained items (if needed) ---# //
        int totalCount = 0;
        List<RegItemproposed> containedRegItems = null;
        List<RegItemclass> regItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
        if (!regItemclasses.isEmpty()) {
            if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                // Getting the child itemclass of the contaier item
                List<RegItemclass> childItemclass = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
                // For the RegItemclassType "Register and Item" there shall be just
                // one child itemclass
                RegItemclass newRegItemclass = childItemclass.get(0);

                regItemclasses.clear();
                regItemclasses.add(newRegItemclass);

//                containedRegItems = regItemproposedManager.getAllNew(regItemclasses, start, length);
//                totalCount = regItemproposedManager.countAllNew(regItemclasses);
//
//                if (containedRegItems!=null && containedRegItems.size() > 0) {
//                    //Check collections
//                    List<RegItemproposed> containedRegItemsCheck = regItemproposedManager.getAllNew(regItemclasses, regItem, regRelationpredicateCollection, start, length);
//                    
//                    if (!containedRegItemsCheck.isEmpty() && containedRegItemsCheck.size() != containedRegItems.size()) {
//                        containedRegItems = containedRegItemsCheck;
//                        totalCount = regItemproposedManager.countAllNew(regItemclasses, regItem, regRelationpredicateCollection);
//                    }
//                }
                List<RegItemproposed> containedRegItemsCheck = regItemproposedManager.getAllNew(regItemclasses, regItem, regRelationpredicateCollection, start, length);

                if (!containedRegItemsCheck.isEmpty()
//                        && containedRegItemsCheck.size() != containedRegItems.size()
                        ) {
                    containedRegItems = containedRegItemsCheck;
                    totalCount = regItemproposedManager.countAllNew(regItemclasses, regItem, regRelationpredicateCollection);
                }

            } else {
                containedRegItems = regItemproposedManager.getAllNew(regItemclasses, start, length);
                totalCount = regItemproposedManager.countAllNew(regItemclasses);
            }
        }

        // #--- Preparing the contents ---# //
        // Flag to highlight unavailability of translations
        boolean languageNotAvailable = false;

        if (containedRegItems != null && !containedRegItems.isEmpty()) {

            // Getting the field list for the contained items
            regFieldmappings = regFieldmappingManager.getAll(regItemclasses.get(0));

            // Showing the table contents
            int i = 0;
            for (RegItemproposed tmpRegItem : containedRegItems) {

                // In case the itemclass contained is more than one (e.g. registry), the system shows only the label 
                if (regItemclasses.size() > 1) {
                    //Gettingthe label
                    RegField label = regFieldManager.getTitleRegField();
                    List<RegLocalizationproposed> tmpRegLocalozations;
                    try {
                        tmpRegLocalozations = regLocalizationproposedManager.getAll(label, tmpRegItem, regLanguagecode);
                        if (tmpRegLocalozations.isEmpty()) {
                            // Getting the localizations in the master langage in case the current language is not available
                            tmpRegLocalozations = regLocalizationproposedManager.getAll(label, tmpRegItem, masterLanguage);
                        }
                    } catch (NoResultException e) {
                        // Getting the localizations in the master langage in case the current language is not available
                        tmpRegLocalozations = regLocalizationproposedManager.getAll(label, tmpRegItem, masterLanguage);
                    }
                    if (i != 0) {
                        outs += ",";
                    }
                    outs += "[\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + tmpRegItem.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalozations.get(0).getValue()) + "</a>\"]";

                    // It the contained itemclass is just one
                } else {
                    if (i != 0) {
                        outs += ",";
                    }
                    outs += "[";
                    int j = 0;
                    for (RegFieldmapping tmpRegFieldmapping : regFieldmappings) {
                        // Handling each field
                        if (tmpRegFieldmapping.getTablevisible()) {

                            if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)) {
                                // Get the "registry" reg relation (only one registry per each register/item)
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicateRegistry);
                                RegRelationproposed regRelationTmp = tmpRegRelations.get(0);

                                // Getting the label for the registry in the reg relation.
                                List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                if (tmpRegLocalizations.isEmpty()) {
                                    tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                }

                                if (j != 0) {
                                    outs += ",";
                                }

                                // There is only one localization for the registry
                                outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";

                                j++;
                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)) {
                                // Get the "register" reg relation (only one registry per each register/item)
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicateRegister);
                                if (!tmpRegRelations.isEmpty()) {
                                    RegRelationproposed regRelationTmp = tmpRegRelations.get(0);

                                    // Getting the label for the register in the reg relation.
                                    List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                    if (tmpRegLocalizations.isEmpty()) {
                                        tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                    }

                                    if (j != 0) {
                                        outs += ",";
                                    }

                                    // There is only one localization for the registry
                                    outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";

                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;
                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)) {

                                // Get the "collection" reg relation (only one collection per each item)
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicateCollection);
                                if (!tmpRegRelations.isEmpty()) {
                                    RegRelationproposed regRelationTmp = tmpRegRelations.get(0);

                                    // Getting the label for the collection in the reg relation.
                                    List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                    if (tmpRegLocalizations.isEmpty()) {
                                        tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                    }
                                    if (j != 0) {
                                        outs += ",";
                                    }

                                    // There is only one localization for the registry
                                    outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";

                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;

                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {

                                // Get the "parent" reg relation
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicateParent);
                                if (!tmpRegRelations.isEmpty()) {
                                    // Handling the multivalued fields
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "[";
                                    }
                                    int k = 0;
                                    for (RegRelationproposed regRelationTmp : tmpRegRelations) {
                                        // Getting the label for the parent in the reg relation.
                                        List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                        if (tmpRegLocalizations.isEmpty()) {
                                            tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                        }
                                        if (k != 0) {
                                            outs += ",";
                                        }
                                        outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";
                                        k++;
                                    }
                                    // Handling the multivalued fields
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "]";
                                    }
                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;

                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PREDECESSOR)) {

                                // Get the "predecessor" reg relation
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicatePredecessor);
                                if (!tmpRegRelations.isEmpty()) {
                                    // Handling the multivalued fields
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "[";
                                    }
                                    int k = 0;
                                    for (RegRelationproposed regRelationTmp : tmpRegRelations) {
                                        // Getting the label for the predecessor in the reg relation.
                                        List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                        if (tmpRegLocalizations.isEmpty()) {
                                            tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                        }
                                        if (k != 0) {
                                            outs += ",";
                                        }
                                        outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";
                                        k++;
                                    }
                                    // Handling the multivalued fields
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "]";
                                    }
                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;

                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_SUCCESSOR)) {

                                // Get the "successor" reg relation
                                List<RegRelationproposed> tmpRegRelations = regRelationproposedManager.getAll(tmpRegItem, regRelationpredicateSuccessor);
                                if (!tmpRegRelations.isEmpty()) {
                                    // Handling the multivalued fields
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "[";
                                    }
                                    int k = 0;
                                    for (RegRelationproposed regRelationTmp : tmpRegRelations) {
                                        // Getting the label for the successor in the reg relation.
                                        List<RegLocalization> tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), regLanguagecode);
                                        if (tmpRegLocalizations.isEmpty()) {
                                            tmpRegLocalizations = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelationTmp.getRegItemObject(), masterLanguage);
                                        }
                                        if (k != 0) {
                                            outs += ",";
                                        }
                                        outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regRelationTmp.getRegItemObject().getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalizations.get(0).getValue()) + " </a>\"";
                                        k++;
                                    }
                                    // Handling the multivalued fields
                                    if (tmpRegRelations.size() > 1) {
                                        outs += "]";
                                    }
                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;

                            } else if (tmpRegFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)) {
                                if (j != 0) {
                                    outs += ",";
                                }

                                // Getting the requested RegStatus
                                RegStatus regStatus = regStatusManager.findByLocalid(tmpRegItem.getRegStatus().getLocalid());
                                RegStatusgroup regStatusgroup = regStatus.getRegStatusgroup();

                                // Check if there are proposed items
                                RegItemproposed regItemproposed = null;
                                String regItemproposedStatus = "";
                                try {
                                    regItemproposed = tmpRegItem;

                                    RegStatus regStatusProposedItem = regItemproposed.getRegStatus();

                                    //Getting the RegStatus localization
                                    RegStatuslocalization regStatusLocalizationProposedItem;
                                    try {
                                        regStatusLocalizationProposedItem = regStatuslocalizationManager.get(regStatusProposedItem, regLanguagecode);
                                    } catch (NoResultException e) {
                                        regStatusLocalizationProposedItem = regStatuslocalizationManager.get(regStatusProposedItem, masterLanguage);
                                    }

                                    regItemproposedStatus = " / " + regStatusLocalizationProposedItem.getLabel();

                                } catch (NoResultException e) {
                                    //Nothing to do in case of no results
                                }

                                //Getting the RegStatus localization
                                RegStatuslocalization regStatusLocalization;
                                try {
                                    regStatusLocalization = regStatuslocalizationManager.get(regStatus, regLanguagecode);
                                } catch (NoResultException e) {
                                    regStatusLocalization = regStatuslocalizationManager.get(regStatus, masterLanguage);
                                }

                                String statusURI = regStatusgroup.getBaseuri()+ "/" + regStatusgroup.getLocalid() + "/" + regStatus.getLocalid();
                                outs += "\"<a data-uri=\\\"/" + statusURI + "\\\" href=\\\"" + statusURI + "\\\">" + regStatusLocalization.getLabel() + "</a>\"";

                                j++;
                            } else {

                                List<RegLocalizationproposed> tmpRegLocalozations;
                                try {
                                    tmpRegLocalozations = regLocalizationproposedManager.getAll(tmpRegFieldmapping.getRegField(), tmpRegItem, regLanguagecode);
                                    if (tmpRegLocalozations.size() < 1) {
                                        throw new NoResultException();
                                    }
                                } catch (NoResultException e) {
                                    if (tmpRegFieldmapping.getRegField().getIstitle()) {
                                        languageNotAvailable = true;
                                    }
                                    tmpRegLocalozations = regLocalizationproposedManager.getAll(tmpRegFieldmapping.getRegField(), tmpRegItem, masterLanguage);
                                }
                                if (!tmpRegLocalozations.isEmpty()) {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    if (tmpRegLocalozations.size() > 1) {
                                        outs += "[";
                                    }
                                    int k = 0;
                                    for (RegLocalizationproposed tmpRegLocalization : tmpRegLocalozations) {

                                        if (tmpRegLocalization.getValue() != null) {
                                            // If the field is a title, put the link to the item
                                            if (tmpRegFieldmapping.getRegField().getIstitle()) {
                                                outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + tmpRegItem.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(tmpRegLocalization.getValue()) + "</a>\"";
                                            } else {
                                                outs += "\"" + StringEscapeUtils.escapeJson(tmpRegLocalization.getValue()) + "\"";
                                            }

                                        } else if (tmpRegLocalization.getRegRelationproposedReference() != null) {
                                            if (k != 0) {
                                                outs += ",";
                                            }

                                            RegRelationproposed regRelation = tmpRegLocalization.getRegRelationproposedReference();
                                            RegItem regItemReference = regRelation.getRegItemObject();

                                            // Setting the localization
                                            List<RegLocalization> regLocalizationTmps;
                                            try {
                                                regLocalizationTmps = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelation.getRegItemObject(), regLanguagecode);
                                            } catch (NoResultException e) {
                                                regLocalizationTmps = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelation.getRegItemObject(), masterLanguage);
                                            }

                                            for (RegLocalization regLocalizationTmp : regLocalizationTmps) {
                                                outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemReference.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(regLocalizationTmp.getValue()) + "</a>\"";
                                            }
                                        } else {
                                            // Check if there is the localization (relationreference) in the master language
                                            // Getting the RegItem localization for the current field
                                            tmpRegLocalozations = regLocalizationproposedManager.getAll(tmpRegFieldmapping.getRegField(), tmpRegItem, masterLanguage);

                                            for (RegLocalizationproposed regLocalizationCheck : tmpRegLocalozations) {
                                                if (regLocalizationCheck.getRegRelationproposedReference() != null) {
                                                    RegRelationproposed regRelation = regLocalizationCheck.getRegRelationproposedReference();
                                                    RegItem regItemReference = regRelation.getRegItemObject();

                                                    // Setting the localization
                                                    List<RegLocalization> regLocalizationTmps;
                                                    try {
                                                        regLocalizationTmps = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelation.getRegItemObject(), regLanguagecode);
                                                    } catch (NoResultException e) {
                                                        regLocalizationTmps = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelation.getRegItemObject(), masterLanguage);
                                                    }

                                                    if (k != 0) {
                                                        outs += ",";
                                                    }

                                                    for (RegLocalization regLocalizationTmp : regLocalizationTmps) {
                                                        outs += "\"<a href=\\\"./browse?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemReference.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode + "\\\">" + StringEscapeUtils.escapeJson(regLocalizationTmp.getValue()) + "</a>\"";
                                                    }
                                                }
                                            }
                                        }
                                        k++;
                                    }

                                    if (tmpRegLocalozations.size() > 1) {
                                        outs += "]";
                                    }
                                } else {
                                    if (j != 0) {
                                        outs += ",";
                                    }
                                    outs += "\"\"";
                                }
                                j++;
                            }
                        }
                    }
                    outs += "]";
                }
                i++;
            }
        }

        outs += "],";
        outs += "\"recordsTotal\":" + totalCount + ",";
        outs += "\"recordsFiltered\":" + totalCount;
        outs += (languageNotAvailable) ? ",\"languageNotAvailable\":true" : "";
        outs += "}";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(outs);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex);
        }
    }
}
