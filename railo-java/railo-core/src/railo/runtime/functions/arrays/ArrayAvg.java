/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;


public final class ArrayAvg extends BIF {
	
	private static final long serialVersionUID = -6440677638555730262L;

	public static double call(PageContext pc , Array array) throws ExpressionException {
		return ArrayUtil.avg(array);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}