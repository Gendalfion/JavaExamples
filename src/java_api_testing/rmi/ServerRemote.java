package java_api_testing.rmi;

/**
 * ��������� ��������� �������, ������������ ���������� Java RMI.
 * ������������ � �������:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 */
public interface ServerRemote extends java.rmi.Remote {
	/**
	 * ����������� ������ ���������� ������� �� �������
	 * @param request - ������, ������� ����� �������� �� �������
	 * @param listener - ��������� callback-���������, ��������������� ��������
	 * @throws java.rmi.RemoteException ��������� ��� ������������� ������� � ������� ���������� ������
	 */
	public void asyncExecute ( Request request, RequestListener listener ) 
			throws java.rmi.RemoteException; // ���������� RemoteException ���������� ����� ���������� ������
	
	/**
	 * ������ ���������, ������������ ������ javax.swing.JFrame, ����������� �� �������
	 * @param frame_title - ��������� ������������ �������
	 * @return ���������� ��������� ������
	 * @throws java.rmi.RemoteException ��������� ��� ������������� ������� � ������� ���������� ������ 
	 */
	public javax.swing.JFrame createFrame ( String frame_title )
			throws java.rmi.RemoteException; // ���������� RemoteException ���������� ����� ���������� ������
}
