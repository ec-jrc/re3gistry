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
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.restapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.restapi.format.CSVFormatter;
import eu.europa.ec.re3gistry2.restapi.format.Formatter;
import eu.europa.ec.re3gistry2.restapi.format.ISO19135Formatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONFormatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONInternalFormatter;
import eu.europa.ec.re3gistry2.restapi.format.RDFFormatter;
import eu.europa.ec.re3gistry2.restapi.format.RORFormatter;
import eu.europa.ec.re3gistry2.restapi.format.XMLFormatter;
import eu.europa.ec.re3gistry2.restapi.model.Item;
import eu.europa.ec.re3gistry2.restapi.util.NoVersionException;
import eu.europa.ec.re3gistry2.restapi.util.RequestUtil;
import eu.europa.ec.re3gistry2.restapi.util.ResponseUtil;

public class ItemsServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(ItemsServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private static EntityManagerFactory emf;
    private static ItemCache cache;
    private static Map<String, Formatter> formatters;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.emf = PersistenceFactory.getEntityManagerFactory();
            this.cache = (ItemCache) config.getServletContext().getAttribute(CacheServlet.ATTRIBUTE_CACHE_KEY);
            this.formatters = new HashMap<>();

            addFormatter(new JSONInternalFormatter());
            addFormatter(new XMLFormatter());
            addFormatter(new JSONFormatter());
            addFormatter(new ISO19135Formatter());
            addFormatter(new RDFFormatter());
            addFormatter(new CSVFormatter(emf));
            addFormatter(new RORFormatter());
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
        try {
            String path = req.getPathInfo();
            String lang = RequestUtil.getParamTrimmed(req, "lang", null);
            String uuid = RequestUtil.getParamTrimmed(req, "uuid", null);
            String uri = RequestUtil.getParamTrimmed(req, "uri", null);
            String format = RequestUtil.getParamTrimmed(req, "format", null);
            uri = removeTrailingSlashes(uri);

            Predicate<Item> typeFilter = getTypeFilter(path);
            Formatter formatter = formatters.get(format);

            if (typeFilter == null) {
                ResponseUtil.err(resp, ApiError.NOT_FOUND);
                return;
            }
            if (formatter == null) {
                ResponseUtil.err(resp, ApiError.FORMAT_NOT_SUPPORTED);
                return;
            }
            if (uuid == null && uri == null) {
                ResponseUtil.err(resp, ApiError.UUID_URI_REQUIRED);
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

                ItemSupplier itemSupplier = new ItemSupplier(em,
                        masterLanguage, languageCode);
                ItemHistorySupplier itemHistorySupplier = new ItemHistorySupplier(em,
                        masterLanguage, languageCode);

                Optional<Item> optItem;
                if (uuid != null) {
                    try {
                        optItem = getItemByUuid(uuid, lang, itemSupplier);
                    } catch (Exception ex) {
                        optItem = getItemHistoryByUuid(uuid, lang, itemHistorySupplier);
                    }
                } else {
                    Integer version = getVersionFromUri(uri);
                    //version is null if the uri doesnt contain any version information, so is a RegItem
                    if (version != null) {
                        if (version == 0) {
                            optItem = getItemByUri(uri.replace(":" + version, ""), lang, itemSupplier);
                        } else {
                            optItem = getItemHistoryByUri(uri, version, lang, itemHistorySupplier);
                            if (!optItem.isPresent()) {
                                optItem = getItemByUri(uri.replace(":" + version, ""), lang, itemSupplier);
                            }
                        }
                    } else {
                        if (uri.endsWith(":0")) {
                            optItem = getItemByUri(uri.replace("0:", ""), lang, itemSupplier);
                        } else {
                            optItem = getItemByUri(uri, lang, itemSupplier);
                        }
                    }
                }

                //try to see if is a status request
                if (!optItem.isPresent()) {
                    StatusSupplier statusSupplier = new StatusSupplier(em, masterLanguage, languageCode);
                    if (uuid != null) {
                        optItem = getItemStatusByUuid(uuid, lang, statusSupplier);
                    } else {
                        optItem = getItemStatusByUri(uri, lang, statusSupplier);
                    }
                }

                Item item = optItem.filter(typeFilter).orElse(null);
                if (item == null) {
                    ResponseUtil.err(resp, ApiError.NOT_FOUND);
                } else {
                    ResponseUtil.ok(resp, item, languageCode, formatter);
                }
            } catch (NoResultException e) {
                try {
                    ResponseUtil.err(resp, ApiError.NOT_FOUND);
                } catch (IOException ex) {
                    LOG.error("Unexpected exception occured", ex);
                }
            } catch (NoVersionException e) {
                try {
                    ResponseUtil.err(resp, ApiError.VERSION_NOT_FOUND);
                } catch (IOException ex) {
                    LOG.error("Unexpected exception occured", ex);
                }
            } catch (Exception e) {
                try {
                    LOG.error("Unexpected exception occured", e);
                    ResponseUtil.err(resp, ApiError.INTERNAL_SERVER_ERROR);
                } catch (IOException ex) {
                    LOG.error("Unexpected exception occured", ex);
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        } catch (IOException e) {
            LOG.error("Unexpected exception occured", e);
        }
    }

    private int countOccurance(String whereToCount, String whatToCount) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = whereToCount.indexOf(whatToCount, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += whatToCount.length();
            }
        }
        return count;
    }

    private Integer getVersionFromUri(String uri) {
        Integer version = null;
        // Check if the part after the last slash contains a colon
        int i = uri.lastIndexOf('/');
        i = uri.indexOf(':', i + 1);
        if (i < 0) {
            version = null;
        } else {
            try {
                version = Integer.parseInt(uri.substring(i + 1));
            } catch (Exception ignore) {
                return null;
            }
        }
        return version;
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

        Item it = cache.getByUrl(language, uri);

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

    private Optional<Item> getItemHistoryByUri(String uri, Integer version, String language, ItemHistorySupplier itemHistorySupplier) throws Exception {
        Item cached = cache.getByUrl(language, uri);
        if (cached != null) {
            return Optional.of(cached);
        }

        Item item = itemHistorySupplier.getItemHistoryByUri(uri, version);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
    }

    private Optional<Item> getItemStatusByUuid(String uuid, String language, StatusSupplier statusSupplier) throws Exception {
        Item cached = cache.getByUuid(language, uuid);
        if (cached != null) {
            return Optional.of(cached);
        }
        Item item = statusSupplier.getItemByUuid(uuid);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
    }

    private Optional<Item> getItemStatusByUri(String uri, String language, StatusSupplier statusSupplier) throws Exception {
        Item cached = cache.getByUrl(language, uri);
        if (cached != null) {
            return Optional.of(cached);
        }

        Item item = statusSupplier.getItemByUri(uri);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item);
        return Optional.of(item);
    }
}
