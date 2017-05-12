В данной папке собраны примеры по скриптовому языку BeanShell на платформе Java.

Для запуска примеров понадобится библиотека расширения BeanShell (bsh-x.xxx.jar, скачивается с оф. сайта http://www.beanshell.org/).

Установка BeanShell выполняется путем добавления jar-файла в classpath для JVM (например, скопировав в папку %JAVA_HOME%/jre/lib/ext).

Для удобства работы с BeanShell можно использовать редактор jEdit (http://www.jedit.org) с плагином Console (скачивается при помощи Plugin manager внутри программы).

Для запуска скриптов с jEdit:
	1. File->Open... (Открываем скрипт *.bsh)
	2. Plugins->Console->Console
	2. Набираем в консоли: java bsh.Interpreter ${f} (запускает интерпретатор BeanShell для текущего открытого файла в редакторе)