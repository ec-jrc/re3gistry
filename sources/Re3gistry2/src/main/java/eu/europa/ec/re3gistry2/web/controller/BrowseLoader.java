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
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
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

@WebServlet(WebConstants.PAGE_URINAME_BROWSELOADER)
public class BrowseLoader extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String itemUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        // Getting the RegUser from session
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Initializing managers
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        // Getting the item by parameter
        RegItem regItem = regItemManager.get(itemUUID);
        request.setAttribute(BaseConstants.KEY_REQUEST_REGITEM, regItem);

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode currentLanguage = null;
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

        // #--- Creating dynamic fields ---# //
        // Listing the RegFields for the current RegItem
        List<RegFieldmapping> regFieldmappings = regFieldmappingManager.getAll(regItem.getRegItemclass());
        request.setAttribute(BaseConstants.KEY_REQUEST_FIELDMAPPINGS, regFieldmappings);

        // Checking if the changes bar need to be shown
        // Getting the eventual RegItemproposed related to this item
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        try {
            RegItemproposed regItemproposed = regItemproposedManager.getByRegItemReference(regItem);
            //Checking if there are some changes for the current language
            RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
            List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regItemproposed, currentLanguage);
            List<RegLocalizationproposed> regLocalizationproposedsRelations = regLocalizationproposedManager.getAllRelationReference(regItemproposed, masterLanguage);

            if (!regLocalizationproposeds.isEmpty() || !regLocalizationproposedsRelations.isEmpty()) {
                request.setAttribute(BaseConstants.KEY_REQUEST_SHOW_CHANGESBAR, true);
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_SHOW_CHANGESBAR, false);
            }

            // Checking regUser, RegItem and permission. If the RegItemproposed
            // has been created by another user, the current user cannot edit it                
            if (regItemproposed != null && !regItemproposed.getRegUser().getSsoreference().equals(regUser.getSsoreference())) {
                //disableActions = " disabled=\"disabled\"";
                //disableMessage = localization.getString("label.userNotAllowed");
            }
        } catch (NoResultException e) {
        }

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_BROWSELOADER + WebConstants.PAGE_URINAME_BROWSELOADER + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
