package java_api_testing.net_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/** 
 * ƒемонстраци€ отправки HTTP GET-запроса на сервер Google при помощи
 * классов java.net.URL и java.net.HttpURLConnection
 * 
 * @author Lab119Alex
 *
 */
public class HttpGET_Testing {
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public HttpGET_Testing () {
		JFrame myFrame = new JFrame ( "java.net.* HTTP GET Request Testing" );
		myFrame.setSize(1000, 600);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmp_panel = new JPanel(new BorderLayout());
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		tmp_panel.add(new JLabel("Google search: "), BorderLayout.WEST);
		JTextField searchField = new JTextField("HTTP GET request");
		searchField.setFont ( searchField.getFont().deriveFont(15.0f) );
		tmp_panel.add(searchField, BorderLayout.CENTER);
		
		JButton goBtn = new JButton("GO...");
		tmp_panel.add(goBtn, BorderLayout.EAST);
		
		JPanel center_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel response_info_panel = new JPanel (new GridLayout(0, 1));
		response_info_panel.setBorder(BorderFactory.createTitledBorder("HTTP Response Info:"));
		
		JTextArea responseInfoArea = new JTextArea(0, 45);
		responseInfoArea.setEditable(false);
		responseInfoArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		response_info_panel.add(new JScrollPane(responseInfoArea));
		
		JPanel url_content_panel = new JPanel (new GridLayout(0, 1));
		url_content_panel.setBorder(BorderFactory.createTitledBorder("URL Content:"));
		
		center_panel.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, response_info_panel, url_content_panel));
		
		JTextArea contentArea = new JTextArea();
		contentArea.setLineWrap(true);
		contentArea.setEditable(false);
		contentArea.setFont(new Font(Font.MONOSPACED, 0, 12));	
		
		
		goBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					// ‘ормируем URL дл€ HTTP-запроса
					// (параметры запроса передаютс€ после символа ? и раздел€ютс€ &):
					java.net.URL url = new java.net.URL (
							// ƒл€ того, чтобы сервер смог декодировать наш запрос, используем
							// статический метод URLEncoder.encode(...), который кодирует заданный текст
							// в соответствии с правилами кодировани€ в стандарте URL:
							String.format("http://www.google.com/search?q=%s", URLEncoder.encode(searchField.getText(), "UTF-8"/*кодировка сервера Google*/)) 
							);
					
					responseInfoArea.setText(""); responseInfoArea.repaint();
					url_content_panel.removeAll(); url_content_panel.repaint();
					goBtn.setText("Loading..."); goBtn.setEnabled(false);
					
					new Thread ( new Runnable() {
						@Override public void run() {
							try {
								responseInfoArea.append( String.format("\tURL request:\n%s\n\n", url) );
								
								// ѕриводим тип вызова метода URL.openConnection() к HttpURLConnection
								// (т. к. мы точно знаем тип протокола в нашем URL-запросе):
								HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
								
								// ”станавливаем тип HTTP-запроса (что не об€зательно, т. к. GET-запрос выполн€етс€ по дефолту):
								httpConnection.setRequestMethod("GET");
								// ѕри помощи метода HttpURLConnection.setRequestProperty(...) мы можем устанавливать 
								// пол€ в заголовке HTTP-запроса.
								// ”становим поле "User-Agent" (т. к. Google возвращает ошибку, если данного пол€ нет в заголовке):
								httpConnection.setRequestProperty("User-Agent", USER_AGENT);
								
								// ¬ыполн€ем передачу сформированного нами запроса на сервер и выводим
								// параметры полученного ответа:
								final String pattern = "%-21.100s: ";
								int responseCode = httpConnection.getResponseCode();
								responseInfoArea.append("\tHttpURLConnection Response Info:\n");
								responseInfoArea.append( String.format(pattern + "%d\n", "getResponseCode()", responseCode) );
								responseInfoArea.append( String.format(pattern + "%s\n", "getResponseMessage()", httpConnection.getResponseMessage()) );
								responseInfoArea.append( String.format(pattern + "%d\n", "getContentLength()", httpConnection.getContentLength()) );
								responseInfoArea.append( String.format(pattern + "%d\n", "getContentLengthLong()", httpConnection.getContentLengthLong()) );
								
								// ѕечатаем содержимое HTTP-заголовка полученного в ответном сообщении
								// при помощи метода HttpURLConnection.getHeaderFields():
								responseInfoArea.append("\n\tHttpURLConnection.getHeaderFields():\n");
								for ( Map.Entry<String, List<String>> map_entry : httpConnection.getHeaderFields().entrySet() ) {
									responseInfoArea.append( String.format(pattern, map_entry.getKey()) );
									
									List<String> fieldVals = map_entry.getValue();
									for ( String fieldVal : fieldVals ) {
										responseInfoArea.append( String.format("%s\n", fieldVal) );
										if ( fieldVals.size() > 1 ) {
											responseInfoArea.append( String.format(pattern, "") );
										}
									}
								}
								
								// ¬ычлен€ем строковое представление кодировки содержимого в ответном сообщении:
								Matcher matcher = Pattern.compile("(?<=charset=)((\\w+-?)+)").matcher(httpConnection.getContentType());
								String charset = ( matcher.find() ) ? (matcher.group(1)) : (Charset.defaultCharset().name());
								
								responseInfoArea.append( String.format("\nUsing \"%s\" charset for content decoding...", charset) );
								try ( BufferedReader in = new BufferedReader(
								        new InputStreamReader(httpConnection.getInputStream(), charset)) ) {
									StringBuilder urlContentSB = new StringBuilder();
	
									// ѕострочно считываем содержимое пол€ данных, отправленных вместе с HTTP-ответом:
									String inputLine;
									while ((inputLine = in.readLine()) != null) {
										urlContentSB.append(inputLine + "\n");
									}
									
									contentArea.setText(urlContentSB.toString());
									url_content_panel.add(new JScrollPane(contentArea));
								}
								
							} catch ( Exception exception ) {
								JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								goBtn.setText("GO...");
								goBtn.setEnabled(true);
							}
						}
					} ).start();
				} catch (MalformedURLException | UnsupportedEncodingException exception) {
					JOptionPane.showMessageDialog(main_panel, "Failed to form URL: " + exception, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new HttpGET_Testing ();
	}

}
