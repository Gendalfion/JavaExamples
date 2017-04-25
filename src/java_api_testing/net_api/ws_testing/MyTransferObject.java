package java_api_testing.net_api.ws_testing;

/**
 * ������ ����� ������������ ��� �������� ����� �������� � �������� ��� ������ ���������� 
 * Java API for XML Web Services (JAX-WS)
 * 
 * <p> ����� ������������:
 * <li> {@link JAXWS_WebServiceTesting_Client}
 * <li> {@link JAXWS_WebServiceTesting_Server}
 * 
 * @author Lab119Alex
 *
 */
public class MyTransferObject {
	// � �������� ����� ������ �� ������ ������������ ������ ������� ����,
	// ��� ������� ������� �������� � ���������� JAXB
	// (���������: http://docs.oracle.com/javaee/6/tutorial/doc/bnazc.html):
	private String mValue1;
	private String mValue2;
	
	// ������������ ������� ������� ���������� ������������ ��� ����������:
	public MyTransferObject () {
		mValue1 = "Default value 1";
		mValue2 = "Default value 2";
	}
	
	public MyTransferObject ( String value1, String value2 ) {
		mValue1 = value1;
		mValue2 = value2;
	}
	
	// ��� ���� ����� ������ ������������ ������� ������� ������� set � get
	// (�������� ��������� �� ����������������� � ���������� JAX WS):
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
