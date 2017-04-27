package java_api_testing.swing.from_book.ch19;
//file:	MoveButton.java
import java.awt.event.*;
import javax.swing.*;

public class MoveButton	extends	JPanel {
  private static final long serialVersionUID = 1L;
	
  JButton button = new JButton("I Move");

  public MoveButton() {
    setLayout(null);
    add(button);
    button.setSize(button.getPreferredSize());
    button.setLocation(20, 20);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
    	  button.setLocation(e.getX(), e.getY());
      }
    });
  }

  public static	void main(String[] args) {
    JFrame frame = new JFrame("MoveButton");
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setSize(250, 200);
    frame.setLocation(200, 200);
    frame.setContentPane(new MoveButton());
    frame.setVisible(true);
  }
}
