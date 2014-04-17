/**
 * Implements the CFML Function ljustify
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class LJustify extends BIF {

	private static final long serialVersionUID = 4431425567063867833L;

	public static String call(PageContext pc , String str, double length) throws ExpressionException {
		int len=(int) length;
		if(len<1) throw new ExpressionException("Parameter 2 of function lJustify which is now ["+len+"] must be a positive integer");
		else if((len-=str.length())<=0) return str;
		else {
			StringBuilder sb=new StringBuilder(str.length()+len);
			sb.append(str);
			for(int i=1;i<=len;i++) {
				//str+=" ";
				sb.append(' ');
			}
			return sb.toString();
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
		throw new FunctionException(pc, "LJustify", 2, 2, args.length);
	}
}