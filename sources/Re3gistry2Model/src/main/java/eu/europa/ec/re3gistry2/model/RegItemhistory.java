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
@Table(name = "reg_itemhistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegItemhistory.findAll", query = "SELECT r FROM RegItemhistory r")
    , @NamedQuery(name = "RegItemhistory.findByUuid", query = "SELECT r FROM RegItemhistory r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegItemhistory.findByLocalid", query = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegItemhistory.findByVersionnumber", query = "SELECT r FROM RegItemhistory r WHERE r.versionnumber = :versionnumber")
    , @NamedQuery(name = "RegItemhistory.findByExternal", query = "SELECT r FROM RegItemhistory r WHERE r.external = :external")
    , @NamedQuery(name = "RegItemhistory.findByInsertdate", query = "SELECT r FROM RegItemhistory r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegItemhistory.findByEditdate", query = "SELECT r FROM RegItemhistory r WHERE r.editdate = :editdate")
    , @NamedQuery(name = "RegItemhistory.findByRorExport", query = "SELECT r FROM RegItemhistory r WHERE r.rorExport = :rorExport")})
public class RegItemhistory implements Serializable {

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
    @Column(name = "versionnumber")
    private int versionnumber;
    @Column(name = "external")
    private Boolean external;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @Column(name = "ror_export")
    private Boolean rorExport;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemhistory")
    private List<RegItemhistoryRegGroupRegRoleMapping> regItemhistoryRegGroupRegRoleMappingList;
    @OneToMany(mappedBy = "regItemhistoryObject")
    private List<RegRelationhistory> regRelationhistoryList;
    @OneToMany(mappedBy = "regItemhistorySubject")
    private List<RegRelationhistory> regRelationhistoryList1;
    @OneToMany(mappedBy = "regItemhistory")
    private List<RegLocalizationhistory> regLocalizationhistoryList;
    @JoinColumn(name = "reg_action", referencedColumnName = "uuid")
    @ManyToOne
    private RegAction regAction;
    @JoinColumn(name = "reg_item_reference", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegItem regItemReference;
    @JoinColumn(name = "reg_itemclass", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegItemclass regItemclass;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;
    @JoinColumn(name = "reg_user", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegUser regUser;

    public RegItemhistory() {
    }

    public RegItemhistory(String uuid, String localid, int versionnumber, Date insertdate) {
        this.uuid = uuid;
        this.localid = localid;
        this.versionnumber = versionnumber;
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

    public int getVersionnumber() {
        return versionnumber;
    }

    public void setVersionnumber(int versionnumber) {
        this.versionnumber = versionnumber;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
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

    public Boolean getRorExport() {
        return rorExport;
    }

    public void setRorExport(Boolean rorExport) {
        this.rorExport = rorExport;
    }

    @XmlTransient
    public List<RegItemhistoryRegGroupRegRoleMapping> getRegItemhistoryRegGroupRegRoleMappingList() {
        return regItemhistoryRegGroupRegRoleMappingList;
    }

    public void setRegItemhistoryRegGroupRegRoleMappingList(List<RegItemhistoryRegGroupRegRoleMapping> regItemhistoryRegGroupRegRoleMappingList) {
        this.regItemhistoryRegGroupRegRoleMappingList = regItemhistoryRegGroupRegRoleMappingList;
    }

    @XmlTransient
    public List<RegRelationhistory> getRegRelationhistoryList() {
        return regRelationhistoryList;
    }

    public void setRegRelationhistoryList(List<RegRelationhistory> regRelationhistoryList) {
        this.regRelationhistoryList = regRelationhistoryList;
    }

    @XmlTransient
    public List<RegRelationhistory> getRegRelationhistoryList1() {
        return regRelationhistoryList1;
    }

    public void setRegRelationhistoryList1(List<RegRelationhistory> regRelationhistoryList1) {
        this.regRelationhistoryList1 = regRelationhistoryList1;
    }

    @XmlTransient
    public List<RegLocalizationhistory> getRegLocalizationhistoryList() {
        return regLocalizationhistoryList;
    }

    public void setRegLocalizationhistoryList(List<RegLocalizationhistory> regLocalizationhistoryList) {
        this.regLocalizationhistoryList = regLocalizationhistoryList;
    }

    public RegAction getRegAction() {
        return regAction;
    }

    public void setRegAction(RegAction regAction) {
        this.regAction = regAction;
    }

    public RegItem getRegItemReference() {
        return regItemReference;
    }

    public void setRegItemReference(RegItem regItemReference) {
        this.regItemReference = regItemReference;
    }

    public RegItemclass getRegItemclass() {
        return regItemclass;
    }

    public void setRegItemclass(RegItemclass regItemclass) {
        this.regItemclass = regItemclass;
    }

    public RegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(RegStatus regStatus) {
        this.regStatus = regStatus;
    }

    public RegUser getRegUser() {
        return regUser;
    }

    public void setRegUser(RegUser regUser) {
        this.regUser = regUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegItemhistory)) {
            return false;
        }
        RegItemhistory other = (RegItemhistory) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegItemhistory[ uuid=" + uuid + " ]";
    }

}
