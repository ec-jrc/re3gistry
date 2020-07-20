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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "itemclasscustomattribute")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itemclasscustomattribute.findAll", query = "SELECT i FROM Itemclasscustomattribute i")
    , @NamedQuery(name = "Itemclasscustomattribute.findByUuid", query = "SELECT i FROM Itemclasscustomattribute i WHERE i.uuid = :uuid")
    , @NamedQuery(name = "Itemclasscustomattribute.findByRequired", query = "SELECT i FROM Itemclasscustomattribute i WHERE i.required = :required")
    , @NamedQuery(name = "Itemclasscustomattribute.findByIsforeignkey", query = "SELECT i FROM Itemclasscustomattribute i WHERE i.isforeignkey = :isforeignkey")})
public class Itemclasscustomattribute implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "required")
    private boolean required;
    @Basic(optional = false)
    @NotNull
    @Column(name = "isforeignkey")
    private boolean isforeignkey;
    @JoinColumn(name = "customattribute", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Customattribute customattribute;
    @JoinColumn(name = "itemclass", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Itemclass itemclass;

    public Itemclasscustomattribute() {
    }

    public Itemclasscustomattribute(String uuid, boolean required, boolean isforeignkey) {
        this.uuid = uuid;
        this.required = required;
        this.isforeignkey = isforeignkey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean getIsforeignkey() {
        return isforeignkey;
    }

    public void setIsforeignkey(boolean isforeignkey) {
        this.isforeignkey = isforeignkey;
    }

    public Customattribute getCustomattribute() {
        return customattribute;
    }

    public void setCustomattribute(Customattribute customattribute) {
        this.customattribute = customattribute;
    }

    public Itemclass getItemclass() {
        return itemclass;
    }

    public void setItemclass(Itemclass itemclass) {
        this.itemclass = itemclass;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Itemclasscustomattribute)) {
            return false;
        }
        Itemclasscustomattribute other = (Itemclasscustomattribute) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclasscustomattribute[ uuid=" + uuid + " ]";
    }

}
