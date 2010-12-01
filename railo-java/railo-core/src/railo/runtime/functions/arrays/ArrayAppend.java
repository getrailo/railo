/**
 * Implements the Cold Fusion Function arrayappend
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

/**
 * implementation of the Function arrayAppend
 */
public final class ArrayAppend implements Function {
	
	private static final long serialVersionUID = 5989673419120862625L;

	/**
	 * @param pc
	 * @param array
	 * @param object
	 * @return has appended
	 * @throws PageException
	 */
	public static boolean call(PageContext pc , Array array, Object object) throws PageException {
		array.append(object);
		return true;
	}
}