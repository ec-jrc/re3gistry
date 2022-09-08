# Re3gistry software

[![European Union Public Licence 1.2](https://img.shields.io/badge/license-EUPL%201.2-blue.svg)](https://joinup.ec.europa.eu/software/page/eupl)

&copy; 2020 European Union, National Land Survey of Finland. Licensed under the EUPL.

## About the Re3gistry

The Re3gistry software is a reusable open-source solution for **managing and sharing ‘reference codes’** through persistent URIs, ensuring that concepts are unambiguously referenced in any domain.

Reference codes can be sets of permissible values for a data field or to provide a reference or context for the data being exchanged between applications. Examples of reference codes are enumerations, controlled vocabularies, taxonomies, thesauri or, simply, ‘lists of things’.

The Re3gistry provides a central access point where labels and descriptions for reference codes are easily accessible by both humans and machines while fostering **semantic interoperability across organisations**.

Licensed under the **[EUPL](http://ec.europa.eu/idabc/eupl.html)**, Re3gistry is an open-source project to be deployed and further developed by contributors from any sector.

### Acknowledgements

The development of the Re3gistry began in 2013 under the [Are3na action](https://joinup.ec.europa.eu/collection/are3na/about) (ISA Programme), continuing since 2016 under the [ELISE action](https://joinup.ec.europa.eu/collection/elise-european-location-interoperability-solutions-e-government/about) of the [ISA2 Programme](https://ec.europa.eu/isa2/isa2_en/). Starting 2022, the **[Digital Europe Programme](https://digital-strategy.ec.europa.eu/en/activities/digital-programme)** is funding its development.

Version 2 of the software has been developed in cooperation with the [National Land Survey of Finland](https://www.maanmittauslaitos.fi/en).

## Re3gistry features

Check out the [latest release](https://github.com/ec-jrc/re3gistry/releases).

* User friendly editing interface to add, edit and manage easily the registers and reference codes
* Management of the full lifecycle of the reference codes (based on the ISO 19135 Standard)
* Highly flexible and customisable data models
* Multi-lingual content support
* Support for versioning
* RESTful API with content negotiation (including OpenAPI 3 descriptor)
* Free-text search
* Supported formats: HTML, ISO 19135 XML, JSON, RDF/XML, Re3gistry XML, CSV
* Service formats can be easily added or customised
* Multiple authentication options
* Externally governed items referenced through URI 
* INSPIRE register federation format support (option to automatically create the RoR format)
* Web-app to access the reference codes in a human readable way.

## Documentation

* [User manual](documentation/user-manual.md)
* [Administrator manual](documentation/administrator-manual.md)
* [Developer manual](documentation/developer-manual.md)

# Re3gistry governance

The governance structure and processes of the Re3gistry aim to ensure the use and sustainability of the re3gistry by the Community, and to keep it aligned to the Community's needs.

The governance is ensured through the managers of the system.

## Project boards

* Improvement proposal (IP)
    * If you would like to discuss an idea before documenting a full IP, simply create a new issue using the [IP template](https://github.com/ec-jrc/re3gistry/issues/new?assignees=&labels=&template=re3gistry-improvement-proposal.md). Complete the template as far as possible and mention that this is not a complete proposal yet, but that you are looking for feedback. If the idea is supported, you will be asked you to complete the proposal.
   
* Bug fixing
    * If you would like to submit a bug report, please create a new issue in the Re3gistry repository using the [Bug report template](https://github.com/ec-jrc/re3gistry/issues/new?assignees=&labels=&template=re3gistry-problem.md). The issue will be monitored and will be added to the bugfixing project board. If additional information is required, you will be contacted.
    * The project board will be updated whenever the status of an issue changes.
    * Pull requests for bugfixes are very welcome (see "Contributing" below)!

# Contribution

If you are interested in contributing to the Re3gistry project, please read carefully the [contribution guidelines](contribution.md).
