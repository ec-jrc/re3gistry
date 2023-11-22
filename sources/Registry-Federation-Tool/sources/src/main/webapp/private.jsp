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
<%@page import="java.util.Date"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.util.Calendar"%>
<%@page import="eu.europa.ec.ror.managers.DescriptorMgr"%>
<%@page import="eu.europa.ec.ror.model.Descriptor"%>
<%@page import="eu.europa.ec.ror.model.Organization"%>
<%@page import="eu.europa.ec.ror.model.User"%>
<%@page import="eu.europa.ec.ror.managers.UserMgr"%>
<%@page import="eu.europa.ec.ror.model.Register"%>
<%@page import="eu.europa.ec.ror.managers.RegisterMgr"%>
<%@page import="eu.europa.ec.ror.model.Relation"%>
<%@page import="eu.europa.ec.ror.managers.RelationMgr"%>
<%@page import="eu.europa.ec.ror.model.Registry"%>
<%@page import="java.util.List"%>
<%@page import="eu.europa.ec.ror.managers.RegistryMgr"%>
<%@page import="eu.europa.ec.ror.utility.Constants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include file="includes/common.inc.jsp" %>
<%    String type = "private";

    if (request.getParameter("ac") != null && request.getParameter("ac").equals("logout")) {
        String url = p.getProperty("web.application_root_url");
        String logoutLink = p.getProperty("application.ecas.baseurl") + "/cas/logout?url=" + url;
        request.getSession(false).invalidate();
        response.sendRedirect(logoutLink);
    }
%>
<!DOCTYPE html>
<html lang="<%=localization.getString("common.localeid")%>" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp" %>

        <div class="container registry">

            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("private.title")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>
            <%
                if (detailedUser != null) {
                    if (u == null) {
            %>
            <p class="well alert"><%=localization.getString("error.user.notavailable")%></p>
            <%
            } else if (u.getActive()) {
            %>            
            <p><%=localization.getString("private.message.welcome").replace("{0}", "<strong>" + detailedUser.getName() + "</strong>")%> <%=(u.getUserlevel() == Constants.USER_LEVEL_ADMIN) ? " <span class=\"pull-right alert alert-warning admin-notice\">" + localization.getString("private.admin.notice") + "&nbsp;&nbsp;<i class=\"fa fa-user-secret\" aria-hidden=\"true\"></i></span>" : ""%></p>
            <div style="width:100%">
                <button id="show-add-descriptor-btn" class="btn btn-success btn-md" type="button" data-toggle="collapse" data-target="#add-descriptor-div" aria-expanded="false" aria-controls="add-descriptor-div">
                    <span class="fa fa-share-alt-square"></span>  
                    <%=localization.getString("private.add.registry")%>
                </button>
            </div>
            <div id="add-descriptor-div" class="collapse">
                <div class="well">
                    <label for="ar-i"><%=localization.getString("private.label.exchangefile")%>:</label>
                    <input tyle="text" id="add-descriptor-url" style="width:50%" value="" /> 
                    <a href="#" id="add-descriptor-btn" class="btn btn-primary btn-sm">
                        <span class="glyphicon glyphicon-ok"></span>  
                        <%=localization.getString("private.save.label")%>
                    </a>
                    &nbsp;
                    <a href="<%=p.get("web.application_root_url")%>help.jsp#descriptors" class="helpinline" title="<%=localization.getString("common.title.descriptorhelp")%>">
                        <span class="glyphicon glyphicon-question-sign"></span>
                    </a>
                </div>
            </div>

            <h3><%=localization.getString("private.label.registrydescriptor")%></h3>
            <table id="descriptors" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <% if (u.getUserlevel() == Constants.USER_LEVEL_ADMIN) {%>
                        <th style="width:53%"><%=localization.getString("private.label.registryfile")%></th>
                        <th style="width:12%"><%=localization.getString("private.label.organization")%></th>
                            <% } else {%>
                        <th style="width:65%"><%=localization.getString("private.label.registryfile")%></th>
                            <% }%>
                        <th style="width:15%"><%=localization.getString("private.label.addedby")%></th>
                        <th style="width:20%"><%=localization.getString("private.label.action")%></th>
                    </tr>
                </thead>
            </table>
            <p></p>

            <div class="relp">
                <a href="#" class="history" title="<%=localization.getString("private.title.showhistory")%>"><span class="glyphicon glyphicon-time"></span><%=localization.getString("private.label.showhistory")%></a>
                <h3 id="hwjh"><%=localization.getString("private.label.harvestingjobs")%></h3>
            </div>

            <table id="hwsp" class="table table-striped table-bordered tbpers" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <% if (u.getUserlevel() == Constants.USER_LEVEL_ADMIN) {%>
                        <th style="width:28%"><%=localization.getString("private.label.object")%></th>
                        <th style="width:12%"><%=localization.getString("private.label.organization")%></th>
                            <% } else {%>
                        <th style="width:40%"><%=localization.getString("private.label.object")%></th>
                            <% }%>                       
                        <th style="width:8%"><%=localization.getString("private.label.type")%></th>
                        <th style="width:13%"><%=localization.getString("private.label.lastjob")%></th>
                        <th style="width:13%"><%=localization.getString("private.label.nextjob")%></th>
                        <th style="width:10%"><%=localization.getString("private.label.status")%></th>
                        <th style="width:16%"><%=localization.getString("private.label.action")%></th>
                    </tr>
                </thead>
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
            
            <%
            } else {
            %>
            <p class="well alert"><%=localization.getString("error.user.notactive")%></p>
            <%
                }
            } else {
            %>
            <p class="well alert"><%=localization.getString("error.user.notavailable")%></p>
            <%
                }
            %>
        </div>

        <p>&nbsp;</p>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

        <script src="./res/js/jquery.magnific-popup.min.js" async defer></script>

        <script>
            $(function () {
                var j = 0;
                $('#descriptors thead tr').clone().appendTo('#descriptors thead');
                var descriptorsTable = $('#descriptors').DataTable({
                    "ajax": {
                        url: "<%=p.get("web.application_root_url")%>includes/services/get-registry-descriptors.jsp",
                        complete: function () {}
                    },
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "pagingType": "full_numbers",
                    "displayLength": 10,
                    "autoWidth": false,
                    "initComplete": function (settings, json) {
                        $('[data-toggle="tooltip"]').tooltip();

                        $('#descriptors thead tr:first th').css('text-align', 'left');
                        $('#descriptors thead tr:first th').each(function () {
                            var title = $('#descriptors thead th').eq($(this).index()).text();
                            var filter = $('#descriptors_filter label').text();
                            $(this).html('<input class="fin' + (j++) + ' form-control input-sm" type="search" placeholder="Filter ' + title + '" aria-controls="descriptors" />');
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
                        $('#descriptors_filter').html("");
                    }
                });
                
                //Binding click events
                $('#descriptors').on('draw.dt', function(){
                    $('.del-descriptor-btn').on('click', function (e) {
                        e.preventDefault();
                        if (confirm('<%=localization.getString("private.confirm.delete")%>')) {
                            var tmp = $(this).addClass('disabled');
                            var smldr = $(this).append('&nbsp;<img id="smldr" src="res/img/ajax-loader-small.gif"/>');
                            id = $(this).data('id');
                            $.ajax({
                                url: '<%=p.get("web.application_root_url")%>RegistryDescriptorHelper?<%= Constants.PARAMETER_REGISTRY_ACTION_TYPE%>=<%= Constants.PARAMETER_REGISTRY_ACTION_TYPE_DELETE%>&<%= Constants.PARAMETER_REGISTRY_ID%>=' + id
                            }).done(function (result) {
                                if (result.status === 'error') {
                                    descriptorsTable.ajax.reload(null, false);
                                    proceduresTable.ajax.reload(null, false);
                                    tmp.removeClass('disabled');
                                    smldr.remove();
                                    $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-danger">' + result.message + '</div>');
                                    setTimeout(function () {
                                        $('#feedback-add-descriptor').hide();
                                    }, <%=p.getProperty("web.feedback.fadeout.delay")%>);

                                    $('#descriptors thead tr:first').remove();
                                    $('#descriptors_filter').html("");
                                } else {
                                    descriptorsTable.ajax.reload(null, false);
                                    proceduresTable.ajax.reload(null, false);
                                    tmp.removeClass('disabled');
                                    smldr.remove();
                                    $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-success">' + result.message + '</div>');
                                    setTimeout(function () {
                                        $('#feedback-add-descriptor').hide();
                                    }, <%=p.getProperty("web.feedback.fadeout.delay")%>);
                                }
                            }).fail(function (a, b) {
                                tmp.removeClass('disabled');
                                smldr.remove();
                                $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-danger"><%= localization.getString("error.procedure.starting")%></div>');
                                setTimeout(function () {
                                    $('#feedback-add-descriptor').hide();
                                }, <%=p.getProperty("web.feedback.fadeout.delay")%>);

                                $('#descriptors thead tr:first').remove();
                                $('#descriptors_filter').html("");
                            });
                        }
                    });
                });

                var i = 0;
                $('#hwsp thead tr').clone().appendTo('#hwsp thead');
                var proceduresTable = $('#hwsp').DataTable({
                    "ajax": {
                        url: "<%=p.get("web.application_root_url")%>includes/services/get-procedures.jsp",
                        complete: function () {}
                    },
                    "dom": '<"top">rt<"bottom"lip><"clear">',
                    "pagingType": "full_numbers",
                    "displayLength": 10,
                    "order": [[<%if (u != null && u.getUserlevel() != 0) {%>2<%} else {%>3<%}%>, "desc"]],
                    "autoWidth": false,
                    "initComplete": function (settings, json) {
                        $('[data-toggle="tooltip"]').tooltip();
                        $('#hwsp thead tr:first th').css('text-align', 'left');
                        $('#hwsp thead tr:first th').each(function () {
                            var title = $('#hwsp thead th').eq($(this).index()).text();
                            var filter = $('#hwsp_filter label').text();
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
                        $('#hwsp_filter').html("");
                    },
                    "rowCallback": function (row, data, index) {
                        if (data[2] === "Registry") {
                            $('td:eq(0)', row).css('border-left', '2px solid #aaa');
                            $('td:last', row).css('border-right', '2px solid #aaa');
                        }
                    }
                });
                //Binding click events
                $('#hwsp').on('draw.dt', function(){
                    if ($('.perror').length) {
                        $('.perror').unbind();
                        $('.perror').on('click', function (e) {
                            e.preventDefault();
                            $.magnificPopup.open({
                                items: {
                                    src: '<%=p.get("web.application_root_url")%>includes/services/get-procedure-report.jsp?pid=' + $(this).data("id")
                                },
                                type: 'iframe'
                            });
                        });
                    }
                    if ($('.mstart').length) {
                        $('.mstart').unbind();
                        $('.mstart').on('click', function (e) {
                            e.preventDefault();
                            var smldr = $(this).append('&nbsp;<img id="smldr" src="res/img/ajax-loader-small.gif" />');
                            var tmp = $(this).addClass('disabled');
                            $.ajax({
                                url: '<%=p.get("web.application_root_url")%>ProcedureHelper?<%= Constants.PARAMETER_REGISTRY_ACTION_STARTER_TYPE%>=<%= Constants.PARAMETER_REGISTRY_ACTION_STARTER_TYPE_MANUAL%>&<%=Constants.PARAMETER_PROCEDURE_ID%>=' + $(this).data('id')
                            }).done(function (result) {
                                if (result.status === 'error') {
                                    smldr.remove();
                                    tmp.removeClass('disabled');
                                    proceduresTable.ajax.reload(null, false);
                                    $('.relp').after('<div id="feedback-start-procedure" class="alert alert-danger">' + result.message + '</div>');
                                    setTimeout(function () {
                                        $('.alert').hide();
                                    }, <%=p.getProperty("web.feedback.fadeout.delay")%>);

                                    $('#hwsp thead tr:first').remove();
                                    $('#hwsp_filter').html("");
                                } else {
                                    smldr.remove();
                                    tmp.removeClass('disabled');
                                    proceduresTable.ajax.reload(null, false);
                                    $('.relp').after('<div id="feedback-start-procedure" class="alert alert-success">' + result.message + '</div>');
                                    setTimeout(function () {
                                        $('.alert').hide();
                                    }, <%=p.getProperty("web.feedback.fadeout.delay")%>);
                                }
                            }).fail(function (a, b, c) {
                                smldr.remove();
                                tmp.removeClass('disabled');
                                $('.relp').after('<div id="feedback-start-procedure" class="alert alert-danger"><%= localization.getString("error.procedure.starting")%></div>');
                                setTimeout(function () {
                                    $('.alert').hide();
                                }, <%=p.getProperty("web.feedback.fadeout.delay")%>);

                                $('#hwsp thead tr:first').remove();
                                $('#hwsp_filter').html("");

                            });
                        });
                    }
                });

                setInterval(function () {
                    descriptorsTable.ajax.reload(null, false);
                    proceduresTable.ajax.reload(null, false);
                }, <%=p.getProperty("web.table.ajax.updteinterval")%>);

                $('#show-add-descriptor-btn').on('click', function (e) {
                    e.preventDefault();
                    $('#feedback-add-descriptor').hide();
                });
                
                $('#add-descriptor-btn').on('click', function (e) {
                    e.preventDefault();
                    var tmp = $(this).addClass('disabled');
                    $('#add-descriptor-btn').after('<img style="margin-left:10px;vertical-align:top" id="ldr" src="res/img/ajax-loader.gif"/>');
                    url = $('#add-descriptor-url').val();
                    $.ajax({
                        url: '<%=p.get("web.application_root_url")%>RegistryDescriptorHelper?<%= Constants.PARAMETER_REGISTRY_ACTION_TYPE%>=<%= Constants.PARAMETER_REGISTRY_ACTION_TYPE_ADD%>&<%= Constants.PARAMETER_REGISTRY_DESCRIPTORURL%>=' + url
                    }).done(function (result) {
                        if (result.status === 'error') {
                            descriptorsTable.ajax.reload(null, false);
                            tmp.removeClass('disabled');
                            $('#ldr').remove();
                            $('#add-descriptor-div').collapse('hide');
                            $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-danger">' + result.message + '</div>');
                            setTimeout(function () {
                                $('#feedback-add-descriptor').hide();
                            }, <%=p.getProperty("web.feedback.fadeout.delay")%>);
                        } else {
                            descriptorsTable.ajax.reload(null, false);
                            proceduresTable.ajax.reload(null, false);
                            tmp.removeClass('disabled');
                            $('#ldr').remove();
                            $('#add-descriptor-div').collapse('hide');
                            $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-success">' + result.message + '</div>');
                            setTimeout(function () {
                                $('#feedback-add-descriptor').hide();
                            }, <%=p.getProperty("web.feedback.fadeout.delay")%>);
                        }
                    }).fail(function (a, b) {
                        $('#ldr').remove();
                        tmp.removeClass('disabled');
                        $('#add-descriptor-div').collapse('hide');
                        $('#show-add-descriptor-btn').after('<div id="feedback-add-descriptor" class="alert alert-danger"><%= localization.getString("error.procedure.starting")%></div>');
                        setTimeout(function () {
                            $('#feedback-add-descriptor').hide();
                        }, <%=p.getProperty("web.feedback.fadeout.delay")%>);
                    });
                });

                // open history
                $('.history').on('click', function (e) {
                    e.preventDefault();
                    var a = $.magnificPopup.open({
                        type: 'ajax',
                        closeOnBgClick: true,
                        closeOnContentClick: false,
                        closeBtnInside: true,
                        showCloseBtn: true,
                        enableEscapeKey: true,
                        items: {
                            src: "<%=p.get("web.application_root_url")%>includes/services/get-procedureshistory.jsp"
                        },
                        callbacks: {
                            ajaxContentAdded: function (e) {
                                var i = 0;
                                $('#phst thead tr').clone().appendTo('#phst thead');
                                var procedureshistoryTable = $('#phst').DataTable({
                                    "dom": '<"top">rt<"bottom"lip><"clear">',
                                    "pagingType": "full_numbers",
                                    "displayLength": 10,
                                    "autoWidth": false,
                                    "order": [[<%if (u != null && u.getUserlevel() != 0) {%>2<%} else {%>3<%}%>, "desc"]],
                                    "initComplete": function (settings, json) {
                                        $('[data-toggle="tooltip"]').tooltip();

                                        $('#phst thead tr:first th').css('text-align', 'left');
                                        $('#phst thead tr:first th').each(function () {
                                            var title = $('#phst thead th').eq($(this).index()).text();
                                            var filter = $('#phst_filter label').text();
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
                                        $('#phst_filter').html("");
                                    },
                                    "rowCallback": function (row, data, index) {
                                        if (data[2] === "Registry") {
                                            $('td:eq(0)', row).css('border-left', '2px solid #aaa');
                                            $('td:last', row).css('border-right', '2px solid #aaa');
                                        }
                                    }
                                });
                                
                                $('#phst').on('draw.dt',function(){
                                    if ($('.psterror').length) {
                                        $('.psterror').on('click', function (e) {
                                            e.preventDefault();
                                            $.magnificPopup.instance.close();
                                            $.magnificPopup.open({
                                                items: {
                                                    src: '<%=p.get("web.application_root_url")%>includes/services/get-procedurehistory-report.jsp?pid=' + $(this).data("id")
                                                },
                                                type: 'iframe'
                                            });
                                        });
                                    }
                                });

                                if ($('.psterror').length) {
                                    $('.psterror').on('click', function (e) {
                                        e.preventDefault();
                                        $.magnificPopup.instance.close();
                                        $.magnificPopup.open({
                                            items: {
                                                src: '<%=p.get("web.application_root_url")%>includes/services/get-procedurehistory-report.jsp?pid=' + $(this).data("id")
                                            },
                                            type: 'iframe'
                                        });
                                    });
                                }
                            }
                        }
                    });
                });
            });
        </script>
    </body>
</html>