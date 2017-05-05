package java_api_testing.xml.from_book.ch24;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Вспомогательный класс, используемый в примерах работы с XML
 */
@XmlRootElement
public class Inventory {

    //@XmlElementRef // take name from Gorilla, works if we pass Gorilla class to newInstance
    //@XmlElementRefs(value = @XmlElementRef(type=Gorilla.class)) // take name from Gorilla, works without passing class to newin
    //@XmlElements(value = @XmlElement(name = "mygorxx", type=Gorilla.class)) // names Gorillas right here
    //@XmlElementWrapper
    //@XmlElement(name="animal")
	public List<Animal> animal = new ArrayList<>();
}
