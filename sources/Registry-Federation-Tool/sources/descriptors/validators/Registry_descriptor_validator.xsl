<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:dcat="http://www.w3.org/ns/dcat#"
	xmlns:dct="http://purl.org/dc/terms/" 
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:foaf="http://xmlns.com/foaf/0.1/" 
	xmlns:locn="http://www.w3.org/ns/locn#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:schema="http://schema.org/" 
	xmlns:skos="http://www.w3.org/2004/02/skos/core#"
	xmlns:vcard="http://www.w3.org/2006/vcard/ns#" 
	exclude-result-prefixes="dct schema foaf rdfs rdf dc locn skos vcard dcat">

	<!-- ################################### -->
	<!-- ## INSPIRE Register Federation   ## -->
	<!-- ## Registry descriptor validator ## -->
	<!-- ##	Version: 0.5 beta		      ## -->
	<!-- ################################### -->
	
	<xsl:param name="json" />
	<xsl:output omit-xml-declaration="yes" />
	
	<!-- Output method selector -->
	<xsl:template match="rdf:RDF">
		<xsl:choose>
			<xsl:when test="string-length($json)!=0">
				<xsl:call-template name="json" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="html" />
			</xsl:otherwise>
		</xsl:choose>					
	</xsl:template>
	
	<!-- JSON root template -->
	<xsl:template name="json">
		{
		<xsl:apply-templates mode="json" />
		}
	</xsl:template>
	
	<!-- HTML root template -->
	<xsl:template name="html">
		<html lang="en">
			<head>			
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
				<meta name="author" content="European Commission - Joint Research Centre"/>
				
				<title>INSPIRE Register Federation - Registry descriptor validator</title>
				
				<link rel="stylesheet" href="http://inspire.ec.europa.eu/cdn/latest/css/ec.eu.css"/>
				<link rel="stylesheet" href="http://inspire.ec.europa.eu/cdn/latest/css/dataTables.bootstrap.css"/>
				<link rel="stylesheet" href="http://inspire.ec.europa.eu/registry/res/css/registry.css"/>
				
				<link rel="shortcut icon" href="http://inspire.ec.europa.eu/cdn/latest/img/ec.ico/favicon.ico"/>
				
				<script type="text/javascript" src="http://inspire.ec.europa.eu/cdn/latest/js/jquery.min.js"></script>
				 <script src="https://bootswatch.com/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
				
				
				<script src="http://inspire.ec.europa.eu/cdn/latest/js/bootstrap-select.min.js"></script>
				<script src="http://inspire.ec.europa.eu/cdn/latest/js/jquery.dataTables.min.js"></script>
				<script src="http://inspire.ec.europa.eu/cdn/latest/js/dataTables.bootstrap.js"></script>
					

				<!--[if lt IE 9]>
				<script src="http://inspire.ec.europa.eu/cdn/latest/js/html5shiv.min.js"></script>
				<script src="http://inspire.ec.europa.eu/cdn/latest/js/respond.min.js"></script>
				<![endif]-->
				
				<style type="text/css">
				h2{
					border-bottom: 1px solid #eee;
					padding-bottom: 3px;
				}
				section{
					margin-bottom:40px
				}
				#other-info{
					margin-top:50px
				}
				.er{font-weight:bold;color:#f00}
				.wa{font-weight:bold;color:#f93}
				.d-desc, abbr[title], abbr[title] a{color:#404040; border-bottom:0;text-decoration:none;cursor:help}
				</style>
				
				<script type="text/javascript">
				var i=0;
				$('table.list thead tr').clone().appendTo('table.list thead');
				$('table.list').DataTable( {
					"pagingType":"full_numbers",
					"displayLength":50,
					"stateSave": true,
					"dom": '&lt;"top"&gt;rt&lt;"bottom"lip&gt;&lt;"clear"&gt;',
					"initComplete": function () {
					$('table.list thead tr:first th').css('text-align','left');
					$('table.list thead tr:first th').each( function () {
						var title = $('table.list thead th').eq($(this).index()).text();
						var filter = 'Filter';
						$(this).html( '&lt;input class="fin'+(i++)+' form-control input-sm" type="search" placeholder="'+filter+' '+title+'" aria-controls="tbl" /&gt;' );
						$(this).css('padding','1px 0');
					});			
					var api = this.api();
					var state = api.state.loaded();
					if(state){
						api.columns().eq(0).each(function(idx) {
							var colSearch = state.columns[idx].search;	
							$('input.fin'+idx).val(colSearch.search);
							$('input.fin'+idx).on('keyup change',function(){
								api.column(idx).search(this.value).draw();
							} );
						} );
					}else{
						api.columns().eq(0).each(function(idx) {
							$('input.fin'+idx).on('keyup change',function(){
								api.column(idx).search(this.value).draw();
							} );
						} );
					}
					$('#tbl_filter').html("");
					//$("table.list").DataTable().draw();
				}
				});
				</script>
      
			</head>
			<body>
				<header>				
					<div class="hb1">
						<div class="container relative">			
							<a class="ec-logo" href="#"><img src="http://inspire.ec.europa.eu/cdn/latest/img/ec.logo/logo_en.gif" /></a>
							<span class="mt">INSPIRE</span>			
							<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#topm" aria-expanded="false" aria-controls="navbar">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>							
							<div id="topm" class="topm navbar-collapse collapse">						
								<ul class="lnk">
									<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/ror/about">About</a></li><li><a href="mailto:inspire-registry-dev@jrc.ec.europa.eu">Contact</a></li><li><a href="http://ec.europa.eu/geninfo/legal_notices_en.htm">Legal notice</a></li>
								</ul>		
								<select data-live-search="true" class="selectpicker">
									<option selected="selected" value="en">English (en)</option>
								</select>
							</div>							
						</div>        
					</div>
					<div class="hb2">
						<div class="container">
							<span>Register Federation</span>
						</div>
					</div>
					<div class="hb3 hidden-xs">
						<div class="container">
						<ol class="breadcrumb" vocab="http://schema.org/" typeof="BreadcrumbList">
						<li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="http://ec.europa.eu/index_en.htm"><span property="name">European Commission</span></a></li>
						<li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="http://inspire.ec.europa.eu"><span property="name">INSPIRE</span></a></li>
						<li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="http://inspire.ec.europa.eu/ror"><span property="name">INSPIRE register federation</span></a></li>
						<li property="itemListElement" typeof="ListItem"><a property="item" typeof="WebPage" href="#"><span property="name">Registry descriptor validator</span></a></li>
						</ol>
						</div>
					</div> 
					  
				</header>			
				
				<xsl:choose>
					<xsl:when test="/rdf:RDF/*[rdf:type/@rdf:resource='http://www.w3.org/ns/dcat#Catalog']|/rdf:RDF/dcat:Catalog">
						<xsl:apply-templates mode="html" />
					</xsl:when>
					<xsl:otherwise>
						<div class="container">
						<p>&#160;</p>
						<div class="alert alert-danger">
						<p><strong>You are applying the wrong validator stylesheet!</strong></p>
						<p>The validator stylesheet that you applied is a "Registry Descriptor" validator, but this files seems to be a "Register Descriptor".</p>
						<p>Please check if the file that you are triyng to validate is a Registry descriptor file or change the validator stylesheet.</p>
						<p>More information: https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_xsl_validators</p>
						</div>
						<p>&#160;</p>
						</div>
					</xsl:otherwise>
				</xsl:choose>	
				
				<xsl:call-template name="footer" />

			</body>
		</html>
	</xsl:template>
	
	<!-- Core template: mode=json -->	
	<xsl:template match="/rdf:RDF/*[rdf:type/@rdf:resource='http://www.w3.org/ns/dcat#Catalog']|/rdf:RDF/dcat:Catalog" mode="json">	
		"conformanceClasses":[
		<xsl:choose>
			<xsl:when test="(
					(string-length(@rdf:about)!=0) and
					((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
					(string-length(dct:publisher/*/@rdf:about)!=0) and
					((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
					(string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0)
			)">
				{"conformanceClass":"1"}
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="(
			  (string-length(@rdf:about)!=0) and
			  ((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
			  (string-length(dct:publisher/*/@rdf:about)!=0) and
			  ((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
			  (string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0) and
			  (string-length(dct:accrualPeriodicity/@rdf:resource)!=0)
		)">
			,{"conformanceClass":"2"}
		</xsl:if>
		<xsl:if test="(
			(string-length(@rdf:about)!=0) and
			((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
			(string-length(dct:publisher/*/@rdf:about)!=0) and
			((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
			(string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0) and
			((string-length(dct:description[@xml:lang='en'])!=0) or (string-length(dct:description)!=0))
		)">
			,{"conformanceClass":"3"}
		</xsl:if>
		],
		"fields":{
			"registryUri":"<xsl:value-of select="@rdf:about" />",
			<xsl:choose>
				<xsl:when test="string-length(dct:title[@xml:lang='en'])!=0">
					<xsl:variable name="newLabel">
						<xsl:call-template name="string-replace-all">
							<xsl:with-param name="text" select="dct:title[@xml:lang='en']" />
							<xsl:with-param name="replace" select="'&quot;'" />
							<xsl:with-param name="by" select="''" />
						</xsl:call-template>
					</xsl:variable>
					"registryName":"<xsl:value-of select="$newLabel" />",
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length(dct:title)!=0">
							<xsl:variable name="newLabel">
								<xsl:call-template name="string-replace-all">
									<xsl:with-param name="text" select="dct:title" />
									<xsl:with-param name="replace" select="'&quot;'" />
									<xsl:with-param name="by" select="''" />
								</xsl:call-template>
							</xsl:variable>
							"registryName":"<xsl:value-of select="$newLabel" />",
						</xsl:when>
						<xsl:otherwise>
							"registryName":"",
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>	
			<xsl:choose>
				<xsl:when test="string-length(dct:description[@xml:lang='en'])!=0">
					<xsl:variable name="newDefinition">
						<xsl:call-template name="string-replace-all">
							<xsl:with-param name="text" select="dct:description[@xml:lang='en']" />
							<xsl:with-param name="replace" select="'&quot;'" />
							<xsl:with-param name="by" select="''" />
						</xsl:call-template>
					</xsl:variable>
					"registryDescription":"<xsl:value-of select="normalize-space($newDefinition)" />",
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length(dct:description)!=0">
							<xsl:variable name="newDefinition">
								<xsl:call-template name="string-replace-all">
									<xsl:with-param name="text" select="dct:description" />
									<xsl:with-param name="replace" select="'&quot;'" />
									<xsl:with-param name="by" select="''" />
								</xsl:call-template>
							</xsl:variable>
							"registryDescription":"<xsl:value-of select="normalize-space($newDefinition)" />",
						</xsl:when>
						<xsl:otherwise>
							"registryDescription":"",
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>	
			"registryPublisherUri":"<xsl:value-of select="dct:publisher/*/@rdf:about" />",
			<xsl:choose>
				<xsl:when test="string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0">
					<xsl:variable name="newPubName">
						<xsl:call-template name="string-replace-all">
							<xsl:with-param name="text" select="dct:publisher/*/foaf:name[@xml:lang='en']" />
							<xsl:with-param name="replace" select="'&quot;'" />
							<xsl:with-param name="by" select="''" />
						</xsl:call-template>
					</xsl:variable>
					"registryPublisherName":"<xsl:value-of select="$newPubName" />",
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length(dct:publisher/*/foaf:name)!=0">
							<xsl:variable name="newPubName">
								<xsl:call-template name="string-replace-all">
									<xsl:with-param name="text" select="dct:publisher/*/foaf:name" />
									<xsl:with-param name="replace" select="'&quot;'" />
									<xsl:with-param name="by" select="''" />
								</xsl:call-template>
							</xsl:variable>
							"registryPublisherName":"<xsl:value-of select="$newPubName" />",
						</xsl:when>
						<xsl:otherwise>
							"registryPublisherName":"",
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:variable name="processedmail">
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="dct:publisher/*/foaf:mbox/@rdf:resource" />
					<xsl:with-param name="replace" select="'mailto:'" />
					<xsl:with-param name="by" select="''" />
				</xsl:call-template>
			</xsl:variable>			
			"registryPublisherEmail":"<xsl:value-of select="$processedmail" />",
			"registryPublisherHomepage":"<xsl:value-of select="dct:publisher/*/foaf:homepage/@rdf:resource" />",
			"registryUpdateFrequency":"<xsl:value-of select="dct:accrualPeriodicity/@rdf:resource" />"			
		},
		"registers":[
		<xsl:for-each select="dcat:dataset">
			{"register":{				
				<xsl:variable name="uri" select="normalize-space(@rdf:resource|*/@rdf:about)"/>
				<xsl:variable name="url" select="normalize-space(/*//*[@rdf:about = $uri]/dcat:distribution[dct:format/@rdf:resource='http://publications.europa.eu/resource/authority/file-type/RDF_XML']/dcat:downloadURL/@rdf:resource)"/>
				"registerUri":"<xsl:value-of select="$uri"/>",
				"registerUrl":"<xsl:value-of select="$url" />"		
				}
			}
			<xsl:if test="position() != last()">
				<xsl:text>,</xsl:text>
			</xsl:if>
		</xsl:for-each>			
		]   
	</xsl:template>
	
	<!-- Core template: mode=html -->
	<xsl:template match="/rdf:RDF/*[rdf:type/@rdf:resource='http://www.w3.org/ns/dcat#Catalog']|/rdf:RDF/dcat:Catalog" mode="html">
    <div class="container registry">
	<section id="results">
		<div class="row bdot">
			<div class="col-sm-9">
				<h1>Registry descriptor validator</h1>
			</div>
			<div class="col-sm-3"></div>
			<div class="hrl"></div>
		</div>
		
		<p>The results of the validation to the Registry descriptor are available below.</p>
		
		<h3>Validation results</h3>
		<xsl:choose>                                                                              
		<xsl:when test="(
			(string-length(@rdf:about)!=0) and
			((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
			(string-length(dct:publisher/*/@rdf:about)!=0) and
			((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
			(string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0)
		)">
		<p>
		This exchange file is conformant to the following conformance classes:
		</p>
		</xsl:when>
		<xsl:otherwise>
		<div class="alert alert-danger">
		This exchange file is <strong>not valid</strong>. Please check the errors reported in the <a href="#summary">validation summary</a> and the <a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements">requirements <span class="glyphicon glyphicon-share"></span></a>.
		</div>
		</xsl:otherwise>
		</xsl:choose>
		<ul>
			<li>
			<xsl:choose>
				<xsl:when test="(
					(string-length(@rdf:about)!=0) and
					((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
					(string-length(dct:publisher/*/@rdf:about)!=0) and
					((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
					(string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0)
				)">
					<div class="alert alert-success">
					<a href="#cc-core" class="ok" title="Conformant. Click to inspect the validation summary.">Core Conformance Class</a>
					</div>
				</xsl:when>
				<xsl:otherwise>
<!--				<div class="alert alert-danger">
					<a href="#cc-core" class="er" title="Not Conformant. Click to inspect the validation summary.">Core Conformance Class [Not Conformant. Click to inspect the validation summary]</a>
					</div>
-->				</xsl:otherwise>
			</xsl:choose>
			</li>
			<li>
				<xsl:choose>
				  <xsl:when test="(
					  (string-length(@rdf:about)!=0) and
					  ((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
					  (string-length(dct:publisher/*/@rdf:about)!=0) and
					  ((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
					  (string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0) and
					  (string-length(dct:accrualPeriodicity/@rdf:resource)!=0)
				  )">
						<div class="alert alert-success">
						<a href="#cc-harvesting" class="ok" title="Conformant. Click to inspect the validation summary.">Automatic Harvesting Conformance Class</a>
						</div>
				  </xsl:when>
				  <xsl:otherwise>
<!--					<div class="alert alert-danger">
						<a href="#cc-harvesting" class="er" title="Not Conformant. Click to inspect the validation summary.">Automatic Harvesting Conformance Class [Not Conformant. Click to inspect the validation summary]</a>
						</div>
-->					</xsl:otherwise>
				</xsl:choose>
			</li>
			<li>
			<xsl:choose>                                                                              
			<xsl:when test="(
				(string-length(@rdf:about)!=0) and
				((string-length(dct:title[@xml:lang='en'])!=0) or (string-length(dct:title)!=0)) and
				(string-length(dct:publisher/*/@rdf:about)!=0) and
				((string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0) or (string-length(dct:publisher/*/foaf:name)!=0)) and
				(string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0) and
				((string-length(dct:description[@xml:lang='en'])!=0) or (string-length(dct:description)!=0))
			)">
				<div class="alert alert-success">
				<a href="#cc-content" class="ok" title="Conformant. Click to inspect the validation summary.">Content Conformance Class</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
<!--				
				<div class="alert alert-danger">
				<a href="#cc-content" class="er" title="Not Conformant. Click to inspect the validation summary.">Content Conformance Class [Not Conformant. Click to inspect the validation summary]</a>
				</div>
-->
			</xsl:otherwise>
			</xsl:choose>
			</li>
		</ul>
		
		<p>The detailed report related to each of the conformance classes are available below.</p>
		
	</section>	
	<section id="summary">	
		<section class="cc" id="cc-core">
			<h2>Core conformance class</h2>
			
			<!-- Registry URI -->
			<div class="row">
			<div class="col-md-3">Registry URI:</div>
			<xsl:choose>
				<xsl:when test="string-length(@rdf:about)!=0">
					<div class="col-md-9"><a target="_blank" href="{@rdf:about}"><xsl:value-of select="@rdf:about" /></a></div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					<xsl:call-template name="ccc-error-required" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			<!-- Registry name -->
			<div class="row">
			<div class="col-md-3">Registry name:</div>		
			<xsl:choose>
				<xsl:when test="string-length(dct:title[@xml:lang='en'])!=0">
					<div class="col-md-9"><xsl:value-of select="dct:title[@xml:lang='en']" /></div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length(dct:title)!=0">
							<div class="col-md-9"><xsl:value-of select="dct:title" /></div>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="ccc-error-required" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			<!-- Publisher URI -->
			<div class="row">
			<div class="col-md-3">Publisher URI:</div>
			<xsl:choose>
				<xsl:when test="string-length(dct:publisher/*/@rdf:about)!=0">
					<div class="col-md-9"><xsl:value-of select="dct:publisher/*/@rdf:about" /></div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					<xsl:call-template name="ccc-error-required" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			<!-- Publisher name -->
			<div class="row">
			<div class="col-md-3">Publisher name:</div>
			<xsl:choose>
					<xsl:when test="string-length(dct:publisher/*/foaf:name[@xml:lang='en'])!=0">
						<div class="col-md-9"><xsl:value-of select="dct:publisher/*/foaf:name[@xml:lang='en']" /></div>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="string-length(dct:publisher/*/foaf:name)!=0">
								<div class="col-md-9"><xsl:value-of select="dct:publisher/*/foaf:name" /></div>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="ccc-error-required" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<!-- Publisher e-mail -->
			<div class="row">
			<div class="col-md-3">Publisher e-mail:</div>
			<xsl:choose>
				<xsl:when test="string-length(dct:publisher/*/foaf:mbox/@rdf:resource)!=0">
					<xsl:variable name="processedmail">
						<xsl:call-template name="string-replace-all">
							<xsl:with-param name="text" select="dct:publisher/*/foaf:mbox/@rdf:resource" />
							<xsl:with-param name="replace" select="'mailto:'" />
							<xsl:with-param name="by" select="''" />
						</xsl:call-template>
					</xsl:variable>					
					<div class="col-md-9"><a target="_blank" href="{dct:publisher/*/foaf:mbox/@rdf:resource}"><xsl:value-of select="$processedmail" /></a></div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					<xsl:call-template name="ccc-error-required" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			<!-- Publisher homepage -->
			<div class="row">
			<div class="col-md-3">Publisher homepage:</div>
			<xsl:choose>
				<xsl:when test="string-length(dct:publisher/*/foaf:homepage/@rdf:resource)!=0">
					<div class="col-md-9"><a target="_blank" href="{dct:publisher/*/foaf:homepage/@rdf:resource}"><xsl:value-of select="dct:publisher/*/foaf:homepage/@rdf:resource" /></a></div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					<xsl:call-template name="ccc-warning-recommended" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			
		</section>
		
		<section class="cc" id="cc-harvesting">
			<h2>Automatic harvesting conformance class</h2>
			
			<!-- Update frequency -->
			<div class="row">
			<div class="col-md-3">Update frequency:</div>
			<xsl:choose>
				<xsl:when test="string-length(dct:accrualPeriodicity/@rdf:resource)!=0">
					<div class="col-md-9"><xsl:value-of select="dct:accrualPeriodicity/@rdf:resource" /></div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					<xsl:call-template name="ahcc-error-required" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>

		</section>
		
		<section class="cc" id="cc-content">
			<h2>Content conformance class</h2>
		
			<!-- Description -->
			<div class="row">
			<div class="col-md-3">Description:</div>
			<xsl:choose>
				<xsl:when test="string-length(dct:description[@xml:lang='en'])!=0">
					<div class="col-md-9"><xsl:value-of select="dct:description[@xml:lang='en']" /></div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length(dct:description)!=0">
							<div class="col-md-9"><xsl:value-of select="dct:description" /></div>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="scc-error-required" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			</div>
		</section>
		
    </section>
	<section id="other-info">
		<h2>Additional information</h2>
			
			<!-- Number of registers -->
			<div class="row">
			<div class="col-md-3">Number of registers:</div>			
			<xsl:choose>							
				<xsl:when test="count(dcat:dataset) > 0">
					<div class="col-md-9">
					<xsl:value-of select="count(dcat:dataset)" />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="col-md-9">
					No registers available
					</div>
				</xsl:otherwise>
			</xsl:choose>
			</div>
			
			<!-- List of Registers -->
			<xsl:if test="count(dcat:dataset) > 0">
			<section class="cc">
				<h3>Registers list</h3>
				<p>The list of registers available from the Registry descriptor. These registers will be added to the INSPIRE register federation.</p>
				<xsl:choose>							
				<xsl:when test="count(dcat:dataset) > 0">
				<table class="list" style="width:100%">
				<thead>
				  <tr>
					<th>Register URI</th>
					<th>Distribution URL</th>
				  </tr>
				</thead>
				<tbody>
				  <xsl:for-each select="dcat:dataset">
					<xsl:variable name="uri" select="normalize-space(@rdf:resource|*/@rdf:about)"/>
					<xsl:variable name="url" select="normalize-space(/*//*[@rdf:about = $uri]/dcat:distribution[dct:format/@rdf:resource='http://publications.europa.eu/resource/authority/file-type/RDF_XML']/dcat:downloadURL/@rdf:resource)"/>
					  <tr>
						  <xsl:choose>
							  <xsl:when test="string-length($url)>0">
								  <td><a target="_blank" href="{$uri}"><xsl:value-of select="$uri"/></a></td>
								  <td><a target="_blank" href="{$url}"><xsl:value-of select="$url"/></a></td>
							  </xsl:when>
							  <xsl:otherwise>
								  <td><a target="_blank" href="{$uri}"><xsl:value-of select="$uri"/></a></td>
								  <td><abbr title="No distribution with the 'dcat:mediaType' attribute set to 'application/rdf+xml' is available. If the register descriptor is not available at the given URI through an HTTP GET request to the URI with the HTTP Accept header set to 'application/rdf+xml', a 'dcat:distribution' sub-element shall be included to specify the location of the register descriptor.">
								  <a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#reguri"><span class="glyphicon glyphicon-info-sign"></span> INFO - No distribution available *</a>
								  </abbr></td>
							  </xsl:otherwise>
						  </xsl:choose>
					  </tr>
				  </xsl:for-each>
				</tbody>
				</table>
				<br/>
				</xsl:when>
				<xsl:otherwise>
				<p>No registers available</p>
				</xsl:otherwise>
				</xsl:choose>
			</section>
			</xsl:if>
		</section>
		</div>
	</xsl:template>
	
	<!-- HTML helper template -->
	<xsl:template name="ccc-error-required">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Core-Conformance-Class" class="er"><span class="glyphicon glyphicon-exclamation-sign"></span> MISSING (required for this class)</a>
	</xsl:template>
	<xsl:template name="ahcc-error-required">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Automatic-Harvesting-Conformance-Class" class="er"><span class="glyphicon glyphicon-exclamation-sign"></span> MISSING (required for this class)</a>
	</xsl:template>
	<xsl:template name="scc-error-required">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Content-Conformance-Class" class="er"><span class="glyphicon glyphicon-exclamation-sign"></span> MISSING (required for this class)</a>
	</xsl:template>
	
	<xsl:template name="ccc-warning-recommended">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Core-Conformance-Class" class="wa"><span class="glyphicon glyphicon-info-sign"></span> MISSING (recommended for this class)</a>
	</xsl:template>
	<xsl:template name="ahcc-warning-recommended">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Automatic-Harvesting-Conformance-Class" class="wa"><span class="glyphicon glyphicon-info-sign"></span> MISSING (recommended for this class)</a>
	</xsl:template>
	<xsl:template name="scc-warning-recommended">
	<a target="_blank" href="https://ies-svn.jrc.ec.europa.eu/projects/inspire-registry/wiki/Registry_federation_requirements#Content-Conformance-Class" class="wa"><span class="glyphicon glyphicon-info-sign"></span> MISSING (recommended for this class)</a>
	</xsl:template>
	
	<xsl:template name="footer">
	<footer>
		<div class="ft1">
			<div class="container">			
				<div class="row">
					<div class="col-xs-6">INSPIRE Register Federation</div>
					<div class="col-xs-6 text-right">Registry descriptor validator v0.1</div>
				</div>
			</div>
		</div>
		<div class="ft2">
			<div class="container">
				<div class="row">
					<div class="col-sm-15 hidden-xs">
						<img class="ec-inspire-logo" src="http://inspire.ec.europa.eu/cdn/latest/img/ec.inspire.logo/logo_en.png" />
					</div>
					<div class="col-sm-15">
						<h4>INSPIRE<a data-toggle="collapse" href="#c1" aria-expanded="false" aria-controls="c1" class="navbar-toggle collapsed"><xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text></a></h4>
						<ol id="c1" class="navbar-collapse collapse">
							<li><a href="http://inspire.ec.europa.eu/">INSPIRE Web Site</a></li>
				<li><a href="http://inspire.ec.europa.eu/legislation">INSPIRE Legislation</a></li>
							<li><a href="http://inspire.ec.europa.eu/library">INSPIRE Library</a></li>
							<li><a href="http://inspire.ec.europa.eu/forum">INSPIRE Forum</a></li>
							<li><a href="http://inspire.ec.europa.eu/thematic-clusters">INSPIRE Thematic clusters</a></li>
						</ol>
					</div>
					<div class="col-sm-15">
						<h4>NEWS <xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text> EVENTS<a data-toggle="collapse" href="#c2" aria-expanded="false" aria-controls="c2" class="navbar-toggle collapsed"><xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text></a></h4>
						<ol id="c2" class="navbar-collapse collapse">
							<li><a href="http://inspire.ec.europa.eu/news">News</a></li>
				<li><a href="http://inspire.ec.europa.eu/events">Events</a></li>
							<li><a href="http://inspire.ec.europa.eu/subscribe_news">Subscribe to INSPIRE news</a></li>
							<li><a href="http://inspire.ec.europa.eu/rssnew.cfm">RSS News</a></li>
						</ol>
					</div>
					<div class="col-sm-15">
						<h4>INSPIRE Tools<a data-toggle="collapse" href="#c3" aria-expanded="false" aria-controls="c3" class="navbar-toggle collapsed"><xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text></a>
						</h4>
						<ol id="c3" class="navbar-collapse collapse">
							<li><a href="http://inspire-geoportal.ec.europa.eu/">Geoportal</a></li>
				<li><a href="http://inspire-geoportal.ec.europa.eu/validator2/">Validator</a></li>
							<li><a href="http://inspire-geoportal.ec.europa.eu/editor/">Metadata Editor</a></li>
							<li><a href="http://inspire.ec.europa.eu/registry/">Registry</a></li>
							<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/dataspecification/">Data Specification toolkit</a></li>
						</ol>
					</div>
					<div class="col-sm-15"> 
						<h4>Register Federation<a data-toggle="collapse" href="#c4" aria-expanded="false" aria-controls="c4" class="navbar-toggle collapsed"><xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text></a></h4>
						<ol id="c4" class="navbar-collapse collapse">
							<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/ror/index.jsp?type=registries">Registries</a></li>
							<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/ror/index.jsp?type=registers">Registers</a></li>
							<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/ror/index.jsp?type=relations">Relations</a></li>
							<li><a href="http://inspire-regadmin.jrc.ec.europa.eu/ror/search.jsp">Federated search</a></li>
						</ol>
					</div>
				</div>
			</div>
		</div>
		<div class="ft3">
			<div class="container">
				<div class="pull-right">
					<ol><li><a href="#en">About</a></li><li><a href="#en">Contact</a></li><li><a target="_blank" href="http://ec.europa.eu/geninfo/legal_notices_en.htm">Legal notice</a></li></ol>
					<div class="social">
						<div>
							<a href="https://twitter.com/INSPIRE_EU"><i class="fa fa-twitter-square "></i></a>
							<a href="https://www.facebook.com/groups/inspiredirective/"><i class="fa fa-facebook-square"></i></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</footer>
	</xsl:template>
	
	<!--############################################################ -->
	<!--## Template to tokenize strings ## -->
	<!--############################################################ -->
	<xsl:template name="tokenizeString">
		<!--passed template parameter -->
		<xsl:param name="list" />
		<xsl:param name="delimiter" />
		<xsl:choose>
			<xsl:when test="contains($list, $delimiter)">
				<li>
					<a class="tag" href="">
						<!-- get everything in front of the first delimiter -->
						<xsl:value-of select="substring-before($list,$delimiter)" />
					</a>
				</li>
				<xsl:call-template name="tokenizeString">
					<!-- store anything left in another variable -->
					<xsl:with-param name="list"
						select="substring-after($list,$delimiter)" />
					<xsl:with-param name="delimiter" select="$delimiter" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$list = ''">
						<xsl:text />
					</xsl:when>
					<xsl:otherwise>
						<li>
							<a class="tag" href="">
								<xsl:value-of select="$list" />
							</a>
						</li>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="last-index-of">
		<xsl:param name="txt" />
		<xsl:param name="remainder" select="$txt" />
		<xsl:param name="delimiter" select="' '" />

		<xsl:choose>
			<xsl:when test="contains($remainder, $delimiter)">
				<xsl:call-template name="last-index-of">
					<xsl:with-param name="txt" select="$txt" />
					<xsl:with-param name="remainder"
						select="substring-after($remainder, $delimiter)" />
					<xsl:with-param name="delimiter" select="$delimiter" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="lastIndex"
					select="string-length(substring($txt, 1, string-length($txt)-string-length($remainder)))+1" />
				<xsl:choose>
					<xsl:when test="string-length($remainder)=0">
						<xsl:value-of select="string-length($txt)" />
					</xsl:when>
					<xsl:when test="$lastIndex>0">
						<xsl:value-of select="($lastIndex - string-length($delimiter))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="substring-after-last">
		<xsl:param name="input" />
		<xsl:param name="substr" />

		<!-- Extract the string which comes after the first occurrence -->
		<xsl:variable name="temp" select="substring-after($input,$substr)" />

		<xsl:choose>
			<!-- If it still contains the search string the recursively process -->
			<xsl:when test="$substr and contains($temp,$substr)">
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="input" select="$temp" />
					<xsl:with-param name="substr" select="$substr" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$temp" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- String replace -->
	<xsl:template name="string-replace-all">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="by" />
		<xsl:choose>
			<xsl:when test="$text = '' or $replace = ''or not($replace)" >
				<!-- Prevent this routine from hanging -->
				<xsl:value-of select="$text" />
			</xsl:when>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$by" />
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="by" select="$by" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet> 
