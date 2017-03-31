package java_api_testing.net_api;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Демонстрация работы с клиентским TCP-сокетом при помощи пакета java.net.*
 * Данный класс работает в паре с классом java_api_testing.net_api.SocketTesting_Server
 * 
 * @author Lab119Alex
 *
 */
public class SocketTesting_Client {
	
	private Writer mSocketWriter = null;
	private Socket mSocketConnection = null;
	
	public SocketTesting_Client () {
		JFrame myFrame = new JFrame ( "java.net.* TCP-Socket Testing" );
		myFrame.setSize(650, 300);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel( new GridLayout(0, 2) );
		main_panel.add( top_panel, BorderLayout.NORTH );
		top_panel.setBorder(BorderFactory.createTitledBorder("Socket options:"));
		
		JPanel tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		tmp_panel.add(new JLabel("SO_TIMEOUT: "));
		JTextField soTimeoutField = new JTextField("2000", 10);
		tmp_panel.add(soTimeoutField);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		JCheckBox tcpNoDelayCheckBox = new JCheckBox("TCP_NODELAY: ");
		tcpNoDelayCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		tmp_panel.add(tcpNoDelayCheckBox);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		JCheckBox soLingerCheckBox = new JCheckBox("SO_LINGER: ");
		soLingerCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		tmp_panel.add(soLingerCheckBox);
		JTextField soLingerField = new JTextField("1000", 10);
		soLingerField.setEnabled(false);
		tmp_panel.add(soLingerField);
		soLingerCheckBox.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				soLingerField.setEnabled(soLingerCheckBox.isSelected());
			}
		});
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		tmp_panel.add(new JLabel("Host: "));
		JTextField hostField = new JTextField("localhost", 15);
		tmp_panel.add(hostField);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		tmp_panel.add(new JLabel("Port: "));
		JTextField portField = new JTextField("10000", 10);
		tmp_panel.add(portField);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		JButton openConnectionBtn = new JButton("Open Connection...");
		tmp_panel.add(openConnectionBtn);
		
		tmp_panel = new JPanel();
		top_panel.add(tmp_panel);
		JButton closeConnectionBtn = new JButton("Close Connection...");
		closeConnectionBtn.setEnabled(false);
		tmp_panel.add(closeConnectionBtn);
		
		JPanel center_panel = new JPanel(new BorderLayout());
		main_panel.add(center_panel, BorderLayout.CENTER);
		center_panel.setBorder(BorderFactory.createTitledBorder("Send message to socket:"));
		
		tmp_panel = new JPanel();
		center_panel.add(tmp_panel, BorderLayout.NORTH);
		
		JTextField messageField = new JTextField("Message...", 25);
		messageField.setEnabled(false);
		tmp_panel.add(messageField);
		
		tmp_panel.add(new JLabel("  Delay: "));
		JTextField delayField = new JTextField("20", 5);
		delayField.setEnabled(false);
		tmp_panel.add(delayField);
		
		tmp_panel.add(new JLabel("  Count: "));
		JTextField countField = new JTextField("5", 5);
		countField.setEnabled(false);
		tmp_panel.add(countField);
		
		JButton sendBtn = new JButton("Send...");
		sendBtn.setEnabled(false);
		tmp_panel.add(sendBtn);
		
		openConnectionBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					mSocketWriter = new BufferedWriter( 
							new OutputStreamWriter(
									// Создаем новое TCP-подключение в обертке BufferedWriter:
									(mSocketConnection = new Socket(hostField.getText(), getTextAsInt(portField, 10000))).getOutputStream() 
							) );
					// Устанавливаем опции подключения:
					
					// setSoTimeout устанавливает таймер для всех блокирующих операций ввода/вывода:
					mSocketConnection.setSoTimeout(getTextAsInt(soTimeoutField, 2000));
					
					// setTcpNoDelay включает/отключает функцию "алгоритм Нейгла":
					mSocketConnection.setTcpNoDelay(tcpNoDelayCheckBox.isSelected());
					
					// setSoLinger определяет таймаут ожидания отправки оставшихся данных 
					// при закрытии соединения до окончания отправки всех данных в очереди:
					mSocketConnection.setSoLinger(soLingerCheckBox.isSelected(), getTextAsInt(soLingerField, 1000));
					
					soTimeoutField.setEnabled(false);
					tcpNoDelayCheckBox.setEnabled(false);
					soLingerCheckBox.setEnabled(false);
					soLingerField.setEnabled(false);
					hostField.setEnabled(false);
					portField.setEnabled(false);
					openConnectionBtn.setEnabled(false);
					closeConnectionBtn.setEnabled(true);
					messageField.setEnabled(true);
					delayField.setEnabled(true);
					countField.setEnabled(true);
					sendBtn.setEnabled(true);
				} catch (UnknownHostException exception) {
					JOptionPane.showMessageDialog(main_panel, "Сервер не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(main_panel, "Ошибка при подключении: " + exception.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		closeConnectionBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					// Закрываем открытое соединения при нажатии на кнопку closeConnectionBtn:
					if ( mSocketConnection != null ) {
						mSocketConnection.close();
						mSocketConnection = null;
					}
					if ( mSocketWriter != null ) {
						mSocketWriter.close();
						mSocketWriter = null;
					}
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(main_panel, "Ошибка при закрытии соединения: " + exception.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				} finally {
					soTimeoutField.setEnabled(true);
					tcpNoDelayCheckBox.setEnabled(true);
					soLingerCheckBox.setEnabled(true);
					soLingerField.setEnabled(soLingerCheckBox.isSelected());
					hostField.setEnabled(true);
					portField.setEnabled(true);
					openConnectionBtn.setEnabled(true);
					closeConnectionBtn.setEnabled(false);
					messageField.setEnabled(false);
					delayField.setEnabled(false);
					countField.setEnabled(false);
					sendBtn.setEnabled(false);
				}
			}
		});
		
		sendBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				final long delay 		= getTextAsInt(delayField, 20);
				final int count			= getTextAsInt(countField, 5);
				final String message 	= messageField.getText();
				new Thread( new Runnable() {
					@Override public void run() {
						try {
							int count_left = count;
							while ( count_left-- > 0 ) {
								// Отправляем строку текста в сокет count раз с задержкой delay мс между отправками:
								mSocketWriter.write(message); mSocketWriter.flush();
								Thread.sleep(delay);
							}
						} catch ( IOException ioException ) {
							JOptionPane.showMessageDialog(main_panel, "Ошибка при передаче: " + ioException.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE);
						} catch ( InterruptedException ieException ) {
						} finally {
							sendBtn.setEnabled(true);
						}
					}
				} ).start();
				sendBtn.setEnabled(false);
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
		new SocketTesting_Client();
	}

}
