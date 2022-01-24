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

import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
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

        JSONObject regItemJsonObject = new JSONObject();
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeVersions(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            JSONObject containedItemsJSON = new JSONObject();
            for (ContainedItem ci : item.getContainedItems()) {
                regItemJsonObject.put("register", writeRegisterShortVersion(containedItemsJSON, ci));
            }
            regItemJsonObject.put("registers", containedItemsJSON);
        }

        //First Employee
        JSONObject employeeObject = new JSONObject();
        employeeObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);

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

        JSONObject regItemJsonObject = new JSONObject();
        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            JSONObject containedItemsJSON = new JSONObject();
            for (ContainedItem ci : item.getContainedItems()) {
                regItemJsonObject.put("value", writeItemShortVersion(containedItemsJSON, ci));

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            regItemJsonObject.put(ci.getItemclass().getId().toLowerCase(), writeItemShortVersion(containedItemsJSON, c));
                        }
                    }
                }
            }
            regItemJsonObject.put("containeditems", containedItemsJSON);
        }

        JSONObject regiItemJSONObject = new JSONObject();
        regiItemJSONObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);

        osw.write(regiItemJSONObject.toJSONString());
        osw.flush();
        osw.close();
    }

    private void writeItem(OutputStream out, Item item) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        JSONObject regItemJsonObject = new JSONObject();
        writeVersions(regItemJsonObject, item);
        writeLanguage(regItemJsonObject);
        writeDate(regItemJsonObject, item);
        writeFields(regItemJsonObject, item);
        writeItemclass(regItemJsonObject, item);
        writeIsDefinedBy(regItemJsonObject, item);
        writeRegistryAndRegister(regItemJsonObject, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            JSONObject containedItemsJSON = new JSONObject();
            for (ContainedItem ci : item.getContainedItems()) {
                regItemJsonObject.put("value", writeItemShortVersion(containedItemsJSON, ci));

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            regItemJsonObject.put("value", writeItemShortVersion(containedItemsJSON, c));
                        }
                    }
                }
            }
            regItemJsonObject.put("containeditems", containedItemsJSON);
        }

        JSONObject regiItemJSONObject = new JSONObject();
        regiItemJSONObject.put(item.getItemclass().getId().toLowerCase(), regItemJsonObject);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        osw.write(mapper.writeValueAsString(regiItemJSONObject));
        osw.flush();
        osw.close();
    }

    private JSONObject writeItemShortVersion(JSONObject regItemJsonObject, ContainedItem item) {
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
        regItemJsonObject.put("thisversion", version.getUri() + ":" + version.getNumber());
        regItemJsonObject.put("latestversion", item.getUri());
        if (!versionHistory.isEmpty()) {
            JSONArray previousversionsArray = new JSONArray();
            JSONObject versionJson = new JSONObject();
            for (VersionInformation versionInformation : versionHistory) {
                versionJson.put("version", versionInformation.getUri() + ":" + versionInformation.getNumber());
            }
            previousversionsArray.put(versionJson);
            regItemJsonObject.put("previousversions", previousversionsArray);
        }
    }

    private void writeFields(JSONObject regItemJsonObject, ContainedItem item) {
        List<LocalizedProperty> localizedProperties = item.getProperties();
        localizedProperties.forEach((localizedProperty) -> {
            String lang = localizedProperty.getLang();
            if (!localizedProperty.getValues().isEmpty()) {
                String value = localizedProperty.getValues().get(0).getValue();
                String href = localizedProperty.getValues().get(0).getHref();

                String fieldName = localizedProperty.getLabel().replace("-item", "");
                String fieldLocalId = localizedProperty.getId();

                if (fieldName != null && "contactpoint".equals(fieldLocalId)) {
                    JSONObject json = new JSONObject();
                    json.put("label", value);
                    json.put("email", href);
                    regItemJsonObject.put(fieldName, json);
                } else if (fieldName != null && "license".equals(fieldLocalId)) {
                    JSONObject json = new JSONObject();
                    json.put("label", value);
                    json.put("uri", href);
                    regItemJsonObject.put(fieldName, json);
                } else if (fieldName != null && "governance-level".equals(fieldLocalId)) {
                    JSONObject labelJson = new JSONObject();
                    labelJson.put("lang", item.getLanguage());
                    labelJson.put("text", value);

                    JSONObject json = new JSONObject();
                    json.put("label", labelJson);
                    json.put("uri", href);

                    regItemJsonObject.put(fieldName, json);
                } else if (fieldName != null && "status".equals(fieldLocalId)) {
                    String itemClassName = item.getItemclass().getId();
                    JSONObject labelJson = new JSONObject();
                    labelJson.put("lang", item.getLanguage());
                    labelJson.put("text", value);

                    JSONObject json = new JSONObject();
                    json.put("label", labelJson);
                    json.put("id", href);

                    regItemJsonObject.put(fieldName, json);
                } else if (fieldName != null && ("annex".equals(fieldLocalId) || "themenumber".equals(fieldLocalId))) {
                    regItemJsonObject.put(fieldLocalId, value);
                } else if (!href.isEmpty() && !value.isEmpty()) {
                    JSONObject labelJson = new JSONObject();
                    labelJson.put("lang", item.getLanguage());
                    labelJson.put("text", value);

                    JSONObject json = new JSONObject();
                    json.put("label", labelJson);
                    json.put("uri", href);

                    regItemJsonObject.put(fieldName, json);
                } else {
                    JSONObject labelJson = new JSONObject();
                    labelJson.put("lang", lang);
                    labelJson.put("text", value);

                    regItemJsonObject.put(fieldLocalId, labelJson);
                }
            }
        });
    }

    private void writeItemclass(JSONObject regItemJsonObject, ContainedItem item) {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                String itemClassName = item.getItemclass().getId();
                JSONObject labelJson = new JSONObject();
                labelJson.put("lang", item.getLanguage());
                labelJson.put("text", itemClassName);

                JSONObject json = new JSONObject();
                json.put("label", labelJson);
                json.put("uriname", itemClassName);

                regItemJsonObject.put("itemclass", json);
                break;
        }
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
                        JSONObject labelJson = new JSONObject();
                        labelJson.put("lang", containedItem.getLanguage());
                        labelJson.put("text", containedItem.getLocalid());

                        JSONObject json = new JSONObject();
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
                JSONObject jsonRegistry = new JSONObject();
                jsonRegistry.put("label", writeTitle(item.getRegistry()));
                jsonRegistry.put("id", item.getRegistry().getUri());

                regItemJsonObject.put("register", jsonRegistry);
                break;
            default:
                JSONObject jsonDefaultRegistry = new JSONObject();
                jsonDefaultRegistry.put("label", writeTitle(item.getRegistry()));
                jsonDefaultRegistry.put("id", item.getRegistry().getUri());

                regItemJsonObject.put("registry", jsonDefaultRegistry);

                JSONObject jsonDefaultRegister = new JSONObject();
                jsonDefaultRegister.put("label", writeTitle(item.getRegister()));
                jsonDefaultRegister.put("id", item.getRegister().getUri());

                regItemJsonObject.put("register", jsonDefaultRegister);
                break;
        }
    }

    private JSONObject writeTitle(ItemRef item) {
        Optional<LocalizedProperty> maybeTitle = item.getProperties().stream()
                .filter(it -> "true".equals(it.getIstitle()))
                .findAny();
        if (!maybeTitle.isPresent()) {
            return new JSONObject();
        }
        LocalizedProperty title = maybeTitle.get();
        String lang = title.getLang();

        JSONObject labelRegister = new JSONObject();
        labelRegister.put("lang", lang);
        labelRegister.put("text", title.getValues().get(0).getValue());
        return labelRegister;

    }

}
