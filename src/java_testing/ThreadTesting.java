package java_testing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ThreadTesting {
	
	public ThreadTesting () {
		JFrame myFrame = new JFrame ( "Thread Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel (new GridLayout(0, 3, 5, 5));
		main_panel.add(center_panel, BorderLayout.CENTER);
		new SimpleThreadTest (center_panel, 100);
		new SimpleThreadTest (center_panel, 300);
		new SimpleThreadTest (center_panel, 600);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new ThreadTesting();
	}
	
	private class SimpleThreadTest implements Runnable /* Для потока нужен интерфейс Runnable */ {
		Thread mThread = new Thread(this);	// Класс потока принимает этот интерфейс на вход (вместо указателя на метод)
		JTextField mTextField = new JTextField( "500", 15); 
		JLabel mLabel = new JLabel();
		int mInterval;
		
		public SimpleThreadTest (JPanel panel) {
			this(panel, 200);
		}
		
		public SimpleThreadTest (JPanel panel, int default_interval) {
			mTextField.setText(Integer.toString(default_interval));
			
			JPanel tmp_panel;
			JButton tmp_btn;
			panel.add(tmp_panel = new JPanel()); tmp_panel.add(mTextField);
			panel.add(tmp_panel = new JPanel()); 
			
			tmp_panel.add(tmp_btn = new JButton("Start"));
			tmp_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!mThread.isAlive()) {
						try {
							mInterval = Integer.valueOf(mTextField.getText());
							if ( mInterval > 0 ) { 
								mThread.start();	// процедура запуска потока
							}
						} catch ( Exception exception ) {}
					} else {
						JOptionPane.showMessageDialog(null, "Thread is alive now!");
					}
				}
			});
			
			tmp_panel.add(tmp_btn = new JButton("Interrupt"));
			tmp_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (mThread.isAlive()) {
						try {
							mThread.interrupt();	// просим поток завершиться
							mThread.join(1000);		// ждем завершения в течении 1000 мс
						} catch ( Exception exception ) {}
					} else {
						JOptionPane.showMessageDialog(null, "Thread is not alive now!");
					}
				}
			});
			
			
			panel.add(tmp_panel = new JPanel()); tmp_panel.add(mLabel);
			
			
		}
		
		public void run() {
			// Процедура потока:
			String s = "";
			try {
				while (!Thread.interrupted()) {
					Thread.sleep(mInterval);
					s += "."; if (s.length() > 20) { s = "."; }
					mLabel.setText(s);
					mLabel.repaint();
				}
			} catch ( InterruptedException ex ) {
				
			}
			System.out.println("interrupted...");
		}
	}
	

}
