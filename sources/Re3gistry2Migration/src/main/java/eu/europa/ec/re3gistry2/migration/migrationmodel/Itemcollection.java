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
@Table(name = "itemcollection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itemcollection.findAll", query = "SELECT i FROM Itemcollection i")
    , @NamedQuery(name = "Itemcollection.findByUuid", query = "SELECT i FROM Itemcollection i WHERE i.uuid = :uuid")
    , @NamedQuery(name = "Itemcollection.findByItem", query = "SELECT i FROM Itemcollection i WHERE i.item = :item")
    , @NamedQuery(name = "Itemcollection.findByCollection", query = "SELECT i FROM Itemcollection i WHERE i.collection = :collection")
    , @NamedQuery(name = "Itemcollection.findByDatecreation", query = "SELECT i FROM Itemcollection i WHERE i.datecreation = :datecreation")
    , @NamedQuery(name = "Itemcollection.findByDatelastupdate", query = "SELECT i FROM Itemcollection i WHERE i.datelastupdate = :datelastupdate")})
public class Itemcollection implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @JoinColumn(name = "collection", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Item collection;
    @JoinColumn(name = "item", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Item item;

    public Itemcollection() {
    }

    public Itemcollection(String uuid, Date datecreation) {
        this.uuid = uuid;
        this.datecreation = datecreation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public Date getDatelastupdate() {
        return datelastupdate;
    }

    public void setDatelastupdate(Date datelastupdate) {
        this.datelastupdate = datelastupdate;
    }

    public Item getCollection() {
        return collection;
    }

    public void setCollection(Item collection) {
        this.collection = collection;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Itemcollection)) {
            return false;
        }
        Itemcollection other = (Itemcollection) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Itemcollection[ uuid=" + uuid + " ]";
    }

}
