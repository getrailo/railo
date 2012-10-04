/**
 * Implements the CFML Function bitnot
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class BitNot implements Function {
	public static double call(PageContext pc , double number) {
		return ~(int)number;
	}
}