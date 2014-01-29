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

	public static Object call(PageContext pc, Struct struct, String key) throws PageException {
		return struct.get(KeyImpl.init(key));
	}
	
	public static Object call(PageContext pc, Struct struct, Collection.Key key) throws PageException {
		return struct.get(key);
	}

	public static Object call(PageContext pc, Struct struct, String key, Object defaultValue) throws PageException {

		return struct.get( Caster.toKey(key), defaultValue );
	}

	public static Object call(PageContext pc, Struct struct, Collection.Key key, Object defaultValue) {

		return struct.get( key, defaultValue );
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if ( args.length == 3 )
			return call(pc, Caster.toStruct(args[0]), Caster.toKey(args[1]), args[2] );

		return call(pc,Caster.toStruct(args[0]),Caster.toKey(args[1]));
	}
}