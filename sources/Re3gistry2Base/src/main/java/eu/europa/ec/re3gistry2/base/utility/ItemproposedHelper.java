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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;

public class ItemproposedHelper {

    private ItemproposedHelper() {
    }

    public static String getProposedURI(RegItemproposed regItemproposed, RegItem regItemRegister, RegRelationpredicate regRelationpredicateCollection, RegRelationproposedManager regRelationproposedManager, RegRelationManager regRelationManager) throws Exception {
        // Creating the full URI
        String uri = regItemproposed.getLocalid();

        // URI for external items
        if (regItemproposed.getExternal()) {
            return uri;
        }

        List<RegRelationproposed> regRelationproposeds = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateCollection);
        List<RegRelation> regRelations = null;

        while (true) {

            if (regRelationproposeds == null || regRelationproposeds.isEmpty() || (regRelations != null && regRelations.isEmpty())) {
                break;
            }
            // Setting the localid of the collection
            if (regRelations == null) {
                uri = regRelationproposeds.get(0).getRegItemObject().getLocalid() + "/" + uri;
                regRelations = regRelationManager.getAll(regRelationproposeds.get(0).getRegItemObject(), regRelationpredicateCollection);
            } else {
                uri = regRelations.get(0).getRegItemObject().getLocalid() + "/" + uri;
                regRelations = regRelationManager.getAll(regRelations.get(0).getRegItemObject(), regRelationpredicateCollection);
            }

            // Checking for other collections at an upper level.
            // For proposed items only one collecion is in RegRelationproposed (the one related to the current oproposed items)
            // The eventual other upper cololectionare in the RegRelation
        }

        switch (regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_ITEM:
                uri = regItemRegister.getRegItemclass().getBaseuri() + "/" + regItemRegister.getLocalid() + "/" + uri;
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                uri = regItemproposed.getRegItemclass().getBaseuri() + "/" + uri;
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                uri = regItemproposed.getRegItemclass().getBaseuri() + "/" + uri;
                break;
            default:
                break;
        }
        return uri;
    }

    public static HashMap<String, String> getRegistryLocalizationByRegItemproposed(RegItemproposed regItemproposed, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelationproposed> regRelation = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                RegItem regItem = regItemManager.get(uuidObject);

                List<RegLocalization> registryLocalizationList;
                try {
                    registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecode);
                    if (registryLocalizationList.isEmpty()) {
                        registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecodeManager.getMasterLanguage());
                    }
                } catch (Exception ex) {
                    registryLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecodeManager.getMasterLanguage());
                }
                String label = registryLocalizationList.get(0).getValue();
                String uri = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItem.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid();

                hashMap.put(label, uri);
            }
        } catch (Exception ex) {
            return null;
        }
        return hashMap;
    }

    public static HashMap<String, String> getRegisterLocalizationByRegItemproposed(RegItemproposed regItemproposed, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelationproposed> regRelation = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                RegItem regItem = regItemManager.get(uuidObject);

                List<RegLocalization> registerLocalizationList;
                try {
                    registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecode);
                    if (registerLocalizationList.isEmpty()) {
                        registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecodeManager.getMasterLanguage());
                    }
                } catch (Exception ex) {
                    registerLocalizationList = regLocalizationManager.getAll(regFieldLabel, regItem, regLanguagecodeManager.getMasterLanguage());
                }
                String label = registerLocalizationList.get(0).getValue();
                String uri = "." + WebConstants.PAGE_URINAME_BROWSE + "?" + BaseConstants.KEY_REQUEST_ITEMUUID + "=" + regItem.getUuid() + "&" + BaseConstants.KEY_REQUEST_LANGUAGEUUID + "=" + regLanguagecode.getUuid();

                hashMap.put(label, uri);
            }
        } catch (Exception ex) {
            return null;

        }
        return hashMap;
    }

    public static String getBreadcrumbCollectionHTMLForRegItemproposed(String HTML, RegItemproposed regItemproposed, EntityManager entityManager, RegLanguagecode regLanguagecode) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        String HTMLlocal = "<li property=\"itemListElement\" typeof=\"ListItem\"><a class=\"text-primary\" property=\"item\" typeof=\"WebPage\" href=\"";
        try {
            RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            RegFieldManager regFieldManager = new RegFieldManager(entityManager);
            RegField regFieldLabel = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID);

            List<RegRelationproposed> regRelation = regRelationproposedManager.getAll(regItemproposed, regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION));
            if (!regRelation.isEmpty()) {
                final String uuidObject = regRelation.get(0).getRegItemObject().getUuid();
                RegItem regItemCollection = regItemManager.get(uuidObject);

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

                return ItemHelper.getBreadcrumbCollectionHTMLForRegitem(HTMLlocal + HTML, regItemCollection, entityManager, regLanguagecode);
            } else {
                return HTML;
            }
        } catch (Exception ex) {
            return HTML;

        }
    }

}
