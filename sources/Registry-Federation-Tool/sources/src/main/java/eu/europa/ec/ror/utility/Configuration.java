/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.utility;

//import eu.europa.ec.ror.utility.log.LogClass;
//import eu.europa.ec.ror.utility.log.LogManager;
import java.io.*;
import java.util.Properties;
import org.apache.logging.log4j.Logger;

public class Configuration {

    private static Configuration instance;
    private Properties properties;
    private static Logger logger;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Configuration() {
        try {
            //Initializing variables
            properties = new Properties();

            //Initializing loggers
            this.initLogging();

            //Reading base app configurations
            String propertiesPath = System.getProperty(Constants.CONFIGURATIONS_FOLDER_NAME);

            InputStream input = new FileInputStream(propertiesPath + File.separator + Constants.CONFIGURATIONS_FILE_NAME);

            properties.load(input);

        } catch (Exception e) {
            System.out.println("@@ Error reading configurations: " + e.getMessage());
        }
    }

    public Properties getProperties() {
        return properties;
    }

//    public LogClass getLogClass(String className) {
//        return LogManager.getLogClass(className);
//    }

    private void initLogging() {
//        System.out.println("### Setting up the logger");
//        String filePropertiesPath = System.getProperty(Constants.CONFIGURATIONS_FOLDER_NAME);
//        File logCfgFile = new File(filePropertiesPath + File.separator + Constants.LOGCONFIG_FILE_NAME);
//        if (logCfgFile.exists() && logCfgFile.isFile()) {
//            LogManager.config(logCfgFile.getAbsolutePath());
//            System.out.println("### Logger configuration completed");
//        } else {
//            System.err.println("@@ The Logger configuration file \"" + Constants.LOGCONFIG_FILE_NAME + "\" was not found: " + logCfgFile.getAbsolutePath());
//        }

        System.out.println("### Setting up the logger");
        logger = org.apache.logging.log4j.LogManager.getLogger(Constants.MODULE_NAME);

        if (logger == null) {
            System.err.println("@@ The logging system was unable to startup correctly");
        }
    }

    public static Logger getLogger() {
        return logger;
    }

}
