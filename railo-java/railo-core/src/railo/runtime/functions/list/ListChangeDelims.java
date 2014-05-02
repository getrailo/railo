/**
 * Implements the CFML Function listchangedelims
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListChangeDelims extends BIF {
	
	private static final long serialVersionUID = 8979553735693035787L;
	
	public static String call(PageContext pc , String list, String newDel) throws PageException {
		return call(pc , list, newDel, ",",false,false);
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel) throws PageException {
		return call(pc, list, newDel, oldDel, false,false);
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel, boolean includeEmptyFields) throws PageException {
		return call(pc, list, newDel, oldDel, includeEmptyFields,false);
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel
			, boolean includeEmptyFields, boolean multiCharacterDelimiter) throws PageException {
		return ListUtil.arrayToList(ListUtil.listToArray(list,oldDel,includeEmptyFields,multiCharacterDelimiter),newDel);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]));
    	
		throw new FunctionException(pc, "ListChangeDelims", 2, 5, args.length);
	}
}