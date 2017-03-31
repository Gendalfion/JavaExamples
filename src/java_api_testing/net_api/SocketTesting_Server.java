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
 * ƒемонстраци€ работы с серверным сокетом при помощи пакета java.net.*
 * ƒанный класс работает в паре с классом java_api_testing.net_api.SocketTesting_Client
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
		
		// ƒл€ обслуживани€ вход€щих TCP-подключений в пакете java.net.* используетс€ класс ServerSocket:
		try ( ServerSocket serverSocket = new ServerSocket(serverPort) ) {
			new Thread(new Runnable() {
				@Override public void run() {
					// ќбслуживаем серверный сокет в отдельном потоке:
					while ( !Thread.interrupted() ) {
						try {
							if ( sOpenedSockets.size() >= MAX_CLIENTS_COUNT ) {
								// ∆дем освобождени€ сокетов, если к серверу подключено MAX_CLIENTS_COUNT клиентов:
								synchronized (SocketTesting_Server.class) {
									SocketTesting_Server.class.wait();
								}
							} else {
								// ћетод ServerSocket.accept() блокирует текущий поток пока не будет создано вход€щее подключение.
								// ¬ качестве результата метод возвращает объект Socket, который используетс€ дл€ обмена данными по TCP-подключению:
								Socket new_client_socket = serverSocket.accept();
								
								// ƒобавл€ем клиента в хранилище открытых сокетов дл€ того, чтобы при завершении работы сервера,
								// корректно закрыть все незакрытые вход€щие соединени€:
								sOpenedSockets.add(new_client_socket);
								
								// «апускаем обработчик вход€щего подключени€ в отдельном потоке:
								threadPool.execute( new SocketServant (new_client_socket) );
							}
						} catch ( SocketException e) {
							// »сключение SocketException генерируетс€ при закрытии serverSocket во врем€ работы метода accept()
							
							// «акрываем все незакрытые соединени€ с клиентами:
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
		// ƒл€ получени€ данных из сокета используетс€ метод Socket.getInputStream():
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(mSocket.getInputStream()) )) {
			System.out.println( String.format("New connection established to server (count = %d)...", SocketTesting_Server.sOpenedSockets.size()) );
			
			// ѕока соединение не будет закрыто, печатаем прин€тые данные из сокета в текстовом виде 
			// (дл€ декодировани€ данных используем системную кодировку по умолчанию):
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
			// ѕри завершении потока работы с сокетом, закрываем его:
			try { mSocket.close(); } catch (IOException e) {}
			
			// ”дал€ем сокет из списка открытых на данный момент сокетов (чтобы не закрыть его повторно при остановке сервера):
			SocketTesting_Server.sOpenedSockets.remove(mSocket);
			
			// ѕредупреждаем поток сервера об изменени€х в количестве подключенных клиентов:
			synchronized (SocketTesting_Server.class) {
				SocketTesting_Server.class.notifyAll();
			}
			
			System.out.println( String.format("Server connection closed (left = %d)...", SocketTesting_Server.sOpenedSockets.size()) );
		}
	}
}
