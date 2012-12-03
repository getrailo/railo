/**
 * Implements the CFML Function bitmaskclear
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class BitMaskClear implements Function {
	public static double call(PageContext pc , double dnumber, double dstart, double dlength) throws FunctionException {

		int number=(int) dnumber,start=(int) dstart,length=(int) dlength;
		
		if(start > 31 || start < 0)
			throw new FunctionException(pc,"bitMaskClear",2,"start","must be beetween 0 and 31 now "+start);
		if(length > 31 || length < 0)
			throw new FunctionException(pc,"bitMaskClear",3,"length","must be beetween 0 and 31 now "+length);
		
		
		return number & ~((1 << length) - 1 << start);
	}
}