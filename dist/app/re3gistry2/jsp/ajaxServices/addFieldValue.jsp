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
<%@page import="java.util.Date"%>
<%@page import="eu.europa.ec.re3gistry2.web.utility.jsp.JspHelper"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager"%>
<%@page import="java.util.ResourceBundle"%>
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
    // Setup the entity manager
    EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
    
    // Loading the system localization (note: it is different from the content localization)
    ResourceBundle localization = (ResourceBundle)request.getAttribute(BaseConstants.KEY_REQUEST_LOCALIZATION);            
    
    // Getting the configuration
    Configuration configuration = Configuration.getInstance();
    
    // Initializing managers
    RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);

    // Getting objects from the request
    String fieldMappingUuid = request.getParameter(BaseConstants.KEY_REQUEST_FIELDMAPPINGUUID);

    // Getting the RegFieldmapping
    RegFieldmapping regFieldmapping = regFieldmappingManager.get(fieldMappingUuid);
    
    // Variable to handle the field's index
    Date tmpDate = new Date();
    String timestamp = Long.toString(tmpDate.getTime());
    
    // Checking the properties of the element
    if(regFieldmapping.getMultivalue()){
        %><li class="multivalue"><%
    }

    %><div id="<%=WebConstants.DATA_PARAMETER_VLLISTIDPREFIX %><%=timestamp %>" class="just-added"><%

    if (regFieldmapping.getHashref()) {            
            
        %>
        <div class="row">
        <div class="col-sm-3"><%= localization.getString("label.value") %></div>
        <div class="col-sm-9">                                
        <%   
            // Handling the required notice (asterisc icon)
            if (regFieldmapping.getRequired() || regFieldmapping.getMultivalue()) {
                %><div class="form-group"><%
                %><div class="input-group"><%
            }else{
                %><div class="input-cnt"><%
            }
            
            if(regFieldmapping.getMultivalue()){
                %><span class="input-group-btn"><%
                    %><a href="#" class="btn btn-danger btn-value-remove" data-<%=WebConstants.DATA_PARAMETER_TEMPID %>="<%=timestamp%>" title="<%= localization.getString("label.remove") %>"><i class="far fa-trash-alt"></i></a><%
                %></span><%
            }
            
            // -- Showing the value --            
            %><input type="<%=JspHelper.handleFieldType(regFieldmapping, localization) %>" class="form-control" value="" name="<%= regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + timestamp %>" <%=((regFieldmapping.getRequired()) ? "required" : "")%> maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength") %>"/><%
           
            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                %><div class="input-group-append" data-toggle="tooltip" data-placement="top" title="<%= localization.getString("label.fieldrequired") %>"><div class="input-group-text"><i class="fas fa-asterisk text-danger"></i > </div></div><%
                %></div><%
                %><div class="help-block with-errors"></div><%
                %></div><%
            }else if (regFieldmapping.getMultivalue()) {
                %></div><%
                %><div class="help-block with-errors"></div><%
                %></div><%
            }else{
                %><div class="help-block with-errors"></div><%
                %></div><%
            }            
           
            %></div><%
            %></div><%
            
            // ----
            
            %><div class="row"><%
            %><div class="col-sm-3"><%= localization.getString("label.href") %>:</div><%
            %><div class="col-sm-9"><%
            
            // -- Showing the href -- 
            %><input type="<%=JspHelper.handleFieldType(regFieldmapping, localization) %>" class="form-control" value="" name="<%= regFieldmapping.getUuid()+ BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + timestamp + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX %>" maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength") %>"/><%
                   
            %></div><%
            %></div><%

    }else{
            // Handling the required notice (asterisc icon)
            if (regFieldmapping.getRequired() || regFieldmapping.getMultivalue()) {
                %><div class="form-group"><%
                %><div class="input-group"><%
            }else{
                %><div class="input-cnt"><%
            }
            
            if(regFieldmapping.getMultivalue()){
                %><span class="input-group-btn"><%
                %><a href="#" class="btn btn-danger btn-value-remove" data-<%=WebConstants.DATA_PARAMETER_TEMPID %>="<%=timestamp%>" title="<%= localization.getString("label.remove") %>"><i class="far fa-trash-alt"></i></a><%
                %></span><%
            }
            
            // -- Showing the value -- 
            if (regFieldmapping.getRegField().getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_LONGTEXT)) {
                %><textarea class="form-control" rows="3" name="<%= regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + timestamp %>" <%=((regFieldmapping.getRequired()) ? " required" : "") %>></textarea><%
            } else {
                %><input type="<%=JspHelper.handleFieldType(regFieldmapping, localization) %>" class="form-control" value="" name="<%= regFieldmapping.getUuid() + BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY + timestamp %>" <%= ((regFieldmapping.getRequired()) ? " required" : "") %> maxlength="<%=configuration.getProperties().getProperty("application.input.maxlength") %>"/><%                  
            }
            
            // Required notice (if needed)
            if (regFieldmapping.getRequired()) {
                %><div class="input-group-append" data-toggle="tooltip" data-placement="top" title="<%= localization.getString("label.fieldrequired") %>"><div class="input-group-text"><i class="fas fa-asterisk text-danger"></i > </div></div><%
                %></div><%
                %><div class="help-block with-errors"></div><%
                %></div><%
            }else if (regFieldmapping.getMultivalue()) {
                %></div><%
                %><div class="help-block with-errors"></div><%
                %></div><%
            }else{
                %><div class="help-block with-errors"></div><%
                %></div><%
            }            
    }    
    %></div><%
    
    if(regFieldmapping.getMultivalue()){
        %></li><%
    }
%>