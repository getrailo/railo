/**
 * Implements the CFML Function arrayresize
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayResize extends BIF {

	private static final long serialVersionUID = -3107784929660340665L;

	public static boolean call(PageContext pc , Array array, double number) {
		try {
			array.resize((int)number);
			return true;
		} catch (PageException e) {
			return false;
		}
		
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]));
	}
}