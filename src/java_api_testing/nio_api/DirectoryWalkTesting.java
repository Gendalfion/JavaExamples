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
 * ������������ ����������� �������� ������ � ���������� �� ��������� API java.nio.file,
 * � ����� ������������ ������������ ������� � �������� �������
 * @author Alex
 *
 */
public class DirectoryWalkTesting {
	// ������ java.nio.file.Path ������������� ��������� ���� � �������� �������� �������
	// ��� ��������, ������� ����� ����������� � ������ �������� ���������� � ����������� ������ java.nio.file.Files
	// ��� ��������� �������� Path ������������ ������ java.nio.file.FileSystem (���������� �� ������� java.nio.file.FileSystems)
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
				// ������� �������� ����� ��� �������� � ����� java.nio.file.Files.newDirectoryStream:
						"*.txt", 			// ��� ����� � ����������� "txt"
						"*.{java,class}",  	// ��� ����� � ����������� "java" ��� "class"
						"[a,b,c]*",			// ��� �����, ������������ �� ����� 'a', 'b' ��� 'c'
						"*[a-e]",			// ��� �����, ��������������� ��������� �� 'a' �� 'e'
						"*[!a-e]",			// ��� �����, ��������������� ������ ��������� ����� 'a', 'b', ... 'e'
						"*.i??"				// ��� ����� � ����������� "i??", ��� ? - ��� ��������� ����� ������
						
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
		
		// ��� ������ ������������ ������ java.nio.file.Files.newDirectoryStream �� ����� ��������� ���������� �������� �����������:
		try ( DirectoryStream<Path> found_paths =
				( (file_filter.isEmpty()) 
				? (Files.newDirectoryStream(mCurrentPath)) // ������� ����� �����������
				: (Files.newDirectoryStream(mCurrentPath, file_filter)) ) // ������� ������ ��� �����, ������� ������������� ��������� �������
			) 
		{
			// ��������� ���������� ���������� ������ �� ������������ ���������:
			if ( mWatchingFuture != null ) {
				mWatchingFuture.cancel(true);
				try {
					mWatchingFuture.get(1000, TimeUnit.MILLISECONDS);
				} catch ( CancellationException e ) {}
			}
			
			for ( Path path_elem : found_paths ) {
				if ( !Files.isDirectory(path_elem) ) { // �������� ����� ��������� ������ � ������:
					list_model.addElement(path_elem.getFileName().toString());
				}
			}
			
			// ��������� ������ �� ������������ ��������� � ������� ��������� ����� mCurrentPath:
			mWatchingFuture = mExecutorService.submit( new Runnable() {
				@Override public void run() {
					try {
						// ������� ������ WatchService ��� ������� �������� �������:
						WatchService watchService = FileSystems.getDefault().newWatchService();
						
						// ������������ ������ � ������� Watchable (�� ������ ��� ������ Path):
						mCurrentPath.register(watchService, 		 // ������������� �� �������:
								StandardWatchEventKinds.ENTRY_CREATE,// �������� ������
								StandardWatchEventKinds.ENTRY_DELETE,// �������� ������
								StandardWatchEventKinds.ENTRY_MODIFY // ��������� ������
								);
						
						while ( !Thread.interrupted() ) {
							// ����� WatchService.take() ��������� ���������� ������ �� ��� ���,
							// ���� �� ���������� ������� � �������� �������.
							// ���������� � ������� ������������ � ������� WatchKey:
							WatchKey changeKey = watchService.take();
							
							// �������� ������ ������������ ������� �� ������� WatchKey:
							List<WatchEvent<?>> watchEvents = changeKey.pollEvents();
							
							for ( WatchEvent<?> watchEvent : watchEvents ) {
								// �������� ������� WatchEvent ������ ������ Path:
								Path eventPath = (Path) watchEvent.context();
								
								// ��� ������������� ������� �� ����� ������ �� ������ WatchEvent.kind():
								printToWatchingArea ( watchEvent.kind().name() + ": " + eventPath.toAbsolutePath() );
							}
							
							// ����� �� ������ ����� ������ reset() ����� ���������� ������ � WatchKey:
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
