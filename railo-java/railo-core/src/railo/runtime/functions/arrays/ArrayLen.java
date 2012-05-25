/**
 * Implements the ColdFusion Function arrayLen
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayLen implements Function {
	public static double call(PageContext pc , Array array) {
		return array.size();
	}
}