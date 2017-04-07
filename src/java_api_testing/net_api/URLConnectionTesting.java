package java_api_testing.net_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

/**
 * ������������ ������������ ������� java.net.URL � java.net.URLConnection ��
 * �������� URL-������ � ������� � �������� �� ��������� URL
 * 
 * @author Lab119Alex
 *
 */
public class URLConnectionTesting {
	
	private String [] PREDEFINED_URLS = {
		// ������� URL ��� ������� � ��������� �������� �� ��������� ����������:
		// (Java ������������ ��������� ���� ����������: HTTP, HTTPS, FTP, FILE, JAR)
		"http://www.webseoanalytics.com/blog/wp-content/uploads/2011/12/transfer-web-hosting-checklist.jpg",
		"http://www.programcreek.com/simple-java/",
		"http://www.nch.com.au/acm/8kulaw.wav",
		"https://cache-mskstoredata08.cdn.yandex.net/kp.cdn.yandex.net/3498/kinopoisk.ru-Lord-Rings-The-Return-King-The-62553.mp4",
		"https://www.google.ru/",
		"ftp://93.100.125.120/VirtualBox.jpg",
		"file:///C:/",
		"jar:file:/D:/users/Amvrosov_Alex/Eclipse_wsp/ISTP/executables/ISTP_sso.jar!/graphics/check.png"
	};

	public URLConnectionTesting () {
		JFrame myFrame = new JFrame ( "java.net.URL API Testing" );
		myFrame.setSize(1000, 600);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmp_panel = new JPanel(new BorderLayout());
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		tmp_panel.add(new JLabel("Input URL: "), BorderLayout.WEST);
		JTextField urlField = new JTextField("https://www.google.ru/");
		urlField.setFont ( urlField.getFont().deriveFont(15.0f) );
		tmp_panel.add(urlField, BorderLayout.CENTER);
		
		JButton loadURLBtn = new JButton("Load URL Content...");
		tmp_panel.add(loadURLBtn, BorderLayout.EAST);
		
		tmp_panel = new JPanel(new BorderLayout());
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		tmp_panel.add(new JLabel("Choose predefined URL: "), BorderLayout.WEST);
		JComboBox<String> urlComboBox = new JComboBox<>(PREDEFINED_URLS);
		urlComboBox.setSelectedIndex(-1);
		tmp_panel.add(urlComboBox, BorderLayout.CENTER);
		urlComboBox.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				urlField.setText( urlComboBox.getItemAt(urlComboBox.getSelectedIndex()) );
			}
		});
		
		JPanel config_panel = new JPanel(new BorderLayout());
		top_panel.add(config_panel);
		config_panel.add(new JLabel("URL Connection Configuration:"), BorderLayout.WEST);
		config_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		tmp_panel = new JPanel();
		config_panel.add(tmp_panel, BorderLayout.CENTER);
		
		tmp_panel.add(new JLabel("Connection timeout: "));
		JTextField connectionTimeoutField = new JTextField("1000", 8);
		tmp_panel.add(connectionTimeoutField);
		
		tmp_panel.add(new JLabel("  Read timeout: "));
		JTextField readTimeoutField = new JTextField("1000", 8);
		tmp_panel.add(readTimeoutField);
		
		JPanel center_panel = new JPanel (new BorderLayout());
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		tmp_panel = new JPanel(new GridLayout(0, 1));
		tmp_panel.setBorder(BorderFactory.createTitledBorder("URL Info:"));
		
		JTextArea urlInfoArea = new JTextArea(0, 45);
		urlInfoArea.setEditable(false);
		urlInfoArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		tmp_panel.add(new JScrollPane(urlInfoArea));
		
		JPanel content_panel = new JPanel(new GridLayout(0, 1));
		content_panel.setBorder(BorderFactory.createTitledBorder("URL Content:"));
		
		center_panel.add( new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tmp_panel, content_panel), BorderLayout.CENTER);
		
		JTextArea contentArea = new JTextArea();
		contentArea.setEditable(false);
		contentArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		
		loadURLBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					// ������� ������ java.net.URL �� ���������� ������������� URL.
					// URL ����� ��������� ������: protocol:[//[user:password@]host[:port]][/]path[?query][#fragment]
					// ���� ���������� � ����������� ������ �� ������������� ������� URL, ��
					// ������������ ���������� MalformedURLException:
					java.net.URL url = new java.net.URL (urlField.getText());
					
					// �������� �������� ������� java.net.URL, ����������� �� ���������� URL-������:
					urlInfoArea.setText("");
					final String pattern = "%-21.100s: ";
					urlInfoArea.append( "java.net.URL Info:\n" );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getHost()" , 		url.getHost()) );
					urlInfoArea.append( String.format(pattern + "%d\n", "URL.getPort()", 		url.getPort()) );
					urlInfoArea.append( String.format(pattern + "%d\n", "URL.getDefaultPort()", url.getDefaultPort()) );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getProtocol()", 	url.getProtocol()) );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getPath()", 		url.getPath()) );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getQuery()", 		url.getQuery()) );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getFile()", 		url.getFile()) );
					urlInfoArea.append( String.format(pattern + "%s\n", "URL.getAuthority()", 	url.getAuthority()) );
					
					// ����� URL.openConnection() ������� ������, ����������� �� URLConnection, ��� ������ ��������
					// ����� ���������� ���������� � ��������, ���������� ������ URL. �������� ���
					// ������� URLConnection ������� �� ���������, ���������� � ������ ������
					// (��������� �������� ���������� ������ ����� �� ����������):
					URLConnection urlConnection = url.openConnection();
					
					// ������������� �������� URL-����������, ������� ����� ����������� ���
					// ������� ��������� ������� � ����������� URL-������:
					urlConnection.setConnectTimeout	( getTextAsInt(connectionTimeoutField, 1000) );
					urlConnection.setReadTimeout	( getTextAsInt(readTimeoutField, 1000) );
					
					content_panel.removeAll();
					content_panel.repaint();
					loadURLBtn.setText("Loading URL Content...");
					loadURLBtn.setEnabled(false);
					new Thread( new Runnable() {
						@Override public void run() {
							try {
								// �������� �������� ������� URLConnection (�� ����� ������ ������ URLConnection.getContentType()
								// ������ URLConnection ������������� �������� ���������� � URL-��������. ��� ����������� �������
								// ������������ ���� �� ��������� ������� � ��������� ������(���� ��������� ��������), ����
								// �� ���������� ����� ����� ������� � URL-������):
								urlInfoArea.append("\njava.net.URLConnection Info:\n");
								urlInfoArea.append( String.format(pattern + "%s\n", "URLConnection type:", urlConnection.getClass().getSimpleName()) );
								// �������� ��������� ������������� ���� ������� (������� �������� ��������� MIME):
								String contentType = urlConnection.getContentType();
								urlInfoArea.append( String.format(pattern + "%s\n", "URLConnection.getContentType()", contentType) );
								urlInfoArea.append( String.format(pattern + "%s\n", "URLConnection.getContentEncoding()", urlConnection.getContentEncoding()) );
								
								// ����� URLConnection.getContent() ���������� ������, ��������� � ��������.
								// ���������� ������ ��������� � ����������� �� ���� ������� (�����, ��������, �����, �����),
								// ������ �������� � ���, ��� ��� ���������, ��������������� ��� ������� � �������� ������������
								// (Java ���������� ����������� �� ������ com.sun.*, ��� ��������� ��� ������������ Java API):
								Object content = urlConnection.getContent();
								urlInfoArea.append( String.format(pattern + "%s\n", "URLConnection.getContent() class type", content.getClass().getName()) );
								
								// ����������� MIME-��� URL-��������:
								contentType = contentType.toUpperCase();
								if ( contentType.startsWith("IMAGE") ) {
									// ��������� �������� �� ���������� URL:
									ImageIcon image = new ImageIcon(url);
									content_panel.add( new JLabel(image) );
								} else 
								if ( contentType.startsWith("TEXT") ) {
									// ��������� ��������� ������:
									int linesToDisplay = 100;
									// ����� URLConnection.getInputStream() ���������� ������� ����� InputStream,
									// � ������� �������� ����� ������� ������ � URL-������� � ����� ����:
									try ( BufferedReader reader = new BufferedReader( new InputStreamReader(urlConnection.getInputStream()) );
										  Formatter fmt = new Formatter() ) {
										while ( linesToDisplay > 0 ) {
											// ������ linesToDisplay ����� ������ �� URL-������� (��� ������������� ������ 
											// ���������� ���������, �������� ��-��������� � ������� �������):
											String nextLine = reader.readLine();
											if ( nextLine != null ) {
												fmt.format("%s\n", nextLine);
												--linesToDisplay;
											} else {
												linesToDisplay = -1;
											}
										}
										if ( linesToDisplay == 0 ) {
											fmt.format("\n  File is too big to display...");
										}
										contentArea.setText(fmt.toString());
										content_panel.add( new JScrollPane(contentArea) );
									}
								} else {
									contentArea.setText("Can not display URL content...");
									content_panel.add( new JScrollPane(contentArea) );
								}
							} catch ( Exception exception ) {
								JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								loadURLBtn.setText("Load URL Content...");
								loadURLBtn.setEnabled(true);
							}
						}
					} ).start();
				} catch ( Exception exception ) {
					JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	private int getTextAsInt ( JTextComponent textComponent, int defaultValue ) {
		try {
			return Integer.valueOf( textComponent.getText() );
		} catch ( Exception e ) {}
		return defaultValue;
	}
	
	public static void main(String[] args) {
		new URLConnectionTesting ();
	}
}
