package myStudy_001;

import javax.swing.*;

public class HelloWorld {

	public static void main(String[] args) {
		JFrame myFrame = new JFrame ( "Hello, World!" );
		
		JLabel myLabel = null;
		if ( args.length > 0 )
		{
			myLabel = new JLabel( args[0], JLabel.CENTER );
		} else
		{
			myLabel = new JLabel("Hello from application!", JLabel.CENTER);
		}
		
		myFrame.setSize(300, 100);
		myFrame.add(myLabel);
		
		
		myFrame.show(true); 
	}

}
