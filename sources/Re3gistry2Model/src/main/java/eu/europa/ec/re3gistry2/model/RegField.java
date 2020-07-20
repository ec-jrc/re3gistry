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
@Table(name = "reg_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegField.findAll", query = "SELECT r FROM RegField r")
    , @NamedQuery(name = "RegField.findByUuid", query = "SELECT r FROM RegField r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegField.findByLocalid", query = "SELECT r FROM RegField r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegField.findByIstitle", query = "SELECT r FROM RegField r WHERE r.istitle = :istitle")
    , @NamedQuery(name = "RegField.findByInsertdate", query = "SELECT r FROM RegField r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegField.findByEditdate", query = "SELECT r FROM RegField r WHERE r.editdate = :editdate")})
public class RegField implements Serializable {

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
    @Column(name = "istitle")
    private Boolean istitle;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regField")
    private List<RegLocalizationproposed> regLocalizationproposedList;
    @JoinColumn(name = "reg_fieldtype", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegFieldtype regFieldtype;
    @JoinColumn(name = "reg_itemclass_reference", referencedColumnName = "uuid")
    @ManyToOne
    private RegItemclass regItemclassReference;
    @JoinColumn(name = "reg_role_reference", referencedColumnName = "uuid")
    @ManyToOne
    private RegRole regRoleReference;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;
    @OneToMany(mappedBy = "regField")
    private List<RegLocalization> regLocalizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regField")
    private List<RegFieldmapping> regFieldmappingList;
    @OneToMany(mappedBy = "regField")
    private List<RegLocalizationhistory> regLocalizationhistoryList;

    public RegField() {
    }

    public RegField(String uuid, String localid, Date insertdate) {
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

    public Boolean getIstitle() {
        return istitle;
    }

    public void setIstitle(Boolean istitle) {
        this.istitle = istitle;
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
    public List<RegLocalizationproposed> getRegLocalizationproposedList() {
        return regLocalizationproposedList;
    }

    public void setRegLocalizationproposedList(List<RegLocalizationproposed> regLocalizationproposedList) {
        this.regLocalizationproposedList = regLocalizationproposedList;
    }

    public RegFieldtype getRegFieldtype() {
        return regFieldtype;
    }

    public void setRegFieldtype(RegFieldtype regFieldtype) {
        this.regFieldtype = regFieldtype;
    }

    public RegItemclass getRegItemclassReference() {
        return regItemclassReference;
    }

    public void setRegItemclassReference(RegItemclass regItemclassReference) {
        this.regItemclassReference = regItemclassReference;
    }

    public RegRole getRegRoleReference() {
        return regRoleReference;
    }

    public void setRegRoleReference(RegRole regRoleReference) {
        this.regRoleReference = regRoleReference;
    }

    public RegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(RegStatus regStatus) {
        this.regStatus = regStatus;
    }

    @XmlTransient
    public List<RegLocalization> getRegLocalizationList() {
        return regLocalizationList;
    }

    public void setRegLocalizationList(List<RegLocalization> regLocalizationList) {
        this.regLocalizationList = regLocalizationList;
    }

    @XmlTransient
    public List<RegFieldmapping> getRegFieldmappingList() {
        return regFieldmappingList;
    }

    public void setRegFieldmappingList(List<RegFieldmapping> regFieldmappingList) {
        this.regFieldmappingList = regFieldmappingList;
    }

    @XmlTransient
    public List<RegLocalizationhistory> getRegLocalizationhistoryList() {
        return regLocalizationhistoryList;
    }

    public void setRegLocalizationhistoryList(List<RegLocalizationhistory> regLocalizationhistoryList) {
        this.regLocalizationhistoryList = regLocalizationhistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegField)) {
            return false;
        }
        RegField other = (RegField) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegField[ uuid=" + uuid + " ]";
    }

}
