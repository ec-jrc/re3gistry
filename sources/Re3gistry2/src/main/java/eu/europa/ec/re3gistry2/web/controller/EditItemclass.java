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
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemclassHandler;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_EDITITEMCLASS)
public class EditItemclass extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String itemclassUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
        String action = request.getParameter(BaseConstants.KEY_REQUEST_ACTION);
        String editFromContentClass = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD_FROM_CONTENTCLASS);
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        String localId = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_LOCALID);
        String baseUri = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_BASEURI);
        
        itemclassUUID = (itemclassUUID != null) ? InputSanitizerHelper.sanitizeInput(itemclassUUID) : null; 
        action = (action != null) ? InputSanitizerHelper.sanitizeInput(action) : null; 
        editFromContentClass = (editFromContentClass != null) ? InputSanitizerHelper.sanitizeInput(editFromContentClass) : null; 
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null; 
        localId = (localId != null) ? InputSanitizerHelper.sanitizeInput(localId) : null; 
        baseUri = (baseUri != null) ? InputSanitizerHelper.sanitizeInput(baseUri) : null; 

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Initializing managers
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);

        // Getting the rection
        RegItemclass regItemclass = null;
        try {
            regItemclass = regItemclassManager.get(itemclassUUID);
        } catch (NoResultException e) {
        }

        if (action != null && action.equals(BaseConstants.KEY_REQUEST_TYPE_EDIT)) {
            // Save action

            RegItemclassHandler regItemclassHandler = new RegItemclassHandler();
            String operationSuccess = regItemclassHandler.editRegitemclass(regItemclass, localId, baseUri, request);

            // Dispatch request
            if (editFromContentClass != null && editFromContentClass.length() > 0 && regItemclass!=null) {
                request.getRequestDispatcher("." + WebConstants.PAGE_PATH_FIELD + WebConstants.PAGE_URINAME_FIELD + "?" + BaseConstants.KEY_REQUEST_ITEMCLASSUUID + "=" + regItemclass.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid).forward(request, response);
            } else {
                response.sendRedirect("." + WebConstants.PAGE_PATH_ITEMCLASS + WebConstants.PAGE_URINAME_ITEMCLASS);
            }

        } else {
            // View action
            if (regItemclass != null) {
                //Getting any eventual child itemclass
                List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItemclass);
                List<RegItem> associatedItems = regItemManager.getAll(regItemclass);
                if (!childItemclasses.isEmpty() || !associatedItems.isEmpty()) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_NOTEDITABLE, Boolean.TRUE);
                } else {
                    request.setAttribute(BaseConstants.KEY_REQUEST_NOTEDITABLE, Boolean.FALSE);
                }

                // Setting the RegItemclassUuid parameter
                request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS, regItemclass);
            }

            // Dispatch request
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_EDITITEMCLASS + WebConstants.PAGE_URINAME_EDITITEMCLASS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
