package java_api_testing.xml.from_book.ch24;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.xpath.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Демонстрация использования XPath API для фильтрации узлов XML-документа
 * 
 * <p> XPath - это язык подобный языку регулярных выражений, позволяющий выбирать
 * части XML-документа, соответствующие определенному шаблону
 * 
 * <p> Данная технология предоставляется в Java при помощи пакета <b>javax.xml.xpath.*</b>
 */
public class TestXPath {

	public static void main( String [] args ) throws Exception
	{
        String expression = ""; 
		if ( args.length >= 1 ) { expression = args[0]; }
		
		String filename = "zooinventory.xml";
		if ( args.length >= 2 ) { filename = args[1]; }
		
		if ( expression.isEmpty() ) {
			// Примеры выражений XPath:
			final String [] expExamples = {
					
				// Синтаксис выражений, связанный с узлами документа:
				"/inventory/animal",				// Все узлы "animal" в корневом элементе "inventory"
				"/inventory/animal/food",			// Все узлы "food" внутри "animal" в корневом элементе "inventory"
				"//name",							// Все узлы "name" где-либо в документе
				"//@animalClass",					// Все атрибуты "animalClass" где-либо в документе
				"//animal/*",						// Все дочерние элементы узлов "animal" где-либо в документе
				"/inventory/animal/.",				// Текущий узел (все узлы внутри "animal" в корневом элементе "inventory")
				"/inventory/animal/..",				// Родительский узел (все узлы в корневом элементе "inventory")
				
				// Использование предикатов:
				"/inventory/animal[1]",				// Первый элемент из дочерних узлов "animal" в корневом элементе "inventory"
				"//animal[@animalClass=\"mammal\"]",// Все узлы "animal" с значением атрибута "animalClass" = "mammal"
				"//animal[name=\"Cocoa\"]",			// Все узлы "animal" с дочерним узлом "name", равным тексту "Cocoa"
				"//animal[weight > 35]/name",		// Все узлы "name" внутри "animal", чей узел "weight" в виде числа больше 35
				"//animal[weight>20 and weight<44]",// Все узлы "name" внутри "animal", чей узел "weight" больше 20 и меньше 44
				
				// Использование функций:
				"/inventory/comment()",					// Все коментарии корневого узла "inventory"
				"/inventory/animal[last()]",			// Последний узел "animal" в корневом элементе "inventory"
				"//foodRecipe[count(ingredient)>2]/..",	// Все родители узла "foodRecipe" кол-во "ingredient" которого больше 2 
				"//animal[starts-with(name,\"C\")]",	// Все узлы "animal", чей элемент "name" начинается со строки "C"
				"//animal[contains(habitat,\"Africa\")]"// Все узлы "animal", чей элемент "habitat" содержит строку "Africa"
			};
			
			// Подготавливаем элементы диалогового окна:
			JComboBox<String> expChooser = new JComboBox<>(expExamples);
			expChooser.setMaximumRowCount(20);
			JTextField expField = new JTextField();
			expChooser.addActionListener( new ActionListener() {
					@Override public void actionPerformed(ActionEvent arg0) {
						expField.setText( (String)expChooser.getSelectedItem() );
					}
				}
			);
			expChooser.setSelectedIndex(0);
			
			// Создаем диалоговое окно для ввода XPath-выражения:
			if ( JOptionPane.showOptionDialog( null
					, new Object [] { "Choose XPath expression:", expChooser, " ", "or enter your own expression:", expField }
					, "XPath Expression"
					, JOptionPane.OK_CANCEL_OPTION
					, JOptionPane.QUESTION_MESSAGE
					, null, null, null) != JOptionPane.OK_OPTION ) {
				System.exit(0);
			}
			if ( !expField.getText().isEmpty() ) {
				expression = expField.getText();
			} else {
				expression = (String)expChooser.getSelectedItem();
			}
		}
		
		InputSource inputSource = new InputSource( TestXPath.class.getResourceAsStream(filename) );

		System.out.println( String.format("*** File name:        \"%s\" ***", filename) );
		System.out.println( String.format("*** XPath expression: \"%s\" ***\n", expression) );
		
		// Выборка узлов, соответствующих заданному XPath-выражению:
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList elements = (NodeList)xpath.evaluate( expression, inputSource, XPathConstants.NODESET );
		
		// Печатаем полученную выборку (список DOM-элементов):
		System.out.println( String.format("*** Found %d matching elements: ***", elements.getLength()) );
		for( int i = 0; i < elements.getLength(); i++ ) {
			System.out.println( String.format("\n*** Element #%d XML:", i+1) );
			if ( elements.item(i) instanceof Element ) {
				printXML( (Element)elements.item(i) );
			} else {
				System.out.println( elements.item(i) );
			}
		}
	}
	
	/**
	 * Печать содержимого элемента DOM-документа в стандартный поток вывода в формате XML
	 * @param element - элемент DOM-документа
	 * @throws TransformerException
	 */
	public static void printXML( Element element ) throws TransformerException
	{
		// Используем javax.xml.transform.Transformer для преобразования Element 
		// в текстовое XML-представление:
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
		transformer.transform( new DOMSource(element), new StreamResult(System.out) );
		System.out.println();
	}
}
