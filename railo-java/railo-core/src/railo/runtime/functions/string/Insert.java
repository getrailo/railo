/**
 * Implements the CFML Function insert
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Insert extends BIF {

	private static final long serialVersionUID = 5926183314989306282L;

	public static String call(PageContext pc , String sub, String str, double pos) throws ExpressionException {
		int p=(int) pos;
		if(p<0 || p>str.length())
			throw new ExpressionException("third parameter of the function insert, must be between 0 and "+str.length()+" now ["+(p)+"]");
		StringBuilder sb=new StringBuilder(str.length()+sub.length());
		
		return sb.append(str.substring(0,p)).append(sub).append(str.substring(p)).toString();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toDoubleValue(args[2]));
    	
		throw new FunctionException(pc, "Insert", 2, 3, args.length);
	}
}