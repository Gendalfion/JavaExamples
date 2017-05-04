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
 * ������������ ����������� ������� �������� � ���������� �� ��������� �������
 * 
 * <p> ��� ������� ������� �����������:
 * <li> ������� � ���������� Java
 * <li> ��������� HTTP-������ (��������, \executables\windows\Run_012_LargerHttpServer.cmd )
 * <li> � ������ �������� ������� URL: http://localhost/java_api_testing/applet_api/AppletResources.html 
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
		
		bottom_panel.add(new JLabel("�������� � �������:", JLabel.CENTER), BorderLayout.NORTH);
		// �� ����� ��������� �������, ������������� ������������ classpath �������:
		Image scaledImage = Toolkit.getDefaultToolkit().getImage( getClass().getResource("mona_lisa.gif") );
		scaledImage = scaledImage.getScaledInstance(100, 130, Image.SCALE_DEFAULT);
		bottom_panel.add ( new JLabel(new ImageIcon(scaledImage), JLabel.CENTER), BorderLayout.CENTER );
	}
	
	private void showParams () {
		// �� ����� ���������� ���������, ���������� ������� ����� HTML-���� � �������� ��������� 
		write ( "���������, ���������� �������:" );
		write ( "Param1 = \"" + this.getParameter("Param1") + "\"" );
		write ( "Param2 = \"" + this.getParameter("Param2") + "\"" );
		write ( "" );
	}
	
	private void showResources () {
		write ( "������� �������:" );
		// ����� getDocumentBase() ���������� URL-����� ���������, �� �������� ��� ������ ������:
		write ( "DocumentBase: " + this.getDocumentBase() );
		// ����� getCodeBase() ���������� URL �����������, � ������� ���������� ��� ������� �������:
		write ( "CodeBase: " + this.getCodeBase() );
		write ( "" );
		
		try {
			// �� ����� ������������� ���������� �� ������� � ��������, � �������� ��� �������� ������:
			HttpURLConnection httpConnection = (HttpURLConnection) this.getCodeBase().openConnection();
			
			if ( httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
				write ( "�������� �� ������ \"" + this.getCodeBase() + "\":" );
				
				try ( BufferedReader in = new BufferedReader( new InputStreamReader(httpConnection.getInputStream())) ) {
					StringBuilder urlContentSB = new StringBuilder();

					// ��������� ��������� ���������� ���� ������, ������������ ������ � HTTP-�������:
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						urlContentSB.append(inputLine + "\n");
					}
					
					write (urlContentSB.toString());
				}
			} else {
				write("������ �������, ��� = " + httpConnection.getResponseCode() + "...");
			}
		} catch (IOException e) {
			write ( "������ ����������: " + e );
		}
		
		URL externalURL = null;
		try {
			// ��������� ���������� ���������� � �������� �������:
			externalURL = new URL ("http://google.com/");
			((HttpURLConnection)externalURL.openConnection()).getResponseCode();
		} catch ( Exception e ) {
			// ��-��������� �������� ������������ �������� �������� ��� ������������� ����������
			// � �������� ���������:
			write ( "������ ������� � \"" + externalURL + "\": " + e );
		}
	}

	private void write( String s ) {
		ta.append( s + "\n" );
	}
}

