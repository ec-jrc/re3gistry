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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ADDITEMCLASS)
public class AddItemclass extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManager);
        
        // Init properties
        Configuration config = Configuration.getInstance();
        Properties properties = config.getProperties();
        
        // Load property to show/hide the system registers
        boolean showSystemRegisters = false;
        try{
            showSystemRegisters = Boolean.valueOf((String)properties.getProperty("application.systemregisters.show"));
        }catch(Exception e){
        }

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting request parameters
        String formLocalId = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        String formItemclassTypeUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID);
        String formParentItemclassUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID);
        String formBaseuri = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_BASEURI);

        String itemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        
        formLocalId = (formLocalId != null) ? InputSanitizerHelper.sanitizeInput(formLocalId) : null;
        formItemclassTypeUuid = (formItemclassTypeUuid != null) ? InputSanitizerHelper.sanitizeInput(formItemclassTypeUuid) : null;
        formParentItemclassUuid = (formParentItemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(formParentItemclassUuid) : null;
        formBaseuri = (formBaseuri != null) ? InputSanitizerHelper.sanitizeInput(formBaseuri) : null;
        itemclassUuid = (itemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(itemclassUuid) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;

        // Checking if the user has the rights to perform this action, otherwise
        // redirecting to the RegItemclasses list page
        if (permissionRegisterRegistry) {
            if (formLocalId != null && formLocalId.length() > 0) {
                // ## This is a save request
                String operationResult;

                // Adding the new RegItemclass
                RegItemclassHandler regItemclassHandler = new RegItemclassHandler();
                operationResult = regItemclassHandler.saveNewRegItemclass(formLocalId, formItemclassTypeUuid, formParentItemclassUuid, formBaseuri);

                // Setting the operation success attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_RESULT_MESSAGE, operationResult);

                if (operationResult == null) {
                    // Redirecting to the itemclass list page
                    response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS);
                }
            }

            // ## this is a view request
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

            // Getting the RegItemclass types to show in the addition form
            List<RegItemclasstype> regItemclasstypes = regItemclasstypeManager.getAll();
            request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSTYPES, regItemclasstypes);

            // Getting the RegItemclasses to show in the addition form
            List<RegItemclass> regItemclasses = regItemclassManager.getAll(showSystemRegisters);
            request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES, regItemclasses);

            // If a specific itemclass is passed by parameter, it is a request 
            // to add a child to that RegItemclass: getting the eventual RegItemclass parent
            if (itemclassUuid != null && itemclassUuid.length() > 0) {
                RegItemclass parentRegItemclass = regItemclassManager.get(itemclassUuid);
                request.setAttribute(BaseConstants.KEY_REQUEST_PARENTREGITEMCLASS, parentRegItemclass);
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_PARENTREGITEMCLASS, null);
            }

            // Getting the specific itemclass
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDITEMCLASS + WebConstants.PAGE_URINAME_ADDITEMCLASS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
