package java_api_testing.net_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ������������ ������ � ��������� ������� ��� ������ ������ java.net.*
 * ������ ����� �������� � ���� � ������� java_api_testing.net_api.SocketTesting_Client
 * 
 * @author Lab119Alex
 *
 */
public class SocketTesting_Server {
	
	private static final int MAX_CLIENTS_COUNT = 3; 
	
	public static final Set<Socket> sOpenedSockets = new HashSet<>();

	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS_COUNT);
		
		int serverPort = 10000;
		if ( args.length > 0 ) {
			serverPort = Integer.valueOf(args[0]);
		}
		
		// ��� ������������ �������� TCP-����������� � ������ java.net.* ������������ ����� ServerSocket:
		try ( ServerSocket serverSocket = new ServerSocket(serverPort) ) {
			new Thread(new Runnable() {
				@Override public void run() {
					// ����������� ��������� ����� � ��������� ������:
					while ( !Thread.interrupted() ) {
						try {
							if ( sOpenedSockets.size() >= MAX_CLIENTS_COUNT ) {
								// ���� ������������ �������, ���� � ������� ���������� MAX_CLIENTS_COUNT ��������:
								synchronized (SocketTesting_Server.class) {
									SocketTesting_Server.class.wait();
								}
							} else {
								// ����� ServerSocket.accept() ��������� ������� ����� ���� �� ����� ������� �������� �����������.
								// � �������� ���������� ����� ���������� ������ Socket, ������� ������������ ��� ������ ������� �� TCP-�����������:
								Socket new_client_socket = serverSocket.accept();
								
								// ��������� ������� � ��������� �������� ������� ��� ����, ����� ��� ���������� ������ �������,
								// ��������� ������� ��� ���������� �������� ����������:
								sOpenedSockets.add(new_client_socket);
								
								// ��������� ���������� ��������� ����������� � ��������� ������:
								threadPool.execute( new SocketServant (new_client_socket) );
							}
						} catch ( SocketException e) {
							// ���������� SocketException ������������ ��� �������� serverSocket �� ����� ������ ������ accept()
							
							// ��������� ��� ���������� ���������� � ���������:
							int client_sockets_closed = 0;
							for ( Socket connected_client : sOpenedSockets ) {
								try { connected_client.close(); ++client_sockets_closed; } catch (IOException io) {}
							}
							if ( client_sockets_closed > 0 ) {
								System.out.println( String.format("%d active clients roughly closed...", client_sockets_closed) );
							}
							
							threadPool.shutdown();
							break;
						} catch ( IOException e ) {
							e.printStackTrace();
						} catch ( InterruptedException ie ) {
							break;
						}
					}
					System.out.println( "Listening thread has terminated..." );
				}
			}).start();
			
			System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping the server..." );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}

class SocketServant implements Runnable {
	Socket mSocket;
	
	public SocketServant ( Socket socket ) {
		mSocket = socket;
	}

	@Override public void run() {
		// ��� ��������� ������ �� ������ ������������ ����� Socket.getInputStream():
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(mSocket.getInputStream()) )) {
			System.out.println( String.format("New connection established to server (count = %d)...", SocketTesting_Server.sOpenedSockets.size()) );
			
			// ���� ���������� �� ����� �������, �������� �������� ������ �� ������ � ��������� ���� 
			// (��� ������������� ������ ���������� ��������� ��������� �� ���������):
			char [] cbuf = new char [256];
			int chars_got;
			while ( (chars_got = reader.read(cbuf)) > 0 ) {
				System.out.println( 
						String.format("%1$tT.%1$tL Got %2$d chars from socket: \"%3$s\"", 
								new java.util.Date(), chars_got, String.valueOf(cbuf, 0, chars_got)) 
						);
			}
		} catch ( IOException ioException ) {
			System.err.println( "IOException - " + ioException.getClass().getName() + " : " + ioException.getMessage() );
		} finally {
			// ��� ���������� ������ ������ � �������, ��������� ���:
			try { mSocket.close(); } catch (IOException e) {}
			
			// ������� ����� �� ������ �������� �� ������ ������ ������� (����� �� ������� ��� �������� ��� ��������� �������):
			SocketTesting_Server.sOpenedSockets.remove(mSocket);
			
			// ������������� ����� ������� �� ���������� � ���������� ������������ ��������:
			synchronized (SocketTesting_Server.class) {
				SocketTesting_Server.class.notifyAll();
			}
			
			System.out.println( String.format("Server connection closed (left = %d)...", SocketTesting_Server.sOpenedSockets.size()) );
		}
	}
}
