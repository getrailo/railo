/**
 * Implements the CFML Function trim
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Trim extends BIF {

	private static final long serialVersionUID = -7769270537431269321L;

	public static String call(PageContext pc , String string) {
		return string.trim();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)
			return call(pc, Caster.toString(args[0]));

		throw new FunctionException(pc, "Trim", 1, 1, args.length);
	}
}