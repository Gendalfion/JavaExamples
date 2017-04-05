package java_api_testing.rmi;

/**
 * ��������� ���������, ������������ ��� ��������� ���������� �������� ��������.
 * ������������ � �������:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 */
public interface RequestListener extends java.rmi.Remote {
	/**
	 * Callback ���������, ���������� �� ��������� ���������� ��������
	 * @param request - ������, ����������� �������� �������� ����� ������� ������
	 * @param result - ��������� ���������� ��������
	 * @throws java.rmi.RemoteException ��������� ��� ������� �������� �������������� � ��������
	 */
	public void requestProcessed ( Request request, Object result )
		throws java.rmi.RemoteException; // ���������� RemoteException ���������� ����� ���������� ������
}
