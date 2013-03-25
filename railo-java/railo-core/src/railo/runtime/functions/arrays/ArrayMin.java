/**
 * Implements the CFML Function arraymin
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayMin extends BIF {

	private static final long serialVersionUID = 7640801691378949924L;

	public static double call(PageContext pc , Array array) throws PageException {
		return ArrayUtil.min(array);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}