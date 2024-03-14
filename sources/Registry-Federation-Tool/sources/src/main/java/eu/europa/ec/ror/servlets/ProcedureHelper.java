/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.servlets;

import eu.cec.digit.ecas.client.jaas.DetailedUser;
import eu.europa.ec.ror.managers.DescriptorMgr;
import eu.europa.ec.ror.managers.ProcedureMgr;
import eu.europa.ec.ror.managers.UserMgr;
import eu.europa.ec.ror.model.Procedure;
import eu.europa.ec.ror.model.User;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.Mail;
import eu.europa.ec.ror.utility.localization.LocalizationMgr;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

public class ProcedureHelper extends HttpServlet {

    // Common varables
    Logger log;
    Locale currentLocale;
    ResourceBundle localization;

    @Override
    public void init() {
        // Initialization
        this.log = Configuration.getInstance().getLogger();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Itinitalizing global variables
        this.currentLocale = LocalizationMgr.getCurrentLocale(request);
        this.localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        // Instantiating local variables
        String outstr = "";
        // Initializing the subject with the name of the type of procvedure
        String mailSubject = localization.getString("mail.subject.prefix.procedure.harvesting");
        String mailBody = "";
        String mailSubjectSuffix = "";

        // Getting the current user
        User user = null;
        DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();
        if (detailedUser != null) {
            String uid = detailedUser.getUid();
            user = UserMgr.getUserByEcasId(uid);
            
            //If ECAS uid is not available, check the ECAS email
            if(user==null){
                uid = detailedUser.getEmail();
                user = UserMgr.getUserByEcasId(uid);
            }
        }

        // Checking the user
        if (user != null && user.getActive()) {

            // Initializing parameters
            String actionstartertype = request.getParameter(Constants.PARAMETER_REGISTRY_ACTION_STARTER_TYPE);  // Action starter actiontype (m=manual | a=automatic)
            if (actionstartertype == null || actionstartertype.length() <= 0) {
                actionstartertype = Constants.PARAMETER_REGISTRY_ACTION_STARTER_TYPE_AUTOMATIC;
                log.info(localization.getString("info.starterparameter.autoset"));
            }

            // Setting the mail subject suffix
            if (actionstartertype.equals(Constants.PARAMETER_REGISTRY_ACTION_STARTER_TYPE_AUTOMATIC)) {
                mailSubjectSuffix = localization.getString("private.starter.automatic");
            } else {
                mailSubjectSuffix = localization.getString("private.starter.manual");
            }

            String procedureid = request.getParameter(Constants.PARAMETER_PROCEDURE_ID);                // The id of the Procedure to start

            try {

                // Getting the procedure to start
                Procedure procedure = ProcedureMgr.getProcedureByID(procedureid);

                // If the procedure is not already running
                if (!procedure.getStatus().equals(Constants.PROCEDURE_STATUS_RUNNING)) {

                    // Check if the current user has the rights to execute the current procedure
                    if (user.getUserlevel() == Constants.USER_LEVEL_ADMIN || (user.getUserlevel() != Constants.USER_LEVEL_ADMIN && user.getOrganization().getUuid().equals(procedure.getOrganization().getUuid()))) {
                        // Checking the Descriptor url
                        DescriptorMgr.checkURL(procedure.getDescriptor().getUrl(), localization, false);

                        // Starting the procedure
                        ProcedureMgr.startProcedure(procedure, localization, user);

                        outstr = "{\"status\":\"" + Constants.JSON_ACTION_STATUS_OK + "\",\"message\":\"" + localization.getString("success.procedure.started").replace("{0}", procedure.getDescriptor().getUrl()) + "\"}";
                        mailSubject += localization.getString("mail.subject.procedure.started");
                        mailBody = localization.getString("mail.boby.procedure.started").replace("{0}", procedure.getDescriptor().getUrl());
                    } else {
                        throw new Exception(localization.getString("error.procedure.norights"));
                    }
                } else {
                    throw new Exception(localization.getString("error.procedure.already.running").replace("{0}", procedure.getDescriptor().getUrl()));
                }

            } catch (Exception e) {
                outstr = "{\"status\":\"" + Constants.JSON_ACTION_STATUS_ERROR + "\",\"message\":\"" + e.getMessage() + "\"}";
                mailSubject += localization.getString("mail.subject.error");
                mailBody = localization.getString("mail.body.error") + e.getMessage();
            }

        } else {
            String error;
            if (user != null && !user.getActive()) {
                error = localization.getString("error.user.notactive");
            } else {
                error = localization.getString("error.user.notavailable");
            }
            outstr = "{\"status\":\"" + Constants.JSON_ACTION_STATUS_ERROR + "\",\"message\":\"" + error.replace("\"", "\\\"") + "\"}";
            mailSubject += localization.getString("mail.subject.error");
            mailBody = localization.getString("mail.body.error") + error;
        }

        // Send the informative mail
        try {
            Properties properties = Configuration.getInstance().getProperties();
            String sendMailLevel = properties.getProperty("mail.send.level");
            if (sendMailLevel.equals("1") || (sendMailLevel.equals("0") && (mailSubject.contains(localization.getString("mail.subject.error")) || !mailSubject.contains(localization.getString("mail.subject.success"))))) {
                Mail.sendMail(localization, user.getOrganization().getEmail(), mailSubject + mailSubjectSuffix, mailBody);
            }
        } catch (Exception e) {
            log.error(localization.getString("mail.error.sending") + e.getMessage());
        }

        // Output the JSON result code
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(outstr);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
