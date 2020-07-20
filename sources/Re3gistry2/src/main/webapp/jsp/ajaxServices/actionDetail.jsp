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
<%@page import="org.json.JSONObject"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegAction"%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%
    // Creating the JSON service for the frontend (Used in the workflow pages)
    RegAction regAction = (RegAction) request.getAttribute(BaseConstants.KEY_REQUEST_ACTION);
    JSONObject obj = new JSONObject(); 
    if (regAction != null) {
        obj.put(BaseConstants.KEY_JSON_FIELDS_UUID, regAction.getUuid());
        obj.put(BaseConstants.KEY_JSON_FIELDS_CHANGELOG, regAction.getChangelog());
        obj.put(BaseConstants.KEY_JSON_FIELDS_ISSUETRACKERLINK, regAction.getIssueTrackerLink());
        obj.put(BaseConstants.KEY_JSON_FIELDS_CHANGEREQUEST, regAction.getChangeRequest());
        obj.put(BaseConstants.KEY_JSON_FIELDS_REJECTMESSAGE, regAction.getRejectMessage());
        obj.put(BaseConstants.KEY_JSON_FIELDS_LABEL, regAction.getLabel());
    }
%><%=obj.toString() %>