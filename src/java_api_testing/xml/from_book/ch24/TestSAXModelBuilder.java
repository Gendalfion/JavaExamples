package java_api_testing.xml.from_book.ch24;

import org.xml.sax.*;
import javax.xml.parsers.*;
import java.beans.XMLEncoder;

/**
 * ������������ ������������� ���������� SAX API ���
 * �������� XML-������ �� ������ ������
 * 
 * <p> ������ ���������� ��������������� � Java ��� ������ 2-� �������:
 * <li> <b>org.xml.sax.*</b> - ����������� ����� ������������ ��� ������ � XML �� W3C (�������������� �� ���������� ������)
 * <li> <b>javax.xml.parsers.*</b> - ���������� ����������� �������������� ������������ �� Java API for XML Processing (JAXP)
 */
public class TestSAXModelBuilder
{
	public static void main( String [] args ) throws Exception
	{
		// ����� javax.xml.parsers.SAXParserFactory ������������ ���
		// ������� � ���������� ���������� ������� �������� SAX
		// (���������� �����, ������� ����� ��������, ������������ � ��������
		// ���������� ����� ������� ��������������� �������� � Java):
		SAXParserFactory factory = SAXParserFactory.newInstance();

        //factory.setValidating( true ); // Use DTD if present

        // �������� SAX-������ �� �������:
		SAXParser saxParser = factory.newSAXParser();
		XMLReader parser = saxParser.getXMLReader();
		// ������������ ��� ���������� ������� ��������������� �������: 
		SAXModelBuilder mb = new SAXModelBuilder( "java_api_testing.xml.from_book.ch24" );
		parser.setContentHandler( mb );

		// ������ �������� XML-�������� �� �������:
		System.out.println( " *** Parsing XML data with SAXParser: ***");
		parser.parse( new InputSource( TestSAXModelBuilder.class.getResourceAsStream("zooinventory.xml") ) );
		// �������� �������� ������� XML-��������� � �������� ��� � ������ �������:
		Inventory inventory = (Inventory)mb.getModel();
		
		// �������� � ���������������� �� XML-��������� ���������:
		System.out.println();
		System.out.println( " *** Printing some Inventory content: ***");
		System.out.println( "Animals = " + inventory.animal );
		Animal cocoa = inventory.animal.get(1);
		FoodRecipe recipe = cocoa.foodRecipe;
		System.out.println( "Recipe = " + recipe );

		// ����� java.beans.XMLEncoder ����� ���� ����������� � �������� ������������ 
		// ������������ �������� ��� ������ ObjectOutputStream.
		// XMLEncoder �������� ������� � XML ������� � ���� ������������������ �������
		// �� get-������� ��� ���� ��������� ������� ������.
		// XMLDecoder ��� �������� ���� ������� �������� �������� �� XML-�������������
		// � �������������� set-������ ��� ������� ������.
		System.out.println();
		System.out.println( " *** Printing Inventory to System.out with XMLEncoder: ***");
        XMLEncoder xmle = new XMLEncoder( System.out );
        xmle.writeObject( inventory );
        xmle.close();
	}
}