package java_api_testing.xml.from_book.ch24;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * ������������ ������������� ������ <b>javax.xml.transform.*</b>
 * ��� ���������� �������������� ��������� � ��������������
 * XSLT-��������������
 * 
 * <p> ��. ���� <a href="zooinventory.xsl">zooinventory.xsl</a>
 */
public class XSLTransform 
{
	public static void main( String [] args ) throws Exception
	{
		String xslFile = "zooinventory.xsl";
		//String xslFile = "zooinventory2.xsl";
		if ( args.length >= 1 ) {
			xslFile = args[0];
		}
		
        String xmlFile = "zooinventory.xml";
        if ( args.length >= 2 ) {
			xslFile = args[1];
		}
        
        // ������� ���� �� ��������� ���������� �������:
        File outTmpFile = new File ( System.getProperty("java.io.tmpdir") + "XSLTransform_" + System.currentTimeMillis() + ".tmp");
        outTmpFile.createNewFile();
        outTmpFile.deleteOnExit();
        
        TransformerFactory factory = TransformerFactory.newInstance();
        // ������� ������ javax.xml.transform.Transformer �� ������ XSLT-�������������� �� �����-������� xslFile:
        Transformer transformer = factory.newTransformer( new StreamSource( XSLTransform.class.getResourceAsStream(xslFile) ) );
		StreamSource xmlsource = new StreamSource( XSLTransform.class.getResourceAsStream(xmlFile) );
		
		try ( FileOutputStream outputStream = new FileOutputStream(outTmpFile) ) {
			StreamResult output = new StreamResult( outputStream );
			// ��������� �������������� XSL � ���������� ����� � �������� ����� outputStream:
			transformer.transform( xmlsource, output );
		}
		
		// ������� JTextPane ��� ����������� ���������� �������������� � ���� html-���������:
		JTextPane outPanel = new JTextPane();
		outPanel.setEditable(false);
		// ��������� ��������� �������������� �� ���������� ����� (������ �������� 
		// ����������� ����� ����� JTextPane.setText(...) ������-�� �� �������� ��� html):
		outPanel.setPage( outTmpFile.toURI().toURL() );
		
		// ������� ������� ����������� ������ ��� ����������� �������������� ���������:
		JFrame frame = new JFrame("Transformation of \"" + xmlFile + "\" using \"" + xslFile + "\"...");
	    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    frame.setSize(900, 700);
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().add( new JScrollPane(outPanel) );
	    frame.setVisible(true);
	}
}