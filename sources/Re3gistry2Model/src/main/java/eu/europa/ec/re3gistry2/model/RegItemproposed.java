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
@Table(name = "reg_itemproposed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegItemproposed.findAll", query = "SELECT r FROM RegItemproposed r")
    , @NamedQuery(name = "RegItemproposed.findByUuid", query = "SELECT r FROM RegItemproposed r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegItemproposed.findByExternal", query = "SELECT r FROM RegItemproposed r WHERE r.external = :external")
    , @NamedQuery(name = "RegItemproposed.findByLocalid", query = "SELECT r FROM RegItemproposed r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegItemproposed.findByInsertdate", query = "SELECT r FROM RegItemproposed r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegItemproposed.findByEditdate", query = "SELECT r FROM RegItemproposed r WHERE r.editdate = :editdate")
    , @NamedQuery(name = "RegItemproposed.findByRorExport", query = "SELECT r FROM RegItemproposed r WHERE r.rorExport = :rorExport")})
public class RegItemproposed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "external")
    private Boolean external;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "localid")
    private String localid;
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
    @JoinColumn(name = "reg_action", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegAction regAction;
    @JoinColumn(name = "reg_item_reference", referencedColumnName = "uuid")
    @ManyToOne
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemproposed")
    private List<RegLocalizationproposed> regLocalizationproposedList;
    @OneToMany(mappedBy = "regItemproposedObject")
    private List<RegRelationproposed> regRelationproposedList;
    @OneToMany(mappedBy = "regItemproposedSubject")
    private List<RegRelationproposed> regRelationproposedList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemproposed")
    private List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappingList;

    public RegItemproposed() {
    }

    public RegItemproposed(String uuid, String localid, Date insertdate) {
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

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
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

    @XmlTransient
    public List<RegLocalizationproposed> getRegLocalizationproposedList() {
        return regLocalizationproposedList;
    }

    public void setRegLocalizationproposedList(List<RegLocalizationproposed> regLocalizationproposedList) {
        this.regLocalizationproposedList = regLocalizationproposedList;
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

    @XmlTransient
    public List<RegItemproposedRegGroupRegRoleMapping> getRegItemproposedRegGroupRegRoleMappingList() {
        return regItemproposedRegGroupRegRoleMappingList;
    }

    public void setRegItemproposedRegGroupRegRoleMappingList(List<RegItemproposedRegGroupRegRoleMapping> regItemproposedRegGroupRegRoleMappingList) {
        this.regItemproposedRegGroupRegRoleMappingList = regItemproposedRegGroupRegRoleMappingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegItemproposed)) {
            return false;
        }
        RegItemproposed other = (RegItemproposed) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegItemproposed[ uuid=" + uuid + " ]";
    }

}
