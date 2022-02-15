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
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemRef;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
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

        String itemclassLocalId;
        if (item.isHasCollection()) {
            itemclassLocalId = "value";
        } else {
            itemclassLocalId = item.getItemclass().getId().toLowerCase();
        }

        xml.writeStartDocument();
        xml.writeStartElement(itemclassLocalId);
        xml.writeDefaultNamespace(item.getUri());

        xml.writeAttribute("xmlns", XSISCHEMALOCATION, "xsi", XSISCHEMALOCATION);
//        xml.writeAttribute(item.getItemclass().getId(), "xmlns", item.getUri());
//        xml.writeAttribute("xmlns", item.getUri());
//        xml.writeAttribute(NS_XML, "xmlns", item.getUri());
        String schemaLocation = "http://inspire.ec.europa.eu/registry http://inspire.ec.europa.eu/draft-schemas/registry/2.0/"+item.getType()+".xsd";
        xml.writeAttribute("xsi", XSISCHEMALOCATION, "schemaLocation", schemaLocation);
//        xml.writeAttribute(item.getItemclass().getId(), "id", item.getUri());
        xml.writeAttribute("id", item.getUri());
//        xml.writeAttribute(NS_XML, "id", item.getUri());

        return xml;
    }

    private void writeRegistry(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, item);

        writeLanguage(xml);
        writeDate(xml, item);
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
//        xml.writeAttribute(NS_XML, "id", item.getUri());

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
                writeItemShortVersion(xml, ci, ci.getItemclass().getId());
            }
            xml.writeEndElement();
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeItemShortVersion(XMLStreamWriter xml, ContainedItem item, String mainElementName) throws XMLStreamException {
        xml.writeStartElement(mainElementName.toLowerCase());
        xml.writeAttribute("id", item.getUri());
//        xml.writeAttribute(NS_XML, "id", item.getUri());

        writeLanguage(xml);
        writeVersions(xml, item);
        writeFields(xml, item);
        writeItemclass(xml, item);
        writeIsDefinedBy(xml, item);
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
        writeIsDefinedBy(xml, item);
        writeRegistryAndRegister(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            xml.writeStartElement("containeditems");
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci, "value");

                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem c : ci.getContainedItems()) {
                            writeItemShortVersion(xml, c, "value");
                        }
                    }
                }
            }
            xml.writeEndElement();
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeVersions(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        List<VersionInformation> versionHistory = item.getVersionHistory();
        VersionInformation version = item.getVersion();

        writeEmptyElement(xml, "thisversion", version.getUri());
        writeEmptyElement(xml, "latestversion", item.getUri());
        if (!versionHistory.isEmpty()) {
            xml.writeStartElement("previousversions");
            for (VersionInformation versionInformation : versionHistory) {
                writeEmptyElement(xml, "version", versionInformation.getUri() + ":" + versionInformation.getNumber());
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
        List<LocalizedProperty> localizedProperties = item.getProperties();
        for (LocalizedProperty localizedProperty : localizedProperties) {

            String lang = localizedProperty.getLang();
            if (!localizedProperty.getValues().isEmpty()) {
                String value = localizedProperty.getValues().get(0).getValue();
                String href = localizedProperty.getValues().get(0).getHref();

                String fieldName = localizedProperty.getLabel().replace("-item", "");
                String fieldLocalId = localizedProperty.getId();
//                String localNameLowerCase = fieldName.toLowerCase();

                if (fieldName != null && "contactpoint".equals(fieldLocalId)) {
                    xml.writeStartElement("contactPoint");
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
                    writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                    writeEmptyElement(xml, "uri", href);
                    xml.writeEndElement();
                } else if (fieldName != null && "collection".equals(fieldLocalId)) {
                    xml.writeStartElement("collection");
                    writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                    writeEmptyElement(xml, "uri", href);
                    xml.writeEndElement();
                } else if (fieldName != null && "successor".equals(fieldLocalId)) {
                    xml.writeStartElement("successor");
                    writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                    writeEmptyElement(xml, "uri", href);
                    xml.writeEndElement();
                } else if (fieldName != null && "predecessor".equals(fieldLocalId)) {
                    xml.writeStartElement("predecessor");
                    writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                    writeEmptyElement(xml, "uri", href);
                    xml.writeEndElement();
                } else if (fieldName != null && "status".equals(fieldLocalId)) {
                    xml.writeStartElement(fieldLocalId);
                    xml.writeAttribute("id", href);
//                    xml.writeAttribute(NS_XML, "id", href);
                    writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", lang, value);
                    xml.writeEndElement();
                } else if (fieldName != null && ("annex".equals(fieldLocalId) || "themenumber".equals(fieldLocalId))) {
                    writeEmptyElement(xml, fieldLocalId, value);
                } else {
                    writeSimpleElementWithAttribute(xml, fieldLocalId, NS_XML, "lang", lang, value);
                }

            }
        }
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
                        writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", containedItem.getLanguage(), containedItem.getLocalid());
                        writeEmptyElement(xml, "uri", containedItem.getUri());
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
//                xml.writeAttribute(NS_XML, "id", item.getRegistry().getUri());
                writeSimpleElementWithAttribute(xml, "label", NS_XML, "lang", item.getLanguage(), item.getRegistry().getUri());
                xml.writeEndElement();
                break;
            default:
                xml.writeStartElement("registry");
                xml.writeAttribute("id", item.getRegistry().getUri());
//                xml.writeAttribute(NS_XML, "id", item.getRegistry().getUri());
                writeTitle(xml, item.getRegistry());

                xml.writeStartElement("register");
                xml.writeAttribute("id", item.getRegister().getUri());
//                xml.writeAttribute(NS_XML, "id", item.getRegister().getUri());
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
        xml.writeAttribute(attrKey, attrValue);
//        xml.writeAttribute(nsAttr, attrKey, attrValue);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

}
