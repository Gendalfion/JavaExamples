package java_api_testing.xml.from_book.ch24;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * ѕример реализации интерфейса org.xml.sax.ContentHandler дл€ обработки событий
 * синтаксического анализатора XML-документа
 * 
 * <p> ѕринцип работы класса основан на технологии Java Reflection api:
 * <br> ћы просматриваем элементы XML-документа и пытаемс€ дл€ каждого нового
 * элемента найти соответствующий класс (по имени элемента).
 * <br>  аждый считанный элемент добавл€етс€ в качестве свойства в свой родительский класс
 * (дл€ построени€ иерархии элементов используетс€ стэк).
 * <br> ѕосле завершени€ работы возвращаетс€ корневой элемент XML-документа,
 * преобразованный в соответствующий класс при помощи отражений в Java.
 * 
 * <p> ѕример использовани€ класса: {@link TestSAXModelBuilder}
 */
public class SAXModelBuilder extends DefaultHandler
{
	private String mBasicPackage = "";
	
	Stack<Object> stack = new Stack<>();
    
    public SAXModelBuilder () {}
    
    /**
     *  онструтор класса с заданным базовым пакетом дл€ поиска Java-классов
     * @param basicPackage - пакет дл€ поиска классов, полученных из XML-документа
     */
    public SAXModelBuilder ( String basicPackage ) {
    	mBasicPackage = basicPackage.trim();
    	if ( mBasicPackage.length() > 0 && !mBasicPackage.endsWith(".") ) {
    		mBasicPackage += ".";
    	}
    }

    // ќбработчик событи€ открывающего тега очередного элемента в XML-документе
    // ƒанные событи€ поступают в пор€дке, соответствующем XML-документу
    public void startElement( String namespace, String localname, String qname, Attributes atts ) throws SAXException
	{
    	println( stack.size(), String.format("startElement( %s, %s, %s, atts.getLength = %d)", namespace, localname, qname, atts.getLength()));
        // Construct the new element and set any attributes on it
		Object element;
        try {
            String className = Character.toUpperCase( qname.charAt( 0 ) ) + qname.substring( 1 );
            element = Class.forName( mBasicPackage + className ).newInstance();
        } catch ( Exception e ) {
            element = new StringBuffer();
        }

		for( int i=0; i<atts.getLength(); i++) {
            try {
                setProperty( atts.getQName( i ), element, atts.getValue( i ) );
            } catch ( Exception e ) { throw new SAXException( "Error: ", e ); }
        }

        stack.push( element );
    }

    // ќбработчик событи€ закрывающего тега очередного элемента в XML-документе
	public void endElement( String namespace, String localname, String qname ) throws SAXException
	{
		// Add the element to its parent
		int spaceGap = 0;
        if ( stack.size() > 1) {
            Object element = stack.pop();
            try {
                setProperty( qname, stack.peek(), element );
            } catch ( Exception e ) { throw new SAXException( "Error: ", e ); }
            spaceGap = stack.size();
        }
        println( spaceGap, String.format("endElement( %s, %s, %s)", namespace, localname, qname) );
	}

	// ќбработчик событи€ получени€ текста между открывающим и закрывающим тегом текущего элемента:
	public void characters(char[] ch, int start, int len )
    {
		// Receive element content text
		String text = new String( ch, start, len );
		
        if ( text.trim().length() == 0 ) { return; }
        println( stack.size(), String.format("characters( \"%s\" )", text.trim()) );
		((StringBuffer)stack.peek()).append( text );
	}

	// ¬спомогательна€ процедура присваивани€ значени€ (value) заданному полю (name) объекта (target):
    void setProperty( String name, Object target, Object value ) throws SAXException, IllegalAccessException, NoSuchFieldException
    {
    	if ( "xmlns".equals(name) ) {
    		// ѕропускаем атрибут "xmlns", указывающий пространство имен текущего элемента:
    		println( stack.size() + 1, "setProperty: Skipping \"xmlns\" attribute, value = \"" + value + "\"..." );
    		return;
    	}
    	println( stack.size() + 1, String.format("setProperty( %s.%s = \"%s\")", target.getClass().getSimpleName(), name, value.toString()) );
    	
        Field field = target.getClass().getField( name );

        // Convert values to field type
        if ( value instanceof StringBuffer ) {
            value = value.toString();
        }
        if ( field.getType() == Double.class ) {
            value = Double.parseDouble( value.toString() );
        }
        if ( Enum.class.isAssignableFrom( field.getType() ) ) {
        	value = Enum.valueOf( (Class<Enum>)field.getType(), value.toString() );
        }

        // Apply to field
        if ( field.getType() == value.getClass() ) {
            field.set( target, value );
        } else {
	        if ( Collection.class.isAssignableFrom( field.getType() ) ) {
	            Collection collection = (Collection)field.get( target );
	            collection.add( value );
	        } else {
	            throw new RuntimeException( "Unable to set property..." );
	        }
        }
	}

    /**
     * 
     * @return ¬озвращает объект корневого элемента XML-документа   
     */
	public Object getModel() { return stack.peek(); }
	
	/**
     * ѕечатает строку текста с заданным отступом в стандартный поток вывода
     * @param space - размер отступа (кажда€ единица отступа равна 3-м пробелам)
     * @param line - строка текста дл€ вывода
     */
    private static void println ( int space, String line ) {
		System.out.println( String.format( "%" + ( (space > 0)?(String.format("%1$d.%1$ds", space * 3)):("s") ) + "%s", "", line) );
	}
}
