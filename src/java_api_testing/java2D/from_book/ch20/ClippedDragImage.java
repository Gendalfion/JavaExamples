package java_api_testing.java2D.from_book.ch20;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClippedDragImage extends DragImage 
{
  private static final long serialVersionUID = 1L;
  
  int oldX, oldY;

  public ClippedDragImage( Image i ) { super(i); }

  public void mouseDragged(MouseEvent e) {
    imageX = e.getX();
    imageY = e.getY();
    Rectangle r = getAffectedArea( oldX, oldY, imageX, imageY, imageWidth, imageHeight);
    // Вызываем метод repaint с областью отсечения r
    repaint(r);  
    // Все операции рисования, которые не будут затрагивать область снаружи
    // прямоугольника отсечения, будут проигнорированы
    oldX = imageX;
    oldY = imageY;
  }

  private Rectangle getAffectedArea( int oldx, int oldy, int newx, int newy, int width, int height) 
  {
	// Вычисляем прямоугольник, описывающий старое и новое положение картинки при перетаскивании:
    int x = Math.min(oldx, newx) - width/2;
    int y = Math.min(oldy, newy) - height/2;
    int w = (Math.max(oldx, newx) + width/2)  - x;
    int h = (Math.max(oldy, newy) + height/2) - y;
    return new Rectangle (x, y, w, h);
  }

  public static void main(String[] args) {
    String imageFile = "L1-Light.jpg";
    if (args.length > 0)
      imageFile = args[0];

    // Turn on/off double buffering:
 	JCheckBox doubleBufferingEnabled = new JCheckBox("Double Buffering:");
 	// По-умолчанию для компонентов swing двойная буферизация включена:
 	doubleBufferingEnabled.setSelected(true);
 	doubleBufferingEnabled.setHorizontalTextPosition(SwingConstants.LEADING);
 	doubleBufferingEnabled.addActionListener( new ActionListener() {
 		@Override public void actionPerformed(ActionEvent e) {
 			RepaintManager.currentManager(null).setDoubleBufferingEnabled(doubleBufferingEnabled.isSelected());
 		}
 	});

    Image image = Toolkit.getDefaultToolkit().getImage(
        ClippedDragImage.class.getResource(imageFile));
	image = image.getScaledInstance(imageWidth, imageHeight,Image.SCALE_DEFAULT);
    JFrame frame = new JFrame("ClippedDragImage");
    frame.getContentPane().add( new ClippedDragImage(image) );
    frame.getContentPane().add( doubleBufferingEnabled, BorderLayout.SOUTH );
    frame.setSize(300, 300);
    frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible(true);
  }
}
