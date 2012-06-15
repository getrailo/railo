/**
 * Implements the CFML Function arrayset
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;

public final class ArraySet implements Function {
	public static boolean call(PageContext pc , Array array, double from, double to, Object value) throws PageException {
		int f=(int) from;
		int t=(int) to;
		if(f<1)
			throw new ExpressionException("Second parameter of the function arraySet must be greater than zero; now ["+f+"]");
		if(f>t)
			throw new ExpressionException("Third parameter of the function arraySet must be greater than the second parameter; now [second:"+f+", third:"+t+"]");
		if(array.getDimension()>1)
			throw new ExpressionException("Function arraySet can only be used with a one-dimensional array; this array has "+array.getDimension()+" dimensions");
        for(int i=f;i<=t;i++) {
			array.setE(i,Duplicator.duplicate(value,true));
		}

		return true;
	}
}