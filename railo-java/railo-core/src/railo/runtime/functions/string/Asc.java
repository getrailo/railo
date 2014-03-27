/**
 * Implements the CFML Function asc
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Asc extends BIF {

	private static final long serialVersionUID = 8147532406904456091L;

	public static double call(PageContext pc , String string) {
		if(string.length()==0)return 0;
		return string.charAt(0);
	}
	public static double call(PageContext pc , String string, double position) {
		int pos=(int) position;
		if(pos<1 || pos>string.length()) return 0;
		return string.charAt(pos-1);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	
		throw new FunctionException(pc, "asc", 1, 2, args.length);
	}
}