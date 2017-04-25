package java_api_testing.net_api.ws_testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * ƒемонстраци€ использовани€ технологии Java API for XML Web Services (JAX-WS) со стороны сервера
 * 
 * <p> ƒанный класс определ€ет веб-сервис при помощи аннотаций.
 * 
 * <p> ѕри публикации сервиса JAX-WS API способен автоматически сгенерировать все необходимые файлы 
 * (такие как WSDL), руководству€сь аннотаци€ми в данном классе
 * 
 * <p>  ласс работает в паре с {@link JAXWS_WebServiceTesting_Client}
 * 
 * @author Lab119Alex
 *
 */
@WebService // јннотаци€ WebService помечает класс в качестве обработчика веб-запросов сервиса
public class JAXWS_WebServiceTesting_Server {
	private MyTransferObject mCurrentTransferObject = new MyTransferObject();

	@WebMethod // јннотаци€ WebMethod помечает публикуемый метод удаленного вызова дл€ веб-сервиса
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
			// ѕубликуем веб-сервис по указанному адресу при помощи статического метода Endpoint.publish(...)
			// ¬се сопутствующие документы (такие как WSDL-файл) будут сгенерированы при помощи
			// технологии JAX-WS на основе аннотаций в классе-имплементаторе:
			endpoint = Endpoint.publish( serviceEndpointURL, new JAXWS_WebServiceTesting_Server ());
			
			// ѕроверить работу сервиса можно запросив WSDL по ссылке: <serviceEndpointURL>?WSDL
			
			System.out.println("Service is published on URL: \"" + serviceEndpointURL + "\"");
			System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping the server..." );
		} catch ( Exception exception ) {
			System.err.println( "Server execution exception: " + exception.toString() );
		} finally {
			if ( endpoint != null ) {
				// ƒл€ завершени€ работы сервиса мы вызываем метод Endpoint.stop():
				endpoint.stop();
			}
		}
	}

}
