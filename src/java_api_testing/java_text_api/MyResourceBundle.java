package java_api_testing.java_text_api;

import java.util.ListResourceBundle;

/**
 *  Класс локализации должен быть наследником ListResourceBundle и иметь название по следующему шаблону:
 *  name{_lang,_country} - где name - название ресурса (используется в методе ResourceBundle.getBundle());
 *	lang - сокращенное название языка (например, ru)
 *  country - сокращенное название страны (например, RU)
 *  
 *  В данном случае класс MyResourceBundle используется как ресурс по-умолчанию, если более специализированного ресурса
 * для заданной локализации не будет обнаружено
 *  Пример использования класса - LocalizationTesting
 * @author Alex
 *
 */
public class MyResourceBundle extends ListResourceBundle {
	
	private final static String [][] MY_BUNDLE_CONTENT_DEFAULT = {
			{"HelloMessage", "Hello in English!"},
			{"ByeMessage", "Goodbye in English..."}
	};

	// Обязательным является переопределение абстрактного метода getContents(), который должен возвращать
	// двумерный массив пар ключ-значение
	@Override
	protected Object[][] getContents() {
		return MY_BUNDLE_CONTENT_DEFAULT;
	}

}
