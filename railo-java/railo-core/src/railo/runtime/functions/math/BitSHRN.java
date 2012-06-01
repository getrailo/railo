/**
 * Implements the CFML Function bitshrn
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class BitSHRN implements Function {
	
	public static double call(PageContext pc , double dnumber, double dcount) throws FunctionException {
		int number=(int) dnumber,count=(int) dcount;
		if(count > 31 || count < 0)
			throw new FunctionException(pc,"bitSHRN",2,"count","must be beetween 0 and 31 now "+count);
		
		return number >>> count;
	}	
}