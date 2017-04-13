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
 * �������, ��������������� ������������� ������������ ��������� ���-����������,
 * � ����� ������������� ������������ ��������� ��� ��������� �������� ��������
 * 
 * <p>������ ����� ������������ � ���� �������: {@link ContextTestingListener}
 */
@WebServlet(
		urlPatterns={"/ContextTesting"},
		// ��� ��������� ��������� ������ �������� � ����������� ������
		// ������������ �������� asyncSupported � ��������� WebServlet:
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
        	// ������������ ������� ������ "��������� ������...":
        	
        	// ���������� ������������� ������������� ����� �������� �������� ������ �� ������:
        	long delayTime = getIntParameter(request, "ResponseDelayMs", 2500);
        	
        	// ����� HttpServletRequest.startAsync() ��������� ����������� ���������
        	// �������� �������. �� ���������� ������ AsyncContext ��� ������
        	// �������� �� ����� ���������� �������� ������ � ������ ������� � ������ �� ������ ������:
        	final AsyncContext asyncContext = request.startAsync();
        	final long requestTimeMillis = System.currentTimeMillis();
        	final String requestURI = request.getRequestURI();
        	
        	// ����� HttpServlet.getServletContext() ��������� �������� ������ � ��������� ����������.
        	// ��� ������ ������� ��������� �������� ����� ��������� ����� ������ �� ������ 
        	// ���-����������, � ����� �������� ������ �� ������� �������� ��:
        	((ScheduledExecutorService)getServletContext().getAttribute("web_testing.servlet_api.ScheduledExecutor"))
        	// �� �������� ������ �� ������ ScheduledExecutorService, ������� �������������� ���
        	// ������������� ��������� ���-���������� � ������� ContextTestingListener.
        	// ��������� ���������� ������ � ������������ ScheduledExecutor:
        		.schedule( new Runnable() {
					@Override public void run() {
						// ����������� ����� ���������� ���������� ������:
						
						// ��� ������ ������������ ��������� asyncContext �������� ������ � ��������� ������ ��������� ���������:
						try (PrintWriter out = asyncContext.getResponse().getWriter()) {
							// ������� �������� ��������� � �������� �����:
							out.println( "<html><head><meta charset=\"" + dstCharset 
									+ "\" /><title>����������� �����</title></head><body><p>" );
							
							out.println("<h2>����� ����������� �� " + (System.currentTimeMillis() - requestTimeMillis) + " ��</h2>");
							
							out.println("<hr><p><a href=\"" + requestURI + "\">��������� �� �������</a>");
							out.println( "</body></html>" );
						} catch ( IOException ioe ) {
							throw new RuntimeException(ioe);
						} finally {
							// ��� ��������� ��������� ������� �� ������ ����������� �������
							// ����� complete() ��� ������������ ���������;
							// ��� ���� ����� �������, ��� ��������� ������� ���������:
							asyncContext.complete();
						}
					}
				}, delayTime, TimeUnit.MILLISECONDS);
        } else {
	        try (PrintWriter out = response.getWriter()) {
	        	// ������� ����� � ������� � ����� ��� ����� �������� ��������� ������:
	        	out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>����������� �������</title></head><body><p>" );
	        	out.println("<h2>������ � ������������ ��������:</h2>");
	        	out.println( "<form method=POST action=" + request.getRequestURI() + "?delayedRequest=>" );
	        	out.println( "<p>����� �������� ������ (��):<input name=ResponseDelayMs value=\"2500\" size=10>");
	        	out.println(  "<p><input type=submit value=\"��������� ������...\">" );
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
