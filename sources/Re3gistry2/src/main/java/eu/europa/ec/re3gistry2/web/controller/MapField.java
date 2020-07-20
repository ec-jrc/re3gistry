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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldtypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegFieldmappingHandler;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegFieldtype;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_MAPFIELD)
public class MapField extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionManageFieldMapping = {BaseConstants.KEY_USER_ACTION_MANAGEFIELDMAPPING};
        boolean permissionManageFieldMapping = UserHelper.checkGenericAction(actionManageFieldMapping, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        // Getting request parameters
        String formFieldUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_FIELDUUID);
        String formItemclassUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSUUID);

        String fieldmappingUuid = request.getParameter(BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID);
        String itemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        formFieldUuid = (formFieldUuid != null) ? InputSanitizerHelper.sanitizeInput(formFieldUuid) : null;
        formItemclassUuid = (formItemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(formItemclassUuid) : null;
        fieldmappingUuid = (fieldmappingUuid != null) ? InputSanitizerHelper.sanitizeInput(fieldmappingUuid) : null;
        itemclassUuid = (itemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(itemclassUuid) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;

        String operationResult;
        if (fieldmappingUuid != null && fieldmappingUuid.length() > 0) {
            // ## This is a request to remove an existing RegFieldmapping            

            //Checking if the user has the rights to remove RegFieldmapping
            if (permissionManageFieldMapping) {

                // Removing the RegFieldmapping
                RegFieldmappingHandler regFieldmappingHandler = new RegFieldmappingHandler();
                operationResult = regFieldmappingHandler.removeRegFieldMapping(fieldmappingUuid);

                // Setting the operation success attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);

                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);

            } else {
                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
            }
        } else if (formFieldUuid != null && formFieldUuid.length() > 0) {
            // ## This is a request to save new RegFieldmapping

            //Checking if the user has the rights to save new RegFieldmapping
            if (permissionManageFieldMapping) {

                // Adding the new Regfieldmapping
                RegFieldmappingHandler regFieldmappingHandler = new RegFieldmappingHandler();
                operationResult = regFieldmappingHandler.addNewRegFieldMapping(formItemclassUuid, formFieldUuid);

                // Setting the operation success attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);

                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
            } else {
                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
            }
        } else {
            // ## This is a view request

            if (itemclassUuid != null && itemclassUuid.length() > 0 && permissionManageFieldMapping) {
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
                request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, currentLanguage);

                // Getting the itemclass to map the field
                try {
                    RegItemclass regItemclass = regItemclassManager.get(itemclassUuid);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS, regItemclass);

                    // Getting the fieldmappings for the current itemclass
                    List<RegFieldmapping> listRegFieldmappingsItemclass = regFieldmappingManager.getAll(regItemclass);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGSITEMCLASS, listRegFieldmappingsItemclass);
                } catch (Exception e) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS, null);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGSITEMCLASS, null);
                }

                // Getting the list of the itemclasses
                try {
                    List<RegItemclass> regItemclasses = regItemclassManager.getAll();
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES, regItemclasses);
                } catch (Exception e) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS, null);
                }

                // Getting all the fieldmappings
                List<RegField> regFields = regFieldManager.getAll();
                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDS, regFields);

                // Getting all the field types
                List<RegFieldtype> regFieldtypes = regFieldtypeManager.getAll();
                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDTYPES, regFieldtypes);

                // JSP
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            } else {
                response.sendRedirect("." + WebConstants.PAGE_URINAME_ITEMCLASS);
            }
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
