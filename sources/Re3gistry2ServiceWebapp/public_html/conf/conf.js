"use strict";

/// *** App configuration file ***///

var registryApp = {};

// The default language of the app (used as a fallback if the language is not 
// passed by URL or cannot be retrieved by Browser language)
registryApp.defaultLanguage = 'en';

// Search maximum results per page
registryApp.maxSearchResultsPerPage = 10;

// Force http URIs (even if the original call has https URIs)
registryApp.forceHttpURIs=true;

// Default Error message parameters (Change if needed to your own repository or link)
registryApp.errorMessageDefinition = "Re3gistry Software repository";
registryApp.errorMessageURL = "https://github.com/ec-jrc/re3gistry";

// Default backend link (To put on links and access to the admin interface)
registryApp.backendURL = "//localhost:8080/re3gistry2";

// The app's base URL
registryApp.domainURL = '//registry-test.eu';
registryApp.hostURL = '//registry-test.eu/registry';
registryApp.searchURL = '//registry-test.eu/registry/search';
registryApp.searchApiURL = '//registry-test.eu/registry/searchapi';
registryApp.dataServiceURL = '//registry-test.eu/registry/rest';


// Webapp icon
// If you need more information, you can find it in the manual.
registryApp.staticResourcesPath = '/ecl-v2/static/media/';

// If you want to have an icon for each language, set this variable to true
// and establish the logo path in registryApp.singleLogoPath.
registryApp.multiLogo = true;

// This variable is ONLY for those who want to establish a single site header logo image.
// To do so, registryApp.multiLogo should be false.
registryApp.singleLogoPath = '/ecl-v2/static/media/../logo/logo-1.png';



