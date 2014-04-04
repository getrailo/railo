/**
 * Implements the CFML Function listtoarray
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListItemTrim extends BIF {
	
	private static final long serialVersionUID = -2254266180423759499L;

	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list,",",false);
	}
	public static String call(PageContext pc , String list, String delimiter) throws PageException {
		return call(pc,list,delimiter,false);
	}
	
	public static String call(PageContext pc , String list, String delimiter,boolean includeEmptyFields) throws PageException {
		if(list.length()==0) return "";
		Array arr = includeEmptyFields?ListUtil.listToArray(list,delimiter):ListUtil.listToArrayRemoveEmpty(list,delimiter);
		return ListUtil.arrayToList(ListUtil.trimItems(arr),delimiter);	
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toBooleanValue(args[2]));
    	
		throw new FunctionException(pc, "ListItemTrim", 1, 3, args.length);
	}
}