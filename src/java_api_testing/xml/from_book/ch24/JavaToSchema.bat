@echo off

rem В данном скрипте демонстрируется использование утилиты schemagen (входит в стандартный JDK)
rem для генерации XSD (W3C XML Schema Definition file) на основе Java-классов, помеченных JAXB-аннотациями

setlocal

set JAVA_CLASS="java_api_testing.xml.from_book.ch24.Inventory"
if not "%1" == "" set "JAVA_CLASS=%1"

set GEN_DIR="genJava_to_XSD"
if not "%2" == "" set "GEN_DIR=%2"

if not exist %GEN_DIR% mkdir %GEN_DIR%

echo Converting Java class %JAVA_CLASS% to XML Schema Definition...

rem Вызываем утилиту schemagen с заданными опциями:
schemagen -d %GEN_DIR% -classpath ..\..\..\..\..\bin\ %JAVA_CLASS%

endlocal 

pause