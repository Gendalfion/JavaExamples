package java_api_testing.java_text_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Formatter;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UnknownFormatConversionException;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextParseAndFormatTesting {
	
	public TextParseAndFormatTesting () {
		JFrame myFrame = new JFrame ( "Text Parsing Testing" );
		myFrame.setSize(1200, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		new ScannerTesting(center_panel);
		new FormatTesting(center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new TextParseAndFormatTesting ();
	}
	
	//  ласс, демонстрирующий основы работы с объектом разбора текста Scanner
	private class ScannerTesting {
		JTextField mInputField = new JTextField("123, ABCDEF , false\t, 12.345", 25);
		JTextField mRegExpField = new JTextField("\\s*,\\s*"/*зап€та€, окруженна€ любым количеством пробелов (возможно нулем)*/, 25);
		
		JTextArea mParsedTextArea = new JTextArea();
		
		public ScannerTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel (new BorderLayout());
			border_panel.setBorder(BorderFactory.createTitledBorder("Scanner Testing"));
			parent_panel.add(border_panel);
			
			JPanel left_panel = new JPanel ();
			left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
			border_panel.add(left_panel, BorderLayout.WEST);
			
			left_panel.add(new JLabel("Input Text to Parse:"));
			left_panel.add(new JPanel().add(mInputField).getParent());
			
			left_panel.add(new JLabel("Regular expression for delimeter:"));
			left_panel.add(new JPanel().add(mRegExpField).getParent());
			
			mParsedTextArea.setBorder(BorderFactory.createTitledBorder("Parsed Text:"));
			mParsedTextArea.setEditable(false);
			mParsedTextArea.setFont(new Font (Font.MONOSPACED, 0, 12));
			border_panel.add(new JScrollPane(mParsedTextArea), BorderLayout.CENTER);
			
			DocumentListener my_documet_listener = new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void insertUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void changedUpdate(DocumentEvent e) { OnChange (); }
				private void OnChange () { ParseText (mInputField.getText(), mRegExpField.getText()); }
			};
			
			ParseText (mInputField.getText(), mRegExpField.getText());
			mInputField.getDocument().addDocumentListener(my_documet_listener);
			mRegExpField.getDocument().addDocumentListener(my_documet_listener);
		}
		
		public void ParseText ( String text, String delimeter_expression_pattern ) {
			
			final String fmt_template = "%-30.30s = \"%s\"\n";
			Formatter fmt = new Formatter(new StringBuilder());
			
			try (
					// ќбъект Scanner позвол€ет парсить текст, состо€щий из заданного набора токенов в заданном пор€дке и с заданным разделителем:
					Scanner scanner = new Scanner(text);
					// ѕримечательно что Scanner - это более мощна€ утилита парсинга текста, пришедша€ на замену утилите StringTokenizer
				) {
				scanner.useDelimiter(delimeter_expression_pattern); // ”станавливаем регул€рное выражение шаблона разделител€
				scanner.useLocale(Locale.US); // ”станавливаем локаль дл€ парсинга чисел
				fmt.format(fmt_template, "scanner.nextInt()", String.valueOf(scanner.nextInt()) );
				fmt.format(fmt_template, "scanner.nextLong(16)", String.valueOf(scanner.nextLong(16/*”казываем систему счислени€ дл€ парсинга числа*/)) );
				fmt.format(fmt_template, "scanner.nextBoolean()", String.valueOf(scanner.nextBoolean()) );
				fmt.format(fmt_template, "scanner.nextDouble()", String.valueOf(scanner.nextDouble()) );
			} catch ( InputMismatchException e ) { // InputMismatchException возникает когда реальный токен не соответсвует желаемому токену из текста
				fmt.format(fmt_template, "InputMismatchException", e.getMessage() );
			} catch ( NoSuchElementException e ) { // NoSuchElementException возникает, если запросить токен когда поток чтени€ уже окончен
				fmt.format(fmt_template, "NoSuchElementException", e.getMessage() );
			} catch ( PatternSyntaxException e ) { // PatternSyntaxException возникает, если передать некорректное регул€рное выражение в метод useDelimiter
				fmt.format(fmt_template, "PatternSyntaxException", e.getMessage() );
			} finally {
				mParsedTextArea.setText(fmt.toString());
				fmt.close();
			}
		}
	}
	
	//  ласс, демонстрирующий примеры работы с printf-style форматированием текста
	private class FormatTesting {
		
		/* Ўаблон форматировани€ следующий:
		 * %[индекс_аргумента$][флаги][ширина(мин. кол-во символов)][.точность(макс. кол-во символов)]тип 
		 */
		
		JTextField mTextField = new JTextField("Text to Format", 25);
		JTextField mTextFormatField = new JTextField("(%1$-20.20S)   (%1$20.20s)", 25);
		JTextField mIntegerField = new JTextField("2047", 25);
		
		JTextArea mFormattedTextArea = new JTextArea();
		
		public FormatTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel (new BorderLayout());
			border_panel.setBorder(BorderFactory.createTitledBorder("Format Testing"));
			parent_panel.add(border_panel);
			
			JPanel left_panel = new JPanel ();
			left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
			border_panel.add(left_panel, BorderLayout.WEST);
			
			left_panel.add(new JLabel("Input Text to Format:"));
			left_panel.add(new JPanel().add(mTextField).getParent());
			
			left_panel.add(new JLabel("Input Text Format Expression:"));
			left_panel.add(new JPanel().add(mTextFormatField).getParent());
			
			left_panel.add(new JLabel("Input Integer Number:"));
			left_panel.add(new JPanel().add(mIntegerField).getParent());
			
			mFormattedTextArea.setBorder(BorderFactory.createTitledBorder("Formatted Text:"));
			mFormattedTextArea.setEditable(false);
			mFormattedTextArea.setFont(new Font (Font.MONOSPACED, 0, 12));
			border_panel.add(new JScrollPane(mFormattedTextArea), BorderLayout.CENTER);
			
			FormatText ();
			DocumentListener my_documet_listener = new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void insertUpdate(DocumentEvent e)  { OnChange (); }
				@Override public void changedUpdate(DocumentEvent e) { OnChange (); }
				private void OnChange () { FormatText (); }
			};
			mTextField.getDocument().addDocumentListener(my_documet_listener);
			mTextFormatField.getDocument().addDocumentListener(my_documet_listener);
			mIntegerField.getDocument().addDocumentListener(my_documet_listener);
		}
		
		public void FormatText () {
			Formatter fmt = new Formatter(new StringBuilder());
			
			final String fmt_template = "%-30.30s = \"%s\"\n";
			try {
				// ѕример форматировани€ строки:
				fmt.format("Format text example:\n");
				fmt.format("\""); fmt.format(mTextFormatField.getText(), mTextField.getText()); fmt.format("\"\n\n");
				
				// ѕример форматировани€ логического типа:
				fmt.format("Text length is greater then 15: %1$b(%1$B)\n\n", mTextField.getText().length() > 15);
				
				// ѕример форматировани€ целого числа:
				fmt.format("Format integer number example:\n");
				fmt.format("\"%1$d\"(%%1$d), \"%1$x\"(%%1$x), \"%1$X\"(%%1$X), \"%1$h\"(%%1$h), \"%1$H\"(%%1$H)\n\n", Integer.valueOf(mIntegerField.getText()).intValue() );
			} catch ( UnknownFormatConversionException e ) { // √енерируетс€, если задан некорректный шаблон форматировани€
				fmt.format(fmt_template, "UnknownFormatConversionException", e.getMessage() );
			} catch ( NumberFormatException e ) { // .valueOf() exception
				fmt.format(fmt_template, "NumberFormatException", e.getMessage() );
			}
			finally {
				mFormattedTextArea.setText(fmt.toString());
				fmt.close();
			}
		}
	}

}
