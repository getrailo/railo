/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayIndexExists extends BIF {
	
	private static final long serialVersionUID = -4490011932571314711L;

	public static boolean call(PageContext pc , Array array, double index) {
        return array.get((int)index,NullSupportHelper.NULL())!=NullSupportHelper.NULL();
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toDoubleValue(args[1]));
	}
}