"use strict";
/// *** Core app scripts ***///

// ** Constants and variables definition ** //

// Element name constants
const elementId_mainContainer = 'content';
const elementId_bodyTitle = 'page-title';
const elementClassName_breadcrumb = 'breadcrumb__container';
const elementClassName_siteIdentity = 'bd-title';
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
const key_marginVL = 'mx-3';
const key_marginT2xl = 'mt-3';
const key_genericError = 's-error-fetch';
const key_paginationPrevious = 'previous';
const key_paginationNext = 'next';
const key_otherFormats = 's-other-formats';

// Value constants
const val_itemTypeItem = 'item';
const val_itemTypeRegister = 'register';
const val_itemTypeRegistry = 'registry';
// HTML snippet constants
const htmlSnippet_table = '<table class="table table-striped table-bordered" cellspacing="0" width="100%"><thead>{0}</thead><tbody>{1}</tbody></table>';
const htmlSnippet_th = '<th>{0}</th>';
const htmlSnippet_tr = '<tr>{0}</tr>';
const htmlSnippet_td = '<td>{0}</td>';
const htmlSnippet_ul = '<ul class="list_nobullet">{0}</ul>';
const htmlSnippet_li = '<li class="ml-2">{0}</li>';
const htmlSnippet_href = '<a href="{0}" class="btn btn-link linktable">{1}</a>';
const htmlSnippet_href_format = '<a href="{0}" class="mr-2"><i class="far fa-file-alt"></i> {1}</a>';
const htmlSnippet_field = '<div class="row my-2"><div class="col-3 font-weight-bold">{0}</div><div class="col-9">{1}</div></div>';
//const htmlSnippet_field = '<dt class="ecl-description-list__term">{0}</dt><dd class="ecl-description-list__definition">{1}</dd>';
const htmlSnippet_hx = '<h{0} class="ecl-u-type-heading-{0}{1}">{2}</h{0}>';
const htmlSnippet_hr = '<hr class="{0}" />';
const htmlSnippet_breadcrumbLastElement = '<li class="breadcrumb-item active" aria-current="page" aria-hidden="false">{0}</li>';
const htmlSnippet_breadcrumbLink = '<li class="breadcrumb-item" aria-hidden="false"><a href="{0}" class="btn btn-link p-0" href="' + registryApp.hostURL + '">{1}</a></li>';
const htmlSnippet_dl = '<dl class="ecl-description-list">{0}</dl>';
//const htmlSnippet_paginationPreviousIcon = '<a class="btn btn-link" href="' + registryApp.hostURL + registryApp.staticResourcesPath + '"><i class="fas fa-chevron-right"></i> <span class="ecl-link__label">{0}</span></a>';
//const htmlSnippet_paginationNextIcon = '<a class="btn btn-link" href="' + registryApp.hostURL + registryApp.staticResourcesPath + '"><span class="ecl-link__label">{0}</span></a>';
//        const htmlSnippet_paginationPreviousIcon = '<svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-270"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.a11fa655.svg#ui--corner-arrow"></use></svg> <span class="ecl-link__label">{0}</span>';
//        const htmlSnippet_paginationNextIcon = '<span class="ecl-link__label">{0}</span> <svg focusable="false" aria-hidden="true" class="ecl-link__icon ecl-icon ecl-icon--xs ecl-icon--rotate-90"><use xlink:href="' + registryApp.hostURL + registryApp.staticResourcesPath + 'icons.a11fa655.svg#ui--corner-arrow"></use></svg>';
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

    // Show the loading overlay
    showLoadingOverlay(true);
    $.ajax({

        // Base URL of the service taken from the configuration
        url: registryApp.dataServiceURL,
        data: {uri: uri, lang: lang, format: key_jsonc}

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
        mainContainer.append(renderFormats(data));
        let containedItems = data.containedItems;
        if (containedItems !== null && typeof containedItems !== val_undefined && containedItems.length > 0) {
            mainContainer.append(renderCollections(data));
        } else {
            mainContainer.append(renderNarrowers(data));
        }
        $('.table').DataTable({
            "dom": "<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
                    "<'row'<'col-sm-12'tr>>" +
                    "<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
            "order": [[0, "asc"]],
            "ordering": true,
            "paging": true,
            "searching": true,
            "search": {"smart": true},
            "oLanguage": {
                "sSearch": "Search table:"
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
//    let elt = document.querySelector('[' + key_dataEclMessage + ']');
//    let message = new ECL.Message(elt);
//    message.init();
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
        htmlOutput += renderField(i18n[key_thisVersion], version.uri);
        // Checking if the version history info are available
        if (versionHistory !== null && versionHistory.length > 0) {

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
//                htmlOutput += renderHr(key_marginVL);
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
    if (data.latest == true) {
        href = data.uri + "/" + data.localid + "." + currentLanguage + ".";
    } else {
        href = data.uri + ":" + data.version.number + "/" + data.localid + "." + currentLanguage + ".";
    }

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

    //TODO to be completed
    let htmlOutput = val_emptyString;
    htmlOutput += renderField(i18n[key_otherFormats], htmlInner);

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
    // If the current element has contained items
    if (containedItems !== null && typeof containedItems !== val_undefined && containedItems.length > 0) {

        // Rendering the header
        htmlOutput += renderHx(i18n[key_collectionTitle], 3, key_marginT2xl);
        // The table header is rendered with the first element (the fields are
        // the same for all the elements)
        tHead = renderTableHeader(containedItems[0].properties);
        // The HTML of each contained item is rendered
        $.each(containedItems, function (index, item) {

            htmlOutput += renderTableProperties(item);
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
        htmlOutput += renderHx(i18n[key_narrowerTitle], 3, key_marginT2xl);
        // The table header is rendered with the first element (the fields are
        // the same for all the elements)
        tHead = renderTableHeader(narrowerItems[0].properties);
        // The HTML of each narrower item is rendered
        $.each(narrowerItems, function (index, item) {

            htmlOutput += renderTableProperties(item);
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

    return htmlSnippet_field.replace('{0}', label).replace('{1}', value);
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
function renderHrefFormat(value, href) {

    return htmlSnippet_href_format.replace('{0}', href).replace('{1}', value);
}

/*
 * Render table properties
 *
 * @param {Json} data The Re3gistry json data
 * @returns {String} The rendered HTML of the table properties
 */
function renderTableProperties(data) {

    let htmlOutput = val_emptyString;
    if (data.properties !== null && data.properties.length > 0) {

        // Sorting the fields of the current element
        data.properties.sort(function (a, b) {
            return sortArray(a.order, b.order, key_ascOrdering);
        });
        // Rendering the HTML of each field for the table
        $.each(data.properties, function (index, item) {
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
                        value = renderHref(tmpValue, data.uri);
                    } else {
                        value = (href !== null && href !== val_emptyString) ? renderHref(tmpValue, href) : tmpValue;
                    }
                }

                // Rendering the HTML of the td
                htmlOutput += renderTd(value);
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
function renderTd(value) {

    return htmlSnippet_td.replace('{0}', value);
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

    // If it is a registyr, just put the registry ending breadcrumb,
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
    // Creating the list of additional elements for the breadcrumbs
    let breadcrumbElements = currentUri.split('/');
    breadcrumbElements = breadcrumbElements.reverse();
    let breadcrumbUrls = [];
    let tmpUrl = currentUri;
    $.each(breadcrumbElements, function (index, item) {
        breadcrumbUrls.push(tmpUrl);
        tmpUrl = tmpUrl.replace('/' + item, '');
    });
    let liHtml = val_emptyString;
    breadcrumbUrls.reverse();
    $.each(breadcrumbUrls, function (index, item) {

        // Filtering elements not needed in the breadcrumbs or already
        // processed (like registry and register or the current element)
        if (item !== key_http + ':/' &&
                item !== key_http + ':' &&
                item !== key_https + ':' &&
                item !== key_http + ':/' &&
                item !== data.registry.uri &&
                item !== data.register.uri &&
                item !== data.uri) {

            // Filtering the base URI
            if (data.registry.uri.indexOf(item) < 0 || data.register.uri.indexOf(item) < 0) {

                liHtml += renderBreadcrumbLink(getUriname(item), item);
            }
        }
    });
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