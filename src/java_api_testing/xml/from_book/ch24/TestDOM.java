package java_api_testing.xml.from_book.ch24;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * ������������ ������������� ���������� DOM API ���
 * �������� XML-���������
 * 
 * <p> ������ ���������� ��������������� � Java ��� ������ 2-� �������:
 * <li> <b>org.w3c.dom.*</b> - ����������� ����� ������������ ��� ������ � XML �� W3C (�������������� �� ���������� ������)
 * <li> <b>javax.xml.parsers.*</b> - ���������� ����������� �������������� ������������ �� Java API for XML Processing (JAXP)
 */
public class TestDOM
{
	public static void main( String [] args ) throws Exception
	{
		// ����������� �������� XML-��������� ��� ������ DOM API �������� ����� org.w3c.dom.Document:
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document document = parser.parse( TestSAXModelBuilder.class.getResourceAsStream("zooinventory.xml") );

		// ����� Document ������������ ������ ���������� XML-��������� (�. �. �� ��������� ����������� � ��. ������):
		Element inventory = document.getDocumentElement();
		NodeList animals = inventory.getElementsByTagName("animal");
		System.out.println("Animals = ");
		for( int i = 0; i < animals.getLength(); i++ ) {
            Element item = (Element)animals.item( i );
            String name 		= item.getElementsByTagName( "name" ).item( 0 ).getTextContent();
            String species 		= item.getElementsByTagName( "species" ).item( 0 ).getTextContent();
            String animalClass 	= item.getAttribute( "animalClass" );
			System.out.println( "  " + name + " (" + animalClass + ", " + species + ")" );
		}

        Element cocoa = (Element)animals.item( 1 );
        Element recipe = (Element)cocoa.getElementsByTagName( "foodRecipe" ).item( 0 );
        String recipeName = recipe.getElementsByTagName( "name" ).item( 0 ).getTextContent();
		System.out.println( "Recipe = " + recipeName );
		NodeList ingredients = recipe.getElementsByTagName("ingredient");
		for(int i=0; i<ingredients.getLength(); i++) {
			System.out.println( "  " + ingredients.item( i ).getTextContent() );
        }
	}
}
