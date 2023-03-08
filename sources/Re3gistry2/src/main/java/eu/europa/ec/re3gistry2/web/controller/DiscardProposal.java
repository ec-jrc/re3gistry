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
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegActionHandler;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.web.utility.jsp.JspCommon;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_DISCARDPROPOSAL)
public class DiscardProposal extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Initializing logger
        Logger logger = Configuration.getInstance().getLogger();

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

        // Gatering parameters
        // The object to be deleted has a reference to the relate RegLocalizationproposed
        // if the Regrelationproposed is not yet available, the reference to the
        // RegLocalization is passed in order to create the RegLocalizationproposed
        String regItemproposedUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String actionUUID = request.getParameter(BaseConstants.KEY_REQUEST_ACTION_UUID);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        
        regItemproposedUUID = (regItemproposedUUID != null) ? InputSanitizerHelper.sanitizeInput(regItemproposedUUID) : null; 
        actionUUID = (actionUUID != null) ? InputSanitizerHelper.sanitizeInput(actionUUID) : null;

        // Redirecting to the right RegItem
        String redirectURI = null;

        try {
            //coming from the page of action with details about the action and the item to be descarded
            if (actionUUID != null && !actionUUID.isEmpty() && regItemproposedUUID != null && !regItemproposedUUID.isEmpty()) {
                RegItemproposed regItemproposed = regItemproposedManager.get(regItemproposedUUID);
                String actionUuid = regItemproposed.getRegAction().getUuid();
                // Check if the current user can perform the action
                boolean canWrite = JspCommon.canWrite(regItemproposed, regUser);

                RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
                if (canWrite
                        || ((regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                        || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                        || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID))
                        && regItemproposed.getRegUser().getUuid().equals(regUser.getUuid()))) {
                    regItemproposedHandler.handleDiscardProposal(regItemproposed);
                }

                redirectURI = "." + WebConstants.PAGE_PATH_SUBMITTINGORGANISATIONS + WebConstants.PAGE_URINAME_SUBMITTINGORGANISATIONS + "?" + BaseConstants.KEY_REQUEST_ACTION_UUID + "=" + actionUuid;
            } else //                discard action
            if (actionUUID != null && !actionUUID.isEmpty()) {
                // Initializing managers
                RegActionManager regActionManager = new RegActionManager(entityManager);

                // Getting the rection
                RegAction regAction = null;
                try {
                    regAction = regActionManager.get(actionUUID);

//                    get all regItemProposed for this action
                    List<RegItemproposed> regItemproposedList = regItemproposedManager.getAll(regAction);
                    
                    MailManager.sendActionMail(regItemproposedList, regAction, Configuration.getInstance().getLocalization(), BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS);

                    if (!regItemproposedList.isEmpty()) {
                        for (RegItemproposed regItemproposed : regItemproposedList) {
                            // Check if the current user can perform the action
                            boolean canWrite = JspCommon.canWrite(regItemproposed, regUser);

                            RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
                            if (canWrite
                                    || ((regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                                    || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                                    || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID))
                                    && regItemproposed.getRegUser().getUuid().equals(regUser.getUuid()))) {
                                regItemproposedHandler.handleDiscardProposal(regItemproposed);
                            }
                        }
                    } else {
                        RegActionHandler regActionHandler = new RegActionHandler();
                        regActionHandler.deleteAction(regAction);
                    }

                    redirectURI = "." + WebConstants.PAGE_PATH_SUBMITTINGORGANISATIONS + WebConstants.PAGE_URINAME_SUBMITTINGORGANISATIONS;
//                    request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_SUBMITTINGORGANISATIONS + WebConstants.PAGE_URINAME_SUBMITTINGORGANISATIONS + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                } catch (NoResultException e) {
                    logger.error(e.getMessage(), e);
                }
            } else //                    discard item
            if (regItemproposedUUID != null && !regItemproposedUUID.isEmpty()) {
                RegItemproposed regItemproposed = regItemproposedManager.get(regItemproposedUUID);

//               if proposed item is discarded, shot to the user the page of the referenced item
                if (regItemproposed.getRegItemReference() != null) {
                    redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemproposed.getRegItemReference().getUuid();
                } else {
                    //show the page of the upper level of the discarded item
                    switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
                        case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                            redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE;
                            break;
                        case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                            RegItem regItemRegistry = null;
                            List<RegRelationproposed> regRelationproposedRegistries = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegistry);
                            try {
                                regItemRegistry = regRelationproposedRegistries.get(0).getRegItemObject();
                            } catch (Exception e) {
                                regItemRegistry = null;
                            }
                            if (regItemRegistry != null) {
                                redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemRegistry.getUuid();
                            } else {
                                redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE;
                            }
                            break;
                        case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                            //                        get the collection
                            RegItem regItemCollection = null;
                            List<RegRelationproposed> regRelationproposedCollection = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
                            try {
                                regItemCollection = regRelationproposedCollection.get(0).getRegItemObject();
                            } catch (Exception e) {
                                regItemCollection = null;
                            }
                            if (regItemCollection != null) {
                                redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemCollection.getUuid();
                            } else {
                                RegItem regItemRegister = null;
                                List<RegRelationproposed> regRelationproposedRegistriesCollection = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
                                try {
                                    regItemRegister = regRelationproposedRegistriesCollection.get(0).getRegItemObject();
                                } catch (Exception e) {
                                    regItemRegister = null;
                                }
                                if (regItemRegister != null) {
                                    redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemRegister.getUuid();
                                } else {
                                    redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE;
                                }
                            }
                            break;
                        default:
                            redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE;
                            break;
                    }

                    if (redirectURI.isEmpty()) {
                        redirectURI = "." + WebConstants.PAGE_URINAME_BROWSE;
                    }

                }
                // Check if the current user can perform the action
                boolean canWrite = JspCommon.canWrite(regItemproposed, regUser);

                RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
                if (canWrite
                        || ((regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                        || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                        || regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID))
                        && regItemproposed.getRegUser().getUuid().equals(regUser.getUuid()))) {
                    regItemproposedHandler.handleDiscardProposal(regItemproposed);
                }
            }
            response.sendRedirect(redirectURI);

        } catch (NoResultException e) {
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
