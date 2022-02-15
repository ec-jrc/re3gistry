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

import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import javax.persistence.NoResultException;

import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusgroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedPropertyValue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class StatusSupplier {

    private final RegStatusManager regStatusManager;
    private final RegItemManager regItemManager;
    private final RegLocalizationManager regLocalizationManager;
    private final RegFieldManager regFieldManager;
    private final RegItemclassManager regItemcassManager;
    private final RegStatusgroupManager regStatusgroupManager;
    private final RegStatuslocalizationManager regStatusLocalizationManager;

    private final RegLanguagecode masterLanguage;
    private final RegLanguagecode languageCode;

    public StatusSupplier(EntityManager em,
            RegLanguagecode masterLanguage,
            RegLanguagecode languageCode) throws Exception {

        this.regStatusManager = new RegStatusManager(em);
        this.regItemManager = new RegItemManager(em);
        this.regItemcassManager = new RegItemclassManager(em);
        this.regFieldManager = new RegFieldManager(em);
        this.regLocalizationManager = new RegLocalizationManager(em);
        this.regStatusgroupManager = new RegStatusgroupManager(em);
        this.regStatusLocalizationManager = new RegStatuslocalizationManager(em);

        this.masterLanguage = masterLanguage;
        this.languageCode = languageCode;
    }

    public Item getItemByUuid(String uuid) throws Exception {
        // Not possible to request specific version with uuid

        try {
            return toItem(regStatusManager.get(uuid));
        } catch (Exception ex) {
            return toItem(regStatusgroupManager.get(uuid));
        }
    }

    public Item getItemByUri(String uri) throws Exception {
        int i = uri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String localid = uri.substring(i + 1);

        try {
            RegStatus regStatus = regStatusManager.findByLocalid(localid);
            if (regStatus == null) {
                return null;
            }
            return toItem(regStatus);
        } catch (Exception ex) {
            RegStatusgroup statusgroup = regStatusgroupManager.findByLocalid(localid);
            if (statusgroup == null) {
                return null;
            }
            return toItem(statusgroup);
        }
    }

    private Item toItem(RegStatus regStatus) throws Exception {
        Item statusItem = new Item();

        setMainPropertiesForRegStatus(regStatus, statusItem);

        return statusItem;
    }

    private Item toItem(RegStatusgroup regStatusgroup) throws Exception {
        Item statusItem = new Item();

        setMainPropertiesForRegStatusGroup(regStatusgroup, statusItem);
        setContainedItems(regStatusgroup, statusItem);

        return statusItem;
    }

    private ContainedItem setMainPropertiesForRegStatus(RegStatus regStatus, Item item) throws Exception {
        item.setUuid(regStatus.getUuid());
        final RegStatusgroup statusgroup = regStatusgroupManager.get(regStatus.getRegStatusgroup().getUuid());

        String baseuri = statusgroup.getBaseuri();
        item.setUri(baseuri + "/" + statusgroup.getLocalid() + "/" + regStatus.getLocalid());
        final String localid = regStatus.getLocalid();
        item.setLocalid(localid);

        setStatusRegistry(baseuri, item);
        setStatusRegister(statusgroup, item);

        RegStatuslocalization regStatusLocalization;
        String language;
        try {
            regStatusLocalization = regStatusLocalizationManager.get(regStatus, languageCode);
            language = languageCode.getIso6391code();
        } catch (Exception ex) {
            regStatusLocalization = regStatusLocalizationManager.get(regStatus, masterLanguage);
            language = masterLanguage.getIso6391code();
        }
        setProperties(regStatusLocalization, language, localid, item);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        item.setInsertdate(df.format(item.getInsertdate()));
        item.setEditdate(df.format(item.getEditdate()));
        item.setLanguage(languageCode.getIso6391code());

        return item;
    }

    private void setStatusRegistry(String baseuri, Item item) throws Exception {
        int i = baseuri.lastIndexOf('/');
        if (i < 0) {
            throw new NoResultException();
        }
        String registrylocalid = baseuri.substring(i + 1);
        List<RegLocalization> regLocalizations;
        String groupLanguage;

        RegField labelField = regFieldManager.getByLocalid("label");
        RegItem registryItem = regItemManager.getByLocalidAndRegItemClass(registrylocalid, regItemcassManager.getByLocalid(registrylocalid));
        try {
            regLocalizations = regLocalizationManager.getAll(labelField, registryItem, languageCode);
            groupLanguage = languageCode.getIso6391code();
        } catch (Exception ex) {
            regLocalizations = regLocalizationManager.getAll(labelField, registryItem, masterLanguage);
            groupLanguage = masterLanguage.getIso6391code();
        }
        if (regLocalizations != null && !regLocalizations.isEmpty()) {
            String label = regLocalizations.get(0).getValue();

            List<LocalizedProperty> propertiesStatusRegistry = new ArrayList<>();
            List<LocalizedPropertyValue> valuesLabel = new ArrayList<>();
            LocalizedPropertyValue localizedPropertyValueLabel = new LocalizedPropertyValue(label, baseuri);
            valuesLabel.add(localizedPropertyValueLabel);
            LocalizedProperty labelLocalizedProperty = new LocalizedProperty(groupLanguage, "Label", true, label, valuesLabel, 0, true);
            propertiesStatusRegistry.add(labelLocalizedProperty);

            ItemRef statusRegistry = new ItemRef(baseuri, propertiesStatusRegistry);
            item.setRegistry(statusRegistry);
        }
    }

    private void setStatusRegister(final RegStatusgroup statusgroup, Item item) throws Exception {
        RegStatuslocalization regStatusGroupLocalization;
        String groupLanguage;
        try {
            regStatusGroupLocalization = regStatusLocalizationManager.get(statusgroup, languageCode);
            groupLanguage = languageCode.getIso6391code();
        } catch (Exception ex) {
            regStatusGroupLocalization = regStatusLocalizationManager.get(statusgroup, masterLanguage);
            groupLanguage = masterLanguage.getIso6391code();
        }
        List<LocalizedProperty> propertiesStatusRegister = new ArrayList<>();
        List<LocalizedPropertyValue> valuesLabel = new ArrayList<>();
        LocalizedPropertyValue localizedPropertyValueLabel = new LocalizedPropertyValue(regStatusGroupLocalization.getLabel(), statusgroup.getBaseuri() + "/" + statusgroup.getLocalid());
        valuesLabel.add(localizedPropertyValueLabel);
        LocalizedProperty labelLocalizedProperty = new LocalizedProperty(groupLanguage, "Label", true, regStatusGroupLocalization.getLabel(), valuesLabel, 0, true);
        propertiesStatusRegister.add(labelLocalizedProperty);

        ItemRef statusRegister = new ItemRef(statusgroup.getBaseuri() + "/" + statusgroup.getLocalid(), propertiesStatusRegister);
        item.setRegister(statusRegister);
    }

    private ContainedItem setMainPropertiesForRegStatusGroup(RegStatusgroup regStatusgroup, Item item) throws Exception {
        item.setUuid(regStatusgroup.getUuid());

        String baseuri = regStatusgroup.getBaseuri();
        final String localid = regStatusgroup.getLocalid();
        item.setUri(baseuri + "/" + localid);
        item.setLocalid(localid);

        setStatusRegistry(baseuri, item);

        RegStatuslocalization regStatusLocalization;
        String language;
        try {
            regStatusLocalization = regStatusLocalizationManager.get(regStatusgroup, languageCode);
            language = languageCode.getIso6391code();
        } catch (Exception ex) {
            regStatusLocalization = regStatusLocalizationManager.get(regStatusgroup, masterLanguage);
            language = masterLanguage.getIso6391code();
        }

        setProperties(regStatusLocalization, language, localid, item);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        item.setInsertdate(df.format(regStatusgroup.getInsertdate()));
        if (regStatusgroup.getEditdate() != null) {
            item.setEditdate(df.format(regStatusgroup.getEditdate()));
        }
        item.setLanguage(languageCode.getIso6391code());

        return item;
    }

    private void setProperties(RegStatuslocalization regStatusLocalization, String language, final String localid, Item item) {
        List<LocalizedProperty> properties = new ArrayList<>();

        List<LocalizedPropertyValue> valuesLabel = new ArrayList<>();
        LocalizedPropertyValue localizedPropertyValueLabel = new LocalizedPropertyValue(regStatusLocalization.getLabel(), null);
        valuesLabel.add(localizedPropertyValueLabel);
        LocalizedProperty labelLocalizedProperty = new LocalizedProperty(language, "Label", true, "Label", valuesLabel, 0, true);
        properties.add(labelLocalizedProperty);

        List<LocalizedPropertyValue> valuesDefinition = new ArrayList<>();
        LocalizedPropertyValue localizedPropertyValueDefinition = new LocalizedPropertyValue(regStatusLocalization.getDescription(), null);
        valuesDefinition.add(localizedPropertyValueDefinition);
        LocalizedProperty definitionLocalizedProperty = new LocalizedProperty(language, "Definition", false, "Definition", valuesDefinition, 1, true);
        properties.add(definitionLocalizedProperty);

        item.setProperties(properties);
    }

    private void setContainedItems(RegStatusgroup regStatusgroup, Item statusItem) throws Exception {
        List<RegStatus> regContainedItems = regStatusManager.getAllPublic(regStatusgroup);
        List<ContainedItem> containedItems = new ArrayList<>();
        for (RegStatus regStatus : regContainedItems) {
            containedItems.add(toItem(regStatus));
        }
        statusItem.setContainedItems(containedItems);
    }

//    private LocalizedProperty getLocalizedProperty(RegFieldmapping fieldmapping,
//            RegItem regItem,
//            Map<String, List<RegLocalization>> localizationsByField,
//            Map<String, List<RegLocalization>> localizationsByFieldML) throws Exception {
//        RegField field = fieldmapping.getRegField();
//        String id = field.getLocalid();
//        String lang = languageCode.getIso6391code();
//        boolean istitle = field.getIstitle();
//        String label = getLabelForField(field);
//        int order = fieldmapping.getListorder();
//
//        List<LocalizedPropertyValue> values = Collections.emptyList();
//
//        //Get Property for allowing to return null value fields
//        String allowEmptyFields = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_ALLOW_NULL_FIELDS, BaseConstants.KEY_BOOLEAN_STRING_FALSE);
//        String dateformat = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_DATEFORMAT, BaseConstants.KEY_STANDARD_DATEFORMAT);
//        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
//
//        String key = field.getUuid();
//        List<RegLocalization> localizations = localizationsByField.get(key);
//        values = localizations.stream()
//                .map(l -> new LocalizedPropertyValue(l.getValue(), l.getHref()))
//                .collect(Collectors.toList());
//
//        if (values.isEmpty() && !allowEmptyFields.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
//            // Don't add properties that have no zero value/href pairs
//            return null;
//        }
//        return new LocalizedProperty(lang, id, istitle, label, values, order);
//    }
}
