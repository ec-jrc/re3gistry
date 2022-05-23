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
registryApp.hostURL = '//host.docker.internal/registry';
registryApp.searchURL = '//host.docker.internal/registry/search';
registryApp.searchApiURL = '//host.docker.internal/registry/searchapi';
registryApp.staticResourcesPath = '/ecl-v2/static/media/';
registryApp.dataServiceURL = '//host.docker.internal/registry/rest';