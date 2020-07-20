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
 *  * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
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
import eu.europa.ec.re3gistry2.restapi.model.ContainedItem;
import eu.europa.ec.re3gistry2.restapi.model.Item;
import eu.europa.ec.re3gistry2.restapi.model.ItemClass;
import eu.europa.ec.re3gistry2.restapi.model.ItemRef;
import eu.europa.ec.re3gistry2.restapi.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.restapi.model.LocalizedPropertyValue;
import eu.europa.ec.re3gistry2.restapi.model.VersionInformation;
import eu.europa.ec.re3gistry2.restapi.util.NoVersionException;
import eu.europa.ec.re3gistry2.restapi.util.StatusLocalization;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class ItemSupplier {

    private static final String TYPE_REGISTRY = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY;
    private static final String TYPE_REGISTER = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER;
    private static final String TYPE_ITEM = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;

    private final RegItemclassManager classManager;
    private final RegItemManager itemManager;
    private final RegItemhistoryManager itemHistoryManager;
    private final RegRelationManager relationManager;
    private final RegFieldManager fieldManager;
    private final RegLocalizationManager localizationManager;
    private final RegFieldmappingManager fieldmappingManager;
    private final RegStatuslocalizationManager statusLocalizationManager;

    private final RegLanguagecode masterLanguage;
    private final RegLanguagecode languageCode;

    private final RegRelationpredicate hasRegistry;
    private final RegRelationpredicate hasRegister;
    private final RegRelationpredicate hasParent;
    private final RegRelationpredicate hasCollection;

    // Lazily populated
    private RegField labelField;
    private Map<String, String> fieldToLabel;
    private Map<String, StatusLocalization> statusToLocalization;
    private Map<String, List<RegFieldmapping>> itemclassToFieldmapping;

    public ItemSupplier(RegItemclassManager classManager,
            RegItemManager itemManager,
            RegItemhistoryManager itemHistoryManager,
            RegRelationpredicateManager relationPredicateManager,
            RegRelationManager relationManager,
            RegFieldManager fieldManager,
            RegLocalizationManager localizationManager,
            RegFieldmappingManager fieldmappingManager,
            RegStatuslocalizationManager statusLocalizationManager,
            RegLanguagecode masterLanguage,
            RegLanguagecode languageCode) throws Exception {
        this.classManager = classManager;
        this.itemManager = itemManager;
        this.itemHistoryManager = itemHistoryManager;
        this.relationManager = relationManager;
        this.fieldManager = fieldManager;
        this.localizationManager = localizationManager;
        this.fieldmappingManager = fieldmappingManager;
        this.statusLocalizationManager = statusLocalizationManager;
        this.masterLanguage = masterLanguage;
        this.languageCode = languageCode;
        this.hasRegistry = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        this.hasRegister = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        this.hasParent = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);
        this.hasCollection = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
    }

    public Item getItemByUuid(String uuid) throws Exception {
        // Not possible to request specific version with uuid
        Integer version = null;
        return toItem(itemManager.get(uuid), version);
    }

    public Item getItemByUri(String uri) throws Exception {
        Integer version;
        // Check if the part after the last slash contains a colon
        int i = uri.lastIndexOf('/');
        i = uri.indexOf(':', i + 1);
        if (i < 0) {
            version = null;
        } else {
            try {
                version = Integer.parseInt(uri.substring(i + 1));
                uri = uri.substring(0, i);
            } catch (Exception ignore) {
                return null;
            }
        }

        RegItem item = getRegItemByUri(uri);
        if (item == null) {
            return null;
        }
        return toItem(item, version);
    }

    private RegItem getRegItemByUri(String uri) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);

        for (RegItem item : itemManager.getByLocalid(localid)) {
            if (uri.equals(getURI(item))) {
                return item;
            }
        }

        return null;
    }

    private Item toItem(RegItem regItem, Integer version) throws Exception {
        if (!regItem.getRegStatus().getIspublic()) {
            return null;
        }

        Item item = new Item();
        item.setUuid(regItem.getUuid());
        item.setUri(getURI(regItem));
        setVersionAndHistory(item, regItem, version);
        item.setType(regItem.getRegItemclass().getRegItemclasstype().getLocalid());
        item.setLanguage(languageCode.getIso6391code());
        item.setItemclass(new ItemClass(regItem.getRegItemclass().getLocalid()));
        item.setProperties(getLocalizedProperties(regItem, fieldMapping -> !fieldMapping.getHidden()));

        Optional<LocalizedProperty> labelProperty = item.getProperty(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

        ItemRef registryRef;
        ItemRef registerRef;

        List<ContainedItem> containedItems = new ArrayList<>();
        List<ContainedItem> narrower = new ArrayList<>();

        switch (item.getType()) {
            case TYPE_REGISTRY:
                registryRef = new ItemRef(item.getUri(), Arrays.asList(labelProperty.get()));
                registerRef = null;

                for (RegItem containedItem : getRegisters(regItem)) {
                    String baseUri = containedItem.getRegItemclass().getBaseuri();
                    if (baseUri == null) {
                        baseUri = item.getUri();
                    }
                    if (!containedItem.getRegItemclass().getSystemitem()) {
                        containedItems.add(toContainedItem(baseUri, containedItem));
                    }
                }
                // Making an assumption that registry can not be parent or collection to any item

                break;
            case TYPE_REGISTER:
                registryRef = toItemRef(getRelatedItemBySubject(regItem, hasRegistry));
                registerRef = new ItemRef(item.getUri(), Arrays.asList(labelProperty.get()));

                for (RegItem containedItem : getDirectlyContainedItemsOfRegister(regItem)) {
                    if (!containedItem.getRegItemclass().getSystemitem()) {
                        containedItems.add(toContainedItem(item.getUri(), containedItem));
                    }
                }

                // Making an assumption that register can not be parent or collection to any item
                break;
            case TYPE_ITEM:
                registryRef = toItemRef(getRelatedItemBySubject(regItem, hasRegistry));
                registerRef = toItemRef(getRelatedItemBySubject(regItem, hasRegister));

                for (RegItem containedItem : getRelatedItemsByObject(regItem, hasCollection)) {
                    if (!containedItem.getRegItemclass().getSystemitem()) {
                        containedItems.add(toContainedItem(item.getUri(), containedItem));
                    }
                }
                for (RegItem childItem : getRelatedItemsByObject(regItem, hasParent)) {
                    if (!childItem.getRegItemclass().getSystemitem()) {
                        narrower.add(toContainedItem(item.getUri(), childItem));
                    }
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        item.setRegistry(registryRef);
        item.setRegister(registerRef);

        if (!containedItems.isEmpty()) {
            item.setContainedItems(containedItems);
        }
        if (!narrower.isEmpty()) {
            item.setNarrower(narrower);
        }

        return item;
    }

    private ContainedItem toContainedItem(String baseUri, RegItem regItem) throws Exception {
        ContainedItem citem = new ContainedItem();
        citem.setUuid(regItem.getUuid());
        citem.setUri(baseUri + "/" + regItem.getLocalid());
        citem.setLanguage(languageCode.getIso6391code());
        citem.setItemclass(new ItemClass(regItem.getRegItemclass().getLocalid()));
        citem.setProperties(getLocalizedProperties(regItem, fieldMapping -> fieldMapping.getTablevisible()));
        return citem;
    }

    private ItemRef toItemRef(RegItem regItem) throws Exception {
        String uri = getURI(regItem);

        RegField labelField = getLabelField();
        RegFieldmapping labelFieldmapping = fieldmappingManager.getByFieldAndItemClass(labelField, regItem.getRegItemclass());

        String lang = languageCode.getIso6391code();
        String id = labelField.getLocalid();
        boolean istitle = labelField.getIstitle();
        int order = labelFieldmapping.getListorder();

        List<RegLocalization> localizations = localizationManager.getAll(labelField, regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            lang = masterLanguage.getIso6391code();
            localizations = localizationManager.getAll(labelField, regItem, masterLanguage);
        }
        List<LocalizedPropertyValue> values = localizations.stream()
                .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
                .collect(Collectors.toList());

        LocalizedProperty property = new LocalizedProperty(lang, id, istitle, id, values, order);
        return new ItemRef(uri, Arrays.asList(property));
    }

    private RegField getLabelField() throws Exception {
        if (labelField == null) {
            labelField = fieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
        }
        return labelField;
    }

    private String getURI(RegItem item) throws Exception {
        if (item == null) {
            return null;
        }
        RegItemclass itemclass = item.getRegItemclass();
        switch (itemclass.getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                return itemclass.getBaseuri() + "/" + item.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                String baseuri = itemclass.getBaseuri();
                if (baseuri != null) {
                    return baseuri + "/" + item.getLocalid();
                }
                String registryURI = getURI(getRelatedItemBySubject(item, hasRegistry));
                return registryURI + "/" + item.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                String registerURI = getURI(getRelatedItemBySubject(item, hasRegister));
                List<RegItem> collectionChain = getCollectionChain(item);
                if (collectionChain.isEmpty()) {
                    return registerURI + "/" + item.getLocalid();
                }
                String collectionsPath = collectionChain.stream()
                        .map(collection -> collection.getLocalid())
                        .collect(Collectors.joining("/"));
                return registerURI + "/" + collectionsPath + "/" + item.getLocalid();
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    private void setVersionAndHistory(Item item, RegItem regItem, Integer version) throws Exception {
        String uri = item.getUri();

        List<RegItemhistory> itemHistory = itemHistoryManager.getByRegItemReference(regItem);

        if (version == null) {
            // Requested current version
            int maxVersionNumber = itemHistory.stream()
                    .mapToInt(ih -> ih.getVersionnumber())
                    .max()
                    .orElse(1); // Default to 1 if for whatever reason we can not find max version
            item.setVersion(new VersionInformation(maxVersionNumber, uri));
            item.setVersionHistory(itemHistory.stream()
                    .filter(ih -> ih.getVersionnumber() != maxVersionNumber)
                    .map(ih -> new VersionInformation(ih.getVersionnumber(), uri + ":" + ih.getVersionnumber()))
                    .collect(Collectors.toList()));
        } else {
            // Requested a specific version, check that such version exists
            if (itemHistory.isEmpty()) {
                // If for whatever reason we can not find version information for the RegItem 
                // (for example for items of type registry this might be the case) then we
                // demand that the request is for version 1
                if (version != 1) {
                    throw new NoVersionException();
                }
            } else {
                if (itemHistory.stream().noneMatch(it -> it.getVersionnumber() == version)) {
                    throw new NoVersionException();
                }
            }
            item.setVersion(new VersionInformation(version, uri + ":" + version));
            item.setVersionHistory(itemHistory.stream()
                    .filter(ih -> ih.getVersionnumber() != version)
                    .map(ih -> new VersionInformation(ih.getVersionnumber(), uri + ":" + ih.getVersionnumber()))
                    .collect(Collectors.toList()));
            // This does add a :version suffix to the max version link even if it's not necessary
            // but it still works and reduces the complexity of this code
        }
    }

    private List<RegFieldmapping> getFieldmappings(RegItemclass itemclass) throws Exception {
        if (itemclassToFieldmapping == null) {
            itemclassToFieldmapping = new HashMap<>();
        }
        String key = itemclass.getUuid();
        List<RegFieldmapping> fieldMappings = itemclassToFieldmapping.get(key);
        if (fieldMappings == null) {
            fieldMappings = fieldmappingManager.getAll(itemclass);
            itemclassToFieldmapping.put(key, fieldMappings);
        }
        return fieldMappings;
    }

    private List<LocalizedProperty> getLocalizedProperties(RegItem regItem, Predicate<RegFieldmapping> fieldmappingFilter) throws Exception {
        List<RegLocalization> localizations = localizationManager.getAll(regItem, languageCode);
        Map<String, List<RegLocalization>> localizationsByField = localizations.stream()
                .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));

        Map<String, List<RegLocalization>> localizationsByFieldML;
        if (languageCode.equals(masterLanguage)) {
            localizationsByFieldML = null;
        } else {
            localizationsByFieldML = localizationManager.getAll(regItem, masterLanguage).stream()
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
                statusLoc = statusLocalizationManager.get(regStatus, languageCode);
                lang = languageCode.getIso6391code();
            } catch (NoResultException ignore) {
                //  Try masterLanguage
                try {
                    statusLoc = statusLocalizationManager.get(regStatus, masterLanguage);
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

    private LocalizedProperty getLocalizedProperty(RegFieldmapping fieldmapping,
            RegItem regItem,
            Map<String, List<RegLocalization>> localizationsByField,
            Map<String, List<RegLocalization>> localizationsByFieldML) throws Exception {
        RegField field = fieldmapping.getRegField();
        String id = field.getLocalid();
        String lang = languageCode.getIso6391code();
        boolean istitle = field.getIstitle();
        String label = getLabelForField(field);
        int order = fieldmapping.getListorder();

        List<LocalizedPropertyValue> values = Collections.emptyList();

        //Get Property for allowing to return null value fields        
        String allowEmptyFields = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_ALLOW_NULL_FIELDS, BaseConstants.KEY_BOOLEAN_STRING_FALSE);
        String dateformat = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

        switch (field.getRegFieldtype().getUuid()) {
            case BaseConstants.KEY_FIELDTYPE_REGISTRY_UUID:
            case BaseConstants.KEY_FIELDTYPE_REGISTER_UUID:
            case BaseConstants.KEY_FIELDTYPE_COLLECTION_UUID:
                // Ignore these here
                return null;
            case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
            case BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID:
            case BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID:
            case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:

                LocalizedProperty linksToRelatedItems = getLinksToRelatedItems(field, label, order, regItem, localizationsByField, localizationsByFieldML);
                
                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItems == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItems == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order);
                } else {
                    return linksToRelatedItems;
                }

            case BaseConstants.KEY_FIELDTYPE_STATUS_UUID:
                StatusLocalization statusLocalization = getLocalizedStatus(fieldmapping.getRegStatus());
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
                
                if(regItem.getEditdate()!=null){
                    String dateEditValue = sdf.format(regItem.getEditdate());
                    values = Collections.singletonList(new LocalizedPropertyValue(dateEditValue, null));
                }
                break;
                
            default:
                String key = field.getUuid();
                List<RegLocalization> localizations = localizationsByField.get(key);
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
        return new LocalizedProperty(lang, id, istitle, label, values, order);
    }

    private LocalizedProperty getLinksToRelatedItems(RegField field,
            String label, int order, RegItem regItem,
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
            String href = getURI(relItem);
            values.add(new LocalizedPropertyValue(value, href));
        }

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order);
    }

    private String getLabelForItem(RegItem regItem) throws Exception {
        List<RegLocalization> localizations = localizationManager.getAll(getLabelField(), regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            localizations = localizationManager.getAll(getLabelField(), regItem, masterLanguage);
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
                label = localizationManager.get(field, languageCode).getValue();
            } catch (NoResultException ignore) {
                label = localizationManager.get(field, masterLanguage).getValue();
            }
            fieldToLabel.put(key, label);
        }
        return label;
    }

    private List<RegItem> getRegisters(RegItem registry) throws Exception {
        List<RegItem> registrysRegisters = new ArrayList<>();
        List<RegItem> registers = getAllRegisters();
        List<RegRelation> relations = relationManager.getAllByRegItemsSubjectAndPredicate(registers, hasRegistry);
        for (RegRelation rel : relations) {
            if (rel.getRegItemObject().equals(registry)) {
                registrysRegisters.add(rel.getRegItemSubject());
            }
        }
        return registrysRegisters;
    }

    private List<RegItem> getAllRegisters() throws Exception {
        return getItemsOfType(TYPE_REGISTER);
    }

    private List<RegItem> getItemsOfType(String classtype) throws Exception {
        List<RegItemclass> allItemClasses = classManager.getAll();
        List<RegItemclass> itemclasses = allItemClasses.stream()
                .filter(it -> it.getRegItemclasstype().getLocalid().equals(classtype))
                .collect(Collectors.toList());
        List<RegItem> list = new ArrayList<>();
        for (RegItemclass itemclass : itemclasses) {
            list.addAll(itemManager.getAll(itemclass));
        }
        return list;
    }

    private List<RegItem> getDirectlyContainedItemsOfRegister(RegItem register) throws Exception {
        return itemManager.getAllSubjectsByRegItemObjectAndPredicateAndSubjectNotPredicate(register, hasRegister, hasCollection);
    }

    private List<RegItem> getCollectionChain(RegItem regItem) throws Exception {
        RegItem collection = getRelatedItemBySubject(regItem, hasCollection);
        if (collection == null) {
            return Collections.emptyList();
        }
        LinkedList<RegItem> collectionChain = new LinkedList<>();
        while (collection != null) {
            collectionChain.addFirst(collection);
            collection = getRelatedItemBySubject(collection, hasCollection);
        }
        return collectionChain;
    }

    private RegItem getRelatedItemBySubject(RegItem regItem, RegRelationpredicate predicate) throws Exception {
        return getRelatedItemsBySubject(regItem, predicate).stream()
                .findAny()
                .orElse(null);
    }

    private List<RegItem> getRelatedItemsBySubject(RegItem regItem, RegRelationpredicate predicate) throws Exception {
        return relationManager.getAllByRegItemSubjectAndPredicate(regItem, predicate).stream()
                .map(rel -> rel.getRegItemObject())
                .collect(Collectors.toList());
    }

    private List<RegItem> getRelatedItemsByObject(RegItem regItem, RegRelationpredicate predicate) throws Exception {
        List<RegRelation> relations = relationManager.getAllByRegItemObjectAndPredicate(regItem, predicate);
        List<RegItem> subjects = new ArrayList<>();
        for (RegRelation relation : relations) {
            subjects.add(relation.getRegItemSubject());
        }
        return subjects;
    }

}
