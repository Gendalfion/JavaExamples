package java_api_testing.rmi;

/**
 * Удаленный интерфейс сервера, использующий технологию Java RMI.
 * Используется в классах:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 */
public interface ServerRemote extends java.rmi.Remote {
	/**
	 * Асинхронный запуск выполнения запроса на сервере
	 * @param request - запрос, который будет выполнен на сервере
	 * @param listener - удаленный callback-интерфейс, предоставляемый клиентом
	 * @throws java.rmi.RemoteException Возникает при возникновении проблем с вызовом удаленного метода
	 */
	public void asyncExecute ( Request request, RequestListener listener ) 
			throws java.rmi.RemoteException; // исключение RemoteException определяет метод удаленного вызова
	
	/**
	 * Пример процедуры, возвращающей объект javax.swing.JFrame, создаваемый на сервере
	 * @param frame_title - заголовок создаваемого объекта
	 * @return Возвращает созданный объект
	 * @throws java.rmi.RemoteException Возникает при возникновении проблем с вызовом удаленного метода 
	 */
	public javax.swing.JFrame createFrame ( String frame_title )
			throws java.rmi.RemoteException; // исключение RemoteException определяет метод удаленного вызова
}
