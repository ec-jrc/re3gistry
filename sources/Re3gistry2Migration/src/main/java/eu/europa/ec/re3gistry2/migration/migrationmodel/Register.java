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
@Table(name = "register")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Register.findAll", query = "SELECT r FROM Register r")
    , @NamedQuery(name = "Register.findByUuid", query = "SELECT r FROM Register r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "Register.findByBaseuri", query = "SELECT r FROM Register r WHERE r.baseuri = :baseuri")
    , @NamedQuery(name = "Register.findByUriname", query = "SELECT r FROM Register r WHERE r.uriname = :uriname")
    , @NamedQuery(name = "Register.findByDatecreation", query = "SELECT r FROM Register r WHERE r.datecreation = :datecreation")
    , @NamedQuery(name = "Register.findByDatelastupdate", query = "SELECT r FROM Register r WHERE r.datelastupdate = :datelastupdate")})
public class Register implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 350)
    @Column(name = "baseuri")
    private String baseuri;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "uriname")
    private String uriname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @OneToMany(mappedBy = "register")
    private List<Localization> localizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "register")
    private List<Itemclass> itemclassList;
    @JoinColumn(name = "contactpoint", referencedColumnName = "uuid")
    @ManyToOne
    private Reference contactpoint;
    @JoinColumn(name = "license", referencedColumnName = "uuid")
    @ManyToOne
    private Reference license;
    @JoinColumn(name = "registercontrolbody", referencedColumnName = "uuid")
    @ManyToOne
    private Reference registercontrolbody;
    @JoinColumn(name = "registermanager", referencedColumnName = "uuid")
    @ManyToOne
    private Reference registermanager;
    @JoinColumn(name = "registerowner", referencedColumnName = "uuid")
    @ManyToOne
    private Reference registerowner;
    @JoinColumn(name = "submitter", referencedColumnName = "uuid")
    @ManyToOne
    private Reference submitter;
    @JoinColumn(name = "registry", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Registry registry;

    public Register() {
    }

    public Register(String uuid, String baseuri, String uriname, Date datecreation) {
        this.uuid = uuid;
        this.baseuri = baseuri;
        this.uriname = uriname;
        this.datecreation = datecreation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBaseuri() {
        return baseuri;
    }

    public void setBaseuri(String baseuri) {
        this.baseuri = baseuri;
    }

    public String getUriname() {
        return uriname;
    }

    public void setUriname(String uriname) {
        this.uriname = uriname;
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
    public List<Itemclass> getItemclassList() {
        return itemclassList;
    }

    public void setItemclassList(List<Itemclass> itemclassList) {
        this.itemclassList = itemclassList;
    }

    public Reference getContactpoint() {
        return contactpoint;
    }

    public void setContactpoint(Reference contactpoint) {
        this.contactpoint = contactpoint;
    }

    public Reference getLicense() {
        return license;
    }

    public void setLicense(Reference license) {
        this.license = license;
    }

    public Reference getRegistercontrolbody() {
        return registercontrolbody;
    }

    public void setRegistercontrolbody(Reference registercontrolbody) {
        this.registercontrolbody = registercontrolbody;
    }

    public Reference getRegistermanager() {
        return registermanager;
    }

    public void setRegistermanager(Reference registermanager) {
        this.registermanager = registermanager;
    }

    public Reference getRegisterowner() {
        return registerowner;
    }

    public void setRegisterowner(Reference registerowner) {
        this.registerowner = registerowner;
    }

    public Reference getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Reference submitter) {
        this.submitter = submitter;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Register)) {
            return false;
        }
        Register other = (Register) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Register[ uuid=" + uuid + " ]";
    }

}
