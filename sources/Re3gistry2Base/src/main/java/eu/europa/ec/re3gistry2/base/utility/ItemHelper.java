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
package eu.europa.ec.re3gistry2.base.utility;

import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

public class ItemHelper {

    private ItemHelper() {
    }

    public static String getURI(RegItem regItem, RegItem regItemRegistry, RegItem regItemRegister, RegRelationpredicate regRelationpredicateCollection, RegRelationManager regRelationManager) throws Exception {

        String uri = regItem.getLocalid();

        // URI for external items
        if (regItem.getExternal()) {
            return uri;
        }

        // Creating the full URI for normal items       
        List<RegRelation> regRelations = regRelationManager.getAll(regItem, regRelationpredicateCollection);

        while (true) {

            if (regRelations == null || regRelations.isEmpty()) {
                break;
            }
            // Setting the localid of the collection
            uri = regRelations.get(0).getRegItemObject().getLocalid() + "/" + uri;

            // Checking for other collections at an upper level.
            regRelations = regRelationManager.getAll(regRelations.get(0).getRegItemObject(), regRelationpredicateCollection);

        }

        switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                uri = regItemRegister.getRegItemclass().getBaseuri() + "/" + regItemRegister.getLocalid() + "/" + uri;
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                uri = regItem.getRegItemclass().getBaseuri() + "/" + uri;
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                uri = regItem.getRegItemclass().getBaseuri() + "/" + uri;
                break;
            default:
                break;
        }
        return uri;
    }

    public static HashMap<String, String> getRegistryLocalizationByRegItem(RegItem regItem, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelation> regRelation = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                RegItem regItemRegistry = regItemManager.get(uuidObject);

                List<RegLocalization> registryLocalizationList;
                try {
                    registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegistry, regLanguagecode);
                    if (registryLocalizationList.isEmpty()) {
                        registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegistry, regLanguagecodeManager.getMasterLanguage());
                    }
                } catch (Exception ex) {
                    registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegistry, regLanguagecodeManager.getMasterLanguage());
                }
                String label = registryLocalizationList.get(0).getValue();
                String uri = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemRegistry.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid();

                hashMap.put(label, uri);
            }
        } catch (Exception ex) {
            return null;

        }
        return hashMap;
    }

    public static HashMap<String, String> getRegisterLocalizationByRegItem(RegItem regItem, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelation> regRelation = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                RegItem regItemRegister = regItemManager.get(uuidObject);

                List<RegLocalization> registerLocalizationList;
                try {
                    registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegister, regLanguagecode);
                    if (registerLocalizationList.isEmpty()) {
                        registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegister, regLanguagecodeManager.getMasterLanguage());
                    }
                } catch (Exception ex) {
                    registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemRegister, regLanguagecodeManager.getMasterLanguage());
                }
                String label = registerLocalizationList.get(0).getValue();
                String uri = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemRegister.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid();
                hashMap.put(label, uri);
            }
        } catch (Exception ex) {
            return null;

        }
        return hashMap;
    }

    public static String getBreadcrumbCollectionHTMLForRegitem(String HTML, RegItem regItem, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        String HTMLlocal = "<li property=\"itemListElement\" typeof=\"ListItem\"><a class=\"text-primary\" property=\"item\" typeof=\"WebPage\" href=\"";
        RegItem regItemCollection = null;
        try {
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelation> regRelation = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                regItemCollection = regItemManager.get(uuidObject);

                List<RegLocalization> collectionLocalizationList;
                try {
                    collectionLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemCollection, regLanguagecode);
                    if (collectionLocalizationList.isEmpty()) {
                        collectionLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemCollection, regLanguagecodeManager.getMasterLanguage());
                    }
                } catch (Exception ex) {
                    collectionLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItemCollection, regLanguagecodeManager.getMasterLanguage());
                }
                String label = collectionLocalizationList.get(0).getValue();
                String uri = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItemCollection.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid();

                HTMLlocal += uri + "\"><span property=\"name\">" + label + "</span></a></li>";

                return getBreadcrumbCollectionHTMLForRegitem(HTMLlocal + HTML, regItemCollection, entityManager, regLanguagecode);
            } else {
                return HTML;
            }
        } catch (Exception ex) {
            return HTML;

        }
    }

    public static String getURI(RegItem regItem) throws Exception {
        // Getting the DB manager
        EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

        RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationpredicate hasRegistry = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate hasRegister = relationPredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);

        if (regItem == null) {
            return null;
        }
        RegItemclass itemclass = regItem.getRegItemclass();
        switch (itemclass.getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                return itemclass.getBaseuri() + "/" + regItem.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                String baseuri = itemclass.getBaseuri();
                if (baseuri != null) {
                    return baseuri + "/" + regItem.getLocalid();
                }
                String registryURI = getURI(getRelatedItemBySubject(regItem, hasRegistry, entityManager));
                return registryURI + "/" + regItem.getLocalid();
            case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                String itemURI = null;
                if (regItem.getExternal()) {
                    itemURI = regItem.getLocalid();
                } else {
                    String registerURI = getURI(getRelatedItemBySubject(regItem, hasRegister, entityManager));
                    List<RegItem> collectionChain = getCollectionChain(regItem, entityManager);
                    if (collectionChain.isEmpty()) {
                        return registerURI + "/" + regItem.getLocalid();
                    }
                    String collectionsPath = collectionChain.stream()
                            .map(collection -> collection.getLocalid())
                            .collect(Collectors.joining("/"));
                    itemURI = registerURI + "/" + collectionsPath + "/" + regItem.getLocalid();
                }

                return itemURI;
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    private static List<RegItem> getCollectionChain(RegItem regItem, EntityManager entityManager) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationpredicate hasCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
        RegItem collection = getRelatedItemBySubject(regItem, hasCollection, entityManager);
        if (collection == null) {
            return Collections.emptyList();
        }
        LinkedList<RegItem> collectionChain = new LinkedList<>();
        while (collection != null) {
            collectionChain.addFirst(collection);
            collection = getRelatedItemBySubject(collection, hasCollection, entityManager);
        }
        return collectionChain;
    }

    protected static RegItem getRelatedItemBySubject(RegItem regItem, RegRelationpredicate predicate, EntityManager entityManager) throws Exception {
        List<RegItem> list = getRelatedItemsBySubject(regItem, predicate, entityManager);
        if (list != null && !list.isEmpty()) {
            return list.stream().findAny().orElse(null);
        }
        return null;
//        return getRelatedItemsBySubject(regItem, predicate).stream()
//                .findAny()
//                .orElse(null);
    }

    private static List<RegItem> getRelatedItemsBySubject(RegItem regItem, RegRelationpredicate predicate, EntityManager entityManager) throws Exception {
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        if (regRelationManager != null && regItem != null && predicate != null
                && regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, predicate) != null) {
            return regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, predicate).stream()
                    .map(rel -> rel.getRegItemObject())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

}
