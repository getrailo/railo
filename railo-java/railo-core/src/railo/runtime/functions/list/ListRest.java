/**
 * Implements the CFML Function listrest
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListRest extends BIF {
	
	private static final long serialVersionUID = -6596215135126751629L;
	
	public static String call(PageContext pc, String list) {
		return ListUtil.rest(list, ",", true, 1);
	}
	public static String call(PageContext pc, String list, String delimiter) {
		return ListUtil.rest(list, delimiter, true, 1);
	}
	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields) {
		return ListUtil.rest(list, delimiter, !includeEmptyFields, 1);
	}

	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields, double offset) throws FunctionException {

		if (offset < 1)
			throw new FunctionException(pc, "ListFirst", 4, "offset", "Argument offset must be a positive value greater than 0");

		return ListUtil.rest(list, delimiter, !includeEmptyFields, (int)offset);
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toBooleanValue(args[2]));
    	
		throw new FunctionException(pc, "ListRemoveDuplicates", 2, 5, args.length);
	}
	
}