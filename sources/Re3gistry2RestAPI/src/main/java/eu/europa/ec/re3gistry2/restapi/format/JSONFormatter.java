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
package eu.europa.ec.re3gistry2.restapi.format;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.OutputStream;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;

import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONFormatter implements Formatter {

    private RegLanguagecode languageFile;

    @Override
    public String getFormatName() {
        return "json";
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void write(Item item, RegLanguagecode language, OutputStream out) throws Exception {
        this.languageFile = language;

        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                writeRegistry(out, item);
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeRegister(out, item);
                break;
            default:
                writeItem(out, item);
                break;
        }
    }

    private void writeRegistry(OutputStream out, Item item) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        JSONObject regItemJsonObject = createOrderedJSONObject();
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeVersions(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            JSONArray containedArray = new JSONArray();
            for (ContainedItem ci : item.getContainedItems()) {
                JSONObject containedItemsJSON = createOrderedJSONObject();
                containedArray.add(writeRegisterShortVersion(containedItemsJSON, ci));
            }
            regItemJsonObject.put("registers", containedArray);
        }

        JSONObject employeeObject = createOrderedJSONObject();
//        employeeObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);
        employeeObject.put("registry", regItemJsonObject);

        osw.write(employeeObject.toJSONString());
        osw.flush();
        osw.close();
    }

    private JSONObject writeRegisterShortVersion(JSONObject regItemJsonObject, ContainedItem item) {
        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);
        return regItemJsonObject;
    }

    private void writeRegister(OutputStream out, Item item) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        JSONObject regItemJsonObject = createOrderedJSONObject();
        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);

        JSONArray containedJSONArray = new JSONArray();
        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            JSONObject containedItemsJSON = createOrderedJSONObject();
            for (ContainedItem ci : item.getContainedItems()) {
                JSONObject itemJSON = createOrderedJSONObject();
                JSONObject valueJSON = createOrderedJSONObject();
                itemJSON.put("value", writeItemShortVersion(valueJSON, ci, ci));
                containedJSONArray.add(itemJSON);

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            JSONObject containedJSON = createOrderedJSONObject();
                            JSONObject containedvalueJSON = createOrderedJSONObject();
                            containedJSON.put(ci.getItemclass().getId().toLowerCase(), writeItemShortVersion(containedvalueJSON, c, ci));
                            containedJSONArray.add(containedJSON);
                        }
                    }
                }
            }
            regItemJsonObject.put("containeditems", containedJSONArray);
        }

        JSONObject regiItemJSONObject = createOrderedJSONObject();
//        regiItemJSONObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);
        regiItemJSONObject.put("register", regItemJsonObject);

        osw.write(regiItemJSONObject.toJSONString());
        osw.flush();
        osw.close();
    }

    private void writeItem(OutputStream out, Item item) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        JSONObject regItemJsonObject = createOrderedJSONObject();
        try {
            Field changeMap = regItemJsonObject.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(regItemJsonObject, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }

        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);

        JSONArray containedItemsJSONArray = new JSONArray();
        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                JSONObject containedItemsJSON = createOrderedJSONObject();
                JSONObject valuecontainedItemsJSON = createOrderedJSONObject();
                if (item.getItemclass().getParentItemClassType().equals("register")
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    containedItemsJSON.put(item.getItemclass().getParentid(), writeItemShortVersion(valuecontainedItemsJSON, ci, item));
                } else {
                    containedItemsJSON.put("value", writeItemShortVersion(valuecontainedItemsJSON, ci, item));
                }
                containedItemsJSONArray.add(containedItemsJSON);

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            JSONObject containedCollectionItemsJSON = createOrderedJSONObject();
                            JSONObject valuecontainedCollectionItemsJSON = createOrderedJSONObject();
                            containedCollectionItemsJSON.put("value", writeItemShortVersion(valuecontainedCollectionItemsJSON, c, ci));
                            containedItemsJSONArray.add(containedCollectionItemsJSON);
                        }
                    } else if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            JSONObject containedCollectionItemsJSON = createOrderedJSONObject();
                            JSONObject valuecontainedCollectionItemsJSON = createOrderedJSONObject();
                            containedCollectionItemsJSON.put("value", writeItemShortVersion(valuecontainedCollectionItemsJSON, c, ci));
                            containedItemsJSONArray.add(containedCollectionItemsJSON);
                        }
                    }
                }
            }
        } else if (item.getContainedItemsBeeingParentItemClass() != null && !item.getContainedItemsBeeingParentItemClass().isEmpty()) {
            for (ContainedItem ci : item.getContainedItemsBeeingParentItemClass()) {
                JSONObject containedItemsJSON = createOrderedJSONObject();
                JSONObject valuecontainedItemsJSON = createOrderedJSONObject();
                if (item.getItemclass().getParentItemClassType().equals("register")
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    containedItemsJSON.put(item.getItemclass().getParentid(), writeItemShortVersion(valuecontainedItemsJSON, ci, item));
                } else {
                    containedItemsJSON.put("value", writeItemShortVersion(valuecontainedItemsJSON, ci, item));
                }
                containedItemsJSONArray.add(containedItemsJSON);

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            JSONObject containedCollectionItemsJSON = createOrderedJSONObject();
                            JSONObject valuecontainedCollectionItemsJSON = createOrderedJSONObject();
                            containedCollectionItemsJSON.put("value", writeItemShortVersion(valuecontainedCollectionItemsJSON, c, ci));
                            containedItemsJSONArray.add(containedCollectionItemsJSON);
                        }
                    } else if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            JSONObject containedCollectionItemsJSON = createOrderedJSONObject();
                            JSONObject valuecontainedCollectionItemsJSON = createOrderedJSONObject();
                            containedCollectionItemsJSON.put("value", writeItemShortVersion(valuecontainedCollectionItemsJSON, c, ci));
                            containedItemsJSONArray.add(containedCollectionItemsJSON);
                        }
                    }
                }
            }
        }
        if (containedItemsJSONArray != null && !containedItemsJSONArray.isEmpty()) {
            regItemJsonObject.put("containeditems", containedItemsJSONArray);
        }

        JSONObject regiItemJSONObject = createOrderedJSONObject();

//            regiItemJSONObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);
        if (item.getItemclass().getParentItemClassType().equals("register")) {
            regiItemJSONObject.put(item.getItemclass().getParentid(), regItemJsonObject);
        } else {
            regiItemJSONObject.put("value", regItemJsonObject);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        osw.write(mapper.writeValueAsString(regiItemJSONObject));
        osw.flush();
        osw.close();
    }

    private JSONObject writeItemShortVersion(JSONObject regItemJsonObject, ContainedItem item, ContainedItem collectionItem) {
        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);
        return regItemJsonObject;
    }

    private void writeLanguage(JSONObject regItemJsonObject) {
        regItemJsonObject.put("language", languageFile.getIso6391code());
    }

    private void writeDate(JSONObject regItemJsonObject, ContainedItem item) {
        regItemJsonObject.put("created", item.getInsertdate());

        if (item.getEditdate() != null) {
            regItemJsonObject.put("issued", item.getEditdate());
        }
    }

    private void writeVersions(JSONObject regItemJsonObject, ContainedItem item) {
        List<VersionInformation> versionHistory = item.getVersionHistory();
        VersionInformation version = item.getVersion();

        regItemJsonObject.put("id", item.getUri());
        regItemJsonObject.put("thisversion", version.getUri());
//        regItemJsonObject.put("thisversion", version.getUri() + ":" + version.getNumber());
        regItemJsonObject.put("latestversion", item.getUri());
        if (!versionHistory.isEmpty()) {
            JSONArray historyversionArray = new JSONArray();
            for (VersionInformation versionInformation : versionHistory) {
                JSONObject versionJson = createOrderedJSONObject();
                versionJson.put("version", versionInformation.getUri());
//                versionJson.put("version", versionInformation.getUri() + ":" + versionInformation.getNumber());
                historyversionArray.add(versionJson);
            }
            regItemJsonObject.put("historyversion", historyversionArray);
        }
    }

    private void writeFields(JSONObject regItemJsonObject, ContainedItem item) {
        // Get configuration properties
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String legacyFlag = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_LEGACY_FLAG);

        List<LocalizedProperty> localizedProperties = item.getProperties();
        localizedProperties.forEach((localizedProperty) -> {
            String lang = localizedProperty.getLang();
            if (!localizedProperty.getValues().isEmpty()) {
                String value = localizedProperty.getValues().get(0).getValue();
                String href = localizedProperty.getValues().get(0).getHref();

                String fieldName = localizedProperty.getLabel().replace("-item", "").toLowerCase();
                String fieldLocalId = localizedProperty.getId().replace("-item", "").toLowerCase();

                if (fieldName != null && "successor".equals(fieldLocalId.toLowerCase())) {
                    JSONArray successorsArray = writeComplexListElement(value, href, lang, fieldLocalId);
                    regItemJsonObject.put("successors", successorsArray);
                } else if (fieldName != null && "predecessor".equals(fieldLocalId.toLowerCase())) {
                    JSONArray predecessorsArray = writeComplexListElement(value, href, lang, fieldLocalId);
                    regItemJsonObject.put("predecessors", predecessorsArray);
                } else if (fieldName != null && "parent".equals(fieldLocalId.toLowerCase())) {
                    JSONArray successorsArray = writeComplexListElement(value, href, lang, fieldLocalId);
                    regItemJsonObject.put("parents", successorsArray);
                } else if (!legacyFlag.equals(BaseConstants.KEY_APPLICATION_LEGACY_FLAG_ON)) {
                    if (!href.isEmpty() && !value.isEmpty()) {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", item.getLanguage());
                        labelJson.put("text", value);

                        JSONObject json = createOrderedJSONObject();
                        json.put("label", labelJson);
                        json.put("id", href);

                        regItemJsonObject.put(fieldName, json);
                    } else {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", lang);
                        labelJson.put("text", value);

                        regItemJsonObject.put(fieldLocalId, labelJson);
                    }
                } else {
                    if (fieldName != null && "contactpoint".equals(fieldLocalId.toLowerCase())) {
                        JSONObject json = createOrderedJSONObject();
                        json.put("label", value);
                        json.put("email", href);
                        regItemJsonObject.put(fieldName, json);
                    } else if (fieldName != null && "license".equals(fieldLocalId.toLowerCase())) {
                        JSONObject json = createOrderedJSONObject();
                        json.put("label", value);
                        json.put("uri", href);
                        regItemJsonObject.put(fieldName, json);
                    } else if (fieldName != null && "governance-level".equals(fieldLocalId.toLowerCase())) {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", item.getLanguage());
                        labelJson.put("text", value);

                        JSONObject json = createOrderedJSONObject();
                        json.put("label", labelJson);
                        json.put("id", href);

                        regItemJsonObject.put(fieldName, json);
                    } else if (fieldName != null && "collection".equals(fieldLocalId.toLowerCase())) {
                        JSONObject json = createOrderedJSONObject();
                        json.put("label", value);
                        json.put("id", href);
                        String registerUri = item.getRegister().getUri();
                        int index = registerUri.lastIndexOf("/");
                        String registerid = registerUri.substring(index + 1);
                        regItemJsonObject.put(registerid, json);
//                        regItemJsonObject.put(item.getItemclass().getParentid(), json);
                    } else if (fieldName != null && "status".equals(fieldLocalId.toLowerCase())) {
                        String itemClassName = item.getItemclass().getId();
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", item.getLanguage());
                        labelJson.put("text", value);

                        JSONObject json = createOrderedJSONObject();
                        json.put("label", labelJson);
                        json.put("id", href);

                        regItemJsonObject.put(fieldName, json);
                    } else if (fieldName != null && ("annex".equals(fieldLocalId.toLowerCase()) || "themenumber".equals(fieldLocalId.toLowerCase()))) {
                        regItemJsonObject.put(fieldLocalId, value);
                    } else if (!href.isEmpty() && !value.isEmpty()) {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", item.getLanguage());
                        labelJson.put("text", value);

                        JSONObject json = createOrderedJSONObject();
                        json.put("label", labelJson);
                        json.put("id", href);

                        regItemJsonObject.put(fieldName, json);
                    } else {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", lang);
                        labelJson.put("text", value);

                        regItemJsonObject.put(fieldLocalId, labelJson);
                    }
                }
            }
        });
    }

    private JSONArray writeComplexListElement(String value, String href, String lang, String name) {
        JSONObject labelJson = createOrderedJSONObject();
        labelJson.put("lang", lang);
        labelJson.put("text", value);

        JSONObject successor = createOrderedJSONObject();
        successor.put("label", labelJson);
        successor.put("id", href);

        JSONObject json = createOrderedJSONObject();
        json.put(name, successor);

        JSONArray successorsArray = new JSONArray();
        successorsArray.add(json);

        return successorsArray;
    }

    private void writeItemclass(JSONObject regItemJsonObject, ContainedItem item) {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                regItemJsonObject.put("itemclass", getItemClassJsonFromItem(item));
                break;
            default:
                regItemJsonObject.put("itemclass", getItemClassJsonFromItem(item));
                break;
        }
    }

    private JSONObject getItemClassJsonFromItem(ContainedItem item) {
        String itemClassName = item.getItemclass().getId();
        JSONObject labelJson = createOrderedJSONObject();
        labelJson.put("lang", item.getLanguage());
        labelJson.put("text", itemClassName);
        JSONObject json = createOrderedJSONObject();
        json.put("label", labelJson);
        json.put("id", itemClassName);
        return json;
    }

    private void writeIsDefinedBy(JSONObject regItemJsonObject, ContainedItem item) {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                List<ContainedItem> referenceList = item.getIsDefinedBy();
                if (referenceList != null) {
                    referenceList.forEach((containedItem) -> {
                        JSONObject labelJson = createOrderedJSONObject();
                        labelJson.put("lang", containedItem.getLanguage());
                        labelJson.put("text", containedItem.getLocalid());

                        JSONObject json = createOrderedJSONObject();
                        json.put("label", labelJson);
                        json.put("uri", containedItem.getUri());

                        regItemJsonObject.put(containedItem.getItemclass().getId().toLowerCase().replace("-item", ""), json);
                    });
                }
                break;
        }
    }

    private void writeRegistryAndRegister(JSONObject regItemJsonObject, ContainedItem item) {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                JSONObject jsonRegistry = createOrderedJSONObject();
                jsonRegistry.put("label", writeTitle(item.getRegistry()));
                jsonRegistry.put("id", item.getRegistry().getUri());

                regItemJsonObject.put("registry", jsonRegistry);
                break;
            default:

                JSONObject jsonDefaultRegister = createOrderedJSONObject();
                jsonDefaultRegister.put("label", writeTitle(item.getRegister()));
                jsonDefaultRegister.put("id", item.getRegister().getUri());

                JSONObject jsonDefaultRegistry = createOrderedJSONObject();
                jsonDefaultRegistry.put("label", writeTitle(item.getRegistry()));
                jsonDefaultRegistry.put("id", item.getRegistry().getUri());
                jsonDefaultRegister.put("registry", jsonDefaultRegistry);

                regItemJsonObject.put("register", jsonDefaultRegister);
                break;
        }
    }

    private JSONObject writeTitle(ItemRef item) {
        Optional<LocalizedProperty> maybeTitle = item.getProperties().stream()
                .filter(it -> "true".equals(it.getIstitle()))
                .findAny();
        if (!maybeTitle.isPresent()) {
            return createOrderedJSONObject();
        }
        LocalizedProperty title = maybeTitle.get();
        String lang = title.getLang();

        JSONObject labelRegister = createOrderedJSONObject();
        labelRegister.put("lang", lang);
        labelRegister.put("text", title.getValues().get(0).getValue());
        return labelRegister;

    }

    public JSONObject createOrderedJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            Field changeMap = jsonObject.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(jsonObject, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
        return jsonObject;
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
