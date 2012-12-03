/**
 * Implements the CFML Function abs
 */
package railo.runtime.functions.math;

import railo.commons.math.MathUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Abs implements Function {
	
    public static double call(PageContext pc , double number) {
		return MathUtil.abs(number);
	}
}