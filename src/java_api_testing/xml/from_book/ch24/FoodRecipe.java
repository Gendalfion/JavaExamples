package java_api_testing.xml.from_book.ch24;
import java.util.*;

/**
 * ��������������� �����, ������������ � �������� ������ � XML
 */
public class FoodRecipe
{
	public String name;
	public List<String> ingredient = new ArrayList<String>();

	public String toString() { return name + ": " + ingredient.toString(); }
}
