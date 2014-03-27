/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;


public final class ListAvg extends BIF {

	private static final long serialVersionUID = -7365055491706152507L;

	public static double call(PageContext pc , String list) throws ExpressionException {
        return call(pc,list,",");
    }
    public static double call(PageContext pc , String list, String delimiter) throws ExpressionException {
        return ArrayUtil.avg(ListUtil.listToArrayRemoveEmpty(list,delimiter));
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "ListAvg", 1, 2, args.length);
	}
}