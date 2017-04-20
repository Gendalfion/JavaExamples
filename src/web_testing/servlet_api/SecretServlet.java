package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ������������ ��������, �������� ����������� �� �������
 */
@WebServlet( 
		name = "SecretServlet",
		urlPatterns = { "/secure" } )
// ����������� ������� ����� ���� ����������� ��� ������ ��������� ServletSecurity:
@ServletSecurity ( 
		// ���������� ���� �������������, ������� ������ � ��������:
		@HttpConstraint(rolesAllowed = "secretagent") 
		)
public class SecretServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		// ��������� ����������� ������ �������� ��������� �������
		// (��� ���������� ������� ������ ��� ���������� �� ������ ��� ������� ������ ��������,
		//  ��� ��������� �������� ������������ � ��������������, ����� ��
		//  ����� �� ������ ��������, �� ����� ����������� ���������� ���������� �������
		//  ��� ������ ������ "�����" � ��������):
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); 	// HTTP 1.0.
        response.setDateHeader("Expires", 0); 		// Proxies.
		
        HttpSession current_session = request.getSession(false);
		if ( request.getParameter("logout") != null ) {
			// ��� ������ ������ HttpServletRequest.logout() �� ����� ��������� �������������� �������: 
			request.logout();
			if ( current_session != null ) {
				current_session.invalidate();
			}
			
			// �������������� ������������ �� ������� �������� (��� ������� ����� ��������������):
			response.sendRedirect( request.getRequestURI() );
			return;
		}
		
		try ( PrintWriter out = response.getWriter() ) {
			out.println("<html><head><title>��������� �������</title></head><body>");
			
			out.println("<h2> ����������� ��������� �����, " + request.getRemoteUser() + " </h2>");
			
			out.println("<hr><h3>���������� � ������� ������������:</h3>");
			out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
			
			final String table_row_pattern = "<tr><td align=\"left\" width=\"250\">%s</td>"
											+"<td align=\"left\">%s</td></tr>\n";
			
			// ������� ���������� � ������� ������������:
			out.printf(table_row_pattern, "request.getRemoteUser()", 		request.getRemoteUser());
			out.printf(table_row_pattern, "request.getAuthType()", 			request.getAuthType());
			if ( current_session != null ) {
				out.printf(table_row_pattern, "request.getSession().getId()",	request.getSession().getId());
			}
			
			// ������ Principal �������� ���������� � ������� ������������������� ������������;
			// ���� � ������� ������ ��� �������������������� ������������, �� ����� HttpServletRequest. getUserPrincipal() ������ null:
			Principal user_principal = request.getUserPrincipal();
			if ( user_principal != null ) {
				out.printf(table_row_pattern, "request.getUserPrincipal()",	user_principal.toString());
			}
			out.printf(table_row_pattern, "request.isSecure()",	request.isSecure());
			
			out.println( "</table>" );
			
			out.println( "<p><a href= " + request.getRequestURI() + "?logout>����� �� ��������...</a>" );
			
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
