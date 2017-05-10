package java_api_testing.xml.from_book.ch24;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * ������������ ������������� ���������� Java API for XML Binding, JAXB (����� <b>javax.xml.bind.*</b>),
 * ��� �������������� ��������� XML � ������ ������ �� Java �������
 * 
 * <p> <a href="http://docs.oracle.com/javaee/5/api/javax/xml/bind/annotation/package-summary.html">�������� ��������� JAXB</a>
 */
public class TestJAXBUnmarshall
{
	public static void main( String [] args ) throws JAXBException
    {
		String xmlFileName = "zooinventory_NS.xml";
		if ( args.length >= 1 ) {
			xmlFileName = args[0];
		}
		
		// ������������ ����� Inventory � ��������� JAXB:
        JAXBContext context = JAXBContext.newInstance( Inventory.class );
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        unmarshaller.setSchema(null); // �� ����� ���������� ����� ��� ��������� �������� XML-���������
        
        // ������ �������� XML ��������� ������������� ��� ������ ����������� ValidationEventHandler:
        //unmarshaller.setEventHandler( new ValidationEventHandler() {...} );
        
        // ��������� ������� XML-��������� � ����������� �� ������ ��� ������ ������ �� Java �������:
        Inventory inventory = (Inventory)unmarshaller.unmarshal( TestJAXBUnmarshall.class.getResourceAsStream(xmlFileName) );

        // �������� ���������� ���� Inventory.animal � ����������� ����� ������:
		System.out.println( "*** Inventory content read from file \"" + xmlFileName + "\": ***" );
		for( Animal animal : inventory.animal ) {
			System.out.println( animal );
		}
	}
}