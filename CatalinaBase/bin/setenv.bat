@echo off

rem ƒанный скрипт используетс€ во врем€ старта сервера Apache Tomcat
rem ≈го задача состоит в установке переменных JAVA_HOME (JRE_HOME)
rem на основе поиска пути установки исполн€емых файлов javac (java) в системе

rem Trying to find JDK home directory:
FOR /F "tokens=* USEBACKQ" %%F IN (`where javac`) DO (
    SET JAVA_HOME=%%F
    
    echo.%JAVA_HOME% | findstr /C:"\bin\javac" 1>nul
    if errorlevel 0 (
        goto jdk_home_found
    )
)

rem Seems that JDK is not installed, trying to find JRE home directory instead:
FOR /F "tokens=* USEBACKQ" %%F IN (`where java`) DO (
    SET JRE_HOME=%%F
    
    echo.%JRE_HOME% | findstr /C:"\bin\java" 1>nul
    if errorlevel 0 (
        goto jre_home_found
    )
)

rem Required Java installation not found, ending startup process:
echo Error while finding Java home, please check your JDK/JRE installation!
goto exit

:jdk_home_found
SET JAVA_HOME=%JAVA_HOME%\..\..\
PUSHD %JAVA_HOME%
SET JAVA_HOME=%cd%
POPD
echo JDK found on path: %JAVA_HOME%

goto server_startup

:jre_home_found
SET JRE_HOME=%JRE_HOME%\..\..\
PUSHD %JRE_HOME%
SET JRE_HOME=%cd%
POPD
echo JRE found on path: %JRE_HOME%

goto server_startup

:server_startup

:exit