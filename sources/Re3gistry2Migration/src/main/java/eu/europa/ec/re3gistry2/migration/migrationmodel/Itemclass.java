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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "itemclass")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itemclass.findAll", query = "SELECT i FROM Itemclass i")
    , @NamedQuery(name = "Itemclass.findByUuid", query = "SELECT i FROM Itemclass i WHERE i.uuid = :uuid")
    , @NamedQuery(name = "Itemclass.findByUriname", query = "SELECT i FROM Itemclass i WHERE i.uriname = :uriname")
    , @NamedQuery(name = "Itemclass.findByOrdernumber", query = "SELECT i FROM Itemclass i WHERE i.ordernumber = :ordernumber")
    , @NamedQuery(name = "Itemclass.findByDataprocedureorder", query = "SELECT i FROM Itemclass i WHERE i.dataprocedureorder = :dataprocedureorder")
    , @NamedQuery(name = "Itemclass.findByDatecreation", query = "SELECT i FROM Itemclass i WHERE i.datecreation = :datecreation")
    , @NamedQuery(name = "Itemclass.findByDatelastupdate", query = "SELECT i FROM Itemclass i WHERE i.datelastupdate = :datelastupdate")})
public class Itemclass implements Serializable {

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
    @Column(name = "ordernumber")
    private int ordernumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dataprocedureorder")
    private int dataprocedureorder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @OneToMany(mappedBy = "itemclass")
    private List<Localization> localizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemclass")
    private List<Item> itemList;
    @OneToMany(mappedBy = "parent")
    private List<Itemclass> itemclassList;
    @JoinColumn(name = "parent", referencedColumnName = "uuid")
    @ManyToOne
    private Itemclass parent;
    @JoinColumn(name = "register", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Register register;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemclass")
    private List<Itemclasscustomattribute> itemclasscustomattributeList;

    public Itemclass() {
    }

    public Itemclass(String uuid, String uriname, int ordernumber, int dataprocedureorder, Date datecreation) {
        this.uuid = uuid;
        this.uriname = uriname;
        this.ordernumber = ordernumber;
        this.dataprocedureorder = dataprocedureorder;
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

    public int getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(int ordernumber) {
        this.ordernumber = ordernumber;
    }

    public int getDataprocedureorder() {
        return dataprocedureorder;
    }

    public void setDataprocedureorder(int dataprocedureorder) {
        this.dataprocedureorder = dataprocedureorder;
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

    @XmlTransient
    public List<Localization> getLocalizationList() {
        return localizationList;
    }

    public void setLocalizationList(List<Localization> localizationList) {
        this.localizationList = localizationList;
    }

    @XmlTransient
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    public List<Itemclass> getItemclassList() {
        return itemclassList;
    }

    public void setItemclassList(List<Itemclass> itemclassList) {
        this.itemclassList = itemclassList;
    }

    public Itemclass getParent() {
        return parent;
    }

    public void setParent(Itemclass parent) {
        this.parent = parent;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    @XmlTransient
    public List<Itemclasscustomattribute> getItemclasscustomattributeList() {
        return itemclasscustomattributeList;
    }

    public void setItemclasscustomattributeList(List<Itemclasscustomattribute> itemclasscustomattributeList) {
        this.itemclasscustomattributeList = itemclasscustomattributeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Itemclass)) {
            return false;
        }
        Itemclass other = (Itemclass) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclass[ uuid=" + uuid + " ]";
    }

}
