/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi;

import eu.europa.ec.re3gistry2.javaapi.cache.CacheAll;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemHistorySupplier;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.model.RegItem;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class CacheAllRunnable implements Runnable {

    private final EntityManager em;
    private final ItemCache cache;
    private final Logger logger;
    private HttpServletRequest req;

    public CacheAllRunnable(EntityManager em, ItemCache cache, Logger logger, HttpServletRequest req) {
            this.em = em;
            this.cache = cache;
            this.logger = logger;
            this.req = req;
    }

    @Override
    public void run() {

        try {
            CacheAll cacheAll = new CacheAll(em, cache, req);
            cacheAll.run();
        } catch (Exception e) {
            this.logger.error("Unexpected exception occured", e);
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
