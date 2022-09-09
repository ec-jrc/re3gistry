# Registry federation requirements


This page describes the requirements that a resource shall fulfill in order to be part of the Register Federation. There are 3 levels of conformance: the first one is mandatory and contains the minimum information required for the RoR to register the Registry and the Registers in the federation. The second level, contains the information to enable the automatic harvesting of the information. The third one is reccomended in order to provide complete information related to each resource.

The following paragraphs will describe the *required/recommended fields* to be provided for each of the classes and the *format* to create the exchange documents (subsequently called *"descriptors"*).

The idea of the *descriptors* is to provide metadata and data about the registries and registers to be included in the federation in a RDF/XML document (subsequently called *"Registry descriptor"* or *"Register descriptor"*) that is publicly available through an http URI.

The proposed approach is as follows:
* The [W3C Data Catalog vocabulary (DCAT)](https://www.w3.org/TR/2014/REC-vocab-dcat-20140116/) is used to model entity registry (`dcat:Catalog`).
* The [W3C Simple Knowledge Organization System (SKOS)](https://www.w3.org/TR/2009/REC-skos-reference-20090818/)  is used to model entity register (`skos:ConceptScheme`) and item (`skos:Concept`).

At the end of each conformance class there are one example file for the *Registry descriptor* and one for the *Register descriptor* (the examples contain also code comments to better understands each of the fields).

A useful resource to validate the conformance of the Registry and Register descriptors to the following classes are the XSL validators.

Note: the RoR needs a direct link to the Descriptors. Cloud platforms like Dropbox, OneDrive or Google Drive are not supported.


## Conformance classes

- [Core](#Core Conformance Class)
- [Automatic harvesting](#Automatic Harvesting Conformance Class)
- [Content](#Content Conformance Class)


### Core Conformance Class
This conformance class is good for sharing registry and register metadata in the federation.

#### Registry descriptor
**Requirement:** metadata about the registry to be included in the federation shall be specified in a `RDF/XML` document (*Registry descriptor*) that is publicly available through an http URI.

**Requirement:** The URIs/URLs in the *Registry descriptor* shall be provided using the absolute format, without abbreviation.

**Note:** even if the RDF Specification allows the use of the `xml:base` property to allow URI/URL abbreviation, there are cases in which some parsers/software are not able to handle it. 

**Requirement:** the registry shall be described as an instance of `dcat:Catalog` class with the following mandatory/optional properties.

| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| dct:publisher | 1..1 | foaf:Agent |
| dct:title | 1..1 | rdfs:Literal |

**Requirement:** the publisher shall be described as instances of `foaf:Agent` class with the following mandatory/optional properties.

**Note:** the URI of the `foaf:Agent` class should be provided using a well defined URI-pattern. Some example are provided below:
* the "Corporate body register" from the EU Pubblication office: http://publications.europa.eu/mdr/resource/authority/corporate-body/html/corporatebodies-eng.html (example URI: http://publications.europa.eu/resource/authority/corporate-body/JRC).
* a DBpedia URI (example URI: http://dbpedia.org/resource/National_Geographic_Institute_(Belgium))

| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| foaf:name | 1..1 | 	rdfs:Literal |
| foaf:mbox | 1..1 | URI |
| foaf:homepage | 0..1 | URL |

**Requirement:** for each register to be federated, the *Registry descriptor* shall include a reference to the document providing the register metadata (the *Register descriptor*).

**Requirement:** the reference to the register shall be described with the `dcat:dataset` property. The `dcat:distribution` property shall be included with the following properties.

**Note:** If the system will check if the *Register descriptor* is available at the given URI through an HTTP GET request to the URI with the HTTP Accept header set to `"application/x-ror-rdf+xml"`. Otherwise it will ask for the resource using the standard HTTP GET request.

| Property | Cardinality | Range | Notes |
| --- | ----------- | ----------- | ----------- | 
| dct:format | 1..1 | 	URI | The format shall be specified using the following codelist provided by the EU Publication office: http://publications.europa.eu/resource/authority/file-type/RDF_XML |
| dcat:downloadUrl | 1..1 | URL | |

#### Register descriptor

**Requirement:** metadata about the register to be included in the federation shall be specified in a `RDF/XML` document (*Register descriptor*) that is publicly available through an http URI.

**Requirement:** The URIs/URLs in the *Register descriptor* shall be provided using the absolute format, without abbreviation.

**Note:** even if the RDF Specification allows the use of the `xml:base` property to allow URI/URL abbreviation, there are cases in which some parsers/software are not able to handle it.

**Requirement:** the register shall be described as an instance of `skos:ConceptScheme` class with the following mandatory/optional properties.

| Property | Cardinality | Range | Notes |
| --- | ----------- | ----------- | ----------- | 
| skos:prefLabel | 1..1 |	rdfs:Literal | |
| voaf:reliesOn | 0..1 | 	URI | The URI of the *register* on which this *register* relies on |
| dct:publisher | 1..1 | foaf:Agent | |
| dct:isPartOf | 1..1 | 	dcat:Catalog | The reference to the *registry* containing this *register*. |

**Requirement:** the *registry* containing this *register* shall be described as an instance of `dcat:Catalog`.

**Requirement:** the publisher shall be described as instances of `foaf:Agent` class with the following mandatory/optional properties.

**Note**: the URI of the `foaf:Agent` class should be provided using a well defined URI-pattern. Some example are provided below:

* the "Corporate body register" from the EU Pubblication office: http://publications.europa.eu/mdr/resource/authority/corporate-body/html/corporatebodies-eng.html (example URI: http://publications.europa.eu/resource/authority/corporate-body/JRC).
* a DBpedia URI (example URI: http://dbpedia.org/resource/National_Geographic_Institute_(Belgium))

| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| foaf:name | 1..1 | 	rdfs:Literal |
| foaf:mbox | 1..1 | URI |
| foaf:homepage | 0..1 | URL |

#### Example files related to this conformance class:
* [Example Registry file for this class](related_docs/Registry_mandatory_conformance_class_example.pdf)
* [Example Register file for this class](related_docs/Register_mandatory_conformance_class_example.pdf)

### Automatic Harvesting Conformance Class
This conformance class is good for allowing a register to be automatically harvested from the Register of Registers.

#### Registry descriptor
**Requirement:** in addition to the properties specified in the Core conformance class, the Registry descriptor shall provide the following property.
| Property | Cardinality | Range | Notes |
| --- | ----------- | ----------- | ----------- | 
| dct:accrualPeriodicity | 1..1 |	URI | Update frequency. For conformance with DCAT-AP, this needs to be specified by using the MDR Frequency register maintained by the EU Publications Office: http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html - Example frequency: http://publications.europa.eu/resource/authority/frequency/DAILY - NOTE: the minimum frequency is "daily" |

#### Register descriptor
**Requirement:** in addition to the properties specified in the Core conformance class, the Register descriptor shall provide the following property.
| Property | Cardinality | Range | Notes |
| --- | ----------- | ----------- | ----------- | 
| dct:accrualPeriodicity | 1..1 |	URI | Update frequency. For conformance with DCAT-AP, this needs to be specified by using the MDR Frequency register maintained by the EU Publications Office: http://publications.europa.eu/mdr/resource/authority/frequency/html/frequencies-eng.html - Example frequency: http://publications.europa.eu/resource/authority/frequency/DAILY - NOTE: the minimum frequency is "daily" |

#### Example files related to this conformance class:
* [Example Registry file for this class](related_docs/Registry_automatic-harvesting_conformance_class_example.pdf)
* [Example Register file for this class](related_docs/Register_automatic-harvesting_conformance_class_example.pdf)

### Content Conformance Class
This conformance class is good for sharing the register content in the federation, e.g. to make it searchable in the Register of Registers and accessible from register clients (incl. other registers).
#### Registry descriptor
**Requirement:** in addition to the properties specified in the Core conformance class, the *Registry descriptor* shall provide the following property.
| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| dct:description | 1..1 | 	rdfs:Literal |

#### Register descriptor
**Requirement:** in addition to the properties specified in the Core conformance class, the *Register descriptor* shall provide the following property.
| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| skos:definition | 1..1 | 	rdfs:Literal |

**Requirement:** the *Register descriptor* shall describe each of the item internally defined as a `skos:Concept` with the following properties.
| Property | Cardinality | Range | Notes |
| --- | ----------- | ----------- | ----------- | 
| skos:inScheme | 1..1 |	URI |  |
| skos:prefLabel | 1..1 |	rdfs:Literal |  |
| skos:definition | 0..1 |	rdfs:Literal |  |
| adms:status | 0..1 |	URI | The codelist to be used for the status is the one provided by the INSPIRE registry for example: http://inspire.ec.europa.eu/registry/status |

**Requirement:** the *Register descriptor*  should include each of the item externally defined with the following property.

**Note:** the externally defined item may be provided to specify that this register is using a subset of the elements from the external register.
| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| skos:inScheme | 1..1 | 	URI |

#### Example files related to this conformance class:
* [Example Registry file for this class](related_docs/Registry_content_conformance_class_example.pdf)
* [Example Register file for this class](related_docs/Register_content_conformance_class_example.pdf)
