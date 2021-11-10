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
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusgroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_STATUS)
public class Status extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the base status group id
        Properties properties = Configuration.getInstance().getProperties();
        String baseRegStatusgroupId = properties.getProperty(BaseConstants.KEY_PROPERTY_BASESTATUSGROUP);

        //Getting the status group
        RegStatusgroupManager regStatusgroupManager = new RegStatusgroupManager(entityManager);
        RegStatusgroup regStatusGroup = regStatusgroupManager.get(baseRegStatusgroupId);

        //Getting the list of status
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        List<RegStatus> regStatuses = regStatusManager.getAll(regStatusGroup);

        // Handling languages
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        List<RegLanguagecode> regLanguagecodes = regLanguagecodeManager.getAllActive();
        request.setAttribute(BaseConstants.KEY_REQUEST_LANGUAGECODES, regLanguagecodes);

        // Getting the specific language via request parameter
        String languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        // Getting the master language
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
        
        RegStatuslocalizationManager  regStatuslocalizationManager = new RegStatuslocalizationManager(entityManager);
        
        List <RegStatuslocalization> regStatuslocalizations = new ArrayList();
        for(RegStatus temp : regStatuses){
            RegStatuslocalization regStatusLocalization = regStatuslocalizationManager.get(temp, regLanguagecode);
            regStatuslocalizations.add(regStatusLocalization);
        }        
        
        request.setAttribute(BaseConstants.KEY_REQUEST_REGSTATUSLOCALIZATIONS, regStatuslocalizations);
        request.setAttribute(BaseConstants.KEY_REQUEST_REGSTATUSGROUP, regStatusGroup);
        
        //Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_STATUS + WebConstants.PAGE_URINAME_STATUS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
