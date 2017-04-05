package java_api_testing.rmi;

/**
 * Удаленный интерфейс, используемый для сообщения выполнения операции сервером.
 * Используется в классах:
 * {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * {@link java_api_testing.rmi.client.RMIClientTesting}
 * 
 * @author Lab119Alex
 */
public interface RequestListener extends java.rmi.Remote {
	/**
	 * Callback процедура, сообщающая об окончании выполнения операции
	 * @param request - запрос, результатом которого послужил вызов данного метода
	 * @param result - результат выполнения операции
	 * @throws java.rmi.RemoteException Возникает при ошибках сетевого взаимодействия с клиентом
	 */
	public void requestProcessed ( Request request, Object result )
		throws java.rmi.RemoteException; // исключение RemoteException определяет метод удаленного вызова
}
