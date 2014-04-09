/**
 * Implements the CFML Function listvaluecountnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class ListValueCountNoCase extends BIF {

	private static final long serialVersionUID = 2648222056209118284L;

	public static double call(PageContext pc , String list, String value) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),",");
	}
	public static double call(PageContext pc , String list, String value, String delimiter) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),delimiter);
		
	}
	public static double call(PageContext pc , String list, String value, String delimiter,boolean includeEmptyFields) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),delimiter,includeEmptyFields);
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	
		throw new FunctionException(pc, "ListValueCountNoCase", 2, 4, args.length);
	}
}