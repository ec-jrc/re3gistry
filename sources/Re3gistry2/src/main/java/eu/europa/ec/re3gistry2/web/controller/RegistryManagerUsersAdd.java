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
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.RandomString;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserCodesManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserHandler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserRegGrouprHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserCodes;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserRegGroupMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserUuidHelper;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS_ADD)
public class RegistryManagerUsersAdd extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Getting properties
        Properties properties = Configuration.getInstance().getProperties();

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // System localization
        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegUserManager regUserManager = new RegUserManager(entityManager);
        RegUserHandler regUserHandler = new RegUserHandler();
        RegUserRegGrouprHandler regUserRegGrouprHandler = new RegUserRegGrouprHandler();

        // Getting form parameter
        String formSubmitAction = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION);
        String name = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USER_NAME);
        String email = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_EMAIL);
        String ssoReference = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SSO_REFERENCE);
        String[] selectedgroups = request.getParameterValues(BaseConstants.KEY_FORM_FIELD_NAME_SELECTED_GROUPS_NEW_USER);

        formSubmitAction = (formSubmitAction != null) ? InputSanitizerHelper.sanitizeInput(formSubmitAction) : null;

        // Handling charset for the textual contents
        byte[] bytes;
        if (name != null) {
            bytes = name.getBytes(Charset.defaultCharset());
            name = new String(bytes, StandardCharsets.UTF_8);
            name = InputSanitizerHelper.sanitizeInput(name);
        }
        if (email != null) {
            email = email.toLowerCase();
            bytes = email.getBytes(Charset.defaultCharset());
            email = new String(bytes, StandardCharsets.UTF_8);
            email = InputSanitizerHelper.sanitizeInput(email);
        }
        if (ssoReference != null) {
            ssoReference = ssoReference.toLowerCase();
            bytes = ssoReference.getBytes(Charset.defaultCharset());
            ssoReference = new String(bytes, StandardCharsets.UTF_8);
            ssoReference = InputSanitizerHelper.sanitizeInput(ssoReference);
            email = email.toLowerCase();
        }

        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

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
        String[] actionManageSystem = {BaseConstants.KEY_USER_ACTION_MANAGESYSTEM};
        boolean permissionManageSystem = UserHelper.checkGenericAction(actionManageSystem, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        if (permissionManageSystem) {
            boolean result = false;
            if (formSubmitAction != null && formSubmitAction.length() > 0) {
                // This is a save request

                try {
                    RegUser newUser = new RegUser();
                    String newUUID = RegUserUuidHelper.getUuid(email);

                    try {
                        //Check if the user is already available (the email is the unique key)
                        regUserManager.get(newUUID);
                        request.setAttribute(BaseConstants.KEY_REQUEST_ERROR_MESSAGE, systemLocalization.getString("error.user.available"));

                    } catch (NoResultException e) {

                        // If it is not available, create it
                        newUser.setUuid(newUUID);
                        newUser.setEmail(email);
                        newUser.setName(name);
                        if (ssoReference != null && ssoReference.length() > 0) {
                            newUser.setSsoreference(ssoReference);
                        } else {
                            newUser.setSsoreference(email);
                        }
                        newUser.setInsertdate(new Date());
                        newUser.setEnabled(false);

                        String loginType = properties.getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE, BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO);
                        String key = "";
                        if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO)) {
                            // Generating random string
                            RandomString generateKey = new RandomString(23, new SecureRandom(), RandomString.alphanum);
                            key = generateKey.nextString();
                            UserHelper.generatePassword(newUser, key);
                        }

                        // Store the user
                        result = regUserHandler.addUser(newUser);

                        /**
                         * save group reference
                         */
                        if (selectedgroups != null && selectedgroups.length > 0) {
                            RegGroupManager regGroupManager = new RegGroupManager(entityManager);
                            for (String selectedgroup : selectedgroups) {
                                RegUserRegGroupMapping regUserRegGroupMapping = new RegUserRegGroupMapping();
                                final RegGroup group = regGroupManager.get(selectedgroup);
                                String newRegUserRegGroupUUID = RegUserRegGroupMappingUuidHelper.getUuid(newUser, group);
                                regUserRegGroupMapping.setUuid(newRegUserRegGroupUUID);
                                regUserRegGroupMapping.setRegUser(newUser);
                                regUserRegGroupMapping.setRegGroup(group);
                                regUserRegGroupMapping.setIsGroupadmin(Boolean.TRUE);
                                regUserRegGroupMapping.setInsertdate(new Date());

                                try {
                                    if (!entityManager.getTransaction().isActive()) {
                                        entityManager.getTransaction().begin();
                                    }
                                    entityManager.persist(regUserRegGroupMapping);
                                    entityManager.getTransaction().commit();
                                } catch (Exception ec) {
                                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersAdd class. Please check the details: " + ec.getMessage(), ec.getMessage());
                                }
                            }
                        }

                        //Generate activation and deletion codes
                        RegUserCodes codeActivation = new RegUserCodes(newUser.getUuid(), BaseConstants.KEY_USER_ACTION_ACTIVATE_USER, new Date());
                        RegUserCodes codeDeletion = new RegUserCodes(newUser.getUuid(), BaseConstants.KEY_USER_ACTION_DELETE_USER, new Date());

                        try {
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            //Persist both codes
                            entityManager.persist(codeActivation);
                            entityManager.persist(codeDeletion);
                            entityManager.getTransaction().commit();
                        } catch (Exception ec) {
                            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersAdd class. Please check the details: " + ec.getMessage(), ec.getMessage());
                        }

                        // Prepare the email email to the user with the generated key
                        String recipientString = newUser.getEmail();
                        InternetAddress[] recipient = {
                            new InternetAddress(recipientString)
                        };

                        String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_NEW_REGISTRATION);
                        String body;
                        final Properties configurationProperties = Configuration.getInstance().getProperties();

                        if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO)) {
                            String mailHost = configurationProperties.getProperty(BaseConstants.KEY_MAIL_HOST);
                            String activationUrl = mailHost.concat(WebConstants.EMAIL_URL).concat(WebConstants.PAGE_URINAME_ACTIVATE).concat("?").concat(BaseConstants.KEY_PROPERTY_CODE).concat("=" + codeActivation.getCode());
                            String deletionUrl = mailHost.concat(WebConstants.EMAIL_URL).concat(WebConstants.PAGE_URINAME_ACTIVATE).concat("?").concat(BaseConstants.KEY_PROPERTY_CODE).concat("=" + codeDeletion.getCode());

                            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_NEW_REGISTRATION);
                            body = (body != null)
                                    ? body.replace("{name}", name)
                                            .replace("{email}", email)
                                            .replace("{key}", key)
                                            .replace("{acceptLink}", activationUrl)
                                            .replace("{deleteLink}", deletionUrl)
                                    : "";
                        } else {
                            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_ECAS_NEW_REGISTRATION);
                            body = (body != null)
                                    ? body.replace("{name}", name)
                                    : "";
                        }

                        // Sending the mail
                        MailManager.sendMail(recipient, subject, body);

                    }
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersAdd class. Please check the details: " + e.getMessage(), e.getMessage());
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            }

            // This is a view request
            RegGroupManager regGroupManager = new RegGroupManager(entityManager);
            // Getting the list of all groups
            List<RegGroup> regGroups = regGroupManager.getAll();
            request.setAttribute(BaseConstants.KEY_REQUEST_REGGROUPS, regGroups);
            try {

                if (!result) {
                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } else {
                    // Redirecting to the   RegUser page
                    response.sendRedirect("." + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS);
                }
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersAdd class. Please check the details: " + e.getMessage(), e.getMessage());
                // Redirecting to the RegItemclasses list page
                response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
            }
        } else {
            response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
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
