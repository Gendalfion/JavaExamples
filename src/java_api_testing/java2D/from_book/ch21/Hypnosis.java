package java_api_testing.java2D.from_book.ch21;
//file: Hypnosis.java
import java.awt.*;
import java.awt.geom.GeneralPath;
import javax.swing.*;

public class Hypnosis extends JComponent implements Runnable {
  private static final long serialVersionUID = 1L;

  private int[] coordinates;
  private int[] deltas;
  private Paint paint;

  public Hypnosis(int numberOfSegments) {
    int numberOfCoordinates = numberOfSegments * 4 + 2;
    coordinates = new int[numberOfCoordinates];
    deltas = new int[numberOfCoordinates];
    for (int i = 0 ; i < numberOfCoordinates; i++) {
      coordinates[i] = (int)(Math.random(  ) * 300);
      deltas[i] = (int)(Math.random(  ) * 4 + 3);
      if (deltas[i] > 4) deltas[i] = -(deltas[i] - 3);
    }
    paint = new GradientPaint(0, 0, Color.blue, 20, 10, Color.red, true);

    Thread t = new Thread(this);
    t.start(  );
  }

  public void run(  ) {
	// Поток, поддерживающий анимацию компонента: 
    try {
      while (true) {
        timeStep(  );
        repaint(  );
        Thread.sleep(1000 / 24);
      }
    }
    catch (InterruptedException ie) {}
  }

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;
    // Выполняем отрисовку компонента на холсте Graphics2D g2:
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Shape s = createShape();
    g2.setPaint(paint);
    g2.fill(s);
    g2.setPaint(Color.white);
    // Swing использует двойную буфферизацию по-умолчанию, поэтому мы не 
    // заметим мерцания нашей анимации:
    g2.draw(s);
  }

  private void timeStep(  ) {
	// Выполняем шаг анимации:
	Dimension d = getSize();
    if (d.width == 0 || d.height == 0) return;
    // Перемещаем координату каждой точки массива coordinates на соответсвующее значение
    // шага из массива deltas (при этом контролируем выход точек за границы компонента):
    for (int i = 0; i < coordinates.length; i++) {
      coordinates[i] += deltas[i];
      int limit = (i % 2 == 0) ? d.width : d.height;
      if (coordinates[i] < 0) {
        coordinates[i] = 0;
        deltas[i] = -deltas[i];
      }
      else if (coordinates[i] > limit) {
        coordinates[i] = limit - 1;
        // Создаем эффект отскока точек при достижении границ компонента: 
        deltas[i] = -deltas[i];
      }
    }
  }

  private Shape createShape(  ) {
	// Создаем форму GeneralPath из массива координат coordinates:
    GeneralPath path = new GeneralPath();
    path.moveTo(coordinates[0], coordinates[1]);
    for (int i = 2; i < coordinates.length; i += 4) {
      path.quadTo(coordinates[i], coordinates[i + 1], coordinates[i + 2], coordinates[i + 3]);
    }
    path.closePath();
    return path;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Hypnosis");
    frame.getContentPane().add( new Hypnosis(4) );
    frame.setSize(300, 300);
    frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible(true);
  }
}
