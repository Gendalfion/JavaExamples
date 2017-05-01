package java_api_testing.java2D.from_book.ch21;
//file: StatusImage.java
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

public class StatusImage extends JComponent
{
  private static final long serialVersionUID = 1L;
  
  boolean loaded = false;
  String message = "Loading...";
  Image image;

  public StatusImage( Image image ) { this.image = image; }

  public void paint(Graphics g) {
	// � ����������� �� ����� loaded, ������� �������� image ��� ��������� message:
    if (loaded) 
		g.drawImage(image, 0, 0, this);
    else {
      g.drawRect(0, 0, getSize().width - 1, getSize(  ).height - 1);
      g.drawString(message, 20, 20);
    }
  }
  public void loaded() {
	loaded = true;
	repaint();
  }
  public void setMessage( String msg ) {
	message = msg;
	repaint();
  }

  public static void main( String [] args ) throws MalformedURLException { 
	JFrame frame = new JFrame("TrackImage");
	Image image;
	if ( args.length < 1 ) {
		image = Toolkit.getDefaultToolkit().getImage( new URL("http://www.sunhome.ru/i/wallpapers/129/bolshie-ozera.1920x1080.jpg") );
    } else {
    	image = Toolkit.getDefaultToolkit().getImage( args[0] );
    }
	StatusImage statusImage = new StatusImage( image );
	frame.getContentPane().add( statusImage );
	frame.setSize(300,300);
	frame.setVisible(true);

	// ����� MediaTracker �������� ��������������� �������� ��� ������������ �������� �������� 
	// ����������� �� ������� � ��������� �������� �������:
    MediaTracker tracker = new MediaTracker( statusImage );
	int MAIN_IMAGE = 0;
	// ��������� ����������� � ������ � ��������� ���������������:
	// �������������� ��������� � ���������� ��������� �� ����������� (��� ������ �����������),
	// � ����� ��� ��������� �������� ��������� �������� ����������� (��� ���� ��������
	// ��������������, ��� ���� ��������� ��������)
    tracker.addImage(image, MAIN_IMAGE);
    try { 
    	// ��������� ������� ����� �� ��������� �������� �������� ���� �����������
    	// � ������ MAIN_IMAGE:
		tracker.waitForID(MAIN_IMAGE); 
	} catch (InterruptedException e) {}
    
    // ��������� ��� ����������� �������� �����������:
    if ( tracker.isErrorID(MAIN_IMAGE) ) 
		statusImage.setMessage("Error");
    else 
    	// ������������� ��, ��� ����� ImageIcon ��� �������� � ���� ���������� MediaTracker
    	// � ��������� ����������� ����������� �������� ����������� ��� ������ ���������� ����:
    	// ImageIcon icon 	= new ImageIcon ( "<Image_Resource>" );
    	// Image image 		= icon.getImage ();
		statusImage.loaded();
  }
}
