/**
 * Implements the CFML Function len
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class StringLen extends BIF {

	private static final long serialVersionUID = -9040645233901974147L;

	public static double call(PageContext pc , String string) {
		return string.length();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)
			return call(pc, Caster.toString(args[0]));

		throw new FunctionException(pc, "StringLen", 1, 1, args.length);
	}
}