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
import eu.europa.ec.re3gistry2.javaapi.handler.RegGroupHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegGroupUuidHelper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS_ADD)
public class RegistryManagerGroupsAdd extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();
        
        // Current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);
        
        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegGroupHandler regGroupHandler = new RegGroupHandler();

        // Getting form parameter
        String formSubmitAction = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION);
        String localId = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        String name = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_GROUP_NAME);
        String email = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_EMAIL);
        String website = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_WEBSITE);

        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        
        formSubmitAction = (formSubmitAction != null) ? InputSanitizerHelper.sanitizeInput(formSubmitAction) : null;
        localId = (localId != null) ? InputSanitizerHelper.sanitizeInput(localId) : null;
        
        // Handling charset for the textual contents
        byte[] bytes;
        if (name!=null) {
            bytes = name.getBytes(StandardCharsets.ISO_8859_1);
            name = new String(bytes, StandardCharsets.UTF_8);
            name = InputSanitizerHelper.sanitizeInput(name);
        }
        if (email!=null) {
            bytes = email.getBytes(StandardCharsets.ISO_8859_1);
            email = new String(bytes, StandardCharsets.UTF_8);
            email = InputSanitizerHelper.sanitizeInput(email);
        }
        if (website!=null) {
            bytes = website.getBytes(StandardCharsets.ISO_8859_1);
            website = new String(bytes, StandardCharsets.UTF_8);
            website = InputSanitizerHelper.sanitizeInput(website);
        }
        
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
                    RegGroup newGroup = new RegGroup();
                    String newUuid = RegGroupUuidHelper.getUuid(localId);
                    newGroup.setUuid(newUuid);
                    newGroup.setEmail(email);
                    newGroup.setLocalid(localId);
                    newGroup.setName(name);
                    newGroup.setWebsite(website);
                    newGroup.setInsertdate(new Date());

                    result = regGroupHandler.addGroup(newGroup,regUser);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            }

            // This is a view request
            try {

                if (!result) {
                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_GROUPS_ADD + WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS_ADD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } else {
                    // Redirecting to the   RegUser page
                    response.sendRedirect("." + WebConstants.PAGE_PATH_REGISTRYMANAGER_GROUPS + WebConstants.PAGE_URINAME_REGISTRYMANAGER_GROUPS);
                }
            } catch (Exception e) {
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
