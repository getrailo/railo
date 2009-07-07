/**
 * Implements the Cold Fusion Function arrayinsertat
 */
package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayInsertAt implements Function {
	public static boolean call(PageContext pc , Array array, double number, Object object) throws PageException {
		return array.insert((int)number,object);
	}
}