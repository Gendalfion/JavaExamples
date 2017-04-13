package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * �������, ��������������� ������������� ������� HttpSession ��� ���������� ������ ����� ����������������� ��������
 */
@WebServlet("/Session")
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	static final String [] mGoodCategories = new String [] { "�������", "�������", "�����", "����(tm)" };
	
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		doGet( request, response );
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		String dstCharset = "utf-8";
		
        response.setContentType("text/html");
        response.setCharacterEncoding(dstCharset);
        
        try (PrintWriter out = response.getWriter()) {	   
        	// ��� ������ ������ HttpServletRequest.getSession() �� ����� ��������
        	// ������ �� ������ HttpSession. ������ HttpSession ����� ������,
        	// ���� � ������ ������������� �� ���� ����������� ����� �� �����,
        	// � ��������� ������ ����������� ��� ������������ ������ ������.
        	// ������ HttpSession ��������� ��������� ������ ��� ������� ������� �����
        	// ������� ��������� �� ������ �������.
        	// ������ ������ ���������� ���� ������������ ������ ������� � ����� ����
        	// ������� ��� ����������� �������� �� ������� �������, ���� ������ �������.
        	HttpSession session = request.getSession();
        	// HttpSession - ��� ��������� ��� ����(���������)/��������(����� ������),
        	// ��� ������ ������ HttpSession.getAttribute(...) �� �������� �������� ������� �� ������:
	        int [] purchases = (int [])session.getAttribute("purchases");
	        if ( purchases == null ) {
	            purchases = new int [ mGoodCategories.length ];
	            // ��� ������ ������ HttpSession.setAttribute(...) �� ��������� ����� ������ � ������ � ������:
	            session.setAttribute( "purchases", purchases );
	        }
	
	        out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>������� �� ������� HttpSession</title></head><body><p>" );
	
	        // ������������ ��������� ����������� ��� �������:
	        if ( request.getParameter("checkout") != null ) {
	        	// ������������ ����� ������ ������� �������:
	            out.println("<h1>������� �� ������� " + calculateGoodsCount(purchases) + " �������!</h1>");
	            // ������� ������ ����������� �� ������� ��������;
	            // ������ ������ ���������� ��������� ������� �������:
	            out.println("<form method=GET action=" + request.getRequestURI() + ">"
	            		+ "<p><input type=submit name=clear value=\"��������� �� �������\"></form>");
	        } else  {
	            if ( request.getParameter("add") != null ) {
	            	// ������������ ����� ������ ���������� ������� � �������:
	                out.println( "<h1>" + addPurchases(request, purchases) + " ������� ��������� � �������...</h1>" );
	            } else {
	                if ( request.getParameter("clear") != null ) {
	                	// ������������ ����� ������ ������� �������:
	                    for (int i=0; i<purchases.length; i++)
	                         purchases[i] = 0;
	                }
	                out.println("<h1>������� ���������� ������� ��� �������:</h1>");
	            }
	            doForm( out, request.getRequestURI(), purchases );
	        }
	        showPurchases( out, purchases );
	        out.println( "</body></html>" );
        }
    }

	private int addPurchases( HttpServletRequest request, int [] purchases ) {
		// ��������� ������� �� ���������� ������� � ������ purchases:
		int items_added = 0;
		for (int i = 0; i < mGoodCategories.length; i++) {
			int added_count = 0;
        	try { added_count = Integer.parseInt( request.getParameter("item_" + i) ); } catch ( Exception e ) {} 
        	if ( added_count > 0 ) {
	        	purchases[i] += added_count;
	        	items_added += added_count;
        	}
	    }
		return items_added;
	}

	private void doForm( PrintWriter out, String requestURI,  int [] purchases ) {
		// ������� ���������� ����� ��� ���������� ���������:
		
        out.println( "<form method=POST action="+ requestURI +">" );
        out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );

        // ������� ���� ��� ����� ���������� ���������� ������� �� ������ ���������:
        for(int i = 0; i < mGoodCategories.length; i++) {
            out.println( "<tr><td align=\"right\" width=\"200\">������ " + mGoodCategories[i] + ":</td>"
            		+ "<td><input name=item_" + i + " value=0 size=3></td></tr>");
        }
        out.println( "</table>" );
        
        // ������� ������ ��� ���������� ��������� �������:
        out.println(  "<p><input type=submit name=add value=\"�������� � �������\">"
        			+ "<input type=submit name=checkout value=\"���������� ������\"" 
        			+ ((calculateGoodsCount(purchases) > 0) ? ("") : ("disabled")) +">"
        			+ "<input type=submit name=clear value=\"�������� �������\">"
        			+ "</form>" );
    }

	private void showPurchases( PrintWriter out, int [] purchases ) {
		// ������� ���������� �������:
		
    	if ( calculateGoodsCount(purchases) <= 0 ) {
    		// ������� �����:
    		out.println("<hr><h2>���� ������� �����...</h2>");
    		return;
    	}
    	
    	// ������� ������ ������� �������:
        out.println("<hr><h2>���� ������� � ���������:</h2>");
        out.println("<ul>");
        for (int i = 0; i < mGoodCategories.length; i++) {
            if ( purchases[i] != 0 ) {
                out.println( "<li> " + mGoodCategories[i] + ":  " + purchases[i] + "<br>" );
            }
        }
        out.println("</ul>");
	}
	
	private int calculateGoodsCount ( int [] purchases ) {
		// ������� ���������� ������� � ������� �������:
		int goods_count = 0;
    	for ( int item_count : purchases ) { goods_count += item_count; }
    	return goods_count;
	}
}
