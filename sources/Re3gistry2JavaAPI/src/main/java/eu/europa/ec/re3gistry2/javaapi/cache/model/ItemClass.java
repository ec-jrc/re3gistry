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
package eu.europa.ec.re3gistry2.javaapi.cache.model;

import java.util.HashMap;

public class ItemClass {

    private String id;
    private String type;
    private String baseuri;
    private String parentid;
    private String parentItemClassType;
    private String childItemClassType;
    private String childItemClassLocalId;
    private String startElement;
    private String registerLocalId;
    private HashMap<String, String> fields;
    private HashMap<String, String> containedItemsFields;

    public ItemClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseuri() {
        return baseuri;
    }

    public void setBaseuri(String baseuri) {
        this.baseuri = baseuri;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getParentItemClassType() {
        return parentItemClassType;
    }

    public void setParentItemClassType(String parentItemClassType) {
        this.parentItemClassType = parentItemClassType;
    }

    public HashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, String> fields) {
        this.fields = fields;
    }

    public HashMap<String, String> getContainedItemsFields() {
        return containedItemsFields;
    }

    public void setContainedItemsFields(HashMap<String, String> containedItemsFields) {
        this.containedItemsFields = containedItemsFields;
    }

    public String getStartElement() {
        return startElement;
    }

    public void setStartElement(String startElement) {
        this.startElement = startElement;
    }

    public void setChildItemClassType(String childItemClassType) {
        this.childItemClassType = childItemClassType;
    }

    public String getChildItemClassType() {
        return childItemClassType;
    }

    public String getRegisterLocalId() {
        return registerLocalId;
    }

    public void setRegisterLocalId(String registerLocalId) {
        this.registerLocalId = registerLocalId;
    }

    public String getChildItemClassLocalId() {
        return childItemClassLocalId;
    }

    public void setChildItemClassLocalId(String childItemClassLocalId) {
        this.childItemClassLocalId = childItemClassLocalId;
    }

}
