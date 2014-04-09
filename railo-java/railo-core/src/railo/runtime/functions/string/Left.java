/**
 * Implements the CFML Function left
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Left extends BIF {

	private static final long serialVersionUID = 571667661130843970L;

public final class Left implements Function {

	public static String call(PageContext pc, String str, double number) throws ExpressionException {
		int len = (int)number;
		if (len == 0) throw new ExpressionException("parameter 2 of the function left can not be 0");
		if (Math.abs(len) >= str.length()) return str;
		if (len < 0) len = str.length() + len;
		return str.substring(0,len);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
		throw new FunctionException(pc, "Left", 2, 2, args.length);
	}
}