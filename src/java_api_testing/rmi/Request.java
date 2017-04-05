package java_api_testing.rmi;

import java.io.Serializable;

/**
 * ������� ��������� ������� ��� ������� {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * 
 * @author Lab119Alex
 *
 */
public interface Request extends Serializable {
	/**
	 * �������, ����������� ������
	 * @return ���������� ��������� ���������� ������� � ���� �������, ������� �������� ������������ � �������� �������
	 */
	public Serializable processRequest ();
}
