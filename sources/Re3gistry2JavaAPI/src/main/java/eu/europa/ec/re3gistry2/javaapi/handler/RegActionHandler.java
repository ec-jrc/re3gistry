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
package eu.europa.ec.re3gistry2.javaapi.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.logging.log4j.Logger;

public class RegActionHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManager;

    // System localization
    ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();

    /**
     * This method initializes the class
     *
     * @throws Exception
     */
    public RegActionHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    public void submitAction(String actionUuid, String changeLog, String issueReference, RegUser regUser) throws Exception {

        // initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        try {

            // Getting the RegStatus submitted
            RegStatus regStatusSubmitted = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_SUBMITTED);

            // Getting the RegAction
            RegAction regAction = regActionManager.get(actionUuid);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Setting the submitted status to the RegAction
                regAction.setRegStatus(regStatusSubmitted);

                // Setting the submittedBy
                regAction.setSubmittedBy(regUser);

                // Setting the changelog
                if (changeLog != null && changeLog.length() > 0) {
                    regAction.setChangelog(changeLog);
                }

                // Setting the issue reference
                if (issueReference != null && issueReference.length() > 0) {
                    regAction.setIssueTrackerLink(issueReference);
                }

                //Updating the RegAction
                regActionManager.update(regAction);

                //Getting the relate RegItemProposed
                List<RegItemproposed> regItemProposeds = regItemproposedManager.getAll(regAction);

                for (RegItemproposed regItemproposed : regItemProposeds) {

                    // Setting the submitted status
                    if (!regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                            && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                            && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)
                            && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED)) {
                        regItemproposed.setRegStatus(regStatusSubmitted);
                    }

                    // Update the RegItemproposed
                    regItemproposedManager.update(regItemproposed);
                }
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void controlBodyAction(String actionUuid, String approveType, String comment, String issueReference, RegUser regUser) throws Exception {

        // initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        RegItemhistoryHandler regItemhistoryHandler = new RegItemhistoryHandler();

        try {

            // Getting the RegAction
            RegAction regAction = regActionManager.get(actionUuid);

            // Getting the right RegStatus based on the approveType
            RegStatus regStatusUpdate = null;

            if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_APPROVE)) {
                regStatusUpdate = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED);
                regAction.setApprovedBy(regUser);
            } else if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_APPROVEWITHCHANGES)) {
                regStatusUpdate = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);
            } else if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_REJECT)) {
                regStatusUpdate = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED);
                regAction.setRejectedBy(regUser);
            }

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                // Setting the submitted status to the RegAction
                regAction.setRegStatus(regStatusUpdate);

                // Setting the comments if the ction is "Accepted with changes"
                if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_APPROVEWITHCHANGES) && comment != null && comment.length() > 0) {
                    regAction.setChangeRequest(comment);
                }

                // Setting the comments if the ction is "Rejected"
                if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_REJECT) && comment != null && comment.length() > 0) {
                    regAction.setRejectMessage(comment);
                }

                // Updating issue reference if not null
                if (issueReference != null) {
                    regAction.setIssueTrackerLink(issueReference);
                }

                //Updating the RegAction
                regActionManager.update(regAction);

                /* ## End Synchronized ## */
                // Getting the list of related RegItemproposeds
                List<RegItemproposed> regItemProposeds = regItemproposedManager.getAll(regAction);

                // In case of reject action, move the related elements in the history
                if (approveType.equals(BaseConstants.KEY_ACTION_TYPE_REJECT)) {
                    for (RegItemproposed regItemproposed : regItemProposeds) {

                        if (!regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED)) {
                            regItemproposed.setRegStatus(regStatusUpdate);
                            regItemproposedManager.update(regItemproposed);
                            entityManager.getTransaction().commit();

                            regItemhistoryHandler.regItemProposedToRegItemhistory(regItemproposed);

                            if (!entityManager.getTransaction().isActive()) {
                                entityManager.getTransaction().begin();
                            }
                        }
                    }
                } else {
                    // Otherwise updating the status of the related items
                    for (RegItemproposed regItemproposed : regItemProposeds) {

                        // Setting the submitted status
                        if (!regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_SUPERSEDED)
                                && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_INVALID)
                                && !regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_RETIRED)) {
                            regItemproposed.setRegStatus(regStatusUpdate);
                        }
                        // Update the RegItemproposed
                        regItemproposedManager.update(regItemproposed);
                    }
                }
                entityManager.getTransaction().commit();
            }
        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void updateLabeleAction(String actionUuid, String label) throws Exception {

        // initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);

        try {

            // Getting the RegAction
            RegAction regAction = regActionManager.get(actionUuid);

            // Updating the label
            regAction.setLabel(label);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                //Updating the RegAction
                regActionManager.update(regAction);

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void registerManagerAction(String actionUuid, RegUser regUser) throws Exception {

        // initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        RegItemHandler regItemHandler = new RegItemHandler();
        RegItemhistoryHandler regItemhistoryHandler = new RegItemhistoryHandler();

        try {

            // Getting the RegAction
            RegAction regAction = regActionManager.get(actionUuid);

            // Getting the right RegStatus for each element
            RegStatus regStatusUpdateValid = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_VALID);
            RegStatus regStatusUpdatePublished = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_PUBLISHED);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                regAction.setPublishedBy(regUser);

                // Setting the submitted status to the RegAction
                regAction.setRegStatus(regStatusUpdatePublished);

                entityManager.getTransaction().commit();

                // Getting the list of related RegItemproposeds
                List<RegItemproposed> regItemProposeds = regItemproposedManager.getAll(regAction);

                // Managing the RegItemproposeds related to the current RegAction
                for (RegItemproposed regItemproposed : regItemProposeds) {

                    if (regItemproposed.getRegItemReference() != null) {
                        //Move the related RegItem to RegItemhistory
                        regItemhistoryHandler.copyRegItemToRegItemhistory(regItemproposed.getRegItemReference());
                    }

                    // If the RegIremproposed status is draft, it has to be set to valid.
                    // If it has another status, it has to be written as it is
                    // (e.g. retirement)
                    if (regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED)) {
                        regItemproposed.setRegStatus(regStatusUpdateValid);
                    }

                    // Move the RegItemproposed to RegItem
                    regItemHandler.regItemProposedToRegItem(regItemproposed);

                    //Remove old regItem
                    if (regItemproposed.getRegItemReference() != null) {
                        cleanupRegItemAfterStorycization(regItemproposed.getRegItemReference());
                    }
                }

            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void registryManagerAction(String actionUuid, RegUser regUser) throws Exception {
        this.registryManagerAction(actionUuid, regUser, false, null, null);
    }
    
    public void registryManagerActionSimplifiedWorkflow(String actionUuid, RegUser regUser, String changelog, String issueReference) throws Exception {
        this.registryManagerAction(actionUuid, regUser, true, changelog, issueReference);
    }
    
    private void registryManagerAction(String actionUuid, RegUser regUser, boolean simplifiedWorkflow, String changelog, String issueReference) throws Exception {

        // initializing managers
        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        RegItemHandler regItemHandler = new RegItemHandler();
        RegItemhistoryHandler regItemhistoryHandler = new RegItemhistoryHandler();

        try {

            // Getting the RegAction
            RegAction regAction = regActionManager.get(actionUuid);

            // Getting the right RegStatus based on the approveType
            RegStatus regStatusUpdateValid = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_VALID);
            RegStatus regStatusUpdatePublished = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_PUBLISHED);

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                regAction.setPublishedBy(regUser);

                // Setting the submitted status to the RegAction
                regAction.setRegStatus(regStatusUpdatePublished);
                
                if (simplifiedWorkflow) {
                    if (changelog!=null) {
                        regAction.setChangelog(changelog);
                    }
                    if (issueReference!=null) {
                        regAction.setIssueTrackerLink(issueReference);
                    }
                }

                entityManager.getTransaction().commit();

                // Getting the list of related RegItemproposeds
                List<RegItemproposed> regItemProposeds = regItemproposedManager.getAll(regAction);

                // Managing the RegItemproposeds related to the current RegAction
                for (RegItemproposed regItemproposed : regItemProposeds) {
                    
                    if (regItemproposed.getRegItemReference() != null) {
                        //Move the related RegItem to RegItemhistory
                        regItemhistoryHandler.copyRegItemToRegItemhistory(regItemproposed.getRegItemReference());
                    }

                    // If the RegIremproposed status is accepted (or submitted for 
                    // the registry), it has to be set to valid.
                    // If it has another status, it has to be written as it is
                    // (e.g. retirement)
                    if (regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_ACCEPTED)
                            || (regItemproposed.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_DRAFT)
                            && (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)
                            || simplifiedWorkflow
                            || regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)))) {
                        regItemproposed.setRegStatus(regStatusUpdateValid);
                    }
                    //regItemproposed = regItemproposedManager.getByLocalidAndRegItemClass(regItemproposed.getLocalid(), regItemproposed.getRegItemclass());
                    // Move the RegItemproposed to RegItem
                    regItemHandler.regItemProposedToRegItem(regItemproposed);
                    regItemHandler.removeUnusedRelations(regItemproposed);
                    
                    
                    //Remove old regItem
                    if (regItemproposed.getRegItemReference() != null) {
                        cleanupRegItemAfterStorycization(regItemproposed.getRegItemReference());
                    }
                }
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }
        }
    }

    public void deleteAction(RegAction regAction) {
        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        try {
            // Check, if this was the only item in the RegAction, removing it
            List<RegItemproposed> regItemproposeds = regItemproposedManager.getAll(regAction);
            if (regItemproposeds.size() < 1) {
                RegActionManager regActionManager = new RegActionManager(entityManager);
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                regActionManager.delete(regAction);
                entityManager.getTransaction().commit();
            }

            /* ## End Synchronized ## */
        } catch (NoResultException e) {
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void cleanupRegItemAfterStorycization(RegItem regItem) throws Exception {

        // Instantiating managers
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);

        // Copying the RegItemLocalizations to history and removing them
        List<RegLocalization> regLocalizations = regLocalizationManager.getAll(regItem);
        for (RegLocalization tmpRegLocalization : regLocalizations) {
            // Delete regLocalization
            regLocalizationManager.delete(tmpRegLocalization);
        }

        // The removal of the regRelation needs to be done after
        // the removal of the RegLocalization because of foreign
        // key constraints
        List<RegRelation> regRelations = regRelationManager.getAllBySubject(regItem);
        for (RegRelation tmpRegRelation : regRelations) {
            regRelationManager.delete(tmpRegRelation);
        }
        List<RegRelation> regRelationsObject = regRelationManager.getAllByObject(regItem);
        for (RegRelation tmpRegRelation : regRelationsObject) {
            regRelationManager.delete(tmpRegRelation);
        }

        // Copy RegItemRegGroupRegRoleMapping to history and removing them
        List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regItem);
        for (RegItemRegGroupRegRoleMapping tmpRegItemRegGroupRegRoleMapping : regItemRegGroupRegRoleMappings) {

            // Delete RegItemRegGroupRegRoleMapping
            regItemRegGroupRegRoleMappingManager.delete(tmpRegItemRegGroupRegRoleMapping);
        }

        //removing RegItem
        RegItemManager regItemManager = new RegItemManager(entityManager);
        regItemManager.delete(regItem);
    }
}
