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

<%@page import="eu.europa.ec.ror.managers.ProcedurehistoryMgr"%>
<%@page import="eu.europa.ec.ror.model.Procedurehistory"%>
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

<%    // ## Get the error related to the Procedure passed by ID (pid parameter) ##
    // Getting the current user from ECAS
    DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();
    
    // Getting the ID parameter
    String pid = request.getParameter("pid");

    // Create the DT data format for each Procedure
    String outhtml = "";

    if (detailedUser != null && pid!=null && pid.length()>0) {

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

            // Getting the list of procedures related to that organization
            Procedurehistory procedurehistory = ProcedurehistoryMgr.getProcedurehistoryByID(pid);
            
            // Checking if the procedure is not null and if the organization of the procedure if the same organization of the current user
            if(procedurehistory!=null && (procedurehistory.getStartedby().getOrganization().getUuid().equals(o.getUuid()) || u.getUserlevel().equals(Constants.USER_LEVEL_ADMIN)) && ((procedurehistory.getStatus().equals(Constants.PROCEDURE_STATUS_ERROR) || procedurehistory.getStatus().equals(Constants.PROCEDURE_STATUS_SUCCESS)))){
                // Getting the error message
                
                // CHecking if the user has the right to show the current report
                if(u.getUserlevel()==Constants.USER_LEVEL_ADMIN || (u.getUserlevel()!=Constants.USER_LEVEL_ADMIN && u.getOrganization().getUuid().equals(procedurehistory.getProcedure().getOrganization().getUuid()))){
                    outhtml = procedurehistory.getMessage();
                }else{
                    outhtml = localization.getString("private.label.report.norights");
                }
                
                // Checking if the error message is not null
                if(outhtml== null || outhtml.length()<=0){
                    outhtml = "<div class=\"alert alert-warning\" role=\"alert\">" + localization.getString("private.procedure.no-error-to-show") + "</div>";
                }
                
            }else{
                outhtml = "<div class=\"alert alert-warning\" role=\"alert\">" + localization.getString("private.procedure.no-error-to-show") + "</div>";
            }

            // Otherwise the list is empty.
        } else {
            outhtml = "<div class=\"alert alert-warning\" role=\"alert\">" + localization.getString("private.procedure.no-error-to-show") + "</div>";
        }
    }else{
        outhtml = "<div class=\"alert alert-warning\" role=\"alert\">" + localization.getString("private.procedure.no-error-to-show") + "</div>";
    }
%>
<%=outhtml%>