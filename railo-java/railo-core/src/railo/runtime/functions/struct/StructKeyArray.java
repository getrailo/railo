/**
 * Implements the CFML Function structkeyarray
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.CollectionUtil;

public final class StructKeyArray extends BIF {

	private static final long serialVersionUID = -3177185567576262172L;

	public static Array call(PageContext pc , railo.runtime.type.Struct struct) {
		return KeyImpl.toArray(CollectionUtil.keys(struct));
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]));
	}
}