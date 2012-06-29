/**
 * Implements the CFML Function bitshln
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class BitSHLN implements Function {
	public static double call(PageContext pc , double dnumber, double dcount) throws FunctionException {
		int number=(int) dnumber,count=(int) dcount;
		if(count > 31 || count < 0)
			throw new FunctionException(pc,"bitSHLN",2,"count","must be beetween 0 and 31 now "+count);
		
		return number << count;
	}	
}