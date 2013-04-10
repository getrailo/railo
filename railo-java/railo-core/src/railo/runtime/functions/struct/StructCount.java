/**
 * Implements the CFML Function structcount
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class StructCount extends BIF {

	private static final long serialVersionUID = -2023978105880376970L;

	public static double call(PageContext pc , Struct struct) {
		return struct.size();
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]));
	}
}