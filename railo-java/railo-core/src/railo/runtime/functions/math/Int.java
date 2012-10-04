/**
 * Implements the CFML Function int
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Int implements Function {
	public static double call(PageContext pc , double number) {
		return Math.floor(number);
	}
}