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
@Table(name = "reg_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegUser.findAll", query = "SELECT r FROM RegUser r")
    , @NamedQuery(name = "RegUser.findByUuid", query = "SELECT r FROM RegUser r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegUser.findBySsoreference", query = "SELECT r FROM RegUser r WHERE r.ssoreference = :ssoreference")
    , @NamedQuery(name = "RegUser.findByName", query = "SELECT r FROM RegUser r WHERE r.name = :name")
    , @NamedQuery(name = "RegUser.findByEmail", query = "SELECT r FROM RegUser r WHERE r.email = :email")
    , @NamedQuery(name = "RegUser.findByEnabled", query = "SELECT r FROM RegUser r WHERE r.enabled = :enabled")
    , @NamedQuery(name = "RegUser.findByInsertdate", query = "SELECT r FROM RegUser r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegUser.findByEditdate", query = "SELECT r FROM RegUser r WHERE r.editdate = :editdate")})
public class RegUser implements Serializable {

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
    @Column(name = "ssoreference")
    private String ssoreference;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 350)
    @Column(name = "name")
    private String name;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 350)
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @Size(min = 1, max = 350)
    @Column(name = "shiropassword")
    private String shiropassword;

    @Basic(optional = false)
    @Size(min = 1, max = 350)
    @Column(name = "shirosalt")
    private String shirosalt;

    @Column(name = "enabled")
    private Boolean enabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regUser")
    private List<RegItemproposed> regItemproposedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regUser")
    private List<RegItem> regItemList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regUser")
    private List<RegAction> regActionList;
    @OneToMany(mappedBy = "approvedBy")
    private List<RegAction> regActionList1;
    @OneToMany(mappedBy = "publishedBy")
    private List<RegAction> regActionList2;
    @OneToMany(mappedBy = "rejectedBy")
    private List<RegAction> regActionList3;
    @OneToMany(mappedBy = "submittedBy")
    private List<RegAction> regActionList4;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regUser")
    private List<RegUserRegGroupMapping> regUserRegGroupMappingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regUser")
    private List<RegItemhistory> regItemhistoryList;

    public RegUser() {
    }

    public RegUser(String uuid, String ssoreference, String name, String email, String shiropassword, String shirosalt, Date insertdate) {
        this.uuid = uuid;
        this.ssoreference = ssoreference;
        this.name = name;
        this.email = email;
        this.shiropassword = shiropassword;
        this.shirosalt = shirosalt;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSsoreference() {
        return ssoreference;
    }

    public void setSsoreference(String ssoreference) {
        this.ssoreference = ssoreference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShiropassword() {
        return shiropassword;
    }

    public void setShiropassword(String shiropassword) {
        this.shiropassword = shiropassword;
    }

    public String getShirosalt() {
        return shirosalt;
    }

    public void setShirosalt(String shirosalt) {
        this.shirosalt = shirosalt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
    public List<RegItem> getRegItemList() {
        return regItemList;
    }

    public void setRegItemList(List<RegItem> regItemList) {
        this.regItemList = regItemList;
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
    public List<RegAction> getRegActionList2() {
        return regActionList2;
    }

    public void setRegActionList2(List<RegAction> regActionList2) {
        this.regActionList2 = regActionList2;
    }

    @XmlTransient
    public List<RegAction> getRegActionList3() {
        return regActionList3;
    }

    public void setRegActionList3(List<RegAction> regActionList3) {
        this.regActionList3 = regActionList3;
    }

    @XmlTransient
    public List<RegAction> getRegActionList4() {
        return regActionList4;
    }

    public void setRegActionList4(List<RegAction> regActionList4) {
        this.regActionList4 = regActionList4;
    }

    @XmlTransient
    public List<RegUserRegGroupMapping> getRegUserRegGroupMappingList() {
        return regUserRegGroupMappingList;
    }

    public void setRegUserRegGroupMappingList(List<RegUserRegGroupMapping> regUserRegGroupMappingList) {
        this.regUserRegGroupMappingList = regUserRegGroupMappingList;
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
        
        if (!(object instanceof RegUser)) {
            return false;
        }
        RegUser other = (RegUser) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegUser[ uuid=" + uuid + " ]";
    }

}
