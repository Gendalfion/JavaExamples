package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GenericsTesting {
	JTextArea	mTextArea;
	
	public GenericsTesting () {
		JFrame myFrame = new JFrame ( "Generics Testing" );
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
		
		((JButton)bottom_panel.add(new JButton("Perform Test"))).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Mouse some_mouse = new Mouse();
				Bird some_bird = new Bird();
				
				MultyTrap<WarmBlooded> trap = new MultyTrap<WarmBlooded>();
				
				mTextArea.append( "\ntrap.snare(some_mouse, some_bird):\n" + trap.snare(some_mouse, some_bird) );
				mTextArea.append( "\ntrap.snare(some_bird):\n" + trap.snare(some_bird) );
				mTextArea.append( "\ntrap.releseAll():\n" + trap.releseAll() );
				
				MultyTrap<Mouse> mouse_trap = new MouseMultyTrap();
				mTextArea.append( "\nmouse_trap.snare(new Mouse(), new Mouse(), new Mouse()):\n" + mouse_trap.snare(new Mouse(), new Mouse(), new Mouse()) );
				mTextArea.append( "\nmouse_trap.releseAll():\n" + mouse_trap.releseAll() );
				// mouse_trap.snare(new Bird()); - ошибка во время компиляции!
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GenericsTesting();
	}

}

abstract class WarmBlooded {
	
}

class Mouse extends WarmBlooded {
	@Override
	public String toString () {
		return "I am a mouse!";
	}
}
class Bird extends WarmBlooded {
	@Override
	public String toString () {
		return "I am a bird!";
	}
}

class MultyTrap < SomeType extends WarmBlooded > {
	List<SomeType> mTrappedList = new ArrayList<>();
	
	public String snare ( /*Possible heap pollution warning if supress removed!*/@SuppressWarnings("unchecked") SomeType ... trapped ) {
		try (Formatter out = new Formatter()) {
			out.format("Snared animals:\n");
			for (SomeType item : trapped) {
				out.format("\tAnimal says: \"%s\"\n", item.toString());
				mTrappedList.add(item);
			}
			return out.toString();
		}
	}
	
	public String releseAll () {
		try (Formatter out = new Formatter()) {
			out.format("Released animals:\n");
			for (SomeType item : mTrappedList) {
				out.format("\tAnimal says: \"%s\"\n", item.toString());
			}
			mTrappedList.clear();
			return out.toString();
		}
	}
}

class MouseMultyTrap extends MultyTrap<Mouse> {
	
}
