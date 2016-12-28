package java_api_testing.java_text_api;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ObjectParseAndFormatTesting {
	// Вспомогательный класс для организации объектов класса Locale в виде сортируемого списка
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
	
	List<MyLocale> 		mLocaleArraySorted  = new ArrayList<>();
	JComboBox<MyLocale>	mLeftHandLocaleBox  = new JComboBox<>();
	JComboBox<MyLocale>	mRightHandLocaleBox = new JComboBox<>();
	
	{
		// Создаем отсортированный по названию массив локалей, доступных на данном компьютере:
		for ( Locale elem : Locale.getAvailableLocales() ) {
			mLocaleArraySorted.add(new MyLocale(elem));
		}
		Collections.sort(mLocaleArraySorted);
		
		// Добавляем локали в ComboBox-ы и делаем выбор локалей по-умолчанию:
		for ( MyLocale elem : mLocaleArraySorted ) {
			mLeftHandLocaleBox.addItem(elem);
			mRightHandLocaleBox.addItem(elem);
		}
		
		Dimension cb_size = new Dimension(250, 30);
		mLeftHandLocaleBox.setPreferredSize(cb_size); mRightHandLocaleBox.setPreferredSize(cb_size);
		mLeftHandLocaleBox.setSelectedIndex( Collections.binarySearch(mLocaleArraySorted, new MyLocale(Locale.US)) );
		mRightHandLocaleBox.setSelectedIndex( Collections.binarySearch(mLocaleArraySorted, new MyLocale(Locale.getDefault())) );
	}
	
	public ObjectParseAndFormatTesting () {
		JFrame myFrame = new JFrame ( "java.text.* Package Testing" );
		myFrame.setSize(800, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel top_panel = new JPanel ();
		top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.X_AXIS));
		main_panel.add(top_panel, BorderLayout.NORTH);
		top_panel.add(new JPanel().add(mLeftHandLocaleBox).getParent());
		top_panel.add(new JPanel().add(mRightHandLocaleBox).getParent());
		
		new CurrencyTesting (center_panel);
		new PercentTesting (center_panel);
		new NumberTesting (center_panel);
		new ChoiceFormatTesting (center_panel);
		new MessageFormatTesting (center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ObjectParseAndFormatTesting ();
	}
	
	/**
	 * Базовый класс для тестирования утилит из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */
	private abstract class AbstractTester implements ItemListener {
		JTextField mInputField = new JTextField(25);
		JTextArea  mOutputField = new JTextArea();
		
		public AbstractTester ( JPanel parent_panel, String title ) {
			JPanel box_panel = new JPanel();
			box_panel.setLayout(new BoxLayout(box_panel, BoxLayout.X_AXIS));
			box_panel.setBorder(BorderFactory.createTitledBorder(title));
			parent_panel.add(box_panel);
			
			box_panel.add(new JPanel().add(mInputField).getParent());
			box_panel.add(mOutputField);
			mOutputField.setEditable(false);
			
			mLeftHandLocaleBox.addItemListener(this);
			mRightHandLocaleBox.addItemListener(this);
			
			mInputField.setText(getInputText());
			mInputField.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void insertUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void changedUpdate(DocumentEvent e) { OnChange (); }
				private void OnChange () { update (); } // Событие редактирования поля mInputField пользователем
			});
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {	// Событие смены текущей локали в верхних ComboBox-ах
			mInputField.setText(getInputText());
			update ();
		}
		
		public void update () {
			try {
				mOutputField.setText(this.convert(mInputField.getText()));
			} catch ( Exception e ) {
				mOutputField.setText( new StringBuilder().append(e.getClass().getName()).append(": ").append(e.getMessage()).toString() );
			}
		}
		
		public abstract String convert ( String input_str) throws Exception;
		public abstract String getInputText ();
		
		public Locale getLeftHandLocale () { return ( (MyLocale)mLeftHandLocaleBox.getSelectedItem() ).mLocale; }
		public Locale getRightHandLocale () { return ( (MyLocale)mRightHandLocaleBox.getSelectedItem() ).mLocale; }
	}
	
	/**
	 * Класс для тестирования утилиты преобразования валют из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */ 
	private class CurrencyTesting extends AbstractTester {
		double mCurrencyValue = 15000.525;
		
		public CurrencyTesting ( JPanel parent_panel ) {
			super(parent_panel, "NumberFormat.getCurrencyInstance Testing");
			mInputField.setText(getInputText());
		}
		
		@Override
		public String getInputText() { return NumberFormat.getCurrencyInstance(getLeftHandLocale()).format(mCurrencyValue); }

		@Override
		public String convert ( String input_str) throws Exception {
			// Парсинг текстового представления валютного числа в его double-значение (в соответствии с выбранной локалью):
			mCurrencyValue = NumberFormat.getCurrencyInstance(getLeftHandLocale()).parse(input_str).doubleValue();
			// Преобразование double-числа в его текстовое представление в качестве валюты (в соответствии с выбранной локалью):
			return NumberFormat.getCurrencyInstance(getRightHandLocale()).format(mCurrencyValue);
		}					
	}
	
	/**
	 * Класс для тестирования утилиты преобразования процентных значений из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */
	private class PercentTesting extends AbstractTester {
		double mPercentValue = 0.33536;
		
		public PercentTesting ( JPanel parent_panel ) {
			super(parent_panel, "NumberFormat.getPercentInstance Testing");
			mInputField.setText(getInputText());
		}
		
		@Override
		public String getInputText() { return NumberFormat.getPercentInstance(getLeftHandLocale()).format(mPercentValue); }

		@Override
		public String convert(String input_str) throws Exception {
			// Парсинг текстового представления процентного числа в его double-значение (в соответствии с выбранной локалью):
			mPercentValue = NumberFormat.getPercentInstance(getLeftHandLocale()).parse(input_str).doubleValue();
			// Преобразование double-числа в его текстовое представление в качестве процентов (в соответствии с выбранной локалью):
			return NumberFormat.getPercentInstance(getRightHandLocale()).format(mPercentValue);
		}		
	}
	
	/**
	 * Класс для тестирования утилиты преобразования чисел в текст из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */
	private class NumberTesting extends AbstractTester {
		double mNumberValue = 123456789.0123456789;
		
		public NumberTesting ( JPanel parent_panel ) {
			super(parent_panel, "NumberFormat.getNumberInstance Testing");
			mInputField.setText(getInputText());
		}
		
		@Override
		public String getInputText() { return NumberFormat.getNumberInstance(getLeftHandLocale()).format(mNumberValue); }

		@Override
		public String convert(String input_str) throws Exception {
			// Парсинг текстового представления числа в его double-значение (в соответствии с выбранной локалью):
			mNumberValue = NumberFormat.getNumberInstance(getLeftHandLocale()).parse(input_str).doubleValue();
			// Преобразование double-числа в его текстовое представление (в соответствии с выбранной локалью):
			return NumberFormat.getNumberInstance(getRightHandLocale()).format(mNumberValue);
		}
	}
	
	/**
	 * Класс для тестирования утилиты ChoiceFormat из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */
	private class ChoiceFormatTesting extends AbstractTester {
		double mCurrentValue = 50.0;
		
		private final double [] LIMITS = { 0.0,    25.0,      50.0,     75.0,            100.0};
		private final String [] LABELS = { "Мало", "Немного", "Средне", "Выше среднего", "Много"};
		// Класс ChoiceFormat позволяет задать соответствие некоторых вещественных пределов строковым значениям:
		ChoiceFormat mChoiceFormat 
			= new ChoiceFormat(LIMITS, LABELS);
		// Эквивалентный вызов:
			//= new ChoiceFormat("0.0#Мало|25.0#Немного|50.0#Средне|75.0#Выше среднего|100.0#Много");
		
		public ChoiceFormatTesting ( JPanel parent_panel ) {
			super(parent_panel, "ChoiceFormat Testing");
			mInputField.setText(getInputText());
		}
		
		@Override
		public String getInputText() { return String.format(getLeftHandLocale(), "%f", mCurrentValue); }
		
		@Override
		public String convert(String input_str) throws Exception {
			// Парсинг текстового представления числа в его double-значение (в соответствии с выбранной локалью):
			mCurrentValue = NumberFormat.getNumberInstance(getLeftHandLocale()).parse(input_str).doubleValue();
			// Преобразование double-числа в его текстовое представление при помощи заданных пределов в объекте ChoiceFormat:
			return mChoiceFormat.format(mCurrentValue);
		}
	}
	
	/**
	 * Класс для тестирования утилиты MessageFormat из пакета java.text.*
	 * 
	 * @author Lab119Alex
	 *
	 */
	private class MessageFormatTesting extends AbstractTester {
		int mIntegerValue = 1222;
		
		public MessageFormatTesting ( JPanel parent_panel ) {
			super(parent_panel, "MessageFormat Testing");
			mInputField.setText(getInputText());
		}
		
		@Override
		public String getInputText() { return String.format(getLeftHandLocale(), "%d", mIntegerValue); }
		
		@Override
		public String convert(String input_str) throws Exception {
			// Парсинг текстового представления числа в его long-значение (в соответствии с выбранной локалью):
			mIntegerValue = NumberFormat.getNumberInstance(getLeftHandLocale()).parse(input_str).intValue();
			
			// MessageFormat является альтернативой для printf-способа форматирования текста:
			MessageFormat mf = new MessageFormat(
					// В качестве шаблона принимается строка вида "... {<индекс аргумента>,<тип>,<стиль>} ..."
					"В стране {0,number,integer} {1,choice,0#человек|2#человека|5#человек} на {2, date, full}"
					, getRightHandLocale());
			int mod_value = mIntegerValue % 100;
			// метод .format( Object [] args ) возвращает строку, отформатированную в соответствии с шаблоном и переданными аргументами:
			return mf.format ( new Object [] { 
						mIntegerValue, 
						((mod_value > 10) && (mod_value < 20))?(mod_value):(mod_value % 10),
						new Date ()
			});
		}
	}

}
