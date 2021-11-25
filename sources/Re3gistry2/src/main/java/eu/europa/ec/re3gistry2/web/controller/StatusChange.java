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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationproposedUuidHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_STATUSCHANGE)
public class StatusChange extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the current user from session
        RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);

        // Synchronization object
        final Object sync = new Object();

        // Instantiating managers
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);

        RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

        // Preparing variables   
        String itemUuid = null;
        String languageUuid = null;
        String newStatusLocalId = null;
        String[] successors = null;

        // Geting parameters
        itemUuid = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        languageUuid = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);
        newStatusLocalId = request.getParameter(BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID);
        successors = request.getParameterValues(BaseConstants.KEY_REQUEST_SUCCESSORS);

        itemUuid = (itemUuid != null) ? InputSanitizerHelper.sanitizeInput(itemUuid) : null;
        languageUuid = (languageUuid != null) ? InputSanitizerHelper.sanitizeInput(languageUuid) : null;
        newStatusLocalId = (newStatusLocalId != null) ? InputSanitizerHelper.sanitizeInput(newStatusLocalId) : null;

        if (itemUuid != null && itemUuid.length() > 0 && newStatusLocalId != null && newStatusLocalId.length() > 0) {

            try {
                // Getting the new status
                RegStatus newRegStatus = regStatusManager.findByLocalid(newStatusLocalId);

                // Getting the RegItem to change status to
                RegItem regItem = regItemManager.get(itemUuid);

                // Copying the RegItem to RegItemproposed
                RegItemproposed regItemproposed = regItemproposedHandler.completeCopyRegItemToRegItemporposed(regItem, currentUser);

                //Updating the status
                /* ## Start Synchronized ## */
                synchronized (sync) {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    regItemproposed.setRegStatus(newRegStatus);
                    regItemproposedManager.update(regItemproposed);

                    // If it is not a retirement, setting the sussessors
                    if (!newRegStatus.getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)) {

                        // Getting the predicate successor and predecessor
                        RegRelationpredicate predicateSuccessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR);
                        RegRelationpredicate predicatePredecessor = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR);

                        for (String tmp : successors) {
                            RegItem tmpSuccessor = regItemManager.get(tmp);

                            // Successor
                            RegRelationproposed newRegRelationproposedSuccessor = new RegRelationproposed();
                            String newSuccessorUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, predicateSuccessor, null, tmpSuccessor);
                            newRegRelationproposedSuccessor.setUuid(newSuccessorUuid);
                            newRegRelationproposedSuccessor.setInsertdate(new Date());
                            newRegRelationproposedSuccessor.setRegItemObject(tmpSuccessor);
                            newRegRelationproposedSuccessor.setRegItemSubject(null);
                            newRegRelationproposedSuccessor.setRegItemproposedObject(null);
                            newRegRelationproposedSuccessor.setRegItemproposedSubject(regItemproposed);
                            newRegRelationproposedSuccessor.setRegRelationpredicate(predicateSuccessor);

                            regRelationproposedManager.add(newRegRelationproposedSuccessor);

                            // Successor
                            RegRelationproposed newRegRelationproposedPredecessor = new RegRelationproposed();
                            String newPredecessorUuid = RegRelationproposedUuidHelper.getUuid(null, tmpSuccessor, predicateSuccessor, regItemproposed, null);
                            newRegRelationproposedPredecessor.setUuid(newPredecessorUuid);
                            newRegRelationproposedPredecessor.setInsertdate(new Date());
                            newRegRelationproposedPredecessor.setRegItemObject(null);
                            newRegRelationproposedPredecessor.setRegItemSubject(tmpSuccessor);
                            newRegRelationproposedPredecessor.setRegItemproposedObject(regItemproposed);
                            newRegRelationproposedPredecessor.setRegItemproposedSubject(null);
                            newRegRelationproposedPredecessor.setRegRelationpredicate(predicatePredecessor);

                            regRelationproposedManager.add(newRegRelationproposedPredecessor);

                        }
                    } else {
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }
                        entityManager.getTransaction().commit();

                        //get all the proposed item with collection the retired item
                        RegRelationpredicate hasCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
                        List<RegRelationproposed> relationsProposed = regRelationproposedManager.getAllByRegItemObjectAndPredicate(regItem, hasCollection);
                        List<RegItemproposed> subjectsProposed = new ArrayList<>();
                        relationsProposed.forEach((relation) -> {
                            subjectsProposed.add(relation.getRegItemproposedSubject());
                        });
                        for (RegItemproposed regItemproposedSubject : subjectsProposed) {
                            //Updating the status
                            /* ## Start Synchronized ## */
                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            regItemproposedSubject.setRegStatus(regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED));
                            regItemproposedManager.update(regItemproposedSubject);

                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                            entityManager.getTransaction().commit();
                        }

                        //the status is retirement, so retire as well all the valid children and not accept the submitted/draft items
                        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
                        List<RegRelation> relations = regRelationManager.getAllByRegItemObjectAndPredicate(regItem, hasCollection);
                        List<RegItem> subjects = new ArrayList<>();
                        relations.forEach((relation) -> {
                            subjects.add(relation.getRegItemSubject());
                        });
                        for (RegItem subject : subjects) {
                            String statusUuid = subject.getRegStatus().getLocalid();
                            if (statusUuid.equals(BaseConstants.KEY_STATUS_LOCALID_VALID)) {
                                // Copying the RegItem to RegItemproposed
                                RegItemproposed regItemproposedsubject = regItemproposedHandler.completeCopyRegItemToRegItemporposed(subject, currentUser);
                                //Updating the status
                                /* ## Start Synchronized ## */
                                if (!entityManager.getTransaction().isActive()) {
                                    entityManager.getTransaction().begin();
                                }
                                regItemproposedsubject.setRegStatus(newRegStatus);
                                regItemproposedManager.update(regItemproposedsubject);

                                if (!entityManager.getTransaction().isActive()) {
                                    entityManager.getTransaction().begin();
                                }
                                entityManager.getTransaction().commit();
                            }
                        }

                    }
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    entityManager.getTransaction().commit();
                }
                /* ## End Synchronized ## */

                response.sendRedirect("." + WebConstants.PAGE_PATH_BROWSE + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + itemUuid + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + languageUuid);

            } catch (NoResultException e) {
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_STATUSCHANGE + WebConstants.PAGE_URINAME_STATUSCHANGE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            } catch (Exception e) {
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_STATUSCHANGE + WebConstants.PAGE_URINAME_STATUSCHANGE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
            } finally {
                if (entityManager != null) {
                    entityManager.close();
                }
            }

        } else {
            // Dispatch request
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_STATUSCHANGE + WebConstants.PAGE_URINAME_STATUSCHANGE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
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
