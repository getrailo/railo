/**
 * Implements the CFML Function listfirst
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListFirst extends BIF {
	
	private static final long serialVersionUID = 1098339742182832847L;
	
	public static String call(PageContext pc, String list) {
		return ListUtil.first(list, ",", true, 1);
	}
	
	public static String call(PageContext pc, String list, String delimiter) {
		return ListUtil.first(list, delimiter, true, 1);
	}
	
	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields) {
		return ListUtil.first(list, delimiter, !includeEmptyFields, 1);
	}

	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields, double count) throws FunctionException {

		if (count < 1)
			throw new FunctionException(pc, "ListFirst", 4, "count", "Argument count must be a positive value greater than 0");

		return ListUtil.first(list, delimiter, !includeEmptyFields, (int)count);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toBooleanValue(args[2]));
    	
		throw new FunctionException(pc, "ListFirst", 1, 3, args.length);
	}
}