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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "relation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Relation.findAll", query = "SELECT r FROM Relation r"),
    @NamedQuery(name = "Relation.findByUuid", query = "SELECT r FROM Relation r WHERE r.uuid = :uuid"),
    @NamedQuery(name = "Relation.findByPredicate", query = "SELECT r FROM Relation r WHERE r.predicate = :predicate"),
    @NamedQuery(name = "Relation.findByStatus", query = "SELECT r FROM Relation r WHERE r.status = :status"),
    @NamedQuery(name = "Relation.findByDbcreationdate", query = "SELECT r FROM Relation r WHERE r.dbcreationdate = :dbcreationdate"),
    @NamedQuery(name = "Relation.findByDblasteditdate", query = "SELECT r FROM Relation r WHERE r.dblasteditdate = :dblasteditdate"),
    
    // Custom queries
    @NamedQuery(name = "Relation.findBySubjectasset", query = "SELECT r FROM Relation r WHERE r.subjectasset = :subjectasset"),
    @NamedQuery(name = "Relation.findByObjectasset", query = "SELECT r FROM Relation r WHERE r.objectasset = :objectasset"),
    @NamedQuery(name = "Relation.findBySubjectassetOrRelatedasset", query = "SELECT r FROM Relation r WHERE r.subjectasset = :register OR r.objectasset = :register")
})
public class Relation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "predicate")
    private String predicate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "status")
    private String status;
    @Column(name = "dbcreationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dbcreationdate;
    @Column(name = "dblasteditdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dblasteditdate;
    @JoinColumn(name = "subjectasset", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Register subjectasset;
    @JoinColumn(name = "objectasset", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Register objectasset;

    public Relation() {
    }

    public Relation(String uuid) {
        this.uuid = uuid;
    }

    public Relation(String uuid, String predicate, String status) {
        this.uuid = uuid;
        this.predicate = predicate;
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Register getSubjectasset() {
        return subjectasset;
    }

    public void setSubjectasset(Register subjectasset) {
        this.subjectasset = subjectasset;
    }

    public Register getObjectasset() {
        return objectasset;
    }

    public void setObjectasset(Register objectasset) {
        this.objectasset = objectasset;
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
        if (!(object instanceof Relation)) {
            return false;
        }
        Relation other = (Relation) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.Relation[ uuid=" + uuid + " ]";
    }
    
    
    public String createUUID(Register subject, Register object) throws Exception{
        return StringUtils.createUUID("Relation_" + subject.getUri() + "_" + object.getUri());
    }
}
