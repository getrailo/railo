/**
 * Implements the CFML Function left
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Left extends BIF {

	private static final long serialVersionUID = 571667661130843970L;

	public static String call(PageContext pc , String str, double number) throws ExpressionException {
		int len=(int) number;
		if(len<1) throw new ExpressionException("parameter 2 of the function left must be a positive number now ["+len+"]");
		if(len>=str.length()) return str;
		return str.substring(0,len);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
		throw new FunctionException(pc, "Left", 2, 2, args.length);
	}
}