/**
 * Implements the CFML Function arraymax
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayMax extends BIF {

	private static final long serialVersionUID = -4347418519322157914L;

	public static double call(PageContext pc , Array array) throws PageException {
		return ArrayUtil.max(array);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}