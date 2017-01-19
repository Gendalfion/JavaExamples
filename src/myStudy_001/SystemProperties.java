package myStudy_001;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SystemProperties {

	public static void main(String[] args) {
		JFrame myFrame = new JFrame ( "Property test" );
		
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);	// Заставляет окно появиться в центре экрана
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		StringBuilder str_bld = new StringBuilder();
		Formatter fmt = new Formatter(str_bld, null);
		int prop_cnt = System.getProperties().size();
		str_bld.append( "Total properties: " + String.valueOf(prop_cnt) + "\r\n" );
		
		int i = 0;
		Enumeration<Object> keys = System.getProperties().keys(), vals = System.getProperties().elements();
		
		while (keys.hasMoreElements() && vals.hasMoreElements())
		{
			fmt.format("%03d: %25s = \"%s\"\r\n", ++i, keys.nextElement().toString(), vals.nextElement().toString());
		}
		fmt.close();
		
		JComboBox<Object> cbKeys = new JComboBox<>( System.getProperties().stringPropertyNames().toArray() );
		cbKeys.setEditable(true);
		
		JLabel lblKeyVal = new JLabel("Select key to show its value...");
		JButton btnToXml = new JButton("XML");
		btnToXml.setToolTipText("Сохранить текущие параметры в XML-файл...");
				
		JPanel panel = new JPanel (new BorderLayout());
		panel.add(new JScrollPane( new JTextArea(str_bld.toString()) ), BorderLayout.CENTER);
		panel.add(cbKeys, BorderLayout.NORTH);
		panel.add(lblKeyVal, BorderLayout.SOUTH);
		panel.add(btnToXml, BorderLayout.WEST);
		
		btnToXml.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.getProperties().storeToXML(new FileOutputStream("save.xml"), "Мой коментарий...");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		cbKeys.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if ( e.getStateChange() == ItemEvent.SELECTED )
				{
					//System.out.println(e.getItem());
					String selected_item = cbKeys.getSelectedItem().toString();
					//JOptionPane.showMessageDialog(myFrame, selected_item + " = " 
					//		+ System.getProperties().getProperty(selected_item) );
					lblKeyVal.setText( selected_item + " = \"" 
									+  System.getProperties().getProperty(selected_item) + "\"" );
				}
			}
		});
		
		myFrame.add(panel);
		
		myFrame.setVisible(true);
	}

}
