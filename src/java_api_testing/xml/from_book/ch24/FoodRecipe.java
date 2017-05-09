package java_api_testing.xml.from_book.ch24;
import java.util.*;

/**
 * Вспомогательный класс, используемый в примерах работы с XML
 */
public class FoodRecipe
{
	public String name;
	public List<String> ingredient = new ArrayList<String>();

	public String toString() { return name + ": " + ingredient.toString(); }
}
