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
 * Серверная часть реализации командного протокола при помощи пакета java.net.* и сериализации объектов в Java
 * Данный класс работает в паре с классом java_api_testing.net_api.obj_prot_testin.ObjectProtocol_Client
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
		
		// Для обслуживания входящих TCP-подключений в пакете java.net.* используется класс ServerSocket:
		try ( ServerSocket serverSocket = new ServerSocket(serverPort) ) {
			new Thread(new Runnable() {
				@Override public void run() {
					// Обслуживаем серверный сокет в отдельном потоке:
					while ( !Thread.interrupted() ) {
						try {
							if ( sOpenedSockets.size() >= MAX_CLIENTS_COUNT ) {
								// Ждем освобождения сокетов, если к серверу подключено MAX_CLIENTS_COUNT клиентов:
								synchronized (ObjectProtocol_Server.class) {
									ObjectProtocol_Server.class.wait();
								}
							} else {
								// Метод ServerSocket.accept() блокирует текущий поток пока не будет создано входящее подключение.
								// В качестве результата метод возвращает объект Socket, который используется для обмена данными по TCP-подключению:
								Socket new_client_socket = serverSocket.accept();
								
								// Добавляем клиента в хранилище открытых сокетов для того, чтобы при завершении работы сервера,
								// корректно закрыть все незакрытые входящие соединения:
								sOpenedSockets.add(new_client_socket);
								
								// Запускаем обработчик входящего подключения в отдельном потоке:
								threadPool.execute( new SocketServant (new_client_socket) );
							}
						} catch ( SocketException e) {
							// Исключение SocketException генерируется при закрытии serverSocket во время работы метода accept()
							
							// Закрываем все незакрытые соединения с клиентами:
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
			  // Открываем потоки ObjectInputStream и ObjectOutputStream для обмена объектами с клиентом:
			  ObjectInputStream inStream = new ObjectInputStream( mSocket.getInputStream() );
			  // Порядок создания потоков ВАЖЕН, т. к. во время открытия входного потока ObjectInputStream у клиента запрашивается
			  // дескриптор его выходного потока ObjectOutputStream (это блокирующая операция).
			  // Таким образом, если клиент сначала создает ObjectOutputStream, а потом ObjectInputStream, то для того, чтобы избежать
			  // взаимной блокировки, сервер должен создавать сначала ObjectInputStream, а потом ObjectOutputStream (и наоборот).
			  ObjectOutputStream outStream = new ObjectOutputStream( mSocket.getOutputStream() ) ) {
			System.out.println( "Object input/output header received, starting object protocol..." );
			
			while ( !Thread.currentThread().isInterrupted() ) {
				// Читаем из входного потока объект и отправляем результат обработки объекта в выходной поток:
				outStream.writeObject( processObject (inStream.readObject()) );
			}
		} catch ( EOFException eofException ) {
			// Завершение потока по инициативе клиента, это нормально - пропускаем исключение
		} catch ( IOException ioException ) {
			System.err.println( "IOException - " + ioException.getClass().getName() + " : " + ioException.getMessage() );
		} catch ( ClassNotFoundException cnfException ) {
			System.err.println( "Unrecognized request - " + cnfException.getClass().getName() + " : " + cnfException.getMessage() );
		} finally {
			// При завершении потока работы с сокетом, закрываем его:
			try { mSocket.close(); } catch (IOException e) {}
			
			// Удаляем сокет из списка открытых на данный момент сокетов (чтобы не закрыть его повторно при остановке сервера):
			ObjectProtocol_Server.sOpenedSockets.remove(mSocket);
			
			// Предупреждаем поток сервера об изменениях в количестве подключенных клиентов:
			synchronized (ObjectProtocol_Server.class) {
				ObjectProtocol_Server.class.notifyAll();
			}
			
			System.out.println( String.format("Server connection closed (left = %d)...", ObjectProtocol_Server.sOpenedSockets.size()) );
		}
	}
	
	private Object processObject ( Object obj ) {
		System.out.println( String.format("%1$tT.%1$tL New object read: \"%2$s\"", new java.util.Date(), obj.getClass().getSimpleName()) );
		
		if ( obj instanceof Request ) {
			// Приводим тип полученного объекта к интерфейсу Request и полиморфно вызываем метод Request.processRequest()
			// (здесь необходимо учитывать, чтобы байт коды реальных объектов-наследников Request были доступны серверу):
			return ((Request)obj).processRequest();
		}
		return null;
	}
}
