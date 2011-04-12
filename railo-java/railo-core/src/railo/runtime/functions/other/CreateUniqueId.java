
package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class CreateUniqueId implements Function {
	
	private static long counter=0;
	
	
	/**
     * method to invoke the function
	 * @param pc
	 * @return UUID String
	 */
	public static String call(PageContext pc ) {
		return invoke();
	}
	public static synchronized String invoke() {
		counter++;
		if(counter<0) counter=1;
		return Long.toString(counter, Character.MAX_RADIX);
	}
}