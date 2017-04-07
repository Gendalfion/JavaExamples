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
 * Простой Web сервер, обрабатывающий HTTP GET-запросы и выдающий
 * запрашиваемые файлы по ресурсному пути относительно текущего classpath среды выполнения.
 * 
 * Отличается от сервера {@link java_api_testing.net_api.TinyHttpServer} тем, что
 * обрабатывает запросы в неблокирующем режиме при помощи пакета java.nio.*
 * 
 * Данный код написан на основе примера из книги:
 * Патрик Нимейер, Дэниэл Леук - Программирование на Java. Исчерпывающее руководство для профессионалов. (4-е издание)
 * 
 * Доработан для поддержки стандарта HTTP 1.1
 */
public class LargerHttpServer
{
	// Объект java.nio.channels.Selector используется в качестве хранилища
	// набора выборочных каналов и опций выборки. Он позволяет производить
	// выборку из набора зарегистрированных в нем каналов или ожидать
	// первого селекторного события на заданном наборе каналов.
	Selector mClientSelector;

	public void run( int serverPort, int threadCount ) throws IOException 
	{
		// Для создания объекта Selector используется статический метод Selector.open():
		try ( Selector clientSelector = Selector.open() ) {
			mClientSelector = clientSelector;
			
			// Создаем канал серверного сокета при помощи статического метода ServerSocketChannel.open():
			ServerSocketChannel ssc = ServerSocketChannel.open();
			
			// Переводим канал в неблокирующий режим:
			ssc.configureBlocking(false);
			
			// Биндим сокет на сетевой интерфейс 0.0.0.0 (что означает бинд серверного сокета на все доступные интерфейсы) 
			// и порт serverPort (по умолчанию ServerSocketChannel создается без привязки):
	        ssc.socket().bind( new InetSocketAddress( "0.0.0.0", serverPort ) );
	        
	        // Регистрируем канал в селекторе clientSelector с опцией выборки OP_ACCEPT
	        // (т. е. нас интересуют только события установки соединения к данному каналу):
			ssc.register( mClientSelector, SelectionKey.OP_ACCEPT );
		
			ExecutorService executor = Executors.newFixedThreadPool( threadCount );
	
			new Thread( new Runnable() {
				@Override public void run() {
					System.out.println( String.format("LargerHttpServer started on port %d (threads count = %d)...", serverPort, threadCount) );
					
					while ( !Thread.currentThread().isInterrupted() ) {
			            try {
			            	// Производим блокирующую выборку при помощи метода Selector.select(...) до тех пор, 
			            	// пока не появятся объекты, удовлетворяющие условиям выборки.
			            	// Мы специально указываем таймаут ожидания и затем снова вызываем select, если во время 
			            	// ожидания не возникло новых объектов. Это делается для того, чтобы обновить условия выборки,
			            	// если в селекторе произошли изменения за время работы метода select (например, в другом потоке
			            	// был добавлен новый селекторный канал):
			                while ( mClientSelector.select(100) == 0 );
			                
			                // Множество каналов, выбранных в методе Selector.select(...) возвращается
			                // методом Selector.selectedKeys():
			                Set<SelectionKey> readySet = mClientSelector.selectedKeys();
			                
			                try {
				                for(Iterator<SelectionKey> it = readySet.iterator(); it.hasNext();)
				                {
				                	// Объект SelectionKey создается автоматически при регистрации канала в селекторе,
				                	// он инкапсулирует сам канал и опции выборки для канала:
				                    final SelectionKey key = it.next();
				                    
				                    // Проверяем причину выбора канала:
				                    if ( key.isAcceptable() ) {
				                    	// Установлено подключение к серверному каналу ssc:
				                        acceptClient( ssc );
				                    } else {
				                    	// Выбран канал коммуникации с клиентом:
				                    	
				                    	// Перед тем, как запустить поток обработки клиентского соединения, снимаем
				                    	// опции выборки с канала при помощи метода SelectionKey.interestOps(0)
				                    	// (если этого не сделать, то последующий вызов select может снова выбрать
				                    	// данный канал и с одним каналом будет работать 2 потока одновременно):
				                        key.interestOps( 0 );
				                        
				                        // Запускаем поток обработки события в канале:
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
			                	// Мы должны сами следить за удалением элементов из выборки, т. к.
			                	// метод Selector.select(...) может только добавлять элементы в выборку Selector.selectedKeys():
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
		// Принимаем подключение на серверном сокете:
		SocketChannel clientSocket = ssc.accept();
		
		// Переводим сокет коммуникации с клиентом в неблокирующий режим:
		clientSocket.configureBlocking(false);
		
		// Регистрируем канал в селекторе mClientSelector (с опцией ожидания готовности к чтению из сокета):
		SelectionKey key =  clientSocket.register( mClientSelector, SelectionKey.OP_READ );
		
		HttpdConnection client = new HttpdConnection( clientSocket );
		// Объект SelectionKey может опционально хранить ссылку на один произвольный объект.
		// Добавляем созданный объект обслуживания HTTP-клиента к каналу clientSocket:
		key.attach( client );
	}

	void handleClient( SelectionKey key ) throws IOException
    {
		// Получаем ссылку на объект обслуживания HTTP-клиента:
		HttpdConnection client = (HttpdConnection)key.attachment();
		
		if ( key.isReadable() ) {
			// Обслуживаем готовность к чтению из сокета:
			client.read( key );
        } else {
        	// Обслуживаем готовность к записи в сокет:
			client.write( key );
        }
		
		// Метод Selector.wakeup() заставляет немедленно завершиться параллельно
		// выполняющийся select на данном селекторе.
		// Мы используем wakeup в качестве сигнала об изменениях
		// в опциях выборки (иначе изменения вступили бы в силу только
		// после штатного выполнения метода select):
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
 * Вспомогательный объект, обслуживающий запросы одного HTTP-клиента
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
		// Если достигнут конец потока, или получен символ перевода строки '\n',
		if ( (mRequest == null) && 
			 ( (mClientSocketChannel.read( mIOBuffer ) == -1) || (mIOBuffer.get( mIOBuffer.position()-1 ) == '\n') ) 
		   ) {
			// То: обрабатываем запрос от клиента:
			processRequest( key );
		} else {
			// Иначе: продолжаем чтение из сокета:
			key.interestOps( SelectionKey.OP_READ );
		}
	}

	@SuppressWarnings("resource")
	void processRequest( SelectionKey key ) {
		mIOBuffer.flip();
		// Декодируем байты из mIOBuffer с использованием кодировки CHARSET:
		mRequest = CHARSET.decode( mIOBuffer ).toString();
		
		if ( mRequest == null ) {
			return;
		}
		
		// Выделям шаблон GET из запроса:
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
				// Пробуем получить файловый канал из полученного имени ресурса mRequest:
				URL fileURL = HttpdConnection.class.getResource(mRequest);
				mFileChannel = new FileInputStream ( fileURL.getFile() ).getChannel();
				// Файл успешно открыт:
				mResponse = "HTTP/1.1 200 OK\r\n\r\n";
			} catch ( FileNotFoundException | NullPointerException e ) {
				// Файл не найден по указанному имени ресурса:
				mResponse = "404 Object Not Found";
				System.err.println( String.format("Resource \"%s\" not found!", mRequest) );
			}
		} else {
			// Запрос не поддерживается на сервере:
			mResponse = "400 Bad Request" ;
			System.err.println( String.format("Bad request: \"%s\"!", mRequest) );
		}

		if ( mResponse != null ) {
			// При наличии текстового ответа записываем его в mIOBuffer в кодировке CHARSET:
			mIOBuffer.clear();
			mIOBuffer.put( mResponse.getBytes(CHARSET) );
			mIOBuffer.flip();
		}
		// Запрос обработан, переключаем селекторный ключ в режим ожидания готовности к записи данных в сокет:
		key.interestOps( SelectionKey.OP_WRITE );
	}

	void write( SelectionKey key ) throws IOException {
		if ( mResponse != null ) {
			// Записываем байты из mIOBuffer в сокет:
			mClientSocketChannel.write( mIOBuffer );
			
			// Когда канал работает в неблокируемом режиме, операция записи отрабатывает мгновенно,
			// а количество записанных байт равняется количеству свободного места в выходном буфере сокета,
			// поэтому мы учитываем, что буфер mIOBuffer может быть отправлен в сокет не с первого раза:
			if ( mIOBuffer.remaining() == 0 ) {
				mResponse = null;
			}
		} else 
		// После отправки mResponse, проверяем нужно ли отправить еще данные из файлового канала mFileChannel:
		if ( mFileChannel != null ) {
			// Для отправки содержимого файла из канала mFileChannel в сокет mClientSocketChannel
			// мы используем метод FileChannel.transferTo (...), который наиболее эффективным образом 
			// (минуя память JVM) перенаправляет данные от файлового канала в канал назначения:
			int remaining = (int)mFileChannel.size() - mCurrentFilePosition;
			long sent = mFileChannel.transferTo( mCurrentFilePosition, remaining, mClientSocketChannel);
			
			// При отправке мы учитываем, что файл может отправляться порциями, т. к. 
			// mClientSocketChannel работает в неблокируемом режиме, что заставляет операцию
			// transferTo(...) отрабатывать мгновенно и возвращать в качестве результата
			// количество байт свободных в выходном буфере сокета:
			if ( (sent >= remaining) || (remaining <= 0) ) {
				// Файл полностью перекачан в mClientSocketChannel, закрываем файловый канал:
				mFileChannel.close();
				mFileChannel = null;
			} else {
				// Запоминаем позицию в файле, чтобы продолжить с нее отправку во время следующего
				// события готовности сокета к отправке данных:
				mCurrentFilePosition += sent;
			}
		} 
		
		if ( (mResponse == null) && (mFileChannel == null) ) {
			// Все данные отправлены, закрываем клиентский сокет:
			mClientSocketChannel.close();
			
			// Удаляем канал из селектора вызовом метода SelectionKey.cancel():
			key.cancel();		
		} else {
			// Остались данные для отправки, продолжаем ожидать события готовности к запси в сокет:
			key.interestOps( SelectionKey.OP_WRITE );
		}
	}
}
