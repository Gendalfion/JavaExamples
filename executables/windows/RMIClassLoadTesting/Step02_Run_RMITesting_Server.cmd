@echo off

rem «апускаем класс java_api_testing.rmi.server.ServerRemoteImpl, имплементирующий
rem удаленный RMI-интерфейс java_api_testing.rmi.ServerRemote.

rem ¬ данном скрипте также настраиваетс€ динамическа€ загрузка классов с удаленного сервера:

rem сист. переменна€ java.rmi.server.codebase указывает местонахождение классов на удаленном ресурсе.
rem     ¬ качестве удаленного ресурса может использоватьс€ HTTP сервер, FTP сервер, jar файл, место в файловой системе.
rem     ѕлатформа Java загружает байт-код классов с удаленного ресурса, если не находит требуемый
rem     класс внутри локально установленной classpath текущей среды выполнени€.

rem ѕо-умолчанию динамическа€ загрузка классов в Java запрещена.
rem „тобы ее разрешить необходимо настроить диспетчер безопасности Java, дл€ этого:
rem * Ќеобходимо включить диспетчер безопасности путем определени€ сист. переменной java.security.manager;
rem * «адать необходимые разрешени€ при помощи сист. переменной java.security.policy

start java -cp .\RMI_server.jar^
    -Djava.rmi.server.codebase="http://localhost/"^
    -Djava.security.manager^
    -Djava.security.policy=security.policy^
    java_api_testing.rmi.server.ServerRemoteImpl ServerRemoteImpl
