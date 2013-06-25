/**
 * Implements the CFML Function structfind
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructFind extends BIF {

	private static final long serialVersionUID = 6251275814429295997L;

	public static Object call(PageContext pc , Struct struct, String key) throws PageException {
		return struct.get(KeyImpl.init(key));
	}
	
	public static Object call(PageContext pc , Struct struct, Collection.Key key) throws PageException {
		return struct.get(key);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]),Caster.toKey(args[1]));
	}
}