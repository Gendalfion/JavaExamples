package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VarargsTesting {
	JTextArea	mTextArea;
	
	public VarargsTesting () {
		JFrame myFrame = new JFrame ( "Variable Arguments Testing" );
		
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
		
		((JButton)bottom_panel.add(new JButton("Print var args"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintVarArgs ("argument #1", "argument #2", "argument #3");
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new VarargsTesting();
	}
	
	public void PrintVarArgs ( Object ... list )
	{
		try ( Formatter formatter = new Formatter (new StringBuilder()) )
		{
			formatter.format("The object list size: %d\r\n", list.length);
		
			for (Object obj: list)
			{
				formatter.format(obj.toString() + "\r\n");
			}
			
			mTextArea.append(formatter.toString());
		}	
	}

}
