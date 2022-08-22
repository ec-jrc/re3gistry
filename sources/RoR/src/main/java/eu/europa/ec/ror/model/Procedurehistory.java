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
@Table(name = "procedurehistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Procedurehistory.findAll", query = "SELECT p FROM Procedurehistory p"),
    @NamedQuery(name = "Procedurehistory.findByUuid", query = "SELECT p FROM Procedurehistory p WHERE p.uuid = :uuid"),
    @NamedQuery(name = "Procedurehistory.findByStatus", query = "SELECT p FROM Procedurehistory p WHERE p.status = :status"),
    @NamedQuery(name = "Procedurehistory.findByMessage", query = "SELECT p FROM Procedurehistory p WHERE p.message = :message"),
    @NamedQuery(name = "Procedurehistory.findByStartdate", query = "SELECT p FROM Procedurehistory p WHERE p.startdate = :startdate"),
    @NamedQuery(name = "Procedurehistory.findByEnddate", query = "SELECT p FROM Procedurehistory p WHERE p.enddate = :enddate"),
    
    // Custom queries
    @NamedQuery(name = "Procedurehistory.findByOrganization", query = "SELECT p FROM Procedurehistory p JOIN FETCH p.procedure WHERE p.procedure.organization = :organization")
})
public class Procedurehistory implements Serializable {

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
    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startdate;
    @Column(name = "enddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enddate;
    @JoinColumn(name = "procedure", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Procedure procedure;
    @JoinColumn(name = "startedby", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private User startedby;

    public Procedurehistory() {
    }

    public Procedurehistory(String uuid) {
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

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
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
        if (!(object instanceof Procedurehistory)) {
            return false;
        }
        Procedurehistory other = (Procedurehistory) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.ror.model.Procedurehistory[ uuid=" + uuid + " ]";
    }
    
    public String createUUID(Procedure procedure) throws Exception{
        return StringUtils.createUUID("Procedurehistory_" + procedure.getUuid() + "_" + System.currentTimeMillis());
    }
    
}
