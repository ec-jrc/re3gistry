"use strict";

/// *** Scripts and utilities common to all the app ***///

// ** Constants and variables definition ** //

// Element name constants

const elementId_searchForm = 'search-form';
const elementId_buttonSearch = 'btn-search';

const elementClassName_languageListButton = 'ecl-language-list__link';
const elementClassName_loadingOverlay = 'overlay-loader';

const elementName_html = 'html';
const elementName_body = 'body';
const elementName_span = 'span';
const elementName_svg = 'svg';

const elementAttributeName_lang = 'lang';
const elementAttributeName_class = 'class';

// Key contants
const key_cookieName_language = 'clanguage';
const key_cookieExpires = 'expires';
const key_cookiePath = 'path';
const key_ascOrdering = 'asc';
const key_descOrdering = 'desc';
const key_json = 'json';
const key_jsonc = 'jsonc';
const key_src = 'src';
const key_searchParameter = 'q';
const key_dataEclMessage = 'data-ecl-message';
const key_http = 'http';
const key_https = 'https';

// Value constants
const val_cookieExpirationDays = 30;
const val_emptyString = '';
const val_dot = '.';
const val_undefined = 'undefined';
const val_object = 'object';
const val_true = 'true';
const val_falsee = 'false';

// HTML snippet constants
const htmlSnippet_errorMessage = '<div role="alert" class="ecl-message ecl-message--error" data-ecl-message="true" data-ecl-auto-init="Message"><svg focusable="false" aria-hidden="true" class="ecl-message__icon ecl-icon ecl-icon--l"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#notifications--error"></use></svg><div class="ecl-message__content"><button data-ecl-message-close="true" type="button" class="ecl-message__close ecl-button ecl-button--ghost"><span class="ecl-button__container"><span class="ecl-button__label" data-ecl-label="true">Close</span><svg focusable="false" aria-hidden="true" data-ecl-icon="true" class="ecl-button__icon ecl-button__icon--after ecl-icon ecl-icon--s"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--close"></use></svg></span></button><div class="ecl-message__title">Error message</div><p class="ecl-message__description">{0}</p></div></div>';

// Event name constants
const eventName_click = 'click';

// Regular expression constants

// Global variables
var uriFromUrl = val_emptyString;
var baseBreadcrumb = val_emptyString;

var unparsedLanguageJSON = "";
var originalLanguages = new Dictionary();
     originalLanguages.add("en", "English");
     originalLanguages.add("es", "Español");
     originalLanguages.add("bg", "български");
     originalLanguages.add("cs", "čeština");
     originalLanguages.add("da", "dansk");
     originalLanguages.add("de", "Deutsch");
     originalLanguages.add("et", "eesti");
     originalLanguages.add("el", "ελληνικά");
     originalLanguages.add("fr", "français");
     originalLanguages.add("ga", "Gaeilge");
     originalLanguages.add("hr", "hrvatski");
     originalLanguages.add("it", "italiano");
     originalLanguages.add("lv", "latviešu");
     originalLanguages.add("lt", "lietuvių");
     originalLanguages.add("hu", "magyar");
     originalLanguages.add("mt", "Malti");
     originalLanguages.add("nl", "Nederlands");
     originalLanguages.add("pl", "polski");
     originalLanguages.add("pt", "português");
     originalLanguages.add("ro", "română");
     originalLanguages.add("sk", "slovenčina");
     originalLanguages.add("sl", "slovenščina");
     originalLanguages.add("fi", "suomi");
     originalLanguages.add("sv", "svenska");

// ** Script body ** //

function bindCommonEvents() {

    // Event associated to the "Search" button
    $('#' + elementId_buttonSearch).on(eventName_click, function (e) {
        e.preventDefault();
        performSearch();
    });

    // Language selector change event
    $('.' + elementClassName_languageListButton).on(eventName_click, function (event) {
        event.preventDefault();
        updateLanguage($(this));
    });
}

/*
 * Seting a cookie
 * 
 * @param {String} cookieName The name of the ckookie
 * @param {String} cookieValue The value of the cookie
 * @param {Integer} expiringDays The number of days to keep the cookie
 */
function setCookie(cookieName, cookieValue, expiringDays) {

    // Checks if the cookies are enabled in the system and if the EU cookie
    // consent has been accepted
//    if ($wt.analytics.isTrackable()) {
        var d = new Date();
        d.setTime(d.getTime() + (expiringDays * 24 * 60 * 60 * 1000));
        var expires = key_cookieExpires + '=' + d.toUTCString();
        document.cookie = cookieName + '=' + cookieValue + ';' + expires + ';' + key_cookiePath + '=/';
//    }
}

/*
 * Getting the cookie by name
 * 
 * @param {String} cookieName The name of the ckookie to retrieve
 * @returns {String} The value of the cookie
 */
function getCookie(cookieName) {
//    if ($wt.analytics.isTrackable()) {
        var name = cookieName + '=';
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) === 0) {
                return c.substring(name.length, c.length);
            }
        }
        return val_emptyString;
//    }
}
/*
 * Function to sort an array (to be used in Array.sort(function(a,b){}))
 * 
 * @param {String} a The first value to compare
 * @param {String} b The second value to compare
 * @param {String} ordering The ordering method: asc|desc
 * @returns {Integer} Zero or one depending on the comparison
 */
function sortArray(a, b, ordering) {

    let aInt = parseInt(a);
    let bInt = parseInt(b);

    let outValue = 0;

    if (ordering === key_descOrdering) {
        // Descending
        outValue = (aInt > bInt) ? 0 : 1;
    } else if (ordering === key_ascOrdering) {

        // Ascending
        outValue = (aInt < bInt) ? 0 : 1;
    }

    return outValue;
}

/*
 * Function to process the URI of the current elemen. It gets eventual language
 * passed by URL parameter (e.g. registry.en.html)
 */
function processUri() {

    // Getting the current URL
    let currentUrl = window.location.href;
    if (currentUrl.endsWith("/")) {
        currentUrl = currentUrl.substring(0, currentUrl.length - 1);
    }
    // Getting the index of the lase occurence of "/"
//    let i = currentUrl.lastIndexOf('/');

    // Cutting the current URL to the last portion
//    let tmpUrl = currentUrl.substring(i);
    // Splitting this portion with "." to chech if it is specified a language
    // (e.g. elementName.en.html, but also the localID could contain a ".")

    //example localID: de.codelist.test
    //example localID with language and format: de.codelist.test.en.xml



//    let urlCheck = tmpUrl.split(val_dot);

    // If the lenght of the urlCheck is 3
//    if (urlCheck.length === 3) {
//
//        // Getting the language specified in the URL and updating the
//        // global variable
//        let tmpLangIndex = tmpUrl.indexOf(val_dot);
//        let tmpLang = tmpUrl.substring(tmpLangIndex + 1, tmpLangIndex + 3);
//        languageFromUrl = tmpLang;
//
//        // Getting the URI to be passed to the data service and updating the
//        // global variable
//        i = currentUrl.lastIndexOf(val_dot) - 3;
//        uriFromUrl = currentUrl.substring(0, i);
//    } else {
//
//        // Passing the current URL as the URI to be passed to the data service
//        languageFromUrl = val_emptyString;
//        uriFromUrl = currentUrl;
//    }

    // Passing the current URL as the URI to be passed to the data service
//    languageFromUrl = val_emptyString;
    uriFromUrl = currentUrl;

    // Check if the flag to force http is on
    if (registryApp.forceHttpURIs) {
        uriFromUrl = uriFromUrl.replace(key_https + '://', key_http + '://')
    }
}

function getUrlParameter(parameterName) {
    let result = null;
    let tmp = [];

    location.search
            .substr(1)
            .split("&")
            .forEach(function (item) {
                tmp = item.split("=");
                if (tmp[0] === parameterName)
                    result = decodeURIComponent(tmp[1]);
            });
    return result;
}

/*
 * This method performs the search
 */
function performSearch() {

    let searchBoxElement = $('#' + elementId_searchForm);
    window.location.href = registryApp.searchURL + '?' + key_searchParameter + '=' + searchBoxElement.val();
}

/* Show or hide the loading overlay */
function showLoadingOverlay(show) {

    let loadingOverlayElement = $('.' + elementClassName_loadingOverlay);

    if (show) {
        loadingOverlayElement.show();
    } else {
        loadingOverlayElement.hide();
    }
}

function Dictionary(){
	this.add = add;
	this.dataStore = [];
	this.find = find;
	this.remove = remove;
}

function add(key, value){
	this.dataStore[key] = value;
}

function remove(key){
	delete this.dataStore[key];
}

function find(key){
	return this.dataStore[key];
}

function fillLanguageTable(){

    //Check if languages have already been set in the table
    // if(unparsedLanguageJSON == "" || document.getElementsByClassName("ecl-language-list__item").length == 0){

    // Get the JSON that contains the active language list
    let JSONLINK = registryApp.hostURL + "/rest?lang=active&format=jsonc"
    unparsedLanguageJSON =  $.ajax({ 
        url: JSONLINK, 
        async: false
     }).responseText;

     var languageJSON = JSON.parse(unparsedLanguageJSON);
     var columnChange = false;

     var finalLanguageColumns = document.getElementsByClassName("ecl-language-list__list");

     for(var i=0; i<languageJSON.length;i++){

        var languageli = document.createElement("li");
        var languagelink = document.createElement("a");
        var languageSpan = document.createElement("span");

        languageSpan.setAttribute("class","ecl-link__label");
        languageli.setAttribute("class", "ecl-language-list__item");
        languagelink.lang = languageJSON[i].iso6391code;
        languagelink.setAttribute("hreflang", languageJSON[i].iso6391code);
        languagelink.rel = "alternate";
        languagelink.href = "#language_" + languageJSON[i].iso6391code;
        languagelink.setAttribute("class", "ecl-language-list__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-after"); 
        languageSpan.innerHTML = originalLanguages.find(languageJSON[i].iso6391code);
        languagelink.appendChild(languageSpan);
        languageli.appendChild(languagelink);

        if(columnChange){
            columnChange = false;
            finalLanguageColumns[0].appendChild(languageli);
        }else{
            columnChange = true;
            finalLanguageColumns[1].appendChild(languageli);
        }
     }

     }