package java_api_testing.nio_api;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	
	public DirectoryWalkTesting () {
		JFrame myFrame = new JFrame ( "Text Area Template" );
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
						"[a,b,ac]*",		// ��� �����, ������������ �� ����� 'a', 'b' ��� 'ac'
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
			for ( Path path_elem : found_paths ) {
				if ( !Files.isDirectory(path_elem) ) { // �������� ����� ��������� ������ � ������:
					list_model.addElement(path_elem.getFileName().toString());
				}
			}
			return mCurrentPath.toString() + ":";
		} catch ( Exception e ) {
			return e.getClass().getName() + " : " + e.getMessage();
		}
	}

	public static void main(String[] args) {
		new DirectoryWalkTesting();
	}

}
