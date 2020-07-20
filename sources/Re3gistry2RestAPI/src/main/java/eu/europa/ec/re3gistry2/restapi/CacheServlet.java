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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europa.ec.re3gistry2.restapi.cache.CaffeineCache;
import eu.europa.ec.re3gistry2.restapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.restapi.util.RequestUtil;
import eu.europa.ec.re3gistry2.restapi.util.ResponseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheServlet extends HttpServlet {

    static final String ATTRIBUTE_CACHE_KEY = "re3gistry-rest-api-cache";
    private static final Logger LOG = LogManager.getLogger(ItemsServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private ItemCache cache;

    public void init(ServletConfig config) throws ServletException {
        this.cache = new CaffeineCache();
        config.getServletContext().setAttribute(ATTRIBUTE_CACHE_KEY, cache);
    }

    @Override
    // doDelete() instead?
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (!"/flush".equals(path)) {
            try {
                ResponseUtil.err(resp, ApiError.NOT_FOUND);
            } catch (Exception e) {
                LOG.error("Unexpected exception occured", e);
            }
            return;
        }
        String uuid = RequestUtil.getParamTrimmed(req, "uuid", null);
        boolean flushAll = uuid == null || uuid.isEmpty();
        if (flushAll) {
            cache.removeAll();
        } else {
            for (String language : cache.getLanguages()) {
                cache.remove(language, uuid);
            }
        }
        try {
            ResponseUtil.ok(resp, new ApiResponse(200, "ok", "Flushed"));
        } catch (Exception e) {
            try {
                ResponseUtil.err(resp, ApiError.INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                LOG.error("Unexpected exception occured", e);
            }
        }
    }

}
