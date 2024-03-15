/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.web.controller;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserCodesManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserHandler;
import eu.europa.ec.re3gistry2.javaapi.handler.RegUserRegCodesHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserCodes;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_ACTIVATE)
public class Activate extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        //añadir booleano para no login?
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();
        HttpSession session = request.getSession();

        // Instantiating managers
        RegUserCodesManager regUserCodesManager = new RegUserCodesManager(entityManager);
        RegUserRegCodesHandler regUserCodesHandler = new RegUserRegCodesHandler();
        RegUserManager regUserManager = new RegUserManager(entityManager);
        RegUserHandler regUserHandler = new RegUserHandler();

        // Getting form parameter
        String code = request.getParameter("code");

        String step = request.getParameter(BaseConstants.KEY_REQUEST_STEP);

        // Getting the user from the code
        if (code != null) {
            session.setAttribute("code", code);
            RegUserCodes regCode = regUserCodesManager.getByCode(code);
            RegUser regUser = regUserManager.get(regCode.getRegUser());
            List<RegUserCodes> regCodeAux = regUserCodesManager.getByRegUser(regCode.getRegUser());

            if (regCode.getAction().equals(BaseConstants.KEY_USER_ACTION_ACTIVATE_USER)) {
                //Enabling the user
                Boolean enabled = regUserHandler.toggleUserEnabled(regUser, Boolean.TRUE);

                if (enabled) {

                    ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();
                    MailManager.sendUserActivatedEmail(regUser, systemLocalization);

                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_CHANGEPASSWORD + WebConstants.PAGE_URINAME_CHANGEPASSWORD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } else {
                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                }
            } else {
                //Delete the codes                
                for (RegUserCodes r : regCodeAux) {
                    regUserCodesHandler.deleteCode(r);
                }

                //Delete the user
                Boolean removed = regUserHandler.removeUser(regUser);
                if (removed) {
                    //response.sendRedirect("/help");
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_DELETE_USER + WebConstants.PAGE_URINAME_DELETE_USER + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } else {
                    //Dispatch request
                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_URINAME_REGISTRYMANAGER_USERS_ADD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                }
            }
        }
        if (step.equals("4")) {
            boolean result = false;
            String oldPassword = request.getParameter(BaseConstants.KEY_REQUEST_USER_OLD_PASSWORD);
            String newPassword = request.getParameter(BaseConstants.KEY_REQUEST_USER_CONFIRM_NEW_PASSWORD);
            String codeNew = (String) session.getAttribute("code");

            RegUserCodes regCode = regUserCodesManager.getByCode(codeNew);
            RegUser regUser = regUserManager.get(regCode.getRegUser());
            List<RegUserCodes> regCodeAux = regUserCodesManager.getByRegUser(regCode.getRegUser());
            Boolean found = UserHelper.checkCurrentUserKey(regUser.getEmail(), oldPassword);

            if (found) {
                UserHelper.generatePassword(regUser, newPassword);
                result = regUserHandler.updateUser(regUser);

                for (RegUserCodes r : regCodeAux) {
                    regUserCodesHandler.deleteCode(r);
                }
            }
            if (result) {
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ACTIVATE + WebConstants.PAGE_URINAME_ACTIVATE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION);
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_CHANGEPASSWORD + WebConstants.PAGE_URINAME_CHANGEPASSWORD + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            }
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
