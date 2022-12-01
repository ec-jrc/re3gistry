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
package eu.europa.ec.re3gistry2.migration.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.migration.manager.MigrationManager;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class RegInstallationStepMigrationPorcessHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManagerRe3gistry2;

    // System localization
    ResourceBundle systemLocalization;
    private final HttpServletRequest request;

    /**
     * This method initializes the class
     *
     * @param request
     * @throws Exception
     */
    public RegInstallationStepMigrationPorcessHandler(HttpServletRequest request, EntityManager entityManagerRe3gistry2) throws Exception {
        this.request = request;
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;

        /**
         * commit if there is something in persist
         */
        String dbAddress = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS);
        String dbPort = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPORT);
        String dbName = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBNAME);
        String dbUsername = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME);
        String dbPassword = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD);
        MigrationManager migrationManager = new MigrationManager(dbAddress, dbPort, dbName, dbUsername, dbPassword, entityManagerRe3gistry2);

        // Getting properties
        Properties properties = Configuration.getInstance().getProperties();

        try {

            if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                entityManagerRe3gistry2.getTransaction().begin();
            }
            entityManagerRe3gistry2.getTransaction().commit();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }

        /**
         * get the user from the session
         */
        HttpSession session = request.getSession();
        RegUser currentRegUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);
        try {
            migrationManager.startMigration();

            /**
             * send success email
             */
            String recipientString = currentRegUser.getEmail();
            InternetAddress[] recipient = {
                new InternetAddress(recipientString)
            };

            String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_SUCCESS);
            String body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_SUCCESS);
            MailManager.sendMail(recipient, subject, body);

            /**
             * set session user to null
             */
            if (request.getSession(false) != null) {
//                session.setAttribute(BaseConstants.KEY_SESSION_USER, null);
                session.invalidate();
            }
        } catch (Exception ex) {
            /**
             * send error email
             */
            String recipientString = currentRegUser.getEmail();
            InternetAddress[] recipient = {
                new InternetAddress(recipientString)
            };
            String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_ERROR);
            String body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_ERROR);
            MailManager.sendMail(recipient, subject, body + ex.getMessage());

            /**
             * set session user to null
             */
            if (session != null) {
                session.setAttribute(BaseConstants.KEY_SESSION_USER, null);
            }

            throw new Exception(ex.getMessage());
        }

    }
}
