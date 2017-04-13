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
 * �������, ��������������� ��������� ����������, ���������� ������ � HTTP-��������� GET � POST
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
		// ���������� ��������� ���� ������� (��� ��� ����������� � ���������� ��� ������������� ������):
		String srcCharset = request.getCharacterEncoding();
		if ( srcCharset == null ) { srcCharset = "ISO-8859-1"; }
		
		// ��� ����������� ������ ���������� ��������� UTF-8 (�������� ������� ��������� �� ���� ������������):
		String dstCharset = "utf-8";
		
		// ����������� ���������� ���������� ��� ������, ����� ������ ��� ��������� ���������� ��� �����:
		response.setContentType("text/html");
		// ������������� ��������� ��������� ���������:
		response.setCharacterEncoding(dstCharset);
		
		// ��� �������� ������ � �������� ����� ���������� ������ PrintWriter;
		// ����� ��������� ������ ������ ������������� ��������� close() ��� ��������� ������:
		try ( PrintWriter out = response.getWriter() ) {
			// ��������� ����� ������ � ������� HTML-���������,
			// ��� ���� ������������� ���� ��������� ��������� ��������� � meta-����:
			out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>������ ����������</title></head>" +
					"<body><h1>��������� (������: " + request_type + ")</h1>" );
			
			// �������� ���������, ���������� ������ � HTTP-��������:
			boolean decodeParams = request_type.equalsIgnoreCase("POST");
			for ( Map.Entry<String, String[]> param : request.getParameterMap().entrySet() ) {
				out.print("<li>" + param.getKey() + " = ");
				
				// ������ �������� ������� ����� ����� ��������� ��������, ��� ����
				// ���������, �������� ����� GET-������, ������������ �������� �������������, �
				// ���������, �������� ����� POST-������, ���������� ������������ �������:
				String param_vals = Arrays.asList(param.getValue()).toString();
				out.print ( decodeParams ? new String(param_vals.getBytes(srcCharset), dstCharset) : param_vals );
				
				out.println("</li>");
			}
			
			// ������� 2 ����� � 2-�� ������ � ������� � ������:
			
			out.println( "<h3>POST-�����:</h3>" );
			// ��� �������� ������ 1-�� ����� ����� ������������ ������ POST:
			out.println("<p><form method=\"POST\" action=\"" + request.getRequestURI() + "\">"
					+ "���� 1 <input name=\"PostField 1\" size=20><br>"
					+ "���� 2 <input name=\"PostField 2\" size=20><br>"
					+ "<br><input type=\"submit\" value=\"��������� ������ POST\"></form>"
					);
			
			out.println( "<h3>GET-�����:</h3>" );
			// ��� �������� ������ 2-�� ����� ����� ������������ ������ GET:
			out.println("<p><form method=\"GET\" action=\"" + request.getRequestURI() + "\">"
					+ "���� 1 <input name=\"GetField 1\" size=20><br>"
					+ "���� 2 <input name=\"GetField 2\" size=20><br>"
					+ "<br><input type=\"submit\" value=\"��������� ������ GET\"></form>"
					);
			
			out.println("</body></html>");
		}
	}

}
