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
package eu.europa.ec.re3gistry2.web.controller;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;

@WebServlet(WebConstants.PAGE_URINAME_LOGOUT)
public class Logout extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Init frontend servlet
        Configuration.getInstance().initServlet(request, response, false, false);

        //Getting the login type
        String loginType = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE);
        
        //Getting the redirect url
        String url = request.getHeader("referer");
        String contextPath = request.getContextPath();
        String homeUrl = "";
        if (contextPath != null) {
            int indexOfContextPath = url.lastIndexOf(contextPath);
            int contextPathLength = contextPath.length();
            if (indexOfContextPath > 0 && contextPathLength > 0) {
                int homeUrlLength = indexOfContextPath + contextPathLength;
                homeUrl = url.substring(0, homeUrlLength) + "/";
            }
        }
        String logoutLink = this.getProperties(BaseConstants.KEY_FILE_ECAS_PROPERTIES).getProperty("eu.cec.digit.ecas.client.filter.ecasBaseUrl") + "/cas/logout?url=" + homeUrl;

        //Perform logout
        if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS)) {
            if (request.getSession() != null) {
                request.getSession().invalidate();
            }
            response.sendRedirect(logoutLink);
        } else if (loginType.equals(BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO)) {
            SecurityUtils.getSubject().logout();
            if (request.getSession() != null) {
                request.getSession().invalidate();
            }
            //Dispatch request
            request.getRequestDispatcher(WebConstants.PAGE_JSP_FOLDER + WebConstants.PAGE_PATH_LOGOUT + WebConstants.PAGE_URINAME_LOGOUT + WebConstants.PAGE_JSP_EXTENSION).forward(request, response);
        } else {
            throw new Exception("Configuration error: login type not properly configured.");
        }

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger logger = Configuration.getInstance().getLogger();
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Get a resource bundle
     *
     * @param path Name of the file
     * @return
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Properties getProperties(String path) throws Exception {
        Properties props = new Properties(System.getProperties());
        File propertiesFile = new File(Logout.class.getClassLoader().getResource(path).toURI());
        if (!propertiesFile.exists()) {
            throw new FileNotFoundException("Properties file not found: " + propertiesFile.getAbsolutePath());
        }
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(propertiesFile);
            props.load(inStream);
            inStream.close();
        } catch (Exception e) {
            if (inStream != null) {
                inStream.close();
            }
            throw e;
        }
        return props;
    }

}
