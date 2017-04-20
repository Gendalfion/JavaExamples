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
 * Демонстрация сервлета, имеющего ограничения по доступу
 */
@WebServlet( 
		name = "SecretServlet",
		urlPatterns = { "/secure" } )
// Ограничение доступа может быть установлено при помощи аннотации ServletSecurity:
@ServletSecurity ( 
		// Определяем роли пользователей, имеющих доступ к сервлету:
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
		
		// Запрещаем кеширование данной страницы браузером клиента
		// (Это заставляет браузер каждый раз обращаться на сервер при запросе данной страницы,
		//  что исключает введение пользователя в замешательство, когда он
		//  вышел из своего аккаунта, но может просмотреть содержимое защищенных страниц
		//  при помощи кнопки "назад" в браузере):
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); 	// HTTP 1.0.
        response.setDateHeader("Expires", 0); 		// Proxies.
		
        HttpSession current_session = request.getSession(false);
		if ( request.getParameter("logout") != null ) {
			// При помощи метода HttpServletRequest.logout() мы можем завершить аутентификацию клиента: 
			request.logout();
			if ( current_session != null ) {
				current_session.invalidate();
			}
			
			// Перенаправляем пользователя на текущую страницу (что вызовет форму аутентификации):
			response.sendRedirect( request.getRequestURI() );
			return;
		}
		
		try ( PrintWriter out = response.getWriter() ) {
			out.println("<html><head><title>Секретный сервлет</title></head><body>");
			
			out.println("<h2> Здравствуте секретный агент, " + request.getRemoteUser() + " </h2>");
			
			out.println("<hr><h3>Информация о текущем пользователе:</h3>");
			out.println( "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">" );
			
			final String table_row_pattern = "<tr><td align=\"left\" width=\"250\">%s</td>"
											+"<td align=\"left\">%s</td></tr>\n";
			
			// Выводим информацию о текущем пользователе:
			out.printf(table_row_pattern, "request.getRemoteUser()", 		request.getRemoteUser());
			out.printf(table_row_pattern, "request.getAuthType()", 			request.getAuthType());
			if ( current_session != null ) {
				out.printf(table_row_pattern, "request.getSession().getId()",	request.getSession().getId());
			}
			
			// Объект Principal содержит информацию о текущем аутентифицированном пользователе;
			// Если в текущей сессии нет аутентифицированного пользователя, то метод HttpServletRequest. getUserPrincipal() вернет null:
			Principal user_principal = request.getUserPrincipal();
			if ( user_principal != null ) {
				out.printf(table_row_pattern, "request.getUserPrincipal()",	user_principal.toString());
			}
			out.printf(table_row_pattern, "request.isSecure()",	request.isSecure());
			
			out.println( "</table>" );
			
			out.println( "<p><a href= " + request.getRequestURI() + "?logout>выход из аккаунта...</a>" );
			
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
