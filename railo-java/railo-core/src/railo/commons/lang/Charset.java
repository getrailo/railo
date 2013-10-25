package railo.commons.lang;

import java.util.Arrays;
import java.util.SortedMap;

public final class Charset {

	public static String UTF8 = "UTF-8";

	public static String[] getAvailableCharsets() {
		 SortedMap map = java.nio.charset.Charset.availableCharsets();
		 String[] keys=(String[]) map.keySet().toArray(new String[map.size()]);
		 Arrays.sort(keys);
		 return keys;
	}
	
	/**
	 * is given charset supported or not
	 * @param charset
	 * @return
	 */
	public static boolean isSupported(String charset) {
		return java.nio.charset.Charset.isSupported(charset);
	}
	
}
