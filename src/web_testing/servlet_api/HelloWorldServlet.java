package web_testing.servlet_api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Демонстрация простейшего HTTP-сервлета "Hello, World!"<br>
 *
 * <p><b>Для запуска примера понадобится:</b>
 * <p>1. Установить Eclipse для <a href="http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon3">
 * 	Java EE (Enterprise Edition)</a><br>
 * 
 * <p>2. Скачать сервер для запуска веб-приложений.<br>
 * 	В нашем случае скачан сервер <a href="http://tomcat.apache.org/download-90.cgi">
 * 	Apache Tomcat v9.0</a> (архив расположен в корне проекта MyStudy)<br>
 * 
 * <p>3. Установить сервер в среде Eclipse:<br>
 * 	Window->Show view->Servers->[ПКМ]->New->Server->"Выбрать свой сервер"->"Установить сервер при помощи мастера"<br>
 * 
 * <p>4. Создать проект "Dynamic Web Project", или изменить существующий, для этого:<br>
 * 	Project->Properties->Project Facets->Выбрать пункт Dynamic Web Module.<br>
 *  Справа, на вкладке Runtimes отметить свой сервер<br>
 *  
 * <p>5. Для компиляции примера нужна библиотека, реализующая спецификацию javax.servlet.*.<br>
 * 	Для нашего случая, библиотека servlet-api.jar просто скопирована в дерево<br>
 *  каталогов проекта из библиотек сервера веб-приложений (в папку: WebContent/WEB-INF/lib/,<br> 
 *  "правильные" способы почему-то не работали)<br>
 *  
 * <p>6. Добавить сервлет HelloWorldServlet в контейнер установленного сервера:<br>
 * 	Window->Show view->Servers->[ПКМ на сервере]->Add and Remove...<br>
 * 
 * <p>7. Запустить сервер:<br>
 * 	Window->Show view->Servers->[ПКМ на сервере]->Start<br>
 * 
 * <p>8. Проверить сервер можно набрав в строке браузера URL:<br>
 * 	http://localhost[:server_port(if different from port 80)]/MyStudy/hello<br>
 * 
 * <p>Для удобства использования пакета javax.servlet.* можно присоединить<br>
 * 	к проекту исходные коды с документацией, для этого:<br>
 * 	Кликнуть с зажатым Ctrl по надписи {@link javax.servlet.http.HttpServlet}<br>
 * 	Нажать кнопку Attach Source... и указать путь к архиву apache-tomcat-9.0.0.M19-src.zip<br>
 * 	(расположен в корневой папке проекта MyStudy)
 */
@WebServlet("/hello") // Объявляем сервлет при помощи аннотации @WebServlet с указанием шаблона URL для доступа к сервлету
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Переопределяем метод класса HttpServlet, который отрабатывает HTTP GET-запрос:
		
		// Устанавливаем MIME-тип отправляемых клиенту данных:
		response.setContentType("text/html");
		
		// Выводим данные в ответный поток response: 
		response.getWriter().println(
				"<html><head><title>Hello, client!</title></head>" +
				"<body><h1>I am Hello World Servlet!</h1></body></html>");
	}
}
