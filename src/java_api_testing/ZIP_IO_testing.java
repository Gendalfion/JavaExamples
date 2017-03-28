package java_api_testing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Formatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import my_util.FileUtil;

/**
 * Демонстрация работы с архивами ZIP при помощи пакетов Java: java.util.zip.* и java.nio.file.*
 * @author Lab119Alex
 *
 */
public class ZIP_IO_testing {
	
	private static int sChunkSize = 4096;
	
	JTextArea	mOutputLogArea = new JTextArea();
	
	{
		mOutputLogArea.setEditable(false);
		mOutputLogArea.setFont( new Font(Font.MONOSPACED, 0, 13) );
	}
	
	public ZIP_IO_testing () {
		JFrame myFrame = new JFrame ( "ZIP Input/Output API" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		main_panel.add ( new JScrollPane(mOutputLogArea), BorderLayout.CENTER );
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mOutputLogArea.setText("");
			}
		});
		mOutputLogArea.setComponentPopupMenu(clear_menu);
		mOutputLogArea.setBorder(BorderFactory.createTitledBorder("Output Log:"));
		
		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.Y_AXIS));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton compressByGZIP_btn = new JButton("Compress file by GZIPOutputStream...");
		top_panel.add(new JPanel().add(compressByGZIP_btn).getParent());
		
		JFileChooser allTypeFileChooser = new JFileChooser();
		allTypeFileChooser.setAcceptAllFileFilterUsed(true);
		compressByGZIP_btn.addActionListener( new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( allTypeFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					File in_file = allTypeFileChooser.getSelectedFile();
					File out_file = new File( in_file.getPath() + ".gz" );
					
					// Объект GZIPOutputStream - это обертка для объекта OutputStream,
					// которая позволяет сжимать данные в формате GZIP:
					try ( GZIPOutputStream gzipOutput = new GZIPOutputStream(new FileOutputStream(out_file))
						; FileInputStream fileInput = new FileInputStream(in_file) ) {
						byte[] buffer = new byte[sChunkSize];
						
						printToOutputLogArea ( String.format("\nCompressing file \"%s\" by GZIP...", in_file.getPath()));
						printToOutputLogArea ( String.format("File size before compression: %d", in_file.length()));
						
						int length = 0;
						while ( (length = fileInput.read(buffer, 0, sChunkSize)) != -1 ) {
							// Направляем данные из входного (непожатого) файла в выходной (сжатый по GZIP) файловый поток:
							gzipOutput.write(buffer, 0, length);
						}
					} catch ( Exception exception ) {
						printToOutputLogArea ( exception.getClass().getName() + " : " + exception.getMessage() );
					}
					printToOutputLogArea ( String.format("File size after GZIP compression: %d", out_file.length()));
				}
			}
		});
		
		JButton decompressFromGZIP_btn = new JButton("Decompress file by GZIPInputStream...");
		top_panel.add(new JPanel().add(decompressFromGZIP_btn).getParent());
		
		JFileChooser gzipFileChooser = new JFileChooser();
		gzipFileChooser.setAcceptAllFileFilterUsed(false);
		gzipFileChooser.setFileFilter( new FileNameExtensionFilter("GZIP-files *.gz", "gz") );
		decompressFromGZIP_btn.addActionListener( new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( gzipFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					File in_file = gzipFileChooser.getSelectedFile();
					File out_file = new File( in_file.getPath().substring(0, in_file.getPath().length() - 3) );
					
					allTypeFileChooser.setSelectedFile(out_file);
					if ( allTypeFileChooser.showSaveDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
						out_file = allTypeFileChooser.getSelectedFile();
						
						// Объект GZIPInputStream - это обертка для объекта InputStream,
						// которая позволяет производить декомпрессию потоковых данных, упакованных при помощи алгоритма GZIP:
						try ( GZIPInputStream gzipInput = new GZIPInputStream(new FileInputStream(in_file))
							; FileOutputStream fileOutput = new FileOutputStream(out_file) ) {
							byte[] buffer = new byte[sChunkSize];
							
							printToOutputLogArea ( String.format("\nDecompressing file \"%s\" from GZIP...", in_file.getPath()) );
							printToOutputLogArea ( String.format("File size before decompression: %d", in_file.length()) );
							
							int length = 0;
							while ( (length = gzipInput.read(buffer, 0, sChunkSize)) != -1 ) {
								// Направляем данные из входного (сжатого по GZIP) файла в выходной (непожатый) файловый поток:
								fileOutput.write(buffer, 0, length);
							}
						} catch ( Exception exception ) {
							printToOutputLogArea ( exception.getClass().getName() + " : " + exception.getMessage() );
						}
						printToOutputLogArea ( String.format("File size after decompression from GZIP: %d", out_file.length()));
					}
				}
			}
		});
		
		JButton compressByZIP_btn = new JButton("Compress files by ZipOutputStream...");
		top_panel.add(new JPanel().add(compressByZIP_btn).getParent());
		
		JFileChooser allTypeMultyFileChooser = new JFileChooser();
		allTypeMultyFileChooser.setAcceptAllFileFilterUsed(true);
		allTypeMultyFileChooser.setMultiSelectionEnabled(true);
		
		JFileChooser zipFileChooser = new JFileChooser();
		zipFileChooser.setAcceptAllFileFilterUsed(false);
		zipFileChooser.setFileFilter( new FileNameExtensionFilter("ZIP-files *.zip", "zip") );
		compressByZIP_btn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( allTypeMultyFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					File [] in_files = allTypeMultyFileChooser.getSelectedFiles();
					
					zipFileChooser.setCurrentDirectory( allTypeMultyFileChooser.getCurrentDirectory() );
					if ( zipFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
						File out_file =  FileUtil.getSelectedFileWithExtension(zipFileChooser);
						
						printToOutputLogArea ( String.format("\nCreating ZIP archive \"%s\" from files:", out_file.getPath()) );
						int total_input_file_length = 0;
						// Объект ZipOutputStream позволяет выводить потоковые сжатые данные в формате ZIP
						// В отличие от GZIPOutputStream данный объект позволяет организовывать данные
						// в архиве в виде нескольких файлов:
						try ( ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(out_file)) ) {
							byte[] buffer = new byte[sChunkSize];
							
							for ( File next_in_file : in_files ) {
								printToOutputLogArea ( String.format("\"%s\", length = %d...", next_in_file.getPath(), next_in_file.length()) );
								total_input_file_length += next_in_file.length();
								
								// Создаем элемент ZIP-архива при помощи объекта ZipEntry:
								ZipEntry zipEntry = new ZipEntry(next_in_file.getName());
								// Метод ZipOutputStream.putNextEntry(...) используется для добавления нового ZipEntry в архив:
								zipOutput.putNextEntry(zipEntry);
								try ( FileInputStream fileInput = new FileInputStream(next_in_file) ) {
									int length = 0;
									while ( (length = fileInput.read(buffer, 0, sChunkSize)) != -1 ) {
										// Данные, записанные в поток ZipOutputStream, будут прикреплены к текущему установленному ZipEntry:
										zipOutput.write(buffer, 0, length);
									}
								}
							}
						} catch ( Exception exception ) {
							printToOutputLogArea ( exception.getClass().getName() + " : " + exception.getMessage() );
						}
						printToOutputLogArea ( String.format("Total file's size before compression: %d (after: %d)..."
								, total_input_file_length, out_file.length()) );
					}
				}
			}
		});
		
		JButton openByZFP_btn = new JButton("Open ZIP-archive by Zip Filesystem Provider...");
		top_panel.add(new JPanel().add(openByZFP_btn).getParent());
		
		openByZFP_btn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( zipFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					Path pathToZIP = zipFileChooser.getSelectedFile().toPath();
					
					// Пакет java.nio.file предоставляет возможность унифицированного доступа к файловым системам разной природы.
					// При помощи метода FileSystems.newFileSystem создадим файловую систему на основе ZIP-архива:
					try ( FileSystem zipFS = FileSystems.newFileSystem(pathToZIP, null) ) {
						// В данном случае java сама определяет подходящий провайдер файловой системы, более подробно об этом
						// можно прочитать: https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html
						try ( Formatter fmt = new Formatter() ) {
							fmt.format( "\nContent of ZIP-filesystem \"%s\":", pathToZIP.toString() );
							// Выводим содержимое файловой системы при помощи метода Files.walkFileTree:
							Files.walkFileTree( zipFS.getPath("/") , new SimpleFileVisitor<Path> () {
								@Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws java.io.IOException {
									if ( attrs.isDirectory() ) {
										fmt.format( "\nDIRECTORY: \"%s\"", file.toString() );
									} else {
										fmt.format( "\nFILE: \"%s\", SIZE = %d", file.toString(), attrs.size() );
									}
									return FileVisitResult.CONTINUE;
								};
							});
							printToOutputLogArea( fmt.toString() );
						}
						
					} catch ( Exception exception ) {
						printToOutputLogArea ( exception.getClass().getName() + " : " + exception.getMessage() );
					}
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	private void printToOutputLogArea ( String text ) {
		mOutputLogArea.append( String.format("%1$tT.%1$tL %2$s\n", new java.util.Date(), text) );
	}

	public static void main(String[] args) {
		new ZIP_IO_testing();
	}
}
