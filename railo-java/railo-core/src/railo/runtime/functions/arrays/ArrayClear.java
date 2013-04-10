/**
 * Implements the CFML Function arrayclear
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;


public final class ArrayClear extends BIF {
	
	private static final long serialVersionUID = 3068664942595555678L;

	public static boolean call(PageContext pc , Array array) {
		array.clear();
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}