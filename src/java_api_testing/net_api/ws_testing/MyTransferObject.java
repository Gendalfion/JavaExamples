package java_api_testing.net_api.ws_testing;

public class MyTransferObject {
	private String mValue1;
	private String mValue2;
	
	public MyTransferObject () {
		mValue1 = "Default value 1";
		mValue2 = "Default value 2";
	}
	
	public MyTransferObject ( String value1, String value2 ) {
		mValue1 = value1;
		mValue2 = value2;
	}
	
	public void setValue1 ( String value ) {
		mValue1 = value;
	}
	
	public String getValue1 () {
		return mValue1;
	}
	
	public void setValue2 ( String value ) {
		mValue2 = value;
	}
	
	public String getValue2 () {
		return mValue2;
	}
}
