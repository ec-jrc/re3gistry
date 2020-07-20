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
package eu.europa.ec.re3gistry2.restapi.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class IndentingXMLStreamWriter extends XMLStreamWriterWrapper {

    private static final String DEFAULT_NEW_LINE = "\n";
    private static final String DEFAULT_INDENT = "  ";

    private final String newLine;
    private final String indent;

    private boolean shouldWriteEndElementAtNextLine;
    private int depth;

    public IndentingXMLStreamWriter(XMLStreamWriter wrapped) {
        this(wrapped, DEFAULT_NEW_LINE, DEFAULT_INDENT);
    }

    public IndentingXMLStreamWriter(XMLStreamWriter wrapped, String newLine, String indent) {
        super(wrapped);
        this.newLine = newLine;
        this.indent = indent;
    }

    private void onStartElement() throws XMLStreamException {
        shouldWriteEndElementAtNextLine = false;
        if (depth > 0) {
            doIndent();
        }
        depth++;
    }

    private void onEndElement() throws XMLStreamException {
        depth--;
        if (shouldWriteEndElementAtNextLine) {
            doIndent();
        } else {
            shouldWriteEndElementAtNextLine = true;
        }
    }

    private void onEmptyElement() throws XMLStreamException {
        shouldWriteEndElementAtNextLine = true;
        if (depth > 0) {
            doIndent();
        }
    }

    private void doIndent() throws XMLStreamException {
        super.writeCharacters(newLine);
        for (int i = 0; i < depth; i++) {
            super.writeCharacters(indent);
        }
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        super.writeStartDocument();
        super.writeCharacters(newLine);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        super.writeStartDocument(version);
        super.writeCharacters(newLine);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        super.writeStartDocument(encoding, version);
        super.writeCharacters(newLine);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        onStartElement();
        super.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        onStartElement();
        super.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        onStartElement();
        super.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        onEmptyElement();
        super.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        onEmptyElement();
        super.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        onEmptyElement();
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        onEndElement();
        super.writeEndElement();
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        shouldWriteEndElementAtNextLine = false;
        super.writeCData(data);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        shouldWriteEndElementAtNextLine = false;
        super.writeCharacters(text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        shouldWriteEndElementAtNextLine = false;
        super.writeCharacters(text, start, len);
    }

}
