# Re3gistry 2 public front-end web-app

## Description

The Re3gistry web-app consumes the Re3gistry 2 Rest API to create a HTML version of the service.
The web-app contains test files to get the service running and test the correct installation and functionalities.
The test files are stored in `<webapp_root>/service-test`. The files are described below.
1. `test.json`: it is an example of the expected data service output from the Re3gistry 2 Rest API
2. `test.php`: it is a testing file that can help in the development and the installation of the system.
3. `test_commented.json`: it is the same file at point 1 but with some line commented to understand the meaning of each field (and in some cases how to retrieve it)
4. `test_error_commented.json`: it is an example of the json format returned in case of errors.


## Installing the Re3gistry web-app

To install the web-app you should have a xampp environment (or equivalent) installed on your system.

In the development environment the web app base URL is set as `inspire.ec.europa.eu` to match the URI of the element in the system.
In order for the system to work the hosts file in your system needs to be updated with the following entry.

    127.0.0.1 inspire.ec.europa.eu

The following steps let you install the Apache httpd configuration file for the web-app. 
This file contains the virtual host, the basic configuration for the web-app, the proxy pass for the Re3gistry 2 Rest API.

### Install the web-app configuration file in xampp
1. Paste the example configuration file `re3gistry2-webapp.conf` in `<xamp_root>\apache\conf\extra`
2. Open the configuration file with a text editor and make sure the paths are reflecting your environment/installation
3. Open the file `<xamp_root>\apache\conf\httpd.conf` with a text editor
4. Add the following line at the very bottom of the file: `Include "conf/extra/re3gistry2-webapp.conf"`
5. Restart the Apache HTTPD from the xampp control panel.

### Setup the web-app
1. Open the configuration file `<webapp_root>/conf/conf.js`
2. Have a look at the configuration. In particular, the `registryApp.dataServiceURL` property is set to the URL of the Re3gistry 2 Rest API configured in the Apache HTTPD file.
3. If the Re3gistry 2 Rest API is not available, you can test the system by uncommenting the reference to the testing file (`registryApp.dataServiceURL = '//inspire.ec.europa.eu/registry/service-test/test.php';`).
4. Once done, calling the HTML web-app at the URL http://inspire.ec.europa.eu/registry should return the HTML page of the registry.

## Notes
Depending on your version of xampp the folders may be different then the ones specified in this installation manual.