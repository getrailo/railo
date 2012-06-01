/**
 * Implements the CFML Function sin
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Sin implements Function {
	public static double call(PageContext pc , double number) {
		return StrictMath.sin(number);
	}
}