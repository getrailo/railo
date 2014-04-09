/**
 * Implements the CFML Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListCompact extends BIF {

	private static final long serialVersionUID = 533751863889168299L;

	public static String call(PageContext pc , String list) {
		return call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimiter) {
		return ListUtil.trim(list,delimiter);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "ListCompact", 1, 2, args.length);
	}
}