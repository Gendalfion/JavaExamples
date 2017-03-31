package java_api_testing.nio_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Демонстрация работы с файлами через пакет java.nio.* с использованием прямого буфера
 * @author Lab119Alex
 *
 */
public class FileMappingTesting implements ListSelectionListener, ActionListener {
	Thread mFileChannelThread = null;
	MappedByteBuffer mMappedBuffer = null;
	
	JList<AddressRange> mRangeList = new JList<>();
	List<AddressRange> mRangeListContent = new Vector<>();
	AbstractListModel<AddressRange> mRangeListModel = new AbstractListModel<AddressRange>() {
		private static final long serialVersionUID = 1L;

		@Override public int getSize() {
			return mRangeListContent.size();
		}

		@Override public AddressRange getElementAt(int index) {
			return mRangeListContent.get(index);
		}
	};
	
	JTextArea mFileContentArea = new JTextArea();
	JTextArea mFileDecodingArea = new JTextArea();
	
	JComboBox<String> mEncodingComboBox = new JComboBox<>( Charset.availableCharsets().keySet().toArray(new String [0]) );
	
	TitledBorder mRangeListBorder = BorderFactory.createTitledBorder("Open file to view it's content...");
	
	{
		Font outFont = new Font(Font.MONOSPACED, Font.BOLD, 13);
		
		mRangeList.setModel(mRangeListModel);
		mRangeList.setFont(outFont);
		mRangeList.addListSelectionListener(this);
		
		mFileContentArea.setEditable(false);
		mFileContentArea.setFont(outFont);
		
		mFileDecodingArea.setEditable(false);
		mFileDecodingArea.setFont(outFont);
		
		mEncodingComboBox.addActionListener(this);
	}
	
	public FileMappingTesting () {
		JFrame myFrame = new JFrame ( "java.nio.* File Channel and Mapped Buffer Testing" );
		myFrame.setSize(1050, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel( new GridLayout(0, 1) );
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel tmp_panel = new JPanel(new GridLayout(0, 1));
		tmp_panel.setBorder(mRangeListBorder);
		tmp_panel.add(new JScrollPane(mRangeList));
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel(new GridLayout(0, 2));
		tmp_panel.setBorder( BorderFactory.createTitledBorder("File content:") );
		tmp_panel.add(new JScrollPane(mFileContentArea));
		tmp_panel.add(new JScrollPane(mFileDecodingArea));
		center_panel.add(tmp_panel);
		
		JPanel top_panel = new JPanel ();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton openFileBtn = new JButton("Open file...");
		top_panel.add(new JPanel().add(openFileBtn).getParent());
		
		top_panel.add( new JLabel("        Choose text encoding: ") );
		
		top_panel.add(new JPanel().add(mEncodingComboBox).getParent());
		mEncodingComboBox.setSelectedItem( Charset.defaultCharset().name() );
		mEncodingComboBox.setMaximumRowCount(35);
		
		JFileChooser allTypeFileChooser = new JFileChooser();
		allTypeFileChooser.setAcceptAllFileFilterUsed(true);
		openFileBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( allTypeFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					if ( mFileChannelThread != null ) {
						// Прерываем текущий поток работы с файловым каналом (если он был запущен к текущему моменту):
						mFileChannelThread.interrupt();
						try { mFileChannelThread.join(1000); } catch ( InterruptedException ex ) { return; }
					}
					
					// Получаем путь к файлу, который выбрал пользователь, в дефолтной файловой системе ОС:
					final Path filePath = allTypeFileChooser.getSelectedFile().toPath();
					
					// Начинаем работу с файловым каналом в отдельном потоке:
					mFileChannelThread = new Thread() {
						@Override public void run() {
							// Для работы с данными внутри файлов в пакете java.nio используются каналы и буферы.
							// В дополнение к потокам java.io каналы могут работать в асинхронном режиме,
							// могут корректно прерываться по сигналу interrupt для своего потока выполнения, а также
							// имеется поддержка прямых буферов:
							try ( FileChannel fc = FileChannel.open(filePath, StandardOpenOption.READ) ) {
								final long file_size = fc.size();
								
								// Создаем прямой буфер на все содержимое открытого канала (файла на диске).
								// Прямой буфер не имеет реальных данных в оперативной памяти, однако
								// при обращении к реальным данным на физическом устройстве, копия этих данных
								// перемещатся по месту востребования средствами прямой подкачки ОС
								// (что может быть очень эффективно при индексном доступе к файлу большого размера):
								mMappedBuffer = fc.map( FileChannel.MapMode.READ_ONLY, 0, file_size );
								
								SwingUtilities.invokeLater(new Runnable() {
									@Override public void run() {
										// Обновляем список для выбора диапазона отображаемых адресов внутри файла:
										mRangeListBorder.setTitle( String.format("%s | size = %s bytes", filePath, String.valueOf(file_size)) );
										
										final long page_size = 256;
										
										long list_size = file_size / page_size + ( ((file_size % page_size) > 0) ? 1 : 0 );
										mRangeListContent = new Vector<>( (int)list_size );
										
										long current_addr = 0;
										while ( current_addr < file_size ) {
											long next_addr = (current_addr + page_size >= file_size) ? ( file_size - 1 ) : (current_addr + page_size - 1);
											mRangeListContent.add ( new AddressRange(current_addr, next_addr) );
											current_addr = next_addr + 1;
										}
										mRangeList.setModel( new AbstractListModel<AddressRange>() {
											private static final long serialVersionUID = 1L;

											@Override public int getSize() {
												return mRangeListContent.size();
											}

											@Override public AddressRange getElementAt(int index) {
												return mRangeListContent.get(index);
											}
										} );
										
										if ( mRangeListContent.size() > 0 ) {
											mRangeList.setSelectedIndex(0);
										}
										
										center_panel.repaint();
									}
								});
								
								while ( !Thread.interrupted() ) {
									Thread.sleep(1000);
								}
							} catch ( InterruptedException ie ) {
							} catch ( Exception exception ) {
								exception.printStackTrace();
								JOptionPane.showMessageDialog( main_panel, exception.getClass().getName() + " : " + exception.getMessage() );
							}
							
						};
					};
					
					mFileChannelThread.start();
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	protected void updateView () {
		AddressRange sel_item = mRangeList.getSelectedValue();
		if ( sel_item == null ) { mFileContentArea.setText(""); mFileDecodingArea.setText(""); return; }
		
		// Обновляем отображение содержимого файла по выбранным адресам:
		try ( Formatter fmt = new Formatter() ) {
			int cur_pos = sel_item.getStartPosition();
			int last_pos = sel_item.getLastPosition();
			
			// Для работы с данными в пакете java.nio используются производные от класса Buffer.
			// Этот класс представляет собой хранилище для данных подобно массиву, и как следствие обладает характеристикой:
			// 	вместимость (capacity) - размер данных, который может вместить в себя буфер (размер массива в байтах)
			//		- для прямого буфера вместимость определяет размер блока данных на физическом устройстве, 
			//		  на который отоборажен данный буфер;
			// В дополнение к функциональности массива буфер имеет следующие характеристики:
			// 	позиция (position) - индекс очередного элемента, который будет прочитан или записан в буфер
			//		(операции чтения/записи данных в буфер зависят от и влияют на текущую позицию в буфере)
			//	граница (limit) - ограничение, в рамках которого происходят операции чтения/записи с буфером;
			//	метка (mark) - помеченный индекс в буфере, который может быть использован в дальнейшей работе;
			// В процессе работы с буфером всегда выполняется следующее условие:
			// 			mark <= position <= limit <= capacity
			
			// Устанавливаем текущую позицию в прямом буфере на начальный адрес выбранного блока данных:
			mMappedBuffer.position( cur_pos );
			// Помечаем данную позицию (будем использовать эту метку чуть позже):
			mMappedBuffer.mark();
			// Устанавливаем верхнюю границу выбранного диапазона адресов:
			mMappedBuffer.limit( sel_item.getLastPosition() + 1 );
			
			int byte_in_row_index = 0;
			while (cur_pos++ <= last_pos) {
				if ( byte_in_row_index++ == 0 ) {
					fmt.format( "%010X: ", cur_pos - 1 );
				}
				// Побайтово читаем данные из буфера при помощи метода Buffer.get().
				// Данный метод читает байт начиная с текущей позиции и увеличивает позицию в буфере на 1:
				fmt.format("%02X ", mMappedBuffer.get());
				
				if ( byte_in_row_index == 8 ) {
					fmt.format( "| " );
				} else 
				if ( byte_in_row_index >= 16 ) {
					fmt.format( "\n" );
					byte_in_row_index = 0;
				}
			}
			
			mFileContentArea.setText( fmt.toString() );
			
			// Сбрасываем текущую позицию в буфере к ранее установленной метке:
			mMappedBuffer.reset();
			
			String curr_charset_name = (String) mEncodingComboBox.getSelectedItem();
			
			// Объект CharsetDecoder из пакета java.nio.charset.* используется для декодирования текстовых данных из буферов:
			CharsetDecoder decoder = Charset.forName( curr_charset_name ).newDecoder();
			try {
				// Декодируем текст из mMappedBuffer (будет использован участок буфера от текущей позиции до границы не включительно):
				CharBuffer decoded_text = decoder.decode(mMappedBuffer);
				mFileDecodingArea.setText( decoded_text.toString() );
			} catch (CharacterCodingException codingException) {
				mFileDecodingArea.setText(String.format("Can-not decode file in \"%s\" charset...", curr_charset_name));
			}
			
			// Сбрасываем текущую границу, метку буфера и устанавливаем позицию в 0 при помощи метода Buffer.clear()
			// (содержимое буфера данный метод никак не затрагивает):
			mMappedBuffer.clear();
		} catch ( Exception exception ) {
			exception.printStackTrace();
			JOptionPane.showMessageDialog( null, exception.getClass().getName() + " : " + exception.getMessage() );
		}
	}
	
	@Override public void valueChanged(ListSelectionEvent e) {
		updateView ();
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		updateView ();
	}
	
	private class AddressRange {
		private long mMin = 0, mMax = 0;
		
		public AddressRange ( long min, long max ) { mMin = min; mMax = max; }
		public int getStartPosition () { return (int)mMin; }
		public int getLastPosition () { return (int)mMax; }
		@Override public String toString() { return String.format("%010X - %010X", mMin, mMax); }
	}

	public static void main(String[] args) {
		new FileMappingTesting ();
	}		
}

