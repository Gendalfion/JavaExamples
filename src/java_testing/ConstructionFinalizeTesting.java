package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.ref.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConstructionFinalizeTesting {
	JTextArea	mTextArea;
	
	public ConstructionFinalizeTesting () {
		JFrame myFrame = new JFrame ( "Construction And Finalize Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		main_panel.add(new JScrollPane(mTextArea = new JTextArea()), BorderLayout.CENTER);
		mTextArea.setEditable(false);
		JPanel bottom_panel = new JPanel(new FlowLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextArea.setText("");
			}
		});
		mTextArea.setComponentPopupMenu(clear_menu);
		
		//
		// TODO Поместите здесь свой код...
		//
		
		((JButton)bottom_panel.add(new JButton("Create object"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//mTextArea.append(new MyClass ().toString() + "\r\n");
				//mTextArea.append(new MyClass ("My String").toString() + "\r\n");
				mTextArea.append(new MyClass ("My String", 11).toString() + "\r\n");
			}
		});
		
		((JButton)bottom_panel.add(new JButton("System.gc()"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.gc();
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ConstructionFinalizeTesting();
	}

}

class MyClass {
	String mClassString;
	int mClassIntVal;
	
	{
		mClassString = "Default string";
		mClassIntVal = -1;
	}
	
	public MyClass () {}
	
	public MyClass (String str) {
		mClassString = str;
	}
	
	public MyClass (String str, int int_val) {
		this(str);
		mClassIntVal = int_val;
	}
	
	@Override
	protected void finalize() throws Throwable {
		JOptionPane.showMessageDialog(null, this.toString() + " is finalized!");

		super.finalize();
	}
	
	@Override
	public String toString() {
		return "mClassString = " + mClassString + "; mClassIntVal = " + String.valueOf(mClassIntVal);
	}
	
}
