package java_api_testing.net_api.obj_prot_testing;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ��������� ����� ���������� ���������� ��������� ��� ������ ������ java.net.* � ������������ �������� � Java
 * ������ ����� �������� � ���� � ������� java_api_testing.net_api.obj_prot_testin.ObjectProtocol_Client
 * 
 * @author Lab119Alex
 *
 */

public class ObjectProtocol_Server {
	
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
								synchronized (ObjectProtocol_Server.class) {
									ObjectProtocol_Server.class.wait();
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
			
			System.out.println( String.format("Object Protocol server started on port %d...", serverPort) );
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
		
		System.out.println( String.format("New connection established to server (count = %d)...", ObjectProtocol_Server.sOpenedSockets.size()) );
		
		try ( 
			  // ��������� ������ ObjectInputStream � ObjectOutputStream ��� ������ ��������� � ��������:
			  ObjectInputStream inStream = new ObjectInputStream( mSocket.getInputStream() );
			  // ������� �������� ������� �����, �. �. �� ����� �������� �������� ������ ObjectInputStream � ������� �������������
			  // ���������� ��� ��������� ������ ObjectOutputStream (��� ����������� ��������).
			  // ����� �������, ���� ������ ������� ������� ObjectOutputStream, � ����� ObjectInputStream, �� ��� ����, ����� ��������
			  // �������� ����������, ������ ������ ��������� ������� ObjectInputStream, � ����� ObjectOutputStream (� ��������).
			  ObjectOutputStream outStream = new ObjectOutputStream( mSocket.getOutputStream() ) ) {
			System.out.println( "Object input/output header received, starting object protocol..." );
			
			while ( !Thread.currentThread().isInterrupted() ) {
				// ������ �� �������� ������ ������ � ���������� ��������� ��������� ������� � �������� �����:
				outStream.writeObject( processObject (inStream.readObject()) );
			}
		} catch ( EOFException eofException ) {
			// ���������� ������ �� ���������� �������, ��� ��������� - ���������� ����������
		} catch ( IOException ioException ) {
			System.err.println( "IOException - " + ioException.getClass().getName() + " : " + ioException.getMessage() );
		} catch ( ClassNotFoundException cnfException ) {
			System.err.println( "Unrecognized request - " + cnfException.getClass().getName() + " : " + cnfException.getMessage() );
		} finally {
			// ��� ���������� ������ ������ � �������, ��������� ���:
			try { mSocket.close(); } catch (IOException e) {}
			
			// ������� ����� �� ������ �������� �� ������ ������ ������� (����� �� ������� ��� �������� ��� ��������� �������):
			ObjectProtocol_Server.sOpenedSockets.remove(mSocket);
			
			// ������������� ����� ������� �� ���������� � ���������� ������������ ��������:
			synchronized (ObjectProtocol_Server.class) {
				ObjectProtocol_Server.class.notifyAll();
			}
			
			System.out.println( String.format("Server connection closed (left = %d)...", ObjectProtocol_Server.sOpenedSockets.size()) );
		}
	}
	
	private Object processObject ( Object obj ) {
		System.out.println( String.format("%1$tT.%1$tL New object read: \"%2$s\"", new java.util.Date(), obj.getClass().getSimpleName()) );
		
		if ( obj instanceof Request ) {
			// �������� ��� ����������� ������� � ���������� Request � ���������� �������� ����� Request.processRequest()
			// (����� ���������� ���������, ����� ���� ���� �������� ��������-����������� Request ���� �������� �������):
			return ((Request)obj).processRequest();
		}
		return null;
	}
}
