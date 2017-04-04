package java_api_testing.net_api.obj_prot_testing;

import java.io.Serializable;

/**
 * Базовый интерфейс запроса для сервера ObjectProtocol_Server
 * 
 * @author Lab119Alex
 *
 */
public interface Request extends Serializable {
	public Serializable processRequest ();
}
