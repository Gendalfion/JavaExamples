package java_api_testing;

import java.io.IOException;
import java.io.Serializable;

// ������������ - ��� �������������� ������ ���������� � �������� ��������� �������� � Java
// ��� �������� ����, ��� ������ ����� ���� ��������������, �� ������ ����������� ��������� java.io.Serializable
// �� ��������� ��������� �������������� ������������ ���������� �������� ���� ����� �������
// ����� ����������� � ���������� �������� ������ transient
public class SerializableObject implements Serializable {
	
	// ������������� serialVersionUID ������������ ��� �������� ������ ��������������� ��������
	// ���� ������ ���������������� ������� ���������� �� �������, �� ������������ ���������� 
	// java.io.InvalidClassException ��� ������� ��������������� ����������� ������.
	// �������������� ��� ������������� serialVersionUID ������ �������� ������ ���
	// ������������� ������������ ���������������� ����� ������� ������� ������ � ��� ������������������ ���������
	private static final long serialVersionUID = 2L;
	
	// ���� ���������������� ������� ������ ���� ���� �������� ������, ����
	// ���������, ������������ ��������� Serializable:
	public String mSerializableString = "I'll gonna be saved...";
	
	// ����, ���������� ��� transient, �� ����� ����������������� ����������� ���������� ������������:
	public transient String mTransientString = "I'm not in serialization...";
	public transient int mLowLevelInt = 0;
	
	// ��� ����, ����� �������������� ����������� ����� ������������ ��� �������,
	// ���������� ���������� ������ ������� ����� �� ��������� ����������:
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// ����� ObjectOutputStream.defaultWriteObject() �������� ����������� ��������� ������������ ��� �������� �������
		// (������ ����� ����� ���� ������ ������ ������ ������ writeObject!)
		out.defaultWriteObject();
		
		// �������� �������� �������������� ������ ������ ����� � ������� � ������������ ������ ����������� �������:
		out.writeInt(mLowLevelInt);
	}
	
	// ��� ����, ����� �������������� ����������� ����� �������������� �������,
	// ���������� ���������� ������ ������� ����� �� ��������� ����������:
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		// ����� ObjectInputStream.defaultReadObject() �������� ����������� ��������� �������������� �������� ������� �� ������
		// (������ ����� ����� ���� ������ ������ ������ ������ readObject!)
		in.defaultReadObject();
		
		// ���������� Int-�������� �� ������ ��� ������ �������������� �������� readInt():
		mLowLevelInt = in.readInt();
	}
	
}
