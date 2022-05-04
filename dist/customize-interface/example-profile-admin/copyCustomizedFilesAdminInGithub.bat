@echo off

Rem THIS FILE SHOULD BE USED TO COPY YOUR LOCAL PROFILE AND SETTING INTO TOMCAT WEBAPP FOLDER AND WEBAPP FOLDER
Rem AS WELL AS CUSTOM FILES WITH THE HEADER AND THE FOOTER

Rem set up the source and destinations folders 
set "copyFROM=PATH_TO_YOUR_CUSTOMIZED_FILES"
set "copyTOTomcatWebapp=PATH_TO_TOMCAT_WEBAPP"
set "copyTOwebappInterface=PATH_TO_WEBAPP_INTERFACE"

set "jspincludes=jsp\insludes"
set "metainf=META-INF"
set "webinf=WEB-INF"
set "Re3gistry2Webapp=re3gistry2"
robocopy %copyFROM%%Re3gistry2Webapp%%jspincludes% %copyTOTomcatWebapp%%Re3gistry2Webapp%%jspincludes% /S
robocopy %copyFROM%%Re3gistry2Webapp%%metainf% %copyTOTomcatWebapp%%Re3gistry2Webapp%%metainf% /S
robocopy %copyFROM%%Re3gistry2Webapp%%webinf% %copyTOTomcatWebapp%%Re3gistry2Webapp%%webinf% /S


Rem copy the webapp .html files
set "Re3gistry2ServiceWebapp=webapp\\public_html"
set "destiantionRe3gistry2ServiceWebapp=webapp\\public_html" 
robocopy %copyFROM%%Re3gistry2ServiceWebapp% %copyTOwebappInterface%%destiantionRe3gistry2ServiceWebapp% *.html /S



exit /b