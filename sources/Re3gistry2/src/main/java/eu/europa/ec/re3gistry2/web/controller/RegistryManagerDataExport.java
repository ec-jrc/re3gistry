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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.cache.CacheAll;
import eu.europa.ec.re3gistry2.javaapi.cache.EhCache;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import eu.europa.ec.re3gistry2.migration.manager.MigrationManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT)
public class RegistryManagerDataExport extends HttpServlet {

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

        // Getting form parameter
        String startIndex = request.getParameter(BaseConstants.KEY_REQUEST_STARTINDEX);
        String startCaching = request.getParameter(BaseConstants.KEY_REQUEST_STARTCACHING);
        String startCachingMasterLanguage = request.getParameter(BaseConstants.KEY_REQUEST_STARTCACHING_MASTERLANGUAGE);
        String removeCaching = request.getParameter(BaseConstants.KEY_REQUEST_REMOVECACHING);

        // Getting request parameter
        String regUserDetailUUID = request.getParameter(BaseConstants.KEY_REQUEST_USERDETAIL_UUID);
        String regUserRegGroupMappingUUID = request.getParameter(BaseConstants.KEY_REQUEST_USERGROUPMAPPING_UUID);
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String actionType = request.getParameter(BaseConstants.KEY_REQUEST_ACTIONTYPE);
        String cacheClassUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);

        startIndex = (startIndex != null) ? InputSanitizerHelper.sanitizeInput(startIndex) : null;
        startCaching = (startCaching != null) ? InputSanitizerHelper.sanitizeInput(startCaching) : null;
        startCachingMasterLanguage = (startCachingMasterLanguage != null) ? InputSanitizerHelper.sanitizeInput(startCachingMasterLanguage) : null;
        regUserDetailUUID = (regUserDetailUUID != null) ? InputSanitizerHelper.sanitizeInput(regUserDetailUUID) : null;
        regUserRegGroupMappingUUID = (regUserRegGroupMappingUUID != null) ? InputSanitizerHelper.sanitizeInput(regUserRegGroupMappingUUID) : null;
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
            ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();
            if (startIndex != null && startIndex.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                // This is a save request

                String subject;
                String body;
                boolean result;
                try {
                    SolrHandler.indexComplete();
                    subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_SOLR_SUCCESS);
                    body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_SOLR_SUCCESS);
                    result = true;
                } catch (Exception e) {
                    subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_SOLR_ERROR);
                    body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_SOLR_ERROR) + e.getMessage();
                    result = false;
                }
                sendMail(request, subject, body);

                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            } else if ((startCaching != null && startCaching.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE))
                    || (startCachingMasterLanguage != null && startCachingMasterLanguage.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE))) {
                // This is a save request

                ItemCache cache = (ItemCache) request.getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
                if (cache == null) {
                    cache = new EhCache();
                    request.setAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY, cache);
                }
//                cache.removeAll();

                String subject;
                String body;
                CacheAll cacheall;
                if (startCaching != null) {
                    cacheall = new CacheAll(entityManager, cache, null);
                } else {
                    cacheall = new CacheAll(entityManager, cache, regLanguagecodeManager.getMasterLanguage());
                }
                boolean result;

                try {
                    if (cacheClassUUID!=null) {
                        cacheall.run(cacheClassUUID);
                    } else {
                        cacheall.run();
                    }
                    subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_CACHE_SUCCESS);
                    body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_CACHE_SUCCESS);
                    result = true;
                } catch (Exception e) {
                    subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_CACHE_ERROR);
                    body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_CACHE_ERROR);
                    result = false;
                }
                sendMail(request, subject, body);

                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);
            } else if (removeCaching != null && removeCaching.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                ItemCache cache = (ItemCache) request.getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
                if (cache == null) {
                    cache = new EhCache();
                    request.setAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY, cache);
                }
                boolean result;
                try {
                    cache.removeAll();
                    result = true;
                } catch (Exception e) {
                    result = false;
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT, result);

                String operationResult = systemLocalization.getString(BaseConstants.KEY_OPERATION_REMOVE_CACHE_SUCCESS);
                // Setting the operation success attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE, operationResult);

            }

            // This is a view request
            try {

                //Dispatch request
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_DATAEXPORT + WebConstants.PAGE_URINAME_REGISTRYMANAGER_DATAEXPORT + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
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

    public void sendMail(HttpServletRequest request, String subject, String body) throws Exception {
        /**
         * get the user from the session
         */
        HttpSession session = request.getSession();
        RegUser currentRegUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);
        /**
         * send email
         */
        String recipientString = currentRegUser.getEmail();
        InternetAddress[] recipient = {
            new InternetAddress(recipientString)
        };

        MailManager.sendMail(recipient, subject, body);
    }

}
