package java_api_testing.swing.from_book.ch18;

public class DialEvent extends java.util.EventObject {
	private static final long serialVersionUID = 1L;
	
	int value;
	
	public DialEvent(Dial source, int value) {
		super(source);
		this.value = value;
	}
	
	public int getValue () {
		return value;
	}

}
