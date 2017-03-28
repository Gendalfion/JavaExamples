package java_api_testing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import my_util.FileUtil;

/**
 * Демонстрация возможностей JavaAPI по сериализации/десериализации пользовательских объектов
 * @author Lab119Alex
 *
 */
public class SerializationTesting {
	
	private static final int sFieldWidth = 25;
	
	public SerializationTesting () {
		JFrame myFrame = new JFrame ( "Serialization/Deserialization Testing" );
		myFrame.setSize(500, 300);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		center_panel.setBorder( BorderFactory.createTitledBorder("Serializable object fields:") );
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel tmp_panel = new JPanel();
		tmp_panel.add(new JLabel("transient String: "));
		JTextField transientStringField = new JTextField(sFieldWidth);
		tmp_panel.add(transientStringField);
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel();
		tmp_panel.add(new JLabel("serializable String: "));
		JTextField serializableStringField = new JTextField(sFieldWidth);
		tmp_panel.add(serializableStringField);
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel();
		tmp_panel.add(new JLabel("low level int: "));
		JTextField lowLevelIntField = new JTextField(sFieldWidth);
		tmp_panel.add(lowLevelIntField);
		center_panel.add(tmp_panel);
		
		JPanel bottom_panel = new JPanel ( new GridBagLayout() );
		main_panel.add(bottom_panel, BorderLayout.NORTH);
		
		JButton writeObjectBtn = new JButton("writeObject()...");
		bottom_panel.add( new JPanel().add(writeObjectBtn).getParent() );
		
		JButton readObjectBtn = new JButton("readObject()...");
		bottom_panel.add( new JPanel().add(readObjectBtn).getParent() );
		
		setSerializableObject(new SerializableObject(), transientStringField, serializableStringField, lowLevelIntField);
		
		JFileChooser serializationFileChooser = new JFileChooser();
		serializationFileChooser.setFileFilter( new FileNameExtensionFilter("Serialization files *.ser", "ser") );
		serializationFileChooser.setAcceptAllFileFilterUsed(false);
		
		writeObjectBtn.addActionListener( new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( serializationFileChooser.showSaveDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					SerializableObject objectToWrite = getSerialzableObject(transientStringField, serializableStringField, lowLevelIntField);
					
					File selectedFile = FileUtil.getSelectedFileWithExtension(serializationFileChooser);
					// Для записи сериализируемых объектов в поток используется класс ObjectOutputStream:
					try ( ObjectOutputStream outputStream = new ObjectOutputStream( new FileOutputStream(selectedFile) ) ) {
						// Метод ObjectOutputStream.writeObject отправляет указанный объект в поток: 
						outputStream.writeObject(objectToWrite);
					} catch ( Exception exception ) {
						JOptionPane.showMessageDialog(main_panel, exception, "Write to stream error", JOptionPane.ERROR_MESSAGE);
						exception.printStackTrace();
					}
				}
			}
		} );
		
		readObjectBtn.addActionListener( new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( serializationFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					File selectedFile = serializationFileChooser.getSelectedFile();
					// Для чтения сериализируемых объектов из потока используется класс ObjectInputStream:
					try ( ObjectInputStream inputStream = new ObjectInputStream( new FileInputStream(selectedFile) ) ) {
						// Метод ObjectInputStream.readObject десериализирует объект из потока:
						SerializableObject readObject = (SerializableObject) inputStream.readObject();
						setSerializableObject (readObject, transientStringField, serializableStringField, lowLevelIntField);
					} catch ( Exception exception ) {
						JOptionPane.showMessageDialog(main_panel, exception, "Write to stream error", JOptionPane.ERROR_MESSAGE);
						exception.printStackTrace();
					}
				}
			}
		} );
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	private void setSerializableObject ( SerializableObject obj, 
			JTextComponent transientStrField, JTextComponent serialStrField, JTextComponent lowLevelIntField ) {
		transientStrField.setText	( obj.mTransientString );
		serialStrField.setText		( obj.mSerializableString );
		lowLevelIntField.setText	( String.valueOf(obj.mLowLevelInt) );
	}
	
	private SerializableObject getSerialzableObject ( JTextComponent transientStrField, JTextComponent serialStrField, JTextComponent lowLevelIntField ) {
		SerializableObject result = new SerializableObject();
		try {
			result.mTransientString		= transientStrField.getText();
			result.mSerializableString 	= serialStrField.getText();
			result.mLowLevelInt			= Integer.valueOf( lowLevelIntField.getText() );
		} catch ( Exception e ) {}
		return result;
	 }
	
	
	
	public static void main(String[] args) {
		new SerializationTesting();
	}
}
