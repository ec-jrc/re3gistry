"use strict";

/// *** App initialization scripts ***///

// ** Events handlers ** //

// ** Ready init handlers ** //
$(document).ready(function () {
    
    // Processing the URI
    processUri();
    
    //Fill langauge selector on init
    fillLanguageTable();

    // Initialization of the localization system
    initLocalization();
    
    // Binding common events
    bindCommonEvents();
});


