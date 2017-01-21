package java_api_testing.observer_api;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Класс, демонстрирующий использование интерфейсов java.util.Observer и java.util.Observable 
 * @author Alex
 *
 */
public class ObserverTesting {
	
	public ObserverTesting () {
		JFrame myFrame = new JFrame ( "java.util.Observer and java.util.Observable Testing" );
		myFrame.setSize(800, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		Observable my_observable = new MyObservable(top_panel);
		
		JPanel center_panel = new JPanel( new GridLayout(0, 1) );
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel tmp_panel = new JPanel(new BorderLayout());
		my_observable.addObserver(new MyObserver(tmp_panel, "class MyObserver implements Observer (#1):"));
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel(new BorderLayout());
		my_observable.addObserver(new MyObserver(tmp_panel, "class MyObserver implements Observer (#2):"));
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel(new BorderLayout());
		center_panel.add(tmp_panel);
		my_observable.addObserver(new MyObserver(tmp_panel, "class MyObserver implements Observer (#3):"));
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	/**
	 * Класс java.util.Observable позволяет делегировать некоторое сообщение между списком своих подписчиков (java.util.Observer-ы)
	 * @author Alex
	 *
	 */
	private class MyObservable extends Observable {
		public MyObservable ( JPanel parent_panel ) {
			JTextField 	msg_field = new JTextField("Some message text...", 25);
			JButton		send_msg_btn = new JButton("notifyObservers(Object message)");
			
			parent_panel.setBorder(BorderFactory.createTitledBorder("class MyObservable extends Observable:"));
			parent_panel.add(new JLabel("Observable message to send:  "));
			parent_panel.add(msg_field);
			parent_panel.add(send_msg_btn);
			
			send_msg_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Отправляем сообщение своим подписчикам путем вызова методов setChanged() и notifyObservers()
					MyObservable.this.setChanged();
					MyObservable.this.notifyObservers(msg_field.getText());
				}
			});
		}
	}
	
	/**
	 * Интерфейс java.util.Observer служит в качестве подписчика на события от класса java.util.Observable 
	 * @author Alex
	 *
	 */
	private class MyObserver implements Observer {
		JTextArea mMessageArea = new JTextArea();
		
		{ mMessageArea.setEditable(false); }
		
		public MyObserver ( JPanel parent_panel, String observer_name ) {
			parent_panel.setBorder(BorderFactory.createTitledBorder(observer_name));
			parent_panel.add(new JScrollPane(mMessageArea), BorderLayout.CENTER);
		}

		@Override
		public void update(Observable arg0, Object arg1) {
			// Принимаем событие через интерфейсный метод update()
			mMessageArea.append( new Date().toString() + " : " + arg1.toString() + "\n" );
		}
	}

	public static void main(String[] args) {
		new ObserverTesting ();
	}

}
