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
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.UserHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRoleManager;
import eu.europa.ec.re3gistry2.javaapi.cache.CacheAll;
import eu.europa.ec.re3gistry2.javaapi.cache.EhCache;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.handler.RegActionHandler;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegRole;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.web.utility.SendEmailFromAction;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(WebConstants.PAGE_URINAME_CONTROLBODY)
public class ControlBody extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        // Setup the entity manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        // Getting the current user
        RegUser regUser = (RegUser) request.getAttribute(BaseConstants.KEY_REQUEST_REGUSER);

        // Instantiating managers
        RegRoleManager regRoleManager = new RegRoleManager(entityManager);
        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
        RegItemhistoryManager regItemhistoryManager = new RegItemhistoryManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(entityManager);

        //Getting request parameter
        String regActionUuid = request.getParameter(BaseConstants.KEY_REQUEST_ACTION_UUID);
        String languageUUID = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGEUUID);

        regActionUuid = (regActionUuid != null) ? InputSanitizerHelper.sanitizeInput(regActionUuid) : null;
        languageUUID = (languageUUID != null) ? InputSanitizerHelper.sanitizeInput(languageUUID) : null;

        // Getting form parameter
        String formRegActionUuid = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_ACTIONUUID);
        String formSubmitAction = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_SUBMITACTION);
        String formComments = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_COMMENTS);
        String formActionType = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_APPROVE_TYPE);
        String formIssueReference = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_ISSUEREFERENCE);

        formRegActionUuid = (formRegActionUuid != null) ? InputSanitizerHelper.sanitizeInput(formRegActionUuid) : null;
        formSubmitAction = (formSubmitAction != null) ? InputSanitizerHelper.sanitizeInput(formSubmitAction) : null;
        formActionType = (formActionType != null) ? InputSanitizerHelper.sanitizeInput(formActionType) : null;
        
        // Handling charset for the textual contents
        byte[] bytes;
        if (formIssueReference != null) {
            bytes = formIssueReference.getBytes(Charset.defaultCharset());
            formIssueReference = new String(bytes, StandardCharsets.UTF_8);
            formIssueReference = InputSanitizerHelper.sanitizeInput(formIssueReference);
        }
        if (formComments != null) {
            bytes = formComments.getBytes(Charset.defaultCharset());
            formComments = new String(bytes, StandardCharsets.UTF_8);
            formComments = InputSanitizerHelper.sanitizeInput(formComments);
        }
        formIssueReference = (formIssueReference != null) ? InputSanitizerHelper.sanitizeInput(formIssueReference) : null;

        //Getting the master language
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        request.setAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE, masterLanguage);

        // Getting the language by parameter (if not available the master language is used)
        RegLanguagecode currentLanguage = null;
        if (languageUUID != null && languageUUID.length() == 2) {
            try {
                currentLanguage = regLanguagecodeManager.get(languageUUID);
            } catch (Exception e) {
                currentLanguage = masterLanguage;
            }
        } else {
            currentLanguage = masterLanguage;
        }
        request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE, currentLanguage);

        // Getting the user permission mapping from the session
        HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

        // Checking if the current user has the rights to add a new itemclass
        String[] actionApproveProposals = {BaseConstants.KEY_USER_ACTION_APPROVEPROPOSAL};
        boolean permissionApproveProposals = UserHelper.checkGenericAction(actionApproveProposals, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

        if (permissionApproveProposals) {

            if (formRegActionUuid != null && formRegActionUuid.length() > 0 && formSubmitAction != null && formSubmitAction.length() > 0 && formActionType != null && formActionType.length() > 0) {
                // This is a save request

                RegActionHandler regActionHandler = new RegActionHandler();
                regActionHandler.controlBodyAction(formRegActionUuid, formActionType, formComments, formIssueReference, regUser);

                regActionUuid = formRegActionUuid;

            }
            // This is a view request

            
//            //Cache the parent of the proposed item
//            ItemCache cache = (ItemCache) request.getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
//            if (cache == null) {
//                cache = new EhCache();
//                request.setAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY, cache);
//            }
//            if (formRegActionUuid != null){
//                RegAction regActionForCache;
//                List<RegItemproposed> regItemProposeds;
//                try{
//                    regActionForCache = regActionManager.get(formRegActionUuid);
//                    regItemProposeds = regItemproposedManager.getAll(regActionForCache);
//                }catch(Exception e){
//                    regItemProposeds = Collections.emptyList();
//                }
//                 
//                HashSet<String> parentsList = new HashSet <String>();
//                HashSet<String> collectionsList = new HashSet <String>();
//                for (RegItemproposed regItemProposed : regItemProposeds) {
//                    List<RegRelationproposed> collections = regRelationproposedManager.getAllByObject(regItemProposed);
//                    
//                    if(collections != null && collections.size() > 0 ){
//                        for (RegRelationproposed collection : collections) {
//                            if(collection.getRegRelationpredicate().getLocalid().equals(BaseConstants.KEY_PREDICATE_COLLECTION)){
//                                String uuid = collection.getRegItemObject().getUuid();
//                                collectionsList.add(uuid);
//                            }
//                        }
//                    }
//                    if(regItemProposed.getRegAction() != null && regItemProposed.getRegAction().getRegItemRegister() != null){
//                        if(regItemProposed.getRegAction().getRegItemRegister().getRegItemclass() != null && regItemProposed.getRegAction().getRegItemRegister().getRegItemclass().getUuid() != null){
//                            String uuid = regItemProposed.getRegAction().getRegItemRegister().getRegItemclass().getUuid();
//                            parentsList.add(uuid);
//                        } 
//                    } 
//                } 
//                EntityManager emCache = PersistenceFactory.getEntityManagerFactory().createEntityManager();
//                CacheAll cacheAll = new CacheAll(emCache, cache, null);
//                for (String uuid : parentsList) {
////                    EntityManager emCache = Persistence.createEntityManagerFactory(BaseConstants.KEY_PROPERTY_PERSISTENCE_UNIT_NAME).createEntityManager();   
//                    cacheAll.run(uuid); 
//                } 
//                for (String uuid : collectionsList) {
////                    EntityManager emCache = Persistence.createEntityManagerFactory(BaseConstants.KEY_PROPERTY_PERSISTENCE_UNIT_NAME).createEntityManager();   
//                    cacheAll.run(uuid); 
//                } 
//            }

            
            // Getting the submitting organization RegRole
            RegRole regRoleControlBody = regRoleManager.getByLocalId(BaseConstants.KEY_ROLE_CONTROLBODY);

            // Getting the list of mapping that contains the specified role (control body)
            List<RegItemRegGroupRegRoleMapping> tmps = regItemRegGroupRegRoleMappingManager.getAll(regRoleControlBody);

            // Getting the action for which the current user is CB
            List<RegAction> regActions = new ArrayList();
            for (RegItemRegGroupRegRoleMapping tmp : tmps) {
                List<RegAction> tmpRegActions = regActionManager.getAllByRegister(tmp.getRegItem());
                regActions.addAll(tmpRegActions);
            }

            try {

                // Checking if it is a detail reqest or a list request
                RegAction regAction = null;
                List<RegItemproposed> regItemproposeds = null;
                List<RegItemhistory> regItemhistorys = null;
                List<RegItem> regItems = null;

                if (regActionUuid != null && regActionUuid.length() > 0) {

                    regActions = new ArrayList();

                    // Getting the detail RegAction
                    try {
                        // Gettign the RegAction passed by Uuid
                        regAction = regActionManager.get(regActionUuid);
                        regActions.add(regAction);

                        if (regAction.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_NOTACCEPTED)) {
                            // Getting the list of RegItemhistory contained in the action
                            regItemhistorys = regItemhistoryManager.getAll(regAction);
                        } else if (regAction.getRegStatus().getLocalid().equals(BaseConstants.KEY_STATUS_LOCALID_PUBLISHED)) {
                            // Getting the list of RegItem contained in the action
                            regItems = regItemManager.getAll(regAction);
                        } else {
                            // Getting the list of RegItemproposed contained in the action
                            regItemproposeds = regItemproposedManager.getAll(regAction);
                        }

                        // If the reg action is valid but no related items are shown 
                        // searching in the history: they may have been historicized
                        // subsequently in another action
                        if ((regItemhistorys == null || regItemhistorys.isEmpty()) && (regItems == null || regItems.isEmpty()) && (regItemproposeds == null || regItemproposeds.isEmpty())) {
                            // Getting the list of RegItemhistory contained in the action
                            regItemhistorys = regItemhistoryManager.getAll(regAction);
                        }

                    } catch (NoResultException e) {
                    }
                }
                
                
                if (regItemproposeds == null && regAction!=null) {
                    regItemproposeds = (List<RegItemproposed>) regItemproposedManager.getAll(regAction);
                }
                
                request.setAttribute(BaseConstants.KEY_REQUEST_ACTION_LIST, regActions);
                request.setAttribute(BaseConstants.KEY_REQUEST_ACTION, regAction);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_PROPOSEDS, regItemproposeds);
                request.setAttribute(BaseConstants.KEY_REQUEST_ITEM_HISTORYS, regItemhistorys);
                request.setAttribute(BaseConstants.KEY_REQUEST_REGITEMS, regItems);
                
                if (formRegActionUuid != null && formRegActionUuid.length() > 0 && formSubmitAction != null && formSubmitAction.length() > 0 && formActionType != null && formActionType.length() > 0) {
                MailManager.sendActionMail(regItemproposeds, regAction, Configuration.getInstance().getLocalization(), BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY);
                }
                
                //Dispatch request
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_CONTROLBODY + WebConstants.PAGE_URINAME_CONTROLBODY + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);

            } catch (Exception e) {
                // Redirecting to the RegItemclasses list page
                response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
            }
        } else {
            response.sendRedirect("." + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX);
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
