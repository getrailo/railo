/**
 * Implements the CFML Function randrange
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class RandRange implements Function {
	public static double call(PageContext pc , double number1, double number2) throws ExpressionException {
		return call(pc,number1,number2,"cfmx_compat");
	}
	public static double call(PageContext pc , double number1, double number2, String algo) throws ExpressionException {
	    
		int min=(int) number1;
		int max=(int) number2;
		
		if(number1>number2) {
			int tmp=min;
			min=max;
			max=tmp;
		}
		int diff=max-min;
		return ((int)(Rand.call(pc,algo)*(diff+1)))+min;
	}
	
	public static int invoke(int min, int max) throws ExpressionException {
	    
		if(min>max) {
			int tmp=min;
			min=max;
			max=tmp;
		}
		int diff=max-min;
		return ((int)(Rand.call(null,"cfmx_compat")*(diff+1)))+min;
	}
	
	
}