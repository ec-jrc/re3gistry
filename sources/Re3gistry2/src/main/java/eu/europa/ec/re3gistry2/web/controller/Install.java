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
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationStep3Handler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationStepCleanInstallationProfileHandler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationStepCleanInstallationSummaryHandler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationStepCleanInstallationProcessHandler;
import eu.europa.ec.re3gistry2.migration.handler.RegInstallationStepMigrationPorcessHandler;
import eu.europa.ec.re3gistry2.migration.handler.RegInstallationStepMigrationSummaryHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.File;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_INSTALL)
public class Install extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, true, false);

        // Setup the entity manager
        EntityManager entityManagerRe3gistry2 = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        //Init session
        HttpSession session = request.getSession();

        //Getting parameters
        String step = request.getParameter(BaseConstants.KEY_REQUEST_STEP);
        step = (step != null) ? InputSanitizerHelper.sanitizeInput(step) : null;

        if (step == null || step.length() < 1) {
            step = "1";
        }

        if (step.equals("2")) {
            request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR, BaseConstants.KEY_REQUEST_USER_CREATION_STARTED);
        }
        if (step.equals("3")) {
            RegInstallationStep3Handler regInstallationStep3Handler = new RegInstallationStep3Handler(request, entityManagerRe3gistry2);
            RegUser adminUser = regInstallationStep3Handler.addUser();
            if (adminUser == null) {
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_ADMIN_USER_ERROR, BaseConstants.KEY_REQUEST_USER_CREATION_ERROR);
                step = "2";
            }
        }

        if (step.equals(BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_PROFILE)) {
            new RegInstallationStepCleanInstallationProfileHandler(request);
        }

        boolean lastStep = false;
        if (step.equals(BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY)) {
            try {
                new RegInstallationStepCleanInstallationSummaryHandler(request);
            } catch (Exception ex) {
                step = BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY;
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR, ex.getMessage());
            }
        }
        if (step.equals(BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_PROCESS)) {
            try {
                createSystemInstallingFile();
                new RegInstallationStepCleanInstallationProcessHandler(request, entityManagerRe3gistry2);
                deleteSystemInstallingFile();
                createSystemInstalledFile();
                lastStep = true;
                if (session != null) {
                    session.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
            } catch (Exception ex) {
                step = "3";
                deleteSystemInstallingFile();
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR, ex.getMessage());
            }
        }

        if (step.equals(BaseConstants.KEY_REQUEST_MIGRATION_SUMMARY)) {
            try {
                new RegInstallationStepMigrationSummaryHandler(request, entityManagerRe3gistry2);
            } catch (Exception ex) {
                step = "3";
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_MIGRATION_ERROR, ex.getMessage());
            }
        }

        if (step.equals(BaseConstants.KEY_REQUEST_MIGRATION_PROCESS)) {
            try {
                createSystemInstallingFile();
                new RegInstallationStepMigrationPorcessHandler(request, entityManagerRe3gistry2);
                deleteSystemInstallingFile();
                createSystemInstalledFile();
                lastStep = true;
                if (session != null) {
                    session.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
                }
            } catch (Exception ex) {
                step = "3";
                deleteSystemInstallingFile();
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_MIGRATION_ERROR, ex.getMessage());
            }
        }

        // Dispatch request
        if (lastStep) {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_LOGIN);
        } else {
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_INSTALL + WebConstants.PAGE_URINAME_STEP + step + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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

    private void createSystemInstallingFile() throws Exception {
        String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);
        String systemInstalledPath = propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING;
        File systemInstalledFile = new File(systemInstalledPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {

            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                throw new IOException();
            }

        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void deleteSystemInstallingFile() throws Exception {
        String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);
        String systemInstalledPath = propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING;
        File file = new File(systemInstalledPath);

        boolean success = false;
        try {
            success = file.delete();
        } catch (Exception e) {
        }

//        if (!success) {
//            throw new IOException();
//        }
    }

    private void createSystemInstalledFile() throws Exception {
        String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);
        String systemInstalledPath = propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLED;
        File systemInstalledFile = new File(systemInstalledPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {
            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                throw new IOException();
            }

        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
