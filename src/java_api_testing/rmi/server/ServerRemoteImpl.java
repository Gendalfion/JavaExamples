package java_api_testing.rmi.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import java_api_testing.rmi.Request;
import java_api_testing.rmi.RequestListener;
import java_api_testing.rmi.ServerRemote;

/**
 * Демонстрация реализации серверной стороны сетевого взаимодействия при помощи
 * технологии удаленного вызова процедур, предоставляемой пакетом java.rmi.*
 * Данный класс работает в паре с классами:
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * {@link java_api_testing.rmi.client.RMIClassLoadTesting}
 * 
 * @author Lab119Alex
 *
 */
public class ServerRemoteImpl
	// Класс UnicastRemoteObject при помощи наследования позволяет сделать указанный объект
	// RMI-сервером. При прямом наследовании от UnicastRemoteObject объект экспортируется в качестве
	// RMI-сервера автоматически. В качестве альтернативы можно использовать статический метод
	// UnicastRemoteObject.exportObject(...).
	extends java.rmi.server.UnicastRemoteObject
	
	// Реализуем RMI-интерфейс ServerRemote:
	implements ServerRemote {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_CLIENTS_COUNT = 3;
	ExecutorService mThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS_COUNT);
	
	private String mServerName;
	
	public ServerRemoteImpl ( String server_bind_name ) throws java.rmi.RemoteException, MalformedURLException {
		// Публикуем интерфейс ServerRemote под именем server_bind_name в реестре удаленных объектов.
		// Данный реестр это сервисная утилита (rmiregistry.exe в папке bin в месте установки JRE), которая
		// прослушивает стандартный (или заданный) порт и предоставляет сервис регистрации объектов
		// (для серверных частей удаленного приложения), а также сервис получения ссылок на удаленные
		// объекты RMI (для клиентских частей удаленного приложения).
		// Для работы с реестром удаленных объектов RMI используется статический класс java.rmi.Naming:
		java.rmi.Naming.rebind ( server_bind_name, this );
		
		mServerName = server_bind_name;
		
		System.out.println( String.format("ServerRemoteImpl server started with registry name \"%s\"...", server_bind_name) );
	}
	
	public void shutdownServer ( long timeout_ms ) throws InterruptedException {
		try {
			java.rmi.server.UnicastRemoteObject.unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			System.err.println( e.toString() );
		}
		
		mThreadPool.shutdownNow ();
		mThreadPool.awaitTermination (timeout_ms, TimeUnit.MILLISECONDS);
		
		System.out.println( "Server was stopped..." );
	}
	
	// Реализуем метод удаленного RMI-интерфейса ServerRemote.asyncExecute:
	@Override public void asyncExecute(Request request, RequestListener listener) throws RemoteException {
		mThreadPool.submit( new Runnable() {
			@Override public void run() {
				try {
					System.out.println( String.format("%1$tT.%1$tL New request: \"%2$s\"", new java.util.Date(), request.getClass().getName()) );
					
					// Выполняем запрос, присланный клиентом.
					// Т. к. интерфейс Request не является удаленным, то выполнение кода Request.processRequest()
					// будет выполнено на сервере:
					Object result = request.processRequest();
					
					// Интерфейс RequestListener является удаленным RMI-объектом,
					// поэтому вызов метода RequestListener.requestProcessed(...) будет
					// исполнен на клиентской стороне клиент-серверного RMI-приложения:
					listener.requestProcessed(request, result);
					
					System.out.println( String.format("%1$tT.%1$tL \"%3$s\" processed, result: \"%2$s\""
							, new java.util.Date(), result.toString(), request.getClass().getSimpleName()) );
				} catch ( RemoteException  re) {
					System.err.println( "Client remote call exception: " + re.toString() );
				}
			}
		} );
	}
	
	// Релизуем метод удаленного RMI-интерфейса ServerRemote.createFrame:
	@Override public JFrame createFrame(String frame_title) throws RemoteException {
		System.out.println( String.format("%1$tT.%1$tL createFrame: title = \"%2$s\""
				, new java.util.Date(), frame_title) );
		// Объект SomeSimpleFrame создается на серверной стороне и передается клиенту
		// в сериализованном виде:
		return new SomeSimpleFrame (frame_title, mServerName);
	}

	public static void main(String[] args) throws InterruptedException {
		String serverName;
		if ( args.length > 0 ) {
			serverName = args[0];
		} else {
			serverName = "ServerRemoteImpl";
		}
		
		ServerRemoteImpl server = null;
		try {
			server = new ServerRemoteImpl(serverName);
			
			System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping the server..." );
		} catch ( Exception exception ) {
			System.err.println( "Server execution exception: " + exception.toString() );
		} finally {
			if ( server != null ) {
				server.shutdownServer(3000);
			}
		}
	}		
}
