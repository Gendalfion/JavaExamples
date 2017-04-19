package web_testing.servlet_api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет, используемый в качестве аутентификационной формы в веб-приложении
 * 
 * <p>Настройка данного сервлета в качестве аутентификационного производится 
 * в дескрипторе приложения web.xml (секция login-config)
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
			out.println("<html><head><title>Доступ ограничен</title>");
			
			out.println("<h2>Введите имя/пароль для доступа к секретным данным:</h2>");
			
			// Выводим форму для аутентификации пользователя в веб-приложении:
			out.println("<body bgcolor=\"white\">");
			// По спецификации, форма должна отвечать следующим требованиям:
			// 	*	form action должен быть равен "j_security_check"
			// 	*	поле для ввода логина должно иметь название "j_username"
			// 	*	поле для ввода пароля должно иметь название "j_password"
			out.println("<form method=\"POST\" action=\"j_security_check\")>");
				out.println("<table border=\"0\" cellspacing=\"5\">");
					out.println("<tr><th align=\"right\">Имя агента:</th>");
			      	out.println("<td align=\"left\"><input type=\"text\" name=\"j_username\"></td></tr>");
			      	
			      	out.println("<tr><th align=\"right\">Пароль:</th>");
			      	out.println("<td align=\"left\"><input type=\"password\" name=\"j_password\"></td></tr>");
			    
			      	out.println("<tr><td align=\"right\"><input type=\"submit\" value=\"Войти в систему...\"></td>");
			      	out.println("<td align=\"left\"><input type=\"reset\"></td></tr>");
			    out.println("</table>");
			out.println("</form>");
			
		    if ( request.getParameter("login_failed") != null ) {
				out.println("<hr><p><font color=\"red\">Имя агента/пароль заданы неверно, попробуйте еще раз...</font></p>");
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
