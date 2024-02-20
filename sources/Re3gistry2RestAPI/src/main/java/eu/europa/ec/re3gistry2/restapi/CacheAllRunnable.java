/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi;

import eu.europa.ec.re3gistry2.javaapi.cache.CacheAll;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
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
            CacheAll cacheAll = new CacheAll(em, cache, null, null);
            cacheAll.run(null);
        } catch (Exception e) {
            this.logger.error("Unexpected exception occured", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
