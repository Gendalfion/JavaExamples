package java_api_testing.net_api.obj_prot_testing;

import java.io.Serializable;

/**
 * Запрос на обработку данных на сервере ObjectProtocol_Server
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
		return mDataToProcess.toUpperCase();
	}

}
