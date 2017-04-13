package java_api_testing.net_api;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Formatter;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/** 
 * ƒемонстраци€ отправки HTTP POST-запроса при помощи
 * классов java.net.URL и java.net.HttpURLConnection
 * 
 * @author Lab119Alex
 *
 */
public class HttpPOST_Testing {
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public HttpPOST_Testing () {
		JFrame myFrame = new JFrame ( "java.net.* HTTP POST Request Testing" );
		myFrame.setSize(1000, 600);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmp_panel = new JPanel(new BorderLayout());
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		tmp_panel.add(new JLabel("Web Service URL: "), BorderLayout.WEST);
		JTextField webServiceField = new JTextField("http://httpbin.org/post");
		webServiceField.setFont ( webServiceField.getFont().deriveFont(15.0f) );
		webServiceField.setForeground(Color.GRAY);
		tmp_panel.add(webServiceField, BorderLayout.CENTER);
		
		JPanel center_panel = new JPanel (new BorderLayout());
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel form_panel = new JPanel(new GridBagLayout());
		JPanel left_panel = new JPanel(new BorderLayout());
		left_panel.add(form_panel, BorderLayout.NORTH);
		form_panel.setBorder(BorderFactory.createTitledBorder("Form Data to Post:"));
		center_panel.add(left_panel, BorderLayout.WEST);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(3, 3, 3, 3);
		
		constraints.gridx = 0; constraints.gridy = 0;
		form_panel.add(new JLabel("First Name:", JLabel.TRAILING), constraints);
		JTextField firstNameField = new JTextField("јлександр", 20);
		constraints.gridx = 1; constraints.gridy = 0;
		form_panel.add(firstNameField, constraints);
		
		constraints.gridx = 0; constraints.gridy = 1;
		form_panel.add(new JLabel("Last Name:", JLabel.TRAILING), constraints);
		JTextField lastNameField = new JTextField("ѕушкин", 20);
		constraints.gridx = 1; constraints.gridy = 1;
		form_panel.add(lastNameField, constraints);
		
		constraints.gridx = 0; constraints.gridy = 2;
		form_panel.add(new JLabel("Password:", JLabel.TRAILING), constraints);
		JPasswordField passwordField = new JPasswordField("password", 20);
		constraints.gridx = 1; constraints.gridy = 2;
		form_panel.add(passwordField, constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 2;
		JButton postBtn = new JButton("POST...");
		constraints.insets.top = 15; constraints.insets.bottom = 15;
		constraints.gridx = 0; constraints.gridy = 3;
		form_panel.add(postBtn, constraints);
		
		tmp_panel = new JPanel(new GridLayout(0, 1));
		tmp_panel.setBorder(BorderFactory.createTitledBorder("Request Info:"));
		left_panel.add(tmp_panel, BorderLayout.CENTER);
		
		JTextArea requestInfoArea = new JTextArea();
		requestInfoArea.setEditable(false);
		requestInfoArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		tmp_panel.add(new JScrollPane(requestInfoArea));
		
		left_panel.setPreferredSize(form_panel.getPreferredSize());
		
		JPanel response_panel = new JPanel (new GridLayout(0, 1));
		response_panel.setBorder(BorderFactory.createTitledBorder("Server Response:"));
		center_panel.add(response_panel, BorderLayout.CENTER);
		
		JTextArea responseArea = new JTextArea();
		responseArea.setEditable(false);
		responseArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		
		postBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				requestInfoArea.setText(""); requestInfoArea.repaint();
				response_panel.removeAll(); response_panel.repaint();
				postBtn.setText("Processing..."); postBtn.setEnabled(false);
				
				new Thread ( new Runnable() {
					@Override public void run() {
						try {
							// ‘ормируем URL дл€ HTTP-запроса:
							java.net.URL url = new java.net.URL (webServiceField.getText());
							
							// ѕриводим тип вызова метода URL.openConnection() к HttpURLConnection
							// (т. к. мы точно знаем тип протокола в нашем URL-запросе):
							HttpURLConnection httpConnection  = (HttpURLConnection)url.openConnection();
							
							String charset = "UTF-8";
							
							// ”станавливаем тип POST дл€ HTTP-запроса.
							// «апрос POST отличаетс€ от GET-запроса тем, что мы можем отправить на сервер
							// произвольные данные в теле HTTP пакета:
							httpConnection.setRequestMethod("POST");
							
							// ѕри помощи метода HttpURLConnection.setRequestProperty(...) мы можем устанавливать 
							// пол€ в заголовке HTTP-запроса:
							httpConnection.setRequestProperty("User-Agent", USER_AGENT);
							// ”станавливаем свойство "Content-type" в значение "application/x-www-form-urlencoded"
							// дл€ того, чтобы сервер пон€л, что мы отправл€ем данные формы (список пар ключ-значение,
							// которые отражают данные, введенные пользователем в некоторую форму):
							httpConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=" + charset);
							
							
							// ћетод HttpURLConnection.setDoOutput(...) настраивает HTTP-соединение на передачу данных от клиента
							// к серверу (устанавливаем в true, т. к. мы хотим отправить данные вместе с запросом POST):
							httpConnection.setDoOutput(true);
							
							try (Formatter postData = new Formatter(httpConnection.getOutputStream(), charset)) {
								// ‘ормируем данные дл€ отправки совместно с запросом POST:
								String postString = String.format("%s=%s&%s=%s&%s=%s", 
										// —татический класс URLEncoder используетс€ дл€ преобразовани€ 
										// символьных данных в MIME-формат "application/x-www-form-urlencoded":
										"first_name", URLEncoder.encode( firstNameField.getText(), charset ),
										"last_name", URLEncoder.encode( lastNameField.getText(), charset ),
										"password", URLEncoder.encode( new String (passwordField.getPassword()), charset ) );
								postData.format("%s", postString);
								
								// ќтправл€ем данные в выходной поток HTTP-соединени€:
								postData.flush();
								
								requestInfoArea.append("POST request data:\n");
								requestInfoArea.append(postString);
							}
							
							// ¬ыводим информацию по полученному ответу от сервера:
							int responseCode = httpConnection.getResponseCode();
							requestInfoArea.append( String.format("\n\nResponse code: %d\n", responseCode) );
							requestInfoArea.append( String.format("Response message: %s\n", httpConnection.getResponseMessage()) );
							
							// ¬ычлен€ем строковое представление кодировки содержимого в ответном сообщении:
							Matcher matcher = Pattern.compile("(?<=charset=)((\\w+-?)+)").matcher(httpConnection.getContentType());
							charset = ( matcher.find() ) ? (matcher.group(1)) : (Charset.defaultCharset().name());
							requestInfoArea.append( String.format("Response charset: %s\n", charset) );
							
							// ѕечатаем содержимое HTTP-заголовка полученного в ответном сообщении
							// при помощи метода HttpURLConnection.getHeaderFields():
							final String pattern = "%-21.100s: ";
							requestInfoArea.append("\nHttpURLConnection.getHeaderFields():\n");
							for ( Map.Entry<String, List<String>> map_entry : httpConnection.getHeaderFields().entrySet() ) {
								requestInfoArea.append( String.format(pattern, map_entry.getKey()) );
								
								List<String> fieldVals = map_entry.getValue();
								for ( String fieldVal : fieldVals ) {
									requestInfoArea.append( String.format("%s\n", fieldVal) );
									if ( fieldVals.size() > 1 ) {
										requestInfoArea.append( String.format(pattern, "") );
									}
								}
							}
							
							StringBuilder responseTextSB = new StringBuilder();
							if ( responseCode == HttpURLConnection.HTTP_OK ) {
								try ( BufferedReader in = new BufferedReader(
								        new InputStreamReader(httpConnection.getInputStream(), charset)) ) {
									// ѕострочно считываем содержимое пол€ данных, отправленных вместе с HTTP-ответом:
									String inputLine;
									while ((inputLine = in.readLine()) != null) {
										responseTextSB.append(inputLine + "\n");
									}
								}
							} else {
								responseTextSB.append( String.format("POST request failed (response code = %d)!", responseCode) );
							}
							responseArea.setText(responseTextSB.toString());
							response_panel.add(new JScrollPane(responseArea));
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
						} finally {
							postBtn.setText("POST...");
							postBtn.setEnabled(true);
						}
					}
				} ).start();
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new HttpPOST_Testing ();
	}

}
