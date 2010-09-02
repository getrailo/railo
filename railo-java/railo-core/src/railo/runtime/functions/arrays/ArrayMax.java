/**
 * Implements the Cold Fusion Function arraymax
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayMax implements Function {
	public static double call(PageContext pc , Array array) throws PageException {
		return ArrayUtil.max(array);
	}
}