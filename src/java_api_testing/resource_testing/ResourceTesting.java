package java_api_testing.resource_testing;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Formatter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ƒемонстраци€ работы с технологией загрузки ресурсов на платформе Java
 * @author Lab119Alex
 *
 */
public class ResourceTesting {
	
	public ResourceTesting () {
		JFrame myFrame = new JFrame ( "Java Resource API Testing" );
		myFrame.setSize(800, 900);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel main_panel = new JPanel (new BorderLayout());
		
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		main_panel.add(center_panel, BorderLayout.CENTER);
		
		//  аждый class-объект в Java имеет статический метод загрузки ресурсов
		// ¬ качестве параметра он принимает строку с абсолютным (начинаетс€ с "/") или относительным путем
		// –еальное положение ресурса определ€етс€ загрузчиком класса и зависит от того, как упакован класс
		// (т. е. это может быть удаленный веб-сервер, jar-архив, или просто место в файловой системе на конкретной платформе)
		
		// ѕолучаем ресурс по пути, относительно класса java_api_testing.resource_testing.ResourceTesting: 
		URL text_resource_URL = ResourceTesting.class.getResource("TextResource.txt");
		
		// ѕолучаем ресурс по абсолютному пути (данный путь определ€етс€ относительно переменной classpath дл€ текущей среды выполнени€ java)
		URL image_resource_URL = Class.class.getResource("/java_api_testing/resource_testing/MyResources/ImageResource.png"); 
		
		JTextArea text_resource_area = new JTextArea();
		if ( text_resource_URL != null ) {
			text_resource_area.setBorder(BorderFactory.createTitledBorder(text_resource_URL.getFile() + ":"));
			text_resource_area.setText(loadTextResource(text_resource_URL));
		} else {
			text_resource_area.setText("Can not load text resource \"TextResource.txt\"");
		}
		text_resource_area.setEditable(false);
		center_panel.add(new JScrollPane(text_resource_area));
		
		JPanel image_res_panel = new JPanel();
		if ( image_resource_URL != null ) {
			image_res_panel.setBorder( BorderFactory.createTitledBorder(image_resource_URL.getFile() + ":") );
			image_res_panel.add(loadImageResource(image_resource_URL));
		} else {
			image_res_panel.setBorder(BorderFactory.createEtchedBorder());
			image_res_panel.add(new JLabel("Can not load image resource \"/java_api_testing/resource_testing/MyResources/ImageResource.png\""));
		}
		center_panel.add (image_res_panel);
		
		myFrame.add(main_panel);
		myFrame.setVisible(true);
	}
	
	public String loadTextResource ( URL resource_URL ) {
		// «агружаем текстовый ресурс по указанному URL (кодировку используем по-умолчанию):
		try ( 	BufferedReader text_reader = new BufferedReader( new InputStreamReader(resource_URL.openStream())); 
				Formatter fmt = new Formatter() 
			) 
		{
			String line;
			while ( (line = text_reader.readLine()) != null ) {
				fmt.format("%s\n", line);
			}
			return fmt.toString();
		} catch (IOException e) {
			return e.getClass().getName() + " : " + e.getMessage();
		}
	}
	
	public JLabel loadImageResource ( URL resource_URL ) {
		// «агружаем изображение по указанному URL:
		try {
			BufferedImage buffImage = ImageIO.read(new File(resource_URL.toURI()));
			if ( buffImage == null ) {
				return new JLabel("Failed to load image: \"" + resource_URL.getPath() + "\"");
			}
			return new JLabel(new ImageIcon(buffImage));
		} catch ( Exception e ) { 
			return new JLabel(e.getClass().getName() + " : " + e.getMessage());
		}
	}	

	public static void main(String[] args) {
		new ResourceTesting();
	}
}
