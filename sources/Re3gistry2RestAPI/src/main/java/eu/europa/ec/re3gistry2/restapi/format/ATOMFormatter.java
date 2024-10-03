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
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedPropertyValue;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ATOMFormatter implements Formatter {

    private static final String XSISCHEMALOCATION = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XMLNS = "http://www.w3.org/2005/Atom";
    private RegLanguagecode languageFile;

    @Override
    public String getFormatName() {
        return "atom";
    }

    @Override
    public String getContentType() {
        return "text/atom+xml";
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

    private XMLStreamWriter getATOMWriter(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = XMLFactory.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, XMLFactory.NEW_LINE, XMLFactory.INDENT);

        xml.writeStartDocument();

        xml.writeStartElement("feed");
        xml.writeDefaultNamespace(XMLNS);

        xml.writeAttribute("xml", XSISCHEMALOCATION, "lang", languageFile.getIso6391code());

        return xml;
    }

    private void writeRegistry(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getATOMWriter(out, item);

        writeId(xml, item);
        writeAuthor(xml, item);
        writeDate(xml, item);
        writeFields(xml, item, true);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeRegisterShortVersion(xml, ci);
            }
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeRegisterShortVersion(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("entry");

        writeId(xml, item);
        writeDate(xml, item);
        writeFields(xml, item, false);
        writeRegistryAndRegister(xml, item);

        xml.writeEndElement();
    }

    private void writeRegister(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getATOMWriter(out, item);

        writeId(xml, item);
        writeAuthor(xml, item);
        writeDate(xml, item);
        writeFields(xml, item, true);
        writeRegistryAndRegister(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci);
            }
        }

        xml.writeEndDocument();
        xml.close();
    }

    private void writeItemShortVersion(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("entry");

        writeId(xml, item);
        writeDate(xml, item);
        writeFields(xml, item, false);
        writeItemclass(xml, item);
        writeRegistryAndRegister(xml, item);

        xml.writeEndElement();
    }

    private void writeItem(OutputStream out, Item item) throws XMLStreamException {
        XMLStreamWriter xml = getATOMWriter(out, item);

        writeId(xml, item);
        writeAuthor(xml, item);
        writeDate(xml, item);
        writeVersions(xml, item);

        writeFields(xml, item, true);
        writeItemclass(xml, item);
        writeRegistryAndRegister(xml, item);

        List<ContainedItem> narrower = item.getNarrower();
        if (narrower != null && !narrower.isEmpty()) {

            for (ContainedItem ci : narrower) {

                if (item.getItemclass().getParentItemClassType().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    writeItemShortVersion(xml, ci);
                } else {
                    writeItemShortVersion(xml, ci);
                }
//                if (ci.isHasCollection()) {
//                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItems()) {
//                            writeItemShortVersion(xml, c);
//                        }
//                    } else if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItemsBeeingParentItemClass()) {
//                            writeItemShortVersion(xml, c);
//                        }
//                    }
//                }
            }
        } else if (item.getContainedItemsBeeingParentItemClass() != null && !item.getContainedItemsBeeingParentItemClass().isEmpty()) {

            for (ContainedItem ci : item.getContainedItemsBeeingParentItemClass()) {
                if (item.getItemclass().getParentItemClassType().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)
                        && ci.getItemclass().getId().equals(item.getItemclass().getId())) {
                    writeItemShortVersion(xml, ci);
                } else {
                    writeItemShortVersion(xml, ci);
                }

//                if (ci.isHasCollection()) {
//                    if (ci.getContainedItemsBeeingParentItemClass() != null && !ci.getContainedItemsBeeingParentItemClass().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItemsBeeingParentItemClass()) {
//                            writeItemShortVersion(xml, c);
//                        }
//                    } else if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
//                        for (ContainedItem c : ci.getContainedItems()) {
//                            writeItemShortVersion(xml, c);
//                        }
//                    }
//                }
            }
        }

        xml.writeEndDocument();

        xml.close();
    }

    private void writeVersions(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        List<VersionInformation> versionHistory = item.getVersionHistory();
        VersionInformation version = item.getVersion();

        xml.writeStartElement("link");
        xml.writeAttribute("href", item.getUri());
        xml.writeAttribute("rel", "self");
        xml.writeEndElement();

        xml.writeStartElement("link");
        if (version != null && version.getUri() != null) {
            xml.writeAttribute("href", version.getUri());
        }
        xml.writeAttribute("rel", "self");
        xml.writeEndElement();

        xml.writeStartElement("link");
        xml.writeAttribute("href", item.getUri());
        xml.writeAttribute("rel", "latest-version");
        xml.writeEndElement();

        if (versionHistory != null && !versionHistory.isEmpty()) {
            for (VersionInformation versionInformation : versionHistory) {
                xml.writeStartElement("link");
                xml.writeAttribute("href", versionInformation.getUri());
                xml.writeAttribute("rel", "version-history");
                xml.writeEndElement();
            }
        }
    }

    private void writeId(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, "id", item.getUri());

        xml.writeStartElement("link");
        xml.writeAttribute("href", item.getUri());
        xml.writeAttribute("rel", "self");
        xml.writeEndElement();
    }

    private void writeAuthor(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("author");

        // Get configuration properties
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String legacyFlag = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_LEGACY_FLAG);
        if (BaseConstants.KEY_APPLICATION_LEGACY_FLAG_ON.equals(legacyFlag)) {
            writeEmptyElement(xml, "name", "INSPIRE Registry team");
            writeEmptyElement(xml, "email", "JRC-INSPIRE-SUPPORT@ec.europa.eu");
        } else {
            writeEmptyElement(xml, "name", "Registry team");
        }

        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                writeEmptyElement(xml, "uri", item.getUri());
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, "uri", item.getRegistry().getUri());
                break;
            default:
                writeEmptyElement(xml, "uri", item.getRegistry().getUri());
                break;
        }
        xml.writeEndElement();
    }

    private void writeDate(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, "created", item.getInsertdate());

        if (item.getEditdate() != null) {
            writeEmptyElement(xml, "updated", item.getEditdate());
        } else {
            writeEmptyElement(xml, "updated", item.getInsertdate());
        }
    }

    private void writeFields(XMLStreamWriter xml, ContainedItem item, boolean mainElement) throws XMLStreamException {
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
                    writeComplexListElement(xml, localizedProperty, lang, "successors", "next");
                } else if (fieldName != null && "predecessor".equals(fieldLocalId)) {
                    writeComplexListElement(xml, localizedProperty, lang, "predecessors", "previous");
                } else if (fieldName != null && "parent".equals(fieldLocalId)) {
                    writeComplexListElement(xml, localizedProperty, lang, "parents", "up");
                } else if (fieldName != null && ("definition".equals(fieldLocalId) || "contentsummary".equals(fieldLocalId))) {
                    if (mainElement) {
                        writeEmptyElement(xml, "subtitle", value);
                    } else {
                        writeEmptyElement(xml, "summary", value);
                    }
                } else if (fieldName != null && ("description".equals(fieldLocalId) || "contentsummary".equals(fieldLocalId))) {
                    writeEmptyElement(xml, "content", value);
                } else if (fieldName != null && "collection".equals(fieldLocalId)) {
                    xml.writeStartElement("link");
                    xml.writeAttribute("href", href);
                    xml.writeAttribute("rel", "up");
                    xml.writeEndElement();
                } else if (fieldName != null && "label".equals(fieldLocalId)) {
                    //writeEmptyElement(xml, fieldLocalId, value);
                    writeEmptyElement(xml, "title", value);
                } else if (href != null && !href.isEmpty()) {
                    xml.writeStartElement("link");
                    xml.writeAttribute("href", href);
                    xml.writeAttribute("rel", "related");
                    xml.writeEndElement();
                } else {
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

                xml.writeStartElement("link");
                xml.writeAttribute("href", hrefSuccessor);
                xml.writeAttribute("rel", secondElement);
                xml.writeEndElement();
            }
        }
        xml.writeEndElement();
    }

    private void writeItemclass(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                String itemClassName = item.getItemclass().getId();
                //xml.writeStartElement("term");
                //xml.writeAttribute("uriname", itemClassName);
                xml.writeStartElement("category");
                xml.writeAttribute("term", itemClassName);
                xml.writeEndElement();

                break;
        }
    }

    private void writeRegistryAndRegister(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                xml.writeStartElement("link");
                xml.writeAttribute("href", item.getRegistry().getUri());
                xml.writeAttribute("rel", "up");
                xml.writeEndElement();
                break;
            default:
                //xml.writeStartElement("link");
                //xml.writeAttribute("href", item.getRegistry().getUri());
                //xml.writeAttribute("rel", "up");
                //xml.writeEndElement();

                xml.writeStartElement("link");
                xml.writeAttribute("href", item.getRegister().getUri());
                xml.writeAttribute("rel", "up");
                xml.writeEndElement();

                break;
        }
    }

    private void writeEmptyElement(XMLStreamWriter xml, String localName, String value) throws XMLStreamException {
        xml.writeStartElement(localName);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
