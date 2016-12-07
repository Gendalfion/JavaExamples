package java_api_testing.java_thread_api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ExecutorsTesting {
	
	public ExecutorsTesting () {
		JFrame myFrame = new JFrame ( "Executors Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel bottom_panel = new JPanel(new FlowLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		JPanel center_panel = new JPanel(new GridLayout(0, 1));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		new FixedThreadPoolTesting 		(center_panel);
		new CachedThreadPoolTesting		(center_panel);
		new ScheduledThreadPoolTesting	(center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ExecutorsTesting();
	}
	
	//------------------------------------------------------------------------------------------------------
	// �����, ��������������� ������ �������� ���� ��� ����� FixedThreadPool
	private class FixedThreadPoolTesting {
		ExecutorService mExecutor = Executors.newFixedThreadPool(3); // ������� ��� �� 3 ������
				// ������ ��� ��������� ���������� ��� ������� ���� Runnable ��� Callable �������� �� 3 ����� ������������
		int mThreadsTotal = 0;
		final String THREAD_NAME_PREFIX = "Thread #";
		
		FixedThreadPoolTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel(new BorderLayout());
			parent_panel.add(border_panel);
			
			JPanel left_border = new JPanel ();
			border_panel.add(left_border, BorderLayout.WEST);
			final JButton start_thread_btn = new JButton ("Start " + THREAD_NAME_PREFIX + "1");
			left_border.add(start_thread_btn);
			
			JPanel right_border = new JPanel();
			border_panel.add(right_border, BorderLayout.CENTER);
			final JTextArea thread_states_tout = new JTextArea(); thread_states_tout.setEditable(false);
			border_panel.add(new JScrollPane(thread_states_tout), BorderLayout.CENTER);
			
			JPopupMenu clear_menu = new JPopupMenu();
			clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					thread_states_tout.setText("");
				}
			});
			thread_states_tout.setComponentPopupMenu(clear_menu);
			
			
			start_thread_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// �� ������� ������ ��������� ����� ����� � ���:
					mExecutor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								thread_states_tout.append( "+++ Thread Started: " + Thread.currentThread().getName() + "\n\r");
								Thread.sleep (5000);
								thread_states_tout.append( "--- Thread Ended: " + Thread.currentThread().getName() + "\n\r");
							} catch (InterruptedException e) { }
						}
					});
					
					start_thread_btn.setText("Start " + THREAD_NAME_PREFIX + Integer.toString(++mThreadsTotal + 1));
				}
			});
		} // FixedThreadPoolTesting ( JPanel parent_panel )
	} // private class FixedThreadPoolTesting
	
	
	//------------------------------------------------------------------------------------------------------
	// �����, ��������������� ������ ������������� ���� ��� ����� CachedThreadPool
	private class CachedThreadPoolTesting {
		ExecutorService mExecutor = Executors.newCachedThreadPool(); // ������� ������������ ���
				// ������ ��� ��������� ���������� ��� ������� ���� Runnable ��� Callable ��� ����������� �� ����������
				// ���������� �����; ���� ����������� ������ ���������� �� ��������� ����� � ������ ��� ���������� ������������� � ����
		
		JTextArea mCalcResultsArea = new JTextArea();
		JTextField mCalcParamField = new JTextField("2", 10);
		
		CachedThreadPoolTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel(new BorderLayout());
			parent_panel.add(border_panel);
			
			JPanel left_border = new JPanel ();
			left_border.setLayout(new BoxLayout (left_border, BoxLayout.Y_AXIS));
			border_panel.add(left_border, BorderLayout.WEST);
			
			final JButton start_thread_btn = new JButton ("Start Calculation");
			left_border.add(start_thread_btn);
			
			JPanel tmp_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp_panel.add(mCalcParamField);
			left_border.add(tmp_panel);
			
			JPanel right_border = new JPanel();
			border_panel.add(right_border, BorderLayout.CENTER);
			mCalcResultsArea.setEditable(false);
			border_panel.add(new JScrollPane(mCalcResultsArea), BorderLayout.CENTER);
			
			JPopupMenu clear_menu = new JPopupMenu();
			clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mCalcResultsArea.setText("");
				}
			});
			mCalcResultsArea.setComponentPopupMenu(clear_menu);
			
			start_thread_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// �� ������� ������ ������� ������ � �������� � � ���:
					Integer calc_param = 0;
					try { calc_param = Integer.valueOf(mCalcParamField.getText()); } catch (Exception e) { }
					// ������� ������, ������������ ��������� � ���� ������� ���� Integer
					final Future<Integer> calc_future_result = mExecutor.submit(new CalculationThread(calc_param));
					
					final String param_equation = calc_param + " + " + calc_param;
					// ��������� �������� ���������� ������ � ��������� ������:
					mExecutor.execute(new Thread () {
						@Override
						public void run() {
							try {
								mCalcResultsArea.append("Waiting for equation: " + param_equation + "\n\r");
								Integer int_result = calc_future_result.get(); // ����� Future<>.get ��������� ����� �� ������� ���� ������ �� ������ ��������
								mCalcResultsArea.append("Result (" + param_equation + ") = " + int_result + "\n\r");
							} catch (Exception ex) {}
						}
					});
				}
			});
		}
		
		// ������� ������� �����, ��������������� ��������� Callable<> ��� ���������� ������, ������������ ��������
		class CalculationThread implements Callable<Integer> {
			Integer mParam;
			
			public CalculationThread(Integer param) {
				mParam = param;
			}
			
			@Override
			public Integer call() throws Exception {
				mCalcResultsArea.append("* Start Calculating: " + mParam + " + " + mParam + "\n\r");	mCalcResultsArea.repaint();
				try { Thread.sleep(5000); } catch (InterruptedException e) { }
				mCalcResultsArea.append("* Calculation done: " + mParam + " + " + mParam + "\n\r");	mCalcResultsArea.repaint();
				return mParam + mParam;
			}
		}
	}
	
	//------------------------------------------------------------------------------------------------------
	// �����, ��������������� ������ ������������ ����� ScheduledThreadPool
	private class ScheduledThreadPoolTesting {
		ScheduledExecutorService mScheduledExecutor 	= Executors.newSingleThreadScheduledExecutor();
									//		= Executors.newScheduledThreadPool(int);
		// ������� ��� ��������������� �����
		// ������ ��� ��������� ��������� ������, ���������� �� ������������ ������ �� �������,
		// ���� ��������� ������ � �������� ��������������

		JTextArea mTextArea = new JTextArea();
		JTextField mTaskDelayField = new JTextField(Integer.toString(10), 6);
		
		int mTaskPeriod = 15;
		JTextField mTaskPeriodField = new JTextField(Integer.toString(mTaskPeriod), 6);
		Future<?> mPeriodicTaskSchedule = null;
		
		ScheduledThreadPoolTesting ( JPanel parent_panel ) {
			JPanel border_panel = new JPanel(new BorderLayout());
			parent_panel.add(border_panel);
			
			JPanel left_border = new JPanel ();
			left_border.setLayout(new BoxLayout (left_border, BoxLayout.Y_AXIS));
			border_panel.add(left_border, BorderLayout.WEST);
			
			final JButton start_thread_btn = new JButton ("Start with delay");
			left_border.add(start_thread_btn);
			JPanel tmp_panel = new JPanel();
			tmp_panel.add(mTaskDelayField);
			tmp_panel.add(new JLabel("seconds"));
			left_border.add(tmp_panel);
			
			left_border.add(Box.createVerticalStrut(10));
			
			final JButton set_task_period_btn = new JButton ("Set task period");
			left_border.add(set_task_period_btn);
			tmp_panel = new JPanel();
			tmp_panel.add(mTaskPeriodField);
			tmp_panel.add(new JLabel("seconds"));
			left_border.add(tmp_panel);
			
			JPanel right_border = new JPanel();
			border_panel.add(right_border, BorderLayout.CENTER);
			mTextArea.setEditable(false);
			border_panel.add(new JScrollPane(mTextArea), BorderLayout.CENTER);
			
			JPopupMenu clear_menu = new JPopupMenu();
			clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mTextArea.setText("");
				}
			});
			mTextArea.setComponentPopupMenu(clear_menu);
			
			// ������ �������������� ���������� ������ � �������� ���������� ������� 
			// (�. �. ������������� �������� ����� ������� ���������� ������ ��� ����������� �� ������� ���������� ����� ������)
			mPeriodicTaskSchedule = mScheduledExecutor.scheduleAtFixedRate
						(		new PeriodicTask(),	// ������ ��� ���������� 
								mTaskPeriod, 		// �������� ����� ������ ����������� ������ 
								mTaskPeriod, 		// �������� ���������� ������
								TimeUnit.SECONDS);	// ������� ��������� ������� � ������������� ���������� ������
			// mScheduledExecutor.scheduleWithFixedDelay(...) - ������������� ���������� ������ � �������� �������� 
			// (�. �. ������������� �������� ����� ������ ���������� ������ � ������� ���������� ����������)
			
			
			start_thread_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int task_delay = 10;
					try {
						task_delay = Integer.valueOf(mTaskDelayField.getText());
						if ( task_delay < 1 ) {
							task_delay = 1;
						}
					} catch (Exception e) {
					}
					mTaskDelayField.setText(Integer.toString(task_delay));
					
					// ��������� ������, ���������� �� ������������ ���������� �������
					mScheduledExecutor.schedule( 
							new DelayedTask (task_delay), 	// ������ ��� ����������
							task_delay, 					// �������� ����� ������ ����������� ������
							TimeUnit.SECONDS );				// ������� ��������� ������� � ������������� ���������� ������
				}
			});
			
			
			set_task_period_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mPeriodicTaskSchedule.cancel(true);	// �������� ����������� ���������� ������������� ������
					
					try {
						mTaskPeriod = Integer.valueOf(mTaskPeriodField.getText());
						if ( mTaskPeriod < 1 ) {
							mTaskPeriod = 1;
						} 
					} catch (Exception e) {
						mTaskPeriod = 15;
					}
					mTaskPeriodField.setText(Integer.toString(mTaskPeriod));
					
					// ��������� ������������� ������ � ����� ��������� ����������
					mPeriodicTaskSchedule = mScheduledExecutor.scheduleAtFixedRate ( 
							new PeriodicTask(), mTaskPeriod, mTaskPeriod, TimeUnit.SECONDS );
				}
			});
		}
		
		private class DelayedTask implements Runnable {
			int mDelay;
			
			public DelayedTask (int delay)
			{
				mDelay = delay;
			}
			
			@Override
			public void run() {
				mTextArea.append("Delayed task: I\'ve started after " + Integer.toString(mDelay) + " seconds delay\n\r" );	
				try { 
					Thread.sleep(3000);
					mTextArea.append("Delayed task execution ended...\n\r");
				} catch (InterruptedException e) {
					mTextArea.append("Delayed task execution interrupted...\n\r");
				}
				mTextArea.repaint();
			}
			
		}
		
		private class PeriodicTask implements Runnable {
			@Override
			public void run() {
				mTextArea.append("Periodic task: I\'m working on interval of " + Integer.toString(mTaskPeriod) + " seconds\n\r" );	
				try { 
					Thread.sleep(3000);
					mTextArea.append("Periodic task execution ended...\n\r");
				} catch (InterruptedException e) {
					mTextArea.append("Periodic task execution interrupted...\n\r");
				}
				mTextArea.repaint();
			}
		}
	}
}
