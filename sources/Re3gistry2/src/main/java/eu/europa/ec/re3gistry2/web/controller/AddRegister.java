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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemclassHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ADDREGISTER)
public class AddRegister extends HttpServlet {

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
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting request parameters
        String formLocalId = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        String formRegistryItemclassUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REGISTRYITEMCLASS_UUID);
        String formRegisterBaseUri = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REGISTER_BASEURI);

        String formLabel = request.getParameter(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
        String formContentsummary = request.getParameter(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID);

        String formRegisterOwner = request.getParameter(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER);
        String formRegisterManager = request.getParameter(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER);
        String formControlBody = request.getParameter(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY);
        String[] formSubmittingOrganizations = request.getParameterValues(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS);

        String registryItemUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        formLocalId = (formLocalId != null) ? InputSanitizerHelper.sanitizeInput(formLocalId) : null;
        formRegistryItemclassUuid = (formRegistryItemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(formRegistryItemclassUuid) : null;
        formRegisterBaseUri = (formRegisterBaseUri != null) ? InputSanitizerHelper.sanitizeInput(formRegisterBaseUri) : null;

        // Handling charset for the textual contents
        byte[] bytes;
        if (formLabel!=null) {
            bytes = formLabel.getBytes(StandardCharsets.ISO_8859_1);
            formLabel = new String(bytes, StandardCharsets.UTF_8);
            formLabel = InputSanitizerHelper.sanitizeInput(formLabel);
        }
        if (formContentsummary!=null) {
            bytes = formContentsummary.getBytes(StandardCharsets.ISO_8859_1);
            formContentsummary = new String(bytes, StandardCharsets.UTF_8);
            formContentsummary = InputSanitizerHelper.sanitizeInput(formContentsummary);
        }

        formRegisterOwner = (formRegisterOwner != null) ? InputSanitizerHelper.sanitizeInput(formRegisterOwner) : null;
        formRegisterManager = (formRegisterManager != null) ? InputSanitizerHelper.sanitizeInput(formRegisterManager) : null;
        formControlBody = (formControlBody != null) ? InputSanitizerHelper.sanitizeInput(formControlBody) : null;
        registryItemUuid = (registryItemUuid != null) ? InputSanitizerHelper.sanitizeInput(registryItemUuid) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;

        // Checking if the user has the rights to perform this action, otherwise
        // redirecting to the RegItemclasses list page
        if (permissionRegisterRegistry) {
            if (formLocalId != null && formLocalId.length() > 0) {
                // ## This is a save request
                String operationResult = null;
                try {
                    RegItemclass registerItemclass = null;

                    //Getting the Itemclass type Register
                    RegItemclasstype regItemclasstypeRegiter = regItemclasstypeManager.getByLocalid(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER);
                    RegItemclasstype regItemclasstypeItem = regItemclasstypeManager.getByLocalid(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM);

                    // Adding the new RegItemclass
                    RegItemclassHandler regItemclassHandler = new RegItemclassHandler();
                    operationResult = regItemclassHandler.saveNewRegItemclass(formLocalId, regItemclasstypeRegiter.getUuid(), formRegistryItemclassUuid, formRegisterBaseUri);

                    // Getting the Registry
                    // If no error happends in creating the Register Item class
                    // the item itemclass is created
                    if (operationResult == null) {

                        // Gettin the newly created Register itemclass
                        registerItemclass = regItemclassManager.getByLocalid(formLocalId);

                        // Adding a new Item Itemclass
                        String itemItemclass = formLocalId + "-item";
                        operationResult = regItemclassHandler.saveNewRegItemclass(itemItemclass, regItemclasstypeItem.getUuid(), registerItemclass.getUuid(), null);

                        if (operationResult == null) {
                            RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
                            regItemproposedHandler.newRegisterLite(registryItemUuid, formLocalId, registerItemclass, regUser, formLabel, formContentsummary, formRegisterOwner, formRegisterManager, formControlBody, formSubmittingOrganizations);
                        }
                    }

                } catch (NoResultException e) {
                    logger.error(e.getMessage(), e);
                    // Setting the operation success attribute
                    operationResult = localization.getString("error.generic");
                    request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);
                }

                if (operationResult == null) {
                    // Redirecting back to the browsing interface
                    operationResult = localization.getString("label.operationsuccess");
                    request.getSession().setAttribute(BaseConstants.KEY_OPERATION_SUCCESS, operationResult);
                    response.sendRedirect("." + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE);
                } else {
                    // Setting the operation result attribute
                    request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);
                }
            }

            // ## This is a view request
            //Getting the master language
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
            request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

            // Getting the language by parameter (if not available the master language is used)
            RegLanguagecode regLanguagecode = null;
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

            if (registryItemUuid != null && registryItemUuid.length() > 0) {
                try {
                    RegItem registryRegItem = regItemManager.get(registryItemUuid);
                    RegItemclass registryRegItemclass = registryRegItem.getRegItemclass();

                    request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRYITEM, registryRegItem);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRYITEMCLASS, registryRegItemclass);

                } catch (NoResultException e) {
                    // Redirecting to the main page because the request was missing a parameter
                    // or it encountered a problem in finding the Registry
                    response.sendRedirect("." + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE);
                }
            } else {
                // Redirecting to the main page because the request was missing a parameter
                response.sendRedirect("." + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE);
            }

            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDREGISTER + WebConstants.PAGE_URINAME_ADDREGISTER + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

        } else {
            // Redirecting to the RegItemclasses list page
            response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS);
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
