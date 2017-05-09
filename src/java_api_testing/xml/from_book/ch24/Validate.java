package java_api_testing.xml.from_book.ch24;

import javax.xml.XMLConstants;
import javax.xml.validation.*;
import org.xml.sax.*;

import javax.xml.transform.sax.SAXSource;

/**
 * ������������ ������������� ������ <b>javax.xml.validation.*</b>
 * ��� ���������� ��������� ��������� XML-���������
 */
public class Validate 
{
	public static void main( String [] args ) throws Exception
	{
        String xmlFile = "zooinventory.xml";
        if ( args.length >= 1 ) {
        	xmlFile = args[0];
        }
        
        String schemaFile = "zooinventory.xsd";	// ���� �� ������ W3C XML Schema
        //String schemaFile = "zooinventory.dtd"; // ���� �� ������ DTD
        if ( args.length >= 2 ) {
        	schemaFile = args[1];
        }
	
		// ���������� ��� ����� ����������� �� ���������� ����� �����:
		String schemaType = ( schemaFile.toLowerCase().endsWith("dtd") ?
			XMLConstants.XML_DTD_NS_URI :		 // � ����������� �������� JDK ����������� �� ����� DTD �� ��������������! 
			XMLConstants.W3C_XML_SCHEMA_NS_URI );// �������, �� ����� �������������� ���� �� ����� W3C XML Schema...

		// ������� ��������� �� ������ ������� ����:
		SchemaFactory factory =  SchemaFactory.newInstance( schemaType );
		Schema schema = factory.newSchema( Validate.class.getResource(schemaFile) );
		Validator validator = schema.newValidator();

		// ������������� ��� ���������� ������ ��� ��������������� ���������:
		BooleanWrapper has_error = new BooleanWrapper();
		ErrorHandler errHandler = new ErrorHandler() {
			public void error( SAXParseException e ) 		{ System.err.println(e); has_error.value = true; }
			public void fatalError( SAXParseException e ) 	{ System.err.println(e); has_error.value = true; }
			public void warning( SAXParseException e ) 		{ System.err.println(e); }
		};
		validator.setErrorHandler( errHandler );

		try {
			// ������������ �������� �� XML-�����:
			validator.validate( new SAXSource( new InputSource( Validate.class.getResourceAsStream(xmlFile) ) ) );
			if ( !has_error.value ) {
				System.out.println("*** File \"" + xmlFile + "\" validation successfull (schema: \"" + schemaFile + "\")! ***");
			}
		} catch ( SAXException e ) {
			System.err.println(e); // ������ ��� �����������
		}
	}
	
	static private class BooleanWrapper {
		boolean value = false;
	}
}
