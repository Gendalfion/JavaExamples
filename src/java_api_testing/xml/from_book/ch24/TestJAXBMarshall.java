package java_api_testing.xml.from_book.ch24;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * ������������ ������������� ���������� Java API for XML Binding, JAXB (����� <b>javax.xml.bind.*</b>),
 * ��� �������������� ���������� ������������ ����������� ������� � ��������� XML
 * 
 * <p> <a href="http://docs.oracle.com/javaee/5/api/javax/xml/bind/annotation/package-summary.html">�������� ��������� JAXB</a>
 */
public class TestJAXBMarshall
{
	public static void main( String [] args ) throws JAXBException
    {
		// ������� ������ ������ �� ����� �������� Inventory, Animal, FoodRecipe:
		Inventory inventory = new Inventory();
		
        Animal firstAnimal = new Animal( Animal.AnimalClass.mammal, "Song Fang", "Giant Panda", "China", "Friendly", 33.0, "Bamboo" );
        
        FoodRecipe recipe = new FoodRecipe();
        recipe.name = "Gorilla Chow";
        recipe.ingredient.addAll( Arrays.asList( "fruit", "shoots", "leaves" ) );
        Animal secondAnimal = new Animal( Animal.AnimalClass.mammal, "Cocoa", "Gorilla", "Ceneral Africa", "Know-it-all", 45.0, recipe );
        
        inventory.animal.add( firstAnimal );
		inventory.animal.add( secondAnimal );

		// ��������� �������������� ������ ������ �� ����������� ������ � XML-��������:
        marshall( inventory, System.out );
	}

	/**
	 * ���������, ����������� �������������� ��������� ������� � ��� XML-������������� ���������� ���������� JAXB
	 * @param jaxbObject - ������������ ������, ��������������� ������������ JAXB ��� ���������� ��������������
	 * @param out - ����� ������, � ������� ����� ������� ��������� ��������������
	 * @throws JAXBException
	 */
    public static void marshall( Object jaxbObject, OutputStream out ) throws JAXBException
    {
    	// ����� javax.xml.bind.JAXBContext.newInstance(...) ��������� ����������� ���������� ��� �������
    	// � �������� ��������� ��� �������� XML-���������. ������������� ��, ���
    	// �������������� �� ������ ���������� �����, �� � ��� ������, ���������� ��������� � ������ �������:
        JAXBContext context = JAXBContext.newInstance( jaxbObject.getClass() );
        Marshaller marshaller = context.createMarshaller();
        
        // ������������� ������������� ����� XML-���������:
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        // ���������� XML-�������� �� ������ ������ jaxbObject � ����� ������:
        marshaller.marshal(jaxbObject, out);
    }
}
