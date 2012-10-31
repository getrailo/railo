/**
 * Implements the CFML Function round
 */
package railo.runtime.functions.math;

import java.math.BigDecimal;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Round implements Function {
	public static double call(PageContext pc , double number) {
		return StrictMath.round(number);
	}
	
	public static double call(PageContext pc, double number, double precision) {
		
		BigDecimal bd = new BigDecimal(Double.toString(number));
		bd = bd.setScale((int)precision, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}