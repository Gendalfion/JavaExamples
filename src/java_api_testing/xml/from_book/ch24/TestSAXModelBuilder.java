package java_api_testing.xml.from_book.ch24;

import org.xml.sax.*;
import javax.xml.parsers.*;
import java.beans.XMLEncoder;

/**
 * ƒемонстраци€ использовани€ технологии SAX API дл€
 * парсинга XML-данных на низком уровне
 * 
 * <p> ƒанна€ технологи€ предоставл€етс€ в Java при помощи 2-х пакетов:
 * <li> <b>org.xml.sax.*</b> - стандартный пакет интерефейсов дл€ работы с XML от W3C (поддерживаетс€ на нескольких €зыках)
 * <li> <b>javax.xml.parsers.*</b> - реализаци€ интерфейсов синтаксических анализаторов от Java API for XML Processing (JAXP)
 */
public class TestSAXModelBuilder
{
	public static void main( String [] args ) throws Exception
	{
		//  ласс javax.xml.parsers.SAXParserFactory используетс€ дл€
		// доступа к конкретной реализации фабрики парсеров SAX
		// (конкретный класс, который будет загружен, определ€етс€ в процессе
		// выполнени€ путем анализа соответствующих настроек в Java):
		SAXParserFactory factory = SAXParserFactory.newInstance();

        //factory.setValidating( true ); // Use DTD if present

        // ѕолучаем SAX-парсер из фабрики:
		SAXParser saxParser = factory.newSAXParser();
		XMLReader parser = saxParser.getXMLReader();
		// –егистрируем наш обработчик событий синтаксического анализа: 
		SAXModelBuilder mb = new SAXModelBuilder( "java_api_testing.xml.from_book.ch24" );
		parser.setContentHandler( mb );

		// ѕарсим заданный XML-документ из ресурса:
		System.out.println( " *** Parsing XML data with SAXParser: ***");
		parser.parse( new InputSource( TestSAXModelBuilder.class.getResourceAsStream("zooinventory.xml") ) );
		// ѕолучаем корневой элемент XML-документа и приводим его к нашему объекту:
		Inventory inventory = (Inventory)mb.getModel();
		
		// –аботаем с восстановленными из XML-документа объектами:
		System.out.println();
		System.out.println( " *** Printing some Inventory content: ***");
		System.out.println( "Animals = " + inventory.animal );
		Animal cocoa = inventory.animal.get(1);
		FoodRecipe recipe = cocoa.foodRecipe;
		System.out.println( "Recipe = " + recipe );

		//  ласс java.beans.XMLEncoder может быть использован в качестве альтернативы 
		// сериализации объектов при помощи ObjectOutputStream.
		// XMLEncoder кодирует объекты в XML формате в виде последовательности вызовов
		// их get-методов дл€ всех публичных свойств класса.
		// XMLDecoder при парсинге этих методов передаст значени€ из XML-пердставлени€
		// в соотвествующие set-методы дл€ свойств класса.
		System.out.println();
		System.out.println( " *** Printing Inventory to System.out with XMLEncoder: ***");
        XMLEncoder xmle = new XMLEncoder( System.out );
        xmle.writeObject( inventory );
        xmle.close();
	}
}