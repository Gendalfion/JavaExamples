package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class EnumTesting {
	JTextArea	mTextArea;
	
	
	
	public EnumTesting () {
		JFrame myFrame = new JFrame ( "Enum Testing" );
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
		
		((JButton)bottom_panel.add(new JButton("Print eWeekday"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextArea.append("eWeekday.values():\r\n");
				for (eWeekday item : eWeekday.values()) {
					mTextArea.append("\t" + item.name() + "\r\n");
				}
			}
		});
		
		((JButton)bottom_panel.add(new JButton("Enum Method Call"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					eWeekday some_day = Enum.valueOf(eWeekday.class, JOptionPane.showInputDialog("Input item of eWeekday:"));
					
					if ( some_day.compareTo(eWeekday.Thursday) == 0 ) {
						mTextArea.append("Enum item method call: " + eWeekday.Thursday.toString() + "\r\n");
					} else {
						mTextArea.append("eWeekday." + some_day.toString() + ".getSomeVar() = " + some_day.getSomeVar() + "\r\n");
					}
				} catch (IllegalArgumentException | NullPointerException ex) {
					if (! (ex instanceof NullPointerException) )
						mTextArea.append("Error in day name: " + ex.getMessage() + "\r\n");
				} 
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//EnumTesting.eWeekday some_day = eWeekday.Frayday;
		//System.out.println(some_day.mSomeVar);
		
		new EnumTesting();
	}
	
	public enum eWeekday {
		Sunday (0), Monday (2),  Tuesday (4), Wednesday (8), 
		Thursday (16) {
			@Override
			public String toString() {
				return "You called eWeekday.Thursday.toString ()";
			}
		}
		, Friday (32), Saturday (64);
		final int mSomeVar;
		private eWeekday(int some_var) {
			mSomeVar = some_var;
		}
		public int getSomeVar () { return mSomeVar; }
	}

}
