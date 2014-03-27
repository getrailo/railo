/**
 * Implements the CFML Function listlen
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListLen extends BIF {
	
	private static final long serialVersionUID = -592317399255505765L;
	
	public static double call(PageContext pc , String list) {
		return ListUtil.len(list,',',true);
	}
	public static double call(PageContext pc , String list, String delimter) {
		return ListUtil.len(list,delimter,true);
	}
	public static double call(PageContext pc , String list, String delimter, boolean includeEmptyFields) {
		return ListUtil.len(list,delimter,!includeEmptyFields);
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