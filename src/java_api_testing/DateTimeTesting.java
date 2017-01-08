package java_api_testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import my_util.JCheckBoxList;

/**
 * ƒемонстраци€ работы с датой и временем в Java (пакет java.util.*)
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
		// ѕолучаем массив доступных временных зон через статический метод TimeZone.getAvailableIDs():
		for ( String zone_ID : TimeZone.getAvailableIDs() ) {
			// ƒобавл€ем названи€ временных зон в список mTimeZoneList через модель mTimeZoneListModel:
			JCheckBox new_elem = new JCheckBox(zone_ID);
			if ( zone_ID.compareTo("Asia/Irkutsk") == 0 ) {
				new_elem.setSelected(true);
			}
			new_elem.addItemListener(this);
			mTimeZoneListModel.addElement(new_elem);
			list_height += new_elem.getPreferredSize().height - 5;
		}	
		// ”станавливаем предпочтительную высоту списка дл€ корректной работы полосы прокрутки:
		mTimeZoneList.setPreferredSize(new Dimension(mTimeZoneList.getPreferredSize().width, list_height));
	}
	
	public DateTimeTesting () {
		JFrame myFrame = new JFrame ( "Date-Time Testing" );
		myFrame.setSize(1000, 700);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		JPanel left_panel = new JPanel (new BorderLayout());
		main_panel.add(left_panel, BorderLayout.WEST);
		
		JPanel center_panel = new JPanel( new BorderLayout() );
		center_panel.setBorder(BorderFactory.createTitledBorder("Current time in different time zones:"));
		center_panel.add(mOutArea, BorderLayout.CENTER);
		mOutArea.setEditable(false);
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		left_panel.setBorder(BorderFactory.createTitledBorder("Select Time Zones:"));
		JScrollPane scrollPane = new JScrollPane(mTimeZoneList);
		left_panel.add(scrollPane, BorderLayout.CENTER);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO: ƒобавить код по обработке нажати€ на Check Box
		
		//
		//
		mOutArea.append( ((JCheckBox)(arg0.getSource())).getText() + "\n" );
		//
		//
	}
	
	public static void main(String[] args) {
		new DateTimeTesting ();
	}	
}
