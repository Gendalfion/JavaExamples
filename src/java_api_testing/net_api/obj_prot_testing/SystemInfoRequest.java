package java_api_testing.net_api.obj_prot_testing;

import java.io.Serializable;

/**
 * Запрос информации об ОС, на которой выполняется сервер ObjectProtocol_Server
 * @author Lab119Alex
 *
 */
public class SystemInfoRequest implements Request {

	private static final long serialVersionUID = 1L;

	@Override public Serializable processRequest() {
		return String.format( "os.arch = <%s>;  os.name = <%s>; os.version = <%s>"
			, System.getProperty("os.arch"), System.getProperty("os.name"), System.getProperty("os.version") );
	}

}
