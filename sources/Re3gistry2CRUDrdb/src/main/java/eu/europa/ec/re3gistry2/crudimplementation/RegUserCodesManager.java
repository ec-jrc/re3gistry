/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.crudimplementation;

import eu.europa.ec.re3gistry2.crudimplementation.constants.ErrorConstants;
import eu.europa.ec.re3gistry2.crudinterface.IRegUserCodesManager;
import eu.europa.ec.re3gistry2.model.RegUserCodes;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author oriol
 */
public class RegUserCodesManager implements IRegUserCodesManager {
    
    private EntityManager em;
    
    public RegUserCodesManager(EntityManager em){
        this.em = em;
    }

    /**
     * Returns the RegUserCodes object
     * 
     * @param uuid The uuid of the RegUserCodes
     * @return RegUserCodes object with the uuid passed by parameter
     * @throws Exception 
     */
    @Override
    public RegUserCodes get(String uuid) throws Exception {
        //Checking parameters
        if(uuid == null){
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }
        
        //Preparing query
        Query q = this.em.createNamedQuery("RegUserCodes.findByUuid");
        q.setParameter("uuid",uuid);
        return (RegUserCodes) q.getSingleResult();
    }

    /**
     * Returns all the RegUserCodes
     * 
     * @return all the RegUserCodes
     * @throws Exception
     */
    @Override
    public List<RegUserCodes> getByRegUser(String regUser) throws Exception {
        //Checking parameters
        if(regUser == null){
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "regUser"));
        }
        
        //Preparing query
        Query q = this.em.createNamedQuery("RegUserCodes.findByRegUser");
        q.setParameter("reguser",regUser);
        return (List<RegUserCodes>) q.getResultList();
    }
    
    /**
     * Returns the RegUserCode by the code
     * @param code the code
     * @return the RegUserCode
     * @throws Exception 
     */
    @Override
    public RegUserCodes getByCode(String code) throws Exception {
        //Checking parameters
        if(code == null){
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "regUser"));
        }
        
        //Preparing query
        Query q = this.em.createNamedQuery("RegUserCodes.findByCode");
        q.setParameter("code",code);
        return (RegUserCodes) q.getSingleResult();
    }

    @Override
    public List<RegUserCodes> getAll() throws Exception {
        //Preparing query
        Query q = this.em.createNamedQuery("RegUserCodes.findAll");
        return (List<RegUserCodes>) q.getResultList();
    }

    @Override
    public boolean add(RegUserCodes regUserCode) throws Exception {
        //Checking parameters
        if (regUserCode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUserCodes.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regUserCode);

        return true;
    }
    
    @Override
    public boolean delete(RegUserCodes regUserCode) throws Exception {
        //Checking parameters
        if (regUserCode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUserCodes.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }
        
        if(!em.contains(regUserCode)){
            regUserCode = em.merge(regUserCode);
        }
        
        //Removing the object
        this.em.remove(regUserCode);

        return true;
    }

    @Override
    public List<RegUserCodes> getByInsertDate(Date date) throws Exception {
        //Checking parameters
        if(date == null){
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "regUser"));
        }
        
        //Preparing query
        Query q = this.em.createNamedQuery("RegUserCodes.findByDate");
        q.setParameter("insertdate",date);
        return (List<RegUserCodes>) q.getResultList();
    }
}
