@echo off

rem ������ ������ �������� .war-����� ��� ���-����������, ���������� �� ��������� ������� MyStudy
rem ���� MyStudy.war ��������� � ����� %CATALINA_BASE%\webapps\
rem ������ ����� �������� ������� zip-������� � ������������ ����������
rem ������ Apache Tomcat ������������� ���������� ����� %CATALINA_BASE%\webapps\ � �������������
rem ������������� ��� .war ����� � ���� ��������
rem �� ��������� ���-���������� MyStudy �������� �� URL: http://<host-name>/MyStudy

rem ����� -keeptmp ��������� ��������� ���������� �������� ����������� ����� .\tmp 

setlocal 

rmdir tmp /s /q  >nul 2>nul

echo Making required temporary directories...
mkdir tmp
mkdir tmp\WEB-INF
mkdir tmp\WEB-INF\classes

if "%CLASSES_HOME%" == ""  set "CLASSES_HOME=%cd%\..\..\bin"
if "%CATALINA_BASE%" == "" set "CATALINA_BASE=%cd%\..\..\CatalinaBase"
echo. & echo Using home dir for java-classes: %CLASSES_HOME%
echo Using base dir for Tomcat Server: %CATALINA_BASE%

echo. & echo Copying web-application files to temporary directory...
copy /A /Y "%CLASSES_HOME%\*.html" ".\tmp\*.html"
xcopy "%CLASSES_HOME%\secret\*" ".\tmp\secret\*" /I /E
echo .xml > .\tmp\exclude.txt
xcopy "%CLASSES_HOME%\web_testing" ".\tmp\WEB-INF\classes\web_testing" /I /E /EXCLUDE:.\tmp\exclude.txt
erase /Q .\tmp\exclude.txt
xcopy "%CLASSES_HOME%\web_testing\web.xml" ".\tmp\WEB-INF\" /I

echo. & echo Creating .war-archive from temporary directory content...
jar -cvf %CATALINA_BASE%\webapps\MyStudy.war -C tmp\ .

if "%1" == "-keeptmp" goto exit
echo. & echo Deleting temporary directory...
rmdir tmp /s /q  >nul 2>nul

:exit

endlocal