@echo off

rem ��������� HTTP-������, ����������� � ������ java_api_testing.net_api.TinyHttpServer.

rem ������ ������ ������������ ��� ������������ �������� ����-���� �������.

PUSHD .\..\..\..

start java -cp .\bin java_api_testing.net_api.TinyHttpServer 80

POPD
