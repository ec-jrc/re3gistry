#!/bin/bash

# Creating the config files from the original ones
yes | cp "app/re3gistry2/WEB-INF/classes/META-INF/persistence.xml.orig" "app/re3gistry2/WEB-INF/classes/META-INF/persistence.xml"
yes | cp "app/re3gistry2/WEB-INF/shiro.ini.orig" "app/re3gistry2/WEB-INF/shiro.ini"
yes | cp "app/re3gistry2/WEB-INF/classes/configurations_files/configuration.properties.orig" "app/re3gistry2/WEB-INF/classes/configurations_files/configuration.properties"
yes | cp "db-scripts/registry2_drop-and-create-and-init.sql.orig" "db-scripts/registry2_drop-and-create-and-init.sql"
yes | cp "app/re3gistry2restapi/WEB-INF/classes/META-INF/persistence.xml.orig" "app/re3gistry2restapi/WEB-INF/classes/META-INF/persistence.xml"

# Replacing values from the init.properties file
while IFS="=" read -r key value; do 
	
	export tmp="${value//\//\\/}"
	tmp=${tmp//$'\n'/}
	tmp=${tmp//$'\r'/}
	
	echo "Replacing $key to $tmp"; 
		
	sed -i "s/$key/$tmp/g" "app/re3gistry2/WEB-INF/classes/META-INF/persistence.xml"
	sed -i "s/$key/$tmp/g" "db-scripts/registry2_drop-and-create-and-init.sql"
	sed -i "s/$key/$tmp/g" "app/re3gistry2/WEB-INF/shiro.ini"
	sed -i "s/$key/$tmp/g" "app/re3gistry2/WEB-INF/classes/configurations_files/configuration.properties"
	sed -i "s/$key/$tmp/g" "app/re3gistry2restapi/WEB-INF/classes/META-INF/persistence.xml"

done < init.properties