@echo off

rem «апускает клиентскую часть клиент-серверного RMI-приложени€.
rem ƒанный клиент запрашивает у сервера класс GUI, байт-код которого
rem отсутствует на клиентской стороне. 

rem ƒл€ загрузки недостающих классов используетс€ динамическа€ загрузка классов в Java:

rem сист. переменна€ java.rmi.server.codebase указывает местонахождение классов на удаленном ресурсе.
rem     ¬ качестве удаленного ресурса может использоватьс€ HTTP сервер, FTP сервер, jar файл, место в файловой системе.
rem     ѕлатформа Java загружает байт-код классов с удаленного ресурса, если не находит требуемый
rem     класс внутри локально установленной classpath текущей среды выполнени€.

rem ѕо-умолчанию динамическа€ загрузка классов в Java запрещена.
rem „тобы ее разрешить необходимо настроить диспетчер безопасности Java, дл€ этого:
rem * Ќеобходимо включить диспетчер безопасности путем определени€ сист. переменной java.security.manager;
rem * «адать необходимые разрешени€ при помощи сист. переменной java.security.policy

start javaw -cp .\RMI_client.jar^
    -Djava.rmi.server.codebase="http://localhost/"^
    -Djava.security.policy=security.policy^
    -Djava.security.manager^
    java_api_testing.rmi.client.RMIClassLoadTesting localhost ServerRemoteImpl "Title sent from client script"