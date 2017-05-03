package netbeans_testing;

import magicbeans.sunw.demo.juggler.Juggler;

import java.io.*;

/**
 * Демонстрация сериализации JavaBeans-компонента
 * 
 * <p> Данный класс работает в паре с классом: {@link JugglerDeserialize}
 * @author Lab119Alex
 *
 */
public class JugglerSerialize {
	public static void main( String [] args ) throws Exception
	{
            Juggler duke = new Juggler();
            duke.setAnimationDelay(1000);
            duke.setJuggling(true);
            // Сериализуем объект Juggler в файл *.ser по указанному пути:
            try ( ObjectOutputStream oout = new ObjectOutputStream(
                    new FileOutputStream( "src/netbeans_testing/juggler.ser" ) ) ) {
                oout.writeObject( duke );
            }
            System.exit(0);
	}
}
