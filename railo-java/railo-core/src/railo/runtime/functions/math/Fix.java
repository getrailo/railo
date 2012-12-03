/**
 * Implements the CFML Function fix
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Fix implements Function {
	public static double call(PageContext pc , double number) {
		if(number==0) return number;
		return number >0 ? StrictMath.floor(number) : StrictMath.ceil(number);
	}
}