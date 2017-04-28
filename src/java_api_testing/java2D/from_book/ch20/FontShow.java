package java_api_testing.java2D.from_book.ch20;
//file: FontShow.java
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import javax.swing.*;

public class FontShow extends JComponent 
{
  private static final long serialVersionUID = 1L;
  
  private static Font [] ALL_FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
  private static int []  FONT_SIZES = { 18, 36, 72, 96, 114 };
  
  protected static JLabel sInfoLabel = new JLabel("", JLabel.LEADING);
  
  private static final int PAD = 25;   // frilly line padding
  private int mCurFontIndex = 0;
  private int mCurFontSizeIndex = 2;
  private String message;

  public FontShow(String message) {
    this.message = message;
    addMouseListener(new MouseAdapter() {
      public void mousePressed (MouseEvent e) {
    	  // Меняем направление обхода массивов при нажатой клавише Ctrl:
    	  int inc_val = ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) ? (-1) : (1);
    	  if ( e.getButton() == MouseEvent.BUTTON1 ) {
    		  // Меняем текущий шрифт при нажатии ЛКМ:
    		  mCurFontIndex = (mCurFontIndex + inc_val + ALL_FONTS.length) % ALL_FONTS.length;
    	  }
    	  if ( e.getButton() == MouseEvent.BUTTON3 ) {
    		  // Меняем размер текущего шрифта при нажатии ПКМ:
    		  mCurFontSizeIndex = (mCurFontSizeIndex + inc_val + FONT_SIZES.length) % FONT_SIZES.length;
    	  }
    	  repaint();
      }
    });
  }

  public void paint(Graphics g) 
  {
    Graphics2D g2 = (Graphics2D)g;

    // Включаем сглаживание при рисовании: 
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Устанавливаем текущий шрифт для вывода текста: 
    int size = FONT_SIZES[mCurFontSizeIndex];
    Font font = ALL_FONTS[mCurFontIndex].deriveFont(Font.PLAIN, size);
    sInfoLabel.setText( String.format("Font.name = %s, size = %d", font.getName(), size) );
    g2.setFont(font);
    int width = getSize().width;
    int height = getSize().height;

    // Получаем метрики шрифта для текущего сообщения message в графическом контексте g2:
    FontRenderContext frc = g2.getFontRenderContext();
    LineMetrics metrics = font.getLineMetrics(message, frc);
    float messageWidth = (float)font.getStringBounds(message, frc).getWidth();
    float ascent = metrics.getAscent();	  // Наибольшая высота символов над линией шрифта (подъем)
    float descent = metrics.getDescent(); // Наибольшее расстояние на которое выступает шрифт ниже линии шрифта (спуск)
    
    // Получаем координаты левого нижнего угла для вывода надписи 
    // (надпись получится ровно в центре компонента):
    float x = (width - messageWidth) / 2;
    float y = (height + metrics.getHeight()) / 2 - descent;

    g2.setPaint(getBackground());
    g2.fillRect(0, 0, width, height);

    // Выводим текст сообщения message:
    g2.setPaint(getForeground());
    g2.drawString(message, x, y);

    // Рисуем линии, визуализирующие метрики шрифта:
    g2.setPaint(Color.white);  // Base lines
    // Горизонтальная белая линия показывает базовую линию шрифта
    // (т. е. по y эта линия равна координате, переданной в g2.drawString(...):
    drawLine(g2, x - PAD, y, x + messageWidth + PAD, y);
    // Вертикальные белые линии показывают границы текста слева и справа:
    drawLine(g2, x, y + PAD, x, y - ascent - PAD);
    drawLine(g2, x + messageWidth, y + PAD, x + messageWidth, y - ascent - PAD);
    // Горизонтальная зеленая линия показывает подъем текущего шрифта:
    g2.setPaint(Color.green);  // Ascent line
    drawLine(g2, x - PAD, y - ascent, x + messageWidth + PAD, y - ascent);
    // Горизонтальная красная линия показывает спуск текущего шрифта:
    g2.setPaint(Color.red);    // Descent line
    drawLine(g2, x - PAD, y + descent, x + messageWidth + PAD, y + descent);
  }

  private void drawLine(Graphics2D g2, double x0, double y0, double x1, double y1) {
    Shape line = new java.awt.geom.Line2D.Double(x0, y0, x1, y1);
    g2.draw(line);
  }

  public static void main(String args[]) {
    String message = "Lemming";
    if (args.length > 0) message = args[0];

    JFrame frame = new JFrame("FontShow");
    frame.setSize(420, 300);
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	JComponent fontShowComp = new FontShow(message);
	fontShowComp.setBackground(Color.GRAY);
    frame.getContentPane().add(fontShowComp);
    frame.getContentPane().add(sInfoLabel, BorderLayout.SOUTH);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}