package java_api_testing.java_thread_api;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ForkJoinTesting {
	public static void main(String[] args) {
		Mandelbrot mandy = new Mandelbrot();
        mandy.setSize( 768, 768 );
        mandy.setVisible( true );
	}
}

class Mandelbrot extends JFrame
{
	JPanel mPanel = new JPanel();
	JToggleButton mModeButton = new JToggleButton("Thread Indication");
	HashMap<String, Integer> mThreadMap = new HashMap<>() ;
	
	public Mandelbrot() {
		super ("Mandelbrot fractal (Fork-Join Testing)");
		
		this.add(mPanel.add(mModeButton).getParent());
		
		mModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Mandelbrot.this.repaint();
			}
		});
	}
	
    @Override public void paint( Graphics g ) {
    	mThreadMap.clear();
    	
        BufferedImage image = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        // Создаем пул потоков ForkJoinPool, который запускает задачи рекурсивных типов:
        ForkJoinPool pool = new ForkJoinPool(); // по-умолчанию резервируется по одному потоку на процессор
        // Запускаем рекурсивную задачу MandelbrotTask для всего изображения
        pool.invoke( new MandelbrotTask( image, 0, image.getWidth()-1, 0, image.getHeight()-1 ) );
        
        // Отрисовываем полученное изображение:
        mPanel.getGraphics().drawImage( image, 0, 0, null );
        mModeButton.repaint();
    }	    
    
    private class MandelbrotTask extends RecursiveAction	// RecursiveAction - тип рекурсивной задачи, не возвращающей значения
    {
        private final double size = 3.0, offsetX = -0.7, thresholdSq = 100;
        private final int maxIterations = 30;
        private BufferedImage image;
        private int xStart, xEnd, yStart, yEnd;
        private final int taskSplitThreshold = 1024;

        MandelbrotTask( BufferedImage image, int xStart, int xEnd, int yStart, int yEnd ) {
            this.image = image;
            this.xStart = xStart; this.xEnd = xEnd; this.yStart = yStart; this.yEnd = yEnd;
        }

        public void renderFull()
        {
            for ( int x = xStart; x <= xEnd; x++ ) {
                for ( int y = yStart; y <= yEnd; y++ )  {
                    double r = x * size / image.getWidth() -size/2 + offsetX;
                    double i = y * size / image.getHeight() -size/2;
                    double zr=0, zi=0;
                    int iter;
                    for ( iter = 0; iter < maxIterations; iter++ ) {
                        double nzr = zr*zr - zi*zi + r;
                        double nzi = 2*zr*zi + i;
                        if ( nzr*nzr + nzi*nzi > thresholdSq ) { break; }
                        zr = nzr; zi=nzi;
                    }
                    if ( mModeButton.isSelected() ) {	// индикация работы потоков на изображении включена:
                    	// Получаем номер текущего потока из отображения по имени потока
                    	Integer thread_num = mThreadMap.get( Thread.currentThread().getName() );
                    	if ( thread_num == null ) {	// Если поток еще не зарегистрирован в отображении:
                    		thread_num = mThreadMap.size() + 1;	// Присваиваем ему номер
                    		// И добавляем поток в отображение с новым номером (на 1 больше предыдущего):
                    		mThreadMap.put(Thread.currentThread().getName(), new Integer(thread_num));
                    	}
                    	// Используем номер текущего потока для регулировки яркости участка изображения, отрисованного потоком:
                    	image.setRGB( x, y, Color.HSBtoRGB( 0.5f * iter / maxIterations, 1.0f, 1.0f / (float)(thread_num.intValue()) ) );
                    } else {
                    	image.setRGB( x, y, Color.HSBtoRGB( 0.5f * iter / maxIterations, 1.0f, 1.0f) );
                    }
                }
            }
        }

        @Override protected void compute()	// метод compute() содержит код рекурсивной задачи
        {
        	// В коде мы дробим изображение на куски и отрисовываем часть изображения только если оно достаточно маленькое
            int width = xEnd-xStart,  height = yEnd-yStart;
            if ( width*height < taskSplitThreshold ) {
                renderFull();
            } else {
            	// метод invokeAll принимает список задач, который мы получаем при рекурсивном дроблении данной задачи
                invokeAll(
                	// Делим изображение на 4 равных квадранта и отрисовываем каждый в своей задаче:
                    new MandelbrotTask( image, xStart, xStart+width/2, yStart, yStart+height/2 ),
                    new MandelbrotTask( image, xStart+width/2+1, xEnd, yStart, yStart+height/2 ),
                    new MandelbrotTask( image, xStart, xStart+width/2, yStart+height/2+1, yEnd ),
                    new MandelbrotTask( image, xStart+width/2+1, xEnd, yStart+height/2+1, yEnd )
                );
            }
        }
    }

}

