package java_api_testing.rmi.client;

import java.io.Serializable;

import java_api_testing.rmi.Request;

/**
 * Запрос на обработку текстовых данных. Используется в классе:
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 *
 */
public class DataProcessingRequest implements Request {

	private static final long serialVersionUID = 1L;
	
	private String mDataToProcess;
	
	public DataProcessingRequest ( String data ) {
		mDataToProcess = data;
	}

	@Override public Serializable processRequest() {
		try {
			Thread.sleep( 5000 );
		} catch ( InterruptedException ie ) {}
		
		return mDataToProcess.toUpperCase();
	}

}
