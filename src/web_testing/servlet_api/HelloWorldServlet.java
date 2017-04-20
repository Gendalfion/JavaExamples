package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

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
@WebServlet(urlPatterns = {"/hello"}, 	// Объявляем сервлет при помощи аннотации @WebServlet с указанием шаблона(ов) URL для доступа к сервлету
			name = "HelloWorldServlet" )// Объявляем название-псевдоним сервлета для того, чтобы на него можно было ссылаться в других 
										// аннотациях или дескрипторе веб-приложения 
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Переопределяем метод класса HttpServlet, который отрабатывает HTTP GET-запрос:
		
		// Устанавливаем MIME-тип отправляемых клиенту данных:
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		// Выводим данные в ответный поток response: 
		try ( PrintWriter out = response.getWriter() ) {
			out.println(
				"<html><head><title>Привет, клиент!</title></head>" +
				"<body><h1>Приветсвие от сервлета HelloWorldServlet!</h1>");
			
			// Метод HttpServlet.getInitParameterNames() используется для доступа к параметрам инициализации, переданным данной копии сервлета
			// Параметры устанавливаются в конфигурационных файлах веб-приложения (например, web.xml)
			if ( getInitParameterNames().hasMoreElements() ) {
				out.println("<hr><h3>Инициализационные параметры, переданные сервлету:</h3>");
				out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
				
				// Выводим все параметры, переданные сервлету, в виде таблицы:
				Enumeration<String> param_names = getInitParameterNames();
				while (param_names.hasMoreElements()) {
					String name = (String) param_names.nextElement();
					
					out.println( "<tr><td align=\"left\" width=\"200\">" + name + "</td>"
	                		+ "<td align=\"left\">" + getInitParameter(name) + "</td></tr>");
				}
				out.println( "</table>" );
			}
			
			// Параметры запроса, переданные в GET/POST запросах, 
			// доступны в виде отображения HttpServletRequest.getParameterMap():
			Map<String, String[]> requestParamMap = request.getParameterMap();
			if ( !requestParamMap.isEmpty() ) {
				out.println("<hr><h3>Параметры запроса к сервлету:</h3>");
				out.println( "<table border=\"0\" cellspacing=\"5\">" );
				
				// Выводим все параметры запроса в виде таблицы:
				for ( String param_name : requestParamMap.keySet() ) {
					out.println( "<tr><td align=\"left\" width=\"200\">" + param_name + "</td>"
	                		+ "<td align=\"left\">" + request.getParameter(param_name) + "</td></tr>");
				}
				out.println( "</table>" );
			}
			
			out.println("</body></html>");
		}
	}
}
