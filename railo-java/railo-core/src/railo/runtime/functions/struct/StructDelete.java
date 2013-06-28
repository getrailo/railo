/**
 * Implements the CFML Function structdelete
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructDelete extends BIF {

	private static final long serialVersionUID = 6670961245029356618L;

	public static boolean call(PageContext pc , Struct struct, String key) {
		return call(pc , struct, key, false);
	}
	public static boolean call(PageContext pc , Struct struct, String key, boolean indicatenotexisting ) {
		return struct.removeEL(KeyImpl.init(key))!=null || !indicatenotexisting;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==3) return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),Caster.toBooleanValue(args[2]));
		return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]));
	}
}