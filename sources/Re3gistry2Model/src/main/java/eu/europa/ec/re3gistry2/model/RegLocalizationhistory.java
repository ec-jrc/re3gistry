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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "reg_localizationhistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegLocalizationhistory.findAll", query = "SELECT r FROM RegLocalizationhistory r")
    , @NamedQuery(name = "RegLocalizationhistory.findByUuid", query = "SELECT r FROM RegLocalizationhistory r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegLocalizationhistory.findByFieldValueIndex", query = "SELECT r FROM RegLocalizationhistory r WHERE r.fieldValueIndex = :fieldValueIndex")
    , @NamedQuery(name = "RegLocalizationhistory.findByValue", query = "SELECT r FROM RegLocalizationhistory r WHERE r.value = :value")
    , @NamedQuery(name = "RegLocalizationhistory.findByHref", query = "SELECT r FROM RegLocalizationhistory r WHERE r.href = :href")
    , @NamedQuery(name = "RegLocalizationhistory.findByInsertdate", query = "SELECT r FROM RegLocalizationhistory r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegLocalizationhistory.findByEditdate", query = "SELECT r FROM RegLocalizationhistory r WHERE r.editdate = :editdate")})
public class RegLocalizationhistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_value_index")
    private int fieldValueIndex;
    @Size(max = 2147483647)
    @Column(name = "value")
    private String value;
    @Size(max = 1024)
    @Column(name = "href")
    private String href;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @JoinColumn(name = "reg_field", referencedColumnName = "uuid")
    @ManyToOne
    private RegField regField;
    @JoinColumn(name = "reg_itemhistory", referencedColumnName = "uuid")
    @ManyToOne
    private RegItemhistory regItemhistory;
    @JoinColumn(name = "reg_languagecode", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegLanguagecode regLanguagecode;
    @JoinColumn(name = "reg_relationhistory_reference", referencedColumnName = "uuid")
    @ManyToOne
    private RegRelationhistory regRelationhistoryReference;
    @JoinColumn(name = "reg_action", referencedColumnName = "uuid")
    @ManyToOne
    private RegAction regAction;

    public RegLocalizationhistory() {
    }

    public RegLocalizationhistory(String uuid, int fieldValueIndex, Date insertdate) {
        this.uuid = uuid;
        this.fieldValueIndex = fieldValueIndex;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getFieldValueIndex() {
        return fieldValueIndex;
    }

    public void setFieldValueIndex(int fieldValueIndex) {
        this.fieldValueIndex = fieldValueIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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

    public RegField getRegField() {
        return regField;
    }

    public void setRegField(RegField regField) {
        this.regField = regField;
    }

    public RegItemhistory getRegItemhistory() {
        return regItemhistory;
    }

    public void setRegItemhistory(RegItemhistory regItemhistory) {
        this.regItemhistory = regItemhistory;
    }

    public RegLanguagecode getRegLanguagecode() {
        return regLanguagecode;
    }

    public void setRegLanguagecode(RegLanguagecode regLanguagecode) {
        this.regLanguagecode = regLanguagecode;
    }

    public RegRelationhistory getRegRelationhistoryReference() {
        return regRelationhistoryReference;
    }

    public void setRegRelationhistoryReference(RegRelationhistory regRelationhistoryReference) {
        this.regRelationhistoryReference = regRelationhistoryReference;
    }

    public RegAction getRegAction() {
        return regAction;
    }

    public void setRegAction(RegAction regAction) {
        this.regAction = regAction;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RegLocalizationhistory)) {
            return false;
        }
        RegLocalizationhistory other = (RegLocalizationhistory) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegLocalizationhistory[ uuid=" + uuid + " ]";
    }

}
