package java_api_testing.xml.from_book.ch24;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * ƒемонстраци€ использовани€ технологии Java API for XML Binding, JAXB (пакет <b>javax.xml.bind.*</b>),
 * дл€ преобразовани€ документа XML в модель данных из Java классов
 * 
 * <p> <a href="http://docs.oracle.com/javaee/5/api/javax/xml/bind/annotation/package-summary.html">ќписание аннотаций JAXB</a>
 */
public class TestJAXBUnmarshall
{
	public static void main( String [] args ) throws JAXBException
    {
		String xmlFileName = "zooinventory_NS.xml";
		if ( args.length >= 1 ) {
			xmlFileName = args[0];
		}
		
		// –егистрируем класс Inventory в контексте JAXB:
        JAXBContext context = JAXBContext.newInstance( Inventory.class );
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        unmarshaller.setSchema(null); // ћы можем установить схему дл€ валидации входного XML-документа
        
        // ќшибки проверки XML документа отлавливаютс€ при помощи обработчика ValidationEventHandler:
        //unmarshaller.setEventHandler( new ValidationEventHandler() {...} );
        
        // ¬ыполн€ем парсинг XML-документа с построением на основе нее модели данных из Java классов:
        Inventory inventory = (Inventory)unmarshaller.unmarshal( TestJAXBUnmarshall.class.getResourceAsStream(xmlFileName) );

        // ѕечатаем содержимое пол€ Inventory.animal в стандартный поток вывода:
		System.out.println( "*** Inventory content read from file \"" + xmlFileName + "\": ***" );
		for( Animal animal : inventory.animal ) {
			System.out.println( animal );
		}
	}
}