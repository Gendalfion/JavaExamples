package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет, использующийся для перехвата ошибок и исключений в веб-приложении
 * 
 * <p>Спецификация servlet_api на данный момент не определяет аннотаций для сервлетов обработки ошибок,
 * поэтому настройку обработчиков ошибок необходимо производить при помощи дескриптора приложения web.xml
 */
public class ErrorHandleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		try ( PrintWriter out = response.getWriter() ) {
			out.println("<html><head><title>Error occurred!</title></head><body>");
			
			// Для обработки ошибки мы можем воспользоваться информацией,
			// которая предоставляется вместе с объектом HttpServletRequest:
			Integer  	status_code		= (Integer)		request.getAttribute("javax.servlet.error.status_code");
			Class<?> 	exception_type 	= (Class<?>)	request.getAttribute("javax.servlet.error.exception_type");
			String 		message 		= (String)		request.getAttribute("javax.servlet.error.message");
			Throwable 	exception 		= (Throwable)	request.getAttribute("javax.servlet.error.exception");
			String 		request_uri		= (String)		request.getAttribute("javax.servlet.error.request_uri");
			String 		servlet_name	= (String)		request.getAttribute("javax.servlet.error.servlet_name");
			
			
			out.println("<h1>" + request.getContextPath() + " application error " + status_code +  "</h1>");
			
			out.println("<hr><h3>Here is an error information:</h3>");
			out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
			
			final String table_row_pattern = "<tr><td align=\"left\" width=\"200\">%s</td>"
											+"<td align=\"left\">%s</td></tr>\n";
			
			// Печатаем информацию по полученной ошибке:
			out.printf(table_row_pattern, "status_code", 	status_code);
			out.printf(table_row_pattern, "exception_type", (exception_type != null) ? exception_type.getName() : "type is undefined");
			out.printf(table_row_pattern, "message", 		message);
			out.printf(table_row_pattern, "exception", 		exception);
			out.printf(table_row_pattern, "request_uri", 	request_uri);
			out.printf(table_row_pattern, "servlet_name", 	servlet_name);
			
			out.println( "</table>" );
			
			out.println("</body></html>");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
