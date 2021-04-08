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
package eu.europa.ec.re3gistry2.restapi;

import java.util.ArrayList;
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
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.restapi.model.BasicContainedItem;
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
import java.util.LinkedList;
import javax.persistence.EntityManager;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class ItemHistorySupplier {

    private static final String TYPE_REGISTRY = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY;
    private static final String TYPE_REGISTER = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER;
    private static final String TYPE_ITEM = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;

    private final RegItemManager regItemManager;
    private final RegItemhistoryManager regItemhistoryManager;
    private final RegRelationhistoryManager regRelationhistoryManager;
    private final RegFieldManager regFieldManager;
    private final RegLocalizationhistoryManager regLocalizationhistoryManager;
    private final RegLocalizationManager regLocalizationManager;
    private final RegFieldmappingManager regFieldmappingManager;
    private final RegStatusManager regStatusManager;
    private final RegStatuslocalizationManager regStatusLocalizationManager;
    private final ItemSupplier itemSupplier;

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
    private final RegItemclassManager regItemClassManager;

    public ItemHistorySupplier(EntityManager em,
            RegLanguagecode masterLanguage,
            RegLanguagecode languageCode) throws Exception {

        this.regItemClassManager = new RegItemclassManager(em);
        this.regItemManager = new RegItemManager(em);
        this.regItemhistoryManager = new RegItemhistoryManager(em);
        RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(em);
        this.regRelationhistoryManager = new RegRelationhistoryManager(em);
        this.regFieldManager = new RegFieldManager(em);
        this.regLocalizationhistoryManager = new RegLocalizationhistoryManager(em);
        this.regLocalizationManager = new RegLocalizationManager(em);
        this.regFieldmappingManager = new RegFieldmappingManager(em);
        this.regStatusManager = new RegStatusManager(em);
        this.regStatusLocalizationManager = new RegStatuslocalizationManager(em);
        this.itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);

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

    public Item getItemHistoryByUuid(String uuid) throws Exception {
        // Not possible to request specific version with uuid
        Integer version = null;
        return toItem(regItemhistoryManager.get(uuid), version);
    }

    public Item getItemHistoryByUri(String uri, Integer version) throws Exception {
        RegItemhistory item = getRegItemByUri(uri, version);
        if (item == null) {
            return null;
        }
        return toItem(item, version);
    }

    private RegItemhistory getRegItemByUri(String uri, int version) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localidWithVersion = uri.substring(i + 1);
        int uriCollection = uri.substring(0, i).lastIndexOf('/');
        String regItemClassLocalId = uri.replace(localidWithVersion, "").substring(uriCollection + 1).replace("/", "");
        String localid = localidWithVersion.replace(":" + version, "");
        RegItemclass regItemRegItemClass = regItemClassManager.getByLocalid(regItemClassLocalId);
        try {
            RegItemclass parentRegItemRegItemClass = regItemClassManager.getChildItemclass(regItemRegItemClass).get(0);
            RegItemhistory regItem;
            try {
                regItem = regItemhistoryManager.getByLocalidVersionnumberAndRegItemClass(localid, version, parentRegItemRegItemClass);
                if (regItem != null && uri.equals(getURI(regItem) + ":" + regItem.getVersionnumber())) {
                    return regItem;
                }
            } catch (Exception ex) {
                List<RegItemhistory> regItemList = regItemhistoryManager.getByLocalidAndRegItemClass(localid, parentRegItemRegItemClass);
                if (regItemList.size() + 1 == version) {
                    return null;
                }
            }
        } catch (Exception e) {
            RegItemhistory regItem = regItemhistoryManager.getByLocalidVersionnumberAndRegItemClass(localid, version, regItemRegItemClass);
            if (regItem != null && uri.equals(getURI(regItem) + ":" + regItem.getVersionnumber())) {
                return regItem;
            }
        }
        return null;
    }

    private Integer getVersionFromUri(String uri) {
        Integer version = null;
        // Check if the part after the last slash contains a colon
        int i = uri.lastIndexOf('/');
        i = uri.indexOf(':', i + 1);
        if (i < 0) {
            version = null;
        } else {
            try {
                version = Integer.parseInt(uri.substring(i + 1));
            } catch (Exception ignore) {
                return null;
            }
        }
        return version;
    }

    private Item toItem(RegItemhistory regItemhistory, Integer version) throws Exception {
        if (!regItemhistory.getRegStatus().getIspublic()) {
            return null;
        }

        Item item = new Item();
        setMainPropertiesForRegItem(regItemhistory, item, version);

        setRegistryAndRegisterItemRef(regItemhistory, item);
        setIsDefinedByFromRegItem(regItemhistory, item);
        setContainedItemsFromRegItem(regItemhistory, item);

        setTopConceptsFromRegItem(regItemhistory, item);
        setInSchemeAndTopConceptOfFromRegItem(regItemhistory, item);
        setNarrowerFromRegItem(regItemhistory, item);
        setBroaderFromRegItem(regItemhistory, item);
        return item;
    }

    private ContainedItem setMainPropertiesForRegItem(RegItemhistory regItemhistory, ContainedItem item, Integer version) throws Exception {
        item.setUuid(regItemhistory.getUuid());
        item.setUri(getURI(regItemhistory));
        item.setLocalid(regItemhistory.getLocalid());
        item.setInsertdate(regItemhistory.getInsertdate());
        item.setEditdate(regItemhistory.getEditdate());
        setVersionAndHistory(regItemhistory, item, version);
        item.setType(regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid());
        item.setLanguage(languageCode.getIso6391code());
        item.setItemclass(new ItemClass(regItemhistory.getRegItemclass().getLocalid()));
        item.setProperties(getLocalizedProperties(regItemhistory, fieldMapping -> !fieldMapping.getHidden()));

        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
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
            List<RegItem> hasCollectionList = getRelatedItemsBySubject(regItemhistory, hasCollection);
            if (hasCollectionList != null & !hasCollectionList.isEmpty()) {
                item.setHasCollection(true);
            }
            List<RegItem> isParentList = getRelatedItemsBySubject(regItemhistory, hasParent);
            if (isParentList != null & !isParentList.isEmpty()) {
                item.setIsParent(true);
            }
            if (regItemhistory.getExternal()) {
                item.setExternal(true);
            }
        }
        return item;
    }

    private RegItem getRelatedItemByRegitemHistorySubject(RegItemhistory regItemhistory, RegRelationpredicate predicate) throws Exception {
        return getRelatedItemsBySubject(regItemhistory, predicate).stream()
                .findAny()
                .orElse(null);
    }

    private List<RegItem> getRelatedItemsBySubject(RegItemhistory regItemhistory, RegRelationpredicate predicate) throws Exception {
        return regRelationhistoryManager.getAllByRegItemHistorySubjectAndPredicate(regItemhistory, predicate)
                .stream()
                .filter((relation) -> (relation.getRegItemObject() instanceof RegItem))
                .map(rel -> rel.getRegItemObject())
                .collect(Collectors.toList());
    }

    private String getURI(RegItemhistory regItemhistory) throws Exception {
        if (regItemhistory == null) {
            return null;
        }
        RegItemclass itemclass = regItemhistory.getRegItemclass();
        switch (itemclass.getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                return itemclass.getBaseuri() + "/" + regItemhistory.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                String baseuri = itemclass.getBaseuri();
                if (baseuri != null) {
                    return baseuri + "/" + regItemhistory.getLocalid();
                }
                String registryURI = itemSupplier.getURI(getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegistry));
                return registryURI + "/" + regItemhistory.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                String itemURI = null;
                if (regItemhistory.getExternal()) {
                    itemURI = regItemhistory.getLocalid();
                } else {
                    String registerURI = itemSupplier.getURI(getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegister));
                    List<RegItem> collectionChain = getCollectionChain(regItemhistory);
                    if (collectionChain.isEmpty()) {
                        return registerURI + "/" + regItemhistory.getLocalid();
                    }
                    String collectionsPath = collectionChain.stream()
                            .map(collection -> collection.getLocalid())
                            .collect(Collectors.joining("/"));
                    itemURI = registerURI + "/" + collectionsPath + "/" + regItemhistory.getLocalid();
                }

                return itemURI;
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    private List<LocalizedProperty> getLocalizedProperties(RegItemhistory regItemhistory, Predicate<RegFieldmapping> fieldmappingFilter) throws Exception {
        List<RegLocalizationhistory> localizations = regLocalizationhistoryManager.getAll(regItemhistory, languageCode);
        Map<String, List<RegLocalizationhistory>> localizationsByField = localizations.stream()
                .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));

        Map<String, List<RegLocalizationhistory>> localizationsByFieldML;
        if (languageCode.equals(masterLanguage)) {
            localizationsByFieldML = null;
        } else {
            localizationsByFieldML = regLocalizationhistoryManager.getAll(regItemhistory, masterLanguage).stream()
                    .collect(Collectors.groupingBy(it -> it.getRegField().getUuid()));
        }

        List<RegFieldmapping> fieldmappings = getFieldmappings(regItemhistory.getRegItemclass());
        return fieldmappings.stream()
                .filter(fieldmappingFilter)
                .map(it -> {
                    try {
                        return getLocalizedProperty(it, regItemhistory, localizationsByField, localizationsByFieldML);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<RegItem> getCollectionChain(RegItemhistory regItemhistory) throws Exception {
        RegItem collection = getRelatedItemByRegitemHistorySubject(regItemhistory, hasCollection);
        if (collection == null) {
            return Collections.emptyList();
        }
        LinkedList<RegItem> collectionChain = new LinkedList<>();
        while (collection != null) {
            collectionChain.addFirst(collection);
            collection = itemSupplier.getRelatedItemBySubject(collection, hasCollection);
        }
        return collectionChain;
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

    private LocalizedProperty getLocalizedProperty(RegFieldmapping fieldmapping,
            RegItemhistory regItemhistory,
            Map<String, List<RegLocalizationhistory>> localizationsByField,
            Map<String, List<RegLocalizationhistory>> localizationsByFieldML) throws Exception {
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
                RegItem collection = getRelatedItemByRegitemHistorySubject(regItemhistory, hasCollection);
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
                RegItem parent = getRelatedItemByRegitemHistorySubject(regItemhistory, hasParent);
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
                RegItem successor = getRelatedItemByRegitemHistorySubject(regItemhistory, hasSuccessor);
                LocalizedProperty linksToRelatedItemsSuccessor = getLinksToRelationItems(field, label, order, tablevisible, successor);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsSuccessor == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsSuccessor == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsSuccessor;
                }
            case BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID:
                RegItem predecessor = getRelatedItemByRegitemHistorySubject(regItemhistory, hasPredecessor);
                LocalizedProperty linksToRelatedItemsPredecessor = getLinksToRelationItems(field, label, order, tablevisible, predecessor);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItemsPredecessor == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItemsPredecessor == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
                } else {
                    return linksToRelatedItemsPredecessor;
                }
            case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:

                LocalizedProperty linksToRelatedItems = getLinksToRelatedItems(field, label, order, tablevisible, regItemhistory, localizationsByField, localizationsByFieldML);

                // Handling the cases in which the values are null and the flag to display null values is true/false
                if (linksToRelatedItems == null && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return null;
                } else if (linksToRelatedItems == null && allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
                    return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
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

                String dateInsertValue = sdf.format(regItemhistory.getInsertdate());
                values = Collections.singletonList(new LocalizedPropertyValue(dateInsertValue, null));
                break;

            case BaseConstants.KEY_FIELDTYPE_DATEEDIT_UUID:

                if (regItemhistory.getEditdate() != null) {
                    String dateEditValue = sdf.format(regItemhistory.getEditdate());
                    values = Collections.singletonList(new LocalizedPropertyValue(dateEditValue, null));
                }
                break;

            default:
                String key = field.getUuid();
                List<RegLocalizationhistory> localizations = localizationsByField.get(key);
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
                label = regLocalizationManager.get(field, languageCode).getValue();
            } catch (NoResultException ignore) {
                label = regLocalizationManager.get(field, masterLanguage).getValue();
            }
            fieldToLabel.put(key, label);
        }
        return label;
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

    private LocalizedProperty getLinksToRelatedItems(RegField field,
            String label, int order, boolean tablevisible, RegItemhistory regItemhistory,
            Map<String, List<RegLocalizationhistory>> localizationsByField,
            Map<String, List<RegLocalizationhistory>> localizationsByFieldML) throws Exception {
        String id = field.getLocalid();
        boolean istitle = field.getIstitle();
        String lang = languageCode.getIso6391code();

        List<RegLocalizationhistory> localizations = localizationsByField.get(field.getUuid());
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
                .map(loc -> loc.getRegRelationhistoryReference().getRegItemObject())
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
            String href = getURI(regItemhistory);
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
        String href = itemSupplier.getURI(collection);
        values.add(new LocalizedPropertyValue(value, href));

        if (values.isEmpty()) {
            // Don't add properties that have no zero value/href pairs
            return null;
        }

        return new LocalizedProperty(lang, id, istitle, label, values, order, tablevisible);
    }

    private String getLabelForItem(RegItem regItem) throws Exception {
        List<RegLocalization> localizations = regLocalizationManager.getAll(getLabelField(), regItem, languageCode);
        if (localizations == null || localizations.isEmpty()) {
            // fallback to master language localization for this field for this reg item
            localizations = regLocalizationManager.getAll(getLabelField(), regItem, masterLanguage);
        }
        return localizations.stream()
                .findAny()
                .map(l -> l.getValue())
                .orElse(null);
    }

    private RegField getLabelField() throws Exception {
        if (labelField == null) {
            labelField = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);
        }
        return labelField;
    }

    private void setVersionAndHistory(RegItemhistory regItemhistory, ContainedItem item, Integer version) throws Exception {
        String uri = item.getUri();

        List<RegItemhistory> itemHistory = regItemhistoryManager.getByRegItemReference(regItemManager.getByLocalidAndRegItemClass(regItemhistory.getLocalid(), regItemhistory.getRegItemclass()));

        if (version == null) {
            // Requested current version
            int maxVersionNumber = itemHistory.stream()
                    .mapToInt(ih -> ih.getVersionnumber())
                    .max()
                    .orElse(1); // Default to 1 if for whatever reason we can not find max version
            item.setVersion(new VersionInformation(maxVersionNumber + 1, uri));
            item.setVersionHistory(itemHistory.stream()
                    .filter(ih -> ih.getVersionnumber() != maxVersionNumber + 1)
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
            } else if (itemHistory.stream().noneMatch(it -> it.getVersionnumber() == version)) {
                throw new NoVersionException();
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

    private void setRegistryAndRegisterItemRef(RegItemhistory regItemhistory, ContainedItem item) throws Exception {
        ItemRef registryRef = null;
        ItemRef registerRef = null;

        switch (item.getType()) {
            case TYPE_REGISTRY:
                registerRef = null;

                break;
            case TYPE_REGISTER:
                registryRef = itemSupplier.toItemRef(getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegistry));
                // Making an assumption that register can not be parent or collection to any item
                break;
            case TYPE_ITEM:
                registryRef = itemSupplier.toItemRef(getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegistry));
                registerRef = itemSupplier.toItemRef(getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegister));

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

    private void setContainedItemsFromRegItem(RegItemhistory regItemhistory, ContainedItem item) throws Exception {
        List<ContainedItem> containedItems = new ArrayList<>();
        List<RegItem> containedItemsList = null;
        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                containedItemsList = getRegisters(regItemhistory);

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(itemSupplier.toContainedItem(containedItem));
                        }
                    }
                    if (!containedItems.isEmpty()) {
                        item.setContainedItems(containedItems);
                    }
                }
                break;
            case TYPE_REGISTER:
                containedItemsList = getDirectlyContainedItemsOfRegister(regItemhistory);

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(itemSupplier.toContainedItem(containedItem));
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
                    List<String> collectionNoParentList = getAllColectionsNoParentOfItem(regItemhistory);
                    for (String uuid : collectionNoParentList) {
                        containedItemsList.add(regItemManager.get(uuid));
                    }
                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        containedItemsList = getRelatedItemsByObject(regItemhistory, hasCollection);
                    }
                    if (containedItemsList == null || containedItemsList.isEmpty()) {
                        containedItemsList = getRelatedItemsByObject(regItemhistory, hasParent);
                    }
//                    }
                    for (RegItem childItem : containedItemsList) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(itemSupplier.toBasicContainedItem(childItem));
                        }
                    }
                } catch (Exception exception) {
                    for (RegItem childItem : getRelatedItemsByObject(regItemhistory, hasParent)) {
                        if (!childItem.getRegItemclass().getSystemitem()) {
                            topConcepts.add(itemSupplier.toBasicContainedItem(childItem));
                        }
                    }
                }
                if (!topConcepts.isEmpty()) {
                    item.setTopConcepts(topConcepts);
                }

                if (containedItemsList != null && !containedItemsList.isEmpty()) {
                    for (RegItem containedItem : containedItemsList) {

                        if (!containedItem.getRegItemclass().getSystemitem()) {
                            containedItems.add(itemSupplier.toContainedItem(containedItem));
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

    private void setInSchemeAndTopConceptOfFromRegItem(RegItemhistory regItemhistory, ContainedItem containedItem) throws Exception {

        BasicContainedItem basicContainedItem = null;
        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:
                RegItem relatedRegistry = getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegistry);
                basicContainedItem = itemSupplier.toBasicContainedItem(relatedRegistry);
                break;
            case TYPE_ITEM:
                RegItem relatedCollection = getRelatedItemByRegitemHistorySubject(regItemhistory, hasCollection);
                if (relatedCollection != null) {
                    RegItem parentCollection = itemSupplier.getRelatedItemBySubject(relatedCollection, hasParent);
                    if (parentCollection != null) {
                        relatedCollection = parentCollection;
                    }
                }
                if (relatedCollection == null) {
                    relatedCollection = getRelatedItemByRegitemHistorySubject(regItemhistory, hasRegister);
                }
                basicContainedItem = itemSupplier.toBasicContainedItem(relatedCollection);
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }
        if (basicContainedItem != null && basicContainedItem.getUri() != null) {
            containedItem.setInScheme(basicContainedItem);
            containedItem.setTopConceptOf(basicContainedItem);
        }
    }

    private void setBroaderFromRegItem(RegItemhistory regItemhistory, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> broader = new ArrayList<>();
        List<RegItem> broaderList = null;

        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                broaderList = getRelatedItemsBySubject(regItemhistory, hasParent);
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = getRelatedItemsBySubject(regItemhistory, hasCollection);
                }
                if ((broaderList == null || broaderList.isEmpty())) {
                    broaderList = getRelatedItemsBySubject(regItemhistory, hasRegister);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        if (broaderList != null && !broaderList.isEmpty()) {
            for (RegItem childItem : broaderList) {
                if (!childItem.getRegItemclass().getSystemitem()) {
                    broader.add(itemSupplier.toBasicContainedItem(childItem));
                }
            }
            if (!broader.isEmpty()) {
                containedItem.setBroader(broader);
            }
        }
    }

    private void setNarrowerFromRegItem(RegItemhistory regItemhistory, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> narrower = new ArrayList<>();
        List<RegItem> narrowerList = null;
        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:
                narrowerList = getDirectlyContainedItemsOfRegister(regItemhistory);
                break;
            case TYPE_ITEM:
                narrowerList = getRelatedItemsByObject(regItemhistory, hasParent);
                if (narrowerList == null || narrowerList.isEmpty()) {
                    narrowerList = getRelatedItemsByObject(regItemhistory, hasCollection);
                }
                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        if (narrowerList != null && !narrowerList.isEmpty()) {
            for (RegItem childItem : narrowerList) {
                if (!childItem.getRegItemclass().getSystemitem()) {
                    narrower.add(itemSupplier.toContainedItemWithoutItems(childItem));
                }
            }
            if (!narrower.isEmpty()) {
                containedItem.setNarrower(narrower);
            }
        }
    }

    private void setTopConceptsFromRegItem(RegItemhistory regItemhistory, ContainedItem containedItem) throws Exception {
        List<BasicContainedItem> topConcepts = new ArrayList<>();
        List<RegItem> childItemList = new ArrayList<>();

        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:
                childItemList = getRegisters(regItemhistory);

                break;
            case TYPE_REGISTER:
                childItemList = getDirectlyContainedItemsOfRegister(regItemhistory);

                break;
            case TYPE_ITEM:
                List<String> collectionNoParentList = getAllColectionsNoParentOfItem(regItemhistory);
                for (String uuid : collectionNoParentList) {
                    childItemList.add(regItemManager.get(uuid));
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = getRelatedItemsByObject(regItemhistory, hasCollection);
                }
                if (childItemList == null || childItemList.isEmpty()) {
                    childItemList = getRelatedItemsByObject(regItemhistory, hasParent);
                }

                break;
            default:
                throw new RuntimeException("Unexpected type");
        }

        for (RegItem childItem : childItemList) {
            if (!childItem.getRegItemclass().getSystemitem()) {
                topConcepts.add(itemSupplier.toBasicContainedItem(childItem));
            }
        }

        if (!topConcepts.isEmpty()) {
            containedItem.setTopConcepts(topConcepts);
        }
    }

    private void setIsDefinedByFromRegItem(RegItemhistory regItemhistory, ContainedItem containedItem) throws Exception {
        List<ContainedItem> isDefinedBy = new ArrayList<>();

        switch (regItemhistory.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case TYPE_REGISTRY:

                break;
            case TYPE_REGISTER:

                break;
            case TYPE_ITEM:
                for (RegItem relatedItem : getRelatedItemsBySubject(regItemhistory, hasReference)) {
                    if (!relatedItem.getRegItemclass().getSystemitem()) {
                        isDefinedBy.add(itemSupplier.toBasicContainedItemDefinedBy(relatedItem));
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

    private List<RegItem> getRegisters(RegItemhistory registry) throws Exception {
        return getRelatedItemsByObject(registry, hasRegistry);
    }

    private List<RegItem> getDirectlyContainedItemsOfRegister(RegItemhistory register) throws Exception {
        return regItemhistoryManager.getAllSubjectsByRegItemObjectAndPredicateAndSubjectNotPredicate(register, hasRegister, hasCollection);
    }

    private List<String> getAllColectionsNoParentOfItem(RegItemhistory regItemhistory) throws Exception {
        return regItemhistoryManager.getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(regItemhistory, regStatusManager.get("1"), hasCollection, hasParent);
    }

    private List<RegItem> getRelatedItemsByObject(RegItemhistory regItemhistory, RegRelationpredicate predicate) throws Exception {
        List<RegRelationhistory> relations = regRelationhistoryManager.getAllByRegItemHistoryObjectAndPredicate(regItemhistory, predicate);
        List<RegItem> subjects = new ArrayList<>();
        relations.forEach((relation) -> {
            subjects.add(relation.getRegItemSubject());
        });
        return subjects;
    }

}
