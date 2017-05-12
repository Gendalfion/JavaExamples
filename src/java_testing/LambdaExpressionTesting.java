package java_testing;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * � ������ ������� ��������������� ������ � ������-�����������, ������������ � Java 8
 * (<a href="http://winterbe.com/posts/2014/03/16/java-8-tutorial/">�������� ������</a>) 
 */
public class LambdaExpressionTesting {
	
	public LambdaExpressionTesting () {
		JFrame myFrame = new JFrame ( "Lambda Expression Testing" );
		myFrame.setSize(700, 800);
		myFrame.setLocationRelativeTo(null);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextPane contentPane = new JTextPane();
		contentPane.setContentType("text/html");
		contentPane.setText(buildExample());
		contentPane.setEditable(false);
		myFrame.getContentPane().add( new JScrollPane(contentPane) );
		
		myFrame.setVisible(true);
	}
	
	private String buildExample () {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try ( PrintWriter out = new PrintWriter(outStream) ) {
			out.println("<html><body>");
			
			out.println("<h2 align=\"center\">Lambda expressions <span style=\"font-weight:normal;\">(see Java code to get comments...)</span></h2>");
			
			// �������� ������� ������ ����� � �������� ��� � ��������� java.util.List:
			List<String> list_1 = Arrays.asList("ddd1", "aaa2", "aaa1", "ccc3", "bbb2", "bbb1");
			
			// � ������� �������� ����� ������� names_1:
			List<String> list_2 = new ArrayList<>(list_1);
			List<String> list_3 = new ArrayList<>(list_1);
			
			// ���������� ���������� ������ names_1 (������������������):
			out.println("<h3>We have List&lt;String&gt; (copied to list_1, list_2 and list_3):</h3>");
			out.println("<table><tr>");
			for ( String s : list_1 ) { out.format("<td>%s</td>", s ); }
			out.println("</tr></table>");
			
			// �������� ���������� ������ names_1 � "����������" ������ (�� Java 8):
			Collections.sort(list_1, new Comparator<String>() {
			    @Override
			    public int compare(String a, String b) {
			        return a.compareTo(b);
			    }
			});
			
			// �������� ���������� ������ names_2 � �������������� ������-���������:
			Collections.sort( list_2, (String a, String b) -> { return a.compareTo(b); } );
			// ������ ��������� �������� �������� �������� ��������� ������� ���
			// �����������, ���������� ���� ����������� ������� (�������������� ����������),
			// �� ���������� ��������� (args) -> { ... return <Result>; },
			// ��� args ��� ��������� ��������� ������� ����������, �������� ��� Comparator - ��� compare(String, String);
			// � ����� ��������� �������� {...} ���������� ��� ������ ����������������� ����������.
			// ������������� ��, ��� ���������� ���������� ������-��������� ��������� �� ������� 
			// ��������� ������� (��������� ������������� .class-������ ������� ���� *$1.class, *$2.class, ...)
			
			// �� ����� �������� ���� ����������, �������� ����� return � ������ {} (���� ������ ��� ������������ ������������ ���������):
			Collections.sort(list_3, (a, b) -> a.compareTo(b) );
			
			out.println("<h3>List copies after sorting:</h3>");
			out.println("<table>");
			/* 
			// ������ �� ������ � ������ ������:
			for ( String s : list_1 ) {
				out.format("<td>%s</td>", s );
			}
			
			// ������������� ������-���������:
			list_1.forEach( (s) -> out.format("<td>%s</td>", s); // ����� Iterable.forEach ��������� �������������� ��������� Consumer<T>
																 // � ������� �������� ���������
			*/
			
			// ������� ���������� ������� � ���� ������� � �������������� ������-���������:
			out.println("<tr><td>list_1: </td>"); list_1.forEach( (s) -> out.format("<td>%s</td>", s) );
			out.println("<tr><td>list_2: </td>"); list_2.forEach( (s) -> out.format("<td>%s</td>", s) );
			out.println("<tr><td>list_3: </td>"); list_3.forEach( (s) -> out.format("<td>%s</td>", s) );
			
			out.println("</table>");
			
			// �������� :: ������������ ��� �������� �������������� "������" �� ����������� ����� ������:
			list_3.forEach( LambdaExpressionTesting::staticMethod );
			/*
			// ������������� ������-���������:
			list_3.forEach( (s) -> LambdaExpressionTesting.staticMethod(s) );
			 
			// ������������� ��� ��� ������������� ������-���������:
			list_3.forEach( new Consumer<String>() {
				@Override public void accept(String s) {
					LambdaExpressionTesting.staticMethod(s);
				}
			} );
			*/
			
			// ������� ������� �� ������� �������������� ������ �� ����� ���������� ������:
			list_3.forEach( this::nonstaticMethod );
			// ������������� ������-���������:
			//list_3.forEach( (s) -> this.nonstaticMethod(s) );
			
			// ����� ��������� :: ������������ ��� �������� �������������� ������ �� ����������� �������:
			NumberFactory<?> intFactory 	= Integer::new;
			// ������������� ������-���������:
			// NumberFactory<?> intFactory 	= (String str) -> { return new Integer (str); };
			NumberFactory<?> doubleFactory 	= Double::new;
			// ������������� ������-���������:
		    // NumberFactory<?> doubleFactory 	= (String str) -> { return new Double (str); };
			
			out.println("<h3>Functional constructor reference:</h3>"); 
			out.println("<p>intFactory.create(\"11\") = " + intFactory.create("11") + "</p>" );
			out.println("<p>doubleFactory.create(\"11.510\") = " + doubleFactory.create("11.510") + "</p>" );
			
			out.println("<h3>Using java.util.function.Function<T, R>:</h3>");
			// ����� ����� java.util.function.* ��������� � Java API ��� �������� �������������� �����������:
			// ��������� Function<T, R>, ��������, ������������� �����  R apply(T) 
			// (��������������� �������� ���� T � �������� ���� R):   
			Function<String, String> convert = (s) -> s.toUpperCase(); 
			// ����� ����, �� ����� ������������� ������� ����� ����� ��� ������ ��������� ������� ���������� Function:
			convert = convert.andThen( (s) -> s.substring(0, 1) + Character.toLowerCase(s.charAt(1)) + s.substring(2) );
			out.println("<p>convert.apply(\"hello\") = " + convert.apply("hello") + "</p>" );
			
			out.println("<h3>Using Collection.stream() and Collection.parallelStream():</h3>");
			out.println("<table>");
			// Java 8 ��������� ������ stream � parallelStream � ���� Collection:
			out.println("<tr><td>stream conversion: </td>");
			list_1.stream() // ������ ����� ��������� ����������� ���������� �������� ��� �������������� ���������:
				  .filter	( (s) -> s.startsWith("a") ) // �������� ��� ��������, ������������ �� ������ "a"
				  .map		( convert )					 // ����������� stream ��� ������ ������� convert
				  .forEach	( (s) -> out.format("<td>%s</td>", s) ); // �������� ���������� stream
			
			// ������� parallelStream ���������� ��������������� ��� ���������� �������� ��� ����� ����������,
			// ������� ��� ������� ��������� �� ����� �������� ������� � ������������������ ��� ������������� parallelStream:
			out.println("<tr><td>parallelStream conversion: </td>");
			list_2.parallelStream()
				  .filter	( (s) -> s.startsWith("b") ) // �������� ��� ��������, ������������ �� ������ "b"
				  .map		( convert )					 // ����������� parallelStream ��� ������ ������� convert
				  .forEach	( (s) -> out.format("<td>%s</td>", s) ); // �������� ���������� parallelStream
			
			// ��� �������� ������ �������� stream ����������� ��� �������������� ��������� ���������,
			// � ������������� ����� �� ������ �� �������� ������ �������� ���������:
			out.println("<tr><td>list_1: </td>"); list_1.forEach( (s) -> out.format("<td>%s</td>", s) );
			out.println("<tr><td>list_2: </td>"); list_2.forEach( (s) -> out.format("<td>%s</td>", s) );
			
			out.println("</table>");
			
			out.println("</body></html>");
		} 
		return outStream.toString();
	}
	
	private static <T> void staticMethod ( T to_print ) {
		System.out.println( "staticPrint: " + to_print );
	}
	
	private <T> void nonstaticMethod ( T to_print ) {
		System.out.println( "nonstaticPrint: " + to_print );
	}
	
	// ������������� ������������ ��������� @FunctionalInterface ��� ���� �������������� ����������� (���� ��� �� �����������):
	@FunctionalInterface // ������ ��������� ��������� ����������� ��������� �������������� ��������� �� ����������
	public static interface NumberFactory<N extends Number> {
		// ������ �������������� ��������� ������ ����� ���� ����������� �����:
		N create ( String number ) throws NumberFormatException;
		
		// ��� ���� �� ����� ����� ������������ ���������� ��������� �������:
		default String getFactoryName () { return "DefaultName"; }
	}

	public static void main(String[] args) {
		new LambdaExpressionTesting();
	}
}
