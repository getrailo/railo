/**
 * Implements the CFML Function bitmaskset
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class BitMaskSet implements Function {
	
	public static double call(PageContext pc , double dnumber, double dmask, double dstart, double dlength) throws FunctionException {

		int number=(int)dnumber, mask=(int)dmask, start=(int)dstart, length=(int)dlength;
		
		if(start > 31 || start < 0)
			throw new FunctionException(pc,"bitMaskSet",2,"start","must be beetween 0 and 31 now "+start);
		if(length > 31 || length < 0)
			throw new FunctionException(pc,"bitMaskSet",3,"length","must be beetween 0 and 31 now "+length);
		
        int tmp = (1 << length) - 1 << start;
        mask &= (1 << length) - 1;
        return number & ~tmp | mask << start;
	}
}