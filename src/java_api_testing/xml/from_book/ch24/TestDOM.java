package java_api_testing.xml.from_book.ch24;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * ƒемонстраци€ использовани€ технологии DOM API дл€
 * парсинга XML-документа
 * 
 * <p> ƒанна€ технологи€ предоставл€етс€ в Java при помощи 2-х пакетов:
 * <li> <b>org.w3c.dom.*</b> - стандартный пакет интерефейсов дл€ работы с XML от W3C (поддерживаетс€ на нескольких €зыках)
 * <li> <b>javax.xml.parsers.*</b> - реализаци€ интерфейсов синтаксических анализаторов от Java API for XML Processing (JAXP)
 */
public class TestDOM
{
	public static void main( String [] args ) throws Exception
	{
		// –езультатом парсинга XML-документа при помощи DOM API €вл€етс€ класс org.w3c.dom.Document:
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document document = parser.parse( TestDOM.class.getResourceAsStream("zooinventory.xml") );

		//  ласс Document представл€ет полное содержимое XML-документа (т. е. он полностью загружаетс€ в оп. пам€ть):
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

