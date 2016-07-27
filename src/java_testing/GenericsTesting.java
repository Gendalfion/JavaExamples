package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
				mTextArea.append( "\ntrap.releseAll():\n" + trap.releaseAll() );
				
				MultyTrap<Mouse> mouse_trap = new MouseMultyTrap();
				mTextArea.append( "\nmouse_trap.snare(new Mouse(), new Mouse(), new Mouse()):\n" + mouse_trap.snare(new Mouse(), new Mouse(), new Mouse()) );
				mTextArea.append( "\nmouse_trap.releseAll():\n" + mouse_trap.releaseAll() );
				// mouse_trap.snare(new Bird()); - ошибка во время компиляции!
				
				// trap = mouse_trap;	// Error - линия наследования не работает на обобщениях!
				
				// Однако имеется прием под названием Wildcards (групповый символы):
				MultyTrap<?> wildcard_trap;	// Использование безграничного группового символа <?>
				wildcard_trap = trap; 		// OK...
				wildcard_trap = mouse_trap;	// OK...
				// wildcard_trap.snare(new Mouse()); - 	// Error - <?> это неизвестный тип, мы не можем использовать функции класса,
														// принимающие обобщенный тип в качестве параметра
				
				MultyTrap<? extends Mouse> top_bounded_wildcard_trap; // Групповой символ, органиченный сверху
				top_bounded_wildcard_trap = mouse_trap;				// OK...
				top_bounded_wildcard_trap = new MultyTrap<Rat>();	// OK...
				//top_bounded_wildcard_trap = trap;					// Error...
				//top_bounded_wildcard_trap.snare(new Mouse());		// Error неопределенность в типе: <? extends Mouse> - это либо Mouse либо Rat либо что-то еще
				
				MultyTrap<? super Mouse> bottom_bounded_wildcard_trap; // Групповой символ, органиченный снизу
				bottom_bounded_wildcard_trap = mouse_trap;				// OK...
				bottom_bounded_wildcard_trap = trap;					// OK...
				//bottom_bounded_wildcard_trap = new MultyTrap<Rat>();	// Error...
				
				// Использование обобщенных методов:
				top_bounded_wildcard_trap = mouse_trap;
				MultyTrap<Rat> rat_trap = new MultyTrap<>();
				rat_trap.snare(new Rat(), new Rat());
				mouse_trap.snare(new Mouse(), new Mouse());
				
				// Перекачаем крыс в мышеловку:
				mTextArea.append( "\nMultyTrap.transfer(rat_trap, mouse_trap):\n" + MultyTrap.transfer(rat_trap, mouse_trap) );
				mTextArea.append( "\nmouse_trap.releseAll():\n" + mouse_trap.releaseAll() );
				// MultyTrap.transfer(new MultyTrap<Bird>(), mouse_trap); 	// Error: Bird does not extends Mouse 
				
				// Из-за стирания типов невозможно создать массивы на базе обобщенных объектов:
				//MultyTrap<WarmBlooded> animal_traps [] = new MultyTrap<WarmBlooded> [3];	// Compilation error!
				// Но можно сделать это для необработанного типа, но с предупреждением от компилятора (это вводит возможность совместимости с
				// унаследованным кодом из Java до версии 5.0):
				@SuppressWarnings("unchecked")	// - здесь мы просим компилятор доверять нам в плане типов
				MultyTrap<? extends WarmBlooded> animal_traps [] = new MultyTrap[3];
				animal_traps [0] = new MultyTrap<Bird>() {
					{ this.snare(new Bird(), new Bird ()); }
				};
				animal_traps [1] = new MultyTrap<Mouse>() {
					{ this.snare(new Mouse(), new Rat()); }
				};
				mTextArea.append( "\nMultyTrap.releaseTraps( animal_traps[0], animal_traps[1] ):\n" 
						+ MultyTrap.releaseTraps( animal_traps[0], animal_traps[1] ) );
				
				// Примеры хитрого использования обобщений в стандартной библиотеке Java:
				// 1) Класс Enum: 
				//		public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable { ... }
				//		- в данном случае граница типа запрещает присваивать или сравнивать перечисления разных типов
				//   	(enum Cat {...} enum Dog {...} ... Cat cat = new Dog (); - Compilation Error!)
				// 2) Метод Collections.sort: 
				//		public static <T extends Comparable<? super T>> void sort(List<T> list) { ... }	
				//		- данная граница типа позволяет использовать сравниваемые элементы в списке, процедура 
				//		сравнения для которых может быть определена для их супертипов (Comparable<? super T>)
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
	public String someString () { return "called someString ()!"; }
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
class Rat extends Mouse {
	@Override
	public String toString () {
		return "I am a rat!";
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
	
	public String releaseAll () {
		try (Formatter out = new Formatter()) {
			out.format("Released animals:\n");
			for (SomeType item : mTrappedList) {
				out.format("\tAnimal says: \"%s\"\n", item.toString());
			}
			mTrappedList.clear();
			return out.toString();
		}
	}
	
	// Применение обобщенных методов:
	static public <T extends WarmBlooded> String transfer ( MultyTrap<? extends T> from, MultyTrap<T> to ) {
		String res = new String();
		for ( T item : from.mTrappedList ) {
			res += to.snare(item);
		}
		res += from.releaseAll();
		return res;
	}
	
	// Пример применения массивов обобщенных типов - это аргументы переменной длины для методов:
	static public String releaseTraps ( MultyTrap<?> ... traps ) {
		String res = new String();
		for ( MultyTrap<?> item : traps ) {
			res += item.releaseAll();
		}
		return res;
	}
}

class MouseMultyTrap extends MultyTrap<Mouse> {
	
}

class WarmBloodedList <T extends WarmBlooded> extends ArrayList<T> {
	
}
