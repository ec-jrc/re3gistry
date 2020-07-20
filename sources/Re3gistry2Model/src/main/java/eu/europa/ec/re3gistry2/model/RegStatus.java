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
@Table(name = "reg_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegStatus.findAll", query = "SELECT r FROM RegStatus r")
    , @NamedQuery(name = "RegStatus.findByUuid", query = "SELECT r FROM RegStatus r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegStatus.findByLocalid", query = "SELECT r FROM RegStatus r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegStatus.findByIspublic", query = "SELECT r FROM RegStatus r WHERE r.ispublic = :ispublic")
    , @NamedQuery(name = "RegStatus.findByInsertdate", query = "SELECT r FROM RegStatus r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegStatus.findByEditdate", query = "SELECT r FROM RegStatus r WHERE r.editdate = :editdate")})
public class RegStatus implements Serializable {

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
    @Column(name = "ispublic")
    private boolean ispublic;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegItemproposed> regItemproposedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegField> regFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegItemclass> regItemclassList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegItem> regItemList;
    @OneToMany(mappedBy = "regStatus")
    private List<RegStatuslocalization> regStatuslocalizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegAction> regActionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegFieldmapping> regFieldmappingList;
    @JoinColumn(name = "reg_statusgroup", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatusgroup regStatusgroup;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regStatus")
    private List<RegItemhistory> regItemhistoryList;

    public RegStatus() {
    }

    public RegStatus(String uuid, String localid, boolean ispublic, Date insertdate) {
        this.uuid = uuid;
        this.localid = localid;
        this.ispublic = ispublic;
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

    public boolean getIspublic() {
        return ispublic;
    }

    public void setIspublic(boolean ispublic) {
        this.ispublic = ispublic;
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
    public List<RegItemproposed> getRegItemproposedList() {
        return regItemproposedList;
    }

    public void setRegItemproposedList(List<RegItemproposed> regItemproposedList) {
        this.regItemproposedList = regItemproposedList;
    }

    @XmlTransient
    public List<RegField> getRegFieldList() {
        return regFieldList;
    }

    public void setRegFieldList(List<RegField> regFieldList) {
        this.regFieldList = regFieldList;
    }

    @XmlTransient
    public List<RegItemclass> getRegItemclassList() {
        return regItemclassList;
    }

    public void setRegItemclassList(List<RegItemclass> regItemclassList) {
        this.regItemclassList = regItemclassList;
    }

    @XmlTransient
    public List<RegItem> getRegItemList() {
        return regItemList;
    }

    public void setRegItemList(List<RegItem> regItemList) {
        this.regItemList = regItemList;
    }

    @XmlTransient
    public List<RegStatuslocalization> getRegStatuslocalizationList() {
        return regStatuslocalizationList;
    }

    public void setRegStatuslocalizationList(List<RegStatuslocalization> regStatuslocalizationList) {
        this.regStatuslocalizationList = regStatuslocalizationList;
    }

    @XmlTransient
    public List<RegAction> getRegActionList() {
        return regActionList;
    }

    public void setRegActionList(List<RegAction> regActionList) {
        this.regActionList = regActionList;
    }

    @XmlTransient
    public List<RegFieldmapping> getRegFieldmappingList() {
        return regFieldmappingList;
    }

    public void setRegFieldmappingList(List<RegFieldmapping> regFieldmappingList) {
        this.regFieldmappingList = regFieldmappingList;
    }

    public RegStatusgroup getRegStatusgroup() {
        return regStatusgroup;
    }

    public void setRegStatusgroup(RegStatusgroup regStatusgroup) {
        this.regStatusgroup = regStatusgroup;
    }

    @XmlTransient
    public List<RegItemhistory> getRegItemhistoryList() {
        return regItemhistoryList;
    }

    public void setRegItemhistoryList(List<RegItemhistory> regItemhistoryList) {
        this.regItemhistoryList = regItemhistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegStatus)) {
            return false;
        }
        RegStatus other = (RegStatus) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegStatus[ uuid=" + uuid + " ]";
    }

}
