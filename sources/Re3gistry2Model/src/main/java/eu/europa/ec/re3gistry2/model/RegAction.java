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
@Table(name = "reg_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegAction.findAll", query = "SELECT r FROM RegAction r")
    , @NamedQuery(name = "RegAction.findByUuid", query = "SELECT r FROM RegAction r WHERE r.uuid = :uuid")
    , @NamedQuery(name = "RegAction.findByLabel", query = "SELECT r FROM RegAction r WHERE r.label = :label")
    , @NamedQuery(name = "RegAction.findByChangeRequest", query = "SELECT r FROM RegAction r WHERE r.changeRequest = :changeRequest")
    , @NamedQuery(name = "RegAction.findByChangelog", query = "SELECT r FROM RegAction r WHERE r.changelog = :changelog")
    , @NamedQuery(name = "RegAction.findByRejectMessage", query = "SELECT r FROM RegAction r WHERE r.rejectMessage = :rejectMessage")
    , @NamedQuery(name = "RegAction.findByChangesImplemented", query = "SELECT r FROM RegAction r WHERE r.changesImplemented = :changesImplemented")
    , @NamedQuery(name = "RegAction.findByIssueTrackerLink", query = "SELECT r FROM RegAction r WHERE r.issueTrackerLink = :issueTrackerLink")
    , @NamedQuery(name = "RegAction.findByInsertdate", query = "SELECT r FROM RegAction r WHERE r.insertdate = :insertdate")
    , @NamedQuery(name = "RegAction.findByEditdate", query = "SELECT r FROM RegAction r WHERE r.editdate = :editdate")})
public class RegAction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "label")
    private String label;
    @Size(max = 2147483647)
    @Column(name = "change_request")
    private String changeRequest;
    @Size(max = 2147483647)
    @Column(name = "changelog")
    private String changelog;
    @Size(max = 2147483647)
    @Column(name = "reject_message")
    private String rejectMessage;
    @Column(name = "changes_implemented")
    private Boolean changesImplemented;
    @Size(max = 512)
    @Column(name = "issue_tracker_link")
    private String issueTrackerLink;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    @Column(name = "editdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regAction")
    private List<RegItemproposed> regItemproposedList;
    @OneToMany(mappedBy = "regAction")
    private List<RegItem> regItemList;
    @JoinColumn(name = "reg_item_registry", referencedColumnName = "uuid")
    @ManyToOne
    private RegItem regItemRegistry;
    @JoinColumn(name = "reg_item_register", referencedColumnName = "uuid")
    @ManyToOne
    private RegItem regItemRegister;
    @JoinColumn(name = "reg_status", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegStatus regStatus;
    @JoinColumn(name = "reg_user", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private RegUser regUser;
    @JoinColumn(name = "approved_by", referencedColumnName = "uuid")
    @ManyToOne
    private RegUser approvedBy;
    @JoinColumn(name = "published_by", referencedColumnName = "uuid")
    @ManyToOne
    private RegUser publishedBy;
    @JoinColumn(name = "rejected_by", referencedColumnName = "uuid")
    @ManyToOne
    private RegUser rejectedBy;
    @JoinColumn(name = "submitted_by", referencedColumnName = "uuid")
    @ManyToOne
    private RegUser submittedBy;
    @OneToMany(mappedBy = "regAction")
    private List<RegItemhistory> regItemhistoryList;

    public RegAction() {
    }

    public RegAction(String uuid, String label, Date insertdate) {
        this.uuid = uuid;
        this.label = label;
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

    public String getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(String changeRequest) {
        this.changeRequest = changeRequest;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }

    public Boolean getChangesImplemented() {
        return changesImplemented;
    }

    public void setChangesImplemented(Boolean changesImplemented) {
        this.changesImplemented = changesImplemented;
    }

    public String getIssueTrackerLink() {
        return issueTrackerLink;
    }

    public void setIssueTrackerLink(String issueTrackerLink) {
        this.issueTrackerLink = issueTrackerLink;
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

    public RegItem getRegItemRegistry() {
        return regItemRegistry;
    }

    public void setRegItemRegistry(RegItem regItemRegistry) {
        this.regItemRegistry = regItemRegistry;
    }

    public RegItem getRegItemRegister() {
        return regItemRegister;
    }

    public void setRegItemRegister(RegItem regItemRegister) {
        this.regItemRegister = regItemRegister;
    }

    public RegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(RegStatus regStatus) {
        this.regStatus = regStatus;
    }

    public RegUser getRegUser() {
        return regUser;
    }

    public void setRegUser(RegUser regUser) {
        this.regUser = regUser;
    }

    public RegUser getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(RegUser approvedBy) {
        this.approvedBy = approvedBy;
    }

    public RegUser getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(RegUser publishedBy) {
        this.publishedBy = publishedBy;
    }

    public RegUser getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(RegUser rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public RegUser getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(RegUser submittedBy) {
        this.submittedBy = submittedBy;
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
        
        if (!(object instanceof RegAction)) {
            return false;
        }
        RegAction other = (RegAction) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegAction[ uuid=" + uuid + " ]";
    }

}
