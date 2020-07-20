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
package eu.europa.ec.re3gistry2.model;

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
@Table(name = "reg_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegRole.findAll", query = "SELECT r FROM RegRole r")
    , @NamedQuery(name = "RegRole.findByUuid", query = "SELECT r FROM RegRole r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegRole.findByLocalid", query = "SELECT r FROM RegRole r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegRole.findByName", query = "SELECT r FROM RegRole r WHERE r.name = :name")
    , @NamedQuery(name = "RegRole.findByInsertdate", query = "SELECT r FROM RegRole r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegRole.findByEditdate", query = "SELECT r FROM RegRole r WHERE r.editdate = :editdate")})
public class RegRole implements Serializable {

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
    @Column(name = "localid")
    private String localid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 350)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regRole")
    private List<RegItemhistoryRegGroupRegRoleMapping> regItemhistoryRegGroupRegRoleMappingList;
    @OneToMany(mappedBy = "regRoleReference")
    private List<RegField> regFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regRole")
    private List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regRole")
    private List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappingList;

    public RegRole() {
    }

    public RegRole(String uuid, String localid, String name, Date insertdate) {
        this.uuid = uuid;
        this.localid = localid;
        this.name = name;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @XmlTransient
    public List<RegItemhistoryRegGroupRegRoleMapping> getRegItemhistoryRegGroupRegRoleMappingList() {
        return regItemhistoryRegGroupRegRoleMappingList;
    }

    public void setRegItemhistoryRegGroupRegRoleMappingList(List<RegItemhistoryRegGroupRegRoleMapping> regItemhistoryRegGroupRegRoleMappingList) {
        this.regItemhistoryRegGroupRegRoleMappingList = regItemhistoryRegGroupRegRoleMappingList;
    }

    @XmlTransient
    public List<RegField> getRegFieldList() {
        return regFieldList;
    }

    public void setRegFieldList(List<RegField> regFieldList) {
        this.regFieldList = regFieldList;
    }

    @XmlTransient
    public List<RegItemproposedRegGroupRegRoleMapping> getRegItemproposedRegGroupRegRoleMappingList() {
        return regItemproposedRegGroupRegRoleMappingList;
    }

    public void setRegItemproposedRegGroupRegRoleMappingList(List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappingList) {
        this.regItemproposedRegGroupRegRoleMappingList = regItemproposedRegGroupRegRoleMappingList;
    }

    @XmlTransient
    public List<RegItemRegGroupRegRoleMapping> getRegItemRegGroupRegRoleMappingList() {
        return regItemRegGroupRegRoleMappingList;
    }

    public void setRegItemRegGroupRegRoleMappingList(List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappingList) {
        this.regItemRegGroupRegRoleMappingList = regItemRegGroupRegRoleMappingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegRole)) {
            return false;
        }
        RegRole other = (RegRole) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegRole[ uuid=" + uuid + " ]";
    }

}
