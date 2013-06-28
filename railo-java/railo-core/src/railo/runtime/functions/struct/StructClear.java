/**
 * Implements the CFML Function structclear
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class StructClear extends BIF {

	private static final long serialVersionUID = 1814513664840100560L;

	public static boolean call(PageContext pc , Struct struct) {
		struct.clear();
		return true;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc, Caster.toStruct(args[0]));
	}
}