/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemHistorySupplier;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class CacheAll {

    private EntityManager em;
    private final ItemCache cache;
    private static HttpServletRequest request;

    public CacheAll(EntityManager em, ItemCache cache, HttpServletRequest request) {
        this.em = em;
        this.cache = cache;
        this.request = request;
    }

    public void run() throws Exception {
        // Init logger
        Logger logger = Configuration.getInstance().getLogger();

        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(em);
        RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
        RegItemManager regItemManager = new RegItemManager(em);
        RegItemclassManager regItemclassManager = new RegItemclassManager(em);

        // Do not run 2 complete Solr export together
        if (checkCacheCompleteRunning()) {
//            return false;
            throw new Exception("The complete CACHE is still running.");
        }

        try {
            RegLanguagecode masterLanguage = languageManager.getMasterLanguage();
            List<RegLanguagecode> availableLanguages = regLanguagecodeManager.getAllActive();

            availableLanguages.forEach((RegLanguagecode languageCode) -> {

                try {
                    logger.info("---[ STARTING CACHE ALL]--- @ " + new Date());
                    System.out.println("---[ STARTING CACHE ALL]--- @ " + new Date());

                    // Getting all ItemClasses
                    List<RegItemclass> itemclassList = regItemclassManager.getAlltemclassOrderAscByDataprocedureorder();

                    createCacheCompleteRunningFile();

                    // Iterating on each ItemClasses
                    itemclassList.forEach((regItemclass) -> {
                        try {
                            logger.info("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());
                            System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());

                            // Getting all the RegItems by ItemClass
                            List<RegItem> regitemList = regItemManager.getAll(regItemclass);

                            // Iterating on the RegItems
                            for (RegItem regItem : regitemList) {

                                try {
                                    logger.info("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                                    System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());

                                    ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                                    Optional<Item> optItem = getItemByUuid(regItem, languageCode.getIso6391code(), itemSupplier);
                                } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                                    if (!em.isOpen()) {
                                        em = em.getEntityManagerFactory().createEntityManager();
                                    }
                                    if (!em.getTransaction().isActive()) {
                                        em.getTransaction().begin();
                                    }

                                    logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                                    System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                                }
                            }

                            // Getting all RegItemHistory by ItemClass                
                            //List<RegItemhistory> regitemHistoryList = regItemhistoryManager.getByRegItemClass(regItemclass);
                            // Iterating on RegItemHistory
                            //for (RegItemhistory regItemhistory : regitemHistoryList) {
                            //logger.info("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + " - @ " + new Date());
                            //System.out.println("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + " - @ " + new Date());
                            // Iterating on all the available languages
                            //List<RegLocalizationhistory> localizationHistoryListForRegItem = regLocalizationhistoryManager.getAll(regItemhistory);
                            //for (RegLocalizationhistory regLocalizationhistory : localizationHistoryListForRegItem) {
                            //RegLanguagecode languageCode = regLocalizationhistory.getRegLanguagecode();
                            //logger.info("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                            //System.out.println("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                            //ItemHistorySupplier itemHistorySupplier = new ItemHistorySupplier(em, masterLanguage, languageCode);
                            //Optional<Item> optItem = getItemHistoryByUuid(regItemhistory.getUuid(), languageCode.getIso6391code(), itemHistorySupplier);
                            //}
                            //}
                        } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                            if (!em.isOpen()) {
                                em = em.getEntityManagerFactory().createEntityManager();
                            }
                            if (!em.getTransaction().isActive()) {
                                em.getTransaction().begin();
                            }

                            logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                            System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(CacheAll.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    deleteCacheCompleteRunningFile();
                    logger.info("END CACHE ALL @ " + new Date());
                    System.out.println("END CACHE ALL @ " + new Date());
                } catch (Exception ex) {
                    logger.error("Unexpected exception occured", ex);
                    System.out.println("Unexpected exception occured" + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            logger.error("Unexpected exception occured", ex);
            deleteCacheCompleteRunningFile();
//            return false;
            throw new Exception("Unexpected exception occured. " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
//            return true;
        }
    }

    private Optional<Item> getItemByUuid(String uuid, String language, ItemSupplier itemSupplier) throws Exception {
        Item cached = cache.getByUuid(language, uuid);
        if (cached != null) {
            return Optional.of(cached);
        }
        Item item = itemSupplier.getItemByUuid(uuid);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
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

        cache.add(language, item);
        return Optional.of(item);
    }

    private Optional<Item> getItemHistoryByUuid(String uuid, String language, ItemHistorySupplier itemHistorySupplier) throws Exception {
        Item cached = cache.getByUuid(language, uuid);
        if (cached != null) {
            return Optional.of(cached);
        }
        Item item = itemHistorySupplier.getItemHistoryByUuid(uuid);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
    }

    public static boolean checkCacheCompleteRunning() {
        boolean running = false;
        try {
            String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;
            File systemInstalledFile = new File(systemCacheallPath);
            if (systemInstalledFile.exists() && !systemInstalledFile.isDirectory()) {
                running = true;
            }

        } catch (Exception e) {
        }
        return running;
    }

    private static void createCacheCompleteRunningFile() throws Exception {
        String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;
        File systemInstalledFile = new File(systemCacheallPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {

            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                //throw new IOException();
            }

        } catch (IOException ex) {
        }
    }

    private static void deleteCacheCompleteRunningFile() {
        String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;

        try {
            File file = new File(systemCacheallPath);

            boolean success = file.delete();

            if (!success) {
                //throw new IOException();
            }
        } catch (Exception ex) {
        }
    }
}
