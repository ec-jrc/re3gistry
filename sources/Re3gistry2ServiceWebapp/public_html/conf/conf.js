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

var myDomain = "//registry-test.eu";
// Default backend link (To put on links and access to the admin interface)
registryApp.backendURL = myDomain + "/re3gistry2";

// The app's base URL
registryApp.domainURL = myDomain;
registryApp.frontendPath = '/registry';
registryApp.hostURL = registryApp.domainURL + registryApp.frontendPath;
registryApp.searchURL = registryApp.hostURL + '/search';
// should point towards /solr/re3gistry2/select
registryApp.searchApiURL = registryApp.hostURL + '/searchapi';
// should point towards /re3gistry2restapi/items/any webapp url/or forward to it
registryApp.dataServiceURL = registryApp.hostURL + '/re3gistry2restapi/items/any';
// should be the URI for the base registry in re3gistry2 webapp (defaults to frontend URL)
registryApp.defaultRegisterURI = registryApp.hostURL;
