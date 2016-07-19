package java_testing;

public class CovariantTypeTesting {

	public static void main(String[] args) {
		Animal cow = new Herbivore();
		
		try {
			Food food = cow.Eat(new Grass());
			System.out.println( "cow.Eat(new Grass()) returned " + food.toString() );
			
			food = cow.Eat(new Meat());
			System.out.println( "cow.Eat(new Meat()) returned " + food.toString() );
		} catch ( MeatInedibleException ex ) {
			System.out.println("MeatInedibleException catch: " + ex.getMessage());
		} catch ( InedibleException ex ) {
			System.out.println("InedibleException catch: " + ex.getMessage());
		}
	}

}

class InedibleException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8448598804727290778L;

	public InedibleException () {
		super ();
	}
	
	public InedibleException (String msg)	{
		super (msg);
	}
}

class MeatInedibleException extends InedibleException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3475182704881591619L;

	public MeatInedibleException () {
		super ();
	}
	
	public MeatInedibleException (String msg)	{
		super (msg);
	}
}

abstract class Food {
}

class Meat extends Food {
	@Override
	public String toString() {
		return "Meat";
	}
}

class Grass extends Food {
	@Override
	public String toString() {
		return "Grass";
	}
}

class EatenGrass extends Food {
	@Override
	public String toString() {
		return "EatenGrass";
	}
}

abstract class Animal {
	Food Eat ( Food food ) throws InedibleException {
		return food;
	}
}

class Herbivore extends Animal {
	@Override
	EatenGrass Eat(Food food)  throws MeatInedibleException {
		if ( !(food instanceof Grass) ) {
			throw new MeatInedibleException (food.toString() + " is not a Grass!");
		}
		return new EatenGrass ();
	}
}
