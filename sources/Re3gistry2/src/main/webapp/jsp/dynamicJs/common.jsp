<%@page import="java.util.Date"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page contentType="application/javascript" pageEncoding="UTF-8"%>
<%
    // Loading the system localization (note: it is different from the content localization)
    ResourceBundle localization = (ResourceBundle)request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);  
%>

/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */

<%--<script>--%>
 
// Configurations
var config={
    baseurl:".",
    reloadDelay:5000,
    sessionCheckInterval:30000,
    sessionCheckWarningTreshold:600000
};

// Variable to handle the unsaved data alert
var unsaved = false;
var submitted = false;

// Common "onload" functions
$(document).ready(function(){
    
    // Re3gistry tool language selector handler
    $("#cngl").on("change",function(){        
        location.href=config.baseurl+"<%=WebConstants.PAGE_URINAME_CHANGELOCALE %>?lang="+$(this).val();
    });
    
    // Restrict input for the URI fields on keypress (Just letters, numbers and ._-)
    $('.input-uri').on('keypress',function(e) {
        var chr = String.fromCharCode(e.which);
        var check = /[^A-Z0-9._-]/ig;
        if(check.test(chr) && e.which!="8" && e.which!="0"){
            return false;
        }
    });
    // Restrict input for the URI fields on paste (Just letters, numbers and ._-)
    $('.input-uri').on('paste',function(e) {
        var _this = this;
        setTimeout( function() {
            var str = $(_this).val();
            str = str.replace(/[^A-Z0-9._-]/ig,"")
            $(_this).val(str);
        }, 100);
    });
    
    // Restrict input for the URI fields on keypress
    $('.input-baseuri').on('keypress',function(e) {
        var chr = String.fromCharCode(e.which);
        var check = /[^A-Z0-9._\-\/:]/ig;
        if(check.test(chr) && e.which!="8" && e.which!="0"){
            return false;
        }
    });
    // Restrict input for the URI fields on paste
    $('.input-baseuri').on('paste',function(e) {
        var _this = this;
        setTimeout( function() {
            var str = $(_this).val();
            str = str.replace(/[^A-Z0-9._\-\/:]/ig,"")
            console.log(str);
            $(_this).val(str);
        }, 100);
    });
    
    //Session expiration message
    setInterval(function(){ 
        <%
        HttpSession sessionCheck = request.getSession();
        Date expiry = new Date(session.getLastAccessedTime() + session.getMaxInactiveInterval()*1000);
        %>
        var sessionExpiration = <%= expiry.getTime() %>
        var now = new Date();
        var nowTime = now.getTime()

        if((sessionExpiration - now) < config.sessionCheckWarningTreshold){
            $('#expirationWarning').modal('show');
        }
        console.log(sessionExpiration - now);

    }, config.sessionCheckInterval);
 
});

// Checking the submit action
$(document).on("submit","form",function(e) {
    if(e.isDefaultPrevented()){
        submitted = false;
    }else{
        submitted = true;
    }
});

// Checking the form invalid status
$(document).on("invalid.bs.validator","form",function(e) {   
    $(".form-validation-messages").show();
});

// Checking the form valid status
$(document).on("valid.bs.validator","form",function(e) {
    if($("#editing-form").has(".has-error, .bs-invalid").length==0){
        $(".form-validation-messages").hide();
    }
});

// Handling the page before unload action
$(window).on('beforeunload', function(){
    return unloadCheck();
});

// Handling the data not saved to avoid data loss
$(document).on("change keyup paste","#editing-form :input:not(.nochange)",function(e){
    unsaved = true;
    $('#save-clarification').prop( "disabled", false );
});
CKEDITOR.on("instanceReady", function(ev) {
  var editor = ev.editor;
  editor.on('change', function(e) {
    unsaved = true;  
    $('#save-clarification').prop( "disabled", false );
  });
});

// Function to raise a message in case of unsaved data
function unloadCheck(){
    if(unsaved && !submitted){
    return '<%=localization.getString("label.js.customcomfirmexit") %>';
    }
}

// Datatables init functions
function loadAndRender(itemUuid,languageUuid,obj,isRegItem){
    
    // Preparing the loading window
    if($("#AJtab").height()>$(".loader_c").height()){
        $(".loader_c").height($("#AJtab").height());
    }
    $(".loader_c").show();
    
    var loadurl;
    if(isRegItem){
        loadurl = config.baseurl+"<%=WebConstants.PAGE_URINAME_BROWSELOADER %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + itemUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID %>=" + languageUuid;
    }else{
        loadurl = config.baseurl+"<%=WebConstants.PAGE_URINAME_BROWSELOADERNEWPROPOSAL %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + itemUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID %>=" + languageUuid;
    }
    var targ = $('#AJtab');
    
    //Loading tab data and table data
    $.get(loadurl+'&rn='+(new Date().getTime()), function(data) {
        $(targ).html(data);
        $('.selectpicker').selectpicker('show');
        handleRemoveButtons();
        $(".loader_c").hide();
        
        $('#list-table').DataTable({
            "dom": '<"top">rt<"bottom"lip><"clear">',
            "ajax": {
                url:config.baseurl+"<%=WebConstants.PAGE_URINAME_ITEMLISTLOADER %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + itemUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID %>=" + languageUuid,
                complete:function(data){
                    if(data.responseJSON.languageNotAvailable){
                        $("#langMessage").show();
                    }else{
                        $("#langMessage").hide();
                    }
                }
            },
            "language": {
                "processing": "<div class=\"loader\"></div>"
            },
            "processing": true,
            "serverSide": true,
            "searching": true,
            "search": {"smart": false},
            "searchDelay": 3000,
            "ordering": false
        });
        
        $('#proposed-list-table').DataTable({
            "dom": '<"top">rt<"bottom"lip><"clear">',
            "ajax": {
                url:config.baseurl+"<%=WebConstants.PAGE_URINAME_ITEMPROPOSEDLISTLOADER %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + itemUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID %>=" + languageUuid,
                complete:function(data){
                    if(data.responseJSON.languageNotAvailable){
                        $("#langMessage").show();
                    }else{
                        $("#langMessage").hide();
                    }                    
                    if(data.responseJSON.data.length<1){
                        $("#proposedShow").hide();
                    } 
                }                    
            },
            "language": {
                "processing": "<div class=\"loader\"></div>"
            },
            "processing": true,
            "serverSide": true,
            "ordering": false
        });
        
        $('#children-list-table').DataTable({
            "dom": '<"top">rt<"bottom"lip><"clear">',
            "ajax": {
                url:config.baseurl+"<%=WebConstants.PAGE_URINAME_ITEMCHILDRENLISTLOADER %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + itemUuid + "&<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID %>=" + languageUuid,
                complete:function(data){
                    if(data.responseJSON.languageNotAvailable){
                        $("#langMessage").show();
                    }else{
                        $("#langMessage").hide();
                    }
                    if(data.responseJSON.data.length<1){
                        $("#childrenShow").hide();
                    }                    
                }                    
            },
            "language": {
                "processing": "<div class=\"loader\"></div>"
            },
            "processing": true,
            "serverSide": true,
            "ordering": false
        });
        
        if(obj){
            obj.tab('show');
        }
        CKEDITOR.replaceAll();
        return false;
    });
}

// Replace url parameters
function updateQueryStringParameter(uri, key, value) {
    var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
    var separator = uri.indexOf('?') !== -1 ? "&" : "?";
    if (uri.match(re)) {
      return uri.replace(re, '$1' + key + "=" + value + '$2');
    }
    else {
      return uri + separator + key + "=" + value;
    }
}

// Script to add a field value
function getAddValueString(fieldMappingUuid, domObject){
    $.ajax({
        url: config.baseurl+"<%=WebConstants.PAGE_URINAME_ADDFIELDVALUE %>?<%=BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID %>=" + fieldMappingUuid
    }).done(function(data) {
        domObject.parent().find("ul.field-list").append(data);
        $('#editing-form').validator('update');
        unsaved = true;
    });
}

// Script to add a field relation
function getAddRelationString(fieldMappingUuid, regItemReferenceUuid, domObject){
    var multivalued = domObject.data("<%=WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD %>");
    
    // Preparing the loading animation and disabling the button during the request
    domObject.addClass("loader_small");
    domObject.prop("disabled", true);
    
    // Performing the request
    $.ajax({
        url: config.baseurl+"<%=WebConstants.PAGE_URINAME_ADDFIELDRELATION %>?<%=BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID %>=" + fieldMappingUuid + "&<%=BaseConstants.KEY_REQUEST_ITEMUUID %>="+regItemReferenceUuid
    }).done(function(data) {
        if(domObject.parent().find("ul.field-list").length>0){
            domObject.parent().find("ul.field-list").append(data);
        }else{
            domObject.parent().append(data);
        }
        $('#editing-form').validator('update');
        $('.selectpicker').selectpicker('show');
       
        domObject.removeClass("loader_small");
        domObject.prop("disabled", false);
        if(multivalued!==true){
            domObject.remove();
        }
        
        unsaved = true;
    });
}

// Script to add a parent
function getAddParentString(fieldMappingUuid, regItemReferenceUuid, domObject){
    var multivalued = domObject.data("<%=WebConstants.DATA_PARAMETER_MULTIVALUEDFIELD %>");

    // Preparing the loading animation and disabling the button during the request
    domObject.addClass("loader_small");
    domObject.prop("disabled", true);
    
    // Get the new item inster param
    var newItemInsert = domObject.data("<%=BaseConstants.KEY_REQUEST_NEWITEMINSERT%>");

    // Performing the request
    $.ajax({
        url: config.baseurl+"<%=WebConstants.PAGE_URINAME_ADDFIELDPARENT %>?<%=BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID %>=" + fieldMappingUuid + "&<%=BaseConstants.KEY_REQUEST_ITEMUUID %>="+regItemReferenceUuid+"&<%=BaseConstants.KEY_REQUEST_NEWITEMINSERT%>="+newItemInsert
    }).done(function(data) {       
        if(domObject.parent().find("ul.field-list").length>0){
            domObject.parent().find("ul.field-list").append(data);
        }else{
            domObject.parent().append(data);
        }
        $('#editing-form').validator('update');
        $('.selectpicker').selectpicker('show');

        domObject.removeClass("loader_small");
        domObject.prop("disabled", false);        
        if(multivalued!==true){
            domObject.remove();
        }
        unsaved = true;
    });
}


// Script to remove a field value
function removeValue(object, regLocalizationproposedUuid, isRegItem){
    
    var checkElemId = object.data("<%=WebConstants.DATA_PARAMETER_TEMPID %>");
    var checkElem = $("#<%= WebConstants.DATA_PARAMETER_VLLISTIDPREFIX %>"+checkElemId);
      
    if(!checkElem.hasClass("just-added")){
        $.ajax({
            url: config.baseurl+"<%=WebConstants.PAGE_URINAME_REMOVEFIELDVALUE %>?<%=BaseConstants.KEY_REQUEST_LOCALIZATIONPROPOSEDUUID %>=" + regLocalizationproposedUuid
        }).done(function(data) {
            if(data.trim()=="<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_REMOVE %>"){
                $("#<%=WebConstants.DATA_PARAMETER_VLLISTIDPREFIX %>" + regLocalizationproposedUuid).closest("li").remove();
            }else if(data.trim()=="<%=BaseConstants.KEY_REQUEST_REMOVE_VALUE_TYPE_RELOAD %>"){
                loadAndRender(itemUuid,"${currentLanguage.uuid}",null,isRegItem);
            }else{
                $("#<%=WebConstants.DATA_PARAMETER_VLLISTIDPREFIX %>" + regLocalizationproposedUuid).find("input, textarea").val("");
            }
            $('#editing-form').validator('update');
            handleRemoveButtons();
            if($("#editing-form").has(".has-error").length==0){
                $(".form-validation-messages").hide();
            }
        });
    }else{
        checkElem.parent().remove();
        $('#editing-form').validator('update');
        if($("#editing-form").has(".has-error").length==0){
            $(".form-validation-messages").hide();
        }
    }
}

// Script to restore the original RegRelationProposed (RegLocalization passed by parameter)
function restoreOriginalRegRelationValue(regLocalizationUuid,isRegItem){
    $.ajax({
        url: config.baseurl+"<%=WebConstants.PAGE_URINAME_RESTOREORIGINALRELATION %>?<%=BaseConstants.KEY_REQUEST_REGLOCALIZATIONUUID %>=" + regLocalizationUuid
    }).done(function(data) {
        loadAndRender(itemUuid,"${currentLanguage.uuid}",null,isRegItem);
    });
}

// Script to discard changes RegRelationProposed
function discardChanges(regItemUuid,isRegItem){
    $.ajax({
        url: config.baseurl+"<%=WebConstants.PAGE_URINAME_DISCARDPROPOSAL %>?<%=BaseConstants.KEY_REQUEST_ITEMUUID %>=" + regItemUuid
    }).done(function(data) {
        loadAndRender(itemUuid,"${currentLanguage.uuid}",null,isRegItem);
    });
}

// Handling remove button (checking the required flag and the number of fields available)
function handleRemoveButtons(){
    $(".field-list-required").each(function(){
        var lis = $(this).find("li");
        if(lis.length<2){
            $(this).find(".btn-value-remove").addClass("disabled");
        }else{
            var validValues = 0;
            var input;
            lis.each(function(){
                input = $(this).find("input, textarea")[0];
                if(input){
                    if($(input).val().trim()!=""){
                        validValues++;
                    }else{
                        $(input).prop('required', false);
                        $(this).find(".btn-value-remove").addClass("disabled");
                        $(this).find(".btn-value-remove").addClass("empty");
                        $('#editing-form').validator('update');
                    }
                }
            })
            if(validValues>1){
                $(this).find(".btn-value-remove").not(".empty").removeClass("disabled");
            }else{
                $(input).prop('required', true);
                $(this).find(".btn-value-remove").addClass("disabled");
                $('#editing-form').validator('update');
            }
        }
    });
    
    $(".field-list-required.data-list").each(function(){
        var lis = $(this).find("li.cnt");
        console.log(lis.length);
        if(lis.length<2){
            $(this).find(".btn-relation-remove").addClass("disabled");
        }else{
            $(this).find(".btn-relation-remove").removeClass("disabled");
        }
    });
}