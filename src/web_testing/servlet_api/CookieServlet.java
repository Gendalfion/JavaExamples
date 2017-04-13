package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет, демонстрирующий использование объекта Cookie для сохранения
 * информации в клиентских браузерах при помощи технологии куки
 */
@WebServlet("/Cookie/*" /* Указываем шаблон для URL-адресов, принимаемых данным сервлетом */)
public class CookieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dstCharset = "utf-8";
		String srcCharset = request.getCharacterEncoding();
		if ( srcCharset == null ) { srcCharset = "ISO-8859-1"; }
		
        response.setContentType("text/html");
        response.setCharacterEncoding(dstCharset);
        
        try (PrintWriter out = response.getWriter()) {
        	out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>Тестирование Cookie</title></head><body><p>" );
        	
        	if ( "true".equalsIgnoreCase( request.getParameter("setcookie") ) ) {
        		// Обрабатываем нажатие пользователем кнопки на форме:
        		
        		// Вычитываем параметры, переданные формой:
        		int agedCookieTime = getIntParameter (request, "AgedCookieTime", 60);
        		// Используем объект HttpSession для сохранения данных, введенных в поля формы,
        		// между клиентскими запросами:
        		request.getSession().setAttribute("AgedCookieTime", String.valueOf(agedCookieTime));
        		String pathCookieURL = request.getParameter("PathCookieURL");
        		request.getSession().setAttribute("PathCookieURL", pathCookieURL);
        		
        		// Создаем объекты Cookie
        		// Куки - это пары имя(строковое)/значение(текстовое, только печатные ASCII символы, с некоторыми исключениями)
        		// Куки формируются на сервере и отправляются клиенту в ответном HTTP-пакете.
        		// Кроме имени и значения сервер отправляет настройки для каждого объекта куки,
        		// такие как время жизни и область действия.
        		// При каждом следующем запросе клиент отправляет куки обратно на сервер,
        		// предварительно определяя необходимость отправки по полученным вместе с куки настройкам 
        		// (сами настройки на сервер обратно не отправляются).
        		Cookie sessionCookie = new Cookie("SessionCookie",
        				// Перекодируем параметры, полученные через POST-форму
        				// из кодировки запроса srcCharset в кодировку ответа dstCharset (для корректной поддержки русских букв):
        				decodeParameter(request.getParameter("SessionCookie"), srcCharset, dstCharset) );
        		
        		Cookie agedCookie = new Cookie("AgedCookie", 
        				decodeParameter(request.getParameter("AgedCookie"), srcCharset, dstCharset) );
        		
        		Cookie pathCookie = new Cookie("PathCookie", 
        				decodeParameter(request.getParameter("PathCookie"), srcCharset, dstCharset) );
        		
        		String cookieOperation;
        		if ( request.getParameter("ClearCookie") != null ) {
        			// Пользователь нажал кнопку очистки всех куки:
        			
        			// Устанавливаем время жизни куки в 0, что информирует браузер клиента о том,
        			// что куки необходимо удалить:
        			sessionCookie.setMaxAge(0);
        			agedCookie.setMaxAge(0);
        			pathCookie.setMaxAge(0);
        			cookieOperation = "удалены";
        		} else {
        			// Устанавливаем время жизни куки, заданное пользователем:
        			agedCookie.setMaxAge(agedCookieTime);
        			
        			// Устанавливаем путь для куки, заданный пользователем.
        			// Браузер будет отправлять данные куки на сервер, только если
        			// текущий запрошенный пользователем путь будет совпадать с путем куки
        			// (или запрошенный путь будет являться дочерним для пути куки):
        			pathCookie.setPath( pathCookieURL);
        			
        			cookieOperation = "установлены";
        		}
        		
        		try {
        			// Добавляем куки в ответный HTTP-пакет:
        			response.addCookie(sessionCookie);
        			response.addCookie(agedCookie);
        			response.addCookie(pathCookie);
        			out.println("<h1>Файлы куки " + cookieOperation + "...</h1>");
        		} catch ( Exception exception ) { 
        			out.println("<h2>Ошибка при изменении куки: " + exception + "</h2>");
        		}
        		out.println("<hr><p><a href=\"" + request.getRequestURI() + "\">Вернуться на главную</a>");
        	} else {
        		out.println("<h2>Ваши куки:</h2>");
        		// Выводим форму для управления куки клиента:
        		out.println( "<form method=POST action=" + request.getRequestURI() + "?setcookie=true>" );
                
                String sessionCookieValue = "";
                String agedCookieValue = "";
                String pathCookieValue = "";
                
                // Выделяем полный путь, запрошенный клиентом:
                String currentFullPath = request.getContextPath() + request.getServletPath();
                if ( request.getPathInfo() != null ) {
                	currentFullPath += request.getPathInfo();
                }
                
                String deleteCookiesBtnEnabled = "disabled";
                
                // Метод HttpServletRequest.getCookies() возвращает все куки, отправленные клиентом:
                Cookie [] cookies = request.getCookies();
                if ( cookies != null ) {
                	// Выделяем значения интересующих нас куки из полученного массива:
                	for ( Cookie cookiesItem : cookies ) {
                		if ( cookiesItem.getName().equalsIgnoreCase("SessionCookie") ) {
                			sessionCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		} else 
                		if ( cookiesItem.getName().equalsIgnoreCase("AgedCookie") ) {
                			agedCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		} else 
            			if ( cookiesItem.getName().equalsIgnoreCase("PathCookie") ) {
            				pathCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		}
                	}
                }
                
                // Рисуем поля для редактирования значений и настроек для куки:
                out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
                
                out.println( "<tr><td align=\"right\" width=\"200\">Сессионные куки:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=SessionCookie value=\"" 
                		+ sessionCookieValue + "\" size=20></td></tr>");
                
                out.println( "<tr><td align=\"right\" width=\"200\">Временные куки:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=AgedCookie value=\"" + agedCookieValue + "\" size=20></td>"
                		+ "<td>Время жизни куки (с):<input name=AgedCookieTime value=\"" 
                		+ getIntSessionParameter(request, "AgedCookieTime", 60 ) + "\" size=20></td></tr>");
                
                out.println( "<tr><td align=\"right\" width=\"200\">Куки с областью действия:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=PathCookie value=\"" + pathCookieValue + "\" size=20></td>"
                		+ "<td>Область действия куки (path):<input name=PathCookieURL value=\"" 
                		+ getStringSessionParameter(request, "PathCookieURL", currentFullPath + "/some_path" ) 
                		+ "\" size=35></td></tr>");
                
                out.println( "</table>" );
                
                // Рисуем кнопки добавления/очистки пользовательских куки:
                out.println(  "<p> Текущий путь (path): " + currentFullPath );
                out.println(  "<p><input type=submit value=\"Установить куки...\">" );
                out.println(  "<p><input type=submit name=ClearCookie value=\"Очистить куки...\" " + deleteCookiesBtnEnabled + "></form>" );
        	}
        	
        	out.println( "</body></html>" );
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String decodeParameter ( String param, String srcCharset, String dstCharset ) {
		try {
			return new String(param.getBytes(srcCharset), dstCharset);
		} catch ( UnsupportedEncodingException uee ) {
		}
		return param;
	}
	
	private int getIntParameter ( HttpServletRequest request, String param_name, int default_val ) {
		try { return Integer.parseInt( request.getParameter(param_name) ); } catch ( Exception e ) {}
		return default_val;
	}
	
	private int getIntSessionParameter ( HttpServletRequest request, String param_name, int default_val ) {
		try { return Integer.parseInt( (String)request.getSession().getAttribute(param_name) ); } catch ( Exception e ) {}
		return default_val;
	}
	
	private String getStringSessionParameter ( HttpServletRequest request, String param_name, String default_val ) {
		Object param_val = request.getSession().getAttribute(param_name);
		if ( param_val instanceof String ) { 
			return (String)param_val; 
		}
		return default_val;
	}
}
