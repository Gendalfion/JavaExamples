package web_testing.servlet_api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ������������ ����������� HTTP-�������� "Hello, World!"
 *
 * ��� ������� ������� �����������:
 * <p>1. ���������� Eclipse ��� Java EE (Enterprise Edition)<br>
 * <p>2. ������� ������ ��� ������� ���-����������.<br>
 * 	� ����� ������ ������ ������ Apache Tomcat v9.0 (����� ���������� � ����� ������� MyStudy)<br>
 * <p>3. ���������� ������ � ����� Eclipse:<br>
 * 	Window->Show view->Servers->[���]->New->Server->"������� ���� ������"->"���������� ������ ��� ������ �������"<br>
 * <p>4. ������� ������ "Dynamic Web Project", ��� �������� ������������, ��� �����:<br>
 * 	Project->Properties->Project Facets->������� ����� Dynamic Web Module.<br>
 *  ������, �� ������� Runtimes �������� ���� ������<br>
 * <p>5. ��� ���������� ������� ����� ����������, ����������� ������������ javax.servlet.*.<br>
 * 	��� ������ ������, ���������� servlet-api.jar ������ ����������� � ������<br>
 *  ��������� ������� (� �����: WebContent/WEB-INF/lib/, "����������" ������� ������-�� �� ��������)<br>
 * <p>6. �������� ������� HelloWorldServlet � ��������� �������������� �������:<br>
 * 	Window->Show view->Servers->[��� �� �������]->Add and Remove...<br>
 * <p>7. ��������� ������:<br>
 * 	Window->Show view->Servers->[��� �� �������]->Start<br>
 * <p>8. ��������� ������ ����� ������ � ������ �������� URL:<br>
 * 	http://localhost:8080/MyStudy/hello<br>
 * 
 * <p>��� �������� ������������� ������ javax.servlet.* ����� ������������<br>
 * 	� ������� �������� ���� � �������������, ��� �����:<br>
 * 	�������� � ������� Ctrl �� ������� {@link javax.servlet.http.HttpServlet}<br>
 * 	������ ������ Attach Source... � ������� ���� � ������ apache-tomcat-9.0.0.M19-src.zip<br>
 * 	(���������� � �������� ����� ������� MyStudy)
 */
@WebServlet("/hello") // ��������� ������� ��� ������ ��������� @WebServlet � ��������� ������� URL ��� ������� � ��������
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// �������������� ����� ������ HttpServlet, ������� ������������ HTTP GET-������:
		
		// ������������� MIME-��� ������������ ������:
		response.setContentType("text/html");
		
		// ������� ������ � �������� ����� response: 
		response.getWriter().println(
				"<html><head><title>Hello, client!</title></head>" +
				"<body><h1>I am Hello World Servlet!</h1></body></html>");
	}
}
