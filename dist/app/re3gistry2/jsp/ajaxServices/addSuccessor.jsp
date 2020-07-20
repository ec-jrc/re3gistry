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
<%@page import="eu.europa.ec.re3gistry2.base.utility.WebConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLocalization"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegLanguagecode"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegField"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegStatus"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegItem"%>
<%@page import="org.json.JSONObject"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegAction"%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%
    // Setup the entity manager
    EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();

    // Init managers
    RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
    RegFieldManager regFieldManager = new RegFieldManager(entityManager);

    RegField regFieldLabel = regFieldManager.getTitleRegField();

    RegItem regItem = (RegItem) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEM);
    List<RegItem> regItems = (List<RegItem>) request.getAttribute(BaseConstants.KEY_REQUEST_SELECT_LIST_ITEMS);
    RegLanguagecode currentLanguage = (RegLanguagecode) request.getAttribute(BaseConstants.KEY_REQUEST_LANGUAGE);
    RegStatus newRegStatus = (RegStatus) request.getAttribute(BaseConstants.KEY_REQUEST_NEWREGSTATUS);
%>
<form method="post" action=".<%=WebConstants.PAGE_URINAME_STATUSCHANGE%>">
    
    <input type="hidden" name="csrfPreventionSalt" value="${csrfPreventionSalt}"/>
    
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_ITEMUUID%>" value="<%=regItem.getUuid()%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_NEWSTATUSLOCALID%>" value="<%=newRegStatus.getLocalid()%>" />
    <input type="hidden" name="<%=BaseConstants.KEY_REQUEST_LANGUAGEUUID%>" value="<%=currentLanguage.getUuid()%>" />    
    <div class="modal-header">
        You are about to change the status to: <%=newRegStatus.getLocalid()%>.
    </div>
    <div class="modal-body">
        <div class="form-group">
            <label for="successor">Select successor(s):</label>
            <select class="selectpicker form-control" name="<%=BaseConstants.KEY_REQUEST_SUCCESSORS%>" multiple data-live-search="true">
                <% for (RegItem tmp : regItems) {
                        List<RegLocalization> tmpLoc = regLocalizationManager.getAll(regFieldLabel, tmp, currentLanguage);
                        // The title has just one localization
                        RegLocalization regLocalization = tmpLoc.get(0);
                %>
                <option value="<%=tmp.getUuid()%>"><%=regLocalization.getValue()%></option>
                <%}%>
            </select>
        </div>
    </div>
    <div class="modal-footer">
        <div class="col-sm-6">
            <button type="button" class="btn btn-secondary width100" data-dismiss="modal"><i class="fas fa-ban"></i> Close</button>
        </div>
        <div class="col-sm-6">
            <button type="submit" class="btn btn-primary width100"><i class="far fa-save"></i> Save changes</button>
        </div>
    </div>
</form>