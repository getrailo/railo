/**
 * Implements the CFML Function log
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class Log implements Function {
	public static double call(PageContext pc , double number) throws ExpressionException {
		if(number<=0.0D) 
			throw new FunctionException(pc,"log",1,"number","value must be a positive number now "+Caster.toString(number)+"");
		return StrictMath.log(number);
	}
}