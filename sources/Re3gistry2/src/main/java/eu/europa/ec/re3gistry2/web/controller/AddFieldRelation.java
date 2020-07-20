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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
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

@WebServlet(WebConstants.PAGE_URINAME_ADDFIELDRELATION)
public class AddFieldRelation extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Gatering parameters
        String fieldMappingUUID = request.getParameter(BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID);
        String regItemUUID = request.getParameter(BaseConstants.KEY_REQUEST_ITEMUUID);
        
        fieldMappingUUID = (fieldMappingUUID != null) ? InputSanitizerHelper.sanitizeInput(fieldMappingUUID) : null;
        regItemUUID = (regItemUUID != null) ? InputSanitizerHelper.sanitizeInput(regItemUUID) : null;        

        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the RegFieldMapping
        RegFieldmappingManager regFieldMappingManager = new RegFieldmappingManager(entityManager);
        RegFieldmapping regFieldmapping = regFieldMappingManager.get(fieldMappingUUID);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

        // Getting the "relationReference" RegRelationpredicate
        RegRelationpredicate regRealtionpredicateRelation = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);

        // Getting the List of RegItems from the Itemclass specified in the currend RegField
        RegItemclass regItemclassReference = regFieldmapping.getRegField().getRegItemclassReference();
        if (regItemclassReference != null) {
            List<RegItem> regItems = regItemManager.getAll(regItemclassReference);
            request.setAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS, regItems);
        } else {
            request.setAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS, null);
        }

        // Getting the regItem referenced from the current regItemProposed (passed by parameter)
        RegItem regItemCheck = null;
        RegItemproposed regItemproposedCheck = null;
        try {
            regItemCheck = regItemManager.get(regItemUUID);
        } catch (NoResultException e) {
            regItemproposedCheck = regItemproposedManager.get(regItemUUID);
        }

        // Get the list of relation that are already associated to this item
        // This will be used to disable the RegItem in the select if already used            
        List<RegRelation> regItemsAlreadyAssociated = regRelationManager.getAll(regItemCheck, regRealtionpredicateRelation);
        List<RegRelationproposed> regItemproposedsAlreadyAssociated = regRelationproposedManager.getAll(regItemproposedCheck, regRealtionpredicateRelation);

        HashMap<String, RegRelation> alreadyAssociatedList = new HashMap();
        regItemsAlreadyAssociated.forEach((tmp) -> {
            alreadyAssociatedList.put(tmp.getRegItemObject().getUuid(), tmp);
        });
        
        regItemproposedsAlreadyAssociated.forEach((tmp) -> {
            alreadyAssociatedList.put(tmp.getRegItemObject().getUuid(), tmp.getRegRelationReference());
        });

        // Adding the current item to the alreadyAssociatedList in order to 
        // avoid the selection of the relation to itself
        if (regItemCheck != null && regItemCheck.getRegItemclass().getUuid().equals(regFieldmapping.getRegField().getRegItemclassReference().getUuid())) {
            alreadyAssociatedList.put(regItemCheck.getUuid(), null);
        }
        if (regItemproposedCheck != null && regItemproposedCheck.getRegItemReference() != null && regItemproposedCheck.getRegItemclass().getUuid().equals(regFieldmapping.getRegField().getRegItemclassReference().getUuid())) {
            alreadyAssociatedList.put(regItemproposedCheck.getRegItemReference().getUuid(), null);
        }

        request.setAttribute(BaseConstants.KEY_REQUEST_ITEMS_ALREADY_IN_RELATION, alreadyAssociatedList);

        // Dispatch request
        request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_ADDFIELDRELATION + WebConstants.PAGE_URINAME_ADDFIELDRELATION + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

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
