package java_api_testing.xml.from_book.ch24;

import java.io.IOException;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;
 
/**
 * ������������ ������������� ��������� ���������� JDOM ���
 * �������� XML-��������� (����� org.jdom2.*)
 * 
 * <p> ������ ���������� ���������� �� DOM API ����� �������
 * �����������, ������������ ����������� �������� ����� Java
 * (������, ���������� �� ���������� �� ���������� DOM �� w3c)
 * 
 * <p> ������� ���������� ����� � ����� �������������: <a href="http://www.jdom.org/">www.jdom.org</a>
 */
public class TestJDOM {
    public static void main(String[] args) throws JDOMException, IOException {
        // ��������� ������� XML-����� (��� ��������� ����� ����� ��������� � ����������� ������, ��� � DOM):
        SAXBuilder builder = new SAXBuilder();
        Document jdomDoc = builder.build( TestJDOM.class.getResource("zooinventory.xml") );
 
        // �������� ��� ���������:
        System.out.println("JDOM.getDocType(): " + jdomDoc.getDocType());
 
        // �������� ������ �� �������� ������� XML-���������:
        Element root_elem = jdomDoc.getRootElement();
        
        // ������� ���������� ��������� �������� � �������, ������� �� JSON:
        System.out.println("\n*** Let's print document content in JSON-like format: ***");
        printJSONLike ( root_elem, 0 );
 
        // ������� ��� ���������� � ���������, ��������� ��������������� ������:
        System.out.println("\n*** Let's print document comments: ***");
        IteratorIterable<Comment> comments = root_elem.getDescendants( Filters.comment() );
        while ( comments.hasNext() ) {
            Comment comment = comments.next();
            System.out.println(comment);
        }
    }
    
    private static void printJSONLike ( Element elem, int stack_depth ) {
    	// �������� �������� ��� �������� ��������:
    	String attributes = "";
    	if ( elem.getAttributes().size() > 0 ) {
    		attributes = " " + elem.getAttributes().toString();
    	}
    	
    	if ( elem.getChildren().size() <= 0 ) {
    		// �������� ��������� ���������� �������� ��������, ���� ������� �� ����� ��������:
    		println ( stack_depth, String.format("\"%s\"%s: \"%s\"", elem.getName(), attributes, elem.getValue()) );
    	} else {
    		// ���������� ��������, ����� ������� ���� �������� ��� �������� ��������:
    		println ( stack_depth, String.format("\"%s\"%s: {", elem.getName(), attributes) );
    		for ( Element child : elem.getChildren() ) {
    			printJSONLike ( child, stack_depth + 1);
    		}
    		println ( stack_depth, "}");
    	}
    }
    
    /**
     * �������� ������ ������ � �������� �������� � ����������� ����� ������
     * @param space - ������ ������� (������ ������� ������� ����� 3-� ��������)
     * @param line - ������ ������ ��� ������
     */
    private static void println ( int space, String line ) {
		System.out.println( String.format( "%" + ( (space > 0)?(String.format("%1$d.%1$ds", space * 3)):("s") ) + "%s", "", line) );
	}
}
