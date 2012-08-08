/**
 * Implements the CFML Function incrementvalue
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class IncrementValue implements Function {
	public static double call(PageContext pc , double number){
		return ++number;
	}
}