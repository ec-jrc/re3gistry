"use strict";

/// *** App configuration file ***///

var registryApp = {};

// The default language of the app (used as a fallback if the language is not
// passed by URL or cannot be retrieved by Browser language)
registryApp.defaultLanguage = 'en';

// Search maximum results per page
registryApp.maxSearchResultsPerPage = 10;

// Force http URIs (even if the original call has https URIs)
registryApp.forceHttpURIs = true;

// The app's base URLs
registryApp.hostURL = '//registry-test.eu/registry';
registryApp.searchURL = '//registry-test.eu/registry/search';
registryApp.searchApiURL = '//registry-test.eu/registry/searchapi';
registryApp.dataServiceURL = '//registry-test.eu/registry/rest';
