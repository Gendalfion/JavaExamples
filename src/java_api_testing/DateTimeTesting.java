package java_api_testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import my_util.JCheckBoxList;
import my_util.StringUtil;

/**
 * Демонстрация работы с датой и временем в Java (пакет java.util.*)
 * @author Lab119Alex
 *
 */
public class DateTimeTesting implements ItemListener {
	
	JTextArea mOutArea = new JTextArea();
	DefaultListModel<JCheckBox> mTimeZoneListModel = new DefaultListModel<>();
	JCheckBoxList mTimeZoneList = new JCheckBoxList (mTimeZoneListModel);
	
	
	{
		mTimeZoneList.setPreferredSize(new Dimension(250, mTimeZoneList.getPreferredSize().height));
		
		int list_height = 0;
		// Получаем массив доступных временных зон через статический метод TimeZone.getAvailableIDs():
		for ( String zone_ID : TimeZone.getAvailableIDs() ) {
			// Добавляем названия временных зон в список mTimeZoneList через mTimeZoneListModel:
			JCheckBox new_elem = new JCheckBox(zone_ID);
			if ( zone_ID.compareTo("Asia/Irkutsk") == 0 ) {
				new_elem.setSelected(true);
			}
			new_elem.addItemListener(this);
			mTimeZoneListModel.addElement(new_elem);
			list_height += new_elem.getPreferredSize().height - 5;
		}	
		// Устанавливаем предпочтительную высоту списка для корректной работы полосы прокрутки:
		mTimeZoneList.setPreferredSize(new Dimension(mTimeZoneList.getPreferredSize().width, list_height));
	}
	
	public DateTimeTesting () {
		JFrame myFrame = new JFrame ( "Date-Time Testing" );
		myFrame.setSize(1170, 700);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel left_panel = new JPanel (new BorderLayout());
		main_panel.add(left_panel, BorderLayout.WEST);
		
		JPanel center_panel = new JPanel( new BorderLayout() );
		center_panel.setBorder(BorderFactory.createTitledBorder("Current time in different time zones:"));
		center_panel.add(mOutArea, BorderLayout.CENTER);
		mOutArea.setEditable(false);
		mOutArea.setFont(new Font (Font.MONOSPACED, 0, 12));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		left_panel.setBorder(BorderFactory.createTitledBorder("Select Time Zones:"));
		JScrollPane scrollPane = new JScrollPane(mTimeZoneList);
		left_panel.add(scrollPane, BorderLayout.CENTER);
		
		// Класс java.util.Timer используется как простой планировщик для выполнения задач TimerTask (имплементирует интерфейс Runnable):
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				update();
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000); // Запускаем немедленно задачу с частотой вызова в 1000 мс 
		
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	protected void printHeader ( Formatter printBuf ) {
		String tmp = String.format("%-25.25s| %-6.6s| %-10.10s| %-5.5s| %-5.5s| %-5.5s| %-5.5s|%40.40s |\n"
				, "TimeZoneID", "Year", "Month", "Day", "Hour", "Min.", "Sec.", "DateFormat.format(...)");
		printBuf.format("%s", tmp);
		printBuf.format( "%s\n", StringUtil.fillStr('-', tmp.length()) );
	}
	
	protected void printDateAndTime ( Formatter printBuf, Calendar calendar, DateFormat date_time_format ) {
		Locale locale = Locale.US;
		printBuf.format("%-25.25s| %-6.6s| %-10.10s| %-5.5s| %-5.5s| %-5.5s| %-5.5s|%40.40s |\n", calendar.getTimeZone().getID()
				// Методы set(int, int) и get(int) используются для доступа к полям календаря, таким как год, месяц, час и т. д. 
				, String.valueOf(calendar.get(Calendar.YEAR))
				, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
				, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
				, String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))
				, String.valueOf(calendar.get(Calendar.MINUTE))
				, String.valueOf(calendar.get(Calendar.SECOND))
				, date_time_format.format(date_time_format.getCalendar().getTime())
				);
	}
	
	synchronized public void update() {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter(sb);
		try {
			printHeader (fmt);
			
			// Объект java.util.Date инкапсулирует в себе абсолютное значение времени, которое
			// представляется в виде long-числа, - количества миллисекунд, прошедших от полуночи 1 января 1970 г. по Гринвичу (GMT+0)
			Date current_date = new Date(System.currentTimeMillis()); // Инициализируем объект Date текущим системным временем
			
			// Объект java.util.Calendar преобразует момент времени в заданную дату (день, месяц, год и т. д.) с учетом текущей локали и временной зоны
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(current_date);
			
			// Объект java.text.DateFormat используется для форматирования абсолютного времени в строку
			// с использованием соответствующей локали и временной зоны и также с указанием степени подробности вывода информации о дате
			DateFormat date_time_format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());
			date_time_format.setCalendar(calendar); // связываем DateFormat с Calendar (для автоматической загрузки информации о временной зоне)
			
			Enumeration<JCheckBox> all_check_boxes = mTimeZoneListModel.elements();
			while ( all_check_boxes.hasMoreElements() ) {
				JCheckBox checkBox = all_check_boxes.nextElement();
				if ( checkBox.isSelected() ) {
					// Объект java.util.TimeZone инкапсулирует в себе временной сдвиг для разных временных зон на планете Земля
					TimeZone time_zone = TimeZone.getTimeZone(checkBox.getText());
					calendar.setTimeZone(time_zone); // Т. к. calendar связан с date_time_format, то изменения в календаре отражаются и на формате вывода 
					
					printDateAndTime ( fmt, calendar, date_time_format );
				}
			}
		} catch ( Exception e ) {
			fmt.format("%-25.25s: %s\n", e.getClass().getName(), e.getMessage());
		} finally {
			mOutArea.setText(fmt.toString());
			fmt.close();
		} 
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		update();
	}
	
	public static void main(String[] args) {
		new DateTimeTesting ();
	}	
}
