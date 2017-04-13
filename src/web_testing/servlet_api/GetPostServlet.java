package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * —ервлет, демонстрирующий получение параметров, переданных вместе с HTTP-запросами GET и POST
 */
@WebServlet("/GetPost")
public class GetPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		showRequestParams(request, response, "GET");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		showRequestParams(request, response, "POST");
	}
	
	private void showRequestParams (HttpServletRequest request, HttpServletResponse response, String request_type) throws IOException {
		// ќпредел€ем кодировку тела запроса (она нам понадобитс€ в дальнейшем дл€ декодировани€ данных):
		String srcCharset = request.getCharacterEncoding();
		if ( srcCharset == null ) { srcCharset = "ISO-8859-1"; }
		
		// ƒл€ кодировани€ ответа используем кодировку UTF-8 (наиболее удачна€ кодировка из всех существующих):
		String dstCharset = "utf-8";
		
		// ќб€зательно необходимо установить тип ответа, чтобы клиент мог правильно восприн€ть наш ответ:
		response.setContentType("text/html");
		// ”станавливаем кодировку ответного сообщени€:
		response.setCharacterEncoding(dstCharset);
		
		// ƒл€ отправки данных в выходной поток используем объект PrintWriter;
		// ѕосле окончани€ вывода данных рекомендуетс€ выполн€ть close() дл€ выходного потока:
		try ( PrintWriter out = response.getWriter() ) {
			// ‘ормируем текст ответа в формате HTML-странички,
			// ѕри этом рекомендуетс€ €вно указывать кодировку странички в meta-теге:
			out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>—писок параметров</title></head>" +
					"<body><h1>ѕараметры (запрос: " + request_type + ")</h1>" );
			
			// ѕечатаем параметры, переданные вместе с HTTP-запросом:
			boolean decodeParams = request_type.equalsIgnoreCase("POST");
			for ( Map.Entry<String, String[]> param : request.getParameterMap().entrySet() ) {
				out.print("<li>" + param.getKey() + " = ");
				
				//  аждый параметр запроса может иметь несколько значений, при этом
				// параметры, заданные через GET-запрос, декодируютс€ сервером автоматически, а
				// параметры, заданные через POST-запрос, необходимо декодировать вручную:
				String param_vals = Arrays.asList(param.getValue()).toString();
				out.print ( decodeParams ? new String(param_vals.getBytes(srcCharset), dstCharset) : param_vals );
				
				out.println("</li>");
			}
			
			// ¬ыводим 2 формы с 2-м€ пол€ми и кнопкой в каждой:
			
			out.println( "<h3>POST-форма:</h3>" );
			// ѕри отправке данных 1-а€ форма будет использовать запрос POST:
			out.println("<p><form method=\"POST\" action=\"" + request.getRequestURI() + "\">"
					+ "ѕоле 1 <input name=\"PostField 1\" size=20><br>"
					+ "ѕоле 2 <input name=\"PostField 2\" size=20><br>"
					+ "<br><input type=\"submit\" value=\"¬ыполнить запрос POST\"></form>"
					);
			
			out.println( "<h3>GET-форма:</h3>" );
			// ѕри отправке данных 2-а€ форма будет использовать запрос GET:
			out.println("<p><form method=\"GET\" action=\"" + request.getRequestURI() + "\">"
					+ "ѕоле 1 <input name=\"GetField 1\" size=20><br>"
					+ "ѕоле 2 <input name=\"GetField 2\" size=20><br>"
					+ "<br><input type=\"submit\" value=\"¬ыполнить запрос GET\"></form>"
					);
			
			out.println("</body></html>");
		}
	}

}
