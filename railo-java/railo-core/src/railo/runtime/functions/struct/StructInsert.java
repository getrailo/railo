/**
 * Implements the CFML Function structinsert
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructInsert extends BIF {
	
	private static final long serialVersionUID = 4244527243856690926L;
	
	public static boolean call(PageContext pc , Struct struct, String key, Object value) throws PageException {
		return call(pc , struct, key, value, false);
	}
	
	public static boolean call(PageContext pc , Struct struct, String strKey, Object value, boolean allowoverwrite) throws PageException {
		Key key = KeyImpl.init(strKey);
		if(allowoverwrite) { 
			struct.set(key,value);
		}
		else {
			if(struct.get(key,null)!=null) throw new ExpressionException("key ["+key+"] already exist in struct");
			struct.set(key,value);
		}
		return true;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==4) return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),args[2],Caster.toBooleanValue(args[3]));
		return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),args[2]);
	}
}