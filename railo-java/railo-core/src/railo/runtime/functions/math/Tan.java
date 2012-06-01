/**
 * Implements the CFML Function tan
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Tan implements Function {
	public static double call(PageContext pc , double number) {
		return StrictMath.tan(number);
	}
}