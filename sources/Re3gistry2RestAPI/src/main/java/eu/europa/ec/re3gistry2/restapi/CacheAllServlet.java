/*
 * /*
 *  * Copyright 2007,2016 EUROPEAN UNION
 *  * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *  * Date: 2020/05/11
 *  * Authors:
 *  * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *  * National Land Survey of Finland, SDI Services - inspire@nls.fi
 *  *
 *  * This work was supported by the Interoperability solutions for public
 *  * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 *  * through Action 2016.10: European Location Interoperability Solutions
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.restapi;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CacheAllServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(CacheServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf;
    private ItemCache cache;
    private Logger logger;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.emf = PersistenceFactory.getEntityManagerFactory();
            this.cache = (ItemCache) config.getServletContext().getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
            this.logger = LogManager.getLogger("Re3gistry2");
        } catch (Exception e) {
            this.logger.error("Unexpected exception occured: cannot load the configuration system", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Getting all the available languages
            EntityManager em = this.emf.createEntityManager();

            RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
            List<RegLanguagecode> availableLanguages = languageManager.getAllActive();

            ExecutorService executor = Executors.newFixedThreadPool(availableLanguages.size());

            Future result = executor.submit(new CacheAllRunnable(em, this.cache, this.logger, req));
            executor.shutdown();
        } catch (Exception e) {
        }
    }
}
