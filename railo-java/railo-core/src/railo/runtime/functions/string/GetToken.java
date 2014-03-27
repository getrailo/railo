/**
 * Implements the CFML Function gettoken
 */
package railo.runtime.functions.string;

import java.util.StringTokenizer;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class GetToken extends BIF {

	private static final long serialVersionUID = 4114410822911429954L;

	public static String call(PageContext pc , String str, double index) throws ExpressionException {
		return call(pc,str,index,null);
	}
	public static String call(PageContext pc , String str, double index, String delimiters) throws ExpressionException {
		if(delimiters==null) delimiters="\r\n\t ";
		
		if(index<1)
			throw new FunctionException(pc,"getToken",2,"index","index must be a positive number now ("+((int)index)+")");
		
		StringTokenizer tokens=new StringTokenizer(str,delimiters);
		int count=0;
		while(tokens.hasMoreTokens()) {
			if(++count==index)return tokens.nextToken();
			tokens.nextToken();
		}
		return "";
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]));
    	
		throw new FunctionException(pc, "GetToken", 2, 3, args.length);
	}
}