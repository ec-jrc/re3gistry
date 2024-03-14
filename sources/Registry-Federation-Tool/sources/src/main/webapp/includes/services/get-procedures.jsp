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

            List<Procedure> procedures = null;
            // If the user is an admin, getting all the procedures available in the system
            if (u.getUserlevel() == Constants.USER_LEVEL_ADMIN) {
                procedures = ProcedureMgr.getAllProcedures();
            } else {
                // Otherwise getting the list of procedures related to that organization
                procedures = ProcedureMgr.getProcedureByOrganization(o);
            }

            int i = 0;
            for (Procedure pr : procedures) {
                if (p != null) {
                    outjson += (i > 0) ? "," : "";

                    // Creating the output strings
                    String url = pr.getDescriptor().getUrl();
                    //String tblurl = "<span class=\\\"stt\\\" data-toggle=\\\"tooltip\\\" data-placement=\\\"bottom\\\" title=\\\"" + url + "\\\">" + ((url.length()>45) ? url.substring(0, 45) + "..."  : url) + "</span>";

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

                    String tblstatus = "<span class=\\\"color-" + status + "\\\"><span class=\\\"glyphicon " + sicon + "\\\"></span> " + localization.getString("private.procedure.status." + status) + "</span>";

                    String actions = "";
                    String disabled = ((pr.getStatus().equals(Constants.PROCEDURE_STATUS_RUNNING)) ? " disabled" : "");
                    String startLabel = ((pr.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) ? localization.getString("private.procedure.label.harvertcomplete") : localization.getString("private.procedure.manualstart"));
                    String startTitle = ((pr.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) ? localization.getString("private.procedure.title.harvertcomplete") : localization.getString("private.procedure.title.manualstart"));

                    if (status.equals(Constants.PROCEDURE_STATUS_ERROR) || status.equals(Constants.PROCEDURE_STATUS_SUCCESS)) {
                        actions = "<a class=\\\"tactions mstart" + disabled + "\\\" href=\\\"#\\\" data-id=\\\"" + pr.getUuid() + "\\\" title=\\\"" + startTitle + "\\\"><span class=\\\"glyphicon glyphicon-play-circle\\\"></span> " + startLabel + "</a>";
                    }
                    if (status.equals(Constants.PROCEDURE_STATUS_ERROR)) {
                        actions += "<a class=\\\"tactions perror\\\" href=\\\"#\\\" data-id=\\\"" + pr.getUuid() + "\\\" title=\\\"" + localization.getString("private.procedure.title.showerror") + "\\\"><span class=\\\"glyphicon glyphicon-list-alt\\\"></span> " + localization.getString("private.procedure.showerror") + "</a>";
                    }
                    if (status.equals(Constants.PROCEDURE_STATUS_SUCCESS)) {
                        actions += "<a class=\\\"tactions perror\\\" href=\\\"#\\\" data-id=\\\"" + pr.getUuid() + "\\\" title=\\\"" + localization.getString("private.procedure.title.showvalidationreport") + "\\\"><span class=\\\"glyphicon glyphicon-list-alt\\\"></span> " + localization.getString("private.procedure.showvalidationreport") + "</a>";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat(localization.getString("common.dateformat"));

                    outjson += "[\"" + url + "\","+((u.getUserlevel()==Constants.USER_LEVEL_ADMIN)? "\"" + pr.getOrganization().getName() + "\"," : "")+"\"" + pr.getDescriptor().getDescriptortype().substring(0, 1).toUpperCase() + pr.getDescriptor().getDescriptortype().substring(1) + "\",\"" + ((pr.getLastharvestdate() != null) ? sdf.format(pr.getLastharvestdate()) : "") + "\",\"" + ((pr.getNextharvestdate() != null) ? sdf.format(pr.getNextharvestdate()) : "") + "\",\"" + tblstatus + "\",\"<span class=\\\"text-sm\\\">" + actions + "</span>\"]";

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