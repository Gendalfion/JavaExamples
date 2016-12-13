package java_api_testing.java_thread_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


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
		new SemaphoreTesting (center_panel);
		new CyclicBarrierTesting (center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new TraditionalSynchronizationTesting();
	}
	
	// **********************************************************************************************
	// Класс, демонстрирующий работу класса синхронизации CountDownLatch
	private class CountDownLatchTesting {
		List<Runnable> mWorkersList = new Vector<>();
		List<Runnable> mWaitingList = new Vector<>();
		ExecutorService mCashedPool = Executors.newCachedThreadPool();
		
		// Данная утилита синхронизации позволяет потокам ожидать значения CountDownLatch пока оно не станет равным 0
		// (данное значение устанавливается при инициализации и может изменяться только в меньшую сторону за время жизни обекта) 
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
								// Ожидаем завершения работающих потоков для возврата контрольных элементов в исходное состояние:
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
					// Завершаем все потоки из пула:
					mCashedPool.shutdownNow();
					try {
						mCashedPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
					} finally {	// Создаем новый пул для возможного повторного запуска потоков:
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
					mCountDownLatch.countDown(); // Уменьшаем значение счетчика на 1
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
					
					mCountDownLatch.await(); // Ожидаем пока mCountDownLatch не станет равно 0 (счетчик уменьшается в потоках WorkingThread)
					
					mLabel.setText("All Work done!");
				} catch ( InterruptedException e ) {
					mLabel.setText("INTERRUPTED");
				}
			}
		}
	}
	
	// **********************************************************************************************
	// Класс, демонстрирующий работу класса семафоров в Java: Semaphore
	private class SemaphoreTesting {
		// Класс семафора в Java работает подобно блокировке Lock с 2-мя отличиями:
		// 1. Семафоры в Java являются счетными (т. е. представляют собой произвольное целое число)
		// 2. У семафора нет фактического владельца (т. е. нет разницы какой поток завладел семафором, а какой будет его отпускать)
		Semaphore mSemaphore = new Semaphore(0/* Начальное значение счетчика */, true /* "Честность" при блокировке потоков семафором */);
		
		int mSemaphoreCounter = 0;
		JLabel mSemaphoreCounterLabel = new JLabel("Semaphore = 0"); 
		
		private void IncSemaphoreCounter () {
			mSemaphoreCounterLabel.setText("Semaphore = " + Integer.toString(++mSemaphoreCounter));
		}
		
		private void DecSemaphoreCounter () {
			mSemaphoreCounterLabel.setText("Semaphore = " + Integer.toString(--mSemaphoreCounter));
		}
		
		public SemaphoreTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel ( new BorderLayout() );
			border_panel.setBorder(BorderFactory.createTitledBorder("Semaphore Testing"));
			parent_panel.add(border_panel);
			
			JPanel box_panel = new JPanel ();
			box_panel.setLayout(new BoxLayout(box_panel, BoxLayout.Y_AXIS));
			border_panel.add(box_panel, BorderLayout.CENTER);
			
			JPanel left_panel = new JPanel ();
			left_panel.setLayout( new BoxLayout(left_panel, BoxLayout.Y_AXIS) );
			border_panel.add(left_panel, BorderLayout.WEST);
			
			JButton release_btn = new JButton("Release Semaphore");
			left_panel.add( new JPanel ().add(release_btn).getParent() );
			left_panel.add( new JPanel ().add(mSemaphoreCounterLabel).getParent() );
			release_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mSemaphore.release(); // Отпускаем семафор в потоке обработки контрольных элементов GUI
					IncSemaphoreCounter ();
				}
			});
			
			new Thread ( new WaitingThread (box_panel, "Waiting Thread #1") ).start();
			new Thread ( new WaitingThread (box_panel, "Waiting Thread #2") ).start();
			new Thread ( new WaitingThread (box_panel, "Waiting Thread #3") ).start();
			new Thread ( new WaitingThread (box_panel, "Waiting Thread #4") ).start();
		}
		
		private class WaitingThread implements Runnable {
			JLabel mLabel = new JLabel();
			
			public WaitingThread ( JPanel box_panel, String thread_name ) {
				JPanel tmp_panel = new JPanel();
				tmp_panel.setBorder ( BorderFactory.createTitledBorder(thread_name) );
				tmp_panel.add(mLabel);
				box_panel.add ( tmp_panel );
			}
			
			@Override
			public void run() {
				while (!Thread.interrupted())
				{
					try {
						mLabel.setText("Waiting for Semaphore...");
						
						Thread.sleep( mRandom.nextInt(2000) + 500 );
						
						mSemaphore.acquire(); // Ожидаем пока другой поток не вызовет release()
						DecSemaphoreCounter ();
						
						mLabel.setText("Semaphore Acquired!");
						Thread.sleep( mRandom.nextInt(3000) + 2000 );
					} catch ( InterruptedException e ) {
						mLabel.setText("INTERRUPTED");
					}
				}
			}
		}
	}
	
	// **********************************************************************************************
	// Класс, демонстрирующий создание точек циклической синхронизации при помощи класса CyclicBarrier
	private class CyclicBarrierTesting {
		JTextArea mLeftArea = new JTextArea("https://translate.google.ru\nhttps://docs.oracle.com\n");
		JTextArea mRightArea = new JTextArea(0, 30);
		
		{
			mRightArea.setFont(new Font (Font.MONOSPACED, 0, 11));
		}
		
		ExecutorService mPool = null;
	
		public CyclicBarrierTesting ( JPanel parent_panel ) {
			JPanel horizont_box_panel = new JPanel ();
			horizont_box_panel.setLayout(new BoxLayout(horizont_box_panel, BoxLayout.X_AXIS));
			horizont_box_panel.setBorder(BorderFactory.createTitledBorder("Cyclic Barrier Testing"));
			parent_panel.add(horizont_box_panel);
			
			JPanel left_panel = new JPanel (new BorderLayout());
			horizont_box_panel.add(left_panel);
			left_panel.add(mLeftArea, BorderLayout.CENTER);
			horizont_box_panel.add(Box.createHorizontalStrut(5));
			JPanel right_panel = new JPanel(new BorderLayout());
			right_panel.add(mRightArea, BorderLayout.CENTER);
			horizont_box_panel.add(right_panel);
			mRightArea.setEditable(false);
			
			JButton update_btn = new JButton("Update URLs");
			left_panel.add(new JPanel().add(update_btn).getParent(), BorderLayout.SOUTH);
			
			startExample ();
			
			update_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					startExample ();
				}
			});
		}
		
		private class Result implements Comparable<Result> {
			Long time;
			String site;
			Result( Long time, String site ) { 
				this.time = new Long (time); 
				this.site = new String (site);
			}
			public int compareTo( Result r ) {
				if ( r != null ) 
					return site.compareTo( r.site );
				return -1; 
			}
			
			public String toString () {
				if ( time >= 0 ) {
					return String.format( "%-30.30s : %d", site, time );
				} else {
					return String.format( "%-30.30s : Invalid URL", site );
				}
			}
		}
		
		private long timeConnect ( String site )
		{
			long start = System.currentTimeMillis();
			try { 
				new URL( site ).openConnection().connect(); 
			} catch ( IOException e ) { 
				return -1;
			}
			return System.currentTimeMillis() - start;
		}
		
		private void printList ( List<?> list ) {
			String str = "";
			for ( Object elem : list ) {
				str += elem.toString() + "\n";
			}
			mRightArea.setText(str + "------------------");
			mRightArea.repaint();
		}
		
		public void startExample ()
		{
			if ( mPool != null ) {
				mPool.shutdownNow();
				mPool = null;
			}
			mPool = Executors.newCachedThreadPool();
		
			List<Result> results = new ArrayList<>();
			List<String> urlList = new ArrayList<>();
			String [] args = mLeftArea.getText().split("[\n\r]"); // Разбиваем строку по символам перевода строки \n или \r
			for ( String arg : args ) {
				if ( arg.length() > 1 ) {	// Добавляем в массив URL только не пустые пути:
					urlList.add(arg);
				}
			} 
			
			Runnable showResultsAction = new Runnable() { 
				public void run() { 
					Collections.sort( results );
					printList (results);
					results.clear();
				} 
			};
			
			// Класс CyclicBarrier позволяет ожидать пока заданное количество потоков не дойдут до единой точки выполнения
			CyclicBarrier barrier = new CyclicBarrier( 
					urlList.size(),		// Количество потоков, требующих синхронизации  
					showResultsAction );// Опционально задается действие, которое будет запущено при достижении точки синхронизации
					
			for ( final String site : urlList ) {
				mPool.execute( new Runnable () {
					public void run() {
						while( true ) {
							long time = timeConnect( site );
							results.add( new Result( time, site ) );
							try {
								barrier.await(); // Задаем точку синхронизации для данного циклического барьера
								// Как только заданное количество потоков достигнет данного барьера, поток продолжит выполнение,
								// а барьер вернется в исходное состояние и будет готов для повторного использования
							} catch ( BrokenBarrierException e ) { 
								return; 
							} catch ( InterruptedException e ) { 
								return; 
							}
						}
					}
				});
			}
		}
	}

}
