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
<%@page import="eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager"%>
<%@page import="eu.europa.ec.re3gistry2.model.RegGroup"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.BaseConstants"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="eu.europa.ec.re3gistry2.base.utility.PersistenceFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<jsp:useBean id="constants" class="eu.europa.ec.re3gistry2.base.utility.BaseConstants" scope="session"/>

<%    // Checking if the current user has the rights to add a new itemclass
    String[] actionManageSystem = {BaseConstants.KEY_USER_ACTION_MANAGESYSTEM};
    boolean permissionManageSystem = UserHelper.checkGenericAction(actionManageSystem, currentUserGroupsMap, regItemRegGroupRegRoleMappingManager);
%>
<!DOCTYPE html>
<html lang="${localization.getString("property.localeid")}" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp"%>

        <div class="container">

            <%
                String csrfError = (String) request.getAttribute(BaseConstants.KEY_REQUEST_CSRF_PREVENTION_ERROR);
                if (csrfError != null && csrfError.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE)) {
            %>
            <div class="alert alert-danger" role="alert">
                ${localization.getString("label.csrferror")}
            </div>
            <%
                }
            %>

            <div class="row">
                <div class="col">
                    <h1 class="page-heading">${localization.getString("index.label.title")}</h1>
                </div>
            </div>

            <div class="row">
                <div class="col">

                    <h3 class="mt-3">${localization.getString("label.pendingitems")}</h3>

                    <table id="ativity-table" class="table table-striped table-bordered" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th>${localization.getString("label.name")}</th>
                                <th>${localization.getString("label.editdate")}</th>
                            </tr>
                        </thead>
                    </table>

                </div>
            </div>

            <hr/>

        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            $(function () {
                $('#ativity-table').DataTable({
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "ajax": {
                        url: config.baseurl + "<%=WebConstants.PAGE_URINAME_USERACTIVITY%>",
                        complete: function (data) {

                        }
                    },
                    "language": {
                        "processing": "<div class=\"loader\"></div>"
                    },
                    "processing": true,
                    "serverSide": true
                });
            });
        </script>

    </body>
</html>