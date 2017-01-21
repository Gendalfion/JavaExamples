package java_api_testing.logger_api;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Демонстрация работы класса java.util.logging.Logger
 * @author Lab119Alex
 *
 */
public class LoggerTesting {
	// Объекты Logger - это регистраторы сообщений, они позволяют журналировать поступающие сообщения с разными уровнями значимости
	// Имена для классов Logger принято организовывать в виде иерархической структуры подобно java-пакетам (это позволяет иерархически конфигурировать логи)
	Logger mMyLogger = Logger.getLogger("java_api_testing.logger_api.my_logger");
	
	// Настройки журнала по-умолчанию хранятся в файле <java_install_path>/jre/lib/logging.properties
	// Задать собственный файл настроек можно при помощи системной опции: -Djava.util.logging.config.file=<myfile.properties>
	// Пример для LoggerTesting: -Djava.util.logging.config.file=".\src\java_api_testing\logger_api\my_logger.properties"
	
	public LoggerTesting () {
		JFrame myFrame = new JFrame ( "java.util.logging.Logger Testing" );
		myFrame.setSize(930, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.Y_AXIS));
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JPanel tmp_panel = new JPanel();
		JTextField log_message_field = new JTextField( "some log message...", 25 );
		tmp_panel.add(new JLabel("Input log message:   "));
		tmp_panel.add(log_message_field);
		top_panel.add(tmp_panel);
		
		//--------------------------------------------------------------
		// Создаем кнопки для добавления сообщений в лог с разными уровнями информирования:
		//--------------------------------------------------------------
		tmp_panel = new JPanel();
		tmp_panel.setLayout(new BoxLayout(tmp_panel, BoxLayout.X_AXIS));
		JButton severe_msg_btn = new JButton("severe(message)");
		tmp_panel.add(severe_msg_btn);
		severe_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.SEVERE) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.severe(log_message_field.getText());
				}
			}
		});
		
		JButton warning_msg_btn = new JButton("warning(message)");
		tmp_panel.add(warning_msg_btn);
		warning_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.WARNING) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.warning(log_message_field.getText());
				}
			}
		});
		
		JButton info_msg_btn = new JButton("info(message)");
		tmp_panel.add(info_msg_btn);
		info_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.INFO) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.info(log_message_field.getText());
				}
			}
		});
		
		JButton config_msg_btn = new JButton("config(message)");
		tmp_panel.add(config_msg_btn);
		config_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.CONFIG) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.config(log_message_field.getText());
				}
			}
		});
		
		JButton fine_msg_btn = new JButton("fine(message)");
		tmp_panel.add(fine_msg_btn);
		fine_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.FINE) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.fine(log_message_field.getText());
				}
			}
		});
		
		JButton finer_msg_btn = new JButton("finer(message)");
		tmp_panel.add(finer_msg_btn);
		finer_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.FINER) ) {// Применяем рекомендуемую оптимизацию
					mMyLogger.finer(log_message_field.getText());
				}
			}
		});
		
		JButton finest_msg_btn = new JButton("finest(message)");
		tmp_panel.add(finest_msg_btn);
		finest_msg_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( mMyLogger.isLoggable(Level.FINEST) ) {	// Применяем рекомендуемую оптимизацию
					mMyLogger.finest(log_message_field.getText());
				}
			}
		});
		top_panel.add(tmp_panel);
		//--------------------------------------------------------------
		
		JTextArea log_area = new JTextArea();
		log_area.setEditable(false);
		log_area.setBorder(BorderFactory.createTitledBorder("Log messages:"));
		main_panel.add(log_area, BorderLayout.CENTER);
		
		// Мы можем определять свой обработчик лог-сообщений:
		Handler my_handler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				if ( this.isLoggable(record) ) {
					log_area.append( new Date(record.getMillis()).toString() + "  " + record.getLevel() + ": " + record.getMessage() + "\n");
				}
			}
			@Override
			public void flush() {
			}
			@Override
			public void close() throws SecurityException {
			}
		};
		mMyLogger.addHandler(my_handler);
		
		JPanel bottom_panel = new JPanel();
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		
		JComboBox<Level> choose_level_box = new JComboBox<> (
				new Level[] { Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINER, Level.FINEST } );
		bottom_panel.add(new JLabel("Choose log level for log handler:  "));
		bottom_panel.add(choose_level_box);
		my_handler.setLevel(Level.CONFIG);
		choose_level_box.setSelectedItem(my_handler.getLevel());
		
		choose_level_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Настраиваем лог-level для нашего обработчика:
				my_handler.setLevel((Level)choose_level_box.getSelectedItem());
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new LoggerTesting();
	}

}
