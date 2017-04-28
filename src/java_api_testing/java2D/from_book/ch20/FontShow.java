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
    	  // ������ ����������� ������ �������� ��� ������� ������� Ctrl:
    	  int inc_val = ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) ? (-1) : (1);
    	  if ( e.getButton() == MouseEvent.BUTTON1 ) {
    		  // ������ ������� ����� ��� ������� ���:
    		  mCurFontIndex = (mCurFontIndex + inc_val + ALL_FONTS.length) % ALL_FONTS.length;
    	  }
    	  if ( e.getButton() == MouseEvent.BUTTON3 ) {
    		  // ������ ������ �������� ������ ��� ������� ���:
    		  mCurFontSizeIndex = (mCurFontSizeIndex + inc_val + FONT_SIZES.length) % FONT_SIZES.length;
    	  }
    	  repaint();
      }
    });
  }

  public void paint(Graphics g) 
  {
    Graphics2D g2 = (Graphics2D)g;

    // �������� ����������� ��� ���������: 
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // ������������� ������� ����� ��� ������ ������: 
    int size = FONT_SIZES[mCurFontSizeIndex];
    Font font = ALL_FONTS[mCurFontIndex].deriveFont(Font.PLAIN, size);
    sInfoLabel.setText( String.format("Font.name = %s, size = %d", font.getName(), size) );
    g2.setFont(font);
    int width = getSize().width;
    int height = getSize().height;

    // �������� ������� ������ ��� �������� ��������� message � ����������� ��������� g2:
    FontRenderContext frc = g2.getFontRenderContext();
    LineMetrics metrics = font.getLineMetrics(message, frc);
    float messageWidth = (float)font.getStringBounds(message, frc).getWidth();
    float ascent = metrics.getAscent();	  // ���������� ������ �������� ��� ������ ������ (������)
    float descent = metrics.getDescent(); // ���������� ���������� �� ������� ��������� ����� ���� ����� ������ (�����)
    
    // �������� ���������� ������ ������� ���� ��� ������ ������� 
    // (������� ��������� ����� � ������ ����������):
    float x = (width - messageWidth) / 2;
    float y = (height + metrics.getHeight()) / 2 - descent;

    g2.setPaint(getBackground());
    g2.fillRect(0, 0, width, height);

    // ������� ����� ��������� message:
    g2.setPaint(getForeground());
    g2.drawString(message, x, y);

    // ������ �����, ��������������� ������� ������:
    g2.setPaint(Color.white);  // Base lines
    // �������������� ����� ����� ���������� ������� ����� ������
    // (�. �. �� y ��� ����� ����� ����������, ���������� � g2.drawString(...):
    drawLine(g2, x - PAD, y, x + messageWidth + PAD, y);
    // ������������ ����� ����� ���������� ������� ������ ����� � ������:
    drawLine(g2, x, y + PAD, x, y - ascent - PAD);
    drawLine(g2, x + messageWidth, y + PAD, x + messageWidth, y - ascent - PAD);
    // �������������� ������� ����� ���������� ������ �������� ������:
    g2.setPaint(Color.green);  // Ascent line
    drawLine(g2, x - PAD, y - ascent, x + messageWidth + PAD, y - ascent);
    // �������������� ������� ����� ���������� ����� �������� ������:
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