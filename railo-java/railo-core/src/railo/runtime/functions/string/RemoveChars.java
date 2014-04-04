/**
 * Implements the CFML Function removechars
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class RemoveChars extends BIF {
	public static String call(PageContext pc , String str, double s, double l) throws ExpressionException {
		int start=(int) s;
		int length=(int) l;
		int strLength=str.length();
		
		// check param 2
		if(start<1 || start>strLength)
			throw new ExpressionException("Parameter 2 of function removeChars which is now ["+start+"] must be a greater 0 and less than the length of the first parameter"); 

		// check param 3
		if(length<0)
			throw new ExpressionException("Parameter 3 of function removeChars which is now ["+length+"] must be a non-negative integer"); 

		if(strLength==0) return "";
		
		String rtn=str.substring(0,start-1);
		
		if(start+length<=strLength) rtn+=str.substring(start+length-1);
		return rtn;
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toDoubleValue(args[2]));

		throw new FunctionException(pc, "RemoveChars", 3, 3, args.length);
	}
}