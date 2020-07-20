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
package eu.europa.ec.re3gistry2.javaapi.solr;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.ItemHelper;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class SolrHandler {

    public static boolean indexComplete() {

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Do not run 2 complete Solr export together
        if (checkSolrCompleteIndexinglRunning()) {
            return false;
        }

        try {

            // Solr indexing
            SolrClient solrClient = getSolrClient();

            if (solrClient == null) {
                return false;
            }

            // Getting the DB manager
            EntityManager em = PersistenceFactory.getEntityManagerFactory().createEntityManager();

            // Instantiating managers
            RegItemManager regItemManager = new RegItemManager(em);

            // Getting all RegItems
            List<RegItem> regItems = regItemManager.getAll();

            // Processing the RegItems
            SolrInputDocument document;

            // Locking other Solr complete index requests
            createSolrCompleteIndexinglRunningFile();

            for (RegItem regItem : regItems) {

                document = indexItem(regItem, em);
                solrClient.add(document);
                solrClient.commit();

            }

            // Removing lock for other Solr complete index requests
            deleteSolrCompleteIndexinglRunningFile();

            return true;

        } catch (Exception e) {
            logger.error(e);
            deleteSolrCompleteIndexinglRunningFile();
            return false;
        }
    }

    public static boolean indexSingleItem(RegItem regItem) {

        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        try {

            // Solr indexing
            SolrClient solrClient = getSolrClient();

            if (solrClient == null) {
                return false;
            }

            // Getting the DB manager
            EntityManager em = PersistenceFactory.getEntityManagerFactory().createEntityManager();

            // Processing the RegItem
            SolrInputDocument document;

            document = indexItem(regItem, em);
            solrClient.add(document);
            solrClient.commit();

            return true;

        } catch (Exception e) {
            logger.error(e);
            deleteSolrCompleteIndexinglRunningFile();
            return false;
        }
    }

    private static SolrClient getSolrClient() {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Getting the configuration
        Properties properties = Configuration.getInstance().getProperties();

        try {
            // Getting Solr configurations
            String isSolrActive = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_ACTIVE);
            String solrUrl = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_URL);
            String solrCoreFields = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_CORE);
            int solrConnectionTimeout = Integer.parseInt(properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_CONNECTION_TIMEOUT, "10000"));
            int solrSocketTimeout = Integer.parseInt(properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_SOCKET_TIMEOUT, "60000"));

            // Checking if Solr is active in the system
            if (!isSolrActive.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE) || solrUrl == null || solrUrl.length() == 0 || solrCoreFields == null || solrCoreFields.length() == 0) {
                return null;
            }

            // Returning the SolrClient
            return new HttpSolrClient.Builder(solrUrl + solrCoreFields)
                    .withConnectionTimeout(solrConnectionTimeout)
                    .withSocketTimeout(solrSocketTimeout)
                    .build();

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public static SolrDocumentList performSearch(String term, RegItem regItem, RegLanguagecode searchLanguage, int start, int rows) {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        // Getting the configuration
        Properties properties = Configuration.getInstance().getProperties();

        if (regItem == null || term == null || term.trim().isEmpty()) {
            return null;
        } else {
            // Preparing text for search            
            term = term.trim();
//            Pattern pattern = Pattern.compile("\\s");
//            Matcher matcher = pattern.matcher(term);
            term = term + "*";
//            if(matcher.find()){
//                term = "\""+term+"*\"";
//            }          
        }

        try {

            // Solr indexing
            SolrClient solrClient = getSolrClient();

            if (solrClient == null) {
                return null;
            }

            // Getting the DB manager
            EntityManager em = PersistenceFactory.getEntityManagerFactory().createEntityManager();

            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(em);
            RegRelationManager regRelationManager = new RegRelationManager(em);

            // Getting & setting the registry
            String registrylocalid = "";
            String registryItemclasslocalid = "";
            String registryBaseuri = "";
            RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
            RegItem regItemRegistry;
            List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
            if (!regRelationRegistries.isEmpty()) {
                // An item can have only one registry => taking the index 0;
                regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
                registryItemclasslocalid = regItemRegistry.getRegItemclass().getLocalid();
                registryBaseuri = regItemRegistry.getRegItemclass().getBaseuri();
                registrylocalid = regItemRegistry.getLocalid();

            } else {
                regItemRegistry = regItem;
                registryItemclasslocalid = regItemRegistry.getRegItemclass().getLocalid();
                registryBaseuri = regItemRegistry.getRegItemclass().getBaseuri();
                registrylocalid = regItemRegistry.getLocalid();
            }

            // Getting & setting the register
            String registerlocalid = "";
            String registerBaseuri = "";
            String registerItemclasslocalid = "";
            RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
            RegItem regItemRegister;
            List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
            if (!regRelationRegisters.isEmpty()) {
                // An item can have only one register => taking the index 0;
                regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                registerlocalid = regItemRegister.getLocalid();
                registerItemclasslocalid = regItemRegister.getRegItemclass().getLocalid();
                registerBaseuri = regItemRegister.getRegItemclass().getBaseuri();

            } else if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                regItemRegister = regItem;
                registerlocalid = regItemRegister.getLocalid();
                registerItemclasslocalid = regItemRegister.getRegItemclass().getLocalid();
                registerBaseuri = regItemRegister.getRegItemclass().getBaseuri();
            } else {
                regItemRegister = null;
            }

            // Getting & setting the collection (if available)
            String collectionlocalid = "";
            String collectionItemclasslocalid = "";
            RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
            try {
                List<RegRelation> regRelationCollections = regRelationManager.getAll(regItem, regRelationpredicateCollection);
                // An item can have only one collection => taking the index 0;
                RegItem regItemCollection = regRelationCollections.get(0).getRegItemObject();
                collectionlocalid = regItemCollection.getLocalid();
                collectionItemclasslocalid = regItemCollection.getRegItemclass().getLocalid();
            } catch (Exception e) {
                // The item doesn't have a collection
            }

            // Getting regItem's child itemclass
            RegItemclassManager regItemclassManager = new RegItemclassManager(em);
            List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());

            // Preparing query
            SolrQuery query = new SolrQuery();
            query.setQuery(BaseConstants.KEY_PROPERTY_SOLR_QUERY_FIELD_LABEL_PREFIX + searchLanguage.getIso6391code() + ":" + term);

            List<String> filterQueries = new ArrayList<>();
            filterQueries.add("registry_localid:" + registrylocalid);

            if (!registerlocalid.isEmpty()) {
                filterQueries.add("register_localid:" + registerlocalid);
            }

            if (childItemclasses != null && childItemclasses.size() == 1) {
                filterQueries.add("itemclass_localid:" + childItemclasses.get(0).getLocalid());
            }

            if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)
                    || regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                filterQueries.add("-collection_localid:*");

                if (childItemclasses != null && childItemclasses.size() > 1) {
                    filterQueries.add("itemclass_type:register");
                    filterQueries.add("itemclass_localid:*");
                }

            } else {
                if (childItemclasses != null && childItemclasses.size() == 1) {
                    filterQueries.add("collection_localid:" + regItem.getLocalid());
                } else {
                    filterQueries.add("-collection_localid:*");
                }
            }

            query.addFilterQuery(filterQueries.toArray(new String[query.size()]));
            query.setFields(BaseConstants.KEY_PROPERTY_SOLR_QUERY_FL);
            query.setStart(0);

            final QueryResponse response = solrClient.query(query);
            final SolrDocumentList documents = response.getResults();

            return documents;

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private static SolrInputDocument indexItem(RegItem regItem, EntityManager em) throws Exception {

        // Instantiating Managers
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(em);
        RegFieldManager regFieldManager = new RegFieldManager(em);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(em);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(em);
        RegItemclassManager regItemclassManager = new RegItemclassManager(em);
        RegRelationManager regRelationManager = new RegRelationManager(em);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(em);

        // Create the Solr document representing the item
        SolrInputDocument document = new SolrInputDocument();
        document.setField(BaseConstants.KEY_SOLR_ITEM_ID, regItem.getUuid());
        document.setField(BaseConstants.KEY_SOLR_ITEM_LOCALID, regItem.getLocalid());

        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();
        document.setField(BaseConstants.KEY_SOLR_ITEM_MASTER_LANGUAGE, masterLanguage.getIso6391code());

        // #--- Creating standard item fields ---# //
        // Adding standard metadata
        document.setField(BaseConstants.KEY_SOLR_ITEM_EXTERNAL, regItem.getExternal());
        document.setField(BaseConstants.KEY_SOLR_ITEM_CURRENTVERSION, regItem.getCurrentversion());
        document.setField(BaseConstants.KEY_SOLR_ITEM_INSERTDATE, regItem.getInsertdate());
        if (regItem.getEditdate() != null) {
            document.setField(BaseConstants.KEY_SOLR_ITEM_EDITDATE, regItem.getEditdate());
        }

        // Adding the item's itemclass information
        document.setField(BaseConstants.KEY_SOLR_ITEM_ITEMCLASSLOCALID, regItem.getRegItemclass().getLocalid());
        document.setField(BaseConstants.KEY_SOLR_ITEM_ITEMCLASSTYPE, regItem.getRegItemclass().getRegItemclasstype().getLocalid());

        //Adding the parent itemclass (if available)
        if (regItem.getRegItemclass().getRegItemclassParent() != null) {
            document.setField(BaseConstants.KEY_SOLR_ITEM_PARENTITEMCLASSLOCALID, regItem.getRegItemclass().getRegItemclassParent().getLocalid());
            /*if (regItem.getRegItemclass().getRegItemclassParent().getBaseuri() != null) {
                document.setField(BaseConstants.KEY_SOLR_ITEM_PARENTITEMCLASSLOCALID, regItem.getRegItemclass().getRegItemclassParent().getBaseuri());
            }*/
            document.setField(BaseConstants.KEY_SOLR_ITEM_PARENTITEMCLASSTYPE, regItem.getRegItemclass().getRegItemclassParent().getRegItemclasstype().getLocalid());
        }

        // Setting the item's status
        Properties properties = Configuration.getInstance().getProperties();
        String statusBaseUri = properties.getProperty(BaseConstants.KEY_PROPERTY_STATUS_BASE_URI);
        document.setField(BaseConstants.KEY_SOLR_ITEM_STATUS, statusBaseUri + "/" + regItem.getRegStatus().getLocalid());

        // Getting & setting the registry
        String registrylocalid = "";
        String registryItemclasslocalid = "";
        String registryBaseuri = "";
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegItem regItemRegistry;
        List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
        if (!regRelationRegistries.isEmpty()) {
            // An item can have only one registry => taking the index 0;
            regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
            registryItemclasslocalid = regItemRegistry.getRegItemclass().getLocalid();
            registryBaseuri = regItemRegistry.getRegItemclass().getBaseuri();
            registrylocalid = regItemRegistry.getLocalid();
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSBASEURI, registryBaseuri);
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSLOCALID, registryItemclasslocalid);
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYLOCALID, registrylocalid);

            // Get all the localization of the registry
            /*List<RegLocalization> regLocalizationsRegister = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegistry);
            for (RegLocalization tmpRegLoc : regLocalizationsRegister) {
                document.setField(BaseConstants.KEY_SOLR_FIELD_NAME_REGISTRY_LABEL + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + tmpRegLoc.getRegLanguagecode().getIso6391code(), tmpRegLoc.getValue());
            }*/
        } else {
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSBASEURI, regItem.getRegItemclass().getBaseuri());
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSLOCALID, regItem.getRegItemclass().getLocalid());
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTRYLOCALID, regItem.getLocalid());
            regItemRegistry = regItem;
        }

        // Getting & setting the register
        String registerlocalid = "";
        String registerBaseuri = "";
        String registerItemclasslocalid = "";
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        RegItem regItemRegister;
        List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
        if (!regRelationRegisters.isEmpty()) {
            // An item can have only one register => taking the index 0;
            regItemRegister = regRelationRegisters.get(0).getRegItemObject();
            registerlocalid = regItemRegister.getLocalid();
            registerItemclasslocalid = regItemRegister.getRegItemclass().getLocalid();
            registerBaseuri = regItemRegister.getRegItemclass().getBaseuri();
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSBASEURI, registerBaseuri);
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSLOCALID, registerItemclasslocalid);
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERLOCALID, registerlocalid);

            // Get all the localization of the register
            /*List<RegLocalization> regLocalizationsRegister = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemRegister);
            for (RegLocalization tmpRegLoc : regLocalizationsRegister) {
                document.setField(BaseConstants.KEY_SOLR_FIELD_NAME_REGISTER_LABEL + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + tmpRegLoc.getRegLanguagecode().getIso6391code(), tmpRegLoc.getValue());
            }*/
        } else if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSBASEURI, regItem.getRegItemclass().getBaseuri());
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSLOCALID, regItem.getRegItemclass().getLocalid());
            document.setField(BaseConstants.KEY_SOLR_ITEM_REGISTERLOCALID, regItem.getLocalid());
            regItemRegister = regItem;
        } else {
            regItemRegister = null;
        }

        // Getting & setting the collection (if available)
        String collectionlocalid = "";
        String collectionItemclasslocalid = "";
        RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
        try {
            List<RegRelation> regRelationCollections = regRelationManager.getAll(regItem, regRelationpredicateCollection);
            // An item can have only one collection => taking the index 0;
            RegItem regItemCollection = regRelationCollections.get(0).getRegItemObject();
            collectionlocalid = regItemCollection.getLocalid();
            collectionItemclasslocalid = regItemCollection.getRegItemclass().getLocalid();
            document.setField(BaseConstants.KEY_SOLR_ITEM_COLLECTIONLOCALID, collectionlocalid);
            document.setField(BaseConstants.KEY_SOLR_ITEM_COLLECTIONITEMCLASSLOCALID, collectionItemclasslocalid);
        } catch (Exception e) {
            // The item doesn't have a collection
        }

        // Setting the item's URI
        String uri = ItemHelper.getURI(regItem, regItemRegistry, regItemRegister, regRelationpredicateCollection, regRelationManager);
        document.setField(BaseConstants.KEY_SOLR_ITEM_URI, uri);

        // Adding the contained itemclass (if available)
        List<RegItemclass> childItemclasses = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
        if (childItemclasses != null) {
            childItemclasses.forEach(tmpregItemclass -> {
                document.addField(BaseConstants.KEY_SOLR_ITEM_CONTAINEDITEMCLASSLOCALID, tmpregItemclass.getLocalid());
            });
        }

        // #--- Creating dynamic fields ---# //
        // Listing the RegFields for the current RegItem
        List<RegFieldmapping> regFieldmappings = regFieldmappingManager.getAll(regItem.getRegItemclass());
        
        // Getting the list of reg fields id to be indexed in solr      
        String fieldToIndexLocalId_string = properties.getProperty(BaseConstants.KEY_PROPERTY_SOLR_FILEDTOINDEX_LOCALID);
        String [] fieldToIndexLocalIds_array = fieldToIndexLocalId_string.split(",");
        HashMap<String, String> fieldToIndexLocalIds = new HashMap<>();
        for (String tmp : fieldToIndexLocalIds_array) {
            fieldToIndexLocalIds.put(tmp, tmp);
        }
        

        // Finding the localization for each RegField of the current RegItem
        for (RegFieldmapping regFiledmapping : regFieldmappings) {
            List<RegLocalization> regLocalizations = regLocalizationManager.getAll(regFiledmapping.getRegField(), regItem);
            
            // Check if the reg field needs to be indexed
            if (fieldToIndexLocalIds.containsKey(regFiledmapping.getRegField().getLocalid())) {

                // Adding to Solr each RegLocalization for the current RegField and RegItem
                for (RegLocalization regLocalization : regLocalizations) {

                    // Adding the simple value of the RegLocalization
                    if (regLocalization.getValue() != null) {
                        document.setField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + regLocalization.getRegLanguagecode().getIso6391code(), regLocalization.getValue());

                        // Checking if the simple value has also a link
                        if (regLocalization.getHref() != null) {
                            document.setField(BaseConstants.KEY_SOLR_ITEM_HREF_PREFIX + BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + regLocalization.getRegLanguagecode().getIso6391code(), regLocalization.getHref());
                        }

                        // Adding the RegRelation localizations
                    } else if (regLocalization.getRegRelationReference() != null) {
                        RegRelation regRelation = regLocalization.getRegRelationReference();
                        RegItem regItemReference = regRelation.getRegItemObject();

                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_LOCALID, regRelation.getRegItemObject().getLocalid());
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_ITEMCLASSLOCALID, regRelation.getRegItemObject().getRegItemclass().getLocalid());

                        // Setting the registry
                        regRelationRegistries = regRelationManager.getAll(regItemReference, regRelationpredicateRegistry);
                        regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTRYLOCALID, regItemRegistry.getLocalid());
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSBASEURI, regItemRegistry.getRegItemclass().getBaseuri());
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTRYITEMCLASSLOCALID, regItemRegistry.getRegItemclass().getLocalid());

                        // Setting the register
                        regRelationRegisters = regRelationManager.getAll(regItemReference, regRelationpredicateRegister);
                        regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTERLOCALID, regItemRegister.getLocalid());
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSBASEURI, regItemRegister.getRegItemclass().getBaseuri());
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_REGISTERITEMCLASSLOCALID, regItemRegister.getRegItemclass().getLocalid());

                        // Setting the URI
                        uri = ItemHelper.getURI(regItemReference, regItemRegistry, regItemRegister, regRelationpredicateCollection, regRelationManager);
                        document.addField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + BaseConstants.KEY_SOLR_ITEM_URI, uri);

                        // Setting the localization
                        List<RegLocalization> regLocalizationTmps = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regRelation.getRegItemObject());
                        for (RegLocalization regLocalizationTmp : regLocalizationTmps) {
                            document.setField(BaseConstants.KEY_SOLR_FIELD_NAME_FIELD_PREFIX + regLocalization.getRegField().getLocalid() + BaseConstants.KEY_SOLR_FIELD_NAME_SEPARATOR + regLocalizationTmp.getRegLanguagecode().getIso6391code(), regLocalizationTmp.getValue());
                        }
                    }

                }
            }
        }
        return document;
    }

    public static boolean checkSolrCompleteIndexinglRunning() {
        boolean running = false;
        try {
            String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);

            File f = new File(propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SOLR_COMPLETE_INDEXING_RUNNING);
            if (f.exists() && !f.isDirectory()) {
                running = true;
            }

        } catch (Exception e) {
        }
        return running;
    }

    private static void createSolrCompleteIndexinglRunningFile() throws Exception {
        String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);
        String systemInstalledPath = propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SOLR_COMPLETE_INDEXING_RUNNING;
        File systemInstalledFile = new File(systemInstalledPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {

            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                //throw new IOException();
            }

        } catch (IOException ex) {
        }
    }

    private static void deleteSolrCompleteIndexinglRunningFile() {
        String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);
        String systemInstalledPath = propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SOLR_COMPLETE_INDEXING_RUNNING;

        try {
            File file = new File(systemInstalledPath);

            boolean success = file.delete();

            if (!success) {
                //throw new IOException();
            }
        } catch (Exception ex) {
        }
    }
}
