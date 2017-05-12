package java_api_testing.java2D.from_book.ch20;

//file: ShowFonts.java
import java.awt.*;

public class ShowFonts {
	public static void main(String[] args) {
		// Выводим все шрифты, доступные в текущей системе:
		Font[] fonts;
		fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		final String format = "%03d: %30.100s : %-30.100s : %s\n";
		for (int i = 0; i < fonts.length; i++) {
			System.out.printf(format, i, fonts[i].getFontName(), fonts[i].getFamily(), fonts[i].getName());
		}
	}
}
