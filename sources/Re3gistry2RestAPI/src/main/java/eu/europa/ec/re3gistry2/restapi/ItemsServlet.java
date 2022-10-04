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

import eu.europa.ec.re3gistry2.restapi.util.ApiError;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.XSDSchemaSupplier;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.StatusSupplier;
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
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemHistorySupplier;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemSupplier;
import eu.europa.ec.re3gistry2.restapi.format.CSVFormatter;
import eu.europa.ec.re3gistry2.restapi.format.Formatter;
import eu.europa.ec.re3gistry2.restapi.format.ISO19135Formatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONFormatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONInternalFormatter;
import eu.europa.ec.re3gistry2.restapi.format.RDFFormatter;
import eu.europa.ec.re3gistry2.restapi.format.RORFormatter;
import eu.europa.ec.re3gistry2.restapi.format.XMLFormatter;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.javaapi.cache.supplier.ItemproposedSupplier;
import eu.europa.ec.re3gistry2.javaapi.cache.util.NoVersionException;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.restapi.format.ATOMFormatter;
import eu.europa.ec.re3gistry2.restapi.format.XSDFormatter;
import eu.europa.ec.re3gistry2.restapi.util.RequestUtil;
import eu.europa.ec.re3gistry2.restapi.util.ResponseUtil;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
            this.cache = (ItemCache) config.getServletContext().getAttribute(BaseConstants.ATTRIBUTE_CACHE_KEY);
            this.formatters = new HashMap<>();

            addFormatter(new JSONInternalFormatter());
            addFormatter(new XMLFormatter());
            addFormatter(new JSONFormatter());
            addFormatter(new ISO19135Formatter());
            addFormatter(new RDFFormatter());
            addFormatter(new CSVFormatter(emf));
            addFormatter(new RORFormatter());
            addFormatter(new XSDFormatter());
            addFormatter(new ATOMFormatter());
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
            String status = RequestUtil.getParamTrimmed(req, "status", null);
            String itemclassparam = RequestUtil.getParamTrimmed(req, "itemclass", null);
            uri = removeTrailingSlashes(uri);

            Predicate<Item> typeFilter = getTypeFilter(path);
            Formatter formatter = formatters.get(format);

            if (typeFilter == null) {
                ResponseUtil.err(resp, ApiError.NOT_FOUND);
                return;
            }

            EntityManager em = null;
            try {
                em = emf.createEntityManager();

                if (lang != null && lang.equalsIgnoreCase("active")) {
                    String type = "application/json";
                    RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(em);
                    List<RegLanguagecode> activeLanguages = null;
                    try {
                        activeLanguages = regLanguagecodeManager.getAllActive();
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(ItemsServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //TOITEM
                    List<Map<String, String>> toActiveLanguages = new ArrayList();
                    for (int i = 0; i < activeLanguages.size(); i++) {
                        HashMap<String, String> language = new HashMap<>();
                        language.put("uuid", activeLanguages.get(i).getUuid());
                        language.put("label", activeLanguages.get(i).getLabel());
                        language.put("iso6391code", activeLanguages.get(i).getIso6391code());
                        language.put("iso6392code", activeLanguages.get(i).getIso6392code());
                        language.put("masterlanguage", activeLanguages.get(i).getMasterlanguage().toString());
                        language.put("active", activeLanguages.get(i).getActive().toString());
                        toActiveLanguages.add(language);
                    }

                    byte[] body = JSONInternalFormatter.OM.writeValueAsBytes(toActiveLanguages);
                    resp.setContentType(type);
                    resp.setContentLength(body.length);
                    try (OutputStream out = resp.getOutputStream()) {
                        out.write(body);
                    }
                    return;
                } else if (itemclassparam != null) {
                    RegItemclassManager regItemclassManager = new RegItemclassManager(em);
                    try {
                        RegItemclass regItemclass = regItemclassManager.getByLocalid(itemclassparam.replace("_register", ""));
                        XSDSchemaSupplier xsdSchemaSupplier = new XSDSchemaSupplier(em, regItemclass);

                        ItemClass itemClass = xsdSchemaSupplier.getItemClass(regItemclass);

                        if (itemClass == null) {
                            ResponseUtil.err(resp, ApiError.NOT_FOUND);
                        } else {
                            ResponseUtil.ok(resp, itemClass, formatter);
                        }
                    } catch (Exception ex) {

                        List<RegItemclass> itemclassList = regItemclassManager.getAll(false);
                        String list = null;
                        for (RegItemclass regItemclass : itemclassList) {
                            list = list + ", " + regItemclass.getLocalid();
                        }
                        ApiError.ITEMCLASS_REQUIRED.getError().getDescription().concat(list);
                        ResponseUtil.err(resp, ApiError.ITEMCLASS_REQUIRED);
                    }
                } else {
                    if (uuid == null && uri == null) {
                        ResponseUtil.err(resp, ApiError.UUID_URI_REQUIRED);
                        return;
                    }

                    if (formatter == null) {
                        ResponseUtil.err(resp, ApiError.FORMAT_NOT_SUPPORTED);
                        return;
                    }

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
                    ItemproposedSupplier itemproposedSupplier = new ItemproposedSupplier(em, masterLanguage, languageCode);

                    Optional<Item> optItem = Optional.empty();
                    
                    Integer version;
                    try{
                         version = getVersionFromUri(uri);
                    }catch(Exception uriError){
                        version = 0;
                    }
                    
                    if(uuid != null && status != null ){                       
                       throw new Exception();
                    }else if (uuid != null){
                        optItem = searchForItemByUuid(uuid, lang, itemSupplier, itemproposedSupplier, itemHistorySupplier);   
                    }else if (uri != null && status != null){
                        optItem = searchForItemByURIStatus(uri, lang, version, status, itemSupplier, itemproposedSupplier, itemHistorySupplier);
                    } else if (uri != null){
                        optItem = searchForItemByURI(uri, version, lang, itemSupplier, itemproposedSupplier, itemHistorySupplier);
                    }
 
                    //try to see if is a status request
                    if (!optItem.isPresent() && status == null) {
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
            version = 0;
        } else {
            try {
                version = Integer.parseInt(uri.substring(i + 1));
                return version;
            } catch (NumberFormatException ignore) {
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

        cache.add(language, item, null);
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

        cache.add(language, item, null);
        return Optional.of(item);
    }

    private Optional<Item> getItemProposedByUuid(String uuid, String language, ItemproposedSupplier itemproposedSupplier) throws Exception {
        Item item = itemproposedSupplier.getItemProposedByUuid(uuid);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    private Optional<Item> getItemProposedByUri(String uri, ItemproposedSupplier itemproposedSupplier) throws Exception {
        Item item = itemproposedSupplier.getItemProposedByUri(uri);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    private Optional<Item> getItemProposedByUriAndStatus(String uri, String itemStatus, ItemproposedSupplier itemproposedSupplier) throws Exception {
        Item item = itemproposedSupplier.getItemProposedByUriAndStatus(uri, itemStatus);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }
    
    private Optional<Item> getItemByUriAndStatus(String uri, String language, ItemSupplier itemSupplier, String itemStatus) throws Exception {
        Item cached = cache.getByUrl(language, uri, null, itemStatus);
        if (cached != null) {
            return Optional.of(cached);
        }

        Item item = itemSupplier.getItemByUriAndStatus(uri, itemStatus);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item, null);
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

        cache.add(language, item, item.getVersion().getNumber());
        return Optional.of(item);
    }

    private Optional<Item> getItemHistoryByUri(String uri, String language, ItemHistorySupplier itemHistorySupplier, Integer version) throws Exception {
        Item cached = cache.getByUrl(language, uri);
        if (cached != null) {
            return Optional.of(cached);
        }

        Item item = itemHistorySupplier.getItemHistoryByUri(uri, version);
        if (item == null) {
            return Optional.empty();
        }

        cache.add(language, item, version);
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

        cache.add(language, item, null);
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

        cache.add(language, item, null);
        return Optional.of(item);
    }

    private Optional<Item> searchForItemByUuid(String uuid, String lang, ItemSupplier itemSupplier, ItemproposedSupplier itemproposedSupplier, ItemHistorySupplier itemHistorySupplier) {
        Optional <Item> optItem;
        try {
                            optItem = getItemByUuid(uuid, lang, itemSupplier);
                        } catch (Exception ex) {
                            try {
                                optItem = getItemProposedByUuid(uuid, lang, itemproposedSupplier);
                            } catch (Exception e) {
                                try {
                                    optItem = getItemHistoryByUuid(uuid, lang, itemHistorySupplier);
                                } catch (Exception ex1) {
                                    optItem = Optional.empty();
                                }
                            }
                        }
        return optItem;
    }

    private Optional<Item> searchForItemByURI(String uri, Integer version, String lang, ItemSupplier itemSupplier, ItemproposedSupplier itemproposedSupplier, ItemHistorySupplier itemHistorySupplier) {
        
        Optional<Item> optItem;
        optItem = Optional.empty();

        if (version == 0) {

            try {
                optItem = getItemByUri(uri.replace(":" + version, ""), lang, itemSupplier);
            } catch (Exception ex) {
                optItem = Optional.empty();
            }

            if (!optItem.isPresent()) {
                try {
                    optItem = getItemProposedByUri(uri.replace(":" + version, ""), itemproposedSupplier);
                } catch (Exception ex) {
                    optItem = Optional.empty();
                }
            }

        } else {
            int sizeHistory;
            sizeHistory = 0;
            try {
                sizeHistory = itemHistorySupplier.sizeItemInHistory(uri);
            } catch (Exception ex) {
                sizeHistory = 0;
            }
            if (sizeHistory != 0 || sizeHistory + 1 != version) {
                try {
                    optItem = getItemHistoryByUri(uri, lang, itemHistorySupplier, version);
                } catch (Exception ex) {
                    optItem = Optional.empty();
                }
            }
        }

        return optItem;     
    }

    private Optional<Item> searchForItemByURIStatus(String uri, String lang, Integer version, String status, ItemSupplier itemSupplier, ItemproposedSupplier itemproposedSupplier, ItemHistorySupplier itemHistorySupplier) {
        
        Optional<Item> optItem;
        
        if (version == 0) {

            if (status.equalsIgnoreCase("valid")
                        || status.equalsIgnoreCase("invalid")
                        || status.equalsIgnoreCase("superseded")
                        || status.equalsIgnoreCase("retired")) {
                try {
                    optItem = getItemByUriAndStatus(uri.replace(":" + version, ""), lang, itemSupplier, status);
                } catch (Exception ex) {
                    optItem = Optional.empty();
                }
            }else{
                try {
                    optItem = getItemProposedByUriAndStatus(uri.replace(":" + version, ""), status, itemproposedSupplier);
                } catch (Exception ex) {
                    optItem = Optional.empty();
                }
            }
            
        } else {
            int sizeHistory;
            try {
                sizeHistory = itemHistorySupplier.sizeItemInHistory(uri);
            } catch (Exception ex) {
                sizeHistory = 0;
            }
            
            if (sizeHistory == 0 || sizeHistory + 1 == version) {
                
                if (status.equalsIgnoreCase("valid")
                        || status.equalsIgnoreCase("invalid")
                        || status.equalsIgnoreCase("superseded")
                        || status.equalsIgnoreCase("retired")) {
                    try {
                        optItem = getItemByUriAndStatus(uri.replace(":" + version, ""), lang, itemSupplier, status);
                    } catch (Exception ex) {
                        optItem = Optional.empty();
                    }
                }else{
                   try {
                    optItem = getItemProposedByUriAndStatus(uri.replace(":" + version, ""), status, itemproposedSupplier);
                } catch (Exception e) {
                    optItem = Optional.empty();
                } 
                } 
            } else {
                try {
                    optItem = getItemHistoryByUri(uri, lang, itemHistorySupplier, version);
                } catch (Exception ex) {
                    optItem = Optional.empty();
                }
            }
        }
        
        return optItem;
    }
}
