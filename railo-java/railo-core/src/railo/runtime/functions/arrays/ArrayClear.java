/**
 * Implements the Cold Fusion Function arrayclear
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;


public final class ArrayClear implements Function {
	public static boolean call(PageContext pc , Array array) {
		array.clear();
		return true;
	}
}