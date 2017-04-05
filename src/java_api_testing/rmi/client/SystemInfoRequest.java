package java_api_testing.rmi.client;

import java.io.Serializable;

import java_api_testing.rmi.Request;

/**
 * Запрос информации об ОС. Используется в классе:
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 *
 */
public class SystemInfoRequest implements Request {

	private static final long serialVersionUID = 1L;

	@Override public Serializable processRequest() {
		
		try {
			Thread.sleep( 4000 );
		} catch ( InterruptedException ie ) {}
		
		return String.format( "os.arch = <%s>;  os.name = <%s>; os.version = <%s>"
			, System.getProperty("os.arch"), System.getProperty("os.name"), System.getProperty("os.version") );
	}
}
