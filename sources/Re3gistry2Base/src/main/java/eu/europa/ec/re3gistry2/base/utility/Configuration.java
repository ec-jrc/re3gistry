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
package eu.europa.ec.re3gistry2.base.utility;

import eu.europa.ec.re3gistry2.base.utility.localization.Localization;
import eu.europa.ec.re3gistry2.base.utility.localization.LocalizationMgr;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Configuration {

    private static Configuration instance;
    private static Properties properties;
    private static Logger logger;
    private ResourceBundle localization;

    public static Configuration getInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                instance = new Configuration();
            }
        }
        return instance;
    }

    public static Configuration getLightInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                // Setting a light instance (true parameter)
                instance = new Configuration(true);
            }
        }
        return instance;
    }

    private Configuration() {

        //Initializing properties
        initProperties();

        //Initializing loggers
        initLogging();

        //Initializing localization
        this.initLocalization();

    }

    // This can be used to just initialize properties (lightConfig mode)
    private Configuration(boolean lightConfig) {

        //Initializing properties
        initProperties();

        if (!lightConfig) {
            //Initializing loggers
            initLogging();

            //Initializing localization
            this.initLocalization();
        }
    }

    private static void initProperties() {
        try {
            properties = new Properties();

            String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);

            try (InputStream input = new FileInputStream(propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_CONFIGURATIONS)) {
                 properties.load(input);
            }

        } catch (Exception e) {
            System.out.println("@@ Error reading configurations: " + e.getMessage());
        }

    }

    public static boolean checkInstallation() {
        boolean installed = false;
        try {
            String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);

            File f = new File(propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLED);
            if (f.exists() && !f.isDirectory()) {
                installed = true;
            }

        } catch (Exception e) {
            logger.error(e);
        }
        return installed;
    }

    public static boolean checkInstallRunning() {
        boolean installing = false;
        try {
            String propertiesPath = System.getProperty(BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS);

            File f = new File(propertiesPath + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING);
            if (f.exists() && !f.isDirectory()) {
                installing = true;
            }

        } catch (Exception e) {
            logger.error(e);
        }
        return installing;
    }

    private static void initLogging() {
        System.out.println("### Setting up the logger");
        logger = LogManager.getLogger(BaseConstants.KEY_APP_NAME);

        if (logger == null) {
            System.err.println("@@ The logging system was unable to startup correctly");
        }
    }

    private void initLocalization() {
        Locale defaultLocale;

        defaultLocale = LocalizationMgr.getDefaultLocale(properties);

        localization = ResourceBundle.getBundle(BaseConstants.KEY_PROPERTY_LOCALIZATION_PREFIX + "." + BaseConstants.KEY_PROPERTY_LOCALIZATION_SUFFIX, defaultLocale);
    }

    public void initServlet(HttpServletRequest request, HttpServletResponse response, boolean fromInstall, boolean fromInstalling) throws ServletException, IOException {
        //Check installation

        // If the system is installed and the user tries to access the 
        // installation, he is redirected to the login
        if (checkInstallation() && (fromInstall || fromInstalling)) {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_LOGIN);
        } else // If the system is installed and the user tries to access the 
        // installation, he is redirected to the login
        if (checkInstallRunning() && !fromInstalling) {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_INSTALL_RUNNING);
        } else // If the system is not installed, and it is not installing every request is redirected to the installation
        if (!checkInstallation() && !checkInstallRunning() && !fromInstall) {
            response.sendRedirect("." + WebConstants.PAGE_URINAME_INSTALL);
        } else {
            //Init properties
            Properties prop = Configuration.getInstance().getProperties();
            request.setAttribute(BaseConstants.KEY_REQUEST_PROPERTIES, prop);

            //Init Localization
            ResourceBundle loc = Configuration.getInstance().getLocalization();
            request.setAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION, loc);

            //Init available languages in the session if needed
            if (request.getSession().getAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES) == null) {
                List<Localization> availableLanguages = new ArrayList<>(LocalizationMgr.getAvailableLanguages());
                request.getSession().setAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES, availableLanguages);
                request.setAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES, availableLanguages);
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_AVAILABLELANGUAGES, request.getSession().getAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES));
            }

            String uri = request.getRequestURI();
            String currentPageName = "/" + uri.substring(uri.lastIndexOf("/") + 1);
            request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTPAGENAME, currentPageName);

            HttpSession session = request.getSession();

            //Getting logged user
            RegUser regUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);

            if (regUser == null) {
                regUser = UserHelper.checkUser(request);
            }

            if (regUser != null) {
                request.setAttribute(BaseConstants.KEY_REQUEST_REGUSER, regUser);
            } else {
                if (!fromInstall && !fromInstalling) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_REGUSER, null);
                    //request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                }
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public Logger getLogger() {
        return logger;
    }

    public ResourceBundle getLocalization() {
        return localization;
    }

    public void setLocalization(String localeCode) {
        Locale newLocale = LocalizationMgr.getLocale(localeCode);
        localization = ResourceBundle.getBundle(BaseConstants.KEY_PROPERTY_LOCALIZATION_PREFIX + "." + BaseConstants.KEY_PROPERTY_LOCALIZATION_SUFFIX, newLocale);
    }

    public void setLocalization(ResourceBundle localization) {
        this.localization = localization;
    }
}
