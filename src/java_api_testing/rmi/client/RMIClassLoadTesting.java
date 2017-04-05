package java_api_testing.rmi.client;

import javax.swing.JOptionPane;

import java_api_testing.rmi.ServerRemote;

/**
 * ������������ ������������� ���������� RMI (����� java.rmi.*) ���
 * �������� ������� GUI � �������
 * ������ ����� �������� � ���� � �������:
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
		
		// ��������� ����� ���������� RMI-����������, ��������� ��� ����� � �������� ���������� RMI-���������� � ������� ��������� ��������:
		String remoteServerURL = String.format("rmi://%s/%s", hostName, serverName);
		try {
			// ��������� ��������� ��������� �� ������� RMI-��������:
			ServerRemote serverRemote = (ServerRemote) java.rmi.Naming.lookup(remoteServerURL);
			
			// ������� ������ JFrame �������� �� ������� serverRemote � �������� ��������� � 
			// ���� ������������ ������� (��� ������ ���������� ������������ �������� � Java):
			serverRemote.createFrame(frameTitle).setVisible(true);
		} catch ( Exception exception ) {
			JOptionPane.showMessageDialog(null
					, "Connection \"" + remoteServerURL + "\" exception: " + exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
