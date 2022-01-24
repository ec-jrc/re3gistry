/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi.cache;

import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationhistoryManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.restapi.ItemHistorySupplier;
import eu.europa.ec.re3gistry2.restapi.ItemSupplier;
import eu.europa.ec.re3gistry2.restapi.model.Item;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.Logger;

public class CacheAll implements Runnable {

    private EntityManagerFactory emf;
    private ItemCache cache;
    private Logger logger;
    private RegLanguagecode languageCode;

    public CacheAll(EntityManagerFactory emf, ItemCache cache, Logger logger, RegLanguagecode languageCode) {
        try {
            this.emf = emf;
            this.cache = cache;
            this.logger = logger;
            this.languageCode = languageCode;
        } catch (Exception ex) {
        }
    }

    @Override
    public void run() {

        EntityManager em = null;
        try {

            em = this.emf.createEntityManager();

            RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
            RegItemManager regItemManager = new RegItemManager(em);
            RegItemclassManager regItemclassManager = new RegItemclassManager(em);

            this.logger.info("---[ STARTING CACHE ALL]--- @ " + new Date());
            System.out.println("---[ STARTING CACHE ALL]--- @ " + new Date());

            RegLanguagecode masterLanguage = languageManager.getMasterLanguage();

            // Getting all ItemClasses
            List<RegItemclass> itemclassList = regItemclassManager.getAlltemclassOrderAscByDataprocedureorder();

            // Iterating on each ItemClasses
            for (RegItemclass regItemclass : itemclassList) {
                try {
                    this.logger.info("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());
                    System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - @ " + new Date());

                    // Getting all the RegItems by ItemClass
                    List<RegItem> regitemList = regItemManager.getAll(regItemclass);

                    // Iterating on the RegItems
                    for (RegItem regItem : regitemList) {

                        try {
                            //this.logger.info("CACHE ALL - regItem: " + regItem.getLocalid() + " - @ " + new Date());
                            //System.out.println("CACHE ALL - regItem: " + regItem.getLocalid() + " - @ " + new Date());
                            // Iterating on all the available languages
                            //for (RegLanguagecode languageCode : availableLanguages) {
                            this.logger.info("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                            System.out.println("CACHE ALL - regItemClass: " + regItemclass.getLocalid() + " - regItem: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());

                            ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                            //Optional<Item> optItem = getItemByUuid(regItem.getUuid(), languageCode.getIso6391code(), itemSupplier);
                            Optional<Item> optItem = getItemByUuid(regItem, languageCode.getIso6391code(), itemSupplier);
                            //}
                        } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                            if (!em.isOpen()) {
                                em = this.emf.createEntityManager();
                            }
                            if (!em.getTransaction().isActive()) {
                                em.getTransaction().begin();
                            }

                            this.logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                            System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                        }
                    }

                    // Getting all RegItemHistory by ItemClass                
                    //List<RegItemhistory> regitemHistoryList = regItemhistoryManager.getByRegItemClass(regItemclass);
                    // Iterating on RegItemHistory
                    //for (RegItemhistory regItemhistory : regitemHistoryList) {
                    //this.logger.info("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + " - @ " + new Date());
                    //System.out.println("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + " - @ " + new Date());
                    // Iterating on all the available languages
                    //List<RegLocalizationhistory> localizationHistoryListForRegItem = regLocalizationhistoryManager.getAll(regItemhistory);
                    //for (RegLocalizationhistory regLocalizationhistory : localizationHistoryListForRegItem) {
                    //RegLanguagecode languageCode = regLocalizationhistory.getRegLanguagecode();
                    //this.logger.info("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                    //System.out.println("CACHE ALL - regItemhistory: " + regItemhistory.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                    //ItemHistorySupplier itemHistorySupplier = new ItemHistorySupplier(em, masterLanguage, languageCode);
                    //Optional<Item> optItem = getItemHistoryByUuid(regItemhistory.getUuid(), languageCode.getIso6391code(), itemHistorySupplier);
                    //}
                    //}
                } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                    if (!em.isOpen()) {
                        em = this.emf.createEntityManager();
                    }
                    if (!em.getTransaction().isActive()) {
                        em.getTransaction().begin();
                    }

                    this.logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                    System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                }
            }

            this.logger.info("END CACHE ALL @ " + new Date());
            System.out.println("END CACHE ALL @ " + new Date());

        } catch (Exception e) {
            this.logger.error("Unexpected exception occured", e);
            //ResponseUtil.err(resp, ApiError.INTERNAL_SERVER_ERROR);
        } finally {
            if (em != null) {
                em.close();
            }
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
}
