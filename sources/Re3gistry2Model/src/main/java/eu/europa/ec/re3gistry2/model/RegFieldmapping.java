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
@Table(name = "reg_fieldmapping")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegFieldmapping.findAll", query = "SELECT r FROM RegFieldmapping r")
    , @NamedQuery(name = "RegFieldmapping.findByUuid", query = "SELECT r FROM RegFieldmapping r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegFieldmapping.findByListorder", query = "SELECT r FROM RegFieldmapping r WHERE r.listorder = :listorder")
    , @NamedQuery(name = "RegFieldmapping.findByTablevisible", query = "SELECT r FROM RegFieldmapping r WHERE r.tablevisible = :tablevisible")
    , @NamedQuery(name = "RegFieldmapping.findByRequired", query = "SELECT r FROM RegFieldmapping r WHERE r.required = :required")
    , @NamedQuery(name = "RegFieldmapping.findByHidden", query = "SELECT r FROM RegFieldmapping r WHERE r.hidden = :hidden")
    , @NamedQuery(name = "RegFieldmapping.findByMultivalue", query = "SELECT r FROM RegFieldmapping r WHERE r.multivalue = :multivalue")
    , @NamedQuery(name = "RegFieldmapping.findByInsertdate", query = "SELECT r FROM RegFieldmapping r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegFieldmapping.findByEditdate", query = "SELECT r FROM RegFieldmapping r WHERE r.editdate = :editdate")
    , @NamedQuery(name = "RegFieldmapping.findByHashref", query = "SELECT r FROM RegFieldmapping r WHERE r.hashref = :hashref")})
public class RegFieldmapping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "listorder")
    private int listorder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tablevisible")
    private boolean tablevisible;
    @Basic(optional = false)
    @NotNull
    @Column(name = "required")
    private boolean required;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hidden")
    private boolean hidden;
    @Basic(optional = false)
    @NotNull
    @Column(name = "multivalue")
    private boolean multivalue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @Column(name = "hashref")
    private Boolean hashref;
    @JoinColumn(name = "reg_field", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegField regField;
    @JoinColumn(name = "reg_itemclass", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegItemclass regItemclass;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;

    public RegFieldmapping() {
    }

    public RegFieldmapping(String uuid, int listorder, boolean tablevisible, boolean required, boolean hidden, boolean multivalue, Date insertdate) {
        this.uuid = uuid;
        this.listorder = listorder;
        this.tablevisible = tablevisible;
        this.required = required;
        this.hidden = hidden;
        this.multivalue = multivalue;
        this.insertdate = insertdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getListorder() {
        return listorder;
    }

    public void setListorder(int listorder) {
        this.listorder = listorder;
    }

    public boolean getTablevisible() {
        return tablevisible;
    }

    public void setTablevisible(boolean tablevisible) {
        this.tablevisible = tablevisible;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean getMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
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

    public Boolean getHashref() {
        return hashref;
    }

    public void setHashref(Boolean hashref) {
        this.hashref = hashref;
    }

    public RegField getRegField() {
        return regField;
    }

    public void setRegField(RegField regField) {
        this.regField = regField;
    }

    public RegItemclass getRegItemclass() {
        return regItemclass;
    }

    public void setRegItemclass(RegItemclass regItemclass) {
        this.regItemclass = regItemclass;
    }

    public RegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(RegStatus regStatus) {
        this.regStatus = regStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RegFieldmapping)) {
            return false;
        }
        RegFieldmapping other = (RegFieldmapping) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegFieldmapping[ uuid=" + uuid + " ]";
    }

}
