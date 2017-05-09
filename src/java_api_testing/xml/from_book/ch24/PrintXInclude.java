package java_api_testing.xml.from_book.ch24;

import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Демонстрация работы с XML-документом, содержащим директивы XInclude
 * 
 * <p> <b>XInclude</b> - это простое средство импорта из одного XML-документа в другой
 * 
 *  <p> Пример файла с XInclude: <a href="chapter.xml">chapter.xml</a>
 */
public class PrintXInclude {
	public static void main( String [] args ) throws Exception 
	{
		// Создаем диалоговое окно выбора опций обработки пространств имен и директив XInclude:
		JCheckBox namespaceAwareCB = new JCheckBox("Namespace Aware", true);
		JCheckBox xincludeAwareCB = new JCheckBox("XInclude Aware", true);
		if ( JOptionPane.showOptionDialog( null
				, new Object [] { "Select XInclude options:", " ", namespaceAwareCB, " ", xincludeAwareCB, " " }
				, "XInclude Options"
				, JOptionPane.OK_CANCEL_OPTION
				, JOptionPane.QUESTION_MESSAGE
				, null, null, null) != JOptionPane.OK_OPTION ) {
			System.exit(0);
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Для обработки директив XInclude мы должны включить обработку пространств имен:
		factory.setNamespaceAware( namespaceAwareCB.isSelected() );
		// а также обработку самих директив XInclude:
		factory.setXIncludeAware( xincludeAwareCB.isSelected() );
		
		DocumentBuilder parser = factory.newDocumentBuilder();
		
		System.out.println( "*** DocumentBuilder options: ***" );
		System.out.println( "isXIncludeAware:  " + parser.isXIncludeAware() );
		System.out.println( "isNamespaceAware: " + parser.isNamespaceAware() );
		
		// Выполняем парсинг XML-документа:
		URL urlToXML = PrintXInclude.class.getResource("chapter.xml");
		Document document = parser.parse( urlToXML.openStream()
			// Указываем базовую директорию для разрешения относительных путей внутри XML-документа:
			, urlToXML.getPath() );
		
		// Выводим полученный объект Document в стандартный поток вывода:
		System.out.println( "\n*** Let's print the Document: ***" );
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform( new DOMSource( document ), new StreamResult( System.out ) );
	}
}
