/**
 * Implements the CFML Function right
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Right extends BIF {
	
	private static final long serialVersionUID = 2270997683293984478L;

	public static String call(PageContext pc, String str, double number) throws ExpressionException {
		int len = (int)number;
		if (len == 0) throw new ExpressionException("parameter 2 of the function right can not be 0");
		int l=str.length();
		if (Math.abs(len) >= l) return str;
		if (len < 0) len = l + len;
		return str.substring(l-len,l);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
		throw new FunctionException(pc, "Right", 2, 2, args.length);
	}
}