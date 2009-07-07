/**
 * Implements the Cold Fusion Function arraynew
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class ArrayNew implements Function {
	public static Array call(PageContext pc , double number) throws ExpressionException {
		return new ArrayImpl((int)number);
	}
}