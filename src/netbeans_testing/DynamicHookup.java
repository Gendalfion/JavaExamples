package netbeans_testing;

import javax.swing.*;
import java.awt.event.*;
import java.beans.EventHandler;

/**
 * ������������ ������������� ���������� ������� ��� ������ ������ java.beans.EventHandler
 * 
 * @author Lab119Alex
 */
public class DynamicHookup extends JFrame {
  private static final long serialVersionUID = 1L;

  JLabel label = new JLabel( "Ready...", JLabel.CENTER );
  int count = 1;

  public DynamicHookup() {
    JButton launchButton = new JButton("Launch!");
    getContentPane().add( launchButton, "South" );
    getContentPane().add( label, "Center" );
	
    launchButton.addActionListener( 
        // ��������� ������������ ���������� ������� � ������ launchButton
        // � ������� ������ DynamicHookup.launchTheMissiles():
	EventHandler.create( ActionListener.class, this, "launchTheMissiles") 
    );
  }

  public void launchTheMissiles() {
    label.setText("Launched: " + count++ );
  }

  public static void main( String[] args ) {
    JFrame frame = new DynamicHookup();
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setSize(150, 150);
    frame.setLocationRelativeTo(null);
    frame.setVisible( true );
  }
}
