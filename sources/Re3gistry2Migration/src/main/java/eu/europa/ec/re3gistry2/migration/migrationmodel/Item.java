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
package eu.europa.ec.re3gistry2.migration.migrationmodel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i")
    , @NamedQuery(name = "Item.findByUuid", query = "SELECT i FROM Item i WHERE i.uuid = :uuid")
    , @NamedQuery(name = "Item.findByUriname", query = "SELECT i FROM Item i WHERE i.uriname = :uriname")
    , @NamedQuery(name = "Item.findByVersionnumber", query = "SELECT i FROM Item i WHERE i.versionnumber = :versionnumber")
    , @NamedQuery(name = "Item.findByDatecreation", query = "SELECT i FROM Item i WHERE i.datecreation = :datecreation")
    , @NamedQuery(name = "Item.findByDatelastupdate", query = "SELECT i FROM Item i WHERE i.datelastupdate = :datelastupdate")
    , @NamedQuery(name = "Item.findByDatetimeaddition", query = "SELECT i FROM Item i WHERE i.datetimeaddition = :datetimeaddition")
    , @NamedQuery(name = "Item.findByDatetimesupersession", query = "SELECT i FROM Item i WHERE i.datetimesupersession = :datetimesupersession")
    , @NamedQuery(name = "Item.findByDatetimeretirment", query = "SELECT i FROM Item i WHERE i.datetimeretirment = :datetimeretirment")
    , @NamedQuery(name = "Item.findByDatetimeinvalidation", query = "SELECT i FROM Item i WHERE i.datetimeinvalidation = :datetimeinvalidation")})

@SqlResultSetMapping(
        name = "itemsResult",
        entities = @EntityResult(
                entityClass = Item.class,
                fields = {
                    @FieldResult(name = "uuid", column = "uuid")
                    ,
                    @FieldResult(name = "localid", column = "localid")
                    ,
                    @FieldResult(name = "versionnumber", column = "versionnumber")
                    ,
                    @FieldResult(name = "datecreation", column = "datecreation")
                    ,
                    @FieldResult(name = "datelastupdate", column = "datelastupdate")
                    ,
                    @FieldResult(name = "datetimeaddition", column = "datetimeaddition")
                    ,
                    @FieldResult(name = "datetimesupersession", column = "datetimesupersession")
                    ,
                    @FieldResult(name = "datetimeretirment", column = "datetimeretirment")
                    ,
                    @FieldResult(name = "datetimeinvalidation", column = "datetimeinvalidation")
                    ,
                    @FieldResult(name = "itemclass", column = "itemclass")
                }
        )
)

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "uriname")
    private String uriname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "versionnumber")
    private int versionnumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @Column(name = "datetimeaddition")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeaddition;
    @Column(name = "datetimesupersession")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimesupersession;
    @Column(name = "datetimeretirment")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeretirment;
    @Column(name = "datetimeinvalidation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeinvalidation;
    @OneToMany(mappedBy = "item")
    private List<Localization> localizationList;
    @JoinColumn(name = "itemclass", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Itemclass itemclass;
    @JoinColumn(name = "status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Status status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Changelog> changelogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Customattributevalue> customattributevalueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Itemsuccessor> itemsuccessorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "successor")
    private List<Itemsuccessor> itemsuccessorList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collection")
    private List<Itemcollection> itemcollectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Itemcollection> itemcollectionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Itemparent> itemparentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<Itemparent> itemparentList1;

    public Item() {
    }

    public Item(String uuid, String uriname, int versionnumber, Date datecreation) {
        this.uuid = uuid;
        this.uriname = uriname;
        this.versionnumber = versionnumber;
        this.datecreation = datecreation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUriname() {
        return uriname;
    }

    public void setUriname(String uriname) {
        this.uriname = uriname;
    }

    public int getVersionnumber() {
        return versionnumber;
    }

    public void setVersionnumber(int versionnumber) {
        this.versionnumber = versionnumber;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public Date getDatelastupdate() {
        return datelastupdate;
    }

    public void setDatelastupdate(Date datelastupdate) {
        this.datelastupdate = datelastupdate;
    }

    public Date getDatetimeaddition() {
        return datetimeaddition;
    }

    public void setDatetimeaddition(Date datetimeaddition) {
        this.datetimeaddition = datetimeaddition;
    }

    public Date getDatetimesupersession() {
        return datetimesupersession;
    }

    public void setDatetimesupersession(Date datetimesupersession) {
        this.datetimesupersession = datetimesupersession;
    }

    public Date getDatetimeretirment() {
        return datetimeretirment;
    }

    public void setDatetimeretirment(Date datetimeretirment) {
        this.datetimeretirment = datetimeretirment;
    }

    public Date getDatetimeinvalidation() {
        return datetimeinvalidation;
    }

    public void setDatetimeinvalidation(Date datetimeinvalidation) {
        this.datetimeinvalidation = datetimeinvalidation;
    }

    @XmlTransient
    public List<Localization> getLocalizationList() {
        return localizationList;
    }

    public void setLocalizationList(List<Localization> localizationList) {
        this.localizationList = localizationList;
    }

    public Itemclass getItemclass() {
        return itemclass;
    }

    public void setItemclass(Itemclass itemclass) {
        this.itemclass = itemclass;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @XmlTransient
    public List<Changelog> getChangelogList() {
        return changelogList;
    }

    public void setChangelogList(List<Changelog> changelogList) {
        this.changelogList = changelogList;
    }

    @XmlTransient
    public List<Customattributevalue> getCustomattributevalueList() {
        return customattributevalueList;
    }

    public void setCustomattributevalueList(List<Customattributevalue> customattributevalueList) {
        this.customattributevalueList = customattributevalueList;
    }

    @XmlTransient
    public List<Itemsuccessor> getItemsuccessorList() {
        return itemsuccessorList;
    }

    public void setItemsuccessorList(List<Itemsuccessor> itemsuccessorList) {
        this.itemsuccessorList = itemsuccessorList;
    }

    @XmlTransient
    public List<Itemsuccessor> getItemsuccessorList1() {
        return itemsuccessorList1;
    }

    public void setItemsuccessorList1(List<Itemsuccessor> itemsuccessorList1) {
        this.itemsuccessorList1 = itemsuccessorList1;
    }

    @XmlTransient
    public List<Itemcollection> getItemcollectionList() {
        return itemcollectionList;
    }

    public void setItemcollectionList(List<Itemcollection> itemcollectionList) {
        this.itemcollectionList = itemcollectionList;
    }

    @XmlTransient
    public List<Itemcollection> getItemcollectionList1() {
        return itemcollectionList1;
    }

    public void setItemcollectionList1(List<Itemcollection> itemcollectionList1) {
        this.itemcollectionList1 = itemcollectionList1;
    }

    @XmlTransient
    public List<Itemparent> getItemparentList() {
        return itemparentList;
    }

    public void setItemparentList(List<Itemparent> itemparentList) {
        this.itemparentList = itemparentList;
    }

    @XmlTransient
    public List<Itemparent> getItemparentList1() {
        return itemparentList1;
    }

    public void setItemparentList1(List<Itemparent> itemparentList1) {
        this.itemparentList1 = itemparentList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Item[ uuid=" + uuid + " ]";
    }

}
