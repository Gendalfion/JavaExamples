package java_api_testing.net_api;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.concurrent.*;

/**
 * Простой Web сервер, обрабатывающий HTTP GET-запросы и выдающий
 * запрашиваемые файлы по ресурсному пути относительно текущего classpath среды выполнения 
 * 
 * Данный код написан на основе примера из книги:
 * Патрик Нимейер, Дэниэл Леук - Программирование на Java. Исчерпывающее руководство для профессионалов. (4-е издание)
 */
public class TinyHttpServer {
	public static void main( String args[] ) throws IOException {
	    ExecutorService executor = Executors.newFixedThreadPool(3);
	    
	    final int serverPort;
		if ( args.length > 0 ) {
			serverPort = Integer.valueOf(args[0]);
		} else {
			serverPort = 80;
		}
		
	    try ( ServerSocket ss = new ServerSocket( serverPort ) ) {
	    	new Thread(new Runnable() {
				@Override public void run() {
					// Поток работы сервера, принимающий входящие TCP-подключения на серверном порту:
					System.out.println( String.format("TinyHttpServer started on port %d...", serverPort) );
					
					while ( !Thread.interrupted() ) {
						try {
							executor.execute( new TinyHttpdConnection( ss.accept() ) );
						} catch ( SocketException e) {
							executor.shutdown();
							break;
						} catch ( IOException e ) {
							e.printStackTrace();
						} 
					}
					System.out.println( "Listening thread has terminated..." );
					
				}
	    	}).start();
	    	
	    	System.out.println( "Press Enter to stop the server..." );
			new BufferedReader( new InputStreamReader(System.in) ).readLine();
			System.out.println( "Stopping TinyHttpServer..." );
	    }
	}
}

class TinyHttpdConnection implements Runnable {
	Socket client;
	TinyHttpdConnection ( Socket client ) throws SocketException {
		this.client = client;
	}
	
	public void run() {
		try {
			// Поток обработки входящего запроса от клиента:
			System.out.println( String.format("%1$tT.%1$tL Got new http connection...", new java.util.Date()) );
			BufferedReader in = new BufferedReader( new InputStreamReader(client.getInputStream(), "8859_1" ) );
			OutputStream out = client.getOutputStream();
			PrintWriter pout = new PrintWriter( new OutputStreamWriter(client.getOutputStream(), "8859_1"), true );
			String request = in.readLine();
			System.out.println( String.format("%1$tT.%1$tL Request: \"%2$s\"", new java.util.Date(), request) );
			
			if ( request == null ) { return; }

			Matcher get = Pattern.compile("GET /?(\\S*).*").matcher( request );
			if ( get.matches() ) {
				request = get.group(1);
				if ( request.endsWith("/") || request.equals("") ) {
					request = request + "index.html";
				}
				if ( !request.startsWith("/") ) {
					request = "/" + request;
				}
				System.out.println( String.format("Requested resource: \"%s\"", request) );
				try ( InputStream is = TinyHttpServer.class.getResourceAsStream(request) ) {
					if ( is != null ) {
						byte [] data = new byte [ 64*1024 ];
						for(int read; (read = is.read( data )) > -1; ) {
							out.write( data, 0, read );
						}
						out.flush();
					} else {
						pout.println( "404 Object Not Found" );
					}
				}
			} else {
				pout.println( "400 Bad Request" );
			}
		} catch ( IOException e ) {
			System.out.println( "I/O error " + e ); 
		} finally {
			try { client.close(); } catch (IOException e) {}
			
			System.out.println( String.format("%1$tT.%1$tL http connection closed...", new java.util.Date()) );
		}
  }
}
