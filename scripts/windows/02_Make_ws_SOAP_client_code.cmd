@echo off

rem ������ ������ ���������� ������ ������������� ������� wsimport �� ����������� �������� JDK
rem ������ ������� ����������� ��� ��������� �������� ����� ��� ���������� Java API for XML Web Services (JAX-WS)
rem ������ ������������� ���������� JAX-WS ��������� � ������: java_api_testing.net_api.ws_testing.JAXWS_WebClientTesting
rem ����� ����������:
rem -s <directory> �����, ���� ����� ����������� �������� ����, ��������������� ��������
rem -d <directory> �����, ���� ����� ����������� .class-�����, ���������� ��� ���������� �������� �����
rem -p <package name> ����� ������� ��������� �������� ����� ��� ��������������� �������
rem � �������� ��������� ����� ������� WSDL-���� � ��������� ���������� ��� ��� �������

PUSHD .\..\..

wsimport -s ./src -d ./bin -verbose -p java_api_testing.net_api.ws_testing.generated http://www.webservicex.net/BibleWebservice.asmx?WSDL

POPD