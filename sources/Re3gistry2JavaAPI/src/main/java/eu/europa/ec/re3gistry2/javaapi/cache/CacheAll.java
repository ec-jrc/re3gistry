/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
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

    public CacheAll(EntityManager em, ItemCache cache, RegLanguagecode regMasterLanguagecode) {
        this.em = em;
        this.cache = cache;
        this.regMasterLanguagecode = regMasterLanguagecode;
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
            List<RegLanguagecode> availableLanguages;
            RegLanguagecode masterLanguage = languageManager.getMasterLanguage();
            if (regMasterLanguagecode != null) {
                availableLanguages = new ArrayList<>();
                availableLanguages.add(regMasterLanguagecode);
            } else {
                availableLanguages = regLanguagecodeManager.getAllActive();
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
                itemclassList.parallelStream().forEach((regItemclass) -> {
                    try {
                        logger.trace("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());
                        System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());

                        // Getting all the RegItems by ItemClass
                        List<RegItem> regitemList = regItemManager.getAll(regItemclass);

                        // Iterating on the RegItems
                        regitemList.parallelStream().forEach((regItem) -> {
                            availableLanguages.parallelStream().forEach((RegLanguagecode languageCode) -> {
                                try {
                                    logger.trace("CACHE ALL - regItem: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                                    System.out.println("CACHE ALL - regItem: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());

                                    ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                                    Optional<Item> optItem = getItemByUuid(regItem, languageCode.getIso6391code(), itemSupplier);

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
        if (cached != null) {
            return Optional.of(cached);
        }
        Item item = itemSupplier.getItem(regItem);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item, null);
        return Optional.of(item);
    }

}
