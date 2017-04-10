package java_api_testing.net_api.ws_testing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import java_api_testing.net_api.ws_testing.generated.*; // ƒанный пакет сгенерирован утилитой wsimport

/**
 * ƒемонстраци€ использовани€ технологии Java API for XML Web Services (JAX-WS)
 * 
 * ƒанна€ технологи€ позвол€ет получать доступ к веб сервису через протоколы WSDL/SOAP:
 * WSDL - основанный на XML протокол, описывающий интерфейс веб сервиса и то, как к нему получить доступ.
 * 		ƒл€ создани€ клиентского кода (подобно RPC) используетс€ утилита JDK: wsimport
 * 		(пример использовани€ утилиты находитс€ в файле: Make_ws_SOAP_client_code.cmd);
 * SOAP - протокол дл€ обмена структурированной информацией между веб сервисами (тоже основан на XML).
 * 
 * @author Lab119Alex
 *
 */
public class JAXWS_WebClientTesting {
	
	private BibleWebserviceSoap mBibleWebservice;
	
	public JAXWS_WebClientTesting () {
		JFrame myFrame = new JFrame ( "Java API for XML Web Services (JAX-WS) Testing" );
		myFrame.setSize(700, 780);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel ();
		main_panel.add(top_panel, BorderLayout.NORTH);
		top_panel.setBorder(BorderFactory.createTitledBorder("Input Bible Webservice params:"));
		
		top_panel.add(new JLabel("Title: "));
		JComboBox<String> bookTitlesComboBox = new JComboBox<>();
		bookTitlesComboBox.setEnabled(false);
		bookTitlesComboBox.setMaximumRowCount(25);
		top_panel.add(bookTitlesComboBox);
		
		top_panel.add(new JLabel("  Chapter: "));
		JTextField chapterField = new JTextField("1", 5);
		chapterField.setEnabled(false);
		top_panel.add(chapterField);
		
		top_panel.add(new JLabel("  Verse: "));
		JTextField verseField = new JTextField("", 5);
		verseField.setEnabled(false);
		top_panel.add(verseField);
		
		JButton getGodsWordBtn = new JButton("Loading Book Titles...");
		getGodsWordBtn.setEnabled(false);
		top_panel.add(getGodsWordBtn);
		
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		center_panel.setBorder(BorderFactory.createTitledBorder("God's Word:"));
		
		JTextArea wordArea = new JTextArea();
		wordArea.setEditable(false);
		wordArea.setLineWrap(true);
		wordArea.setFont( wordArea.getFont().deriveFont(15.0f) );
		center_panel.add( new JScrollPane(wordArea) );
		
		new Thread(new Runnable() {
			@Override public void run() {
				try {
					// —оздаем прокси интерфейс BibleWebserviceSoap, который предоставит нам
					// клиентские функции удаленного вызова дл€ доступа к интерфейсу веб сервиса BibleWebservice:
					mBibleWebservice = new BibleWebservice().getBibleWebserviceSoap();
					
					// ¬ычлен€ем названи€ книг в библии при помощи API регул€рных выражений:
					Matcher matcher = Pattern.compile("<BookTitle>(.*?)</BookTitle>").matcher(
							// ћетод BibleWebserviceSoap.getBookTitles() возвращает XML-структуру, содержащую
							// информацию о всех книгах библии (о которых знает сервер, предоставл€ющий нам веб сервис):
							mBibleWebservice.getBookTitles());
					while ( matcher.find() ) {
						// ƒобавл€ем все найденные совпадени€ в список bookTitlesComboBox:
						bookTitlesComboBox.addItem( matcher.group(1) );
					}
					
					bookTitlesComboBox.setEnabled(true);
					getGodsWordBtn.setText("Get God's Word...");
					getGodsWordBtn.setEnabled(true);
					chapterField.setEnabled(true);
					verseField.setEnabled(true);
				} catch ( Exception exception ) {
					JOptionPane.showMessageDialog(main_panel, exception, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}).start();
		
		getGodsWordBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				wordArea.setText("");
				String bookTitle = bookTitlesComboBox.getItemAt( bookTitlesComboBox.getSelectedIndex() );
				int chapter = getTextAsInt ( chapterField, 1 );
				int verse = getTextAsInt ( verseField, -1 );
				
				String bibleWordsInXML;
				if ( verse < 0 ) {
					// «апрашиваем все библейские стихи по названию книги и номеру главы:
					bibleWordsInXML = mBibleWebservice.getBibleWordsByBookTitleAndChapter(bookTitle, chapter);
				} else {
					// «апрашиваем библейский стих по названию книги, номеру главы и номеру стиха:
					bibleWordsInXML = mBibleWebservice.getBibleWordsByChapterAndVerse(bookTitle, chapter, verse);
				}
				
				// –азбираем полученную от сервера строку bibleWordsInXML при помощи API регул€рных выражений
				// в цел€х поиска библейских стихов по тегу <BibleWords>:
				Matcher matcher = Pattern.compile("<BibleWords>(.*?)</BibleWords>").matcher(bibleWordsInXML);
				try ( Formatter fmt = new Formatter() ) {
					while ( matcher.find() ) {
						fmt.format("%s\n\n", matcher.group(1) );
					}
					wordArea.setText(fmt.toString());
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	private int getTextAsInt ( JTextComponent textComponent, int defaultValue ) {
		try { return Integer.valueOf( textComponent.getText() ); } catch ( Exception e ) {}
		return defaultValue;
	}

	public static void main(String[] args) {
		new JAXWS_WebClientTesting ();
	}
}
