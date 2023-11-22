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
@Table(name = "register")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Register.findAll", query = "SELECT r FROM Register r"),
    @NamedQuery(name = "Register.findByUuid", query = "SELECT r FROM Register r WHERE r.uuid = :uuid"),
    @NamedQuery(name = "Register.findByUri", query = "SELECT r FROM Register r WHERE r.uri = :uri"),
    @NamedQuery(name = "Register.findByLabel", query = "SELECT r FROM Register r WHERE r.label = :label"),
    @NamedQuery(name = "Register.findByDescription", query = "SELECT r FROM Register r WHERE r.description = :description"),
    @NamedQuery(name = "Register.findByPublisheruri", query = "SELECT r FROM Register r WHERE r.publisheruri = :publisheruri"),
    @NamedQuery(name = "Register.findByPublishername", query = "SELECT r FROM Register r WHERE r.publishername = :publishername"),
    @NamedQuery(name = "Register.findByPublisheremail", query = "SELECT r FROM Register r WHERE r.publisheremail = :publisheremail"),
    @NamedQuery(name = "Register.findByPublisherhomepage", query = "SELECT r FROM Register r WHERE r.publisherhomepage = :publisherhomepage"),
    @NamedQuery(name = "Register.findByUpdatefrequency", query = "SELECT r FROM Register r WHERE r.updatefrequency = :updatefrequency"),
    @NamedQuery(name = "Register.findByDbcreationdate", query = "SELECT r FROM Register r WHERE r.dbcreationdate = :dbcreationdate"),
    @NamedQuery(name = "Register.findByDblasteditdate", query = "SELECT r FROM Register r WHERE r.dblasteditdate = :dblasteditdate"),
    
    // Custom queries
    @NamedQuery(name = "Register.findByRegistry", query = "SELECT r FROM Register r WHERE r.registry = :registry"),
    @NamedQuery(name = "Register.findByDescriptor", query = "SELECT r FROM Register r WHERE r.descriptor = :descriptor")
})
public class Register implements Serializable {

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
    @Column(name = "uri")
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "label")
    private String label;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Size(max = 400)
    @Column(name = "publisheruri")
    private String publisheruri;
    @Size(max = 250)
    @Column(name = "publishername")
    private String publishername;
    @Size(max = 250)
    @Column(name = "publisheremail")
    private String publisheremail;
    @Size(max = 400)
    @Column(name = "publisherhomepage")
    private String publisherhomepage;
    @Size(max = 400)
    @Column(name = "updatefrequency")
    private String updatefrequency;
    @Column(name = "dbcreationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dbcreationdate;
    @Column(name = "dblasteditdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dblasteditdate;
    @JoinColumn(name = "descriptor", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Descriptor descriptor;
    @JoinColumn(name = "registry", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Registry registry;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectasset")
    private List<Relation> relationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "objectasset")
    private List<Relation> relationList1;

    public Register() {
    }

    public Register(String uuid) {
        this.uuid = uuid;
    }

    public Register(String uuid, String uri, String label) {
        this.uuid = uuid;
        this.uri = uri;
        this.label = label;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisheruri() {
        return publisheruri;
    }

    public void setPublisheruri(String publisheruri) {
        this.publisheruri = publisheruri;
    }

    public String getPublishername() {
        return publishername;
    }

    public void setPublishername(String publishername) {
        this.publishername = publishername;
    }

    public String getPublisheremail() {
        return publisheremail;
    }

    public void setPublisheremail(String publisheremail) {
        this.publisheremail = publisheremail;
    }

    public String getPublisherhomepage() {
        return publisherhomepage;
    }

    public void setPublisherhomepage(String publisherhomepage) {
        this.publisherhomepage = publisherhomepage;
    }

    public String getUpdatefrequency() {
        return updatefrequency;
    }

    public void setUpdatefrequency(String updatefrequency) {
        this.updatefrequency = updatefrequency;
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

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @XmlTransient
    public List<Relation> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<Relation> relationList) {
        this.relationList = relationList;
    }

    @XmlTransient
    public List<Relation> getRelationList1() {
        return relationList1;
    }

    public void setRelationList1(List<Relation> relationList1) {
        this.relationList1 = relationList1;
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
        if (!(object instanceof Register)) {
            return false;
        }
        Register other = (Register) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.Register[ uuid=" + uuid + " ]";
    }
    
    public String createUUID(Descriptor descriptor) throws Exception{
        return StringUtils.createUUID("Register_" + descriptor.getUrl());
    }
    
}
