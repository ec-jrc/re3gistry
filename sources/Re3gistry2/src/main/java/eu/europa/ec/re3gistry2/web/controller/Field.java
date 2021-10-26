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

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.InputSanitizerHelper;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegFieldHandler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegFieldmappingHandler;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_FIELD)
public class Field extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Instantiating managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionManageFieldMapping = {BaseConstants.KEY_USER_ACTION_MANAGEFIELDMAPPING};
        String[] actionManageField = {BaseConstants.KEY_USER_ACTION_MANAGEFIELD};
        boolean permissionManageFieldMapping = UserHelper.checkGenericAction(actionManageFieldMapping, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
        boolean permissionManageField = UserHelper.checkGenericAction(actionManageField, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting all the languages
        List<RegLanguagecode> regLanguagecodes = regLanguagecodeManager.getAllActive();
        request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES, regLanguagecodes);

        // Getting reorder parameter
        String formNewPosition = request.getParameter(BaseConstants.KEY_REQUEST_NEWPOSITION);
        String formEditField = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD);
        String formLanguagecodeUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LANGUAGEUUID);
        String formValue = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_VALUE);
        String formChecked = request.getParameter(BaseConstants.KEY_REQUEST_CHECKED);
        String formCheckboxType = request.getParameter(BaseConstants.KEY_REQUEST_CHECKBOXTYPE);

        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String itemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String localizationUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALIZATIONUUID);
        String fieldUuid = request.getParameter(BaseConstants.KEY_REQUEST_FIELDUUID);
        
        formNewPosition = (formNewPosition != null) ? InputSanitizerHelper.sanitizeInput(formNewPosition) : null;
        formEditField = (formEditField != null) ? InputSanitizerHelper.sanitizeInput(formEditField) : null;
        formLanguagecodeUuid = (formLanguagecodeUuid != null) ? InputSanitizerHelper.sanitizeInput(formLanguagecodeUuid) : null;
        
        // Handling charset for the textual contents
        byte[] bytes;
        if (formValue!=null) {
            bytes = formValue.getBytes(StandardCharsets.ISO_8859_1);
            formValue = new String(bytes, StandardCharsets.UTF_8);
            formValue = InputSanitizerHelper.sanitizeInput(formValue);
        }        
        
        formChecked = (formChecked != null) ? InputSanitizerHelper.sanitizeInput(formChecked) : null;
        formCheckboxType = (formCheckboxType != null) ? InputSanitizerHelper.sanitizeInput(formCheckboxType) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;
        itemclassUuid = (itemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(itemclassUuid) : null;
        localizationUuid = (localizationUuid != null) ? InputSanitizerHelper.sanitizeInput(localizationUuid) : null;
        fieldUuid = (fieldUuid != null) ? InputSanitizerHelper.sanitizeInput(fieldUuid) : null;        

        if (!permissionManageFieldMapping && !permissionManageField) {
            // In case of unauthorized user, redirecting to the Index page
            response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_INDEX);
        }

        String operationResult;
        if (formCheckboxType != null && formCheckboxType.length() > 0) {
            // ## This is a checkbox update request

            // Checking if the user has the rights to change the RegFieldmapping (properties)
            if (permissionManageFieldMapping) {
                // Getting the boolean value from string parameter
                boolean checkedBoolean = (formChecked.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));

                // Updating the checkbox
                RegFieldmappingHandler regFieldmappingHandler = new RegFieldmappingHandler();
                operationResult = regFieldmappingHandler.updateCheckbox(fieldUuid, itemclassUuid, formCheckboxType, checkedBoolean);

                // Setting the operation result attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);
            }
        } else if (formEditField != null && formEditField.length() > 0) {
            // ## This is a request to edit a RegField (translation) request

            // Checking if the user has the rights to edit a RegField
            if (permissionManageField) {

                // Updating the field                
                RegFieldHandler regFieldHandler = new RegFieldHandler();
                operationResult = regFieldHandler.editRegField(fieldUuid, localizationUuid, formLanguagecodeUuid, formValue);

                // Setting the operation result attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);
                // Redirecting to the itemclass list page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + itemclassUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid + "&" + BaseConstants.KEY_REQUEST_FIELDUUID + "=" + fieldUuid);
            } else {
                // Redirecting to the itemclass list page
                response.sendRedirect("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + itemclassUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid + "&" + BaseConstants.KEY_REQUEST_FIELDUUID + "=" + fieldUuid);
            }
        } else if (formNewPosition != null && formNewPosition.length() > 0) {
            // ## This is a reorder request (to change the list order of the RegFieldmapping)

            //Checking if the user has the rights to change the listorder of a RegFieldmapping
            if (permissionManageFieldMapping) {
                int newPositionValue = -1;
                try {
                    newPositionValue = Integer.parseInt(formNewPosition);
                } catch (Exception e) {
                }

                // Updating the order
                RegFieldmappingHandler regFieldmappingHandler = new RegFieldmappingHandler();
                operationResult = regFieldmappingHandler.reorderRegFieldmapping(itemclassUuid, fieldUuid, newPositionValue);

                // Setting the operation result attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);

                // This is an Ajax request: no needs to redirect
            }
        } else {
            // ## This is a view request

            // Getting the RegField object if needed
            if (fieldUuid != null && fieldUuid.length() > 0) {
                try {
                    RegField regField = regFieldManager.get(fieldUuid);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELD, regField);
                } catch (Exception e) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELD, null);
                }
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELD, null);
            }

            //Getting the master language
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

            // Getting the specific itemclass
            if (itemclassUuid != null && itemclassUuid.length() > 0) {
                try {
                    RegItemclass regItemclass = regItemclassManager.get(itemclassUuid);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS, regItemclass);

                    // #--- Getting the list of field for the specific itemclass ---# //
                    List<RegFieldmapping> regFieldmappings = regFieldmappingManager.getAll(regItemclass);
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGS, regFieldmappings);

                    // Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } catch (Exception e) {
                    response.sendRedirect("." + WebConstants.PAGE_URINAME_ITEMCLASS);
                }
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
