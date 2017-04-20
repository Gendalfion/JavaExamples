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
 * ������ ������ � ��������� � Servlet API
 *
 * <p> ������� ������������ ��� ����������� ��������� ���������� �������� � ��������� ������� 
 */
@WebFilter (	// �� ����� �������� ������ ��� ������ ���������:
		filterName  	= "testing_filter",	// ��� ������� ������������ ��� ������ �� ������ ����� ������� �� ������ ������ ���-����������
		urlPatterns 	= {"/Cookie/*"},	// ������ ������� URL-�������, � ������� ����������� ������ ������
		servletNames 	= {"HelloWorldServlet"}, // �� ����� ������ ����� ���������� ��������� ���������, ��� ������� ����������� ������
		// �� ����� ������ ��������� ������������� ��� ������� �� ���������
		initParams 		= { @WebInitParam(name = "init_param", value = "Init from annotations") }
		)
public class FilterTesting implements Filter {
	private volatile int mRequestCounter;
	private String mFilterName;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// ����� init ���������� ����������� ��������� �������� ��� ������������� ��������� ���
		// ������ �������� ������� �������:
		mRequestCounter = 0;
		
		mFilterName = filterConfig.getFilterName();
		System.out.println( mFilterName + " filter initialization, init parameter = \""
				// ���������� �������� ������������� �� ������� FilterConfig:
				+ filterConfig.getInitParameter("init_param") + "\"..." );
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// ����� doFilter ���������� ��� ����������� �������, ������������ ��� ������� �������� ��������
		// ��� ����������� ��� ������ ������� ������������ � �������, ������
		// ����������� ������� � ������� �������� ����� ������ FilterChain;
		// ��������� �������� � ������� �������� �������, ������� ���������� ������������� ��������� �������.
		// ������� ���������� �������� ������������ �������� �� ���������� � ����� web.xml (��� ��������� ������� �� ���������)
		// � ����������� ��������� ������� ���������� �������� ������������ ��������� ���������� �������
		// (������� ���������� ������� � �������� ������ ����������� �������, �������� /*;
		// ����� ����� ������, �������� /docs/* -> /docs/index.html; ����� ������� � ���������
		// ���������� ���������)
		
		// ������� �������� ��� �������� ������� � ������:
		WrappedRequest wrapped_request = new WrappedRequest ((HttpServletRequest)request);
		WrappedResponse wrapped_response = new WrappedResponse((HttpServletResponse)response, ++mRequestCounter);
		
		// ������ FilterChain ��������� ���������� ������/����� ������ �� ������� ��������:
		chain.doFilter( wrapped_request, wrapped_response );
		
		// ������� ����� WrappedResponse.close() ��� ���������� ���������� ��������� ���������
		// � �������� ��������� ������:
		wrapped_response.close();
	}
	
	@Override
	public void destroy() {
		// ����� destroy ���������� ����������� ��������� ����� ����������� ��������� ���
		// ������ �������� ������� �������:
		System.out.println( "Destroying " + mFilterName + "..." );
		
		Filter.super.destroy();
	}
}

/**
 * ��������������� �����-�������� ��� ���������� �������
 * 
 * <p>Servlet API ������������� ����������� ����� HttpServletRequestWrapper
 * ��� ���������� ��������� ���������� ��������
 */
class WrappedRequest extends HttpServletRequestWrapper {
	public WrappedRequest (HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		if ( name.startsWith("to_filter") ) {
			// ��������� ��������� �������, ��� ����� ���������� �� ������ "to_filter":
			return "������������� (" + super.getParameter(name) + ")";
		}
		return super.getParameter(name);
	}
}

/**
 * ��������������� �����-�������� ��� ���������� ������
 * 
 * <p>Servlet API ������������� ����������� ����� HttpServletResponseWrapper
 * ��� ���������� ��������� ���������� �������
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
		// �� ����� ������ ���������� ������ html-�������:
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
	 * ��������������� �����-�������� ��� ������� PrintWriter
	 */
	private class WrappedWriter extends PrintWriter {
		public WrappedWriter ( Writer origin ) {
			super(origin);
		}
		
		@Override
		public void close() {
			// ������������� ����� close() ��������� ������� Writer � ��������� html-�����
			// ����� ���������������� ��������� ������:
			println("<html><body><footer><hr>������� �������� � ���-����������: " + mCurrentRequestCounter + "</footer></body></html>");
			
			super.close();
		}
	}
	
}

