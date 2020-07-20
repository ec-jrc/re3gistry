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
@Table(name = "reg_relationproposed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegRelationproposed.findAll", query = "SELECT r FROM RegRelationproposed r")
    , @NamedQuery(name = "RegRelationproposed.findByUuid", query = "SELECT r FROM RegRelationproposed r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegRelationproposed.findByInsertdate", query = "SELECT r FROM RegRelationproposed r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegRelationproposed.findByEditdate", query = "SELECT r FROM RegRelationproposed r WHERE r.editdate = :editdate")})
public class RegRelationproposed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(mappedBy = "regRelationproposedReference")
    private List<RegLocalizationproposed> regLocalizationproposedList;
    @JoinColumn(name = "reg_item_object", referencedColumnName = "uuid")
    @ManyToOne
    private RegItem regItemObject;
    @JoinColumn(name = "reg_item_subject", referencedColumnName = "uuid")
    @ManyToOne
    private RegItem regItemSubject;
    @JoinColumn(name = "reg_itemproposed_object", referencedColumnName = "uuid")
    @ManyToOne
    private RegItemproposed regItemproposedObject;
    @JoinColumn(name = "reg_itemproposed_subject", referencedColumnName = "uuid")
    @ManyToOne
    private RegItemproposed regItemproposedSubject;
    @JoinColumn(name = "reg_relation_reference", referencedColumnName = "uuid")
    @ManyToOne
    private RegRelation regRelationReference;
    @JoinColumn(name = "reg_relationpredicate", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegRelationpredicate regRelationpredicate;

    public RegRelationproposed() {
    }

    public RegRelationproposed(String uuid, Date insertdate) {
        this.uuid = uuid;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public RegItem getRegItemObject() {
        return regItemObject;
    }

    public void setRegItemObject(RegItem regItemObject) {
        this.regItemObject = regItemObject;
    }

    public RegItem getRegItemSubject() {
        return regItemSubject;
    }

    public void setRegItemSubject(RegItem regItemSubject) {
        this.regItemSubject = regItemSubject;
    }

    public RegItemproposed getRegItemproposedObject() {
        return regItemproposedObject;
    }

    public void setRegItemproposedObject(RegItemproposed regItemproposedObject) {
        this.regItemproposedObject = regItemproposedObject;
    }

    public RegItemproposed getRegItemproposedSubject() {
        return regItemproposedSubject;
    }

    public void setRegItemproposedSubject(RegItemproposed regItemproposedSubject) {
        this.regItemproposedSubject = regItemproposedSubject;
    }

    public RegRelation getRegRelationReference() {
        return regRelationReference;
    }

    public void setRegRelationReference(RegRelation regRelationReference) {
        this.regRelationReference = regRelationReference;
    }

    public RegRelationpredicate getRegRelationpredicate() {
        return regRelationpredicate;
    }

    public void setRegRelationpredicate(RegRelationpredicate regRelationpredicate) {
        this.regRelationpredicate = regRelationpredicate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegRelationproposed)) {
            return false;
        }
        RegRelationproposed other = (RegRelationproposed) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegRelationproposed[ uuid=" + uuid + " ]";
    }

}
