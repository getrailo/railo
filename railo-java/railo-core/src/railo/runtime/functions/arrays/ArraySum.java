/**
 * Implements the ColdFusion Function arraySum
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArraySum implements Function {
	public static double call(PageContext pc , Array array) throws ExpressionException {
		return ArrayUtil.sum(array);
	}
}