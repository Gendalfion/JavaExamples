package java_api_testing.java_text_api;

import java.util.ListResourceBundle;

/**
 *  Русская локализация ресурса MyResourceBundle (см. класс MyResourceBundle)
 * @author Alex
 *
 */
public class MyResourceBundle_ru extends ListResourceBundle {
	
	private final static String [][] MY_BUNDLE_CONTENT_RU = {
			{"HelloMessage", "Привет на Русском!"},
			{"ByeMessage", "Пока по Русски..."}
	};

	@Override
	protected Object[][] getContents() {
		return MY_BUNDLE_CONTENT_RU;
	}

}
