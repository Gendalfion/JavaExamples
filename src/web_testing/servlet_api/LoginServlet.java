package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �������, ������������ � �������� ������������������ ����� � ���-����������
 * 
 * <p>��������� ������� �������� � �������� ������������������� ������������ 
 * � ����������� ���������� web.xml (������ login-config)
 */
@WebServlet("/secret/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		try ( PrintWriter out = response.getWriter() ) {
			out.println("<html><head><title>������ ���������</title>");
			
			out.println("<h2>������� ���/������ ��� ������� � ��������� ������:</h2>");
			
			// ������� ����� ��� �������������� ������������ � ���-����������:
			out.println("<body bgcolor=\"white\">");
			// �� ������������, ����� ������ �������� ��������� �����������:
			// 	*	form action ������ ���� ����� "j_security_check"
			// 	*	���� ��� ����� ������ ������ ����� �������� "j_username"
			// 	*	���� ��� ����� ������ ������ ����� �������� "j_password"
			out.println("<form method=\"POST\" action=\"j_security_check\")>");
				out.println("<table border=\"0\" cellspacing=\"5\">");
					out.println("<tr><th align=\"right\">��� ������:</th>");
			      	out.println("<td align=\"left\"><input type=\"text\" name=\"j_username\"></td></tr>");
			      	
			      	out.println("<tr><th align=\"right\">������:</th>");
			      	out.println("<td align=\"left\"><input type=\"password\" name=\"j_password\"></td></tr>");
			    
			      	out.println("<tr><td align=\"right\"><input type=\"submit\" value=\"����� � �������...\"></td>");
			      	out.println("<td align=\"left\"><input type=\"reset\"></td></tr>");
			    out.println("</table>");
			out.println("</form>");
			
		    if ( request.getParameter("login_failed") != null ) {
				out.println("<hr><p><font color=\"red\">��� ������/������ ������ �������, ���������� ��� ���...</font></p>");
			}
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
