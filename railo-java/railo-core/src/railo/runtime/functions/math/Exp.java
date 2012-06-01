/**
 * Implements the CFML Function exp
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Exp implements Function {
	public static double call(PageContext pc , double number) {
		return Math.exp(number);
	}
}