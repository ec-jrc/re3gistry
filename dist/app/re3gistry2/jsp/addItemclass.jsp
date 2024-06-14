<%-- 
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
--%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclasstype"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelation"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelationpredicate"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.ItemHelper"%>
<%@page import="javax.persistence.NoResultException"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldmapping"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>
<%    // Setting system localization
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    Properties properties = configuration.getProperties();

    boolean allowMultiRegistry = Boolean.valueOf((String) properties.getProperty("application.multiregistry.allow"));

    // Initializing managers
    RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);

    // Getting objects from the request
    List<RegItemclasstype> regItemclasstypes = (List<RegItemclasstype>) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSTYPES);
    List<RegItemclass> regItemclasses = (List<RegItemclass>) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES);
    RegItemclass parentRegItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_PARENTREGITEMCLASS);

    String operationSuccess = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionRegisterRegistry = {BaseConstants.KEY_USER_ACTION_MANAGEREGISTERREGISTRY};
    boolean permissionRegisterRegistry = UserHelper.checkGenericAction(actionRegisterRegistry, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container registry">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("addItemclass.label.title")}</h1>
                </div>
            </div>

            <%-- Editing form --%>
            <form id="editing-form" method="post" accept-charset="utf-8" role="form">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>

                <%-- Showing the list of itemclass types --%>    
                <div class="row form-group editing-labels mb-3">
                    <div class="col-sm-4"><label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID%>">${localization.getString("label.itemclasstype")}</label></div>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select class="selectpicker form-control" id="itemclass_type" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSTYPEUUID%>" required>
                                <option value=""></option> 
                                <%
                                    String itemclassType = null;
                                    if (parentRegItemclass != null) {
                                        if (parentRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                                            itemclassType = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;
                                        } else if (parentRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                                            itemclassType = BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER;
                                        } else if (parentRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                                            itemclassType = BaseConstants.KEY_ITEMCLASS_TYPE_ITEM;
                                        }
                                    }
                                    for (RegItemclasstype tmpRegItemclasstype : regItemclasstypes) {
                                        if (tmpRegItemclasstype.getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM) || (tmpRegItemclasstype.getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY) && allowMultiRegistry)) {
                                %><option data-<%=WebConstants.DATA_PARAMETER_ITEMCLASSLOCALID%>="<%=tmpRegItemclasstype.getLocalid()%>" value="<%=tmpRegItemclasstype.getUuid()%>"<%=(parentRegItemclass != null && (itemclassType != null && itemclassType.equals(tmpRegItemclasstype.getLocalid())) ? " selected=\"selected\"" : "")%>><%=tmpRegItemclasstype.getLocalid()%></option><%
                                        }
                                    }
                                %>
                            </select>
                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>     
                </div>

                <%-- Showing the list of itemclass --%>         
                <div class="row form-group editing-labels">
                    <div class="col-sm-4"><label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID%>">${localization.getString("label.parentitemclass")}</label></div>
                    <div class="col-sm-8">
                        <div class="input-cnt">
                            <select class="selectpicker form-control" id="parent_itemclass" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_PARENTITEMCLASSUUID%>" disabled="disabled">
                                <option value="" data-type=""></option>
                                <%
                                    for (RegItemclass tmpRegItemclass : regItemclasses) {
                                        List<RegItemclass> check = regItemclassManager.getChildItemclass(tmpRegItemclass);

                                        RegItemclass tmp = tmpRegItemclass.getRegItemclassParent();
                                        List<String> baseuri = new ArrayList();
                                        int i = 0;
                                        while (tmp != null) {
                                            if (tmp.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                                                baseuri.add(i, "[item-local-id]");
                                            } else if (tmp.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                                                baseuri.add(i, tmp.getLocalid());
                                            } else {
                                                baseuri.add(i, tmp.getBaseuri());
                                            }
                                            tmp = tmp.getRegItemclassParent();
                                            i++;
                                        }

                                        String baseUriStr = "";
                                        for (int j = baseuri.size(); j > 0; j--) {
                                            baseUriStr += baseuri.get(j - 1) + ((j == 0) ? "" : "/");
                                        }

                                        if (tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {
                                            baseUriStr = baseUriStr + "/[item-local-id]";
                                        }

                                %><option data-baseuri="<%=(tmpRegItemclass.getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) ? baseUriStr : tmpRegItemclass.getBaseuri()%>" data-localid="<%=tmpRegItemclass.getLocalid()%>" data-haschild="<%=(check.isEmpty()) ? "false" : "true"%>" value="<%=tmpRegItemclass.getUuid()%>" data-type="<%=tmpRegItemclass.getRegItemclasstype().getLocalid()%>"<%=(parentRegItemclass != null && parentRegItemclass.getLocalid().equals(tmpRegItemclass.getLocalid()) ? " selected=\"selected\"" : "")%>><%=tmpRegItemclass.getLocalid()%> [<%=tmpRegItemclass.getRegItemclasstype().getLocalid()%>]</option><%
                                    }
                                %>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4" for="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>">${localization.getString("label.baseuri")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input type="text" id="base_uri" class="form-control input-baseuri" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>" value="" required maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>"/>
                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" class="col-sm-4">${localization.getString("label.contentclasslocalid")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input id="localid" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" data-toggle="tooltip" data-placement="top" title="<%=localization.getString("label.fielduriinfo")%>" type="text" class="form-control input-uri" value="" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" required/>
                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>
                </div>

                <hr/>

                <%--<div class="row editing-labels">
                    <div class="form-group">
                        <div class="col-sm-4"><label for="<%=BaseConstants.KEY_FORM_FIELD_NAME_BASEURI%>">URI preview</label></div>
                        <div class="col-sm-8">
                            <span id="uripreview"></span>
                        </div>
                    </div>
                </div>
                
                <hr/>--%>



                <% if (operationSuccess != null) {%>
                <div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <%=operationSuccess%>
                </div>
                <% } %>

                <%-- If the user has the rights to insert a new RegItemclass, showing 
                the save button --%>
                <% if (permissionRegisterRegistry) {%>
                <div class="row">
                    <div class="col-sm-6"></div>
                    <div class="col-sm-3">
                        <a role="button" class="btn btn-secondary width100" href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS%>"><i class="fas fa-ban"></i> ${localization.getString("label.cancel")}</a>
                    </div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
                    </div>
                </div>
                <% }%>

            </form>     

            <%-- Handling the various itemclasstypes/icemclass relation and constraints --%> 
            <script>
                $('#editing-form').on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        submitted = false;
                    } else {
                        itemclass_t = $('#itemclass_type');
                        p_itemclass = $('#parent_itemclass');
                        itemclass_t.prop('disabled', false);
                        p_itemclass.prop('disabled', false);
                        submittted = true;
                        //$('#editing-form').submit();
                    }
                });

                $('#itemclass_type').on('change', function (e) {
                    $this = $(this);
                    var localid = $this.find(':selected').data('<%=WebConstants.DATA_PARAMETER_ITEMCLASSLOCALID%>');
                    p_itemclass = $('#parent_itemclass');
                    base_uri = $('#base_uri');
                    base_uri.val('');
                    if (localid == 'registry') {
                        p_itemclass.val('');
                        p_itemclass.attr('disabled', true);
                        p_itemclass.selectpicker('refresh');
                        p_itemclass.prop('required', false);
                        base_uri.attr('disabled', false);
                        base_uri.prop('required', true);
                    } else {
                        p_itemclass.val('');
                        p_itemclass.attr('disabled', false);
                        base_uri.prop('required', false);
                        base_uri.attr('disabled', true);
                        if (localid == 'register') {
                            base_uri.prop('required', true);
                            $('#parent_itemclass option').each(function (ev) {
                                if ($(this).data('type') == 'registry') {
                                    $(this).attr('disabled', false);
                                    $(this).show();
                                } else {
                                    $(this).attr('disabled', true);
                                    $(this).hide();
                                }
                            });
                        } else if (localid == 'item') {
                            base_uri.val('');
                            base_uri.prop('required', false);
                            base_uri.attr('disabled', true);
                            $('#parent_itemclass option').each(function (ev) {
                                if ($(this).data('type') == 'registry' || $(this).data('type') == '' || $(this).data('haschild') == true) {
                                    $(this).attr('disabled', true);
                                    $(this).hide();
                                } else {
                                    $(this).attr('disabled', false);
                                    $(this).show();
                                }
                            });
                        } else if (localid == '') {
                            p_itemclass.val('');
                            p_itemclass.attr('disabled', true);
                        }
                        p_itemclass.prop('required', true);
                        p_itemclass.selectpicker('refresh')
                    }
                    $('#editing-form').validator('update');
                    $('#editing-form').validator('validate');
                });

                $('#parent_itemclass').on('change', function (e) {
                    $this = $(this);
                    var baseuri = $this.find(':selected').data('baseuri');
                    base_uri = $('#base_uri');
                    if (baseuri) {
                        base_uri.val(baseuri);
                    } else {
                        base_uri.val('');
                    }
                });

                <%--$('#editing-form :input').on('keyup paste change',function(e){
                    base_uri=$('#base_uri');
                    localid=$('#localid');
                    parentitemclass=$('#parent_itemclass').find(':selected');
                    uripreview=$('#uripreview');
                    
                    localidval='';                  
                    if(localid.val()!=''){
                        localidval = ''+localid.val();
                    }
                    if($('#itemclass_type').find(':selected').data('<%=WebConstants.DATA_PARAMETER_ITEMCLASSLOCALID %>')=='item'){
                        localidval='[item-local-id]';
                    }
                    
                    baseurival = base_uri.val();
                    if(base_uri.val()==''){
                        baseurival = parentitemclass.data('baseuri');
                    }
                    
                    uripreview.html(baseurival + localidval);
                });--%>

                $(function () {
                    $('#editing-form').validator();
                    var sel = 0;
                    $('#itemclass_type option').each(function () {
                        var $this = $(this);
                        if ($this.attr("selected") !== undefined) {
                            sel++;
                        }
                    });
                    $('#parent_itemclass option').each(function () {
                        var $this = $(this);
                        if ($this.attr("selected") !== undefined) {
                            sel++;
                        }
                    });
                    if (sel < 2) {
                        $('#itemclass_type').val('');
                        $('#parent_itemclass').val('');
                        $('#parent_itemclass').attr('disabled', true);
                    }

                <%-- Disable the choiche of the parent itemclass and the type in case of addition of a parent itemclass --%>
                <% if (parentRegItemclass != null) { %>
                    $('#itemclass_type').attr('disabled', true);
                    $('#parent_itemclass').attr('disabled', true);
                    $('#base_uri').prop('required', false);
                    $('#base_uri').attr('disabled', true);
                    $('#editing-form').validator('update');
                    $('#editing-form').validator('validate');
                <% }%>
                });

                $(function () {
                    $('[data-toggle="tooltip"]').tooltip();
                });
            </script>            
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
    </body>
</html>