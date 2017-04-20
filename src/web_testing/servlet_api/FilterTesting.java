package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Пример работы с фильтрами в Servlet API
 *
 * <p> Фильтры используются для конвейерной обработки клиентских запросов и серверных ответов 
 */
@WebFilter (	// Мы можем объявить фильтр при помощи аннотаций:
		filterName  	= "testing_filter",	// имя фильтра используется для ссылки на данную копию фильтра из других частей веб-приложения
		urlPatterns 	= {"/Cookie/*"},	// задаем шаблоны URL-адресов, к которым применяется данный фильтр
		servletNames 	= {"HelloWorldServlet"}, // мы можем задать имена конкретных сущностей сервлетов, для которых применяется фильтр
		// мы можем задать параметры инициализации для фильтра из аннотаций
		initParams 		= { @WebInitParam(name = "init_param", value = "Init from annotations") }
		)
public class FilterTesting implements Filter {
	private volatile int mRequestCounter;
	private String mFilterName;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Метод init вызывается контейнером сервлетов единожды при инициализации контекста для
		// каждой сущности данного фильтра:
		mRequestCounter = 0;
		
		mFilterName = filterConfig.getFilterName();
		System.out.println( mFilterName + " filter initialization, init parameter = \""
				// Вычитываем параметр инициализации из объекта FilterConfig:
				+ filterConfig.getInitParameter("init_param") + "\"..." );
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Метод doFilter вызывается при поступлении запроса, подпадающего под область действия фильртра
		// Все подпадающие под запрос фильтры организуются в цепочку, каждый
		// последующий элемент в цепочке доступен через объект FilterChain;
		// Финальным объектом в цепочке является сервлет, который производит окончательную обработку запроса.
		// Порядок следования фильтров определяется порядком их следования в файле web.xml (для аннотаций порядок не определен)
		// В конфликтных ситуациях порядок следования фильтров определяется точностью совпадения шаблона
		// (сначала вызываются фильтры с наименее точным совпадением шаблона, например /*;
		// затем более точные, например /docs/* -> /docs/index.html; затем фильтры с указанием
		// конкретных сервлетов)
		
		// Создаем оболочки для объектов запроса и ответа:
		WrappedRequest wrapped_request = new WrappedRequest ((HttpServletRequest)request);
		WrappedResponse wrapped_response = new WrappedResponse((HttpServletResponse)response, ++mRequestCounter);
		
		// Объект FilterChain позволяет передавать запрос/ответ дальше по цепочке фильтров:
		chain.doFilter( wrapped_request, wrapped_response );
		
		// Вызваем метод WrappedResponse.close() для выполнения фильтрации ответного сообщения
		// и закрытия выходного потока:
		wrapped_response.close();
	}
	
	@Override
	public void destroy() {
		// Метод destroy вызывается контейнером сервлетов перед разрушением контекста для
		// каждой сущности данного фильтра:
		System.out.println( "Destroying " + mFilterName + "..." );
		
		Filter.super.destroy();
	}
}

/**
 * Вспомогательный класс-оболочка для фильтрации запроса
 * 
 * <p>Servlet API предоставляет специальный класс HttpServletRequestWrapper
 * для реализации конвейера фильтрации запросов
 */
class WrappedRequest extends HttpServletRequestWrapper {
	public WrappedRequest (HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		if ( name.startsWith("to_filter") ) {
			// Фильтруем параметры запроса, чьи имена начинаются со строки "to_filter":
			return "Отфильтровано (" + super.getParameter(name) + ")";
		}
		return super.getParameter(name);
	}
}

/**
 * Вспомогательный класс-оболочка для фильтрации ответа
 * 
 * <p>Servlet API предоставляет специальный класс HttpServletResponseWrapper
 * для реализации конвейера фильтрации ответов
 */
class WrappedResponse extends HttpServletResponseWrapper {
	private boolean mDoFilterFlag = false;
	private PrintWriter mResponseWriter = null;
	private int mCurrentRequestCounter;

	public WrappedResponse(HttpServletResponse response, int current_request_counter) {
		super(response);
		
		mCurrentRequestCounter = current_request_counter;
	}
	
	@Override
	public void setContentType(String type) {
		super.setContentType(type);
		// Мы будем делать фильтрацию только html-страниц:
		mDoFilterFlag = type.startsWith("text/html");
	}
	
	@Override
	public PrintWriter getWriter() throws IOException {
		if ( mResponseWriter == null ) {
			if ( mDoFilterFlag ) {
				mResponseWriter = new WrappedWriter(super.getWriter());
			} else {
				mResponseWriter = super.getWriter();
			}
		}
		return mResponseWriter;
	}
	
	public void close () {
		if ( mResponseWriter != null ) {
			mResponseWriter.close();
		}
	}
	
	/**
	 * Вспомогательный класс-оболочка для объекта PrintWriter
	 */
	private class WrappedWriter extends PrintWriter {
		public WrappedWriter ( Writer origin ) {
			super(origin);
		}
		
		@Override
		public void close() {
			// Перехватываем метод close() дочернего объекта Writer и добавляем html-текст
			// перед непосредственным закрытием потока:
			println("<html><body><footer><hr>Счетчик запросов к веб-приложению: " + mCurrentRequestCounter + "</footer></body></html>");
			
			super.close();
		}
	}
	
}

