/*
 * Copyright 2010,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-info@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.model;

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
@Table(name = "roruser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUuid", query = "SELECT u FROM User u WHERE u.uuid = :uuid"),
    @NamedQuery(name = "User.findByEcasid", query = "SELECT u FROM User u WHERE u.ecasid = :ecasid"),
    @NamedQuery(name = "User.findByUserlevel", query = "SELECT u FROM User u WHERE u.userlevel = :userlevel"),
    @NamedQuery(name = "User.findByActive", query = "SELECT u FROM User u WHERE u.active = :active"),
    @NamedQuery(name = "User.findByDbcreationdate", query = "SELECT u FROM User u WHERE u.dbcreationdate = :dbcreationdate"),
    @NamedQuery(name = "User.findByDblasteditdate", query = "SELECT u FROM User u WHERE u.dblasteditdate = :dblasteditdate")})
public class User implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "startedby")
    private List<Procedurehistory> procedurehistoryList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "ecasid")
    private String ecasid;
    @Column(name = "userlevel")
    private Integer userlevel;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "dbcreationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dbcreationdate;
    @Column(name = "dblasteditdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dblasteditdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "addedby")
    private List<Descriptor> descriptorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "startedby")
    private List<Procedure> procedureList;
    @JoinColumn(name = "organization", referencedColumnName = "uuid")
    @ManyToOne
    private Organization organization;

    public User() {
    }

    public User(String uuid) {
        this.uuid = uuid;
    }

    public User(String uuid, String ecasid) {
        this.uuid = uuid;
        this.ecasid = ecasid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEcasid() {
        return ecasid;
    }

    public void setEcasid(String ecasid) {
        this.ecasid = ecasid;
    }

    public Integer getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(Integer userlevel) {
        this.userlevel = userlevel;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getDbcreationdate() {
        return dbcreationdate;
    }

    public void setDbcreationdate(Date dbcreationdate) {
        this.dbcreationdate = dbcreationdate;
    }

    public Date getDblasteditdate() {
        return dblasteditdate;
    }

    public void setDblasteditdate(Date dblasteditdate) {
        this.dblasteditdate = dblasteditdate;
    }

    @XmlTransient
    public List<Descriptor> getDescriptorList() {
        return descriptorList;
    }

    public void setDescriptorList(List<Descriptor> descriptorList) {
        this.descriptorList = descriptorList;
    }

    @XmlTransient
    public List<Procedure> getProcedureList() {
        return procedureList;
    }

    public void setProcedureList(List<Procedure> procedureList) {
        this.procedureList = procedureList;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.User[ uuid=" + uuid + " ]";
    }

    @XmlTransient
    public List<Procedurehistory> getProcedurehistoryList() {
        return procedurehistoryList;
    }

    public void setProcedurehistoryList(List<Procedurehistory> procedurehistoryList) {
        this.procedurehistoryList = procedurehistoryList;
    }
    
}
