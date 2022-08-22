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
<%@page import="org.json.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<footer>
    <div class="ft1">
        <div class="container">			
            <div class="row">
                <div class="col-xs-6"><%=localization.getString("common.inspire")%>&nbsp;<%=localization.getString("common.title")%></div>
                <div class="col-xs-6 text-right"><%=p.getProperty("application.version")%></div>
            </div>
        </div>
    </div>
    <div class="ft2">
        <div class="container">
            <div class="row">
                <div class="col-sm-15 hidden-xs">
                    <img class="ec-inspire-logo" src="<%=p.get("web.cdn_url")%>img/ec.inspire.logo/logo_en.png"  alt="<%=localization.getString("common.inspire.logo")%>">
                </div>
                <div class="col-sm-15">
                    <h4><%=localization.getString("footer.c1.title")%><a data-toggle="collapse" href="#c1" aria-expanded="false" aria-controls="c1" class="navbar-toggle collapsed">&nbsp;</a></h4>
                    <ol id="c1" class="navbar-collapse collapse">
                        <li><a href="<%=localization.getString("footer.c1.l1")%>"><%=localization.getString("footer.c1.i1")%></a></li>
			<li><a href="<%=localization.getString("footer.c1.l2")%>"><%=localization.getString("footer.c1.i2")%></a></li>
                        <li><a href="<%=localization.getString("footer.c1.l3")%>"><%=localization.getString("footer.c1.i3")%></a></li>
                        <li><a href="<%=localization.getString("footer.c1.l4")%>"><%=localization.getString("footer.c1.i4")%></a></li>
                        <li><a href="<%=localization.getString("footer.c1.l5")%>"><%=localization.getString("footer.c1.i5")%></a></li>
                    </ol>
                </div>
                <div class="col-sm-15">
                    <h4><%=localization.getString("footer.c2.title")%><a data-toggle="collapse" href="#c2" aria-expanded="false" aria-controls="c2" class="navbar-toggle collapsed">&nbsp;</a></h4>
                    <ol id="c2" class="navbar-collapse collapse">
                        <li><a href="<%=localization.getString("footer.c2.l1")%>"><%=localization.getString("footer.c2.i1")%></a></li>
			<li><a href="<%=localization.getString("footer.c2.l2")%>"><%=localization.getString("footer.c2.i2")%></a></li>
                        <li><a href="<%=localization.getString("footer.c2.l3")%>"><%=localization.getString("footer.c2.i3")%></a></li>
                        <li><a href="<%=localization.getString("footer.c2.l4")%>"><%=localization.getString("footer.c2.i4")%></a></li>
                    </ol>
                </div>
                <div class="col-sm-15">
                    <h4><%=localization.getString("footer.c3.title")%>
                        <a data-toggle="collapse" href="#c3" aria-expanded="false" aria-controls="c3" class="navbar-toggle collapsed">&nbsp;</a>
                    </h4>
                    <ol id="c3" class="navbar-collapse collapse">
                        <li><a href="<%=localization.getString("footer.c3.l1")%>"><%=localization.getString("footer.c3.i1")%></a></li>
			<li><a href="<%=localization.getString("footer.c3.l2")%>"><%=localization.getString("footer.c3.i2")%></a></li>
                        <li><a href="<%=localization.getString("footer.c3.l3")%>"><%=localization.getString("footer.c3.i3")%></a></li>
                        <li><a href="<%=localization.getString("footer.c3.l4")%>"><%=localization.getString("footer.c3.i4")%></a></li>
                        <li><a href="<%=localization.getString("footer.c3.l5")%>"><%=localization.getString("footer.c3.i5")%></a></li>
                    </ol>
                </div>
                <div class="col-sm-15">
                    <h4><%=localization.getString("footer.c4.title")%>
                        <a data-toggle="collapse" href="#c4" aria-expanded="false" aria-controls="c4" class="navbar-toggle collapsed">&nbsp;</a>
                    </h4>
                    <ol id="c4" class="navbar-collapse collapse">
                        <li><a href="<%=localization.getString("footer.c4.l1")%>"><%=localization.getString("footer.c4.i1")%></a></li>
			<li><a href="<%=localization.getString("footer.c4.l2")%>"><%=localization.getString("footer.c4.i2")%></a></li>
                        <li><a href="<%=localization.getString("footer.c4.l3")%>"><%=localization.getString("footer.c4.i3")%></a></li>
                        <%---<li><a href="<%=localization.getString("footer.c4.l4")%>"><%=localization.getString("footer.c4.i4")%></a></li>---%>
                        <li><a href="<%=localization.getString("footer.c4.l5")%>"><%=localization.getString("footer.c4.i5")%></a></li>
                        <%---<li id="brexit-content-disclaimer" style="display:none"><a href="//ec.europa.eu/info/brexit-content-disclaimer">Brexit content disclaimer</a></li>---%>
                    </ol>
                </div>
            </div>
        </div>
    </div>
    <div class="ft3">
        <div class="container">
            <div class="pull-right">
                <ol><li><a href="<%=p.get("web.link_about").toString().replace("__lng__",localization.getString("common.localeid"))%>"><%=localization.getString("common.about")%></a></li><li><a href="<%=p.get("web.link_contact").toString().replace("__lng__",localization.getString("common.localeid"))%>"><%=localization.getString("common.contact")%></a></li><li><a href="<%=p.get("web.link_privacy").toString()%>"><%=localization.getString("common.privacy")%></a></li><li><a href="<%=p.get("web.link_legal").toString().replace("__lng__",localization.getString("common.localeid"))%>"><%=localization.getString("common.legal")%></a></li><li><a href="<%=p.get("web.link_cookies").toString()%>"><%=localization.getString("common.cookies")%></a></li></ol>
                <div class="social">
                    <div>
                        <a href="<%=p.get("web.link_social_tw")%>"><i class="fa fa-twitter-square "></i></a>
                        <a href="<%=p.get("web.link_social_li")%>"><i class="fa fa-linkedin-square"></i></a>
                        <a href="<%=p.get("web.link_social_fb")%>"><i class="fa fa-facebook-square"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>

<script>
    let brexitContentDisclaimerElement = $('#brexit-content-disclaimer');
    let publicationDate = new Date(2020,1,1,0,0,1,0);
    let currentDate = new Date();
    
    if(currentDate.getTime()>=publicationDate.getTime()){
        brexitContentDisclaimerElement.removeAttr('style');
    }    
</script>              