package java_testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ReflectionTesting implements ActionListener {
	JTextArea 	mOutArea = new JTextArea();
	JTextField	mClassNameField = new JTextField ("java.lang.String", 25);
	//JTextField	mClassNameField = new JTextField ("java_testing.MyObject", 25);
	JTextField	mConstructorField = new JTextField ( "", 15);
	JTextField  mMethodParamField = new JTextField ( "", 15);
	JButton		mBtnGenerate = new JButton("Generate Class Info");
	JButton 	mBtnInvoke	= new JButton("Invoke Method");
	JComboBox<String> mMethodList = new JComboBox<String> ();
	
	Class<?>	mCurrentClass;
	Object 		mCurrentObject;
	
	{
		mBtnGenerate.addActionListener(this);
		mBtnInvoke.addActionListener(this);
		mMethodList.setEnabled(false);	
		mBtnInvoke.setEnabled(false);
		mMethodParamField.setEnabled(false);
		mClassNameField.setToolTipText("Input full class name and push Generate button");
		mMethodParamField.setToolTipText("Input String parameter for the method invocation");
		mConstructorField.setToolTipText("Input String parameter for the constructor");
	}
	
	public ReflectionTesting() {
		JFrame my_frame = new JFrame("Reflection Testing");
		my_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		my_frame.setSize(700, 800);
		my_frame.setLocationRelativeTo(null);
		
		JPanel main_panel = new JPanel(new BorderLayout());
		my_frame.add(main_panel);
		
		main_panel.add ( new JScrollPane(mOutArea), BorderLayout.CENTER ); 
		mOutArea.setEditable(false);
		
		JPanel top_panel = new JPanel(new FlowLayout());
		main_panel.add ( top_panel, BorderLayout.NORTH );
		top_panel.add ( mClassNameField );
		top_panel.add ( mBtnGenerate );
		top_panel.add ( mConstructorField );
		
		JPanel bottom_panel = new JPanel(new FlowLayout());
		main_panel.add ( bottom_panel, BorderLayout.SOUTH );
		bottom_panel.add(mMethodList);
		bottom_panel.add(mBtnInvoke);
		bottom_panel.add(mMethodParamField);
		
		my_frame.setVisible(true);
	} 

	public static void main(String[] args) {
		new ReflectionTesting();
	}
	
	String printFields (Class<?> someClass) {
		StringBuilder res = new StringBuilder();
		
		for (Field field : someClass.getFields()) {
			res.append("\t" + field.getName() + "\n");
		}
		return res.toString();
	}
	
	String printMethods (Class<?> someClass, JComboBox<String> cb) {
		StringBuilder res = new StringBuilder();
		cb.removeAllItems();
		
		// Печатаем все методы класса в строку:
		for (Method method : someClass.getMethods()) {
			res.append("\t\"" + method.getName() + "\"");
			// Выводим аннотации для метода:
			if (method.getDeclaredAnnotations().length > 0) { res.append(": "); }
			for (Annotation annotation : method.getDeclaredAnnotations()) {
				res.append(annotation.toString() + " ");
			}
			res.append("\n");
			cb.addItem(method.getName());
		}
		return res.toString();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == mBtnGenerate ) {
			mOutArea.setText("");
			boolean edit_enabled = false;
			try {
				mCurrentClass = Class.forName( mClassNameField.getText() );
				
				mOutArea.append("Printing information for class \"" + mCurrentClass.getName() + "\":\n");
				
				mOutArea.append("* Fields:\n" + printFields(mCurrentClass));
				mOutArea.append("* Methods:\n" + printMethods(mCurrentClass, mMethodList));
				
				if ( mConstructorField.getText().isEmpty() ) {
					// Вызываем базовый конструктор-умолчания для класса:
					mCurrentObject = mCurrentClass.newInstance();
				} else {
					// Вызываем конструктор со строковым параметром:
					mCurrentObject = mCurrentClass.getDeclaredConstructor(new Class[] {String.class}).newInstance(mConstructorField.getText());
				}
						
				edit_enabled = true;
			} catch (ClassNotFoundException ex) {
				mOutArea.append("\nClass \"" + mClassNameField.getText() + "\" not found!");
			} catch (Exception ex) {
				mOutArea.append( ex.getClass().getName() + " : " +  ex.getMessage() + "\n" );
			}
			
			mMethodList.setEnabled(edit_enabled);
			mBtnInvoke.setEnabled(edit_enabled);
			mMethodParamField.setEnabled(edit_enabled);
		} else
		if ( e.getSource() == mBtnInvoke ) {
			try {
				String method_name = (String)mMethodList.getSelectedItem();
				
				Method method = ( mMethodParamField.getText().isEmpty() )
						// Если текстовое поле пустое, ищем метод без параметров:
						? (mCurrentClass.getDeclaredMethod(method_name)) 
						// Если текстовое поле не пустое, ищем метод с параметром Object
						: (mCurrentClass.getDeclaredMethod(method_name, new Class [] {Object.class})) ;
				method.setAccessible(true);
				Object result =  ( mMethodParamField.getText().isEmpty() ) 
						// Вызываем метод для объекта mCurrentObject без параметров:
						? (method.invoke(mCurrentObject)) 
						// Вызываем метод для объекта mCurrentObject со строковым параметром
						: (method.invoke(mCurrentObject, mMethodParamField.getText()));
						
				mOutArea.append(method.getName() + ".invoke(" + mMethodParamField.getText() + ") = " + result + "\n");
			} catch ( Exception ex) {
				mOutArea.append( ex.getClass().getName() + " : " +  ex.getMessage() + "\n" );
			}
		}
	}

}

class MyObject {
	String mString = "default";
	
	public String Test (Object test) {
		return "Called MyObject(\"" + mString + "\").Test (" + test + ")";  
	}
	@Deprecated
	public String Test () {
		return "Called MyObject(\"" + mString + "\").Test (<null>)";  
	}
	
	public MyObject () {
	}
	
	public MyObject ( String string ) {
		mString = string;
	}
}
