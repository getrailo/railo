/**
 * Implements the CFML Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.tag.util.DeprecatedUtil;

public final class ListTrim extends BIF {
	
	private static final long serialVersionUID = 2354456835027080741L;
	
	public static String call(PageContext pc , String list) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimiter) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,delimiter);
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "ListTrim", 1, 2, args.length);
	}
}