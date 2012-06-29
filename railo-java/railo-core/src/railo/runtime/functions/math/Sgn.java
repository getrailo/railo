/**
 * Implements the CFML Function sgn
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Sgn implements Function {
	public static double call(PageContext pc , double number) {
		return number != 0.0d ? number >= 0.0d ? 1 : -1 : 0;
	}
}