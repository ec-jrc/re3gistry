# Registry federation requirementst


This page describes the requirements that a resource shall fulfill in order to be part of the INSPIRE Register Federation. There are 3 levels of conformance: the first one is mandatory and contains the minimum information required for the RoR to register the Registry and the Registers in the federation. The second level, contains the information to enable the automatic harvesting of the information. The third one is reccomended in order to provide complete information related to each resource.

The following paragraphs will describe the required/recommended fields to be provided for each of the classes and the format to create the exchange documents (subsequently called "descriptors").

The idea of the descriptors is to provide metadata and data about the registries and registers to be included in the federation in a RDF/XML document (subsequently called "Registry descriptor" or "Register descriptor") that is publicly available through an http URI.

The proposed approach is as follows:
* The [W3C Data Catalog vocabulary (DCAT)](https://www.w3.org/TR/2014/REC-vocab-dcat-20140116/) is used to model entity registry (dcat:Catalog).
* The [W3C Simple Knowledge Organization System (SKOS)](https://www.w3.org/TR/2009/REC-skos-reference-20090818/)  is used to model entity register (skos:ConceptScheme) and item (skos:Concept).

At the end of each conformance class there are one example file for the *Registry descriptor* and one for the *Register descriptor* (the examples contain also code comments to better understands each of the fields).

A useful resource to validate the conformance of the Registry and Register descriptors to the following classes are the XSL validators.

Note: the RoR needs a direct link to the Descriptors. Cloud platforms like Dropbox, OneDrive or Google Drive are not supported.


## Conformance classes
### Core
### Automatic harvesting
### Content

### Core Conformance Class
This conformance class is good for sharing registry and register metadata in the federation.

#### Registry descriptor
**Requirement:** metadata about the registry to be included in the federation shall be specified in a RDF/XML document (Registry descriptor) that is publicly available through an http URI.

**Requirement:** The URIs/URLs in the Registry descriptor shall be provided using the absolute format, without abbreviation.

**Note:** even if the RDF Specification allows the use of the xml:base property to allow URI/URL abbreviation, there are cases in which some parsers/software are not able to handle it. 

**Requirement:** the registry shall be described as an instance of dcat:Catalog class with the following mandatory/optional properties.

| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| dct:publisher | 1..1 | foaf:Agent |
| dct:title | 1..1 | rdfs:Literal |

**Requirement:** the publisher shall be described as instances of foaf:Agent class with the following mandatory/optional properties.

**Note:** the URI of the foaf:Agent class should be provided using a well defined URI-pattern. Some example are provided below:
* the "Corporate body register" from the EU Pubblication office: http://publications.europa.eu/mdr/resource/authority/corporate-body/html/corporatebodies-eng.html (example URI: http://publications.europa.eu/resource/authority/corporate-body/JRC).
* a DBpedia URI (example URI: http://dbpedia.org/resource/National_Geographic_Institute_(Belgium))

| Property | Cardinality | Range |
| --- | ----------- | ----------- |
| foaf:name | 1..1 | 	rdfs:Literal |
| foaf:mbox | 1..1 | URI |
| foaf:homepage | 0..1 | URL |

**Requirement:** for each register to be federated, the Registry descriptor shall include a reference to the document providing the register metadata (the Register descriptor).

**Requirement:** the reference to the register shall be described with the dcat:dataset property. The dcat:distribution property shall be included with the following properties.

**Note:** If the system will check if the Register descriptor is available at the given URI through an HTTP GET request to the URI with the HTTP Accept header set to "application/x-ror-rdf+xml". Otherwise it will ask for the resource using the standard HTTP GET request.
