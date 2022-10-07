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

import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ContainedItem;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.model.LocalizedProperty;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.stream.XMLStreamException;

public class CSVFormatter implements Formatter {

    private static final String XSISCHEMALOCATION = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    private RegLanguagecode languageFile;
    private final String NEW_LINE = "\n";
    private final String PIPE = "|";

    private EntityManager em;

    public CSVFormatter(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    @Override
    public String getFormatName() {
        return "csv";
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public void write(Item item, RegLanguagecode lang, OutputStream out) throws Exception {
        this.languageFile = lang;

        writeItem(out, item);
    }

    private void writeItem(OutputStream out, Item item) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(em);
        RegItemclassManager regItemclassManager = new RegItemclassManager(em);

        String fileContent = "ID|Language";
        List<String> headerFieldsList = new ArrayList<>();

        if (item.getContainedItems() != null && !item.getContainedItems().isEmpty()) {

            ContainedItem firstItem = item.getContainedItems().get(0);

            List<RegFieldmapping> fieldList = regFieldmappingManager.getAllOrderAscByListorder(regItemclassManager.getByLocalid(firstItem.getItemclass().getId()));
            for (RegFieldmapping regFieldmapping : fieldList) {
                String fieldName = regFieldmapping.getRegField().getLocalid();

                fileContent = fileContent + PIPE + fieldName;
                headerFieldsList.add(fieldName);
            }
            fileContent = fileContent + NEW_LINE;

            for (ContainedItem ci : item.getContainedItems()) {
                fileContent = fileContent + ci.getUri() + PIPE + languageFile.getIso6391code();

                for (String filedName : headerFieldsList) {
                    fileContent = fileContent + PIPE + getFieldValue(ci, filedName);
                }
                fileContent = fileContent + NEW_LINE;
                if (ci.isHasCollection()) {
                    if (ci.getContainedItems() != null && !ci.getContainedItems().isEmpty()) {
                        for (ContainedItem containedItem : ci.getContainedItems()) {
                            fileContent = fileContent + containedItem.getUri() + PIPE + languageFile.getIso6391code();

                            for (String filedName : headerFieldsList) {
                                if (null != filedName) {
                                    switch (filedName) {
                                        case "registry":
                                            fileContent = fileContent + PIPE + getRegistryValue(containedItem, filedName);
                                            break;
                                        case "register":
                                            fileContent = fileContent + PIPE + getRegisterValue(containedItem, filedName);
                                            break;
                                        default:
                                            fileContent = fileContent + PIPE + getFieldValue(containedItem, filedName);
                                            break;
                                    }
                                }
                            }
                            fileContent = fileContent + NEW_LINE;
                        }
                    }
                }
            }
        }
        osw.write(fileContent);
        osw.flush();
        osw.close();
    }

    private String getRegistryValue(ContainedItem item, String propertyName) throws XMLStreamException {
        return item.getRegistry().getUri();
    }

    private String getRegisterValue(ContainedItem item, String propertyName) throws XMLStreamException {
        return item.getRegister().getUri();
    }

    private String getFieldValue(ContainedItem item, String propertyName) throws XMLStreamException {
        Optional<LocalizedProperty> optional = item.getProperty(propertyName);
        if (!optional.isPresent() && propertyName.equals("contentsummary")) {
            optional = item.getProperty("definition");
        }
        if (!optional.isPresent()) {
            return "";
        }
        LocalizedProperty localizedProperty = optional.get();
        if (!localizedProperty.getValues().isEmpty()) {
            String value = localizedProperty.getValues().get(0).getValue();
            String href = localizedProperty.getValues().get(0).getHref();
            if (!href.isEmpty()) {
                return href;
            }
            return value;
        } else {
            return "";
        }
    }

    @Override
    public void write(ItemClass itemClass, OutputStream out) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
