/**
 * Implements the CFML Function listsort
 */
package railo.runtime.functions.list;


import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListSort extends BIF {
	
	private static final long serialVersionUID = -1153055612742304078L;
	
	public static String call(PageContext pc , String list, String sortType) throws PageException {
		return call(pc,list,sortType,"asc",",",false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder) throws PageException {
		return call(pc,list,sortType,sortOrder,",",false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder, String delimiter) throws PageException {
		return call(pc,list,sortType,sortOrder,delimiter,false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder, String delimiter , boolean includeEmptyFields) throws PageException {
		if(includeEmptyFields) return ListUtil.sort(list,sortType, sortOrder,delimiter);
		return ListUtil.sortIgnoreEmpty(ListUtil.trim(list,delimiter),sortType, sortOrder,delimiter);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toString(args[3]));
    	if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toString(args[3]), Caster.toBooleanValue(args[4]));
    	
		throw new FunctionException(pc, "ListSort", 2, 5, args.length);
	}
}
