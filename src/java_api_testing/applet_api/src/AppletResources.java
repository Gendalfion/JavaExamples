package java_api_testing.applet_api.src;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Демонстрация доступности внешних ресурсов и параметров из контекста апплета
 * 
 * <p> Для запуска апплета понадобится:
 * <li> Браузер с поддержкой Java
 * <li> Запустить HTTP-сервер (например, \executables\windows\Run_012_LargerHttpServer.cmd )
 * <li> В строке браузера набрать URL: http://localhost/java_api_testing/applet_api/AppletResources.html 
 *
 */
public class AppletResources extends JApplet {
	private static final long serialVersionUID = 1L;
	
	JTextArea ta = new JTextArea();
	
	@Override public void init() {
		ta.setEditable(false);
		getContentPane().add( new JScrollPane(ta), BorderLayout.CENTER );
		
		showParams ();
		showResources ();
		
		JPanel bottom_panel = new JPanel( new BorderLayout() );
		getContentPane().add( bottom_panel, BorderLayout.SOUTH );
		
		bottom_panel.add(new JLabel("Картинка с сервера:", JLabel.CENTER), BorderLayout.NORTH);
		// Мы можем загружать ресурсы, расположенные относительно classpath апплета:
		Image scaledImage = Toolkit.getDefaultToolkit().getImage( getClass().getResource("mona_lisa.gif") );
		scaledImage = scaledImage.getScaledInstance(100, 130, Image.SCALE_DEFAULT);
		bottom_panel.add ( new JLabel(new ImageIcon(scaledImage), JLabel.CENTER), BorderLayout.CENTER );
	}
	
	private void showParams () {
		// Мы можем вычитывать параметры, переданные апплету через HTML-теги в исходном документе 
		write ( "Параметры, переданные апплету:" );
		write ( "Param1 = \"" + this.getParameter("Param1") + "\"" );
		write ( "Param2 = \"" + this.getParameter("Param2") + "\"" );
		write ( "" );
	}
	
	private void showResources () {
		write ( "Ресурсы апплета:" );
		// Метод getDocumentBase() возвращает URL-адрес документа, из которого был вызван апплет:
		write ( "DocumentBase: " + this.getDocumentBase() );
		// Метод getCodeBase() возвращает URL дирректории, в которой содержится код данного апплета:
		write ( "CodeBase: " + this.getCodeBase() );
		write ( "" );
		
		try {
			// Мы можем устанавливать соединения из апплета с сервером, с которого был загружен апплет:
			HttpURLConnection httpConnection = (HttpURLConnection) this.getCodeBase().openConnection();
			
			if ( httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
				write ( "Документ по адресу \"" + this.getCodeBase() + "\":" );
				
				try ( BufferedReader in = new BufferedReader( new InputStreamReader(httpConnection.getInputStream())) ) {
					StringBuilder urlContentSB = new StringBuilder();

					// Построчно считываем содержимое поля данных, отправленных вместе с HTTP-ответом:
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						urlContentSB.append(inputLine + "\n");
					}
					
					write (urlContentSB.toString());
				}
			} else {
				write("Ошибка запроса, код = " + httpConnection.getResponseCode() + "...");
			}
		} catch (IOException e) {
			write ( "Ошибка соединения: " + e );
		}
		
		URL externalURL = null;
		try {
			// Попробуем установить соединение к внешнему ресурсу:
			externalURL = new URL ("http://google.com/");
			((HttpURLConnection)externalURL.openConnection()).getResponseCode();
		} catch ( Exception e ) {
			// По-умолчанию политика безопасности браузера запретит нам устанавливать соединения
			// с внешними ресурсами:
			write ( "Ошибка доступа к \"" + externalURL + "\": " + e );
		}
	}

	private void write( String s ) {
		ta.append( s + "\n" );
	}
}

