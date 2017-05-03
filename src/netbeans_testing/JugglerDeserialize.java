package netbeans_testing;

import javax.swing.*;

import java.awt.BorderLayout;
import java.beans.*; 
 
/**
 * Демонстрация загрузки JavaBeans-компонентов при помощи класса java.beans.Beans
 * 
 * <p> Данный класс работает в паре с классом: {@link JugglerSerialize}
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
			// Класс java.beans.Beans предоставляет статические методя для 
			// работы с JavaBeans-компонентами.
			
			// Метод java.beans.Beans.instantiate(...) выполняет загрузку объекта
			// по указанному имени (при этом первоначально выполняется попытка
			// загрузки сериализованной версии объекта, путем подставновки в
			// конце имени расширения .ser и замены точек в имени на символы '/'):
			Object bean = Beans.instantiate( getClass().getClassLoader(), name ); 

			// Методы java.beans.Beans.isInstanceOf(...) и java.beans.Beans.getInstanceOf(...)
			// используются для проверки и преобразования объекта bean к 
			// определенному типу:
			if ( Beans.isInstanceOf(bean, JComponent.class) ) { 
				// Выполняем преобразование к типу JComponent:
				JComponent comp = (JComponent) Beans.getInstanceOf( bean, JComponent.class ); 
				// Помещяем полученный компонент на форму:
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

