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
 * ������������ ������ � XML-����������, ���������� ��������� XInclude
 * 
 * <p> <b>XInclude</b> - ��� ������� �������� ������� �� ������ XML-��������� � ������
 * 
 *  <p> ������ ����� � XInclude: <a href="chapter.xml">chapter.xml</a>
 */
public class PrintXInclude {
	public static void main( String [] args ) throws Exception 
	{
		// ������� ���������� ���� ������ ����� ��������� ����������� ���� � �������� XInclude:
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
		// ��� ��������� �������� XInclude �� ������ �������� ��������� ����������� ����:
		factory.setNamespaceAware( namespaceAwareCB.isSelected() );
		// � ����� ��������� ����� �������� XInclude:
		factory.setXIncludeAware( xincludeAwareCB.isSelected() );
		
		DocumentBuilder parser = factory.newDocumentBuilder();
		
		System.out.println( "*** DocumentBuilder options: ***" );
		System.out.println( "isXIncludeAware:  " + parser.isXIncludeAware() );
		System.out.println( "isNamespaceAware: " + parser.isNamespaceAware() );
		
		// ��������� ������� XML-���������:
		URL urlToXML = PrintXInclude.class.getResource("chapter.xml");
		Document document = parser.parse( urlToXML.openStream()
			// ��������� ������� ���������� ��� ���������� ������������� ����� ������ XML-���������:
			, urlToXML.getPath() );
		
		// ������� ���������� ������ Document � ����������� ����� ������:
		System.out.println( "\n*** Let's print the Document: ***" );
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform( new DOMSource( document ), new StreamResult( System.out ) );
	}
}
