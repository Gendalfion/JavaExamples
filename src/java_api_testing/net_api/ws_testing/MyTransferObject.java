package java_api_testing.net_api.ws_testing;

/**
 * Данный класс используется для передачи между клиентом и сервером при помощи технологии 
 * Java API for XML Web Services (JAX-WS)
 * 
 * <p> Класс используется:
 * <li> {@link JAXWS_WebServiceTesting_Client}
 * <li> {@link JAXWS_WebServiceTesting_Server}
 * 
 * @author Lab119Alex
 *
 */
public class MyTransferObject {
	// В качестве полей класса мы должны использовать только простые типы,
	// для которых имеется привязка в технологии JAXB
	// (подробнее: http://docs.oracle.com/javaee/6/tutorial/doc/bnazc.html):
	private String mValue1;
	private String mValue2;
	
	// Спецификация требует наличия публичного конструктора без аргументов:
	public MyTransferObject () {
		mValue1 = "Default value 1";
		mValue2 = "Default value 2";
	}
	
	public MyTransferObject ( String value1, String value2 ) {
		mValue1 = value1;
		mValue2 = value2;
	}
	
	// Для всех полей класса спецификация требует наличия методов set и get
	// (передача поведения не предусматривается в технологии JAX WS):
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
