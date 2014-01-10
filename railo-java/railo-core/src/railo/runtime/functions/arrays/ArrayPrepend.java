/**
 * Implements the CFML Function arrayprepend
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayPrepend extends BIF {

	private static final long serialVersionUID = 777525067673834084L;

	public static boolean call(PageContext pc , Array array, Object object) throws PageException {
		array.prepend(object);
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),args[1]);
	}
}