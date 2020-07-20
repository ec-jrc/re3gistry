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
@Table(name = "dataprocedure")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dataprocedure.findAll", query = "SELECT d FROM Dataprocedure d")
    , @NamedQuery(name = "Dataprocedure.findByUuid", query = "SELECT d FROM Dataprocedure d WHERE d.uuid = :uuid")
    , @NamedQuery(name = "Dataprocedure.findByFolder", query = "SELECT d FROM Dataprocedure d WHERE d.folder = :folder")
    , @NamedQuery(name = "Dataprocedure.findByComment", query = "SELECT d FROM Dataprocedure d WHERE d.comment = :comment")
    , @NamedQuery(name = "Dataprocedure.findByIgnorewarning", query = "SELECT d FROM Dataprocedure d WHERE d.ignorewarning = :ignorewarning")
    , @NamedQuery(name = "Dataprocedure.findByDatestarted", query = "SELECT d FROM Dataprocedure d WHERE d.datestarted = :datestarted")
    , @NamedQuery(name = "Dataprocedure.findByDatecompleted", query = "SELECT d FROM Dataprocedure d WHERE d.datecompleted = :datecompleted")})
public class Dataprocedure implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "folder")
    private String folder;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "comment")
    private String comment;
    @Column(name = "ignorewarning")
    private Boolean ignorewarning;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datestarted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datestarted;
    @Column(name = "datecompleted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecompleted;
    @JoinColumn(name = "procedurestatus", referencedColumnName = "uuid")
    @ManyToOne
    private Procedurestatus procedurestatus;
    @JoinColumn(name = "users", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Users users;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataprocedure")
    private List<Changelog> changelogList;

    public Dataprocedure() {
    }

    public Dataprocedure(String uuid, String folder, String comment, Date datestarted) {
        this.uuid = uuid;
        this.folder = folder;
        this.comment = comment;
        this.datestarted = datestarted;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIgnorewarning() {
        return ignorewarning;
    }

    public void setIgnorewarning(Boolean ignorewarning) {
        this.ignorewarning = ignorewarning;
    }

    public Date getDatestarted() {
        return datestarted;
    }

    public void setDatestarted(Date datestarted) {
        this.datestarted = datestarted;
    }

    public Date getDatecompleted() {
        return datecompleted;
    }

    public void setDatecompleted(Date datecompleted) {
        this.datecompleted = datecompleted;
    }

    public Procedurestatus getProcedurestatus() {
        return procedurestatus;
    }

    public void setProcedurestatus(Procedurestatus procedurestatus) {
        this.procedurestatus = procedurestatus;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @XmlTransient
    public List<Changelog> getChangelogList() {
        return changelogList;
    }

    public void setChangelogList(List<Changelog> changelogList) {
        this.changelogList = changelogList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Dataprocedure)) {
            return false;
        }
        Dataprocedure other = (Dataprocedure) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Dataprocedure[ uuid=" + uuid + " ]";
    }

}
