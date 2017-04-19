@echo off

rem Данный скрипт показывает пример использования утилиты wsimport из стандартной поставки JDK
rem Данная утилита применяется для генерации исходных кодов для технологии Java API for XML Web Services (JAX-WS)
rem Пример использования технологии JAX-WS находится в классе: java_api_testing.net_api.ws_testing.JAXWS_WebClientTesting
rem Опции приложения:
rem -s <directory> Папка, куда будут скопированы исходные коды, сгенерированные утилитой
rem -d <directory> Папка, куда будут скопированы .class-файлы, полученные при компиляции исходных кодов
rem -p <package name> Явным образом указывает корневой пакет для сгенерированных классов
rem В качестве исходного файла берется WSDL-файл с описанием интерфейса для веб сервиса

PUSHD .\..\..

wsimport -s ./src -d ./bin -verbose -p java_api_testing.net_api.ws_testing.generated http://www.webservicex.net/BibleWebservice.asmx?WSDL

POPD