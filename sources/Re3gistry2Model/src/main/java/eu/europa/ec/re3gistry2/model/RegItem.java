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
@Table(name = "reg_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegItem.findAll", query = "SELECT r FROM RegItem r")
    , @NamedQuery(name = "RegItem.findByUuid", query = "SELECT r FROM RegItem r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegItem.findByLocalid", query = "SELECT r FROM RegItem r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegItem.findByExternal", query = "SELECT r FROM RegItem r WHERE r.external = :external")
    , @NamedQuery(name = "RegItem.findByCurrentversion", query = "SELECT r FROM RegItem r WHERE r.currentversion = :currentversion")
    , @NamedQuery(name = "RegItem.findByInsertdate", query = "SELECT r FROM RegItem r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegItem.findByEditdate", query = "SELECT r FROM RegItem r WHERE r.editdate = :editdate")
    , @NamedQuery(name = "RegItem.findByRorExport", query = "SELECT r FROM RegItem r WHERE r.rorExport = :rorExport")})
public class RegItem implements Serializable {

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
    @Column(name = "external")
    private Boolean external;
    @Column(name = "currentversion")
    private Integer currentversion;
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
    @OneToMany(mappedBy = "regItemReference")
    private List<RegItemproposed> regItemproposedList;
    @OneToMany(mappedBy = "regItemObject")
    private List<RegRelationhistory> regRelationhistoryList;
    @OneToMany(mappedBy = "regItemSubject")
    private List<RegRelationhistory> regRelationhistoryList1;
    @OneToMany(mappedBy = "regItemObject")
    private List<RegRelationproposed> regRelationproposedList;
    @OneToMany(mappedBy = "regItemSubject")
    private List<RegRelationproposed> regRelationproposedList1;
    @JoinColumn(name = "reg_action", referencedColumnName = "uuid")
    @ManyToOne
    private RegAction regAction;
    @JoinColumn(name = "reg_itemclass", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegItemclass regItemclass;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;
    @JoinColumn(name = "reg_user", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegUser regUser;
    @OneToMany(mappedBy = "regItem")
    private List<RegLocalization> regLocalizationList;
    @OneToMany(mappedBy = "regItemRegistry")
    private List<RegAction> regActionList;
    @OneToMany(mappedBy = "regItemRegister")
    private List<RegAction> regActionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemReference")
    private List<RegItemhistory> regItemhistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemObject")
    private List<RegRelation> regRelationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemSubject")
    private List<RegRelation> regRelationList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItem")
    private List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappingList;

    public RegItem() {
    }

    public RegItem(String uuid, String localid, Date insertdate) {
        this.uuid = uuid;
        this.localid = localid;
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

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Integer getCurrentversion() {
        return currentversion;
    }

    public void setCurrentversion(Integer currentversion) {
        this.currentversion = currentversion;
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
    public List<RegItemproposed> getRegItemproposedList() {
        return regItemproposedList;
    }

    public void setRegItemproposedList(List<RegItemproposed> regItemproposedList) {
        this.regItemproposedList = regItemproposedList;
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
    public List<RegRelationproposed> getRegRelationproposedList() {
        return regRelationproposedList;
    }

    public void setRegRelationproposedList(List<RegRelationproposed> regRelationproposedList) {
        this.regRelationproposedList = regRelationproposedList;
    }

    @XmlTransient
    public List<RegRelationproposed> getRegRelationproposedList1() {
        return regRelationproposedList1;
    }

    public void setRegRelationproposedList1(List<RegRelationproposed> regRelationproposedList1) {
        this.regRelationproposedList1 = regRelationproposedList1;
    }

    public RegAction getRegAction() {
        return regAction;
    }

    public void setRegAction(RegAction regAction) {
        this.regAction = regAction;
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

    @XmlTransient
    public List<RegLocalization> getRegLocalizationList() {
        return regLocalizationList;
    }

    public void setRegLocalizationList(List<RegLocalization> regLocalizationList) {
        this.regLocalizationList = regLocalizationList;
    }

    @XmlTransient
    public List<RegAction> getRegActionList() {
        return regActionList;
    }

    public void setRegActionList(List<RegAction> regActionList) {
        this.regActionList = regActionList;
    }

    @XmlTransient
    public List<RegAction> getRegActionList1() {
        return regActionList1;
    }

    public void setRegActionList1(List<RegAction> regActionList1) {
        this.regActionList1 = regActionList1;
    }

    @XmlTransient
    public List<RegItemhistory> getRegItemhistoryList() {
        return regItemhistoryList;
    }

    public void setRegItemhistoryList(List<RegItemhistory> regItemhistoryList) {
        this.regItemhistoryList = regItemhistoryList;
    }

    @XmlTransient
    public List<RegRelation> getRegRelationList() {
        return regRelationList;
    }

    public void setRegRelationList(List<RegRelation> regRelationList) {
        this.regRelationList = regRelationList;
    }

    @XmlTransient
    public List<RegRelation> getRegRelationList1() {
        return regRelationList1;
    }

    public void setRegRelationList1(List<RegRelation> regRelationList1) {
        this.regRelationList1 = regRelationList1;
    }

    @XmlTransient
    public List<RegItemRegGroupRegRoleMapping> getRegItemRegGroupRegRoleMappingList() {
        return regItemRegGroupRegRoleMappingList;
    }

    public void setRegItemRegGroupRegRoleMappingList(List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappingList) {
        this.regItemRegGroupRegRoleMappingList = regItemRegGroupRegRoleMappingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegItem)) {
            return false;
        }
        RegItem other = (RegItem) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegItem[ uuid=" + uuid + " ]";
    }

}
