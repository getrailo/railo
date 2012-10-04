/**
 * Implements the CFML Function round
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Round implements Function {
	public static double call(PageContext pc , double number) {
		return StrictMath.round(number);
	}
}