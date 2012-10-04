/**
 * Implements the CFML Function arrayresize
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayResize implements Function {
	public static boolean call(PageContext pc , Array array, double number) {
		try {
			array.resize((int)number);
			return true;
		} catch (PageException e) {
			return false;
		}
		
	}
}