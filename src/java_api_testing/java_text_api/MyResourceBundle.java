package java_api_testing.java_text_api;

import java.util.ListResourceBundle;

/**
 *  ����� ����������� ������ ���� ����������� ListResourceBundle � ����� �������� �� ���������� �������:
 *  name{_lang,_country} - ��� name - �������� ������� (������������ � ������ ResourceBundle.getBundle());
 *	lang - ����������� �������� ����� (��������, ru)
 *  country - ����������� �������� ������ (��������, RU)
 *  
 *  � ������ ������ ����� MyResourceBundle ������������ ��� ������ ��-���������, ���� ����� ������������������� �������
 * ��� �������� ����������� �� ����� ����������
 *  ������ ������������� ������ - LocalizationTesting
 * @author Alex
 *
 */
public class MyResourceBundle extends ListResourceBundle {
	
	private final static String [][] MY_BUNDLE_CONTENT_DEFAULT = {
			{"HelloMessage", "Hello in English!"},
			{"ByeMessage", "Goodbye in English..."}
	};

	// ������������ �������� ��������������� ������������ ������ getContents(), ������� ������ ����������
	// ��������� ������ ��� ����-��������
	@Override
	protected Object[][] getContents() {
		return MY_BUNDLE_CONTENT_DEFAULT;
	}

}
