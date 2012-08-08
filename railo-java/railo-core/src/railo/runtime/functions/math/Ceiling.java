/**
 * Implements the CFML Function ceiling
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Ceiling implements Function {
	public static double call(PageContext pc , double number) {
		 return StrictMath.ceil(number);
	}
}