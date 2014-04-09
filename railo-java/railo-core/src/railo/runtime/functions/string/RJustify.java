/**
 * Implements the CFML Function rjustify
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class RJustify extends BIF {

	private static final long serialVersionUID = -4245695462372641408L;

	public static String call(PageContext pc , String str, double length) throws ExpressionException {
		int len=(int) length;
		if(len<1) throw new ExpressionException("Parameter 2 of function rJustify which is now ["+len+"] must be a positive integer");
		else if((len-=str.length())<=0) return str;
		else {
			StringBuilder sb=new StringBuilder(str.length()+len);
			for(int i=1;i<=len;i++) {
				sb.append(' ');
				//str=" "+str;
			}
			return sb.append(str).toString();
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
		throw new FunctionException(pc, "RJustify", 2, 2, args.length);
	}
}