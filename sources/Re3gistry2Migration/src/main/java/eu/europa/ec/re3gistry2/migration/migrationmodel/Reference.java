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
@Table(name = "reference")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reference.findAll", query = "SELECT r FROM Reference r")
    , @NamedQuery(name = "Reference.findByUuid", query = "SELECT r FROM Reference r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "Reference.findByEmail", query = "SELECT r FROM Reference r WHERE r.email = :email")
    , @NamedQuery(name = "Reference.findByReftype", query = "SELECT r FROM Reference r WHERE r.reftype = :reftype")
    , @NamedQuery(name = "Reference.findByDatecreation", query = "SELECT r FROM Reference r WHERE r.datecreation = :datecreation")
    , @NamedQuery(name = "Reference.findByDatelastupdate", query = "SELECT r FROM Reference r WHERE r.datelastupdate = :datelastupdate")})
public class Reference implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 300)
    @Column(name = "email")
    private String email;
    @Size(max = 300)
    @Column(name = "reftype")
    private String reftype;
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @OneToMany(mappedBy = "reference")
    private List<Localization> localizationList;
    @OneToMany(mappedBy = "registrymanager")
    private List<Registry> registryList;
    @OneToMany(mappedBy = "contactpoint")
    private List<Register> registerList;
    @OneToMany(mappedBy = "license")
    private List<Register> registerList1;
    @OneToMany(mappedBy = "registercontrolbody")
    private List<Register> registerList2;
    @OneToMany(mappedBy = "registermanager")
    private List<Register> registerList3;
    @OneToMany(mappedBy = "registerowner")
    private List<Register> registerList4;
    @OneToMany(mappedBy = "submitter")
    private List<Register> registerList5;

    public Reference() {
    }

    public Reference(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReftype() {
        return reftype;
    }

    public void setReftype(String reftype) {
        this.reftype = reftype;
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
    public List<Registry> getRegistryList() {
        return registryList;
    }

    public void setRegistryList(List<Registry> registryList) {
        this.registryList = registryList;
    }

    @XmlTransient
    public List<Register> getRegisterList() {
        return registerList;
    }

    public void setRegisterList(List<Register> registerList) {
        this.registerList = registerList;
    }

    @XmlTransient
    public List<Register> getRegisterList1() {
        return registerList1;
    }

    public void setRegisterList1(List<Register> registerList1) {
        this.registerList1 = registerList1;
    }

    @XmlTransient
    public List<Register> getRegisterList2() {
        return registerList2;
    }

    public void setRegisterList2(List<Register> registerList2) {
        this.registerList2 = registerList2;
    }

    @XmlTransient
    public List<Register> getRegisterList3() {
        return registerList3;
    }

    public void setRegisterList3(List<Register> registerList3) {
        this.registerList3 = registerList3;
    }

    @XmlTransient
    public List<Register> getRegisterList4() {
        return registerList4;
    }

    public void setRegisterList4(List<Register> registerList4) {
        this.registerList4 = registerList4;
    }

    @XmlTransient
    public List<Register> getRegisterList5() {
        return registerList5;
    }

    public void setRegisterList5(List<Register> registerList5) {
        this.registerList5 = registerList5;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Reference)) {
            return false;
        }
        Reference other = (Reference) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Reference[ uuid=" + uuid + " ]";
    }

}
