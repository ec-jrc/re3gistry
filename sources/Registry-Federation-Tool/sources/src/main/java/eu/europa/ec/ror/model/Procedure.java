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
@Table(name = "procedure")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Procedure.findAll", query = "SELECT p FROM Procedure p"),
    @NamedQuery(name = "Procedure.findByUuid", query = "SELECT p FROM Procedure p WHERE p.uuid = :uuid"),
    @NamedQuery(name = "Procedure.findByStatus", query = "SELECT p FROM Procedure p WHERE p.status = :status"),
    @NamedQuery(name = "Procedure.findByMessage", query = "SELECT p FROM Procedure p WHERE p.message = :message"),
    @NamedQuery(name = "Procedure.findByLastharvestdate", query = "SELECT p FROM Procedure p WHERE p.lastharvestdate = :lastharvestdate"),
    @NamedQuery(name = "Procedure.findByNextharvestdate", query = "SELECT p FROM Procedure p WHERE p.nextharvestdate = :nextharvestdate"),
    @NamedQuery(name = "Procedure.findByDbcreationdate", query = "SELECT p FROM Procedure p WHERE p.dbcreationdate = :dbcreationdate"),
    
    // Custom queries
    @NamedQuery(name = "Procedure.findByDescriptor", query = "SELECT p FROM Procedure p WHERE p.descriptor = :descriptor"),
    @NamedQuery(name = "Procedure.findByOrganization", query = "SELECT p FROM Procedure p WHERE p.organization = :organization"),
    @NamedQuery(name = "Procedure.findByStatus", query = "SELECT p FROM Procedure p WHERE p.status = :status"),
    @NamedQuery(name = "Procedure.findByNextharvestdateToday", query = "SELECT p FROM Procedure p WHERE cast(p.nextharvestdate DATE) <= CURRENT_DATE")
})
public class Procedure implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "procedure")
    private List<Procedurehistory> procedurehistoryList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "uuid")
    private String uuid;
    @Size(max = 100)
    @Column(name = "status")
    private String status;
    @Size(max = 2147483647)
    @Column(name = "message")
    private String message;
    @Column(name = "lastharvestdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastharvestdate;
    @Column(name = "nextharvestdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextharvestdate;
    @Column(name = "dbcreationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dbcreationdate;
    @JoinColumn(name = "descriptor", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Descriptor descriptor;
    @JoinColumn(name = "organization", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Organization organization;
    @JoinColumn(name = "startedby", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private User startedby;

    public Procedure() {
    }

    public Procedure(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getLastharvestdate() {
        return lastharvestdate;
    }

    public void setLastharvestdate(Date lastharvestdate) {
        this.lastharvestdate = lastharvestdate;
    }

    public Date getNextharvestdate() {
        return nextharvestdate;
    }

    public void setNextharvestdate(Date nextharvestdate) {
        this.nextharvestdate = nextharvestdate;
    }

    public Date getDbcreationdate() {
        return dbcreationdate;
    }

    public void setDbcreationdate(Date dbcreationdate) {
        this.dbcreationdate = dbcreationdate;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getStartedby() {
        return startedby;
    }

    public void setStartedby(User startedby) {
        this.startedby = startedby;
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
        if (!(object instanceof Procedure)) {
            return false;
        }
        Procedure other = (Procedure) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.Procedure[ uuid=" + uuid + " ]";
    }
    
    public String createUUID(Descriptor descriptor) throws Exception{
        return StringUtils.createUUID("Procedure_" + descriptor.getUrl());
    }

    @XmlTransient
    public List<Procedurehistory> getProcedurehistoryList() {
        return procedurehistoryList;
    }

    public void setProcedurehistoryList(List<Procedurehistory> procedurehistoryList) {
        this.procedurehistoryList = procedurehistoryList;
    }
    
}
