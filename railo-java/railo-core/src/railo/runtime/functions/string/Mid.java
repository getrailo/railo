/**
 * Implements the CFML Function mid
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Mid extends BIF {
	public static String call(PageContext pc , String str, double start) throws ExpressionException {
		return call(pc,str,start,-1);
	}
	public static String call(PageContext pc , String str, double start, double count) throws ExpressionException {
		int s=(int) (start-1);
		int c=(int) count;
		
		if(s<0) throw new ExpressionException("Parameter 2 of function mid which is now ["+(s+1)+"] must be a positive integer");
		if(c==-1) c=str.length();
		else if(c<-1) throw new ExpressionException("Parameter 3 of function mid which is now ["+c+"] must be a non-negative integer or -1 (for string length)");
		c+=s;
		if(s>str.length()) return "";
		else if(c>=str.length())return str.substring(s);
		else {
			return str.substring(s,c);
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toDoubleValue(args[2]));

		throw new FunctionException(pc, "Mid", 2, 3, args.length);
	}
}