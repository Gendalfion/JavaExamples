package java_api_testing.rmi.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

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

import java_api_testing.rmi.Request;
import java_api_testing.rmi.RequestListener;
import java_api_testing.rmi.ServerRemote;

/**
 * Демонстрация реализации клиентской стороны сетевого взаимодействия при помощи
 * технологии удаленного вызова процедур, предоставляемой пакетом java.rmi.*
 * Данный класс работает в паре с классом:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * 
 * @author Lab119Alex
 *
 */
public class RMIClientTesting {
	
	ServerRemote mServerRemote = null;
	
	JTextArea mOutputArea = new JTextArea();
	
	{
		mOutputArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		mOutputArea.setEditable(false);
	}
	
	public RMIClientTesting () throws RemoteException {
		JFrame myFrame = new JFrame ( "java.rmi.* RMI Client Testing" );
		myFrame.setMinimumSize (new Dimension(600, 700));
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel( new GridLayout(0, 1) );
		main_panel.add( top_panel, BorderLayout.NORTH );
		
		JPanel tmp_panel = new JPanel ();
		top_panel.add(tmp_panel);
		tmp_panel.setBorder(BorderFactory.createTitledBorder("Remote Server Options:"));
		
		tmp_panel.add(new JLabel("Host: "));
		JTextField hostField = new JTextField("localhost", 12);
		tmp_panel.add(hostField);
		
		tmp_panel.add(new JLabel("Registration name: "));
		JTextField registryField = new JTextField("ServerRemoteImpl", 12);
		tmp_panel.add(registryField);
		
		JButton openConnectionBtn = new JButton("Connect...");
		tmp_panel.add(openConnectionBtn);
		
		JPanel center_panel = new JPanel ( new GridLayout(0, 1) );
		main_panel.add( center_panel, BorderLayout.CENTER );
		center_panel.setBorder(BorderFactory.createTitledBorder("Request answers got from remote server:"));
		
		center_panel.add ( new JScrollPane(mOutputArea) );
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mOutputArea.setText("");
			}
		});
		mOutputArea.setComponentPopupMenu(clear_menu);
		
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
		
		RequestListener systemInfoRequestListener = new RequestListenerImpl (systemInfoRequestBtn);
		RequestListener dataProcessRequestListener = new RequestListenerImpl (dataProcessingRequestBtn);
		
		openConnectionBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				// Формируем адрес серверного RMI-интерфейса, используя имя хоста и название интерфейса в реестре удаленных объектов:
				String remoteServerURL = String.format("rmi://%s/%s", hostField.getText(), registryField.getText());
				try {
					// Извлекаем удаленный интерфейс из реестра RMI-объектов:
					mServerRemote = (ServerRemote) java.rmi.Naming.lookup(remoteServerURL); 
					
					hostField.setEnabled( false );
					registryField.setEnabled( false );
					openConnectionBtn.setText("Connected!");
					openConnectionBtn.setEnabled( false );
					systemInfoRequestBtn.setEnabled(true);
					dataProcessingRequestBtn.setEnabled(true);
				} catch ( Exception exception ) {
					JOptionPane.showMessageDialog(main_panel
							, "Connection \"" + remoteServerURL + "\" exception: " + exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		systemInfoRequestBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( executeRemoteRequest (new SystemInfoRequest(), systemInfoRequestListener) ) {
					systemInfoRequestBtn.setText("Processing...");
					systemInfoRequestBtn.setEnabled(false);
				}
			}
		});
		
		dataProcessingRequestBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( executeRemoteRequest (new DataProcessingRequest ( textDataField.getText() ), dataProcessRequestListener) ) {
					dataProcessingRequestBtn.setText("Processing...");
					dataProcessingRequestBtn.setEnabled(false);
				}
			}
		});
		
		myFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				try {
					java.rmi.server.UnicastRemoteObject.unexportObject(systemInfoRequestListener, true);
				} catch (NoSuchObjectException exception) {
					System.err.println("Unexporting failed: " + exception.toString());
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	private boolean executeRemoteRequest ( Request request, RequestListener requestListener ) {
		if ( mServerRemote != null ) {
			try {
				// Делаем удаленный вызов процедуры ServerRemote.asyncExecute(...).
				// В данном вызове мы передаем ссылку на локальный объект request 
				// (который будет передан на сервер с использованием технологии сериализации объектов в Java),
				// А также передаем ссылку на удаленный объект requestListener - сериализованная копия данного
				// объекта не будет передаваться на сервер, а вызов методов интерфейса RequestListener
				// на серверной стороне будет приводить к удаленному вызову методов объекта requestListener
				// на клиентской стороне:
				mServerRemote.asyncExecute(request, requestListener);
				return true;
			} catch (RemoteException remoteException) {
				JOptionPane.showMessageDialog(null, "Remote method call error: " + remoteException.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}
	
	private void printRequestProcessingResult ( Request request, Object result ) {
		mOutputArea.append( String.format("%1$tT.%1$tL request: \"%2$s\"\n   result: \"%3$s\"\n", new java.util.Date()
				, request.getClass().getName(), result.toString()) );
	}
	
	/**
	 * Реализация RMI-интерфейса RequestListener
	 * 
	 * @author Lab119Alex
	 *
	 */
	private class RequestListenerImpl extends java.rmi.server.UnicastRemoteObject implements RequestListener {
		private static final long serialVersionUID = 1L;
		
		private JButton mRequestBtn;

		protected RequestListenerImpl( JButton request_btn ) throws RemoteException {
			mRequestBtn = request_btn;
		}

		@Override public void requestProcessed(Request request, Object result) throws RemoteException {
			mRequestBtn.setText("Send request...");
			mRequestBtn.setEnabled(true);
			printRequestProcessingResult (request, result);
		}	
	}
	
	public static void main(String[] args) throws RemoteException {
		new RMIClientTesting ();
	}
}
