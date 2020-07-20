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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.restapi.model.ContainedItem;
import eu.europa.ec.re3gistry2.restapi.model.Item;
import eu.europa.ec.re3gistry2.restapi.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;

/**
 * TODO:
 * Not nearly completed
 */
public class RDFFormatter implements Formatter {

    private static final String DCAT   = "http://www.w3.org/ns/dcat#";
    private static final String ADMS   = "http://www.w3.org/ns/adms#";
    private static final String VCARD  = "http://www.w3.org/2006/vcard/ns#";
    private static final String VANN   = "http://purl.org/vocab/vann/";
    private static final String DC     = "http://purl.org/dc/elements/1.1/";
    private static final String RDF    = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String XSI    = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String RDFS   = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String OWL    = "http://www.w3.org/2002/07/owl#";
    private static final String SKOS   = "http://www.w3.org/2004/02/skos/core#";
    private static final String DCT    = "http://purl.org/dc/terms/";
    private static final String FOAF   = "http://xmlns.com/foaf/0.1/";
    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";

    private static final String NEW_LINE = "\n";
    private static final String INDENT   = "   ";

    private static final Map<String, String> ROLE_PROPERTY_TO_ELEMENT;
    static {
        Map<String, String> roleToElement = new LinkedHashMap<>();
        roleToElement.put(BaseConstants.KEY_ROLE_REGISTEROWNER, "rightsHolder");
        roleToElement.put(BaseConstants.KEY_ROLE_REGISTERMANAGER, "publisher");
        roleToElement.put(BaseConstants.KEY_ROLE_CONTROLBODY, "creator");
        roleToElement.put(BaseConstants.KEY_ROLE_CONTACT_POINT, "contactPoint");
        ROLE_PROPERTY_TO_ELEMENT = Collections.unmodifiableMap(roleToElement);
    }

    private MapNameSpaceContext getNSContext() {
        MapNameSpaceContext nsCtx = new MapNameSpaceContext();
        nsCtx.add("dcat", DCAT);
        nsCtx.add("adms", ADMS);
        nsCtx.add("vcard", VCARD);
        nsCtx.add("vann", VANN);
        nsCtx.add("dc", DC);
        nsCtx.add("rdf", RDF);
        nsCtx.add("xsi", XSI);
        nsCtx.add("rdfs", RDFS);
        nsCtx.add("owl", OWL);
        nsCtx.add("skos", SKOS);
        nsCtx.add("dct", DCT);
        nsCtx.add("foaf", FOAF);
        return nsCtx;
    }

    @Override
    public String getFormatName() {
        return "rdf";
    }

    @Override
    public String getContentType() {
        return "application/rdf+xml";
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        XMLStreamWriter xml = getXMLWriter(out, RDF, "RDF"); // <redf:RDF xmlns:dcat=""...>

        switch (item.getType()) {
        case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
            writeRegistry(xml, item);
            break;
        case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
            writeRegister(xml, item);
            break;
        default:
            writeItem(xml, item);
            break;
        }

        xml.writeEndElement(); // </rdf:RDF>
        xml.writeEndDocument();
        xml.close();
    }

    private void writeRegistry(XMLStreamWriter xml, Item item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUuid());
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Catalog");
        writeTitle(xml, item);
        writeDescription(xml, item);
        writeRoles(xml, item);
        for (ContainedItem ci : item.getContainedItems()) {
            writeEmptyElement(xml, DCT, "dataset", RDF, "resource", ci.getUri());
        }
        xml.writeEndElement();
    }

    private void writeRegister(XMLStreamWriter xml, Item item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUuid());
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Dataset");
        writeTitle(xml, item);
        writeDescription(xml, item);
        writeRoles(xml, item);
        for (ContainedItem ci : item.getContainedItems()) {
            writeEmptyElement(xml, DCT, "hasPart", RDF, "resource", ci.getUri());
        }
        xml.writeEndElement();
    }

    private void writeItem(XMLStreamWriter xml, Item item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUuid());
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://purl.org/net/provenance/ns#DataItem");
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Dataset");
        writeTitle(xml, item);
        writeRoles(xml, item);
        writeHasParts(xml, item);
        xml.writeEndElement();
    }

    private void writeTitle(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        Optional<LocalizedProperty> maybeTitle = item.getProperties().stream()
                .filter(it -> "true".equals(it.getIstitle()))
                .findAny();
        if (!maybeTitle.isPresent()) {
            return;
        }
        LocalizedProperty title = maybeTitle.get();
        String lang = title.getLang();
        writeSimpleElement(xml, DCT, "title", NS_XML, "lang", lang, title.getValues().get(0).getValue());
    }

    private void writeDescription(XMLStreamWriter xml, Item item) throws XMLStreamException {
        Optional<LocalizedProperty> maybeDescription = item.getProperty("description");
        if (!maybeDescription.isPresent()) {
            return;
        }
        LocalizedProperty desc = maybeDescription.get();
        String lang = desc.getLang();
        String description = desc.getValues().stream()
                .findAny().map(it -> it.getValue()).orElse(null);
        if (description == null || description.isEmpty()) {
            return;
        }
        writeSimpleElement(xml, DCT, "description", NS_XML, "lang", lang, description);
    }

    private void writeRoles(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        for (String roleProperty : ROLE_PROPERTY_TO_ELEMENT.keySet()) {
            Optional<LocalizedProperty> p = item.getProperty(roleProperty);
            if (!p.isPresent()) {
                continue;
            }
            String localName = ROLE_PROPERTY_TO_ELEMENT.get(roleProperty);
            xml.writeStartElement(DCAT, localName);

            String lang = p.get().getLang();
            String value = p.get().getValues().get(0).getValue();
            String href = p.get().getValues().get(0).getHref();

            if ("contactPoint".equals(localName)) {
                xml.writeStartElement(VCARD, "Kind");
                writeSimpleElement(xml, VCARD, "fn", NS_XML, "lang", lang, value);
                writeEmptyElement(xml, VCARD, "hasEmail", RDF, "resource", href);
                xml.writeEndElement();
            } else {
                xml.writeStartElement(FOAF, "Agent");
                writeSimpleElement(xml, FOAF, "name", NS_XML, "lang", lang, value);
                xml.writeEndElement();
            }

            xml.writeEndElement();
        }
    }

    private void writeHasParts(XMLStreamWriter xml, Item item) throws XMLStreamException {
        for (ContainedItem ci : item.getContainedItems()) {
            writeEmptyElement(xml, DCT, "hasPart", RDF, "resource", ci.getUri());
        }
    }

    private XMLStreamWriter getXMLWriter(OutputStream out, String rootNS, String rootElement) throws XMLStreamException {
        XMLStreamWriter xml = XML.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, NEW_LINE, INDENT);
        MapNameSpaceContext nsCtx = getNSContext();
        xml.setNamespaceContext(nsCtx);
        xml.writeStartDocument("utf-8", "1.0");
        xml.writeStartElement(rootNS, rootElement);
        for (Map.Entry<String, String> namespaces : nsCtx.prefixToNS.entrySet()) {
            String prefix = namespaces.getKey();
            String nsURI = namespaces.getValue();
            xml.writeNamespace(prefix, nsURI);
        }
        return xml;
    }

    private void writeSimpleElement(XMLStreamWriter xml, String ns, String localName, String nsAttr, String attrKey, String attrValue, String value) throws XMLStreamException {
        xml.writeStartElement(ns, localName);
        xml.writeAttribute(nsAttr, attrKey, attrValue);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    private void writeEmptyElement(XMLStreamWriter xml, String ns, String localName, String nsAttr, String attrKey, String attrValue) throws XMLStreamException {
        xml.writeEmptyElement(ns, localName);
        xml.writeAttribute(nsAttr, attrKey, attrValue);
    }

}

