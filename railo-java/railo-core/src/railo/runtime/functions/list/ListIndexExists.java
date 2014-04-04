/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListIndexExists extends BIF {
   
	private static final long serialVersionUID = 7642583305678735361L;
	
	public static boolean call(PageContext pc , String list, double index) {
        return call(pc,list,index,",",false);
    }
    public static boolean call(PageContext pc , String list, double index, String delimiter) {
        return call(pc,list,index,delimiter,false);
    }
    public static boolean call(PageContext pc , String list, double index, String delimiter,boolean includeEmptyFields) {
        if(includeEmptyFields)return ListUtil.listToArray(list,delimiter).get((int)index,null)!=null;
    	return ListUtil.listToArrayRemoveEmpty(list,delimiter).get((int)index,null)!=null;
    }
    
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	
		throw new FunctionException(pc, "ListIndexExists", 2, 4, args.length);
	}
}