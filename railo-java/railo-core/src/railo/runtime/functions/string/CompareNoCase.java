/**
 * Implements the CFML Function comparenocase
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class CompareNoCase extends BIF {

	private static final long serialVersionUID = 4570856747042434801L;

	public static double call(PageContext pc , String str1, String str2) {
		int compare=str1.compareToIgnoreCase(str2);
		if(compare==0) return 0;
		return compare>0?1:-1;
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));

		throw new FunctionException(pc, "CompareNoCase", 2, 2, args.length);
	}
}