# Register-Federation-Tool

## Overview
The Rgister-Federation-Tool is a reusable open-source solution.
This manual will cover the installation and configuration of the software

The requirements to run the software are:

* Machine with 4GB of RAM
* [Apache Httpd server](https://httpd.apache.org/) (or another Http server)
* Java 1.8 – versions higher than 1.8 should not be used, and the code should be compiled and run using Java 1.8[^note_java_8]
* [Apache Tomcat](https://tomcat.apache.org) 8.5 or 9.0[^note_apache_tomcat]
* [Apache Solr](https://solr.apache.org/) 8.x[^note_apache_solr]
* PostgreSQL 9.6 (the system supports other relational databases, but it has been tested only on the specified one - for the list of supported databases, please check [this link](https://wiki.eclipse.org/EclipseLink/FAQ/JPA).)

[^note_java_8]: This is because the software is dependent on the [https://en.wikipedia.org/wiki/Jakarta_XML_Binding](JAXB framework) (package `javax.xml.bind`, see also [JAXB on Java 9, 10, 11 and beyond](https://www.jesperdj.com/2018/09/30/jaxb-on-java-9-10-11-and-beyond/).
[^note_apache_solr]: Check the version of the jar file `solr-solrj-8.x.y.jar` of the Solrj Client API in the distribution (this version can be set when building the software with Maven, using property `${application.solr.version}`, and install the matching server.
[^note_apache_tomcat]: The software is dependent on classes from the `javax.servlet` package, and therefore Tomcat 10 cannot be used; see also [Migration Guide - Tomcat 10.0.x](https://tomcat.apache.org/migration-10.html). See [Apache Tomcat Versions](https://tomcat.apache.org/whichversion.html) for more information about the different Apache Tomcat Versions.

The prerequisites to follow this manual are:
* to have an instance of Apache Tomcat installed and configured to run with Java 1.8
* to have an instance of Apache Solr installed and configured
* to have a PostgreSQL database server installed and configured (or an alternative relational database server)

## Installing the Federation Tool software 

The following steps need to be followed to install the system properly. 

Distribution package 

The distribution folder contains all required files for the Federation Tool system installation. Download the distribution package and check the folder content. 

The "sources" folder contains the Federation Tool main application. 

The "db-script" folder contains the database initialization script. 

All the folders mentioned above contain files that must be customised for your environment. Therefore, the distribution package includes an automatic initialisation script that will prepare the files for the installation. 

In particular, the pom.xml file contains the properties to be customised. 

* applicationrooturl: the root URL of the application (e.g. http://www.test-url.eu/ror) 
* dbhost: the address of the database to be used by the Federation Tool (e.g. 192.168.0.1) 
* dbport: the port of the database (e.g. 5432) 
* dbname: the name of the database (e.g. ror) 
* dbuser: the user to access the database 
* dbpassword: the password to access the database 
* solr_url: the URL of the Apache Solr instance to be used (e.g. http://localhost:8983/solr/). You must create a new dedicated Solr core named "RoR". If you need to change the name, you can edit it in the configuration.properties (application.solr.core property). 
* registry_validator_path: the path to the registry validator (usually under sources\descriptors\validators) 
* register_validator_path: the path to the register validator (usually under sources\descriptors\validators) 
* Base.ror.path 
* default.log.file.dir 

Once the above file has been customised, the init script must be launched. Depending on your system, you must launch init-config.bat (Windows systems) or init-config.sh (Linux systems). 

Note: the script is just customising the properties required for the system to run. The application's configuration files contain more customisation and configuration options. You can change those options by checking the following files. All the properties are well documented in the configuration file itself. 

* app/re3gistry2/WEB-INF/classes/configurations_files/configuration.properties 
* app/re3gistry2restapi/WEB-INF/classes/configurations_files/configuration.properties 

## Database setup 

The first step is to run the database initialization script on the database created for the Federation Tool (if you do not have a database, create a new one). 

Run the SQL script available in dist/db-scripts/ror_drop-and-create-and-init.sql (customized following the steps presented in the previous section). 

Note: there are some scripts to help you set up your database correctly. Place these scripts under your PostgreSQL root folder and run them: 
* Init_postgres.bat 
* RoR_restore_db.bat 
* Start_postgres.bat 

## Installation of the Federation Tool app on Tomcat 

Make sure the Tomcat instance is not running (stop it). 

To install the application, copy the content of the dist/app folder (customized following the steps presented in the previous section) in the <tomcat-home>/webapp folder. The Tomcat webapp folder should have the following two folders: 

* <tomcat-home>/webapp/ror.war 

The last step is to start Tomcat. 
