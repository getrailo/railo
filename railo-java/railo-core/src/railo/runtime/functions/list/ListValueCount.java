/**
 * Implements the CFML Function listvaluecount
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListValueCount extends BIF {

	private static final long serialVersionUID = -1808030347105091742L;

	public static double call(PageContext pc , String list, String value) throws PageException {
		return call(pc,list,value,",",false);
	}

	public static double call(PageContext pc , String list, String value, String delimiter) throws PageException {
		return call(pc,list,value,delimiter,false);
	}
	public static double call(PageContext pc , String list, String value, String delimiter,boolean includeEmptyFields) throws PageException {
		int count=0;
		Array arr= includeEmptyFields?ListUtil.listToArray(list,delimiter):ListUtil.listToArrayRemoveEmpty(list,delimiter);
		int len=arr.size();
		
		for(int i=1;i<=len;i++) {
			if(arr.getE(i).equals(value))count++;
		}
		return count;
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	
		throw new FunctionException(pc, "ListValueCount", 2, 4, args.length);
	}
}