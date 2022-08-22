/*
 * Copyright 2010,2015 EUROPEAN UNION
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
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.model;

import eu.europa.ec.ror.utility.StringUtils;
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
@Table(name = "descriptor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descriptor.findAll", query = "SELECT d FROM Descriptor d"),
    @NamedQuery(name = "Descriptor.findByUuid", query = "SELECT d FROM Descriptor d WHERE d.uuid = :uuid"),
    @NamedQuery(name = "Descriptor.findByUrl", query = "SELECT d FROM Descriptor d WHERE d.url = :url"),
    @NamedQuery(name = "Descriptor.findByDescriptortype", query = "SELECT d FROM Descriptor d WHERE d.descriptortype = :descriptortype"),
    @NamedQuery(name = "Descriptor.findByDbcreationdate", query = "SELECT d FROM Descriptor d WHERE d.dbcreationdate = :dbcreationdate"),
	
    // Custom queries
    @NamedQuery(name = "Descriptor.findByOrganization", query = "SELECT d FROM Descriptor d WHERE d.organization = :organization"),
    @NamedQuery(name = "Descriptor.findByParentDescriptor", query = "SELECT d FROM Descriptor d WHERE d.parentdescriptor = :descriptor")
})
public class Descriptor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 400)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "descriptortype")
    private String descriptortype;
    @Column(name = "dbcreationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dbcreationdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "descriptor", orphanRemoval = true)
    private List<Registry> registryList;
    @OneToMany(mappedBy = "parentdescriptor")
    private List<Descriptor> descriptorList;
    @JoinColumn(name = "parentdescriptor", referencedColumnName = "uuid")
    @ManyToOne
    private Descriptor parentdescriptor;
    @JoinColumn(name = "organization", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Organization organization;
    @JoinColumn(name = "addedby", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private User addedby;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "descriptor")
    private List<Procedure> procedureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "descriptor")
    private List<Register> registerList;

    public Descriptor() {
    }

    public Descriptor(String uuid) {
        this.uuid = uuid;
    }

    public Descriptor(String uuid, String url, String descriptortype) {
        this.uuid = uuid;
        this.url = url;
        this.descriptortype = descriptortype;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescriptortype() {
        return descriptortype;
    }

    public void setDescriptortype(String descriptortype) {
        this.descriptortype = descriptortype;
    }

    public Date getDbcreationdate() {
        return dbcreationdate;
    }

    public void setDbcreationdate(Date dbcreationdate) {
        this.dbcreationdate = dbcreationdate;
    }

    @XmlTransient
    public List<Registry> getRegistryList() {
        return registryList;
    }

    public void setRegistryList(List<Registry> registryList) {
        this.registryList = registryList;
    }

    @XmlTransient
    public List<Descriptor> getDescriptorList() {
        return descriptorList;
    }

    public void setDescriptorList(List<Descriptor> descriptorList) {
        this.descriptorList = descriptorList;
    }

    public Descriptor getParentdescriptor() {
        return parentdescriptor;
    }

    public void setParentdescriptor(Descriptor parentdescriptor) {
        this.parentdescriptor = parentdescriptor;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getAddedby() {
        return addedby;
    }

    public void setAddedby(User addedby) {
        this.addedby = addedby;
    }

    @XmlTransient
    public List<Procedure> getProcedureList() {
        return procedureList;
    }

    public void setProcedureList(List<Procedure> procedureList) {
        this.procedureList = procedureList;
    }

    @XmlTransient
    public List<Register> getRegisterList() {
        return registerList;
    }

    public void setRegisterList(List<Register> registerList) {
        this.registerList = registerList;
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
        if (!(object instanceof Descriptor)) {
            return false;
        }
        Descriptor other = (Descriptor) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.Descriptor[ uuid=" + uuid + " ]";
    }
	
	public String createUUID(String url) throws Exception{
        return StringUtils.createUUID("Descriptor_" + url);
    }
    
}
