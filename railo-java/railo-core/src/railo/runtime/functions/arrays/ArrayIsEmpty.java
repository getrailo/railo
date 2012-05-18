/**
 * Implements the ColdFusion Function arrayIsEmpty
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayIsEmpty implements Function {
	public static boolean call(PageContext pc , Array array) {
		return array.size()==0;
	}
}