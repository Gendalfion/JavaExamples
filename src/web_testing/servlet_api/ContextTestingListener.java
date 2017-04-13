package web_testing.servlet_api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ����� � ���������� WebListener ������������ ��� ������������ �������
 * �� ������ ���-����������. ���������� ������ ������������� ������� ������������
 * ��� ������ ������ ����������� �����������-���������� (��. �������� ��������� WebListener)
 * 
 * <p>������ ����� ������������ � ���� �������: {@link ContextTestingAsyncServlet}
 */
@WebListener
public class ContextTestingListener
	// ��������� ServletContextListener ������������ ��� ������������ �������
	// ��������/���������� ��������� ��� �������� ���-����������:
	implements ServletContextListener {
	ScheduledExecutorService mExecutorService = null;
	
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  {
    	System.out.println(this.getClass().getName() + " contextInitialized...");
    	
        if ( mExecutorService == null ) {
        	// ��� ������������� ��������� ������� ��� �������, ������� ����� �����������
        	// � ��������� ������ ���-����������:
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
    		// ��������� ������ ���� ������� ��� ���������� ��������� ���-����������:
    		mExecutorService.shutdown();
    	}
    }	
}
