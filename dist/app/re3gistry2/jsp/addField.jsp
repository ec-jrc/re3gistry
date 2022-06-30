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
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldtype"%>
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
<%    // Getting properties
    Properties properties = configuration.getProperties();

    //Loading the system localization (note: it is different from the content localization)
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Getting objects from the request
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    List<RegFieldtype> regFieldtypes = (List<RegFieldtype>) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELDTYPES);
    List<RegItemclass> regItemclasses = (List<RegItemclass>) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASSES);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionManageFieldMapping = {BaseConstants.KEY_USER_ACTION_MANAGEFIELDMAPPING};
    boolean permissionManageFieldMapping = UserHelper.checkGenericAction(actionManageFieldMapping, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

    // Getting parameters
    String regItemclassUuid = (String) request.getAttribute(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("label.addNewField")}</h1>
                </div>
            </div>

            <ol class="breadcrumb breadcrumb-in-page mb-3" vocab="http://schema.org/" typeof="BreadcrumbList">
                <li property="itemListElement" typeof="ListItem">
                    <a property="item" typeof="WebPage" href=".<%=WebConstants.PAGE_URINAME_ITEMCLASS%>"><span property="name">${localization.getString('menu.itemclass.label')}</span></a>
                </li>
                <li property="itemListElement" typeof="ListItem">
                    <a property="item" typeof="WebPage" href=".<%=WebConstants.PAGE_URINAME_FIELD%>"><span property="name">${localization.getString("field.label.title")}</span></a>
                </li>
                <li property="itemListElement" typeof="ListItem">
                    <span property="item" typeof="WebPage"><span property="name">${localization.getString("label.addNewField")}</span></span>
                </li>
            </ol>

            <form id="editing-form" method="post"  accept-charset="UTF-8" data-toggle="validator" role="form">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>" value="<%=regItemclassUuid%>" />

                <p class="mb-3">${localization.getString("mapField.label.insertnew")}</p>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("label.localid")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input  maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>" data-toggle="tooltip" data-placement="top" title="<%=localization.getString("label.fielduriinfo")%>" type="text" class="form-control input-uri" value="" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALID%>" required/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("mapField.label.labelinmasterlanguage")} (<%=masterLanguage.getIso6391code()%>)</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <input type="text" class="form-control" value="" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LABEL%>" required maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>"/>
                            <div class="input-group-append" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("label.fieldtype")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select id="fieldtype" class="selectpicker form-control" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_FIELDTYPEUUID%>" required>
                                <option value="" selected="selected"></option>
                                <%
                                    for (RegFieldtype tmpRegFieldtype : regFieldtypes) {
                                        if (!tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_COLLECTION)
                                                && !tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTER)
                                                && !tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_REGISTRY)
                                                && !tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STATUS)
                                                && !tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {
                                %><option value="<%=tmpRegFieldtype.getUuid()%>" data-reference="<%=tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)%>"><%=tmpRegFieldtype.getLocalid()%><%=((tmpRegFieldtype.getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_STRING)) ? " (" + MessageFormat.format(localization.getString("label.maxchars"), properties.getProperty("application.input.maxlength")) + ")" : "")%></option><%
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

                <div class="row form-group editing-labels d-none" id="referencepickerdiv">
                    <label class="col-sm-4">${localization.getString("label.itemclassreference")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select id="referencepicker" class="selectpicker form-control d-none" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSREFERENCEUUID%>" data-live-search="true">
                                <option value="" selected="selected"></option>
                                <%
                                    for (RegItemclass tmpRegItemclass : regItemclasses) {
                                %><option value="<%=tmpRegItemclass.getUuid()%>" data-reference="<%=tmpRegItemclass.getLocalid()%>"><%=tmpRegItemclass.getLocalid()%> [<%=tmpRegItemclass.getRegItemclasstype().getLocalid()%>]</option><%
                                    }
                                %>
                            </select>
                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="row">
                    <div class="col-sm-9"></div>
                    <div class="col-sm-3">
                        <%-- If the user has the rights to add a RegField, show the save button --%>
                        <% if (permissionManageFieldMapping) { %>
                        <button type="submit" class="btn btn-success w-100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
                        <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>
                        <% }%>
                    </div>
                </div>
            </form>

        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            //Handle the reference selector
            $(function () {
                //$('#referencepicker').addClass('d-none');
            });
            $('#fieldtype').on('change', function () {
                var $this = $(this);
                if ($this.find('option:selected').data('reference') == true) {
                    $('#referencepickerdiv').removeClass('d-none');
                    $('#referencepickerdiv').addClass('show');
                    $('#referencepickerdiv .bootstrap-select').removeClass('d-none');
                    $('#referencepickerdiv .bootstrap-select').addClass('show');
                    $('#referencepicker').removeClass('d-none');
                    $('#referencepicker').addClass('show');
                    $('#referencepicker').prop('required', true);
                } else {
                    $('#referencepickerdiv').removeClass('show');
                    $('#referencepickerdiv').addClass('d-none');
                    $('#referencepickerdiv .bootstrap-select').removeClass('show');
                    $('#referencepickerdiv .bootstrap-select').addClass('d-none');
                    $('#referencepicker').removeClass('show');
                    $('#referencepicker').addClass('d-none');
                    $('#referencepicker').prop('required', false);
                }
                $('#editing-form').validator('update');
                $('#editing-form').validator('validate');
            });
        </script>
    </body>
</html>