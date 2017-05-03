package netbeans_testing;

import javax.swing.*;

import java.awt.BorderLayout;
import java.beans.*; 
 
/**
 * ������������ �������� JavaBeans-����������� ��� ������ ������ java.beans.Beans
 * 
 * <p> ������ ����� �������� � ���� � �������: {@link JugglerSerialize}
 * @author Lab119Alex
 *
 */
public class JugglerDeserialize extends JFrame
{ 
	private static final long serialVersionUID = 1L;

	public JugglerDeserialize( String name ) 
	{ 
		super("Revived Beans!"); 
		try { 
			// ����� java.beans.Beans ������������� ����������� ������ ��� 
			// ������ � JavaBeans-������������.
			
			// ����� java.beans.Beans.instantiate(...) ��������� �������� �������
			// �� ���������� ����� (��� ���� ������������� ����������� �������
			// �������� ��������������� ������ �������, ����� ������������ �
			// ����� ����� ���������� .ser � ������ ����� � ����� �� ������� '/'):
			Object bean = Beans.instantiate( getClass().getClassLoader(), name ); 

			// ������ java.beans.Beans.isInstanceOf(...) � java.beans.Beans.getInstanceOf(...)
			// ������������ ��� �������� � �������������� ������� bean � 
			// ������������� ����:
			if ( Beans.isInstanceOf(bean, JComponent.class) ) { 
				// ��������� �������������� � ���� JComponent:
				JComponent comp = (JComponent) Beans.getInstanceOf( bean, JComponent.class ); 
				// �������� ���������� ��������� �� �����:
				getContentPane().add(comp, BorderLayout.CENTER); 
			} else {
				System.out.println("Bean is not a Component..."); 
			}
		} catch ( java.io.IOException e1 ) { 
			System.out.println("Error loading the serialized object");
		} catch ( ClassNotFoundException e2 ) { 
			System.out.println(
				"Can't find the class that goes with the object");
		} 
	 } 
 
	public static void main(String [] args) { 
		String beanClass;
		if ( args.length > 0 ) {
			beanClass = args[0];
		} else {
			//beanClass = "magicbeans.sunw.demo.juggler.Juggler";
			beanClass = "netbeans_testing.juggler";
		}
		
		JFrame frame = new JugglerDeserialize( beanClass );
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);	
		frame.setVisible(true);
	} 
}

