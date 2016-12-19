package java_api_testing.java_text_api;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class LocalizationTesting {
	// Вспомогательный класс для более специфичной работы с объектами класса Locale
	private class MyLocale implements Comparable<MyLocale> {
		public Locale mLocale;
		
		public MyLocale( Locale locale ) {
			mLocale = locale;
		}
		public String toString () {
			return mLocale.getDisplayName();
		}
		@Override
		public int compareTo(MyLocale o) {
			return this.toString().compareTo(o.toString());
		}
		
	}
	
	JList<MyLocale> mLocaleList = new JList<>();
	List<MyLocale> mLocaleArraySorted = new ArrayList<>(); 
	
	JTextArea mRightArea = new JTextArea();
	TitledBorder mRightBorder = BorderFactory.createTitledBorder("Current locale: ");
	
	{
		// Создаем отсортированный по названию массив локалей, доступных на данном компьютере:
		for ( Locale elem : Locale.getAvailableLocales() ) {
			mLocaleArraySorted.add(new MyLocale(elem));
		}
		Collections.sort(mLocaleArraySorted);
		
		// Соединяем массив локалей с объектом отображения локалей в виде списка:
		mLocaleList.setModel(new AbstractListModel<MyLocale>() {
			private static final long serialVersionUID = 1L;
			@Override
			public MyLocale getElementAt(int arg0) {
				return mLocaleArraySorted.get(arg0);
			}
			@Override
			public int getSize() {
				return mLocaleArraySorted.size();
			}
		});
		mLocaleList.setLayoutOrientation(JList.VERTICAL);
		mLocaleList.setVisibleRowCount(-1);
		
		mLocaleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
		mLocaleList.setSelectedIndex( Collections.binarySearch(mLocaleArraySorted, new MyLocale(Locale.getDefault())) );
	}
	
	public LocalizationTesting () {
		JFrame myFrame = new JFrame ( "Localization Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel left_panel = new JPanel (new BorderLayout());
		main_panel.add(left_panel, BorderLayout.WEST);
		
		JScrollPane list_scroller = new JScrollPane(mLocaleList);
		list_scroller.setPreferredSize(new Dimension(200, 80));
		left_panel.add(list_scroller, BorderLayout.CENTER);
		
		center_panel.add(new JScrollPane(mRightArea));
		mRightArea.setEditable(false);
		mRightArea.setBorder(mRightBorder);
		mRightArea.setFont(new Font (Font.MONOSPACED, 0, 12));
		
		mLocaleList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				printLocaleInfo ( getCurrentSelectedLocale() );
			}
		});
		
		printLocaleInfo ( getCurrentSelectedLocale() );
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new LocalizationTesting ();
	}
	
	public Locale getCurrentSelectedLocale () {
		return mLocaleList.getSelectedValue().mLocale;
	}
	
	public void printLocaleInfo ( Locale locale ) {
		// Класс StringBuilder отличается от String тем, что позволяет эффективно увеличивать свое содержимое динамически
		// У StringBuilder есть брат-близнец StringBuffer, который отличается лишь тем, что методы StringBuffer синхронизированны
		Formatter fmt = new Formatter(new StringBuilder());
		
		// Печатаем информацию по локали из объекта Locale:
		final String fmt_template = "%-35.35s = \"%s\"\n";
		fmt.format(locale, fmt_template, "locale.getCountry()", 				locale.getCountry() );
		fmt.format(locale, fmt_template, "locale.getDisplayCountry()", 			locale.getDisplayCountry() );
		fmt.format(locale, fmt_template, "locale.getDisplayCountry(locale)", 	locale.getDisplayCountry(locale) );
		fmt.format(locale, fmt_template, "locale.getLanguage()", 				locale.getLanguage() );
		fmt.format(locale, fmt_template, "locale.getDisplayLanguage()", 		locale.getDisplayLanguage() );
		fmt.format(locale, fmt_template, "locale.getDisplayLanguage(locale)", 	locale.getDisplayLanguage(locale) );
		fmt.format(locale, fmt_template, "locale.getVariant()", 				locale.getVariant() );
		fmt.format(locale, fmt_template, "locale.getDisplayName()", 			locale.getDisplayName() );
		fmt.format(locale, fmt_template, "locale.getDisplayName(locale)", 		locale.getDisplayName(locale) );
		fmt.format(locale, fmt_template, "locale.getDisplayScript()", 			locale.getDisplayScript() );
		fmt.format(locale, fmt_template, "locale.getDisplayScript(locale)", 	locale.getDisplayScript(locale) );
		fmt.format("\n");
		
		// Печатаем зависящую от локали информацию:
		fmt.format(locale, "%-35.35s = \"%f\"\n", "PI", Math.PI);
		Date currentDate = new Date();
		fmt.format(locale, fmt_template, "DateFormat(Short)", DateFormat.getDateInstance(DateFormat.SHORT, locale).format(currentDate));
		fmt.format(locale, fmt_template, "DateFormat(Long)", DateFormat.getDateInstance(DateFormat.LONG, locale).format(currentDate));
		
		mRightArea.setText(fmt.toString());
		mRightBorder.setTitle("Current Locale: " + locale.getDisplayName());
		fmt.close();
	}

}
