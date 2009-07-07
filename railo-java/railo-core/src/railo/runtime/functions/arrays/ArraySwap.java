/**
 * Implements the Cold Fusion Function arrayswap
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArraySwap implements Function {
	public static boolean call(PageContext pc ,Array array, double number, double number2) throws ExpressionException {
		ArrayUtil.swap(array,(int)number,(int)number2);
		return true;
	}
}