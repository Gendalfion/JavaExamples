Для компиляции примеров и дайльнешей работы на платформе Java потребуется:

   1. Скачать и установить инструменты разработчика Java (JDK) для своей операционной системы:
      http://www.oracle.com/technetwork/java/javase/downloads/index.html
      
   2. Скачать IDE для написания и компиляции своих проектов (например, среда Eclipse):
      http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/neonr
      
      Примечание 1: для запуска примеров из пакета web_testing.* понадобится среда с поддержкой Java EE:
      http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon3
      
      Примечание 2: примеры из пакета netbeans_testing.* созданы в среде NetBeans:
      https://netbeans.org/downloads/index.html
      
   3. После установки JDK и IDE импортировать классы из примеров (File->Import) в свой Workspace
   
   4. Открыть любой файл в IDE (*.java файл) и нажать кнопку Run на панели инструментов
   
Структура репозитория:

\--                                      Корневая папка проекта

\  \-- bin                               .class файлы для исполнения Java-машиной (результат компиляции .java файлов средой Eclipse)

\  \-- build                             результаты компиляции и сборки проекта средой NetBeans

\  \-- executables                       папка с командными файлами для запуска некоторых примеров (.java файлы должны быть скомпилированы)

\  \-- src                               папка с исходными кодами примеров (.java файлы)

\  \-- thirdparty                        дополнительные библиотеки, использованные в примерах, не входящие в стандартную сборку Java API

\  \-- scripts                           папка со скриптовыми командными файлами для сборки исполняемых файлов и приложений

\  \-- CatalinaBase                      базовая директория для среды выполнения сервера Apache Tomcat (при ручном запуске сервера при помощи скриптов из папки executables)

\  \-- .settings                         настройки проекта для IDE Eclipse

\  \-- nbproject                         настройки проекта для IDE NetBeans

\  \-- README.md                         Данный документ