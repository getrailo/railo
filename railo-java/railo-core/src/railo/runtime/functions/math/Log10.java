/**
 * Implements the CFML Function log10
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Log10 implements Function {
	public static double call(PageContext pc , double number) throws ExpressionException {
		if(number<=0) throw new ExpressionException("invalid argument at function log10, vale must be a positive number now "+((int)number)+"");
		return 0.43429448190325182D * StrictMath.log(number);
	}
}