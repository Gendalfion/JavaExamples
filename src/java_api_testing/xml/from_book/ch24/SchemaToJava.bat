@echo off

rem � ������ ������� ��������������� ������������� ������� xjc (������ � ����������� JDK)
rem ��� ��������� Java ���� �� ������ ����� XSD (W3C XML Schema Definition file)

setlocal

set XSD_FILE="zooinventory.xsd"
if not "%1" == "" set "XSD_FILE=%1"

set GEN_DIR="genXSD_to_Java"
if not "%2" == "" set "GEN_DIR=%2"

set GEN_PACK="mypackage"
if not "%3" == "" set "GEN_PACK=%3"

if not exist %GEN_DIR% mkdir %GEN_DIR%

echo Converting XML W3C Schema %XSD_FILE% to Java code...

rem �������� ������� xjc � ��������� �������:
xjc -d %GEN_DIR% -p %GEN_PACK% %XSD_FILE%

endlocal 

pause