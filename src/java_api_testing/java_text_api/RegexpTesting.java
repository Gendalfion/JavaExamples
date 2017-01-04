package java_api_testing.java_text_api;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Демонстрация работы с регулярными выражениями в Java (пакет java.util.regex.*)
 * @author Lab119Alex
 *
 */
public class RegexpTesting implements DocumentListener, ListSelectionListener {
	// Примеры регулярных выражений для выделения паттернов в тексте:
	static final String [] mSearchRegexpArray = {
		  "example"				// Простой шаблон, совпадающий со входным текстом символ в символ
		, ".xample."			// Специальный символ '.' соответствует любому единичному символу
		, "\\sexample\\S"		// '\s' = {' ', '\t', '\r', '\n', '\f'}; '\S' = все, кроме символов '\s'
		, "\\d\\D"				// '\d' = ['0'..'9'], '\D\ = все, кроме символов '\D'
		, "\\w\\W"				// 'w' = {['0'..'9'], ['a'..'z'], ['A'..'Z'], '_'}, 'W' = все, кроме символов '\w'
		, "[A-Za-z&&[^b-zST]]" // [] - используется для построения групп из произвольных символов, && - пересечение множеств групп
								// '^' - все символы, кроме символов из множества
		, "^[Tt]his"			// Специальный символ '^' в начале выражения соответствует началу ввода (строки в многострочном режиме)
		, "(?m)[Tt]his$"		// Специальный символ '$' в конце выражения соответствует концу ввода (концу строки в многострочном режиме)
								// (?m) в начале выражения включает многострочный режим
		, "(?i)this"			// (?i) в начале выражения включает режимнечуствительности к регистру
		, "\\d*0"				// Специальный символ '*' означает любое количество повторений предшествующего символа (0 или более)
		, "\\d+0"				// Специальный символ '+' означает повторение предшествующего символа (1 или более раз)
		, "\\bexamples?\\b"		// '\b' - метка границы слова, '?' - 0 или 1 повторение предшествующего символа
		, "\\b\\w{2,4}\\b"		// {x,y} - повторение предшествующего символа от x до y раз,
								// если y не указано, подразумевается y = бесконечности
		, "\\w+@\\w+(\\.\\w+)+"	// () - группирование в шаблоне позволяет применять символы множественности к целому выражению в скобках
		, "\\b(\\w)\\w*\\1\\b"	// \n, где n = [1, 2, ...] - захват группы, позволяющий использовать ранее захваченный текст
								// по номеру группы (номер группы определяется по количетву открытых скобок и начинается с 1)
		, "\\w+@(\\w+\\.)+(net|org|ru)"	// Символ '|' означает логическое или при выборке 
		, "</?.*?>"				// '?' после сивола повторения означает "нежадную" выборку символов (выборка как можно
								// меньшего количества символов перед наступлением условия окончания поиска)
		, "(?=\\w+\\W)is"		// (?=) - операция просмотра вперед требует наличия определенного шаблона далее по тексту
								// (но не вбирает его в качестве совпадения)
		, "(?![Ee]xample)ex"	// '!' - просмотр вперед паттернов, не совпадающих с выражением в скобках
		, "(?<=[Ee]xample )text"// (?<=) - операция просмотра назад требует наличия определенного шаблона перед заданным паттерном
		
	};
	
	// Примеры регулярных выражений для замены выделенных паттернов в тексте:
	static final String [] mReplaceRegexpArray = {
			"simple"			// Простой текст, который заменит все найденные паттерны во входном тексте
		,	"Group-\"$1\""		// $n - ссылка на группу в паттерне входного текста, n = [1, 2, ...]
		,	"Pattern-\"$0\""	// $0 - ссылка на весь паттерн из входного текста
	};
	
	JTextField mSearchRegexpField = new JTextField(25);
	JList<String> mSearchRegexpList = new JList<>(mSearchRegexpArray);
	JTextField mReplaceRegexpField = new JTextField(25);
	JList<String> mReplaceRegexpList = new JList<>(mReplaceRegexpArray);
	JTextPane mInputPane = new JTextPane();
	JTextPane mOutputPane = new JTextPane();
	
	/**
	 * Вспомогательный класс, сохраняющий позицию выделения в тексте
	 * @author Lab119Alex
	 *
	 */
	private class TextSelection {
		public int mSelStart = 0;
		public int mSelLength = 0;
		
		public TextSelection ( int sel_start, int sel_length ) {
			mSelStart 	= sel_start;
			mSelLength 	= sel_length;
		}
	}
	/**
	 * Вспомогательный класс, сохраняющий изменения в тексте после его обработки
	 * @author Lab119Alex
	 *
	 */
	private class ChangeBuffer {
		public List<TextSelection> mInputTextSelection;
		public List<TextSelection> mOutputTextSelection;
		public String mOutputText;
		
		{
			mInputTextSelection  = new ArrayList<>();
			mOutputTextSelection = new ArrayList<>();
		}
	}
	
	AttributeSet mHighlitedTextAttributes;
	AttributeSet mDefaultTextAttributes;
	
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		mHighlitedTextAttributes = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.LIGHT_GRAY);
		mHighlitedTextAttributes = sc.addAttribute(mHighlitedTextAttributes, StyleConstants.FontFamily, "Lucida Console");
		
		mDefaultTextAttributes = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, "Lucida Console");
	}
	
	public RegexpTesting () {
		JFrame myFrame = new JFrame ( "Regular Expression Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel left_panel = new JPanel (new GridLayout(0, 1));
		main_panel.add(left_panel, BorderLayout.WEST);
		
		JPanel tmp_panel;
		
		JPanel left_top_panel = new JPanel (new BorderLayout());
		left_panel.add(left_top_panel);
		mSearchRegexpList.setBorder(BorderFactory.createTitledBorder("Search Expression Examples:"));
		left_top_panel.add(new JScrollPane(mSearchRegexpList), BorderLayout.CENTER);
		mSearchRegexpList.addListSelectionListener(this);
		
		tmp_panel = new JPanel(); tmp_panel.setBorder(BorderFactory.createTitledBorder("Enter Your Search Expression:"));
		tmp_panel.add(mSearchRegexpField);
		left_top_panel.add(tmp_panel, BorderLayout.NORTH);
		mSearchRegexpField.getDocument().addDocumentListener(this);
		
		JPanel left_bottom_panel = new JPanel (new BorderLayout());
		left_panel.add(left_bottom_panel);
		mReplaceRegexpList.setBorder(BorderFactory.createTitledBorder("Replace Expression Examples:"));
		left_bottom_panel.add(mReplaceRegexpList, BorderLayout.CENTER);
		mReplaceRegexpList.addListSelectionListener(this);
		
		tmp_panel = new JPanel(); tmp_panel.setBorder(BorderFactory.createTitledBorder("Enter Your Replace Expression:"));
		tmp_panel.add(mReplaceRegexpField);
		left_bottom_panel.add(tmp_panel, BorderLayout.NORTH);
		mReplaceRegexpField.getDocument().addDocumentListener(this);
		
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		mInputPane.setBorder(BorderFactory.createTitledBorder("Input Text:"));
		mInputPane.setText("This is example text (Example text)\n"
					+ "Second line of text examples\n"
					+ "this end of line (This is here) this\n"
					+ "01234567890 1234567890_,0\n"
					+ "foo@mail.com friend__@foo.mail.org invalid@.ru vaild@ua.ru\n"
					+ "<teg> Example text inside teg </teg>");
		center_panel.add(mInputPane);
		mInputPane.getDocument().addDocumentListener(this);
		mInputPane.setToolTipText("found pattern occurrences are highlighted");
		
		mOutputPane.setBorder(BorderFactory.createTitledBorder("Output Text:"));
		mOutputPane.setEditable(false);
		center_panel.add(mOutputPane);
		mOutputPane.setToolTipText("replaced pattern occurrences are highlighted");
		
		mSearchRegexpList.setSelectedIndex(0);
		mReplaceRegexpList.setSelectedIndex(0);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	protected void updateAll () {
		ChangeBuffer change_buffer = new ChangeBuffer();
		try {
			String exp_search_pattern = null;
			String exp_replace_pattern = null;
			
			// Получаем текстовое представление регулярного выражения для поиска шаблона в тексте:
			if ( !mSearchRegexpField.getText().isEmpty() ) {
				exp_search_pattern = mSearchRegexpField.getText();
			} else {
				if ( mSearchRegexpList.getSelectedIndex() >= 0 ) {
					exp_search_pattern = mSearchRegexpList.getSelectedValue();
				}
			}
			
			// Получаем текстовое представление регулярного выражения для замены шаблона в тексте:
			if ( !mReplaceRegexpField.getText().isEmpty() ) {
				exp_replace_pattern = mReplaceRegexpField.getText();
			} else {
				if ( mReplaceRegexpList.getSelectedIndex() >= 0 ) {
					exp_replace_pattern = mReplaceRegexpList.getSelectedValue();
				}
			}
			
			if ( exp_search_pattern == null ) {
				throw new Exception ("No Search Expression Defined");
			}
			
			change_buffer.mOutputText = processPattern(mInputPane.getText(), exp_search_pattern, exp_replace_pattern, change_buffer);
		} catch ( Exception e ) {
			change_buffer.mOutputText = new StringBuilder().append(e.getClass().getName()).append(": ").append(e.getMessage()).toString();
		}
		
		Runnable pane_update = new Runnable() {
			@Override
			public void run() {
				// Делаем отрисовку в отдельном потоке, т. к. мы не можем менять JTextPane внутри его AWT-события:
				
				// Подсвечиваем найденные паттерны во входном тексте:
				setTextAttributesInPane ( mInputPane, mDefaultTextAttributes, 0, mInputPane.getText().length() );
				for ( TextSelection item : change_buffer.mInputTextSelection ) {
					setTextAttributesInPane ( mInputPane, mHighlitedTextAttributes, item.mSelStart, item.mSelLength );
				}
				
				// Подсвечиваем замененные паттерны в выходном тексте:
				mOutputPane.setText(change_buffer.mOutputText);
				setTextAttributesInPane ( mOutputPane, mDefaultTextAttributes, 0, mOutputPane.getText().length() );
				for ( TextSelection item : change_buffer.mOutputTextSelection ) {
					setTextAttributesInPane ( mOutputPane, mHighlitedTextAttributes, item.mSelStart, item.mSelLength );
				}
			}
		};
		SwingUtilities.invokeLater(pane_update); // Запускаем отрисовку в отдельной задаче после обработки всех событий в диспетчере Swing
	}
	
	protected String processPattern ( String input_text, String search_pattern_str, String replace_pattern_str, ChangeBuffer change_buf ) {
		// Инструмент Matcher позволяет сопоставить шаблон регулярного выражения search_pattern_str с некоторым текстом в mInputPane:
		Matcher matcher = Pattern.compile(search_pattern_str).matcher(input_text);
		
		StringBuffer out_buffer = new StringBuffer();
		int output_buff_offset = 0;
		while (matcher.find()) {	// метод .find() используется для перехода между совпадениями шаблона в тексте
			// Запоминаем найденные совпадения во входном тексте:
			change_buf.mInputTextSelection.add(new TextSelection(
					matcher.start(), 			// Индекс первого символа совпавшего шаблона во входном тексте
					matcher.group().length())); // Метод .group() возвращает подстроку из текста, совпавшую с шаблоном
			
			if (replace_pattern_str != null) {
				// метод .appendReplacement(...) используется в паре с методом .find() для тотечной замены очередного совпадения шаблона 
				// на некоторую строку replace_pattern_str (которая может содержать параметры захвата шаблона)
				matcher.appendReplacement(out_buffer, replace_pattern_str);
				
				// Запоминаем начало и конец замененного паттерна в выходном тексте:
				int sel_start = matcher.start() + output_buff_offset;
				int sel_length = out_buffer.length() - sel_start;
				output_buff_offset += (sel_length - matcher.group().length());
				change_buf.mOutputTextSelection.add( new TextSelection(sel_start, sel_length) );
			}
		}
		// Метод .appendTail(...) пристыковывает оставшийся "хвост" от входного текста, который не был пройден внутри метода .find()
		matcher.appendTail(out_buffer);
		
		return out_buffer.toString();
	}
	
	protected void setTextAttributesInPane ( JTextPane pane, AttributeSet aset, int sel_start, int sel_length ) {
		pane.getStyledDocument().setCharacterAttributes(sel_start, sel_length, aset, true);
	}
	
	@Override public void removeUpdate(DocumentEvent e)  	{ updateAll (); }
	@Override public void insertUpdate(DocumentEvent e)  	{ updateAll (); }
	@Override public void changedUpdate(DocumentEvent e) 	{ /*updateAll ();*/ }
	@Override public void valueChanged(ListSelectionEvent e){
		if (e.getSource() == mSearchRegexpList) {
			mSearchRegexpField.setText( mSearchRegexpList.getSelectedValue() );
		}
		if (e.getSource() == mReplaceRegexpList) {
			mReplaceRegexpField.setText( mReplaceRegexpList.getSelectedValue() );
		}
	}

	public static void main(String[] args) {
		new RegexpTesting ();
	}	
}
