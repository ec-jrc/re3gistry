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
 *  * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.restapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemhistoryManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.restapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.restapi.format.Formatter;
import eu.europa.ec.re3gistry2.restapi.format.ISO19135Formatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONFormatter;
import eu.europa.ec.re3gistry2.restapi.model.Item;
import eu.europa.ec.re3gistry2.restapi.util.NoVersionException;
import eu.europa.ec.re3gistry2.restapi.util.RequestUtil;
import eu.europa.ec.re3gistry2.restapi.util.ResponseUtil;

public class ItemsServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(ItemsServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf;
    private ItemCache cache;
    private Map<String, Formatter> formatters;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.emf = PersistenceFactory.getEntityManagerFactory();
            this.cache = (ItemCache) config.getServletContext().getAttribute(CacheServlet.ATTRIBUTE_CACHE_KEY);
            this.formatters = new HashMap<>();
            addFormatter(new JSONFormatter());
            addFormatter(new ISO19135Formatter());
            // addFormatter(new RDFFormatter());
        } catch (Exception e) {
            LOG.error("Unexpected exception occured: cannot load the configuration system", e);
        }
    }

    private void addFormatter(Formatter formatter) {
        formatters.put(formatter.getFormatName(), formatter);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        String lang = RequestUtil.getParamTrimmed(req, "lang", null);
        String format = RequestUtil.getParamTrimmed(req, "format", "json");
        String uuid = RequestUtil.getParamTrimmed(req, "uuid", null);
        String uri = RequestUtil.getParamTrimmed(req, "uri", null);
        uri = removeTrailingSlashes(uri);

        Predicate<Item> typeFilter = getTypeFilter(path);
        Formatter formatter = formatters.get(format);

        if (typeFilter == null) {
            try {
                ResponseUtil.err(resp, ApiError.NOT_FOUND);
            } catch (Exception e) {
                LOG.error("Unexpected exception occured", e);
            }
            return;
        }
        if (formatter == null) {
            try {
                ResponseUtil.err(resp, ApiError.FORMAT_NOT_SUPPORTED);
            } catch (Exception e) {
                LOG.error("Unexpected exception occured", e);
            }
            return;
        }
        if (uuid == null && uri == null) {
            try {
                ResponseUtil.err(resp, ApiError.UUID_URI_REQUIRED);
            } catch (Exception e) {
                LOG.error("Unexpected exception occured", e);
            }
            return;
        }

        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
            RegLanguagecode masterLanguage = languageManager.getMasterLanguage();
            RegLanguagecode languageCode = getLanguageCode(languageManager, lang, masterLanguage);
            if (languageCode == null) {
                ResponseUtil.err(resp, ApiError.LANGUAGE_NOT_SUPPORTED);
                return;
            }

            RegItemclassManager classManager = new RegItemclassManager(em);
            RegItemManager itemManager = new RegItemManager(em);
            RegItemhistoryManager itemHistoryManager = new RegItemhistoryManager(em);
            RegRelationpredicateManager relationPredicateManager = new RegRelationpredicateManager(em);
            RegRelationManager relationManager = new RegRelationManager(em);
            RegFieldManager fieldManager = new RegFieldManager(em);
            RegLocalizationManager localizationManager = new RegLocalizationManager(em);
            RegFieldmappingManager fieldmappingManager = new RegFieldmappingManager(em);
            RegStatuslocalizationManager statusLocalizationManager = new RegStatuslocalizationManager(em);

            ItemSupplier itemSupplier = new ItemSupplier(classManager,
                    itemManager, itemHistoryManager,
                    relationPredicateManager, relationManager,
                    fieldManager, localizationManager,
                    fieldmappingManager, statusLocalizationManager,
                    masterLanguage, languageCode);

            Optional<Item> optItem;
            if (uuid != null) {
                optItem = getItemByUuid(uuid, lang, itemSupplier);
            } else {
                optItem = getItemByUri(uri, lang, itemSupplier);
            }

            Item item = optItem.filter(typeFilter).orElse(null);
            if (item == null) {
                try {
                    ResponseUtil.err(resp, ApiError.NOT_FOUND);
                } catch (Exception e) {
                    LOG.error("Unexpected exception occured", e);
                }
            } else {
                try {
                    ResponseUtil.ok(resp, item, languageCode, formatter);
                } catch (Exception e) {
                    LOG.error("Unexpected exception occured", e);
                }
            }
        } catch (NoResultException e) {
            try {
                ResponseUtil.err(resp, ApiError.NOT_FOUND);
            } catch (Exception ex) {
                LOG.error("Unexpected exception occured", e);
            }
        } catch (NoVersionException e) {
            try {
                ResponseUtil.err(resp, ApiError.VERSION_NOT_FOUND);
            } catch (Exception ex) {
                LOG.error("Unexpected exception occured", e);
            }
        } catch (Exception e) {
            LOG.error("Unexpected exception occured", e);
            try {
                ResponseUtil.err(resp, ApiError.INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                LOG.error("Unexpected exception occured", e);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private RegLanguagecode getLanguageCode(RegLanguagecodeManager languageManager, String lang, RegLanguagecode fallback) throws Exception {
        if (lang == null) {
            return fallback;
        }
        for (RegLanguagecode l : languageManager.getAll()) {
            if (l.getIso6391code().equals(lang) || l.getIso6392code().equals(lang)) {
                return l;
            }
        }
        return null;
    }

    private Predicate<Item> getTypeFilter(String path) {
        switch (path) {
            case "/registry":
                return item -> item.getType().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY);
            case "/register":
                return item -> item.getType().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER);
            case "/item":
                return item -> item.getType().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM);
            case "/any":
                return any -> true;
            default: // 404
                return null;
        }
    }

    private String removeTrailingSlashes(String s) {
        if (s == null) {
            return null;
        }
        while (!s.isEmpty() && s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.isEmpty()) {
            return null;
        }
        return s;
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

    private Optional<Item> getItemByUri(String uri, String language, ItemSupplier itemSupplier) throws Exception {
        Item cached = cache.getByUrl(language, uri);
        if (cached != null) {
            return Optional.of(cached);
        }

        Item item = itemSupplier.getItemByUri(uri);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
    }

}
