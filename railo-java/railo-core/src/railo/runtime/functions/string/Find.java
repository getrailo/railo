/**
 * Implements the CFML Function find
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class Find extends BIF {

	private static final long serialVersionUID = 1399049740954864771L;

	public static double call(PageContext pc , String sub, String str) {
		return str.indexOf(sub)+1;
	}
	public static double call(PageContext pc , String sub, String str, double number) {
		if(sub.length()==0) return (int) number;
		return str.indexOf(sub,(int)number-1)+1;
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toDoubleValue(args[2]));
    	
		throw new FunctionException(pc, "Find", 2, 3, args.length);
	}
}