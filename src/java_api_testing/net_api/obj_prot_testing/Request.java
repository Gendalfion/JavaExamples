package java_api_testing.net_api.obj_prot_testing;

import java.io.Serializable;

/**
 * ������� ��������� ������� ��� ������� ObjectProtocol_Server
 * 
 * @author Lab119Alex
 *
 */
public interface Request extends Serializable {
	public Serializable processRequest ();
}
