package java_api_testing.swing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

/**
 * Демонстрация использования акселераторов для получения событий нажатия клавиш на клавиатуре
 * 
 * @author Lab119Alex
 *
 */
public class AcceleratorTesting {
	
	public AcceleratorTesting () {
		JFrame myFrame = new JFrame ( "Accelerator Testing" );
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel center_panel = new JPanel ( new GridLayout(0, 3, 5, 5) );
		myFrame.add(center_panel);
		
		JToggleButton leftBtn 	= createBtn("Left(A)");
		JToggleButton rightBtn 	= createBtn("Right(D)");
		JToggleButton upBtn 	= createBtn("Up(W)");
		JToggleButton downBtn 	= createBtn("Down(S)");
		JToggleButton spaceBtn 	= createBtn("Space");
		
		center_panel.add(new JLabel());
		center_panel.add(upBtn);
		center_panel.add(new JLabel());
		center_panel.add(leftBtn);
		center_panel.add(spaceBtn);
		center_panel.add(rightBtn);
		center_panel.add(new JLabel());
		center_panel.add(downBtn);
		
		ActionListener listener = new ActionListener() {
			String lastCmd = "";
			@Override public void actionPerformed(ActionEvent e) {
				//System.out.println( e.getActionCommand() );
				String cmd = e.getActionCommand();
				if ( !lastCmd.equals(cmd) ) {
					lastCmd = cmd;
				} else {
					return;
				}
				if ( "Left_pressed".equals(cmd) ) {
					leftBtn.setSelected(true);
				} else 
				if ( "Left_released".equals(cmd) ) {
					leftBtn.setSelected(false);
				} else 
				if ( "Right_pressed".equals(cmd) ) {
					rightBtn.setSelected(true);
				} else 
				if ( "Right_released".equals(cmd) ) {
					rightBtn.setSelected(false);
				} else 
				if ( "Up_pressed".equals(cmd) ) {
					upBtn.setSelected(true);
				} else 
				if ( "Up_released".equals(cmd) ) {
					upBtn.setSelected(false);
				} else 
				if ( "Down_pressed".equals(cmd) ) {
					downBtn.setSelected(true);
				} else 
				if ( "Down_released".equals(cmd) ) {
					downBtn.setSelected(false);
				} else 
				if ( "Space_pressed".equals(cmd) ) {
					spaceBtn.setSelected(true);
				} else 
				if ( "Space_released".equals(cmd) ) {
					spaceBtn.setSelected(false);
				}
			}
		};
		
		// Создаем контекстное меню и прикрепляем его к JLabel с нулевым размером
		// для того, чтобы меню было невидимым пользователю, но при этом полностью функционировало:
		JPopupMenu menu = new JPopupMenu();
		JLabel tmp_label = new JLabel();
		tmp_label.add(menu);
		center_panel.add(tmp_label);
		
		createMenuItem ( menu, listener, "Left", 	KeyEvent.VK_LEFT, 	KeyEvent.VK_A );
		createMenuItem ( menu, listener, "Right", 	KeyEvent.VK_RIGHT, 	KeyEvent.VK_D );
		createMenuItem ( menu, listener, "Up", 		KeyEvent.VK_UP, 	KeyEvent.VK_W );
		createMenuItem ( menu, listener, "Down", 	KeyEvent.VK_DOWN, 	KeyEvent.VK_S );
		createMenuItem ( menu, listener, "Space", 	KeyEvent.VK_SPACE );
		
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);
		myFrame.setVisible(true);
	}
	
	/**
	 * Создает кнопку JToggleButton с указанным текстом
	 * @param btn_title - текст на кнопке
	 * @return Возвращает созданную кнопку
	 */
	private JToggleButton createBtn ( String btn_title ) {
		JToggleButton btn = new JToggleButton (btn_title);
		btn.setPreferredSize(new Dimension(90, 90));
		btn.setFocusable(false);
		return btn;
	}
	
	/**
	 * Создает пункты меню для JPopupMenu и привязывает их к определенным кодам клавиш на клавиатуре
	 * @param menu - меню, куда будут добавлены созданные элементы
	 * @param listener - обработчик срабатывания пункта меню
	 * @param item_action - строка, определяющая действие, связанное с пунктом меню
	 * @param key_codes - коды клавиш, связанные с действием
	 */
	private void createMenuItem ( JPopupMenu menu, ActionListener listener, String item_action, int ... key_codes ) {
		for ( int key_code : key_codes ) {
			JMenuItem tmp_item = new JMenuItem( item_action + "_pressed" );
			// Устанавливаем акселератор на нажатие кнопки с указанным кодом, 
			// который будет вызывать обработчик listener для данного пункта меню:
			tmp_item.setAccelerator(KeyStroke.getKeyStroke(key_code, 0, false));
			tmp_item.addActionListener(listener);
			menu.add(tmp_item);
			
			tmp_item = new JMenuItem( item_action + "_released" );
			// Устанавливаем акселератор на отжатие данной кнопки:
			tmp_item.setAccelerator(KeyStroke.getKeyStroke(key_code, 0, true));
			tmp_item.addActionListener(listener);
			menu.add(tmp_item);
		}
	}

	public static void main(String[] args) {
		new AcceleratorTesting ();
	}
}
