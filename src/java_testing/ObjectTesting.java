package java_testing;

public class ObjectTesting {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Sheep dolly = new Sheep("Dolly", 10);
		Sheep clone = dolly.clone ();
		
		System.out.print("dolly.toString(): "); System.out.println(dolly.toString());
		System.out.print("clone.toString(): "); System.out.println(clone.toString());
		System.out.println("dolly.mName.equals(clone.mName): " + dolly.mName.equals(clone.mName));
		System.out.println("dolly.mName == clone.mName: " + new Boolean (dolly.mName == clone.mName));
		System.out.println("dolly.hashCode(): " + dolly.hashCode());
		System.out.println("clone.hashCode(): " + clone.hashCode());
		System.out.println();
		
		dolly.mAges = 15;
		clone.mName = new String(dolly.mName);
		System.out.print("dolly.toString(): "); System.out.println(dolly.toString());
		System.out.print("clone.toString(): "); System.out.println(clone.toString());
		System.out.println("dolly.mName.equals(clone.mName): " + dolly.mName.equals(clone.mName));
		System.out.println("dolly.mName == clone.mName: " + new Boolean (dolly.mName == clone.mName));
		System.out.println("dolly.hashCode(): " + dolly.hashCode());
		System.out.println("clone.hashCode(): " + clone.hashCode());
		System.out.println();
	}

}

class Sheep implements Cloneable // Обязательно указываем, что объект является клонируемым! 
{
	String mName;
	int mAges;
	
	Sheep (String name) {
		mName = name;
	}
	
	Sheep (String name, int ages) {
		this(name);
		mAges = ages;
	}
	
	@Override
	public Sheep clone () { // Перезаписываем метод Object.clone() с увеличением видимости до public
		try {
			return (Sheep)super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return new Sheep("Err No #");
	}
	
	@Override
	public String toString () {
		return "I am sheep \"" + mName + "\", " + mAges + " years old";
	}
}
