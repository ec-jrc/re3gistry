/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.javaapi.handler;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserCodesManager;
import eu.europa.ec.re3gistry2.model.RegUserCodes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

public class RegUserRegCodesHandler {
    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManager;

    // System localization
    ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();
    
    public RegUserRegCodesHandler () throws Exception{
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }
    
    public boolean deleteCode(RegUserCodes regUserCodes){
        RegUserCodesManager regUserCodesManager = new RegUserCodesManager(entityManager);
        
        boolean operationResult = false;
        
        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized(sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }

                //Remove de code
                regUserCodesManager.delete(regUserCodes);
                
                operationResult = true;
                
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */
        } catch (Exception e){
            logger.error("@ RegUserRegCodesHandler.deleteCode: generic error.", e);
        } finally{
            if (entityManager != null) {
                //entityManager.close();
            }
        }
        return operationResult;
    }
    /**
     * Get all codes expired
     * 
     * @return list of the expired codes
     */
    public List<RegUserCodes> getExpiredCodes(){
        List<RegUserCodes> codes = null;
        try {
            RegUserCodesManager regUserCodesManager = new RegUserCodesManager(entityManager);
            Date currentDate = new Date();
            
            // convert date to localdatetime
            LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            localDateTime = localDateTime.minusDays(1);
            
            // convert LocalDateTime to date
            Date yesterdayDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            
            codes = regUserCodesManager.getByInsertDate(yesterdayDate);
            
            
        } catch (Exception e) {
            logger.error("@ RegUserRegCodesHandler.getExpiredCodes: generic error.", e);
        }
        return codes;
    }
}
