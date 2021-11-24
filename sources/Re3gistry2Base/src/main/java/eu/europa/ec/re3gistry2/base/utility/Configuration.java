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
    private static String pathHelperFiles;
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
            String fileName = BaseConstants.KEY_FOLDER_NAME_CONFIGURATIONS + File.separator + BaseConstants.KEY_FILE_NAME_CONFIGURATIONS;
            // logger is not initialised at this point
            System.out.println("File name properties path: " + fileName);
            try (InputStream propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
                 properties.load(propertiesStream);
                 System.out.println(properties.keySet().size() + " properties loaded from " + fileName);
            }

        } catch (Exception e) {
            System.out.println("@@ Error reading configurations: " + e.getMessage());
        }

    }

    public static boolean checkInstallation() {
      boolean installed = false;
      try {
          File f = new File(Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLED);
          logger.trace("Checking for existence of file " + f.getAbsolutePath());
          if (f.exists() && !f.isDirectory()) {
              installed = true;
              logger.trace("The system has been installed");
          } else {
            logger.trace("The system has not yet been installed");
          }
      } catch (Exception e) {
          logger.error(e.getMessage(), e);
      }
      return installed;
    }

    public static boolean checkInstallRunning() {
        boolean installing = false;
        try {
            File f = new File(Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_SYSTEMINSTALLING);
            if (f.exists() && !f.isDirectory()) {
              installing = true;
              logger.trace("Installation of the system is in progress");
          } else {
            logger.trace("Installation of the system is not in progress");
          }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        HttpSession session = request.getSession();
        Configuration.getInstance().getLogger().debug("Start initialising servlet; session id: " + session.getId());
        
        //Check installation

        // If the system is installed and the user tries to access the 
        // installation, he is redirected to the login
        if (checkInstallation() && (fromInstall || fromInstalling)) {
          Configuration.getInstance().getLogger().trace("Redirecting to " + "." + WebConstants.PAGE_URINAME_LOGIN);
            response.sendRedirect("." + WebConstants.PAGE_URINAME_LOGIN);
        } else // If the system is installed and the user tries to access the 
        // installation, he is redirected to the login
        if (checkInstallRunning() && !fromInstalling) {
          Configuration.getInstance().getLogger().trace("Redirecting to " + "." + WebConstants.PAGE_URINAME_INSTALL_RUNNING);
            response.sendRedirect("." + WebConstants.PAGE_URINAME_INSTALL_RUNNING);
        } else // If the system is not installed, and it is not installing every request is redirected to the installation
        if (!checkInstallation() && !checkInstallRunning() && !fromInstall) {
          Configuration.getInstance().getLogger().trace("Redirecting to " + "." + WebConstants.PAGE_URINAME_INSTALL);
            response.sendRedirect("." + WebConstants.PAGE_URINAME_INSTALL);
        } else {
            Configuration.getInstance().getLogger().trace("Proceed initialising servlet");
          
            //Init properties
            Properties prop = Configuration.getInstance().getProperties();
            request.setAttribute(BaseConstants.KEY_REQUEST_PROPERTIES, prop);

            //Init Localization
            ResourceBundle loc = Configuration.getInstance().getLocalization();
            request.setAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION, loc);

            //Init available languages in the session if needed
            if (session.getAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES) == null) {
                List<Localization> availableLanguages = new ArrayList<>(LocalizationMgr.getAvailableLanguages());
                session.setAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES, availableLanguages);
                request.setAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES, availableLanguages);
            } else {
                request.setAttribute(BaseConstants.KEY_REQUEST_AVAILABLELANGUAGES, session.getAttribute(BaseConstants.KEY_SESSION_AVAILABLELANGUAGES));
            }

            String uri = request.getRequestURI();
            String currentPageName = "/" + uri.substring(uri.lastIndexOf("/") + 1);
            request.setAttribute(BaseConstants.KEY_REQUEST_CURRENTPAGENAME, currentPageName);
            Configuration.getInstance().getLogger().debug("Current page name=" + currentPageName);


            //Getting logged user
            RegUser regUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);

            if (regUser == null) {
                Configuration.getInstance().getLogger().trace("No " + BaseConstants.KEY_SESSION_USER + " found in the session, checking user");
                regUser = UserHelper.checkUser(request);
            }

            if (regUser != null) {
                Configuration.getInstance().getLogger().trace("User found using checkUser: " + regUser);
                request.setAttribute(BaseConstants.KEY_REQUEST_REGUSER, regUser);
            } else {
                Configuration.getInstance().getLogger().trace("No user found");
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
    
    public static String getPathHelperFiles() {
      return pathHelperFiles;
    }

    public static void setPathHelperFiles(String thePathHelperFiles) {
      pathHelperFiles = thePathHelperFiles;
    }
    
}
