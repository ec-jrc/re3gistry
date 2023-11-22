/*
 * Copyright 2010,2016 EUROPEAN UNION
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
 * inspire-registry-info@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.servlets;

import eu.europa.ec.ror.managers.ProcedureMgr;
import eu.europa.ec.ror.managers.UserMgr;
import eu.europa.ec.ror.model.Procedure;
import eu.europa.ec.ror.model.User;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.Mail;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutomaticHarvestingHelper extends HttpServlet {

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

        Logger log = Configuration.getInstance().getLogger();
        Properties properties = Configuration.getInstance().getProperties();
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, new Locale("en"));

        String mailSubject = localization.getString("mail.subject.prefix.procedure.harvesting");
        String mailBody = "";
        String mailSubjectSuffix = "";
        String adminEmail = properties.getProperty("application.admin.email");
        String adminMailStr = "";
        String outMessage = "";
        HashMap runnedProcedureDescriptorByOrganization = new HashMap();

        try {
            // Getting the list of procedures to run today
            List<Procedure> procedures = ProcedureMgr.getAllProceduresToRunToday();

            // Getting the default resource bundle
            // Getting the default user for the automatic procedures
            User user = UserMgr.getUserById(properties.getProperty("application.automaticharvesting.userid"));

            // Starting procedures
            for (Procedure p : procedures) {
                ProcedureMgr.startProcedure(p, localization, user);
                // Mail Body for the admin
                adminMailStr += "<li>" + p.getDescriptor().getUrl() + "</li>";

                //Creating list of procedure runned by organization mail
                if (runnedProcedureDescriptorByOrganization.containsKey(p.getOrganization().getEmail())) {
                    ArrayList tmp = (ArrayList) runnedProcedureDescriptorByOrganization.get(p.getOrganization().getEmail());
                    tmp.add(p.getDescriptor().getUrl());
                    runnedProcedureDescriptorByOrganization.put(p.getOrganization().getEmail(), tmp);
                } else {
                    ArrayList tmp = new ArrayList();
                    tmp.add(p.getDescriptor().getUrl());
                    runnedProcedureDescriptorByOrganization.put(p.getOrganization().getEmail(), tmp);
                }

            }

            log.info(localization.getString("autoharvesting.log.prefix") + " " + localization.getString("autoharvesting.log.success"));

            mailSubject += localization.getString("mail.subject.procedure.started") + localization.getString("private.starter.automatic");
            mailBody = localization.getString("mail.boby.procedure.automatic.started").replace("{0}", adminMailStr);

            // Output to console for cron logs
            outMessage = new Date() + " - " + localization.getString("autoharvesting.log.success");

        } catch (Exception e) {
            log.error(localization.getString("autoharvesting.log.prefix") + " " + localization.getString("autoharvesting.log.error") + " " + e.getMessage());

            mailSubject += localization.getString("mail.subject.error");
            mailBody = localization.getString("mail.body.error") + e.getMessage();

            // Output to console for cron logs
            outMessage = new Date() + " - " + localization.getString("autoharvesting.log.error") + " " + e.getMessage();
        }

        // Send the informative mail
        try {
            if (adminMailStr.length() <= 0) {
                mailBody = localization.getString("mail.body.procedure.automatic.noprocedures");
            }
            // Sending mail to the admin
            Mail.sendMail(localization, adminEmail, localization.getString("mail.admin.message") + " " + mailSubject + mailSubjectSuffix, mailBody);

            //Sending mail to the users
            Iterator itr = runnedProcedureDescriptorByOrganization.keySet().iterator();
            while (itr.hasNext()) {
                String email = (String) itr.next();
                ArrayList descriptors = (ArrayList) runnedProcedureDescriptorByOrganization.get(email);

                Iterator tmpItr = descriptors.iterator();
                String outstr = "";
                while (tmpItr.hasNext()) {
                    String descriptor = (String) tmpItr.next();
                    outstr += "<li>" + descriptor + "</li>";
                }

                String sendMailLevel = properties.getProperty("mail.send.level");
                if (sendMailLevel.equals("1") || (sendMailLevel.equals("0") && (mailSubject.contains(localization.getString("mail.subject.error")) || !mailSubject.contains(localization.getString("mail.subject.success"))))) {
                    Mail.sendMail(localization, email, mailSubject + mailSubjectSuffix, localization.getString("mail.boby.procedure.automatic.started.users").replace("{0}", outstr));
                }
            }

        } catch (Exception e) {
            log.error(localization.getString("mail.error.sending") + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.print(outMessage);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
    }// </editor-fold>

}
