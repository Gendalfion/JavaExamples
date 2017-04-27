package myStudy_001;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BlinkAndColors {

	public static void main(String[] args) {
		JFrame myWnd = new JFrame("Blink And Colors");
		myWnd.setSize(600, 300);
		myWnd.setLocationRelativeTo(null);	// Заставляет окно появиться в центре экрана
		myWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWnd.add( new BlinkAndColorsComponent("Текст, который можно перетаскивать (ПКМ)") );
		myWnd.setVisible(true);
	}

}

class BlinkAndColorsComponent extends JComponent implements MouseMotionListener, ActionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private String mOutLabelText = "";
	
	private int mMousePosX = 100, mMousePosY = 100;
	private int mCurMouseBtnPressed = MouseEvent.NOBUTTON;
	private JButton myButton = null;
	private Font mTextFont = new Font("", Font.BOLD, 16);
	private boolean mTextBlinkState = true;
	
	private int mTextColorIndex = 0;
	private final Color [] TEXT_COLORS = { Color.BLACK, Color.BLUE, Color.GREEN, Color.CYAN, Color.RED };
	
	public BlinkAndColorsComponent( String text_str ) {
		mOutLabelText = text_str;
		
		this.addMouseMotionListener(this);
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				mCurMouseBtnPressed = MouseEvent.NOBUTTON;
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				mCurMouseBtnPressed = arg0.getButton();
			}
		});
		
		setLayout( new FlowLayout() );
		myButton = new JButton("Сменить цвет текста");
		myButton.addActionListener(this);
		this.add(myButton);
		
		new Thread(this).start();
		
		System.out.println("Конструктор класса BlinkAndColorsComponent...");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (mTextBlinkState)
		{
			g.setFont(mTextFont);
			g.setColor( getTextColor() );
			g.drawString(mOutLabelText, mMousePosX, mMousePosY);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ( mCurMouseBtnPressed == MouseEvent.BUTTON3 )
		{	// Реагируем на перетаскивание только правой кнопкой мыши
			mMousePosX = e.getX();
			mMousePosY = e.getY();
			
			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
	protected synchronized Color getTextColor() {
		return TEXT_COLORS[mTextColorIndex];
	}
	
	protected synchronized void NextColor () {
		mTextColorIndex = (mTextColorIndex + 1) % TEXT_COLORS.length;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if ( arg0.getSource().equals(myButton))
		{
			NextColor();
			System.out.println("Нажата кнопка \"" + myButton.getText() +"\"");
			
			this.repaint();
		}
	}
	
	@Override
	public void run() {
		try {
			while ( true )
			{	
				Thread.sleep(500);
				mTextBlinkState = !mTextBlinkState;
				repaint();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
