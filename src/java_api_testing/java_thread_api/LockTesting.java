package java_api_testing.java_thread_api;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LockTesting {
	
	Random mRandom = new Random( System.currentTimeMillis() );
	
	public LockTesting () {
		JFrame myFrame = new JFrame ( "Lock Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		new ReentrantLockTesting (center_panel);
		new ReadWriteLockTesting (center_panel);
		new ConditionLockTesting (center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new LockTesting();
	}
	
	// **********************************************************************************************
	// �����, ��������������� ������ ������ ������� ���������� ReentrantLock
	private class ReentrantLockTesting {
		// ������� ������ ������ ReentrantLock - ������� ���������� �� ������ � ������������ ���������� ������ ���������� ������ ������,
		// ��� ���������� ������ �����������
		Lock mReentrantLock = new ReentrantLock(true /* ������ �������� ��������� �� "���������" ������������� ���������� */);
		// ������� �������� ����������, ������� ��� ������ ������ ���������� ��������� ��������� ���������� �������� �� ��������� ����������
		// ������������� ��, ��� ���������� ���������� Java (�� ��������� synchronized) �������� �� �������� 
		
		static final int THREAD_SLEEP_TIME_MS = 6500; 
		
		public ReentrantLockTesting ( JPanel parent_panel ) {
			JPanel grid_panel = new JPanel (new GridLayout(0, 2));
			grid_panel.setBorder(BorderFactory.createTitledBorder("ReentrantLock(true) testing"));
			parent_panel.add(grid_panel);
			
			new LockPicker(grid_panel);
			new LockPicker(grid_panel);
			new LockPicker(grid_panel);
			new LockTryTester(grid_panel);
		}
		
		private class LockPicker implements Runnable {
			JButton mLockBtn = new JButton("get Lock");
			JLabel  mLockLabel = new JLabel ("...");
			
			LockPicker ( JPanel grid_panel ) {
				grid_panel.add( new JPanel().add(mLockBtn).getParent() );
				grid_panel.add( new JPanel().add(mLockLabel).getParent() );
				
				mLockBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						synchronized (LockPicker.this) {
							LockPicker.this.notify();	// �������� ������, ����� �� ����� �����������
						}	
					}
				});
				
				new Thread(this).start();
			}
			
			public void run() {
				while ( !Thread.interrupted() ) {
					try {
						synchronized (this) {
							this.wait(); // ���� ������� �����, ����� ������ �����������
						}
						mLockLabel.setText("Trying to get Lock..."); mLockLabel.repaint();
						mReentrantLock.lock();	// �������� ���������� mReentrantLock
						
						mLockLabel.setText("I am the Lock owner!"); mLockLabel.repaint();
						Thread.sleep( mRandom.nextInt(THREAD_SLEEP_TIME_MS) + 1000 );
					} catch ( Exception e ) {
						mLockLabel.setText(e.getMessage()); mLockLabel.repaint();
					} finally {
						mReentrantLock.unlock(); // ����������� ���������� mReentrantLock
							// � ������� �� ���������� ���������� Java, API ���������� Lock �����
							// ��������� ������� ����������, ���� �� ��������� ��������������� ��� ����������������!
						mLockLabel.setText("..."); mLockLabel.repaint();
					}
				}
			}
		}
		
		private class LockTryTester implements Runnable {
			JButton mTryLockBtn = new JButton("try Lock");
			JLabel  mTryLockLabel = new JLabel ("...");
			
			LockTryTester ( JPanel grid_panel ) {
				grid_panel.add( new JPanel().add(mTryLockBtn).getParent() );
				grid_panel.add( new JPanel().add(mTryLockLabel).getParent() );
				
				mTryLockBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						synchronized (LockTryTester.this) {
							LockTryTester.this.notify();	// �������� ������, ����� �� ����� �����������
						}	
					}
				});
				
				new Thread(this).start();
			}
			
			public void run() {
				while ( !Thread.interrupted() ) {
					boolean lock_aqcuired = false;
					try {
						synchronized (this) {
							this.wait(); // ���� ������� �����, ����� ������ �����������
						}
						mTryLockLabel.setText("Trying to get Lock during 2 seconds..."); mTryLockLabel.repaint();
						lock_aqcuired = mReentrantLock.tryLock(2, TimeUnit.SECONDS);	// ������� �������� ���������� mReentrantLock � ������� 2-� ������
						
						if ( lock_aqcuired ) {
							// ���������� ���� ������� ��������� �� ���������� �����
							mTryLockLabel.setText("I am the Lock owner!"); mTryLockLabel.repaint();
							Thread.sleep( mRandom.nextInt(THREAD_SLEEP_TIME_MS) + 1000 );
						} else {
							// ���������� ��������� �� ������� �� ���������� �����...
							mTryLockLabel.setText("Failed to get the Lock in given time..."); mTryLockLabel.repaint();
							Thread.sleep( 3000 );
						}
						
					} catch ( Exception e ) {
						mTryLockLabel.setText(e.getMessage()); mTryLockLabel.repaint();
					} finally {
						if ( lock_aqcuired ) {
							mReentrantLock.unlock(); // ����������� ���������� mReentrantLock, ���� ��� ���� ������� ��������� �������
						}
							// � ������� �� ���������� ���������� Java, API ���������� Lock �����
							// ��������� ������� ����������, ���� �� ��������� ��������������� ��� ����������������!
						mTryLockLabel.setText("..."); mTryLockLabel.repaint();
					}
				}
			}
		}
	}
	
	// **********************************************************************************************
	// �����, ��������������� ������ ������ ���������� ������/������
	private class ReadWriteLockTesting {
		// ������� ������ ������ ReentrantReadWriteLock - ����������, ����������� ����� ������������� �� ��������� � ���������:
		// �������� ����� �������� ���������� ��� ����������� ������ ��������� (�� �� ���������);
		// �������� ����� �������� ���������� ������ ����� ��� ����������� �� ���� ������ ��������� � ���������
		ReadWriteLock mReentrantReadWriteLock = new ReentrantReadWriteLock(false /* ������� �� ����������� ���������� */);
		
		static final int THREAD_SLEEP_TIME_MS = 6500; 
		
		public ReadWriteLockTesting ( JPanel parent_panel ) {
			JPanel grid_panel = new JPanel (new GridLayout(0, 2));
			grid_panel.setBorder(BorderFactory.createTitledBorder("ReentrantReadWriteLock(false) testing"));
			parent_panel.add(grid_panel);
			
			new Reader ( grid_panel );
			new Reader ( grid_panel );
			new Writer ( grid_panel );
			new Writer ( grid_panel );
			
		}
		
		private class Reader implements Runnable {
			JButton mReadBtn = new JButton("readLock()");
			JLabel  mLabel = new JLabel("...");
			
			public Reader ( JPanel grid_panel ) {
				grid_panel.add( new JPanel().add(mReadBtn).getParent() );
				grid_panel.add( new JPanel().add(mLabel).getParent() );
				
				mReadBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						synchronized (Reader.this) {
							Reader.this.notify();	// �������� ������, ����� �� ����� �����������
						}	
					}
				});
				
				new Thread(this).start();
			}
			
			public void run() {
				while ( !Thread.interrupted() ) {
					try {
						synchronized (this) {
							this.wait(); // ���� ������� �����, ����� ������ �����������
						}
						mLabel.setText("Trying to get Lock for reading..."); mLabel.repaint();
						mReentrantReadWriteLock.readLock().lock();	// ������� �������� ���������� mReentrantReadWriteLock �� ������
						
						mLabel.setText("I am the ReadLock owner!"); mLabel.repaint();
						Thread.sleep( mRandom.nextInt(THREAD_SLEEP_TIME_MS) + 1000 );
					} catch ( Exception e ) {
						mLabel.setText(e.getMessage()); mLabel.repaint();
					} finally {
						mReentrantReadWriteLock.readLock().unlock(); // ����������� ���������� mReentrantReadWriteLock �� ������
						mLabel.setText("..."); mLabel.repaint();
					}
				}
			}
		}
		
		private class Writer implements Runnable {
			JButton mWriteBtn = new JButton("writeLock()");
			JLabel  mLabel = new JLabel("...");
			
			public Writer ( JPanel grid_panel ) {
				grid_panel.add( new JPanel().add(mWriteBtn).getParent() );
				grid_panel.add( new JPanel().add(mLabel).getParent() );
				
				mWriteBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						synchronized (Writer.this) {
							Writer.this.notify();	// �������� ������, ����� �� ����� �����������
						}	
					}
				});
				
				new Thread(this).start();
			}
			
			public void run() {
				while ( !Thread.interrupted() ) {
					try {
						synchronized (this) {
							this.wait(); // ���� ������� �����, ����� ������ �����������
						}
						mLabel.setText("Trying to get Lock for writing..."); mLabel.repaint();
						mReentrantReadWriteLock.writeLock().lock();	// ������� �������� ���������� mReentrantReadWriteLock �� ������
						
						mLabel.setText("I am the WriteLock owner!"); mLabel.repaint();
						Thread.sleep( mRandom.nextInt(THREAD_SLEEP_TIME_MS) + 1000 );
					} catch ( Exception e ) {
						mLabel.setText(e.getMessage()); mLabel.repaint();
					} finally {
						mReentrantReadWriteLock.writeLock().unlock(); // ����������� ���������� mReentrantReadWriteLock �� ������
						mLabel.setText("..."); mLabel.repaint();
					}
				}
			}
		}
	}
	
	// **********************************************************************************************
	// �����, ��������������� ������ � ��������� � ��������� ������ java.util.concurrent.locks
	private class ConditionLockTesting {
		Lock mLock = new ReentrantLock();	// ������� ������� ���������� ���� ReentrantLock
		
		// ������� ������� �� �������� ���������� (��� ������ ������� �������� ����� ��������� ����� ��������)
		// ������ ���������� ����� ����� ��������� �������
		Condition mCondition = mLock.newCondition();	

		public ConditionLockTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel (new BorderLayout());
			border_panel.setBorder(BorderFactory.createTitledBorder("Condition testing"));
			parent_panel.add(border_panel);
			
			JPanel grid_panel = new JPanel ( new GridLayout(0, 1) );
			border_panel.add(grid_panel, BorderLayout.CENTER);
			
			JPanel left_panel = new JPanel ();
			left_panel.setLayout( new BoxLayout(left_panel, BoxLayout.Y_AXIS) );
			border_panel.add(left_panel, BorderLayout.WEST);
			
			JButton tmp_btn;
			
			(tmp_btn = new JButton("signal()")).addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try { 
								mLock.lock();		// ������ � ��������� �������� ������ ��� ��������� ��������������� ����������
													// (����� ��� � �� ����������� ������������ Java - ����������� ���������� API)
								// ������ ��������� ������ ������� (������ ������ ����� ���� �����, ������������ �� ���� ������):
								mCondition.signal();
							} finally {
								mLock.unlock();
							}
						}
					}
			);
			left_panel.add(new JPanel().add(tmp_btn).getParent());
			
			(tmp_btn = new JButton("signalAll()")).addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try { 
								mLock.lock();		
								// ������ ����������������� ������ ������� (������ ������ ��� ������, ��������� ��� � ������ ������):
								mCondition.signalAll(); 
							} finally {
								mLock.unlock();
							}
						}
					}
			);
			left_panel.add(new JPanel().add(tmp_btn).getParent());
			
			new SignalReceiver(grid_panel, "Thread #1");
			new SignalReceiver(grid_panel, "Thread #2");
			new SignalReceiver(grid_panel, "Thread #3");
			new SignalReceiver(grid_panel, "Thread #4");
		}
		
		private class SignalReceiver implements Runnable {
			JLabel mSignalLabel = new JLabel();

			public SignalReceiver (JPanel grid_panel, String title) {
				JPanel tmp_panel = new JPanel ();
				tmp_panel.setBorder(BorderFactory.createTitledBorder(title));
				tmp_panel.add(mSignalLabel);
				grid_panel.add(tmp_panel);
				new Thread(this).start();
			}
			
			@Override
			public void run() {
				while ( !Thread.interrupted() )
				{
					mSignalLabel.setText("Waiting for signal..."); mSignalLabel.repaint();
					try {
						try { 
							mLock.lock();		// ������ � ��������� �������� ������ ��� ��������� ��������������� ����������
												// (����� ��� � �� ����������� ������������ Java - ����������� ���������� API)
							mCondition.await();	// ���� ������� �����
						} finally {
							mLock.unlock();
						}
						mSignalLabel.setText("Signal received!"); mSignalLabel.repaint();
						Thread.sleep(3500);
					} catch ( InterruptedException e ) {}
				}
			}
		}
	}

}
