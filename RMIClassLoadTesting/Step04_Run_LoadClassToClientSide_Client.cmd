@echo off

rem ��������� ���������� ����� ������-���������� RMI-����������.
rem ������ ������ ����������� � ������� ����� GUI, ����-��� ��������
rem ����������� �� ���������� �������. 

rem ��� �������� ����������� ������� ������������ ������������ �������� ������� � Java:

rem ����. ���������� java.rmi.server.codebase ��������� ��������������� ������� �� ��������� �������.
rem     � �������� ���������� ������� ����� �������������� HTTP ������, FTP ������, jar ����, ����� � �������� �������.
rem     ��������� Java ��������� ����-��� ������� � ���������� �������, ���� �� ������� ���������
rem     ����� ������ �������� ������������� classpath ������� ����� ����������.

rem ��-��������� ������������ �������� ������� � Java ���������.
rem ����� �� ��������� ���������� ��������� ��������� ������������ Java, ��� �����:
rem * ���������� �������� ��������� ������������ ����� ����������� ����. ���������� java.security.manager;
rem * ������ ����������� ���������� ��� ������ ����. ���������� java.security.policy

start javaw -cp .\RMI_client.jar^
    -Djava.rmi.server.codebase="http://localhost/"^
    -Djava.security.policy=security.policy^
    -Djava.security.manager^
    java_api_testing.rmi.client.RMIClassLoadTesting localhost ServerRemoteImpl "Title sent from client script"