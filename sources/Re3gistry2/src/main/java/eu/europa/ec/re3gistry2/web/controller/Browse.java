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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_BROWSE)
public class Browse extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Init properties
        Configuration config = Configuration.getInstance();
        Properties properties = config.getProperties();

        // Load property to enable/disable the multi regisrty feature [not yet developed]
        boolean allowMultiRegistry = false;
        String mainRegistryLocalId = null;
        String mainRegistryItemclassLocalId = null;
        try {
            allowMultiRegistry = Boolean.valueOf(properties.getProperty("application.multiregistry.allow"));
            mainRegistryItemclassLocalId = properties.getProperty("application.multiregistry.mainregistryitemclasslocalid");
            mainRegistryLocalId = properties.getProperty("application.multiregistry.mainregistrylocalid");
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Browse class. Please check the details: " + e.getMessage(), e.getMessage());
            
        }

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Instantiating managers
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Preparing variables   
        String itemUuid;
        String languageUuid;
        String formItemUuid;

        // Loading the needed RegRelation predicates (to create the URI)
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

        // Geting parameters
        itemUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        formItemUuid = request.getParameter(BaseConstants.KEY_REQUEST_FORM_ITEMUUID);
        
        itemUuid = (itemUuid != null) ? InputSanitizerHelper.sanitizeInput(itemUuid) : null;
        formItemUuid = (formItemUuid != null) ? InputSanitizerHelper.sanitizeInput(formItemUuid) : null;

        if (itemUuid == null || itemUuid.length() < 1) {
            itemUuid = formItemUuid;
        }

        // Init variables for the items
        RegItem regItem = null;
        RegItemproposed regItemproposed = null;
        
        // Init variables to check the permissions
        RegItem regItemCheck = null;
        RegItemproposed regItemproposedCheck = null;
        
        // If no item parameter is specified, take the list of registries or the
        // single registry in case of multi registry disabled
        List<RegItem> registryList;
        if (itemUuid == null || itemUuid.length() <= 0) {

            // Get the itemclass of type registry
            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManager);
            RegItemclasstype regItemclasstypeRegistry = regItemclasstypeManager.getByLocalid(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY);

            // Get all the items (itemclasstype registry)
            registryList = regItemManager.getAllActive(regItemclasstypeRegistry);

            // If there are more than one registy and if the multi registry is enabled
            if (registryList.size() > 1 && allowMultiRegistry) {
                request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRYLIST, registryList);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM, null);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED, null);
            } else {
                // If the list contains just one registry, or the multi registry
                // is disabled return the registry
                request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRYLIST, null);
                // The first registry is taken because in case of multiregistry disabled,
                // just 1 registry item class shall be "active=true"
                regItem = registryList.get(0);
                regItemCheck = regItem;
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM, regItem);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED, null);
            }
        } else {
            // If the itemUuid is specified, return the item
            request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRYLIST, null);
            
            try {
                regItem = regItemManager.get(itemUuid);
            } catch (NoResultException e) {
                // Check if it is a request for a new item (proposed)
                try {
                    regItemproposed = regItemproposedManager.get(itemUuid);
                } catch (NoResultException e1) {
                    // If the itemUuid passed by parameter is not related to RegItem
                    // or RegItemproposed, redirecting to the main browse page.
                    response.sendRedirect("." + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE);
                }
            }
            
            if (regItem != null) {
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM, regItem);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED, null);
            } else if (regItemproposed != null) {
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM, null);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED, regItemproposed);
            }
        }

        // Setting the registry
        RegItem regItemRegistry = null;
        if (regItem != null && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
            try {
                regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegistry = null;
            }
        } else if (regItemproposed != null && !regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            List<RegRelationproposed> regRelationRegistries = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegistry);
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
        } else if (regItemproposed != null && !regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
            List<RegRelationproposed> regRelationproposedRegisters = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
            try {
                regItemRegister = regRelationproposedRegisters.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegister = null;
            }
        }

        // Getting the uuid of the register to check the group. If the Register is 
        // empty, using the registry

        if (regItem != null) {
            if (regItemRegister == null && regItemRegistry != null) {
                regItemCheck = regItem;
            } else if (regItemRegister != null) {
                regItemCheck = regItemRegister;
            } else if (regItemRegister == null && regItemRegistry == null) {
                regItemCheck = regItem;
            }
        } else {
            if (regItemRegister == null && regItemRegistry != null) {
                regItemproposedCheck = regItemproposed;
            } else if (regItemRegister != null) {
                regItemCheck = regItemRegister;
            } else if (regItemRegister == null && regItemRegistry == null) {
                regItemproposedCheck = regItemproposed;
            }
        }

        // Getting the item, group, role mapping for this item
        List<RegItemRegGroupRegRoleMapping> itemMappings = null;
        List<RegItemproposedRegGroupRegRoleMapping> itemProposedMappings = null;
        if (regItemproposedCheck == null) {
            itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);
            // If the item is not a registry, adding the registry to the itemMappings in
            // order to allow the registry managers to manage the contained registers and items
            List<RegItemRegGroupRegRoleMapping> itemMappingsRegistry = regItemRegGroupRegRoleMappingManager.getAll(regItemRegistry);
            itemMappings.addAll(itemMappingsRegistry);
        } else {
            itemProposedMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemproposedCheck);
        }

        // Checking if the current user has the rights to add a new itemclass
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

        // Setting request character encoding
        request.setCharacterEncoding("UTF-8");

        try {
            // Getting the current user
            RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

            // Getting all the languages
            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
            List<RegLanguagecode> regLanguagecodes = regLanguagecodeManager.getAllActive();
            request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES, regLanguagecodes);

            // Getting the specific language via request parameter
            languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

            // Check if the request is a save request
            if (formItemUuid != null && formItemUuid.length() > 0) {

                // *** Handling the save request *** //
                // Checking the user role and related permission on RegItems
                boolean saveError = false;
                if (regItem != null && ( // Permission to edit Registers and items
                        ((regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) && permissionItemRegisterRegistry)
                        // Permission to edit Registry
                        || (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && permissionRegisterRegistry))) {

                    // Save request for RegItem
                    RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

                    // Getting the request's parameters
                    Map requestParameters = request.getParameterMap();

                    // Dispatch the rerquest
                    try {
                        regItemproposedHandler.handleRegItemproposedSave(requestParameters, regUser);
                    } catch (Exception e) {
                        java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Browse class. Please check the details: " + e.getMessage(), e.getMessage());
                        saveError = true;
                    }
                } else if (regItemproposed != null && ( // Permission to edit Registers and items
                        ((regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) || regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) && permissionItemRegisterRegistry)
                        // Permission to edit Registry
                        || (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && permissionRegisterRegistry))) {

                    // Save request for RegItemproposed
                    RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

                    // Getting the request's parameters
                    Map requestParameters = request.getParameterMap();

                    // Dispatch the rerquest
                    try {
                        regItemproposedHandler.handleRegItemproposedNewSave(requestParameters, regUser);
                    } catch (Exception e) {
                        java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Browse class. Please check the details: " + e.getMessage(), e.getMessage());
                        saveError = true;
                    }
                }
                
                if(saveError){
                    request.getSession().setAttribute(BaseConstants.KEY_ERROR_GENERIC, true);
                    request.getSession().setAttribute(BaseConstants.KEY_OPERATION_SUCCESS, false);
                }else{
                    request.getSession().setAttribute(BaseConstants.KEY_ERROR_GENERIC, false);
                    request.getSession().setAttribute(BaseConstants.KEY_OPERATION_SUCCESS, true);
                }
                                
                // Redirecting to the Item container page
                response.sendRedirect("." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + itemUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid);

            }

            // *** Handling the view request *** //
            // Getting the master language
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
            request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

            // Getting the language by parameter (if not available the master language is used)
            RegLanguagecode regLanguagecode;
            if (languageUuid != null && languageUuid.length() == 2) {
                try {
                    regLanguagecode = regLanguagecodeManager.get(languageUuid);
                } catch (Exception e) {
                    regLanguagecode = masterLanguage;
                }
            } else {
                regLanguagecode = masterLanguage;
            }
            request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, regLanguagecode);

            // Getting the eventual RegItemproposed related to this item
            if (regItem != null) {
                try {
                    regItemproposed = regItemproposedManager.getByRegItemReference(regItem);
                    request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSED, regItemproposed);

                    // Get all the localization for that specific item proposed
                    // This is needed to highlight the tab of the language that has some changes
                    RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
                    List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemproposed);
                    // Create a map with the list of languages with changes
                    List<String> changedLanguages = new ArrayList();
                    regLocalizationproposeds.forEach(temp -> {
                        changedLanguages.add(temp.getRegLanguagecode().getUuid());
                    });
                    request.setAttribute(BaseConstants.KEY_REQUEST_LOCALIZATIONPROPOSED_CHANGEDLANGUAGES, changedLanguages);

                } catch (NoResultException e) {
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Browse class. Please check the details: " + e.getMessage(), e.getMessage());
            response.sendRedirect(".");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
