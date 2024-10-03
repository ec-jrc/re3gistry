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
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserRegGroupMappingUuidHelper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS)
public class RegistryManagerUsers extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegUserManager regUserManager = new RegUserManager(entityManager);
        RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(entityManager);
        RegUserHandler regUserHandler = new RegUserHandler();
        RegGroupManager regGroupManager = new RegGroupManager(entityManager);

        //System localization
        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        // Getting form parameter
        String formRegUserUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USERUUID);
        String formSubmitAction = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION);
        String formName = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USER_NAME);

        formRegUserUuid = (formRegUserUuid != null) ? InputSanitizerHelper.sanitizeInput(formRegUserUuid) : null;
        formSubmitAction = (formSubmitAction != null) ? InputSanitizerHelper.sanitizeInput(formSubmitAction) : null;

        // Handling charset for the textual contents
        byte[] bytes;
        if (formName != null) {
            bytes = formName.getBytes(Charset.defaultCharset());
            formName = new String(bytes, StandardCharsets.UTF_8);
            formName = InputSanitizerHelper.sanitizeInput(formName);
        }

        // Getting request parameter
        String regUserDetailUUID = request.getParameter(BaseConstants.KEY_REQUEST_USERDETAIL_UUID);
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String actionType = request.getParameter(BaseConstants.KEY_REQUEST_ACTIONTYPE);

        String[] checkBoxCheckedNoFormat = request.getParameterValues(BaseConstants.KEY_REQUEST_CHECKED);
        String[] selectedRegGroupUUIDNoFormat = request.getParameterValues(BaseConstants.KEY_REQUEST_GROUP_UUID);
        String[] regUserRegGroupMappingUUIDNoFormat = request.getParameterValues(BaseConstants.KEY_REQUEST_USERGROUPMAPPING_UUID);
        List<String> checkBoxChecked = null;
        List<String> selectedRegGroupUUID = null;
        List<String> regUserRegGroupMappingUUID = null;

        if (checkBoxCheckedNoFormat != null) {
            checkBoxChecked = Arrays.asList(checkBoxCheckedNoFormat[0].split(","));
        }
        if (selectedRegGroupUUIDNoFormat != null) {
            selectedRegGroupUUID = Arrays.asList(selectedRegGroupUUIDNoFormat[0].split(","));
        }
        if (regUserRegGroupMappingUUIDNoFormat != null) {
            regUserRegGroupMappingUUID = Arrays.asList(regUserRegGroupMappingUUIDNoFormat[0].split(","));
        }

        regUserDetailUUID = (regUserDetailUUID != null) ? InputSanitizerHelper.sanitizeInput(regUserDetailUUID) : null;
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;
        actionType = (actionType != null) ? InputSanitizerHelper.sanitizeInput(actionType) : null;

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the current user from session
        RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);

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

            if (((formRegUserUuid != null && formRegUserUuid.length() > 0) || (regUserDetailUUID != null && regUserDetailUUID.length() > 0) || (regUserRegGroupMappingUUID != null && !regUserRegGroupMappingUUID.isEmpty())) && ((formSubmitAction != null && formSubmitAction.length() > 0) || (actionType != null && actionType.length() > 0))) {
                // This is a save request

                boolean result = false;
                try {

                    RegUser regUserDetail;
                    if (regUserDetailUUID != null && regUserDetailUUID.length() > 0) {
                        // Geting the regUserDetail
                        regUserDetail = regUserManager.get(regUserDetailUUID);

                        if (actionType != null && actionType.equals(BaseConstants.KEY_ACTION_TYPE_ENABLEUSER)) {
                            // Enable the user request
                            result = regUserHandler.toggleUserEnabled(regUserDetail, true);
                        } else if (actionType != null && actionType.equals(BaseConstants.KEY_ACTION_TYPE_DISABLEUSER)) {
                            // Disable the user request
                            if (!regUserDetail.getUuid().equals(currentUser.getUuid())) {
                                result = regUserHandler.toggleUserEnabled(regUserDetail, false);
                            }
                        } else if (formSubmitAction != null && formSubmitAction.length() > 0) {
                            // Update user name                        
                            regUserDetail.setName(formName);
                            result = regUserHandler.updateUser(regUserDetail);
                        } else if (actionType != null && actionType.equals(BaseConstants.KEY_ACTION_TYPE_REMOVEUSERGROUP)) {
                            List<String> addedGroups = new ArrayList<>();
                            List<String> removedGroups = new ArrayList<>();
                            if (checkBoxChecked != null) {
                                List<RegUserRegGroupMapping> activeGroupsList = regUserRegGroupMappingManager.getAll(regUserDetail);
                                for (int i = 0; i < checkBoxChecked.size(); i++) {
                                    if (checkBoxChecked.get(i).equals(BaseConstants.KEY_BOOLEAN_STRING_FALSE)) {
                                        // Removing the selected group from the user
                                        if (regUserRegGroupMappingUUID != null && !regUserRegGroupMappingUUID.isEmpty() && regUserRegGroupMappingUUID.size() >= i) {
                                            if (!regUserRegGroupMappingUUID.get(i).isEmpty()) {
                                                try {
                                                    RegUserRegGroupMapping regUserRegGroupMapping = regUserRegGroupMappingManager.get(regUserRegGroupMappingUUID.get(i));
                                                    for (RegUserRegGroupMapping r : activeGroupsList) {
                                                        if (r.getUuid().equals(regUserRegGroupMapping.getUuid())) {
                                                            result = regUserHandler.removeUserFromGroup(regUserRegGroupMapping);
                                                            if (selectedRegGroupUUID != null && !selectedRegGroupUUID.isEmpty() && selectedRegGroupUUID.size() >= i) {
                                                                RegGroup selectedRegGroup = regGroupManager.get(selectedRegGroupUUID.get(i));
                                                                String group = selectedRegGroup.getName();
                                                                removedGroups.add(group);
                                                                break;
                                                            }
                                                        }

                                                    }
                                                } catch (Exception e) {
                                                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerGroupsAdd class. Please check the details: " + e.getMessage(), e.getMessage());
                                                }
                                            }
                                        }
                                    } else {
                                        // Adding the selected group to the user
                                        if (selectedRegGroupUUID != null && !selectedRegGroupUUID.isEmpty() && selectedRegGroupUUID.size() >= i) {
                                            //RegUserRegGroupMapping regUserRegGroupMapping = regUserRegGroupMappingManager.get(regUserRegGroupMappingUUID.get(i));
                                            RegGroup selectedRegGroup = regGroupManager.get(selectedRegGroupUUID.get(i));
                                            RegUserRegGroupMapping newMapping = new RegUserRegGroupMapping();
                                            boolean b = false;
                                            for (RegUserRegGroupMapping r : activeGroupsList) {
                                                if (r.getRegGroup().getUuid().equals(selectedRegGroup.getUuid())) {
                                                    b = true;
                                                    break;
                                                }
                                            }
                                            if (!b) {
                                                String newUUID = RegUserRegGroupMappingUuidHelper.getUuid(regUserDetail, selectedRegGroup);
                                                newMapping.setUuid(newUUID);
                                                newMapping.setRegUser(regUserDetail);
                                                newMapping.setRegGroup(selectedRegGroup);
                                                newMapping.setIsGroupadmin(true);
                                                newMapping.setInsertdate(new Date());
                                                result = regUserHandler.addUserFromGroup(newMapping);
                                                String group = selectedRegGroup.getName();
                                                addedGroups.add(group);
                                            }

                                        } else {
                                            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersclass. Please check the details: " + "Failed retrieving selected regGroup", "Failed retrieving selected regGroup");
                                        }
                                    }
                                }
                            } else {
                                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsersclass. Please check the details: " + "Failed retrieving selected regGroup", "Couldn't retrieve checkboxes");
                               
                            }
                            //Send email to affected user/s
                            LinkedHashSet<InternetAddress> users = new LinkedHashSet<>();
                            users.add(new InternetAddress(regUserDetail.getEmail()));
                            if (!users.isEmpty()) {
                                InternetAddress[] recipient = new InternetAddress[users.size()];
                                users.toArray(recipient);
                                String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_GROUPSCHANGED);
                                subject = (subject != null)
                                        ? subject.replace("{contact}", systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED_ENDING_CONTACT_NAME))
                                        : "";

                                String body = "";
                                body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED);
                                body = (body != null)
                                        ? body.replace("{user}", regUserDetail.getName())
                                                .replace("{id}", regUserDetail.getEmail())
                                        : "";

                                if (!addedGroups.isEmpty()) {
                                    body += systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED_ADD);
                                    body = (body != null)
                                            ? body.replace("{name}", regUserDetail.getEmail())
                                            : "";
                                    for (int i = 0; i < addedGroups.size(); i++) {
                                        if (i == addedGroups.size() - 1) {
                                            body += addedGroups.get(i) + ". <br/>";
                                        } else {
                                            body += addedGroups.get(i) + ", ";
                                        }
                                    }
                                }

                                if (!removedGroups.isEmpty()) {
                                    body += systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED_REMOVE);
                                    body = (body != null)
                                            ? body.replace("{name}", regUserDetail.getEmail())
                                            : "";
                                    for (int y = 0; y < removedGroups.size(); y++) {
                                        if (y == removedGroups.size() - 1) {
                                            body += removedGroups.get(y) + ". ";
                                        } else {
                                            body += removedGroups.get(y) + ", ";
                                        }
                                    }
                                }
                                body += systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED_ENDING);

                                String value = request.getHeader("host");
                                value = value + "/re3gistry2/userProfile";
                                body = (body != null)
                                        ? body.replace("{page}", value)
                                                .replace("{name}", regUserDetail.getName())
                                                .replace("{contact}", systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_GROUPSCHANGED_ENDING_CONTACT_NAME))
                                        : "";
                                MailManager.sendMail(recipient, subject, body);
                                regUserHandler.closeEntityManager();
                            }
                        }
                    }
                } catch (Exception e) {
                    java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerUsers class. Please check the details: " + e.getMessage(), e.getMessage());
                    logger.error(e.getMessage(), e);
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            }

            // This is a view request
            try {

                // Detail request
                if (regUserDetailUUID != null && regUserDetailUUID.length() > 0) {

                    //Getting the regUser
                    RegUser regUserDetail = regUserManager.get(regUserDetailUUID);

                    // Getting the list of all groups
                    List<RegGroup> regGroups = regGroupManager.getAll();

                    // Getting the groups to which the user is associated
                    List<RegUserRegGroupMapping> regUserRegGroupMappings = regUserRegGroupMappingManager.getAll(regUserDetail);

                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERDETAIL, regUserDetail);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERSDETAIL, null);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGGROUPS, regGroups);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERREGGROUPMAPPINGS, regUserRegGroupMappings);

                } else {
                    // List request

                    // Getting the list of users
                    List<RegUser> regUsersDetail = regUserManager.getAll();

                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERDETAIL, null);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSERSDETAIL, regUsersDetail);
                }

                //Dispatch request
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

            } catch (Exception e) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within RegistryManagerGroupsAdd class. Please check the details: " + e.getMessage(), e.getMessage());;
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
