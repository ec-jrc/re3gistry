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
@Table(name = "reg_languagecode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegLanguagecode.findAll", query = "SELECT r FROM RegLanguagecode r")
    , @NamedQuery(name = "RegLanguagecode.findAllActive", query = "SELECT r FROM RegLanguagecode r WHERE r.active = TRUE")
    , @NamedQuery(name = "RegLanguagecode.findByUuid", query = "SELECT r FROM RegLanguagecode r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegLanguagecode.findByLabel", query = "SELECT r FROM RegLanguagecode r WHERE r.label = :label")
    , @NamedQuery(name = "RegLanguagecode.findByIso6391code", query = "SELECT r FROM RegLanguagecode r WHERE r.iso6391code = :iso6391code")
    , @NamedQuery(name = "RegLanguagecode.findByIso6392code", query = "SELECT r FROM RegLanguagecode r WHERE r.iso6392code = :iso6392code")
    , @NamedQuery(name = "RegLanguagecode.findByMasterlanguage", query = "SELECT r FROM RegLanguagecode r WHERE r.masterlanguage = :masterlanguage")
    , @NamedQuery(name = "RegLanguagecode.findByActive", query = "SELECT r FROM RegLanguagecode r WHERE r.active = :active")
    , @NamedQuery(name = "RegLanguagecode.findByInsertdate", query = "SELECT r FROM RegLanguagecode r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegLanguagecode.findByEditdate", query = "SELECT r FROM RegLanguagecode r WHERE r.editdate = :editdate")})
public class RegLanguagecode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "label")
    private String label;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "iso6391code")
    private String iso6391code;
    @Size(max = 10)
    @Column(name = "iso6392code")
    private String iso6392code;
    @Column(name = "masterlanguage")
    private Boolean masterlanguage;
    @Column(name = "active")
    private Boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regLanguagecode")
    private List<RegLocalizationproposed> regLocalizationproposedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regLanguagecode")
    private List<RegStatuslocalization> regStatuslocalizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regLanguagecode")
    private List<RegLocalization> regLocalizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regLanguagecode")
    private List<RegLocalizationhistory> regLocalizationhistoryList;

    public RegLanguagecode() {
    }

    public RegLanguagecode(String uuid, String label, String iso6391code, Date insertdate) {
        this.uuid = uuid;
        this.label = label;
        this.iso6391code = iso6391code;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIso6391code() {
        return iso6391code;
    }

    public void setIso6391code(String iso6391code) {
        this.iso6391code = iso6391code;
    }

    public String getIso6392code() {
        return iso6392code;
    }

    public void setIso6392code(String iso6392code) {
        this.iso6392code = iso6392code;
    }

    public Boolean getMasterlanguage() {
        return masterlanguage;
    }

    public void setMasterlanguage(Boolean masterlanguage) {
        this.masterlanguage = masterlanguage;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @XmlTransient
    public List<RegStatuslocalization> getRegStatuslocalizationList() {
        return regStatuslocalizationList;
    }

    public void setRegStatuslocalizationList(List<RegStatuslocalization> regStatuslocalizationList) {
        this.regStatuslocalizationList = regStatuslocalizationList;
    }

    @XmlTransient
    public List<RegLocalization> getRegLocalizationList() {
        return regLocalizationList;
    }

    public void setRegLocalizationList(List<RegLocalization> regLocalizationList) {
        this.regLocalizationList = regLocalizationList;
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
        
        if (!(object instanceof RegLanguagecode)) {
            return false;
        }
        RegLanguagecode other = (RegLanguagecode) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegLanguagecode[ uuid=" + uuid + " ]";
    }

}
