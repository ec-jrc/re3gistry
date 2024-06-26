/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

public class CacheAll {

    private EntityManager em;
    private final ItemCache cache;
    private RegLanguagecode regMasterLanguagecode;
    private List <RegLanguagecode> availableLanguages;
    private Logger logger = Configuration.getInstance().getLogger();

    public CacheAll(EntityManager em, ItemCache cache, List <RegLanguagecode> availableLanguages) {
        this.em = em;
        this.cache = cache;
        this.availableLanguages = availableLanguages;
    }

    public void run(String itemclassID) throws Exception {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(em);
        RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
        RegItemManager regItemManager = new RegItemManager(em);
        RegItemclassManager regItemclassManager = new RegItemclassManager(em);

        // Do not run 2 complete cache export together
        if (CacheHelper.checkCacheCompleteRunning()) {
//            return false;
            throw new Exception("The complete CACHE is still running.");
        }

        try {
            RegLanguagecode masterLanguage = languageManager.getMasterLanguage();
                    
            if (availableLanguages==null) {
                if (regMasterLanguagecode != null) {
                    availableLanguages = new ArrayList<>();
                    availableLanguages.add(regMasterLanguagecode);
                } else {
                    availableLanguages = regLanguagecodeManager.getAllActive();
                }
            }
            
            

            try {
                logger.trace("---[ STARTING CACHE ALL]--- @ " + new Date());
                System.out.println("---[ STARTING CACHE ALL]--- @ " + new Date());

                // Getting all ItemClasses
                List<RegItemclass> itemclassList = new ArrayList<>();

                if (itemclassID != null) {
                    RegItemclass regItemclassToCache = regItemclassManager.get(itemclassID);
                    itemclassList.add(regItemclassToCache);
                } else {
                    itemclassList.addAll(regItemclassManager.getAlltemclassOrderAscByDataprocedureorder());
                }

                CacheHelper.createCacheCompleteRunningFile();

                // Iterating on each ItemClasses
                itemclassList.forEach((regItemclass) -> {
                    try {
                        logger.trace("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());
                        System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());

                        // Getting all the RegItems by ItemClass
                        List<RegItem> regitemList = regItemManager.getAllInternalItems(regItemclass);

                        // Iterating on the RegItems
                        regitemList.forEach((regItem) -> {
                            availableLanguages.forEach((RegLanguagecode languageCode) -> {
                                try {
                                    logger.trace("CACHE ALL - regItem: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                                    System.out.println("CACHE ALL - regItem: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                                    if (!em.isOpen()) {
                                        em = em.getEntityManagerFactory().createEntityManager();
                                    }
                                    if (!em.getTransaction().isActive()) {
                                        em.getTransaction().begin();
                                    }
                                    ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                                    getItemByUuidCache(regItem, languageCode.getIso6391code(), itemSupplier);
                                } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                                    if (!em.isOpen()) {
                                        em = em.getEntityManagerFactory().createEntityManager();
                                    }
                                    if (!em.getTransaction().isActive()) {
                                        em.getTransaction().begin();
                                    }

                                    logger.trace("DB Connection problem, trying to reconnect - " + e.getMessage());
                                    System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                                } catch (Exception ex) {
                                    logger.trace("Error in creating the Item - " + regItem.getLocalid() + " with reg_itemclass: " + regItem.getRegItemclass().getLocalid() + ", error: " + ex.getMessage());
                                }
                            });
                        });

                    } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                        if (!em.isOpen()) {
                            em = em.getEntityManagerFactory().createEntityManager();
                        }
                        if (!em.getTransaction().isActive()) {
                            em.getTransaction().begin();
                        }

                        logger.trace("DB Connection problem, trying to reconnect - " + e.getMessage());
                        System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                    } catch (Exception ex) {
                        CacheHelper.deleteCacheCompleteRunningFile();
                        logger.trace("Unexpected exception occured", ex);
                        System.out.println("Unexpected exception occured" + ex.getMessage());
                    }
                });

                CacheHelper.deleteCacheCompleteRunningFile();
                logger.trace("END CACHE ALL @ " + new Date());
                System.out.println("END CACHE ALL @ " + new Date());
            } catch (Exception ex) {
                CacheHelper.deleteCacheCompleteRunningFile();
                logger.trace("Unexpected exception occured", ex);
                System.out.println("Unexpected exception occured" + ex.getMessage());
            }
        } catch (Exception ex) {
            CacheHelper.deleteCacheCompleteRunningFile();
            logger.trace("Unexpected exception occured", ex);
            throw new Exception("Unexpected exception occured. " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private Optional<Item> getItemByUuid(RegItem regItem, String language, ItemSupplier itemSupplier) throws Exception {
        Item cached = cache.getByUuid(language, regItem.getUuid());
//        if (cached != null) {
//            return Optional.of(cached);
//        }
        Item item = itemSupplier.getItem(regItem);
        if (item == null) {
            return Optional.empty();
        }

        if (cached != null) {
            cache.remove(language, regItem.getUuid());
        }
        cache.add(language, item, null);
        return Optional.of(item);
    }
     private void getItemByUuidCache(RegItem regItem, String language, ItemSupplier itemSupplier) throws Exception {
        RegItemhistoryManager regItemHistoryManager = new RegItemhistoryManager(em);
        RegItemhistory regItemHistoryFound = null;
        Item cached = cache.getByUuid(language, regItem.getUuid());
        //Validate if the item is already in cache
        if (cached != null) {
            //If the item is on cache, check if this item has been modified
            regItemHistoryFound = regItemHistoryManager.getMaxVersionByLocalidAndRegItemClass(regItem.getLocalid(), regItem.getRegItemclass());
            //Check if the modification is already stored in cached
            if (cached.getVersionHistory() != null && regItemHistoryFound != null) {
                if (cached.getVersionHistory().size() == regItemHistoryFound.getVersionnumber()) {
                    //If it is stores, establish regItemHistoryFound as null
                    regItemHistoryFound = null;
                }
            }
        }
        //If an item has been modified or it isn't in cached, find it
        if (regItemHistoryFound != null || cached == null) {
            Item item = itemSupplier.getItem(regItem);
            //If the item was a modification, delete the old one.
            if (cached != null) {
                cache.remove(language, regItem.getUuid());
            }
            //Add the item to cache
            cache.add(language, item, null);
        }
  }

}
    
