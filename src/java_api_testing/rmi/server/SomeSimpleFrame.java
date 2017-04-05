package java_api_testing.rmi.server;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Вспомогательный класс, используемый в качестве объекта при возврате данных 
 * из метода удаленного RMI-интерфейса.
 * Используется в классе:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * 
 * @author Lab119Alex
 *
 */
public class SomeSimpleFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	public SomeSimpleFrame ( String title, String server_name ) {
		super(title);
		
		setMinimumSize (new Dimension(500, 300));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new GridLayout(0, 1));
		
		main_panel.add( new JPanel(new GridBagLayout()).add(new JLabel("I was created on server \"" + server_name + "\"")).getParent() );
		
		JButton simpleMessageButton = new JButton("Show simple message...");
		main_panel.add( new JPanel().add(simpleMessageButton).getParent() );
		
		// Т. к. данный объект будет передаваться через удаленный вызов процедур,
		// для корректной передачи списка слушателей кнопки simpleMessageButton мы должны
		// использовать сериализуемый объект в качестве интерфейса ActionListener:
		simpleMessageButton.addActionListener( this );
		
		add(main_panel);
	}

	@Override public void actionPerformed(ActionEvent e) {
		// Объект класса SimpleMessage создается при нажатии на кнопку уже на клиентской стороне,
		// поэтому SimpleMessage может быть не сериализуемым (однако, байт-код класса SimpleMessage должен 
		// быть доступен клиенту):
		new SimpleMessage( "I was created on client side, OS: \"" 
				+ System.getProperty("os.name") + " " + System.getProperty("os.version") + "\"", SomeSimpleFrame.this ).showMessage ();
	}
}

/**
 * Вспомогательный класс, конструктор которого (в рамках данного примера) выполняется 
 * на клиентской стороне клиент-серверного RMI-приложения
 * 
 * @author Lab119Alex
 *
 */
class SimpleMessage {
	String mMessage;
	Component mParent;
	
	public SimpleMessage ( String message, Component parent ) {
		mMessage = message;
		mParent	 = parent;
	}
	
	public void showMessage () {
		JOptionPane.showMessageDialog(mParent, mMessage);
	}
}
