package java_api_testing.io_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Демонстрация работы с потоковыми классами java.io.PipedInputStream и java.io.PipedOutputStream,
 * а также с использованием кодировок символьной информации в Java API
 * @author Lab119Alex
 *
 */
public class PipeStreamTesting {
	// Объект PipedOutputStream используется в паре с PipedInputStream для межпотоковой передачи данных
	// Оба объекта являются наследниками абстракций OutputStream и InputStream соответственно;
	// Это означает, что данные объекты работают с неструктурированными потоками байт
	PipedOutputStream	mPipedOutputStream = new PipedOutputStream();
	PipedInputStream 	mPipedInputStream = new PipedInputStream();
	
	// Объект InputStreamReader является промежуточным звеном между иерархией классов, унаследованных от InputStream и Reader
	// Т. е. он преобразует неструктурированный набор входных байт в набор символов с соответствующей кодировкой
	InputStreamReader	mInputStreamReader = new InputStreamReader(mPipedInputStream); // Оборачиваем mPipedInputStream в обертку InputStreamReader
	
	// Объект OutputStreamWriter является промежуточным звеном между иерархией классов, унаследованных от mOutputStreamReader и Writer
	// Т. е. он преобразует неструктурированный набор выходных байт в набор символов с соответствующей кодировкой
	OutputStreamWriter	mOutputStreamWriter = new OutputStreamWriter(mPipedOutputStream); // Оборачиваем mPipedOutputStream в обертку OutputStreamWriter
	
	// Объект PrintWriter является вспомогательной оберткой для потоковых объектов
	// Данный класс позволяет писать в выходной поток строки текста с автоматическим сбросом (flush) потока (второй параметр конструктора), 
	// А также он не генерирует исключение IOException после каждой неудачной операции записи в поток (ошибку можно отловить при помощи метода checkError())
	PrintWriter mPrintWriter = new PrintWriter(mOutputStreamWriter, true); // Оборачиваем mOutputStreamWriter в обертку PrintWriter
	
	// Объект BufferedReader является вспомогательной оберткой для входных символных потоков
	// Он позволяет буфферизовать ввод и читать из потока символьные строки целиком
	BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader); // Оборачиваем mInputStreamReader в обертку BufferedReader
	
	// Получаем массив доступных кодировок на платформе Java при помощи класса java.nio.charset.Charset:
	final static Charset [] SUPPORTED_CHARSETS = Charset.availableCharsets().values().toArray(new Charset[0]);
	
	{
		try {
			// Связываем входной и выходной каналы (без выполнения данной операции каналы Pipe работать не будут!):
			mPipedInputStream.connect(mPipedOutputStream);
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, e.getClass().getName() + " : " + e.getMessage() );
		}
	}

	public PipeStreamTesting () {
		JFrame myFrame = new JFrame ( "java.io.PipedInputStream and java.io.PipedOutputStream Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JTextArea output_stream_area = new JTextArea("Сообщение на Русском (english symbols here)...");
		output_stream_area.setBorder(BorderFactory.createTitledBorder("java.io.PipedOutputStream:"));
		output_stream_area.setFont(new Font(Font.MONOSPACED, 0, 14));
		center_panel.add(new JScrollPane(output_stream_area));
		
		JTextArea input_stream_area = new JTextArea();
		input_stream_area.setBorder(BorderFactory.createTitledBorder("java.io.PipedInputStream:"));
		input_stream_area.setEditable(false);
		input_stream_area.setFont(new Font(Font.MONOSPACED, 0, 14));
		center_panel.add(new JScrollPane(input_stream_area));
		
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				input_stream_area.setText("");
			}
		});
		input_stream_area.setComponentPopupMenu(clear_menu);
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JComboBox<Charset> outstream_encoding_box = new JComboBox<>(SUPPORTED_CHARSETS);
		top_panel.add(new JLabel("OutputStreamWriter encoding:  "));
		top_panel.add(outstream_encoding_box);
		
		outstream_encoding_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Создаем объект-обертку OutputStreamWriter над mPipedOutputStream, указывая кодировку для кодирования символов в байты:
				mOutputStreamWriter = 
						new OutputStreamWriter(mPipedOutputStream, outstream_encoding_box.getItemAt(outstream_encoding_box.getSelectedIndex()));
				// Создаем объект-обертку PrintWriter над mOutputStreamWriter:
				mPrintWriter = new PrintWriter(mOutputStreamWriter, true); 
			}
		});
		outstream_encoding_box.setSelectedItem( Charset.defaultCharset() );
		outstream_encoding_box.setMaximumRowCount(40);
		
		JButton print_btn = new JButton("PrintWriter.println(String)");
		top_panel.add(print_btn);
		print_btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( output_stream_area.getText().length() > 0 ) {
					// Отправляем строку в выходной поток через объект PrintWriter:
					mPrintWriter.println(output_stream_area.getText());
					
					// Проверяем ошибки записи в поток при помощи метода checkError():
					if ( mPrintWriter.checkError() ) {
						JOptionPane.showMessageDialog(myFrame, "PrintWriter error!");
					}
				}
			}
		});
		
		JPanel bottom_panel = new JPanel ();
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		
		JComboBox<Charset> instream_decoding_box = new JComboBox<>(SUPPORTED_CHARSETS);
		bottom_panel.add(new JLabel("InputStreamReader decoding:  "));
		bottom_panel.add(instream_decoding_box);
		
		instream_decoding_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Создаем объект-обертку InputStreamReader над mPipedInputStream, указывая кодировку для декодирования из сырых байт в символы:
				mInputStreamReader =  
							new InputStreamReader(mPipedInputStream, instream_decoding_box.getItemAt(instream_decoding_box.getSelectedIndex()));
				// Создаем объект-обертку BufferedReader над mInputStreamReader:
				mBufferedReader = new BufferedReader(mInputStreamReader);
			}
		});
		instream_decoding_box.setSelectedItem(Charset.defaultCharset());
		instream_decoding_box.setMaximumRowCount(40);
		
		// Создаем поток чтения данных из входного потока:
		new Thread ( new Runnable() {
			@Override
			public void run() {
				String str;
				try {
					int i = 1;
					// Читаем строку из буферизированного входного потока:
					while ( (str = mBufferedReader.readLine()) != null ) {
						int skipped = 0;
						// Отбрасываем все оставшиеся символы во входном потоке (иначе выполнение может намертво зависнуть в функциях чтения/записи):
						while ( mBufferedReader.ready() ) { 
							mBufferedReader.skip(1); 
							++skipped; 
						}
						input_stream_area.append( String.format("%6d: \"%s\" (%d chars skipped after eol)\n", i++, str, skipped) );
					}
				} catch ( IOException e ) {
					JOptionPane.showMessageDialog( null, e.getClass().getName() + " : " + e.getMessage() );
				} 
			}
		} ).start();
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new PipeStreamTesting();
	}
}
