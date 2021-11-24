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
package eu.europa.ec.re3gistry2.base.utility.localization;

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class LocalizationMgr {

    private static ArrayList<Localization> availableLanguages;
    private static Locale defaultLocale;

    private LocalizationMgr() {
    }

    public static Locale getDefaultLocale() {
        return getDefaultLocale(Configuration.getInstance().getProperties());
    }

    public static Locale getDefaultLocale(Properties prop) {
        if (defaultLocale == null) {
            String defaultLocaleProperty = prop.getProperty(BaseConstants.KEY_PROPERTY_DEFAULT_LOCALE);
            defaultLocale = getLocale(defaultLocaleProperty);
        }
        return defaultLocale;
    }

    /**
     * This method get the current locale loaded in the session variable.
     *
     * @param request the HttpServletRequest
     * @return the Locale instance
     */
    public static Locale getCurrentLocale(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String currentLanguage = (String) session.getAttribute(BaseConstants.KEY_SESSION_LANGUAGE);
            if (currentLanguage == null) {
                session.setAttribute(BaseConstants.KEY_SESSION_LANGUAGE, ((request.getLocale().toString() != null) ? request.getLocale().toString() : Locale.getDefault().toString()));
            }
            return new Locale(session.getAttribute(BaseConstants.KEY_SESSION_LANGUAGE).toString());
        } catch (Exception e) {
            Configuration.getInstance().getLogger().error("Error while getting the locale's information from session - Message: " + e.getMessage(), e);
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
            String languageId = request.getParameter(BaseConstants.KEY_REQUEST_LANGUAGE);
            if (languageId != null && !languageId.trim().equals("")) {
                session.setAttribute(BaseConstants.KEY_SESSION_LANGUAGE, languageId);
                Configuration.getInstance().setLocalization(languageId);
            }
        } catch (Exception e) {
            Configuration.getInstance().getLogger().error("Error while changing the locale - Message: " + e.getMessage(), e);
        }
    }

    /**
     * This method calls the loader of the available language, if an instance of
     * this object is not jet initialized;
     *
     * @return the available language in a key-value array: language code,
     * language label
     */
    public static List<Localization> getAvailableLanguages() {
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
    private static ArrayList<Localization> initLanguages() {
        String languagesString = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_AVAILABLE_LANGUAGE);
        if (languagesString != null && !languagesString.trim().equals("")) {
            String[] languages = languagesString.split(BaseConstants.KEY_PROPERTY_SEPARATOR_AVAILABLE_LANGUAGE);
            availableLanguages = new ArrayList<>();
            if (languages.length > 0) {
                for (String languageKey : languages) {
                    String languageLabel = Configuration.getInstance().getProperties().getProperty(BaseConstants.KEY_PROPERTY_AVAILABLE_LANGUAGE_LABEL + languageKey);
                    Localization localization = new Localization(languageKey, languageLabel);
                    availableLanguages.add(localization);
                }
                return availableLanguages;
            } else {
                Configuration.getInstance().getLogger().error("Error while setting up the available languages.");
                return null;
            }
        } else {
            Configuration.getInstance().getLogger().error("Warning, there are no \"available language\"; check the properties file.");
            return null;
        }
    }

    public static Locale getLocale(String localeId) {
        Locale outLocale = null;

        switch (localeId) {
            case "en":
                outLocale = Locale.ENGLISH;
                break;
            case "de":
                outLocale = Locale.GERMAN;
                break;
            case "fr":
                outLocale = Locale.FRENCH;
                break;
            case "it":
                outLocale = Locale.ITALIAN;
                break;
            case "es":
                outLocale = new Locale("es");
                break;
            case "pt":
                outLocale = new Locale("pt");
                break;
            case "bg":
                outLocale = new Locale("bg");
                break;
            case "cs":
                outLocale = new Locale("cs");
                break;
            case "da":
                outLocale = new Locale("da");
                break;
            case "hr":
                outLocale = new Locale("hr");
                break;
            case "lv":
                outLocale = new Locale("lv");
                break;
            case "lt":
                outLocale = new Locale("lt");
                break;
            case "hu":
                outLocale = new Locale("hu");
                break;
            case "mt":
                outLocale = new Locale("mt");
                break;
            case "nl":
                outLocale = new Locale("nl");
                break;
            case "pl":
                outLocale = new Locale("pl");
                break;
            case "ro":
                outLocale = new Locale("ro");
                break;
            case "sk":
                outLocale = new Locale("sk");
                break;
            case "sl":
                outLocale = new Locale("sl");
                break;
            case "fi":
                outLocale = new Locale("fi");
                break;
            case "sv":
                outLocale = new Locale("sv");
                break;
            default:
                outLocale = Locale.ENGLISH;
                break;
        }
        
        return outLocale;
    }
}
