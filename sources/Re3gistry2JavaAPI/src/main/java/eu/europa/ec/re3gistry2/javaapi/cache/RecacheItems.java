/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegRelation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class RecacheItems extends Thread {

    private EntityManager em;
    private ItemCache cache;
    private final Logger logger;
    private final List<RegItem> regItems;
    private final HttpServletRequest request;

    private static final String TYPE_REGISTRY = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY;
    private static final String TYPE_REGISTER = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER;
    private static final String TYPE_ITEM = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;

    public RecacheItems(EntityManager em, HttpServletRequest request, List<RegItem> regItems) {
        this.em = em;
        this.request = request;
        this.regItems = regItems;

        // Init logger
        this.logger = Configuration.getInstance().getLogger();

        this.cache = (ItemCache) request.getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
        if (cache == null) {
            this.cache = new EhCache();
            request.setAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY, cache);
        }
    }

    @Override
    public void run() {
        try {
            if (!CacheHelper.checkCacheCompleteRunning()) {
                cacheItems(regItems);
            }
        } catch (Exception e) {
            CacheHelper.deleteCacheCompleteRunningFile();
            this.logger.error("Unexpected exception occured", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void cacheItems(List<RegItem> regItems) throws Exception {
        CacheHelper.createCacheCompleteRunningFile();
        RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);

        this.logger.info("---[ STARTING RECACHE]--- @ " + new Date());
        System.out.println("---[ STARTING RECACHE]--- @ " + new Date());

        RegLanguagecode masterLanguage = languageManager.getMasterLanguage();
        List<RegLanguagecode> availableLanguages = languageManager.getAllActive();

        RegRelationpredicateManager relationpredicateManager = new RegRelationpredicateManager(em);
        RegRelationManager regRelationManager = new RegRelationManager(em);
        List<RegItem> itemsToRecache = new ArrayList<>();

        // Iterating on the RegItems
        regItems.parallelStream().forEach((regItem) -> {
            availableLanguages.parallelStream().forEach((RegLanguagecode languageCode) -> {
                try {
                    this.logger.info("RECACHE - regItems: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                    System.out.println("RECACHE - regItems: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());

                    ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                    Optional<Item> optItem = updateCacheForItemByUuid(regItem, languageCode.getIso6391code(), itemSupplier);

                    switch (regItem.getRegItemclass().getRegItemclasstype().getLocalid()) {
                        case TYPE_REGISTRY:
                            break;
                        case TYPE_REGISTER:

                            RegItem registry = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY)).get(0).getRegItemObject();
                            if (!itemsToRecache.contains(registry)) {
                                itemsToRecache.add(registry);
                            }
                            break;
                        case TYPE_ITEM:
                            List<RegRelation> collectionList = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION));
                            if (collectionList != null && !collectionList.isEmpty()) {
                                RegItem collection = collectionList.get(0).getRegItemObject();
                                if (!itemsToRecache.contains(collection)) {
                                    itemsToRecache.add(collection);
                                }
                            }
                            List<RegRelation> successorRelationList = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_SUCCESSOR));
                            successorRelationList.stream().filter((regRelation) -> (!itemsToRecache.contains(regRelation.getRegItemObject()))).forEachOrdered((regRelation) -> {
                                itemsToRecache.add(regRelation.getRegItemObject());
                            });
                            List<RegRelation> predecessorRelationList = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_PREDECESSOR));
                            predecessorRelationList.stream().filter((regRelation) -> (!itemsToRecache.contains(regRelation.getRegItemObject()))).forEachOrdered((regRelation) -> {
                                itemsToRecache.add(regRelation.getRegItemObject());
                            });
//                            List<RegRelation> referenceRelationList = regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE));
//                            referenceRelationList.stream().filter((regRelation) -> (!itemsToRecache.contains(regRelation.getRegItemObject()))).forEachOrdered((regRelation) -> {
//                                itemsToRecache.add(regRelation.getRegItemObject());
//                            });
                            break;
                        default:
                            throw new RuntimeException("Unexpected type");
                    }
                } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                    if (!em.isOpen()) {
                        em = em.getEntityManagerFactory().createEntityManager();
                    }
                    if (!em.getTransaction().isActive()) {
                        em.getTransaction().begin();
                    }
                    CacheHelper.deleteCacheCompleteRunningFile();
                    this.logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                    System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
                } catch (Exception ex) {
                    CacheHelper.deleteCacheCompleteRunningFile();
                    this.logger.info("Error: " + ex.getMessage());
                    System.out.println("Error: " + ex.getMessage());
                }
            });
        });

        // Iterating on the relation RegItems
        //if the item has a collection, parent, successor, predecessor update it
        itemsToRecache.parallelStream().forEach((regItem) -> {
            try {
                for (RegLanguagecode languageCode : availableLanguages) {
                    this.logger.info("RECACHE - regItems: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());
                    System.out.println("RECACHE - regItems: " + regItem.getLocalid() + ", language: " + languageCode.getIso6391code() + " - @ " + new Date());

                    ItemSupplier itemSupplier = new ItemSupplier(em, masterLanguage, languageCode);
                    Optional<Item> optItem = updateCacheForItemByUuid(regItem, languageCode.getIso6391code(), itemSupplier);
                }
            } catch (javax.persistence.PersistenceException | org.eclipse.persistence.exceptions.DatabaseException | org.postgresql.util.PSQLException e) {
                if (!em.isOpen()) {
                    em = em.getEntityManagerFactory().createEntityManager();
                }
                if (!em.getTransaction().isActive()) {
                    em.getTransaction().begin();
                }
                CacheHelper.deleteCacheCompleteRunningFile();
                this.logger.info("DB Connection problem, trying to reconnect - " + e.getMessage());
                System.out.println("DB Connection problem, trying to reconnect - " + e.getMessage());
            } catch (Exception ex) {
                CacheHelper.deleteCacheCompleteRunningFile();
                this.logger.info("Error: " + ex.getMessage());
                System.out.println("Error: " + ex.getMessage());
            }
        });

        CacheHelper.deleteCacheCompleteRunningFile();
        this.logger.info("END RECACHE @ " + new Date());
        System.out.println("END RECACHE @ " + new Date());

    }

    private Optional<Item> updateCacheForItemByUuid(RegItem regItem, String language, ItemSupplier itemSupplier) throws Exception {
        Item cached = cache.getByUuid(language, regItem.getUuid());
        if (cached != null) {
            cache.remove(language, regItem.getUuid());
        }
        Item item = itemSupplier.getItem(regItem);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item, null);
        return Optional.of(item);
    }

}
