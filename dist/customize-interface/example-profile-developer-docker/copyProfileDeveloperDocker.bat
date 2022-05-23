@echo off

Rem THIS FILE SHOULD BE USED TO COPY YOUR LOCAL PROFILE AND SETTING INTO GITHUB FOLDER
Rem AS WELL AS CUSTOM FILES WITH THE HEADER AND THE FOOTER, WITH ECL AND SHIRO(inspire_local) OR ECAS (inspire_server)

Rem set up the source and destinations folders of the Repository where to copy from and the repo where to copy to SOURCE folder
set "copyFROM=PATH_TO_YOUR_CUSTOMIZED_FILES"
set "copyTOsourceGithub=PATH_TO_GITHUB_REPO\\sources\\"
set "copyTOdistGithub=PATH_TO_GITHUB_REPO\\dist\\"


set "Re3gistry2buildhelper=Re3gistry2-build-helper"
Rem copy from local to github
robocopy %copyFROM%%Re3gistry2buildhelper% %copyTOsourceGithub%%Re3gistry2buildhelper% /S


set "Re3gistry2Resource=Re3gistry2\src\main\"
set "configurationsfiles=resources\configurations_files"
robocopy %copyFROM%%Re3gistry2Resource%%configurationsfiles% %copyTOsourceGithub%%Re3gistry2Resource%%configurationsfiles% /S

set "jspincludes=jsp\includes"
set "metainf=META-INF"
set "webinf=WEB-INF"
set "Re3gistry2Webapp=Re3gistry2\src\main\webapp\"
robocopy %copyFROM%%Re3gistry2Webapp%%jspincludes% %copyTOsourceGithub%%Re3gistry2Webapp%%jspincludes% /S
robocopy %copyFROM%%Re3gistry2Webapp%%metainf% %copyTOsourceGithub%%Re3gistry2Webapp%%metainf% /S
robocopy %copyFROM%%Re3gistry2Webapp%%webinf% %copyTOsourceGithub%%Re3gistry2Webapp%%webinf% /S


Rem copy the webapp .html files
set "Re3gistry2ServiceWebapp=Re3gistry2ServiceWebapp"
set "destiantionRe3gistry2ServiceWebapp=webapp" 
robocopy %copyFROM%%Re3gistry2ServiceWebapp% %copyTOdistGithub%%destiantionRe3gistry2ServiceWebapp% /S



exit /b