package java_api_testing.nio_api;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Демонстрация возможности перебора файлов в директории по средствам API java.nio.file,
 * а также демонстрация отслеживания событий в файловой системе
 * @author Alex
 *
 */
public class DirectoryWalkTesting {
	// Объект java.nio.file.Path инкапуслирует некоторый путь в заданной файловой системе
	// Все операции, которые можно проделывать с данным объектом содержатся в статическом классе java.nio.file.Files
	// Для получения объектов Path используется объект java.nio.file.FileSystem (получаемый из фабрики java.nio.file.FileSystems)
	Path mCurrentPath = null;
	
	ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
	Future<?> mWatchingFuture = null;
	
	JTextArea mWatchingLogArea = new JTextArea();
	
	{
		mWatchingLogArea.setEditable(false);
		mWatchingLogArea.setFont( new Font(Font.MONOSPACED, 0, 13) );
	}
	
	public DirectoryWalkTesting () {
		JFrame myFrame = new JFrame ( "Directory Walk and Update Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		DefaultListModel<String> files_list_model = new DefaultListModel<>();
		JList<String> files_list = new JList<>(files_list_model);
		TitledBorder files_list_border = BorderFactory.createTitledBorder("Choose directory to list its files...");
		files_list.setBorder(files_list_border);
		main_panel.add(new JScrollPane(files_list), BorderLayout.CENTER);
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton choose_directory_btn = new JButton("Choose directory...");
		top_panel.add(choose_directory_btn);
		
		JTextField file_name_filter_field = new JTextField("", 10);
		top_panel.add(new JLabel("  Input file name filter: "));
		top_panel.add(file_name_filter_field);
		file_name_filter_field.getDocument().addDocumentListener( new DocumentListener() {
			public void update () {
				files_list_border.setTitle(updateFilesInDirectory(files_list_model, file_name_filter_field.getText()));
			}
			@Override public void removeUpdate(DocumentEvent arg0) 	{ update (); }
			@Override public void insertUpdate(DocumentEvent arg0) 	{ update (); }			
			@Override public void changedUpdate(DocumentEvent arg0) { update (); }
		} );
		
		JComboBox<String> filters_box = new JComboBox<>( 
				new String[]{
				// Примеры шаблонов путей для передачи в метод java.nio.file.Files.newDirectoryStream:
						"*.txt", 			// Все файлы с расширением "txt"
						"*.{java,class}",  	// Все файлы с расширением "java" или "class"
						"[a,b,c]*",			// Все файлы, начинающиеся со строк 'a', 'b' или 'c'
						"*[a-e]",			// Все файлы, заканчивающиеся символами от 'a' до 'e'
						"*[!a-e]",			// Все файлы, заканчивающиеся любыми символами кроме 'a', 'b', ... 'e'
						"*.i??"				// Все файлы с расширением "i??", где ? - это единичный любой символ
						
				} );
		top_panel.add(new JLabel("  Filter examples: "));
		top_panel.add(filters_box);
		filters_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				file_name_filter_field.setText(filters_box.getItemAt(filters_box.getSelectedIndex()));
			}
		});
		
		JScrollPane area_scroll = new JScrollPane(mWatchingLogArea);
		mWatchingLogArea.setPreferredSize(
				new Dimension(mWatchingLogArea.getPreferredSize().width, 200));
		mWatchingLogArea.setBorder( BorderFactory.createTitledBorder("File system update monitor:") );
		JPopupMenu area_menu = new JPopupMenu();
		area_menu.add(new JMenuItem("Clear...")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mWatchingLogArea.setText("");
			}
		});
		mWatchingLogArea.setComponentPopupMenu(area_menu);
		main_panel.add(area_scroll, BorderLayout.SOUTH);
		
		JFileChooser directory_chooser = new JFileChooser();
		directory_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		choose_directory_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ( directory_chooser.showOpenDialog(myFrame) == JFileChooser.APPROVE_OPTION ) {
					mCurrentPath = directory_chooser.getSelectedFile().toPath();
					files_list_border.setTitle(updateFilesInDirectory(files_list_model, file_name_filter_field.getText()));
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public String updateFilesInDirectory (DefaultListModel<? super String> list_model, String file_filter) { 
		list_model.clear();
		if (mCurrentPath == null) {
			return "Choose directory to list its files...";
		}
		
		// При помощи статического метода java.nio.file.Files.newDirectoryStream мы можем перебрать содержимое заданной дирректории:
		try ( DirectoryStream<Path> found_paths =
				( (file_filter.isEmpty()) 
				? (Files.newDirectoryStream(mCurrentPath)) // Перебор всего содержимого
				: (Files.newDirectoryStream(mCurrentPath, file_filter)) ) // Перебор только тех путей, которые соответствуют заданному шаблону
			) 
		{
			// Прерываем выполнение предыдущей задачи по отслеживанию изменений:
			if ( mWatchingFuture != null ) {
				mWatchingFuture.cancel(true);
				try {
					mWatchingFuture.get(1000, TimeUnit.MILLISECONDS);
				} catch ( CancellationException e ) {}
			}
			
			for ( Path path_elem : found_paths ) {
				if ( !Files.isDirectory(path_elem) ) { // Помещаем имена найденных файлов в список:
					list_model.addElement(path_elem.getFileName().toString());
				}
			}
			
			// Запускаем задачу по отслеживанию изменений в текущей выбранной папке mCurrentPath:
			mWatchingFuture = mExecutorService.submit( new Runnable() {
				@Override public void run() {
					try {
						// Создаем объект WatchService для текущей файловой системы:
						WatchService watchService = FileSystems.getDefault().newWatchService();
						
						// Регистрируем сервис в объекте Watchable (им служит сам объект Path):
						mCurrentPath.register(watchService, 		 // Подписываемся на события:
								StandardWatchEventKinds.ENTRY_CREATE,// Создания файлов
								StandardWatchEventKinds.ENTRY_DELETE,// Удаления файлов
								StandardWatchEventKinds.ENTRY_MODIFY // Изменения файлов
								);
						
						while ( !Thread.interrupted() ) {
							// Метод WatchService.take() блокирует выполнение потока до тех пор,
							// пока не произойдет событие в файловой системе.
							// Информация о событии возвращается в объекте WatchKey:
							WatchKey changeKey = watchService.take();
							
							// Получаем список произошедших событий из объекта WatchKey:
							List<WatchEvent<?>> watchEvents = changeKey.pollEvents();
							
							for ( WatchEvent<?> watchEvent : watchEvents ) {
								// Объектом события WatchEvent служит объект Path:
								Path eventPath = (Path) watchEvent.context();
								
								// Вид произошедшего события мы можем узнать из метода WatchEvent.kind():
								printToWatchingArea ( watchEvent.kind().name() + ": " + eventPath.toAbsolutePath() );
							}
							
							// Важно не забыть вызов метода reset() после завершения работы с WatchKey:
							changeKey.reset();
						}
					} catch ( InterruptedException e ) {
					} catch ( Exception e ) {
						printToWatchingArea ( e.getClass().getName() + " : " + e.getMessage() );
					}
				}
			});
			
			return mCurrentPath.toString() + ":";
		} catch ( Exception e ) {
			return e.getClass().getName() + " : " + e.getMessage();
		}
	}
	
	private void printToWatchingArea ( String text ) {
		mWatchingLogArea.append( String.format("%1$tT.%1$tL %2$s\n", new java.util.Date(), text) );
		mWatchingLogArea.repaint();
	}

	public static void main(String[] args) {
		new DirectoryWalkTesting();
	}

}
