@echo off

rem Запускаем HTTP-сервер, находящийся в классе java_api_testing.net_api.TinyHttpServer.

rem Данный сервер используется для динамической загрузки байт-кода классов.

PUSHD .\..\..\..

start java -cp .\bin java_api_testing.net_api.TinyHttpServer 80

POPD
