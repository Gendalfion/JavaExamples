package java_api_testing.xml.from_book.ch24;

import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * ������������ ������������� ������ javax.xml.transform.*
 * ��� ������ DOM-������� Document � ����� ������ � ������� XML
 * 
 * <p> ����� ��������������� ������������� DOM API ��� ���������
 * ��������� �� ����� DTD
 */
public class PrintDOM {
	public static void main( String [] args ) throws Exception 
	{
		// ������� ���������� ���� ������ ����� �������� � ����������� xml-���������:
		JCheckBox validatingCB = new JCheckBox("DTD Validating");
		
		JRadioButton xml1RB = new JRadioButton("zooinventory.xml", true);	xml1RB.setActionCommand(xml1RB.getText());
		JRadioButton xml2RB = new JRadioButton("zooinventory_DTD.xml"); 	xml2RB.setActionCommand(xml2RB.getText());
		ButtonGroup xmlBG = new ButtonGroup(); xmlBG.add(xml1RB); xmlBG.add(xml2RB);
		
		if ( JOptionPane.showOptionDialog( null
				, new Object [] { "Select schema validation options:", validatingCB, " "
						, "Choose XML file to parse:", xml1RB, xml2RB, " " }
				, "XInclude Options"
				, JOptionPane.OK_CANCEL_OPTION
				, JOptionPane.QUESTION_MESSAGE
				, null, null, null) != JOptionPane.OK_OPTION ) {
			System.exit(0);
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		// ������������� ����� ���������/���������� ����������� ��������� �� ����� DTD:
		factory.setValidating(validatingCB.isSelected());
		System.out.println( "*** XML-validation: " + (validatingCB.isSelected() ? "on" : "off") +  " ***\n" );
		
		DocumentBuilder parser =  factory.newDocumentBuilder();
		
		// ���������� ���������� ������ �������� DOM-���������:
		parser.setErrorHandler( new ErrorHandler() {
			@Override public void warning(SAXParseException arg0) throws SAXException {
				System.err.println( "ErrorHandler.warning: " + arg0 );
			}
			
			@Override public void fatalError(SAXParseException arg0) throws SAXException {
				System.err.println( "ErrorHandler.fatalError: " + arg0 );
			}
			
			@Override public void error(SAXParseException arg0) throws SAXException {
				System.err.println( "ErrorHandler.error: " + arg0 );
			}
		} );
		
		// ��������� ������� ��������� XML-���������:
		String selectedXMLFile = xmlBG.getSelection().getActionCommand();
		URL urlToXML = PrintDOM.class.getResource(selectedXMLFile);
		Document document = parser.parse( urlToXML.openStream()
			// ��������� ������� ���������� ��� ���������� ������������� ����� ������ XML-���������:
			, urlToXML.getPath() );
		
		// ������� DOM-������ document ��� ������ ������ javax.xml.transform.Transformer:
		System.out.println( "*** XML-file \"" + selectedXMLFile + "\" parsed, document content: ***" );
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform( new DOMSource( document ), new StreamResult( System.out ) );
	}
}
