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
package eu.europa.ec.re3gistry2.migration.migrationmodel;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "languagecode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Languagecode.findAll", query = "SELECT l FROM Languagecode l")
    , @NamedQuery(name = "Languagecode.findByUuid", query = "SELECT l FROM Languagecode l WHERE l.uuid = :uuid")
    , @NamedQuery(name = "Languagecode.findByLabel", query = "SELECT l FROM Languagecode l WHERE l.label = :label")
    , @NamedQuery(name = "Languagecode.findByIsocode", query = "SELECT l FROM Languagecode l WHERE l.isocode = :isocode")
    , @NamedQuery(name = "Languagecode.findByIso6392", query = "SELECT l FROM Languagecode l WHERE l.iso6392 = :iso6392")
    , @NamedQuery(name = "Languagecode.findByMasterlanguage", query = "SELECT l FROM Languagecode l WHERE l.masterlanguage = :masterlanguage")})
public class Languagecode implements Serializable {

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
    @Column(name = "label")
    private String label;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "isocode")
    private String isocode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "iso639_2")
    private String iso6392;
    @Column(name = "masterlanguage")
    private Boolean masterlanguage;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "language")
    private List<Localization> localizationList;

    public Languagecode() {
    }

    public Languagecode(String uuid, String label, String isocode, String iso6392) {
        this.uuid = uuid;
        this.label = label;
        this.isocode = isocode;
        this.iso6392 = iso6392;
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

    public String getIsocode() {
        return isocode;
    }

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public String getIso6392() {
        return iso6392;
    }

    public void setIso6392(String iso6392) {
        this.iso6392 = iso6392;
    }

    public Boolean getMasterlanguage() {
        return masterlanguage;
    }

    public void setMasterlanguage(Boolean masterlanguage) {
        this.masterlanguage = masterlanguage;
    }

    @XmlTransient
    public List<Localization> getLocalizationList() {
        return localizationList;
    }

    public void setLocalizationList(List<Localization> localizationList) {
        this.localizationList = localizationList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Languagecode)) {
            return false;
        }
        Languagecode other = (Languagecode) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode[ uuid=" + uuid + " ]";
    }

}
