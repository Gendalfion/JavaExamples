package java_api_testing.java2D.from_book.ch21;
//file: NoisyButton.java
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NoisyButton {

  public static void main(String[] args) throws Exception {
    JFrame frame = new JFrame("NoisyButton");
    
    // ƒл€ воспроизведени€ звуков мы используем
    // простой интерфейс, предоставл€емый классом java.applet.AudioClip:
    final AudioClip sound;
    if ( args.length < 1 ) {
    	sound = Applet.newAudioClip( NoisyButton.class.getResource("bark.aiff") );
    } else {
    	java.io.File file = new java.io.File( args[0] );
    	sound = Applet.newAudioClip( file.toURI().toURL() );
    }

    // set up the button
    JButton button = new JButton("Woof!");
    button.addActionListener(new ActionListener(  ) {
      public void actionPerformed(ActionEvent e) {
    	  // ¬оспроизводим звук sound: 
    	  sound.play(); 
      }
    });

	Container content = frame.getContentPane();
    content.setLayout(new GridBagLayout());
    content.add(button);
    frame.setVisible(true);
    frame.setSize(200, 200);
    frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame.setVisible(true);
  }
}
