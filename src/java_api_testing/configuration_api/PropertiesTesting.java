package java_api_testing.configuration_api;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

/**
 * Демострация работы класса java.util.Properties
 * @author Lab119Alex
 *
 */
public class PropertiesTesting {
	// Класс java.util.Properties является хэш-таблицей, содержащей строковые пары ключ-значение:
	Properties 	mMyProperties = new Properties();
	
	{
		// Заполняем объект mMyProperties значениями по-умолчанию:
		mMyProperties.setProperty("PropertiesTesting.MyProperties.my_value_1", "default_value_1");
		mMyProperties.setProperty("PropertiesTesting.MyProperties.my_value_2", "default_value_2");
	}
	
	public PropertiesTesting () {
		JFrame myFrame = new JFrame ( "java.util.Properties Testing" );
		myFrame.setSize(900, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		center_panel.setBorder(BorderFactory.createTitledBorder("Properties list:"));
		
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton 	add_property_button = new JButton( "add property" );
		JTextField	property_name_field = new JTextField("PropertiesTesting.MyProperties.example", 30);
		
		top_panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		top_panel.add(new JPanel().add(new JLabel("Input property name:")).getParent());
		top_panel.add(new JPanel().add(property_name_field).getParent());
		top_panel.add(new JPanel().add(add_property_button).getParent());
		
		JPanel bottom_panel = new JPanel(new BorderLayout());
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		
		JPanel bottom_up_panel = new JPanel();
		bottom_panel.add(bottom_up_panel, BorderLayout.NORTH);
		
		JTextField comment_field = new JTextField( "MyProperties storing comment", 35 );
		bottom_up_panel.add(comment_field);
		
		JButton store_to_OutStream 	= new JButton("store(OutputStream, comment)");
		JButton load_from_InStream 	= new JButton("load(InputStream)");
		JButton store_to_XML		= new JButton("storeToXML(OutputStream, comment)");
		JButton load_from_XML		= new JButton("loadFromXML(InputStream)");
		
		JPanel bottom_down_panel = new JPanel();
		bottom_panel.add( bottom_down_panel, BorderLayout.SOUTH);
		
		bottom_down_panel.add(new JPanel().add(store_to_OutStream).getParent());
		bottom_down_panel.add(new JPanel().add(load_from_InStream).getParent());
		bottom_down_panel.add(new JPanel().add(store_to_XML).getParent());
		bottom_down_panel.add(new JPanel().add(load_from_XML).getParent());
		
		JFileChooser text_file_chooser = new JFileChooser();
		text_file_chooser.setFileFilter(new FileNameExtensionFilter("Text files *.txt", "txt"));
		
		JFileChooser XML_file_chooser = new JFileChooser();
		XML_file_chooser.setFileFilter(new FileNameExtensionFilter("XML files *.xml", "xml"));
		
		add_property_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Добавление новых свойств осуществляется через метод setProperty (key, val):
				mMyProperties.setProperty(property_name_field.getText(), "default");
				updatePropertyList (center_panel);
			}
		});
		
		store_to_OutStream.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( text_file_chooser.showSaveDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(text_file_chooser.getSelectedFile()) ) {
						// Сохраняем список свойств в файле с форматом, похожим на Windows .ini:
						mMyProperties.store(fileOutputStream, comment_field.getText());
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					} 
				}
			}
		});
		
		load_from_InStream.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( text_file_chooser.showOpenDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileInputStream fileInputStream = new FileInputStream(text_file_chooser.getSelectedFile()) ) {
						// Загружаем список свойств из файла, сохраненного в формате похожем на Windows .ini:
						mMyProperties.load(fileInputStream);
						updatePropertyList (center_panel);
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					}
				}
			}
		});
		
		store_to_XML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( XML_file_chooser.showSaveDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(XML_file_chooser.getSelectedFile()) ) {
						// Сохраняем список свойств в файле с форматом XML:
						mMyProperties.storeToXML(fileOutputStream, comment_field.getText());
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					} 
				}
			}
		});
		
		load_from_XML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( XML_file_chooser.showOpenDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileInputStream fileInputStream = new FileInputStream(XML_file_chooser.getSelectedFile()) ) {
						// Загружаем список свойств из файла в формате XML:
						mMyProperties.loadFromXML(fileInputStream);
						updatePropertyList (center_panel);
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					}
				}
			}
		});
		
		updatePropertyList (center_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public void updatePropertyList ( JPanel parent_panel ) {
		parent_panel.removeAll();
		
		// Добавляем все пары ключ/значение из mMyProperties на главную панель приложения:
		for (Map.Entry<Object, Object> elem : mMyProperties.entrySet()) {
			String key = (String)(elem.getKey());
			String value = (String)(elem.getValue());
			
			JPanel one_property_panel = new JPanel();
			JLabel 		key_label = new JLabel(key);
			JTextField	property_field = new JTextField(value, 25);
			JButton 	set_property_button = new JButton( "setProperty(key, val)" );
			
			one_property_panel.add(new JPanel().add(key_label).getParent());
			one_property_panel.add(new JPanel().add(property_field).getParent());
			one_property_panel.add(new JPanel().add(set_property_button).getParent());
			
			set_property_button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// По нажатию кнопки устанавливаем значение из property_field по ключю из key_label:
					mMyProperties.setProperty(key_label.getText(), property_field.getText());
				}
			});
			
			parent_panel.add(one_property_panel);
		}
		
		parent_panel.revalidate();
	}

	public static void main(String[] args) {
		new PropertiesTesting ();
	}
}
