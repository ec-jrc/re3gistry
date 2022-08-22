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
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="org.apache.solr.common.SolrDocument"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>
<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="org.apache.solr.client.solrj.SolrQuery"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="org.apache.solr.client.solrj.impl.HttpSolrServer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="includes/common.inc.jsp" %>
<%    String type = "search";
    String searchTerms = request.getParameter("q");
    searchTerms = StringEscapeUtils.escapeHtml4(searchTerms);
    searchTerms = StringEscapeUtils.escapeEcmaScript(searchTerms);
    searchTerms = StringEscapeUtils.escapeXml11(searchTerms);

    long totalRes = 0;

    int pageParameter = 1;
    try {
        pageParameter = (request.getParameter("p") != null && request.getParameter("p").length() > 0) ? Integer.parseInt(request.getParameter("p")) : 1;
    } catch (Exception e) {
    }

    // Getting paginator properties 
    int maxItemsPerPage = 10;
    int maxItemsAdjacent = 2;
    try {
        maxItemsPerPage = Integer.parseInt(p.getProperty("web.search_max_item_per_page"));
        maxItemsAdjacent = Integer.parseInt(p.getProperty("web.search_page_max_adjacent"));
    } catch (Exception e) {
    }

    SolrDocumentList results = null;
    double searchTime = 0;
    String solrUrl = p.getProperty("solr.url");
    String solrUrlAutocomplete = p.getProperty("solr.url.autocomplete") + "/suggest";

    // if the search term is not null, perform the search
    if (searchTerms != null && searchTerms.length() > 0) {

        searchTime = System.currentTimeMillis();

        SolrServer server = new HttpSolrServer(solrUrl);

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(searchTerms);
        solrQuery.set("qf", "label^4 definition^2 registername^3  id^1");
        solrQuery.setFields("id", "label", "definition", "status", "inscheme", "type", "registername", "registryname", "registryuri");
        solrQuery.set("defType", "dismax");
        solrQuery.setSort("score", SolrQuery.ORDER.desc);
        solrQuery.setStart((pageParameter - 1) * maxItemsPerPage);
        solrQuery.setRows(maxItemsPerPage);

        QueryResponse rsp = server.query(solrQuery);
        results = rsp.getResults();
        totalRes = results.getNumFound();

        searchTime = System.currentTimeMillis() - searchTime;
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
                    <h1><%=localization.getString("common.search.title")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>

            <form id="sfrm">
                <div class="input-group">    
                    <input type="text" name="q" id="q" class="form-control" placeholder="<%= localization.getString("common.search.placeholder")%>" value="<%=(searchTerms != null && searchTerms.length() > 0) ? searchTerms : ""%>" />
                    <span class="input-group-btn">
                        <button class="btn btn-default"><%= localization.getString("common.search.button.label")%></button>
                    </span>
                </div>
            </form>

            <p>&nbsp;</p>

            <%
                if (results == null || searchTerms == null || searchTerms.length() <= 0) {
                    out.print(localization.getString("common.search.informative"));
                } else if (searchTerms != null && searchTerms.length() > 0 && results != null && results.size() <= 0) {
                    out.print(localization.getString("common.search.noresults").replace("{0}", searchTerms));
                } else {
            %>    
            <p><%=localization.getString("common.search.resinfo").replace("{0}", Long.toString(totalRes)).replace("{1}", Double.toString((searchTime / 1000.0)))%></p>
            <%
                for (int i = 0; i < results.size(); ++i) {
                    SolrDocument sdoc = results.get(i);
            %>    
            <div class="row sres">
                <div class="col-md-12">
                    <%
                        String sIco = "";
                        String sTitle = "";
                        if (sdoc.getFieldValue("type") != null && sdoc.getFieldValue("type").equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) {
                            sIco = "fa fa-database";
                            sTitle = localization.getString("common.label.search.registry");
                        } else if (sdoc.getFieldValue("type") != null && sdoc.getFieldValue("type").equals(Constants.DESCRIPTOR_TYPE_REGISTER)) {
                            sIco = "fa fa-list";
                            sTitle = localization.getString("common.label.search.register");
                        } else {
                            sIco = "fa fa-file-text-o";
                            sTitle = localization.getString("common.label.search.item");
                        }
                    %>                    
                    <%
                        String regString = "";
                        if (sdoc.getFieldValue("registername") != null) {
                            regString = " - [Register: <a href=\"" + sdoc.getFieldValue("inscheme") + "\">" + sdoc.getFieldValue("registername") + "</a> <span class=\"glyphicon glyphicon-new-window smallico\"></span>]";
                        } else if (sdoc.getFieldValue("registryname") != null) {
                            regString = " - [Registry: <a href=\"" + sdoc.getFieldValue("registryuri") + "\">" + sdoc.getFieldValue("registryname") + "</a> <span class=\"glyphicon glyphicon-new-window smallico\"></span>]";
                        }
                    %>                  
                    <h4><i class="<%=sIco%>" title="<%=sTitle%>" aria-hidden="true"></i> <a target="_blank" href="<%=sdoc.getFieldValue("id")%>"><strong><%=sdoc.getFieldValue("label")%></strong></a> <span class="glyphicon glyphicon-new-window smallico"></span><%=regString%></h4>
                    <p><%=sdoc.getFieldValue("id")%></p>

                </div>
            </div>
            <%
                }
            %>
            <%@include file="includes/paging.inc.jsp" %>
            <%                }
            %>

            <p>&nbsp;</p>

            <p><%=localization.getString("common.legend")%> &nbsp;<span class="fa fa-database"></span> <%=localization.getString("common.label.search.registry")%> | <span class="fa fa-list"></span> <%=localization.getString("common.label.search.register")%> | <span class="fa fa-file-text-o"></span> <%=localization.getString("common.label.search.item")%></p>

            <p>&nbsp;</p>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>
        <script src="./res/js/jquery-ui.min.js"></script>
        <script>
            $('#q').autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '<%=solrUrlAutocomplete%>',
                        data: {
                            suggest: true,
                            'suggest.build': true,
                            'suggest.dictionary': 'rorSuggester',
                            'suggest.q': request.term,
                            wt: 'jsonp'
                        },
                        dataType: 'jsonp',
                        jsonp: 'json.wrf',
                        success: function (data) {
                            response($.map(data.suggest.rorSuggester[request.term].suggestions, function (item) {
                                return {label: item.term.toLowerCase()};
                            }));
                        }
                    });                    
                },
                appendTo: $("form:first"),
                minLength: 1,
                mustMatch: true,
                select: function (event, ui) {
                    $('#q').val(ui.item.value);
                    $('#sfrm').submit();
                }
            });
            $("#q").data("ui-autocomplete")._renderMenu = function (ul, items) {
                var that = this;
                ul.attr("class", "nav nav-pills nav-stacked");
                $.each(items, function (index, item) {
                    that._renderItemData(ul, item);
                });
            };
            function processSuggestions(data) {
                console.log(data.suggest.rorSuggester.s);
                $.map(data.suggest.rorSuggester.s.suggestions, function (item) {
                    return {label: item.term.toLowerCase()};
                });
            }
        </script>

    </body>
</html>