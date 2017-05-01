package java_api_testing.java2D.from_book.ch21;
//file: ObserveImageLoad.java
import java.awt.*;
import java.awt.image.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ObserveImageLoad {

  public static void main( String [] args) throws MalformedURLException 
  {
	// ��������� ImageObserver ������������ ��� �������� �� ���������
	// ����������� �������� ����������� java.awt.Image:
	ImageObserver myObserver = new ImageObserver() {
		private long mTotalPixelsLoaded = 0;
		
		// �� �������� ����������� � �������� � �������� �������� ����������� ��� ������
		// ������ ImageObserver.imageUpdate (...):
		public boolean imageUpdate( Image image, int flags, int x, int y, int width, int height) 
		{	
			if ( (flags & HEIGHT) !=0 )
			  System.out.println("Image height = " + height );
			if ( (flags & WIDTH ) !=0 )
			  System.out.println("Image width = " + width );
			if ( (flags & FRAMEBITS) != 0 )
			  System.out.println("Another frame finished.");
			if ( (flags & SOMEBITS) != 0 ) {
				System.out.println("Image section :"
					+ new Rectangle( x, y, width, height ) + "; Total pixels loaded: " 
					+ (mTotalPixelsLoaded += (width * height)) );
			}
			if ( (flags & ALLBITS) != 0 )
			  System.out.println("Image finished!");
			if ( (flags & ABORT) != 0 ) 
			  System.out.println("Image load aborted...");
			return true;
	  }
	};

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Image img;
    // ����� Toolkit.getImage(...) �� ���������� ����������� �������� �����������:
    if ( args.length < 1 ) {
    	img = toolkit.getImage( new URL("http://www.sunhome.ru/i/wallpapers/129/bolshie-ozera.1920x1080.jpg") );
    } else {
    	img = toolkit.getImage( args[0] );
    }
    // �� �������� ����� Toolkit.prepareImage(...) ��� ������ ����������� ������� �������� ����������� ��
    // ���������� ������� (����� ����������� ����������� ��� ������ ������� Image.getWidth, Image.getHeight,
    // Graphics2D.drawImage � ��.):
    toolkit.prepareImage( img, -1, -1, myObserver );
  }

}
