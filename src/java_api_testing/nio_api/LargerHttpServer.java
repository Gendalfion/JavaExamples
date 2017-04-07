package java_api_testing.nio_api;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

/**
 * ������� Web ������, �������������� HTTP GET-������� � ��������
 * ������������� ����� �� ���������� ���� ������������ �������� classpath ����� ����������.
 * 
 * ���������� �� ������� {@link java_api_testing.net_api.TinyHttpServer} ���, ���
 * ������������ ������� � ������������� ������ ��� ������ ������ java.nio.*
 * 
 * ������ ��� ������� �� ������ ������� �� �����:
 * ������ �������, ������ ���� - ���������������� �� Java. ������������� ����������� ��� ��������������. (4-� �������)
 * 
 * ��������� ��� ��������� ��������� HTTP 1.1
 */
public class LargerHttpServer
{
	// ������ java.nio.channels.Selector ������������ � �������� ���������
	// ������ ���������� ������� � ����� �������. �� ��������� �����������
	// ������� �� ������ ������������������ � ��� ������� ��� �������
	// ������� ������������ ������� �� �������� ������ �������.
	Selector mClientSelector;

	public void run( int serverPort, int threadCount ) throws IOException 
	{
		// ��� �������� ������� Selector ������������ ����������� ����� Selector.open():
		try ( Selector clientSelector = Selector.open() ) {
			mClientSelector = clientSelector;
			
			// ������� ����� ���������� ������ ��� ������ ������������ ������ ServerSocketChannel.open():
			ServerSocketChannel ssc = ServerSocketChannel.open();
			
			// ��������� ����� � ������������� �����:
			ssc.configureBlocking(false);
			
			// ������ ����� �� ������� ��������� 0.0.0.0 (��� �������� ���� ���������� ������ �� ��� ��������� ����������) 
			// � ���� serverPort (�� ��������� ServerSocketChannel ��������� ��� ��������):
	        ssc.socket().bind( new InetSocketAddress( "0.0.0.0", serverPort ) );
	        
	        // ������������ ����� � ��������� clientSelector � ������ ������� OP_ACCEPT
	        // (�. �. ��� ���������� ������ ������� ��������� ���������� � ������� ������):
			ssc.register( mClientSelector, SelectionKey.OP_ACCEPT );
		
			ExecutorService executor = Executors.newFixedThreadPool( threadCount );
	
			new Thread( new Runnable() {
				@Override public void run() {
					System.out.println( String.format("LargerHttpServer started on port %d (threads count = %d)...", serverPort, threadCount) );
					
					while ( !Thread.currentThread().isInterrupted() ) {
			            try {
			            	// ���������� ����������� ������� ��� ������ ������ Selector.select(...) �� ��� ���, 
			            	// ���� �� �������� �������, ��������������� �������� �������.
			            	// �� ���������� ��������� ������� �������� � ����� ����� �������� select, ���� �� ����� 
			            	// �������� �� �������� ����� ��������. ��� �������� ��� ����, ����� �������� ������� �������,
			            	// ���� � ��������� ��������� ��������� �� ����� ������ ������ select (��������, � ������ ������
			            	// ��� �������� ����� ����������� �����):
			                while ( mClientSelector.select(100) == 0 );
			                
			                // ��������� �������, ��������� � ������ Selector.select(...) ������������
			                // ������� Selector.selectedKeys():
			                Set<SelectionKey> readySet = mClientSelector.selectedKeys();
			                
			                try {
				                for(Iterator<SelectionKey> it = readySet.iterator(); it.hasNext();)
				                {
				                	// ������ SelectionKey ��������� ������������� ��� ����������� ������ � ���������,
				                	// �� ������������� ��� ����� � ����� ������� ��� ������:
				                    final SelectionKey key = it.next();
				                    
				                    // ��������� ������� ������ ������:
				                    if ( key.isAcceptable() ) {
				                    	// ����������� ����������� � ���������� ������ ssc:
				                        acceptClient( ssc );
				                    } else {
				                    	// ������ ����� ������������ � ��������:
				                    	
				                    	// ����� ���, ��� ��������� ����� ��������� ����������� ����������, �������
				                    	// ����� ������� � ������ ��� ������ ������ SelectionKey.interestOps(0)
				                    	// (���� ����� �� �������, �� ����������� ����� select ����� ����� �������
				                    	// ������ ����� � � ����� ������� ����� �������� 2 ������ ������������):
				                        key.interestOps( 0 );
				                        
				                        // ��������� ����� ��������� ������� � ������:
				                        executor.execute( new Runnable() {
					                        public void run() {
						                        try {
						                        	handleClient( key );
						                        } catch ( IOException e) { 
						                        	System.err.println(e); 
						                        }
					                        }
				                        });
				                    }
				                }
			                } finally {
			                	// �� ������ ���� ������� �� ��������� ��������� �� �������, �. �.
			                	// ����� Selector.select(...) ����� ������ ��������� �������� � ������� Selector.selectedKeys():
								readySet.clear();
							}
			            } catch ( ClosedSelectorException cse ) {
			            	break;
			            } catch ( IOException e ) { 
			            	System.err.println(e); 
			            }
			        }
					System.out.println( "Listening thread has terminated..." );
				}
			}).start();
		
			System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping LargerHttpServer..." );
			
			executor.shutdown();
		} catch ( Exception exception ) {
			System.err.println(exception);
		}
		
	}

	void acceptClient( ServerSocketChannel ssc ) throws IOException
    {
		// ��������� ����������� �� ��������� ������:
		SocketChannel clientSocket = ssc.accept();
		
		// ��������� ����� ������������ � �������� � ������������� �����:
		clientSocket.configureBlocking(false);
		
		// ������������ ����� � ��������� mClientSelector (� ������ �������� ���������� � ������ �� ������):
		SelectionKey key =  clientSocket.register( mClientSelector, SelectionKey.OP_READ );
		
		HttpdConnection client = new HttpdConnection( clientSocket );
		// ������ SelectionKey ����� ����������� ������� ������ �� ���� ������������ ������.
		// ��������� ��������� ������ ������������ HTTP-������� � ������ clientSocket:
		key.attach( client );
	}

	void handleClient( SelectionKey key ) throws IOException
    {
		// �������� ������ �� ������ ������������ HTTP-�������:
		HttpdConnection client = (HttpdConnection)key.attachment();
		
		if ( key.isReadable() ) {
			// ����������� ���������� � ������ �� ������:
			client.read( key );
        } else {
        	// ����������� ���������� � ������ � �����:
			client.write( key );
        }
		
		// ����� Selector.wakeup() ���������� ���������� ����������� �����������
		// ������������� select �� ������ ���������.
		// �� ���������� wakeup � �������� ������� �� ����������
		// � ������ ������� (����� ��������� �������� �� � ���� ������
		// ����� �������� ���������� ������ select):
		mClientSelector.wakeup();
	}

	public static void main( String args[] ) throws IOException {
		final int serverPort;
		if ( args.length > 0 ) {
			serverPort = Integer.valueOf(args[0]);
		} else {
			serverPort = 80;
		}
		
		final int threadCount;
		if ( args.length > 1 ) {
			threadCount = Integer.valueOf(args[1]);
		} else {
			threadCount = 3;
		}
		
        new LargerHttpServer().run( serverPort, threadCount );
	}
}

/**
 * ��������������� ������, ������������� ������� ������ HTTP-�������
 */
class HttpdConnection {
	static final Charset CHARSET = Charset.forName("8859_1");
	static final Pattern HTTP_GET_PATTERN = Pattern.compile("(?s)GET /?(\\S*).*");
	SocketChannel mClientSocketChannel = null;
	ByteBuffer mIOBuffer = ByteBuffer.allocateDirect( 64*1024 );
	String mRequest = null, mResponse = null;
	FileChannel mFileChannel = null;
	int mCurrentFilePosition = 0;

	public HttpdConnection ( SocketChannel clientSocket ) {
		mClientSocketChannel = clientSocket;
	}

	void read( SelectionKey key ) throws IOException {
		// ���� ��������� ����� ������, ��� ������� ������ �������� ������ '\n',
		if ( (mRequest == null) && 
			 ( (mClientSocketChannel.read( mIOBuffer ) == -1) || (mIOBuffer.get( mIOBuffer.position()-1 ) == '\n') ) 
		   ) {
			// ��: ������������ ������ �� �������:
			processRequest( key );
		} else {
			// �����: ���������� ������ �� ������:
			key.interestOps( SelectionKey.OP_READ );
		}
	}

	@SuppressWarnings("resource")
	void processRequest( SelectionKey key ) {
		mIOBuffer.flip();
		// ���������� ����� �� mIOBuffer � �������������� ��������� CHARSET:
		mRequest = CHARSET.decode( mIOBuffer ).toString();
		
		if ( mRequest == null ) {
			return;
		}
		
		// ������� ������ GET �� �������:
		Matcher get = HTTP_GET_PATTERN.matcher( mRequest );
		if ( get.matches() ) {
			mRequest = get.group(1);
			if ( mRequest.endsWith("/") || mRequest.equals("") ) {
				mRequest = mRequest + "index.html";
			}
			if ( !mRequest.startsWith("/") ) {
				mRequest = "/" + mRequest;
			}
			System.out.println( String.format("%1$tT.%1$tL Requested resource: \"%2$s\"", new java.util.Date(), mRequest) );
			
			try {
				// ������� �������� �������� ����� �� ����������� ����� ������� mRequest:
				URL fileURL = HttpdConnection.class.getResource(mRequest);
				mFileChannel = new FileInputStream ( fileURL.getFile() ).getChannel();
				// ���� ������� ������:
				mResponse = "HTTP/1.1 200 OK\r\n\r\n";
			} catch ( FileNotFoundException | NullPointerException e ) {
				// ���� �� ������ �� ���������� ����� �������:
				mResponse = "404 Object Not Found";
				System.err.println( String.format("Resource \"%s\" not found!", mRequest) );
			}
		} else {
			// ������ �� �������������� �� �������:
			mResponse = "400 Bad Request" ;
			System.err.println( String.format("Bad request: \"%s\"!", mRequest) );
		}

		if ( mResponse != null ) {
			// ��� ������� ���������� ������ ���������� ��� � mIOBuffer � ��������� CHARSET:
			mIOBuffer.clear();
			mIOBuffer.put( mResponse.getBytes(CHARSET) );
			mIOBuffer.flip();
		}
		// ������ ���������, ����������� ����������� ���� � ����� �������� ���������� � ������ ������ � �����:
		key.interestOps( SelectionKey.OP_WRITE );
	}

	void write( SelectionKey key ) throws IOException {
		if ( mResponse != null ) {
			// ���������� ����� �� mIOBuffer � �����:
			mClientSocketChannel.write( mIOBuffer );
			
			// ����� ����� �������� � ������������� ������, �������� ������ ������������ ���������,
			// � ���������� ���������� ���� ��������� ���������� ���������� ����� � �������� ������ ������,
			// ������� �� ���������, ��� ����� mIOBuffer ����� ���� ��������� � ����� �� � ������� ����:
			if ( mIOBuffer.remaining() == 0 ) {
				mResponse = null;
			}
		} else 
		// ����� �������� mResponse, ��������� ����� �� ��������� ��� ������ �� ��������� ������ mFileChannel:
		if ( mFileChannel != null ) {
			// ��� �������� ����������� ����� �� ������ mFileChannel � ����� mClientSocketChannel
			// �� ���������� ����� FileChannel.transferTo (...), ������� �������� ����������� ������� 
			// (����� ������ JVM) �������������� ������ �� ��������� ������ � ����� ����������:
			int remaining = (int)mFileChannel.size() - mCurrentFilePosition;
			long sent = mFileChannel.transferTo( mCurrentFilePosition, remaining, mClientSocketChannel);
			
			// ��� �������� �� ���������, ��� ���� ����� ������������ ��������, �. �. 
			// mClientSocketChannel �������� � ������������� ������, ��� ���������� ��������
			// transferTo(...) ������������ ��������� � ���������� � �������� ����������
			// ���������� ���� ��������� � �������� ������ ������:
			if ( (sent >= remaining) || (remaining <= 0) ) {
				// ���� ��������� ��������� � mClientSocketChannel, ��������� �������� �����:
				mFileChannel.close();
				mFileChannel = null;
			} else {
				// ���������� ������� � �����, ����� ���������� � ��� �������� �� ����� ����������
				// ������� ���������� ������ � �������� ������:
				mCurrentFilePosition += sent;
			}
		} 
		
		if ( (mResponse == null) && (mFileChannel == null) ) {
			// ��� ������ ����������, ��������� ���������� �����:
			mClientSocketChannel.close();
			
			// ������� ����� �� ��������� ������� ������ SelectionKey.cancel():
			key.cancel();		
		} else {
			// �������� ������ ��� ��������, ���������� ������� ������� ���������� � ����� � �����:
			key.interestOps( SelectionKey.OP_WRITE );
		}
	}
}
