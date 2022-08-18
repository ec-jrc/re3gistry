/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.web.utility;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.ItemHelper;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.web.controller.RegisterManager;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UpdateRSS {

    public UpdateRSS() {
    }

    public static void updateRSS(RegAction regAction, List<RegItem> regItems) throws Exception {
        Date regActionInsertDate = regAction.getInsertdate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(regActionInsertDate);
        try {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String pathRSS = properties.getProperty(BaseConstants.KEY_RELEASENOTE_RSS_APPLICATION) + WebConstants.PAGE_PATH_RSS + WebConstants.PAGE_URINAME_RSS + WebConstants.PAGE_RSS_EXTENSION;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(pathRSS));

            NodeList nList = document.getElementsByTagName("item");

            boolean itemExist = false;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;

                    if (strDate.equals(eElement.getAttribute("id"))) {
                        updateInformationToItemRSS(document, eElement, regAction, regItems);
                        itemExist = true;
                        break;
                    }
                }
            }

            if (!itemExist) {
                createItemRSS(document, regAction, regItems);
            }

            //for output to file, console
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);

            //write to file
            StreamResult file = new StreamResult(new File(pathRSS));

            //write data
            transformer.transform(source, file);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            java.util.logging.Logger.getLogger(RegisterManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void updateInformationToItemRSS(Document document, Element eElement, RegAction regAction, List<RegItem> regItems) throws Exception {
        NodeList descriptionNodeList = eElement.getElementsByTagName("description");
        Element descriptionElement = (Element) descriptionNodeList.item(0);
        String description = descriptionNodeList.item(0).getTextContent().replace("<![CDATA[", "").replace("]]>", "");
        String newActionText = "<p>Changes for action: " + (regAction.getChangelog() == null ? "The registry item has been updated." : regAction.getChangelog()) + "</p>\n";
        newActionText += "<ul>";
        for (RegItem regItem : regItems) {
            newActionText += "<li> " + ItemHelper.getURI(regItem) + "</li>\n";
        }
        newActionText += "</ul>";
        
        Element descriptionNewElement = document.createElement("description");
        descriptionNewElement.appendChild(document.createCDATASection(newActionText.concat(description)));
        
        eElement.replaceChild(descriptionNewElement, descriptionElement);
    }

    private static void createItemRSS(Document document, RegAction regAction, List<RegItem> regItems) throws Exception {
        Element root = document.getDocumentElement();
        Element itemElement = document.createElement("item");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(regAction.getInsertdate());
        itemElement.setAttribute("id", strDate);

        Element titleElement = document.createElement("title");
        titleElement.setTextContent("Release: " + strDate);
        itemElement.appendChild(titleElement);

        Element descriptionElement = document.createElement("description");
        String itemsDescription = "<p>Changes for action: " + (regAction.getChangelog() == null ? "The registry item has been updated." : regAction.getChangelog()) + "</p>\n";
        itemsDescription += "<ul>";
        for (RegItem regItem : regItems) {
            itemsDescription += "<li>" + ItemHelper.getURI(regItem) + "</li>\n";
        }
        itemsDescription += "</ul> \n";

        descriptionElement.appendChild(document.createCDATASection(itemsDescription));
        itemElement.appendChild(descriptionElement);

        NodeList channelNodeList = document.getElementsByTagName("channel");
        Node channel = channelNodeList.item(0);
        channel.getParentNode().insertBefore(itemElement, channel.getNextSibling());
    }

}
