/**
 * Implements the CFML Function arrayswap
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArraySwap extends BIF {

	private static final long serialVersionUID = 4539191577529273165L;

	public static boolean call(PageContext pc ,Array array, double number, double number2) throws ExpressionException {
		ArrayUtil.swap(array,(int)number,(int)number2);
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]),Caster.toDoubleValue(args[2]));
	}
}