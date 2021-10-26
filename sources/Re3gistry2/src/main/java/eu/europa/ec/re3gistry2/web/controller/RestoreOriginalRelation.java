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
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.javaapi.handler.RegItemproposedHandler;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
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

@WebServlet(WebConstants.PAGE_URINAME_RESTOREORIGINALRELATION)
public class RestoreOriginalRelation extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Getting the current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Init managers
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

        RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Gatering parameters
        // The object to be deleted has a reference to the relate RegLocalizationproposed
        // if the Regrelationproposed is not yet available, the reference to the
        // RegLocalization is passed in order to create the RegLocalizationproposed
        String regLocalizationUuid = request.getParameter(BaseConstants.KEY_REQUEST_REGLOCALIZATIONUUID);

        RegLocalization regLocalization = null;
        RegRelation regRelation = null;
        RegItem regItem = null;

        //Getting the RegLocalization
        try {
            regLocalization = regLocalizationManager.get(regLocalizationUuid);
            regItem = regLocalization.getRegItem();
        } catch (NoResultException e) {
            // If the regLocalization is not available, checking the RegRelation.
            // In case of generical relation (like parent relation) the RegRelation 
            // uuid is passed rather than the RegLocalization uuid
            regRelation = regRelationManager.get(regLocalizationUuid);
            regItem = regRelation.getRegItemSubject();
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
        }

        // Getting the uuid of the register to check the group. If the Register is 
        // empty, using the registry
        RegItem regItemCheck = null;

        if (regItem != null) {
            if (regItemRegister == null && regItemRegistry != null) {
                regItemCheck = regItem;
            } else if (regItemRegister != null) {
                regItemCheck = regItemRegister;
            } else if (regItemRegister == null && regItemRegistry == null) {
                regItemCheck = regItem;
            }
        }

        // Checking the permissions            
        List<RegItemRegGroupRegRoleMapping> itemMappings = regItemRegGroupRegRoleMappingManager.getAll(regItemCheck);
        String[] actionItemRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEITEMPROPOSAL, BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
        boolean permissionItemRegisterRegistry = false;
        if (itemMappings != null) {
            permissionItemRegisterRegistry = UserHelper.checkRegItemAction(actionItemRegisterRegistry, currentUserGroupsMap, itemMappings);
        }

        // Checking the permission to write
        RegItemproposed regItemProposed = null;
        try {
            regItemProposed = regItemproposedManager.getByRegItemReference(regItem);
        } catch (NoResultException e) {
        }
        boolean canWrite = JspCommon.canWrite(regItemProposed, regUser);

        if (permissionItemRegisterRegistry && canWrite) {
            regItemproposedHandler.restoreOriginalRelation(regLocalizationUuid);
        }

        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_RESTOREORIGINALRELATION + WebConstants.PAGE_URINAME_RESTOREORIGINALRELATION + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
