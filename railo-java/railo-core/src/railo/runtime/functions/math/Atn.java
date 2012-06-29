/**
 * Implements the CFML Function atn
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Atn implements Function {
	public static double call(PageContext pc , double number) {
		return StrictMath.atan(number);
	}
}