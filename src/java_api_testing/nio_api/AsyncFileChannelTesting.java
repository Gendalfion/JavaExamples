package java_api_testing.nio_api;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import my_util.FileUtil;

/**
 * Демонстрация работы с файлами через пакет java.nio.* с использованием асинхронного канала AsynchronousFileChannel
 * @author Lab119Alex
 *
 */
public class AsyncFileChannelTesting {
	// Асинхронный файловый канал из пакета java.nio.* позволяет производить операции 
	// ввода/вывода с файлами в неблокирующем режиме
	AsynchronousFileChannel mAsynchronousFileChannel = null;
	
	public AsyncFileChannelTesting () {
		JFrame myFrame = new JFrame ( "java.nio.* Asynchronous File Channel Testing" );
		myFrame.setSize(500, 600);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel (new GridLayout(0, 1));
		center_panel.setBorder(BorderFactory.createTitledBorder("Type your message:"));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JTextArea message_area = new JTextArea("This is testing message text...\n");
		message_area.setFont(message_area.getFont().deriveFont(16.0f));
		center_panel.add(new JScrollPane(message_area));
		
		JPanel top_panel = new JPanel (new BorderLayout());
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel repeat_cnt_panel = new JPanel ();
		repeat_cnt_panel.setLayout(new BoxLayout(repeat_cnt_panel, BoxLayout.Y_AXIS));
		top_panel.add(repeat_cnt_panel, BorderLayout.CENTER);
		
		final int MAX_REPEAT_CNT = 10000000;
		JSlider repeat_cnt_slider = new JSlider (1, MAX_REPEAT_CNT, 1);
		repeat_cnt_panel.add( new JPanel(new GridLayout(0, 1)).add(repeat_cnt_slider).getParent() );
		repeat_cnt_slider.setMinorTickSpacing (MAX_REPEAT_CNT / 20);
		repeat_cnt_slider.setPaintTicks(true);
		
		JLabel repeat_cnt_label = new JLabel("Repeat count: ...");
		repeat_cnt_panel.add( new JPanel().add(repeat_cnt_label).getParent() );
		
		JPanel btn_panel = new JPanel();
		top_panel.add( btn_panel, BorderLayout.EAST );
				
		JButton write_to_file_btn = new JButton ("Write to File...");
		btn_panel.add( write_to_file_btn );
		
		JButton cancel_writing_btn = new JButton("Cancel writing!");
		cancel_writing_btn.setVisible(false);
		btn_panel.add( cancel_writing_btn );
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
		
		repeat_cnt_slider.addChangeListener(new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				repeat_cnt_label.setText( String.format("Repeat count: %d", repeat_cnt_slider.getValue()) );
			}
		});
		repeat_cnt_slider.setValue(MAX_REPEAT_CNT);
		
		JFileChooser txtFileChooser = new JFileChooser();
		txtFileChooser.setAcceptAllFileFilterUsed(true);
		txtFileChooser.setFileFilter( new FileNameExtensionFilter("Text files *.txt", "txt") );
		write_to_file_btn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( txtFileChooser.showSaveDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					// Получаем путь к файлу, который выбрал пользователь, в дефолтной файловой системе ОС:
					Path out_path =  FileUtil.getSelectedFileWithExtension(txtFileChooser).toPath();
					
					
					try {
						// Создаем файловый канал по выбранному пути с опциями создания файла для записи (если файла нет по указанному пути):
						mAsynchronousFileChannel = AsynchronousFileChannel.open(out_path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
						
						// Повторяем текст из message_area заданное количество раз и записываем результат в CharBuffer:
						String text_to_repeat = message_area.getText();
						CharBuffer cb = CharBuffer.allocate( text_to_repeat.length() * repeat_cnt_slider.getValue() );
						for ( int i = 0; i < repeat_cnt_slider.getValue(); ++ i ) { cb.put(text_to_repeat); }
						
						// Сбрасываем текущую позицию и границу в буфере (для того, чтобы записать все содержимое буфера в файл)
						cb.clear();
						
						// Записываем содержимое буфера в файл, предварительно кодируя символы буфера в дефолтной кодировке ОС:
						mAsynchronousFileChannel.write( Charset.defaultCharset().encode( cb ), 0, null, 
							// Операция записи будет производиться в отдельном потоке из пула потоков системы.
							// Результат операции будет сообщен через интерфейс CompletionHandler:
							new CompletionHandler<Integer, Object>() {
								@Override public void completed(Integer result, Object attachment) {
									// Операция выполнена успешно, в параметре result передается количество реально записанных байт:
									try {
										if ( mAsynchronousFileChannel != null ) {
											mAsynchronousFileChannel.close();
											mAsynchronousFileChannel = null;
										}
									} catch (IOException e) {}
									restoreGUIState ();
									repeat_cnt_label.setText( String.format("%d bytes written to file \"%s\"...", result, out_path.getFileName().toString()) );
								}

								@Override public void failed(Throwable exc, Object attachment) {
									// Произошла ошибка во время выполнения операции, в параметре exc передается причина ошибки:
									restoreGUIState ();
									repeat_cnt_label.setText( String.format("Failed to write file \"%s\"!", out_path.getFileName().toString()) );
									exc.printStackTrace();
									JOptionPane.showMessageDialog( main_panel, exc.getClass().getName() + " : " + exc.getMessage() );
								}
							
								private void restoreGUIState () {
									message_area.setEnabled(true);
									repeat_cnt_slider.setEnabled(true);
									write_to_file_btn.setVisible(true);
									cancel_writing_btn.setVisible(false);
									btn_panel.revalidate();
									top_panel.repaint();
								}
						} );
						
						message_area.setEnabled(false); message_area.repaint();
						repeat_cnt_slider.setEnabled(false); repeat_cnt_slider.repaint();
						repeat_cnt_label.setText( String.format("Writing text to file: \"%s\"...", out_path.getFileName().toString()) );
						write_to_file_btn.setVisible(false); write_to_file_btn.repaint();
						cancel_writing_btn.setVisible(true); cancel_writing_btn.repaint();
						btn_panel.revalidate();
					} catch (Exception exception) {
						exception.printStackTrace();
						JOptionPane.showMessageDialog( main_panel, exception.getClass().getName() + " : " + exception.getMessage() );
					}
				}
			}
		});
		
		cancel_writing_btn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				// Закрываем асинхронный канал по нажатию кнопки cancel_writing_btn:
				AsynchronousFileChannel fc = mAsynchronousFileChannel;
				if ( fc != null ) {
					// ! По факту операция почему то не отменяется при вызове close() (поток блокируется до завершения операции записи)...
					try { fc.close(); } catch (IOException ex) { ex.printStackTrace(); }
					mAsynchronousFileChannel = null;
				}
			}
		});
	}

	public static void main(String[] args) {
		new AsyncFileChannelTesting ();
	}

}
