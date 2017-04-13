package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет, демонстрирующий использование разделяемого контекста веб-приложения,
 * а также использование асинхронного контекста для обработки запросов сервлета
 * 
 * <p>Данный класс используется в паре классом: {@link ContextTestingListener}
 */
@WebServlet(
		urlPatterns={"/ContextTesting"},
		// Для включения поддержки работы сервлета в асинхронном режиме
		// используется параметр asyncSupported в аннотации WebServlet:
		asyncSupported=true)
public class ContextTestingAsyncServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dstCharset = "utf-8";
		
		response.setContentType("text/html");
        response.setCharacterEncoding(dstCharset);
        
        if ( request.getParameter("delayedRequest") != null ) {
        	// Обрабатываем нажатие кнопки "Выполнить запрос...":
        	
        	// Вычитываем установленное пользователем время задержки отправки ответа на запрос:
        	long delayTime = getIntParameter(request, "ResponseDelayMs", 2500);
        	
        	// Метод HttpServletRequest.startAsync() запускает асинхронную обработку
        	// текущего запроса. Он возвращает объект AsyncContext при помощи
        	// которого мы можем асинхронно получать доступ к данным запроса и ответа на данный запрос:
        	final AsyncContext asyncContext = request.startAsync();
        	final long requestTimeMillis = System.currentTimeMillis();
        	final String requestURI = request.getRequestURI();
        	
        	// Метод HttpServlet.getServletContext() позволяет получить доступ к контексту приложения.
        	// При помощи данного контекста сервлеты могут разделять общие данные на уровне 
        	// веб-приложения, а также получать доступ ко внешним ресурсам ОС:
        	((ScheduledExecutorService)getServletContext().getAttribute("web_testing.servlet_api.ScheduledExecutor"))
        	// Мы получаем ссылку на объект ScheduledExecutorService, заранее подготовленный при
        	// инициализации контекста веб-приложения в объекте ContextTestingListener.
        	// Запускаем отложенную задачу в планировщике ScheduledExecutor:
        		.schedule( new Runnable() {
					@Override public void run() {
						// Асинхронный поток выполнения отложенной задачи:
						
						// При помощи асинхронного контекста asyncContext получаем доступ к выходному потоку ответного сообщения:
						try (PrintWriter out = asyncContext.getResponse().getWriter()) {
							// Выводим ответное сообщение в выходной поток:
							out.println( "<html><head><meta charset=\"" + dstCharset 
									+ "\" /><title>Асинхронный ответ</title></head><body><p>" );
							
							out.println("<h2>Ответ сформирован за " + (System.currentTimeMillis() - requestTimeMillis) + " мс</h2>");
							
							out.println("<hr><p><a href=\"" + requestURI + "\">Вернуться на главную</a>");
							out.println( "</body></html>" );
						} catch ( IOException ioe ) {
							throw new RuntimeException(ioe);
						} finally {
							// При окончании обработки запроса мы должны обязательно вызвать
							// метод complete() для асинхронного контекста;
							// Это даст знать серверу, что обработка запроса завершена:
							asyncContext.complete();
						}
					}
				}, delayTime, TimeUnit.MILLISECONDS);
        } else {
	        try (PrintWriter out = response.getWriter()) {
	        	// Выводим форму с кнопкой и полем для ввода задержки ответного пакета:
	        	out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>Асинхронный сервлет</title></head><body><p>" );
	        	out.println("<h2>Запрос к асинхронному сервлету:</h2>");
	        	out.println( "<form method=POST action=" + request.getRequestURI() + "?delayedRequest=>" );
	        	out.println( "<p>Время задержки ответа (мс):<input name=ResponseDelayMs value=\"2500\" size=10>");
	        	out.println(  "<p><input type=submit value=\"Выполнить запрос...\">" );
	        	out.println( "</form></body></html>" );
	        }
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private int getIntParameter ( HttpServletRequest request, String param_name, int default_val ) {
		try { return Integer.parseInt( request.getParameter(param_name) ); } catch ( Exception e ) {}
		return default_val;
	}
}
