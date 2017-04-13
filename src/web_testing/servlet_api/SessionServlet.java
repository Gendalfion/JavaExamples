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
 * Сервлет, демонстрирующий использование объекта HttpSession для сохранения данных между пользовательскими сессиями
 */
@WebServlet("/Session")
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	static final String [] mGoodCategories = new String [] { "Шоколад", "Варенье", "Масло", "Мясо(tm)" };
	
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		doGet( request, response );
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		String dstCharset = "utf-8";
		
        response.setContentType("text/html");
        response.setCharacterEncoding(dstCharset);
        
        try (PrintWriter out = response.getWriter()) {	   
        	// При помощи метода HttpServletRequest.getSession() мы можем получить
        	// ссылку на объект HttpSession. Объект HttpSession будет создан,
        	// если с данным пользователем не было установлено связи до этого,
        	// в противном случае возвратится уже существующий объект сессии.
        	// Объект HttpSession позволяет сохранять данные для каждого клиента между
        	// разными запросами от одного клиента.
        	// Однако сессия существует лишь определенный момент времени и может быть
        	// закрыта при перезапуске браузера со стороны клиента, либо самого сервера.
        	HttpSession session = request.getSession();
        	// HttpSession - это множество пар ключ(строковый)/значение(любой объект),
        	// При помощи метода HttpSession.getAttribute(...) мы получаем значение объекта из сессии:
	        int [] purchases = (int [])session.getAttribute("purchases");
	        if ( purchases == null ) {
	            purchases = new int [ mGoodCategories.length ];
	            // При помощи метода HttpSession.setAttribute(...) мы связываем новый объект с ключем в сессии:
	            session.setAttribute( "purchases", purchases );
	        }
	
	        out.println( "<html><head><meta charset=\"" + dstCharset + "\" /><title>Магазин на объекте HttpSession</title></head><body><p>" );
	
	        // Обрабатываем параметры присланного нам запроса:
	        if ( request.getParameter("checkout") != null ) {
	        	// Пользователь нажал кнопку покупки товаров:
	            out.println("<h1>Спасибо за покупку " + calculateGoodsCount(purchases) + " товаров!</h1>");
	            // Создаем кнопку возвращения на главную страницу;
	            // Данная кнопка фактически выполняет очистку корзины:
	            out.println("<form method=GET action=" + request.getRequestURI() + ">"
	            		+ "<p><input type=submit name=clear value=\"Вернуться на главную\"></form>");
	        } else  {
	            if ( request.getParameter("add") != null ) {
	            	// Пользователь нажал кнопку добавления товаров в корзину:
	                out.println( "<h1>" + addPurchases(request, purchases) + " товаров добавлено в корзину...</h1>" );
	            } else {
	                if ( request.getParameter("clear") != null ) {
	                	// Пользователь нажал кнопку очистки корзины:
	                    for (int i=0; i<purchases.length; i++)
	                         purchases[i] = 0;
	                }
	                out.println("<h1>Укажите количество товаров для покупки:</h1>");
	            }
	            doForm( out, request.getRequestURI(), purchases );
	        }
	        showPurchases( out, purchases );
	        out.println( "</body></html>" );
        }
    }

	private int addPurchases( HttpServletRequest request, int [] purchases ) {
		// Добавляем покупки из параметров запроса в массив purchases:
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
		// Выводим клиентскую форму для управления покупками:
		
        out.println( "<form method=POST action="+ requestURI +">" );
        out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );

        // Выводим поля для ввода количества покупаемых товаров по каждой категории:
        for(int i = 0; i < mGoodCategories.length; i++) {
            out.println( "<tr><td align=\"right\" width=\"200\">Купить " + mGoodCategories[i] + ":</td>"
            		+ "<td><input name=item_" + i + " value=0 size=3></td></tr>");
        }
        out.println( "</table>" );
        
        // Выводим кнопки для управления процессом покупок:
        out.println(  "<p><input type=submit name=add value=\"Добавить в корзину\">"
        			+ "<input type=submit name=checkout value=\"Приобрести товары\"" 
        			+ ((calculateGoodsCount(purchases) > 0) ? ("") : ("disabled")) +">"
        			+ "<input type=submit name=clear value=\"Очистить корзину\">"
        			+ "</form>" );
    }

	private void showPurchases( PrintWriter out, int [] purchases ) {
		// Выводим содержимое таблицы:
		
    	if ( calculateGoodsCount(purchases) <= 0 ) {
    		// Корзина пуста:
    		out.println("<hr><h2>Ваша корзина пуста...</h2>");
    		return;
    	}
    	
    	// Выводим список покупок клиента:
        out.println("<hr><h2>Ваша корзина с покупками:</h2>");
        out.println("<ul>");
        for (int i = 0; i < mGoodCategories.length; i++) {
            if ( purchases[i] != 0 ) {
                out.println( "<li> " + mGoodCategories[i] + ":  " + purchases[i] + "<br>" );
            }
        }
        out.println("</ul>");
	}
	
	private int calculateGoodsCount ( int [] purchases ) {
		// Подсчет количества покупок в корзине клиента:
		int goods_count = 0;
    	for ( int item_count : purchases ) { goods_count += item_count; }
    	return goods_count;
	}
}
