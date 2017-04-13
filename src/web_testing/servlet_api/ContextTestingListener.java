package web_testing.servlet_api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Класс с аннотацией WebListener используется для отслеживания событий
 * на уровне веб-приложения. Конкретный список отслеживаемых событий регулируется
 * при помощи списка реализуемых интерфейсов-слушателей (см. описание аннотации WebListener)
 * 
 * <p>Данный класс используется в паре классом: {@link ContextTestingAsyncServlet}
 */
@WebListener
public class ContextTestingListener
	// Интерфейс ServletContextListener используется для отслеживания события
	// создания/разрушения контекста для текущего веб-приложения:
	implements ServletContextListener {
	ScheduledExecutorService mExecutorService = null;
	
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  {
    	System.out.println(this.getClass().getName() + " contextInitialized...");
    	
        if ( mExecutorService == null ) {
        	// При инициализации контекста создаем пул потоков, который будет использован
        	// в сервлетах нашего веб-приложения:
	        mExecutorService = Executors.newScheduledThreadPool(1);
	        sce.getServletContext().setAttribute("web_testing.servlet_api.ScheduledExecutor", mExecutorService);
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    	System.out.println(this.getClass().getName() + " contextDestroyed...");
    	if ( mExecutorService != null ) {
    		// Завершаем работу пула потоков при разрушении контекста веб-приложения:
    		mExecutorService.shutdown();
    	}
    }	
}
