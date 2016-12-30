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
 * ������������ ������ � ����������� ����������� � Java (����� java.util.regex.*)
 * @author Lab119Alex
 *
 */
public class RegexpTesting implements DocumentListener, ListSelectionListener {
	// ������� ���������� ��������� ��� ��������� ��������� � ������:
	static final String [] mSearchRegexpArray = {
		"example"
	};
	
	// ������� ���������� ��������� ��� ������ ���������� ��������� � ������:
	static final String [] mReplaceRegexpArray = {
		"simple"
	};
	
	JTextField mSearchRegexpField = new JTextField(25);
	JList<String> mSearchRegexpList = new JList<>(mSearchRegexpArray);
	JTextField mReplaceRegexpField = new JTextField(25);
	JList<String> mReplaceRegexpList = new JList<>(mReplaceRegexpArray);
	JTextPane mInputPane = new JTextPane();
	JTextPane mOutputPane = new JTextPane();
	
	/**
	 * ��������������� �����, ����������� ������� ��������� � ������
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
	 * ��������������� �����, ����������� ��������� � ������ ����� ��� ���������
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
		left_top_panel.add(mSearchRegexpList, BorderLayout.CENTER);
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
		mInputPane.setText("This is example text (Example text)\n");
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
			
			// �������� ��������� ������������� ����������� ��������� ��� ������ ������� � ������:
			if ( !mSearchRegexpField.getText().isEmpty() ) {
				exp_search_pattern = mSearchRegexpField.getText();
				mSearchRegexpList.setEnabled(false);
			} else {
				mSearchRegexpList.setEnabled(true);
				if ( mSearchRegexpList.getSelectedIndex() >= 0 ) {
					exp_search_pattern = mSearchRegexpList.getSelectedValue();
				}
			}
			
			// �������� ��������� ������������� ����������� ��������� ��� ������ ������� � ������:
			if ( !mReplaceRegexpField.getText().isEmpty() ) {
				exp_replace_pattern = mReplaceRegexpField.getText();
				mReplaceRegexpList.setEnabled(false);
			} else {
				mReplaceRegexpList.setEnabled(true);
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
				// ������ ��������� � ��������� ������, �. �. �� �� ����� ������ JTextPane ������ ��� AWT-�������:
				
				// ������������ ��������� �������� �� ������� ������:
				setTextAttributesInPane ( mInputPane, mDefaultTextAttributes, 0, mInputPane.getText().length() );
				for ( TextSelection item : change_buffer.mInputTextSelection ) {
					setTextAttributesInPane ( mInputPane, mHighlitedTextAttributes, item.mSelStart, item.mSelLength );
				}
				
				// ������������ ���������� �������� � �������� ������:
				mOutputPane.setText(change_buffer.mOutputText);
				setTextAttributesInPane ( mOutputPane, mDefaultTextAttributes, 0, mOutputPane.getText().length() );
				for ( TextSelection item : change_buffer.mOutputTextSelection ) {
					setTextAttributesInPane ( mOutputPane, mHighlitedTextAttributes, item.mSelStart, item.mSelLength );
				}
			}
		};
		SwingUtilities.invokeLater(pane_update); // ��������� ��������� � ��������� ������ ����� ��������� ���� ������� � ���������� Swing
	}
	
	protected String processPattern ( String input_text, String search_pattern_str, String replace_pattern_str, ChangeBuffer change_buf ) {
		// ���������� Matcher ��������� ����������� ������ ����������� ��������� search_pattern_str � ��������� ������� � mInputPane:
		Matcher matcher = Pattern.compile(search_pattern_str).matcher(input_text);
		
		StringBuffer out_buffer = new StringBuffer();
		int output_buff_offset = 0;
		while (matcher.find()) {	// ����� .find() ������������ ��� �������� ����� ������������ ������� � ������
			// ���������� ��������� ���������� �� ������� ������:
			change_buf.mInputTextSelection.add(new TextSelection(
					matcher.start(), 			// ������ ������� ������� ���������� ������� �� ������� ������
					matcher.group().length())); // ����� .group() ���������� ��������� �� ������, ��������� � ��������
			
			if (replace_pattern_str != null) {
				// ����� .appendReplacement(...) ������������ � ���� � ������� .find() ��� �������� ������ ���������� ���������� ������� 
				// �� ��������� ������ replace_pattern_str (������� ����� ��������� ��������� ������� �������)
				matcher.appendReplacement(out_buffer, replace_pattern_str);
				
				// ���������� ������ � ����� ����������� �������� � �������� ������:
				int sel_start = matcher.start() + output_buff_offset;
				int sel_length = out_buffer.length() - sel_start;
				output_buff_offset += (sel_length - matcher.group().length());
				change_buf.mOutputTextSelection.add( new TextSelection(sel_start, sel_length) );
			}
		}
		// ����� .appendTail(...) �������������� ���������� "�����" �� �������� ������, ������� �� ��� ������� ������ ������ .find()
		matcher.appendTail(out_buffer);
		
		return out_buffer.toString();
	}
	
	protected void setTextAttributesInPane ( JTextPane pane, AttributeSet aset, int sel_start, int sel_length ) {
		pane.getStyledDocument().setCharacterAttributes(sel_start, sel_length, aset, true);
	}
	
	@Override public void removeUpdate(DocumentEvent e)  	{ updateAll (); }
	@Override public void insertUpdate(DocumentEvent e)  	{ updateAll (); }
	@Override public void changedUpdate(DocumentEvent e) 	{ /*updateAll ();*/ }
	@Override public void valueChanged(ListSelectionEvent e){ updateAll (); }

	public static void main(String[] args) {
		new RegexpTesting ();
	}	
}
