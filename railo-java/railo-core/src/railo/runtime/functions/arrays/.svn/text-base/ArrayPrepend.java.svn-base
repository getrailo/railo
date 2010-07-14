/**
 * Implements the Cold Fusion Function arrayprepend
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayPrepend implements Function {
	public static boolean call(PageContext pc , Array array, Object object) throws PageException {
		array.prepend(object);
		return true;
	}
}