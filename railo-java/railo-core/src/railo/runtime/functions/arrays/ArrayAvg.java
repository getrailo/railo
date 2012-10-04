/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;


public final class ArrayAvg implements Function {
	
	private static final long serialVersionUID = -6440677638555730262L;

	public static double call(PageContext pc , Array array) throws ExpressionException {
		return ArrayUtil.avg(array);
	}
}