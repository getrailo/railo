/**
 * Implements the CFML Function acos
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class ACos implements Function {
	public static double call(PageContext pc , double number) throws ExpressionException {
		if(number>=-1d && number<=1d)
			return StrictMath.acos(number);
		throw new ExpressionException("invalid range of argument for function aCos, argument range must be between -1 and 1, now is ["+number+"]");
	}
}