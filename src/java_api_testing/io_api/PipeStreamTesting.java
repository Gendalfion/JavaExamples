package java_api_testing.io_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ������������ ������ � ���������� �������� java.io.PipedInputStream � java.io.PipedOutputStream,
 * � ����� � �������������� ��������� ���������� ���������� � Java API
 * @author Lab119Alex
 *
 */
public class PipeStreamTesting {
	// ������ PipedOutputStream ������������ � ���� � PipedInputStream ��� ������������ �������� ������
	// ��� ������� �������� ������������ ���������� OutputStream � InputStream ��������������;
	// ��� ��������, ��� ������ ������� �������� � �������������������� �������� ����
	PipedOutputStream	mPipedOutputStream = new PipedOutputStream();
	PipedInputStream 	mPipedInputStream = new PipedInputStream();
	
	// ������ InputStreamReader �������� ������������� ������ ����� ��������� �������, �������������� �� InputStream � Reader
	// �. �. �� ����������� ������������������� ����� ������� ���� � ����� �������� � ��������������� ����������
	InputStreamReader	mInputStreamReader = new InputStreamReader(mPipedInputStream); // ����������� mPipedInputStream � ������� InputStreamReader
	
	// ������ OutputStreamWriter �������� ������������� ������ ����� ��������� �������, �������������� �� mOutputStreamReader � Writer
	// �. �. �� ����������� ������������������� ����� �������� ���� � ����� �������� � ��������������� ����������
	OutputStreamWriter	mOutputStreamWriter = new OutputStreamWriter(mPipedOutputStream); // ����������� mPipedOutputStream � ������� OutputStreamWriter
	
	// ������ PrintWriter �������� ��������������� �������� ��� ��������� ��������
	// ������ ����� ��������� ������ � �������� ����� ������ ������ � �������������� ������� (flush) ������ (������ �������� ������������), 
	// � ����� �� �� ���������� ���������� IOException ����� ������ ��������� �������� ������ � ����� (������ ����� �������� ��� ������ ������ checkError())
	PrintWriter mPrintWriter = new PrintWriter(mOutputStreamWriter, true); // ����������� mOutputStreamWriter � ������� PrintWriter
	
	// ������ BufferedReader �������� ��������������� �������� ��� ������� ��������� �������
	// �� ��������� ������������� ���� � ������ �� ������ ���������� ������ �������
	BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader); // ����������� mInputStreamReader � ������� BufferedReader
	
	// �������� ������ ��������� ��������� �� ��������� Java ��� ������ ������ java.nio.charset.Charset:
	final static Charset [] SUPPORTED_CHARSETS = Charset.availableCharsets().values().toArray(new Charset[0]);
	
	{
		try {
			// ��������� ������� � �������� ������ (��� ���������� ������ �������� ������ Pipe �������� �� �����!):
			mPipedInputStream.connect(mPipedOutputStream);
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, e.getClass().getName() + " : " + e.getMessage() );
		}
	}

	public PipeStreamTesting () {
		JFrame myFrame = new JFrame ( "java.io.PipedInputStream and java.io.PipedOutputStream Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JTextArea output_stream_area = new JTextArea("��������� �� ������� (english symbols here)...");
		output_stream_area.setBorder(BorderFactory.createTitledBorder("java.io.PipedOutputStream:"));
		output_stream_area.setFont(new Font(Font.MONOSPACED, 0, 14));
		center_panel.add(new JScrollPane(output_stream_area));
		
		JTextArea input_stream_area = new JTextArea();
		input_stream_area.setBorder(BorderFactory.createTitledBorder("java.io.PipedInputStream:"));
		input_stream_area.setEditable(false);
		input_stream_area.setFont(new Font(Font.MONOSPACED, 0, 14));
		center_panel.add(new JScrollPane(input_stream_area));
		
		JPopupMenu clear_menu = new JPopupMenu();
		clear_menu.add(new JMenuItem("Clear")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				input_stream_area.setText("");
			}
		});
		input_stream_area.setComponentPopupMenu(clear_menu);
		
		JPanel top_panel = new JPanel();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JComboBox<Charset> outstream_encoding_box = new JComboBox<>(SUPPORTED_CHARSETS);
		top_panel.add(new JLabel("OutputStreamWriter encoding:  "));
		top_panel.add(outstream_encoding_box);
		
		outstream_encoding_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ������� ������-������� OutputStreamWriter ��� mPipedOutputStream, �������� ��������� ��� ����������� �������� � �����:
				mOutputStreamWriter = 
						new OutputStreamWriter(mPipedOutputStream, outstream_encoding_box.getItemAt(outstream_encoding_box.getSelectedIndex()));
				// ������� ������-������� PrintWriter ��� mOutputStreamWriter:
				mPrintWriter = new PrintWriter(mOutputStreamWriter, true); 
			}
		});
		outstream_encoding_box.setSelectedItem( Charset.defaultCharset() );
		outstream_encoding_box.setMaximumRowCount(40);
		
		JButton print_btn = new JButton("PrintWriter.println(String)");
		top_panel.add(print_btn);
		print_btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( output_stream_area.getText().length() > 0 ) {
					// ���������� ������ � �������� ����� ����� ������ PrintWriter:
					mPrintWriter.println(output_stream_area.getText());
					
					// ��������� ������ ������ � ����� ��� ������ ������ checkError():
					if ( mPrintWriter.checkError() ) {
						JOptionPane.showMessageDialog(myFrame, "PrintWriter error!");
					}
				}
			}
		});
		
		JPanel bottom_panel = new JPanel ();
		main_panel.add(bottom_panel, BorderLayout.SOUTH);
		
		JComboBox<Charset> instream_decoding_box = new JComboBox<>(SUPPORTED_CHARSETS);
		bottom_panel.add(new JLabel("InputStreamReader decoding:  "));
		bottom_panel.add(instream_decoding_box);
		
		instream_decoding_box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ������� ������-������� InputStreamReader ��� mPipedInputStream, �������� ��������� ��� ������������� �� ����� ���� � �������:
				mInputStreamReader =  
							new InputStreamReader(mPipedInputStream, instream_decoding_box.getItemAt(instream_decoding_box.getSelectedIndex()));
				// ������� ������-������� BufferedReader ��� mInputStreamReader:
				mBufferedReader = new BufferedReader(mInputStreamReader);
			}
		});
		instream_decoding_box.setSelectedItem(Charset.defaultCharset());
		instream_decoding_box.setMaximumRowCount(40);
		
		// ������� ����� ������ ������ �� �������� ������:
		new Thread ( new Runnable() {
			@Override
			public void run() {
				String str;
				try {
					int i = 1;
					// ������ ������ �� ����������������� �������� ������:
					while ( (str = mBufferedReader.readLine()) != null ) {
						int skipped = 0;
						// ����������� ��� ���������� ������� �� ������� ������ (����� ���������� ����� �������� ��������� � �������� ������/������):
						while ( mBufferedReader.ready() ) { 
							mBufferedReader.skip(1); 
							++skipped; 
						}
						input_stream_area.append( String.format("%6d: \"%s\" (%d chars skipped after eol)\n", i++, str, skipped) );
					}
				} catch ( IOException e ) {
					JOptionPane.showMessageDialog( null, e.getClass().getName() + " : " + e.getMessage() );
				} 
			}
		} ).start();
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new PipeStreamTesting();
	}
}
