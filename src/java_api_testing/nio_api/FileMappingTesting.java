package java_api_testing.nio_api;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * ������������ ������ � ������� ����� ����� java.nio.* � �������������� ������� ������
 * @author Lab119Alex
 *
 */
public class FileMappingTesting implements ListSelectionListener, ActionListener {
	Thread mFileChannelThread = null;
	MappedByteBuffer mMappedBuffer = null;
	
	JList<AddressRange> mRangeList = new JList<>();
	List<AddressRange> mRangeListContent = new Vector<>();
	AbstractListModel<AddressRange> mRangeListModel = new AbstractListModel<AddressRange>() {
		private static final long serialVersionUID = 1L;

		@Override public int getSize() {
			return mRangeListContent.size();
		}

		@Override public AddressRange getElementAt(int index) {
			return mRangeListContent.get(index);
		}
	};
	
	JTextArea mFileContentArea = new JTextArea();
	JTextArea mFileDecodingArea = new JTextArea();
	
	JComboBox<String> mEncodingComboBox = new JComboBox<>( Charset.availableCharsets().keySet().toArray(new String [0]) );
	
	TitledBorder mRangeListBorder = BorderFactory.createTitledBorder("Open file to view it's content...");
	
	{
		Font outFont = new Font(Font.MONOSPACED, Font.BOLD, 13);
		
		mRangeList.setModel(mRangeListModel);
		mRangeList.setFont(outFont);
		mRangeList.addListSelectionListener(this);
		
		mFileContentArea.setEditable(false);
		mFileContentArea.setFont(outFont);
		
		mFileDecodingArea.setEditable(false);
		mFileDecodingArea.setFont(outFont);
		
		mEncodingComboBox.addActionListener(this);
	}
	
	public FileMappingTesting () {
		JFrame myFrame = new JFrame ( "java.nio.* File Channel and Mapped Buffer Testing" );
		myFrame.setSize(1050, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel( new GridLayout(0, 1) );
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		JPanel tmp_panel = new JPanel(new GridLayout(0, 1));
		tmp_panel.setBorder(mRangeListBorder);
		tmp_panel.add(new JScrollPane(mRangeList));
		center_panel.add(tmp_panel);
		
		tmp_panel = new JPanel(new GridLayout(0, 2));
		tmp_panel.setBorder( BorderFactory.createTitledBorder("File content:") );
		tmp_panel.add(new JScrollPane(mFileContentArea));
		tmp_panel.add(new JScrollPane(mFileDecodingArea));
		center_panel.add(tmp_panel);
		
		JPanel top_panel = new JPanel ();
		main_panel.add(top_panel, BorderLayout.NORTH);
		
		JButton openFileBtn = new JButton("Open file...");
		top_panel.add(new JPanel().add(openFileBtn).getParent());
		
		top_panel.add( new JLabel("        Choose text encoding: ") );
		
		top_panel.add(new JPanel().add(mEncodingComboBox).getParent());
		mEncodingComboBox.setSelectedItem( Charset.defaultCharset().name() );
		mEncodingComboBox.setMaximumRowCount(35);
		
		JFileChooser allTypeFileChooser = new JFileChooser();
		allTypeFileChooser.setAcceptAllFileFilterUsed(true);
		openFileBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if ( allTypeFileChooser.showOpenDialog(main_panel) == JFileChooser.APPROVE_OPTION ) {
					if ( mFileChannelThread != null ) {
						// ��������� ������� ����� ������ � �������� ������� (���� �� ��� ������� � �������� �������):
						mFileChannelThread.interrupt();
						try { mFileChannelThread.join(1000); } catch ( InterruptedException ex ) { return; }
					}
					
					// �������� ���� � �����, ������� ������ ������������, � ��������� �������� ������� ��:
					final Path filePath = allTypeFileChooser.getSelectedFile().toPath();
					
					// �������� ������ � �������� ������� � ��������� ������:
					mFileChannelThread = new Thread() {
						@Override public void run() {
							// ��� ������ � ������� ������ ������ � ������ java.nio ������������ ������ � ������.
							// � ���������� � ������� java.io ������ ����� �������� � ����������� ������,
							// ����� ��������� ����������� �� ������� interrupt ��� ������ ������ ����������, � �����
							// ������� ��������� ������ �������:
							try ( FileChannel fc = FileChannel.open(filePath, StandardOpenOption.READ) ) {
								final long file_size = fc.size();
								
								// ������� ������ ����� �� ��� ���������� ��������� ������ (����� �� �����).
								// ������ ����� �� ����� �������� ������ � ����������� ������, ������
								// ��� ��������� � �������� ������ �� ���������� ����������, ����� ���� ������
								// ����������� �� ����� ������������� ���������� ������ �������� ��
								// (��� ����� ���� ����� ���������� ��� ��������� ������� � ����� �������� �������):
								mMappedBuffer = fc.map( FileChannel.MapMode.READ_ONLY, 0, file_size );
								
								SwingUtilities.invokeLater(new Runnable() {
									@Override public void run() {
										// ��������� ������ ��� ������ ��������� ������������ ������� ������ �����:
										mRangeListBorder.setTitle( String.format("%s | size = %s bytes", filePath, String.valueOf(file_size)) );
										
										final long page_size = 256;
										
										long list_size = file_size / page_size + ( ((file_size % page_size) > 0) ? 1 : 0 );
										mRangeListContent = new Vector<>( (int)list_size );
										
										long current_addr = 0;
										while ( current_addr < file_size ) {
											long next_addr = (current_addr + page_size >= file_size) ? ( file_size - 1 ) : (current_addr + page_size - 1);
											mRangeListContent.add ( new AddressRange(current_addr, next_addr) );
											current_addr = next_addr + 1;
										}
										mRangeList.setModel( new AbstractListModel<AddressRange>() {
											private static final long serialVersionUID = 1L;

											@Override public int getSize() {
												return mRangeListContent.size();
											}

											@Override public AddressRange getElementAt(int index) {
												return mRangeListContent.get(index);
											}
										} );
										
										if ( mRangeListContent.size() > 0 ) {
											mRangeList.setSelectedIndex(0);
										}
										
										center_panel.repaint();
									}
								});
								
								while ( !Thread.interrupted() ) {
									Thread.sleep(1000);
								}
							} catch ( InterruptedException ie ) {
							} catch ( Exception exception ) {
								exception.printStackTrace();
								JOptionPane.showMessageDialog( main_panel, exception.getClass().getName() + " : " + exception.getMessage() );
							}
							
						};
					};
					
					mFileChannelThread.start();
				}
			}
		});
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	protected void updateView () {
		AddressRange sel_item = mRangeList.getSelectedValue();
		if ( sel_item == null ) { mFileContentArea.setText(""); mFileDecodingArea.setText(""); return; }
		
		// ��������� ����������� ����������� ����� �� ��������� �������:
		try ( Formatter fmt = new Formatter() ) {
			int cur_pos = sel_item.getStartPosition();
			int last_pos = sel_item.getLastPosition();
			
			// ��� ������ � ������� � ������ java.nio ������������ ����������� �� ������ Buffer.
			// ���� ����� ������������ ����� ��������� ��� ������ ������� �������, � ��� ��������� �������� ���������������:
			// 	����������� (capacity) - ������ ������, ������� ����� �������� � ���� ����� (������ ������� � ������)
			//		- ��� ������� ������ ����������� ���������� ������ ����� ������ �� ���������� ����������, 
			//		  �� ������� ���������� ������ �����;
			// � ���������� � ���������������� ������� ����� ����� ��������� ��������������:
			// 	������� (position) - ������ ���������� ��������, ������� ����� �������� ��� ������� � �����
			//		(�������� ������/������ ������ � ����� ������� �� � ������ �� ������� ������� � ������)
			//	������� (limit) - �����������, � ������ �������� ���������� �������� ������/������ � �������;
			//	����� (mark) - ���������� ������ � ������, ������� ����� ���� ����������� � ���������� ������;
			// � �������� ������ � ������� ������ ����������� ��������� �������:
			// 			mark <= position <= limit <= capacity
			
			// ������������� ������� ������� � ������ ������ �� ��������� ����� ���������� ����� ������:
			mMappedBuffer.position( cur_pos );
			// �������� ������ ������� (����� ������������ ��� ����� ���� �����):
			mMappedBuffer.mark();
			// ������������� ������� ������� ���������� ��������� �������:
			mMappedBuffer.limit( sel_item.getLastPosition() + 1 );
			
			int byte_in_row_index = 0;
			while (cur_pos++ <= last_pos) {
				if ( byte_in_row_index++ == 0 ) {
					fmt.format( "%010X: ", cur_pos - 1 );
				}
				// ��������� ������ ������ �� ������ ��� ������ ������ Buffer.get().
				// ������ ����� ������ ���� ������� � ������� ������� � ����������� ������� � ������ �� 1:
				fmt.format("%02X ", mMappedBuffer.get());
				
				if ( byte_in_row_index == 8 ) {
					fmt.format( "| " );
				} else 
				if ( byte_in_row_index >= 16 ) {
					fmt.format( "\n" );
					byte_in_row_index = 0;
				}
			}
			
			mFileContentArea.setText( fmt.toString() );
			
			// ���������� ������� ������� � ������ � ����� ������������� �����:
			mMappedBuffer.reset();
			
			String curr_charset_name = (String) mEncodingComboBox.getSelectedItem();
			
			// ������ CharsetDecoder �� ������ java.nio.charset.* ������������ ��� ������������� ��������� ������ �� �������:
			CharsetDecoder decoder = Charset.forName( curr_charset_name ).newDecoder();
			try {
				// ���������� ����� �� mMappedBuffer (����� ����������� ������� ������ �� ������� ������� �� ������� �� ������������):
				CharBuffer decoded_text = decoder.decode(mMappedBuffer);
				mFileDecodingArea.setText( decoded_text.toString() );
			} catch (CharacterCodingException codingException) {
				mFileDecodingArea.setText(String.format("Can-not decode file in \"%s\" charset...", curr_charset_name));
			}
			
			// ���������� ������� �������, ����� ������ � ������������� ������� � 0 ��� ������ ������ Buffer.clear()
			// (���������� ������ ������ ����� ����� �� �����������):
			mMappedBuffer.clear();
		} catch ( Exception exception ) {
			exception.printStackTrace();
			JOptionPane.showMessageDialog( null, exception.getClass().getName() + " : " + exception.getMessage() );
		}
	}
	
	@Override public void valueChanged(ListSelectionEvent e) {
		updateView ();
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		updateView ();
	}
	
	private class AddressRange {
		private long mMin = 0, mMax = 0;
		
		public AddressRange ( long min, long max ) { mMin = min; mMax = max; }
		public int getStartPosition () { return (int)mMin; }
		public int getLastPosition () { return (int)mMax; }
		@Override public String toString() { return String.format("%010X - %010X", mMin, mMax); }
	}

	public static void main(String[] args) {
		new FileMappingTesting ();
	}		
}

