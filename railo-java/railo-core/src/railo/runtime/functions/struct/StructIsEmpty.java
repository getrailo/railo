/**
 * Implements the CFML Function structisempty
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class StructIsEmpty extends BIF {

	private static final long serialVersionUID = 6804878535578055394L;

	public static boolean call(PageContext pc , Struct struct) {
		return struct.size()==0;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]));
	}
}