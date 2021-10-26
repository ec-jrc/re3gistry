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
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ADDITEM)
public class AddItem extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Loading the system localization (note: it is different from the content localization)
        ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

        // Getting the current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        // Instantiating managers
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

        // Loading the needed RegRelation predicates (to chreate the URI)
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

        // Getting parameters
        //String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String itemUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String formNewItemCheck = request.getParameter(BaseConstants.KEY_REQUEST_NEWITEMINSERT);
        String newItemItemclassUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID);
        String formRegItemContainerUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REGITEMCONTAINERUUID);
        String externalItem = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM);
        
        //languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;
        itemUuid = (itemUuid != null) ? InputSanitizerHelper.sanitizeInput(itemUuid) : null;
        formNewItemCheck = (formNewItemCheck != null) ? InputSanitizerHelper.sanitizeInput(formNewItemCheck) : null;
        newItemItemclassUuid = (newItemItemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(newItemItemclassUuid) : null;
        formRegItemContainerUuid = (formRegItemContainerUuid != null) ? InputSanitizerHelper.sanitizeInput(formRegItemContainerUuid) : null;
        externalItem = (externalItem != null) ? InputSanitizerHelper.sanitizeInput(externalItem) : null;   


        if (itemUuid == null || itemUuid.length() < 1) {
            itemUuid = formRegItemContainerUuid;
        }

        // Setting the external item flag
        if (externalItem != null && externalItem.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            request.setAttribute(BaseConstants.KEY_FORM_FIELD_NAME_EXTERNAL_ITEM, true);
        }

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode regLanguagecode = null;
        
        // Adding a new item it always add in the masterLanguage
        /*if (languageUuid != null && languageUuid.length() == 2) {
            try {
                regLanguagecode = regLanguagecodeManager.get(languageUuid);
            } catch (Exception e) {
                regLanguagecode = masterLanguage;
            }
        } else {*/
            regLanguagecode = masterLanguage;
        //}
        request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, regLanguagecode);

        // Check if the reference to the containerd item is available otherwise, 
        // redirtecting to the browse home
        RegItem regItem = null;
        if (itemUuid != null && itemUuid.length() > 0) {

            // This is the RegItem that will contain the new proposed item
            try {
                regItem = regItemManager.get(itemUuid);
                request.setAttribute(BaseConstants.KEY_REQUEST_REGITEM, regItem);

                // Setting the registry
                RegItem regItemRegistry = null;
                if (regItem != null && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                    List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
                    try {
                        regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
                    } catch (Exception e) {
                        regItemRegistry = null;
                    }
                }

                // Setting the register
                RegItem regItemRegister = null;
                if (regItem != null && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                    List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
                    try {
                        regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                    } catch (Exception e) {
                        regItemRegister = null;
                    }
                }

                // Getting the uuid of the register to check the group. If the Register is 
                // empty, using the registry
                RegItem regItemCheck = null;

                if (regItem != null) {
                    if (regItemRegister == null && regItemRegistry != null) {
                        regItemCheck = regItem;
                    } else if (regItemRegister != null) {
                        regItemCheck = regItemRegister;
                    } else if (regItemRegister == null && regItemRegistry == null) {
                        regItemCheck = regItem;
                    }
                }

                // Getting the item, group, role mapping for this item
                List<RegItemRegGroupRegRoleMapping> itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);

                // Getting the user permission mapping from the session
                HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

                // Checking if the current user has the rights to add a new itemclass
                String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
                String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
                boolean permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
                boolean permissionRegisterRegistry = UserHelper.checkRegItemAction(actionRegisterRegistry, currentUserGroupsMap, itemMappings);

                // Getting the RegItemclass of the new RecItem
                // If the itemclass for the new item has not jet been defined, getting it
                RegItemclass newRegItemclass;
                if (newItemItemclassUuid == null || newItemItemclassUuid.length() < 1) {
                    // Getting the Itemclass of the new item
                    RegItemclass containerRegItemclass = (regItem != null) ? regItem.getRegItemclass() : null;
                    // Getting the child regItemclass of the container
                    List<RegItemclass> childItemclass = regItemclassManager.getChildItemclass(containerRegItemclass);
                    // For the RegItemclassType "Register and Item" there shall be just
                    // one child itemclass
                    newRegItemclass = childItemclass.get(0);
                } else {
                    newRegItemclass = regItemclassManager.get(newItemItemclassUuid);
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS_NEWITEM, newRegItemclass);

                // Checking permission, roles, mapped items and groups
                if (regItem != null && ( // Permission to add Items
                        ((newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) && permissionItemRegisterRegistry)
                        // Permission to add Register and Registry
                        || ((newRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) && permissionRegisterRegistry))) {

                    // Check if the request is a save request
                    if (formNewItemCheck != null && formNewItemCheck.length() > 0 && formNewItemCheck.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {

                        // Save request
                        RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

                        // Getting the request's parameters
                        Map requestParameters = request.getParameterMap();
                                              
                        // Dispatch the rerquest
                        try {
                            regItemproposedHandler.handleRegItemproposedAddition(requestParameters, regUser, localization);
                        } catch (Exception e) {
                            String operationResult = e.getMessage();
                            request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);
                            logger.error(e.getMessage(), e);
                        }

                        // Redirecting to the Item container page
                        response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + formRegItemContainerUuid);

                    } else {

                        // View request
                        // Getting the specific itemclass
                        try {
                            if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && (newItemItemclassUuid == null || newItemItemclassUuid.length() < 1)) {
                                // Add a register

                                // Get the child itemclass
                                List<RegItemclass> childRegItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());

                                // If the item is not a Registry (checked above) it can have only 1 child item
                                RegItemclass childItemclass = childRegItemclasses.get(0);
                                request.setAttribute(BaseConstants.KEY_REQUEST_CHILDITEMCLASS, childItemclass);

                                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGS, null);
                            } else {
                                // Add an item

                                // Get the child itemclass
                                List<RegItemclass> childRegItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());

                                // If the item is not a Registry (checked above) it can have only 1 child item
                                RegItemclass childItemclass;
                                if (newItemItemclassUuid == null) {
                                    childItemclass = childRegItemclasses.get(0);
                                } else {
                                    RegItemclass newItemclass = regItemclassManager.get(newItemItemclassUuid);
                                    childItemclass = newItemclass;
                                }

                                request.setAttribute(BaseConstants.KEY_REQUEST_CHILDITEMCLASS, childItemclass);

                                // #--- Getting the list of field for the specific itemclass ---# //
                                List<RegFieldmapping> regFieldmappings = regFieldmappingManager.getAll(childItemclass);
                                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGS, regFieldmappings);

                                // If it is a request to add a new register and the Itemclass
                                // for the new item has already been selected
                                if (newItemItemclassUuid != null && newItemItemclassUuid.length() > 0) {
                                    request.setAttribute(BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID, newItemItemclassUuid);
                                } else {
                                    request.setAttribute(BaseConstants.KEY_FORM_FIELD_NAME_NEWITEMREGITEMCLASSUUID, newRegItemclass.getUuid());
                                }
                            }

                            // Dispatch request
                            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDITEM + WebConstants.PAGE_URINAME_ADDITEM + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

                        } catch (Exception e) {
                            // Redirecting to the Item container page
                            response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + itemUuid);
                        }
                    }
                } else {
                    // Redirecting to the Item container page
                    response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + itemUuid);
                }
            } catch (NoResultException e) {
                // Redirecting to the Item container page
                response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + itemUuid);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex.getMessage(), ex);
        }
    }
}
