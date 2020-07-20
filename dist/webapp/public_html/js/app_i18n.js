"use strict";
/// *** Scripts to manage the localization ***///

// ** Constants and variables definition ** //

// Element name constants
//const elementId_languageSelector = 'ecl-language-list__button';

const elementClassName_languageListButtonActive = 'ecl-language-list__item--is-active';
const elementClassName_languagePrefixPattern = 'language-';
const elementClassName_selectedLangLabel = 'ecl-site-header__selector-link';
const elementClassName_selectedLangIcon = 'ecl-site-header__language-icon';
const elementClassName_selectedLangCodeText = 'ecl-site-header__language-code';
const elementClassName_dialogDismiss = 'ecl-language-list__close-button';
const elementClassName_eclsiteHeaderLogoImage = 'ecl-site-header__logo-image';
// Key contants
const key_i18n = 'i18n';
const key_i18nLink = 'i18n-link';
const key_dataLocalizationFilesPath = '/js/i18n';
const key_languageLogoPattern = 'logo--{0}.ec.svg';
// Value constants

// HTML snippet constants
const htmlSnippet_iconSelected = '<a href="' + registryApp.hostURL + registryApp.staticResourcesPath + '"> <i class="fas fa-check"></i></a>';
// Event name constants

// Regular expression constants
const regularExpression_languagePrefix = /language-[aA-zZ]+/;
// Global variables
var languageFromUrl = val_emptyString;
var currentLanguage = val_emptyString;
var i18n;
// ** Script body ** //

/*
 * This method ititialize the language of the webapp
 * The global variable currentLanguage is initialized with the language passed
 * the 'selector' parameter or with the default webapp language taken from the
 * configuration file.
 * The UI is then updated with the selected language.
 *
 * @param {type} selector
 */
function initLocalization(selector) {

    let storedLanguage = val_emptyString;
    // checking if there is the language passed by URL
    if (languageFromUrl !== null && languageFromUrl.length === 2) {

        currentLanguage = languageFromUrl;
    } else {

        // Checking if there is a language stored in the cookies
        if (navigator.cookieEnabled) {

            // Getting the language stored in the cookie
            storedLanguage = getCookie(key_cookieName_language);
        }
        
        let fallbackLang = getBrowserLanguage();
        if(selector !== val_emptyString || typeof selector === val_undefined || selector === val_emptyString){
            fallbackLang = $(selector).attr('hreflang');
        }
        
        // Takes the cookie stored language if available, otherwise the default
        currentLanguage = (storedLanguage !== val_emptyString && typeof storedLanguage !== val_undefined && storedLanguage !== null) ? storedLanguage : fallbackLang;

    }

    // Storing the language to the cookie if needed
    if ((storedLanguage === null || typeof storedLanguage === val_undefined || storedLanguage === val_emptyString) && navigator.cookieEnabled) {
        // Storing the language in the cookie 
        setCookie(key_cookieName_language, currentLanguage, val_cookieExpirationDays);
    }

    // Getting the right language button if not passed by parameter.
    if (selector === '' || typeof selector === val_undefined || selector === null) {
        selector = $('.' + elementClassName_languageListButton + '[' + elementAttributeName_lang + '="' + currentLanguage + '"]');
    }

    // Loading the localization file for the current language
    loadI18nFile(currentLanguage);
    // Iinitializing the page elements with the language
    refreshSelectedLanguages(selector);
}

/*
 * This method update the language of the webapp with the one selected by the
 * user (using the language selector in the UI).
 *
 * @param {type} selector
 */
function updateLanguage(selector) {

    // Getting the new language selected
    let newLanguage = selector.attr(elementAttributeName_lang);
    if (newLanguage !== val_emptyString || typeof newLanguage !== val_undefined || newLanguage !== null) {

        // Storing the new selected language in the cookie 
        setCookie(key_cookieName_language, newLanguage, val_cookieExpirationDays);

        // Re-initializing the page elements with the new language
        initLocalization(selector);
        // Launch the update language relate actions
        //updateLanguageActions();
    }

    // Close the language dialog
//    $('.' + elementClassName_dialogDismiss).trigger(eventName_click);

    $('#selectedLanguage').html(newLanguage);
    $('#changeLanguage').modal('hide');
}

/*
 * This method is called every time the language is changed.
 */
function updateLanguageActions() {

    // Update all the HTML element with the i18n data attribute available
    updateDataI18nLocalization();
    // Update all the href element with the i18n-link data attribute available
    updateDataI18nLocalizationLinks();
    // Fetching the data
    fetchData();
}

/*
 * This method is handling the UI changes after a new language has been selected
 *
 * @param {DOM element} selector
 */
function refreshSelectedLanguages(selector) {

    // Remove initial active classes and elements in the language selector dialog
    $('.' + elementClassName_languageListButton).each(function () {
        $(this).parent().removeClass(elementClassName_languageListButtonActive);
        $(this).find(elementName_svg).remove();
    });
    // Set the active language in the language selector dialog
    selector.parent().addClass(elementClassName_languageListButtonActive);
    selector.append(htmlSnippet_iconSelected);
    // Update the HTML lang
    $(elementName_html).attr(elementAttributeName_lang, currentLanguage);
    // Update the main logo
    $('.' + elementClassName_eclsiteHeaderLogoImage).attr(key_src, registryApp.hostURL + registryApp.staticResourcesPath + key_languageLogoPattern.replace('{0}', currentLanguage));
    // Updating the selected text label and code
    let currentLanguageLabel = selector.text();
    let currentLinkHtml = $('.' + elementClassName_selectedLangLabel);
    let spanLanguageCode = currentLinkHtml.find(elementName_span + '.' + elementClassName_selectedLangIcon);
    currentLinkHtml.html(currentLanguageLabel);
    currentLinkHtml.append(spanLanguageCode);
    $('.' + elementClassName_selectedLangCodeText).text(currentLanguage);
}

/*
 * This method loads the i18n localization file and fires the
 * updateLanguageActions method
 *
 * @param {String} locale The locale file to load
 */
function loadI18nFile(locale) {
    $.getJSON(registryApp.hostURL + key_dataLocalizationFilesPath + '/' + locale + '.' + key_json, function (data) {

        // Stores the i18n Json object in the global variable i18n
        i18n = data;
        // Launch the methods related to the change locale action
        updateLanguageActions();
    })
            .fail(function () {
                // If the locale language file  is not available, reading the
                // default one
                console.log('Failed loading locale file. Reading the default one.');
                loadI18nFile(registryApp.defaultLanguage);
            });
}

/*
 * This method update all the HTML element with the i18n data attribute
 * available with the text localized in the 'currentLanguage' localization
 */
function updateDataI18nLocalization() {

    // Getting all the elements that have the 'i18n' data attribute valorized
    // The value of the i18n data attribute is the key to get the right text
    // from the i18n localization file.
    let el = $('*').filter(function () {
        return typeof $(this).data(key_i18n) !== val_undefined;
    });
    // For each element retrieved, the text is updated with the new
    // localized text
    $.each(el, function () {
        let localizationKey = $(this).data(key_i18n);
        $(this).html(i18n[localizationKey]);
    });
}

/*
 * This method update all the href element with the i18n data link attribute
 * available with the link in the right language
 */
function updateDataI18nLocalizationLinks() {

    // Getting all the elements that have the 'i18n-link' data attribute valorized
    // The value of the i18n-link data attribute is the key to get the right link
    // from the i18n localization file.
    let linkEl = $('*').filter(function () {
        return typeof $(this).data(key_i18nLink) !== val_undefined;
    });
    // For each element retrieved, the link is updated with the new
    // localized link
    $.each(linkEl, function () {
        let localizationKey = $(this).data(key_i18nLink);
        $(this).attr('href', i18n[localizationKey].replace('{0}', currentLanguage));
    });
}

/*
 * This method check and set the Browser language
 *
 * @returns {String} The 2 characters language of the browser if available
 * otherwhise it returns the registryApp.defaultLanguage
 */
function getBrowserLanguage() {

    let userLang = navigator.language || navigator.userLanguage;
    if (userLang !== null && userLang.length > 0) {

        // Getting just the first 2 characters from the string
        userLang = userLang.substring(0, 2);
    } else {
        userLang = registryApp.defaultLanguage;
    }

    return userLang;
}