package java_api_testing.applet_api.src;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;

/**
 * ������������ �������� Java-�������
 * 
 * <p> ��� ������� ������� �����������:
 * <li> ������� � ���������� Java (Chrome Java �� ������������ �������������...)
 * <li> ��������� HTTP-������ (��������, \executables\windows\Run_012_LargerHttpServer.cmd )
 * <li> � ������ �������� ������� URL: http://localhost/java_api_testing/applet_api/showapplet.html 
 * 
 */
public class ShowApplet extends JApplet {
	private static final long serialVersionUID = 1L;
	
	JTextArea text = new JTextArea();
	int startCount;

	// ��������� init() ���������� �������� ����� �������� ������� � ����� ��� �������� � ��������
	@Override public void init() 
	{
		// ������ - ��� ������� GUI ���������, ������� ������������ � HTML-��������:
		
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
		text.append( "������ Java: " + System.getProperty("java.version") + "\n" );
		text.append( "JApplet.init()\n" );
	}
	
	// ��������� start() ���������� ������ ��� ����� �������������� ������ �������
	@Override public void start() {
		text.append( "JApplet.start(): "+ (startCount++) +"\n" );
	}
	
	// ��������� stop() ���������� ������ ��� ����� ������������� ������ �������
	@Override public void stop() {
		text.append( "JApplet.stop().\n" );
	}
	
	// ��������� destroy() ���������� �������� ����� ��������� ������� �� ����� ���������� ��������
	@Override public void destroy() {
		super.destroy();
	}
}

