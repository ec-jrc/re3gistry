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
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegStatus;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ADDSUCCESSOR)
public class AddSuccessor extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String itemUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String newStatusLocalId = request.getParameter(BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID);

        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the RegFieldMapping
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegLanguagecodeManager regLanguageCodeManager = new RegLanguagecodeManager(entityManager);

        // Getting the current RegLanguagecode
        RegLanguagecode currentRegLanguagecode = regLanguageCodeManager.get(languageUuid);

        //Getting the current RegItem
        RegItem regItem = regItemManager.get(itemUuid);

        // Get all the RegItems in the same RegItemclass of the current item
        List<RegItem> regItems = regItemManager.getAll(regItem.getRegItemclass());

        // Remove the current RegItem
        regItems.remove(regItem);

        // Getting the new RegStatus
        RegStatus regStatus = regStatusManager.findByLocalid(newStatusLocalId);

        // Setting the arrtibutes
        request.setAttribute(BaseConstants.KEY_REQUEST_REGITEM, regItem);
        request.setAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS, regItems);
        request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGE, currentRegLanguagecode);
        request.setAttribute(BaseConstants.KEY_REQUEST_NEWREGSTATUS, regStatus);

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDSUCCESSOR + WebConstants.PAGE_URINAME_ADDSUCCESSOR + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
