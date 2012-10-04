/**
 * Implements the CFML Function max
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Max implements Function {
	public static double call(PageContext pc , double number1, double number2) {
		return (number1>number2)?number1:number2;
	}
}