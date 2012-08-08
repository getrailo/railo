/**
 * Implements the CFML Function sqr
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Sqr implements Function {
	public static double call(PageContext pc , double number) throws ExpressionException {
	    if(number >= 0.0D)
            return StrictMath.sqrt(number);
        throw new ExpressionException("invalid argument, function argument must be a positive number");
    
	}
}