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

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicContainedItem;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedPropertyValue;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLFormatter implements Formatter {

    private static final String XSISCHEMALOCATION = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    private RegLanguagecode languageFile;

    @Override
    public String getFormatName() {
        return "xml";
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        this.languageFile = lang;

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

    private XMLStreamWriter getXMLWriter(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = XMLFactory.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, XMLFactory.NEW_LINE, XMLFactory.INDENT);

        xml.writeStartDocument();

        // Get configuration properties
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String schemaPath = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_SCHEMA_PATH) + "/";

        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                xml.writeStartElement("registry");
                xml.writeDefaultNamespace(item.getUri());

                String schemaLocationRegistry = item.getUri() + " " + item.getUri() + schemaPath + "registry.xsd";
                xml.writeAttribute("xsi", XSISCHEMALOCATION, "schemaLocation", schemaLocationRegistry);

                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                xml.writeStartElement("register");
                xml.writeDefaultNamespace(item.getUri() + "_register");

                String schemaLocationRegister = item.getUri() + "_register " + item.getRegistry().getUri() + schemaPath + item.getItemclass().getId() + "_register.xsd";
                xml.writeAttribute("xsi", XSISCHEMALOCATION, "schemaLocation", schemaLocationRegister);

                break;
            default:
                String itemclassLocalId = item.getItemclass().getId();
                String startElement;

                if (item.getItemclass().getParentItemClassType().equals("register")) {
                    startElement = item.getItemclass().getParentid();
                } else {
                    startElement = "value";
                }
                xml.writeStartElement(startElement);
                xml.writeDefaultNamespace(item.getRegister().getUri() + "_register/" + itemclassLocalId);

                String schemaLocationItem = item.getRegister().getUri() + "_register/" + itemclassLocalId + " " + item.getRegistry().getUri() + schemaPath + itemclassLocalId + ".xsd";
                xml.writeAttribute("xsi", XSISCHEMALOCATION, "schemaLocation", schemaLocationItem);

                break;
        }

        xml.writeAttribute("xmlns", XSISCHEMALOCATION, "xsi", XSISCHEMALOCATION);
        xml.writeAttribute("id", item.getUri());

        return xml;
    }

    private void writeRegistry(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, item);

        writeLanguage(xml);
        writeDate(xml, item);
//        writeVersions(xml, item);
        writeFields(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            xml.writeStartElement("registers");
            for (ContainedItem ci : item.getContainedItems()) {
                writeRegisterShortVersion(xml, ci);
            }
            xml.writeEndElement();
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeRegisterShortVersion(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("register");
        xml.writeAttribute("id", item.getUri());

        writeLanguage(xml);
        writeDate(xml, item);
        writeFields(xml, item);
        writeRegistryAndRegister(xml, item);

        xml.writeEndElement();
    }

    private void writeRegister(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, item);

        writeLanguage(xml);
        writeDate(xml, item);
        writeFields(xml, item);
        writeRegistryAndRegister(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            xml.writeStartElement("containeditems");
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci, item.getItemclass().getId().toLowerCase(), ci);
            }
            xml.writeEndElement();
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeItemShortVersion(XMLStreamWriter xml, ContainedItem item, String mainElementName, ContainedItem collectionItem) throws XMLStreamException {
        xml.writeStartElement(mainElementName);
        xml.writeAttribute("id", item.getUri());
//        xml.writeAttribute(NS_XML, "id", item.getUri());

        writeLanguage(xml);
        writeDate(xml, item);
        writeVersions(xml, item);
        writeFields(xml, item);
        writeItemclass(xml, item);
//        writeIsDefinedBy(xml, item);
        writeRegistryAndRegister(xml, item);

        xml.writeEndElement();
    }

    private void writeItem(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, item);
        writeLanguage(xml);
        writeDate(xml, item);
        writeVersions(xml, item);

        writeFields(xml, item);
        writeItemclass(xml, item);
//        writeIsDefinedBy(xml, item);
        writeRegistryAndRegister(xml, item);

        List<ContainedItem> narrower = item.getNarrower();
        if (narrower != null && !narrower.isEmpty()) {

            xml.writeStartElement("containeditems");
            for (ContainedItem ci : narrower) {

                if (item.getItemclass().getParentItemClassType().equals("register")
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    writeItemShortVersion(xml, ci, item.getItemclass().getParentid(), item);
                } else {
                    writeItemShortVersion(xml, ci, "value", item);
                }
//                if (ci.isHasCollection()) {
//                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItems()) {
//                            writeItemShortVersion(xml, c, "value", item);
//                        }
//                    } else if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItemsBeeingParentItemClass()) {
//                            writeItemShortVersion(xml, c, "value", item);
//                        }
//                    }
//                }
            }
            xml.writeEndElement();
        } else if (item.getContainedItemsBeeingParentItemClass() != null && !item.getContainedItemsBeeingParentItemClass().isEmpty()) {

            xml.writeStartElement("containeditems");
            for (ContainedItem ci : item.getContainedItemsBeeingParentItemClass()) {
                if (item.getItemclass().getParentItemClassType().equals("register")
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    writeItemShortVersion(xml, ci, item.getItemclass().getParentid(), item);
                } else {
                    writeItemShortVersion(xml, ci, "value", item);
                }

//                if (ci.isHasCollection()) {
//                    if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItemsBeeingParentItemClass()) {
//                            writeItemShortVersion(xml, c, "value", item);
//                        }
//                    } else if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItems()) {
//                            writeItemShortVersion(xml, c, "value", item);
//                        }
//                    }
//                }
            }
            xml.writeEndElement();
        }

        xml.writeEndDocument();

        xml.close();
    }

    private void writeVersions(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        List<VersionInformation> versionHistory = item.getVersionHistory();
        VersionInformation version = item.getVersion();
        if(version!=null && version.getUri()!=null) {
            writeEmptyElement(xml, "thisversion", version.getUri());
        }
        if(item!=null && item.getUri()!=null) {
            writeEmptyElement(xml, "latestversion", item.getUri());
        }
        if (versionHistory != null && !versionHistory.isEmpty()) {
            xml.writeStartElement("previousversions");
            for (VersionInformation versionInformation : versionHistory) {
                writeEmptyElement(xml, "version", versionInformation.getUri());
//                writeEmptyElement(xml, "version", versionInformation.getUri() + ":" + versionInformation.getNumber());
            }
            xml.writeEndElement();
        }
    }

    private void writeLanguage(XMLStreamWriter xml) throws XMLStreamException {
        writeEmptyElement(xml, "language", languageFile.getIso6391code());
    }

    private void writeDate(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, "created", item.getInsertdate());

        if (item.getEditdate() != null) {
            writeEmptyElement(xml, "issued", item.getEditdate());
        }
    }

    private void writeFields(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        // Get configuration properties
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String legacyFlag = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_LEGACY_FLAG);

        List<LocalizedProperty> localizedProperties = item.getProperties();
        for (LocalizedProperty localizedProperty : localizedProperties) {

            String lang = localizedProperty.getLang();
            if (!localizedProperty.getValues().isEmpty()) {
                String value = localizedProperty.getValues().get(0).getValue();
                String href = localizedProperty.getValues().get(0).getHref();

                String fieldName = localizedProperty.getLabel().replace("-item", "").toLowerCase();
                String fieldLocalId = localizedProperty.getId().replace("-item", "").toLowerCase();

                if (fieldName != null && "successor".equals(fieldLocalId)) {
                    writeComplexListElement(xml, localizedProperty, lang, "successors", fieldLocalId);
                } else if (fieldName != null && "predecessor".equals(fieldLocalId)) {
                    writeComplexListElement(xml, localizedProperty, lang, "predecessors", fieldLocalId);
                } else if (fieldName != null && "parent".equals(fieldLocalId)) {
                    writeComplexListElement(xml, localizedProperty, lang, "parents", fieldLocalId);
                } else if (!BaseConstants.KEY_APPLICATION_LEGACY_FLAG_ON.equals(legacyFlag)) {
                    if (href != null && !href.isEmpty()) {
                        xml.writeStartElement(fieldLocalId);
                        xml.writeAttribute("id", href);
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        xml.writeEndElement();
                    } else {
                        writeSimpleElementWithAttribute(xml, fieldLocalId, NS_XML, "lang", lang, value);
                    }
                } else {
                    if (fieldName != null && "contactpoint".equals(fieldLocalId)) {
                        xml.writeStartElement("contactpoint");
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        writeEmptyElement(xml, "email", href);
                        xml.writeEndElement();
                    } else if (fieldName != null && "license".equals(fieldLocalId)) {
                        xml.writeStartElement("license");
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        writeEmptyElement(xml, "uri", href);
                        xml.writeEndElement();
                    } else if (fieldName != null && "governance-level".equals(fieldLocalId)) {
                        xml.writeStartElement("governance-level");
                        xml.writeAttribute("id", href);
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        xml.writeEndElement();
                    } else if (fieldName != null && "collection".equals(fieldLocalId)) {
                        String registerUri = item.getRegister().getUri();
                        int index = registerUri.lastIndexOf("/");
                        String registerid = registerUri.substring(index + 1);
                        xml.writeStartElement(registerid);
//                        xml.writeStartElement(item.getItemclass().getParentid());
                        xml.writeAttribute("id", href);
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        xml.writeEndElement();
                    } else if (fieldName != null && "status".equals(fieldLocalId)) {
                        xml.writeStartElement(fieldLocalId);
                        xml.writeAttribute("id", href);
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        xml.writeEndElement();
                    } else if (fieldName != null && ("annex".equals(fieldLocalId) || "themenumber".equals(fieldLocalId))) {
                        writeEmptyElement(xml, fieldLocalId, value);
                    } else if (href != null && !href.isEmpty()) {
                        xml.writeStartElement(fieldLocalId);
                        xml.writeAttribute("id", href);
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                        xml.writeEndElement();
                    } else {
                        writeSimpleElementWithAttribute(xml, fieldLocalId, NS_XML, "lang", lang, value);
                    }
                }

            }
        }
    }

    private void writeComplexListElement(XMLStreamWriter xml, LocalizedProperty localizedProperty, String lang, String mainElement, String secondElement) throws XMLStreamException {
        xml.writeStartElement(mainElement);

        List<LocalizedPropertyValue> valuesListsuccessors = localizedProperty.getValues();
        if (valuesListsuccessors != null && !valuesListsuccessors.isEmpty()) {
            for (LocalizedPropertyValue localizedPropertyValue : valuesListsuccessors) {
                String valueSuccessor = localizedPropertyValue.getValue();
                String hrefSuccessor = localizedPropertyValue.getHref();

                xml.writeStartElement(secondElement);
                xml.writeAttribute("id", hrefSuccessor);
                writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, valueSuccessor);
                xml.writeEndElement();
            }
        }
        xml.writeEndElement();
    }

    private void writeIsDefinedBy(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                List<ContainedItem> referenceList = item.getIsDefinedBy();
                if (referenceList != null) {
                    for (ContainedItem containedItem : referenceList) {
                        xml.writeStartElement(containedItem.getItemclass().getId().toLowerCase().replace("-item", ""));
                        xml.writeAttribute("id", containedItem.getUri());
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", containedItem.getLanguage(), containedItem.getLocalid());
                        xml.writeEndElement();
                    }
                }
                break;
        }
    }

    private void writeItemclass(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                String itemClassName = item.getItemclass().getId();
                xml.writeStartElement("itemclass");
                xml.writeAttribute("uriname", itemClassName);
//                xml.writeAttribute(NS_XML, "uriname", itemClassName);
                writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", item.getLanguage(), itemClassName);
                xml.writeEndElement();

                break;
        }
    }

    private void writeRegistryAndRegister(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                xml.writeStartElement("registry");
                xml.writeAttribute("id", item.getRegistry().getUri());
                writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", item.getLanguage(), item.getRegistry().getUri());
                xml.writeEndElement();
                break;
            default:
                xml.writeStartElement("register");
                xml.writeAttribute("id", item.getRegister().getUri());
                writeTitle(xml, item.getRegister());

                xml.writeStartElement("registry");
                xml.writeAttribute("id", item.getRegistry().getUri());
                writeTitle(xml, item.getRegistry());

                xml.writeEndElement();
                xml.writeEndElement();
                break;
        }
    }

    private void writeTitle(XMLStreamWriter xml, ItemRef item) throws XMLStreamException {
        Optional<LocalizedProperty> maybeTitle = item.getProperties().stream()
                .filter(it -> "true".equals(it.getIstitle()))
                .findAny();
        if (!maybeTitle.isPresent()) {
            return;
        }
        LocalizedProperty title = maybeTitle.get();
        String lang = title.getLang();
        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, title.getValues().get(0).getValue());
    }

    private void writeEmptyElement(XMLStreamWriter xml, String localName, String value) throws XMLStreamException {
        xml.writeStartElement(localName);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    private void writeSimpleElementWithAttribute(XMLStreamWriter xml, String localName, String nsAttr, String attrKey, String attrValue, String value) throws XMLStreamException {
        xml.writeStartElement(localName);
        xml.writeAttribute("xml", attrValue, "lang", attrValue);
//        xml.writeAttribute(attrKey, attrValue);
//        xml.writeAttribute(nsAttr, attrKey, attrValue);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
