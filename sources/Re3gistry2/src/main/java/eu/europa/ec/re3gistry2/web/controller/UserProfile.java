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
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_USERPROFILE)
public class UserProfile extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // System localization
        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(entityManager);
        RegUserManager regUserManager = new RegUserManager(entityManager);
        RegUserHandler regUserHandler = new RegUserHandler();

        // Getting form parameter
        String formSubmitAction = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION);
        String formRegUserUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USERUUID);
        String formName = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USER_NAME);
        
        formSubmitAction = (formSubmitAction != null) ? InputSanitizerHelper.sanitizeInput(formSubmitAction) : null;
        formRegUserUuid = (formRegUserUuid != null) ? InputSanitizerHelper.sanitizeInput(formRegUserUuid) : null;
        formName = (formName != null) ? InputSanitizerHelper.sanitizeInput(formName) : null;
        
        // Getting request parameter
        String actionType = request.getParameter(BaseConstants.KEY_REQUEST_ACTIONTYPE);      
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        
        actionType = (actionType != null) ? InputSanitizerHelper.sanitizeInput(actionType) : null;
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;

        String currentKey = request.getParameter(BaseConstants.KEY_REQUEST_USER_OLD_PASSWORD);
        String newPassword = request.getParameter(BaseConstants.KEY_REQUEST_USER_NEW_PASSWORD);
        
        currentKey = (currentKey != null) ? InputSanitizerHelper.sanitizeInput(currentKey) : null;
        newPassword = (newPassword != null) ? InputSanitizerHelper.sanitizeInput(newPassword) : null;

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the current user from session
        RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);
        String regUserDetailUUID = currentUser.getUuid();

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode currentLanguage;
        if (languageUUID != null && languageUUID.length() == 2) {
            try {
                currentLanguage = regLanguagecodeManager.get(languageUUID);
            } catch (Exception e) {
                currentLanguage = masterLanguage;
            }
        } else {
            currentLanguage = masterLanguage;
        }
        request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, currentLanguage);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to manage users
        String[] manageItemProposal = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL};
        boolean permissionManageSystem = UserHelper.checkGenericAction(manageItemProposal, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        //if (permissionManageSystem) {
            if (((formRegUserUuid != null && formRegUserUuid.length() > 0)
                    || (regUserDetailUUID != null && regUserDetailUUID.length() > 0) //                    || (regUserRegGroupMappingUUID != null && regUserRegGroupMappingUUID.length() > 0)
                    )
                    && ((formSubmitAction != null && formSubmitAction.length() > 0)
                    || (actionType != null && actionType.length() > 0))) {

                // This is a view request
                boolean result = false;
                try {

                    RegUser regUserDetail;
                    if (regUserDetailUUID != null && regUserDetailUUID.length() > 0) {
                        // Geting the regUserDetail
                        regUserDetail = regUserManager.get(regUserDetailUUID);
                        if (actionType != null && actionType.equals(BaseConstants.KEY_ACTION_TYPE_CHANGEPASSWORD)) {
                            // change password
                            try {

                                if (UserHelper.checkCurrentUserKey(regUserDetail.getEmail(), currentKey)) {
                                    UserHelper.generatePassword(regUserDetail, newPassword);
                                    result = regUserHandler.updateUser(regUserDetail);
                                    request.setAttribute(BaseConstants.KEY_REQUEST_USER_SUCCESS_MESSAGES, systemLocalization.getString("passwrod.updated"));
                                    request.getSession().setAttribute(BaseConstants.KEY_SESSION_USER, regUserDetail);
                                } else {
                                    request.setAttribute(BaseConstants.KEY_REQUEST_ERROR_MESSAGE, systemLocalization.getString("login.text.error.credentialerror"));
                                }

                            } catch (Exception e) {
                                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within UserProfile class. Please check the details: " + e.getMessage(), e.getMessage());
                                request.setAttribute(BaseConstants.KEY_REQUEST_ERROR_MESSAGE, systemLocalization.getString("login.text.error.credentialerror"));
                            }

                        } else if (formSubmitAction != null && formSubmitAction.length() > 0) {
                            // Update user name                        
                            regUserDetail.setName(formName);
                            result = regUserHandler.updateUser(regUserDetail);
                            request.getSession().setAttribute(BaseConstants.KEY_SESSION_USER, regUserDetail);
                            request.setAttribute(BaseConstants.KEY_REQUEST_USER_SUCCESS_MESSAGES, systemLocalization.getString("name.updated"));
                        }
                    }

                    //Getting the list of groups to which the user is associated
                    List<RegUserRegGroupMapping> regUserRegGroupMappings = regUserRegGroupMappingManager.getAll(currentUser);

                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS, regUserRegGroupMappings);

                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_USERPROFILE + WebConstants.PAGE_URINAME_USERPROFILE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within UserProfile class. Please check the details: " + e.getMessage(), e.getMessage());
                    // Redirecting to the index page
                    response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
                }

                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            }

            // This is a view request
            try {

                // Detail request
                if (regUserDetailUUID != null && regUserDetailUUID.length() > 0) {

                    //Getting the regUser
                    RegUser regUserDetail = regUserManager.get(regUserDetailUUID);

                    //Getting the list of groups to which the user is associated
                    List<RegUserRegGroupMapping> regUserRegGroupMappings = regUserRegGroupMappingManager.getAll(currentUser);

                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS, regUserRegGroupMappings);

                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_USERPROFILE + WebConstants.PAGE_URINAME_USERPROFILE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

                } else {
                    // List request

                }

                //Dispatch request
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

            } catch (Exception e) {
               java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within UserProfile class. Please check the details: " + e.getMessage(), e.getMessage());
                // Redirecting to the RegItemclasses list page
                response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
            }
        //} else {
        //    response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
        //}

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
