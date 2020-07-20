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
@Table(name = "customattribute")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customattribute.findAll", query = "SELECT c FROM Customattribute c")
    , @NamedQuery(name = "Customattribute.findByUuid", query = "SELECT c FROM Customattribute c WHERE c.uuid = :uuid")
    , @NamedQuery(name = "Customattribute.findByName", query = "SELECT c FROM Customattribute c WHERE c.name = :name")
    , @NamedQuery(name = "Customattribute.findByMultivalue", query = "SELECT c FROM Customattribute c WHERE c.multivalue = :multivalue")
    , @NamedQuery(name = "Customattribute.findByCoded", query = "SELECT c FROM Customattribute c WHERE c.coded = :coded")
    , @NamedQuery(name = "Customattribute.findByDatecreation", query = "SELECT c FROM Customattribute c WHERE c.datecreation = :datecreation")
    , @NamedQuery(name = "Customattribute.findByDatelastupdate", query = "SELECT c FROM Customattribute c WHERE c.datelastupdate = :datelastupdate")})
public class Customattribute implements Serializable {

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
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "multivalue")
    private boolean multivalue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "coded")
    private boolean coded;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customattribute")
    private List<Customattributecode> customattributecodeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customattribute")
    private List<Customattributevalue> customattributevalueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customattribute")
    private List<Itemclasscustomattribute> itemclasscustomattributeList;

    public Customattribute() {
    }

    public Customattribute(String uuid, String name, boolean multivalue, boolean coded, Date datecreation) {
        this.uuid = uuid;
        this.name = name;
        this.multivalue = multivalue;
        this.coded = coded;
        this.datecreation = datecreation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    public boolean getCoded() {
        return coded;
    }

    public void setCoded(boolean coded) {
        this.coded = coded;
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
    public List<Customattributecode> getCustomattributecodeList() {
        return customattributecodeList;
    }

    public void setCustomattributecodeList(List<Customattributecode> customattributecodeList) {
        this.customattributecodeList = customattributecodeList;
    }

    @XmlTransient
    public List<Customattributevalue> getCustomattributevalueList() {
        return customattributevalueList;
    }

    public void setCustomattributevalueList(List<Customattributevalue> customattributevalueList) {
        this.customattributevalueList = customattributevalueList;
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
        
        if (!(object instanceof Customattribute)) {
            return false;
        }
        Customattribute other = (Customattribute) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Customattribute[ uuid=" + uuid + " ]";
    }

}
