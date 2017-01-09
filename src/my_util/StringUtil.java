package my_util;

import java.util.Arrays;

public class StringUtil {
	
	public static String fillStr ( char c, int repeat_cnt ) {
		if ( repeat_cnt > 0 ) {
			char[] array = new char[repeat_cnt];
		    Arrays.fill(array, c);
		    return new String(array);
		}
		return "";
	}
}
