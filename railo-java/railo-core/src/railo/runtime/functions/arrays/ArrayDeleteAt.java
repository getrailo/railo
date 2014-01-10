/**
 * Implements the CFML Function arraydeleteat
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;


public final class ArrayDeleteAt extends BIF {
	
	private static final long serialVersionUID = -5900967522809749154L;

	public static boolean call(PageContext pc , Array array, double number) throws PageException {
		array.removeE((int)number);
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]));
	}
}