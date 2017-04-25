@echo off

rem ������ ������ ���������� ������ ������������� ������� wsimport �� ����������� �������� JDK
rem ������ ������� ����������� ��� ��������� �������� ����� ��� ���������� Java API for XML Web Services (JAX-WS)
rem ������ ������������� ���-������� ��������� � �������: 
rem                              java_api_testing.net_api.ws_testing.JAXWS_WebServiceTesting_Server
rem                              java_api_testing.net_api.ws_testing.JAXWS_WebServiceTesting_Client
rem * ����������: ��� ������ �������, ��������� ����� JAXWS_WebServiceTesting_Server ������ ���� �������...
rem ����� ����������:
rem -s <directory> �����, ���� ����� ����������� �������� ����, ��������������� ��������
rem -d <directory> �����, ���� ����� ����������� .class-�����, ���������� ��� ���������� �������� �����
rem -p <package name> ����� ������� ��������� �������� ����� ��� ��������������� �������
rem � �������� ��������� ����� ������� WSDL-���� � ��������� ���������� ��� ��� �������

PUSHD .\..\..

wsimport -s ./src -d ./bin -verbose -p java_api_testing.net_api.ws_testing.wsImpl http://localhost:8080/myservice?WSDL

POPD