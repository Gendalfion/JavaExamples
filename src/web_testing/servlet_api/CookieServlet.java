package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �������, ��������������� ������������� ������� Cookie ��� ����������
 * ���������� � ���������� ��������� ��� ������ ���������� ����
 */
@WebServlet("/Cookie/*" /* ��������� ������ ��� URL-�������, ����������� ������ ��������� */)
public class CookieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dstCharset = "utf-8";
		String srcCharset = request.getCharacterEncoding();
		if ( srcCharset == null ) { srcCharset = "ISO-8859-1"; }
		
        response.setContentType("text/html");
        response.setCharacterEncoding(dstCharset);
        
        try (PrintWriter out = response.getWriter()) {
        	out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>������������ Cookie</title></head><body><p>" );
        	
        	if ( "true".equalsIgnoreCase( request.getParameter("setcookie") ) ) {
        		// ������������ ������� ������������� ������ �� �����:
        		
        		// ���������� ���������, ���������� ������:
        		int agedCookieTime = getIntParameter (request, "AgedCookieTime", 60);
        		// ���������� ������ HttpSession ��� ���������� ������, ��������� � ���� �����,
        		// ����� ����������� ���������:
        		request.getSession().setAttribute("AgedCookieTime", String.valueOf(agedCookieTime));
        		String pathCookieURL = request.getParameter("PathCookieURL");
        		request.getSession().setAttribute("PathCookieURL", pathCookieURL);
        		
        		// ������� ������� Cookie
        		// ���� - ��� ���� ���(���������)/��������(���������, ������ �������� ASCII �������, � ���������� ������������)
        		// ���� ����������� �� ������� � ������������ ������� � �������� HTTP-������.
        		// ����� ����� � �������� ������ ���������� ��������� ��� ������� ������� ����,
        		// ����� ��� ����� ����� � ������� ��������.
        		// ��� ������ ��������� ������� ������ ���������� ���� ������� �� ������,
        		// �������������� ��������� ������������� �������� �� ���������� ������ � ���� ���������� 
        		// (���� ��������� �� ������ ������� �� ������������).
        		Cookie sessionCookie = new Cookie("SessionCookie",
        				// ������������ ���������, ���������� ����� POST-�����
        				// �� ��������� ������� srcCharset � ��������� ������ dstCharset (��� ���������� ��������� ������� ����):
        				decodeParameter(request.getParameter("SessionCookie"), srcCharset, dstCharset) );
        		
        		Cookie agedCookie = new Cookie("AgedCookie", 
        				decodeParameter(request.getParameter("AgedCookie"), srcCharset, dstCharset) );
        		
        		Cookie pathCookie = new Cookie("PathCookie", 
        				decodeParameter(request.getParameter("PathCookie"), srcCharset, dstCharset) );
        		
        		String cookieOperation;
        		if ( request.getParameter("ClearCookie") != null ) {
        			// ������������ ����� ������ ������� ���� ����:
        			
        			// ������������� ����� ����� ���� � 0, ��� ����������� ������� ������� � ���,
        			// ��� ���� ���������� �������:
        			sessionCookie.setMaxAge(0);
        			agedCookie.setMaxAge(0);
        			pathCookie.setMaxAge(0);
        			cookieOperation = "�������";
        		} else {
        			// ������������� ����� ����� ����, �������� �������������:
        			agedCookie.setMaxAge(agedCookieTime);
        			
        			// ������������� ���� ��� ����, �������� �������������.
        			// ������� ����� ���������� ������ ���� �� ������, ������ ����
        			// ������� ����������� ������������� ���� ����� ��������� � ����� ����
        			// (��� ����������� ���� ����� �������� �������� ��� ���� ����):
        			pathCookie.setPath( pathCookieURL);
        			
        			cookieOperation = "�����������";
        		}
        		
        		try {
        			// ��������� ���� � �������� HTTP-�����:
        			response.addCookie(sessionCookie);
        			response.addCookie(agedCookie);
        			response.addCookie(pathCookie);
        			out.println("<h1>����� ���� " + cookieOperation + "...</h1>");
        		} catch ( Exception exception ) { 
        			out.println("<h2>������ ��� ��������� ����: " + exception + "</h2>");
        		}
        		out.println("<hr><p><a href=\"" + request.getRequestURI() + "\">��������� �� �������</a>");
        	} else {
        		out.println("<h2>���� ����:</h2>");
        		// ������� ����� ��� ���������� ���� �������:
        		out.println( "<form method=POST action=" + request.getRequestURI() + "?setcookie=true>" );
                
                String sessionCookieValue = "";
                String agedCookieValue = "";
                String pathCookieValue = "";
                
                // �������� ������ ����, ����������� ��������:
                String currentFullPath = request.getContextPath() + request.getServletPath();
                if ( request.getPathInfo() != null ) {
                	currentFullPath += request.getPathInfo();
                }
                
                String deleteCookiesBtnEnabled = "disabled";
                
                // ����� HttpServletRequest.getCookies() ���������� ��� ����, ������������ ��������:
                Cookie [] cookies = request.getCookies();
                if ( cookies != null ) {
                	// �������� �������� ������������ ��� ���� �� ����������� �������:
                	for ( Cookie cookiesItem : cookies ) {
                		if ( cookiesItem.getName().equalsIgnoreCase("SessionCookie") ) {
                			sessionCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		} else 
                		if ( cookiesItem.getName().equalsIgnoreCase("AgedCookie") ) {
                			agedCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		} else 
            			if ( cookiesItem.getName().equalsIgnoreCase("PathCookie") ) {
            				pathCookieValue = cookiesItem.getValue(); deleteCookiesBtnEnabled = "";
                		}
                	}
                }
                
                // ������ ���� ��� �������������� �������� � �������� ��� ����:
                out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
                
                out.println( "<tr><td align=\"right\" width=\"200\">���������� ����:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=SessionCookie value=\"" 
                		+ sessionCookieValue + "\" size=20></td></tr>");
                
                out.println( "<tr><td align=\"right\" width=\"200\">��������� ����:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=AgedCookie value=\"" + agedCookieValue + "\" size=20></td>"
                		+ "<td>����� ����� ���� (�):<input name=AgedCookieTime value=\"" 
                		+ getIntSessionParameter(request, "AgedCookieTime", 60 ) + "\" size=20></td></tr>");
                
                out.println( "<tr><td align=\"right\" width=\"200\">���� � �������� ��������:</td>"
                		+ "<td align=\"left\" width=\"200\"><input name=PathCookie value=\"" + pathCookieValue + "\" size=20></td>"
                		+ "<td>������� �������� ���� (path):<input name=PathCookieURL value=\"" 
                		+ getStringSessionParameter(request, "PathCookieURL", currentFullPath + "/some_path" ) 
                		+ "\" size=35></td></tr>");
                
                out.println( "</table>" );
                
                // ������ ������ ����������/������� ���������������� ����:
                out.println(  "<p> ������� ���� (path): " + currentFullPath );
                out.println(  "<p><input type=submit value=\"���������� ����...\">" );
                out.println(  "<p><input type=submit name=ClearCookie value=\"�������� ����...\" " + deleteCookiesBtnEnabled + "></form>" );
        	}
        	
        	out.println( "</body></html>" );
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String decodeParameter ( String param, String srcCharset, String dstCharset ) {
		try {
			return new String(param.getBytes(srcCharset), dstCharset);
		} catch ( UnsupportedEncodingException uee ) {
		}
		return param;
	}
	
	private int getIntParameter ( HttpServletRequest request, String param_name, int default_val ) {
		try { return Integer.parseInt( request.getParameter(param_name) ); } catch ( Exception e ) {}
		return default_val;
	}
	
	private int getIntSessionParameter ( HttpServletRequest request, String param_name, int default_val ) {
		try { return Integer.parseInt( (String)request.getSession().getAttribute(param_name) ); } catch ( Exception e ) {}
		return default_val;
	}
	
	private String getStringSessionParameter ( HttpServletRequest request, String param_name, String default_val ) {
		Object param_val = request.getSession().getAttribute(param_name);
		if ( param_val instanceof String ) { 
			return (String)param_val; 
		}
		return default_val;
	}
}
