package java_api_testing.net_api.obj_prot_testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

public class ObjectProtocol_Client {
	
	Socket				mSocket = null;
	ObjectInputStream 	mObjectInputStream = null;
	ObjectOutputStream 	mObjectOutputStream = null;
	
	JTextArea mOutputArea = new JTextArea();
	
	{
		mOutputArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		mOutputArea.setEditable(false);
	}
	
	public ObjectProtocol_Client () {
		JFrame myFrame = new JFrame ( "java.net.* Object Protocol Testing" );
		myFrame.setMinimumSize (new Dimension(600, 700));
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel( new GridLayout(0, 1) );
		main_panel.add( top_panel, BorderLayout.NORTH );
		
		JPanel tmp_panel = new JPanel ();
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createTitledBorder("Socket configuration:"));
		
		tmp_panel.add(new JLabel("Host: "));
		JTextField hostField = new JTextField("localhost", 15);
		tmp_panel.add(hostField);
		
		tmp_panel.add(new JLabel("Port: "));
		JTextField portField = new JTextField("10000", 10);
		tmp_panel.add(portField);
		
		JButton openConnectionBtn = new JButton("Connect...");
		tmp_panel.add(openConnectionBtn);
		
		JButton closeConnectionBtn = new JButton("Disconnect...");
		closeConnectionBtn.setEnabled(false);
		tmp_panel.add(closeConnectionBtn);
		
		tmp_panel = new JPanel ();
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createTitledBorder("SystemInfoRequest:"));
		
		JButton systemInfoRequestBtn = new JButton("Send request...");
		systemInfoRequestBtn.setEnabled(false);
		tmp_panel.add(systemInfoRequestBtn);
		
		tmp_panel = new JPanel ( new BorderLayout() );
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createTitledBorder("DataProcessingRequest:"));
		
		tmp_panel.add(new JLabel("Input text data: "), BorderLayout.WEST);
		JTextField textDataField = new JTextField("Some text data to process on server...");
		tmp_panel.add(textDataField, BorderLayout.CENTER);
		
		JButton dataProcessingRequestBtn = new JButton("Send request...");
		dataProcessingRequestBtn.setEnabled(false);
		tmp_panel.add(dataProcessingRequestBtn, BorderLayout.EAST);
		
		JPanel center_panel = new JPanel ( new GridLayout(0, 1) );
		main_panel.add( center_panel, BorderLayout.CENTER );
		center_panel.setBorder(BorderFactory.createTitledBorder("Request answers got from socket:"));
		
		center_panel.add ( new JScrollPane(mOutputArea) );
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mOutputArea.setText("");
			}
		});
		mOutputArea.setComponentPopupMenu(clear_menu);
		
		openConnectionBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					// Открываем клиентский TCP-сокет для подключения к заданному хосту:
					mSocket = new Socket( hostField.getText(), getTextAsInt(portField, 10000) );
					
					// Открываем потоки ObjectInputStream и ObjectOutputStream для обмена объектами с сервером:
					mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
					// Порядок создания потоков ВАЖЕН, т. к. во время открытия выходного потока ObjectOutputStream у сервера запрашивается
					// дескриптор его входного потока ObjectInputStream (это блокирующая операция).
					// Таким образом, если сервер сначала создает ObjectInputStream, а потом ObjectOutputStream, то для того, чтобы избежать
					// взаимной блокировки, клиент должен создавать сначала ObjectOutputStream, а потом ObjectInputStream (и наоборот).
					mObjectInputStream  = new ObjectInputStream(mSocket.getInputStream());
					
					hostField.setEnabled(false);
					portField.setEnabled(false);
					openConnectionBtn.setEnabled(false);
					closeConnectionBtn.setEnabled(true);
					systemInfoRequestBtn.setEnabled(true);
					dataProcessingRequestBtn.setEnabled(true);
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
					// Закрываем сокет и связанные с ним потоки данных:
					if ( mSocket 				!= null ) { mSocket.close(); mSocket = null; }
					if ( mObjectOutputStream 	!= null ) { mObjectOutputStream.close(); mObjectOutputStream = null; }
					if ( mObjectInputStream 	!= null ) { mObjectInputStream.close(); mObjectInputStream = null; }
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(main_panel, "Ошибка при закрытии соединения: " + exception.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				} finally {
					hostField.setEnabled(true);
					portField.setEnabled(true);
					openConnectionBtn.setEnabled(true);
					closeConnectionBtn.setEnabled(false);
					systemInfoRequestBtn.setEnabled(false);
					dataProcessingRequestBtn.setEnabled(false);
				}
			}
		});
		
		systemInfoRequestBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				sendRequestAndReadAnswer ( new SystemInfoRequest() );
			}
		});
		
		dataProcessingRequestBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				sendRequestAndReadAnswer ( new DataProcessingRequest( textDataField.getText() ) );
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
	
	private void sendRequestAndReadAnswer ( Request request ) {
		try {
			// Отправляем объект Request на сервер при помощи метода ObjectOutputStream.writeObject(...):
			mObjectOutputStream.writeObject( request );
			mObjectOutputStream.flush();
			
			// Принимаем ответный объект от сервера при помощи метода ObjectOutputStream.readObject():
			Object answer = mObjectInputStream.readObject();
			
			// При подобном обмене объектами с удаленным сервером важно помнить о том, чтобы байт коды
			// участвующих в обмене объектов были установлены как на серверной, так и на клиентской стороне!
			
			mOutputArea.append( String.format("%1$tT.%1$tL request: \"%2$s\"\n   answer: \"%3$s\"\n", new java.util.Date()
					, request.getClass().getSimpleName(), answer.toString()) );
		} catch (IOException | ClassNotFoundException exception) {
			JOptionPane.showMessageDialog(null, "Ошибка при обмене данными: " + exception.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new ObjectProtocol_Client ();
	}

}
