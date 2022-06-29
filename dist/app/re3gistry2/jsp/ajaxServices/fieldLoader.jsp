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
<%@page import="eu.europa.ec.re3gistry2.base.utility.Configuration"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.UserHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager"%>
<%@page import="javax.persistence.NoResultException"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.ItemHelper"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelationpredicate"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegRelation"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegFieldmapping"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Getting objects from the request
    RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_CURRENTLANGUAGE);
    RegLanguagecode masterLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_MASTERLANGUAGE);

    RegLocalization regLocalization = (RegLocalization) request.getAttribute(BaseConstants.KEY_REQUEST_REGLOCALIZATION);
    RegLocalization regLocalizationMasterLanguage = (RegLocalization) request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATIONMASTERLANGUAGE);

    // Getting the configuration
    Configuration configuration = Configuration.getInstance();

    // Setup the entity manager
    EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

    // Getting the user permission mapping from the session
    HashMap<String, RegGroup> currentUserGroupsMap = (HashMap) request.getSession().getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP);

    // Instantiating managers
    RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);

    // Checking if the current user has the rights to add a new itemclass
    String[] actionManageField = {BaseConstants.KEY_USER_ACTION_MANAGEFIELD};
    boolean permissionManageField = UserHelper.checkGenericAction(actionManageField, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);

    // Getting the RegItemclass Uuid
    String itemclassUuid = (String) request.getAttribute(BaseConstants.KEY_REQUEST_ITEMCLASSUUID);
    String fieldUuid = (String) request.getAttribute(BaseConstants.KEY_REQUEST_FIELDUUID);
%>
<form id="editing-form" method="post"  accept-charset="UTF-8" data-toggle="validator" role="form">
    
    <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
    
    <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LOCALIZATIONUUID%>" value="<%=(regLocalization != null) ? regLocalization.getUuid() : ""%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ITEMCLASSUUID%>" value="<%=itemclassUuid%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_FIELDUUID%>" value="<%=fieldUuid%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_LANGUAGEUUID%>" value="<%=currentLanguage.getUuid()%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_FORM_FIELD_NAME_REQUEST_EDITFIELD%>" value="true" />

    <%-- Showing the translation in the current language (if available) --%>
    <div class="row form-group">
        <label class="col-sm-4">${localization.getString("label.label")} (<%=currentLanguage.getIso6391code()%>):</label>
        <div class="col-sm-8"><input <%=(!permissionManageField) ? "disabled " : ""%>name="<%=BaseConstants.KEY_FORM_FIELD_NAME_VALUE%>" <%=(currentLanguage.getUuid().equals(masterLanguage.getUuid())) ? "required" : ""%> class="form-control" type="text" value="<%=(regLocalization != null) ? regLocalization.getValue() : ""%>" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength")%>"/></div>
    </div>

    <%-- If the current language is not masterlanguage, the master language is also provided in order to help with the translation --%>
    <%if (!currentLanguage.getUuid().equals(masterLanguage.getUuid())) {%>
    <div class="row form-group">
        <div class="col-sm-4">${localization.getString("label.masterlanguagelabel")} (<%=masterLanguage.getIso6391code()%>):</div>
        <div class="col-sm-8"><%=regLocalizationMasterLanguage.getValue()%></div>
    </div>
    <%}%>

    <!--<hr/>-->
    <%-- If the user has the rights to edit RegFields, showing the save button --%>
    <% if (permissionManageField) { %>
    <div class="row">
        <div class="col-sm-9"></div>
        <div class="col-sm-3">
            <button type="submit" class="btn btn-success width100"><i class="far fa-save"></i> ${localization.getString("label.save")}</button>
        </div>
    </div>
    <p class="form-validation-messages"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ${localization.getString("label.completerequiredfields")}</p>
    <% }%>
</form>