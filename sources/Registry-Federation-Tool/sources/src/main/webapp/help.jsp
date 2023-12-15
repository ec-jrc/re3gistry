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

<%@include file="includes/common.inc.jsp" %>
<%    String type = "help";
%>
<!DOCTYPE html>
<html lang="<%=p.get("web.default_locale")%>" role="document">
    <%@include file="includes/head.inc.jsp" %>
    <body>
        <%@include file="includes/header.inc.jsp" %>

        <div class="container registry">


            <div class="row bdot">
                <div class="col-sm-9">
                    <h1><%=localization.getString("common.label.help")%></h1>
                </div>
                <div class="col-sm-3">
                    <%@include file="includes/topwidget.inc.jsp" %>
                </div>
                <div class="hrl">
                </div>
            </div>
                
            <p>The INSPIRE register federation is a distributed federation of registers related to the INSPIRE Directive.</p>
            <p>The development of the INSPIRE Register Federation was undertaken by the JRC as part of ARE3NA, ISA Action 1.17</p>
            <p>The European Commission is operating a (central) registry providing common INSPIRE registers, e.g. to manage INSPIRE code lists or themes. Where Member States extend these code lists, they are obliged to make available the extended values in local registers. Such local registers can also support other use cases, e.g. the management of organisations in a country that have to implement INSPIRE, or thematic vocabularies, such as those provided by EU institutions and bodies. </p>
            <p>The Register of Registers  (RoR) is the central access point to the federation. It provides information about the registers included in the federation and the relationships between them, and allows searching for registers and the items contained in them. The RoR can be accessed using the RoR's graphical user interface or API, both of which are publicly accessible.</p>
            
            <h3 id="account">Getting an account</h3>    
            <p>For access to administration area of the RoR, a user account is required. To obtain a user account, you need to:</p>
            <ol class="hlp">
                <li>Get an <a href="https://webgate.ec.europa.eu/cas">ECAS account</a> (if you do not already have one)</li>
                <li>Send an e-mail to <a title="Prefilled e-mail model" href="mailto:jrc-inspire-support@jrc.ec.europa.eu?Subject=INSPIRE%20Register%20Federation%20-%20Account%20request&Body=Dear%20Register%20Federation%20team%2C%0AI%20would%20like%20to%20have%20access%20to%20the%20private%20area%20of%20the%20INSPIRE%20Register%20Federation%20system.%20Below%20you%20can%20find%20the%20details%20for%20the%20account.%0A%0A-%20ECAS%20uid%3A%20...%0A-%20Organization%20name%3A%20...%0A-%20Email%20for%20automatic%20notifications%3A%20...%0A%0AThank%20you%2C%0A">jrc-inspire-support@jrc.ec.europa.eu</a> <span class="glyphicon gly"></span> with your ECAS uid, the organization you are affiliated with and an e-mail address for automatic notifications</li>
            </ol>    
            <p>After your request has been validated, you will receive confirmation email.</p>
            
            <h3 id="descriptors">Descriptors</h3>
            <p>The idea of the descriptors is to provide metadata and data about the registries and registers to be included in the federation in a RDF/XML document (subsequently called "Registry descriptor" or "Register descriptor") that is publicly available through an http URI.</p>
            <p>You can find detailed information on the Descriptors and  Field to be provided in the <a href="https://webgate.ec.europa.eu/fpfis/wikis/display/InspireMIG/Registry+federation+requirements">INSPIRE Register Federation conformance classes wiki page</a>.</p>

            <p>&nbsp;</p>
            <p>&nbsp;</p>
        </div>

        <%@include file="includes/footer.inc.jsp" %>
        <%@include file="includes/pageend.inc.jsp" %>

    </body>
</html>