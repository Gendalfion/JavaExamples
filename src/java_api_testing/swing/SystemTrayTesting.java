package java_api_testing.swing;

import java.awt.AWTException;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Демонстрация работы с классами java.awt.SystemTray и java.awt.TrayIcon для отображения
 * значка в области уведомлений ОС (для Windows - это системный лоток)
 * 
 * @author Lab119Alex
 *
 */
public class SystemTrayTesting {
	
	TrayIcon 	mTrayIcon;
	MenuItem  	mShowFrameItem = new MenuItem("Show GUI...");
	
	{
		PopupMenu trayIconMenu = new PopupMenu();
		trayIconMenu.add(mShowFrameItem);
		
		Icon  icon 		= UIManager.getIcon("OptionPane.informationIcon");
		Image iconImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon ( null, iconImage.getGraphics(), 0, 0 );
		
		// Создаем объект java.awt.TrayIcon с указанным изображением, подсказкой и всплывающим меню:
		mTrayIcon = new TrayIcon(iconImage, "java.awt.SystemTray Testing", trayIconMenu);
		mTrayIcon.setImageAutoSize(true);
	}
	
	public SystemTrayTesting () {
		JFrame myFrame = new JFrame ( "java.awt.SystemTray Testing" );
		myFrame.setSize(300, 200);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton hideToTrayBtn = new JButton( "Hide to tray..." );
		myFrame.add( new JPanel(new GridBagLayout()).add(hideToTrayBtn).getParent() );
		
		hideToTrayBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				setGUIVisibility (false, myFrame);
			}
		});
		
		ActionListener trayIconListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				setGUIVisibility (true, myFrame);
			}
		};
		
		mShowFrameItem.addActionListener(trayIconListener);
		mTrayIcon.addActionListener(trayIconListener);
		
		myFrame.setVisible(true);
	}
	
	private void setGUIVisibility ( boolean visible, JFrame frame ) {
		try {
			// Добавляем/удаляем объект mTrayIcon с области уведомлений системы: 
			if ( visible ) {
				SystemTray.getSystemTray().remove(mTrayIcon);
			} else {
				SystemTray.getSystemTray().add(mTrayIcon);
			}
			frame.setVisible(visible);
		} catch (AWTException exception) {
			JOptionPane.showMessageDialog(frame, exception, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new SystemTrayTesting ();
	}
}
