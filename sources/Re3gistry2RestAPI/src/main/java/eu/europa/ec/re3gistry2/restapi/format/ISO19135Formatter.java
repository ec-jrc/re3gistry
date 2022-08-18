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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;

/**
 * https://standards.iso.org/iso/19135/-2/reg/1.0/registration.xsd
 */
public class ISO19135Formatter implements Formatter {

    @Override
    public String getFormatName() {
        return "iso19135xml";
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        if (BaseConstants.KEY_ITEMCLASS_TYPE_ITEM.equals(item.getType())) {
            if (item.getContainedItems() == null || item.getContainedItems().isEmpty()) {
                // Not a Collection
                writeRegisterItem(out, item, lang);
                return;
            }
        }
        writeRegister(out, item, lang);
    }

    private XMLStreamWriter getXMLWriter(OutputStream out, String rootElement) throws XMLStreamException {
        XMLStreamWriter xml = XMLFactory.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, XMLFactory.NEW_LINE, XMLFactory.INDENT);
        
        MapNameSpaceContext nsCtx = new MapNameSpaceContext();
        nsCtx.add("gmd", "http://www.isotc211.org/2005/gmd");
        nsCtx.add("gco", "http://www.isotc211.org/2005/gco");
        nsCtx.add("xlink", "http://www.w3.org/1999/xlink");
        nsCtx.add("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xml.setNamespaceContext(nsCtx);
        xml.setDefaultNamespace("http://www.isotc211.org/2005/grg");

        xml.writeStartDocument();
        xml.writeStartElement(rootElement);

        for (Map.Entry<String, String> namespaces : nsCtx.prefixToNS.entrySet()) {
            String prefix = namespaces.getKey();
            String nsURI = namespaces.getValue();
            xml.writeNamespace(prefix, nsURI);
        }
        xml.writeDefaultNamespace("http://www.isotc211.org/2005/grg");

        String schemaLocation = "http://www.isotc211.org/2005/grg http://standards.iso.org/iso/19135/-2/reg/1.0/registration.xsd";
        xml.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);

        return xml;
    }

    private void writeRegister(OutputStream out, Item item, RegLanguagecode lang) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, "RE_Register");

        /*
        <sequence>
        <element name="name" type="gco:CharacterString_PropertyType"/>
        <element name="contentSummary" type="gco:CharacterString_PropertyType"/>
        <element maxOccurs="unbounded" name="uniformResourceIdentifier" type="mcc:Abstract_OnlineResource_PropertyType"/>
        <element name="operatingLanguage" type="reg:RE_Locale_PropertyType"/>
        <element maxOccurs="unbounded" name="alternativeLanguages" type="reg:RE_Locale_PropertyType">...</element>
        <element minOccurs="0" name="version" type="reg:RE_Version_PropertyType"/>
        <element minOccurs="0" name="dateOfLastChange" type="gco:Date_PropertyType"/>
        <element maxOccurs="unbounded" name="submitter" type="reg:RE_SubmittingOrganization_PropertyType"/>
        <element maxOccurs="unbounded" name="containedItem" type="reg:RE_RegisterItem_PropertyType"/>
        <element name="manager" type="reg:RE_RegisterManager_PropertyType"/>
        <element name="owner" type="reg:RE_RegisterOwner_PropertyType"/>
        <element maxOccurs="unbounded" name="containedItemClass" type="reg:RE_ItemClass_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="citation" type="reg:RE_ReferenceSource_PropertyType"/>
        </sequence>
         */

        writeName(xml, item);
        writeContentSummary(xml, item);
        writeUniformResourceIdentifier(xml, item);
        writeOperatingLanguage(xml, lang);
        writeGcoNilReason(xml, "alternativeLanguages", "inapplicable");
        // version
        // dateOfLastChange
        writeSubmitter(xml, item);
        for (ContainedItem containedItem : item.getContainedItems()) {
            xml.writeStartElement("containedItem");
            xml.writeAttribute("http://www.w3.org/1999/xlink", "href", containedItem.getUri());
            xml.writeStartElement("RE_RegisterItem");
            writeRegisterItem(xml, containedItem, lang);
            xml.writeEndElement();
            xml.writeEndElement();
        }
        writeManager(xml, item);
        writeOwner(xml, item);
        writeContainedItemClass(xml, item);
        // citation

        xml.writeEndElement();
        xml.writeEndDocument();
        xml.close();
    }

    private void writeName(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        String value = item.getProperty(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID)
                .map(property -> property.getValues().get(0).getValue())
                .orElse("");
        writeGcoCharacterString(xml, "name", value);
    }

    private void writeContentSummary(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        String contentSummary = item.getProperty(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID)
                .map(property -> property.getValues().get(0).getValue())
                .orElse("");
        writeGcoCharacterString(xml, "contentSummary", contentSummary);
    }

    private void writeUniformResourceIdentifier(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("uniformResourceIdentifier");
        xml.writeStartElement("http://www.isotc211.org/2005/gmd", "CI_OnlineResource");
        xml.writeStartElement("http://www.isotc211.org/2005/gmd", "linkage");
        xml.writeStartElement("http://www.isotc211.org/2005/gmd", "URL");
        xml.writeCharacters(item.getUri());
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeOperatingLanguage(XMLStreamWriter xml, RegLanguagecode lang) throws XMLStreamException {
        xml.writeStartElement("operatingLanguage");
        xml.writeStartElement("RE_Locale");

        writeGcoCharacterString(xml, "name", lang.getLabel());

        xml.writeStartElement("language");
        xml.writeStartElement("http://www.isotc211.org/2005/gmd", "LanguageCode");
        xml.writeAttribute("codeList", "http://www.loc.gov/standards/iso639-2");
        xml.writeAttribute("codeListValue", lang.getIso6392code());
        xml.writeCharacters(lang.getIso6392code());
        xml.writeEndElement();
        xml.writeEndElement();

        writeGcoNilReason(xml, "country", "missing");

        xml.writeStartElement("characterEncoding");
        xml.writeStartElement("http://www.isotc211.org/2005/gmd", "MD_CharacterSetCode");
        xml.writeAttribute("codeList", "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml");
        xml.writeAttribute("codeListValue", "utf-8");
        xml.writeEndElement();
        xml.writeEndElement();

        writeGcoNilReason(xml, "citation", "missing");

        xml.writeEndElement(); // Close RE_Locale
        xml.writeEndElement(); // Close operatingLanguage
    }

    private void writeSubmitter(XMLStreamWriter xml, Item item) throws XMLStreamException {
        writeRegisterAdministratorRole(xml, item,
                "submitter", "RE_RegisterSubmitter", BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS, "publisher");
    }

    private void writeOwner(XMLStreamWriter xml, Item item) throws XMLStreamException {
        writeRegisterAdministratorRole(xml, item,
                "owner", "RE_RegisterOwner", BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER, "owner");
    }

    private void writeManager(XMLStreamWriter xml, Item item) throws XMLStreamException {
        writeRegisterAdministratorRole(xml, item,
                "manager", "RE_RegisterManager", BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER, "custodian");
    }

    private void writeRegisterAdministratorRole(XMLStreamWriter xml, Item item,
            String role, String type, String field, String roleCode) throws XMLStreamException {
        Optional<LocalizedProperty> val = item.getProperty(field);
        if (!val.isPresent()) {
            writeGcoNilReason(xml, role, "inapplicable");
        } else {
            xml.writeStartElement(role);
            xml.writeStartElement(type);

            writeGcoCharacterString(xml, "name", val.get().getValues().get(0).getValue());

            xml.writeStartElement("contact");
            xml.writeStartElement("http://www.isotc211.org/2005/gmd", "CI_ResponsibleParty");
            xml.writeStartElement("http://www.isotc211.org/2005/gmd", "role");

            xml.writeStartElement("http://www.isotc211.org/2005/gmd", "CI_RoleCode");
            xml.writeAttribute("codeList", "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode");
            xml.writeAttribute("codeListValue", roleCode);
            xml.writeEndElement();

            xml.writeEndElement();
            xml.writeEndElement();
            xml.writeEndElement();

            xml.writeEndElement();
            xml.writeEndElement();
        }
    }

    private void writeRegisterItem(OutputStream out, Item item, RegLanguagecode lang) throws XMLStreamException {
        XMLStreamWriter xml = getXMLWriter(out, "RE_RegisterItem");
        writeRegisterItem(xml, item, lang);
        xml.writeEndElement();
        xml.writeEndDocument();
        xml.close();
    }

    private void writeRegisterItem(XMLStreamWriter xml, ContainedItem item, RegLanguagecode lang) throws XMLStreamException {
        /*
        <sequence>
        <element maxOccurs="unbounded" minOccurs="0" name="predecessor" type="reg:RE_RegisterItem_PropertyType"/>
        <element name="itemIdentifier" type="gco:Integer_PropertyType"/>
        <element name="name" type="gco:CharacterString_PropertyType"/>
        <element name="status" type="reg:RE_ItemStatus_PropertyType"/>
        <element minOccurs="0" name="dateAccepted" type="gco:Date_PropertyType"/>
        <element minOccurs="0" name="dateAmended" type="gco:Date_PropertyType"/>
        <element name="definition" type="gco:CharacterString_PropertyType"/>
        <element minOccurs="0" name="description" type="gco:CharacterString_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="fieldOfApplication" type="reg:RE_FieldOfApplication_PropertyType">...</element>
        <element maxOccurs="unbounded" minOccurs="0" name="alternativeExpressions" type="reg:RE_AlternativeExpression_PropertyType">...</element>
        <element minOccurs="0" name="specificationSource" type="reg:RE_Reference_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="specificationLineage" type="reg:RE_Reference_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="successor" type="reg:RE_RegisterItem_PropertyType"/>
        <element name="itemClass" type="reg:RE_ItemClass_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="clarificationInformation" type="reg:RE_ClarificationInformation_PropertyType"/>
        <element maxOccurs="unbounded" minOccurs="0" name="amendmentInformation" type="reg:RE_AmendmentInformation_PropertyType"/>
        <element maxOccurs="unbounded" name="additionInformation" type="reg:RE_AdditionInformation_PropertyType"/>
        </sequence>
         */
        // predecessor
        writeGcoNilReason(xml, "itemIdentifier", "inapplicable");
        writeName(xml, item);
        writeStatus(xml, item);
        // dateAccepted
        // dateAmended
        writeDefinition(xml, item);
        writeDescription(xml, item);
        // fieldOfApplication
        // alternativeExpressions
        // specificationSource
        // specificationLineage
        // successor
        writeItemClass(xml, item);
        // clarificationInformation
        // amendmentInformation
        writeXLink(xml, "additionInformation", item.getUri());
    }

    private void writeStatus(XMLStreamWriter xml, ContainedItem item) {
        // TODO Auto-generated method stub
    }

    private void writeDefinition(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        Optional<LocalizedProperty> val = item.getProperty("definition");
        if (!val.isPresent()) {
            writeGcoNilReason(xml, "definition", "missing");
        } else {
            LocalizedProperty prop = val.get();
            if (prop.getValues().isEmpty()) {
                writeGcoNilReason(xml, "definition", "missing");
            } else {
                writeGcoCharacterString(xml, "definition", prop.getValues().get(0).getValue());
            }
        }
    }

    private void writeDescription(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        Optional<LocalizedProperty> val = item.getProperty("description");
        if (!val.isPresent()) {
            writeGcoNilReason(xml, "description", "missing");
        } else {
            LocalizedProperty prop = val.get();
            if (prop.getValues().isEmpty()) {
                writeGcoNilReason(xml, "description", "missing");
            } else {
                writeGcoCharacterString(xml, "description", prop.getValues().get(0).getValue());
            }
        }
    }

    private void writeContainedItemClass(XMLStreamWriter xml, Item item) throws XMLStreamException {
        if (item.getContainedItems().isEmpty()) {
            writeGcoNilReason(xml, "containedItemClass", "missing");
        } else {
            xml.writeStartElement("containedItemClass");
            writeRE_ItemClass(xml, item.getContainedItems().get(0));
            xml.writeEndElement();
        }
    }

    private void writeItemClass(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("itemClass");
        writeRE_ItemClass(xml, item);
        xml.writeEndElement();
    }

    private void writeRE_ItemClass(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement("RE_ItemClass");
        writeGcoCharacterString(xml, "name", item.getItemclass().getId());
        writeGcoNilReason(xml, "technicalStandard", "inapplicable");
        writeGcoNilReason(xml, "alternativeNames", "inapplicable");
        writeGcoNilReason(xml, "describedItem", "inapplicable");
        xml.writeEndElement();
    }

    private void writeGcoNilReason(XMLStreamWriter xml, String element, String reason) throws XMLStreamException {
        xml.writeStartElement(element);
        xml.writeAttribute("http://www.isotc211.org/2005/gco", "nilReason", reason);
        xml.writeEndElement();
    }

    private void writeGcoCharacterString(XMLStreamWriter xml, String element, String value) throws XMLStreamException {
        if (value == null) {
            return;
        }
        xml.writeStartElement(element);
        xml.writeStartElement("http://www.isotc211.org/2005/gco", "CharacterString");
        xml.writeCharacters(value);
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeXLink(XMLStreamWriter xml, String element, String value) throws XMLStreamException {
        xml.writeStartElement(element);
        xml.writeAttribute("http://www.w3.org/1999/xlink", "href", value);
        xml.writeEndElement();
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

