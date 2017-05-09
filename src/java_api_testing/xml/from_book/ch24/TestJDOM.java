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
 * Демонстрация использования сторонней библиотеки JDOM для
 * парсинга XML-документа (пакет org.jdom2.*)
 * 
 * <p> Данная библиотека отличается от DOM API более удобным
 * интерфейсом, использующим продвинутые средства языка Java
 * (однако, библиотека не совместима со стандартом DOM от w3c)
 * 
 * <p> Скачать библиотеку можно с сайта разработчиков: <a href="http://www.jdom.org/">www.jdom.org</a>
 */
public class TestJDOM {
    public static void main(String[] args) throws JDOMException, IOException {
        // Вполяняем парсинг XML-файла (вся структура файла будет загружена в оперативную память, как в DOM):
        SAXBuilder builder = new SAXBuilder();
        Document jdomDoc = builder.build( TestJDOM.class.getResource("zooinventory.xml") );
 
        // Печатаем тип документа:
        System.out.println("JDOM.getDocType(): " + jdomDoc.getDocType());
 
        // Получаем ссылку на корневой элемент XML-документа:
        Element root_elem = jdomDoc.getRootElement();
        
        // Выводим содержимое корневого элемента в формате, похожем на JSON:
        System.out.println("\n*** Let's print document content in JSON-like format: ***");
        printJSONLike ( root_elem, 0 );
 
        // Выводим все коментарии в документе, используя соответствующий фильтр:
        System.out.println("\n*** Let's print document comments: ***");
        IteratorIterable<Comment> comments = root_elem.getDescendants( Filters.comment() );
        while ( comments.hasNext() ) {
            Comment comment = comments.next();
            System.out.println(comment);
        }
    }
    
    private static void printJSONLike ( Element elem, int stack_depth ) {
    	// Получаем атрибуты для текущего элемента:
    	String attributes = "";
    	if ( elem.getAttributes().size() > 0 ) {
    		attributes = " " + elem.getAttributes().toString();
    	}
    	
    	if ( elem.getChildren().size() <= 0 ) {
    		// Печатаем текстовое содержимое текущего элемента, если элемент не имеет потомков:
    		println ( stack_depth, String.format("\"%s\"%s: \"%s\"", elem.getName(), attributes, elem.getValue()) );
    	} else {
    		// Используем рекурсию, чтобы вывести всех потомков для текущего элемента:
    		println ( stack_depth, String.format("\"%s\"%s: {", elem.getName(), attributes) );
    		for ( Element child : elem.getChildren() ) {
    			printJSONLike ( child, stack_depth + 1);
    		}
    		println ( stack_depth, "}");
    	}
    }
    
    /**
     * Печатает строку текста с заданным отступом в стандартный поток вывода
     * @param space - размер отступа (каждая единица отступа равна 3-м пробелам)
     * @param line - строка текста для вывода
     */
    private static void println ( int space, String line ) {
		System.out.println( String.format( "%" + ( (space > 0)?(String.format("%1$d.%1$ds", space * 3)):("s") ) + "%s", "", line) );
	}
}
