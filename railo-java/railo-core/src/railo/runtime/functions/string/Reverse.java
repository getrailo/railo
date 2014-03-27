/**
 * Implements the CFML Function reverse
 */
package railo.runtime.functions.string;

import railo.print;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Reverse extends BIF {

	private static final long serialVersionUID = 7810003809895120054L;

	public static String call(PageContext pc , String string) {
		return new StringBuilder(string).reverse().toString();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {print.ds();
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	
		throw new FunctionException(pc, "Reverse", 1, 1, args.length);
	}
}