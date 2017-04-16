@echo off

rem ������ ��� ������� Web-������� Apache Tomcat 9.0

rem *** ��� ������� ������� �����������:
rem 1. ����������� � ����� ����� �� ����� ����� \thirparty\apache-tomcat-9.0.0.M19.zip
rem 2. ���������� ���������� CATALINA_HOME � ������������ � ������ ���������� ������ (��. ���� �� ���� �������) 
rem 3. ���������, ��� � ��������� ���������� PATH ������ ���� �� ����� ��������� JDK ��� JRE
rem 4. ��� �������� ������� �������, ������� ������ ������������� ������ � ��������
rem    ���-���������� �� ������ http://localhost

setlocal

rem CATALINA_HOME - ������ ���� ���������� ����������� ������ ������� Apache, � ����� ����-���� ����� ��������� ��� ���� ���-����������
set "CATALINA_HOME=%cd%\..\..\..\apache-tomcat-9.0.0.M19"

rem CATALINA_BASE - ��-��������� ��������� � CATALINA_HOME, �� ����� ����������. �� ������� ���� ������ �������� ������������ ����� �����,
rem � ����� ����� ����� ����������� ����������� ��� ������� ��������� ����������
set "CATALINA_BASE=%cd%\..\..\CatalinaBase"

if exist "%CATALINA_HOME%\bin\startup.bat" goto startup_apache_server

echo Error while finding server startup script on path: "%CATALINA_HOME%\bin\startup.bat"

goto exit

:startup_apache_server

call "%CATALINA_HOME%\bin\startup.bat"

:exit

endlocal

