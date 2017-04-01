package java_api_testing.net_api;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * Демонстрация работы с UDP-сокетом при помощи пакета java.net.*
 * 
 * @author Lab119Alex
 *
 */
public class DatagramSocketTesting {
	
	DatagramSocket mInputSocket = null;
	
	public DatagramSocketTesting () {
		JFrame myFrame = new JFrame ( "java.net.* UDP-Socket Testing" );
		myFrame.setMinimumSize( new Dimension(680, 600) );
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel (new GridLayout(0, 2));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmpPanel = new JPanel ();
		top_panel.add(tmpPanel);
		tmpPanel.setBorder(BorderFactory.createTitledBorder("Output Datagram Socket:"));
		
		tmpPanel.add(new JLabel("Host: "));
		JTextField hostField = new JTextField("localhost", 10);
		tmpPanel.add(hostField);
		
		tmpPanel.add(new JLabel("  Port: "));
		JTextField outPortField = new JTextField("10000", 10);
		tmpPanel.add(outPortField);
		
		tmpPanel = new JPanel ();
		top_panel.add(tmpPanel);
		tmpPanel.setBorder(BorderFactory.createTitledBorder("Input Datagram Port:"));
		
		tmpPanel.add(new JLabel("Input Port: "));
		JTextField inPortField = new JTextField("10000", 10);
		tmpPanel.add(inPortField);
		
		JButton reopenPortBtn = new JButton("Reopen port...");
		tmpPanel.add(reopenPortBtn);
		
		JPanel center_panel = new JPanel (new BorderLayout());
		main_panel.add ( center_panel, BorderLayout.CENTER );
		
		tmpPanel = new JPanel (new BorderLayout(10, 10));
		center_panel.add(tmpPanel, BorderLayout.NORTH);
		tmpPanel.setBorder(BorderFactory.createTitledBorder("Send datagram message to socket:"));
		
		JTextField messageField = new JTextField("Hello from UDP!");
		tmpPanel.add(messageField, BorderLayout.CENTER);
		
		JButton sendDatagramBtn = new JButton("Send datagram...");
		tmpPanel.add(sendDatagramBtn, BorderLayout.EAST);
		
		tmpPanel = new JPanel(new GridLayout(0, 1));
		center_panel.add(tmpPanel, BorderLayout.CENTER);
		tmpPanel.setBorder(BorderFactory.createTitledBorder("Datagram messages got from socket:"));
		
		JTextArea inputMessageArea = new JTextArea();
		inputMessageArea.setEditable(false);
		inputMessageArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		tmpPanel.add(new JScrollPane(inputMessageArea));
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputMessageArea.setText("");
			}
		});
		inputMessageArea.setComponentPopupMenu(clear_menu);
		
		sendDatagramBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				// Создаем дейтаграммный сокет DatagramSocket, который будет забинден на
				// свободный порт (который предоставит ОС) и на localhost
				// (т. е. с данного порта можно отправлять UDP-пакеты только на localhost):
				try ( DatagramSocket outSocket = new DatagramSocket() ) {
					// Формируем адрес получателя при помощи класса InetAddress:
					InetAddress destAddress = InetAddress.getByName(hostField.getText());
					
					// Подготавливаем массив байт для отправки:
					byte [] data = messageField.getText().getBytes(Charset.defaultCharset());
					
					// Создаем UDP-пакет для отправки, задавая ему буфер данных, адрес и порт получателя:
					DatagramPacket datagramPacket = 
							new DatagramPacket(data, data.length, destAddress, getTextAsInt(outPortField, 10000));
					
					// Отправляем подготовленный пакет при помощи метода  DatagramSocket.send():
					outSocket.send(datagramPacket);
				} catch ( Exception exception ) {
					JOptionPane.showMessageDialog(main_panel, "Ошибка при отправке UDP-пакета: " + exception.toString()
						, "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		reopenPortBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( mInputSocket != null ) {
					// Закрываем принимающий сокет, если он не закрыт к данному моменту:
					mInputSocket.close();
					mInputSocket = null;
				}
				
				new Thread(new Runnable() {
					private static final int BUF_SIZE = 1024;
					@Override public void run() {
						// Создаем принимающий сокет на выбранном пользователем порту
						// (по умолчанию сокет будет забинден на localhost):
						try ( DatagramSocket socket = new DatagramSocket(getTextAsInt(inPortField, 10000)) ) {
							mInputSocket = socket;
							
							while (!Thread.interrupted()) {
								// Подготавливаем DatagramPacket на чтение данных из сокета:
								DatagramPacket inDatagram = new DatagramPacket(new byte [BUF_SIZE], BUF_SIZE);
								
								// Принимаем данные из сокета при помощи метода DatagramSocket.receive ()
								// (данный метод заблокирует текущий поток пока не будут доступны данные
								// для чтения из сокета или не выйдет таймаут, установленный при помощи
								// метода DatagramSocket.setSoTimeout()):
								mInputSocket.receive(inDatagram);
								
								// В полученном дейтаграммном пакете кроме самих данных можно извлеч
								// адрес и порт отправителя при помощи метода DatagramSocket.getSocketAddress():
								inputMessageArea.append(
										String.format( "%1$tT.%1$tL Source: \"%3$s\"; Data: \"%2$s\"\n"
											, new java.util.Date()
											, new String( inDatagram.getData(), 0, inDatagram.getLength(), Charset.defaultCharset())
											, inDatagram.getSocketAddress().toString() ) );
							}	
						} catch ( SocketException socketException ) {
							// Сокет был закрыт снаружи, пропускаем исключени и молча завершаем поток...
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog(main_panel, "Ошибка при работе с UDP-сокетом: " + exception.toString()
								, "Ошибка", JOptionPane.ERROR_MESSAGE);
						}
					}
				}).start();
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
		new DatagramSocketTesting ();
	}

}
