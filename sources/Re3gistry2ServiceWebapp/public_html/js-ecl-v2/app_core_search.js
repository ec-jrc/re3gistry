"use strict";
/// *** Core app scripts ***///

// ** Constants and variables definition ** //

// Search query
const searchQuery = 'fl_label_{0}:({1}*)^3.0 OR fl_definition_{0}:({1}*)^1.0';

// Element name constants
const elementId_resultsContainer = 'resultsContainer';
const elementId_dynamicFacetsContainer = 'dynamic-facets-container';
const elementId_previousPageLink = 'previous-page-link';
const elementId_nextPageLink = 'next-page-link';
const elementId_paginationContainer = 'paginationContainer';
const elementId_buttonClearAll = 'btn-clear-all';
const elementId_buttonRefineResults = 'btn-refine-results';
const elementId_facetForm = 'facet-form';
const elementId_selectedFacet = 'selected-facet';

const elementClassName_searchResultsCount = 'search-results-count';
const elementClassName_searchResultsCountCurrentPage = 'search-results-count-current-page';
const elementClassName_hrSearchResults = 'ecl-u-mv-none';
const elementClassName_selectedFacetItem = 'selected-facet-item';

// Key contants
const key_errorFetch = 's-error-fetch';
const key_pageParameter = 'p';
const key_facetParameter = 'f';
const key_searchResultsTitle = 's-search-results';
const key_facetTitlePrefix = 's-facet-title-';
const key_searchResultsCurrentPageTitle = 's-search-results-current-page';
const key_facetAllLabel = 's-facet-all';
const key_solrResultFieldLabelPrefix = 'fl_label_';
const key_solrResultFieldDefinitionPrefix = 'fl_definition_';
const key_solrResultFieldDescritpionPrefix = 'fl_description_';
const key_searchRegisterLabel = 's-search-register-label';
const key_searchNoResultsFound = 's-search-no-result-found';
const key_paginationPrevious = 's-pagination-previous';
const key_paginationNext = 's-pagination-next';
const key_paginationPage = 's-pagination-page';
const key_paginationGoToPage = 's-pagination-gotopage';
const key_paginationGoToPreviousPage = 's-pagination-gotopreviouspage';
const key_paginationGoToNextPage = 's-pagination-gotonextpage';
const key_facetParamSeparator = '+';
const key_facetParamKeyValueSeparator = ':';
const key_facetParamValueSplitter = /:(.+)?/;
const key_selectedFacetFirst = 'ecl-u-ml-lg-m ecl-u-mt-m ecl-u-mt-lg-none';
const key_dataFacetParameter = 'facetparameter';
const key_JsonObjectFieldKeyFq = 'fq';
const key_value = 'value';
const key_selected = 'selected';
const key_facetPrefix = 'facet-';
const key_option = 'option';
const key_all = '*'

// Value constants
const val_paginationManySeparator = '...';

// HTML snippet constants
const htmlSnippet_facet = '<div class="ecl-u-mt-m ecl-form-group ecl-form-group--select"><label class="ecl-form-label" for="facet-{0}">{1}</label><div class="ecl-select__container ecl-select__container--m"><select id="facet-{0}" class="ecl-select">{2}</select><div class="ecl-select__icon"><svg focusable="false" aria-hidden="true" class="ecl-select__icon-shape ecl-icon ecl-icon--s ecl-icon--rotate-180"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg></div></div></div>';
const htmlSnippet_option = '<option value="{0}">{1} {2}</option>';
const htmlSnippet_option_selected = '<option selected value="{0}">{1} {2}</option>';
const htmlSnippet_searchResults = '<article class="ecl-u-type-m ecl-u-mt-l ecl-u-pb-l ecl-u-pb-lg-m"><a href="{0}" class="ecl-u-type-prolonged-m ecl-u-type-bold ecl-link">{1}</a><p class="ecl-u-type-paragraph-m ecl-u-type-color-grey ecl-u-mv-none">{2}</p><p class="ecl-u-type-paragraph-m ecl-u-type-color-grey ecl-u-mv-none">{3}</p></article>';
const htmlSnippet_searchNoResults = '<article class="ecl-u-type-m ecl-u-mt-l ecl-u-pb-l ecl-u-pb-lg-m"><span class="ecl-u-type-prolonged-m ecl-u-type-bold">{0}</span></article>';
const htmlSnippet_paginationUl = '<ul class="ecl-pagination__list">{0}</ul>';
const htmlSnippet_paginationPrevious = '<li class="ecl-pagination__item ecl-pagination__item--previous"><a id="previous-page-link" aria-label="{0}" href="{1}" class="ecl-pagination__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-270"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg> <span class="ecl-link__label">{2}</span></a></li>';
const htmlSnippet_paginationNext = '<li class="ecl-pagination__item ecl-pagination__item--next"><a id="next-page-link" aria-label="{0}" href="{1}" class="ecl-pagination__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-after"><span class="ecl-link__label">{2}</span> <svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-90"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg></a></li>';
const htmlSnippet_currentPage = '<li class="ecl-pagination__item ecl-pagination__item--current"><span class="ecl-pagination__text ecl-pagination__text--summary" aria-label="{1} {0}" aria-current="true">{0}</span><span class="ecl-pagination__text ecl-pagination__text--full" aria-current="true">{1} {0}</span></li>';
const htmlSnippet_page = '<li class="ecl-pagination__item"><a aria-label="{0} {1}" href="{2}" class="ecl-pagination__link ecl-link ecl-link--standalone">{1}</a></li>';
const htmlSnippet_paginationLi = '<li class="ecl-pagination__item">{0}</li>';
const htmlSnippet_selectedFacetElement = '<span class="{0}"><span>{1}</span><button data-' + key_dataFacetParameter + '="{2}" class="ecl-u-ml-s ecl-tag ecl-tag--removable selected-facet-item">{3}<span class="ecl-tag__icon"><svg focusable="false" aria-hidden="true" class="ecl-tag__icon-close ecl-icon ecl-icon--xs"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--close"></use></svg><svg focusable="false" aria-hidden="true" class="ecl-tag__icon-close-filled ecl-icon ecl-icon--xs"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--close-filled"></use></svg></span></button></span>';
const htmlSnippet_href = '<a href="{0}" class="ecl-link ecl-link--standalone">{1}</a>';
const htmlSnippet_hr = '<hr class="{0}" />';


// Event name constants

// Regular expression constants

// Global variables

// Facets
const searchFacets = {
    register_itemclass_localid: {
        type: 'terms',
        field: 'register_itemclass_localid',
        limit: -1
    },
    status_uri: {
        type: 'terms',
        field: 'status_uri',
        limit: -1
    }
};

// ** Script body ** //

/*
 * Fetch Re3gistry data
 * 
 * @param {String} uri The uri of the item to retrieve
 * @param {String} lang The language of the data  
 */
function fetchData(uri, lang, startFrom, searchTerm, facetParam) {

    if (uri === null || typeof uri === val_undefined || uri.length === 0) {
        uri = uriFromUrl;
    }

    if (lang === null || typeof lang === val_undefined || lang.length === 0) {
        lang = currentLanguage;
    }

    // Getting the search query
    if (searchTerm === null || typeof searchTerm === val_undefined || searchTerm.length === 0) {
        searchTerm = getUrlParameter(key_searchParameter);
        if (searchTerm === null || typeof searchTerm === val_undefined || searchTerm.length === 0) {
            searchTerm = key_all;
        } else {
            // Setting the search therm in the search box
            updateSearchBox(searchTerm);
        }
    } else {
        // Setting the search therm in the search box
        updateSearchBox(searchTerm);
    }

    // Getting the results page (if available)
    if (startFrom === null || typeof startFrom === val_undefined || isNaN(startFrom)) {
        startFrom = getUrlParameter(key_pageParameter);
        if (startFrom === null || typeof startFrom === val_undefined || isNaN(startFrom)) {
            startFrom = 1
        }
    }

    // Getting eventrual facet parameter
    if (facetParam === null || typeof facetParam === val_undefined) {
        facetParam = getUrlParameter(key_facetParameter);
        if (facetParam === null || typeof facetParam === val_undefined) {
            facetParam = val_emptyString;
        }
    }

    // Preparing the search expression
    let queryEncoded = searchQuery.split('{0}').join(currentLanguage).split('{1}').join(searchTerm);

    // Preparing the query
    let queryParameters = {
        q: queryEncoded,
        start: (startFrom - 1) * registryApp.maxSearchResultsPerPage,
        rows: registryApp.maxSearchResultsPerPage,
        wt: key_json,
        facet: val_true,
        'json.facet': JSON.stringify(searchFacets)
    };

    // Adding eventual cfacet query to the query parameters
    let facetParamArray = parseFacetParam(facetParam);
    queryParameters = addFacetParamToQuery(facetParamArray, queryParameters);

    // Setting the traditional style of param serialization
    $.ajaxSetup({traditional: true});

    // Performing the request
    $.ajax({
        // Base URL of the service taken from the configuration 
        url: registryApp.searchApiURL,
        data: queryParameters,
        dataType: key_json

    }).done(function (responseData) {

        // Rendering the HTML
        renderData(responseData, searchTerm, facetParamArray, facetParam);

        // Binding UI events
        bindEvents();

    }).fail(function (data) {

        // Clearing the intrerface
        renderData(data, null, null, null);

        // Error handler
        renderFetchError(data.responseJSON);

    });
}

/*
 * Render the succesful response from the service
 * 
 * @param {Json} data The Re3gistry json data 
 */
function renderData(data, searchTerm, facetParamArray, facetParam) {

    if (data !== null && typeof data !== val_undefined) {

        // Rendering the results counts
        renderSearchResultsCount(data);
        renderSearchResultsCountCurrentPage(data);

        // Rendering the facets
        renderFacets(data, facetParamArray);
        renderSelectedFacets(facetParamArray);

        // Rendering the results
        renderResults(data, searchTerm);

        // Render pages links
        renderPagesLinks(data, searchTerm, facetParam);

    } else {

        // If there is an error on the service respose, rendering the error
        renderServiceError(data);

    }
}

/*
 * Bind the events for the elements dynamically created after the search api request
 */
function bindEvents() {

//    // Event associated to the "Search" button
//    $('#' + elementId_buttonSearch).on(eventName_click, function (e) {
//        e.preventDefault();
//        performSearch();
//    });

    // Event associated to the "Clear all" button (to clear all the facets)
    $('#' + elementId_buttonClearAll).on(eventName_click, function (e) {
        e.preventDefault();
        clearAllFacets();
    });

    // Event associated to the "Refine results" button
    $('#' + elementId_buttonRefineResults).on(eventName_click, function (e) {
        e.preventDefault();
        refineResults();
    });

    // Event fired once a facet is removed from  the list of selected facets
    $('.' + elementClassName_selectedFacetItem).on(eventName_click, function (e) {
        e.preventDefault();
        removeFacet($(this));
    });
}

/*
 * Remove the facet from the list of selected facets
 * 
 * @param {Object} item The button item
 */
function removeFacet(item) {

    let facetParam = item.data(key_dataFacetParameter);

    // Selecting the empty option
    $('#' + key_facetPrefix + facetParam + ' ' + key_option).each(function () {
        if ($(this).prop(key_value) === val_emptyString) {
            $(this).prop(key_selected, key_selected);
        }
    });

    // Refining the results with the empty option selected
    refineResults();
}

/*
 * This method updates the search box (reading from the "q" url parameter)
 */
function updateSearchBox(searchTerm) {

    let searchInputElement = $('#' + elementId_searchForm);
    searchInputElement.val(searchTerm);
}

/*
 * This method is used to refine the results using the eventual facets selected
 */
function refineResults() {

    let searchBoxElement = $('#' + elementId_searchForm);

    // Getting the selected facets
    let facetSelects = $('#' + elementId_facetForm + ' ' + key_option + ':' + key_selected);

    let facetUrlParams = val_emptyString;

    let i = 0;
    // Cycling on the selected facets
    for (let tmpSelect of facetSelects) {

        // If the selected facet is not empty (not the "All" option), creating
        // the url param
        if ($(tmpSelect).val().trim().length > 0) {
            if (i !== 0) {
                facetUrlParams += key_facetParamSeparator;
            }
            facetUrlParams += $(tmpSelect).val();
            i++;
        }
    }

    // Composing the URL to call
    window.location.href = registryApp.searchURL + '?' + key_searchParameter + '=' + searchBoxElement.val() + ((facetUrlParams.length > 0) ? '&' + key_facetParameter + '=' + facetUrlParams : '');
}

/*
 * This method is used to clear all the selected facets
 */
function clearAllFacets() {

    let searchBoxElement = $('#' + elementId_searchForm);
    window.location.href = registryApp.searchURL + '?' + key_searchParameter + '=' + searchBoxElement.val();
}

/*
 * This method is used to parse the facet parameter from the URL
 * 
 * @param {String} facetParam THe facet parameter from the URL 
 */
function parseFacetParam(facetParam) {

    let facetParameterArray = [];

    // Splitting using the separator betweeb different facets
    let tmpParams = facetParam.split(key_facetParamSeparator);
    for (let tmpParam of tmpParams) {

        // Splitting using the separator between key/values
        let tmpKeyVal = tmpParam.split(key_facetParamValueSplitter);
        // Adding the facet to the array of selected facets (if not empty - not the "All" option) 
        if (tmpKeyVal !== null && typeof tmpKeyVal !== val_undefined && tmpKeyVal.length > 1) {
            facetParameterArray.push(tmpKeyVal);
        }
    }

    return facetParameterArray;
}

/*
 * This method is used to add the facet parameters to the standard query
 * parameters (json object)
 * 
 * @param {Json} queryParameters The json object representing the 
 * query parameters (for jQuery ajax call)
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets 
 */
function addFacetParamToQuery(facetParamArray, queryParameters) {

    if (facetParamArray !== null && typeof facetParamArray !== val_undefined) {

        // Adding any eventual facets to the query 
        let tmpParam = [];
        for (let param of facetParamArray) {
            tmpParam.push(param[0] + key_facetParamKeyValueSeparator + '"' + param[1] + '"');
        }

        queryParameters[key_JsonObjectFieldKeyFq] = tmpParam;
    }

    return queryParameters;
}


/*
 * Update the search results count
 * 
 * @param {Json} data The search API response
 */
function renderSearchResultsCount(data) {

    // Getting the search results UI element
    let searchResultsCountContainer = $('.' + elementClassName_searchResultsCount);

    let tmpText = i18n[key_searchResultsTitle];

    if (data.response !== null && typeof data.response !== val_undefined &&
            data.response.numFound !== null && typeof data.response.numFound !== val_undefined) {

        tmpText = tmpText.replace('{0}', data.response.numFound);

    } else {
        tmpText = tmpText.replace('{0}', 0);
    }

    searchResultsCountContainer.html(tmpText);
}

/*
 * Update the search results count for this page
 * 
 * @param {Json} data The search API response
 */
function renderSearchResultsCountCurrentPage(data) {

    // Getting the search results current page UI element
    let searchResultsCountCurrentPageContainer = $('.' + elementClassName_searchResultsCountCurrentPage);

    let tmpText = i18n[key_searchResultsCurrentPageTitle];

    if (data.response !== null && typeof data.response !== val_undefined &&
            data.response.start !== null && typeof data.response.start !== val_undefined &&
            data.response.numFound !== null && typeof data.response.numFound !== val_undefined && data.response.numFound > 0) {

        tmpText = tmpText.replace('{0}', data.response.start + 1).replace('{1}', data.response.start + registryApp.maxSearchResultsPerPage);

    } else {
        tmpText = val_emptyString;
    }

    searchResultsCountCurrentPageContainer.html(tmpText);
}

/*
 * Rendering the facets element (left menu)
 * 
 * @param {Json} data The search API response
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets
 */
function renderFacets(data, facetParamArray) {

    let dynamicFacetsContainer = $('#' + elementId_dynamicFacetsContainer);
    let clearAllElement = $('#' + elementId_buttonClearAll);
    let refineResultsElement = $('#' + elementId_buttonRefineResults);

    // Hiding contols
    clearAllElement.hide();
    refineResultsElement.hide();

    let htmlOut = val_emptyString;

    if (data.facets !== null && typeof data.facets !== val_undefined) {

        // Generating html (select) for each facet
        for (const [key, value] of Object.entries(data.facets)) {
            if (typeof value === val_object) {

                htmlOut += htmlSnippet_facet.split('{0}').join(key)
                        .replace('{1}', i18n[key_facetTitlePrefix + key]);
                let tmpOptions = generateFacetsOptions(value.buckets, key, facetParamArray);

                htmlOut = htmlOut.replace('{2}', tmpOptions);
            }
        }

        // Showing the control button only if at least one facet is available 
        if (htmlOut !== val_emptyString) {
            clearAllElement.show();
            refineResultsElement.show();
        }
    }

    dynamicFacetsContainer.html(htmlOut);
}

/*
 * Check if a specific facet value has been selected in the html select
 * 
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets
 * @param {String} val The value to be checked
 */
function checkSelected(facetParamArray, val) {
    for (let tmp of facetParamArray) {
        if (tmp[1] === val) {
            return true;
        }
    }
    return false;
}

/*
 * Rendering the facets options
 * 
 * @param {Json} bucket data from the facets
 */
function generateFacetsOptions(buckets, key, facetParamArray) {

    let htmlOut = val_emptyString;

    // Generate the empty selection option
    htmlOut = htmlSnippet_option.replace('{0}', val_emptyString).replace('{1}', i18n[key_facetAllLabel]).replace('{2}', val_emptyString);

    if (buckets !== null && typeof buckets !== val_undefined) {

        for (let bucket of buckets) {

            if (checkSelected(facetParamArray, bucket.val)) {
                htmlOut += htmlSnippet_option_selected.replace('{0}', key + ':' + bucket.val).replace('{1}', bucket.val).replace('{2}', '(' + bucket.count + ')');
            } else {
                htmlOut += htmlSnippet_option.replace('{0}', key + ':' + bucket.val).replace('{1}', bucket.val).replace('{2}', '(' + bucket.count + ')');
            }
        }
    }
    return htmlOut;
}

/*
 * Rendering the selected facets (under the search results counts)
 * 
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets
 */
function renderSelectedFacets(facetParamArray) {

    let htmlOut = val_emptyString;

    let selectedFacetElement = $('#' + elementId_selectedFacet);

    if (facetParamArray !== null && typeof facetParamArray !== val_undefined) {
        let i = 0;
        for (let tmpParam of facetParamArray) {

            let tmpClass = val_emptyString;
            if (i !== 0) {
                tmpClass += key_selectedFacetFirst;
            }

            htmlOut += htmlSnippet_selectedFacetElement.replace('{0}', tmpClass).replace('{1}', i18n[key_facetTitlePrefix + tmpParam[0]]).replace('{2}', tmpParam[0]).split('{3}').join(tmpParam[1]);
            i++;
        }

        selectedFacetElement.html(htmlOut);
    }
}

/*
 * Function to render the search results
 * 
 * @param {Json} data The search API response
 */
function renderResults(data, searchTerm) {

    // Getting the results container element
    let resultsContainer = $('#' + elementId_resultsContainer);

    let htmlOut = val_emptyString;
    if (data.response !== null && typeof data.response !== val_undefined &&
            data.response.docs !== null && typeof data.response.docs !== val_undefined && data.response.docs.length > 0) {

        let i = 0;
        for (let result of data.response.docs) {

            // Handling the URI
            let tmpOut = htmlSnippet_searchResults.replace('{0}', result.uri);
            // Handling the label
            let tmpResLabel = checkField(result, key_solrResultFieldLabelPrefix, currentLanguage);
            tmpOut = tmpOut.replace('{1}', tmpResLabel);

            // Handling the register
            let tmpHref = result.register_itemclass_baseuri + '/' + result.register_itemclass_localid
            tmpOut = tmpOut.replace('{2}', i18n[key_searchRegisterLabel] + ': ' + renderHref(tmpHref,tmpHref));

            // Handling definition
            let tmpResDefinition = checkField(result, key_solrResultFieldDefinitionPrefix, currentLanguage);

            if (tmpResDefinition.length > 0) {
                tmpOut = tmpOut.replace('{3}', tmpResDefinition);
            } else {
                // Trying to show the description in case the definition is not available
                let tmpResDescription = checkField(result, key_solrResultFieldDescritpionPrefix, currentLanguage);
                tmpOut = tmpOut.replace('{3}', tmpResDescription);
            }

            if (i !== 0) {
                htmlOut += renderHr(elementClassName_hrSearchResults);
            }

            htmlOut += tmpOut;

            i++;
        }

    } else {

        //No results
        htmlOut = htmlSnippet_searchNoResults.replace('{0}', i18n[key_searchNoResultsFound]).replace('{1}', searchTerm);
    }

    resultsContainer.html(htmlOut);
}

/*
 * The method checks if a field is available in the current language otherwise it check if
 * the default language is available (otherwise it returns empty string)
 * 
 * @param {Json} data The search API response
 * @param {String} fieldPrefix The prefix of the filed name (without language)
 * @param {String} language The language for the field to be checked
 */
function checkField(data, fieldPrefix, language) {
    let tmpString = data[fieldPrefix + language];

    // If the field is not available in the current language
    if (tmpString === null || typeof tmpString === val_undefined || tmpString.lenght <= 0) {
        // If the language is not the default language, the check is relaunched
        // with the default language
        if (language !== registryApp.defaultLanguage) {

            tmpString = checkField(data, fieldPrefix, registryApp.defaultLanguage);
        } else {
            tmpString = val_emptyString;
        }
    }
    return tmpString;
}

/*
 * The method renders the pagination links
 * 
 * @param {Json} data The search API response
 * @param {String} query Thesearch query (search term from the search box)
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets
 */
function renderPagesLinks(data, query, facetParam) {

    let htmlOut = val_emptyString;
    let paginationContainer = $('#' + elementId_paginationContainer);

    if (data.response !== null && typeof data.response !== val_undefined &&
            data.response.numFound !== null && typeof data.response.numFound !== val_undefined && data.response.numFound > 0) {

        let currentPageNumber = Math.ceil(data.response.start / registryApp.maxSearchResultsPerPage) + 1;
        let totalPages = Math.ceil(data.response.numFound / registryApp.maxSearchResultsPerPage);

        // Previous page link
        if (+currentPageNumber > 1) {
            if (+currentPageNumber === 2) {
                htmlOut += htmlSnippet_paginationPrevious.replace('{0}', i18n[key_paginationGoToPreviousPage]).replace('{1}', createSearchPageUrl((+currentPageNumber - 1), query, facetParam)).replace('{2}', i18n[key_paginationPrevious]);
            } else {
                htmlOut += htmlSnippet_paginationPrevious.replace('{0}', i18n[key_paginationGoToPreviousPage]).replace('{1}', createSearchPageUrl((+currentPageNumber - 1), query, facetParam)).replace('{2}', i18n[key_paginationPrevious]);
            }
        } else {
            htmlOut += val_emptyString;
        }

        // Inserting dots in case of many pages
        if ((+currentPageNumber - 2) > 1) {

            htmlOut += htmlSnippet_page.split('{0}').join(i18n[key_paginationGoToPage]).split('{1}').join(1).split('{2}').join(createSearchPageUrl(1, query, facetParam));
            htmlOut += htmlSnippet_paginationLi.replace('{0}', val_paginationManySeparator);
        }

        // Rendering generic pages
        for (var i = +currentPageNumber - 2; i <= +currentPageNumber; i++) {
            if (i >= 1) {
                if (+currentPageNumber !== i) {

                    htmlOut += htmlSnippet_page.split('{0}').join(i18n[key_paginationGoToPage]).split('{1}').join(i).split('{2}').join(createSearchPageUrl(i, query, facetParam));

                } else {

                    htmlOut += htmlSnippet_currentPage.split('{0}').join(i).split('{1}').join(i18n[key_paginationPage]);
                }
            }
        }

        for (var i = +currentPageNumber + 1; i <= +currentPageNumber + 2; i++) {
            if (i <= totalPages) {
                if (+currentPageNumber !== i) {

                    htmlOut += htmlSnippet_page.split('{0}').join(i18n[key_paginationGoToPage]).split('{1}').join(i).split('{2}').join(createSearchPageUrl(i, query, facetParam));

                } else {

                    htmlOut += htmlSnippet_currentPage.split('{0}').join(i).split('{1}').join(i18n[key_paginationPage]);
                }
            }
        }

        if ((+currentPageNumber + 2) < totalPages) {

            htmlOut += htmlSnippet_paginationLi.replace('{0}', val_paginationManySeparator);
            htmlOut += htmlSnippet_page.split('{0}').join(i18n[key_paginationGoToPage]).split('{1}').join(totalPages).split('{2}').join(createSearchPageUrl(totalPages, query, facetParam));
        }


        if (+currentPageNumber < totalPages) {

            htmlOut += htmlSnippet_paginationNext.replace('{0}', i18n[key_paginationGoToNextPage]).replace('{1}', createSearchPageUrl((+currentPageNumber + 1), query, facetParam)).replace('{2}', i18n[key_paginationNext]);

        } else {

            htmlOut += val_emptyString;
        }

        paginationContainer.html(htmlSnippet_paginationUl.replace('{0}', htmlOut));
    } else {
        paginationContainer.html(val_emptyString);
    }
}

/*
 * This method creates the search page URL including the search query, page 
 * and eventually the facets
 * 
 * @param {Number} page The page number
 * @param {String} query The search query (search term from the search box)
 * @param {Array} facetParamArray The array containing the key value pair 
 * for the selected facets
 */
function createSearchPageUrl(page, query, facetParam) {

    return registryApp.searchURL + '?' + key_searchParameter + '=' + encodeURIComponent(query) + '&' + key_pageParameter + '=' + page + ((facetParam.length > 0) ? '&' + key_facetParameter + '=' + facetParam : '');
}

/*
 * Render the error response from the service
 * 
 * @param {Json} data The Re3gistry json data 
 */
function renderFetchError(data) {
    // Getting the container element
    let resultsContainer = $('#' + elementId_resultsContainer);

    // Clearing the conatiner
    resultsContainer.empty();

    resultsContainer.append(htmlSnippet_errorMessage.replace('{0}', i18n[key_errorFetch]));

    // Initializing the ECL Message component after creating it
    let elt = document.querySelector('[' + key_dataEclMessage + ']');
    let message = new ECL.Message(elt);
    message.init();
}

/*
 * Render the error response from the service
 * 
 * @param {Json} data The Re3gistry json data 
 * @return {String} The rendered html of the error
 */
function renderServiceError(data) {

    let resultsContainer = $('#' + elementId_resultsContainer);

    // Clearing the conatiner
    resultsContainer.empty();

    resultsContainer.append(htmlSnippet_errorMessage.replace('{0}', i18n[key_errorFetch]));

    // Initializing the ECL Message component after creating it
    let elt = document.querySelector('[' + key_dataEclMessage + ']');
    let message = new ECL.Message(elt);
    message.init();
}

/*
 * Render the href of the field in HTML
 * 
 * @param {String} value The value of the field
 * @param {String} href The href of the field
 * @returns {String} The rendered href HTML element of the field  
 * 
 */
function renderHref(value, href) {

    return htmlSnippet_href.replace('{0}', href).replace('{1}', value);
}

/*
 * Render the HR
 * 
 * @returns {String} The rendered HTML of the HR
 */
function renderHr(classString) {
    return htmlSnippet_hr.replace('{0}', classString);
}