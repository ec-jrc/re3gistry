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
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XSDFormatter implements Formatter {

    private static final String XSISCHEMALOCATION = "http://www.w3.org/2001/XMLSchema";
    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    private RegLanguagecode languageFile;

    public String getFormatName() {
        return "xsd";
    }

    public String getContentType() {
        return "text/xsd";
    }

    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        switch (itemClass.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                writeRegistry(out, itemClass);
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeRegister(out, itemClass);
                break;
            default:
                writeItem(out, itemClass);
                break;
        }
    }

    private XMLStreamWriter getXMLWriter(OutputStream out, ItemClass itemClass) throws XMLStreamException {
        XMLStreamWriter xml = XMLFactory.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, XMLFactory.NEW_LINE, XMLFactory.INDENT);

        xml.writeStartDocument();
        switch (itemClass.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                xml.writeStartElement("xs:schema");
                xml.writeDefaultNamespace(itemClass.getBaseuri() + "/registry");
                xml.writeAttribute("targetNamespace", itemClass.getBaseuri() + "/registry");
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                xml.writeStartElement("xs:schema");
                xml.writeDefaultNamespace(itemClass.getBaseuri() + "/" + itemClass.getId() + "_register");
                xml.writeAttribute("targetNamespace", itemClass.getBaseuri() + "/" + itemClass.getId() + "_register");
                break;
            default:
                String itemclassLocalId = itemClass.getId();
                xml.writeStartElement("xs:schema");
                xml.writeDefaultNamespace(itemClass.getBaseuri() + "/" + itemClass.getRegisterLocalId() + "_register/" + itemclassLocalId);
                xml.writeAttribute("targetNamespace", itemClass.getBaseuri() + "/" + itemClass.getRegisterLocalId() + "_register/" + itemclassLocalId);
                break;
        }

        xml.writeAttribute("xmlns", XSISCHEMALOCATION, "xs", XSISCHEMALOCATION);
        xml.writeAttribute("elementFormDefault", "qualified");
        xml.writeAttribute("attributeFormDefault", "unqualified");
        xml.writeAttribute("version", "1.0");

        return xml;
    }

    private void writeRegistry(OutputStream out, ItemClass itemClass) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, itemClass);

        writeImport(xml);

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", itemClass.getStartElement());
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        writeLanguage(xml);
        writeDate(xml);
//        writeVersions(xml);
        writeFields(xml, itemClass.getType(), null, itemClass.getFields());

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "registers");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:sequence");
        writeRegisterWitoutContainedItems(xml, itemClass);
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", "id");
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndDocument();
        xml.close();
    }

    private void writeRegisterWitoutContainedItems(XMLStreamWriter xml, ItemClass itemClass) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "register");
        xml.writeAttribute("minOccurs", "0");
        xml.writeAttribute("maxOccurs", "unbounded");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        writeLanguage(xml);
        writeDate(xml);
//        writeVersions(xml);
        writeFields(xml, itemClass.getChildItemClassType(), null, itemClass.getContainedItemsFields());
        writeRegistryAndRegister(xml, itemClass.getChildItemClassType());

        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", "id");
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeRegister(OutputStream out, ItemClass itemClass) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, itemClass);

        writeImport(xml);

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", itemClass.getStartElement());
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        writeLanguage(xml);
        writeDate(xml);
//        writeVersions(xml);
        writeFields(xml, itemClass.getType(), null, itemClass.getFields());
        writeRegistryAndRegister(xml, itemClass.getType());

        if (itemClass.getChildItemClassLocalId() != null) {
            xml.writeStartElement("xs:element");
            xml.writeAttribute("name", "containeditems");
            xml.writeStartElement("xs:complexType");
            xml.writeStartElement("xs:sequence");
            writeItemChildren(xml, itemClass, itemClass.getId().toLowerCase());
            xml.writeEndElement();
            xml.writeEndElement();
            xml.writeEndElement();
        }
        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", "id");
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndDocument();
        xml.close();
    }

    private void writeItem(OutputStream out, ItemClass itemClass) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, itemClass);

        writeImport(xml);

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", itemClass.getStartElement());
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        writeLanguage(xml);
        writeDate(xml);
        writeVersions(xml);
        writeFields(xml, itemClass.getType(), itemClass.getRegisterLocalId(), itemClass.getFields());
        writeItemclass(xml, itemClass.getType());
        writeRegistryAndRegister(xml, itemClass.getType());

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "containeditems");
        xml.writeAttribute("minOccurs", "0");
        xml.writeAttribute("maxOccurs", "1");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:sequence");
        if (!itemClass.getStartElement().equals("value")) {
            writeItemSameItemClass(xml, itemClass);
        }
        writeItemChildren(xml, itemClass, "value");
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", "id");
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndDocument();

        xml.close();
    }

    private void writeItemSameItemClass(XMLStreamWriter xml, ItemClass itemClass) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", itemClass.getStartElement());
        xml.writeAttribute("minOccurs", "0");
        xml.writeAttribute("maxOccurs", "unbounded");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        writeLanguage(xml);
        writeDate(xml);
        writeVersions(xml);
        writeFields(xml, itemClass.getType(), itemClass.getRegisterLocalId(), itemClass.getFields());
        writeItemclass(xml, itemClass.getType());
        writeRegistryAndRegister(xml, itemClass.getType());

        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", "id");
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeItemChildren(XMLStreamWriter xml, ItemClass itemClass, String name) throws XMLStreamException {
        if (itemClass.getChildItemClassType() != null && !itemClass.getContainedItemsFields().isEmpty()) {
            xml.writeStartElement("xs:element");
            xml.writeAttribute("name", name);
            xml.writeAttribute("minOccurs", "0");
            xml.writeAttribute("maxOccurs", "unbounded");
            xml.writeStartElement("xs:complexType");
            xml.writeStartElement("xs:all");

            writeLanguage(xml);
            writeDate(xml);
            writeVersions(xml);
            String registerlocalid;
            if (itemClass.getRegisterLocalId() != null && !itemClass.getRegisterLocalId().isEmpty()) {
                registerlocalid = itemClass.getRegisterLocalId();
            } else {
                registerlocalid = itemClass.getStartElement();
            }
            writeFields(xml, itemClass.getChildItemClassType(), registerlocalid, itemClass.getContainedItemsFields());
            writeItemclass(xml, itemClass.getChildItemClassType());
            writeRegistryAndRegister(xml, itemClass.getChildItemClassType());

            xml.writeEndElement();

            xml.writeStartElement("xs:attribute");
            xml.writeAttribute("name", "id");
            xml.writeAttribute("type", "xs:anyURI");
            xml.writeAttribute("use", "required");
            xml.writeEndElement();

            xml.writeEndElement();
            xml.writeEndElement();
        }
    }

    private void writeVersions(XMLStreamWriter xml) throws XMLStreamException {
        writeEmptyElement(xml, "thisversion");
        writeEmptyElement(xml, "latestversion");
        createPreviousVersionsElement(xml, "previousversions", 0, 1, "version");
    }

    private void writeLanguage(XMLStreamWriter xml) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "language");
        xml.writeAttribute("type", "xs:string");
        xml.writeAttribute("minOccurs", "0");
        xml.writeAttribute("maxOccurs", "1");
        xml.writeEndElement();
    }

    private void writeDate(XMLStreamWriter xml) throws XMLStreamException {
        writeEmptyElement(xml, "created");
        writeEmptyElement(xml, "issued");
    }

    private void writeFields(XMLStreamWriter xml, String itemClassType, String registerLocalId, HashMap<String, String> localizedProperties) throws XMLStreamException {
        // Get configuration properties
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String legacyFlag = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_LEGACY_FLAG);

        for (Map.Entry<String, String> entry : localizedProperties.entrySet()) {
            String fieldLocalId = entry.getKey().replace("-item", "").toLowerCase();
            String fieldType = entry.getValue();

            if (fieldType != null) {
                String type = fieldType.toLowerCase();
                if (type.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)
                        || type.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)
                        || fieldLocalId.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)
                        || fieldLocalId.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                    continue;
                }
            }

            if (!BaseConstants.KEY_APPLICATION_LEGACY_FLAG_ON.equals(legacyFlag)) {
                if (fieldLocalId != null && "successor".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "successors", 0, 1, "successor");
                } else if (fieldLocalId != null && "predecessor".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "predecessors", 0, 1, "predecessor");
                } else if (fieldLocalId != null && "parent".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "parents", 0, 1, "parent");
                } else {
                    writeSimpleElementWithLanguageAndOptionalId(xml, fieldLocalId, "id", "0", "1");
                }
            } else {
                if (fieldLocalId != null && "contactpoint".equals(fieldLocalId)) {
                    xml.writeStartElement("xs:element");
                    xml.writeAttribute("name", "contactpoint");
                    xml.writeAttribute("minOccurs", "0");
                    xml.writeAttribute("maxOccurs", "1");
                    xml.writeStartElement("xs:complexType");
                    xml.writeStartElement("xs:all");

                    writeSimpleElementWithLanguage(xml, "label", "0", "1");
                    writeSimpleElementWitoutLanguage(xml, "email", "0", "1");

                    xml.writeEndElement();
                    xml.writeEndElement();
                    xml.writeEndElement();
                } else if (fieldLocalId != null && "license".equals(fieldLocalId)) {
                    xml.writeStartElement("xs:element");
                    xml.writeAttribute("name", "license");
                    xml.writeAttribute("minOccurs", "0");
                    xml.writeAttribute("maxOccurs", "1");
                    xml.writeStartElement("xs:complexType");
                    xml.writeStartElement("xs:all");

                    writeSimpleElementWithLanguage(xml, "label", "0", "1");
                    writeSimpleElementWitoutLanguage(xml, "uri", "0", "1");

                    xml.writeEndElement();
                    xml.writeEndElement();
                    xml.writeEndElement();
                } else if (fieldLocalId != null && "collection".equals(fieldLocalId)) {
                    if (!registerLocalId.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER) && !registerLocalId.equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                        writeComplexeElementWithElementAndattribute(xml, registerLocalId, "id", "0", "1");
                    }
                } else if (fieldLocalId != null && "successor".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "successors", 0, 1, "successor");
                } else if (fieldLocalId != null && "predecessor".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "predecessors", 0, 1, "predecessor");
                } else if (fieldLocalId != null && "parent".equals(fieldLocalId)) {
                    writeComplexeListElements(xml, "parents", 0, 1, "parent");
                } else if (fieldLocalId != null
                        && ("status".equals(fieldLocalId)
                        || fieldType.equals(BaseConstants.KEY_FIELD_TYPE_RELATION)
                        || "referencelink".equals(fieldLocalId)
                        || "extensibility".equals(fieldLocalId)
                        || "governance-level".equals(fieldLocalId)
                        || "legislationuri".equals(fieldLocalId))) {
                    writeComplexeElementWithElementAndattribute(xml, fieldLocalId, "id", "0", "1");
                } else if (fieldLocalId != null && ("sourcedefinition".equals(fieldLocalId) || "eioneturi".equals(fieldLocalId))) {
                    writeSimpleElementWithLanguageAndOptionalId(xml, fieldLocalId, "id", "0", "1");
                } else if (fieldLocalId != null && ("annex".equals(fieldLocalId) || "themenumber".equals(fieldLocalId))) {
                    writeEmptyElement(xml, fieldLocalId);
                } else {
                    writeSimpleElementWithLanguage(xml, fieldLocalId, "0", "1");
                }
            }
        }
    }

    private void writeItemclass(XMLStreamWriter xml, String itemclassType) throws XMLStreamException {
        switch (itemclassType) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                break;
            default:
                writeComplexeElementWithElementAndattribute(xml, "itemclass", "uriname", "1", "1");
                break;
        }
    }

    private void writeRegistryAndRegister(XMLStreamWriter xml, String itemClassType) throws XMLStreamException {
        if (itemClassType != null) {
            switch (itemClassType) {
                case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                    break;
                case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                    writeComplexeElementWithElementAndattribute(xml, "registry", "id", "1", "1");
                    break;
                default:
                    xml.writeStartElement("xs:element");
                    xml.writeAttribute("name", "register");
                    xml.writeAttribute("minOccurs", "1");
                    xml.writeAttribute("maxOccurs", "1");
                    xml.writeStartElement("xs:complexType");
                    xml.writeStartElement("xs:all");

                    writeSimpleElementWithLanguage(xml, "label", "1", "1");
                    writeComplexeElementWithElementAndattribute(xml, "registry", "id", "1", "1");

                    xml.writeEndElement();

                    xml.writeStartElement("xs:attribute");
                    xml.writeAttribute("name", "id");
                    xml.writeAttribute("type", "xs:anyURI");
                    xml.writeAttribute("use", "required");
                    xml.writeEndElement();

                    xml.writeEndElement();
                    xml.writeEndElement();
                    break;
            }
        }
    }

    private void writeEmptyElement(XMLStreamWriter xml, String localName) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", localName);
        xml.writeAttribute("type", "xs:string");
        xml.writeAttribute("minOccurs", "0");
        xml.writeAttribute("maxOccurs", "1");
        xml.writeEndElement();
    }

    private void writeImport(XMLStreamWriter xml) throws XMLStreamException {
//<xs:import namespace="http://www.w3.org/XML/1998/namespace" schemaLocation="http://www.w3.org/2001/xml.xsd"/>
        xml.writeStartElement("xs:import");
        xml.writeAttribute("namespace", NS_XML);
        xml.writeAttribute("schemaLocation", "http://www.w3.org/2001/xml.xsd");
        xml.writeEndElement();
    }

    private void createPreviousVersionsElement(XMLStreamWriter xml, String elementName, int minOccurs, int maxOccurs, String name) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", String.valueOf(minOccurs));
        xml.writeAttribute("maxOccurs", String.valueOf(maxOccurs));
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:sequence");

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", name);
        xml.writeAttribute("type", "xs:string");
        xml.writeAttribute("maxOccurs", "unbounded");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeSimpleElementWithLanguage(XMLStreamWriter xml, String elementName, String minOccurs, String maxOccurs) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", minOccurs);
        xml.writeAttribute("maxOccurs", maxOccurs);
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:simpleContent");

        xml.writeStartElement("xs:extension");
        xml.writeAttribute("base", "xs:string");
        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("ref", "xml:lang");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeSimpleElementWitoutLanguage(XMLStreamWriter xml, String elementName, String minOccurs, String maxOccurs) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", minOccurs);
        xml.writeAttribute("maxOccurs", maxOccurs);
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:simpleContent");

        xml.writeStartElement("xs:extension");
        xml.writeAttribute("base", "xs:string");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeComplexeElementWithElementAndattribute(XMLStreamWriter xml, String elementName, String attributeName, String minOccurs, String maxOccurs) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", minOccurs);
        xml.writeAttribute("maxOccurs", maxOccurs);
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:all");

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "label");
        xml.writeAttribute("minOccurs", "1");
        xml.writeAttribute("maxOccurs", "1");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:simpleContent");
        xml.writeStartElement("xs:extension");
        xml.writeAttribute("base", "xs:string");

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("ref", "xml:lang");
        xml.writeAttribute("use", "required");

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", attributeName);
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "required");
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeSimpleElementWithLanguageAndOptionalId(XMLStreamWriter xml, String elementName, String attributeName, String minOccurs, String maxOccurs) throws XMLStreamException {
//        <xs:element name="sourcedefinition" maxOccurs="1" minOccurs="0">
//                                 <xs:complexType mixed="true">
//                                    <xs:sequence>
//                                       <xs:element name="label" minOccurs="0">
//                                          <xs:complexType>
//                                             <xs:simpleContent>
//                                                <xs:extension base="xs:string">
//                                                   <xs:attribute ref="xml:lang"/>
//                                                </xs:extension>
//                                             </xs:simpleContent>
//                                          </xs:complexType>
//                                       </xs:element>
//                                    </xs:sequence>
//                                    <xs:attribute type="xs:string" name="id" use="optional"/>
//                                    <xs:attribute ref="xml:lang"/>
//                                 </xs:complexType>
//                              </xs:element>

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", minOccurs);
        xml.writeAttribute("maxOccurs", maxOccurs);
        xml.writeStartElement("xs:complexType");
        xml.writeAttribute("mixed", "true");
        xml.writeStartElement("xs:sequence");

        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", "label");
        xml.writeAttribute("minOccurs", "0");
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:simpleContent");
        xml.writeStartElement("xs:extension");
        xml.writeAttribute("base", "xs:string");

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("ref", "xml:lang");

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("name", attributeName);
        xml.writeAttribute("type", "xs:anyURI");
        xml.writeAttribute("use", "optional");
        xml.writeEndElement();

        xml.writeStartElement("xs:attribute");
        xml.writeAttribute("ref", "xml:lang");
        xml.writeEndElement();

        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeComplexeListElements(XMLStreamWriter xml, String elementName, int minOccurs, int maxOccurs, String secondElement) throws XMLStreamException {
        xml.writeStartElement("xs:element");
        xml.writeAttribute("name", elementName);
        xml.writeAttribute("minOccurs", String.valueOf(minOccurs));
        xml.writeAttribute("maxOccurs", String.valueOf(maxOccurs));
        xml.writeStartElement("xs:complexType");
        xml.writeStartElement("xs:sequence");

        writeComplexeElementWithElementAndattribute(xml, secondElement, "id", "0", "unbounded");

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
