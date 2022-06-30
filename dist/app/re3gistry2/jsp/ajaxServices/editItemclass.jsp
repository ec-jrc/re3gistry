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
<%@page import="eu.europa.ec.re3gistry2.model.RegItemclass"%>
<%@page import="org.json.JSONObject"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%
    // Creating the JSON service for the frontend (Used in the workflow pages)
    Boolean notEditable = (Boolean) request.getAttribute(BaseConstants.KEY_REQUEST_NOTEDITABLE);
    RegItemclass regItemclass = (RegItemclass) request.getAttribute(BaseConstants.KEY_REQUEST_REGITEMCLASS);
    JSONObject obj = new JSONObject(); 
    if (regItemclass != null) {
        obj.put(BaseConstants.KEY_JSON_FIELDS_UUID, regItemclass.getUuid());
        obj.put(BaseConstants.KEY_JSON_FIELDS_LOCALID, regItemclass.getLocalid());
        obj.put(BaseConstants.KEY_JSON_FIELDS_BASEURI, regItemclass.getBaseuri());
        obj.put(BaseConstants.KEY_JSON_FIELDS_ITEMCLASSTYPE, regItemclass.getRegItemclasstype().getLocalid());
        obj.put(BaseConstants.KEY_JSON_FIELDS_NOTEDITABLE, notEditable);
    }
%><%=obj.toString() %>