/**
 * Implements the CFML Function round
 */
package railo.runtime.functions.math;

import java.math.BigDecimal;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Round implements Function {

	private static final long serialVersionUID = 3955271203445975609L;

	public static double call(PageContext pc , double number) {
		return call(pc,number,0);
	}
	
	public static double call(PageContext pc, double number, double precision) {
		if(precision<=0)
			return StrictMath.round(number);
		
		BigDecimal bd = new BigDecimal(number);
		bd = bd.setScale((int)precision, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();	
	}
}