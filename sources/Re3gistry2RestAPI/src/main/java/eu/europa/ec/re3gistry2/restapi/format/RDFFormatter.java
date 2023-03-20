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
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.BasicContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import eu.europa.ec.re3gistry2.javaapi.cache.model.VersionInformation;
import eu.europa.ec.re3gistry2.restapi.util.AvailableFormatsUtil;
import eu.europa.ec.re3gistry2.restapi.util.IndentingXMLStreamWriter;
import java.util.List;

public class RDFFormatter implements Formatter {

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String DC = "http://purl.org/dc/elements/1.1/";
    private static final String DCT = "http://purl.org/dc/terms/";
    private static final String XPATH_FUNTIONS = "http://www.w3.org/2005/02/xpath-functions";
    private static final String FOAF = "http://xmlns.com/foaf/0.1/";
    private static final String GML = "http://www.opengis.net/gml#";
    private static final String GRG = "http://www.isotc211.org/schemas/grg/";
    private static final String OWL = "http://www.w3.org/2002/07/owl#";
    private static final String VOID = "http://rdfs.org/ns/void#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    private static final String XPATH = "http://www.w3.org/2005/02/xpath-datatypes";
    private static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

    private static final String DCAT = "http://www.w3.org/ns/dcat#";
    private static final String ADMS = "http://www.w3.org/ns/adms#";
    private static final String VCARD = "http://www.w3.org/2006/vcard/ns#";
    private static final String VANN = "http://purl.org/vocab/vann/";
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
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUri());

        writeRDFType(xml, item);

        writeVersion(xml, item);
        writeDate(xml, item);
        writeFileFormat(xml, item);
        writeAuthorityFrequency(xml, item);

        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeRoles(xml, item);
        writeHasPartsTopConcepts(xml, item);
        writeStatus(xml, item);
        xml.writeEndElement();

//        write formats by selected language
        writeFormats(xml, item);

//        write registers
        for (ContainedItem ci : item.getContainedItems()) {
            writeRegisterShortVersion(xml, ci, item);
            writeFormats(xml, ci);
        }
    }

    private void writeRegisterShortVersion(XMLStreamWriter xml, ContainedItem containedItem, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", containedItem.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", containedItem.getUri());

        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Dataset");
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#Concept");

        writeTitle(xml, containedItem);
        writeDefinition(xml, containedItem);
        writeDescription(xml, containedItem);

        writeInScheme(xml, containedItem, item);
        writeBroader(xml, containedItem);
        writeNarrower(xml, containedItem);
        xml.writeEndElement();
        //        write formats by selected language
//        writeFormats(xml, item);

//        write items
        List<ContainedItem> containedItemList = containedItem.getContainedItems();
        if (containedItemList != null && !containedItemList.isEmpty()) {
            for (ContainedItem ci : containedItemList) {
                writeItemShortVersion(xml, ci);
                writeFormats(xml, ci);
            }
        }
    }

    private void writeRegister(XMLStreamWriter xml, Item item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUri());

        writeRDFType(xml, item);

        writeVersion(xml, item);
        writeDate(xml, item);
        writeFileFormat(xml, item);
        writeAuthorityFrequency(xml, item);
        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeRoles(xml, item);
        writeInScheme(xml, item);
        writeHasPartsTopConcepts(xml, item);
        writeStatus(xml, item);

        xml.writeEndElement();

        //        write formats by selected language
        writeFormats(xml, item);

//        write items
        for (ContainedItem ci : item.getContainedItems()) {
            writeItemShortVersion(xml, ci);
            writeFormats(xml, ci);
        }
    }

    private void writeItemShortVersion(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUri());

        writeRegistryElement(xml, item);
        writeRegisterElement(xml, item);

        writeVersion(xml, item);

//        writeRDFType(xml, item);
        writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#Concept");

        writeInScheme(xml, item);
        writeTopConceptOf(xml, item);

        writeDate(xml, item);

//        writeFileFormat(xml, item);
//        writeAuthorityFrequency(xml, item);
//        writeRoles(xml, item);
        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

        writeNarrower(xml, item);
//        writeBroader(xml, item);//WRONG
        writeHasPartsTopConcepts(xml, item);

        writeIsDefinedBy(xml, item);
        writeStatus(xml, item);
        xml.writeEndElement();

        //        write formats by selected language
//        writeFormats(xml, item);
//        write items
        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci);
            }
        }
    }

    private void writeItem(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        xml.writeStartElement(RDF, "Description");
        xml.writeAttribute(RDF, "about", item.getUri());
        writeSimpleElement(xml, DCT, "identifier", RDF, "datatype", "http://www.w3.org/2000/01/rdf-schema#Literal", item.getUri());

        writeRegistryElement(xml, item);
        writeRegisterElement(xml, item);

        writeVersion(xml, item);

        writeRDFType(xml, item);

        writeInScheme(xml, item);
        writeTopConceptOf(xml, item);

        writeDate(xml, item);

        writeFileFormat(xml, item);
        writeAuthorityFrequency(xml, item);

        writeRoles(xml, item);

        writeTitle(xml, item);
        writeDefinition(xml, item);
        writeDescription(xml, item);

//        writeNarrower(xml, item);
//        writeBroader(xml, item);//WRONG
        writeHasPartsTopConcepts(xml, item);

        writeIsDefinedBy(xml, item);
        writeStatus(xml, item);
        xml.writeEndElement();

        writeFormats(xml, item);

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {
            for (ContainedItem ci : item.getContainedItems()) {
                writeItemShortVersion(xml, ci);
            }
        } else if (item.getContainedItemsBeeingParentItemClass() != null && !item.getContainedItemsBeeingParentItemClass().isEmpty()) {
            for (ContainedItem ci : item.getContainedItemsBeeingParentItemClass()) {
                writeItemShortVersion(xml, ci);
            }
        }
    }

    private void writeRDFType(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Catalog");
                writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/2004/02/skos/core#ConceptScheme");
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, RDF, "type", RDF, "resource", "http://www.w3.org/ns/dcat#Dataset");
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

    private void writeDate(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeSimpleElement(xml, DCT, "created", RDF, "datatype", "http://www.w3.org/2001/XMLSchema#date", item.getInsertdate());

        if (item.getEditdate() != null) {
            writeSimpleElement(xml, DCT, "issued", RDF, "datatype", "http://www.w3.org/2001/XMLSchema#date", item.getEditdate());
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
        writeSimpleElement(xml, DCT, "title", NS_XML, "lang", lang, title.getValues().get(0).getValue());
        writeSimpleElement(xml, SKOS, "prefLabel", NS_XML, "lang", lang, title.getValues().get(0).getValue());
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
        writeSimpleElement(xml, DCT, "description", NS_XML, "lang", lang, description);
        writeSimpleElement(xml, SKOS, "definition", NS_XML, "lang", lang, description);
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
                xml.writeStartElement(DCAT, "contactPoint");
                xml.writeStartElement(VCARD, "Kind");
                writeSimpleElement(xml, VCARD, "fn", NS_XML, "lang", lang, value);
                writeEmptyElement(xml, VCARD, "hasEmail", RDF, "resource", href);
                xml.writeEndElement();
                xml.writeEndElement();
            } else {
                xml.writeStartElement(DCT, localName);
                xml.writeStartElement(FOAF, "Agent");
                writeSimpleElement(xml, FOAF, "name", NS_XML, "lang", lang, value);
                xml.writeEndElement();
                xml.writeEndElement();
            }

        }
    }

    private void writeHasPartsTopConcepts(XMLStreamWriter xml, ContainedItem containedItem) throws XMLStreamException {
        switch (containedItem.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY: {
            }
            break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, SKOS, "hasTopConcept", RDF, "resource", containedItem.getRegistry().getUri());
                break;
            default:
                if (containedItem.getTopConceptOf() != null) {
                    writeEmptyElement(xml, SKOS, "hasTopConcept", RDF, "resource", containedItem.getTopConceptOf().getUri());
                }
                break;
        }   
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

    private void writeNarrower(XMLStreamWriter xml, ContainedItem containedItem) throws XMLStreamException {
        List<ContainedItem> narrowerList = containedItem.getNarrower();
        if (narrowerList != null && !narrowerList.isEmpty()) {
            for (BasicContainedItem narrower : narrowerList) {
                writeEmptyElement(xml, SKOS, "narrower", RDF, "resource", narrower.getUri());
            }
        }
    }

    private void writeBroader(XMLStreamWriter xml, ContainedItem containedItem) throws XMLStreamException {
        List<BasicContainedItem> broaderList = containedItem.getBroader();
        if (broaderList != null && !broaderList.isEmpty()) {
            for (BasicContainedItem broader : broaderList) {
                writeEmptyElement(xml, SKOS, "broader", RDF, "resource", broader.getUri());
            }
        }
    }

    private void writeInScheme(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, SKOS, "inScheme", RDF, "resource", item.getRegistry().getUri());
                break;
            default:
                if (item.getInScheme() != null && item.getInScheme().getUri() != null) {
                    writeEmptyElement(xml, SKOS, "inScheme", RDF, "resource", item.getInScheme().getUri());
                }
                break;
        }
    }

    private void writeTopConceptOf(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        if (item.getTopConcepts() != null) {
            for (BasicContainedItem ci : item.getTopConcepts()) {
                switch (item.getType()) {
                    case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY: {
                        writeEmptyElement(xml, DCAT, "dataset", RDF, "resource", ci.getUri());
                    }
                    break;
                    default:
                        writeEmptyElement(xml, DCT, "hasPart", RDF, "resource", ci.getUri());
                        break;
                }
            }
            for (BasicContainedItem ci : item.getTopConcepts()) {
                writeEmptyElement(xml, SKOS, "topConceptOf", RDF, "resource", ci.getUri());
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
                    for (ContainedItem basicContainedItem : referenceList) {
                        writeEmptyElement(xml, RDFS, "isDefinedBy", RDF, "resource", basicContainedItem.getUri());
                    }
                }
                break;
        }
    }

    private void writeInScheme(XMLStreamWriter xml, ContainedItem containedItem, ContainedItem item) throws XMLStreamException {
        switch (item.getType()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY: {
                writeEmptyElement(xml, SKOS, "inScheme", RDF, "resource", containedItem.getRegistry().getUri());
            }
            break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                writeEmptyElement(xml, SKOS, "inScheme", RDF, "resource", containedItem.getRegister().getUri());
                break;
            default:
                writeEmptyElement(xml, SKOS, "inScheme", RDF, "resource", item.getUri());
                break;
        }
    }

    private void writeFormats(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        String identifierPart;
        if (item.isExternal()) {
            identifierPart = item.getUri() + "." + languageFile.getIso6391code() + ".";
        } else {
            xml.writeStartElement(RDF, "Description");
            xml.writeAttribute(RDF, "about", item.getUri());
            writeEmptyElement(xml, FOAF, "primaryTopic", RDF, "resource", item.getUri());
            identifierPart = item.getUri() + "/" + item.getLocalid() + "." + languageFile.getIso6391code() + ".";

            AvailableFormatsUtil availableFormatsUtil = new AvailableFormatsUtil();
            final List<String> availableFormatsList = availableFormatsUtil.getFormatList();
            for (String format : availableFormatsList) {
                writeEmptyElement(xml, DCT, "hasFormat", RDF, "resource", identifierPart + format);
            }

            xml.writeEndElement();

            for (String format : availableFormatsList) {
                xml.writeStartElement(RDF, "Description");
                xml.writeAttribute(RDF, "about", identifierPart + format);
                writeSimpleElement(xml, RDFS, "label", NS_XML, "lang", languageFile.getIso6391code(), format.toUpperCase());
                writeEmptyElement(xml, DCT, "format", RDF, "resource", "http://publications.europa.eu/resource/authority/file-type/" + format.toUpperCase());
                writeEmptyElement(xml, DCT, "language", RDF, "resource", "http://publications.europa.eu/resource/authority/file-type/" + languageFile.getIso6392code().toUpperCase());
                xml.writeEndElement();
            }
        }
    }

    private void writeStatus(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        Optional<LocalizedProperty> maybeStatus = item.getProperty("status");
        if (!maybeStatus.isPresent()) {
            return;
        }
        LocalizedProperty desc = maybeStatus.get();
        String lang = desc.getLang();
        String status = desc.getValues().stream()
                .findAny().map(it -> it.getHref()).orElse(null);
        if (status == null || status.isEmpty()) {
            return;
        }

        writeEmptyElement(xml, ADMS, "status", RDF, "resource", status);
    }

    private void writeFileFormat(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, DCT, "format", RDF, "resource", "http://www.iana.org/assignments/media-types/application/rdf+xml");
    }

    private void writeAuthorityFrequency(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        writeEmptyElement(xml, DCT, "accrualPeriodicity", RDF, "resource", "http://publications.europa.eu/resource/authority/frequency/UNKNOWN");
    }

    private void writeVersion(XMLStreamWriter xml, ContainedItem item) throws XMLStreamException {
        List<VersionInformation> versionHistory = item.getVersionHistory();
        VersionInformation version = item.getVersion();
        if (version!=null && version.getUri()!=null) {  
            writeEmptyElement(xml, OWL, "sameAs", RDF, "resource", version.getUri());
            writeEmptyElement(xml, ADMS, "last", RDF, "resource", item.getUri());
        }
        if (versionHistory != null && !versionHistory.isEmpty()) {
            for (VersionInformation versionInformation : versionHistory) {
                writeEmptyElement(xml, ADMS, "prev", RDF, "resource", versionInformation.getUri());
            }
        }
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
