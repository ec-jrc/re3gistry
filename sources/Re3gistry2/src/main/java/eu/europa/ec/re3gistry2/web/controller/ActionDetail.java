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
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ACTIONDETAIL)
public class ActionDetail extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception  {
        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String actionUUID = request.getParameter(BaseConstants.KEY_REQUEST_ACTION_UUID);
        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        
        // Initializing logger
        Logger logger = Configuration.getInstance().getLogger();
        
        // Initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);
        
        // Getting the rection
        RegAction regAction = null;
        try {
            regAction = regActionManager.get(actionUUID);
        } catch (NoResultException e) {
            logger.error(e);
        }

        // Setting the RegAction parameter
        request.setAttribute(BaseConstants.KEY_REQUEST_ACTION, regAction);

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ACTIONDETAIL + WebConstants.PAGE_URINAME_ACTIONDETAIL + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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