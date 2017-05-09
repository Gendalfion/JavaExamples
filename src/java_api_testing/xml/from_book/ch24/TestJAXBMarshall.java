package java_api_testing.xml.from_book.ch24;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Arrays;

/**
 * ƒемонстраци€ использовани€ технологии Java API for XML Binding, JAXB (пакет <b>javax.xml.bind.*</b>),
 * дл€ преобразовани€ помеченных специальными аннотаци€ми классов в документы XML
 * 
 * <p> <a href="http://docs.oracle.com/javaee/5/api/javax/xml/bind/annotation/package-summary.html">ќписание аннотаций JAXB</a>
 */
public class TestJAXBMarshall
{
	public static void main( String [] args ) throws JAXBException
    {
		// —оздаем модель данных из наших объектов Inventory, Animal, FoodRecipe:
		Inventory inventory = new Inventory();
		
        Animal firstAnimal = new Animal( Animal.AnimalClass.mammal, "Song Fang", "Giant Panda", "China", "Friendly", 33.0, "Bamboo" );
        inventory.animal.add( firstAnimal );
        FoodRecipe recipe = new FoodRecipe();
        recipe.name = "Gorilla Chow";
        recipe.ingredient.addAll( Arrays.asList( "fruit", "shoots", "leaves" ) );
        
        Animal secondAnimal = new Animal( Animal.AnimalClass.mammal, "Cocoa", "Gorilla", "Ceneral Africa", "Know-it-all", 45.0, recipe );
		inventory.animal.add( secondAnimal );

		// ¬ыполн€ем преобразование модели данных из оперативной пам€ти в XML-документ:
        marshall( inventory );
	}

	/**
	 * ѕроцедура, выполн€юща€ преобразование заданного объекта в его XML-представление средствами технологии JAXB
	 * @param jaxbObject - произвольный объект, соответствующий спецификации JAXB дл€ выполнени€ преобразовани€
	 * @throws JAXBException
	 */
    public static void marshall( Object jaxbObject ) throws JAXBException
    {
    	// ћетод javax.xml.bind.JAXBContext.newInstance(...) выполн€ет регистрацию переданных ему классов
    	// в качестве контекста дл€ прив€зки XML-документа. ѕримечательно то, что
    	// регистрируетс€ не только переданный класс, но и все классы, статически св€занные с данным классом:
        JAXBContext context = JAXBContext.newInstance( jaxbObject.getClass() );
        Marshaller marshaller = context.createMarshaller();
        
        // ”станавливаем удобочитаемый вывод XML-документа:
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        // √енерируем XML-документ на основе модели jaxbObject в стандартный поток вывода:
        marshaller.marshal(jaxbObject, System.out);
    }
}
