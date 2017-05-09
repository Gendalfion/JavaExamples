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
 * ������������ ������������� XPath API ��� ���������� ����� XML-���������
 * 
 * <p> XPath - ��� ���� �������� ����� ���������� ���������, ����������� ��������
 * ����� XML-���������, ��������������� ������������� �������
 * 
 * <p> ������ ���������� ��������������� � Java ��� ������ ������ <b>javax.xml.xpath.*</b>
 */
public class TestXPath {

	public static void main( String [] args ) throws Exception
	{
        String expression = ""; 
		if ( args.length >= 1 ) { expression = args[0]; }
		
		String filename = "zooinventory.xml";
		if ( args.length >= 2 ) { filename = args[1]; }
		
		if ( expression.isEmpty() ) {
			// ������� ��������� XPath:
			final String [] expExamples = {
					
				// ��������� ���������, ��������� � ������ ���������:
				"/inventory/animal",				// ��� ���� "animal" � �������� �������� "inventory"
				"/inventory/animal/food",			// ��� ���� "food" ������ "animal" � �������� �������� "inventory"
				"//name",							// ��� ���� "name" ���-���� � ���������
				"//@animalClass",					// ��� �������� "animalClass" ���-���� � ���������
				"//animal/*",						// ��� �������� �������� ����� "animal" ���-���� � ���������
				"/inventory/animal/.",				// ������� ���� (��� ���� ������ "animal" � �������� �������� "inventory")
				"/inventory/animal/..",				// ������������ ���� (��� ���� � �������� �������� "inventory")
				
				// ������������� ����������:
				"/inventory/animal[1]",				// ������ ������� �� �������� ����� "animal" � �������� �������� "inventory"
				"//animal[@animalClass=\"mammal\"]",// ��� ���� "animal" � ��������� �������� "animalClass" = "mammal"
				"//animal[name=\"Cocoa\"]",			// ��� ���� "animal" � �������� ����� "name", ������ ������ "Cocoa"
				"//animal[weight > 35]/name",		// ��� ���� "name" ������ "animal", ��� ���� "weight" � ���� ����� ������ 35
				"//animal[weight>20 and weight<44]",// ��� ���� "name" ������ "animal", ��� ���� "weight" ������ 20 � ������ 44
				
				// ������������� �������:
				"/inventory/comment()",					// ��� ���������� ��������� ���� "inventory"
				"/inventory/animal[last()]",			// ��������� ���� "animal" � �������� �������� "inventory"
				"//foodRecipe[count(ingredient)>2]/..",	// ��� �������� ���� "foodRecipe" ���-�� "ingredient" �������� ������ 2 
				"//animal[starts-with(name,\"C\")]",	// ��� ���� "animal", ��� ������� "name" ���������� �� ������ "C"
				"//animal[contains(habitat,\"Africa\")]"// ��� ���� "animal", ��� ������� "habitat" �������� ������ "Africa"
			};
			
			// �������������� �������� ����������� ����:
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
			
			// ������� ���������� ���� ��� ����� XPath-���������:
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
		
		// ������� �����, ��������������� ��������� XPath-���������:
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList elements = (NodeList)xpath.evaluate( expression, inputSource, XPathConstants.NODESET );
		
		// �������� ���������� ������� (������ DOM-���������):
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
	 * ������ ����������� �������� DOM-��������� � ����������� ����� ������ � ������� XML
	 * @param element - ������� DOM-���������
	 * @throws TransformerException
	 */
	public static void printXML( Element element ) throws TransformerException
	{
		// ���������� javax.xml.transform.Transformer ��� �������������� Element 
		// � ��������� XML-�������������:
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
		transformer.transform( new DOMSource(element), new StreamResult(System.out) );
		System.out.println();
	}
}
