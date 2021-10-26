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
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemclassHandler;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet(WebConstants.PAGE_URINAME_ITEMCLASS)
public class Itemclass extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init properties
        Configuration config = Configuration.getInstance();
        Properties properties = config.getProperties();

        // Load property to show/hide the system registers
        boolean showSystemRegisters = false;
        try {
            showSystemRegisters = Boolean.valueOf((String) properties.getProperty("application.systemregisters.show"));
        } catch (Exception e) {
        }

        // Instantiating managers
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);

        // Getting all the languages
        List<RegLanguagecode> regLanguagecodes = regLanguagecodeManager.getAllActive();
        request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES, regLanguagecodes);

        // Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to manage RegItemclass
        String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting request parameters
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String itemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String itemclassNewPosition = request.getParameter(BaseConstants.KEY_REQUEST_NEWPOSITION);
        String action = request.getParameter(BaseConstants.KEY_REQUEST_ACTION);
        String fromFieldPage = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_REMOVEFIELD);

        itemclassUuid = (itemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(itemclassUuid) : null;
        itemclassNewPosition = (itemclassNewPosition != null) ? InputSanitizerHelper.sanitizeInput(itemclassNewPosition) : null;
        action = (action != null) ? InputSanitizerHelper.sanitizeInput(action) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;
        fromFieldPage = (fromFieldPage != null) ? InputSanitizerHelper.sanitizeInput(fromFieldPage) : null;

        boolean operationSuccess = false;

        if (!permissionRegisterRegistry) {
            // In case of unauthorized user, redirecting to the Index page
            response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_INDEX);
        }

        if (action != null && action.equals(BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE)) {
            //Checking if the user has the rights to perform this action
            if (permissionRegisterRegistry) {

                // Removing the itemclass
                RegItemclassHandler regItemclassHandler = new RegItemclassHandler();
                RegItemclass regItemclass = regItemclassManager.get(itemclassUuid);
                operationSuccess = regItemclassHandler.removeRegItemclass(regItemclass, request);

//                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationSuccess);
            } else {
                // In case of unauthorized user, redirecting to the RegItemclasses page
                response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS);
            }
        } else if (itemclassNewPosition != null && itemclassNewPosition.length() > 0) {
            // ## This is a reorder request (change the "procedure order" of the RegItemclasses)

            //Checking if the user has the rights to perform this action
            if (permissionRegisterRegistry) {
                // Creating the int value of the new position parameter
                int newPositionValue = -1;
                try {
                    newPositionValue = Integer.parseInt(itemclassNewPosition);
                } catch (Exception e) {
                }

                // Updating the procedure order
                RegItemclassHandler regItemclassHandler = new RegItemclassHandler();
                regItemclassHandler.reorderRegItemclass(itemclassUuid, newPositionValue);

                //This operations comes from an Ajax request. No redirect needed.            
            } else {
                // In case of unauthorized user, redirecting to the RegItemclasses page
                response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS);
            }
        }
        // ## This is a view request

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode currentLanguage;
        if (languageUuid != null && languageUuid.length() == 2) {
            try {
                currentLanguage = regLanguagecodeManager.get(languageUuid);
            } catch (Exception e) {
                currentLanguage = masterLanguage;
            }
        } else {
            currentLanguage = masterLanguage;
        }
        // Setting the current language attribute to the request
        request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, currentLanguage);

        // #--- Getting the list of itemclasses ---# //
        List<RegItemclass> regItemclasses = regItemclassManager.getAll(showSystemRegisters);

        // Divide the itemclass by type
        List<RegItemclass> registryRegItemclasses = new ArrayList<>();
        List<RegItemclass> registerRegItemclasses = new ArrayList<>();
        List<RegItemclass> itemRegItemclasses = new ArrayList<>();

        for (RegItemclass tmp : regItemclasses) {
            switch (tmp.getRegItemclasstype().getLocalid()) {
                case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                    registryRegItemclasses.add(tmp);
                    break;
                case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                    registerRegItemclasses.add(tmp);
                    break;
                case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                    itemRegItemclasses.add(tmp);
                    break;
                default:
                    break;
            }
        }

        request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_REGITEMCLASSES, registryRegItemclasses);
        request.setAttribute(BaseConstants.KEY_REQUEST_REGISTER_REGITEMCLASSES, registerRegItemclasses);
        request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_REGITEMCLASSES, itemRegItemclasses);

        request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES, regItemclasses);

        // Dispatch request
        if (fromFieldPage != null && !operationSuccess) {
            request.getRequestDispatcher("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + itemclassUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid).forward(request, response);
        } else {
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
