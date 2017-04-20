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
 * ������������ ����������� HTTP-�������� "Hello, World!"<br>
 *
 * <p><b>��� ������� ������� �����������:</b>
 * <p>1. ���������� Eclipse ��� <a href="http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon3">
 * 	Java EE (Enterprise Edition)</a><br>
 * 
 * <p>2. ������� ������ ��� ������� ���-����������.<br>
 * 	� ����� ������ ������ ������ <a href="http://tomcat.apache.org/download-90.cgi">
 * 	Apache Tomcat v9.0</a> (����� ���������� � ����� ������� MyStudy)<br>
 * 
 * <p>3. ���������� ������ � ����� Eclipse:<br>
 * 	Window->Show view->Servers->[���]->New->Server->"������� ���� ������"->"���������� ������ ��� ������ �������"<br>
 * 
 * <p>4. ������� ������ "Dynamic Web Project", ��� �������� ������������, ��� �����:<br>
 * 	Project->Properties->Project Facets->������� ����� Dynamic Web Module.<br>
 *  ������, �� ������� Runtimes �������� ���� ������<br>
 *  
 * <p>5. ��� ���������� ������� ����� ����������, ����������� ������������ javax.servlet.*.<br>
 * 	��� ������ ������, ���������� servlet-api.jar ������ ����������� � ������<br>
 *  ��������� ������� �� ��������� ������� ���-���������� (� �����: WebContent/WEB-INF/lib/,<br> 
 *  "����������" ������� ������-�� �� ��������)<br>
 *  
 * <p>6. �������� ������� HelloWorldServlet � ��������� �������������� �������:<br>
 * 	Window->Show view->Servers->[��� �� �������]->Add and Remove...<br>
 * 
 * <p>7. ��������� ������:<br>
 * 	Window->Show view->Servers->[��� �� �������]->Start<br>
 * 
 * <p>8. ��������� ������ ����� ������ � ������ �������� URL:<br>
 * 	http://localhost[:server_port(if different from port 80)]/MyStudy/hello<br>
 * 
 * <p>��� �������� ������������� ������ javax.servlet.* ����� ������������<br>
 * 	� ������� �������� ���� � �������������, ��� �����:<br>
 * 	�������� � ������� Ctrl �� ������� {@link javax.servlet.http.HttpServlet}<br>
 * 	������ ������ Attach Source... � ������� ���� � ������ apache-tomcat-9.0.0.M19-src.zip<br>
 * 	(���������� � �������� ����� ������� MyStudy)
 */
@WebServlet(urlPatterns = {"/hello"}, 	// ��������� ������� ��� ������ ��������� @WebServlet � ��������� �������(��) URL ��� ������� � ��������
			name = "HelloWorldServlet" )// ��������� ��������-��������� �������� ��� ����, ����� �� ���� ����� ���� ��������� � ������ 
										// ���������� ��� ����������� ���-���������� 
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// �������������� ����� ������ HttpServlet, ������� ������������ HTTP GET-������:
		
		// ������������� MIME-��� ������������ ������� ������:
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		// ������� ������ � �������� ����� response: 
		try ( PrintWriter out = response.getWriter() ) {
			out.println(
				"<html><head><title>������, ������!</title></head>" +
				"<body><h1>���������� �� �������� HelloWorldServlet!</h1>");
			
			// ����� HttpServlet.getInitParameterNames() ������������ ��� ������� � ���������� �������������, ���������� ������ ����� ��������
			// ��������� ��������������� � ���������������� ������ ���-���������� (��������, web.xml)
			if ( getInitParameterNames().hasMoreElements() ) {
				out.println("<hr><h3>����������������� ���������, ���������� ��������:</h3>");
				out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
				
				// ������� ��� ���������, ���������� ��������, � ���� �������:
				Enumeration<String> param_names = getInitParameterNames();
				while (param_names.hasMoreElements()) {
					String name = (String) param_names.nextElement();
					
					out.println( "<tr><td align=\"left\" width=\"200\">" + name + "</td>"
	                		+ "<td align=\"left\">" + getInitParameter(name) + "</td></tr>");
				}
				out.println( "</table>" );
			}
			
			// ��������� �������, ���������� � GET/POST ��������, 
			// �������� � ���� ����������� HttpServletRequest.getParameterMap():
			Map<String, String[]> requestParamMap = request.getParameterMap();
			if ( !requestParamMap.isEmpty() ) {
				out.println("<hr><h3>��������� ������� � ��������:</h3>");
				out.println( "<table border=\"0\" cellspacing=\"5\">" );
				
				// ������� ��� ��������� ������� � ���� �������:
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
