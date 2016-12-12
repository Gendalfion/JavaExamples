package java_api_testing.java_thread_api;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class TraditionalSynchronizationTesting {
	
	Random mRandom = new Random( System.currentTimeMillis() );
	
	public TraditionalSynchronizationTesting () {
		JFrame myFrame = new JFrame ( "Traditional Synchronization Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		new CountDownLatchTesting (center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new TraditionalSynchronizationTesting();
	}
	
	// **********************************************************************************************
	//  ласс, демонстрирующий работу класса синхронизации CountDownLatch
	private class CountDownLatchTesting {
		List<Runnable> mWorkersList = new Vector<>();
		List<Runnable> mWaitingList = new Vector<>();
		ExecutorService mCashedPool = Executors.newCachedThreadPool();
		
		// ƒанна€ утилита синхронизации позвол€ет потокам ожидать значени€ CountDownLatch пока оно не станет равным 0
		// (данное значение устанавливаетс€ при инициализации и может измен€тьс€ только в меньшую сторону за врем€ жизни обекта) 
		CountDownLatch mCountDownLatch = null;
		
		public CountDownLatchTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel ( new BorderLayout() );
			border_panel.setBorder(BorderFactory.createTitledBorder("Count Down Latch Testing"));
			parent_panel.add(border_panel);
			
			JPanel box_panel = new JPanel ();
			box_panel.setLayout(new BoxLayout(box_panel, BoxLayout.Y_AXIS));
			border_panel.add(box_panel, BorderLayout.CENTER);
			
			JPanel left_panel = new JPanel ();
			left_panel.setLayout( new BoxLayout(left_panel, BoxLayout.Y_AXIS) );
			border_panel.add(left_panel, BorderLayout.WEST);
			
			JButton start_btn = new JButton("Start threads");
			left_panel.add(new JPanel().add(start_btn).getParent());
			
			JButton stop_btn = new JButton("Stop Threads Now!");
			stop_btn.setEnabled(false);
			left_panel.add(new JPanel().add(stop_btn).getParent());
			
			mWorkersList.add( new WorkingThread (box_panel, "First Working Thread") );
			mWorkersList.add( new WorkingThread (box_panel, "Second Working Thread") );
			mWorkersList.add( new WorkingThread (box_panel, "Third Working Thread") );
			
			mWaitingList.add( new WaitingThread (box_panel, "First Waiting Thread") );
			mWaitingList.add( new WaitingThread (box_panel, "Second Waiting Thread") );
			
			start_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					start_btn.setEnabled(false);
					stop_btn.setEnabled(true);
					
					mCountDownLatch = new CountDownLatch (mWorkersList.size());
							
					for (Runnable runnable : mWorkersList) {
						mCashedPool.execute( runnable );
					}
					
					for (Runnable runnable : mWaitingList) {
						mCashedPool.execute( runnable );
					}
					
					new Thread ( new Runnable() {
						@Override
						public void run() {
							try {
								// ќжидаем завершени€ работающих потоков дл€ возврата контрольных элементов в исходное состо€ние:
								mCountDownLatch.await();
							} catch (InterruptedException e) {
							} finally {
								start_btn.setEnabled(true);
								stop_btn.setEnabled(false);
							}
						}
					} ).start();
				}
			});
			
			stop_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// «авершаем все потоки из пула:
					mCashedPool.shutdownNow();
					try {
						mCashedPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
					} finally {	// —оздаем новый пул дл€ возможного повторного запуска потоков:
						mCashedPool = Executors.newCachedThreadPool();
					}
				}
			});
		}
		
		private class WorkingThread implements Runnable {
			JLabel mLabel = new JLabel("IDLE");
			
			public WorkingThread ( JPanel box_panel, String thread_name ) {
				JPanel tmp_panel = new JPanel();
				tmp_panel.setBorder ( BorderFactory.createTitledBorder(thread_name) );
				tmp_panel.add(mLabel);
				box_panel.add ( tmp_panel );
			}
			
			protected void setProgress ( double progress ) {
				if ( progress > 100.0 ) { progress = 100.0; } 
				
				mLabel.setText( "Working progress: " + Integer.toString((int)progress) );
				mLabel.repaint();
			}

			@Override
			public void run() {
				double run_progress = 0.0;
				int run_speed = mRandom.nextInt(350) + 50;
				setProgress(run_progress);
				
				try {
					while ( run_progress < 100.0 ) {
						Thread.sleep(40);
						run_progress += (double)run_speed / 100.0;
						setProgress(run_progress);
					}
					mLabel.setText("Work complete!");
				} catch ( InterruptedException e ) {
					mLabel.setText("INTERRUPTED");
				} finally {
					mCountDownLatch.countDown(); // ”меньшаем значение счетчика на 1
				}
			}
		}
		
		private class WaitingThread implements Runnable {
			JLabel mLabel = new JLabel("IDLE");
			
			public WaitingThread ( JPanel box_panel, String thread_name ) {
				JPanel tmp_panel = new JPanel();
				tmp_panel.setBorder ( BorderFactory.createTitledBorder(thread_name) );
				tmp_panel.add(mLabel);
				box_panel.add ( tmp_panel );
			}
			
			@Override
			public void run() {
				try {
					mLabel.setText("Waiting for Working threads...");
					
					mCountDownLatch.await(); // ќжидаем пока mCountDownLatch не станет равно 0 (счетчик уменьшаетс€ в потоках WorkingThread)
					
					mLabel.setText("All Work done!");
				} catch ( InterruptedException e ) {
					mLabel.setText("INTERRUPTED");
				}
			}
		}
	}

}
