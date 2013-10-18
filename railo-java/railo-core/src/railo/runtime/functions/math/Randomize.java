/**
 * Implements the CFML Function randomize
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Randomize implements Function {
	
	public static double call(PageContext pc, double number) throws ExpressionException {

        Rand.getRandom( CFMXCompat.ALGORITHM_NAME, number );

        return Rand.call( pc, CFMXCompat.ALGORITHM_NAME );
	}

	public static double call(PageContext pc, double number, String algorithm) throws ExpressionException {

        Rand.getRandom( algorithm, number );

        return Rand.call( pc, algorithm );
	}
	
}