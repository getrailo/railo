/**
 * Implements the CFML Function compare
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Compare extends BIF {

	private static final long serialVersionUID = -6982310146145687711L;

	public static double call(PageContext pc , String str1, String str2) {
		int compare=str1.compareTo(str2);
		if(compare==0) return 0;
		return compare>0?1:-1;
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));

		throw new FunctionException(pc, "Compare", 2, 2, args.length);
	}
	
	
}