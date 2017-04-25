package java_api_testing.net_api.ws_testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import java_api_testing.net_api.ws_testing.wsImpl.JAXWSWebServiceTestingServer;
import java_api_testing.net_api.ws_testing.wsImpl.JAXWSWebServiceTestingServerService;

/**
 * Демонстрация использования технологии Java API for XML Web Services (JAX-WS) со стороны клиента
 * 
 * <p> Клиентский код для доступа к веб-сервису генерируется на основе публикуемого сервисом
 * WSDL-файла при помощи утилиты wsimport (содержится в стандартной поставе JDK)
 * 
 * <p> Пример скрипта, генерирующего клиентские файлы для доступа к веб-сервису находится по пути:
 * <b>{project_dir}/scripts/windows/04_Make_wsTesting_client_code.cmd</b> 
 * (результат работы утилиты wsimport содержится в пакете: <b>java_api_testing.net_api.ws_testing.wsImpl</b>)
 * 
 * <p> Класс работает в паре с {@link JAXWS_WebServiceTesting_Server}
 * 
 * @author Lab119Alex
 *
 */
public class JAXWS_WebServiceTesting_Client {
	
	ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	JAXWSWebServiceTestingServer mWebServicePort = null;
	
	public JAXWS_WebServiceTesting_Client () {
		JFrame myFrame = new JFrame ( "Web Service Client Testing" );
		myFrame.setSize(600, 300);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel bottom_panel = new JPanel(new GridBagLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		TitledBorder bottomPanelBorder = BorderFactory.createTitledBorder("Connect to server to get MyTransferObject values...");
		bottom_panel.setBorder(bottomPanelBorder);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(3, 3, 3, 3);
		
		constraints.gridx = 0; constraints.gridy = 0;
		bottom_panel.add(new JLabel("String MyTransferObject.getValue1():", JLabel.TRAILING), constraints);
		JTextField value1Field = new JTextField("", 20);
		value1Field.setFont ( value1Field.getFont().deriveFont(15.0f) );
		value1Field.setEditable(false);
		constraints.gridx = 1; constraints.gridy = 0;
		bottom_panel.add(value1Field, constraints);
		
		constraints.gridx = 0; constraints.gridy = 1;
		bottom_panel.add(new JLabel("String MyTransferObject.getValue2():", JLabel.TRAILING), constraints);
		JTextField value2Field = new JTextField("", 20);
		value2Field.setFont ( value1Field.getFont() );
		value2Field.setEditable(false);
		constraints.gridx = 1; constraints.gridy = 1;
		bottom_panel.add(value2Field, constraints);
		
		JPanel center_panel = new JPanel(new GridBagLayout());
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		constraints.gridx = 0; constraints.gridy = 0;
		center_panel.add(new JLabel("MyTransferObject.setValue1(String):", JLabel.TRAILING), constraints);
		JTextField setValue1Field = new JTextField("", 20);
		setValue1Field.setFont ( value1Field.getFont() );
		setValue1Field.setEnabled(false);
		constraints.gridx = 1; constraints.gridy = 0;
		center_panel.add(setValue1Field, constraints);
		
		constraints.gridx = 0; constraints.gridy = 1;
		center_panel.add(new JLabel("MyTransferObject.setValue2(String):", JLabel.TRAILING), constraints);
		JTextField setValue2Field = new JTextField("", 20);
		setValue2Field.setFont ( value1Field.getFont() );
		setValue2Field.setEnabled(false);
		constraints.gridx = 1; constraints.gridy = 1;
		center_panel.add(setValue2Field, constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 2;
		constraints.insets.top = 15; 
		constraints.gridx = 0; constraints.gridy = 3;
		JButton sendBtn = new JButton("WebService.setMyTransferObject(...)");
		sendBtn.setEnabled(false);
		center_panel.add(sendBtn, constraints);
		
		JPanel top_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmp_panel = new JPanel (new BorderLayout());
		top_panel.add(tmp_panel);
		tmp_panel.add(new JLabel("Web-service Server URL: "), BorderLayout.WEST);
		JTextField serviceURLField = new JTextField("Press \"Connect...\" to load service URL...");
		serviceURLField.setEditable(false);
		serviceURLField.setFont ( serviceURLField.getFont().deriveFont(15.0f) );
		serviceURLField.setForeground(Color.GRAY);
		tmp_panel.add(serviceURLField, BorderLayout.CENTER);
		
		JButton connectBtn = new JButton("Connect...");
		tmp_panel.add(connectBtn, BorderLayout.EAST);
		
		setValue1Field.setText("Hello,");
		setValue2Field.setText("World!");
		connectBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				connectBtn.setText("Connecting...");
				connectBtn.setEnabled(false);
				mExecutorService.schedule( new Runnable() {
					@Override public void run() {
						try {
							// Пробуем создать подключение к сервису при помощи сгенерированных на основе WSDL классов:
							JAXWSWebServiceTestingServerService service = new JAXWSWebServiceTestingServerService();
							// URL-адрес сервиса прописывается в коде генерируемых классов, поэтому
							// мы можем не указывать явный адрес веб-сервиса:
							mWebServicePort = service.getJAXWSWebServiceTestingServerPort();
							
							serviceURLField.setText( service.getWSDLDocumentLocation().toString() );
							connectBtn.setText("Connected!");
							
							setValue1Field.setEnabled(true);
							setValue2Field.setEnabled(true);
							sendBtn.setEnabled(true);
							
							// Запускаем поток, запрашивающий объект MyTransferObject с сервера каждые 3 сек:
							mExecutorService.scheduleWithFixedDelay(new Runnable() {
								@Override public void run() {
									try {
										bottomPanelBorder.setTitle("Reading MyTransferObject from Server...");
										bottom_panel.repaint();
										
										// Несмотря на то, что мы имеем доступ к объекту  java_api_testing.net_api.ws_testing.MyTransferObject,
										// мы должны пользоваться сгенерированной версией объекта MyTransferObject
										// из пакета java_api_testing.net_api.ws_testing.wsImpl:
										java_api_testing.net_api.ws_testing.wsImpl.MyTransferObject myTransferObject
											= mWebServicePort.getMyTransferObject();
										
										bottomPanelBorder.setTitle("MyTransferObject Server values:");
										value1Field.setText( myTransferObject.getValue1() );
										value2Field.setText( myTransferObject.getValue2() );										
									} catch (Exception exception) {
										bottomPanelBorder.setTitle(exception.toString());
									} finally {
										bottom_panel.repaint();
									}
								}
							}, 0, 3, TimeUnit.SECONDS);
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
							connectBtn.setText("Connect...");
							connectBtn.setEnabled(true);
						}
					}
				}, 0, TimeUnit.SECONDS );
			}
		});
		
		sendBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				sendBtn.setEnabled(false);
				sendBtn.setText("Sending data to server...");
				// Выполняем передачу объекта MyTransferObject на сервер в отдельном потоке:
				mExecutorService.schedule( new Runnable() {
					@Override public void run() {
						try {
							java_api_testing.net_api.ws_testing.wsImpl.MyTransferObject myTransferObject 
								= new java_api_testing.net_api.ws_testing.wsImpl.MyTransferObject ();
							
							myTransferObject.setValue1(setValue1Field.getText());
							myTransferObject.setValue2(setValue2Field.getText());
							// Вызываем метод веб-сервиса при помощи прокси:
							mWebServicePort.setMyTransferObject( myTransferObject );
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
						} finally {
							sendBtn.setEnabled(true);
							sendBtn.setText("WebService.setMyTransferObject(...)");
						}
					}
				}, 0, TimeUnit.SECONDS );
			}
		});
		
		myFrame.addWindowListener(new WindowAdapter () {
			@Override public void windowClosing(WindowEvent e) {
				mExecutorService.shutdown();
				super.windowClosing(e);
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new JAXWS_WebServiceTesting_Client ();
	}
}