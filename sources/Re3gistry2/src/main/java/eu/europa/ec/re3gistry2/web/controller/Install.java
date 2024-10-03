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
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_INSTALL)
public class Install extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Configuration.getInstance().getLogger().trace("Start processing request in " + Install.class.getName());

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

        Configuration.getInstance().getLogger().debug("install step = " + step);

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
            if (session != null) {
                request.getSession().setAttribute(BaseConstants.KEY_REQUEST_WORKFLOW, request.getParameterValues(BaseConstants.KEY_REQUEST_WORKFLOW)[0]);
            }
            try {
                new RegInstallationStepCleanInstallationProfileHandler(request);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
                Configuration.getInstance().getLogger().error(ex.getMessage(), ex);
                step = "3";
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR, ex.getMessage());
            }
        }

        boolean lastStep = false;
        if (step.equals(BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY)) {
            try {
                new RegInstallationStepCleanInstallationSummaryHandler(request);
            } catch (Exception ex) {
                Configuration.getInstance().getLogger().error(ex.getMessage(), ex);
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
                step = BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_SUMMARY;
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR, ex.getMessage());
            }
        }
        if (step.equals(BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_PROCESS)) {
            Configuration.getInstance().getLogger().info("Start clean installation");
            try {
                createSystemInstallingFile();
                new RegInstallationStepCleanInstallationProcessHandler(request, entityManagerRe3gistry2);
                deleteSystemInstallingFile();
                createSystemInstalledFile();
                if (session != null) {
                    String workflow = (String) session.getAttribute(BaseConstants.KEY_REQUEST_WORKFLOW);
                    if (StringUtils.equals(workflow, BaseConstants.KEY_REQUEST_WORKFLOW_SIMPLIFIED)) {
                        createWorkflowFile();
                    }
                }
                lastStep = true;
                if (session != null) {
                    session.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
                }
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
                Configuration.getInstance().getLogger().info("Installation succeeded");
            } catch (Exception ex) {
                Configuration.getInstance().getLogger().error("Exception occurred in step " + BaseConstants.KEY_REQUEST_CLEAN_INSTALLATION_PROCESS, ex);
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
                step = "3";
                deleteSystemInstallingFile();
                request.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_CLEAN_DB_ERROR, ex.getMessage());
            }
        }

        if (step.equals(BaseConstants.KEY_REQUEST_MIGRATION_SUMMARY)) {
            try {
                new RegInstallationStepMigrationSummaryHandler(request, entityManagerRe3gistry2);
            } catch (Exception ex) {
                Configuration.getInstance().getLogger().error("Exception occurred in step " + BaseConstants.KEY_REQUEST_MIGRATION_SUMMARY, ex);
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
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
                if (session != null) {
                    String workflow = (String) session.getAttribute(BaseConstants.KEY_REQUEST_WORKFLOW);
                    if (StringUtils.equals(workflow, BaseConstants.KEY_REQUEST_WORKFLOW_SIMPLIFIED)) {
                        createWorkflowFile();
                    }
                }
                lastStep = true;
                if (session != null) {
                    session.setAttribute(BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS, BaseConstants.KEY_REQUEST_INSTALLATION_SUCCESS);
                }
            } catch (Exception ex) {
                Configuration.getInstance().getLogger().error("Exception occurred in step " + BaseConstants.KEY_REQUEST_MIGRATION_PROCESS, ex);
                java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
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
        String systemInstallingPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING;
        File systemInstallingFile = new File(systemInstallingPath);

        systemInstallingFile.getParentFile().mkdirs();
        try {

            boolean success = systemInstallingFile.createNewFile();

            if (!success) {
                throw new IOException();
            }

        } catch (IOException ex) {
            Configuration.getInstance().getLogger().error("Error while trying to create the system installing file", ex);
            throw new Exception(ex.getMessage());
        }
    }

    private void createWorkflowFile() throws Exception {
        String systemInstallingPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_WORKFLOW_SIMPLIFIED;
        File workflowFile = new File(systemInstallingPath);

        workflowFile.getParentFile().mkdirs();
        try {

            boolean success = workflowFile.createNewFile();

            if (!success) {
                throw new IOException();
            }

        } catch (IOException ex) {
            Configuration.getInstance().getLogger().error("Error while trying to create the system workflow file", ex);
            throw new Exception(ex.getMessage());
        }
    }

    private void deleteSystemInstallingFile() throws Exception {
        String systemInstallingPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING;
        File systemInstallingFile = new File(systemInstallingPath);

        boolean success = false;
        try {
            success = systemInstallingFile.delete();
        } catch (Exception e) {
            Configuration.getInstance().getLogger().error("Error while trying to delete the system installing file", e);
        }

//        if (!success) {
//            throw new IOException();
//        }
    }

    private void createSystemInstalledFile() throws Exception {
        String systemInstalledPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLED;
        File systemInstalledFile = new File(systemInstalledPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {
            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                throw new IOException();
            }

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, "Error encountered within Install class. Please check the details: " + ex.getMessage(), ex.getMessage());
            Configuration.getInstance().getLogger().error("Error while trying to create the system installed file", ex);
            throw new Exception(ex.getMessage());
        }
    }
}
