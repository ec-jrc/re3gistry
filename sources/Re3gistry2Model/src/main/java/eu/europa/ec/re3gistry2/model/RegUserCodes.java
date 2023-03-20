/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.model;

import eu.europa.ec.re3gistry2.model.utility.RandomStringUtils;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserCodesUuidHelper;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author oriol
 */
@Entity
@Table(name = "reg_user_codes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegUserCodes.findAll", query = "SELECT r from RegUserCodes r"),
    @NamedQuery(name = "RegUserCodes.findByUuid", query = "SELECT r from RegUserCodes r WHERE r.uuid = :uuid"),
    @NamedQuery(name = "RegUserCodes.findByRegUser", query = "SELECT r from RegUserCodes r WHERE r.regUser = :reguser"),
    @NamedQuery(name = "RegUserCodes.findByCode", query = "SELECT r from RegUserCodes r WHERE r.code = :code"),
    @NamedQuery(name = "RegUserCodes.findByDate", query = "SELECT r from RegUserCodes r WHERE r.insertdate >= :insertdate")
})
public class RegUserCodes implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final int EXPIRATION = 60 * 24;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "uuid")
    private String uuid;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "reg_user")
    private String regUser;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "code")
    private String code;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "action")
    private String action;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "insertdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertdate;
    
    /*private Date expiryDate;
   
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }*/
    
    public RegUserCodes(){
    }
    
    public RegUserCodes(String uuid, String regUser, String code, String action, Date insertdate)throws NoSuchAlgorithmException{
        this.uuid = uuid;
        this.regUser = regUser;
        this.code = RandomStringUtils.encodeString(50,regUser);
        this.action = action;
        this.insertdate = insertdate;
    }
    
    public RegUserCodes(String uuid, String regUser, String action, Date insertdate) throws NoSuchAlgorithmException{
        this.uuid = uuid;
        this.regUser = regUser;
        this.code = RandomStringUtils.encodeString(50,regUser);
        this.action = action;
        this.insertdate = insertdate;
    }
    
    public RegUserCodes(String regUser, String action, Date insertdate) throws NoSuchAlgorithmException, Exception{
        this.uuid = RegUserCodesUuidHelper.getUuid(regUser, action);
        this.regUser = regUser;
        this.code = RandomStringUtils.encodeString(50,action+regUser);
        this.action = action;
        this.insertdate = insertdate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRegUser() {
        return regUser;
    }

    public String getCode() {
        return code;
    }

    public String getAction() {
        return action;
    }

    public Date getInsertdate() {
        return insertdate;
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
        RegUserCodes other = (RegUserCodes) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.europa.ec.re3gistry2.model.RegUserCodes[ uuid=" + uuid + " ]";
    }
}
