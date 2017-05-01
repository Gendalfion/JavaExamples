package java_api_testing.java2D.from_book.ch21;

import java.net.URL;
import javax.swing.*;
import javax.media.*;

import java.awt.BorderLayout;
import java.awt.Component;

public class MediaPlayer 
{
	public static void main( String[] args ) throws Exception {
		// Для компиляции и запуска данного примера потребуется 
		// сторонняя библиотека jmf.jar из библиотеки Java Media Framework 
		// (скачивается с официального сайта Oracle)
		
		final JFrame frame = new JFrame("MediaPlayer");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		URL url;
		if ( args.length < 1 ) {
			url = MediaPlayer.class.getResource("dancing_baby.avi");
	    } else {
	    	url = new URL( args[0] );
	    }
		// Создаем объект javax.media.Player для воспроизведения 
		// медийного ресурса по указанному URL:
		final Player player = Manager.createPlayer( url );

		// Метод Player.realize() выполняет подготовку медийного контента к воспроизведению 
		// (в отдельном потоке, результат сообщается через интерфейс ControllerListener):
		player.realize();
		
		player.addControllerListener( new ControllerListener() {
			public void controllerUpdate( ControllerEvent ce ) {
				// Мы ждем события завершения реализации RealizeCompleteEvent:
				if ( ce instanceof RealizeCompleteEvent ) 
				{
					// Как только реализация завершена, мы можем запросить
					// визуальные компоненты, связанные с медиа-контентом:
					
					// Запрашиваем компонент, визуализирующий сам контент:
					Component visual = player.getVisualComponent();
					// Запрашиваем компонент, визуализирующий управление воспроизведением:
					Component control = player.getControlPanelComponent();
					
					// Добавляем компоненты на главное окно:
					if ( visual != null ) {
						frame.getContentPane().add( visual, BorderLayout.CENTER );
					}
					frame.getContentPane().add( control, BorderLayout.SOUTH );
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible( true );
					
					// Стартуем воспроизведение медиа-контента:
					player.start();
				}
			}
		});
	}
}
