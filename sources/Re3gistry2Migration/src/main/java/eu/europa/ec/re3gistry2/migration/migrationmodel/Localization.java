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
@Table(name = "localization")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Localization.findAll", query = "SELECT l FROM Localization l")
    , @NamedQuery(name = "Localization.findByUuid", query = "SELECT l FROM Localization l WHERE l.uuid = :uuid")
    , @NamedQuery(name = "Localization.findByLabel", query = "SELECT l FROM Localization l WHERE l.label = :label")
    , @NamedQuery(name = "Localization.findByDefinition", query = "SELECT l FROM Localization l WHERE l.definition = :definition")
    , @NamedQuery(name = "Localization.findByDescription", query = "SELECT l FROM Localization l WHERE l.description = :description")
    , @NamedQuery(name = "Localization.findByUri", query = "SELECT l FROM Localization l WHERE l.uri = :uri")
    , @NamedQuery(name = "Localization.findByDatecreation", query = "SELECT l FROM Localization l WHERE l.datecreation = :datecreation")
    , @NamedQuery(name = "Localization.findByDatelastupdate", query = "SELECT l FROM Localization l WHERE l.datelastupdate = :datelastupdate")})
public class Localization implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1500)
    @Column(name = "label")
    private String label;
    @Size(max = 2147483647)
    @Column(name = "definition")
    private String definition;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Size(max = 2147483647)
    @Column(name = "uri")
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datecreation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation;
    @Column(name = "datelastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastupdate;
    @JoinColumn(name = "customattributevalue", referencedColumnName = "uuid")
    @ManyToOne
    private Customattributevalue customattributevalue;
    @JoinColumn(name = "item", referencedColumnName = "uuid")
    @ManyToOne
    private Item item;
    @JoinColumn(name = "itemclass", referencedColumnName = "uuid")
    @ManyToOne
    private Itemclass itemclass;
    @JoinColumn(name = "language", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private Languagecode language;
    @JoinColumn(name = "reference", referencedColumnName = "uuid")
    @ManyToOne
    private Reference reference;
    @JoinColumn(name = "register", referencedColumnName = "uuid")
    @ManyToOne
    private Register register;
    @JoinColumn(name = "registry", referencedColumnName = "uuid")
    @ManyToOne
    private Registry registry;
    @JoinColumn(name = "status", referencedColumnName = "uuid")
    @ManyToOne
    private Status status;

    public Localization() {
    }

    public Localization(String uuid, String label, Date datecreation) {
        this.uuid = uuid;
        this.label = label;
        this.datecreation = datecreation;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public Customattributevalue getCustomattributevalue() {
        return customattributevalue;
    }

    public void setCustomattributevalue(Customattributevalue customattributevalue) {
        this.customattributevalue = customattributevalue;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Itemclass getItemclass() {
        return itemclass;
    }

    public void setItemclass(Itemclass itemclass) {
        this.itemclass = itemclass;
    }

    public Languagecode getLanguage() {
        return language;
    }

    public void setLanguage(Languagecode language) {
        this.language = language;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Localization)) {
            return false;
        }
        Localization other = (Localization) object;
        return !((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid)));
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.migration.migrationmodel.Localization[ uuid=" + uuid + " ]";
    }

}
