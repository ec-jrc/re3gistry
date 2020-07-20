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
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.web.utility.jsp.JspCommon;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_REMOVEFIELDVALUE)
public class RemoveFieldValue extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Init managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

        // Getting the current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

        // Gatering parameters
        String regLocalizationproposedUUID = request.getParameter(BaseConstants.KEY_REQUEST_LOCALIZATIONPROPOSEDUUID);
        regLocalizationproposedUUID = (regLocalizationproposedUUID != null) ? InputSanitizerHelper.sanitizeInput(regLocalizationproposedUUID) : null;

        // The object to be deleted has a reference to the relate RegLocalizationproposed
        // if the Regrelationproposed is not yet available, the reference to the
        // RegLocalization is passed in order to create the RegLocalizationproposed
        RegItem regItem = null;
        RegItemproposed regItemproposed = null;

        try {
            // Getting the regLocalizationproposed to remove
            RegLocalizationproposed regLocalizationproposed = regLocalizationproposedManager.get(regLocalizationproposedUUID);
            regItemproposed = regLocalizationproposed.getRegItemproposed();
        } catch (NoResultException e) {
            // In this case the reference passed is the RegLocalization and not the 
            // RegLocalizationproposed)
            try {
                try {
                    // Getting the related RegLocalization
                    RegLocalization tmpLocalization = regLocalizationManager.get(regLocalizationproposedUUID);
                    // Getting the related RegItem
                    regItem = tmpLocalization.getRegItem();
                } catch (NoResultException ee) {
                    // Trying to get the RegRelation
                    RegRelation tmpRegRelation = regRelationManager.get(regLocalizationproposedUUID);
                    regItem = tmpRegRelation.getRegItemSubject();
                }

                try {
                    regItemproposed = regItemproposedManager.getByRegItemReference(regItem);
                } catch (NoResultException e1) {
                }
            } catch (NoResultException e1) {

                // In this case the reference passed is the Uuid of the RegRelationproposed
                // to be deleted.
                RegRelationproposed regRelationproposed = regRelationproposedManager.get(regLocalizationproposedUUID);
                regItemproposed = regRelationproposed.getRegItemproposedSubject();
            }
        }

        // Setting the registry
        RegItem regItemRegistry = null;
        if (regItem != null && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
            try {
                regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegistry = null;
            }
        } else if (regItemproposed != null && !regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            List<RegRelationproposed> regRelationRegistries = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegistry);
            try {
                regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegistry = null;
            }
        }

        // Setting the register
        RegItem regItemRegister = null;
        if (regItem != null && !regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
            List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
            try {
                regItemRegister = regRelationRegisters.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegister = null;
            }
        } else if (regItemproposed != null && !regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
            List<RegRelationproposed> regRelationproposedRegisters = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateRegister);
            try {
                regItemRegister = regRelationproposedRegisters.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegister = null;
            }
        }

        // Getting the current user from session
        RegUser currentUser = (RegUser) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USER);

        // Getting the uuid of the register to check the group. If the Register is 
        // empty, using the registry
        RegItem regItemCheck = null;
        RegItemproposed regItemproposedCheck = null;

        if (regItem != null) {
            if (regItemRegister == null && regItemRegistry != null) {
                regItemCheck = regItem;
            } else if (regItemRegister != null) {
                regItemCheck = regItemRegister;
            } else if (regItemRegister == null && regItemRegistry == null) {
                regItemCheck = regItem;
            }
        } else {
            if (regItemRegister == null && regItemRegistry != null) {
                regItemproposedCheck = regItemproposed;
            } else if (regItemRegister != null) {
                regItemCheck = regItemRegister;
            } else if (regItemRegister == null && regItemRegistry == null) {
                regItemproposedCheck = regItemproposed;
            }
        }

        // Getting the item, group, role mapping for this item
        List<RegItemRegGroupRegRoleMapping> itemMappings = null;
        List<RegItemproposedRegGroupRegRoleMapping> itemProposedMappings = null;
        if (regItemproposedCheck == null) {
            itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);
            // If the item is not a registry, adding the registry to the itemMappings in
            // order to allow the registry managers to manage the contained registers and items
            List<RegItemRegGroupRegRoleMapping> itemMappingsRegistry = regItemRegGroupRegRoleMappingManager.getAll(regItemRegistry);
            itemMappings.addAll(itemMappingsRegistry);
        } else {
            itemProposedMappings = regItemproposedRegGroupRegRoleMappingManager.getAll(regItemproposedCheck);
        }

        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};

        boolean permissionItemRegisterRegistry;

        if (itemMappings != null) {
            permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
        } else {
            permissionItemRegisterRegistry = UserHelper.checkRegItemproposedAction(actionItemRegisterRegistry, currentUserGroupsMap, itemProposedMappings);
        }

        // Check if there are no proposals or if the user is the current owner of the proposal
        /*boolean currentUserOwnerOfProposal = false;
        if (regItemproposed != null) {
            currentUserOwnerOfProposal = (regItemproposed.getRegUser().getUuid().equals(currentUser.getUuid()));
        } else {
            currentUserOwnerOfProposal = true;
        }*/
        boolean canWrite = JspCommon.canWrite(regItemproposed, currentUser);

        String removeType = "";
        if (permissionItemRegisterRegistry && canWrite) {
            // Checking the permission
            removeType = regItemproposedHandler.handleDeleteFieldContent(regLocalizationproposedUUID, regUser);
        }
        request.setAttribute(BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE, removeType);

        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_REMOVEFIELDVALUE + WebConstants.PAGE_URINAME_REMOVEFIELDVALUE + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex);
        }
    }
}
