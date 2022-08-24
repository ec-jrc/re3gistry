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
package eu.europa.ec.re3gistry2.javaapi.cache.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

/**
 * ContainedItem information
 */
public class ContainedItem extends BasicContainedItem implements Serializable {

    private String uuid;
    private String uri;
    private String localid;
    private VersionInformation version;
    private List<VersionInformation> versionHistory;
    private String language;
    private String type;
    private boolean hasCollection;
    private boolean isParent;
    private boolean latest = false;
    private boolean external;
    private String insertdate;
    private String editdate;
    private BasicItemClass itemclass;
    private ItemRef registry;
    private ItemRef register;

    private BasicContainedItem inScheme;
    private BasicContainedItem topConceptOf;
    private List<ContainedItem> isDefinedBy;

    private List<LocalizedProperty> properties;

    private List<ContainedItem> containedItemsBeeingParentItemClass;
    private List<ContainedItem> containedItems;
    private List<BasicContainedItem> narrower;
    private List<BasicContainedItem> broader;
    private List<BasicContainedItem> topConcepts;

    @JsonIgnore
    public String getUuid() {
        return uuid;
    }

    @JsonIgnore
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri.replace("\\/", "/");
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

    public VersionInformation getVersion() {
        return version;
    }

    public void setVersion(VersionInformation version) {
        this.version = version;
    }

    public List<VersionInformation> getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(List<VersionInformation> versionHistory) {
        this.versionHistory = versionHistory;
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

    public boolean isHasCollection() {
        return hasCollection;
    }

    public void setHasCollection(boolean hasCollection) {
        this.hasCollection = hasCollection;
    }

    public boolean isIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(String insertdate) {
        this.insertdate = insertdate;
    }

    public String getEditdate() {
        return editdate;
    }

    public void setEditdate(String editdate) {
        this.editdate = editdate;
    }

    public BasicItemClass getItemclass() {
        return itemclass;
    }

    public void setItemclass(BasicItemClass itemclass) {
        this.itemclass = itemclass;
    }

    public ItemRef getRegistry() {
        return registry;
    }

    public void setRegistry(ItemRef registry) {
        this.registry = registry;
    }

    public ItemRef getRegister() {
        return register;
    }

    public void setRegister(ItemRef register) {
        this.register = register;
    }

    public BasicContainedItem getInScheme() {
        return inScheme;
    }

    public void setInScheme(BasicContainedItem inScheme) {
        this.inScheme = inScheme;
    }

    public BasicContainedItem getTopConceptOf() {
        return topConceptOf;
    }

    public void setTopConceptOf(BasicContainedItem topConceptOf) {
        this.topConceptOf = topConceptOf;
    }

    public List<LocalizedProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<LocalizedProperty> properties) {
        this.properties = properties;
    }

    public Optional<LocalizedProperty> getProperty(String localId) {
        if (properties == null) {
            return Optional.empty();
        }
        return properties.stream()
                .filter(l -> l.getId().equals(localId))
                .findAny();
    }

    public List<ContainedItem> getContainedItemsBeeingParentItemClass() {
        return containedItemsBeeingParentItemClass;
    }

    public void setContainedItemsBeeingParentItemClass(List<ContainedItem> containedItemsBeeingParentItemClass) {
        this.containedItemsBeeingParentItemClass = containedItemsBeeingParentItemClass;
    }

    public List<ContainedItem> getContainedItems() {
        return containedItems;
    }

    public void setContainedItems(List<ContainedItem> containedItems) {
        this.containedItems = containedItems;
    }

    public List<BasicContainedItem> getNarrower() {
        return narrower;
    }

    public void setNarrower(List<BasicContainedItem> narrower) {
        this.narrower = narrower;
    }

    public List<BasicContainedItem> getBroader() {
        return broader;
    }

    public void setBroader(List<BasicContainedItem> broader) {
        this.broader = broader;
    }

    public List<BasicContainedItem> getTopConcepts() {
        return topConcepts;
    }

    public void setTopConcepts(List<BasicContainedItem> topConcepts) {
        this.topConcepts = topConcepts;
    }

    public void setIsDefinedBy(List<ContainedItem> isDefinedBy) {
        this.isDefinedBy = isDefinedBy;
    }

    public List<ContainedItem> getIsDefinedBy() {
        return isDefinedBy;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

}
