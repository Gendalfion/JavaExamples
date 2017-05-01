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
	// В зависимости от флага loaded, выводим картинку image или сообщение message:
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

	// Класс MediaTracker является вспомогательным объектом для отслеживания процесса загрузки 
	// изображений из сетевых и локальных ресурсов системы:
    MediaTracker tracker = new MediaTracker( statusImage );
	int MAIN_IMAGE = 0;
	// Добавляем изображение в трекер с указанным идентификатором:
	// Идентификаторы позволяют в дальнейшем ссылаться на изображение (или группу изображений),
	// а также они позволяют задавать приоритет загрузки изображений (чем ниже значение
	// идентификатора, тем выше приоритет загрузки)
    tracker.addImage(image, MAIN_IMAGE);
    try { 
    	// Блокируем текущий поток до окончания процесса загрузки всех изображений
    	// в группе MAIN_IMAGE:
		tracker.waitForID(MAIN_IMAGE); 
	} catch (InterruptedException e) {}
    
    // Проверяем чем закончилась загрузка изображения:
    if ( tracker.isErrorID(MAIN_IMAGE) ) 
		statusImage.setMessage("Error");
    else 
    	// Примечательно то, что класс ImageIcon уже содержит в себе встроенный MediaTracker
    	// и позволяет производить блокирующую загрузку изображений при помощи следующего кода:
    	// ImageIcon icon 	= new ImageIcon ( "<Image_Resource>" );
    	// Image image 		= icon.getImage ();
		statusImage.loaded();
  }
}
