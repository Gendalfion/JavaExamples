package java_api_testing.io_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 * Демонстрация работы с классом java.io.File
 * @author Lab119Alex
 *
 */
public class FileTesting {
	// Класс java.io.File инкапсулирует в себе дескриптор на файл или дирректорию,
	// позволяя получать или управлять свойствами файлов и дирректорий в файловой системе конкретной платформы (Windows, Mac, Unix, Linux)
	File mMyFile; 
	
	public FileTesting () {
		try {
			// Создаем объект java.io.File, передавая в качестве аргумента полный путь к файлу:
			// Данная операция не создает сам файл, она лишь создает объект с информацией о заданном пути в файловой системе
			// В качестве платформо-независимого разделителя для пути к файлу рекомендуется использовать "/" (будет работать как на Windows, так и на Unix)
			// Или можно обратиться к статической переменной: File.pathSeparator, File.pathSeparatorChar
			mMyFile = new File(System.getProperty("java.io.tmpdir") + "my_temp_file.tmp");
			
			// Пробуем создать mMyFile при помощи метода createNewFile()
			// В случае успеха будет создан файл нулевой длины
			if ( !mMyFile.createNewFile() ) {
				// Используем наш временный файл в качестве блокировки для запуска нескольких копий данного приложения:
				JOptionPane.showMessageDialog(null, "Only one instance of this application can run at the moment!");
				return;
			}
			// Указываем при помощи метода deleteOnExit() маркер того, чтобы файл был автоматически удален при выходе из приложения:
			mMyFile.deleteOnExit();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getName() + " : " + e.getMessage());
		}
		
		JFrame myFrame = new JFrame ( "java.io.File Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JTextArea file_info_area = new JTextArea();
		file_info_area.setEditable(false);
		file_info_area.setFont(new Font(Font.MONOSPACED, 0, 14));
		main_panel.add(new JScrollPane(file_info_area), BorderLayout.CENTER);
		
		TitledBorder file_border = BorderFactory.createTitledBorder("Choose file or directory...");
		file_border.setTitleFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
		file_info_area.setBorder(file_border);
		
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton choose_file_btn = new JButton("Choose file or directory...");
		top_panel.add(choose_file_btn);
		
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		choose_file_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( file_chooser.showOpenDialog(myFrame) == JFileChooser.APPROVE_OPTION ) {
					file_info_area.setText( printFileInfo(file_chooser.getSelectedFile()) );
					file_border.setTitle("Information for: " + file_chooser.getSelectedFile().getAbsolutePath());
					file_info_area.repaint();
				}
			}
		});
		
		JPanel bottom_panel = new JPanel ();
		bottom_panel.add(new JLabel("Temp file created: \"" + mMyFile.getAbsolutePath() + "\""));
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}	
	
	public String printFileInfo ( File file ) {
		try ( Formatter fmt = new Formatter() ) {
			final String print_fmt = "%-25.60s: \"%s\"\n";
			
			// Выводим информацию об объекте java.io.File:
			fmt.format(print_fmt, "isDirectory()", String.valueOf(file.isDirectory()));
			fmt.format(print_fmt, "isFile()", String.valueOf(file.isFile()));
			fmt.format(print_fmt, "isAbsolute()", String.valueOf(file.isAbsolute()));
			fmt.format(print_fmt, "isHidden()", String.valueOf(file.isHidden()));
			fmt.format(print_fmt, "getPath()", String.valueOf(file.getPath()));
			fmt.format(print_fmt, "getAbsolutePath()", String.valueOf(file.getAbsolutePath()));
			fmt.format(print_fmt, "getCanonicalPath()", String.valueOf(file.getCanonicalPath()));
			fmt.format(print_fmt, "getName()", String.valueOf(file.getName()));
			fmt.format(print_fmt, "canExecute()", String.valueOf(file.canExecute()));
			fmt.format(print_fmt, "canRead()", String.valueOf(file.canRead()));
			fmt.format(print_fmt, "canWrite()", String.valueOf(file.canWrite()));
			fmt.format(print_fmt, "exists()", String.valueOf(file.exists()));
			fmt.format(print_fmt, "getFreeSpace()", String.valueOf(file.getFreeSpace()));
			fmt.format(print_fmt, "getTotalSpace()", String.valueOf(file.getTotalSpace()));
			fmt.format(print_fmt, "getUsableSpace()", String.valueOf(file.getUsableSpace()));
			fmt.format(print_fmt, "lastModified()",  new Date(file.lastModified()) );
			fmt.format(print_fmt, "length()", String.valueOf(file.length()));
			if ( file.list() != null ) {
				fmt.format(print_fmt, "list()",  String.join("; ", file.list()) );
			}
			
			return fmt.toString();
		} catch (Exception e) {
			return e.getClass().getName() + " : " + e.getMessage();
		}
	}
	
	public static void main(String[] args) {
		new FileTesting();
	}
}
