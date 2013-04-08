/**
 * Implements the CFML Function arrayisempty
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayIsEmpty extends BIF {

	private static final long serialVersionUID = 6459041981875254607L;

	public static boolean call(PageContext pc , Array array) {
		return array.size()==0;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}