package netbeans_testing;

import magicbeans.sunw.demo.juggler.Juggler;

import java.io.*;

/**
 * ������������ ������������ JavaBeans-����������
 * 
 * <p> ������ ����� �������� � ���� � �������: {@link JugglerDeserialize}
 * @author Lab119Alex
 *
 */
public class JugglerSerialize {
	public static void main( String [] args ) throws Exception
	{
            Juggler duke = new Juggler();
            duke.setAnimationDelay(1000);
            duke.setJuggling(true);
            // ����������� ������ Juggler � ���� *.ser �� ���������� ����:
            try ( ObjectOutputStream oout = new ObjectOutputStream(
                    new FileOutputStream( "src/netbeans_testing/juggler.ser" ) ) ) {
                oout.writeObject( duke );
            }
            System.exit(0);
	}
}
