/**
 * Implements the CFML Function bitor
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class BitOr implements Function {
	public static double call(PageContext pc , double number, double number2) {
		return (int)number|(int)number2;
	}
}