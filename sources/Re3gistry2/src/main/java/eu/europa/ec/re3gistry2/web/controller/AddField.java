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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldtypeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegFieldHandler;
import eu.europa.ec.re3gistry2.model.RegFieldtype;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ADDFIELD)
public class AddField extends HttpServlet {

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
        RegFieldtypeManager regFieldtypeManager = new RegFieldtypeManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

        // Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionManageField = {BaseConstants.KEY_USER_ACTION_MANAGEFIELD};
        boolean permissionManageField = UserHelper.checkGenericAction(actionManageField, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        // Getting parameters
        String formFieldLocalId = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        String formItemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String formLabel = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LABEL);
        String formFieldtypeUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_FIELDTYPEUUID);
        String formItemclassreferenceUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSREFERENCEUUID);

        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String itemclassUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);

        formFieldLocalId = (formFieldLocalId != null) ? InputSanitizerHelper.sanitizeInput(formFieldLocalId) : null;
        formItemclassUuid = (formItemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(formItemclassUuid) : null;
        
        // Handling charset for the textual contents
        byte[] bytes;
        if (formLabel!=null) {
            bytes = formLabel.getBytes(StandardCharsets.ISO_8859_1);
            formLabel = new String(bytes, StandardCharsets.UTF_8);
            formLabel = InputSanitizerHelper.sanitizeInput(formLabel);
        }
        
        formFieldtypeUuid = (formFieldtypeUuid != null) ? InputSanitizerHelper.sanitizeInput(formFieldtypeUuid) : null;
        formItemclassreferenceUuid = (formItemclassreferenceUuid != null) ? InputSanitizerHelper.sanitizeInput(formItemclassreferenceUuid) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;
        itemclassUuid = (itemclassUuid != null) ? InputSanitizerHelper.sanitizeInput(itemclassUuid) : null;

        if (formFieldLocalId != null && formFieldLocalId.length() > 0) {
            // ## This is a request to add a field

            String operationResult;

            //Checking if the user has the rights to add RegField
            if (permissionManageField) {

                RegField regField = null;
                ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();
                try {
                    regFieldManager.getByLocalid(formFieldLocalId);
                    operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_FIELD_EXIST_LOCALID);
                } catch (NoResultException e) {

                    List<RegLocalization> check = regLocalizationManager.getAllFieldsByValue(formLabel);
                    if (check.size() > 0) {
                        operationResult = systemLocalization.getString(BaseConstants.KEY_ERROR_FIELD_EXIST_LABEL);
                    } else {
                        // Adding the new field
                        RegFieldHandler regFieldHandler = new RegFieldHandler();
                        operationResult = regFieldHandler.newRegField(formFieldLocalId, formLabel, formFieldtypeUuid, formItemclassreferenceUuid);
                        regField = regFieldManager.getByLocalid(formFieldLocalId);
                    }
                }
                // Setting the operation success attribute
                request.setAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT, operationResult);

                // Redirecting to the field map page
                if (regField != null) {
                    response.sendRedirect("." + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid + "&" + BaseConstants.KEY_FORM_FIELD_NAME_FIELDUUID + "=" + regField.getUuid());
                } else {
                    request.getRequestDispatcher("." + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid).forward(request, response);
                    //response.sendRedirect("." + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
                }
            } else {
                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
            }
        } else {
            // ## This is a view request

            if (permissionManageField) {
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

                // Setting the itemclass uuid parameter
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEMCLASSUUID, itemclassUuid);

                // Getting the list of the itemclasses
                List<RegItemclass> regItemclasses = regItemclassManager.getAll();
                request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES, regItemclasses);

                // Getting all the field types
                List<RegFieldtype> regFieldtypes = regFieldtypeManager.getAll();
                request.setAttribute(BaseConstants.KEY_REQUEST_REGFIELDTYPES, regFieldtypes);

                // JSP
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDFIELD + WebConstants.PAGE_URINAME_ADDFIELD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            } else {
                // Redirecting to the field map page
                response.sendRedirect("." + WebConstants.PAGE_PATH_MAPFIELD + WebConstants.PAGE_URINAME_MAPFIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + formItemclassUuid);
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
