package java_testing;

import java.awt.BorderLayout;
import java.awt.Button;
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

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.omg.IOP.ExceptionDetailMessage;

public class ExceptionTesting implements ActionListener {
	JButton 	mBtnThrow1, mBtnThrow2, mBtnThrow3, mBtnThrow4, mBtnThrow5, mBtnExit; 
	JTextArea	mTextArea;
	
	enum eExceptionChoice {ecMyException, ecException};
	
	public ExceptionTesting () {
		JFrame myFrame = new JFrame ( "Exception test" );
		
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		main_panel.add(new JScrollPane(mTextArea = new JTextArea()), BorderLayout.CENTER);
		mTextArea.setEditable(false);
		
		JPanel bottom_panel = new JPanel(new FlowLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		bottom_panel.add(mBtnThrow1 = new JButton("MyException"));
		bottom_panel.add(mBtnThrow2 = new JButton("Exception"));
		bottom_panel.add(mBtnThrow4 = new JButton("Choice MyException"));
		bottom_panel.add(mBtnThrow3 = new JButton("Choice Exception"));
		bottom_panel.add(mBtnThrow5 = new JButton("Exception Levels"));
		bottom_panel.add(mBtnExit = new JButton("Exit"));
		
		
		mBtnThrow1.addActionListener(this);
		mBtnThrow2.addActionListener(this);
		mBtnThrow3.addActionListener(this);
		mBtnThrow4.addActionListener(this);
		mBtnThrow5.addActionListener(this);
		
		mBtnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	protected void throwMyException() throws Exception {
		//throw new MyException ("Exception from ExceptionTesting::throwMyException");
		throw new MyException ();
	}
	
	protected void throwException() throws Exception {
		throw new Exception ("Exception from ExceptionTesting::throwException");
	}
	
	protected void throwByChoice(eExceptionChoice choice) throws Exception {
		switch (choice) {
		case ecException :
			throwException ();
			break;
		case ecMyException :
			throwMyException();
			break;
		}
	}
	
	protected void someHighLevelProc () throws MyHighLevelException {
		try {
			someMiddleLevelProc();
		} catch (MyMiddleLevelException ex) {
			// ... ... ...
			throw new MyHighLevelException("someHighLevelProc failed!", ex);
		}
	}
	
	protected void someMiddleLevelProc () throws MyMiddleLevelException {
		try {
			someLowLevelProc();
		} catch (MyLowLevelException ex) {
			// ... ... ...
			throw new MyMiddleLevelException("someMiddleLevelProc failed!", ex);
		}
	}
	
	protected void someLowLevelProc () throws MyLowLevelException {
		// ... ... ... 
		throw new MyLowLevelException("someLowLevelProc failed!");
	}
	

	public static void main(String[] args) {
		new ExceptionTesting();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ( e.getSource().equals(mBtnThrow1) ) {
				throwMyException();
			} else
			if ( e.getSource().equals(mBtnThrow2) ) {
				throwException();
			} else
			if ( e.getSource().equals(mBtnThrow3) ) {
				throwByChoice(eExceptionChoice.ecException);
			} else
			if ( e.getSource().equals(mBtnThrow4) ) {
				throwByChoice(eExceptionChoice.ecMyException);
			} else 
			if ( e.getSource().equals(mBtnThrow5) ) {
				someHighLevelProc();
			}
			
		} catch (MyException ex) {
			printExceptionInfo("MyException handler: ", ex);
		} 
		catch (Exception ex) {
			printExceptionInfo("Exception handler: ", ex);
		} 
		finally {
			System.out.println("finally block reached!");
		}
	}
	
	protected void printExceptionInfo(final String handler_title, Exception ex) {
		ByteArrayOutputStream stream;
		ex.printStackTrace(new PrintStream(stream = new ByteArrayOutputStream()));
		
		mTextArea.setText(handler_title + stream.toString());
	}

}

class MyException extends Exception 
{
	public MyException () {
		super ();
	}
	
	public MyException (String msg)	{
		super (msg);
	}
}

class MyLowLevelException extends Exception 
{
	public MyLowLevelException () {
		super ();
	}
	
	public MyLowLevelException (String msg)	{
		super (msg);
	}
	
	public MyLowLevelException (String msg, Throwable cause)	{
		super (msg, cause);
	}
}

class MyMiddleLevelException extends Exception 
{
	public MyMiddleLevelException () {
		super ();
	}
	
	public MyMiddleLevelException (String msg)	{
		super (msg);
	}
	
	public MyMiddleLevelException (String msg, Throwable cause)	{
		super (msg, cause);
	}
}

class MyHighLevelException extends Exception 
{
	public MyHighLevelException () {
		super ();
	}
	
	public MyHighLevelException (String msg)	{
		super (msg);
	}
	
	public MyHighLevelException (String msg, Throwable cause)	{
		super (msg, cause);
	}
}
