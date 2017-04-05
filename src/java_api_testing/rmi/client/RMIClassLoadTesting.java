package java_api_testing.rmi.client;

import javax.swing.JOptionPane;

import java_api_testing.rmi.ServerRemote;

/**
 * Демонстрация использования технологии RMI (пакет java.rmi.*) для
 * загрузки классов GUI с сервера
 * Данный класс работает в паре с классом:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * 
 * @author Lab119Alex
 *
 */
public class RMIClassLoadTesting {

	public static void main(String[] args) {
		String hostName;
		if ( args.length > 0 ) {
			hostName = args[0];
		} else {
			hostName = "localhost";
		}
		
		String serverName;
		if ( args.length > 1 ) {
			serverName = args[1];
		} else {
			serverName = "ServerRemoteImpl";
		}
		
		String frameTitle;
		if ( args.length > 2 ) {
			frameTitle = args[2];
		} else {
			frameTitle = "Title sent from client";
		}
		
		// Формируем адрес серверного RMI-интерфейса, используя имя хоста и название серверного RMI-интерфейса в реестре удаленных объектов:
		String remoteServerURL = String.format("rmi://%s/%s", hostName, serverName);
		try {
			// Извлекаем удаленный интерфейс из реестра RMI-объектов:
			ServerRemote serverRemote = (ServerRemote) java.rmi.Naming.lookup(remoteServerURL);
			
			// Создаем объект JFrame удаленно на сервере serverRemote и получаем результат в 
			// виде упакованного объекта (при помощи технологии сериализации объектов в Java):
			serverRemote.createFrame(frameTitle).setVisible(true);
		} catch ( Exception exception ) {
			JOptionPane.showMessageDialog(null
					, "Connection \"" + remoteServerURL + "\" exception: " + exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
