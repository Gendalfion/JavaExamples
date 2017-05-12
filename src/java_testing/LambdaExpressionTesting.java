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
 * В данном примере демонстрируется работа с лямбда-выражениями, добавленными в Java 8
 * (<a href="http://winterbe.com/posts/2014/03/16/java-8-tutorial/">Исходная статья</a>) 
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
			
			// Создадим простой массив строк и поместим его в интерфейс java.util.List:
			List<String> list_1 = Arrays.asList("ddd1", "aaa2", "aaa1", "ccc3", "bbb2", "bbb1");
			
			// В добавок создадим копии массива names_1:
			List<String> list_2 = new ArrayList<>(list_1);
			List<String> list_3 = new ArrayList<>(list_1);
			
			// Напечатаем содержимое списка names_1 (неотсортированного):
			out.println("<h3>We have List&lt;String&gt; (copied to list_1, list_2 and list_3):</h3>");
			out.println("<table><tr>");
			for ( String s : list_1 ) { out.format("<td>%s</td>", s ); }
			out.println("</tr></table>");
			
			// Выполним сортировку списка names_1 в "устаревшей" манере (до Java 8):
			Collections.sort(list_1, new Comparator<String>() {
			    @Override
			    public int compare(String a, String b) {
			        return a.compareTo(b);
			    }
			});
			
			// Выполним сортировку списка names_2 с использованием лямбда-выражений:
			Collections.sort( list_2, (String a, String b) -> { return a.compareTo(b); } );
			// Лямбда выражения помогают заменять создание анонимных классов для
			// интерфейсов, содержащих одну абстрактную функцию (функциональные интерфейсы),
			// на лаконичное выражение (args) -> { ... return <Result>; },
			// где args это локальные параметры функции интерфейса, например для Comparator - это compare(String, String);
			// а между фигурными скобками {...} содержится код метода имплементируемого интерфейса.
			// Примечательно то, что внутренняя реализация лямбда-выражений физически не создает 
			// анонимных классов (уменьшает замусоривание .class-файлов файлами вида *$1.class, *$2.class, ...)
			
			// Мы можем опускать типы параметров, ключевое слово return и скобки {} (если внутри них используется единственное выражение):
			Collections.sort(list_3, (a, b) -> a.compareTo(b) );
			
			out.println("<h3>List copies after sorting:</h3>");
			out.println("<table>");
			/* 
			// Проход по списку в старой манере:
			for ( String s : list_1 ) {
				out.format("<td>%s</td>", s );
			}
			
			// Эквивалентное лямбда-выражение:
			list_1.forEach( (s) -> out.format("<td>%s</td>", s); // Метод Iterable.forEach применяет функциональный интерфейс Consumer<T>
																 // к каждому элементу коллекции
			*/
			
			// Выведем содержимое списков в виде таблицы с использованием лямбда-выражений:
			out.println("<tr><td>list_1: </td>"); list_1.forEach( (s) -> out.format("<td>%s</td>", s) );
			out.println("<tr><td>list_2: </td>"); list_2.forEach( (s) -> out.format("<td>%s</td>", s) );
			out.println("<tr><td>list_3: </td>"); list_3.forEach( (s) -> out.format("<td>%s</td>", s) );
			
			out.println("</table>");
			
			// Оператор :: используется для создания функциональной "ссылки" на статический метод класса:
			list_3.forEach( LambdaExpressionTesting::staticMethod );
			/*
			// Эквивалентное лямбда-выражение:
			list_3.forEach( (s) -> LambdaExpressionTesting.staticMethod(s) );
			 
			// Эквивалентный код без использования лямбда-выражений:
			list_3.forEach( new Consumer<String>() {
				@Override public void accept(String s) {
					LambdaExpressionTesting.staticMethod(s);
				}
			} );
			*/
			
			// Похожим образом мы создаем функциональную ссылку на метод экземпляра класса:
			list_3.forEach( this::nonstaticMethod );
			// Эквивалентное лямбда-выражение:
			//list_3.forEach( (s) -> this.nonstaticMethod(s) );
			
			// Также опрератор :: используется для создания функциональной ссылки на конструктор объекта:
			NumberFactory<?> intFactory 	= Integer::new;
			// Эквивалентное лямбда-выражение:
			// NumberFactory<?> intFactory 	= (String str) -> { return new Integer (str); };
			NumberFactory<?> doubleFactory 	= Double::new;
			// Эквивалентное лямбда-выражение:
		    // NumberFactory<?> doubleFactory 	= (String str) -> { return new Double (str); };
			
			out.println("<h3>Functional constructor reference:</h3>"); 
			out.println("<p>intFactory.create(\"11\") = " + intFactory.create("11") + "</p>" );
			out.println("<p>doubleFactory.create(\"11.510\") = " + doubleFactory.create("11.510") + "</p>" );
			
			out.println("<h3>Using java.util.function.Function<T, R>:</h3>");
			// Новый пакет java.util.function.* добавляет в Java API ряд полезных функциональных интерфейсов:
			// Интерфейс Function<T, R>, например, предоставляет метод  R apply(T) 
			// (преобразовывает аргумент типа T в аргумент типа R):   
			Function<String, String> convert = (s) -> s.toUpperCase(); 
			// кроме того, мы можем комбинировать функции между собой при помощи дефолтных методов интерфейса Function:
			convert = convert.andThen( (s) -> s.substring(0, 1) + Character.toLowerCase(s.charAt(1)) + s.substring(2) );
			out.println("<p>convert.apply(\"hello\") = " + convert.apply("hello") + "</p>" );
			
			out.println("<h3>Using Collection.stream() and Collection.parallelStream():</h3>");
			out.println("<table>");
			// Java 8 добавляет методы stream и parallelStream к типу Collection:
			out.println("<tr><td>stream conversion: </td>");
			list_1.stream() // Данный метод позволяет производить конвеерные операции над представлением коллекции:
				  .filter	( (s) -> s.startsWith("a") ) // Выбираем все элементы, начинающиеся со строки "a"
				  .map		( convert )					 // Преобразуем stream при помощи функции convert
				  .forEach	( (s) -> out.format("<td>%s</td>", s) ); // Печатаем содержимое stream
			
			// Конвеер parallelStream использует многопоточность для выполнения операций над своим содержимым,
			// поэтому для больших коллекций мы можем получить выйгрыш в производительности при использовании parallelStream:
			out.println("<tr><td>parallelStream conversion: </td>");
			list_2.parallelStream()
				  .filter	( (s) -> s.startsWith("b") ) // Выбираем все элементы, начинающиеся со строки "b"
				  .map		( convert )					 // Преобразуем parallelStream при помощи функции convert
				  .forEach	( (s) -> out.format("<td>%s</td>", s) ); // Печатаем содержимое parallelStream
			
			// Все операции внутри конвеера stream выполняются над представлением элементов коллекции,
			// а следовательно никак не влияют на элементы внутри исходной коллекции:
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
	
	// Рекомендуется использовать аннотацию @FunctionalInterface для всех фукнциональных интерфейсов (хотя она не обязательна):
	@FunctionalInterface // Данная аннотация позволяет компилятору проверить функциональный интерфейс на валидность
	public static interface NumberFactory<N extends Number> {
		// Каждый функциональный интерфейс должен иметь один абстрактный метод:
		N create ( String number ) throws NumberFormatException;
		
		// При этом он может иметь произвольное количество дефолтных методов:
		default String getFactoryName () { return "DefaultName"; }
	}

	public static void main(String[] args) {
		new LambdaExpressionTesting();
	}
}
