package java_api_testing.configuration_api;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

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

/**
 * ������������ ������ ������ java.util.prefs.Preferences
 * @author Lab119Alex
 *
 */
public class PreferencesTesting {
	// ����� Preferences ��������� ��������� ���� ����/�������� � ���������-����������� ���� ������ �� ���� ������� Windows
	// ������ ������������ � ���� ����������� ��������� ���� "root_node/child_node/.../endpoint_node"
	// ������ ���� ����� ��������� ������ ���� ��� ��������������� ���� ����/��������
	// ���������� ��� ���� �������� �����:
	//		userRoot() - ���������������� ��������� (�������������� ���� ����� �������� ��� ������� ������������ ��)
	//		systemRoot () - ��������� ��������� (������ ��������� ��� ���� ������������� �������)
	Preferences mMyPreferences = // � �������� ���� � ����������� ��������� ������ ���� � ������ ������:
			Preferences.userNodeForPackage(PreferencesTesting.class); // <=> Preferences.userRoot().node("java_api_testing/configuration_api")
	
	// ��� Windows 7 ��������� Preferences ����������� � �������: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
	// ��-��������� Windows 7 ��������� ��������� ������� ������ ������� ����
	// ��� ����, ����� ��������� ���, ���������� ������� ������ Prefs � ����: HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\
	
	public JFrame myFrame;
	
	public PreferencesTesting ( JFrame parent_frame ) {
		myFrame = new JFrame ( "java.util.prefs.Preferences Testing" );
		myFrame.setSize(800, 800);
		if ( parent_frame != null ) {
			myFrame.setLocation(new Point(parent_frame.getLocation().x + 100, parent_frame.getLocation().y + 100));
		} else {
			myFrame.setLocationRelativeTo(null);
		}
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		center_panel.setBorder(BorderFactory.createTitledBorder("Preferences list:"));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel tmp_panel;
		
		JTextField 	boolean_field = new JTextField(TEXT_FIELD_COLUMN);
		JButton		putBoolean_button = new JButton("putBoolean(\"boolean_val\", boolean)");
		(tmp_panel = new JPanel()).add(new JLabel("Boolean preference:  "));
		tmp_panel.add(boolean_field);
		tmp_panel.add(putBoolean_button);
		center_panel.add(tmp_panel);
		putBoolean_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ���������� boolean-�������� �� ���������� �����:
				mMyPreferences.putBoolean ("boolean_val", Boolean.valueOf(boolean_field.getText()));
				flushMyPreferences ();
			}
		});
		
		JTextField 	double_field = new JTextField(TEXT_FIELD_COLUMN);
		JButton		putDouble_button = new JButton("putDouble(\"double_val\", double)");
		(tmp_panel = new JPanel()).add(new JLabel("Double preference:  "));
		tmp_panel.add(double_field);
		tmp_panel.add(putDouble_button);
		center_panel.add(tmp_panel);
		putDouble_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ���������� double-�������� �� ���������� �����:
				mMyPreferences.putDouble ("double_val", Double.valueOf(double_field.getText()));
				flushMyPreferences ();
			}
		});
		
		JTextField 	long_field = new JTextField(TEXT_FIELD_COLUMN);
		JButton		putLong_button = new JButton("putLong(\"long_val\", long)");
		(tmp_panel = new JPanel()).add(new JLabel("Long preference:  "));
		tmp_panel.add(long_field);
		tmp_panel.add(putLong_button);
		center_panel.add(tmp_panel);
		putLong_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ���������� long-�������� �� ���������� �����:
				mMyPreferences.putLong ("long_val", Long.valueOf(long_field.getText()));
				flushMyPreferences ();
			}
		});
		
		JTextField 	string_field = new JTextField(TEXT_FIELD_COLUMN);
		JButton		putString_button = new JButton("put(\"string_val\", String)");
		(tmp_panel = new JPanel()).add(new JLabel("String preference:  "));
		tmp_panel.add(string_field);
		tmp_panel.add(putString_button);
		center_panel.add(tmp_panel);
		putString_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ���������� String-�������� �� ���������� �����:
				mMyPreferences.put ("string_val", string_field.getText());
				flushMyPreferences ();
			}
		});
		
		JTextField 	sub_node_string_field = new JTextField(TEXT_FIELD_COLUMN);
		JButton		put_sub_node_String_button = new JButton("put(\"sub_node/sub_node_key\", String)");
		(tmp_panel = new JPanel()).add(new JLabel("Subnode String preference:  "));
		tmp_panel.add(sub_node_string_field);
		tmp_panel.add(put_sub_node_String_button);
		center_panel.add(tmp_panel);
		put_sub_node_String_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ���������� String-�������� �� ���������� ����� � �������� ���� ��� mMyPreferences:
				mMyPreferences.node("sub_node").put ("sub_node_key", sub_node_string_field.getText());
				flushMyPreferences ();
			}
		});
		
		JFileChooser XML_file_chooser = new JFileChooser();
		XML_file_chooser.setFileFilter(new FileNameExtensionFilter("XML files *.xml", "xml"));
		
		JPanel bottom_panel = new JPanel();
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		JButton exportNode_btn		= new JButton("exportNode(OutputStream)");
		JButton importPrefs_btn		= new JButton("importPreferences(InputStream)");
		JButton exportSubtree_btn	= new JButton("exportSubtree(OutputStream)");
		
		bottom_panel.add( new JPanel().add(exportNode_btn).getParent() );
		bottom_panel.add( new JPanel().add(exportSubtree_btn).getParent() );
		bottom_panel.add( new JPanel().add(importPrefs_btn).getParent() );
		
		exportNode_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( XML_file_chooser.showSaveDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(XML_file_chooser.getSelectedFile()) ) {
						// ��������� ����� mMyPreferences � ���� � �������� XML:
						mMyPreferences.exportNode(fileOutputStream); // ����� �� �������� ����� ��� mMyPreferences �� �������������� � ������ ������!
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					} 
				}
			}
		});
		
		exportSubtree_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( XML_file_chooser.showSaveDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(XML_file_chooser.getSelectedFile()) ) {
						// ��������� ����� mMyPreferences � ���� � �������� XML:
						mMyPreferences.exportSubtree(fileOutputStream); // ����� �� �������� ����� ��� mMyPreferences �������������� � ������ ������!
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					}
				}
			}
		});
		
		importPrefs_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( XML_file_chooser.showOpenDialog(myFrame)  == JFileChooser.APPROVE_OPTION ) {
					try (FileInputStream fileInputStream = new FileInputStream(XML_file_chooser.getSelectedFile()) ) {
						// ��������� ������ �������� Preferences �� ����� � �������� XML:
						Preferences.importPreferences(fileInputStream);
						updatePreferences (boolean_field, double_field, long_field, string_field, sub_node_string_field);
					} catch ( Exception ex ) {
						JOptionPane.showMessageDialog(myFrame, ex.getClass().getName() + " : " + ex.getMessage());
					}
				}
			}
		});
		
		// ��� ������ ���������� PreferenceChangeListener �������� ������������ ��������� ��� ��������� ����:
		PreferenceChangeListener my_preference_change_listener = new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				// �� �������� ������� �������������� ��������� ������ ��� ���������� � Preferences, ��������� ������ ����� JVM
				updatePreferences (boolean_field, double_field, long_field, string_field, sub_node_string_field);
			}
		};
		mMyPreferences.addPreferenceChangeListener(my_preference_change_listener);
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				JOptionPane.showMessageDialog(myFrame, e.getClass().getName() + " : " + e.getMessage());
			}
		});
		
		updatePreferences (boolean_field, double_field, long_field, string_field, sub_node_string_field);
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public void updatePreferences ( JTextField boolean_field, JTextField double_field, JTextField long_field, JTextField string_field
								  , JTextField sub_node_string_field ) {
		// ��������� �������� ����� �� ������ � ���� mMyPreferences (������ ������� ���������� �������� ��-���������, ���� ���� �� ������):
		boolean_field.setText( Boolean.toString(mMyPreferences.getBoolean("boolean_val", false)) ); 
		double_field.setText( Double.toString(mMyPreferences.getDouble("double_val", 0.0)) ); 
		long_field.setText( Long.toString(mMyPreferences.getLong("long_val", 0)) );
		string_field.setText( mMyPreferences.get("string_val", "default") );
		sub_node_string_field.setText(mMyPreferences.node("sub_node").get("sub_node_key", "default"));
	}
	
	public void flushMyPreferences () {
		try {
			// ����� flush() �������� ������������ ������ ���� ��������� mMyPreferences � ��������� �������� Java:
			mMyPreferences.flush();
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getName() + " : " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new PreferencesTesting ( new PreferencesTesting (null).myFrame );
	}

	private static final int TEXT_FIELD_COLUMN = 25; 
}
