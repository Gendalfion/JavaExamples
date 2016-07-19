package java_testing;

public class VarOverrideTesting {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DoubleCalculator dc = new DoubleCalculator();
		IntegerCalculator ic_ref = dc;
		//IntegerCalculator ic_ref = new IntegerCalculator();
		
		System.out.printf("DoubleCalculator.GetVal()  = %.3f\n", dc.GetVal());
		System.out.printf("IntegerCalculator.GetVal() = %.3f\n", ic_ref.GetVal());
		System.out.printf("DoubleCalculator.mVal  = %.3f\n", dc.mVal);
		System.out.printf("IntegerCalculator.mVal = %d\n", ic_ref.mVal);
		
		ic_ref.Divide();
		System.out.println("\nAfter call ic_ref.Divide():");
		System.out.printf("DoubleCalculator.GetVal()  = %.3f\n", dc.GetVal());
		System.out.printf("IntegerCalculator.GetVal() = %.3f\n", ic_ref.GetVal());
		System.out.printf("DoubleCalculator.mVal  = %.3f\n", dc.mVal);
		System.out.printf("IntegerCalculator.mVal = %d\n", ic_ref.mVal);
		
		((IntegerCalculator)dc).Divide();
		System.out.println("\nAfter call ((IntegerCalculator)dc).Divide():");
		System.out.printf("DoubleCalculator.GetVal()  = %.3f\n", dc.GetVal());
		System.out.printf("IntegerCalculator.GetVal() = %.3f\n", ic_ref.GetVal());
		System.out.printf("DoubleCalculator.mVal  = %.3f\n", dc.mVal);
		System.out.printf("IntegerCalculator.mVal = %d\n", ic_ref.mVal);
		
		ic_ref.SetVal((double)2.3);
		System.out.println("\nAfter call ic_ref.SetVal((double)2.3):");
		System.out.printf("DoubleCalculator.GetVal()  = %.3f\n", dc.GetVal());
		System.out.printf("IntegerCalculator.GetVal() = %.3f\n", ic_ref.GetVal());
		System.out.printf("DoubleCalculator.mVal  = %.3f\n", dc.mVal);
		System.out.printf("IntegerCalculator.mVal = %d\n", ic_ref.mVal);
		
		ic_ref.SetVal("14.5");
		System.out.println("\nAfter call ic_ref.SetVal(\"14.5\"):");
		System.out.printf("DoubleCalculator.GetVal()  = %.3f\n", dc.GetVal());
		System.out.printf("IntegerCalculator.GetVal() = %.3f\n", ic_ref.GetVal());
		System.out.printf("DoubleCalculator.mVal  = %.3f\n", dc.mVal);
		System.out.printf("IntegerCalculator.mVal = %d\n", ic_ref.mVal);

	}

}

class IntegerCalculator {
	int mVal = 10;
	
	public IntegerCalculator () {
	}
	
	public void Divide () { this.mVal /= 3; }
	
	public double GetVal () { return this.mVal; }
	
	public void SetVal (double val) { mVal = (int)val; }
	
	public void SetVal (String val) { mVal = Integer.parseInt(val); }
}

class DoubleCalculator extends IntegerCalculator {
	double mVal = 20.0;
	
	public DoubleCalculator () {
	}
	
	@Override
	public void Divide () { this.mVal /= 1.3; }
	
	@Override
	public double GetVal () { return this.mVal; }
	
	@Override public void SetVal (String val) { mVal = Double.parseDouble(val); }
}