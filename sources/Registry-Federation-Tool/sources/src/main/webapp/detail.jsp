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
<%@page import="org.apache.solr.common.SolrDocument"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>
<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="org.apache.solr.client.solrj.SolrQuery"%>
<%@page import="org.apache.http.impl.conn.SystemDefaultRoutePlanner"%>
<%@page import="org.apache.http.client.HttpClient"%>
<%@page import="org.apache.http.impl.client.HttpClients"%>
<%@page import="org.apache.solr.client.solrj.impl.HttpSolrServer"%>
<%@page import="java.net.ProxySelector"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="eu.europa.ec.ror.managers.RelationMgr"%>
<%@page import="eu.europa.ec.ror.model.Register"%>
<%@page import="eu.europa.ec.ror.managers.RegisterMgr"%>
<%@page import="eu.europa.ec.ror.model.Registry"%>
<%@page import="eu.europa.ec.ror.managers.RegistryMgr"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<%    
    String type = (request.getParameter("type") != null) ? request.getParameter("type") : "registries";

    String id = (request.getParameter("id") != null) ? request.getParameter("id") : null;
    if (id == null) {
        response.sendRedirect("index.jsp");
    }
%>
<!DOCTYPE html>
<html lang="<%=localization.getString("common.localeid")%>" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp" %>

        <div class="container registry">

            <%if (type.equals("registries")) {%>
            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("registries.title.singular")%></h1>
                </div>
                <div class="col-md-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>
            <p></p>

            <div class="panel panel-eu">
                <div class="panel-heading"><strong><%=localization.getString("common.details")%></strong></div>
                <ul class="list-group">
                    <!--<li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.localid")%>:</div>
                            <div class="col-sm-9" id="r-lcid"></div>
                        </div>
                    </li>-->
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.label")%>:</div>
                            <div class="col-sm-9" id="r-lid"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.uri")%>:</div>
                            <div class="col-sm-9" id="r-uri"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.publisher")%>:</div>
                            <div class="col-sm-9">
                                <ul>
                                    <li><span id="plabel"></span></li>
                                    <li><span id="puri"></span></li>
                                    <li><span id="pemail"></span></li>
                                    <li><span id="phome"></span></li>
                                </ul>
                            </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.updatefrequency")%>:</div>
                            <div class="col-sm-9" id="updf"></div>
                        </div>    
                    </li>
                </ul>
            </div>

            <div class="well">
                REST URL: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.registries")%>/<%=id%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.registries")%>/<%=id%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            </div>

            <h3><%=localization.getString("common.associated")%>&nbsp;<%=localization.getString("registers.title")%></h3>
            <table id="registers" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th><%=localization.getString("common.table.label")%></th>
                        <th><%=localization.getString("registries.title.singular")%></th>
                        <th><%=localization.getString("common.table.uri")%></th>
                    </tr>
                </thead>
            </table>
            <p>&nbsp;</p>

            <div class="well">
                REST URL: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>/q/registry=<%=id%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>/q/<%=RegisterMgr.REGISTRY_ID%>=<%=id%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            </div>

            <p>&nbsp;</p>

            <%} else if (type.equals("registers")) {%>

            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("registers.title.singular")%></h1>
                </div>
                <div class="col-md-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>
            <p></p>

            <div class="panel panel-eu">
                <div class="panel-heading"><strong><%=localization.getString("common.details")%></strong></div>
                <ul class="list-group">
                    <!--<li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.localid")%>:</div>
                            <div class="col-sm-9" id="r-lcid"></div>
                        </div>
                    </li>-->
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.label")%>:</div>
                            <div class="col-sm-9" id="r-lid"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.uri")%>:</div>
                            <div class="col-sm-9" id="r-uri"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.publisher")%>:</div>
                            <div class="col-sm-9">
                                <ul>
                                    <li><span id="plabel"></span></li>
                                    <li><span id="puri"></span></li>
                                    <li><span id="pemail"></span></li>                                    
                                    <li><span id="phome"></span></li>
                                </ul>
                            </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("registries.title.singular")%>:</div>
                            <div class="col-sm-9">
                                <ul>
                                    <li><span id="rr-lid"></span></li>
                                    <li><span id="rr-uri"></span></li>
                                </ul>                                
                            </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.updatefrequency")%>:</div>
                            <div class="col-sm-9" id="updf"></div>
                        </div>    
                    </li>
                </ul>
            </div>

            <div class="well">
                REST URL: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>/<%=id%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>/<%=id%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            </div>

            <h3><%=localization.getString("common.associated")%>&nbsp;<%=localization.getString("relations.title")%></h3>
            <table id="relations" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th><%=localization.getString("common.table.localid")%></th>
                        <th><%=localization.getString("common.table.subject")%></th>
                        <th><%=localization.getString("common.table.predicate")%></th>
                        <th><%=localization.getString("common.table.object")%></th>
                        <!--<th><%=localization.getString("common.table.status")%></th>-->
                    </tr>
                </thead>
            </table>
            <p>&nbsp;</p>

            <h3><%=localization.getString("common.available.items")%></h3>
            <table id="items" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <!--<th><%=localization.getString("common.table.uri")%></th>-->
                        <th><%=localization.getString("common.table.label")%></th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        Properties properties = Configuration.getInstance().getProperties();
                        SolrServer server = null;

                        // Connection to Solr
                        String solrUrl = properties.getProperty("solr.url");
                        server = new HttpSolrServer(solrUrl);
                        //HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
                        //server = new HttpSolrServer(solrUrl, client);
                      
                        Register r = RegisterMgr.getRegisterByID(request.getParameter("id"));
                        
                        SolrQuery query = new SolrQuery();
                        query.setQuery("registeruri:\""+r.getUri()+"\"");
                        query.setRows(2000);
                        QueryResponse res = null;
                        try {
                            res = server.query(query);
                        } catch (Exception e) {
                            %><%=e.getMessage()%><%
                        }
                        SolrDocumentList list = res.getResults();
                        for (int i = 0; i < list.size(); ++i) {
                            SolrDocument sd = (SolrDocument)(list.get(i));
                            %><tr><!--<td><%=sd.getFirstValue("id")%></td>--><td><a target="_blank" href="<%=sd.getFirstValue("id")%>"><%=sd.getFirstValue("label")%></a> <span class="glyphicon glyphicon-new-window smallico"></span> </td></tr><%
                        }

                    %>                    
                </tbody>
            </table>
            <p>&nbsp;</p>

            <div class="well">
                REST URL: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>/q/<%=RelationMgr.SUBJECT_REGISTER_OR_OBJECT_REGISTER_ID%>=<%=id%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>/q/<%=RelationMgr.SUBJECT_REGISTER_OR_OBJECT_REGISTER_ID%>=<%=id%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            </div>

            <p>&nbsp;</p>
            <%} else if (type.equals("relations")) {%>

            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("relations.title.singular")%></h1>
                </div>
                <div class="col-md-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>
            <p></p>

            <div class="panel panel-eu">
                <div class="panel-heading"><strong><%=localization.getString("common.details")%></strong></div>
                <ul class="list-group">
                    <!--<li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.label.localid")%>:</div>
                            <div class="col-sm-9" id="r-lid"></div>
                        </div>
                    </li>-->

                    <!--<li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.status")%>:</div>
                            <div class="col-sm-9" id="r-sta"></div>
                        </div>
                    </li>-->
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.subject")%>:</div>
                            <div class="col-sm-9" id="sr-lid"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.predicate")%>:</div>
                            <div class="col-sm-9" id="sr-uri"></div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="row">
                            <div class="col-md-3"><%=localization.getString("common.table.object")%>:</div>
                            <div class="col-sm-9" id="rr-lid"></div>
                        </div>
                    </li>
                </ul>
            </div>


            <div class="well">
                REST URL: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>/<%=id%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>/<%=id%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            </div>

            <p>&nbsp;</p>

            <%}%>

        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script>
            $(document).ready(function () {
                $.ajax({
                    url: "<%=p.get("web.application_root_url")%>rest/<%=type%>/<%=id%>"
                }).done(function (data) {
            <%
                if (type.equals("registries")) {
            %>
                //$("#r-lcid").html(data.registry.id);
                $("#r-lid").html(data.registry.label);
                $("#r-uri").html('<a target="_blank" href="' + data.registry.uri + '">' + data.registry.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                $("#plabel").html(data.registry.publisher.label);
                eml = data.registry.publisher.email;
                $("#pemail").html('<a href="mailto:' + eml.replace("mailto:", "") + '">' + data.registry.publisher.email + '</a> <span class="glyphicon glyphicon-envelope"></span>');
                $("#puri").html('<a target="_blank" href="' + data.registry.publisher.uri + '">' + data.registry.publisher.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                $("#phome").html('<a target="_blank" href="' + data.registry.publisher.homepage + '">' + data.registry.publisher.homepage + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');

                if (data.registry.updatefrequency) {
                    $("#updf").html('<a href="http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html" target="_blank">'+data.registry.updatefrequency+'</a>  <span class="glyphicon glyphicon-new-window smallico"></span>');
                } else {
                    $("#updf").html('<%=localization.getString("common.label.notavailable")%>');
                }

                if (data.registry.lastmodificationdate) {
                    $("#lasteditdate").html(data.registry.lastmodificationdate);
                } else {
                    $("#lasteditdate").html('<%=localization.getString("common.label.notavailable")%>');
                }
                if (data.registry.lastmodificationdate) {
                    $("#lastharvestdate").html(data.registry.lastmodificationdate);
                } else {
                    $("#lastharvestdate").html('<%=localization.getString("common.label.notavailable")%>');
                }

                /*Register */
                var i = 0;
                $('#registers thead tr').clone().appendTo('#registers thead');
                $('#registers').DataTable({
                    "ajax": {
                        "url": "<%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>/q/<%=RegisterMgr.REGISTRY_ID%>=<%=id%>",
                        "error": function () {
                            $('#registers thead tr:first').remove();
                            $('#registers_filter').html("");
                        }
                    },
                    "ajaxDataProp": "registers",
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "columns": [
                        {"data": "register.label"},
                        {"data": "register.registry.label"},
                        {"data": "register.uri"}
                    ],
                    "rowCallback": function (row, data, displayIndex) {
                        $('td:eq(0)', row).html('<a href="detail.jsp?type=registers&id=' + data.register.id + '">' + data.register.label + '</a>');
                        $('td:eq(1)', row).html('<a href="detail.jsp?type=registries&id=' + data.register.registry.id + '">' + data.register.registry.label + '</a>');
                        $('td:eq(2)', row).html('<a target="_blank" href="' + data.register.uri + '">' + data.register.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                        return row;
                    },
                    "pagingType": "full_numbers",
                    "displayLength": 10,
                    "stateSave": true,
                    "language": {"url": "<%=p.get("web.application_root_url")%>res/js/jquery.dataTables.localization/<%=localization.getString("common.localeid")%>.json"},
                    "initComplete": function () {
                        $('#registers thead tr:first th').css('text-align', 'left');
                        $('#registers thead tr:first th').each(function () {
                            var title = $('#registers thead th').eq($(this).index()).text();
                            var filter = $('#registers_filter label').text();
                            $(this).html('<input class="fin' + (i++) + ' form-control input-sm" type="search" placeholder="Filter ' + title + '" aria-controls="registers" />');
                            $(this).css('padding', '5px 0');
                        });
                        var api = this.api();
                        var state = api.state.loaded();
                        if (state) {
                            api.columns().eq(0).each(function (idx) {
                                var colSearch = state.columns[idx].search;
                                $('input.fin' + idx).val(colSearch.search);
                                $('input.fin' + idx).on('keyup change', function () {
                                    api.column(idx).search(this.value).draw();
                                });
                            });
                        } else {
                            api.columns().eq(0).each(function (idx) {
                                $('input.fin' + idx).on('keyup change', function () {
                                    api.column(idx).search(this.value).draw();
                                });
                            });
                        }
                        $('#registers_filter').html("");
                    }
                });
            <%
            } else if (type.equals("registers")) {
            %>
                //$("#r-lcid").html(data.register.id);
                $("#r-lid").html(data.register.label);
                $("#r-uri").html('<a target="_blank" href="' + data.register.uri + '">' + data.register.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                $("#rr-lid").html('<a href="detail.jsp?type=registries&id=' + data.register.registry.id + '">' + data.register.registry.label + '</a>');
                $("#rr-uri").html('<a target="_blank" href="' + data.register.registry.uri + '">' + data.register.registry.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');

                $("#plabel").html(data.register.publisher.label);
                eml = data.register.publisher.email;
                $("#pemail").html('<a href="mailto:' + eml.replace("mailto:", "") + '">' + data.register.publisher.email + '</a> <span class="glyphicon glyphicon-envelope"></span>');
                $("#puri").html('<a target="_blank" href="' + data.register.publisher.uri + '">' + data.register.publisher.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                $("#phome").html('<a target="_blank" href="' + data.register.publisher.homepage + '">' + data.register.publisher.homepage + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');


                if (data.register.updatefrequency) {
                    $("#updf").html('<a href="http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html" target="_blank">'+data.register.updatefrequency+'</a>  <span class="glyphicon glyphicon-new-window smallico"></span>');
                } else {
                    $("#updf").html('<%=localization.getString("common.label.notavailable")%>');
                }

                if (data.register.lastmodificationdate) {
                    $("#lasteditdate").html(data.register.lastmodificationdate);
                } else {
                    $("#lasteditdate").html('<%=localization.getString("common.label.notavailable")%>');
                }
                if (data.register.lastmodificationdate) {
                    $("#lastharvestdate").html(data.register.lastmodificationdate);
                } else {
                    $("#lastharvestdate").html('<%=localization.getString("common.label.notavailable")%>');
                }

                /* Relations */
                var i = 0;
                $('#relations thead tr').clone().appendTo('#relations thead');
                $('#relations').DataTable({
                    "ajax": {
                        url: "<%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>/q/<%=RelationMgr.SUBJECT_REGISTER_OR_OBJECT_REGISTER_ID%>=" + data.register.id,
                        "error": function () {
                            $('#relations thead tr:first').remove();
                            $('#relations_filter').html("");
                        }
                    },
                    "ajaxDataProp": "relations",
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "columns": [
                        {"data": "relation.id"},
                        {"data": "relation.subjectRegister.uri"},
                        {"data": "relation.predicate"},
                        {"data": "relation.objectRegister.uri"}/*,
                        {"data": "relation.status"}*/
                    ],
                    "rowCallback": function (row, data, displayIndex) {
                        $('td:eq(0)', row).html('<a href="detail.jsp?type=relations&id=' + data.relation.id + '">' + data.relation.id + '</a>');
                        $('td:eq(1)', row).html('<a href="detail.jsp?type=registers&id=' + data.relation.subjectRegister.id + '">' + data.relation.subjectRegister.registry.label + ' - ' + data.relation.subjectRegister.label + '</a>');
                        $('td:eq(3)', row).html('<a href="detail.jsp?type=registers&id=' + data.relation.objectRegister.id + '">' + data.relation.objectRegister.registry.label + ' - ' + data.relation.objectRegister.label + '</a>');
                        /*$('td:eq(4)', row).html('<a target="_blank" href="' + data.relation.status + '">' + data.relation.status + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');*/
                        return row;
                    },
                    "pagingType": "full_numbers",
                    "displayLength": 10,
                    "stateSave": true,
                    "language": {"url": "<%=p.get("web.application_root_url")%>res/js/jquery.dataTables.localization/<%=localization.getString("common.localeid")%>.json"},
                    "initComplete": function () {
                        $('#relations thead tr:first th').css('text-align', 'left');
                        $('#relations thead tr:first th').each(function () {
                            var title = $('#relations thead th').eq($(this).index()).text();
                            var filter = $('#relations_filter label').text();
                            $(this).html('<input class="fin' + (i++) + ' form-control input-sm" type="search" placeholder="Filter ' + title + '" aria-controls="relations" />');
                            $(this).css('padding', '5px 0');
                        });
                        var api = this.api();
                        var state = api.state.loaded();
                        if (state) {
                            api.columns().eq(0).each(function (idx) {
                                var colSearch = state.columns[idx].search;
                                $('input.fin' + idx).val(colSearch.search);
                                $('input.fin' + idx).on('keyup change', function () {
                                    api.column(idx).search(this.value).draw();
                                });
                            });
                        } else {
                            api.columns().eq(0).each(function (idx) {
                                $('input.fin' + idx).on('keyup change', function () {
                                    api.column(idx).search(this.value).draw();
                                });
                            });
                        }
                        $('#relations_filter').html("");
                    }
                });
                
            /* Items */
            var i = 0;
            $('#items thead tr').clone().appendTo('#items thead');
            $('#items').DataTable({
                "dom": '<"top">rt<"bottom"lip><"clear">',
                "rowCallback": function (row, data, displayIndex) {
                    //$('td:eq(0)', row).html('<a href="' + data[0] +'" target="_blank">' + data[0] + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                    //return row;
                },
                "pagingType": "full_numbers",
                "displayLength": 10,
                "stateSave": true,
                "language": {"url": "<%=p.get("web.application_root_url")%>res/js/jquery.dataTables.localization/<%=localization.getString("common.localeid")%>.json"},
                "initComplete": function () {
                    $('#items thead tr:first th').css('text-align', 'left');
                    $('#items thead tr:first th').each(function () {
                        var title = $('#items thead th').eq($(this).index()).text();
                        var filter = $('#items_filter label').text();
                        $(this).html('<input class="fin' + (i++) + ' form-control input-sm" type="search" placeholder="Filter ' + title + '" aria-controls="items" />');
                        $(this).css('padding', '5px 0');
                    });
                    var api = this.api();
                    var state = api.state.loaded();
                    if (state) {
                        api.columns().eq(0).each(function (idx) {
                            var colSearch = state.columns[idx].search;
                            $('input.fin' + idx).val(colSearch.search);
                            $('input.fin' + idx).on('keyup change', function () {
                                api.column(idx).search(this.value).draw();
                            });
                        });
                    } else {
                        api.columns().eq(0).each(function (idx) {
                            $('input.fin' + idx).on('keyup change', function () {
                                api.column(idx).search(this.value).draw();
                            });
                        });
                    }
                    $('#items_filter').html("");
                }
            });
            <%
            } else if (type.equals("relations")) {
            %>
                //$("#r-lid").html(data.relation.id);
                //$("#r-sta").html(data.relation.status);
                $("#sr-lid").html('<a href="detail.jsp?type=registers&id=' + data.relation.objectRegister.id + '">' + data.relation.objectRegister.label + '</a>');
                $("#sr-uri").html(data.relation.predicate);
                $("#rr-lid").html('<a href="detail.jsp?type=registers&id=' + data.relation.subjectRegister.id + '">' + data.relation.subjectRegister.label + '</a>');
            <%
                }%>
            });
        });
        </script>
    </body>
</html>