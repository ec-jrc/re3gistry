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

<%@page import="java.util.TimeZone"%>
<%@page import="java.util.Calendar"%>
<%@page import="eu.europa.ec.ror.managers.ProcedurehistoryMgr"%>
<%@page import="eu.europa.ec.ror.model.Procedurehistory"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.europa.ec.ror.model.Procedure"%>
<%@page import="eu.europa.ec.ror.managers.ProcedureMgr"%>
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

<%    // ## Get the list of Procedures associated to the organization of the current user in the DataTable [DT] ajax source format ##
    // Getting the current user from ECAS
    DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();

    // Create the DT data format for each Procedure
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

            List<Procedurehistory> procedureshistory = null;
            // If the user is an admin, getting all the procedure history available in the system
            if (u.getUserlevel() == Constants.USER_LEVEL_ADMIN) {
                procedureshistory = ProcedurehistoryMgr.getAllProcedurehistories();
            } else {
                // Otherwise getting the list of procedures related to that organization
                procedureshistory = ProcedurehistoryMgr.getProcedurehistoriesByOrganization(o);
            }
%>
<div class="phst-cont">
    <h3>Harvesting job history</h3>
    <table id="phst" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
        <thead>
            <tr>
                <% if(u.getUserlevel()==Constants.USER_LEVEL_ADMIN){ %>
                    <th style="width:26%"><%=localization.getString("private.label.object")%></th>
                    <th style="width:12%"><%=localization.getString("private.label.organization")%></th>
                <% }else{ %>
                    <th style="width:38%"><%=localization.getString("private.label.object")%></th>
                <% } %> 
                <th style="width:8%">Type</th>
                <th style="width:10%">Date start</th>
                <th style="width:10%">Date end</th>
                <th style="width:10%">Status</th>
                <th style="width:10%">Started by</th>
                <th style="width:14%">Message</th>
            </tr>
        </thead>
        <%
            int i = 0;
            SimpleDateFormat sdf = new SimpleDateFormat(localization.getString("common.dateformat"));
            for (Procedurehistory pr : procedureshistory) {
                String status = pr.getStatus();
                String sicon = "";
                if (status.equals(Constants.PROCEDURE_STATUS_ERROR)) {
                    sicon = "glyphicon-alert";
                } else if (status.equals(Constants.PROCEDURE_STATUS_FIRSTINSERT)) {
                    sicon = "";
                } else if (status.equals(Constants.PROCEDURE_STATUS_RUNNING)) {
                    sicon = "glyphicon-flash";
                } else if (status.equals(Constants.PROCEDURE_STATUS_SUCCESS)) {
                    sicon = "glyphicon-ok-circle";
                } else if (status.equals(Constants.PROCEDURE_STATUS_WAITING)) {
                    sicon = "glyphicon-hourglass";
                }
                String tblstatus = "<span class=\"color-" + status + "\"><span class=\"glyphicon " + sicon + "\"></span> " + localization.getString("private.procedure.status." + status) + "</span>";

                String actions = "";
                if (status.equals(Constants.PROCEDURE_STATUS_ERROR)) {
                    actions += "<a class=\"tactions psterror\" href=\"#\" data-id=\"" + pr.getUuid() + "\" title=\"" + localization.getString("private.procedure.title.showerror") + "\"><span class=\"glyphicon glyphicon-list-alt\"></span> " + localization.getString("private.procedure.showerror") + "</a>";
                }
                if (status.equals(Constants.PROCEDURE_STATUS_SUCCESS)) {
                    actions += "<a class=\"tactions psterror\" href=\"#\" data-id=\"" + pr.getUuid() + "\" title=\"" + localization.getString("private.procedure.title.showvalidationreport") + "\"><span class=\"glyphicon glyphicon-list-alt\"></span> " + localization.getString("private.procedure.showvalidationreport") + "</a>";
                }
        %>                
        <tr>
            <% if(u.getUserlevel()==Constants.USER_LEVEL_ADMIN){ %>
                <td><%=pr.getProcedure().getDescriptor().getUrl()%></td>
                <td><%=pr.getProcedure().getOrganization().getName() %></td>
            <% }else{ %>
                <td><%=pr.getProcedure().getDescriptor().getUrl()%></td>
            <% } %>
            <td><%=pr.getProcedure().getDescriptor().getDescriptortype().substring(0, 1).toUpperCase() + pr.getProcedure().getDescriptor().getDescriptortype().substring(1)  %></td>
            <td><%=sdf.format(pr.getStartdate())%></td>
            <td><%=sdf.format(pr.getEnddate())%></td>
            <td><%=tblstatus%></td>
            <td><%=pr.getStartedby().getEcasid()%></td>
            <td><span class="text-sm"><%=actions%></span></td>
        </tr>
        <%
            }
        %>
    </table>
    
    <%
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        int offset = timeZone.getRawOffset();
        if (timeZone.inDaylightTime(now.getTime())) {
            offset += timeZone.getDSTSavings();
        }
        offset = offset / 1000 / 60 / 60;
    %>
    <p class="timenote"><%=localization.getString("common.timezone.message").replace("{0}", String.valueOf(offset)).replace("{1}", "("+timeZone.getDisplayName(timeZone.inDaylightTime(now.getTime()),TimeZone.SHORT)+")")%></p>
    
</div>
<%
    // Otherwise the list is empty.
} else {
%>
<div class="alert alert-warning" role="alert"><%=localization.getString("error.unauthorized.access")%></div>
<%
    }
} else {
%>
<div class="alert alert-warning" role="alert"><%=localization.getString("error.unauthorized.access")%></div>
<%
    }
%>
