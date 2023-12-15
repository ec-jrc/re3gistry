<%-- 
/*
 * Copyright 2010,2017 EUROPEAN UNION
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
<%@page import="eu.europa.ec.ror.model.Register"%>
<%@page import="eu.europa.ec.ror.managers.RegisterMgr"%>
<%@page import="eu.europa.ec.ror.model.Relation"%>
<%@page import="eu.europa.ec.ror.managers.RelationMgr"%>
<%@page import="eu.europa.ec.ror.model.Registry"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.ror.managers.RegistryMgr"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<%    String type = (request.getParameter("type") != null) ? request.getParameter("type") : "registries"; %>
<!DOCTYPE html>
<html lang="<%=localization.getString("common.localeid")%>" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp" %>

        <div class="container registry">

            <%if (type.equals("registries")) {%>
            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("registries.title")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>

            <table id="registries" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th><%=localization.getString("common.table.label")%></th>
                        <th><%=localization.getString("common.table.publisher")%></th>
                        <th><%=localization.getString("common.table.uri")%></th>
                    </tr>
                </thead>
            </table>
            
            <p>&nbsp;</p>
                    
            <div class="well">
                <%=localization.getString("common.resturl")%>: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.registries")%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.registries")%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
                <div class="pull-right"><a role="button" data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample"><span class="glyphicon glyphicon-cog"></span>&nbsp;<%=localization.getString("common.wsinfo")%></a></div>
            </div>
                        <div class="collapse" id="collapseExample">
                <div class="well">
                    <p><strong><%=localization.getString("common.wadllocation")%>:</strong> <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%>rest/application.wadl"><%=p.get("web.application_root_url")%>rest/application.wadl</a>  <span class="glyphicon glyphicon-new-window smallico"></span></p>
                    <p><strong><%=localization.getString("common.restfilter")%>:</strong> <span class="serviceurl"><%=p.get("web.application_root_url")%>rest/<strong>&lt;registries|registers|relations&gt;</strong>/q/<strong>&lt;field&gt;</strong>=<strong>&lt;value&gt;</strong></span></p>
                </div>
            </div>
            
            <%} else if (type.equals("registers")) {%>

            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("registers.title")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>

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
            <%=localization.getString("common.resturl")%>: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            <div class="pull-right"><a role="button" data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample"><span class="glyphicon glyphicon-cog"></span>&nbsp;<%=localization.getString("common.wsinfo")%></a></div>
            </div>
            
                        <div class="collapse" id="collapseExample">
                <div class="well">
                    <p><strong><%=localization.getString("common.wadllocation")%>:</strong> <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%>rest/application.wadl"><%=p.get("web.application_root_url")%>rest/application.wadl</a>  <span class="glyphicon glyphicon-new-window smallico"></span></p>
                    <p><strong><%=localization.getString("common.restfilter")%>:</strong> <span class="serviceurl"><%=p.get("web.application_root_url")%>rest/<strong>&lt;registries|registers|relations&gt;</strong>/q/<strong>&lt;field&gt;</strong>=<strong>&lt;value&gt;</strong></span></p>
                </div>
            </div>
            
                        
            <%} else if (type.equals("relations")) {%>
            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("relations.title")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>

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
            <div class="well">
            <%=localization.getString("common.resturl")%>: <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>"><%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%></a> <span class="glyphicon glyphicon-new-window smallico"></span>
            <div class="pull-right"><a role="button" data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample"><span class="glyphicon glyphicon-cog"></span>&nbsp;<%=localization.getString("common.wsinfo")%></a></div>
            </div>
            <div class="collapse" id="collapseExample">
                <div class="well">
                    <p><strong><%=localization.getString("common.wadllocation")%>:</strong> <a class="serviceurl" target="_blank" href="<%=p.get("web.application_root_url")%>rest/application.wadl"><%=p.get("web.application_root_url")%>rest/application.wadl</a>  <span class="glyphicon glyphicon-new-window smallico"></span></p>
                    <p><strong><%=localization.getString("common.restfilter")%>:</strong> <span class="serviceurl"><%=p.get("web.application_root_url")%>rest/<strong>&lt;registries|registers|relations&gt;</strong>/q/<strong>&lt;field&gt;</strong>=<strong>&lt;value&gt;</strong></span></p>
                </div>
            </div>            
            <%}%>
                
            <p>&nbsp;</p>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
        <script>
            $(document).ready(function () {
                <%if (type.equals("registries")) {%>
                /* Registry */
                var i = 0;
                $('#registries thead tr').clone().appendTo('#registries thead');
                $('#registries').DataTable({
                    "ajax": {
                        "url":"<%=p.get("web.application_root_url")%><%=p.get("rest.path.registries")%>",
                        "error": function () {
                            $('#registries thead tr:first').remove();
                            $('#registries_filter').html("");
                        }
                    },
                    "ajaxDataProp": "registries",
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "columns": [
                        {"data": "registry.id"},
                        {"data": "registry.publisher.label"},
                        {"data": "registry.uri"}
                    ],
                    "rowCallback": function (row, data, displayIndex) {
                        $('td:eq(0)', row).html('<a href="detail.jsp?type=registries&id=' + data.registry.id + '">' + data.registry.label + '</a>');
                        $('td:eq(1)', row).html('<a target="_blank" href="'+ data.registry.publisher.uri + '">' + data.registry.publisher.label + '</a>  <span class="glyphicon glyphicon-new-window smallico"></span>');
                        $('td:eq(2)', row).html('<a target="_blank" href="' + data.registry.uri + '">' + data.registry.uri + '</a> <span class="glyphicon glyphicon-new-window smallico"></span>');
                        return row;
                    },
                    "pagingType": "full_numbers",
                    "displayLength": 10,
                    "stateSave": true,
                    "language": {"url": "<%=p.get("web.application_root_url")%>res/js/jquery.dataTables.localization/<%=localization.getString("common.localeid")%>.json"},
                    "initComplete": function () {
                        $('#registries thead tr:first th').css('text-align', 'left');
                        $('#registries thead tr:first th').each(function () {
                            var title = $('#registries thead th').eq($(this).index()).text();
                            $(this).html('<input class="fin' + (i++) + ' form-control input-sm" type="search" placeholder="Filter ' + title + '" aria-controls="registries" />');
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
                        $('#registries_filter').html("");
                    }
                });
                <%} else if (type.equals("registers")) {%>
                /*Register */
                var i = 0;
                $('#registers thead tr').clone().appendTo('#registers thead');
                $('#registers').DataTable({
                    "ajax": {
                        "url":"<%=p.get("web.application_root_url")%><%=p.get("rest.path.registers")%>",
                        "error": function () {
                            $('#registers thead tr:first').remove();
                            $('#registers_filter').html("");
                        }
                    },
                    "ajaxDataProp": "registers",
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "columns": [
                        {"data": "register.label"},
                        {"data": "register.registry.uri"},
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
                <%} else if (type.equals("relations")) {%>
                /*Relation */
                var i = 0;
                $('#relations thead tr').clone().appendTo('#relations thead');
                $('#relations').DataTable({
                    "ajax": {
                        "url":"<%=p.get("web.application_root_url")%><%=p.get("rest.path.relations")%>",
                        "error": function () {
                                    $('#relations thead tr:first').remove();
                                    $('#relations_filter').html("");
                                }
                    },
                    "ajaxDataProp": "relations",
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "columns": [
                        {"data": "relation.id"},
                        {"data": "relation.relatedregister.uri"},
                        {"data": "relation.predicate"},
                        {"data": "relation.sourceregister.uri"}/*,
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
                <%}%>
            });
        </script>
    </body>
</html>