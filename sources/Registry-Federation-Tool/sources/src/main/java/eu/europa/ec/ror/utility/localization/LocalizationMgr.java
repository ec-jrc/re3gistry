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
package eu.europa.ec.ror.utility.localization;

import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class LocalizationMgr {

    private static final Logger log = Configuration.getInstance().getLogger();
    private static ArrayList<Localization> availableLanguages;

    /**
     * This method get the current locale loaded in the session variable.
     *
     * @param request the HttpServletRequest
     * @return the Locale instance
     */
    public static Locale getCurrentLocale(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String currentLanguage = (String) session.getAttribute(Constants.LANGUAGE_SESSION_KEY);
            if (currentLanguage == null) {
                session.setAttribute(Constants.LANGUAGE_SESSION_KEY, ((request.getLocale().toString()!=null)? request.getLocale().toString() : Locale.getDefault().toString()));
            }
            return new Locale(session.getAttribute(Constants.LANGUAGE_SESSION_KEY).toString());
        } catch (Exception e) {
            log.error("Error while getting the locale's information from session - Message: " + e.getMessage(), e);
            return Locale.getDefault();
        }
    }

    /**
     * This method changes the locale set in the session variable.
     *
     * @param request the HttpServletRequest
     */
    public static void changeLanguage(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String languageId = request.getParameter(Constants.LANGUAGE_REQUEST_KEY);
            if (languageId != null && !languageId.trim().equals("")) {
                session.setAttribute(Constants.LANGUAGE_SESSION_KEY, languageId);
            }
        } catch (Exception e) {
            log.error("Error while changing the locale - Message: " + e.getMessage(), e);
        }
    }

    /**
     * This method calls the loader of the available language, if an instance of
     * this objet is not jet initialized;
     *
     * @return the available language in a key-value array: language code,
     * language label
     */
    public static ArrayList getAvailableLanguages() throws Exception {
        if (availableLanguages == null) {
            availableLanguages = initLanguages();
        }
        return availableLanguages;
    }

    /**
     * This method load the list of the available languages and returns an array
     * instance containing all the available language configured.
     *
     * @return the available language in a key-value array: language code,
     * language label
     */
    private static ArrayList<Localization> initLanguages() throws Exception {
        Properties p = Configuration.getInstance().getProperties();
        String languagesString = p.getProperty(Constants.AVAILABLE_LANGUAGE_PROPERTY_KEY);
        if (languagesString != null && !languagesString.trim().equals("")) {
            String[] languages = languagesString.split(Constants.AVAILABLE_LANGUAGE_SEPARATOR);
            availableLanguages = new ArrayList<>();
            if (languages.length > 0) {
                for (String languageKey : languages) {
                    String languageLabel = p.getProperty(Constants.AVAILABLE_LANGUAGE_LABEL_PROPERTY_KEY + languageKey);
                    Localization localization = new Localization(languageKey, languageLabel);
                    availableLanguages.add(localization);
                }
                return availableLanguages;
            } else {
                throw new Exception("Error while setting up the available languages.");
            }
        } else {
            throw new Exception("Warning, there are no \"available language\"; check the properties file.");
        }
    }
}
