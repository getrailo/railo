/**
 * Implements the CFML Function decrementvalue
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class DecrementValue implements Function {
	public static double call(PageContext pc , double number) {
		return --number;
	}
}