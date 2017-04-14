@echo off

setlocal

set "CATALINA_HOME=%cd%\..\..\..\apache-tomcat-9.0.0.M19"

set "CATALINA_BASE=%cd%\..\..\CatalinaBase"

if exist "%CATALINA_HOME%\bin\startup.bat" goto startup_apache_server

echo Error while finding server stratup script on path: "%CATALINA_HOME%\bin\startup.bat"

goto exit

:startup_apache_server

call "%CATALINA_HOME%\bin\startup.bat"

:exit

endlocal

