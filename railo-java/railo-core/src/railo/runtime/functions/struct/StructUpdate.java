/**
 * Implements the CFML Function structupdate
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

public final class StructUpdate extends BIF {

	private static final long serialVersionUID = -6768103097076333814L;

	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key, Object object) throws PageException {
		Key k = KeyImpl.init(key);
		struct.get(k);
		struct.set(k,object);
		return true;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),args[2]);
	}
}