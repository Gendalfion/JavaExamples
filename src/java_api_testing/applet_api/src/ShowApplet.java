package java_api_testing.applet_api.src;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;

/**
 * Демонстрация простого Java-апплета
 * 
 * <p> Для запуска апплета понадобится:
 * <li> Браузер с поддержкой Java (Chrome Java не поддерживает принципиально...)
 * <li> Запустить HTTP-сервер (например, \executables\windows\Run_012_LargerHttpServer.cmd )
 * <li> В строке браузера набрать URL: http://localhost/java_api_testing/applet_api/showapplet.html 
 * 
 */
public class ShowApplet extends JApplet {
	private static final long serialVersionUID = 1L;
	
	JTextArea text = new JTextArea();
	int startCount;

	// Процедура init() вызывается единожды после загрузки апплета и перед его запуском в браузере
	@Override public void init() 
	{
		// Апплет - это обычный GUI контейнер, который встраивается в HTML-документ:
		
		JButton button = new JButton("Press Me");
		button.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				text.append("Button Pressed!\n"); 
			}
		} );
		getContentPane().add( new JScrollPane( text ), BorderLayout.CENTER );
		JPanel panel = new JPanel();
		panel.add( button );
		getContentPane().add( panel, BorderLayout.SOUTH );
		text.append( "Версия Java: " + System.getProperty("java.version") + "\n" );
		text.append( "JApplet.init()\n" );
	}
	
	// Процедура start() вызывается каждый раз перед возобновлением работы апплета
	@Override public void start() {
		text.append( "JApplet.start(): "+ (startCount++) +"\n" );
	}
	
	// Процедура stop() вызывается каждый раз перед приостановкой работы апплета
	@Override public void stop() {
		text.append( "JApplet.stop().\n" );
	}
	
	// Процедура destroy() вызывается единожды перед выгрузкой апплета из среды выполнения браузера
	@Override public void destroy() {
		super.destroy();
	}
}

