<%-- 
/*
 * Copyright 2010,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-info@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
--%>

<%@page import="java.util.ResourceBundle"%>
<%@page import="eu.europa.ec.ror.utility.Constants"%>
<%@page import="java.util.Locale"%>
<%@page import="eu.europa.ec.ror.utility.localization.LocalizationMgr"%>
<%@page import="eu.europa.ec.ror.model.Descriptor"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.ror.managers.DescriptorMgr"%>
<%@page import="eu.europa.ec.ror.model.Organization"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="eu.europa.ec.ror.managers.UserMgr"%>
<%@page import="eu.europa.ec.ror.model.User"%>
<%@page import="eu.cec.digit.ecas.client.jaas.DetailedUser"%>

<%@include file="../common.inc.jsp" %>

<%
    // ## Get the list of Registry descriptor associated to the organization of the current user in the DataTable [DT] ajax source format ##

    // Getting the current user from ECAS
    DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();

    // Create the DT data format for each Descriptor
    String outjson = "{\"data\":[";

    if (detailedUser != null) {

        // Getting the RoR user from the DB using the ECAS id
        String uid = detailedUser.getUid();
        User u = UserMgr.getUserByEcasId(uid);
        
        //If ECAS uid is not available, check the ECAS email
        if(u==null){
            uid = detailedUser.getEmail();
            u = UserMgr.getUserByEcasId(uid);
        }

        // If the user is not null and it is active
        if (u != null && u.getActive()) {
            // Getting the organization related to the user
            Organization o = u.getOrganization();

            List<Descriptor> descriptors = null;
            // If the user is an admin, getting all the descriptor available in the system
            if(u.getUserlevel() == Constants.USER_LEVEL_ADMIN){
                descriptors = DescriptorMgr.getAllDescriptors();
            }else{
                // Otherwise getting the list of descriptors related to that organization
                descriptors = DescriptorMgr.getDescriptorByOrganization(o);
            }
            int i = 0;
            for (Descriptor d : descriptors) {
                if (d != null && d.getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) {
                    outjson += (i > 0) ? "," : "";
                    outjson += "[\"<a target=\\\"_blank\\\" href=\\\""+d.getUrl()+"\\\">" + d.getUrl() + "</a> <span class=\\\"glyphicon glyphicon-new-window smallico\\\"></span>\","+((u.getUserlevel()==Constants.USER_LEVEL_ADMIN)? "\"" + d.getOrganization().getName() + "\"," : "")+"\"" + d.getAddedby().getEcasid() + "\",\"<span class=\\\"text-sm\\\"><a data-id=\\\"" + d.getUuid() + "\\\" href=\\\"#\\\" class=\\\"tactions del-descriptor-btn\\\" title=\\\"" + localization.getString("private.title.deletedescriptor") + "\\\"><span class=\\\"glyphicon glyphicon-trash\\\"></span>" + localization.getString("private.label.deletedescriptor") + "</a></span>\"]";
                    i++;
                }
            }
            outjson += "]}";

            // Otherwise the list is empty.
        } else {
            outjson += "]}";
        }
    }
%>
<%=outjson%>