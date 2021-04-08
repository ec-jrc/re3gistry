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
package eu.europa.ec.re3gistry2.restapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;

/**
 * ContainedItem information
 */
public class StatusItem extends BasicContainedItem implements Serializable{

    private String uuid;
    private String uri;
    private String localid;
    private String language;
    private String type;
    private Date insertdate;
    private Date editdate;

    private List<LocalizedProperty> properties;
    private List<StatusItem> containedItems;

    @JsonIgnore
    public String getUuid() {
        return uuid;
    }

    @JsonIgnore
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(Date insertdate) {
        this.insertdate = insertdate;
    }

    public Date getEditdate() {
        return editdate;
    }

    public void setEditdate(Date editdate) {
        this.editdate = editdate;
    }

    public List<LocalizedProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<LocalizedProperty> properties) {
        this.properties = properties;
    }

    public List<StatusItem> getContainedItems() {
        return containedItems;
    }

    public void setContainedItems(List<StatusItem> containedItems) {
        this.containedItems = containedItems;
    }

}
