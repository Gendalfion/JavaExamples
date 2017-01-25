package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AssertionTesting implements ActionListener{
	JButton mBtnAssert1;
	JTextArea	mTextArea;
	
	public AssertionTesting () {
		JFrame myFrame = new JFrame ( "Assertion Testing" );
		
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		main_panel.add(new JScrollPane(mTextArea = new JTextArea()), BorderLayout.CENTER);
		mTextArea.setEditable(false);
		
		JPanel bottom_panel = new JPanel(new FlowLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		bottom_panel.add(mBtnAssert1 = new JButton("Assert Test"));
		
		mBtnAssert1.addActionListener(this);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new AssertionTesting();
	}
	
	protected void printExceptionInfo(final String handler_title, Exception ex) {
		ByteArrayOutputStream stream;
		ex.printStackTrace(new PrintStream(stream = new ByteArrayOutputStream()));
		
		mTextArea.setText(handler_title + stream.toString());
	}
	
	protected void printErrorInfo(final String handler_title, Error ex) {
		ByteArrayOutputStream stream;
		ex.printStackTrace(new PrintStream(stream = new ByteArrayOutputStream()));
		
		mTextArea.setText(handler_title + stream.toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ( e.getSource().equals(mBtnAssert1) ) {
				assert e.getSource().getClass().getName() != "javax.swing.JButton" : "Class name = " + e.getClass().getName();
			} 
			
		} catch (AssertionError ex) {
			printErrorInfo("AssertionError handler: ", ex);
		} 
		catch (Exception ex) {
			printExceptionInfo("Exception handler: ", ex);
		} 
	}
}
