/**
 * Implements the CFML Function pi
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Pi implements Function {
	public static double call(PageContext pc ) {
		return StrictMath.PI;
	}
}