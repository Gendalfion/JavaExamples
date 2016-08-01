package java_testing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java_testing.Monkey.StaticBrain;

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
		new SynchronizationTesting(center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new ThreadTesting();
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// Простой класс, демонстрирующий создание, прерывание и уничтожение потоков в Java:
	private class SimpleThreadTest implements Runnable /* Для потока нужен интерфейс Runnable */ {
		Thread mThread;	
		JTextField mTextField = new JTextField( "500", 15); 
		JLabel mLabel = new JLabel();
		int mInterval;
		
		public SimpleThreadTest (JPanel panel) {
			this(panel, 200);
		}
		
		public SimpleThreadTest (JPanel panel, int default_interval) {
			mTextField.setText(Integer.toString(default_interval));
			
			JPanel 		tmp_panel;
			JButton 	tmp_btn;
			final JCheckBox	cb_daemon_thread = new JCheckBox("Create Daemon");
			panel.add(tmp_panel = new JPanel()); tmp_panel.add(mTextField); tmp_panel.add(cb_daemon_thread);
			panel.add(tmp_panel = new JPanel()); 
			
			tmp_panel.add(tmp_btn = new JButton("Start"));
			tmp_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (mThread == null) {
						try {
							mInterval = Integer.valueOf(mTextField.getText());
							if ( mInterval > 0 ) { 
								// Класс потока принимает интерфейс Runnable на вход (вместо указателя на метод):
								mThread = new Thread (SimpleThreadTest.this); 
								if (cb_daemon_thread.isSelected()) {
									// Создаем "демонический поток" (поток, который автоматически будет уничтожен, 
									// если закроются все остальные недемонические потоки приложения)
									mThread.setDaemon(true);
								}
								mThread.start();	// процедура запуска потока (может быть вызвана только один раз за время жизни объекта)
								cb_daemon_thread.setEnabled(false);
							}
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog( null, "Exception during thread start: " + exception.getMessage() );
						}
					} else {
						JOptionPane.showMessageDialog(null, "Thread is alive now!");
					}
				}
			});
			
			tmp_panel.add(tmp_btn = new JButton("Interrupt"));
			tmp_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (mThread != null) {
						try {
							mThread.interrupt();	// просим поток завершиться
							mThread.join(1000);		// ждем завершения в течении 1000 мс
							mThread = null;			// убиваем ссылку на поток для его очистки из памяти
							cb_daemon_thread.setEnabled(true);
						} catch ( Exception exception ) {
							JOptionPane.showMessageDialog( null, "Exception during thread interruption: " + exception.getMessage() );
						}
					} else {
						JOptionPane.showMessageDialog( null, "Thread is not alive now!" );
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
				System.out.println("interrupted...");
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// Класс, демострирующий синхронизацию потоков в Java:
	private class SynchronizationTesting {
		JLabel mCommonResource = new JLabel("I'm free");
		JLabel mAnotherResource = new JLabel("I'm free");
		
		// Метод synchronized имеет встроенную блокировку при вызовах в нескольких потоках:
		public synchronized void TakeResource (String thread_name, int block_time_ms) throws InterruptedException {
			mCommonResource.setText("I'm taken by " + thread_name);
			mCommonResource.repaint();
			
			try {
				Thread.sleep(block_time_ms);
			} finally {
				mCommonResource.setText("I'm free");
				mCommonResource.repaint();
			}
		}
		
		// Другой synchronized-метод резделяет блокировку с первым (т. е. класс может иметь только одну блокировку на все свои методы):
		public synchronized void TakeAnotherResource (String thread_name, int block_time_ms) throws InterruptedException {
			mAnotherResource.setText("I'm taken by " + thread_name);
			mAnotherResource.repaint();
					
			try {
				Thread.sleep(block_time_ms);
			} finally {
				mAnotherResource.setText("I'm free");
				mAnotherResource.repaint();
			}
		}
		
		public SynchronizationTesting (JPanel panel) {
			JPanel 		tmp_panel;
			JButton 	tmp_btn;
			JLabel		tmp_label;
			panel.add(tmp_panel = new JPanel(new GridLayout(0, 2, 5, 5)));
			new RandomResourceClient(tmp_panel, "Thread 1");
			new RandomResourceClient(tmp_panel, "Thread 2");
			new RandomResourceClient(tmp_panel, "Thread 3");
			
			panel.add(tmp_panel = new JPanel()); tmp_panel.add(mCommonResource);
			
			panel.add(tmp_panel = new JPanel()); tmp_panel.add(mAnotherResource);
		}
		
		private class RandomResourceClient implements Runnable {
			JLabel mStateLabel;
			String mThreadName = "Unnamed";
			
			private void setStateText(String text) {
				mStateLabel.setText(text);
				mStateLabel.repaint();
			}
			
			public RandomResourceClient (JPanel parent, String thread_name) {
				mThreadName = thread_name;
				
				final JToggleButton start_stop_btn = new JToggleButton("Start");
				parent.add(start_stop_btn);
				start_stop_btn.addActionListener(new ActionListener() {
					Thread mThread;
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if ( mThread == null ) {
							mThread = new Thread(RandomResourceClient.this);
							mThread.start();
							start_stop_btn.setText("Stop");
							start_stop_btn.setSelected(true);
						} else {
							try {
								mThread.interrupt();
								mThread.join();
								mThread = null;
								start_stop_btn.setText("Start");
								start_stop_btn.setSelected(false);
								mStateLabel.setText("Stopped");
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				parent.add(mStateLabel = new JLabel("Stopped"));
			}

			@Override
			public void run() {
				Random rand = new Random(new Date().getTime());
				final int RAND_WIDTH = 2000, RAND_MIN = 800;  
				try {
					while (!Thread.interrupted()) {
						setStateText("Sleeping");
						Thread.sleep(rand.nextInt(RAND_WIDTH) + RAND_MIN);
						setStateText("Trying res 1");
						TakeResource(mThreadName, rand.nextInt(RAND_WIDTH) + RAND_MIN);
						setStateText("Sleeping");
						Thread.sleep(rand.nextInt(RAND_WIDTH) + RAND_MIN);
						setStateText("Trying res 2");
						TakeAnotherResource(mThreadName, rand.nextInt(RAND_WIDTH) + RAND_MIN);
					}
				} catch ( Exception ex ) {
					setStateText("Interrupted");
				}
			}
		}
	}
	

}
