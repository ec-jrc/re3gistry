@echo off 

REM Creating the config files from the original ones
echo Y | copy app\re3gistry2\WEB-INF\classes\META-INF\persistence.xml.orig app\re3gistry2\WEB-INF\classes\META-INF\persistence.xml
echo Y | copy app\re3gistry2\WEB-INF\shiro.ini.orig app\re3gistry2\WEB-INF\shiro.ini
echo Y | copy app\re3gistry2\WEB-INF\classes\configurations_files\configuration.properties.orig app\re3gistry2\WEB-INF\classes\configurations_files\configuration.properties
echo Y | copy db-scripts\registry2_drop-and-create-and-init.sql.orig db-scripts\registry2_drop-and-create-and-init.sql
echo Y | copy app\re3gistry2restapi\WEB-INF\classes\META-INF\persistence.xml.orig app\re3gistry2restapi\WEB-INF\classes\META-INF\persistence.xml

REM Replacing values from the init.properties file
For /F "tokens=1* delims==" %%A IN (init.properties) DO (
	
	echo 'Replacing %%A to %%B'
	
	powershell -Command "(gc app\re3gistry2\WEB-INF\classes\META-INF\persistence.xml) -replace '%%A', '%%B' | sc app\re3gistry2\WEB-INF\classes\META-INF\persistence.xml"
	powershell -Command "(gc db-scripts\registry2_drop-and-create-and-init.sql) -replace '%%A', '%%B' | sc db-scripts\registry2_drop-and-create-and-init.sql"
	powershell -Command "(gc app\re3gistry2\WEB-INF\shiro.ini) -replace '%%A', '%%B' | sc app\re3gistry2\WEB-INF\shiro.ini"
	powershell -Command "(gc app\re3gistry2\WEB-INF\classes\configurations_files\configuration.properties) -replace '%%A', '%%B' | sc app\re3gistry2\WEB-INF\classes\configurations_files\configuration.properties"
	powershell -Command "(gc app\re3gistry2restapi\WEB-INF\classes\META-INF\persistence.xml) -replace '%%A', '%%B' | sc app\re3gistry2restapi\WEB-INF\classes\META-INF\persistence.xml"

)