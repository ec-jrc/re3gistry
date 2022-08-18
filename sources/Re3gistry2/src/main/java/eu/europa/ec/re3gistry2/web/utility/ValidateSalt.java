/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.web.utility;

import com.google.common.cache.Cache;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

public class ValidateSalt implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // NOOP.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        Configuration.getInstance().getLogger().trace("Start filter " + ValidateSalt.class.getName());

        // Assume its HTTP
        HttpServletRequest httpReq = (HttpServletRequest) request;

        // Get the salt sent with the request
        String salt = (String) httpReq.getAttribute(BaseConstants.KEY_REQUEST_CSRF_PREVENTIONSALT);

        Configuration.getInstance().getLogger().debug("Validating that the salt is in the cache");
        Cache<String, Boolean> csrfPreventionSaltCache = (Cache<String, Boolean>)
        httpReq.getSession().getAttribute(BaseConstants.KEY_REQUEST_CSRF_PREVENTIONSALTCACHE);

        if (csrfPreventionSaltCache != null &&
                salt != null &&
                csrfPreventionSaltCache.getIfPresent(salt) != null){

            Configuration.getInstance().getLogger().debug("Salt is in cache, thus moving on");
            chain.doFilter(request, response);
        } else {
            // Otherwise we throw an exception aborting the request flow
            if(httpReq.getMethod().equals("POST")){
                Configuration.getInstance().getLogger().debug("Salt is not in cache, and HTTP method is POST, thus potential CSRF");
                request.setAttribute(BaseConstants.KEY_REQUEST_CSRF_PREVENTION_ERROR, BaseConstants.KEY_BOOLEAN_STRING_TRUE);
                request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_INDEX + WebConstants.PAGE_URINAME_INDEX + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
                Logger logger = Configuration.getInstance().getLogger();
                logger.error("Potential CSRF detected");
                //throw new ServletException("Potential CSRF detected");
            }else{
                Configuration.getInstance().getLogger().debug("Salt is not in cache, but HTTP method is not POST, thus moving on");
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        // NOOP.
    }
}
