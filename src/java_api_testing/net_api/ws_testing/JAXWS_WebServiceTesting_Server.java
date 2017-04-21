package java_api_testing.net_api.ws_testing;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class JAXWS_WebServiceTesting_Server {
	private MyTransferObject mCurrentTransferObject = new MyTransferObject();

	@WebMethod
	public void setMyTransferObject ( MyTransferObject to_set ) {
		mCurrentTransferObject = to_set;
	}
	
	@WebMethod
	public MyTransferObject getMyTransferObject () {
		return mCurrentTransferObject;
	}
	
	public static void main(String[] args) {
		/*Endpoint endpoint =*/ Endpoint.publish( "http://localhost:8080/myservice", new JAXWS_WebServiceTesting_Server ());
		
		System.out.println("Service is published!");
	}

}
