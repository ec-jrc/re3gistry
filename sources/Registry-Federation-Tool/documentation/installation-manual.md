# Register-Federation-Tool

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
