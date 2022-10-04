"use strict";

/// *** Core app scripts ***///

// ** Constants and variables definition ** //

// Element name constants
const elementId_mainContainer = 'content';
const elementId_bodyTitle = 'page-title';

const elementClassName_breadcrumb = 'ecl-breadcrumb__container';
const elementClassName_siteIdentity = 'ecl-page-header__title';

const elementName_title = 'title';

// Key contants
const key_uri = 's-uri';
const key_mainAppTitle = 's-site-title';
const key_thisVersion = 's-thisversion';
const key_versionHistory = 's-versionhistory';
const key_collectionTitle = 's-collection-title';
const key_narrowerTitle = 's-narrower-title';
const key_errorFetch = 's-error-fetch';
const key_errorPrefix = 's-error-';
const key_marginVL = 'ecl-u-mv-l';
const key_marginT3xl = 'ecl-u-mt-3xl';
const key_genericError = 's-error-fetch';
const key_paginationPrevious = 's-pagination-previous';
const key_paginationNext = 's-pagination-next';
const key_otherFormats = 's-other-formats';
const key_insertDate = 's-insert-date';
const key_editDate = 's-edit-date';

// Value constants
const val_itemTypeItem = 'item';
const val_itemTypeRegister = 'register';
const val_itemTypeRegistry = 'registry';

// HTML snippet constants
const htmlSnippet_table = '<table class="ecl-table"><thead class="ecl-table__head">{0}</thead><tbody class="ecl-table__body">{1}</tbody></table>';
const htmlSnippet_th = '<th class="ecl-table__header">{0}</th>';
const htmlSnippet_tr = '<tr class="ecl-table__row">{0}</tr>';
const htmlSnippet_td = '<td data-ecl-table-header="{1}" class="ecl-table__cell">{0}</td>';
const htmlSnippet_ul = '<ul class="ecl-unordered-list ecl-unordered-list--no-bullet">{0}</ul>';
const htmlSnippet_li = '<li class="ecl-unordered-list__item">{0}</li>';
const htmlSnippet_href = '<a href="{0}" class="ecl-link ecl-link--standalone">{1}</a>';
const htmlSnippet_href_external_link = '<a href="{0}" class="ecl-link ecl-link--standalone">{1} <svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--external"></use></svg></a>';
const htmlSnippet_href_format = '<li class="ecl-social-media-follow__item"><a href="{0}" class="ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before ecl-social-media-follow__link"><svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#general--file"></use></svg> {1}</a></li>';
const htmlSnippet_field = '<div class="ecl-row"><div class="ecl-col-lg-3 ecl-col-sm-12 ecl-u-mv-s"><span class="ecl-u-type-m">{0}</span></div><div class="ecl-col-lg-9 ecl-col-sm-12 ecl-u-mv-s"><span class="ecl-u-type-m {2}">{1}</span></div></div>';
const htmlSnippet_field_format = '<div class="ecl-row"><div class="ecl-col-lg-3 ecl-col-sm-12 ecl-u-mv-s"><span class="ecl-u-type-m">{0}</span></div><div class="ecl-col-lg-9 ecl-col-sm-12 ecl-u-mv-s"><ul class="ecl-social-media-follow__list">{1}</ul></div></div>';
//const htmlSnippet_field = '<dt class="ecl-description-list__term">{0}</dt><dd class="ecl-description-list__definition">{1}</dd>';
const htmlSnippet_hx = '<h{0} class="ecl-u-type-heading-{0}{1}">{2}</h{0}>';
const htmlSnippet_hr = '<hr class="{0}" />';
const htmlSnippet_breadcrumbLastElement = '<li class="ecl-breadcrumb__segment ecl-breadcrumb__current-page" aria-current="page" data-ecl-breadcrumb-item="static" aria-hidden="false">{0}</li>';
const htmlSnippet_breadcrumbLink = '<li class="ecl-breadcrumb__segment" data-ecl-breadcrumb-item="static" aria-hidden="false"><a href="{0}" class="ecl-breadcrumb__link ecl-link ecl-link--standalone">{1}</a><svg focusable="false" aria-hidden="true" role="presentation" class="ecl-breadcrumb__icon ecl-icon ecl-icon--xs ecl-icon--rotate-90"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg></li>';
const htmlSnippet_dl = '<dl class="ecl-description-list">{0}</dl>';
const htmlSnippet_paginationPreviousIcon = '<svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-270"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg> <span class="ecl-link__label">{0}</span>';
const htmlSnippet_paginationNextIcon = '<span class="ecl-link__label">{0}</span> <svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-90"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.svg#ui--corner-arrow"></use></svg>';


// Event name constants

// Regular expression constants

// Global variables

// ** Script body ** //

/*
 * Fetch Re3gistry data
 * 
 * @param {String} uri The uri of the item to retrieve
 * @param {String} lang The language of the data  
 */
function fetchData(uri, lang) {

    if (uri === null || typeof uri === val_undefined || uri.length === 0) {
        uri = uriFromUrl;
    }

    if (lang === null || typeof lang === val_undefined || lang.length === 0) {
        lang = currentLanguage;
    }
    
    var url = new URL(uri);
    var status = url.searchParams.get("status");
    uri = uri.split('?')[0];

    // Show the loading overlay
    showLoadingOverlay(true);

    $.ajax({

        // Base URL of the service taken from the configuration 
        url: registryApp.dataServiceURL,
        data: {uri: uri, lang: lang, format: key_jsonc, status: status}

    }).done(function (responseData) {

        // Rendering the HTML
        renderData(responseData);

        // Hide the loading overlay
        showLoadingOverlay(false);

    }).fail(function (data) {

        // Error handler
        renderFetchError(data.responseJSON);

        // Hide the loading overlay
        showLoadingOverlay(false);
    });
}

/*
 * Render the succesful response from the service
 * 
 * @param {Json} data The Re3gistry json data 
 */
function renderData(data) {

    // Getting the container element
    let mainContainer = $('#' + elementId_mainContainer);

    // Clearing the conatiner
    mainContainer.empty();

    if (data.error) {

        // If there is an error on the service respose, rendering the error
        renderServiceError(data);

    } else {

        // Rendering the HMTL
        handleBreadcrumbs(data);
        renderSiteIdentity(data);
        mainContainer.append(renderProperties(data));
        mainContainer.append(renderDate(data));
        mainContainer.append(renderFormats(data));
//        mainContainer.append(renderHr(key_marginVL));
        let containedItems = data.containedItems;
        if (containedItems !== null && typeof containedItems !== val_undefined && containedItems.length > 0) {
            mainContainer.append(renderCollections(data));
        } else {
            mainContainer.append(renderNarrowers(data));
        }

        $('.ecl-table').DataTable({
            'dom': '<"top-tools ecl-container"lif>tp',
            'scrollX': true,
            'bAutoWidth': false,
            "oLanguage": {
                "sSearch": "Filter: "
            },
            'drawCallback': function () {
                enhanceTables();
            }
        });

    }
}

/*
 * Render the error response from the service
 * 
 * @param {Json} data The Re3gistry json data 
 */
function renderFetchError(data) {
    // Getting the container element
    let mainContainer = $('#' + elementId_mainContainer);

    // Clearing the conatiner
    mainContainer.empty();

    if (data) {
        mainContainer.append(htmlSnippet_errorMessage.replace('{0}', i18n[key_errorPrefix + data.error.code]));
    } else {
        mainContainer.append(htmlSnippet_errorMessage.replace('{0}', i18n[key_genericError]));
    }

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

    let htmlOutput = val_emptyString;

    htmlOutput += htmlSnippet_errorMessage.replace('{0}', i18n[key_errorPrefix + data.error.code]);

    mainContainer.append(htmlOutput);

    // Initializing the ECL Message component after creating it
    let elt = document.querySelector('[' + key_dataEclMessage + ']');
    let message = new ECL.Message(elt);
    message.init();
}

/*
 * Render the URI field
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML string of the URI  
 */
function renderUri(data) {

    return renderField(i18n[key_uri], renderHref(data.uri, data.uri));
}

/*
 * Render the version(s) information of an element (including eventual history)
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML string of the version(s)
 */
function renderVersionInfo(data) {

    let htmlOutput = val_emptyString;

    // The version info are only available for elements of type 'item'
    if (data.type === val_itemTypeItem) {

        // Getting version info from the data
        let version = data.version;
        let versionHistory = data.versionHistory;

        // Rendering info of current version
        htmlOutput += renderField(i18n[key_thisVersion], renderHref(version.uri, version.uri));

        // Checking if the version history info are available
        if (typeof versionHistory !== 'undefined' && versionHistory !== null && versionHistory.length > 0) {

            // Sorting the version history descending
            versionHistory.sort(function (a, b) {
                return sortArray(a.number, b.number, key_descOrdering);
            });

            // Preparing the list of values
            let listValues = [];
            $.each(versionHistory, function (index, item) {

                // Creating the object to be processed by the 
                // renderFieldListValues method
                let tmpObject = {};
                tmpObject.value = item.uri;
                tmpObject.href = item.uri;
                listValues.push(tmpObject);
            });

            // Rendering the field list
            htmlOutput += renderFieldListValues(i18n[key_versionHistory], listValues);
        }
    }

    return htmlOutput;
}

/*
 * Render the properties of the current element
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML string of the properties
 */
function renderProperties(data) {

    let htmlOutput = val_emptyString;

    // Sorting the properties (fields) by 'order' ascending
    data.properties.sort(function (a, b) {
        return sortArray(a.order, b.order, key_ascOrdering);
    });

    // Rendering URI field
    htmlOutput += renderUri(data);

    // Rendering version info
    htmlOutput += renderVersionInfo(data);

    // Render each properties
    $.each(data.properties, function (index, item) {

        let values = item.values;

        let tmpHtml = val_emptyString;

        if (values.length > 1) {
            htmlOutput += renderFieldListValues(item.label, values);
        } else if (values.length == 1) {
            let value = values[0].value;
            let href = values[0].href;

            tmpHtml = (href !== null && href !== val_emptyString) ? renderHref(value, href) : value;
            htmlOutput += renderField(item.label, tmpHtml);

            // If the property is the title, updating the page title.
            if (item.istitle !== null && item.istitle === val_true) {
                updatePageTitle(tmpHtml);
            }
        }
    });

    htmlOutput = renderDl(htmlOutput);

//    htmlOutput += renderHr(key_marginVL);

    return htmlOutput;
}

/*
 * Render the formats of the current element
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML string of the properties
 */
function renderFormats(data) {

    let href;
    let hrefRor;
    let htmlOutput = val_emptyString;

    if (data.latest == true) {
        href = data.uri + "/" + data.localid + "." + currentLanguage + ".";
        hrefRor = data.uri + "/" + data.localid + ".";
    } else {
        if (typeof data.version !== 'undefined') {
            href = data.uri + ":" + data.version.number + "/" + data.localid + "." + currentLanguage + ".";
            hrefRor = data.uri + ":" + data.version.number + "/" + data.localid + ".";
        }
    }

    if (typeof href !== 'undefined') {
//        href = encodeURIComponent(href);

        let htmlInner = val_emptyString;
        htmlInner += renderHrefFormat("XML Registry", href + "xml");
        htmlInner += renderHrefFormat("XML ISO 19135", href + "iso19135xml");
        htmlInner += renderHrefFormat("RDF/XML", href + "rdf");
        htmlInner += renderHrefFormat("JSON", href + "json");

        let containedItems = data.containedItems;
        let narrowerItems = data.narrower;
        if (containedItems !== null && typeof containedItems !== val_undefined && containedItems.length > 0) {
            htmlInner += renderHrefFormat("CSV", href + "csv");
        } else if (narrowerItems !== null && typeof narrowerItems !== val_undefined && narrowerItems.length > 0) {
            htmlInner += renderHrefFormat("CSV", href + "csv");
        }

        htmlInner += renderHrefFormat("ATOM", href + "atom");
        htmlInner += renderHrefFormat("ROR", hrefRor + "ror");

        //TODO to be completed
        htmlOutput += renderFieldFormat(i18n[key_otherFormats], htmlInner);
    }
    return htmlOutput;
}

/*
 * Render the date of the current element
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML string of the properties
 */
function renderDate(data) {

    let htmlOutput = val_emptyString;

    if (data.insertdate) {
        htmlOutput += renderField(i18n[key_insertDate], data.insertdate);
    }
    if (data.editdate) {
        htmlOutput += renderField(i18n[key_editDate], data.editdate);
    }

    return htmlOutput;
}

/*
 * Render the collection related to the current element
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML of the collections   
 */
function renderCollections(data) {

    let htmlOutput = val_emptyString;
    let tHead = val_emptyString;

    let containedItems = data.containedItems;

    // If the current element has Available items
    if (containedItems !== null && typeof containedItems !== val_undefined && containedItems.length > 0) {

        // Rendering the header
        htmlOutput += renderHx(i18n[key_collectionTitle], 3, key_marginT3xl);

        // The table header is rendered with the first element (the fields are
        // the same for all the elements)
        let max = -Infinity;
        let index = -1;
        containedItems.forEach(function (a, i) {
            if (a.properties.length > max) {
                max = a.properties.length;
                index = i;
            }
        });
        var headerProperties = [];
        $.each(containedItems[index].properties, function (index1, propertie) {
            if (propertie.tablevisible !== null && propertie.tablevisible === val_true) {
                headerProperties.push(propertie);
            }
        });

        tHead = renderTableHeader(headerProperties);

        // The HTML of each contained item is rendered
        $.each(containedItems, function (index, item) {

            htmlOutput += renderTableProperties(item, headerProperties);
        });

        // The HTML of the table is rendered
        htmlOutput = renderTable(htmlOutput, tHead);
    }

    return htmlOutput;
}

/*
 * Render the narrower related to the current element
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML of the narrowers   
 */
function renderNarrowers(data) {

    let htmlOutput = val_emptyString;
    let tHead = val_emptyString;

    let narrowerItems = data.narrower;

    // If the current element has narrower items
    if (narrowerItems !== null && typeof narrowerItems !== val_undefined && narrowerItems.length > 0) {

        // Rendering the header
        htmlOutput += renderHx(i18n[key_narrowerTitle], 3, key_marginT3xl);

        // The table header is rendered with the first element (the fields are
        // the same for all the elements)
        let max = -Infinity;
        let index = -1;
        narrowerItems.forEach(function (a, i) {
            if (a.properties.length > max) {
                max = a.properties.length;
                index = i;
            }
        });
        var headerProperties = [];
        $.each(narrowerItems[index].properties, function (index1, propertie) {
            if (propertie.tablevisible !== null && propertie.tablevisible === val_true) {
                headerProperties.push(propertie);
            }
        });

        tHead = renderTableHeader(headerProperties);

        // The HTML of each narrower item is rendered
        $.each(narrowerItems, function (index, item) {

            htmlOutput += renderTableProperties(item, headerProperties);
        });

        // The HTML of the table is rendered
        htmlOutput = renderTable(htmlOutput, tHead);
    }

    return htmlOutput;
}

/*
 * Render the label and value of the field in HTML
 * 
 * @param {String} label The label of the field
 * @param {String} value The value of the field
 * @returns {String} The rendered HTML of the field  
 */
function renderField(label, value) {
    if (label.toLowerCase() === 'label') {
        return htmlSnippet_field.replace('{0}', label).replace('{1}', value).replace('{2}', 'ecl-u-type-bold');
    } else {
        return htmlSnippet_field.replace('{0}', label).replace('{1}', value).replace('{2}', '');
    }
}
function renderFieldFormat(label, value) {
    return htmlSnippet_field_format.replace('{0}', label).replace('{1}', value).replace('{2}', '');
}

/*
 * Render the label and values (list) of the field in HTML
 * 
 * @param {String} label The label of the field
 * @param {String} values The values (list) of the field
 * @returns {String} The rendered HTML of the field
 */
function renderFieldListValues(label, values) {

    let htmlOutput = val_emptyString;

    // Render the HTML of each element of the values list
    htmlOutput = renderList(values);

    // Renred the field and the list values processed before
    return renderField(label, htmlOutput);
}

/*
 * Render the label and values (list) of the field in HTML
 * 
 * @param {String} label The label of the field
 * @param {String} values The values (list) of the field
 * @returns {String} The rendered HTML of the field
 */
function renderList(values) {

    let htmlOutput = val_emptyString;

    // Render the HTML of each element of the values list
    $.each(values, function (index, item) {

        let value = item.value;
        let href = item.href;
        let tmpOutput = val_emptyString;

        tmpOutput = (href !== null && href !== val_emptyString) ? renderHref(value, href) : value;

        htmlOutput += htmlSnippet_li.replace('{0}', tmpOutput);
    });

    // Render the list container
    return htmlSnippet_ul.replace('{0}', htmlOutput);
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
 * Render the href of the field in HTML
 * 
 * @param {String} value The value of the field
 * @param {String} href The href of the field
 * @returns {String} The rendered href HTML element of the field  
 * 
 */
function renderHrefExternalLink(value, href) {

    return htmlSnippet_href_external_link.replace('{0}', href).replace('{1}', value);
}

/*
 * Render the href of the field in HTML
 * 
 * @param {String} value The value of the field
 * @param {String} href The href of the field
 * @returns {String} The rendered href HTML element of the field  
 * 
 */
function renderHrefFormat(value, href) {

    return htmlSnippet_href_format.replace('{0}', href).replace('{1}', value);
}


/*
 * Render table properties
 * 
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML of the table properties
 */
function renderTableProperties(data, headerProperties) {
    let htmlOutput = val_emptyString;

    if (data.properties !== null && data.properties.length > 0) {

        // Sorting the fields of the current element
        data.properties.sort(function (a, b) {
            return sortArray(a.order, b.order, key_ascOrdering);
        });

        $.each(headerProperties, function (index1, propertie) {
            if (propertie.tablevisible !== null && propertie.tablevisible === val_true) {

                // Rendering the HTML of each field for the table
                let valueAdded = false;
                $.each(data.properties, function (index, item) {
                    if ((item.id === propertie.id) && !valueAdded) {
                        if (item.tablevisible !== null && item.tablevisible === val_true) {
                            let values = item.values;
                            let value = val_emptyString;

                            if (values.length > 1) {
                                value = renderList(values);
                            } else if (values.length == 1) {
                                let tmpValue = values[0].value;
                                let href = values[0].href;

                                // In the table view, in case the field is a 'titlefield' the href
                                // is added, in order to enable the link to the related element 
                                // directly from the table
                                if (item.istitle !== null && item.istitle === val_true) {

                                    let windowlocation = window.location.href.replace("http://", "").replace("https://", "");
                                    let indexSlash = windowlocation.indexOf("/");
                                    let contain = windowlocation.substring(0, indexSlash);

                                    if (data.uri.includes(contain)) {
                                          if(data.properties[2].values[0].value.toLowerCase() === "valid"){
                                            value = renderHref(tmpValue, data.uri);
                                          } else {
                                            value = renderHref(tmpValue, data.uri + "?status=" + data.properties[2].values[0].value.toLowerCase());
                                          }   
                                    } else {
                                        value = renderHrefExternalLink(tmpValue, data.uri);
                                    }

                                } else {
                                    value = (href !== null && href !== val_emptyString) ? renderHref(tmpValue, href) : tmpValue;
                                }
                            }

                            // Rendering the HTML of the td
                            htmlOutput += renderTd(value, item.label);
                            valueAdded = true;
                        }
                    }
                });
                if (!valueAdded) {
                    // Rendering the HTML of the td
                    let value = val_emptyString;
                    htmlOutput += renderTd(value, propertie.label);
                }
            }
        });

        // Rendering the HTML of the tr
        htmlOutput = renderTr(htmlOutput);
    }

    return htmlOutput;
}

/*
 * Render the HTML of a td
 * 
 * @param {String} value The value to be put inside the td
 * @returns {String} The rendered HTML of the td
 */
function renderTd(value, field) {

    return htmlSnippet_td.replace('{0}', value).replace('{1}', field);
}

/*
 * Render the HTML of a th
 * 
 * @param {String} value The value to be put inside the th
 * @returns {String} The rendered HTML of the th
 */
function renderTh(value) {

    return htmlSnippet_th.replace('{0}', value);
}

/*
 * Render the HTML of a tr
 * 
 * @param {String} value The value to be put inside the tr
 * @returns {String} The rendered HTML of the tr
 */
function renderTr(value) {

    return htmlSnippet_tr.replace('{0}', value);
}

/*
 * Render the HTML of the table header
 * 
 * @param {Json} The Json object representing the properties
 * @returns {String} The rendered HTML of the table head
 */
function renderTableHeader(properties) {

    // Sorting the fields ascending
    properties.sort(function (a, b) {
        return sortArray(a.order, b.order, key_ascOrdering);
    });

    // Rendering the table head
    let htmlOutput = val_emptyString;
    $.each(properties, function (index, item) {
        if (item.tablevisible !== null && item.tablevisible === val_true) {
            htmlOutput += renderTh(item.label, val_emptyString);
        }
    });

    // Rendering the HTML of the tr
    return renderTr(htmlOutput);
}

/*
 * Render the HTML of a table
 * 
 * @param {String} value The value to be put inside the table
 * @returns {String} The rendered HTML of the table
 */
function renderTable(value, thead) {
    return htmlSnippet_table.replace('{0}', thead).replace('{1}', value);
}

function enhanceTables() {

    /** Customize Pagination **/

    let dtPaginate = $('.dataTables_paginate');

    // Remove all classes
    dtPaginate.removeClass();
    dtPaginate.addClass('ecl-u-mt-l ecl-pagination');

    let paginationLi = $('<li class="ecl-pagination__item"></li>');
    let paginationLiCurrent = $('<li class="ecl-pagination__item ecl-pagination__item--current"></li>');

    // Pagination previous
    let dtPagination_previous = $('.paginate_button.previous');
    let prevClasses = dtPagination_previous.attr('class');
    dtPagination_previous.removeClass();
    dtPagination_previous.addClass('ecl-pagination__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-before');
    dtPagination_previous.text(val_emptyString);
    dtPagination_previous.append(htmlSnippet_paginationPreviousIcon.replace('{0}', i18n[key_paginationPrevious]));
    if (prevClasses.indexOf('disabled') >= 0) {
        dtPagination_previous.hide();
    }
    dtPagination_previous.wrap(paginationLi);

    // Pagination next
    let dtPagination_next = $('.paginate_button.next');
    let nextClasses = dtPagination_next.attr('class');
    dtPagination_next.removeClass();
    dtPagination_next.addClass('ecl-pagination__link ecl-link ecl-link--standalone ecl-link--icon ecl-link--icon-after');
    dtPagination_next.text(val_emptyString);
    dtPagination_next.append(htmlSnippet_paginationNextIcon.replace('{0}', i18n[key_paginationNext]));
    if (nextClasses.indexOf('disabled') >= 0) {
        dtPagination_next.hide();
    }
    dtPagination_next.wrap(paginationLi);

    let pages = $('.ecl-pagination span a, .ecl-pagination span span');
    pages.each(function (i) {
        let tmpEl = $(this);
        let classes = tmpEl.attr('class');

        tmpEl.removeClass();
        tmpEl.addClass('ecl-pagination__link ecl-link ecl-link--standalone');
        if (classes.indexOf('current') >= 0) {
            tmpEl.wrap(paginationLiCurrent);
        } else {
            tmpEl.wrap(paginationLi);
        }
    });

    let liPaginations = $('.ecl-pagination').find('li');

    liPaginations.wrapAll('<ul class="ecl-pagination__list">');


    /** Customize item per page selector **/
    let itemPerpage = $('.dataTables_length label');
    //itemPerpage.addClass('ecl-form-label');

    /** Customize item show info **/
    let itemPageInfo = $('.dataTables_info');
    //itemPageInfo.addClass('ecl-form-label');

    /** Customize item filter **/
    let itemFilter = $('.dataTables_filter label');
    //itemFilter.addClass('ecl-form-label');

    // adding hr
    let topTools = $('.dataTables_scroll');
    topTools.find('hr').remove();
    topTools.prepend(renderHr(key_marginVL));


    // ECL grid
    let toolsTableToEcl = $('.top-tools div').not('.ecl-row');

    toolsTableToEcl.each(function () {
        let tmpEl = $(this);
        tmpEl.removeClass();
        tmpEl.addClass('ecl-col-4 ecl-form-label');
    });

    if ($('.top-tools .ecl-row').length == 0) {
        toolsTableToEcl.wrapAll('<div class="ecl-row">');
    }
}

/*
 * Render the Heading
 * 
 * @param {String} string The value to be put inside the Hx
 * @param {Integer} hLevel The level of the html heading (1 to 6)
 * @returns {String} The rendered HTML of the Hx
 */
function renderHx(string, hLevel, additionalClasses) {
    let htmlOutput = val_emptyString;
    if (additionalClasses !== null && typeof additionalClasses !== val_undefined && additionalClasses !== val_emptyString) {
        htmlOutput = htmlSnippet_hx.replace('{2}', string).replace('{1}', ' ' + additionalClasses).split('{0}').join(hLevel);
    } else {
        htmlOutput = htmlSnippet_hx.replace('{2}', string).replace('{1}', '').split('{0}').join(hLevel);
    }
    return htmlOutput;
}

/*
 * Render the HR
 * 
 * @returns {String} The rendered HTML of the HR
 */
function renderHr(classString) {
    return htmlSnippet_hr.replace('{0}', classString);
}

/*
 * Render the LI
 * 
 * @returns {String} The rendered HTML of the LI
 */
function renderLi(string) {
    return htmlSnippet_li.replace('{0}', string);
}

/*
 * Update the page title
 * 
 * @param {String} string The title of the page
 */
function updatePageTitle(string) {

    let pageTitle = $(elementName_title);
    let bodyTitle = $('#' + elementId_bodyTitle);

    pageTitle.text(string + ' - ' + i18n[key_mainAppTitle]);
    bodyTitle.text(string);
}

/*
 * It creates/updates the breadcrumb in the page
 * 
 * @param {String} string The data received from the service
 */
function handleBreadcrumbs(data) {

    let breadcrumbOl = $('.' + elementClassName_breadcrumb);

    // Creating the base breadcrumb (only the first time)
    if (baseBreadcrumb === val_emptyString) {
        baseBreadcrumb = breadcrumbOl.html();
    }

    // If it is a registry, just put the registry ending breadcrumb,
    // otherwhise add the registry link
    let liHtml = val_emptyString;
    liHtml = renderRegistryBreadcrumb(data);
    breadcrumbOl.html(baseBreadcrumb);
    breadcrumbOl.append(liHtml);
}

/*
 * This functions gets the title field from a list of properties
 * 
 * @param {String} string The data related to the object containing the fields
 * @returns {String} The value of the title field 
 */
function getTitleField(data) {

    let titleValue = val_emptyString;

    $.each(data.properties, function (index, item) {

        // If the property is the title
        if (item.istitle !== null && item.istitle === val_true) {
            let values = item.values;

            titleValue = values[0].value;
            return false;
        }
    });

    return titleValue;
}

/*
 * It renders the last element of the breadcrumb
 * 
 * @param {String} value The value to be put in the breadcrumb element
 * @returns {String} The formatted html of the breadcrubm element 
 */
function renderBreadcrumbLastElement(value) {
    return htmlSnippet_breadcrumbLastElement.replace('{0}', value)
}

/*
 * It renders the element of the breadcrumb including the link
 * 
 * @param {String} value The value to be put in the breadcrumb element
 * @param {String} href The href to be put in the breadcrumb element
 * @returns {String} The formatted html of the breadcrubm element 
 */
function renderBreadcrumbLink(value, href) {
    return htmlSnippet_breadcrumbLink.replace('{0}', href).replace('{1}', value);
}

/*
 * It renders the registry element of the breadcrumb. If available, it renders
 * subsequently also the other elements
 * 
 * @param {Json Object} data The data of the json object returned by the service
 * @returns {String} The formatted html of the breadcrubm element 
 */
function renderRegistryBreadcrumb(data) {
    // If it is a registyr, just put the registry ending breadcrumb,
    // otherwhise add the registry link and render the register breadcrumbs
    let liHtml = val_emptyString;
    if (!data.registry) {
    } else if (!data.register) {
        liHtml = renderBreadcrumbLink(getTitleField(data.registry), data.registry.uri);
        liHtml += renderBreadcrumbLastElement(getTitleField(data));
    } else {
        liHtml = renderBreadcrumbLink(getTitleField(data.registry), data.registry.uri);
        liHtml += renderRegisterBreadcrumb(data);
    }
    return liHtml;
}

/*
 * It renders the register element of the breadcrumb. If available, it renders
 * subsequently also the other elements
 * 
 * @param {Json Object} data The data of the json object returned by the service
 * @returns {String} The formatted html of the breadcrubm element 
 */
function renderRegisterBreadcrumb(data) {
    // If it is a registey, just put the register ending breadcrumb,
    // otherwhise add the register link and render the subsequent items breadcrumbs
    let liHtml = val_emptyString;
    if (!data.registry || (data.register && data.type === val_itemTypeRegister)) {
        liHtml = renderBreadcrumbLastElement(getTitleField(data));
    } else {
        liHtml = renderBreadcrumbLink(getTitleField(data.register), data.register.uri);
        liHtml += renderItemsBreadcrumb(data);
    }
    return liHtml;
}

/*
 * It renders the item element of the breadcrumb. It recursively renders all 
 * the elements in the path of the URI of the element
 * 
 * @param {Json Object} data The data of the json object returned by the service
 * @returns {String} The formatted html of the breadcrubm element 
 */
function renderItemsBreadcrumb(data) {

    let liHtml = val_emptyString;

    let currentItemUri = data.uri;
    let registerUri = data.register.uri;

    let tmpCheck = currentItemUri.replace(registerUri, '');
    let checkArray = tmpCheck.split('/');

    if (checkArray.length > 2) {

        liHtml += generateBreadcrumbTrail(data);

    } else {

        liHtml += renderBreadcrumbLastElement(getTitleField(data));

    }
    return liHtml;
}

/*
 * It generates the breadcrumb trails of the URI of the current element
 * excluding the ones already processed
 * 
 * @param {Json Object} data The data of the json object returned by the service
 * @returns {String} The formatted html of the breadcrubm trail 
 */
function generateBreadcrumbTrail(data) {

    let currentUri = data.uri;
    let myBreadCrumbElements = [];
    let myBreadCrumbElementsURL = [];

    //Rest of items
    let topconcepts = true;
    let currentLevel = data;
    while(topconcepts){
        try {
            myBreadCrumbElements.push(currentLevel.topConceptOf.values[0].value); 
            myBreadCrumbElementsURL.push(currentLevel.topConceptOf.uri)
            currentLevel = currentLevel.topConceptOf;
        } catch (error) {
            topconcepts = false;
        }
    }

    myBreadCrumbElements.reverse();
    myBreadCrumbElementsURL.reverse();
    let liHtml = val_emptyString;
    for(let i= 0 ; i<myBreadCrumbElements.length; i++){
        liHtml += renderBreadcrumbLink(myBreadCrumbElements[i], myBreadCrumbElementsURL[i]);
    }

    // Rendering the last element of the breadcrumb (current element)
    liHtml += renderBreadcrumbLastElement(getTitleField(data));

    return liHtml;
}

/*
 * It get the uriname from the last path of the URL
 * 
 * @param {String} url The url to be parsed
 * @returns {String} The uriname of the last path of the URL
 */
function getUriname(url) {
    let i = url.lastIndexOf('/');
    url = url.substring(i + 1);
    return url;
}

/*
 * It renders the title of the registry
 * 
 * @param {String} data The data returned by the service
 */
function renderSiteIdentity(data) {
    let siteIdentity = $('.' + elementClassName_siteIdentity);
    if (!data.registry) {
        siteIdentity.html(getTitleField(data));
    } else {
        siteIdentity.html(getTitleField(data.registry));
    }
}

/*
 * It renders the Dl
 * 
 * @param {String} string The data for the Dl
 */
function renderDl(string) {
    return htmlSnippet_dl.replace('{0}', string);
}