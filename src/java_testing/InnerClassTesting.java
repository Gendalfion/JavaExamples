package java_testing;

public class InnerClassTesting {

	public static void main(String[] args) {
		Monkey bill = new Monkey("Bill");
		Monkey steven = new Monkey("Steven");
		
		System.out.print( "System.out.println(bill): " ); System.out.println( bill );
		System.out.print( "System.out.println(steven): " ); System.out.println( steven );
		System.out.println();
		
		Monkey.Brain monkey_brain_1 = bill.createBrain();
		Monkey.Brain monkey_brain_2 = steven.createBrain();
		Monkey.Brain monkey_brain_3 = bill.new Brain (); // ����� Brain ������ �������� � ���������� ������ Monkey!
		
		
		System.out.println( "monkey_brain_1.tellYourNameMonkey() = "
				+ monkey_brain_1.tellYourNameMonkey());
		System.out.println( "monkey_brain_2.tellYourNameMonkey() = "
				+ monkey_brain_2.tellYourNameMonkey());
		System.out.println( "monkey_brain_3.tellYourNameMonkey() = "
				+ monkey_brain_3.tellYourNameMonkey());
		System.out.println();
		
		// ����������� ���������� ����� ����� �� ������ � ��������� ������ �������� ������:
		Monkey.StaticBrain static_brain_1 = new Monkey.StaticBrain(steven);
		Monkey.StaticBrain static_brain_2 = new Monkey.StaticBrain();
		
		System.out.println( "static_brain_1.tellYourNameMonkey() = "
				+ static_brain_1.tellYourNameMonkey());
		System.out.println( "static_brain_2.tellYourNameMonkey() = "
				+ static_brain_2.tellYourNameMonkey());
	}

}

class Monkey  {
	String mName;
	
	Monkey (String name) {
		mName = name;
	}
	
	class Brain {
		String tellYourNameMonkey () {
			return mName;
		}
	}
	
	static class StaticBrain {
		Monkey mMonkey = new Monkey("Unnamed");
		
		StaticBrain () {}
		
		StaticBrain (Monkey monkey) {
			mMonkey = monkey;
		}
		
		String tellYourNameMonkey () {
			return mMonkey.createBrain().tellYourNameMonkey();
		}
	}
	
	Brain createBrain () {
		return new Brain();
	}
}
