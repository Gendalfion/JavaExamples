package java_api_testing.xml.from_book.ch24;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * Демонстрация использования пакета <b>javax.xml.transform.*</b>
 * для выполнения преобразования документа с использованием
 * XSLT-преобразования
 * 
 * <p> См. файл <a href="zooinventory.xsl">zooinventory.xsl</a>
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
        
        // Создаем файл во временной директории системы:
        File outTmpFile = new File ( System.getProperty("java.io.tmpdir") + "XSLTransform_" + System.currentTimeMillis() + ".tmp");
        outTmpFile.createNewFile();
        outTmpFile.deleteOnExit();
        
        TransformerFactory factory = TransformerFactory.newInstance();
        // Создаем объект javax.xml.transform.Transformer на основе XSLT-преобразования из файла-ресурса xslFile:
        Transformer transformer = factory.newTransformer( new StreamSource( XSLTransform.class.getResourceAsStream(xslFile) ) );
		StreamSource xmlsource = new StreamSource( XSLTransform.class.getResourceAsStream(xmlFile) );
		
		try ( FileOutputStream outputStream = new FileOutputStream(outTmpFile) ) {
			StreamResult output = new StreamResult( outputStream );
			// Выполняем преобразования XSL и направляем вывод в файловый поток outputStream:
			transformer.transform( xmlsource, output );
		}
		
		// Создаем JTextPane для отображения результата преобразования в виде html-странички:
		JTextPane outPanel = new JTextPane();
		outPanel.setEditable(false);
		// Загружаем результат преобразования из временного файла (прямая загрузка 
		// содержимого через метод JTextPane.setText(...) почему-то не работает для html):
		outPanel.setPage( outTmpFile.toURI().toURL() );
		
		// Создаем простое графическое окошко для отображения сформированной странички:
		JFrame frame = new JFrame("Transformation of \"" + xmlFile + "\" using \"" + xslFile + "\"...");
	    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    frame.setSize(900, 700);
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().add( new JScrollPane(outPanel) );
	    frame.setVisible(true);
	}
}