# Re3gistry

The Re3gistry is a reusable open source solution for managing and sharing ‘reference codes’, ensuring concepts are correctly referenced in INSPIRE, or for content in any other sector.

## Re3gistry and INSPIRE registry test-bed

The sandbox instance to test the Re3gistry 2 software and the new INSPIRE registry service is available at the following addresses:

- management interface: https://inspire-sandbox.jrc.ec.europa.eu/re3gistry2
- publication interface (with example content from INSPIRE): https://inspire-sandbox.jrc.ec.europa.eu/registry

The aim of the test-bed is to let the users try the new version of the Re3gistry software using the management interface and workflow of the system to complete some common tasks like the examples listed below.
- Adding a new reference codes
- Editing an existing reference code
- Adding a relation between two reference codes
- Superseding, Invalidating, Retiring a reference code
- Adding a new field

To understand the main concepts and functionalities of the  Re3gistry 2 management interface, check the **[quick start guide](Quick-start-guide.md)**.

Once these tasks are completed and published in the management interface, the publication interface can be used to check the results.

If you want to participate to the test-bed, send an email to jrc-inspire-support@ec.europa.eu including your **EU Login** email .
During the test period, users can report issues or provide feedback using the [issue tracker](https://github.com/ec-jrc/re3gistry/issues).
The test period will run until 6 March 2020.

### Major changes

The list below shows the major changes that the Re3gistry 2 software will introduce compared to the current release of the software (1.3.1).
- Backward compatibility with Re3gistry >= v1 (data migration tool included)
- User friendly editing interface (Create, edit, update registers, items, fields)
- Handle localization of each custom field
- Manage the ISO 19135 workflow in a visual way
- Manage users, roles and groups
- Better handling of the service (new caching system for the data access, no more static files)
- APIs
- Simple software installation


### Release Plan

The **Re3gistry software v2** is currently being tested, with a target release date as open source project in June 2020.

A development collaboration with **National Land Survey of Finland** has been set up to work on the implementation of the **data cache** and **data API** module. Below there is an approximate release plan for the Re3gistry 2.

**January**

- The JRC will launch a test-bed on the system. If you want to participate in the test-bed get in contact with us at jrc-inspire-support@ec.europa.eu

**June**

- Planned release of v2 as open source project
