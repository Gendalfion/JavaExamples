package java_api_testing.net_api.ws_testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * ������������ ������������� ���������� Java API for XML Web Services (JAX-WS) �� ������� �������
 * 
 * <p> ������ ����� ���������� ���-������ ��� ������ ���������.
 * 
 * <p> ��� ���������� ������� JAX-WS API �������� ������������� ������������� ��� ����������� ����� 
 * (����� ��� WSDL), �������������� ����������� � ������ ������
 * 
 * <p> ����� �������� � ���� � {@link JAXWS_WebServiceTesting_Client}
 * 
 * @author Lab119Alex
 *
 */
@WebService // ��������� WebService �������� ����� � �������� ����������� ���-�������� �������
public class JAXWS_WebServiceTesting_Server {
	private MyTransferObject mCurrentTransferObject = new MyTransferObject();

	@WebMethod // ��������� WebMethod �������� ����������� ����� ���������� ������ ��� ���-�������
	public void setMyTransferObject ( MyTransferObject to_set ) {
		mCurrentTransferObject = to_set;
		System.out.println( String.format("%1$tT.%1$tL setMyTransferObject: value1 = %2$s, value2 = %3$s..."
				, new java.util.Date()
				, mCurrentTransferObject.getValue1()
				, mCurrentTransferObject.getValue2()) );
	}
	
	@WebMethod
	public MyTransferObject getMyTransferObject () {
		return mCurrentTransferObject;
	}
	
	public static void main(String[] args) {
		Endpoint endpoint = null;
		
		final String serviceEndpointURL; 
		
		if ( args.length > 0 ) {
			serviceEndpointURL = args[0];
		} else {
			serviceEndpointURL = "http://localhost:8080/myservice";
		}
		
		try {
			// ��������� ���-������ �� ���������� ������ ��� ������ ������������ ������ Endpoint.publish(...)
			// ��� ������������� ��������� (����� ��� WSDL-����) ����� ������������� ��� ������
			// ���������� JAX-WS �� ������ ��������� � ������-��������������:
			endpoint = Endpoint.publish( serviceEndpointURL, new JAXWS_WebServiceTesting_Server ());
			
			// ��������� ������ ������� ����� �������� WSDL �� ������: <serviceEndpointURL>?WSDL
			
			System.out.println("Service is published on URL: \"" + serviceEndpointURL + "\"");
			System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping the server..." );
		} catch ( Exception exception ) {
			System.err.println( "Server execution exception: " + exception.toString() );
		} finally {
			if ( endpoint != null ) {
				// ��� ���������� ������ ������� �� �������� ����� Endpoint.stop():
				endpoint.stop();
			}
		}
	}

}
