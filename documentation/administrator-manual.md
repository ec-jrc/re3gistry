# Administrator manual

## Scope

This manual is intended for users that are going to install and set-up the Re3gistry 2 software.

The following documentation provides details related to the installation and configuration of the Re3gistry 2 software. If you want to have more information on how to use the software, refer to the [User manual](user-manual.md).

This is a live document; it is being improved continuously. To have the last version you can refer to https://github.com/ec-jrc/re3gistry.

Please report any feedback on the documentation [in the GitHub issue tracker](https://github.com/ec-jrc/re3gistry/issues).

## Overview

The Re3gistry 2 is a reusable open source solution for managing and sharing "reference codes".

This manual will cover the following topics:

* Installing and configuring the software
* Installing and configuring "registry service front-end web application

The minimum requirements to run the software are listed below:

* Machine with 4GB of RAM
* Apache Httpd server (or another Http server)
* Java 1.8
* Apache Tomcat 7
* Apache Solr 8
* PostgreSQL 9.6 (the system supports other relational databases but it has been tested only on the specified one - for the list of supported database, please check [this link](https://wiki.eclipse.org/EclipseLink/FAQ/JPA).)

The prerequisites to follow this manual are:
* to have an instance of Apache Tomcat installed and configured to run with Java 1.8
* to have an instance of Apache Solr installed and configured
* to have PostgreSQL database server installed and configured (or an alternative relational database server)

## Installing the Re3gistry 2 software

The following steps needs to be followed in order to properly install the system.

### Distribution package

The [distribution folder](../dist/) contains all the file required for the Re3gistry 2 system installation. Download the distribution package and check the folder content. 

The "app" folder contains the Registry 2 main application and the related Re3gistry 2 rest API application.

The "webapp" folder contains an example web app that can be used to create the public service front-end. 

The "db-script" folder contains the database initialization script.

All the above mentioned folders contains files that needs to be customised for your environment. For that reason, the distribution package, includes an automatic initialisation script that will prepare the files for the installation.

In particular, the `init.properties` contains the properties to be customised (detailed below).

* dbhost: the address of the database to be used by the Re3gistry 2 (e.g. 192.168.0.1)
* dbport: the port of the database (e.g. 5432)
* dbname: the name of the database (e.g. re3gistry2_db)
* dbuser: the user to access the database
* dbpassword: the password to access the database
* statusbaseuri: the status base URI is the first part of the URL of the status contained in the system. Usually this URI is the same of the main URL of the service but since in some cases it may be different, the Re3gistry system is providing this option (e.g. https://test-uri.eu).
* solrurl: the URL of the Apache Solr instance to be used (e.g. http://localhost:8983/solr/)
* smtphost: the address of the SMTP server (e.g. yoursmtp.test-url.eu)
* applicationrooturl: the root URL of the application (e.g. http://www.test-url.eu/r3egistry2

Once the above file has been customised, the init script needs to be launched. Depending on your system you need to launch init-config.bat (Windows systems) or init-config.sh (Linux systems).

Note: the script is just customising the properties required for the system to run. The application's configuration files contains more customisation and configuration options. You can change those options, checking the following files. All the properties are well documented in the configuration file itself.

* app/re3gistry2/WEB-INF/classes/configurations_files/configuration.properties
* app/re3gistry2restapi/WEB-INF/classes/configurations_files/configuration.properties

### Database setup

The first step is to run the database initialization script on the database created for the Re3gistry 2 (if you do not have a database, create a new one).

Run the SQL script available in `dist/db-scripts/registry2_drop-and-create-and-init.sql`
(customized following the steps presented in the previous section).

### Installation of the Re3gistry 2 core app on tomcat

Make sure the tomcat instance is not running (stop it).

To install the application, copy the content of the `dist/app` folder (customized following the steps presented in the previous section) in the `<tomcat-home>/webapp` folder.
The tomcat webapp folder should have the following 2 folders:

* `<tomcat-home>/webapp/re3gistry2`
* `<tomcat-home>/webapp/re3gistry2restapi`

The last step is to start tomcat.

### Installation wizard

To start the installation wizard, visit the following address: `http://<tomcat-url>:<tomcat-port>/re3gistry2/install` (e.g. `http://localhost:8080/re3gistry2/install`).

The first page is just an introductory description. Click on "continue".

The "Registry manager" page lets you define the account for the system administration (Registry manager). Insert a valid e-mail and a password. Then click on "Save and continue".

![Installation wizard - Registry manager creation](images/init-registrymanager-def.png)

The next step allows you to choose between 2 options:

*  Installation: choose this option if you want to install the Re3gistry software for the first time and to create a registry from scratch
* Migration: choose this option if you want to upgrade a preexisting Re3gistry instance. This option will keep your settings and content. Note that this option **will work only on an existing instance running on Re3gistry version 1.3.X**.

#### Installaion option

In case the "Installation" option is chosen, the basic configuration for the new registry will be requested.

* System languages: the system languages are the languages supported and handled by the Re3gistry 2. Note that you can select one or more system languages.
* Master language: the master language is the one used by the Re3gistry 2 to handle multilingualism. Note that this will require all content to be available in the chosen master language.
* Base URI: the base URI will be part of the public URL, generally this should match with your own domain name: **http://example.com**/registry
* Local ID: the local ID will be part of the public URL, generally the path following the domain name: http://example.com/**registry**
* Label: the label of the registry will be the ‘title’ of the Re3gistry 2 instance. It will be shown in the registry’s landing page.
* Content summary: this text should describe shortly what would be available under your Re3gistry 2 instance. It will be shown in the registry’s landing page.

After filling all the information, click on "Save and continue".

![Installation wizard - Clean installation](images/init-cleaninstall.png)

The next step will present a summary of the information inserted. If all the fields are correct, click on "Install" to begin the installation.

Once the installation is finished, the user is redirected to the login page.

#### Migration option

In case the "Migration" option is chosen, the details of the database from the existing Re3gistry installation are requested.

Fill the fileds requested and click to the "Save and continue" button. You will then get a page containing a summary of the data retrieved in the existing instance of the Re3gistry.

![Installation wizard - Migration summary](images/init-migration.png)

Click on "Install" to begin the migration. An information page will be shown. Now you can close the browser and the system will notify you via e-mail using the address specified in the "Registry manager" definition step.

## Installing the example service frontend

The Re3gistry webapp consumes the Re3gistry 2 Rest API to create a HTML version of the service. This webapp can be used to publish the services using a web interface. 
The guide below explain how to install the webapp.

### Important information

The example webapp is provided without any cookie notice script. It has to be implemented by the final user.

### Installation steps

#### Copy required files
The first step to install the webapp is to copy the files contained in the folder [public_html](dist/webapp/) in the document root designated to host this webapp (e.g. in  /var/www/registry-example.eu/public_html). 

#### Configuring the webapp

Once the files have been copied, the configuration files needs to be customized. The configuration files can be found at [webapp-root]/conf/conf.js.

Every configuration entry is well documented with inline comment. An important part to be customized is the one where all the required URL are specified ("The app's base URLs").

#### Configuring the HTTP server

The webapp contains also an example configuration files fot the Apache HTTP server. 
This file may needs some customization based on your OS and Apache version.

The Apache configuration file (apache-example-configuration.conf) can be found in the [webapp](../dist/webapp/) folder.

Remember to restart the Apache HTTP server after including the configuration.

## Additional notes

### Open API descriptor

The OpenAPI 3 descriptor is available in the [source](../sources/Re3gistry2RestAPI) folder: "openapi.yaml" .
This file can be exposed through a HTTP server in order to describe the Re3gistry 2 API in a machine readable way. 

