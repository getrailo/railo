/**
 * Implements the CFML Function arrayinsertat
 */
package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayInsertAt extends BIF {

	private static final long serialVersionUID = -418752384898360791L;

	public static boolean call(PageContext pc , Array array, double number, Object object) throws PageException {
		return array.insert((int)number,object);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]),args[2]);
	}
}