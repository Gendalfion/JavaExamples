@echo off

rem —крипт дл€ запуска Web-сервера Apache Tomcat 9.0

rem *** ƒл€ запуска сервера потребуетс€:
rem 1. –аспаковать в любое место на диске архив \thirparty\apache-tomcat-9.0.0.M19.zip
rem 2. ”становить переменную CATALINA_HOME в соответствии с местом распаковки архива (см. ниже по коду скрипта) 
rem 3. ”бедитьс€, что в системной переменной PATH пробит путь до места установки JDK или JRE
rem 4. ѕри успешном запуске сервера, браузер должен предоставл€ть доступ к примерам
rem    веб-приложений по адресу http://localhost

setlocal

rem CATALINA_HOME - хранит путь распаковки исполн€емых файлов сервера Apache, а также байт-коды общих библиотек дл€ всех веб-приложений
set "CATALINA_HOME=%cd%\..\..\..\apache-tomcat-9.0.0.M19"

rem CATALINA_BASE - по-умолчанию совпадает с CATALINA_HOME, но может отличатьс€. ѕо данному пути сервер получает конфигурацию своей среды,
rem а также здесь могут содержатьс€ специфичные дл€ данного контекста библиотеки
set "CATALINA_BASE=%cd%\..\..\CatalinaBase"

if exist "%CATALINA_HOME%\bin\startup.bat" goto startup_apache_server

echo Error while finding server startup script on path: "%CATALINA_HOME%\bin\startup.bat"

goto exit

:startup_apache_server

call "%CATALINA_HOME%\bin\startup.bat"

:exit

endlocal

