@echo off

rem ��������� ����� java_api_testing.rmi.server.ServerRemoteImpl, ����������������
rem ��������� RMI-��������� java_api_testing.rmi.ServerRemote.

rem � ������ ������� ����� ������������� ������������ �������� ������� � ���������� �������:

rem ����. ���������� java.rmi.server.codebase ��������� ��������������� ������� �� ��������� �������.
rem     � �������� ���������� ������� ����� �������������� HTTP ������, FTP ������, jar ����, ����� � �������� �������.
rem     ��������� Java ��������� ����-��� ������� � ���������� �������, ���� �� ������� ���������
rem     ����� ������ �������� ������������� classpath ������� ����� ����������.

rem ��-��������� ������������ �������� ������� � Java ���������.
rem ����� �� ��������� ���������� ��������� ��������� ������������ Java, ��� �����:
rem * ���������� �������� ��������� ������������ ����� ����������� ����. ���������� java.security.manager;
rem * ������ ����������� ���������� ��� ������ ����. ���������� java.security.policy

start java -cp .\RMI_server.jar^
    -Djava.rmi.server.codebase="http://localhost/"^
    -Djava.security.manager^
    -Djava.security.policy=security.policy^
    java_api_testing.rmi.server.ServerRemoteImpl ServerRemoteImpl
