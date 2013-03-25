/**
 * Implements the CFML Function arraysum
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArraySum extends BIF {

	private static final long serialVersionUID = 2414586741503001864L;

	public static double call(PageContext pc , Array array) throws ExpressionException {
		return ArrayUtil.sum(array);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}