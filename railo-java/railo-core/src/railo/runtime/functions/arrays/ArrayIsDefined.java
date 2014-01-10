/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayIsDefined extends BIF {

	private static final long serialVersionUID = 5821478169641360902L;

	public static boolean call(PageContext pc , Array array, double index) {
    	return ArrayIndexExists.call(pc, array, index);
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]));
	}
}