/**
 * Implements the CFML Function findnocase
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class FindNoCase extends BIF {

	private static final long serialVersionUID = 3372064423000880501L;

	public static double call(PageContext pc , String sub, String str) {
		return StringUtil.indexOfIgnoreCase(str, sub)+1;
	}
	
	public static double call(PageContext pc , String sub, String str, double number) {
		return Find.call(pc,sub.toLowerCase(),str.toLowerCase(),number);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toDoubleValue(args[2]));
    	
		throw new FunctionException(pc, "FindNoCase", 2, 3, args.length);
	}
}