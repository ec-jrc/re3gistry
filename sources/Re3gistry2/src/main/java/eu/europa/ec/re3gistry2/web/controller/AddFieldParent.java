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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
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

@WebServlet(WebConstants.PAGE_URINAME_ADDFIELDPARENT)
public class AddFieldParent extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String regItemUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        String newItemInsert = request.getParameter(BaseConstants.KEY_REQUEST_NEWITEMINSERT);

        regItemUUID = (regItemUUID != null) ? InputSanitizerHelper.sanitizeInput(regItemUUID) : null;
        newItemInsert = (newItemInsert != null) ? InputSanitizerHelper.sanitizeInput(newItemInsert) : null;

        boolean newItemInsertBoolean = (newItemInsert != null && newItemInsert.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));

        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the RegFieldMapping
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);

        // Getting the "parent" RegRelationpredicate
        RegRelationpredicate regRealtionpredicateParent = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);

        // Getting the regItem referenced from the current regItemProposed (passed by parameter)
        RegItem regItemCheck = null;
        RegItemproposed regItemproposedCheck = null;
        RegItemclass regItemclassReference;
        try {
            regItemCheck = regItemManager.get(regItemUUID);
            // Getting the RegItemclass of the current item
            regItemclassReference = regItemCheck.getRegItemclass();
        } catch (NoResultException e) {
            regItemproposedCheck = regItemproposedManager.get(regItemUUID);
            // Getting the RegItemclass of the current item
            regItemclassReference = regItemproposedCheck.getRegItemclass();
        }

        // If it is an insert of a new item, the item passed is the "container".
        // Getting the itemclass child
        if (newItemInsertBoolean) {
            List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItemclassReference);
            if (!childItemclasses.isEmpty()) {
                // Each itemclass shall hava just one child except the Registry itemclass
                regItemclassReference = childItemclasses.get(0);
            } else {
                regItemclassReference = null;
            }
        }

        if (regItemclassReference != null) {
            List<RegItem> regItems = regItemManager.getAll(regItemclassReference);
            request.setAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS, regItems);
        } else {
            request.setAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS, null);
        }

        // Getting the eventual regItemProposed
        if (regItemCheck != null) {
            try {
                regItemproposedCheck = regItemproposedManager.getByRegItemReference(regItemCheck);
            } catch (NoResultException e) {
            }
        }

        // Get the list of relation that are already associated to this item
        // This will be used to disable the RegItem in the select if already used            
        List<RegRelation> regItemsAlreadyAssociated = regRelationManager.getAll(regItemCheck, regRealtionpredicateParent);
        List<RegRelationproposed> regItemproposedsAlreadyAssociated = regRelationproposedManager.getAll(regItemproposedCheck, regRealtionpredicateParent);

        HashMap<String, RegRelation> alreadyAssociatedList = new HashMap();
        regItemsAlreadyAssociated.forEach((tmp) -> {
            alreadyAssociatedList.put(tmp.getRegItemObject().getUuid(), tmp);
        });
        regItemproposedsAlreadyAssociated.forEach((tmp) -> {
            alreadyAssociatedList.put(tmp.getRegItemObject().getUuid(), tmp.getRegRelationReference());
        });

        // Adding the current item to the alreadyAssociatedList in order to 
        // avoid the selection of the parent to itself
        alreadyAssociatedList.put(regItemUUID, null);

        request.setAttribute(BaseConstants.KEY_REQUEST_ITEMS_ALREADY_IN_RELATION, alreadyAssociatedList);

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDFIELDPARENT + WebConstants.PAGE_URINAME_ADDFIELDPARENT + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
