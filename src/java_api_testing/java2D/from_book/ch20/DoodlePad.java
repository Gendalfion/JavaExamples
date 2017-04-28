package java_api_testing.java2D.from_book.ch20;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DoodlePad 
{
  public static void main(String[] args) 
  {
    JFrame frame = new JFrame("DoodlePad");
    Container content = frame.getContentPane();
    content.setLayout(new BorderLayout());
    final DrawPad drawPad = new DrawPad();
    content.add(drawPad, BorderLayout.CENTER);
    
    JCheckBox antiAliasingEnabled = new JCheckBox("Antialiasing: ");
    antiAliasingEnabled.setHorizontalTextPosition(SwingConstants.LEADING);
    antiAliasingEnabled.setSelected(true);
    antiAliasingEnabled.addActionListener(new ActionListener(  ) {
        public void actionPerformed(ActionEvent e) {
        	// Позволяем пользователю включать/отключать сглаживание при рисовании:
            drawPad.setAntialiasingEnabled( antiAliasingEnabled.isSelected() );
          }
    });
    
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(new ActionListener(  ) {
      public void actionPerformed(ActionEvent e) {
    	// Очищаем холст при нажатии кнопки "Clear":
        drawPad.clear ();
        antiAliasingEnabled.setSelected(true);
      }
    });
    
    JPanel panel = new JPanel();
    panel.add(clearButton);
    panel.add(antiAliasingEnabled);
    
    content.add(panel, BorderLayout.SOUTH);
    frame.setSize(400, 400);
    frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible(true);
  }

} // end of class DoodlePad

class DrawPad extends JComponent 
{
  private static final long serialVersionUID = 1L;
  
  Image image;
  Graphics2D graphics2D;
  int currentX, currentY, oldX, oldY;

  public DrawPad() {
    setDoubleBuffered(false);
    addMouseListener(new MouseAdapter(  ) {
      public void mousePressed(MouseEvent e) {
        oldX = e.getX();
        oldY = e.getY();
      }
    });
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        if (graphics2D != null) {
          // Рисуем на внеэкранном холсте graphics2D, который отображен в память:
          graphics2D.drawLine(oldX, oldY, currentX, currentY);
        }
        repaint();
        oldX = currentX;
        oldY = currentY;
      }
    });
  }

  public void paintComponent(Graphics g) {
    if (image == null) {
      // Создаем внеэкранное изображение в памяти:
      image = createImage(getSize().width, getSize().height);
      // Настраиваем холст Graphics2D для внеэкранного рисования:
      graphics2D = (Graphics2D)image.getGraphics();
      graphics2D.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setPaint (Color.white);
	  graphics2D.fillRect (0, 0, getSize().width, getSize().height);
	  graphics2D.setPaint (Color.black);
	  graphics2D.setStroke(new BasicStroke (1.5f));
    }
    // Выводим внеэкранное изображение на экран:
    g.drawImage(image, 0, 0, null);
  }

  public void clear () {
	image = null;
    repaint();
  }
  
  public void setAntialiasingEnabled ( boolean enabled ) {
	  graphics2D.setRenderingHint ( RenderingHints.KEY_ANTIALIASING
			  , (enabled) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
  }
}
