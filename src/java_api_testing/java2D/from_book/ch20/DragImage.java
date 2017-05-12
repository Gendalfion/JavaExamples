package java_api_testing.java2D.from_book.ch20;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DragImage extends JComponent implements MouseMotionListener {
	private static final long serialVersionUID = 1L;

	static int imageWidth = 160, imageHeight = 160;
	int grid = 10;
	int imageX = 100, imageY = 100;
	Image image;

	public DragImage(Image i) {
		image = i;
		addMouseMotionListener(this);
	}

	public void mouseDragged(MouseEvent e) {
		// ������������� ����������� ��� ������ �����:
		imageX = e.getX();
		imageY = e.getY();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// ������ ��������� �����:
		int w = getSize().width / grid;
		int h = getSize().height / grid;
		boolean black = false;
		for (int y = 0; y <= grid; y++)
			for (int x = 0; x <= grid; x++) {
				g2.setPaint(black ? Color.black : Color.white);
				black = !black;
				g2.fillRect(x * w, y * h, w, h);
			}

		// ������ ����������� � ������� �����������:
		g2.drawImage(image, imageX - image.getWidth(this) / 2, imageY - image.getHeight(this) / 2, this);
		// ������ g2.drawImage(...), image.getWidth(...), image.getHeight(...)
		// ��������
		// ������������ � �������������, ��� ����������� ����� ���� ��� ��
		// ��������� �� ���������
		// � ������� �������. � ����� ������, drawImage ������ ������ �� �����
		// �����������, ������� ���������;
		// � getWidth � getHeight ���������� -1.
		// ��� ��������� ������� � ���������/��������� �������� �����������
		// ������������ callback-���������
		// ImageObserver. �� � ������ ������ ���������� ��� ��������� DragImage
		// � �������� ImageObserver.
		// (��-��������� ��������� ����� �������� ���� ����� repaint ���
		// ���������� ������� �������� �����������)
	}

	public static void main(String[] args) {
		String imageFile = "L1-Light.jpg";
		if (args.length > 0)
			imageFile = args[0];

		// Turn on/off double buffering:
		JCheckBox doubleBufferingEnabled = new JCheckBox("Double Buffering:");
		// ��-��������� ��� ����������� swing ������� ����������� ��������:
		doubleBufferingEnabled.setSelected(true);
		doubleBufferingEnabled.setHorizontalTextPosition(SwingConstants.LEADING);
		doubleBufferingEnabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RepaintManager.currentManager(null).setDoubleBufferingEnabled(doubleBufferingEnabled.isSelected());
			}
		});

		// ��������� �������� ����������� �� ���������� �������:
		Image image = Toolkit.getDefaultToolkit().getImage(DragImage.class.getResource(imageFile));
		// ������������ ����������� �� ��������� ��������:
		image = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
		JFrame frame = new JFrame("DragImage");
		frame.getContentPane().add(new DragImage(image));
		frame.getContentPane().add(doubleBufferingEnabled, BorderLayout.SOUTH);
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
