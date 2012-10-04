/**
 * Implements the CFML Function asin
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class ASin implements Function {
	public static double call(PageContext pc , double number) throws ExpressionException {
		if(number>=-1d && number<=1d)
			return StrictMath.asin(number);
		throw new ExpressionException("invalid range of argument for function aSin, argument range must be between -1 and 1, now is ["+number+"]");
	}
}