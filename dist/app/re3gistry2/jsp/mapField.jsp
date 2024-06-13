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
<%    //Loading the system localization (note: it is different from the content localization)
    ResourceBundle localization = (ResourceBundle) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);

    // Initializing managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

    // Getting objects from the request
    RegLanguagecode regLanguagecode = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);
    RegItemclass regItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS);
    List<RegField> regFields = (List<RegField>) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELDS);
    List<RegFieldmapping> listRegFieldmappingsItemclass = (List<RegFieldmapping>) request.getAttribute(BaseConstants.KEY_REQUEST_REGFIELDMAPPINGSITEMCLASS);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionManageFieldMapping = {BaseConstants.KEY_USER_ACTION_MANAGEFIELDMAPPING};
    String[] actionManageField = {BaseConstants.KEY_USER_ACTION_MANAGEFIELD};
    boolean permissionManageFieldMapping = UserHelper.checkGenericAction(actionManageFieldMapping, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
    boolean permissionManageField = UserHelper.checkGenericAction(actionManageField, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("mapField.label.title")}</h1>
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
                    <span property="item" typeof="WebPage"><span property="name">${localization.getString("mapField.label.title")}</span></span>
                </li>
            </ol>

            <%
           String operationError = (String) request.getAttribute(BaseConstants.KEY_REQUEST_OPERATIONRESULT);
           if (operationError != null) {
            %>
            <div class="alert alert-danger alert-dismissible" role="alert">
                <%=operationError%>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <%
                }
            %>

            <p><%=MessageFormat.format(localization.getString("mapField.label.informative"), regItemclass.getLocalid())%></p>    
            <p>&nbsp;</p>

            <form id="editing-form" method="post" accept-charset="utf-8" role="form" data-toggle="validator">
                
                <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
                
                <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_ITEMCLASSUUID%>" value="<%=regItemclass.getUuid()%>" />
                <div class="row form-group editing-labels">
                    <label class="col-sm-4">${localization.getString("mapField.label.textselectfield")}</label>
                    <div class="col-sm-8">
                        <div class="input-group">
                            <select class="select-field form-control" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_FIELDUUID%>" required data-live-search="true">
                                <option value=""></option>
                                <%
                                    for (RegField tmpRegField : regFields) {
                                        RegLocalization tmpReglocalization = regLocalizationManager.get(tmpRegField, regLanguagecode);
                                        int found = 0;
                                        for (RegFieldmapping tmpRegFieldmapping : listRegFieldmappingsItemclass) {
                                            found += (tmpRegFieldmapping.getRegField().getLocalid().equals(tmpRegField.getLocalid()) ? 1 : 0);
                                        }
                                %><option value="<%=tmpRegField.getUuid()%>" <%=(found > 0) ? "disabled=\"disabled\"" : ""%>><%=tmpReglocalization.getValue()%> [<%=tmpRegField.getRegFieldtype().getLocalid()%>]</option><%
                                    }
                                %>
                            </select>

                            <div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <div class="input-group-text"><i class="fas fa-asterisk text-danger" aria-hidden="true"></i></div>
                            </div>
                            <%--<div class="input-group-append required-sign" data-toggle="tooltip" data-placement="top" title="${localization.getString("label.fieldrequired")}">
                                <i class="fas fa-asterisk text-danger" aria-hidden="true"></i>
                            </div>--%>
                        </div>
                    </div>
                </div>
                <div class="row form-group">
                    <div class="col-sm-9"></div>
                    <div class="col-sm-3">
                        <%-- If the user has the rights to map RegFields, show the map button --%>
                        <% if (permissionManageFieldMapping) { %>
                        <button type="submit" class="btn btn-success ml-2 float-right w-100"><i class="far fa-save"></i> ${localization.getString("label.map")}</button>
                        <% }%>
                    </div>
                </div>

                <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>

                <hr/>

                <div class="row form-group editing-labels">
                    <label class="col-sm-9">${localization.getString("mapField.label.textcreatenewfield")}</label>
                    <div class="col-sm-3">              
                        <% if (permissionManageField) {%>
                        <a class="btn btn-success w-100" href=".<%=WebConstants.PAGE_URINAME_ADDFIELD%>?<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>=<%=regItemclass.getUuid()%>"><i class="fas fa-plus"></i> ${localization.getString("label.addNewField")}</a>
                        <% }%>
                    </div>
                </div>
            </form>

        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
    </body>
</html>