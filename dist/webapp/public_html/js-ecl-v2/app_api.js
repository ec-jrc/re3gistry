"use strict";

const domainURL = registryApp.domainURL;
const link_ecl_unordered_list = 'link-ecl-unordered-list';


$(document).ready(function () {

    $(".link-ecl-unordered-list").each(function () {
        var listtext = $(this).text();
        listtext = listtext.replaceAll("_registryApp_domainURL_", domainURL);
        $(this).text(listtext);


        var href = $(this).attr('href');
        href = listtext.replaceAll("_registryApp_domainURL_", domainURL);
        $(this).attr("href", href);
    });

});