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
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.RandomString;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserUuidHelper;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_RESETPASSWORD)
public class ResetPassword extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Getting properties
        Properties properties = Configuration.getInstance().getProperties();

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // System localization
        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        //Init session
        HttpSession session = request.getSession();

        // Instantiating managers
        RegUserManager regUserManager = new RegUserManager(entityManager);
        RegUserHandler regUserHandler = new RegUserHandler();

        // Getting form parameter
        String email = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USERNAME);
        email = (email != null) ? InputSanitizerHelper.sanitizeInput(email) : null;

        String loginType = properties.getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE, BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO);
        if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS)) {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_LOGIN);
        } else if (email != null && !email.isEmpty()) {
            String newUUID = RegUserUuidHelper.getUuid(email);
            boolean result = false;
            try {
                //Check if the user is already available (the email is the unique key)
                RegUser regUser = regUserManager.get(newUUID);

                // Generating random string
                RandomString generateKey = new RandomString(23, new SecureRandom(), RandomString.alphanum);
                String key = generateKey.nextString();
                UserHelper.generatePassword(regUser, key);

                // Store the user
                result = regUserHandler.updateUser(regUser);

                // Prepare the email email to the user with the generated key
                String recipientString = regUser.getEmail();
                InternetAddress[] recipient = {
                    new InternetAddress(recipientString)
                };

                String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_RESETPASSWORD);
                String registry_link = properties.getProperty(BaseConstants.KEY_MAIL_APPLICATION_ROOTURL);
                String body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_RESETPASSWORD);
                body = (body != null)
                        ? body.replace("{name}", regUser.getName())
                                .replace("{email}", email)
                                .replace("{key}", key)
                                .replace("{registry_link}", registry_link)
                        : "";

                // Sending the mail
                MailManager.sendMail(recipient, subject, body);

                if (result) {
//                    request.setAttribute(BaseConstants.KEY_REQUEST_USER_SUCCESS_MESSAGES, BaseConstants.KEY_REQUEST_USER_RESETPASSWORD_SUCCESS);
                    session.setAttribute(BaseConstants.KEY_SESSION_RESET_PASSWORD_SUCCESS, BaseConstants.KEY_REQUEST_USER_RESETPASSWORD_SUCCESS);
                    response.sendRedirect("." + WebConstants.PAGE_URINAME_LOGIN);
//                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_LOGIN + WebConstants.PAGE_URINAME_LOGIN + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } else {
                    request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_RESETPASSWORD_ERROR);
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_RESETPASSWORD + WebConstants.PAGE_URINAME_RESETPASSWORD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_RESETPASSWORD_USERNOTAVAILABLE_ERROR);
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_RESETPASSWORD + WebConstants.PAGE_URINAME_RESETPASSWORD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            }

        } else {
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_RESETPASSWORD + WebConstants.PAGE_URINAME_RESETPASSWORD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
}
