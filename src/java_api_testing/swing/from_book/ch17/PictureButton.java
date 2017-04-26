package java_api_testing.swing.from_book.ch17;

import java.awt.event.*;
import javax.swing.*;

public class PictureButton
{
  public static void main(String[] args) 
  {
    JFrame frame = new JFrame();

    Icon icon = new ImageIcon( PictureButton.class.getResource("rhino.gif") );
    JButton button = new JButton(icon);
    button.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        System.out.println("Urp!");
      }
    });

    frame.getContentPane().add( button );
	frame.pack();
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible(true);
  }
}
