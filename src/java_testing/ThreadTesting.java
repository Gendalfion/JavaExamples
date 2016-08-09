package java_testing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ThreadTesting {
	
	public ThreadTesting () {
		JFrame myFrame = new JFrame ( "Thread Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel (new GridLayout(0, 3, 5, 5));
		final JTextArea exceptions_area = new JTextArea();	
		main_panel.add(center_panel, BorderLayout.CENTER);
		main_panel.add(new JScrollPane(exceptions_area), BorderLayout.SOUTH);
		new SimpleThreadTest (center_panel, 100, "My Thread #1");
		new SimpleThreadTest (center_panel, 300, "My Thread #2");
		new SimpleThreadTest (center_panel, 600, "My Thread #3");
		new SynchronizationTesting(center_panel);
		new WaitNotifyTesting (center_panel);
		
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exceptions_area.setText("");
			}
		});
		exceptions_area.setComponentPopupMenu(clear_menu);
		exceptions_area.setEditable(false);
		exceptions_area.setRows(4);
		
		// �� ����� ���������� ���������� ������������� ��������� ��� ���� �������
		// (����� ��� � ��� ���������� ������ Thread ��� ��� ������ ������� ThreadGroup):
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				exceptions_area.append(t + " raised exception: " + e + "\n\r");
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new ThreadTesting();
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// ������� �����, ��������������� ��������, ���������� � ����������� ������� � Java:
	private class SimpleThreadTest implements Runnable /* ��� ������ ����� ��������� Runnable */ {
		Thread mThread;	
		JTextField mTextField = new JTextField( "500", 15); 
		JLabel mLabel = new JLabel();
		int mInterval;
		
		public SimpleThreadTest (JPanel panel, int default_interval, final String thread_name) {
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
								// ����� ������ ��������� ��������� Runnable �� ���� (������ ��������� �� �����):
								mThread = new Thread (SimpleThreadTest.this, thread_name); 
								if (cb_daemon_thread.isSelected()) {
									// ������� "������������ �����" (�����, ������� ������������� ����� ���������, 
									// ���� ��������� ��� ��������� �������������� ������ ����������)
									mThread.setDaemon(true);
								}
								mThread.start();	// ��������� ������� ������ (����� ���� ������� ������ ���� ��� �� ����� ����� �������)
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
							mThread.interrupt();	// ������ ����� �����������
							mThread.join(1000);		// ���� ���������� � ������� 1000 ��
							mThread = null;			// ������� ������ �� ����� ��� ��� ������� �� ������
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
			// ��������� ������:
			String s = "";
			try {
				while (!Thread.interrupted()) {
					Thread.sleep(mInterval);
					s += "."; if (s.length() > 20) { s = "."; }
					mLabel.setText(s);
					mLabel.repaint();
				}
			} catch ( InterruptedException ex ) {
				// ����������� ���������� NullPointerException ��� ����, ����� ����������� ��� � default-�� �����������
				throw new NullPointerException("Thread succesfully interrupted...");
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------
	// �����, �������������� ������������� ������� � Java:
	private class SynchronizationTesting {
		JLabel mCommonResource = new JLabel("I'm free");
		JLabel mAnotherResource = new JLabel("I'm free");
		
		// ����� synchronized ����� ���������� ���������� ��� ������� � ���������� �������:
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
		
		// ������ synchronized-����� ��������� ���������� � ������ (�. �. ����� ����� ����� ������ ���� ���������� �� ��� ���� ������):
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
	
	//---------------------------------------------------------------------------------------------------------------
	// �����, �������������� ������������� ������� ��� ������ ������� wait � notify ������ Object � Java
	// ������ ���� ������� ��������� ������� ����� (wait), ��������� � ������ ��������, �� ������������� ������������� ������� � ������ ������ (notify)
	private class WaitNotifyTesting {
		JLabel mFirstRecieverLbl = new JLabel();
		JLabel mSecondRecieverLbl = new JLabel();
		JLabel mFirstMsgCountLabel = new JLabel ();
		JLabel mSecondMsgCountLabel = new JLabel ();
		JLabel mFirstThreadStateLabel = new JLabel ();
		JLabel mSecondThreadStateLabel = new JLabel ();
		
		List<String> mMsgQueue = new ArrayList<>();
		final int MAX_MSG_COUNT = 10;
		
		int WaitUntilQueueSizeChange () throws InterruptedException {
			synchronized (mMsgQueue) { // ���������� ���������� �� ������ mMsgQueue
				int old_size = mMsgQueue.size(), new_size;
				while ( old_size == (new_size = mMsgQueue.size()) ) {
					mMsgQueue.wait(); // ����, ���� ���-������ ���������� � ������� mMsgQueue.this
				}
				return new_size;
			}
		}
		
		synchronized void PutMessage (String msg) throws InterruptedException {
			while (mMsgQueue.size() >= MAX_MSG_COUNT) {	// ���������, ����� ������� ��������� �� ���� ������
				WaitUntilQueueSizeChange ();
			}
			synchronized (mMsgQueue) {
				mMsgQueue.add(msg);
				mMsgQueue.notifyAll(); // ���������� ������� ��� ������� mMsgQueue (������� �������� ��� ������, ��������� ������ mMsgQueue.wait)
			}
			this.notify (); // ���������� ������� ��� ������� this, ������� �������� ������ 1 �����
		}
		
		synchronized String PickMessage (long wait_ms) throws InterruptedException {
			if (mMsgQueue.isEmpty()) {
				this.wait(wait_ms);			// ���������� ������ wait(time_ms) ��� �������� ������� � ������� ��������� ��������
				if (mMsgQueue.isEmpty()) {	// ���������, ����� ������� ��������� �� ���� �����
					return null; 			// ������� �����, ������ ����� ������� �������� ���������
				}
			}
			String res;
			synchronized (mMsgQueue) { 
				res = mMsgQueue.remove(0);
				mMsgQueue.notifyAll(); // ���������� ������� ��� ������� mMsgQueue (������� �������� ��� ������, ��������� ������ mMsgQueue.wait) 
			} 
			return res;
		}
		
		public WaitNotifyTesting (JPanel panel) {
			JPanel 		tmp_panel;
			JButton 	tmp_btn;
			panel.add(tmp_panel = new JPanel());
			tmp_panel.add( tmp_btn = new JButton() );
			
			panel.add(tmp_panel = new JPanel(new GridLayout(3, 1))); 
			tmp_panel.add(mFirstRecieverLbl); 
			tmp_panel.add(mFirstMsgCountLabel);
			tmp_panel.add(mFirstThreadStateLabel);
			
			panel.add(tmp_panel = new JPanel(new GridLayout(3, 1))); 
			tmp_panel.add(mSecondRecieverLbl); 
			tmp_panel.add(mSecondMsgCountLabel);
			tmp_panel.add(mSecondThreadStateLabel);
			
			new ButtonUpdater(tmp_btn);
			new MessageSenderOnBtnClick (tmp_btn);
			new MessagePicker (mFirstRecieverLbl, mFirstMsgCountLabel, mFirstThreadStateLabel, 3000);
			new MessagePicker (mSecondRecieverLbl, mSecondMsgCountLabel, mSecondThreadStateLabel, 3000);
		}
		
		private class ButtonUpdater implements Runnable {	// ������� ����� ��� ���������� ������� �� ������
			JButton mBtn;
			
			public ButtonUpdater( JButton btn ) {
				mBtn = btn;
				new Thread (this).start();
			}
			
			private void setBtnText (String msg) {
				mBtn.setText(msg);
				mBtn.repaint();
			}
			
			@Override
			public void run() {
				setBtnText("Send message...");
				try {
					while (!Thread.interrupted()) {
						// ������� ����� ��������� �������:
						int msg_cnt = WaitUntilQueueSizeChange();
						mBtn.setEnabled(true);
						if (msg_cnt <= 0) {
							setBtnText("Queue is empty...");
						} else if (msg_cnt >= MAX_MSG_COUNT) {
							mBtn.setEnabled(false);
							setBtnText("Queue is full!");
						} else {
							setBtnText("Msg count = " + msg_cnt);
						}
					}
				} catch (InterruptedException ex) {}
			}
		}
		
		private class MessageSenderOnBtnClick implements Runnable, ActionListener { // ������� ����� ��� ���������� ��������� � ������� �� ������� ������
			JButton mBtn;
			int mMsgSend = 0;
			
			public MessageSenderOnBtnClick( JButton btn ) {
				mBtn = btn;
				mBtn.addActionListener(this);
				new Thread (this).start();
			}
			
			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						synchronized (this) { // ���������� ��������� ������������� (�. �. ������ notify � wait ������ ���� ������ ������ synchronized-������)
							this.wait ();	// ���� ������� �� ������
							PutMessage("Message #" + (++mMsgSend)); // �� ������� ��������� ��������� � �������
						}
					}
				} catch (InterruptedException ex) {}
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					this.notifyAll(); // ���������� ��������� �� ����������� ���������� ������� ������ ������
				}
			}
		}
		
		private class MessagePicker implements Runnable { // ������� �����, �������� ��������� �� ������� � ������������ ��������������
			JLabel mLabel;
			JLabel mMsgCntLabel;
			JLabel mThreadStateLabel;
			long mPickTimeout;
			
			Thread mPickerThread;
			ThreadLocal<Integer> mTotalPickedCount = new ThreadLocal<>(); // �����-������� ��� ���������� �������, �������������� ����������� ������
			
			public MessagePicker (JLabel label, JLabel msg_cnt_label, JLabel thread_state_label, long pick_timeout) {
				mLabel = label;
				mMsgCntLabel = msg_cnt_label;
				mThreadStateLabel = thread_state_label;
				mPickTimeout = pick_timeout;
				(mPickerThread = new Thread(this)).start();
				
				new Thread( new Runnable() {	// ������� ������� �����, ���������� ��������� ������ mPickerThread
					@Override
					public void run() {
						Thread.State prev_state = null;
						try {
							while (!Thread.interrupted()) {
								if ( !mPickerThread.getState().equals(prev_state) ) {
									SetThreadStateLabelText ((prev_state = mPickerThread.getState()).toString());
								}
								Thread.sleep(5);
							}
						} catch (InterruptedException ex) {}
					}
				} ).start();
			}
			
			private void SetLabelText (String text) {
				mLabel.setText(text);
				mLabel.repaint();
			}
			
			private void SetThreadStateLabelText (String text) {
				mThreadStateLabel.setText(text);
				mThreadStateLabel.repaint();
			}
			
			private void BlinkText (String text, int blink_time) throws InterruptedException {
				for (int i = 0; i < 2; ++i) {
					SetLabelText (text); Thread.sleep(blink_time);
					SetLabelText (""); Thread.sleep(blink_time);
				}
				SetLabelText (text);
			}
			
			@Override
			public void run() {
				mTotalPickedCount.set(new Integer(0)); // ������������� ��������� �������� � ��������� ������ mTotalPickedCount ��� �������� ������
				try {
					while (!Thread.interrupted()) {
						SetLabelText ("Trying to Pick a Message...");
						String message = PickMessage(mPickTimeout); // �������� �������� ��������� � ������� � ������� �������� mPickTimeout
						if (message == null) {
							SetLabelText ("Message Pick Timeout...");
							Thread.sleep(4000);
						} else {
							mTotalPickedCount.set(mTotalPickedCount.get() + 1); // ������ set/get ������ ThreadLocal �������� 
									// � ���������� ������� �������� ��� ������� ������ ���������
							mMsgCntLabel.setText("Total picked: " + mTotalPickedCount.get()); mMsgCntLabel.repaint();
							BlinkText ("Picked message: " + message, 200);
							
							// ��������� ���� ���������� ��� �������� ������ � ��������� RUNNABLE:
							for (long j = 0; j < 400L; ++j) {
								synchronized (WaitNotifyTesting.this) { 
									for (long i = 0; i < 50000000L; ++i) { long g = i + j; }
								}
								Thread.yield(); // ����� �� ������� �������� ����� ������� �������� ��
							}
							
							Thread.sleep(1000);
						}
					}
				} catch (InterruptedException ex) {}
			}
		}
	}
}
