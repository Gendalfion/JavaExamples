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
 * ������������ �������� HTTP POST-������� ��� ������
 * ������� java.net.URL � java.net.HttpURLConnection
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
		JTextField firstNameField = new JTextField("���������", 20);
		constraints.gridx = 1; constraints.gridy = 0;
		form_panel.add(firstNameField, constraints);
		
		constraints.gridx = 0; constraints.gridy = 1;
		form_panel.add(new JLabel("Last Name:", JLabel.TRAILING), constraints);
		JTextField lastNameField = new JTextField("������", 20);
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
							// ��������� URL ��� HTTP-�������:
							java.net.URL url = new java.net.URL (webServiceField.getText());
							
							// �������� ��� ������ ������ URL.openConnection() � HttpURLConnection
							// (�. �. �� ����� ����� ��� ��������� � ����� URL-�������):
							HttpURLConnection httpConnection  = (HttpURLConnection)url.openConnection();
							
							String charset = "UTF-8";
							
							// ������������� ��� POST ��� HTTP-�������.
							// ������ POST ���������� �� GET-������� ���, ��� �� ����� ��������� �� ������
							// ������������ ������ � ���� HTTP ������:
							httpConnection.setRequestMethod("POST");
							
							// ��� ������ ������ HttpURLConnection.setRequestProperty(...) �� ����� ������������� 
							// ���� � ��������� HTTP-�������:
							httpConnection.setRequestProperty("User-Agent", USER_AGENT);
							// ������������� �������� "Content-type" � �������� "application/x-www-form-urlencoded"
							// ��� ����, ����� ������ �����, ��� �� ���������� ������ ����� (������ ��� ����-��������,
							// ������� �������� ������, ��������� ������������� � ��������� �����):
							httpConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=" + charset);
							
							
							// ����� HttpURLConnection.setDoOutput(...) ����������� HTTP-���������� �� �������� ������ �� �������
							// � ������� (������������� � true, �. �. �� ����� ��������� ������ ������ � �������� POST):
							httpConnection.setDoOutput(true);
							
							try (Formatter postData = new Formatter(httpConnection.getOutputStream(), charset)) {
								// ��������� ������ ��� �������� ��������� � �������� POST:
								String postString = String.format("%s=%s&%s=%s&%s=%s", 
										// ����������� ����� URLEncoder ������������ ��� �������������� 
										// ���������� ������ � MIME-������ "application/x-www-form-urlencoded":
										"first_name", URLEncoder.encode( firstNameField.getText(), charset ),
										"last_name", URLEncoder.encode( lastNameField.getText(), charset ),
										"password", URLEncoder.encode( new String (passwordField.getPassword()), charset ) );
								postData.format("%s", postString);
								
								// ���������� ������ � �������� ����� HTTP-����������:
								postData.flush();
								
								requestInfoArea.append("POST request data:\n");
								requestInfoArea.append(postString);
							}
							
							// ������� ���������� �� ����������� ������ �� �������:
							int responseCode = httpConnection.getResponseCode();
							requestInfoArea.append( String.format("\n\nResponse code: %d\n", responseCode) );
							requestInfoArea.append( String.format("Response message: %s\n", httpConnection.getResponseMessage()) );
							
							// ��������� ��������� ������������� ��������� ����������� � �������� ���������:
							Matcher matcher = Pattern.compile("(?<=charset=)((\\w+-?)+)").matcher(httpConnection.getContentType());
							charset = ( matcher.find() ) ? (matcher.group(1)) : (Charset.defaultCharset().name());
							requestInfoArea.append( String.format("Response charset: %s\n", charset) );
							
							// �������� ���������� HTTP-��������� ����������� � �������� ���������
							// ��� ������ ������ HttpURLConnection.getHeaderFields():
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
									// ��������� ��������� ���������� ���� ������, ������������ ������ � HTTP-�������:
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
