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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
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
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedPropertyValue;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.javaapi.cache.util.StatusLocalization;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class ItemSupplier {

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
    private final RegGroupManager regGroupManager;
    private final RegLocalizationproposedManager reglocalizationproposedManager;
    private final ItemproposedSupplier itemproposedSupplier;
    private final RegLanguagecodeManager regLanguagecodeManager;

    private final EntityManager entityManager;
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

    public ItemSupplier(EntityManager entityManager,
            RegLanguagecode masterLanguage,
            RegLanguagecode languageCode) throws Exception {

        this.regItemClassManager = new RegItemclassManager(entityManager);
        this.regItemManager = new RegItemManager(entityManager);
        this.regItemHistoryManager = new RegItemhistoryManager(entityManager);
        RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(entityManager);
        this.regRelationManager = new RegRelationManager(entityManager);
        this.regFieldManager = new RegFieldManager(entityManager);
        this.reglocalizationManager = new RegLocalizationManager(entityManager);
        this.regFieldmappingManager = new RegFieldmappingManager(entityManager);
        this.regStatusManager = new RegStatusManager(entityManager);
        this.regStatusLocalizationManager = new RegStatuslocalizationManager(entityManager);
        this.regItemproposedManager = new RegItemproposedManager(entityManager);
        this.regRelationproposedManager = new RegRelationproposedManager(entityManager);
        this.itemproposedSupplier = new ItemproposedSupplier(entityManager, masterLanguage, languageCode);
        this.reglocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        this.regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        this.regGroupManager = new RegGroupManager(entityManager);

        this.entityManager = entityManager;
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

    public Item getItemByUuid(String uuid) throws Exception {
        // Not possible to request specific version with uuid
        return toItem(regItemManager.get(uuid));
    }

    public Item getItem(RegItem regItem) throws Exception {
        // Not possible to request specific version with uuid
        return toItem(regItem);
    }

    public Item getItemByUri(String uri) throws Exception {
        RegItem item = getRegItemByUri(uri);
        if (item == null) {
            return null;
        }
        return toItem(item);
    }

    public Item getItemByUriAndStatus(String uri, String status) throws Exception {
        RegItem item = getRegItemByUriAndStatus(uri, status);
        if (item == null) {
            return null;
        }
        return toItem(item);
    }

    private RegItem getRegItemByUri(String uri) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);
        try {
            int uriCollection = uri.substring(0, i).lastIndexOf('/');
            String regItemClassLocalId = uri.substring(uriCollection + 1).replace("/" + localid, "");
            RegItem regItem;
            try {
                RegItemclass parentClass = regItemClassManager.getByLocalid(regItemClassLocalId);
                RegItemclass regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass).get(0);

                regItem = regItemManager.getByLocalidAndRegItemClass(localid, regItemRegItemClass);

                if (uri.replace("https://", "").replace("http://", "").equals(ItemHelper.getURI(regItem).replace("https://", "").replace("http://", ""))) {
                    return regItem;
                }
            } catch (Exception e) {
                int uriItemClassCollection = uri.substring(0, uriCollection).lastIndexOf('/');
                String regItemClassCollectionLocalId = uri.substring(uriItemClassCollection + 1, uriCollection).replace("/" + regItemClassLocalId, "").replace("/" + localid, "");
                RegItemclass parentClass = regItemClassManager.getByLocalid(regItemClassCollectionLocalId);
                RegItemclass regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass).get(0);

                RegItemclass regItemRegItemClassChild = regItemClassManager.getChildItemclass(regItemRegItemClass).get(0);
                regItem = regItemManager.getByLocalidAndRegItemClass(localid, regItemRegItemClassChild);

                return regItem;
            }

//            if (uri.equals(ItemHelper.getURI(regItem))) {
//                return regItem;
//            }
        } catch (Exception e) {
            for (RegItem item : regItemManager.getByLocalid(localid)) {
                if (uri.replace("https://", "").replace("http://", "").equals(ItemHelper.getURI(item).replace("https://", "").replace("http://", ""))) {
                    return item;
                }
            }
        }
        return null;
    }

    private RegItem getRegItemByUriAndStatus(String uri, String itemStatus) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);
        try {
            int uriCollection = uri.substring(0, i).lastIndexOf('/');
            String regItemClassLocalId = uri.substring(uriCollection + 1).replace("/" + localid, "");
            RegItemclass parentClass = regItemClassManager.get(regItemClassLocalId);
            RegItemclass regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass).get(0);

            RegStatus status = regStatusManager.findByLocalid(itemStatus);
            RegItem regItem;
            if (status.getIspublic()) {
                regItem = regItemManager.getByLocalidAndRegItemClassAndRegStatus(localid, regItemRegItemClass, status);
            } else {
                return null;
            }

            if (uri.equals(ItemHelper.getURI(regItem))) {
                return regItem;
            }
        } catch (Exception e) {
            for (RegItem item : regItemManager.getByLocalid(localid)) {
                if (uri.equals(ItemHelper.getURI(item))) {
                    return item;
                }
            }
        }
        return null;
    }

    private Item toItem(RegItem regItem) throws Exception {
        if (!regItem.getRegStatus().getIspublic()) {
            return null;
        }

        Item item = new Item();
        setMainPropertiesForRegItem(regItem, item);

        setRegistryAndRegisterItemRef(regItem, item);
        setIsDefinedByFromRegItem(regItem, item);

        //refactoring create once the lists and than add them to the list that needs them
        //improve cache
        if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(TYPE_ITEM)) {
            List<RegItem> collection = ItemHelper.getRelatedItemsByObject(regItem, hasCollection, entityManager);
            List<String> collectionNoParentList = getAllColectionsNoParentOfItem(regItem);
            List<RegItem> parent = ItemHelper.getRelatedItemsByObject(regItem, hasParent, entityManager);
        }

        //all the items of the codelist, so all items with hasCollection regItem
        setContainedItemsFromRegItem(regItem, item);

        setContainedItemsFromRegItemClassWithParent(regItem, item);

        //all the item of codelist without a parent 
        setTopConceptsFromRegItem(regItem, item);
        
        //get che codelist
        setInSchemeAndTopConceptOfFromRegItem(regItem, item);
        
        //all direct suns, so all that have the item as parent hasParent
        setNarrowerFromRegItem(regItem, item);
        
        //direct Parent hasParent
        setBroaderFromRegItem(regItem, item);

//        setActiveLangList(item);        
        return item;
    }

    protected ContainedItem toContainedItem(RegItem regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();

        setMainPropertiesForRegItem(regItem, containedItem);
        setRegistryAndRegisterItemRef(regItem, containedItem);
        setIsDefinedByFromRegItem(regItem, containedItem);

        setTopConceptsFromRegItem(regItem, containedItem);
        setInSchemeAndTopConceptOfFromRegItem(regItem, containedItem);
        if (topConceptItem) {
            setNarrowerFromRegItem(regItem, containedItem);
            setBroaderFromRegItem(regItem, containedItem);
            setContainedItemsFromRegItem(regItem, containedItem);
        }

        return containedItem;
    }

    protected ContainedItem toContainedItemWithoutTopConcept(RegItem regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();

        setMainPropertiesForRegItem(regItem, containedItem);
        setRegistryAndRegisterItemRef(regItem, containedItem);
//        setIsDefinedByFromRegItem(regItem, containedItem);
//
//        setTopConceptsFromRegItem(regItem, containedItem);
//        setInSchemeAndTopConceptOfFromRegItem(regItem, containedItem);
//        if (topConceptItem) {
//            setNarrowerFromRegItem(regItem, containedItem);
//            setBroaderFromRegItem(regItem, containedItem);
//            setContainedItemsFromRegItem(regItem, containedItem);
//        }

        return containedItem;
    }

    protected BasicContainedItem toBasicContainedItem(RegItem regItem) throws Exception {
        BasicContainedItem citem = new BasicContainedItem();
        String label = getLabelfromItem(regItem);
        LocalizedPropertyValue myvalues = new LocalizedPropertyValue(label, "");
        citem.addValue(myvalues);
        citem.setUri(ItemHelper.getURI(regItem));

        RegItem topConcept = ItemHelper.getRelatedItemBySubject(regItem, hasCollection, entityManager);
        if (topConcept != null) {
            citem.setTopConceptOf(toBasicContainedItem(topConcept));
            boolean moreParents = true;
            while (moreParents) {
                RegItem item = ItemHelper.getRelatedItemBySubject(topConcept, hasCollection, entityManager);
                if (item != null) {
                    if (regItem.equals(item)) {
                        return null;
                    }
                    BasicContainedItem subTopConcept = citem.getTopConceptOf();
                    subTopConcept.setTopConceptOf(toBasicContainedItem(item));
                } else {
                    moreParents = false;
                }
            }

        }
        return citem;
    }

    protected ContainedItem toBasicContainedItemDefinedBy(RegItem regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();
        setMainPropertiesForRegItem(regItem, containedItem);
        return containedItem;
    }

    protected ContainedItem toBasicContainedItemproposedDefinedBy(RegItemproposed regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();
        containedItem = this.itemproposedSupplier.toContainedItemWithoutItemsProposed(regItem);
        return containedItem;
    }

    protected ContainedItem toContainedItemWithoutItems(RegItem regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();

        setMainPropertiesForRegItem(regItem, containedItem);
        setRegistryAndRegisterItemRef(regItem, containedItem);

        setIsDefinedByFromRegItem(regItem, containedItem);
        setTopConceptsFromRegItem(regItem, containedItem);
        setInSchemeAndTopConceptOfFromRegItem(regItem, containedItem);

        return containedItem;
    }

    protected ContainedItem toContainedItemWithoutItemsproposed(RegItemproposed regItem) throws Exception {
        ContainedItem containedItem = new ContainedItem();
        containedItem = this.itemproposedSupplier.toContainedItemWithoutItemsProposed(regItem);

        return containedItem;
    }

    private ContainedItem setMainPropertiesForRegItem(RegItem regItem, ContainedItem item) throws Exception {
        item.setUuid(regItem.getUuid());
        item.setUri(ItemHelper.getURI(regItem));
        item.setLocalid(regItem.getLocalid());
        item.setLatest(true);
        if (regItem.getExternal()){
            item.setExternal(true);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        item.setInsertdate(df.format(regItem.getInsertdate()));
        if (regItem.getEditdate() != null) {
            item.setEditdate(df.format(regItem.getEditdate()));
        }

        setVersionAndHistory(regItem, item);
        item.setType(regItem.getRegItemclass().getRegItemclasstype().getLocalid());
        item.setLanguage(languageCode.getIso6391code());
        RegItemclass itemclassParent = regItem.getRegItemclass().getRegItemclassParent();
        if (itemclassParent != null) {
            item.setItemclass(new BasicItemClass(regItem.getRegItemclass().getLocalid(),
                    itemclassParent.getLocalid(),
                    itemclassParent.getRegItemclasstype().getLocalid()));
        } else {
            item.setItemclass(new BasicItemClass(regItem.getRegItemclass().getLocalid(), null, null));
        }
        item.setProperties(getLocalizedProperties(regItem, fieldMapping -> !fieldMapping.getHidden()));

        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
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
            List<RegItem> hasCollectionList = ItemHelper.getRelatedItemsBySubject(regItem, hasCollection, entityManager);
            if (hasCollectionList != null && !hasCollectionList.isEmpty()) {
                item.setHasCollection(true);
            }
            List<RegItem> isParentList = ItemHelper.getRelatedItemsBySubject(regItem, hasParent, entityManager);
            if (isParentList != null && !isParentList.isEmpty()) {
                item.setIsParent(true);
            }
            if (regItem.getExternal()) {
                item.setExternal(true);
            }
        }
        return item;
    }

    private void setVersionAndHistory(RegItem regItem, ContainedItem item) throws Exception {
        String uri = item.getUri();

        List<RegItemhistory> itemHistory = regItemHistoryManager.getByRegItemReference(regItem);
        if (itemHistory != null && !itemHistory.isEmpty()) {

            // Requested current version
            int maxVersionNumber = itemHistory.stream()
                    .mapToInt(ih -> ih.getVersionnumber())
                    .max()
                    .orElse(1); // Default to 1 if for whatever reason we can not find max version
            int minVersionNumber = itemHistory.stream()
                    .mapToInt(ih -> ih.getVersionnumber())
                    .min()
                    .orElse(1); // Default to 1 if for whatever reason we can not find max version
            int thisversion = itemHistory.size() + 1;
            item.setVersion(new VersionInformation(thisversion, uri + ":" + thisversion));

            if (minVersionNumber == 0) {
                item.setVersionHistory(itemHistory.stream()
                        .filter(ih -> ih.getVersionnumber() != maxVersionNumber + 1)
                        .map(ih -> new VersionInformation(ih.getVersionnumber(), uri + ":" + (ih.getVersionnumber() + 1)))
                        .collect(Collectors.toList()));
            } else if (minVersionNumber == 1) {
                item.setVersionHistory(itemHistory.stream()
                        .filter(ih -> ih.getVersionnumber() != maxVersionNumber + 1)
                        .map(ih -> new VersionInformation(ih.getVersionnumber(), uri + ":" + ih.getVersionnumber()))
                        .collect(Collectors.toList()));
            }
        } else {
            item.setVersion(new VersionInformation(1, uri + ":" + 1));
        }
    }

    private void setRegistryAndRegisterItemRef(RegItem regItem, ContainedItem item) throws Exception {
        ItemRef registryRef = null;
        ItemRef registerRef = null;

        switch (item.getType()) {
            case TYPE_REGISTRY:
                registerRef = null;

                break;
            case TYPE_REGISTER:
                registryRef = toItemRef(ItemHelper.getRelatedItemBySubject(regItem, hasRegistry, entityManager));

                // Making an assumption that register can not be parent or collection to any item
                break;
            case TYPE_ITEM:
                registryRef = toItemRef(ItemHelper.getRelatedItemBySubject(regItem, hasRegistry, entityManager));
                registerRef = toItemRef(ItemHelper.getRelatedItemBySubject(regItem, hasRegister, entityManager));

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

    private void setContainedItemsFromRegItem(RegItem regItem, ContainedItem item) throws Exception {
        List<ContainedItem> containedItems = new ArrayList<>();
        List<RegItem> containedItemsList = null;
        List<RegItemproposed> containedItemsproposedList = null;
        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                containedItemsList = getRegisters(regItem);

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItems(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItems(containedItems);
                    }
                }
                break;
            case TYPE_REGISTER:

                // ADD REGITEMs
                containedItemsList = getDirectlyContainedItemsOfRegister(regItem);
                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItems(containedItem));
                        }
                    }
                }

                // ADD REGITEMPROPOSEDs
                containedItemsproposedList = getDirectlyContainedItemsproposedOfRegister(regItem);

                if (containedItemsproposedList != null && !containedItemsproposedList.isEmpty()) {
                    for (RegItemproposed containedItemproposed : containedItemsproposedList) {

                        if (!containedItemproposed.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItemsproposed(containedItemproposed));
                        }
                    }
                }

                //toContainedItemWithoutItemsproposed(containedItemsProposedList);
                // ADD TO THE CONTAINED ITEMS
                if (!containedItems.isEmpty()) {
                    item.setContainedItems(containedItems);
                }
                break;
            case TYPE_ITEM:
                List<BasicContainedItem> topConcepts = new ArrayList<>();
                try {
                    //take all the items af the collection
                    containedItemsList = ItemHelper.getRelatedItemsByObject(regItem, hasCollection, entityManager);

                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        List<String> collectionNoParentList = getAllColectionsNoParentOfItem(regItem);
                        for (String uuid : collectionNoParentList) {
                            containedItemsList.add(regItemManager.get(uuid));
                        }
                    }
                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        containedItemsList = ItemHelper.getRelatedItemsByObject(regItem, hasParent, entityManager);
                    }
//                    }
                    for (RegItem childItem : containedItemsList) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(toBasicContainedItem(childItem));
                        }
                    }
                } catch (Exception exception) {
                    for (RegItem childItem : ItemHelper.getRelatedItemsByObject(regItem, hasParent, entityManager)) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(toBasicContainedItem(childItem));
                        }
                    }
                }
                if (!topConcepts.isEmpty()) {
                    item.setTopConcepts(topConcepts);
                }

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItem(containedItem));
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

    private void setContainedItemsFromRegItemClassWithParent(RegItem regItem, ContainedItem item) throws Exception {
        List<ContainedItem> containedItems = new ArrayList<>();
        List<RegItem> containedItemsList = new ArrayList<>();
        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                //theme register, codelist register
                List<RegItemclass> childItemClassList = regItemClassManager.getChildItemclass(regItem.getRegItemclass());

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
                    containedItemsList.addAll(regItemManager.getAll(regItemclass));
                }

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(toContainedItemWithoutItems(containedItem));
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

    private void setInSchemeAndTopConceptOfFromRegItem(RegItem regItem, ContainedItem containedItem) throws Exception {

        BasicContainedItem basicContainedItem = null;
        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:
                RegItem relatedRegistry = ItemHelper.getRelatedItemBySubject(regItem, hasRegistry, entityManager);
                basicContainedItem = toBasicContainedItem(relatedRegistry);
                break;
            case TYPE_ITEM:
                RegItem relatedCollection = ItemHelper.getRelatedItemBySubject(regItem, hasCollection, entityManager);
                if (relatedCollection != null) {
                    RegItem parentCollection = ItemHelper.getRelatedItemBySubject(relatedCollection, hasParent, entityManager);
                    if (parentCollection != null) {
                        relatedCollection = parentCollection;
                    }
                }
                if (relatedCollection == null) {
                    relatedCollection = ItemHelper.getRelatedItemBySubject(regItem, hasRegister, entityManager);
                }
                basicContainedItem = toBasicContainedItem(relatedCollection);
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (basicContainedItem != null && basicContainedItem.getUri() != null) {
            containedItem.setInScheme(basicContainedItem);
            containedItem.setTopConceptOf(basicContainedItem);
        }
    }

    private void setBroaderFromRegItem(RegItem regItem, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> broader = new ArrayList<>();
        List<RegItem> broaderList = null;

        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                broaderList = ItemHelper.getRelatedItemsBySubject(regItem, hasParent, entityManager);
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = ItemHelper.getRelatedItemsBySubject(regItem, hasCollection, entityManager);
                }
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = ItemHelper.getRelatedItemsBySubject(regItem, hasRegister, entityManager);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        if (broaderList != null && !broaderList.isEmpty()) {
            for (RegItem childItem : broaderList) {
                if (!childItem.getRegItemclass().getSystemitem()) {
                    broader.add(toBasicContainedItem(childItem));
                }
            }
            if (!broader.isEmpty()) {
                containedItem.setBroader(broader);
            }
        }
    }

    private void setNarrowerFromRegItem(RegItem regItem, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> narrower = new ArrayList<>();
        List<RegItem> narrowerList = null;
        if (regItem != null && containedItem != null) {
            switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
                case TYPE_REGISTRY:

                    break;
                case TYPE_REGISTER:
//                narrowerList = getDirectlyContainedItemsOfRegister(regItem);
                    break;
                case TYPE_ITEM:
                    narrowerList = ItemHelper.getRelatedItemsByObject(regItem, hasParent, entityManager);
                    if (narrowerList == null || narrowerList.isEmpty()) {
                        narrowerList = ItemHelper.getRelatedItemsByObject(regItem, hasCollection, entityManager);
                    }
                    break;
                default:
                    throw new RuntimeException("Unexpected type");
            }

            if (narrowerList != null && !narrowerList.isEmpty()) {
                for (RegItem childItem : narrowerList) {
                    if (!childItem.getRegItemclass().getSystemitem()) {
                        try {
                            narrower.add(toContainedItemWithoutItems(childItem));
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

    private void setTopConceptsFromRegItem(RegItem regItem, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> topConcepts = new ArrayList<>();
        List<RegItem> childItemList = new ArrayList<>();

        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                childItemList = getRegisters(regItem);

                break;
            case TYPE_REGISTER:
                childItemList = getDirectlyContainedItemsOfRegister(regItem);

                break;
            case TYPE_ITEM:
                List<String> collectionNoParentList = getAllColectionsNoParentOfItem(regItem);
                for (String uuid : collectionNoParentList) {
                    childItemList.add(regItemManager.get(uuid));
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = ItemHelper.getRelatedItemsByObject(regItem, hasCollection, entityManager);
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = ItemHelper.getRelatedItemsByObject(regItem, hasParent, entityManager);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        for (RegItem childItem : childItemList) {
            if (!childItem.getRegItemclass().getSystemitem()) {
                topConcepts.add(toBasicContainedItem(childItem));
            }
        }

        if (!topConcepts.isEmpty()) {
            containedItem.setTopConcepts(topConcepts);
        }
    }

    private void setIsDefinedByFromRegItem(RegItem regItem, ContainedItem containedItem) throws Exception {
        List<ContainedItem> isDefinedBy = new ArrayList<>();

        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                for (RegItem relatedItem : ItemHelper.getRelatedItemsBySubject(regItem, hasReference, entityManager)) {
                    if (!relatedItem.getRegItemclass().getSystemitem()) {
                        isDefinedBy.add(toBasicContainedItemDefinedBy(relatedItem));
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

    protected ItemRef toItemproposedRef(RegItemproposed regItem) throws Exception {
        String uri = ItemproposedHelper.getProposedURI(regItem);

        RegField labelField = getLabelField();
        RegFieldmapping labelFieldmapping = regFieldmappingManager.getByFieldAndItemClass(labelField, regItem.getRegItemclass());

        String lang = languageCode.getIso6391code();
        String id = labelField.getLocalid();
        boolean istitle = labelField.getIstitle();
        int order = labelFieldmapping.getListorder();
        boolean tablevisible = labelFieldmapping.getTablevisible();

        List<RegLocalizationproposed> localizations = reglocalizationproposedManager.getAll(labelField, regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            lang = masterLanguage.getIso6391code();
            localizations = reglocalizationproposedManager.getAll(labelField, regItem, masterLanguage);
        }
        List<LocalizedPropertyValue> values = localizations.stream()
                .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                .collect(Collectors.toList());

        LocalizedProperty property = new LocalizedProperty(lang, id, istitle, id, values, order, tablevisible);
        return new ItemRef(uri, Arrays.asList(property));
    }

    private RegField getLabelField() throws Exception {
        if (labelField == null) {
            labelField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
        }
        return labelField;
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

    private List<LocalizedProperty> getLocalizedProperties(RegItem regItem, Predicate<RegFieldmapping> fieldmappingFilter) throws Exception {
        List<RegLocalization> localizations = reglocalizationManager.getAll(regItem, languageCode);
        Map<String, List<RegLocalization>> localizationsByField = localizations.stream()
                .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));

        Map<String, List<RegLocalization>> localizationsByFieldML;
        if (languageCode.equals(masterLanguage)) {
            localizationsByFieldML = null;
        } else {
            localizationsByFieldML = reglocalizationManager.getAll(regItem, masterLanguage).stream()
                    .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));
        }

        List<RegFieldmapping> fieldmappings = getFieldmappings(regItem.getRegItemclass());
        return fieldmappings.stream()
                .filter(fieldmappingFilter)
                .map(it -> {
                    try {
                        return getLocalizedPropertyProposed(it, regItem, localizationsByField, localizationsByFieldML);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    private LocalizedProperty getLocalizedPropertyProposed(RegFieldmapping fieldmapping,
            RegItem regItem,
            Map<String, List<RegLocalization>> localizationsByField,
            Map<String, List<RegLocalization>> localizationsByFieldML) throws Exception {
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
                RegItem collection = ItemHelper.getRelatedItemBySubject(regItem, hasCollection, entityManager);

                LocalizedProperty linksToRelatedItemsCollection = getLinksToRelationItems(field, label, order, tablevisible, collection);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsCollection == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsCollection == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsCollection;
                }
            case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
                RegItem parent = ItemHelper.getRelatedItemBySubject(regItem, hasParent, entityManager);
                LocalizedProperty linksToRelatedItemsParent = getLinksToRelationItems(field, label, order, tablevisible, parent);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsParent == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsParent == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsParent;
                }
            case BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID:
                List<RegItem> successorList = ItemHelper.getRelatedItemsBySubject(regItem, hasSuccessor, entityManager);
                List<LocalizedPropertyValue> successorValues = new ArrayList<>();

                for (RegItem successor : successorList) {
                    LocalizedProperty linksToRelatedItemsSuccessor = getLinksToRelationItems(field, label, order, tablevisible, successor);
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
                List<RegItem> predecessorList = ItemHelper.getRelatedItemsBySubject(regItem, hasPredecessor, entityManager);
                List<LocalizedPropertyValue> predecessorValues = new ArrayList<>();

                for (RegItem predecessor : predecessorList) {
                    LocalizedProperty linksToRelatedItemsPredecessor = getLinksToRelationItems(field, label, order, tablevisible, predecessor);
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
                LocalizedProperty linksToRelatedItems = getLinksToRelatedItems(field, label, order, tablevisible, regItem, localizationsByField, localizationsByFieldML);

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
                RegGroup rolLabel = null;
                List<RegLocalization> localizations = localizationsByField.get(key);
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
                    // fallback to master language localization for this field for this reg item. Whan do i need this?
//                    RegLocalization localization = this.reglocalizationManager.get(field, masterLanguage);
//                    if (localization != null) {
//                        localizations = new ArrayList<>();
//                        localizations.add(localization);
//                    }
                    try {
                        rolLabel = this.regGroupManager.getByLocalid(regFieldManager.get(key).getLocalid());
                    } catch (Exception e) {
                    }

//                    if (localization == null) {
//                        break;
//                    }
                }
                if (localizations != null && !localizations.isEmpty()) {
                    values = localizations.stream()
                            .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                            .collect(Collectors.toList());
                }
                if (rolLabel != null) {
                    if (!values.isEmpty()) {
                        values.clear();
                    }
                    values = Collections.singletonList(new LocalizedPropertyValue(rolLabel.getName(), null));
                }
                break;
        }

//        if (values.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
//            // Don't add properties that have no zero value/href pairs
//            return null;
//        }
        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private LocalizedProperty getLinksToRelatedItems(RegField field,
            String label, int order, boolean tablevisible, RegItem regItem,
            Map<String, List<RegLocalization>> localizationsByField,
            Map<String, List<RegLocalization>> localizationsByFieldML) throws Exception {
        String id = field.getLocalid();
        boolean istitle = field.getIstitle();
        String lang = languageCode.getIso6391code();

        List<RegLocalization> localizations = localizationsByField.get(field.getUuid());
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

        List<RegItem> relatedItems = localizations.stream()
                .map(loc -> loc.getRegRelationReference().getRegItemObject())
                .collect(Collectors.toList());
        if (relatedItems.isEmpty()) {
            return null;
        }

        List<LocalizedPropertyValue> values = new ArrayList<>(relatedItems.size());
        for (RegItem relItem : relatedItems) {
            String value = getLabelForItem(relItem);
            if (value == null) {
                continue;
            }
            String href = ItemHelper.getURI(relItem);
            values.add(new LocalizedPropertyValue(value, href));
        }

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private LocalizedProperty getLinksToRelationItems(RegField field,
            String label, int order, boolean tablevisible, RegItem collection) throws Exception {
        String id = field.getLocalid();
        boolean istitle = field.getIstitle();
        String lang = languageCode.getIso6391code();

        List<LocalizedPropertyValue> values = new ArrayList<>();
        String value = getLabelForItem(collection);
        if (value == null) {
            return null;
        }
        String href = ItemHelper.getURI(collection);
        values.add(new LocalizedPropertyValue(value, href));

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private String getLabelForItem(RegItem regItem) throws Exception {
        List<RegLocalization> localizations = reglocalizationManager.getAll(getLabelField(), regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            localizations = reglocalizationManager.getAll(getLabelField(), regItem, masterLanguage);
        }
        return localizations.stream()
                .findAny()
                .map(l -> l.getValue())
                .orElse(null);
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

    private List<RegItem> getRegisters(RegItem registry) throws Exception {
        List<RegItem> registrysRegisters = new ArrayList<>();
        List<RegItem> registers = getAllRegisters();
        List<RegRelation> relations = regRelationManager.getAllByRegItemsSubjectAndPredicate(registers, hasRegistry);
        relations.stream().filter((rel) -> (rel.getRegItemObject().equals(registry))).forEachOrdered((rel) -> {
            registrysRegisters.add(rel.getRegItemSubject());
        });
        return registrysRegisters;
    }

    protected List<RegItem> getAllRegisters() throws Exception {
        return getItemsOfType(TYPE_REGISTER);
    }

    private List<RegItem> getItemsOfType(String classtype) throws Exception {
        List<RegItemclass> allItemClasses = regItemClassManager.getAll();
        List<RegItemclass> itemclasses = allItemClasses.stream()
                .filter(it -> it.getRegItemclasstype().getLocalid().equals(classtype))
                .collect(Collectors.toList());
        List<RegItem> list = new ArrayList<>();
        for (RegItemclass itemclass : itemclasses) {
            list.addAll(regItemManager.getAll(itemclass));
        }
        return list;
    }

    private List<RegItem> getDirectlyContainedItemsOfRegister(RegItem register) throws Exception {
        return regItemManager.getAllSubjectsByRegItemObjectAndPredicateAndSubjectNotPredicate(register, hasRegister, hasCollection);
    }

    private List<RegItemproposed> getDirectlyContainedItemsproposedOfRegister(RegItem register) throws Exception {
        RegItemclass parentClass = regItemClassManager.getByLocalid(register.getLocalid());
        List<RegItemclass> regItemRegItemClass = regItemClassManager.getChildItemclass(parentClass);

        List<RegItemproposed> regItemproposedList = new ArrayList<>();

        for (int i = 0; i < regItemRegItemClass.size(); i++) {

            List<RegItemproposed> proposedItemsfromList = regItemproposedManager.getAll(regItemRegItemClass.get(i));

            for (int y = 0; y < proposedItemsfromList.size(); y++) {
                RegItemproposed proposedItem = proposedItemsfromList.get(y);
                boolean isPublic = proposedItem.getRegStatus().getIspublic();

                if (isPublic) {
                    regItemproposedList.add(proposedItem);
                }
            }
        }

        return regItemproposedList;
    }

    private List<String> getAllColectionsNoParentOfItem(RegItem item) throws Exception {
        return regItemManager.getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(item, regStatusManager.get("1"), hasCollection, hasParent);
    }

    private String getLabelfromItem(RegItem regitem) throws Exception {
        RegField field = regFieldManager.getByLocalid("label");
        RegLocalization loc = (reglocalizationManager.getAll(field, regitem)).get(0);

        return loc.getValue();
    }

    private void setActiveLangList(Item item) throws Exception {
        List<RegLanguagecode> activeLanguages = regLanguagecodeManager.getAllActive();
        item.setActiveLanguages(activeLanguages);
    }
}
