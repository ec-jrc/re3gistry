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
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.util.Properties;

public class RORFormatter implements Formatter {

    private static final String DCAT = "http://www.w3.org/ns/dcat#";
    private static final String ADMS = "http://www.w3.org/ns/adms#";
    private static final String VCARD = "http://www.w3.org/2006/vcard/ns#";
    private static final String VANN = "http://purl.org/vocab/vann/";
    private static final String DC = "http://purl.org/dc/elements/1.1/";
    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String OWL = "http://www.w3.org/2002/07/owl#";
    private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    private static final String VOAF = "http://labs.mondeca.com/vocab/voaf#";
    private static final String DCT = "http://purl.org/dc/terms/";
    private static final String FOAF = "http://xmlns.com/foaf/0.1/";

    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";

    private static final Map<String, String> ROLE_PROPERTY_TO_ELEMENT;

    static {
        Map<String, String> roleToElement = new LinkedHashMap<>();
        roleToElement.put(BaseConstants.KEY_ROLE_REGISTRYMANAGER, "publisher");
        roleToElement.put(BaseConstants.KEY_ROLE_REGISTEROWNER, "rightsHolder");
        roleToElement.put(BaseConstants.KEY_ROLE_REGISTERMANAGER, "publisher");
        roleToElement.put(BaseConstants.KEY_ROLE_CONTROLBODY, "creator");
        roleToElement.put(BaseConstants.KEY_ROLE_CONTACT_POINT, "contactPoint");
        ROLE_PROPERTY_TO_ELEMENT = Collections.unmodifiableMap(roleToElement);
    }

    private RegLanguagecode languageFile;

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
        nsCtx.add("voaf", VOAF);
        nsCtx.add("dct", DCT);
        nsCtx.add("foaf", FOAF);
        return nsCtx;
    }

    @Override
    public String getFormatName() {
        return "ror";
    }

    @Override
    public String getContentType() {
        return "application/rdf+xml";
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        XMLStreamWriter xml = getXMLWriter(out, RDF, "RDF");
        this.languageFile = lang;

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

    private void writeRegistry(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());

        writeRDFType(xml, item);

        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeRoles(xml, item);
        writeAuthorityFrequency(xml, item);

//        write registers
        if (item.getContainedItemsBeeingParentItemClass() != null && !item.getContainedItemsBeeingParentItemClass().isEmpty()) {
            for (ContainedItem ci : item.getContainedItemsBeeingParentItemClass()) {
                writeItemShortVersion(xml, ci);
            }
        }
        xml.writeEndElement();
    }

    private void writeRegister(XMLStreamWriter xml, Item item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());

        writeRDFType(xml, item);

        writeRegistryElement(xml, item);

        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeRoles(xml, item);
        writeAuthorityFrequency(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci);
            }
        }
        xml.writeEndElement();
    }

    private void writeItemShortVersion(XMLStreamWriter xml, ContainedItem containedItem) throws XMLStreamException {
        xml.writeStartElement(DCT, "dataset");
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", containedItem.getUri());

        xml.writeStartElement(DCAT, "distribution");
        xml.writeAttribute(RDF, "parseType", "Resource");

        writeEmptyElement(xml, DCT, "format", RDF, "resource", "http://publications.europa.eu/resource/authority/file-type/RDF_XML");
        writeEmptyElement(xml, DCAT, "downloadURL", RDF, "resource", containedItem.getUri() + "/" + containedItem.getLocalid() + ".ror");

        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private void writeItem(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());

        writeRDFType(xml, item);

        writeRegistryElement(xml, item);
        writeRegisterElement(xml, item);

        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeAuthorityFrequency(xml, item);
        writeRoles(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci);
            }
        }
        xml.writeEndElement();
    }

    private void writeRDFType(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Catalog");
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#ConceptScheme");
                break;
            default:
                if (item.isIsParent()) {
                    writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#Dataset");
                } else if (item.isHasCollection()) {
                    writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#Concept");
                } else {
                    writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#ConceptScheme");
                }
                break;
        }
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

        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY: {
                writeSimpleElement(xml, DCT, "title", NS_XML, "lang", lang, title.getValues().get(0).getValue());
            }
            break;
            default:
                writeSimpleElement(xml, SKOS, "prefLabel", NS_XML, "lang", lang, title.getValues().get(0).getValue());
                break;
        }
    }

    private void writeDefinition(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        Optional<LocalizedProperty> maybeDefinition = item.getProperty("contentsummary");
        if (!maybeDefinition.isPresent()) {
            maybeDefinition = item.getProperty("definition");
        }
        if (!maybeDefinition.isPresent()) {
            return;
        }
        LocalizedProperty desc = maybeDefinition.get();
        String lang = desc.getLang();
        String description = desc.getValues().stream()
                .findAny().map(it -> it.getValue()).orElse(null);
        if (description == null || description.isEmpty()) {
            return;
        }

        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY: {
                writeSimpleElement(xml, DCT, "description", NS_XML, "lang", lang, description);
            }
            break;
            default:
                writeSimpleElement(xml, SKOS, "definition", NS_XML, "lang", lang, description);
                break;
        }
    }

    private void writeDescription(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
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
        writeSimpleElement(xml, VANN, "usageNote", NS_XML, "lang", lang, description);
    }

    private void writeRoles(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(DCT, "publisher");

//        writeEmptyElement(xml, FOAF, "Agent", RDF, "about", "http://publications.europa.eu/resource/authority/corporate-body/JRC");
        xml.writeStartElement(FOAF, "Agent");
        xml.writeAttribute(RDF, "about", "http://publications.europa.eu/resource/authority/corporate-body/JRC");

        for (String roleProperty : ROLE_PROPERTY_TO_ELEMENT.keySet()) {
            Optional<LocalizedProperty> p = item.getProperty(roleProperty);
            if (!p.isPresent()) {
                continue;
            }
            String localName = ROLE_PROPERTY_TO_ELEMENT.get(roleProperty);

            String lang = p.get().getLang();
            String value = p.get().getValues().get(0).getValue();
            String href = p.get().getValues().get(0).getHref();

            if ("contactPoint".equals(localName)) {
                writeEmptyElement(xml, FOAF, "hasEmail", RDF, "resource", "mailto:" + href);
            } else if ("publisher".equals(localName)) {
                writeSimpleElement(xml, FOAF, "name", NS_XML, "lang", lang, value);
            }
        }
        final Properties configurationProperties = Configuration.getInstance().getProperties();
        String legacyFlag = configurationProperties.getProperty(BaseConstants.KEY_APPLICATION_LEGACY_FLAG);
        if (!legacyFlag.equals(BaseConstants.KEY_APPLICATION_LEGACY_FLAG_ON)) {
            writeEmptyElement(xml, FOAF, "mbox", RDF, "resource", "mailto:jrc-inspire-support@ec.europa.eu");
        }
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private XMLStreamWriter getXMLWriter(OutputStream out, String rootNS, String rootElement) throws XMLStreamException {
        XMLStreamWriter xml = XMLFactory.XOF.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        xml = new IndentingXMLStreamWriter(xml, XMLFactory.NEW_LINE, XMLFactory.INDENT);
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

    private void writeAuthorityFrequency(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, DCT, "accrualPeriodicity", RDF, "resource", "http://publications.europa.eu/resource/authority/frequency/MONTHLY");
    }

    private void writeRegistryElement(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(DCAT, "isPartOf");
        writeEmptyElement(xml, DCAT, "Catalog", RDF, "about", item.getRegistry().getUri());
        xml.writeEndElement();
    }

    private void writeRegisterElement(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(DCAT, "isPartOf");
        writeEmptyElement(xml, DCAT, "Catalog", RDF, "about", item.getRegister().getUri());
        xml.writeEndElement();
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
