package java_api_testing.xml.from_book.ch24;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * ¬спомогательный класс, используемый в примерах работы с XML
 */
@XmlRootElement // ѕомечаем класс Inventory в качестве корневого элемента XML-документа, формируемого при помощи JAXB API
//@XmlRootElement(name = "myinventory") // јтрибут "name" позвол€ет переопредел€ть название элемента в XML-документе
public class Inventory {

    //@XmlElementRef // take name from Gorilla, works if we pass Gorilla class to newInstance
    //@XmlElementRefs(value = @XmlElementRef(type=Gorilla.class)) // take name from Gorilla, works without passing class to newin
    //@XmlElements(value = @XmlElement(name = "mygorxx", type=Gorilla.class)) // names Gorillas right here
    //@XmlElementWrapper
    //@XmlElement(name="animal")
	public List<Animal> animal = new ArrayList<>();
	
	private String privateElement = "Private Element";
	
	// ћы можем определ€ть свойства XML-документа более переносимым способом через get/set-методы класса:
	//@XmlElement(name="testElement") // ѕереопредел€ем название элемента с "privateElement" на "testElement"
	public String getPrivateElement () { return privateElement; }
	public void setPrivateElement ( String element ) { privateElement = element; }
}
