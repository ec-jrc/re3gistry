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
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_USERACTIVITY)
public class ItemListUserActivity extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Init properties
        Configuration config = Configuration.getInstance();
        Properties properties = config.getProperties();

        // Getting system localization
        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        // Init the date formatter
        SimpleDateFormat dt = new SimpleDateFormat(systemLocalization.getString("property.dateformat"));

        // Getting the RegUser from session
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        String sStart = request.getParameter(BaseConstants.KEY_REQUEST_DT_START);
        String sLength = request.getParameter(BaseConstants.KEY_REQUEST_DT_LENGTH);
        String sDraw = request.getParameter(BaseConstants.KEY_REQUEST_DT_DRAW);
        
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;
        sStart = (sStart != null) ? InputSanitizerHelper.sanitizeInput(sStart) : null;
        sLength = (sLength != null) ? InputSanitizerHelper.sanitizeInput(sLength) : null;
        sDraw = (sDraw != null) ? InputSanitizerHelper.sanitizeInput(sDraw) : null;
        
        int start;
        int length;
        int draw;

        try {
            draw = Integer.parseInt(sDraw);
            start = Integer.parseInt(sStart);
            length = Integer.parseInt(sLength);
        } catch (Exception e) {
            draw = 0;
            start = 0;
            length = 10;
        }

        //Preparing the out string
        String outs = "{\"draw\":" + draw + ",";
        outs += "\"data\":[";

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Initializing managers
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode regLanguagecode;
        if (languageUUID != null && languageUUID.length() == 2) {
            try {
                regLanguagecode = regLanguagecodeManager.get(languageUUID);
            } catch (Exception e) {
                regLanguagecode = masterLanguage;
            }
        } else {
            regLanguagecode = masterLanguage;
        }

        // Getting the item by user
        List<RegItemproposed> regItemProposeds = regItemproposedManager.getAll(regUser, start, length);

        int totalCount = 0;
        totalCount = regItemproposedManager.countAll(regUser);

        int i = 0;
        for (RegItemproposed tmpRegitemproposed : regItemProposeds) {

            if (i != 0) {
                outs += ",";
            }

            RegField regLabel = regFieldManager.getTitleRegField();
            List<RegLocalizationproposed> regLocalizationproposeds = regLocalizationproposedManager.getAll(regLabel, tmpRegitemproposed, regLanguagecode);
            if(regLocalizationproposeds.isEmpty()){
                regLocalizationproposeds = regLocalizationproposedManager.getAll(regLabel, tmpRegitemproposed, masterLanguage);
            }

            String refUuid = (tmpRegitemproposed.getRegItemReference() != null) ? tmpRegitemproposed.getRegItemReference().getUuid() : tmpRegitemproposed.getUuid();

            if (!regLocalizationproposeds.isEmpty()) {
                outs += "[\"<a href=\\\"." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + refUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid() + "\\\">" + StringEscapeUtils.escapeJson(regLocalizationproposeds.get(0).getValue()) + "</a>\",\"" + ((tmpRegitemproposed.getEditdate() != null) ? dt.format(tmpRegitemproposed.getEditdate()) : dt.format(tmpRegitemproposed.getInsertdate())) + "\"]";
            } else {
                outs += "\"\"";
            }
            i++;
        }

        outs += "],";
        outs += "\"recordsTotal\":" + totalCount + ",";
        outs += "\"recordsFiltered\":" + totalCount;
        //outs += (languageNotAvailable) ? ",\"languageNotAvailable\":true" : "";
        outs += "}";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(outs);

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
