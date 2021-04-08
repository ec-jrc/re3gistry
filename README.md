# Re3gistry 2 software

[![European Union Public Licence 1.2](https://img.shields.io/badge/license-EUPL%201.2-blue.svg)](https://joinup.ec.europa.eu/software/page/eupl)

&copy; 2020 European Union, National Land Survey of Finland. Licensed under the EUPL.

## About the Re3gistry 2

The Re3gistry 2 is a reusable open source solution for managing and sharing "reference codes".

It provides a consistent central access point where labels and descriptions for reference codes can be easily browsed by humans and retrieved by machines.

Reference codes are exchanged between applications to uniquely reference some ‘thing’. They can be used to define sets of permissible values for a data field or to provide a reference or context for the data being exchanged. Examples are enumerations, controlled vocabularies, taxonomies, thesauri or, simply, ‘lists of things’.

The Re3gistry 2 supports organisations in managing and updating reference codes in a consistent way.

The Re3gistry software version numbers comply with the [Semantic Versioning Specification 2.0.0](http://semver.org/spec/v2.0.0.html).

### Acknowledgments

The development of this tool has been funded by the European Iinteroperability Programme ISA2 though the [ELISE action](https://joinup.ec.europa.eu/collection/elise-european-location-interoperability-solutions-e-government/elise-re3gistry-software). The European Location Interoperability Solutions for e-Government (ELISE) Action aims at Enabling Digital Government through Geospatial Data and Location Intelligence.

This new version of the software has been developed in cooperation with the [National Land Survey of Finland](https://www.maanmittauslaitos.fi/en).

## Re3gistry 2 features

Check out the [latest release](https://github.com/ec-jrc/re3gistry/releases).

* User friendly editing interface to add, edit and manage easily the registers and reference codes
* Management of the full lifecycle of the reference codes (based on the ISO 19135 Standard)
* Highly flexible and customisable data models
* Multi-lingual content support
* Support for versioning
* RESTful API with content negotiation (including OpenAPI 3 descriptor)
* Free-text search
* Supported formats: HTML, ISO 19135 XML, JSON, RDF/XML, ATOM, Re3gistry XML, CSV
* Service formats can be easily added or customised
* Multiple authentication options
* Externally governed items referenced through URI 
* INSPIRE register federation format support (option to automatically create the RoR format)
* Web-app to access the reference codes in a human readable way.

## Documentation

* [User manual](documentation/user-manual.md)
* [Administrator manual](documentation/administrator-manual.md)
* [Developer manual](documentation/developer-manual.md)

## Community

Use the [the issue tracker](https://github.com/ec-jrc/re3gistry/issues) to:
* report a problem,
* propose a new feature or an improvement to the existing functionality,
* start a discussion or raise a question

## Missing features

* Cache of the elements in the dropdowns of the application.
* Improvements on the cache of the data API.
