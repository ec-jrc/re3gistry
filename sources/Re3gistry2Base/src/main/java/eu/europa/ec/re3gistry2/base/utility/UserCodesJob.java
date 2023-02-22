/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.base.utility;

import eu.europa.ec.re3gistry2.crudimplementation.RegUserCodesManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserCodes;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

public class UserCodesJob{

    private EntityManager em;
    // Init logger
    Logger logger;
    // System localization
    ResourceBundle systemLocalization;
    // Synchronization object
    private static final Object sync = new Object();
    
    public UserCodesJob() throws Exception{
        this.em = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        this.logger = Configuration.getInstance().getLogger();
        this.systemLocalization = Configuration.getInstance().getLocalization();
    }
    
    public void deleteExpiredCodes(){
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                List<RegUserCodes> expired = null;
                try {
                    RegUserCodesManager regUserCodesManager = new RegUserCodesManager(em);
                    RegUserManager regUserManager = new RegUserManager(em);
                    Date currentDate = new Date();

                    // convert date to localdatetime
                    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    localDateTime = localDateTime.minusDays(1);

                    // convert LocalDateTime to date
                    Date yesterdayDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                    expired = regUserCodesManager.getByInsertDate(yesterdayDate);

                    if(!expired.isEmpty()){
                        for(RegUserCodes ruCodes : expired){

                            RegUser regUser = regUserManager.get(ruCodes.getRegUser());
                            // The writing operation on the Database are synchronized
                            /* ## Start Synchronized ## */
                            synchronized (sync) {
                                if (!em.getTransaction().isActive()) {
                                    em.getTransaction().begin();
                                }
                                regUserCodesManager.delete(ruCodes);
                                //First we delete all group mapping for the user
                                RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(em);
                                List<RegUserRegGroupMapping> groups = regUserRegGroupMappingManager.getAll(regUser);
                                for(RegUserRegGroupMapping group : groups){
                                    regUserRegGroupMappingManager.delete(group);
                                }

                                // Remove the user
                                regUserManager.delete(regUser);
                            }
                            /* ## End Synchronized ## */
                        }
                        em.getTransaction().commit();
                    }
                } catch (Exception e) {
                    System.out.println("@ UserCodesJob.deleteExpiredCodes: generic error."+ e);
                }
            }            
        };
        
        Timer timer = new Timer("deleteExpiredCodes");
        
        long delay = 1000L;
        //Set the period as every 5 minutes
        long period = 1000L * 60L * 5L;
        timer.scheduleAtFixedRate(task, delay, period);
    }
}
