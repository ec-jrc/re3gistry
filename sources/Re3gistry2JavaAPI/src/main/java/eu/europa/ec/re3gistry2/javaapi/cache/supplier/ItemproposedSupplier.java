/*
 * /*
 *  * Copyright 2007,2016 EUROPEAN UNION
 *  * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *  * Date: 2020/05/11
 *  * Authors:
 *  * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *  * National Land Survey of Finland, SDI Services - inspire@nls.fi
 *  *
 *  * This work was supported by the Interoperability solutions for public
 *  * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 *  * through Action 2016.10: European Location Interoperability Solutions
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.javaapi.cache.supplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.ItemHelper;
import eu.europa.ec.re3gistry2.base.utility.ItemproposedHelper;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedPropertyValue;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.javaapi.cache.util.StatusLocalization;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class ItemproposedSupplier {

    private static final Logger LOG = LogManager.getLogger(ItemSupplier.class.getName());

    private static final String TYPE_REGISTRY = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY;
    private static final String TYPE_REGISTER = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER;
    private static final String TYPE_ITEM = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;

    private final RegItemclassManager regItemClassManager;
    private final RegItemManager regItemManager;
    private final RegItemhistoryManager regItemHistoryManager;
    private final RegRelationManager regRelationManager;
    private final RegFieldManager regFieldManager;
    private final RegLocalizationManager reglocalizationManager;
    private final RegFieldmappingManager regFieldmappingManager;
    private final RegStatusManager regStatusManager;
    private final RegStatuslocalizationManager regStatusLocalizationManager;
    private final RegItemproposedManager regItemproposedManager;
    private final RegRelationproposedManager regRelationproposedManager;
    private RegLocalizationproposedManager regLocalizationproposedManager;
    private final RegGroupManager regGroupManager;
    private final RegLanguagecode masterLanguage;
    private final RegLanguagecode languageCode;

    private final RegRelationpredicate hasRegistry;
    private final RegRelationpredicate hasRegister;
    private final RegRelationpredicate hasParent;
    private final RegRelationpredicate hasCollection;
    private final RegRelationpredicate hasSuccessor;
    private final RegRelationpredicate hasPredecessor;
    private final RegRelationpredicate hasReference;

    // Lazily populated
    private RegField labelField;
    private Map<String, String> fieldToLabel;
    private Map<String, StatusLocalization> statusToLocalization;
    private Map<String, List<RegFieldmapping>> itemclassToFieldmapping;
    private boolean topConceptItem = false;

    public ItemproposedSupplier(EntityManager em,
            RegLanguagecode masterLanguage,
            RegLanguagecode languageCode) throws Exception {
        this.regItemClassManager = new RegItemclassManager(em);
        this.regItemManager = new RegItemManager(em);
        this.regItemHistoryManager = new RegItemhistoryManager(em);
        RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(em);
        this.regRelationManager = new RegRelationManager(em);
        this.regFieldManager = new RegFieldManager(em);
        this.reglocalizationManager = new RegLocalizationManager(em);
        this.regFieldmappingManager = new RegFieldmappingManager(em);
        this.regStatusManager = new RegStatusManager(em);
        this.regStatusLocalizationManager = new RegStatuslocalizationManager(em);
        this.regItemproposedManager = new RegItemproposedManager(em);
        this.regRelationproposedManager = new RegRelationproposedManager(em);
        this.regLocalizationproposedManager = new RegLocalizationproposedManager(em);
        this.regGroupManager = new RegGroupManager(em);
        this.masterLanguage = masterLanguage;
        this.languageCode = languageCode;

        this.hasRegistry = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        this.hasRegister = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        this.hasParent = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);
        this.hasCollection = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
        this.hasSuccessor = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR);
        this.hasPredecessor = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR);
        this.hasReference = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);

    }

    public Item getItemProposedByUuid(String uuid) throws Exception {
        // Not possible to request specific version with uuid
        // String uuid, String localid, Date insertdate
        RegItemproposed itemProposed = regItemproposedManager.get(uuid);
        //RegItem finalItem = new RegItem(itemProposed.getUuid(), itemProposed.getLocalid(), itemProposed.getInsertdate());
        return toItemProposed(itemProposed);
    }
    
    public Item getItemProposedByUri(String uri) throws Exception {
        RegItemproposed item = getRegItemProposedByUri(uri);
        if (item == null) {
            return null;
        }
        return toItemProposed(item);
    }

    public Item getItemProposedByUriAndStatus(String uri, String itemStatus) throws Exception {
        RegItemproposed item = getRegItemProposedByUriAndStatus(uri, itemStatus);
        if (item == null) {
            return null;
        }
        return toItemProposed(item);
    }

    private RegItemproposed getRegItemProposedByUri(String uri) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);
        try {
            int uriCollection = uri.substring(0, i).lastIndexOf('/');
            String regItemClassLocalId = uri.substring(uriCollection + 1).replace("/" + localid, "");
            RegItemclass parentClass = regItemClassManager.getByLocalid(regItemClassLocalId);
            RegItemclass regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass).get(0);
            RegItemproposed regItemproposed = regItemproposedManager.getByLocalidAndRegItemClass(localid, regItemRegItemClass);            

            return regItemproposed;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private RegItemproposed getRegItemProposedByUriAndStatus(String uri, String itemStatus) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);
        try {
            int uriCollection = uri.substring(0, i).lastIndexOf('/');
            String regItemClassLocalId = uri.substring(uriCollection + 1).replace("/" + localid, "");
            RegItemclass parentClass = regItemClassManager.getByLocalid(regItemClassLocalId);
            RegItemclass regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass).get(0);

            RegStatus status = regStatusManager.findByLocalid(itemStatus);
            RegItemproposed regItemproposed;
            if(status.getIspublic()){
                regItemproposed = regItemproposedManager.getByLocalidAndRegItemClassAndRegStatus(localid, regItemRegItemClass, status);
            }else{
                return null;
            }

            return regItemproposed;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    private Item toItemProposed(RegItemproposed regItemproposed) throws Exception {
        boolean isPublic = regItemproposed.getRegStatus().getIspublic();
        
        if (!isPublic) {
            return null;
        }
        Item item = new Item();

        setMainPropertiesForRegItemProposed(regItemproposed, item);
        setRegistryAndRegisterItemRefProposed(regItemproposed, item);      
//        setIsDefinedByFromRegItemProposed(regItemproposed, item);     
//        setContainedItemsFromRegItemProposed(regIP, item);  
//        setContainedItemsFromRegItemProposedClassWithParent(regIP, item); 
//        setTopConceptsFromRegItemProposed(regItemproposed, item);    
//        setInSchemeAndTopConceptOfFromRegItemProposed(regIP, item); 
//        setNarrowerFromRegItemProposed(regIP, item);  
//        setBroaderFromRegItemProposed(regIP, item); 
        return item;
    }

    protected ContainedItem toContainedItemProposed(RegItemproposed regItemproposed) throws Exception {
        ContainedItem containedItem = new ContainedItem();
  
        boolean isPublic = regItemproposed.getRegStatus().getIspublic();
        
        if (!isPublic) {
            return null;
        }
        
        setMainPropertiesForRegItemProposed(regItemproposed, containedItem);
        setRegistryAndRegisterItemRefProposed(regItemproposed, containedItem);
        setIsDefinedByFromRegItemProposed(regItemproposed, containedItem);

        setTopConceptsFromRegItemProposed(regItemproposed, containedItem);
        setInSchemeAndTopConceptOfFromRegItemProposed(regItemproposed, containedItem);
        if (topConceptItem) {
            setNarrowerFromRegItemProposed(regItemproposed, containedItem);
            setBroaderFromRegItemProposed(regItemproposed, containedItem);
            setContainedItemsFromRegItemProposed(regItemproposed, containedItem);
        }

        return containedItem;
    }

    protected BasicContainedItem toBasicContainedItemProposed(RegItemproposed regItemproposed) throws Exception {
        BasicContainedItem citem = new ContainedItem();
        RegItem regItem = regItemproposed.getRegItemReference();

        citem.setUri(ItemproposedHelper.getProposedURI(regItemproposed, regItem, hasCollection, regRelationproposedManager, regRelationManager));
        return citem;
    }

    protected ContainedItem toBasicContainedItemProposedDefinedBy(RegItemproposed regItemproposed) throws Exception {
        ContainedItem containedItem = new ContainedItem();
        setMainPropertiesForRegItemProposed(regItemproposed, containedItem);
        return containedItem;
    }

    protected ContainedItem toContainedItemWithoutItemsProposed(RegItemproposed regItemproposed) throws Exception {
        ContainedItem containedItem = new ContainedItem();

        setMainPropertiesForRegItemProposed(regItemproposed, containedItem);
        setRegistryAndRegisterItemRefProposed(regItemproposed, containedItem);
//        setIsDefinedByFromRegItemProposed(regItemproposed, containedItem);
//        setTopConceptsFromRegItemProposed(regItemproposed, containedItem);
//        setInSchemeAndTopConceptOfFromRegItemProposed(regItemproposed, containedItem);

        return containedItem;
    }

    private void setRegistryAndRegisterItemRefProposed(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
        ItemRef registryRef = null;
        ItemRef registerRef = null;
        
        switch (item.getType()) {
            case TYPE_REGISTRY:
                registerRef = null;

                break;
            case TYPE_REGISTER:
                registryRef = toItemRef(getRelatedItemProposedBySubject2(regItemproposed, hasRegistry));

                // Making an assumption that register can not be parent or collection to any item
                break;
            case TYPE_ITEM:
//                registryRef = toItemRefProposed(getRelatedItemProposedBySubject(regItemproposed, hasRegistry));
//                registerRef = toItemRefProposed(getRelatedItemProposedBySubject(regItemproposed, hasRegister));
                
                registryRef = toItemRef(getRelatedItemProposedBySubject2(regItemproposed, hasRegistry));
                registerRef = toItemRef(getRelatedItemProposedBySubject2(regItemproposed, hasRegister));

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (registryRef != null) {
            item.setRegistry(registryRef);
        }
        if (registerRef != null) {
            item.setRegister(registerRef);
        }
    }

    private void setContainedItemsFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
        List<ContainedItem> containedItems = new ArrayList<>();
        List<RegItemproposed> containedItemsList = null;
        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                containedItemsList = getRegistersItemProposed(regItemproposed);

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItemproposed containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItemsProposed(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItems(containedItems);
                    }
                }
                break;
            case TYPE_REGISTER:
                containedItemsList = getDirectlyContainedItemsProposedOfRegister(regItemproposed);

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItemproposed containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItemsProposed(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItems(containedItems);
                    }
                }
                break;
            case TYPE_ITEM:
                List<BasicContainedItem> topConcepts = new ArrayList<>();
                try {
                    containedItemsList = new ArrayList<>();
//                    if (complexItem) {
//                        containedItemsList = getRelatedItemsByObject(regItem, hasCollection);
//                    } else {
                    List<String> collectionNoParentList = getAllColectionsNoParentOfItemProposed(regItemproposed);
                    for (String uuid : collectionNoParentList) {
                        containedItemsList.add(regItemproposedManager.get(uuid));
                    }
                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        containedItemsList = getRelatedItemsProposedByObject(regItemproposed, hasCollection);
                    }
                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        containedItemsList = getRelatedItemsProposedByObject(regItemproposed, hasParent);
                    }
//                    }
                    for (RegItemproposed childItem : containedItemsList) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(toBasicContainedItemProposed(childItem));
                        }
                    }
                } catch (Exception exception) {
                    for (RegItemproposed childItem : getRelatedItemsProposedByObject(regItemproposed, hasParent)) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(toBasicContainedItemProposed(childItem));
                        }
                    }
                }
                if (!topConcepts.isEmpty()) {
                    item.setTopConcepts(topConcepts);
                }

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItemproposed containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemProposed(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItems(containedItems);
                    }
                }
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

    }

    private void setContainedItemsFromRegItemProposedClassWithParent(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
        List<ContainedItem> containedItems = new ArrayList<>();
        List<RegItemproposed> containedItemsList = new ArrayList<>();
        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                //theme register, codelist register
                List<RegItemclass> childItemClassList = regItemClassManager.getChildItemclass(regItemproposed.getRegItemclass());

                List<RegItemclass> itemclassWithChildren = new ArrayList<>();
                for (RegItemclass regItemclass : childItemClassList) {
                    if (!regItemclass.getSystemitem()) {
                        //theme am, codelist
                        List<RegItemclass> childofChildItemClassList = regItemClassManager.getChildItemclass(regItemclass);
                        List<RegItemclass> grandchildItemClassList = new ArrayList<>();
                        for (RegItemclass childofChildItemClass : childofChildItemClassList) {
                            //codelistvalues
                            grandchildItemClassList = regItemClassManager.getChildItemclass(childofChildItemClass);
                            if (!grandchildItemClassList.isEmpty()) {
                                break;
                            }
                        }
                        if (!grandchildItemClassList.isEmpty()) {
                            itemclassWithChildren.addAll(childofChildItemClassList);
                        } else {
                            itemclassWithChildren.add(regItemclass);
                        }
                    }
                }

                for (RegItemclass regItemclass : itemclassWithChildren) {
                    containedItemsList.addAll(regItemproposedManager.getAll(regItemclass));
                }

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItemproposed containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItemsProposed(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItemsBeeingParentItemClass(containedItems);
                    }
                }
                break;
            case TYPE_REGISTER:
                break;
            case TYPE_ITEM:
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

    }

    private void setInSchemeAndTopConceptOfFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem containedItem) throws Exception {

        BasicContainedItem basicContainedItem = null;
        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:
                RegItemproposed relatedRegistry = getRelatedItemProposedBySubject(regItemproposed, hasRegistry);
                basicContainedItem = toBasicContainedItemProposed(relatedRegistry);
                break;
            case TYPE_ITEM:
                RegItemproposed relatedCollection = getRelatedItemProposedBySubject(regItemproposed, hasCollection);
                if (relatedCollection != null) {
                    RegItemproposed parentCollection = getRelatedItemProposedBySubject(relatedCollection, hasParent);
                    if (parentCollection != null) {
                        relatedCollection = parentCollection;
                    }
                }
                if (relatedCollection == null) {
                    relatedCollection = getRelatedItemProposedBySubject(regItemproposed, hasRegister);
                }
                basicContainedItem = toBasicContainedItemProposed(relatedCollection);
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (basicContainedItem != null && basicContainedItem.getUri() != null) {
            containedItem.setInScheme(basicContainedItem);
            containedItem.setTopConceptOf(basicContainedItem);
        }
    }

    private void setBroaderFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> broader = new ArrayList<>();
        List<RegItemproposed> broaderList = null;

        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                broaderList = getRelatedItemsProposedBySubject(regItemproposed, hasParent);
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = getRelatedItemsProposedBySubject(regItemproposed, hasCollection);
                }
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = getRelatedItemsProposedBySubject(regItemproposed, hasRegister);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        if (broaderList != null && !broaderList.isEmpty()) {
            for (RegItemproposed childItem : broaderList) {
                if (!childItem.getRegItemclass().getSystemitem()) {
                    broader.add(toBasicContainedItemProposed(childItem));
                }
            }
            if (!broader.isEmpty()) {
                containedItem.setBroader(broader);
            }
        }
    }

    private void setNarrowerFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> narrower = new ArrayList<>();
        List<RegItemproposed> narrowerList = null;
        if (regItemproposed != null && containedItem != null) {
            switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
                case TYPE_REGISTRY:

                    break;
                case TYPE_REGISTER:
//                narrowerList = getDirectlyContainedItemsOfRegister(regItem);
                    break;
                case TYPE_ITEM:
                    narrowerList = getRelatedItemsProposedByObject(regItemproposed, hasParent);
                    if (narrowerList == null || narrowerList.isEmpty()) {
                        narrowerList = getRelatedItemsProposedByObject(regItemproposed, hasCollection);
                    }
                    break;
                default:
                    throw new RuntimeException("Unexpected type");
            }

            if (narrowerList != null && !narrowerList.isEmpty()) {
                for (RegItemproposed childItem : narrowerList) {
                    if (!childItem.getRegItemclass().getSystemitem()) {
                        try {
                            narrower.add(toContainedItemWithoutItemsProposed(childItem));
                        } catch (Exception ex) {
                            LOG.error("childItem:" + childItem.getUuid() + ", Error:" + ex.getMessage());
                        }
                    }
                }
                if (!narrower.isEmpty()) {
                    containedItem.setNarrower(narrower);
                }
            }
        }
    }

    private void setTopConceptsFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> topConcepts = new ArrayList<>();
        List<RegItemproposed> childItemList = new ArrayList<>();

        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                childItemList = getRegistersItemProposed(regItemproposed);

                break;
            case TYPE_REGISTER:
                childItemList = getDirectlyContainedItemsProposedOfRegister(regItemproposed);

                break;
            case TYPE_ITEM:
                List<String> collectionNoParentList = getAllColectionsNoParentOfItemProposed(regItemproposed);
                for (String uuid : collectionNoParentList) {
                    childItemList.add(regItemproposedManager.get(uuid));
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = getRelatedItemsProposedByObject(regItemproposed, hasCollection);
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = getRelatedItemsProposedByObject(regItemproposed, hasParent);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        for (RegItemproposed childItem : childItemList) {
            if (!childItem.getRegItemclass().getSystemitem()) {
                topConcepts.add(toBasicContainedItemProposed(childItem));
            }
        }

        if (!topConcepts.isEmpty()) {
            containedItem.setTopConcepts(topConcepts);
        }
    }

    private void setIsDefinedByFromRegItemProposed(RegItemproposed regItemproposed, ContainedItem containedItem) throws Exception {
        List<ContainedItem> isDefinedBy = new ArrayList<>();

        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                for (RegItemproposed relatedItem : getRelatedItemsProposedBySubject(regItemproposed, hasReference)) {
                    if (!relatedItem.getRegItemclass().getSystemitem()) {
                        isDefinedBy.add(toBasicContainedItemProposedDefinedBy(relatedItem));
                    }
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (!isDefinedBy.isEmpty()) {
            containedItem.setIsDefinedBy(isDefinedBy);
        }
    }

    protected ItemRef toItemRefProposed(RegItemproposed regItemproposed) throws Exception {

        RegItem regItem = null;
        
        if(regItemproposed.getRegItemReference() != null){
            regItem = regItemproposed.getRegItemReference(); 
        }else{ 
            regItem = regItemproposed.getRegAction().getRegItemRegister();
        }
        
        String uri = ItemproposedHelper.getProposedURI(regItemproposed, regItem, hasCollection, regRelationproposedManager, regRelationManager);

        RegField labelField = getLabelField();
        RegFieldmapping labelFieldmapping = regFieldmappingManager.getByFieldAndItemClass(labelField, regItem.getRegItemclass());

        String lang = languageCode.getIso6391code();
        String id = labelField.getLocalid();
        boolean istitle = labelField.getIstitle();
        int order = labelFieldmapping.getListorder();
        boolean tablevisible = labelFieldmapping.getTablevisible();

        List<RegLocalizationproposed> localizations = regLocalizationproposedManager.getAll(labelField, regItemproposed, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            lang = masterLanguage.getIso6391code();
            localizations = regLocalizationproposedManager.getAll(labelField, regItemproposed, masterLanguage);
        }
        List<LocalizedPropertyValue> values = localizations.stream()
                .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                .collect(Collectors.toList());

        LocalizedProperty property = new LocalizedProperty(lang, id, istitle, id, values, order, tablevisible);
        return new ItemRef(uri, Arrays.asList(property));
    }

    private List<LocalizedProperty> getLocalizedPropertiesItemProposed(RegItemproposed regItemproposed, Predicate<RegFieldmapping> fieldmappingFilter) throws Exception {
        List<RegLocalizationproposed> localizations = regLocalizationproposedManager.getAll(regItemproposed, languageCode);
        Map<String, List<RegLocalizationproposed>> localizationsByField = localizations.stream()
                .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));

        Map<String, List<RegLocalizationproposed>> localizationsByFieldML;
        if (languageCode.equals(masterLanguage)) {
            localizationsByFieldML = null;
        } else {
            localizationsByFieldML = regLocalizationproposedManager.getAll(regItemproposed, masterLanguage).stream()
                    .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));
        }

        List<RegFieldmapping> fieldmappings = getFieldmappings(regItemproposed.getRegItemclass());
        return fieldmappings.stream()
                .filter(fieldmappingFilter)
                .map(it -> {
                    try {
                        return getLocalizedPropertyItemProposed(it, regItemproposed, localizationsByField, localizationsByFieldML);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LocalizedProperty getLocalizedPropertyItemProposed(RegFieldmapping fieldmapping,
            RegItemproposed regItemproposed,
            Map<String, List<RegLocalizationproposed>> localizationsByField,
            Map<String, List<RegLocalizationproposed>> localizationsByFieldML) throws Exception {
        RegField field = fieldmapping.getRegField();
        String id = field.getLocalid();
        String lang = languageCode.getIso6391code();
        boolean istitle = field.getIstitle();
        String label = getLabelForField(field);
        int order = fieldmapping.getListorder();
        boolean tablevisible = fieldmapping.getTablevisible();

        List<LocalizedPropertyValue> values = Collections.emptyList();

        //Get Property for allowing to return null value fields
        String allowEmptyFields = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_ALLOW_NULL_FIELDS, BaseConstants.KEY_BOOLEAN_STRING_FALSE);
        String dateformat = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

        switch (field.getRegFieldtype().getUuid()) {
            case BaseConstants.KEY_FIELDTYPE_REGISTRY_UUID:
                return null;
            case BaseConstants.KEY_FIELDTYPE_REGISTER_UUID:
                return null;
            case BaseConstants.KEY_FIELDTYPE_COLLECTION_UUID:
                RegItemproposed collection = getRelatedItemProposedBySubject(regItemproposed, hasCollection);

                LocalizedProperty linksToRelatedItemsCollection = getLinksToRelationItemsproposed(field, label, order, tablevisible, collection);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsCollection == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsCollection == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsCollection;
                }
            case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
                RegItemproposed parent = getRelatedItemProposedBySubject(regItemproposed, hasParent);
                LocalizedProperty linksToRelatedItemsParent = getLinksToRelationItemsproposed(field, label, order, tablevisible, parent);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsParent == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsParent == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsParent;
                }
            case BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID:
                List<RegItemproposed> successorList = getRelatedItemsProposedBySubject(regItemproposed, hasSuccessor);
                List<LocalizedPropertyValue> successorValues = new ArrayList<>();

                for (RegItemproposed successor : successorList) {
                    LocalizedProperty linksToRelatedItemsSuccessor = getLinksToRelationItemsproposed(field, label, order, tablevisible, successor);
                    if (linksToRelatedItemsSuccessor.getValues() != null && !linksToRelatedItemsSuccessor.getValues().isEmpty()) {
                        successorValues.addAll(linksToRelatedItemsSuccessor.getValues());
                    }
                }

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (successorValues.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (successorValues.isEmpty() && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return new LocalizedProperty(lang, id, istitle, label, successorValues, order, tablevisible);
                }
            case BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID:
                List<RegItemproposed> predecessorList = getRelatedItemsProposedBySubject(regItemproposed, hasPredecessor);
                List<LocalizedPropertyValue> predecessorValues = new ArrayList<>();

                for (RegItemproposed predecessor : predecessorList) {
                    LocalizedProperty linksToRelatedItemsPredecessor = getLinksToRelationItemsproposed(field, label, order, tablevisible, predecessor);
                    if (linksToRelatedItemsPredecessor.getValues() != null && !linksToRelatedItemsPredecessor.getValues().isEmpty()) {
                        predecessorValues.addAll(linksToRelatedItemsPredecessor.getValues());
                    }
                }

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (predecessorValues.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (predecessorValues.isEmpty() && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return new LocalizedProperty(lang, id, istitle, label, predecessorValues, order, tablevisible);
                }
            case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                LocalizedProperty linksToRelatedItems = getLinksToRelatedItemsProposed(field, label, order, tablevisible, regItemproposed, localizationsByField, localizationsByFieldML);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItems == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItems == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItems;
                }

            case BaseConstants.KEY_FIELDTYPE_STATUS_UUID:
                StatusLocalization statusLocalization = getLocalizedStatus(regItemproposed.getRegStatus());
                if (statusLocalization == null) {
                    return null;
                }
                lang = statusLocalization.getLang();
                String value = statusLocalization.getValue();
                String href = statusLocalization.getHref();
                values = Collections.singletonList(new LocalizedPropertyValue(value, href));
                break;

            case BaseConstants.KEY_FIELDTYPE_DATECREATION_UUID:

                String dateInsertValue = sdf.format(regItemproposed.getInsertdate());
                values = Collections.singletonList(new LocalizedPropertyValue(dateInsertValue, null));
                break;

            case BaseConstants.KEY_FIELDTYPE_DATEEDIT_UUID:

                if (regItemproposed.getEditdate() != null) {
                    String dateEditValue = sdf.format(regItemproposed.getEditdate());
                    values = Collections.singletonList(new LocalizedPropertyValue(dateEditValue, null));
                }
                break;

            default:
                String key = field.getUuid();
                RegGroup rolLabel = null;
                List<RegLocalizationproposed> localizations = localizationsByField.get(key);
                if (localizations == null || localizations.isEmpty()) {
                    if (localizationsByFieldML != null) {
                        lang = masterLanguage.getIso6391code();
                        localizations = localizationsByFieldML.get(key);
                        if (localizations != null) {
                            values = localizations.stream()
                                    .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                                    .collect(Collectors.toList());
                            break;
                        }
                    }
                    // fallback to master language localization for this field for this reg item
                    RegLocalizationproposed localization = this.regLocalizationproposedManager.get(field, masterLanguage);
                    if (localization != null) {
                        localizations = new ArrayList<>();
                        localizations.add(localization);
                    }

                    if (localization == null) {
                        break;
                    }
                        
                    // fix search in role
                    // rols are not in reglocalization
                    rolLabel = this.regGroupManager.getByLocalid(label);
                }
                
                values = localizations.stream()
                        .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                        .collect(Collectors.toList());
                                if (rolLabel != null) {
                }
                if (rolLabel != null) {
                    values.clear();
                    values.add(new LocalizedPropertyValue(rolLabel.getName(), null));
                }
                break;
        }

        if (values.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }
        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private String getLabelForItemProposed(RegItemproposed regItemproposed) throws Exception {
        List<RegLocalizationproposed> localizations = regLocalizationproposedManager.getAll(getLabelField(), regItemproposed, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            localizations = regLocalizationproposedManager.getAll(getLabelField(), regItemproposed, masterLanguage);
        }
        return localizations.stream()
                .findAny()
                .map(l -> l.getValue())
                .orElse(null);
    }

    private List<RegItemproposed> getRegistersItemProposed(RegItemproposed registry) throws Exception {
        List<RegItemproposed> registrysRegisters = new ArrayList<>();
        List<RegItemproposed> registers = getAllRegistersItemProposed();
        List<RegRelationproposed> relations = regRelationproposedManager.getAllByRegItemsSubjectAndPredicate(registers, hasRegistry);
        relations.stream().filter((rel) -> (rel.getRegItemObject().equals(registry))).forEachOrdered((rel) -> {
            registrysRegisters.add(rel.getRegItemproposedObject());
        });
        return registrysRegisters;
    }

    protected List<RegItemproposed> getAllRegistersItemProposed() throws Exception {
        return getItemsProposedOfType(TYPE_REGISTER);
    }

    private List<RegItemproposed> getItemsProposedOfType(String classtype) throws Exception {
        List<RegItemclass> allItemClasses = regItemClassManager.getAll();
        List<RegItemclass> itemclasses = allItemClasses.stream()
                .filter(it -> it.getRegItemclasstype().getLocalid().equals(classtype))
                .collect(Collectors.toList());
        List<RegItemproposed> list = new ArrayList<>();
        for (RegItemclass itemclass : itemclasses) {
            list.addAll(regItemproposedManager.getAllItemProposed(itemclass));
        }
        return list;
    }

    private List<RegItemproposed> getDirectlyContainedItemsProposedOfRegister(RegItemproposed register) throws Exception {
        RegItem register2 = null;
        return regItemproposedManager.getAllSubjectsByRegItemProposedObjectAndPredicateAndSubjectNotPredicate(register2, hasRegister, hasCollection);
    }

    private List<String> getAllColectionsNoParentOfItemProposed(RegItemproposed item) throws Exception {

        return regItemproposedManager.getAllItemByRegItemProposedObjectAndPredicateAndSubjectNotPredicate(item, regStatusManager.get("1"), hasCollection, hasParent);
    }

    protected RegItemproposed getRelatedItemProposedBySubject(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {
        List<RegItemproposed> list = getRelatedItemsProposedBySubject(regItemproposed, predicate);
        if (list != null && !list.isEmpty()) {
            return list.stream().findAny().orElse(null);
            }     
            return null;
        }

    private List<RegItemproposed> getRelatedItemsProposedBySubject(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {

        if (regRelationproposedManager != null && regItemproposed != null && predicate != null
                && regRelationproposedManager.getAll(regItemproposed, predicate) != null) {
            return regRelationproposedManager.getAll(regItemproposed, predicate).stream()
                    .map(rel -> rel.getRegItemproposedSubject())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private RegField getLabelField() throws Exception {
        if (labelField == null) {
            labelField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
        }
        return labelField;
    }

    private String getLabelForField(RegField field) throws Exception {
        if (fieldToLabel == null) {
            fieldToLabel = new HashMap<>();
        }
        String key = field.getUuid();
        String label = fieldToLabel.get(key);
        if (label != null) {
            return label;
        }
        if (label == null) {
            try {
                label = reglocalizationManager.get(field, languageCode).getValue();
            } catch (NoResultException ignore) {
                label = reglocalizationManager.get(field, masterLanguage).getValue();
            }
            fieldToLabel.put(key, label);
        }
        return label;
    }

    private List<RegFieldmapping> getFieldmappings(RegItemclass itemclass) throws Exception {
        if (itemclassToFieldmapping == null) {
            itemclassToFieldmapping = new HashMap<>();
        }
        String key = itemclass.getUuid();
        List<RegFieldmapping> fieldMappings = itemclassToFieldmapping.get(key);
        if (fieldMappings == null) {
            fieldMappings = regFieldmappingManager.getAll(itemclass);
            itemclassToFieldmapping.put(key, fieldMappings);
        }
        return fieldMappings;
    }

    private StatusLocalization getLocalizedStatus(RegStatus regStatus) throws Exception {
        if (statusToLocalization == null) {
            statusToLocalization = new HashMap<>();
        }
        String key = regStatus.getUuid();
        StatusLocalization statusLocalization = statusToLocalization.get(key);
        if (statusLocalization == null) {
            RegStatusgroup regStatusgroup = regStatus.getRegStatusgroup();
            RegStatuslocalization statusLoc;
            String lang;
            try {
                statusLoc = regStatusLocalizationManager.get(regStatus, languageCode);
                lang = languageCode.getIso6391code();
            } catch (NoResultException ignore) {
                //  Try masterLanguage
                try {
                    statusLoc = regStatusLocalizationManager.get(regStatus, masterLanguage);
                    lang = masterLanguage.getIso6391code();
                } catch (NoResultException ignore2) {
                    return null;
                }
            }
            String value = statusLoc.getLabel();
            String href = regStatusgroup.getBaseuri() + "/" + regStatusgroup.getLocalid() + "/" + regStatus.getLocalid();
            statusLocalization = new StatusLocalization(lang, value, href);
            statusToLocalization.put(key, statusLocalization);
        }
        return statusLocalization;
    }

    private ContainedItem setMainPropertiesForRegItemProposed(RegItemproposed regItemproposed, ContainedItem item) throws Exception {

        item.setUuid(regItemproposed.getUuid());
        RegItem regitem = regItemproposed.getRegItemclass().getRegItemclassParent().getRegItemList().get(0);
        String uri = ItemproposedHelper.getProposedURI(regItemproposed, regitem, hasCollection, regRelationproposedManager, regRelationManager);
        item.setUri(uri);
        item.setLocalid(regItemproposed.getLocalid());
        item.setLatest(true);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        item.setInsertdate(df.format(regItemproposed.getInsertdate()));
        if (regItemproposed.getEditdate() != null) {
            item.setEditdate(df.format(regItemproposed.getEditdate()));
        }

        setVersionAndHistoryItemproposed(regItemproposed, item);

        item.setType(regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid());
        item.setLanguage(languageCode.getIso6391code());
        RegItemclass itemclassParent = regItemproposed.getRegItemclass().getRegItemclassParent();
        if (itemclassParent != null) {
            item.setItemclass(new BasicItemClass(regItemproposed.getRegItemclass().getLocalid(), itemclassParent.getLocalid(), itemclassParent.getRegItemclasstype().getLocalid()));
        } else {
            item.setItemclass(new BasicItemClass(regItemproposed.getRegItemclass().getLocalid(), null, null));
        }
        item.setProperties(getLocalizedPropertiesItemproposed(regItemproposed, fieldMapping -> !fieldMapping.getHidden())); // ERROR QUERY BD

        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                topConceptItem = false;
                break;
            case TYPE_REGISTER:
                topConceptItem = false;
                break;
            case TYPE_ITEM:
                topConceptItem = true;
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (topConceptItem) {
            List<RegItemproposed> hasCollectionList = getRelatedItemsProposedBySubject(regItemproposed, hasCollection);
            if (hasCollectionList != null && !hasCollectionList.isEmpty()) {
                item.setHasCollection(true);
            }
            List<RegItemproposed> isParentList = getRelatedItemsProposedBySubject(regItemproposed, hasParent);
            if (isParentList != null && !isParentList.isEmpty()) {
                item.setIsParent(true);
            }
            if(regItemproposed.getExternal() != null){
                if (regItemproposed.getExternal()) {
                item.setExternal(true);
                }
            }
            
        }

        return item;
    }

//    private ContainedItem setMainPropertiesForRegItemProposed(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
//
////        RegItem regItem = regItemproposed.getRegItemReference();
//        item.setUuid(regItemproposed.getUuid());
//        //item.setUri(ItemproposedHelper.getProposedURI(regItemproposed, regItem, hasCollection, regRelationproposedManager, regRelationManager));
//        item.setLocalid(regItemproposed.getLocalid());
//        item.setLatest(true);
//
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
//        item.setInsertdate(df.format(regItemproposed.getInsertdate()));
//        if (regItemproposed.getEditdate() != null) {
//            item.setEditdate(df.format(regItemproposed.getEditdate()));
//        }
//
//        //setVersionAndHistoryItemProposed(regItemproposed, item); //-------------------------------- ERROR NO HAY URI
//        item.setType(regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid());
//        item.setLanguage(languageCode.getIso6391code());
//        item.setItemclass(new ItemClass(regItemproposed.getRegItemclass().getLocalid()));
//        //item.setProperties(getLocalizedPropertiesItemProposed(regItemproposed, fieldMapping -> !fieldMapping.getHidden())); // ERROR QUERY BD
//
//        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
//            case TYPE_REGISTRY:
//                topConceptItem = false;
//                break;
//            case TYPE_REGISTER:
//                topConceptItem = false;
//                break;
//            case TYPE_ITEM:
//                topConceptItem = true;
//                break;
//            default:
//                throw new RuntimeException("Unexpected type");
//        }
//        if (topConceptItem) {
//            List<RegItemproposed> hasCollectionList = getRelatedItemsProposedBySubject(regItemproposed, hasCollection);
//            if (hasCollectionList != null && !hasCollectionList.isEmpty()) {
//                item.setHasCollection(true);
//            }
//            List<RegItemproposed> isParentList = getRelatedItemsProposedBySubject(regItemproposed, hasParent);
//            if (isParentList != null && !isParentList.isEmpty()) {
//                item.setIsParent(true);
//            }
//            if (regItemproposed.getExternal()) {
//                item.setExternal(true);
//            }
//        }
//
//        // DELETE
//        item.setUri("");
//
//        return item;
//    }
    private List<LocalizedProperty> getLocalizedPropertiesItemproposed(RegItemproposed regItem, Predicate<RegFieldmapping> fieldmappingFilter) throws Exception {
        List<RegLocalizationproposed> localizations = regLocalizationproposedManager.getAll(regItem, languageCode);
        Map<String, List<RegLocalizationproposed>> localizationsByField = localizations.stream()
                .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));

        Map<String, List<RegLocalizationproposed>> localizationsByFieldML;
        if (languageCode.equals(masterLanguage)) {
            localizationsByFieldML = null;
        } else {
            localizationsByFieldML = regLocalizationproposedManager.getAll(regItem, masterLanguage).stream()
                    .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));
        }

        List<RegFieldmapping> fieldmappings = getFieldmappings(regItem.getRegItemclass());
        return fieldmappings.stream()
                .filter(fieldmappingFilter)
                .map(it -> {
                    try {
                        return getLocalizedProperty(it, regItem, localizationsByField, localizationsByFieldML);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LocalizedProperty getLocalizedProperty(RegFieldmapping fieldmapping,
            RegItemproposed regItem,
            Map<String, List<RegLocalizationproposed>> localizationsByField,
            Map<String, List<RegLocalizationproposed>> localizationsByFieldML) throws Exception {
        RegField field = fieldmapping.getRegField();
        String id = field.getLocalid();
        String lang = languageCode.getIso6391code();
        boolean istitle = field.getIstitle();
        String label = getLabelForField(field);
        int order = fieldmapping.getListorder();
        boolean tablevisible = fieldmapping.getTablevisible();

        List<LocalizedPropertyValue> values = Collections.emptyList();

        //Get Property for allowing to return null value fields
        String allowEmptyFields = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_ALLOW_NULL_FIELDS, BaseConstants.KEY_BOOLEAN_STRING_FALSE);
        String dateformat = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

        switch (field.getRegFieldtype().getUuid()) {
            case BaseConstants.KEY_FIELDTYPE_REGISTRY_UUID:
                return null;
            case BaseConstants.KEY_FIELDTYPE_REGISTER_UUID:
                return null;
            case BaseConstants.KEY_FIELDTYPE_COLLECTION_UUID:
                RegItemproposed collection = getRelatedItemproposedBySubject(regItem, hasCollection);

                LocalizedProperty linksToRelatedItemsCollection = getLinksToRelationItemsproposed(field, label, order, tablevisible, collection);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsCollection == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsCollection == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsCollection;
                }
            case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
                RegItemproposed parent = getRelatedItemproposedBySubject(regItem, hasParent);
                LocalizedProperty linksToRelatedItemsParent = getLinksToRelationItemsproposed(field, label, order, tablevisible, parent);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsParent == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsParent == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsParent;
                }
            case BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID:
                List<RegItemproposed> successorList = getRelatedItemsproposedBySubject(regItem, hasSuccessor);
                List<LocalizedPropertyValue> successorValues = new ArrayList<>();

                for (RegItemproposed successor : successorList) {
                    LocalizedProperty linksToRelatedItemsSuccessor = getLinksToRelationItemsproposed(field, label, order, tablevisible, successor);
                    if (linksToRelatedItemsSuccessor.getValues() != null && !linksToRelatedItemsSuccessor.getValues().isEmpty()) {
                        successorValues.addAll(linksToRelatedItemsSuccessor.getValues());
                    }
                }

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (successorValues.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (successorValues.isEmpty() && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return new LocalizedProperty(lang, id, istitle, label, successorValues, order, tablevisible);
                }
            case BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID:
                List<RegItemproposed> predecessorList = getRelatedItemsProposedBySubject(regItem, hasPredecessor);
                List<LocalizedPropertyValue> predecessorValues = new ArrayList<>();

                for (RegItemproposed predecessor : predecessorList) {
                    LocalizedProperty linksToRelatedItemsPredecessor = getLinksToRelationItemsproposed(field, label, order, tablevisible, predecessor);
                    if (linksToRelatedItemsPredecessor.getValues() != null && !linksToRelatedItemsPredecessor.getValues().isEmpty()) {
                        predecessorValues.addAll(linksToRelatedItemsPredecessor.getValues());
                    }
                }

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (predecessorValues.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (predecessorValues.isEmpty() && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return new LocalizedProperty(lang, id, istitle, label, predecessorValues, order, tablevisible);
                }
            case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                LocalizedProperty linksToRelatedItems = getLinksToRelatedItemsProposed(field, label, order, tablevisible, regItem, localizationsByField, localizationsByFieldML);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItems == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItems == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItems;
                }

            case BaseConstants.KEY_FIELDTYPE_STATUS_UUID:
                StatusLocalization statusLocalization = getLocalizedStatus(regItem.getRegStatus());
                if (statusLocalization == null) {
                    return null;
                }
                lang = statusLocalization.getLang();
                String value = statusLocalization.getValue();
                String href = statusLocalization.getHref();
                values = Collections.singletonList(new LocalizedPropertyValue(value, href));
                break;

            case BaseConstants.KEY_FIELDTYPE_DATECREATION_UUID:

                String dateInsertValue = sdf.format(regItem.getInsertdate());
                values = Collections.singletonList(new LocalizedPropertyValue(dateInsertValue, null));
                break;

            case BaseConstants.KEY_FIELDTYPE_DATEEDIT_UUID:

                if (regItem.getEditdate() != null) {
                    String dateEditValue = sdf.format(regItem.getEditdate());
                    values = Collections.singletonList(new LocalizedPropertyValue(dateEditValue, null));
                }
                break;

            default:
                String key = field.getUuid();
                List<RegLocalizationproposed> localizations = localizationsByField.get(key);
                if (localizations == null || localizations.isEmpty()) {
                    if (localizationsByFieldML == null) {
                        break;
                        //return null;
                    }
                    // fallback to master language localization for this field for this reg item
                    lang = masterLanguage.getIso6391code();
                    localizations = localizationsByFieldML.get(key);
                    if (localizations == null) {
                        break;
                        //return null;
                    }
                }
                values = localizations.stream()
                        .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                        .collect(Collectors.toList());
                break;
        }

        if (values.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }
        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    protected RegItemproposed getRelatedItemproposedBySubject(RegItemproposed regItem, RegRelationpredicate predicate) throws Exception {
        List<RegItemproposed> list = getRelatedItemsproposedBySubject(regItem, predicate);
        if (list != null && !list.isEmpty()) {
            return list.stream().findAny().orElse(null);
        }
        return null;
    }

//        private void setRegistryAndRegisterItemRefProposed_ItemSupplier(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
//        ItemRef registryRef = null;
//        ItemRef registerRef = null;
//
//        switch (item.getType()) {
//            case TYPE_REGISTRY:
//                registerRef = null;
//
//                break;
//            case TYPE_REGISTER:
//                registryRef = toItemproposedRef(getRelatedItemproposedBySubject(regItemproposed, hasRegistry));
//
//                // Making an assumption that register can not be parent or collection to any item
//                break;
//            case TYPE_ITEM:
//                registryRef = toItemproposedRef(getRelatedItemproposedBySubject(regItemproposed, hasRegistry));
//                registerRef = toItemproposedRef(getRelatedItemproposedBySubject(regItemproposed, hasRegister));
//
//                break;
//            default:
//                throw new RuntimeException("Unexpected type");
//        }
//        if (registryRef != null) {
//            item.setRegistry(registryRef);
//        }
//        if (registerRef != null) {
//            item.setRegister(registerRef);
//        }
//    }
//        private LocalizedProperty getLinksToRelationItemsproposed(RegField field,
//            String label, int order, boolean tablevisible, RegItemproposed collection) throws Exception {
//        String id = field.getLocalid();
//        boolean istitle = field.getIstitle();
//        String lang = languageCode.getIso6391code();
//
//        List<LocalizedPropertyValue> values = new ArrayList<>();
//        String value = getLabelForItemProposed(collection);
//        if (value == null) {
//            return null;
//        }
//
//        RegItem regItem = collection.getRegItemReference();
//
////        String href = ItemHelper.getURItemProposed(collection);
////        
//        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
//
//        String href = ItemproposedHelper.getProposedURI(collection, regItem, hasCollection, regRelationproposedManager, regRelationManager);
//        values.add(new LocalizedPropertyValue(value, href));
//
//        if (values.isEmpty()) {
//            // Don't add properties that have no zero value/href pairs
//            return null;
//        }
//
//        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
//    }
    private LocalizedProperty getLinksToRelationItemsproposed(RegField field,
            String label, int order, boolean tablevisible, RegItemproposed collection) throws Exception {
        String id = field.getLocalid();
        boolean istitle = field.getIstitle();
        String lang = languageCode.getIso6391code();

        List<LocalizedPropertyValue> values = new ArrayList<>();
        String value = getLabelForItemproposed(collection);
        if (value == null) {
            return null;
        }
        String href = ItemproposedHelper.getProposedURI(collection);
        values.add(new LocalizedPropertyValue(value, href));

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private List<RegItemproposed> getRelatedItemsProposedByObject(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {

        if (regRelationproposedManager != null && regItemproposed != null && predicate != null
                && regRelationproposedManager.getAll(regItemproposed, predicate) != null) {
            List<RegRelationproposed> relations = regRelationproposedManager.getAll(regItemproposed, predicate);
            List<RegItemproposed> subjects = new ArrayList<>();
            relations.forEach((relation) -> {
                subjects.add(relation.getRegItemproposedSubject());
            });
            return subjects;
        } else {
            return null;
        }
    }
//    
//
//    private List<RegItemproposed> getRelatedItemsProposedByObject(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {
//
//        if (regRelationproposedManager != null && regItemproposed != null && predicate != null
//                && regRelationproposedManager.getAll(regItemproposed, predicate) != null) {
//            return regRelationproposedManager.getAll(regItemproposed, predicate).stream()
//                    .map(rel -> rel.getRegItemproposedObject())
//                    .collect(Collectors.toList());
//        } else {
//            return null;
//        }
//    }

    public List<RegItemproposed> getRelatedItemsproposedBySubject(RegItemproposed regItem, RegRelationpredicate predicate) throws Exception {

        if (regRelationproposedManager != null && regItem != null && predicate != null
                && regRelationproposedManager.getAll(regItem, predicate) != null) {
            return regRelationproposedManager.getAll(regItem, predicate).stream()
                    .map(rel -> rel.getRegItemproposedSubject())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private String getLabelForItemproposed(RegItemproposed regItem) throws Exception {
        List<RegLocalizationproposed> localizations = regLocalizationproposedManager.getAll(getLabelField(), regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            localizations = regLocalizationproposedManager.getAll(getLabelField(), regItem, masterLanguage);
        }
        return localizations.stream()
                .findAny()
                .map(l -> l.getValue())
                .orElse(null);
    }

//    private LocalizedProperty getLinksToRelatedItemsProposed(RegField field,
//            String label, int order, boolean tablevisible, RegItemproposed regItemproposed,
//            Map<String, List<RegLocalizationproposed>> localizationsByField,
//            Map<String, List<RegLocalizationproposed>> localizationsByFieldML) throws Exception {
//        String id = field.getLocalid();
//        boolean istitle = field.getIstitle();
//        String lang = languageCode.getIso6391code();
//
//        List<RegLocalizationproposed> localizations = localizationsByField.get(field.getUuid());
//        if (localizations == null || localizations.isEmpty()) {
//            if (localizationsByFieldML == null) {
//                return null;
//            }
//            // fallback to master language localization for this field for this reg item
//            lang = masterLanguage.getIso6391code();
//            localizations = localizationsByFieldML.get(field.getUuid());
//            if (localizations == null) {
//                return null;
//            }
//        }
//
//        List<RegItemproposed> relatedItems = localizations.stream()
//                .map(loc -> loc.getRegRelationproposedReference().getRegItemproposedObject())
//                .collect(Collectors.toList());
//        if (relatedItems.isEmpty()) {
//            return null;
//        }
//
//        List<LocalizedPropertyValue> values = new ArrayList<>(relatedItems.size());
//        for (RegItemproposed relItem : relatedItems) {
//            String value = getLabelForItemProposed(relItem);
//            if (value == null) {
//                continue;
//            }
//            // TO DO
//            // String href = ItemproposedHelper.getURI(relItem);
//            String href = "";
//            values.add(new LocalizedPropertyValue(value, href));
//        }
//
//        if (values.isEmpty()) {
//            // Don't add properties that have no zero value/href pairs
//            return null;
//        }
//
//        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
//    }
    private LocalizedProperty getLinksToRelatedItemsProposed(RegField field,
            String label, int order, boolean tablevisible, RegItemproposed regItem,
            Map<String, List<RegLocalizationproposed>> localizationsByField,
            Map<String, List<RegLocalizationproposed>> localizationsByFieldML) throws Exception {
        String id = field.getLocalid();
        boolean istitle = field.getIstitle();
        String lang = languageCode.getIso6391code();

        List<RegLocalizationproposed> localizations = localizationsByField.get(field.getUuid());
        if (localizations == null || localizations.isEmpty()) {
            if (localizationsByFieldML == null) {
                return null;
            }
            // fallback to master language localization for this field for this reg item
            lang = masterLanguage.getIso6391code();
            localizations = localizationsByFieldML.get(field.getUuid());
            if (localizations == null) {
                return null;
            }
        }

        List<RegItemproposed> relatedItems = localizations.stream()
                .map(loc -> loc.getRegRelationproposedReference().getRegItemproposedObject())
                .collect(Collectors.toList());
        if (relatedItems.isEmpty()) {
            return null;
        }

        List<LocalizedPropertyValue> values = new ArrayList<>(relatedItems.size());
        for (RegItemproposed relItem : relatedItems) {
            String value = getLabelForItemproposed(relItem);
            if (value == null) {
                continue;
            }
            String href = ItemproposedHelper.getProposedURI(relItem);
            values.add(new LocalizedPropertyValue(value, href));
        }

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private void setVersionAndHistoryItemproposed(RegItemproposed regItemproposed, ContainedItem item) throws Exception {
     
       RegItem regItemReference = regItemproposed.getRegItemReference();
       String uri = item.getUri();
       List<RegItemhistory> itemHistory = regItemHistoryManager.getByRegItemReferenceProposed(regItemproposed);
                
       item.setVersion(new VersionInformation(0, null));       
//     item.setVersion(new VersionInformation(0, uri + ":" + 0));
        if (regItemReference != null) {
//            int thisversion = itemHistory.size() + 2;
//            item.setVersion(new VersionInformation(thisversion, uri + ":" + thisversion));
            item.setVersion(new VersionInformation(0, null));
            String localId = regItemproposed.getRegItemReference().getLocalid();


            List<RegItem> itemValid = regItemManager.getByLocalid(localId);

            // Requested current version
            int maxVersionNumber = itemHistory.stream()
                    .mapToInt(ih -> ih.getVersionnumber())
                    .max()
                    .orElse(1); // Default to 1 if for whatever reason we can not find max version

            item.setVersionHistory(itemHistory.stream()
                    .filter(ih -> ih.getVersionnumber() != maxVersionNumber + 1)
                    .map(ih -> new VersionInformation(ih.getVersionnumber()+1, uri + ":" + (ih.getVersionnumber()+1))) // needed for showing the correct version
                    .collect(Collectors.toList()));

            List<VersionInformation> history = item.getVersionHistory();       
            int validVersion = itemHistory.size() + 1;
            VersionInformation validItem = new VersionInformation(validVersion, uri + ":" + validVersion);        
            history.add(validItem);
        }else{
            item.setVersionHistory(new Vector());
        }
    }

    
    protected ItemRef toItemRef(RegItem regItem) throws Exception {
        String uri = ItemHelper.getURI(regItem);

        RegField labelField = getLabelField();
        RegFieldmapping labelFieldmapping = regFieldmappingManager.getByFieldAndItemClass(labelField, regItem.getRegItemclass());

        String lang = languageCode.getIso6391code();
        String id = labelField.getLocalid();
        boolean istitle = labelField.getIstitle();
        int order = labelFieldmapping.getListorder();
        boolean tablevisible = labelFieldmapping.getTablevisible();

        List<RegLocalization> localizations = reglocalizationManager.getAll(labelField, regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            lang = masterLanguage.getIso6391code();
            localizations = reglocalizationManager.getAll(labelField, regItem, masterLanguage);
        }
        List<LocalizedPropertyValue> values = localizations.stream()
                .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                .collect(Collectors.toList());

        LocalizedProperty property = new LocalizedProperty(lang, id, istitle, id, values, order, tablevisible);
        return new ItemRef(uri, Arrays.asList(property));
    }

    private RegItem getRelatedItemProposedBySubject2(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {
        List<RegItem> list = getRelatedItemsBySubject(regItemproposed, predicate);
        if (list != null && !list.isEmpty()) {
            return list.stream().findAny().orElse(null);
        }
        return null;
    }
    
    private List<RegItem> getRelatedItemsBySubject(RegItemproposed regItemproposed, RegRelationpredicate predicate) throws Exception {

        if (regRelationproposedManager != null && regItemproposed != null && predicate != null
                && regRelationproposedManager.getAll(regItemproposed, predicate) != null) {
            return regRelationproposedManager.getAll(regItemproposed, predicate).stream()
                    .map(rel -> rel.getRegItemObject())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
    
}
