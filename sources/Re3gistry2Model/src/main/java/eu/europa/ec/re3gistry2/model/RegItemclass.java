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
@Table(name = "reg_itemclass")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegItemclass.findAll", query = "SELECT r FROM RegItemclass r")
    , @NamedQuery(name = "RegItemclass.findByUuid", query = "SELECT r FROM RegItemclass r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegItemclass.findByLocalid", query = "SELECT r FROM RegItemclass r WHERE r.localid = :localid")
    , @NamedQuery(name = "RegItemclass.findByBaseuri", query = "SELECT r FROM RegItemclass r WHERE r.baseuri = :baseuri")
    , @NamedQuery(name = "RegItemclass.findBySystemitem", query = "SELECT r FROM RegItemclass r WHERE r.systemitem = :systemitem")
    , @NamedQuery(name = "RegItemclass.findByActive", query = "SELECT r FROM RegItemclass r WHERE r.active = :active")
    , @NamedQuery(name = "RegItemclass.findByDataprocedureorder", query = "SELECT r FROM RegItemclass r WHERE r.dataprocedureorder = :dataprocedureorder")
    , @NamedQuery(name = "RegItemclass.findByInsertdate", query = "SELECT r FROM RegItemclass r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegItemclass.findByEditdate", query = "SELECT r FROM RegItemclass r WHERE r.editdate = :editdate")})
public class RegItemclass implements Serializable {

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
    @Size(max = 500)
    @Column(name = "baseuri")
    private String baseuri;
    @Column(name = "systemitem")
    private Boolean systemitem;
    @Column(name = "active")
    private Boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dataprocedureorder")
    private int dataprocedureorder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemclass")
    private List<RegItemproposed> regItemproposedList;
    @OneToMany(mappedBy = "regItemclassReference")
    private List<RegField> regFieldList;
    @OneToMany(mappedBy = "regItemclassParent")
    private List<RegItemclass> regItemclassList;
    @JoinColumn(name = "reg_itemclass_parent", referencedColumnName = "uuid")
    @ManyToOne
    private RegItemclass regItemclassParent;
    @JoinColumn(name = "reg_itemclasstype", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegItemclasstype regItemclasstype;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemclass")
    private List<RegItem> regItemList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemclass")
    private List<RegFieldmapping> regFieldmappingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regItemclass")
    private List<RegItemhistory> regItemhistoryList;

    public RegItemclass() {
    }

    public RegItemclass(String uuid, String localid, int dataprocedureorder, Date insertdate) {
        this.uuid = uuid;
        this.localid = localid;
        this.dataprocedureorder = dataprocedureorder;
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

    public String getBaseuri() {
        return baseuri;
    }

    public void setBaseuri(String baseuri) {
        this.baseuri = baseuri;
    }

    public Boolean getSystemitem() {
        return systemitem;
    }

    public void setSystemitem(Boolean systemitem) {
        this.systemitem = systemitem;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getDataprocedureorder() {
        return dataprocedureorder;
    }

    public void setDataprocedureorder(int dataprocedureorder) {
        this.dataprocedureorder = dataprocedureorder;
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

    public RegItemclass getRegItemclassParent() {
        return regItemclassParent;
    }

    public void setRegItemclassParent(RegItemclass regItemclassParent) {
        this.regItemclassParent = regItemclassParent;
    }

    public RegItemclasstype getRegItemclasstype() {
        return regItemclasstype;
    }

    public void setRegItemclasstype(RegItemclasstype regItemclasstype) {
        this.regItemclasstype = regItemclasstype;
    }

    public RegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(RegStatus regStatus) {
        this.regStatus = regStatus;
    }

    @XmlTransient
    public List<RegItem> getRegItemList() {
        return regItemList;
    }

    public void setRegItemList(List<RegItem> regItemList) {
        this.regItemList = regItemList;
    }

    @XmlTransient
    public List<RegFieldmapping> getRegFieldmappingList() {
        return regFieldmappingList;
    }

    public void setRegFieldmappingList(List<RegFieldmapping> regFieldmappingList) {
        this.regFieldmappingList = regFieldmappingList;
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
        
        if (!(object instanceof RegItemclass)) {
            return false;
        }
        RegItemclass other = (RegItemclass) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegItemclass[ uuid=" + uuid + " ]";
    }

}
