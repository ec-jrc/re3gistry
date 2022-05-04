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

// The app's base URL
registryApp.domainURL = 'http://testinspire.ec.europa.eu';
registryApp.hostURL = '//testinspire.ec.europa.eu/registry';
registryApp.searchURL = '//testinspire.ec.europa.eu/registry/search';
registryApp.searchApiURL = '//testinspire.ec.europa.eu/registry/searchapi';
registryApp.staticResourcesPath = '/ecl-v2/static/media/';

registryApp.dataServiceURL = '//testinspire.ec.europa.eu/registry/rest';