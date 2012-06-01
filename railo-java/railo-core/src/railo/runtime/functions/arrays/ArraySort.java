/**
 * Implements the CFML Function arraysort
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArraySort implements Function {
	public static boolean call(PageContext pc , Array array, String sorttype) throws PageException {
		return call(pc , array, sorttype, "asc");
	}
	public static boolean call(PageContext pc , Array array, String sorttype, String sortorder) throws PageException {
		array.sort(sorttype, sortorder);
		return true;
	}
}