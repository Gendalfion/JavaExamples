package java_api_testing;

import java.io.IOException;
import java.io.Serializable;

// Сериализация - это автоматический способ сохранения и загрузки состояния объектов в Java
// Для указания того, что объект может быть сериализирован, он должен наследовать интерфейс java.io.Serializable
// По умолчанию процедура автоматической сериализации записывает значения всех полей объекта
// кроме статических и помеченных ключевым словом transient
public class SerializableObject implements Serializable {
	
	// Идентификатор serialVersionUID используется для контроля версий сериализованных объектов
	// Если версия сериализованного объекта отличается от текущей, то генерируется исключение 
	// java.io.InvalidClassException при попытке десериализовать сохраненный объект.
	// Предполагается что идентификатор serialVersionUID должен меняться только при
	// возникновении неразрешимых несовместимостей между текущей версией класса и его сериализированными объектами
	private static final long serialVersionUID = 2L;
	
	// Поля сериализируемого объекта должны быть либо простыми типами, либо
	// объектами, реализующими интерфейс Serializable:
	public String mSerializableString = "I'll gonna be saved...";
	
	// Поля, помеченные как transient, НЕ будут сериализироваться стандартной процедурой сериализации:
	public transient String mTransientString = "I'm not in serialization...";
	public transient int mLowLevelInt = 0;
	
	// Для того, чтобы переопределить стандартный метод сериализации для объекта,
	// необходимо определить внутри объекта метод со следующей сигнатурой:
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// Метод ObjectOutputStream.defaultWriteObject() вызывает стандартную процедуру сериализации для текущего объекта
		// (данный метод может быть вызван только внутри метода writeObject!)
		out.defaultWriteObject();
		
		// Вызываем операцию низкоуровневой записи целого числа в добавок к стандартному методу сериалиации объекта:
		out.writeInt(mLowLevelInt);
	}
	
	// Для того, чтобы переопределить стандартный метод десериализации объекта,
	// необходимо определить внутри объекта метод со следующей сигнатурой:
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		// Метод ObjectInputStream.defaultReadObject() вызывает стандартную процедуру десериализации текущего объекта из потока
		// (данный метод может быть вызван только внутри метода readObject!)
		in.defaultReadObject();
		
		// Вычитываем Int-значение из потока при помощи низкоуровневой операции readInt():
		mLowLevelInt = in.readInt();
	}
	
}
