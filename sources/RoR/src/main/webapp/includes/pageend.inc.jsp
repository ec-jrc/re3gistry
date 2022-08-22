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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<span id="top-link-block" class="hidden">
    <a href="#top" class="well well-sm"  onclick="$('html,body').animate({scrollTop:0},'slow');return false;">
        <i class="glyphicon glyphicon-chevron-up"></i>
    </a>
</span>

<script src="<%=p.get("web.cdn_url")%>js/bootstrap.min.js"></script>
<script src="<%=p.get("web.cdn_url")%>js/bootstrap-select.min.js"></script>
<script src="<%=p.get("web.cdn_url")%>js/jquery.dataTables.min.js"></script>
<script src="<%=p.get("web.cdn_url")%>js/dataTables.bootstrap.js"></script>
<!--<script src="<%=p.get("web.cdn_url")%>js/jquery.cookieNotice.js"></script>
<script src="<%=p.get("web.cdn_url")%>js/jquery.backToTop.js"></script>-->
<script src="<%=p.get("web.application_root_url")%>res/js/common.js"></script>

<script type="application/ld+json">
{
	"@context": "http://schema.org",
	"@type": "WebSite",
	"url": "<%=p.get("web.application_root_url")%>",
	"potentialAction": {
		"@type": "SearchAction",
		"target": "<%=p.get("web.application_root_url")%>/search.jsp?q={search_term_string}",
		"query-input": "required name=search_term_string"
	}
}
</script>

<%
    String analyticsId = p.getProperty("web.analytics.id");
    String analyticsUrl = p.getProperty("web.analytics.url");
if(analyticsId!=null && analyticsId.length()>0 && analyticsUrl!=null && analyticsUrl.length()>0){
%>
<!-- Piwik -->
<!--<script type="text/javascript">
  var _paq = _paq || [];
  _paq.push(["setDomains", ["*.inspire-regadmin.jrc.ec.europa.eu/ror"]]);
  _paq.push(['trackPageView']);
  _paq.push(['enableLinkTracking']);
  (function() {
    var u="<%=analyticsUrl %>";
    _paq.push(['setTrackerUrl', u+'piwik.php']);
    _paq.push(['setSiteId', <%=analyticsId %>]);
    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
    g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'piwik.js'; s.parentNode.insertBefore(g,s);
  })();
</script>-->
<noscript><p><img src="<%=analyticsUrl %>piwik.php?idsite=<%=analyticsId %>" style="border:0;" alt="" /></p></noscript>
<!-- End Piwik Code -->
<% } %>
