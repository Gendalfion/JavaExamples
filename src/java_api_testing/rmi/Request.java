package java_api_testing.rmi;

import java.io.Serializable;

/**
 * Базовый интерфейс запроса для сервера {@link java_api_testing.rmi.server.ServerRemoteImpl}
 * 
 * @author Lab119Alex
 *
 */
public interface Request extends Serializable {
	/**
	 * Функция, выполняющая запрос
	 * @return Возвращает результат выполнения запроса в виде объекта, который подлежит сериализации и передаче клиенту
	 */
	public Serializable processRequest ();
}
