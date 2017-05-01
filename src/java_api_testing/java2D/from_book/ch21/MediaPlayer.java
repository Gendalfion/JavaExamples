package java_api_testing.java2D.from_book.ch21;

import java.net.URL;
import javax.swing.*;
import javax.media.*;

import java.awt.BorderLayout;
import java.awt.Component;

public class MediaPlayer 
{
	public static void main( String[] args ) throws Exception {
		// ��� ���������� � ������� ������� ������� ����������� 
		// ��������� ���������� jmf.jar �� ���������� Java Media Framework 
		// (����������� � ������������ ����� Oracle)
		
		final JFrame frame = new JFrame("MediaPlayer");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		URL url;
		if ( args.length < 1 ) {
			url = MediaPlayer.class.getResource("dancing_baby.avi");
	    } else {
	    	url = new URL( args[0] );
	    }
		// ������� ������ javax.media.Player ��� ��������������� 
		// ��������� ������� �� ���������� URL:
		final Player player = Manager.createPlayer( url );

		// ����� Player.realize() ��������� ���������� ��������� �������� � ��������������� 
		// (� ��������� ������, ��������� ���������� ����� ��������� ControllerListener):
		player.realize();
		
		player.addControllerListener( new ControllerListener() {
			public void controllerUpdate( ControllerEvent ce ) {
				// �� ���� ������� ���������� ���������� RealizeCompleteEvent:
				if ( ce instanceof RealizeCompleteEvent ) 
				{
					// ��� ������ ���������� ���������, �� ����� ���������
					// ���������� ����������, ��������� � �����-���������:
					
					// ����������� ���������, ��������������� ��� �������:
					Component visual = player.getVisualComponent();
					// ����������� ���������, ��������������� ���������� ����������������:
					Component control = player.getControlPanelComponent();
					
					// ��������� ���������� �� ������� ����:
					if ( visual != null ) {
						frame.getContentPane().add( visual, BorderLayout.CENTER );
					}
					frame.getContentPane().add( control, BorderLayout.SOUTH );
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible( true );
					
					// �������� ��������������� �����-��������:
					player.start();
				}
			}
		});
	}
}
